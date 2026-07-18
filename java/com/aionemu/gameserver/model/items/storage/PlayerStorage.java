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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * Represents the personal storage system for a {@link Player}.<br>
 * It manages the collection of {@link Item} objects owned by a specific character.<br>
 * This class extends the base {@link Storage} functionality to handle player-specific data.
 * @author ATracer
 */
public class PlayerStorage extends Storage
{
	private Player actor;
	
	/**
	 * Creates a new instance of {@link PlayerStorage}.<br>
	 * This constructor initializes the storage using the provided type.
	 * @param storageType The {@code StorageType} to assign to this storage.
	 */
	public PlayerStorage(StorageType storageType)
	{
		super(storageType);
	}
	
	/**
	 * Sets the owner of this {@link PlayerStorage}.<br>
	 * This method assigns a {@code Player} to the storage.
	 * @param actor The {@code Player} who will own the storage.
	 */
	@Override
	public void setOwner(Player actor)
	{
		this.actor = actor;
	}
	
	/**
	 * Processes the loading logic for a specific {@code Item}.<br>
	 * It checks if the item is already equipped to determine the correct handler.<br>
	 * If it is equipped, it delegates to the actor's equipment handler.<br>
	 * Otherwise, it calls the default behavior from the superclass.
	 * @param item The {@code Item} object to process.
	 */
	@Override
	public void onLoadHandler(Item item)
	{
		if (item.isEquipped())
		{
			actor.getEquipment().onLoadHandler(item);
		}
		else
		{
			super.onLoadHandler(item);
		}
	}
	
	/**
	 * Adds a specific amount of currency to the storage.<br>
	 * This method updates the balance for the associated {@link Player}.
	 * @param amount The number of Kinah to add.
	 */
	@Override
	public void increaseKinah(long amount)
	{
		increaseKinah(amount, actor);
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
		increaseKinah(amount, updateType, actor);
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
		return tryDecreaseKinah(amount, actor);
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
		return tryDecreaseKinah(amount, updateType, actor);
	}
	
	/**
	 * Reduces the amount of Kinah in the storage.<br>
	 * This method updates the balance for the associated {@link Player}.
	 * @param amount The quantity of Kinah to remove.
	 */
	@Override
	public void decreaseKinah(long amount)
	{
		decreaseKinah(amount, actor);
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
		decreaseKinah(amount, updateType, actor);
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
		return increaseItemCount(item, count, actor);
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
		return increaseItemCount(item, count, updateType, actor);
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
		return decreaseItemCount(item, count, actor);
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
		return decreaseItemCount(item, count, updateType, actor);
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
		return decreaseItemCount(item, count, updateType, questStatus, actor);
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
		return add(item, actor);
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
		return add(item, addType, actor);
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
		return put(item, actor);
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
		return delete(item, actor);
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
		return delete(item, deleteType, actor);
	}
	
	/**
	 * Reduces the quantity of an item in storage using its ID.<br>
	 * This method checks if enough items exist before subtracting.
	 * @param itemId The unique identifier for the item to decrease.
	 * @param count The amount of the item to remove.
	 * @return {@code true} if the operation succeeded, or {@code false} otherwise.
	 */
	@Override
	public boolean decreaseByItemId(int itemId, long count)
	{
		return decreaseByItemId(itemId, count, actor);
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
		return decreaseByItemId(itemId, count, questStatus, actor);
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
		return decreaseByObjectId(itemObjId, count, actor);
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
		return decreaseByObjectId(itemObjId, count, questStatus, actor);
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
		return decreaseByObjectId(itemObjId, count, updateType, actor);
	}
}
