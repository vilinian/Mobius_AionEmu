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
package com.aionemu.gameserver.model.templates.item.actions;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.enchant.EnchantService;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Handles the logic for enchanting an item when a {@link Player} uses it.<br>
 * It interacts with the {@link EnchantService} to process the enchantment request.
 * @author Nemiroff, Wakizashi, vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnchantItemAction")
public class EnchantItemAction extends AbstractItemAction
{
	// Count of required supplements
	@XmlAttribute(name = "count")
	private int count;
	
	// Min level of enchantable item
	@XmlAttribute(name = "min_level")
	private Integer min_level;
	
	// Max level of enchantable item
	@XmlAttribute(name = "max_level")
	private Integer max_level;
	@XmlAttribute(name = "manastone_only")
	private boolean manastone_only;
	@XmlAttribute(name = "chance")
	private float chance;
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It checks for valid items, equipment status, and sufficient currency.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		final int EnchantKinah = EnchantService.EnchantKinah(targetItem);
		
		if (isSupplementAction())
		{
			return false;
		}
		
		if ((targetItem == null) || (parentItem == null))
		{
			// no item selected.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		
		if (targetItem.isEquipped() && (targetItem.getItemTemplate().getCategory() == ItemCategory.STIGMA))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_ENCHANT_EQUIPED);
			return false;
		}
		
		if (targetItem.isAmplified() && parentItem.getItemTemplate().isEnchantmentStone() && (player.getInventory().getKinah() < EnchantKinah))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return false;
		}
		
		if ((targetItem.canAmplify()) && parentItem.getItemTemplate().isEnchantmentStone() && (targetItem.getEnchantOrAuthorizeLevel() == targetItem.getItemTemplate().getMaxEnchantLevel()) && !targetItem.isAmplified())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_EXCEED_ENCHANT_CANNOT_01(new DescriptionId(targetItem.getNameId())));
			return false;
		}
		
		AchievementService.getInstance().onUpdateAchievementAction(player, parentItem.getItemId(), 1, AchievementActionType.ITEM_PLAY);
		return true;
	}
	
	/**
	 * Executes the enchantment action.<br>
	 * This method handles the logic when a {@link Player} uses an item to enchant another {@link Item}.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		act(player, parentItem, targetItem, null, 1);
	}
	
	// necessary overloading to not change AbstractItemAction
	/**
	 * Executes the enchantment process for a specific item.<br>
	 * It validates the items and plays an animation before processing the result.<br>
	 * The method handles different categories like Stigma, Enchantment, and Manastones.
	 * @param player The {@link Player} performing the action.
	 * @param parentItem The item used to perform the enchantment.
	 * @param targetItem The item being enchanted.
	 * @param supplementItem The optional item required for the enchantment.
	 * @param targetWeapon The ID of the weapon if a Manastone is used.
	 */
	public void act(Player player, Item parentItem, Item targetItem, Item supplementItem, int targetWeapon)
	{
		if ((supplementItem != null) && !checkSupplementLevel(player, supplementItem.getItemTemplate(), targetItem.getItemTemplate()))
		{
			return;
		}
		
		// Current enchant level
		final int currentEnchant = targetItem.getEnchantOrAuthorizeLevel();
		final boolean isSuccess = isSuccess(player, parentItem, targetItem, supplementItem, targetWeapon);
		int currentEnchantOrAuthorize = 0;
		switch (targetItem.getItemTemplate().getItemQuality())
		{
			case ANCIENT:
			case RELIC:
			case FINALITY:
				switch (targetItem.getItemTemplate().getEnchantType())
				{
					case PVP:
					{
						currentEnchantOrAuthorize = targetItem.getItemTemplate().getMaxAuthorize();
						break;
					}
					case PVE:
					{
						currentEnchantOrAuthorize = targetItem.getItemTemplate().getMaxEnchantLevel();
						break;
					}
					default:
						break;
				}
				
				if (currentEnchant == currentEnchantOrAuthorize)
				{
					System.out.println("Enchant 1");
					
					// You cannot enchant %0 any further.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_IT_CAN_NOT_BE_ENCHANTED_MORE_TIME(targetItem.getNameId()));
					return;
				}
				break;
			default:
				if ((targetItem.getItemTemplate().getArmorType() != ArmorType.WING) && !targetItem.getItemTemplate().getExceedEnchant() && (targetItem.getEnchantOrAuthorizeLevel() == 15) && ((parentItem.getItemTemplate().getTemplateId() / 1000000) == 166))
				{
					System.out.println("Enchant 2");
					
					// You cannot enchant %0 any further.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_IT_CAN_NOT_BE_ENCHANTED_MORE_TIME(targetItem.getNameId()));
					return;
				}
				break;
		}
		
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), targetItem.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), EnchantsConfig.ENCHANT_CAST_DELAY, 9));
		
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300457, new DescriptionId(targetItem.getNameId()))); // Enchant Item canceled
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), targetItem.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 11));
				player.getObserveController().removeObserver(this);
			}
		};
		player.getObserveController().attach(observer);
		
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(() ->
		{
			player.getObserveController().removeObserver(observer);
			
			// Enchantment stone
			switch (parentItem.getItemTemplate().getCategory())
			{
				case ENCHANTMENT:
				case AMPLIFICATION:
				{
					if (targetItem.getItemTemplate().getCategory() == ItemCategory.STIGMA)
					{
						EnchantService.enchantStigmaAct(player, parentItem, targetItem, currentEnchant, isSuccess);
					}
					else
					{
						// Item
						EnchantService.enchantItemAct(player, parentItem, targetItem, currentEnchant, isSuccess);
					}
					break;
				}
				case STIGMA:
				{
					if (parentItem.getItemTemplate().getCategory() == targetItem.getItemTemplate().getCategory())
					{
						EnchantService.enchantStigmaAct(player, parentItem, targetItem, currentEnchant, isSuccess);
					}
					break;
				}
				default:
					EnchantService.socketManastoneAct(player, parentItem, targetItem, targetWeapon, isSuccess);
					break;
			}
			
			if (CustomConfig.ENABLE_ENCHANT_ANNOUNCE)
			{
				switch (parentItem.getItemTemplate().getCategory())
				{
					case ENCHANTMENT:
					case AMPLIFICATION:
					{
						final Iterator<Player> iter = World.getInstance().getPlayersIterator();
						while (iter.hasNext())
						{
							final Player player2 = iter.next();
							switch (targetItem.getEnchantOrAuthorizeLevel())
							{
								case 15:
								{
									if ((player2.getRace() == player.getRace()) && isSuccess)
									{
										PacketSendUtility.sendPacket(player2, SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEEDED_15(player.getName(), targetItem.getItemTemplate().getNameId()));
									}
									break;
								}
								case 20:
								{
									if ((player2.getRace() == player.getRace()) && isSuccess)
									{
										PacketSendUtility.sendPacket(player2, SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEEDED_20(player.getName(), targetItem.getItemTemplate().getNameId()));
									}
									break;
								}
								default:
									break;
							}
						}
					}
					default:
						break;
				}
			}
			
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), targetItem.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, isSuccess ? 1 : 2));
		}, EnchantsConfig.ENCHANT_CAST_DELAY));
	}
	
	/**
	 * Checks if the enchantment or socketing action was successful.<br>
	 * It determines the correct service to call based on the {@code parentItem} category.
	 * @param player The {@link Player} performing the action.
	 * @param parentItem The item used to perform the action.
	 * @param targetItem The item being modified.
	 * @param supplementItem The material used for the process.
	 * @param targetWeapon The ID of the weapon involved in socketing.
	 * @return {@code true} if the action succeeded, otherwise {@code false}.
	 */
	private boolean isSuccess(Player player, Item parentItem, Item targetItem, Item supplementItem, int targetWeapon)
	{
		if (parentItem.getItemTemplate() != null)
		{
			switch (parentItem.getItemTemplate().getCategory())
			{
				case ENCHANTMENT:
				case AMPLIFICATION:
				case STIGMA:
				{
					return EnchantService.enchantItem(player, parentItem, targetItem, supplementItem);
				}
				default:
					return EnchantService.socketManastone(player, parentItem, targetItem, supplementItem, targetWeapon);
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the current count value.<br>
	 * This method returns the integer stored in the {@code count} field.
	 * @return The current count as an {@code int}.
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Retrieves the maximum level for an enchantment.<br>
	 * It returns {@code 0} if no value is defined.
	 * @return The maximum level as an {@code int}.
	 */
	private int getMaxLevel()
	{
		return max_level != null ? max_level : 0;
	}
	
	/**
	 * Retrieves the minimum level required for an item to be enchanted.<br>
	 * Returns {@code 0} if no specific level is defined.
	 * @return The minimum level as an {@code int}.
	 */
	private int getMinLevel()
	{
		return min_level != null ? min_level : 0;
	}
	
	/**
	 * Checks if the action requires only Manastones.<br>
	 * This is used to determine if additional supplements are needed.
	 * @return {@code true} if only Manastones are required, {@code false} otherwise.
	 */
	public boolean isManastoneOnly()
	{
		return manastone_only;
	}
	
	/**
	 * Retrieves the probability of this drop occurring.<br>
	 * The value is stored as a {@code float}.
	 * @return The drop chance value.
	 */
	public float getChance()
	{
		return chance;
	}
	
	/**
	 * Checks if the action requires a supplement item.<br>
	 * It evaluates conditions like level requirements or success chances.
	 * @return {@code true} if any supplement-related condition is met, {@code false} otherwise.
	 */
	private boolean isSupplementAction()
	{
		return (getMinLevel() > 0) || (getMaxLevel() > 0) || (getChance() > 0) || isManastoneOnly();
	}
	
	/**
	 * Verifies if the supplement item is compatible with the target item level.<br>
	 * This method checks the required level range for enchantment actions.<br>
	 * It returns {@code false} and sends a system message if the levels do not match.
	 * @param player The {@link Player} attempting to use the supplement.
	 * @param supplementTemplate The {@link ItemTemplate} of the supplement being used.
	 * @param targetItemTemplate The {@link ItemTemplate} of the item being enchanted.
	 * @return {@code true} if the levels are valid, otherwise {@code false}.
	 */
	private boolean checkSupplementLevel(Player player, ItemTemplate supplementTemplate, ItemTemplate targetItemTemplate)
	{
		// Is item manastone? True - check if player can use supplement
		if (supplementTemplate.getCategory() != ItemCategory.ENCHANTMENT)
		{
			// Check if max item level is ok for the enchant
			int minEnchantLevel = targetItemTemplate.getLevel();
			int maxEnchantLevel = targetItemTemplate.getLevel();
			
			final EnchantItemAction action = supplementTemplate.getActions().getEnchantAction();
			if (action != null)
			{
				if (action.getMinLevel() != 0)
				{
					minEnchantLevel = action.getMinLevel();
				}
				
				if (action.getMaxLevel() != 0)
				{
					maxEnchantLevel = action.getMaxLevel();
				}
			}
			
			if ((minEnchantLevel <= targetItemTemplate.getLevel()) && (maxEnchantLevel >= targetItemTemplate.getLevel()))
			{
				return true;
			}
			
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_ENCHANT_ASSISTANT_NO_RIGHT_ITEM);
			return false;
		}
		
		return true;
	}
}
