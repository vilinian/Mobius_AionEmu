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
package com.aionemu.gameserver.model.items.storage;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;

/**
 * Represents a storage system for managing and persisting {@link Item} objects.<br>
 * This class provides the base functionality for different types of item containers.<br>
 * It handles how items are stored, retrieved, and updated within the game world.
 * @author KID, ATracer
 */
public abstract class Storage implements IStorage
{
	private final ItemStorage itemStorage;
	private Item kinahItem;
	private final StorageType storageType;
	private Queue<Item> deletedItems;
	/**
	 * Can be of 2 types: UPDATED and UPDATE_REQUIRED
	 */
	private PersistentState persistentState = PersistentState.UPDATED;
	
	/**
	 * Creates a new instance of {@link Storage}.<br>
	 * This constructor initializes the storage with default settings.<br>
	 * It sets the internal deleted items queue to {@code true}.
	 * @param storageType The type of storage to create.
	 */
	public Storage(StorageType storageType)
	{
		this(storageType, true);
	}
	
	/**
	 * Creates a new instance of {@link Storage}.<br>
	 * This constructor initializes the internal item storage based on the provided type.<br>
	 * It also determines if deleted items should be tracked.
	 * @param storageType The category of the storage to create.
	 * @param withDeletedItems Set to {@code true} to initialize the deleted items queue, otherwise {@code false}.
	 */
	public Storage(StorageType storageType, boolean withDeletedItems)
	{
		itemStorage = new ItemStorage(storageType);
		this.storageType = storageType;
		if (withDeletedItems)
		{
			deletedItems = new ConcurrentLinkedQueue<>();
		}
	}
	
	/**
	 * Retrieves the current amount of Kinah from the storage.<br>
	 * This method returns {@code 0} if no Kinah item exists.
	 * @return The total count of Kinah as a {@code long}.
	 */
	@Override
	public long getKinah()
	{
		return kinahItem == null ? 0 : kinahItem.getItemCount();
	}
	
	/**
	 * Retrieves the {@code Kinah} item from the storage.<br>
	 * This method returns the internal currency object.
	 * @return The {@code Item} representing the currency, or {@code null} if it does not exist.
	 */
	@Override
	public Item getKinahItem()
	{
		return kinahItem;
	}
	
	/**
	 * Gets the type of this storage.<br>
	 * This identifies how the storage is categorized within the system.
	 * @return the {@code StorageType} of this instance.
	 */
	@Override
	public StorageType getStorageType()
	{
		return storageType;
	}
	
	/**
	 * Increases the amount of kinah for a specific player.<br>
	 * This method updates the storage balance and notifies the {@link Player}.
	 * @param amount The quantity of kinah to add.
	 * @param actor The {@link Player} who is performing the action.
	 */
	public void increaseKinah(long amount, Player actor)
	{
		increaseKinah(amount, ItemUpdateType.INC_KINAH_COLLECT, actor);
	}
	
	/**
	 * Increases the amount of Kinah in the storage.<br>
	 * This method ensures a {@code kinahItem} exists before updating.<br>
	 * It calls {@code long, ItemUpdateType, Player)} if the amount is positive.
	 * @param amount The quantity of Kinah to add.
	 * @param updateType The type of item update to perform.
	 * @param actor The player performing the action.
	 */
	void increaseKinah(long amount, ItemUpdateType updateType, Player actor)
	{
		if (kinahItem == null)
		{
			add(ItemFactory.newItem(ItemId.KINAH.value(), 0), actor);
		}
		
		if (amount > 0)
		{
			increaseItemCount(kinahItem, amount, updateType, actor);
		}
	}
	
