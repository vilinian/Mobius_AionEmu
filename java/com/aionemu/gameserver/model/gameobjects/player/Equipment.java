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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemUseLimits;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.StigmaService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * Represents the equipment currently worn by a {@link com.aionemu.gameserver.model.gameobjects.player.Player}.<br>
 * This class manages the collection of items equipped in specific slots and handles their associated effects.<br>
 * It provides data for calculating player stats and updating visual appearances.
 * @author Avol, ATracer, kosyachok
 * @modified cura
 */
public class Equipment
{
	private static final Logger log = LoggerFactory.getLogger(Equipment.class);
	private final SortedMap<Long, Item> equipment = new TreeMap<>();
	private Player owner;
	private final Set<Long> markedFreeSlots = new HashSet<>();
	private PersistentState persistentState = PersistentState.UPDATED;
	private static final long[] ARMOR_SLOTS = new long[]
	{
		ItemSlot.BOOTS.getSlotIdMask(),
		ItemSlot.GLOVES.getSlotIdMask(),
		ItemSlot.PANTS.getSlotIdMask(),
		ItemSlot.SHOULDER.getSlotIdMask(),
		ItemSlot.TORSO.getSlotIdMask()
	};
	
	/**
	 * Creates a new {@code Equipment} instance for a specific player.<br>
	 * This constructor sets the owner of the equipment to the provided {@link Player}.
	 * @param player The {@link Player} who will own this equipment.
	 */
	public Equipment(Player player)
	{
		owner = player;
	}
	
	/**
	 * Equips a specific item to the player's equipment.<br>
	 * This method validates if the player meets all requirements such as class, level, race, and gender.<br>
	 * It also checks for slot availability and handles soul binding logic.
	 * @param itemUniqueId The unique identifier of the {@link Item} to equip.
	 * @param slot The target equipment slot ID.
	 * @return The successfully equipped {@link Item}, or {@code null} if the action fails.
	 */
	public Item equipItem(int itemUniqueId, long slot)
	{
		final Item item = owner.getInventory().getItemByObjId(itemUniqueId);
		
		if (item == null)
		{
			return null;
		}
		
		final ItemTemplate itemTemplate = item.getItemTemplate();
		
		if (!item.getItemTemplate().isClassSpecific(owner.getCommonData().getPlayerClass()))
		{
			// Your Class cannot use the selected item
			PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_CLASS);
			return null;
		}
		
