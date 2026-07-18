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
package com.aionemu.gameserver.services.item;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.GodstoneInfo;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logic for socketing items within the game.<br>
 * It manages how {@link Item} objects interact with stones and other socketable components. It provides methods to process, validate, and update item sockets in the database.
 * @author ATracer
 */
public class ItemSocketService
{
	private static final Logger log = LoggerFactory.getLogger(ItemSocketService.class);
	
	/**
	 * Adds a {@link ManaStone} to an existing {@link Item}.<br>
	 * This method checks if the item has available sockets before adding.<br>
	 * It automatically finds the next available slot for the new stone.
	 * @param item The {@link Item} that will receive the stone.
	 * @param itemId The unique identifier of the stone to add.
	 * @return The newly created {@link ManaStone} object, or {@code null} if the item is full or invalid.
	 */
	public static ManaStone addManaStone(Item item, int itemId)
	{
		if (item == null)
		{
			return null;
		}
		
		final Set<ManaStone> manaStones = item.getItemStones();
		if (manaStones.size() >= item.getSockets(false))
		{
			return null;
		}
		
		int nextSlot = 0;
		boolean slotFound = false;
		for (ManaStone ms : manaStones)
		{
			if (nextSlot != ms.getSlot())
			{
				slotFound = true;
				break;
			}
			
			nextSlot++;
		}
		
		if (!slotFound)
		{
			nextSlot = manaStones.size();
		}
		
		final ManaStone stone = new ManaStone(item.getObjectId(), itemId, nextSlot, PersistentState.NEW);
		manaStones.add(stone);
		
		return stone;
	}
	
	/**
	 * Adds a {@link ManaStone} to a specific item.<br>
	 * This method checks if the item has available slots before adding.<br>
	 * It returns the newly created {@code ManaStone} object.
	 * @param item The {@code Item} that will receive the stone.
	 * @param itemId The unique identifier for the stone type.
	 * @param slotId The specific slot index where the stone is placed.
	 * @return The added {@code ManaStone} or {@code null} if it fails.
	 */
	public static ManaStone addManaStone(Item item, int itemId, int slotId)
	{
		if (item == null)
		{
			return null;
		}
		
		final Set<ManaStone> manaStones = item.getItemStones();
		if (manaStones.size() >= Item.MAX_BASIC_STONES)
		{
			return null;
		}
		
		final ManaStone stone = new ManaStone(item.getObjectId(), itemId, slotId, PersistentState.NEW);
		manaStones.add(stone);
		return stone;
	}
	
	/**
	 * Copies all {@link ManaStone} objects from one item to another.<br>
	 * This method checks if the {@code source} has any stones before copying.<br>
	 * It duplicates both mana stones and fusion stones into the {@code target}.
	 * @param source The {@code Item} that currently holds the stones.
	 * @param target The {@code Item} that will receive the copied stones.
	 */
	public static void copyManaStones(Item source, Item target)
	{
		if (source.hasManaStones())
		{
			for (ManaStone manaStone : source.getItemStones())
			{
				target.getItemStones().add(new ManaStone(target.getObjectId(), manaStone.getItemId(), manaStone.getSlot(), PersistentState.NEW));
			}
			
			for (ManaStone manaStone : source.getFusionStones())
			{
				target.getFusionStones().add(new ManaStone(target.getObjectId(), manaStone.getItemId(), manaStone.getSlot(), PersistentState.NEW));
			}
		}
	}
	
	/**
	 * Copies the {@code ManaStone} data from a source item to a target item.<br>
	 * This method checks if the {@code source} has any stones.<br>
	 * It then adds new stones to the {@code target} fusion stone list.
	 * @param source The {@link Item} containing the original stones.
	 * @param target The {@link Item} that will receive the copied stones.
	 */
	public static void copyFusionStones(Item source, Item target)
	{
		if (source.hasManaStones())
		{
			for (ManaStone manaStone : source.getItemStones())
			{
				target.getFusionStones().add(new ManaStone(target.getObjectId(), manaStone.getItemId(), manaStone.getSlot(), PersistentState.NEW));
			}
		}
	}
	
