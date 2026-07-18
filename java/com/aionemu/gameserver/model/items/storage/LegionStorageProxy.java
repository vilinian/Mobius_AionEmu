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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * This class acts as a proxy for the {@link Storage} system specifically for Legion storage.<br>
 * It handles item interactions and management within the context of Legion-related mechanics.
 * @author ATracer
 */
public class LegionStorageProxy extends Storage
{
	private final Player actor;
	private final Storage storage;
	
	/**
	 * Creates a new {@link LegionStorageProxy} instance.<br>
	 * This proxy wraps an existing {@code Storage} object for a specific {@link Player}.<br>
	 * It allows the player to interact with the storage system.
	 * @param storage The underlying {@code Storage} object to wrap.
	 * @param actor The {@link Player} who owns or is interacting with this storage.
	 */
	public LegionStorageProxy(Storage storage, Player actor)
	{
		super(storage.getStorageType(), false);
		this.actor = actor;
		this.storage = storage;
	}
	
	/**
	 * Adds a specific amount of currency to the storage.<br>
	 * This method updates the balance for the associated {@link Player}.
	 * @param amount The number of Kinah to add.
	 */
	@Override
	public void increaseKinah(long amount)
	{
		storage.increaseKinah(amount, actor);
	}
	
	/**
	 * Increases the amount of Kinah in the storage.<br>
	 * This method updates the balance based on the provided {@code amount}.<br>
	 * It also handles how the change is communicated to the client.
	 * @param amount The number of Kinah to add.
	 * @param updateType The type of item update to perform.
	 */
	@Override
	public void increaseKinah(long amount, ItemUpdateType updateType)
	{
		storage.increaseKinah(amount, updateType, actor);
	}
	
	/**
	 * Attempts to subtract a specific amount of Kinah from the storage.<br>
	 * This method checks if enough funds are available before proceeding.
	 * @param amount The quantity of Kinah to remove.
	 * @return {@code true} if the deduction was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean tryDecreaseKinah(long amount)
	{
		return storage.tryDecreaseKinah(amount, actor);
	}
	
	/**
	 * Attempts to subtract a specific amount of Kinah from the storage.<br>
	 * This method checks if the balance is sufficient before making changes.<br>
	 * It uses the provided {@code ItemUpdateType} to handle synchronization.
	 * @param amount The quantity of Kinah to remove.
	 * @param updateType The type of item update to perform.
	 * @return {@code true} if the deduction was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean tryDecreaseKinah(long amount, ItemUpdateType updateType)
	{
		return storage.tryDecreaseKinah(amount, updateType, actor);
	}
	
	/**
	 * Reduces the amount of Kinah in the storage.<br>
	 * This method updates the balance for the associated {@link Player}.
	 * @param amount The quantity of Kinah to remove.
	 */
	@Override
	public void decreaseKinah(long amount)
	{
		storage.decreaseKinah(amount, actor);
	}
	
	/**
	 * Reduces the amount of Kinah in the storage.<br>
	 * This method updates the balance based on the provided type.
	 * @param amount The quantity of Kinah to remove.
	 * @param updateType The {@code ItemUpdateType} used for the transaction.
	 */
	@Override
	public void decreaseKinah(long amount, ItemUpdateType updateType)
	{
		storage.decreaseKinah(amount, updateType, actor);
	}
	
	/**
	 * Increases the quantity of a specific {@code Item} in the storage.<br>
	 * This method updates the item count for the provided {@code actor}.
	 * @param item The {@code Item} object to update.
	 * @param count The amount to add to the current stack.
	 * @return The new total count of the item after the increase.
	 */
	@Override
	public long increaseItemCount(Item item, long count)
	{
		return storage.increaseItemCount(item, count, actor);
	}
	
	/**
	 * Increases the quantity of a specific item in the storage.<br>
	 * This method updates the item count based on the provided type.
	 * @param item The {@code Item} object to be updated.
	 * @param count The amount to add to the item.
	 * @param updateType The {@code ItemUpdateType} defining how the change is handled.
	 * @return The new total count of the item after the increase.
	 */
	@Override
	public long increaseItemCount(Item item, long count, ItemUpdateType updateType)
	{
		return storage.increaseItemCount(item, count, updateType, actor);
	}
	
	/**
	 * Reduces the quantity of a specific item in the storage.<br>
	 * This method updates the {@code Item} count by the specified amount.
	 * @param item The {@link Item} to be modified.
	 * @param count The number of items to remove from the storage.
	 * @return The new total count of the item after the decrease.
	 */
	@Override
	public long decreaseItemCount(Item item, long count)
	{
		return storage.decreaseItemCount(item, count, actor);
	}
	