		final int requiredLevel = item.getItemTemplate().getRequiredLevel(owner.getCommonData().getPlayerClass()) - item.getReductionLevel();
		if ((requiredLevel == -1) || (requiredLevel > owner.getLevel()))
		{
			// You cannot use %1 until you reach level %0
			PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(item.getNameId(), itemTemplate.getLevel()));
			return null;
		}
		
		if ((itemTemplate.getRace() != Race.PC_ALL) && (itemTemplate.getRace() != owner.getRace()))
		{
			// Your race cannot use this item
			PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_RACE);
			return null;
		}
		
		final ItemUseLimits limits = itemTemplate.getUseLimits();
		if ((limits.getGenderPermitted() != null) && (limits.getGenderPermitted() != owner.getGender()))
		{
			// This item cannot be used by your gender
			PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_GENDER);
			return null;
		}
		
		if (!verifyRankLimits(item))
		{
			// You cannot use the selected item until you reach the %0 rank
			PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_RANK(AbyssRankEnum.getRankById(limits.getMinRank()).getDescriptionId()));
			return null;
		}
		
		long itemSlotToEquip = 0;
		
		synchronized (equipment)
		{
			markedFreeSlots.clear();
			
			// validate item against current equipment and mark free slots
			final long oldSlot = item.getEquipmentSlot();
			item.setEquipmentSlot(slot);
			switch (item.getEquipmentType())
			{
				case ARMOR:
					if (!validateEquippedArmor(item, true))
					{
						item.setEquipmentSlot(oldSlot);
						return null;
					}
					break;
				case WEAPON:
					if (!validateEquippedWeapon(item, true))
					{
						item.setEquipmentSlot(oldSlot);
						return null;
					}
					break;
				default:
					break;
			}
			
			// check whether there is already item in specified slot
			long itemSlotMask = 0;
			switch (item.getItemTemplate().getCategory())
			{
				case STIGMA:
				case ESTIMA:
					itemSlotMask = slot;
					break;
				default:
					itemSlotMask = itemTemplate.getItemSlot();
					break;
			}
			
			final ItemSlot[] possibleSlots = ItemSlot.getSlotsFor(itemSlotMask);
			
			// find correct slot
			for (int i = 0; i < possibleSlots.length; i++)
			{
				final ItemSlot possibleSlot = possibleSlots[i];
				final long slotId = possibleSlot.getSlotIdMask();
				if ((equipment.get(slotId) == null) || markedFreeSlots.contains(slotId))
				{
					if (item.getItemTemplate().isTwoHandWeapon())
					{
						itemSlotMask &= ~slotId;
					}
					else
					{
						itemSlotToEquip = slotId;
						break;
					}
				}
			}
			
			if (item.getItemTemplate().isTwoHandWeapon())
			{
				if (itemSlotMask != 0)
				{
					return null;
				}
				
				itemSlotToEquip = itemTemplate.getItemSlot();
			}
			
			if (!StigmaService.notifyEquipAction(owner, item, slot))
			{
				return null;
			}
			
			// equip first occupied slot if there is no free
			if (itemSlotToEquip == 0)
			{
				itemSlotToEquip = possibleSlots[0].getSlotIdMask();
			}
		}
		
		if (itemSlotToEquip == 0)
		{
			return null;
		}
		
		if (itemTemplate.isSoulBound() && !item.isSoulBound())
		{
			soulBindItem(owner, item, itemSlotToEquip);
			return null;
		}
		
		return equip(itemSlotToEquip, item);
	}
	
	/**
	 * Equips a specific {@code Item} into the designated slot.<br>
	 * This method handles inventory removal, unequip logic for existing items, and validation.<br>
	 * It also updates player stats and notifies relevant systems after successful equipment.
	 * @param itemSlotToEquip The unique identifier for the equipment slot.
	 * @param item The {@code Item} object to be equipped.
	 * @return The successfully equipped {@code Item}, or {@code null} if the operation fails.
	 */
	private Item equip(long itemSlotToEquip, Item item)
	{
		if (item.getOptionalSocket() == -1)
		{
			log.warn("item can't be equiped because hasTune" + item.getObjectId());
			return null;
		}
		
		synchronized (equipment)
		{
			final ItemSlot[] allSlots = ItemSlot.getSlotsFor(itemSlotToEquip);
			if ((allSlots.length > 1) && !item.getItemTemplate().isTwoHandWeapon())
			{
				throw new IllegalArgumentException("itemSlotToEquip can not be composite!");
			}
			
			// remove item first from inventory to have at least one slot free
			owner.getInventory().remove(item);
			
			// do unequip of necessary items
			final Item equippedItem = equipment.get(allSlots[0].getSlotIdMask());
			if (equippedItem != null)
			{
				if (equippedItem.getItemTemplate().isTwoHandWeapon())
				{
					unEquip(equippedItem.getEquipmentSlot());
				}
				else
				{
					for (ItemSlot slot : allSlots)
					{
						unEquip(slot.getSlotIdMask());
					}
				}
			}
			
			switch (item.getEquipmentType())
			{
				case ARMOR:
					validateEquippedArmor(item, false);
					break;
				case WEAPON:
					validateEquippedWeapon(item, false);
					break;
				default:
					break;
			}
			
			if (equipment.get(allSlots[0].getSlotIdMask()) != null)
			{
				log.error("CHECKPOINT : putting item to already equiped slot. Info slot: " + itemSlotToEquip + " new item: " + item.getItemTemplate().getTemplateId() + " old item: " + equipment.get(allSlots[0].getSlotIdMask()).getItemTemplate().getTemplateId());
				return null;
			}
			
			// equip target item
			for (ItemSlot slot : allSlots)
			{
				equipment.put(slot.getSlotIdMask(), item);
			}
			
			item.setEquipped(true);
			item.setEquipmentSlot(itemSlotToEquip);
			ItemPacketService.updateItemAfterEquip(owner, item);
			
			// update stats
			notifyItemEquipped(item);
			owner.getLifeStats().updateCurrentStats();
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			QuestEngine.getInstance().onEquipItem(new QuestEnv(null, owner, 0, 0), item.getItemId());
			
			return item;
		}
	}
	
	/**
	 * Updates the game state when a player puts on an item.<br>
	 * This method triggers listeners and notifies observers.<br>
	 * It also refreshes the stats for any active summons.
	 * @param item The {@code Item} that was just equipped.
	 */
	private void notifyItemEquipped(Item item)
	{
		ItemEquipmentListener.onItemEquipment(item, owner);
		owner.getObserveController().notifyItemEquip(item, owner);
		tryUpdateSummonStats();
	}
	
	/**
	 * Notifies the system that an item has been removed from equipment.<br>
	 * This method triggers listeners and updates summon statistics.
	 * @param item The {@code Item} object that was unequipped.
	 */
	private void notifyItemUnequip(Item item)
	{
		ItemEquipmentListener.onItemUnequipment(item, owner);
		owner.getObserveController().notifyItemUnEquip(item, owner);
		tryUpdateSummonStats();
	}
	
	/**
	 * Updates the statistics of the player's summon.<br>
	 * This method checks if the owner has a {@link Summon}.<br>
	 * If a summon exists, it calls {@code updateStatsAndSpeedVisually()} on its game stats.
	 */
	private void tryUpdateSummonStats()
	{
		final Summon summon = owner.getSummon();
		if (summon != null)
		{
			summon.getGameStats().updateStatsAndSpeedVisually();
		}
	}
	
	/**
	 * Removes an equipped item from the player and returns it.<br>
	 * This method checks if the inventory has enough space before proceeding.<br>
	 * It also handles special logic for off-hand weapons and power shards.
	 * @param itemUniqueId The unique identifier of the item to remove.
	 * @param slot The equipment slot where the item is located.
	 * @return The {@code Item} that was removed, or {@code null} if the action failed.
	 */
	public Item unEquipItem(int itemUniqueId, long slot)
	{
		// if inventory is full unequip action is disabled
		if (owner.getInventory().isFull())
		{
			PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_UI_INVENTORY_FULL);
			return null;
		}
		
		synchronized (equipment)
		{
			Item itemToUnequip = null;
			
			for (Item item : equipment.values())
			{
				if (item.getObjectId() == itemUniqueId)
				{
					itemToUnequip = item;
					break;
				}
			}
			
			if ((itemToUnequip == null) || !itemToUnequip.isEquipped())
			{
				return null;
			}
			
			// Looks very odd - but its retail like
			if (itemToUnequip.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask())
			{
				final Item ohWeapon = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
				if ((ohWeapon != null) && ohWeapon.getItemTemplate().isWeapon())
				{
					if (owner.getInventory().getFreeSlots() < 2)
					{
						return null;
					}
					
					unEquip(ItemSlot.SUB_HAND.getSlotIdMask());
				}
			}
			
			// if unequip power shard
			if (itemToUnequip.getItemTemplate().isArmor() && (itemToUnequip.getItemTemplate().getCategory() == ItemCategory.SHARD))
			{
				owner.unsetState(CreatureState.POWERSHARD);
				PacketSendUtility.sendPacket(owner, new SM_EMOTION(owner, EmotionType.POWERSHARD_OFF, 0, 0));
			}
			
			if (!StigmaService.notifyUnequipAction(owner, itemToUnequip))
			{
				return null;
			}
			
			unEquip(itemToUnequip.getEquipmentSlot());
			
			return itemToUnequip;
		}
	}
	
	/**
	 * Removes an item from a specific equipment slot.<br>
	 * This method updates the player stats and moves the item back to the inventory.<br>
	 * It handles composite slots like two-handed weapons automatically.
	 * @param slot The unique identifier for the equipment slot.
	 */
	private void unEquip(long slot)
	{
		final ItemSlot[] allSlots = ItemSlot.getSlotsFor(slot);
		final Item item = equipment.remove(allSlots[0].getSlotIdMask());
		if (item == null)
		{
			// NPE check, there is no item in the given slot.
			return;
		}
		
		if (allSlots.length > 1)
		{
			if (!item.getItemTemplate().isTwoHandWeapon())
			{
				equipment.put(allSlots[0].getSlotIdMask(), item);
				throw new IllegalArgumentException("slot can not be composite!");
			}
			
			equipment.remove(allSlots[1].getSlotIdMask());
		}
		
		item.setEquipped(false);
		item.setEquipmentSlot(0);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		notifyItemUnequip(item);
		owner.getLifeStats().updateCurrentStats();
		owner.getGameStats().updateStatsAndSpeedVisually();
		owner.getInventory().put(item);
	}
	
	/**
	 * Checks if the player has any dual wielding skills.<br>
	 * It looks for specific skill IDs in the player's skill list.
	 * @return {@code true} if at least one dual wielding skill is present, {@code false} otherwise.
	 */
	private boolean hasDualWieldingSkills()
	{
		return owner.getSkillList().isSkillPresent(55) || owner.getSkillList().isSkillPresent(171) || owner.getSkillList().isSkillPresent(143) || owner.getSkillList().isSkillPresent(144) || owner.getSkillList().isSkillPresent(207);
	}
	
	/**
	 * Validates if a weapon can be equipped based on skills, slot availability, and item types.<br>
	 * It checks for two-handed weapon restrictions and dual-wielding requirements.<br>
	 * If {@code validateOnly} is {@code false}, it will automatically unequip conflicting items.
	 * @param item The {@link Item} to be validated.
	 * @param validateOnly Set to {@code true} to check validity without modifying the equipment.
	 * @return {@code true} if the weapon can be equipped, {@code false} otherwise.
	 */
	private boolean validateEquippedWeapon(Item item, boolean validateOnly)
	{
		// Disable arrow equipment
		if (item.getItemTemplate().getArmorType() == ArmorType.ARROW)
		{
			return false;
		}
		
		// check present skill
		final int[] requiredSkills = item.getItemTemplate().getWeaponType().getRequiredSkills();
		
		if (!checkAvailableEquipSkills(requiredSkills))
		{
			return false;
		}
		
		Item itemInRightHand, itemInLeftHand;
		long rightSlot, leftSlot;
		if ((item.getEquipmentSlot() & ItemSlot.MAIN_OR_SUB.getSlotIdMask()) != 0)
		{
			rightSlot = ItemSlot.MAIN_HAND.getSlotIdMask();
			leftSlot = ItemSlot.SUB_HAND.getSlotIdMask();
			itemInRightHand = equipment.get(rightSlot);
			itemInLeftHand = equipment.get(leftSlot);
		}
		else if ((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_OR_SUB_OFF.getSlotIdMask()) != 0)
		{
			rightSlot = ItemSlot.MAIN_OFF_HAND.getSlotIdMask();
			leftSlot = ItemSlot.SUB_OFF_HAND.getSlotIdMask();
			itemInRightHand = equipment.get(rightSlot);
			itemInLeftHand = equipment.get(leftSlot);
		}
		else
		{
			return false;
		}
		
		// for dual weapons, they occupy two slots, so check if the same item
		if (itemInRightHand == itemInLeftHand)
		{
			itemInLeftHand = null;
		}
		
		int requiredInventorySlots = 0;
		final boolean mainIsTwoHand = (itemInRightHand != null) && itemInRightHand.getItemTemplate().isTwoHandWeapon();
		
		if (item.getItemTemplate().isTwoHandWeapon())
		{
			if (mainIsTwoHand)
			{
				if (validateOnly)
				{
					requiredInventorySlots++;
					markedFreeSlots.add(rightSlot);
					markedFreeSlots.add(leftSlot);
				}
				else
				{
					unEquip(rightSlot | leftSlot);
				}
			}
			else
			{
				if (itemInRightHand != null)
				{
					if (validateOnly)
					{
						requiredInventorySlots++;
						markedFreeSlots.add(rightSlot);
					}
					else
					{
						unEquip(rightSlot);
					}
				}
				
				if (itemInLeftHand != null)
				{
					if (validateOnly)
					{
						requiredInventorySlots++;
						markedFreeSlots.add(leftSlot);
					}
					else
					{
						unEquip(leftSlot);
					}
				}
			}
		}
		else
		{
			// adding one-handed weapon
			if (itemInRightHand != null)
			{
				// main hand is already occupied
				final boolean addingLeftHand = (item.getEquipmentSlot() & ItemSlot.LEFT_HAND.getSlotIdMask()) != 0;
				
				// if occupied by 2H weapon, we have to unequip both slots, skills are not required
				if (mainIsTwoHand)
				{
					if (validateOnly)
					{
						requiredInventorySlots++;
						markedFreeSlots.add(rightSlot);
						markedFreeSlots.add(leftSlot);
					}
					else
					{
						unEquip(rightSlot | leftSlot);
					}
					
				} // main hand is already occupied and adding unknown hand, needs skills to be checked
				else if (hasDualWieldingSkills())
				{
					// if adding to empty left hand that is ok
					if ((itemInLeftHand == null) && addingLeftHand)
					{
						switch (owner.getPlayerClass())
						{
							case SCOUT:
							case ASSASSIN:
							case RANGER:
							case GUNNER:
							case GLADIATOR:
								return true;
							default:
								unEquip(rightSlot);
								return false;
						}
					}
					
					final long switchSlot = addingLeftHand ? leftSlot : rightSlot;
					
					if (validateOnly)
					{
						requiredInventorySlots++;
						markedFreeSlots.add(switchSlot);
					}
					else
					{
						unEquip(switchSlot);
					}
				}
				else
				{
					// requiredInventorySlots are 0
					if (addingLeftHand && (itemInLeftHand != null))
					{
						// this is not good, if inventory is full, should switch slots
						if (validateOnly)
						{
							markedFreeSlots.add(leftSlot);
						}
						
						// Dual weapons occupy two slots, and players cannot equip two one-handed weapons between versions 4.9 and 5.1.
						return false;
						// unEquip(leftSlot);
					}
					
					// Replace the main hand regardless of which slot is equipped, as the client sends slot 2 even for a double-click.
					if (validateOnly)
					{
						markedFreeSlots.add(rightSlot);
					}
					else
					{
						unEquip(rightSlot);
					}
					
					item.setEquipmentSlot(rightSlot);
					return true;
				}
			}
		}
		
		// check again = required slots
		return (requiredInventorySlots == 0) || (owner.getInventory().getFreeSlots() >= requiredInventorySlots);
	}
	
	/**
	 * Checks if the player possesses any of the required skills.<br>
	 * Returns {@code true} if no skills are required or if at least one skill is found.
	 * @param requiredSkills An array of {@code int} IDs representing the necessary skills.
	 * @return {@code true} if a valid skill is present, otherwise {@code false}.
	 */
	private boolean checkAvailableEquipSkills(int[] requiredSkills)
	{
		boolean isSkillPresent = false;
		
		// if no skills required - validate as true
		if (requiredSkills.length == 0)
		{
			return true;
		}
		
		for (int skill : requiredSkills)
		{
			if (owner.getSkillList().isSkillPresent(skill))
			{
				isSkillPresent = true;
				break;
			}
		}
		
		return isSkillPresent;
	}
	
	/**
	 * Checks if an item can be equipped as armor.<br>
	 * It verifies required skills and handles conflicts with two-handed weapons.
	 * @param item The {@code Item} to validate.
	 * @param validateOnly If {@code true}, only checks validity without removing items.
	 * @return {@code true} if the item can be equipped, {@code false} otherwise.
	 */
	private boolean validateEquippedArmor(Item item, boolean validateOnly)
	{
		// allow wearing of jewelry etc stuff
		final ArmorType armorType = item.getItemTemplate().getArmorType();
		if (armorType == null)
		{
			return true;
		}
		
		if (armorType == ArmorType.ARROW)
		{
			return false;
		}
		
		// check present skill
		final int[] requiredSkills = armorType.getRequiredSkills();
		if (!checkAvailableEquipSkills(requiredSkills))
		{
			return false;
		}
		
		ItemSlot slotToCheck1 = ItemSlot.MAIN_HAND;
		ItemSlot slotToCheck2 = ItemSlot.SUB_HAND;
		if ((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_OR_SUB_OFF.getSlotIdMask()) != 0)
		{
			slotToCheck1 = ItemSlot.MAIN_OFF_HAND;
			slotToCheck2 = ItemSlot.SUB_OFF_HAND;
		}
		
		final Item itemInMainHand = equipment.get(slotToCheck1.getSlotIdMask());
		if ((itemInMainHand != null) && (armorType == ArmorType.SHIELD) && itemInMainHand.getItemTemplate().isTwoHandWeapon())
		{
			if (validateOnly)
			{
				if (owner.getInventory().isFull())
				{
					return false;
				}
				
				markedFreeSlots.add(slotToCheck1.getSlotIdMask());
				markedFreeSlots.add(slotToCheck2.getSlotIdMask());
			}
			else
			{
				// remove 2H weapon
				unEquip(slotToCheck1.getSlotIdMask() | slotToCheck2.getSlotIdMask());
			}
		}
		
		return true;
	}
	
	/**
	 * Finds an equipped {@link Item} based on its unique object ID.<br>
	 * This method searches through all currently equipped items.<br>
	 * It returns the matching item if found, or {@code null} otherwise.
	 * @param value The unique object ID of the item to find.
	 * @return The {@link Item} with the matching ID, or {@code null}.
	 */
	public Item getEquippedItemByObjId(int value)
	{
		synchronized (equipment)
		{
			for (Item item : equipment.values())
			{
				if (item.getObjectId() == value)
				{
					return item;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a list of items currently equipped by the player that match a specific template ID.<br>
	 * This method searches through all equipped slots to find matching {@link Item} objects.
	 * @param value The unique template ID to search for.
	 * @return A {@code List<Item>} containing all equipped items with the specified ID, or an empty list if none are found.
	 */
	public List<Item> getEquippedItemsByItemId(int value)
	{
		final List<Item> equippedItemsById = new ArrayList<>();
		synchronized (equipment)
		{
			for (Item item : equipment.values())
			{
				if (item.getItemTemplate().getTemplateId() == value)
				{
					equippedItemsById.add(item);
				}
			}
		}
		
		return equippedItemsById;
	}
	
	/**
	 * Retrieves all items currently equipped by the player.<br>
	 * This method collects every {@link Item} from the equipment slots.
	 * @return A {@code List} of all equipped {@link Item} objects.
	 */
	public List<Item> getEquippedItems()
	{
		final HashSet<Item> equippedItems = new HashSet<>();
		for (Item i : equipment.values())
		{
			equippedItems.add(i);
		}
		
		return Arrays.asList(equippedItems.toArray(new Item[0]));
	}
	
	/**
	 * Retrieves a list of unique IDs for all currently equipped items.<br>
	 * This method iterates through the {@code equipment} map to collect every {@code Item}.<br>
	 * It returns the IDs as a {@code List<Integer>}.
	 * @return A {@code List<Integer>} containing the IDs of all equipped items.
	 */
	public List<Integer> getEquippedItemIds()
	{
		final HashSet<Integer> equippedIds = new HashSet<>();
		for (Item i : equipment.values())
		{
			equippedIds.add(i.getItemId());
		}
		
		return Arrays.asList(equippedIds.toArray(new Integer[0]));
	}
	
	/**
	 * Retrieves a list of items currently equipped by the player.<br>
	 * This method excludes any items located in {@code isStigma} slots.<br>
	 * It also ensures that only one two-handed weapon is included in the result.
	 * @return A {@code List} containing the filtered {@link Item} objects.
	 */
	public List<Item> getEquippedItemsWithoutStigma()
	{
		final List<Item> equippedItems = new ArrayList<>();
		Item twoHanded = null;
		for (Item item : equipment.values())
		{
			if (!ItemSlot.isStigma(item.getEquipmentSlot()))
			{
				if (item.getItemTemplate().isTwoHandWeapon())
				{
					if (twoHanded != null)
					{
						continue;
					}
					
					twoHanded = item;
				}
				
				equippedItems.add(item);
			}
		}
		
		return equippedItems;
	}
	
	/**
	 * Retrieves a list of currently equipped items.<br>
	 * This method excludes items located in {@code Stigma} slots.<br>
	 * It also handles logic to prevent multiple two-handed weapons from being counted.
	 * @return A {@code List} containing the filtered {@link Item} objects.
	 */
	public List<Item> getEquippedItemsWithoutStigmaOld()
	{
		final List<Item> equippedItems = new ArrayList<>();
		Item twoHanded = null;
		Item offTwoHanded = null;
		for (Item item : equipment.values())
		{
			if (!ItemSlot.isStigma(item.getEquipmentSlot()))
			{
				if (item.getItemTemplate().isTwoHandWeapon())
				{
					if (((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_OR_SUB_OFF.getSlotIdMask()) != 0) && (offTwoHanded != null))
					{
						continue;
					}
					else if ((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_OR_SUB_OFF.getSlotIdMask()) != 0)
					{
						offTwoHanded = item;
					}
					
					if (((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_OR_SUB_OFF.getSlotIdMask()) == 0) && (twoHanded != null))
					{
						continue;
					}
					else if ((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_OR_SUB_OFF.getSlotIdMask()) == 0)
					{
						twoHanded = item;
					}
				}
				
				equippedItems.add(item);
			}
		}
		
		return equippedItems;
	}
	
	/**
	 * Retrieves a list of items currently equipped for appearance.<br>
	 * This method filters out specific stigma slots and handles two-handed weapon logic.<br>
	 * It ensures that only one two-handed weapon is included in the resulting list.
	 * @return A {@code List} containing the filtered {@link Item} objects.
	 */
	public List<Item> getEquippedForApparence()
	{
		final List<Item> equippedItems = new ArrayList<>();
		Item twoHanded = null;
		for (Item item : equipment.values())
		{
			final long slot = item.getEquipmentSlot();
			if (!ItemSlot.isStigma(slot) || (slot > ItemSlot.GLYPH.getSlotIdMask()))
			{
				if (slot <= ItemSlot.BRACELET.getSlotIdMask())
				{
					if (item.getItemTemplate().isTwoHandWeapon())
					{
						if (twoHanded != null)
						{
							continue;
						}
						
						twoHanded = item;
					}
					
					equippedItems.add(item);
				}
			}
		}
		
		return equippedItems;
	}
	
	/**
	 * Retrieves all items currently equipped in stigma slots.<br>
	 * This method filters the player's equipment to find specific types.<br>
	 * It checks each item against {@code isStigma}.
	 * @return A {@code List} of {@link Item} objects that are equipped in stigma slots.
	 */
	public List<Item> getEquippedItemsAllStigma()
	{
		final List<Item> equippedItems = new ArrayList<>();
		for (Item item : equipment.values())
		{
			if (ItemSlot.isStigma(item.getEquipmentSlot()))
			{
				equippedItems.add(item);
			}
		}
		
		return equippedItems;
	}
	
	/**
	 * Retrieves the IDs of all items currently equipped in stigma slots.<br>
	 * This method filters the equipment to only include those marked as stigma by {@link ItemSlot}.
	 * @return A {@code List<Integer>} containing the item IDs.
	 */
	public List<Integer> getEquippedItemsAllStigmaIds()
	{
		final List<Integer> equippedItemIds = new ArrayList<>();
		for (Item item : equipment.values())
		{
			if (ItemSlot.isStigma(item.getEquipmentSlot()))
			{
				equippedItemIds.add(item.getItemId());
			}
		}
		
		return equippedItemIds;
	}
	
	/**
	 * Retrieves a list of items currently equipped in regular stigma slots.<br>
	 * This method filters the player's equipment to include only those matching {@code isRegularStigma}.
	 * @return A {@code List} of {@link Item} objects that are equipped in regular stigma slots.
	 */
	public List<Item> getEquippedItemsRegularStigma()
	{
		final List<Item> equippedItems = new ArrayList<>();
		for (Item item : equipment.values())
		{
			if (ItemSlot.isRegularStigma(item.getEquipmentSlot()))
			{
				equippedItems.add(item);
			}
		}
		
		return equippedItems;
	}
	
	/**
	 * Retrieves a list of items currently equipped in advanced stigma slots.<br>
	 * This method filters the player's equipment to find specific slot types.
	 * @return A {@code List} of {@link Item} objects that are in advanced stigma slots.
	 */
	public List<Item> getEquippedItemsAdvancedStigma()
	{
		final List<Item> equippedItems = new ArrayList<>();
		for (Item item : equipment.values())
		{
			if (ItemSlot.isAdvancedStigma(item.getEquipmentSlot()))
			{
				equippedItems.add(item);
			}
		}
		
		return equippedItems;
	}
	
	/**
	 * Retrieves a list of items currently equipped in major stigma slots.<br>
	 * This method filters the player's equipment based on {@code isMajorStigma}.
	 * @return A {@code List} of {@code Item} objects that are equipped in major stigma slots.
	 */
	public List<Item> getEquippedItemsMajorStigma()
	{
		final List<Item> equippedItems = new ArrayList<>();
		for (Item item : equipment.values())
		{
			if (ItemSlot.isMajorStigma(item.getEquipmentSlot()))
			{
				equippedItems.add(item);
			}
		}
		
		return equippedItems;
	}
	
	/**
	 * Retrieves a list of items currently equipped in special stigma slots.<br>
	 * This method filters the player's equipment to find specific slot types.
	 * @return A {@code List} of {@link Item} objects that are equipped in special stigma slots.
	 */
	public List<Item> getEquippedItemsSpecialStigma()
	{
		final List<Item> equippedItems = new ArrayList<>();
		for (Item item : equipment.values())
		{
			if (ItemSlot.isSpecialStigma(item.getEquipmentSlot()))
			{
				equippedItems.add(item);
			}
		}
		
		return equippedItems;
	}
	
	/**
	 * Counts how many parts of a specific item set are currently equipped.<br>
	 * This method ignores items in the main or sub off-hand slots.<br>
	 * It also ensures that only one two-handed weapon is counted toward the total.
	 * @param itemSetTemplateId The unique identifier for the {@code ItemSetTemplate} to check.
	 * @return The number of equipped items belonging to the specified set.
	 */
	public int itemSetPartsEquipped(int itemSetTemplateId)
	{
		int number = 0;
		Item twoHanded = null;
		
		for (Item item : equipment.values())
		{
			if (((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_HAND.getSlotIdMask()) != 0) || ((item.getEquipmentSlot() & ItemSlot.SUB_OFF_HAND.getSlotIdMask()) != 0))
			{
				continue;
			}
			
			if (item.getItemTemplate().isTwoHandWeapon())
			{
				if (twoHanded != null)
				{
					continue;
				}
				
				twoHanded = item;
			}
			
			final ItemSetTemplate setTemplate = item.getItemTemplate().getItemSet();
			if ((setTemplate != null) && (setTemplate.getId() == itemSetTemplateId))
			{
				++number;
			}
		}
		
		return number;
	}
	
	/**
	 * Handles the logic for loading an {@code Item} into the equipment slots.<br>
	 * This method validates if the item can be equipped based on its type.<br>
	 * It also handles special cases like two-handed weapons and duplicate slot checks.<br>
	 * If validation fails, the item is returned to the inventory.
	 * @param item The {@code Item} object to be loaded into equipment.
	 */
	public void onLoadHandler(Item item)
	{
		final ItemTemplate template = item.getItemTemplate();
		
		// Unequip arrows during the 4.0 upgrade and return them to the inventory while checking their item levels.
		if (template.getArmorType() != null)
		{
			if (!validateEquippedArmor(item, true))
			{
				putItemBackToInventory(item);
				return;
			}
		}
		
		if (template.getWeaponType() != null)
		{
			if (!validateEquippedWeapon(item, true))
			{
				putItemBackToInventory(item);
				return;
			}
		}
		
		if (template.isTwoHandWeapon())
		{
			ItemSlot[] oldSlots = ItemSlot.getSlotsFor(item.getEquipmentSlot());
			if (oldSlots.length != 2)
			{
				// update slot during upgrade to 4.0
				final long currentSlot = item.getEquipmentSlot();
				if ((item.getEquipmentSlot() & ItemSlot.MAIN_OR_SUB.getSlotIdMask()) != 0)
				{
					item.setEquipmentSlot(ItemSlot.MAIN_OR_SUB.getSlotIdMask());
				}
				else
				{
					item.setEquipmentSlot(ItemSlot.MAIN_OFF_OR_SUB_OFF.getSlotIdMask());
				}
				
				if (currentSlot != item.getEquipmentSlot())
				{
					setPersistentState(PersistentState.UPDATE_REQUIRED);
				}
				
				oldSlots = ItemSlot.getSlotsFor(item.getEquipmentSlot());
			}
			
			for (ItemSlot sl : oldSlots)
			{
				if (equipment.containsKey(sl.getSlotIdMask()))
				{
					log.warn("Duplicate equipped item in slot : " + sl.getSlotIdMask() + " " + owner.getObjectId());
					putItemBackToInventory(item);
					break;
				}
				
				equipment.put(sl.getSlotIdMask(), item);
			}
			return;
		}
		
		if (equipment.containsKey(item.getEquipmentSlot()))
		{
			log.warn("Duplicate equipped item in slot: " + item.getEquipmentSlot() + " " + owner.getObjectId());
			putItemBackToInventory(item);
			return;
		}
		
		equipment.put(item.getEquipmentSlot(), item);
	}
	
	/**
	 * Removes an item from the equipment slots and returns it to the player's inventory.<br>
	 * This method updates the {@code Item} state and marks it for a database update.
	 * @param item The {@code Item} object to be moved back to the inventory.
	 */
	private void putItemBackToInventory(Item item)
	{
		item.setEquipped(false);
		item.setEquipmentSlot(0);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		owner.getInventory().put(item);
	}
	
	/**
	 * Applies statistics for all currently equipped items.<br>
	 * This method iterates through the {@code equipment} map to update player stats.<br>
	 * It ensures that only one two-handed weapon is processed at a time.<br>
	 * It also triggers {@code Player)} for each valid item.
	 */
	public void onLoadApplyEquipmentStats()
	{
		Item twoHanded = null;
		for (Item item : equipment.values())
		{
			if (((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_HAND.getSlotIdMask()) == 0) && ((item.getEquipmentSlot() & ItemSlot.SUB_OFF_HAND.getSlotIdMask()) == 0))
			{
				if (item.getItemTemplate().isTwoHandWeapon())
				{
					if (twoHanded != null)
					{
						continue;
					}
					
					twoHanded = item;
				}
				
				if (item.getOptionalSocket() == -1)
				{
					log.warn("on load all eqipment, item can't be equiped because hasTune" + item.getObjectId());
					continue;
				}
				
				ItemEquipmentListener.onItemEquipment(item, owner);
				owner.getLifeStats().synchronizeWithMaxStats();
			}
		}
	}
	
	/**
	 * Checks if the player currently has a shield equipped.<br>
	 * It looks for an item in the {@code SUB_HAND} slot.<br>
	 * The item must have an {@link ArmorType} of {@code SHIELD}.
	 * @return {@code true} if a shield is equipped, otherwise {@code false}
	 */
	public boolean isShieldEquipped()
	{
		final Item subHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		return (subHandItem != null) && (subHandItem.getItemTemplate().getArmorType() == ArmorType.SHIELD);
	}
	
	/**
	 * Retrieves the shield currently equipped by the player.<br>
	 * This method checks the {@code SUB_HAND} slot for an item.<br>
	 * It verifies if the item's {@code ArmorType} is a {@code SHIELD}.
	 * @return the {@code Item} object if a shield is equipped, otherwise {@code null}
	 */
	public Item getEquippedShield()
	{
		final Item subHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		return ((subHandItem != null) && (subHandItem.getItemTemplate().getArmorType() == ArmorType.SHIELD)) ? subHandItem : null;
	}
	
	/**
	 * Retrieves the currently equipped plume item.<br>
	 * This method checks if an item is placed in the {@code PLUME} slot.<br>
	 * It verifies that the item belongs to the {@code PLUME} category.
	 * @return the {@link Item} object if a valid plume is equipped, otherwise {@code null}.
	 */
	public Item getEquipedPlume()
	{
		final Item plume = equipment.get(ItemSlot.PLUME.getSlotIdMask());
		return ((plume != null) && (plume.getItemTemplate().getCategory() == ItemCategory.PLUME)) ? plume : null;
	}
	
	/**
	 * Checks if a specific type of armor is currently equipped.<br>
	 * This method iterates through all equipment to find a match.<br>
	 * It ignores items that are weapons or in the sub-off-hand slot.
	 * @param type The {@code ArmorType} to check for.
	 * @return {@code true} if the armor type is equipped, otherwise {@code false}.
	 */
	public boolean isArmorTypeEquipped(ArmorType type)
	{
		for (Item item : equipment.values())
		{
			if ((item == null) || (item.getItemTemplate().getWeaponType() != null))
			{
				continue;
			}
			
			// TODO: Check it! Not sure for dual hand
			if ((item.getItemTemplate().getArmorType() == type) && item.isEquipped() && (item.getEquipmentSlot() != ItemSlot.SUB_OFF_HAND.getSlotIdMask()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the type of weapon currently held in the main hand.<br>
	 * This method checks if a weapon is equipped in the {@code ItemSlot.MAIN_HAND}.<br>
	 * If no item is found, it returns {@code null}.
	 * @return The {@code WeaponType} of the main hand item, or {@code null} if empty.
	 */
	public WeaponType getMainHandWeaponType()
	{
		final Item mainHandItem = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		if (mainHandItem == null)
		{
			return null;
		}
		
		return mainHandItem.getItemTemplate().getWeaponType();
	}
	
	/**
	 * Retrieves the type of weapon held in the off-hand slot.<br>
	 * This method checks if an item exists in the {@code SUB_HAND} slot.<br>
	 * It ensures the item is not the same as the main hand weapon.<br>
	 * It returns the {@code WeaponType} only if the item is a valid weapon.
	 * @return The {@code WeaponType} of the off-hand weapon, or {@code null} if no weapon is equipped.
	 */
	public WeaponType getOffHandWeaponType()
	{
		Item offHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		final Item mainHandItem = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		if (mainHandItem == offHandItem)
		{
			offHandItem = null;
		}
		
		if ((offHandItem != null) && offHandItem.getItemTemplate().isWeapon())
		{
			return offHandItem.getItemTemplate().getWeaponType();
		}
		
		return null;
	}
	
	/**
	 * Checks if the player currently has an arrow equipped.<br>
	 * It looks for an item in the {@code SUB_HAND} slot.<br>
	 * The item must have an {@link ArmorType} of {@code ARROW}.
	 * @return {@code true} if an arrow is equipped, otherwise {@code false}
	 */
	public boolean isArrowEquipped()
	{
		final Item arrow = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		if ((arrow != null) && (arrow.getItemTemplate().getArmorType() == ArmorType.ARROW))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the player has any power shards equipped.<br>
	 * This method looks for items in both the left and right power shard slots.
	 * @return {@code true} if at least one power shard is equipped, otherwise {@code false}.
	 */
	public boolean isPowerShardEquipped()
	{
		final Item leftPowershard = equipment.get(ItemSlot.POWER_SHARD_LEFT.getSlotIdMask());
		if (leftPowershard != null)
		{
			return true;
		}
		
		final Item rightPowershard = equipment.get(ItemSlot.POWER_SHARD_RIGHT.getSlotIdMask());
		if (rightPowershard != null)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the power shard equipped in the right hand slot.<br>
	 * This method checks the {@code equipment} map for a specific slot mask.<br>
	 * It returns the {@code Item} if it exists, otherwise it returns {@code null}.
	 * @return The {@code Item} in the right power shard slot or {@code null} if empty.
	 */
	public Item getMainHandPowerShard()
	{
		final Item mainHandPowerShard = equipment.get(ItemSlot.POWER_SHARD_RIGHT.getSlotIdMask());
		if (mainHandPowerShard != null)
		{
			return mainHandPowerShard;
		}
		
		return null;
	}
	
	/**
	 * Retrieves the item equipped in the left power shard slot.<br>
	 * This method checks if an {@code Item} exists in that specific slot.
	 * @return the {@code Item} from the left power shard slot, or {@code null} if empty.
	 */
	public Item getOffHandPowerShard()
	{
		final Item offHandPowerShard = equipment.get(ItemSlot.POWER_SHARD_LEFT.getSlotIdMask());
		if (offHandPowerShard != null)
		{
			return offHandPowerShard;
		}
		
		return null;
	}
	
	/**
	 * Consumes a specific amount of power shards from an item.<br>
	 * If the stack becomes empty, it tries to equip the next available shard.<br>
	 * If no more shards are found, it removes the power shard state and sends a system message.
	 * @param powerShardItem The {@code Item} object representing the power shard to use.
	 * @param count The number of shards to consume from the stack.
	 */
	public void usePowerShard(Item powerShardItem, int count)
	{
		decreaseEquippedItemCount(powerShardItem.getObjectId(), count);
		
		if (powerShardItem.getItemCount() <= 0)
		{// Search for next same power shards stack
			final List<Item> powerShardStacks = owner.getInventory().getItemsByItemId(powerShardItem.getItemTemplate().getTemplateId());
			if (powerShardStacks.size() != 0)
			{
				equipItem(powerShardStacks.get(0).getObjectId(), powerShardItem.getEquipmentSlot());
			}
			else
			{
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_MSG_WEAPON_BOOST_MODE_BURN_OUT);
				owner.unsetState(CreatureState.POWERSHARD);
			}
		}
	}
	
	/**
	 * Increases the count of a specific {@link Item} if it is a shard.<br>
	 * This method updates the item's internal count and triggers a stats update.<br>
	 * If the item is not a shard, no changes are made to the item.
	 * @param item The {@link Item} object to modify.
	 * @param count The amount to increase by.
	 * @return The remaining count of the item after the increase.
	 */
	public long increaseEquippedItemCount(Item item, long count)
	{
		// Only Shards can be increased
		if (item.getItemTemplate().getCategory() != ItemCategory.SHARD)
		{
			return count;
		}
		
		final long leftCount = item.increaseItemCount(count);
		ItemPacketService.updateItemAfterInfoChange(owner, item, ItemUpdateType.STATS_CHANGE);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return leftCount;
	}
	
	/**
	 * Reduces the count of a specific shard item currently equipped by the player.<br>
	 * This method checks if the item is a {@code SHARD} before making any changes.<br>
	 * If the count reaches zero, the item is removed from the equipment slot and deleted.
	 * @param itemObjId The unique object ID of the item to modify.
	 * @param count The amount by which to decrease the item count.
	 */
	private void decreaseEquippedItemCount(int itemObjId, int count)
	{
		final Item equippedItem = getEquippedItemByObjId(itemObjId);
		
		// Only Shards can be decreased
		if (equippedItem.getItemTemplate().getCategory() != ItemCategory.SHARD)
		{
			return;
		}
		
		if (equippedItem.getItemCount() >= count)
		{
			equippedItem.decreaseItemCount(count);
		}
		else
		{
			equippedItem.decreaseItemCount(equippedItem.getItemCount());
		}
		
		if (equippedItem.getItemCount() == 0)
		{
			equipment.remove(equippedItem.getEquipmentSlot());
			PacketSendUtility.sendPacket(owner, new SM_DELETE_ITEM(equippedItem.getObjectId()));
			DAOManager.getDAO(InventoryDAO.class).store(equippedItem, owner);
		}
		
		ItemPacketService.updateItemAfterInfoChange(owner, equippedItem, ItemUpdateType.STATS_CHANGE);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Swaps the equipment between the left and right hands.<br>
	 * This method identifies all currently equipped weapons.<br>
	 * It unequips them, swaps their hand positions, and re-equips them.<br>
	 * Finally, it updates the player stats and removes any active stance effects.
	 */
	public void switchHands()
	{
		final Item mainHandItem = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		final Item subHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		final Item mainOffHandItem = equipment.get(ItemSlot.MAIN_OFF_HAND.getSlotIdMask());
		final Item subOffHandItem = equipment.get(ItemSlot.SUB_OFF_HAND.getSlotIdMask());
		
		final List<Item> equippedWeapon = new ArrayList<>();
		
		if (mainHandItem != null)
		{
			equippedWeapon.add(mainHandItem);
		}
		
		if ((subHandItem != null) && (subHandItem != mainHandItem))
		{
			equippedWeapon.add(subHandItem);
		}
		
		if (mainOffHandItem != null)
		{
			equippedWeapon.add(mainOffHandItem);
		}
		
		if ((subOffHandItem != null) && (subOffHandItem != mainOffHandItem))
		{
			equippedWeapon.add(subOffHandItem);
		}
		
		for (Item item : equippedWeapon)
		{
			if (item.getItemTemplate().isTwoHandWeapon())
			{
				final ItemSlot[] slots = ItemSlot.getSlotsFor(item.getEquipmentSlot());
				for (ItemSlot slot : slots)
				{
					equipment.remove(slot.getSlotIdMask());
				}
			}
			else
			{
				equipment.remove(item.getEquipmentSlot());
			}
			
			item.setEquipped(false);
			PacketSendUtility.sendPacket(owner, new SM_INVENTORY_UPDATE_ITEM(owner, item, ItemUpdateType.EQUIP_UNEQUIP));
			if (owner.getGameStats() != null)
			{
				if (((item.getEquipmentSlot() & ItemSlot.MAIN_HAND.getSlotIdMask()) != 0) || ((item.getEquipmentSlot() & ItemSlot.SUB_HAND.getSlotIdMask()) != 0))
				{
					notifyItemUnequip(item);
				}
			}
		}
		
		for (Item item : equippedWeapon)
		{
			long oldSlots = item.getEquipmentSlot();
			if ((oldSlots & ItemSlot.RIGHT_HAND.getSlotIdMask()) != 0)
			{
				oldSlots ^= ItemSlot.RIGHT_HAND.getSlotIdMask();
			}
			
			if ((oldSlots & ItemSlot.LEFT_HAND.getSlotIdMask()) != 0)
			{
				oldSlots ^= ItemSlot.LEFT_HAND.getSlotIdMask();
			}
			
			item.setEquipmentSlot(oldSlots);
		}
		
		for (Item item : equippedWeapon)
		{
			if (item.getItemTemplate().isTwoHandWeapon())
			{
				final ItemSlot[] slots = ItemSlot.getSlotsFor(item.getEquipmentSlot());
				for (ItemSlot slot : slots)
				{
					equipment.put(slot.getSlotIdMask(), item);
				}
			}
			else
			{
				equipment.put(item.getEquipmentSlot(), item);
			}
			
			item.setEquipped(true);
			ItemPacketService.updateItemAfterEquip(owner, item);
		}
		
		if (owner.getGameStats() != null)
		{
			for (Item item : equippedWeapon)
			{
				if (((item.getEquipmentSlot() & ItemSlot.MAIN_HAND.getSlotIdMask()) != 0) || ((item.getEquipmentSlot() & ItemSlot.SUB_HAND.getSlotIdMask()) != 0))
				{
					notifyItemEquipped(item);
				}
			}
		}
		
		owner.getLifeStats().updateCurrentStats();
		owner.getGameStats().updateStatsAndSpeedVisually();
		
		// remove stance effect when switchhand
		if (owner.getController().isUnderStance())
		{
			owner.getController().stopStance();
		}
		
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Checks if the player has a specific type of weapon equipped.<br>
	 * This method looks at both the main hand and sub hand slots.<br>
	 * It returns {@code true} if either slot contains the specified {@code WeaponType}.
	 * @param weaponType The type of weapon to check for.
	 * @return {@code true} if a matching weapon is equipped, otherwise {@code false}.
	 */
	public boolean isWeaponEquipped(WeaponType weaponType)
	{
		if (((equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask()) != null) && (equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask()).getItemTemplate().getWeaponType() == weaponType)) || ((equipment.get(ItemSlot.SUB_HAND.getSlotIdMask()) != null) && (equipment.get(ItemSlot.SUB_HAND.getSlotIdMask()).getItemTemplate().getWeaponType() == weaponType)))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the player has a dual weapon equipped in the specified slot.<br>
	 * It verifies that the items are not two-handed weapons.<br>
	 * This method returns {@code true} if at least one valid weapon is found.
	 * @param slot The {@link ItemSlot} to check for dual weapons.
	 * @return {@code true} if a dual weapon is equipped, {@code false} otherwise.
	 */
	public boolean hasDualWeaponEquipped(ItemSlot slot)
	{
		final ItemSlot[] slotValues = ItemSlot.getSlotsFor(slot.getSlotIdMask());
		if (slotValues.length == 0)
		{
			return false;
		}
		
		for (ItemSlot s : slotValues)
		{
			final Item weapon = equipment.get(s.getSlotIdMask());
			if ((weapon == null) || weapon.getItemTemplate().isTwoHandWeapon())
			{
				continue;
			}
			
			if (weapon.getItemTemplate().getWeaponType() != null)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the player is currently wearing a specific type of armor.<br>
	 * This method iterates through all available {@code ARMOR_SLOTS}.<br>
	 * It returns {@code true} if any equipped item matches the provided {@code armorType}.
	 * @param armorType The {@link ArmorType} to check for.
	 * @return {@code true} if the armor type is equipped, otherwise {@code false}.
	 */
	public boolean isArmorEquipped(ArmorType armorType)
	{
		if (armorType == null)
		{
			return false;
		}
		
		for (long slot : ARMOR_SLOTS)
		{
			if ((equipment.get(slot) != null) && (equipment.get(slot).getItemTemplate().getArmorType() == armorType))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the player is currently using a Keyblade.<br>
	 * This method looks for a weapon in the main hand slot.<br>
	 * It verifies if the item type matches {@code WeaponType.KEYBLADE_2H}.
	 * @return {@code true} if a Keyblade is equipped, otherwise {@code false}
	 */
	public boolean isKeybladeEquipped()
	{
		final Item keyblade = getMainHandWeapon(); // equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		
		if ((keyblade != null) && (keyblade.getItemTemplate().getWeaponType() == WeaponType.KEYBLADE_2H))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific equipment slot is currently occupied.<br>
	 * This method returns {@code true} if there is an item in the slot.<br>
	 * It returns {@code false} if the slot is empty.
	 * @param slot The unique identifier for the equipment slot to check.
	 * @return {@code true} if the slot has an item, otherwise {@code false}.
	 */
	public boolean isSlotEquipped(long slot)
	{
		return !(equipment.get(slot) == null);
	}
	
	/**
	 * Retrieves the item currently equipped in the main hand.<br>
	 * This method looks up the {@code Item} using the {@code ItemSlot.MAIN_HAND} mask.
	 * @return The {@code Item} object in the main hand slot, or {@code null} if empty.
	 */
	public Item getMainHandWeapon()
	{
		return equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
	}
	
	/**
	 * Retrieves the weapon currently equipped in the off-hand slot.<br>
	 * This method returns {@code null} if the item in that slot is the same as the main-hand weapon.
	 * @return The {@link Item} in the off-hand slot, or {@code null} if it matches the main-hand weapon.
	 */
	public Item getOffHandWeapon()
	{
		final Item result = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		if (getMainHandWeapon() == result)
		{
			return null;
		}
		
		return result;
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this decoration.<br>
	 * This method assigns a new {@link PersistentState} to the object.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		this.persistentState = persistentState;
	}
	
	/**
	 * Sets the owner of this object to a specific {@link Player}.<br>
	 * This method updates the internal {@code owner} field.
	 * @param player The {@code Player} who will become the new owner.
	 */
	public void setOwner(Player player)
	{
		owner = player;
	}
	
	/**
	 * Checks if a player can soul bind an item to their character.<br>
	 * Validates the player's current state and inventory before showing a confirmation window.<br>
	 * If successful, it triggers an animation and equips the item to the specified slot.
	 * @param player The {@link Player} attempting to perform the action.
	 * @param item The {@link Item} that will be soul bound.
	 * @param slot The equipment slot where the item should be placed.
	 * @return {@code false} because this method initiates an asynchronous request process.
	 */
	private boolean soulBindItem(Player player, Item item, long slot)
	{
		if ((player.getInventory().getItemByObjId(item.getObjectId()) == null) || player.isInState(CreatureState.GLIDING))
		{
			return false;
		}
		
		if (CreatureActions.isAlreadyDead(player))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_INVALID_STANCE(2800119));
			return false;
		}
		else if (player.isInPlayerMode(PlayerMode.RIDE))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_INVALID_STANCE(2800114));
			return false;
		}
		else if (player.isInState(CreatureState.CHAIR))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_INVALID_STANCE(2800117));
			return false;
		}
		else if (player.isInState(CreatureState.RESTING))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_INVALID_STANCE(2800115));
			return false;
		}
		else if (player.isInState(CreatureState.FLYING))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_INVALID_STANCE(2800111));
			return false;
		}
		else if (player.isInState(CreatureState.WEAPON_EQUIPPED))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_INVALID_STANCE(2800159));
			return false;
		}
		
		final RequestResponseHandler responseHandler = new RequestResponseHandler(player)
		{
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				player.getController().cancelUseItem();
				
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, item.getObjectId(), item.getItemId(), 5000, 4), true);
				
				player.getController().cancelTask(TaskId.ITEM_USE);
				
				final ActionObserver moveObserver = new ActionObserver(ObserverType.MOVE)
				{
					
					@Override
					public void moved()
					{
						player.getController().cancelTask(TaskId.ITEM_USE);
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_ITEM_CANCELED(item.getNameId()));
						PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, item.getObjectId(), item.getItemId(), 0, 8), true);
					}
				};
				player.getObserveController().attach(moveObserver);
				
				// item usage animation
				player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(() ->
				{
					player.getObserveController().removeObserver(moveObserver);
					
					PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, item.getObjectId(), item.getItemId(), 0, 6), true);
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_ITEM_SUCCEED(item.getNameId()));
					
					item.setSoulBound(true);
					ItemPacketService.updateItemAfterInfoChange(owner, item);
					
					equip(slot, item);
					PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), getEquippedForApparence()), true);
				}, 5000));
			}
			
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_ITEM_CANCELED(item.getNameId()));
			}
		};
		
		final boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_SOUL_BOUND_ITEM_DO_YOU_WANT_SOUL_BOUND, responseHandler);
		if (requested)
		{
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_SOUL_BOUND_ITEM_DO_YOU_WANT_SOUL_BOUND, 0, 0, new DescriptionId(item.getNameId())));
		}
		else
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_CLOSE_OTHER_MSG_BOX_AND_RETRY);
		}
		
		return false;
	}
	
	/**
	 * Checks if the player has a high enough rank to use an item.<br>
	 * This method compares the owner's abyss rank against the limits defined in the {@code ItemTemplate}.<br>
	 * It also checks the requirements for fusioned items if they exist.
	 * @param item The {@code Item} being checked for rank restrictions.
	 * @return {@code true} if the player meets the rank requirements, {@code false} otherwise.
	 */
	private boolean verifyRankLimits(Item item)
	{
		final int rank = owner.getAbyssRank().getRank().getId();
		if (!item.getItemTemplate().getUseLimits().verifyRank(rank))
		{
			return false;
		}
		
		if (item.getFusionedItemTemplate() != null)
		{
			return item.getFusionedItemTemplate().getUseLimits().verifyRank(rank);
		}
		
		return true;
	}
	
	/**
	 * Checks all currently equipped items against their rank requirements.<br>
	 * If an item exceeds its allowed rank, it is automatically unequipped.<br>
	 * A system message is sent to the player for each removed item.
	 */
	public void checkRankLimitItems()
	{
		for (Item item : getEquippedItems())
		{
			if (!verifyRankLimits(item))
			{
				unEquipItem(item.getObjectId(), item.getEquipmentSlot());
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_MSG_UNEQUIP_RANKITEM(item.getNameId()));
				// TODO: Check retail what happens with full inv and the task msgs.
			}
		}
	}
}
