/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.toypet;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerPetsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.pets.PetBuff;
import com.aionemu.gameserver.model.team2.common.legacy.LootRuleType;
import com.aionemu.gameserver.model.templates.item.ItemUseLimits;
import com.aionemu.gameserver.model.templates.item.actions.AbstractItemAction;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.pet.FoodType;
import com.aionemu.gameserver.model.templates.pet.PetBonusAttr;
import com.aionemu.gameserver.model.templates.pet.PetFeedResult;
import com.aionemu.gameserver.model.templates.pet.PetFlavour;
import com.aionemu.gameserver.model.templates.pet.PetFunction;
import com.aionemu.gameserver.model.templates.pet.PetFunctionType;
import com.aionemu.gameserver.model.templates.pet.PetMerchandEntry;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This service manages the core logic and behaviors for {@link Pet} objects in the game.<br>
 * It handles pet interactions, feeding mechanics, and data synchronization between the server and clients.
 * @author M@xx, IlBuono, xTz, Rolandas
 */
public class PetService
{
	Logger log = LoggerFactory.getLogger(PetService.class);
	private PetBuff PetBuff;
	private boolean autoSeel = false;
	private boolean autoBuff = false;
	
	/**
	 * Retrieves the singleton instance of the {@link PetService}.<br>
	 * This provides a global access point to pet-related logic.
	 * @return The active {@code PetService} instance.
	 */
	public static PetService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This class uses the singleton pattern via {@code getInstance}.
	 */
	private PetService()
	{
	}
	
	/**
	 * Changes the name of a player's pet.<br>
	 * This method updates the name in the database and sends a packet to the player.<br>
	 * It only performs an action if the {@code Player} has a pet equipped.
	 * @param player The {@link Player} who owns the pet.
	 * @param name The new name to assign to the pet.
	 */
	public void renamePet(Player player, String name)
	{
		final Pet pet = player.getPet();
		if (pet != null)
		{
			pet.getCommonData().setName(name);
			DAOManager.getDAO(PlayerPetsDAO.class).updatePetName(pet.getCommonData());
			PacketSendUtility.broadcastPacket(player, new SM_PET(10, pet), true);
		}
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It handles any initialization logic required for the player's session.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		final Collection<PetCommonData> playerPets = player.getPetList().getPets();
		if ((playerPets != null) && (playerPets.size() > 0))
		{
			PacketSendUtility.sendPacket(player, new SM_PET(0, playerPets));
		}
	}
	
