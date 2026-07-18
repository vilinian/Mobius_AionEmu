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
package com.aionemu.gameserver.services.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dao.PlayerLunaShopDAO;
import com.aionemu.gameserver.dao.PlayerWardrobeDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.LunaBuffBonus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerLunaShop;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.luna.LunaConsumeRewardsTemplate;
import com.aionemu.gameserver.model.templates.recipe.LunaComponent;
import com.aionemu.gameserver.model.templates.recipe.LunaComponentElement;
import com.aionemu.gameserver.model.templates.recipe.LunaTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_INSTANCE_BUFF;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SYSTEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SYSTEM_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Manages the logic and operations for the Luna Shop system.<br>
 * This service handles player interactions with {@link PlayerLunaShop} and processes related rewards and items.
 */
public class LunaShopService
{
	private final Logger log = LoggerFactory.getLogger(LunaShopService.class);
	PlayerWardrobeDAO wDAO = DAOManager.getDAO(PlayerWardrobeDAO.class);
	private boolean dailyGenerated = true;
	// private boolean specialGenerated = true;
	private boolean reciveBonus = false;
	private final List<Integer> DailyCraft = new ArrayList<>();
	private final List<Integer> SpecialCraft = new ArrayList<>();
	// private List<Integer> armors = new ArrayList<Integer>();
	// private List<Integer> pants = new ArrayList<Integer>();
	// private List<Integer> shoes = new ArrayList<Integer>();
	// private List<Integer> gloves = new ArrayList<Integer>();
	// private List<Integer> shoulders = new ArrayList<Integer>();
	// private List<Integer> weapons = new ArrayList<Integer>();
	