	/**
	 * Reduces the quantity of a specific item in the storage.<br>
	 * This method updates the inventory and returns the new total amount.
	 * @param item The {@code Item} object to be modified.
	 * @param count The number of items to remove.
	 * @param updateType The type of update to apply during the process.
	 * @return The remaining count of the item after the decrease.
	 */
	@Override
	public long decreaseItemCount(Item item, long count, ItemUpdateType updateType)
	{
		return storage.decreaseItemCount(item, count, updateType, actor);
	}
	
	/**
	 * Reduces the quantity of a specific item in storage.<br>
	 * This method is used when an item is consumed or moved.<br>
	 * It requires information about the update type and quest status.
	 * @param item The {@code Item} object to be modified.
	 * @param count The amount to subtract from the current total.
	 * @param updateType The {@code ItemUpdateType} defining how the change is handled.
	 * @param questStatus The {@code QuestStatus} associated with this action.
	 * @return The new quantity of the item after the decrease.
	 */
	@Override
	public long decreaseItemCount(Item item, long count, ItemUpdateType updateType, QuestStatus questStatus)
	{
		throw new UnsupportedOperationException("Quests should not update LWH!");
	}
	
	/**
	 * Adds a new {@link Item} to the storage.<br>
	 * This method delegates the action to the underlying {@code Storage} object.
	 * @param item The {@code Item} to be added.
	 * @return The {@code Item} that was successfully added.
	 */
	@Override
	public Item add(Item item)
	{
		return storage.add(item, actor);
	}
	
	/**
	 * Adds an {@code Item} to the storage.<br>
	 * This method uses the specified {@code ItemAddType} for the operation.<br>
	 * It delegates the action to the underlying {@link Storage} object.
	 * @param item The {@code Item} to be added.
	 * @param addType The type of addition to perform.
	 * @return The {@code Item} that was added.
	 */
	@Override
	public Item add(Item item, ItemAddType addType)
	{
		return storage.add(item, addType, actor);
	}
	
	/**
	 * Adds an {@code Item} to the storage.<br>
	 * This method delegates the action to the underlying {@link Storage} object.
	 * @param item The {@code Item} to be stored.
	 * @return The {@code Item} that was added to the storage.
	 */
	@Override
	public Item put(Item item)
	{
		return storage.put(item, actor);
	}
	
	/**
	 * Removes a specific {@code Item} from the storage.<br>
	 * This method calls the underlying {@code Player)} logic.
	 * @param item The {@code Item} object to be removed.
	 * @return The {@code Item} that was deleted or {@code null}.
	 */
	@Override
	public Item delete(Item item)
	{
		return storage.delete(item, actor);
	}
	
	/**
	 * Removes an {@code Item} from the storage.<br>
	 * This method uses the specified {@code ItemDeleteType} to handle the deletion logic.
	 * @param item The {@code Item} object to be removed.
	 * @param deleteType The type of deletion to perform.
	 * @return The {@code Item} that was deleted, or {@code null} if it did not exist.
	 */
	@Override
	public Item delete(Item item, ItemDeleteType deleteType)
	{
		return storage.delete(item, deleteType, actor);
	}
	
	/**
	 * Reduces the quantity of a specific item in the storage.<br>
	 * This method checks if enough items exist before subtracting.
	 * @param itemId The unique identifier for the item to decrease.
	 * @param count The amount of the item to remove.
	 * @return {@code true} if the operation succeeded, or {@code false} otherwise.
	 */
	@Override
	public boolean decreaseByItemId(int itemId, long count)
	{
		return storage.decreaseByItemId(itemId, count, actor);
	}
	
	/**
	 * Decreases the quantity of a specific item in storage.<br>
	 * This method is used when an item is consumed for a quest.<br>
	 * It will throw an {@code UnsupportedOperationException} if called with a valid status.
	 * @param itemId The unique identifier of the item to decrease.
	 * @param count The amount of the item to remove from storage.
	 * @param questStatus The current status of the quest associated with this action.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean decreaseByItemId(int itemId, long count, QuestStatus questStatus)
	{
		throw new UnsupportedOperationException("Quests should not update LWH!");
	}
	
	/**
	 * Reduces the quantity of a specific item in the storage.<br>
	 * This method uses the unique object ID to identify the target item.<br>
	 * It delegates the operation to the underlying {@link Storage} instance.
	 * @param itemObjId The unique identifier for the item object.
	 * @param count The amount to subtract from the current quantity.
	 * @return {@code true} if the reduction was successful, {@code false} otherwise.
	 */
	@Override
	public boolean decreaseByObjectId(int itemObjId, long count)
	{
		return storage.decreaseByObjectId(itemObjId, count, actor);
	}
	
