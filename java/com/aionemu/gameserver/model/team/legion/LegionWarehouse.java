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
package com.aionemu.gameserver.model.team.legion;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * Represents a specialized storage system for the legion.<br>
 * It extends {@link Storage} to provide shared inventory functionality for players within a team.
 * @author Simple
 */
public class LegionWarehouse extends Storage
{
	private Legion legion;
	private int curentWhUser;
	
	/**
	 * Creates a new {@link LegionWarehouse} instance for a specific legion.<br>
	 * This constructor initializes the storage type and sets the capacity based on the {@code legion}.
	 * @param legion The {@link Legion} object associated with this warehouse.
	 */
	public LegionWarehouse(Legion legion)
	{
		super(StorageType.LEGION_WAREHOUSE);
		this.legion = legion;
		setLimit(legion.getWarehouseSlots());
	}
	
	/**
	 * Retrieves the {@link Legion} associated with this member.<br>
	 * This method returns the internal {@code legion} field.
	 * @return the {@code Legion} object or {@code null} if no legion is assigned.
	 */
	public Legion getLegion()
	{
		return legion;
	}
	
	/**
	 * Sets the owner of this warehouse.<br>
	 * This method updates the {@code legion} field with a new {@link Legion} object.
	 * @param legion The {@code Legion} that will own this warehouse.
	 */
	public void setOwnerLegion(Legion legion)
	{
		this.legion = legion;
	}
	
	/**
	 * Adds a specific amount of currency to the storage.<br>
	 * This method updates the balance for the associated {@link Player}.
	 * @param amount The number of Kinah to add.
	 */
	@Override
	public void increaseKinah(long amount)
	{
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}
	
	/**
	 * Reduces the amount of Kinah in the storage.<br>
	 * This method updates the balance for the associated {@link Player}.
	 * @param amount The quantity of Kinah to remove.
	 */
	@Override
	public void decreaseKinah(long amount)
	{
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}
	
	/**
	 * Adds an {@link Item} to the warehouse.<br>
	 * This method is not supported directly and must be called through a proxy.
	 * @param item The {@code Item} to add.
	 * @return The added {@code Item}.
	 */
	@Override
	public Item add(Item item)
	{
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}
	
	/**
	 * This method is not supported directly.<br>
	 * It throws an {@code UnsupportedOperationException} because the warehouse must be accessed via a proxy.
	 * @param itemId The unique identifier for the item to decrease.
	 * @param count The amount of the item to remove.
	 * @return Always returns {@code false}.
	 */
	@Override
	public boolean decreaseByItemId(int itemId, long count)
	{
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
		throw new UnsupportedOperationException("LWH should be used behind proxy");
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
	
	/**
	 * Updates the current user ID for the warehouse.<br>
	 * This method sets the {@code curentWhUser} field to a new value.
	 * @param curentWhUser The new user ID to assign.
	 */
	public void setWhUser(int curentWhUser)
	{
		this.curentWhUser = curentWhUser;
	}
	
	/**
	 * Retrieves the current user ID of the warehouse.<br>
	 * This value is stored in the {@code curentWhUser} field.
	 * @return The unique identifier for the current warehouse user.
	 */
	public int getWhUser()
	{
		return curentWhUser;
	}
}
