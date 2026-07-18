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

import static com.aionemu.gameserver.services.item.ItemPacketService.sendStorageUpdatePacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.ExchangeService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for splitting items into multiple smaller quantities.<br>
 * It manages {@link Item} updates and ensures correct synchronization with player storage.
 * @author ATracer
 */
public class ItemSplitService
{
	private static final Logger log = LoggerFactory.getLogger(ItemSplitService.class);
	
	/**
	 * Splits a stack of items from one storage location to another.<br>
	 * This method handles moving quantities between different storage types or slots.<br>
	 * It validates player status and item restrictions before performing the split.
	 * @param player The {@link Player} who is performing the action.
	 * @param itemObjId The unique object ID of the source item to be split.
	 * @param destinationObjId The unique object ID of the target location or item.
	 * @param splitAmount The quantity of items to move.
	 * @param slotNum The specific slot number for the new item if in the same storage.
	 * @param sourceStorageType The type identifier for the source storage.
	 * @param destinationStorageType The type identifier for the destination storage.
	 */
	public static void splitItem(Player player, int itemObjId, int destinationObjId, long splitAmount, short slotNum, byte sourceStorageType, byte destinationStorageType)
	{
		if (splitAmount <= 0)
		{
			return;
		}
		
		if (player.isTrading())
		{
			// You cannot split items in the inventory during a trade.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300713));
			return;
		}
		
		final IStorage sourceStorage = player.getStorage(sourceStorageType);
		final IStorage destStorage = player.getStorage(destinationStorageType);
		if ((sourceStorage == null) || (destStorage == null))
		{
			log.warn(String.format("storage null playerName sourceStorage destStorage %s %d %d", player.getName(), sourceStorageType, destinationStorageType));
			return;
		}
		
		Item sourceItem = sourceStorage.getItemByObjId(itemObjId);
		final Item targetItem = destStorage.getItemByObjId(destinationObjId);
		
		if (sourceItem == null)
		{
			sourceItem = sourceStorage.getKinahItem();
			if ((sourceItem == null) || (sourceItem.getObjectId() != itemObjId))
			{
				log.warn(String.format("CHECKPOINT: attempt to split null item %d %d %d", itemObjId, splitAmount, slotNum));
				return;
			}
		}
		
		if ((sourceStorageType != destinationStorageType) && (ItemRestrictionService.isItemRestrictedTo(player, sourceItem, destinationStorageType) || ItemRestrictionService.isItemRestrictedFrom(player, sourceItem, sourceStorageType)))
		{
			sendStorageUpdatePacket(player, StorageType.getStorageTypeById(sourceStorageType), sourceItem);
			return;
		}
		
		// To move kinah from inventory to warehouse and vice versa client using split item packet
		if (sourceItem.getItemTemplate().isKinah())
		{
			moveKinah(player, sourceStorage, splitAmount);
			return;
		}
		
		if (targetItem == null)
		{
			final long oldItemCount = sourceItem.getItemCount() - splitAmount;
			if ((sourceItem.getItemCount() < splitAmount) || (oldItemCount == 0))
			{
				return;
			}
			
			if (sourceStorageType != destinationStorageType)
			{
				LegionService.getInstance().addWHItemHistory(player, sourceItem.getItemId(), splitAmount, sourceStorage, destStorage);
			}
			
			final Item newItem = ItemFactory.newItem(sourceItem.getItemTemplate().getTemplateId(), splitAmount);
			if (sourceStorageType == destinationStorageType)
			{
				newItem.setEquipmentSlot(slotNum);
			}
			
			sourceStorage.decreaseItemCount(sourceItem, splitAmount, sourceStorageType == destinationStorageType ? ItemUpdateType.DEC_ITEM_SPLIT : ItemUpdateType.DEC_ITEM_SPLIT_MOVE);
			PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(sourceStorage.getStorageType(), player));
			if (destStorage.add(newItem) == null)
			{
				// if item was not added - we can release its id
				ItemService.releaseItemId(newItem);
			}
		}
		else if (targetItem.getItemId() == sourceItem.getItemId())
		{
			if (sourceStorageType != destinationStorageType)
			{
				LegionService.getInstance().addWHItemHistory(player, sourceItem.getItemId(), splitAmount, sourceStorage, destStorage);
			}
			
			mergeStacks(sourceStorage, destStorage, sourceItem, targetItem, splitAmount);
		}
	}
	
	/**
	 * Merges a specific amount of items from one stack into another.<br>
	 * This method updates the counts in both {@code sourceStorage} and {@code destStorage}.<br>
	 * It ensures that the number of moved items does not exceed the available space.
	 * @param sourceStorage The storage containing the original item stack.
	 * @param destStorage The storage where the items will be merged into.
	 * @param sourceItem The {@link Item} being taken from.
	 * @param targetItem The {@link Item} receiving the additional count.
	 * @param count The amount of items to move.
	 */
	public static void mergeStacks(IStorage sourceStorage, IStorage destStorage, Item sourceItem, Item targetItem, long count)
	{
		if (sourceItem.getItemCount() >= count)
		{
			final long freeCount = targetItem.getFreeCount();
			count = count > freeCount ? freeCount : count;
			final long leftCount = destStorage.increaseItemCount(targetItem, count, sourceStorage.getStorageType() == destStorage.getStorageType() ? ItemUpdateType.INC_ITEM_MERGE : ItemUpdateType.INC_ITEM_COLLECT);
			sourceStorage.decreaseItemCount(sourceItem, count - leftCount, sourceStorage.getStorageType() == destStorage.getStorageType() ? ItemUpdateType.DEC_ITEM_SPLIT : ItemUpdateType.DEC_ITEM_SPLIT_MOVE);
		}
		
	}
	
	/**
	 * Moves a specific amount of Kinah between storage types.<br>
	 * This method handles transfers between the {@code CUBE} and {@code ACCOUNT_WAREHOUSE}.<br>
	 * It validates the balance before calling {@code long, IStorage)}.
	 * @param player The {@code Player} performing the action.
	 * @param source The {@code IStorage} where Kinah is being taken from.
	 * @param splitAmount The amount of Kinah to move.
	 */
	private static void moveKinah(Player player, IStorage source, long splitAmount)
	{
		if ((source.getKinah() < splitAmount) || ExchangeService.getInstance().isPlayerInExchange(player))
		{
			return;
		}
		
		switch (source.getStorageType())
		{
			case CUBE:
			{
				final IStorage destination = player.getStorage(StorageType.ACCOUNT_WAREHOUSE.getId());
				final long chksum = (source.getKinah() - splitAmount) + (destination.getKinah() + splitAmount);
				
				if (chksum != (source.getKinah() + destination.getKinah()))
				{
					return;
				}
				
				updateKinahCount(source, splitAmount, destination);
				break;
			}
			
			case ACCOUNT_WAREHOUSE:
			{
				final IStorage destination = player.getStorage(StorageType.CUBE.getId());
				final long chksum = (source.getKinah() - splitAmount) + (destination.getKinah() + splitAmount);
				
				if (chksum != (source.getKinah() + destination.getKinah()))
				{
					return;
				}
				
				updateKinahCount(source, splitAmount, destination);
				break;
			}
			default:
				break;
		}
	}
	
	/**
	 * Updates the Kinah balance between two storage locations.<br>
	 * This method subtracts a specific amount from the {@code source} and adds it to the {@code destination}.<br>
	 * It uses internal update types to track these changes correctly.
	 * @param source The {@link IStorage} where the Kinah will be removed from.
	 * @param splitAmount The number of Kinah to move between storages.
	 * @param destination The {@link IStorage} where the Kinah will be added to.
	 */
	private static void updateKinahCount(IStorage source, long splitAmount, IStorage destination)
	{
		source.decreaseKinah(splitAmount, ItemUpdateType.DEC_ITEM_SPLIT);
		destination.increaseKinah(splitAmount, ItemUpdateType.INC_KINAH_MERGE);
	}
}
