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

import static com.aionemu.gameserver.services.item.ItemPacketService.sendItemDeletePacket;
import static com.aionemu.gameserver.services.item.ItemPacketService.sendStorageUpdatePacket;

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.ItemStorage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.services.ExchangeService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;

/**
 * Handles the logic for moving {@link Item} objects between different locations.<br>
 * This service manages transfers between player inventories, storage systems, and other players.
 * @author ATracer
 */
public class ItemMoveService
{
	/**
	 * Moves an item between different storage types or slots for a player.<br>
	 * This method handles stack merging and restriction checks automatically.<br>
	 * It updates the player's inventory and sends the necessary network packets.
	 * @param player The {@link Player} who owns the items.
	 * @param itemObjId The unique identifier of the item to move.
	 * @param sourceStorageType The storage type where the item is currently located.
	 * @param destinationStorageType The storage type where the item should be moved to.
	 * @param slot The target slot index, or {@code -1} to find the first available slot.
	 */
	public static void moveItem(Player player, int itemObjId, byte sourceStorageType, byte destinationStorageType, short slot)
	{
		if (ExchangeService.getInstance().isPlayerInExchange(player))
		{
			return;
		}
		
		final IStorage sourceStorage = player.getStorage(sourceStorageType);
		final Item item = player.getStorage(sourceStorageType).getItemByObjId(itemObjId);
		
		if (item == null)
		{
			return;
		}
		
		if (sourceStorageType == destinationStorageType)
		{
			if (item.getEquipmentSlot() != slot)
			{
				moveInSameStorage(sourceStorage, item, slot);
			}
			return;
		}
		
		if ((sourceStorageType != destinationStorageType) && (ItemRestrictionService.isItemRestrictedTo(player, item, destinationStorageType) || ItemRestrictionService.isItemRestrictedFrom(player, item, sourceStorageType)))
		{
			sendStorageUpdatePacket(player, StorageType.getStorageTypeById(sourceStorageType), item, ItemAddType.ALL_SLOT);
			return;
		}
		
		final IStorage targetStorage = player.getStorage(destinationStorageType);
		LegionService.getInstance().addWHItemHistory(player, item.getItemId(), item.getItemCount(), sourceStorage, targetStorage);
		if (slot == -1)
		{
			if (item.getItemTemplate().isStackable())
			{
				final List<Item> sameItems = targetStorage.getItemsByItemId(item.getItemId());
				for (Item sameItem : sameItems)
				{
					final long itemCount = item.getItemCount();
					if (itemCount == 0)
					{
						break;
					}
					
					// we can merge same stackable items
					ItemSplitService.mergeStacks(sourceStorage, targetStorage, item, sameItem, itemCount);
				}
			}
		}
		
		if (!targetStorage.isFull() && (item.getItemCount() > 0))
		{
			sourceStorage.remove(item);
			sendItemDeletePacket(player, StorageType.getStorageTypeById(sourceStorageType), item, ItemDeleteType.MOVE);
			item.setEquipmentSlot(sourceStorageType == destinationStorageType ? slot : ItemStorage.FIRST_AVAILABLE_SLOT);
			targetStorage.add(item);
		}
	}
	
	/**
	 * Moves an {@code Item} to a specific slot within the same {@link IStorage}.<br>
	 * This method updates the equipment slot of the item.<br>
	 * It also marks both the storage and the item as requiring a persistent state update.
	 * @param storage The {@link IStorage} where the item is located.
	 * @param item The {@code Item} to be moved.
	 * @param slot The target slot index for the move.
	 */
	private static void moveInSameStorage(IStorage storage, Item item, short slot)
	{
		storage.setPersistentState(PersistentState.UPDATE_REQUIRED);
		item.setEquipmentSlot(slot);
		item.setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Swaps the positions of two items between different storage types.<br>
	 * This method checks for item restrictions before performing the swap.<br>
	 * It updates both the database and sends the necessary packets to the {@link Player}.
	 * @param player The {@code Player} who owns the storage.
	 * @param sourceStorageType The ID of the first storage type.
	 * @param sourceItemObjId The unique ID of the item in the first storage.
	 * @param replaceStorageType The ID of the second storage type.
	 * @param replaceItemObjId The unique ID of the item in the second storage.
	 */
	public static void switchItemsInStorages(Player player, byte sourceStorageType, int sourceItemObjId, byte replaceStorageType, int replaceItemObjId)
	{
		final IStorage sourceStorage = player.getStorage(sourceStorageType);
		final IStorage replaceStorage = player.getStorage(replaceStorageType);
		
		final Item sourceItem = sourceStorage.getItemByObjId(sourceItemObjId);
		if (sourceItem == null)
		{
			return;
		}
		
		final Item replaceItem = replaceStorage.getItemByObjId(replaceItemObjId);
		if (replaceItem == null)
		{
			return;
		}
		
		// restrictions checks
		if (ItemRestrictionService.isItemRestrictedFrom(player, sourceItem, sourceStorageType) || ItemRestrictionService.isItemRestrictedFrom(player, replaceItem, replaceStorageType) || ItemRestrictionService.isItemRestrictedTo(player, sourceItem, replaceStorageType) || ItemRestrictionService.isItemRestrictedTo(player, replaceItem, sourceStorageType))
		{
			return;
		}
		
		final long sourceSlot = sourceItem.getEquipmentSlot();
		final long replaceSlot = replaceItem.getEquipmentSlot();
		
		sourceItem.setEquipmentSlot(replaceSlot);
		replaceItem.setEquipmentSlot(sourceSlot);
		
		sourceStorage.remove(sourceItem);
		replaceStorage.remove(replaceItem);
		
		// correct UI update order is 1)delete items 2) add items
		sendItemDeletePacket(player, StorageType.getStorageTypeById(sourceStorageType), sourceItem, ItemDeleteType.MOVE);
		sendItemDeletePacket(player, StorageType.getStorageTypeById(replaceStorageType), replaceItem, ItemDeleteType.MOVE);
		sourceStorage.add(replaceItem);
		replaceStorage.add(sourceItem);
	}
}