	/**
	 * Adds a fusion stone to an existing item.<br>
	 * This method finds the first available slot for the new stone.<br>
	 * It returns {@code null} if the item is full or invalid.
	 * @param item The {@link Item} that will receive the stone.
	 * @param itemId The unique identifier of the fusion stone to add.
	 * @return The newly created {@link ManaStone} object, or {@code null} if it could not be added.
	 */
	public static ManaStone addFusionStone(Item item, int itemId)
	{
		if (item == null)
		{
			return null;
		}
		
		final Set<ManaStone> manaStones = item.getFusionStones();
		if (manaStones.size() >= item.getSockets(true))
		{
			return null;
			
		}
		
		int nextSlot = 0;
		boolean slotFound = false;
		for (ManaStone ms : manaStones)
		{
			if (nextSlot != ms.getSlot())
			{
				slotFound = true;
				break;
			}
			
			nextSlot++;
		}
		
		if (!slotFound)
		{
			nextSlot = manaStones.size();
		}
		
		final ManaStone stone = new ManaStone(item.getObjectId(), itemId, nextSlot, PersistentState.NEW);
		manaStones.add(stone);
		return stone;
	}
	
	/**
	 * Adds a {@link ManaStone} to an item at a specific socket.<br>
	 * This method checks if the item has enough available slots before adding.<br>
	 * It returns {@code null} if the item is {@code null} or if there are no free slots.
	 * @param item The {@link Item} that will receive the stone.
	 * @param itemId The unique identifier for the stone type.
	 * @param slotId The specific slot index where the stone should be placed.
	 * @return The newly created {@link ManaStone} object, or {@code null} if the operation fails.
	 */
	public static ManaStone addFusionStone(Item item, int itemId, int slotId)
	{
		if (item == null)
		{
			return null;
		}
		
		final Set<ManaStone> fusionStones = item.getFusionStones();
		if (fusionStones.size() > item.getSockets(true))
		{
			return null;
		}
		
		final ManaStone stone = new ManaStone(item.getObjectId(), itemId, slotId, PersistentState.NEW);
		fusionStones.add(stone);
		return stone;
	}
	
	/**
	 * Removes a specific {@link ManaStone} from an item in the player's inventory.<br>
	 * This method updates the database and refreshes the item information for the {@code Player}.
	 * @param player The {@code Player} who owns the item.
	 * @param itemObjId The unique object ID of the item to modify.
	 * @param slotNum The index of the stone slot to clear.
	 */
	public static void removeManastone(Player player, int itemObjId, int slotNum)
	{
		final Storage inventory = player.getInventory();
		final Item item = inventory.getItemByObjId(itemObjId);
		if (item == null)
		{
			log.warn("Item not found during manastone remove");
			return;
		}
		
		if (!item.hasManaStones())
		{
			log.warn("Item stone list is empty");
			return;
		}
		
		final Set<ManaStone> itemStones = item.getItemStones();
		
		if (itemStones.size() <= slotNum)
		{
			return;
		}
		
		int counter = 0;
		for (ManaStone ms : itemStones)
		{
			if (counter == slotNum)
			{
				ms.setPersistentState(PersistentState.DELETED);
				DAOManager.getDAO(ItemStoneListDAO.class).storeManaStones(Collections.singleton(ms));
				itemStones.remove(ms);
				break;
			}
			
			counter++;
		}
		
		ItemPacketService.updateItemAfterInfoChange(player, item);
	}
	
	/**
	 * Removes a specific fusion stone from an item in the player's inventory.<br>
	 * This method updates the database and refreshes the item information for the {@link Player}.
	 * @param player The {@code Player} who owns the item.
	 * @param itemObjId The unique object ID of the item to modify.
	 * @param slotNum The index of the fusion stone to remove.
	 */
	public static void removeFusionstone(Player player, int itemObjId, int slotNum)
	{
		final Storage inventory = player.getInventory();
		final Item item = inventory.getItemByObjId(itemObjId);
		if (item == null)
		{
			log.warn("Item not found during manastone remove");
			return;
		}
		
		if (!item.hasFusionStones())
		{
			log.warn("Item stone list is empty");
			return;
		}
		
		final Set<ManaStone> itemStones = item.getFusionStones();
		
		if (itemStones.size() <= slotNum)
		{
			return;
		}
		
		int counter = 0;
		for (ManaStone ms : itemStones)
		{
			if (counter == slotNum)
			{
				ms.setPersistentState(PersistentState.DELETED);
				DAOManager.getDAO(ItemStoneListDAO.class).storeFusionStones(Collections.singleton(ms));
				itemStones.remove(ms);
				break;
			}
			
			counter++;
		}
		
		ItemPacketService.updateItemAfterInfoChange(player, item);
	}
	