	/**
	 * Attempts to subtract a specific amount of kinah from the storage.<br>
	 * It checks if the {@link Player} has enough balance before proceeding.<br>
	 * If the operation succeeds, it returns {@code true}.<br>
	 * If there is insufficient balance, it returns {@code false}.
	 * @param amount The quantity of kinah to remove.
	 * @param actor The {@link Player} performing the action.
	 * @return {@code true} if the reduction was successful, otherwise {@code false}.
	 */
	public boolean tryDecreaseKinah(long amount, Player actor)
	{
		if (getKinah() >= amount)
		{
			decreaseKinah(amount, actor);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Attempts to subtract a specific amount of kinah from the storage.<br>
	 * This method checks if the player has enough balance before proceeding.<br>
	 * It calls {@code ItemUpdateType, Player)} if successful.
	 * @param amount The quantity of kinah to remove.
	 * @param updateType The type of item update to perform.
	 * @param actor The player performing the action.
	 * @return {@code true} if the reduction was successful.
	 */
	boolean tryDecreaseKinah(long amount, ItemUpdateType updateType, Player actor)
	{
		if (getKinah() >= amount)
		{
			decreaseKinah(amount, updateType, actor);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Reduces the amount of kinah for a specific player.<br>
	 * This method updates the storage balance and notifies the {@link Player}.
	 * @param amount The quantity of kinah to remove.
	 * @param actor The {@link Player} who is performing the action.
	 */
	public void decreaseKinah(long amount, Player actor)
	{
		decreaseKinah(amount, ItemUpdateType.DEC_KINAH_BUY, actor);
	}
	
	/**
	 * Reduces the amount of kinah in the storage.<br>
	 * This method calls {@code long, ItemUpdateType, Player)} if the {@code amount} is positive.
	 * @param amount The quantity of kinah to remove.
	 * @param updateType The type of item update to perform.
	 * @param actor The player performing the action.
	 */
	void decreaseKinah(long amount, ItemUpdateType updateType, Player actor)
	{
		if (amount > 0)
		{
			decreaseItemCount(kinahItem, amount, updateType, actor);
		}
	}
	
	/**
	 * Increases the quantity of a specific {@link Item} in storage.<br>
	 * This method updates the item count and returns the new total amount.
	 * @param item The {@code Item} object to modify.
	 * @param count The number of items to add.
	 * @param actor The {@link Player} performing the action.
	 * @return The new total count of the item after the increase.
	 */
	long increaseItemCount(Item item, long count, Player actor)
	{
		return increaseItemCount(item, count, ItemUpdateType.DEC_ITEM_USE, actor);
	}
	
	/**
	 * Increases the quantity of a specific {@link Item} in the storage.<br>
	 * This method sends an update packet to the {@code actor}.<br>
	 * It also sets the persistent state to {@code UPDATE_REQUIRED}.
	 * @param item The {@link Item} to be updated.
	 * @param count The amount to add to the item.
	 * @param updateType The type of update to send to the client.
	 * @param actor The {@link Player} who performed the action.
	 * @return The remaining count of the item after the increase.
	 */
	long increaseItemCount(Item item, long count, ItemUpdateType updateType, Player actor)
	{
		final long leftCount = item.increaseItemCount(count);
		ItemPacketService.sendItemPacket(actor, storageType, item, updateType);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return leftCount;
	}
	
	/**
	 * Reduces the quantity of a specific {@link Item} in storage.<br>
	 * This method updates the item count and notifies the {@code Player}.<br>
	 * It returns the new total amount of the item remaining.
	 * @param item The {@link Item} to be modified.
	 * @param count The number of items to remove.
	 * @param actor The {@link Player} performing the action.
	 * @return The updated quantity of the item after the decrease.
	 */
	long decreaseItemCount(Item item, long count, Player actor)
	{
		return this.decreaseItemCount(item, count, ItemUpdateType.DEC_ITEM_USE, actor);
	}
	
	/**
	 * Reduces the quantity of a specific {@code Item} in storage.<br>
	 * This method updates the item count based on the provided {@code ItemUpdateType}.<br>
	 * It returns the remaining amount of the item after the decrease.
	 * @param item The {@code Item} object to modify.
	 * @param count The number of items to remove.
	 * @param updateType The type of update to apply to the item.
	 * @param actor The {@code Player} performing the action.
	 * @return The new quantity of the item remaining in storage.
	 */
	long decreaseItemCount(Item item, long count, ItemUpdateType updateType, Player actor)
	{
		return decreaseItemCount(item, count, updateType, QuestStatus.NONE, actor);
	}
	
	/**
	 * Reduces the quantity of a specific {@code Item} in storage.<br>
	 * This method handles item deletion if the count reaches zero based on the {@code QuestStatus}.<br>
	 * It also sends the necessary update packets to the {@code Player}.
	 * @param item The {@code Item} object to modify.
	 * @param count The amount to subtract from the current count.
	 * @param updateType The type of update for the packet service.
	 * @param questStatus The current status of the quest associated with this action.
	 * @param actor The {@code Player} performing the action.
	 * @return The remaining count of the item after the decrease.
	 */
	long decreaseItemCount(Item item, long count, ItemUpdateType updateType, QuestStatus questStatus, Player actor)
	{
		if (item == null)
		{
			return 0;
		}
		
		final long leftCount = item.decreaseItemCount(count);
		if ((item.getItemCount() <= 0) && !item.getItemTemplate().isKinah())
		{
			if (questStatus == QuestStatus.NONE)
			{
				delete(item, ItemDeleteType.fromUpdateType(updateType), actor);
			}
			else
			{
				delete(item, ItemDeleteType.fromQuestStatus(questStatus), actor);
			}
		}
		else
		{
			ItemPacketService.sendItemPacket(actor, storageType, item, updateType);
		}
		
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return leftCount;
	}
	
	/**
	 * Processes an {@code Item} to determine its storage location.<br>
	 * If the item is a Kinah, it updates the {@code kinahItem} field.<br>
	 * Otherwise, it places the item into the {@code itemStorage}.
	 * @param item The {@code Item} object to be processed.
	 */
	@Override
	public void onLoadHandler(Item item)
	{
		if (item.getItemTemplate().isKinah())
		{
			kinahItem = item;
		}
		else
		{
			itemStorage.putItem(item);
		}
	}
	
	/**
	 * Adds an {@code Item} to the storage.<br>
	 * This method handles the logic for placing an object into the current storage container.<br>
	 * It uses the {@link Player} as the entity performing the action.
	 * @param item The {@code Item} to be added to the storage.
	 * @param actor The {@link Player} who is adding the item.
	 * @return The {@code Item} that was successfully added.
	 */
	Item add(Item item, Player actor)
	{
		return add(item, ItemService.DEFAULT_UPDATE_PREDICATE.getAddType(), actor);
	}
	
	/**
	 * Adds an {@code Item} to the storage.<br>
	 * This method handles special logic for kinah items and updates the persistent state.<br>
	 * It also sends a notification packet to the {@code Player}.
	 * @param item The {@code Item} to be added to the storage.
	 * @param addType The type of addition used for the network packet.
	 * @param actor The {@code Player} who is performing the action.
	 * @return The added {@code Item} if successful, or {@code null} if it could not be added.
	 */
	Item add(Item item, ItemAddType addType, Player actor)
	{
		if (item.getItemTemplate().isKinah())
		{
			kinahItem = item;
		}
		else if (!itemStorage.putItem(item))
		{
			return null;
		}
		
		item.setItemLocation(storageType.getId());
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		ItemPacketService.sendStorageUpdatePacket(actor, storageType, item, addType);
		
		// TODO: move to ItemService
		if (storageType == StorageType.CUBE)
		{
			QuestEngine.getInstance().onItemGet(new QuestEnv(null, actor, 0, 0), item.getItemTemplate().getTemplateId());
			if (item.getItemTemplate().isQuestUpdateItem())
			{
				actor.getController().updateNearbyQuests();
			}
		}
		
		return item;
	}
	
	/**
	 * Adds an {@link Item} to the storage.<br>
	 * If the item is Kinah, it updates the special kinah field.<br>
	 * Otherwise, it places the item into the internal storage list.<br>
	 * This method marks the storage state as requiring a database update.
	 * @param item The {@link Item} to be added to the storage.
	 * @return The added {@link Item} if successful, or {@code null} if the item could not be stored.
	 */
	public Item add_CharacterTransfer(Item item)
	{
		if (item.getItemTemplate().isKinah())
		{
			kinahItem = item;
		}
		else if (!itemStorage.putItem(item))
		{
			return null;
		}
		
		item.setItemLocation(storageType.getId());
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return item;
	}
	
	// a bit misleading name - but looks like its used only for equipment
	/**
	 * Adds an {@code Item} to the current storage.<br>
	 * This method updates the item location and notifies the {@link Player}.<br>
	 * It returns the added item or {@code null} if the operation fails.
	 * @param item The {@code Item} to be stored.
	 * @param actor The {@link Player} who is performing the action.
	 * @return The successfully stored {@code Item}, or {@code null}.
	 */
	Item put(Item item, Player actor)
	{
		if (!itemStorage.putItem(item))
		{
			return null;
		}
		
		item.setItemLocation(storageType.getId());
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		ItemPacketService.sendItemUpdatePacket(actor, storageType, item, ItemUpdateType.EQUIP_UNEQUIP);
		return item;
	}
	
	/**
	 * Removes a specific {@code Item} from the storage.<br>
	 * This method delegates the removal to the underlying {@link Storage} object.
	 * @param item The {@code Item} to be removed.
	 * @return The {@code Item} that was removed, or {@code null} if it was not found.
	 */
	@Override
	public Item remove(Item item)
	{
		return itemStorage.removeItem(item.getObjectId());
	}
	
	/**
	 * Removes an {@link Item} from the storage.<br>
	 * This method handles the deletion logic for a specific item.<br>
	 * It identifies the action as a quest reward removal.
	 * @param item The {@code Item} to be removed from the storage.
	 * @param actor The {@link Player} who is performing the deletion.
	 * @return The {@code Item} that was deleted.
	 */
	Item delete(Item item, Player actor)
	{
		return delete(item, ItemDeleteType.QUEST_REWARD, actor);
	}
	
	/**
	 * Removes an item from the storage and updates the player's view.<br>
	 * This method handles state changes and sends necessary network packets.<br>
	 * It also triggers quest updates if the item is a quest-related object.
	 * @param item The {@code Item} to be removed from the storage.
	 * @param deleteType The type of deletion for the packet sent to the player.
	 * @param actor The {@link Player} who performed the deletion action.
	 * @return The deleted {@code Item} if successful, or {@code null} if it was not found.
	 */
	Item delete(Item item, ItemDeleteType deleteType, Player actor)
	{
		if (remove(item) != null)
		{
			item.setPersistentState(PersistentState.DELETED);
			deletedItems.add(item);
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			ItemPacketService.sendItemDeletePacket(actor, StorageType.getStorageTypeById(item.getItemLocation()), item, deleteType);
			if (item.getItemTemplate().isQuestUpdateItem())
			{
				actor.getController().updateNearbyQuests();
			}
			
			return item;
		}
		
		return null;
	}
	
	/**
	 * Reduces the quantity of a specific item in storage.<br>
	 * This method checks if the {@code itemId} exists and has enough quantity.<br>
	 * It updates the inventory for the provided {@link Player}.
	 * @param itemId The unique identifier of the item to remove.
	 * @param count The amount of items to decrease.
	 * @param actor The player performing the action.
	 * @return {@code true} if the operation succeeded, or {@code false} otherwise.
	 */
	boolean decreaseByItemId(int itemId, long count, Player actor)
	{
		return decreaseByItemId(itemId, count, QuestStatus.NONE, actor);
	}
	
	/**
	 * Reduces the quantity of a specific item in storage.<br>
	 * This method iterates through items matching the {@code itemId}.<br>
	 * It uses {@code long, ItemUpdateType, QuestStatus, Player)} to update each item.
	 * @param itemId The unique identifier for the item type.
	 * @param count The total amount to remove from storage.
	 * @param questStatus The current status of the quest associated with this action.
	 * @param actor The {@link Player} performing the action.
	 * @return {@code true} if the full amount was successfully removed, {@code false} otherwise.
	 */
	boolean decreaseByItemId(int itemId, long count, QuestStatus questStatus, Player actor)
	{
		final List<Item> items = itemStorage.getItemsById(itemId);
		if (items.size() == 0)
		{
			return false;
		}
		
		for (Item item : items)
		{
			if (count == 0)
			{
				break;
			}
			
			count = decreaseItemCount(item, count, ItemUpdateType.DEC_ITEM_USE, questStatus, actor);
		}
		
		return count == 0;
	}
	
	/**
	 * Reduces the quantity of a specific item in storage.<br>
	 * This method uses the {@code ItemUpdateType.DEC_ITEM_USE} type.<br>
	 * It returns {@code true} if the operation succeeds.
	 * @param itemObjId The unique identifier of the item object.
	 * @param count The amount to subtract from the item stack.
	 * @param actor The {@link Player} performing the action.
	 * @return {@code true} if the quantity was successfully decreased, otherwise {@code false}.
	 */
	boolean decreaseByObjectId(int itemObjId, long count, Player actor)
	{
		return decreaseByObjectId(itemObjId, count, ItemUpdateType.DEC_ITEM_USE, actor);
	}
	
	/**
	 * Reduces the quantity of a specific item in storage.<br>
	 * This method checks if the item exists and has enough count before decreasing it.<br>
	 * It is used to handle item consumption for quests.
	 * @param itemObjId The unique identifier of the item object.
	 * @param count The amount to subtract from the item stack.
	 * @param questStatus The current status of the associated quest.
	 * @param actor The player performing the action.
	 * @return {@code true} if the reduction was successful, otherwise {@code false}.
	 */
	boolean decreaseByObjectId(int itemObjId, long count, QuestStatus questStatus, Player actor)
	{
		final Item item = itemStorage.getItemByObjId(itemObjId);
		if ((item == null) || (item.getItemCount() < count))
		{
			return false;
		}
		
		return decreaseByObjectId(itemObjId, count, questStatus, actor);
	}
	
	/**
	 * Reduces the quantity of a specific item in storage.<br>
	 * This method checks if the item exists and has enough quantity before decreasing it.<br>
	 * It returns {@code true} if the reduction was successful.
	 * @param itemObjId The unique identifier of the item to decrease.
	 * @param count The amount to subtract from the item.
	 * @param updateType The type of update to apply during the process.
	 * @param actor The {@link Player} performing the action.
	 * @return {@code true} if the count was successfully decreased, otherwise {@code false}.
	 */
	boolean decreaseByObjectId(int itemObjId, long count, ItemUpdateType updateType, Player actor)
	{
		final Item item = itemStorage.getItemByObjId(itemObjId);
		if ((item == null) || (item.getItemCount() < count))
		{
			return false;
		}
		
		return decreaseItemCount(item, count, updateType, actor) == 0;
	}
	
	/**
	 * Retrieves the first {@link Item} that matches a specific ID.<br>
	 * This method searches through the underlying storage.
	 * @param itemId The unique identifier of the item to find.
	 * @return The first {@code Item} found, or {@code null} if no match exists.
	 */
	@Override
	public Item getFirstItemByItemId(int itemId)
	{
		return itemStorage.getFirstItemById(itemId);
	}
	
	/**
	 * Retrieves all items from the storage.<br>
	 * If a {@code kinahItem} exists, it is added to the list.
	 * @return A {@code List} containing the {@link Item} objects.
	 */
	@Override
	public List<Item> getItemsWithKinah()
	{
		final List<Item> items = itemStorage.getItems();
		if (kinahItem != null)
		{
			items.add(kinahItem);
		}
		
		return items;
	}
	
	/**
	 * Retrieves all items from the underlying {@link Storage}.<br>
	 * This method delegates the request to the internal storage object.
	 * @return a {@code List} of {@link Item} objects.
	 */
	@Override
	public List<Item> getItems()
	{
		return itemStorage.getItems();
	}
	
	/**
	 * Retrieves a list of items that match a specific ID.<br>
	 * This method calls the underlying {@code getItemsByItemId} method.
	 * @param itemId The unique identifier for the item to search for.
	 * @return A {@code List} containing all matching {@link Item} objects.
	 */
	@Override
	public List<Item> getItemsByItemId(int itemId)
	{
		return itemStorage.getItemsById(itemId);
	}
	
	/**
	 * Retrieves the collection of items that have been removed.<br>
	 * This method returns the internal queue of {@link Item} objects.
	 * @return A {@code Queue} containing all {@link Item} objects marked as deleted.
	 */
	@Override
	public Queue<Item> getDeletedItems()
	{
		return deletedItems;
	}
	
	/**
	 * Retrieves an {@link Item} based on its unique object ID.<br>
	 * This method looks up the item in the internal storage map.
	 * @param itemObjId The unique identifier of the item to find.
	 * @return The {@code Item} associated with the provided ID, or {@code null} if not found.
	 */
	@Override
	public Item getItemByObjId(int itemObjId)
	{
		return itemStorage.getItemByObjId(itemObjId);
	}
	
	/**
	 * Calculates the total count of items for a specific ID.<br>
	 * It sums up the quantities of all matching {@code Item} objects.
	 * @param itemId The unique identifier of the item to count.
	 * @return The total sum of counts for the given {@code itemId}.
	 */
	@Override
	public long getItemCountByItemId(int itemId)
	{
		final List<Item> temp = itemStorage.getItemsById(itemId);
		if (temp.size() == 0)
		{
			return 0;
		}
		
		long cnt = 0;
		for (Item item : temp)
		{
			cnt += item.getItemCount();
		}
		
		return cnt;
	}
	
	/**
	 * Checks if the storage has reached its maximum capacity.<br>
	 * It delegates this check to the internal {@code itemStorage}.
	 * @return {@code true} if the storage is full, {@code false} otherwise.
	 */
	@Override
	public boolean isFull()
	{
		return itemStorage.isFull();
	}
	
	/**
	 * Checks if the storage is full of special cube items.<br>
	 * This method compares the count of special cubes to the {@code specialLimit}.
	 * @return {@code true} if the number of special cubes reaches or exceeds the limit, {@code false} otherwise.
	 */
	public boolean isFullSpecialCube()
	{
		return itemStorage.isFullSpecialCube();
	}
	
	/**
	 * Checks if the storage capacity is currently reached.<br>
	 * This method evaluates the status based on the provided {@code inventory} count.
	 * @param inventory The current number of items in the storage.
	 * @return {@code true} if the storage is full, otherwise {@code false}.
	 */
	public boolean isFull(int inventory)
	{
		if (inventory > 0)
		{
			return isFullSpecialCube();
		}
		
		return isFull();
	}
	
	/**
	 * Calculates the number of available slots in the storage.<br>
	 * It checks if the {@code inventory} is greater than {@code 0}.<br>
	 * If true, it calls {@code getSpecialCubeFreeSlots}.<br>
	 * Otherwise, it returns the standard free slot count.
	 * @param inventory The current inventory size to check.
	 * @return The number of available slots as an {@code int}.
	 */
	public int getFreeSlots(int inventory)
	{
		if (inventory > 0)
		{
			return getSpecialCubeFreeSlots();
		}
		
		return getFreeSlots();
	}
	
	/**
	 * Retrieves the number of available slots for special cube items.<br>
	 * This value is provided by the internal {@link ItemStorage} instance.
	 * @return The count of free slots remaining for special cubes.
	 */
	public int getSpecialCubeFreeSlots()
	{
		return itemStorage.getSpecialCubeFreeSlots();
	}
	
	/**
	 * Calculates the number of available slots in the storage.<br>
	 * This value is based on the total {@code limit} minus the current count of cube items.
	 * @return The number of free slots remaining.
	 */
	@Override
	public int getFreeSlots()
	{
		return itemStorage.getFreeSlots();
	}
	
	/**
	 * Updates the maximum number of items allowed in this storage.<br>
	 * This method checks if the current cube item count exceeds the new {@code limit}.<br>
	 * If the limit is valid, it updates the value and returns {@code true}.<br>
	 * Otherwise, it returns {@code false}.
	 * @param limit The new maximum capacity for items.
	 * @return {@code true} if the limit was successfully updated, {@code false} otherwise.
	 */
	public boolean setLimit(int limit)
	{
		return itemStorage.setLimit(limit);
	}
	
	/**
	 * Retrieves the maximum number of items allowed in this storage.<br>
	 * This value is used to determine if the storage is full.
	 * @return The current {@code int} limit.
	 */
	@Override
	public int getLimit()
	{
		return itemStorage.getLimit();
	}
	
	/**
	 * Returns the total number of slots in this storage.<br>
	 * This value is determined by the {@link StorageType}.
	 * @return The length of the row as an {@code int}.
	 */
	@Override
	public int getRowLength()
	{
		return itemStorage.getRowLength();
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	@Override
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this decoration.<br>
	 * This method assigns a new {@link PersistentState} to the object.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	@Override
	public void setPersistentState(PersistentState persistentState)
	{
		this.persistentState = persistentState;
	}
	
	/**
	 * Returns the total number of items in this storage.<br>
	 * This method delegates to {@code itemStorage.size()}.
	 * @return The count of items currently held in the storage.
	 */
	@Override
	public int size()
	{
		return itemStorage.size();
	}
	
	/**
	 * Removes all items from this storage.<br>
	 * This method iterates through the {@code itemStorage} and calls {@code remove} for each one.
	 */
	public void clear()
	{
		for (Item i : itemStorage.getItems())
		{
			remove(i);
		}
	}
}