	/**
	 * Reduces the quantity of a specific item in the storage.<br>
	 * This method uses the unique object ID to identify the target item.<br>
	 * It updates the storage based on the provided {@code ItemUpdateType}.
	 * @param itemObjId The unique identifier for the item object.
	 * @param count The amount to subtract from the current quantity.
	 * @param updateType The type of update to apply during the operation.
	 * @return {@code true} if the reduction was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean decreaseByObjectId(int itemObjId, long count, ItemUpdateType updateType)
	{
		return storage.decreaseByObjectId(itemObjId, count, updateType, actor);
	}
	
	/**
	 * Decreases the quantity of an item in storage based on its unique object ID.<br>
	 * This method is specifically used for quest-related item updates.<br>
	 * It will throw an {@code UnsupportedOperationException} if called.
	 * @param itemObjId The unique identifier of the item object to decrease.
	 * @param count The amount to subtract from the current quantity.
	 * @param questStatus The status of the quest associated with this action.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean decreaseByObjectId(int itemObjId, long count, QuestStatus questStatus)
	{
		throw new UnsupportedOperationException("Quests should not update LWH!");
	}
	
	/**
	 * Retrieves the current amount of Kinah from the underlying storage.<br>
	 * This method delegates the request to the {@link Storage} object.
	 * @return The total amount of Kinah as a {@code long}.
	 */
	@Override
	public long getKinah()
	{
		return storage.getKinah();
	}
	
	/**
	 * Retrieves the {@code Kinah} item from the underlying storage.<br>
	 * This method delegates the request to the internal {@link Storage} object.
	 * @return The {@code Item} representing the currency, or {@code null} if it does not exist.
	 */
	@Override
	public Item getKinahItem()
	{
		return storage.getKinahItem();
	}
	
	/**
	 * Retrieves the type of the underlying storage.<br>
	 * This method delegates the call to the internal {@code Storage} object.
	 * @return the {@code StorageType} of this proxy.
	 */
	@Override
	public StorageType getStorageType()
	{
		return storage.getStorageType();
	}
	
	/**
	 * Delegates the loading logic to the underlying {@link Storage} instance.<br>
	 * This ensures that the item is processed according to specific storage rules.
	 * @param item The {@code Item} object to be handled by the storage.
	 */
	@Override
	public void onLoadHandler(Item item)
	{
		storage.onLoadHandler(item);
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
		return storage.remove(item);
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
		return storage.getFirstItemByItemId(itemId);
	}
	
	/**
	 * Retrieves all items that have a Kinah value.<br>
	 * This method delegates the request to the underlying {@link Storage} object.
	 * @return A {@code List} containing the filtered {@link Item} objects.
	 */
	@Override
	public List<Item> getItemsWithKinah()
	{
		return storage.getItemsWithKinah();
	}
	
	/**
	 * Retrieves all items from the underlying {@link Storage}.<br>
	 * This method delegates the request to the internal storage object.
	 * @return a {@code List} of {@link Item} objects.
	 */
	@Override
	public List<Item> getItems()
	{
		return storage.getItems();
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
		return storage.getItemsByItemId(itemId);
	}
	
	/**
	 * Retrieves a list of items that were recently deleted.<br>
	 * This method fetches the data from the underlying {@link Storage} object.
	 * @return A {@code Queue} containing all {@link Item} objects marked as deleted.
	 */
	@Override
	public Queue<Item> getDeletedItems()
	{
		return storage.getDeletedItems();
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
		return storage.getItemByObjId(itemObjId);
	}
	
	/**
	 * Checks if the storage has reached its maximum capacity.<br>
	 * This method delegates the check to the underlying {@link Storage} object.
	 * @return {@code true} if the storage is full, {@code false} otherwise.
	 */
	@Override
	public boolean isFull()
	{
		return storage.isFull();
	}
	
	/**
	 * Calculates the number of available slots in the storage.<br>
	 * This value is based on the total {@code limit} minus the current count of cube items.
	 * @return The number of free slots remaining.
	 */
	@Override
	public int getFreeSlots()
	{
		return storage.getFreeSlots();
	}
	
	/**
	 * Updates the maximum capacity of the underlying storage.<br>
	 * This method delegates the operation to the internal {@link Storage} object.<br>
	 * It returns {@code true} if the update was successful.
	 * @param limit The new maximum number of items allowed.
	 * @return {@code true} if the limit was updated, {@code false} otherwise.
	 */
	@Override
	public boolean setLimit(int limit)
	{
		return storage.setLimit(limit);
	}
	
	/**
	 * Retrieves the maximum number of items allowed in this storage.<br>
	 * This value is used to determine if the storage is full.
	 * @return The current {@code int} limit.
	 */
	@Override
	public int getLimit()
	{
		return storage.getLimit();
	}
	
	/**
	 * Returns the total number of items in the storage.<br>
	 * This method delegates to the underlying {@code size} method.
	 * @return The current count of items.
	 */
	@Override
	public int size()
	{
		return storage.size();
	}
	
	/**
	 * Sets the owner of this object to a specific {@link Player}.<br>
	 * This method updates the internal {@code owner} field.
	 * @param player The {@code Player} who will become the new owner.
	 */
	@Override
	public void setOwner(Player player)
	{
		throw new UnsupportedOperationException("LWH doesnt have owner");
	}
}