	/**
	 * Removes all {@link ManaStone} objects from a specific {@link Item}.<br>
	 * This method updates the database and clears the stones from the player's inventory.<br>
	 * It also triggers an update for the item information sent to the {@link Player}.
	 * @param player The {@link Player} who owns the item.
	 * @param item The {@link Item} from which all manastones should be removed.
	 */
	public static void removeAllManastone(Player player, Item item)
	{
		if (item == null)
		{
			log.warn("Item not found during manastone remove");
			return;
		}
		
		if (!item.hasManaStones())
		{
			return;
		}
		
		final Set<ManaStone> itemStones = item.getItemStones();
		for (ManaStone ms : itemStones)
		{
			ms.setPersistentState(PersistentState.DELETED);
		}
		
		DAOManager.getDAO(ItemStoneListDAO.class).storeManaStones(itemStones);
		itemStones.clear();
		
		ItemPacketService.updateItemAfterInfoChange(player, item);
	}
	
	/**
	 * Removes all fusion stones from a specific item.<br>
	 * This method updates the database and clears the stone set.<br>
	 * It also refreshes the item information for the player.
	 * @param player The {@link Player} who owns the item.
	 * @param item The {@link Item} to be cleared of fusion stones.
	 */
	public static void removeAllFusionStone(Player player, Item item)
	{
		if (item == null)
		{
			log.warn("Item not found during manastone remove");
			return;
		}
		
		if (!item.hasFusionStones())
		{
			return;
		}
		
		final Set<ManaStone> fusionStones = item.getFusionStones();
		for (ManaStone ms : fusionStones)
		{
			ms.setPersistentState(PersistentState.DELETED);
		}
		
		DAOManager.getDAO(ItemStoneListDAO.class).storeFusionStones(fusionStones);
		fusionStones.clear();
		
		ItemPacketService.updateItemAfterInfoChange(player, item);
	}
	
	/**
	 * Sockets a godstone into a specific weapon for the player.<br>
	 * This method checks if the item is equipped or in inventory.<br>
	 * It validates the item and stone before applying the effect.
	 * @param player The {@link Player} who owns the items.
	 * @param weaponIdObj The unique object ID of the target weapon.
	 * @param stoneId The template ID of the godstone to use.
	 */
	public static void socketGodstone(Player player, int weaponIdObj, int stoneId)
	{
		Item weaponItem = player.getEquipment().getEquippedItemByObjId(weaponIdObj); // if item equiped
		
		if (weaponItem == null)
		{
			// if item not equiped
			weaponItem = player.getInventory().getItemByObjId(weaponIdObj);
		}
		
		if (weaponItem == null)
		{
			// PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_CANNOT_GIVE_PROC_TO_EQUIPPED_ITEM);
			log.warn("Weapon item null");
			return;
		}
		
		if (!weaponItem.canSocketGodstone())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_NOT_ADD_PROC(new DescriptionId(weaponItem.getNameId())));
		}
		
		final Item godstone = player.getInventory().getFirstItemByItemId(stoneId);
		
		final int godStoneItemId = godstone.getItemTemplate().getTemplateId();
		final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(godStoneItemId);
		final GodstoneInfo godstoneInfo = itemTemplate.getGodstoneInfo();
		
		if (godstoneInfo == null)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_NO_PROC_GIVE_ITEM);
			log.warn("Godstone info missing for itemid " + godStoneItemId);
			return;
		}
		
		if (!player.getInventory().decreaseByItemId(stoneId, 1))
		{
			return;
		}
		
		weaponItem.addGodStone(godStoneItemId);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_ENCHANTED_TARGET_ITEM(new DescriptionId(weaponItem.getNameId())));
		
		ItemPacketService.updateItemAfterInfoChange(player, weaponItem);
	}
}