	/**
	 * Removes a specific quantity of an item from the player's inventory.<br>
	 * This method is used to handle pet feeding actions.<br>
	 * It validates the item existence and initiates the scheduled feeding process.
	 * @param objectId The unique ID of the item to be removed.
	 * @param count The amount of the item to remove.
	 * @param action The specific action type associated with this removal.
	 * @param player The {@link Player} instance performing the action.
	 */
	public void removeObject(int objectId, int count, int action, Player player)
	{
		final Item item = player.getInventory().getItemByObjId(objectId);
		if ((item == null) || (player.getPet() == null) || (count > item.getItemCount()))
		{
			return;
		}
		
		final Pet pet = player.getPet();
		pet.getCommonData().setCancelFeed(false);
		PacketSendUtility.sendPacket(player, new SM_PET(1, action, item.getObjectId(), count, pet));
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FEEDING, 0, player.getObjectId()));
		
		schedule(pet, player, item, count, action);
	}
	
	/**
	 * Schedules a delayed task to check pet feeding logic.<br>
	 * The task runs after a delay of {@code 2500} milliseconds.<br>
	 * It verifies if the {@link Pet} feed action can be completed.
	 * @param pet The {@link Pet} object involved in the action.
	 * @param player The {@link Player} who initiated the action.
	 * @param item The {@link Item} being used for feeding.
	 * @param count The quantity of the item to use.
	 * @param action The specific type of action performed.
	 */
	private void schedule(Pet pet, Player player, Item item, int count, int action)
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if (!pet.getCommonData().getCancelFeed())
			{
				checkFeeding(pet, player, item, count, action);
			}
		}, 2500);
	}
	
	/**
	 * Validates and processes the feeding action for a {@link Pet}.<br>
	 * It checks if the provided {@link Item} is compatible with the pet's flavor.<br>
	 * The method updates the pet's hunger progress and handles rewards or scheduling.
	 * @param pet The {@link Pet} being fed.
	 * @param player The {@link Player} performing the action.
	 * @param item The {@link Item} used as food.
	 * @param count The number of items to be consumed.
	 * @param action The specific action type associated with the feeding.
	 */
	private void checkFeeding(Pet pet, Player player, Item item, int count, int action)
	{
		final PetCommonData commonData = pet.getCommonData();
		final PetFeedProgress progress = commonData.getFeedProgress();
		
		if (!commonData.getCancelFeed())
		{
			final PetFunction func = pet.getPetTemplate().getPetFunction(PetFunctionType.FOOD);
			final PetFlavour flavour = DataManager.PET_FEED_DATA.getFlavourById(func.getId());
			FoodType foodType = flavour.getFoodType(item.getItemId());
			PetFeedResult reward = null;
			
			if (flavour.isLovedFood(foodType, item.getItemId()) && (progress.getLovedFoodRemaining() == 0))
			{
				foodType = null;
			}
			
			if (foodType != null)
			{
				player.getInventory().decreaseItemCount(item, 1, ItemUpdateType.DEC_PET_FOOD);
				reward = flavour.processFeedResult(progress, foodType, item.getItemTemplate().getLevel(), player.getCommonData().getLevel());
				if ((progress.getHungryLevel() == PetHungryLevel.FULL) && (reward != null))
				{
					PacketSendUtility.sendPacket(player, new SM_PET(2, action, item.getObjectId(), 0, pet));
				}
				else
				{
					PacketSendUtility.sendPacket(player, new SM_PET(2, action, item.getObjectId(), --count, pet));
				}
			}
			else
			{
				// non eatable item
				PacketSendUtility.sendPacket(player, new SM_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_TOYPET_FEED_FOOD_NOT_LOVEFLAVOR(pet.getName(), item.getItemTemplate().getNameId()));
				return;
			}
			
			if ((progress.getHungryLevel() == PetHungryLevel.FULL) && (reward != null))
			{
				PacketSendUtility.sendPacket(player, new SM_PET(6, action, reward.getItem(), 0, pet));
				PacketSendUtility.sendPacket(player, new SM_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
				PacketSendUtility.sendPacket(player, new SM_PET(7, action, 0, 0, pet)); // 2151591961
				
				ItemService.addItem(player, reward.getItem(), 1);
				commonData.scheduleRefeed(flavour.getCooldDown() * 60000);
				final long refeedTime = System.currentTimeMillis() + (flavour.getCooldDown() * 60000);
				commonData.setRefeedTime(refeedTime);
				DAOManager.getDAO(PlayerPetsDAO.class).setTime(player, pet.getPetId(), refeedTime);
				progress.reset();
			}
			else if (count > 0)
			{
				schedule(pet, player, item, count, action);
			}
			else
			{
				PacketSendUtility.sendPacket(player, new SM_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
			}
		}
	}
	
	/**
	 * Moves a doping item between two slots in the pet's doping bag.<br>
	 * This method swaps the items located at {@code targetSlot} and {@code destinationSlot}.<br>
	 * It updates the pet data and sends the necessary packets to the player.
	 * @param player The {@link Player} who owns the pet.
	 * @param targetSlot The index of the first doping slot.
	 * @param destinationSlot The index of the second doping slot.
	 */
	public void relocateDoping(Player player, int targetSlot, int destinationSlot)
	{
		final Pet pet = player.getPet();
		if ((pet == null) || (pet.getCommonData().getDopingBag() == null))
		{
			return;
		}
		
		final int[] scrollBag = pet.getCommonData().getDopingBag().getScrollsUsed();
		final int targetItem = scrollBag[targetSlot - 2];
		if ((destinationSlot - 2) > (scrollBag.length - 1))
		{
			pet.getCommonData().getDopingBag().setItem(targetItem, destinationSlot);
			PacketSendUtility.sendPacket(player, new SM_PET(0, targetItem, destinationSlot));
			pet.getCommonData().getDopingBag().setItem(0, targetSlot);
			PacketSendUtility.sendPacket(player, new SM_PET(0, 0, targetSlot));
		}
		else
		{
			pet.getCommonData().getDopingBag().setItem(scrollBag[destinationSlot - 2], targetSlot);
			PacketSendUtility.sendPacket(player, new SM_PET(0, scrollBag[destinationSlot - 2], targetSlot));
			pet.getCommonData().getDopingBag().setItem(targetItem, destinationSlot);
			PacketSendUtility.sendPacket(player, new SM_PET(0, targetItem, destinationSlot));
		}
	}
	
	/**
	 * Handles the usage of doping items for a player's pet.<br>
	 * This method manages adding, replacing, or using items in the pet's doping bag.<br>
	 * It also checks if the pet has any remaining doping items to maintain its buff status.
	 * @param player The {@link Player} who is interacting with the pet.
	 * @param action The type of action to perform on the item.
	 * @param itemId The unique identifier for the item being used.
	 * @param slot The specific inventory slot for the item.
	 */
	public void useDoping(Player player, int action, int itemId, int slot)
	{
		final Pet pet = player.getPet();
		if ((pet == null) || (pet.getCommonData().getDopingBag() == null))
		{
			return;
		}
		
		if (action < 2)
		{
			// add, replace or delete item
			pet.getCommonData().getDopingBag().setItem(itemId, slot);
			action = 0;
		}
		else if (action == 3)
		{
			// use item
			final List<Item> items = player.getInventory().getItemsByItemId(itemId);
			for (;;)
			{
				final Item useItem = items.get(0);
				final ItemActions itemActions = useItem.getItemTemplate().getActions();
				final ItemUseLimits limit = new ItemUseLimits();
				int useDelay = player.getItemCooldown(useItem.getItemTemplate()) / 3;
				if (useDelay < 3000)
				{
					useDelay = 3000;
				}
				
				limit.setDelayId(useItem.getItemTemplate().getUseLimits().getDelayId());
				limit.setDelayTime(useDelay);
				
				if (player.isItemUseDisabled(limit))
				{
					final int useAction = action;
					final int useItemId = itemId;
					final int useSlot = slot;
					
					// schedule re-check
					ThreadPoolManager.getInstance().schedule(() -> PacketSendUtility.sendPacket(player, new SM_PET(useAction, useItemId, useSlot)), useDelay);
					return;
				}
				
				if (!RestrictionsManager.canUseItem(player, useItem) || player.isProtectionActive())
				{
					// client sends the correct restriction message with that
					player.addItemCoolDown(limit.getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);
					break;
				}
				
				player.getController().cancelCurrentSkill();
				for (AbstractItemAction itemAction : itemActions.getItemActions())
				{
					if (itemAction.canAct(player, useItem, null))
					{
						itemAction.act(player, useItem, null);
					}
				}
				break;
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_PET(action, itemId, slot));
		
		itemId = pet.getCommonData().getDopingBag().getFoodItem();
		long totalDopes = player.getInventory().getItemCountByItemId(itemId);
		
		itemId = pet.getCommonData().getDopingBag().getDrinkItem();
		totalDopes += player.getInventory().getItemCountByItemId(itemId);
		
		final int[] scrollBag = pet.getCommonData().getDopingBag().getScrollsUsed();
		for (int i = 0; i < scrollBag.length; i++)
		{
			if (scrollBag[i] != 0)
			{
				totalDopes += player.getInventory().getItemCountByItemId(scrollBag[i]);
			}
		}
		
		if (totalDopes == 0)
		{
			pet.getCommonData().setIsBuffing(false);
			PacketSendUtility.sendPacket(player, new SM_PET(1, false));
		}
	}
	
	/**
	 * Enables or disables the looting status for a player's pet.<br>
	 * This method updates the {@code isLooting} state in the pet's common data.<br>
	 * It also sends a system message if the loot is activated and the player is in a team.
	 * @param player The {@link Player} whose pet status will be updated.
	 * @param activate A boolean where {@code true} enables looting and {@code false} disables it.
	 */
	public void activateLoot(Player player, boolean activate)
	{
		if (player.getPet() == null)
		{
			return;
		}
		
		if (activate)
		{
			if (player.isInTeam())
			{
				final LootRuleType lootType = player.getLootGroupRules().getLootRule();
				if (lootType == LootRuleType.FREEFORALL)
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOTING_PET_MESSAGE03);
					return;
				}
			}
			
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOTING_PET_MESSAGE01);
		}
		
		player.getPet().getCommonData().setIsLooting(activate);
		PacketSendUtility.sendPacket(player, new SM_PET(activate));
	}
	
	/**
	 * Toggles the automatic buff effect for a player's pet.<br>
	 * This method checks if the player has enough food to activate the buff.<br>
	 * It applies or removes the {@code PetBuff} based on the provided boolean value.
	 * @param player The {@link Player} who owns the pet.
	 * @param activate Set to {@code true} to start the buff, or {@code false} to stop it.
	 */
	public void activateBuff(Player player, boolean activate)
	{
		if (player.getPet() == null)
		{
			return;
		}
		
		final Pet pet = player.getPet();
		final PetTemplate petTemp = DataManager.PET_DATA.getPetTemplate(pet.getPetId());
		final PetBonusAttr petBuff = DataManager.PET_BUFF_DATA.getPetBonusattr(petTemp.getPetFunction(PetFunctionType.BUFF).getId());
		
		if (activate && (player.getInventory().getItemCountByItemId(182007162) < petBuff.getFoodCount()))
		{// Aether Cherry
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_BUFF_PET_USE_STOP_MESSAGE_03);
			return;
		}
		
		if (activate)
		{
			autoBuff = true;
			PetBuff = new PetBuff(petBuff.getBuffId());
			PetBuff.applyEffect(player, 300000);
			player.getInventory().decreaseByItemId(182007162, petBuff.getFoodCount());
		}
		else
		{
			autoBuff = false;
			PetBuff.endEffect(player);
		}
	}
	
	/**
	 * Toggles the auto-sell feature for a player's pet.<br>
	 * This method updates the {@code isSeller} status in the pet's common data.<br>
	 * It logs whether the feature was enabled or disabled.
	 * @param player The {@link Player} who owns the pet.
	 * @param activate Set to {@code true} to enable auto-sell, or {@code false} to disable it.
	 */
	public void activeAutoSell(Player player, boolean activate)
	{
		final Pet pet = player.getPet();
		final PetTemplate petTemp = DataManager.PET_DATA.getPetTemplate(pet.getPetId());
		final PetMerchandEntry merchand = DataManager.PET_MERCHAND_DATA.getMerchandTemplate(petTemp.getPetFunction(PetFunctionType.MERCHANT).getId());
		if (player.getPet() == null)
		{
			return;
		}
		
		if (activate)
		{
			autoSeel = true;
			pet.getCommonData().setIsSeller(true);
			log.info("auto sell activated. merchand id " + merchand.getId() + " rate " + merchand.getRatePrice());
		}
		else
		{
			autoSeel = false;
			pet.getCommonData().setIsSeller(false);
			log.info("auto sell deactivated");
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out of the game.<br>
	 * This method schedules a background task to clean up resources.<br>
	 * It ensures that logout processing does not block the main thread.
	 * @param player The {@code Player} object who is logging out.
	 */
	public void onPlayerLogout(Player player)
	{
		if (autoBuff)
		{
			activateBuff(player, false);
		}
		
		if (autoSeel)
		{
			activeAutoSell(player, false);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final PetService instance = new PetService();
	}
}