	/**
	 * Initializes the Luna system and schedules periodic tasks.<br>
	 * It checks if daily crafts exist and generates them if necessary.<br>
	 * This method also schedules a {@link CronService} task to reset free Luna and generate new crafts.
	 */
	public void init()
	{
		log.info("[LunaSystem] Luna Reset");
		final String daily = "0 0 9 1/1 * ? *";
		
		// String weekly = "0 0 9 ? * WED *";
		if (DailyCraft.size() == 0)
		{
			generateDailyCraft();
		}
		
		// if (SpecialCraft.size() == 0) {
		// generateSpecialCraft();
		// }
		
		CronService.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				dailyGenerated = false;
				generateDailyCraft();
				resetFreeLuna();
			}
		}, daily);
		
		// CronService.getInstance().schedule(new Runnable() {
		//
		// This method overrides the run method.
		// specialGenerated = false;
		// generateSpecialCraft();
		// }
		// }, weekly);
	}
	
	// public void generateSpecialCraft() {
	// if (SpecialCraft.size() > 0) {
	// SpecialCraft.clear();
	// }
	// armors.add(10029);
	// armors.add(10031);
	// armors.add(10033);
	// armors.add(10035);
	// pants.add(10037);
	// pants.add(10039);
	// pants.add(10041);
	// pants.add(10043);
	// shoes.add(10061);
	// shoes.add(10063);
	// shoes.add(10065);
	// shoes.add(10067);
	// gloves.add(10053);
	// gloves.add(10055);
	// gloves.add(10057);
	// gloves.add(10059);
	// shoulders.add(10045);
	// shoulders.add(10047);
	// shoulders.add(10049);
	// shoulders.add(10051);
	// weapons.add(10021);
	// weapons.add(10017);
	// weapons.add(10025);
	// weapons.add(10005);
	// weapons.add(10011);
	// weapons.add(10023);
	// weapons.add(10003);
	// weapons.add(10007);
	// weapons.add(10019);
	// weapons.add(10013);
	// weapons.add(10027);
	// weapons.add(10009);
	// weapons.add(10015);
	// weapons.add(10001);
	// int rnd = Rnd.get(1, 6);
	// switch (rnd) {
	// case 1:
	// SpecialCraft.addAll(weapons);
	// break;
	// case 2:
	// SpecialCraft.addAll(armors);
	// break;
	// case 3:
	// SpecialCraft.addAll(pants);
	// break;
	// case 4:
	// SpecialCraft.addAll(shoes);
	// break;
	// case 5:
	// SpecialCraft.addAll(gloves);
	// break;
	// case 6:
	// SpecialCraft.addAll(shoulders);
	// break;
	// }
	// if (!specialGenerated) {
	// updateSpecialCraft();
	// }
	// }
	
	/**
	 * Resets the free Luna data in the database.<br>
	 * This method clears all entries from {@code PlayerLunaShopDAO}.<br>
	 * It then calls {@code updateFreeLuna} to refresh the current state.
	 */
	public void resetFreeLuna()
	{
		DAOManager.getDAO(PlayerLunaShopDAO.class).delete();
		updateFreeLuna();
	}
	
	/**
	 * Sends the special craft system information to a specific player.<br>
	 * This method triggers the {@code SM_LUNA_SYSTEM_INFO} packet for the {@code SpecialCraft} type.
	 * @param player The {@link Player} who will receive the packet.
	 */
	public void sendSpecialCraft(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(2, 0, SpecialCraft));
	}
	
	// private void updateSpecialCraft() {
	// World.getInstance().doOnAllPlayers(new Visitor<Player>() {
	//
	// Visit the specified player.
	// PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(2, 0, SpecialCraft));
	// }
	// });
	// }
	
	/**
	 * Updates the free Luna shop state for all online players.<br>
	 * This method initializes a new {@code PlayerLunaShop} instance for every player.<br>
	 * It sets the persistent state to {@code UPDATE_REQUIRED} and saves the data via {@link PlayerLunaShopDAO}.
	 */
	private void updateFreeLuna()
	{
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				final PlayerLunaShop pls = new PlayerLunaShop(true, true, true);
				pls.setPersistentState(PersistentState.UPDATE_REQUIRED);
				player.setPlayerLunaShop(pls);
				DAOManager.getDAO(PlayerLunaShopDAO.class).store(player);
			}
		});
	}
	
	/**
	 * Generates a new set of daily craft recipes.<br>
	 * This method selects 5 random items from the {@code DataManager.LUNA_DATA}.<br>
	 * It clears any existing entries in the {@code DailyCraft} list before updating.<br>
	 * If no previous generation occurred, it calls {@code updateDailyCraft}.
	 */
	public void generateDailyCraft()
	{
		if (DailyCraft.size() > 0)
		{
			dailyGenerated = false;
			DailyCraft.clear();
		}
		
		final List<LunaTemplate> test = DataManager.LUNA_DATA.getLunaTemplatesAny();
		final Random rand = new Random();
		for (int i = 0; i < 5; i++)
		{
			final int randomIndex = rand.nextInt(test.size());
			final LunaTemplate randomElement = test.get(randomIndex);
			DailyCraft.add(randomElement.getId());
		}
		
		if (!dailyGenerated)
		{
			updateDailyCraft();
		}
	}
	
	/**
	 * Sends the daily craft information packet to a specific player.<br>
	 * This method uses {@code generateDailyCraft} data to update the client UI.
	 * @param player The {@code Player} object receiving the packet.
	 */
	public void sendDailyCraft(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(2, 1, DailyCraft));
	}
	
	/**
	 * Updates the daily craft status for all online players.<br>
	 * This method sends a {@code SM_LUNA_SYSTEM_INFO} packet to every player.<br>
	 * It sets the {@code dailyGenerated} flag to {@code true}.
	 */
	private void updateDailyCraft()
	{
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(2, 1, DailyCraft));
				dailyGenerated = true;
			}
		});
	}
	
	/**
	 * Updates the Luna account points for a specific {@link Player}.<br>
	 * This method sets the new point value and sends an {@code SM_LUNA_SYSTEM_INFO} packet to the client.
	 * @param player The {@link Player} object to update.
	 * @param point The new integer value for the Luna account points.
	 */
	public void lunaPointController(Player player, int point)
	{
		player.setLunaAccount(point);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0, player.getLunaAccount()));
	}
	
	/**
	 * Updates the number of Muni Keys for a specific player.<br>
	 * This method sets the {@code keys} value on the {@link Player} object.<br>
	 * It also sends an {@code SM_LUNA_SYSTEM_INFO} packet to the client.
	 * @param player The {@link Player} whose keys are being updated.
	 * @param keys The new number of Muni Keys to assign.
	 */
	public void muniKeysController(Player player, int keys)
	{
		player.setMuniKeys(keys);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(4));
	}
	
	/**
	 * Initializes the Luna Shop data and sends necessary system packets to the player.<br>
	 * This method ensures a {@code PlayerLunaShop} exists for the {@code Player}.<br>
	 * It also triggers special and daily craft logic upon login.
	 * @param player The {@code Player} object who is currently logging into the game.
	 */
	public void onLogin(Player player)
	{
		if (player.getPlayerLunaShop() == null)
		{
			final PlayerLunaShop pls = new PlayerLunaShop(true, true, true);
			pls.setPersistentState(PersistentState.UPDATE_REQUIRED);
			player.setPlayerLunaShop(pls);
			DAOManager.getDAO(PlayerLunaShopDAO.class).add(player.getObjectId(), pls.isFreeUnderpath(), pls.isFreeFactory(), pls.isFreeChest());
		}
		
		// PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(6));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(7));
		sendSpecialCraft(player);
		sendDailyCraft(player);
		for (int i = 0; i < 9; i++)
		{
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(8, i, 0));
		}
		
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(4, player.getMuniKeys()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(9, 0));
		
		if (!player.getPlayerLunaShop().isFreeUnderpath())
		{
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 45));
		}
		
		if (!player.getPlayerLunaShop().isFreeFactory())
		{
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 47));
		}
		
		if (!player.getPlayerLunaShop().isFreeChest())
		{
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 1));
		}
	}
	
	/**
	 * Handles the special design process for a specific recipe.<br>
	 * It checks if the {@code player} has enough materials to craft the item.<br>
	 * If successful, it removes components and grants the new item.<br>
	 * If unsuccessful, it only informs the player of the failure.
	 * @param player The {@code Player} attempting to perform the design.
	 * @param recipeId The unique identifier for the {@code LunaTemplate} recipe.
	 */
	public void specialDesign(Player player, int recipeId)
	{
		final LunaTemplate recipe = DataManager.LUNA_DATA.getLunaTemplateById(recipeId);
		final int product_id = recipe.getProductid();
		final int quantity = recipe.getQuantity();
		final ItemTemplate item = DataManager.ITEM_DATA.getItemTemplate(product_id);
		final boolean isSuccess = isSuccess(player, recipeId);
		if (isSuccess)
		{
			for (LunaComponent lc : recipe.getLunaComponent())
			{
				for (LunaComponentElement a : lc.getComponents())
				{
					if (!player.getInventory().decreaseByItemId(a.getItemid(), a.getQuantity()))
					{
						System.out.println("!!! Possible item hack CHEATER(?) !!!");
						PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(2, item, 1));
						PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(3, product_id, quantity, false));
						return;
					}
				}
			}
			
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(2, item, 0));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(3, product_id, quantity, true));
			ItemService.addItem(player, product_id, quantity);
		}
		else
		{
			for (LunaComponent lc : recipe.getLunaComponent())
			{
				for (LunaComponentElement a : lc.getComponents())
				{
					if (!player.getInventory().decreaseByItemId(a.getItemid(), a.getQuantity()))
					{
						System.out.println("!!! Possible item hack CHEATER(?) !!!");
						PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(2, item, 1));
						PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(3, product_id, quantity, false));
						return;
					}
				}
			}
			
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(2, item, 1));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(3, product_id, quantity, false));
		}
	}
	
	/**
	 * Crafts a Luna Material Chest for the player.<br>
	 * This method checks if the player has a free chest available.<br>
	 * If not, it deducts {@code 2} from the player's Luna account balance.<br>
	 * It then grants one item with ID {@code 188055460} to the player.
	 * @param player The {@link Player} who is performing the craft.
	 */
	public void craftBox(Player player)
	{
		final int itemId = 188055460;
		if (player.getPlayerLunaShop().isFreeChest())
		{
			player.getPlayerLunaShop().setFreeChest(false);
		}
		else
		{
			player.setLunaAccount(player.getLunaAccount() - 2);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0));
			
		}
		
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(3, itemId, 1, true));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 1));
		ItemService.addItem(player, itemId, 1); // Luna Material Chest
	}
	
	/**
	 * Checks if a crafting attempt was successful.<br>
	 * It compares a random number against the success rate of the recipe.
	 * @param player The {@code Player} attempting the craft.
	 * @param recipeId The unique identifier for the {@code LunaTemplate}.
	 * @return {@code true} if the craft succeeded, otherwise {@code false}.
	 */
	private boolean isSuccess(Player player, int recipeId)
	{
		final LunaTemplate recipe = DataManager.LUNA_DATA.getLunaTemplateById(recipeId);
		boolean result = false;
		final float random = Rnd.get(1, 100);
		if (recipe.getRate() == 100)
		{
			result = true;
		}
		else if (recipe.getRate() < 100)
		{
			if (random <= recipe.getRate())
			{
				result = true;
			}
			else
			{
				result = false;
			}
		}
		
		return result;
	}
	
	/**
	 * Allows a {@link Player} to purchase items using Luna points.<br>
	 * This method adds the specified quantity of an item to the player's inventory.<br>
	 * It then deducts the total cost from the player's account balance.
	 * @param player The {@link Player} who is making the purchase.
	 * @param itemId The unique identifier for the item being bought.
	 * @param count The amount of items to purchase.
	 */
	public void buyMaterials(Player player, int itemId, long count)
	{
		final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		final int lunaPrice = itemTemplate.getLunaPrice();
		final long price = count * lunaPrice;
		ItemService.addItem(player, itemId, count);
		player.setLunaAccount((player.getLunaAccount() - price));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(4, player.getMuniKeys()));
	}
	
	/**
	 * Loads the wardrobe data for a specific player.<br>
	 * This method retrieves the item count from {@code PlayerWardrobeDAO}.<br>
	 * It then sends an {@code SM_LUNA_SYSTEM} packet to the player with the slot and size information.
	 * @param player The {@code Player} object whose wardrobe needs to be loaded.
	 */
	public void dorinerkWardrobeLoad(Player player)
	{
		final int size = DAOManager.getDAO(PlayerWardrobeDAO.class).getItemSize(player.getObjectId());
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(8, player.getWardrobeSlot(), size));
	}
	
	/**
	 * Processes the action of applying an item to a specific wardrobe slot.<br>
	 * It checks if the slot is already occupied and deducts {@code 10} Luna Account points if it is.<br>
	 * The method updates the player's wardrobe and removes the used item from their inventory.
	 * @param player The {@link Player} performing the action.
	 * @param applySlot The index of the wardrobe slot to be updated.
	 * @param itemObjId The unique object ID of the item being applied.
	 */
	public void dorinerkWardrobeAct(Player player, int applySlot, int itemObjId)
	{
		final int itemId = player.getInventory().getItemByObjId(itemObjId).getItemId();
		final int itemOnDB = DAOManager.getDAO(PlayerWardrobeDAO.class).getWardrobeItemBySlot(player.getObjectId(), applySlot);
		if (itemOnDB != 0)
		{
			DAOManager.getDAO(PlayerWardrobeDAO.class).delete(player.getObjectId(), itemOnDB);
			player.setLunaAccount(player.getLunaAccount() - 10);
			player.getWardrobe().addItem(player, itemId, applySlot, 0);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0, player.getLunaAccount()));
		}
		else
		{
			player.getWardrobe().addItem(player, itemId, applySlot, 0);
		}
		
		player.getInventory().decreaseByObjectId(itemObjId, 1);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(10, 0x00, applySlot, itemId, 1));
	}
	
	/**
	 * Modifies the appearance of an item in the player's wardrobe.<br>
	 * This method applies a skin template to a specific item based on a slot.<br>
	 * It also updates the reskin count and deducts Luna Account points if applicable.
	 * @param player The {@code Player} object who is performing the action.
	 * @param applySlot The index of the wardrobe slot to use for the modification.
	 * @param itemObjId The unique identifier of the item being modified.
	 */
	public void dorinerkWardrobeModifyAppearance(Player player, int applySlot, int itemObjId)
	{
		final int itemId = DAOManager.getDAO(PlayerWardrobeDAO.class).getWardrobeItemBySlot(player.getObjectId(), applySlot);
		final int reskinCount = DAOManager.getDAO(PlayerWardrobeDAO.class).getReskinCountBySlot(player.getObjectId(), applySlot);
		final ItemTemplate it = DataManager.ITEM_DATA.getItemTemplate(itemId);
		final Storage inventory = player.getInventory();
		final Item keepItem = inventory.getItemByObjId(itemObjId);
		if (reskinCount != 0)
		{
			DAOManager.getDAO(PlayerWardrobeDAO.class).setReskinCountBySlot(player.getObjectId(), applySlot, reskinCount + 1);
			player.setLunaAccount(player.getLunaAccount() - 15);
			keepItem.setItemSkinTemplate(it);
			if (!keepItem.getItemTemplate().isItemDyePermitted())
			{
				keepItem.setItemColor(0);
			}
			
			keepItem.setLunaReskin(true);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0, player.getLunaAccount()));
		}
		else
		{
			DAOManager.getDAO(PlayerWardrobeDAO.class).setReskinCountBySlot(player.getObjectId(), applySlot, reskinCount + 1);
			keepItem.setItemSkinTemplate(it);
			if (!keepItem.getItemTemplate().isItemDyePermitted())
			{
				keepItem.setItemColor(0);
			}
			
			keepItem.setLunaReskin(true);
		}
		
		ItemPacketService.updateItemAfterInfoChange(player, keepItem, ItemUpdateType.STATS_CHANGE);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_SUCCEED(new DescriptionId(keepItem.getItemTemplate().getNameId())));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(11, applySlot));
	}
	
	/**
	 * Increases the number of available wardrobe slots for a player.<br>
	 * This method deducts the required price from the {@code LunaAccount}.<br>
	 * It then updates the player's slot count and sends the necessary system packets.
	 * @param player The {@link Player} who is extending their wardrobe.
	 */
	public void dorinerkWardrobeExtendSlots(Player player)
	{
		final int currentSlot = player.getWardrobeSlot();
		final int size = DAOManager.getDAO(PlayerWardrobeDAO.class).getItemSize(player.getObjectId());
		player.setWardrobeSlot(currentSlot + 1);
		player.setLunaAccount(player.getLunaAccount() - wardrobePrice(currentSlot + 1));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(9, player.getWardrobeSlot(), size));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(4, player.getMuniKeys()));
	}
	
	/**
	 * Starts the Taki Adventure for a specific player.<br>
	 * This method sends a {@code SM_LUNA_SYSTEM} packet to the player.<br>
	 * The packet includes the provided dungeon ID.
	 * @param player The {@link Player} who is starting the adventure.
	 * @param indun_id The unique identifier for the dungeon.
	 */
	public void takiAdventure(Player player, int indun_id)
	{
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(14, indun_id));
	}
	
	/**
	 * Teleports a player to a specific dungeon instance based on the provided IDs.<br>
	 * This method checks if the player has free entry for the dungeon.<br>
	 * If they do not have free entry, it deducts the required amount from their Luna account.<br>
	 * It handles teleportation logic and sends necessary system packets to the client.
	 * @param player The {@code Player} object to be teleported.
	 * @param indun_unk An unknown integer parameter used for internal logic.
	 * @param indun_id The unique identifier of the dungeon instance.
	 */
	public void takiAdventureTeleport(Player player, int indun_unk, int indun_id)
	{
		if (indun_id == 1)
		{
			if (player.getPlayerLunaShop().isFreeUnderpath())
			{
				final WorldMapInstance contaminatedUnderpath = InstanceService.getNextAvailableInstance(301630000);
				InstanceService.registerPlayerWithInstance(contaminatedUnderpath, player);
				TeleportService2.teleportTo(player, 301630000, contaminatedUnderpath.getInstanceId(), 230f, 169f, 164f, (byte) 60);
				player.getPlayerLunaShop().setLunaShopByObjId(player.getObjectId());
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 45));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(0, 0));
				player.getPlayerLunaShop().setFreeUnderpath(false);
			}
			else
			{
				final WorldMapInstance contaminatedUnderpath = InstanceService.getNextAvailableInstance(301630000);
				InstanceService.registerPlayerWithInstance(contaminatedUnderpath, player);
				TeleportService2.teleportTo(player, 301630000, contaminatedUnderpath.getInstanceId(), 230f, 169f, 164f, (byte) 60);
				player.setLunaAccount(player.getLunaAccount() - 89);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 45));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(0, 0));
			}
		}
		
		if (indun_id == 2)
		{
			if (player.getPlayerLunaShop().isFreeFactory())
			{
				final WorldMapInstance secretMunitionsFactory = InstanceService.getNextAvailableInstance(301640000);
				InstanceService.registerPlayerWithInstance(secretMunitionsFactory, player);
				TeleportService2.teleportTo(player, 301640000, secretMunitionsFactory.getInstanceId(), 400.3279f, 290.5061f, 198.64015f, (byte) 60);
				player.getPlayerLunaShop().setLunaShopByObjId(player.getObjectId());
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 47));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(0, 0));
				player.getPlayerLunaShop().setFreeFactory(false);
			}
			else
			{
				final WorldMapInstance secretMunitionsFactory = InstanceService.getNextAvailableInstance(301640000);
				InstanceService.registerPlayerWithInstance(secretMunitionsFactory, player);
				TeleportService2.teleportTo(player, 301640000, secretMunitionsFactory.getInstanceId(), 400.3279f, 290.5061f, 198.64015f, (byte) 60);
				player.setLunaAccount(player.getLunaAccount() - 59);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 47));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(0, 0));
			}
		}
	}
	
	/**
	 * Moves a player to a specific location based on an action type.<br>
	 * This method sends the corresponding {@code SM_LUNA_SYSTEM} packet to the user.
	 * @param player The {@link Player} object to be teleported.
	 * @param action The integer code representing the type of teleport action.
	 * @param teleportId The unique identifier for the destination location.
	 */
	public void teleport(Player player, int action, int teleportId)
	{
		switch (action)
		{
			case 6:
				PacketSendUtility.sendMessage(player, "teleportId : " + teleportId);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(6));
				break;
			case 7:
				PacketSendUtility.sendMessage(player, "teleportId : " + teleportId);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(7));
				break;
		}
	}
	
	/**
	 * Opens the Munirunerks Treasure Chamber for the player.<br>
	 * This method consumes one {@code muniKeys} if available.<br>
	 * If no keys exist, it deducts 19 from {@code lunaAccount} and adds points to {@code lunaConsumePoint}.<br>
	 * It rewards the player with random items based on their consumption count.
	 * @param player The {@link Player} who is opening the treasure chamber.
	 */
	public void munirunerksTreasureChamber(Player player)
	{
		final HashMap<Integer, Long> hm = new HashMap<>();
		hm.put(188054633, (long) 1); // [Event] Special Head Executor Weapon Box
		hm.put(188054634, (long) 1); // [Event] Special Head Executor Armor Box
		hm.put(166030013, (long) 1); // [Event] Tempering Solution
		hm.put(166020003, (long) 1); // [Event] Omega Enchantment Stone
		hm.put(188054122, (long) 1); // Major Stigma Bundle
		hm.put(188055183, (long) 1); // Major Felicitous Socketing Box (Mythic)
		hm.put(188054287, (long) 1); // Greater Stigma Bundle
		hm.put(188054462, (long) 1); // Illusion Godstone Bundle
		hm.put(188052639, (long) 1); // [Event] Heroic Godstone Bundle
		hm.put(169405339, (long) 10); // Pallasite Crystal
		hm.put(164000076, (long) 10); // Greater Running Scroll
		hm.put(164000134, (long) 10); // Greater Awakening Scroll
		hm.put(166000196, (long) 3); // Enchantment Stone
		hm.put(186000242, (long) 2); // Ceramium Medal
		hm.put(186000051, (long) 2); // Major Ancient Crown
		hm.put(188055168, (long) 10); // [Event] Blood Medal Box
		hm.put(188054283, (long) 30); // Blood Mark Box
		hm.put(188054463, (long) 1); // [Event] Fabled Godstone Bundle
		hm.put(188053002, (long) 1); // [Event] Noble Composite Manastone Bundle
		hm.put(188100335, (long) 2000); // Enchantment Stone Dust
		hm.put(164000073, (long) 10); // Greater Courage Scroll
		hm.put(160002497, (long) 1); // Fresh Oily Plucar Dragon Salad
		hm.put(160002499, (long) 1); // Fresh Oily Plucar Dragon Soup
		
		if (player.getMuniKeys() > 0)
		{
			player.setMuniKeys(player.getMuniKeys() - 1);
		}
		else
		{
			player.setLunaAccount(player.getLunaAccount() - 19);
			player.setLunaConsumePoint(player.getLunaConsumePoint() + 25);
			switch (player.getLunaConsumePoint())
			{
				case 25:
					reciveBonus = true;
					player.setLunaConsumeCount(1);
					break;
				case 50:
					reciveBonus = true;
					player.setLunaConsumeCount(2);
					break;
				case 100:
					reciveBonus = true;
					player.setLunaConsumeCount(3);
					muniKeysController(player, player.getMuniKeys() + 1);
					break;
				case 150:
					reciveBonus = true;
					player.setLunaConsumeCount(4);
					muniKeysController(player, player.getMuniKeys() + 1);
					break;
				case 300:
					reciveBonus = true;
					player.setLunaConsumeCount(5);
					muniKeysController(player, player.getMuniKeys() + 2);
					break;
				case 500:
					reciveBonus = true;
					player.setLunaConsumeCount(6);
					muniKeysController(player, player.getMuniKeys() + 2);
					break;
				case 1000:
					reciveBonus = true;
					player.setLunaConsumeCount(7);
					muniKeysController(player, player.getMuniKeys() + 3);
					break;
				default:
					reciveBonus = false;
					break;
			}
			
			if (reciveBonus)
			{
				final LunaConsumeRewardsTemplate lt = DataManager.LUNA_CONSUME_REWARDS_DATA.getLunaConsumeRewardsId(player.getLunaConsumeCount());
				ItemService.addItem(player, lt.getCreateItemId(), lt.getCreateItemCount());
			}
		}
		
		final HashMap<Integer, Long> mt = new HashMap<>();
		for (int i = 0; i < 3; i++)
		{
			final Object[] crunchifyKeys = hm.keySet().toArray();
			final Object key = crunchifyKeys[new Random().nextInt(crunchifyKeys.length)];
			mt.put((int) key, (long) hm.get(key));
		}
		
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				for (Map.Entry<Integer, Long> e : mt.entrySet())
				{
					ItemService.addItem(player, e.getKey(), e.getValue());
					final ItemTemplate t = DataManager.ITEM_DATA.getItemTemplate(e.getKey());
					if (e.getValue() == 1)
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_REWARD_GOTCHA_ITEM(t.getNameId()));
					}
					else if (e.getValue() > 1)
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_REWARD_GOTCHA_ITEM_MULTI(e.getValue(), t.getNameId()));
					}
				}
				
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(mt));
			}
		}, 1);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(4, player.getMuniKeys()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0, player.getLunaAccount()));
		
		// As you spend Luna, you can earn keys to open Munirunerks Treasure Chest.
		// If you do not have any keys, you can spend 3 Luna to open a chest immediately.
		// The Luna you spend on opening chests will also count towards your Luna Rewards!
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out.<br>
	 * It saves the {@code Kisk} object if the player has one.<br>
	 * This ensures the binding is kept while the player is offline.
	 * @param player The {@code Player} who is logging out.
	 */
	public void onLogout(Player player)
	{
		final PlayerLunaShop pls = player.getPlayerLunaShop();
		pls.setPersistentState(PersistentState.UPDATE_REQUIRED);
		DAOManager.getDAO(PlayerLunaShopDAO.class).store(player);
	}
	
	/**
	 * Calculates the price for a specific wardrobe slot.<br>
	 * It returns different values based on the {@code WardrobeSlot} index.<br>
	 * If the slot is not recognized, it returns {@code 0}.
	 * @param WardrobeSlot The index of the wardrobe slot to check.
	 * @return The price associated with the given slot.
	 */
	private int wardrobePrice(int WardrobeSlot)
	{
		// Done
		switch (WardrobeSlot)
		{
			case 1:
			case 2:
			case 3:
			case 4:
				return 69;
			case 5:
			case 6:
			case 7:
			case 8:
				return 99;
		}
		
		return 0;
	}
	
	/**
	 * Executes the Luna Dice Game logic for a specific player.<br>
	 * This method calculates a random result and updates the player's dice tries, points, and account balance.<br>
	 * It also sends the necessary system packets to update the client UI.
	 * @param player The {@link Player} object who is participating in the game.
	 */
	public void diceGame(Player player)
	{
		// TODO Golden Dice + Golden Price fix..
		final int random = Rnd.get(1, 1000);
		if ((random >= 100) && (random <= 400))
		{
			player.setLunaDiceGame(1, false);
		}
		else if ((random >= 450) && (random <= 749))
		{
			player.setLunaDiceGame(2, false);
		}
		else if ((random >= 750) && (random <= 849))
		{
			player.setLunaDiceGame(3, false);
		}
		else if ((random >= 850) && (random <= 900))
		{
			player.setLunaDiceGame(4, false);
		}
		else if ((random >= 950) && (random <= 1000))
		{
			player.setLunaDiceGame(5, false);
		}
		
		final int diceTry = player.getLunaDiceGameTry();
		
		if (diceTry < 1)
		{
			player.setLunaDiceGameTry(player.getLunaDiceGameTry() + 1);
			player.setLunaConsumePoint(player.getLunaConsumePoint() + lunaDicePrice(diceTry));
			player.setLunaAccount((player.getLunaAccount() - lunaDicePrice(diceTry)));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 78));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(15));
		}
		else
		{
			player.setLunaDiceGameTry(player.getLunaDiceGameTry() + 1);
			player.setLunaConsumePoint(player.getLunaConsumePoint() + lunaDicePrice(diceTry));
			player.setLunaAccount((player.getLunaAccount() - lunaDicePrice(diceTry)));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 79));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(15));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(4));
		}
		
		System.out.println("Random: " + random);
		System.out.println("Try: " + player.getLunaDiceGameTry());
		System.out.println("Consum: " + player.getLunaConsumePoint());
	}
	
	/**
	 * Awards the player with items for participating in the dice game.<br>
	 * This method grants a specific item and updates the player's game state.<br>
	 * It also sends necessary system packets to the client.
	 * @param player The {@link Player} who receives the reward.
	 */
	public void diceGameReward(Player player)
	{
		// TODO
		ItemService.addItem(player, 162001014, 4);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(16, 162001014, 4));
		player.setLunaDiceGame(0, true);
		player.setLunaDiceGameTry(0);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 78));
	}
	
	/**
	 * Calculates the price for a Luna dice roll.<br>
	 * This method returns a specific value based on the {@code diceTry} input.<br>
	 * It maps different attempt numbers to their corresponding costs.
	 * @param diceTry The number of times the player has tried the dice.
	 * @return The calculated price as an {@code int}.
	 */
	public int lunaDicePrice(int diceTry)
	{
		// Done
		switch (diceTry)
		{
			case 0:
				return 20;
			case 1:
				return 22;
			case 2:
				return 24;
			case 3:
				return 26;
			case 4:
			case 5:
				return 30;
			case 6:
				return 32;
			case 7:
				return 36;
			case 8:
			case 9:
				return 38;
			case 10:
				return 40;
			default:
				return 40;
		}
	}
	
	/**
	 * Sends a Luna instance buff packet to the specified player.<br>
	 * This method uses {@code sendPacket} to deliver the data.
	 * @param player The {@code Player} who will receive the buff packet.
	 * @param buffId The unique identifier for the buff to be sent.
	 */
	public void sendLunaInstanceBuff(Player player, int buffId)
	{
		PacketSendUtility.sendPacket(player, new SM_LUNA_INSTANCE_BUFF(buffId, false));
	}
	
	/**
	 * Allows a {@link Player} to purchase a specific buff using Luna points.<br>
	 * This method checks if the player has at least {@code 20} Luna points.<br>
	 * It deducts the cost and applies the effect of the requested {@code buffId}.
	 * @param player The {@link Player} who is purchasing the buff.
	 * @param buffId The unique identifier for the buff to be purchased.
	 */
	public void buyLunaBuff(Player player, int buffId)
	{
		if (player.getLunaAccount() < 20)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
			return;
		}
		
		player.setLunaAccount(player.getLunaAccount() - 20);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(0));
		player.setLunaBuffBonus(new LunaBuffBonus(buffId));
		player.getLunaBuffBonus().applyEffect(player);
		PacketSendUtility.sendPacket(player, new SM_LUNA_INSTANCE_BUFF(buffId, true));
		PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_QUNABUFF_SUCCEEDED, 3000);
	}
	
	/**
	 * Provides the global instance of the {@link LunaShopService}.<br>
	 * This method follows the singleton pattern to ensure only one service exists.
	 * @return The active {@code LunaShopService} instance.
	 */
	public static LunaShopService getInstance()
	{
		return NewSingletonHolder.INSTANCE;
	}
	
	private static class NewSingletonHolder
	{
		private static final LunaShopService INSTANCE = new LunaShopService();
	}
}
