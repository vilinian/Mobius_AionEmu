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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aionemu.gameserver.model.gameobjects.Item;

/**
 * This class manages the storage and retrieval of {@link Item} objects.<br>
 * It provides functionality to handle item collections within the game world.
 * @author KID
 */
public class ItemStorage
{
	public static final long FIRST_AVAILABLE_SLOT = 65535L;
	private final Map<Integer, Item> items;
	private int limit;
	private final int specialLimit;
	private final StorageType storageType;
	
	/**
	 * Creates a new instance of {@link ItemStorage}.<br>
	 * This constructor initializes the storage limits based on the provided type.<br>
	 * It also sets up the internal item map.
	 * @param storageType The {@code StorageType} used to configure this storage.
	 */
	public ItemStorage(StorageType storageType)
	{
		limit = storageType.getLimit();
		specialLimit = storageType.getSpecialLimit();
		this.storageType = storageType;
		items = new HashMap<>();
	}
	
	/**
	 * Retrieves all items currently stored in this storage.<br>
	 * It returns a new {@code List} containing the values from the internal map.
	 * @return A {@code List} of {@link Item} objects.
	 */
	public List<Item> getItems()
	{
		final List<Item> temp = new ArrayList<>();
		temp.addAll(items.values());
		return temp;
	}
	
	/**
	 * Retrieves the maximum number of items allowed in this storage.<br>
	 * This value is used to determine if the storage is full.
	 * @return The current {@code int} limit.
	 */
	public int getLimit()
	{
		return limit;
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
		if (getCubeItems().size() > limit)
		{
			return false;
		}
		
		this.limit = limit;
		return true;
	}
	
	/**
	 * Returns the total number of slots in this storage.<br>
	 * This value is determined by the {@link StorageType}.
	 * @return The length of the row as an {@code int}.
	 */
	public int getRowLength()
	{
		return storageType.getLength();
	}
	
	/**
	 * Finds the first {@link Item} that matches a specific ID.<br>
	 * It searches through all items currently in storage.<br>
	 * If no match is found, it returns {@code null}.
	 * @param itemId The unique template ID to search for.
	 * @return The first matching {@code Item} or {@code null}.
	 */
	public Item getFirstItemById(int itemId)
	{
		for (Item item : items.values())
		{
			if (item.getItemTemplate().getTemplateId() == itemId)
			{
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves all items that match a specific ID.<br>
	 * This method searches through the storage for matching {@link Item} objects.<br>
	 * It returns a list of results based on the provided {@code itemId}.
	 * @param itemId The unique identifier used to filter the items.
	 * @return A {@code List} containing all items that match the given ID.
	 */
	public List<Item> getItemsById(int itemId)
	{
		final List<Item> temp = new ArrayList<>();
		for (Item item : items.values())
		{
			if (item.getItemTemplate().getTemplateId() == itemId)
			{
				temp.add(item);
			}
		}
		
		return temp;
	}
	
	/**
	 * Retrieves an {@link Item} based on its unique object ID.<br>
	 * This method looks up the item in the internal storage map.
	 * @param itemObjId The unique identifier of the item to find.
	 * @return The {@code Item} associated with the provided ID, or {@code null} if not found.
	 */
	public Item getItemByObjId(int itemObjId)
	{
		return items.get(itemObjId);
	}
	
	/**
	 * Finds the slot ID for a specific item type.<br>
	 * It searches through all items in the storage.<br>
	 * If no match is found, it returns {@code -1}.
	 * @param itemId The unique template ID of the item to find.
	 * @return The equipment slot ID of the first matching item, or {@code -1} if not found.
	 */
	public long getSlotIdByItemId(int itemId)
	{
		for (Item item : items.values())
		{
			if (item.getItemTemplate().getTemplateId() == itemId)
			{
				return item.getEquipmentSlot();
			}
		}
		
		return -1;
	}
	
	/**
	 * Retrieves an {@link Item} based on its specific slot ID.<br>
	 * This method searches through the cube items to find a match.<br>
	 * It returns {@code null} if no item is found in that slot.
	 * @param slotId The unique identifier for the equipment slot.
	 * @return The {@link Item} at the specified slot, or {@code null}.
	 */
	public Item getItemBySlotId(short slotId)
	{
		for (Item item : getCubeItems())
		{
			if (item.getEquipmentSlot() == slotId)
			{
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a special item based on its equipment slot.<br>
	 * This method searches through all special cube items.<br>
	 * It returns the first item that matches the provided {@code slotId}.
	 * @param slotId The specific equipment slot to search for.
	 * @return The matching {@link Item} object, or {@code null} if no match is found.
	 */
	public Item getSpecialItemBySlotId(short slotId)
	{
		for (Item item : getSpecialCubeItems())
		{
			if (item.getEquipmentSlot() == slotId)
			{
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Finds the slot ID for a specific object.<br>
	 * This method uses {@code getItemByObjId} to locate the item.<br>
	 * It returns the equipment slot if the item exists.
	 * @param objId The unique identifier of the object.
	 * @return The slot ID as a {@code long}, or -1 if no item is found.
	 */
	public long getSlotIdByObjId(int objId)
	{
		final Item item = getItemByObjId(objId);
		if (item != null)
		{
			return item.getEquipmentSlot();
		}
		
		return -1;
	}
	
	/**
	 * Finds the next empty slot in the storage.<br>
	 * This method returns the starting point for available slots.<br>
	 * It uses the constant {@code FIRST_AVAILABLE_SLOT}.
	 * @return The ID of the first available slot.
	 */
	public long getNextAvailableSlot()
	{
		return FIRST_AVAILABLE_SLOT;
	}
	
	/**
	 * Adds a new {@link Item} to the storage.<br>
	 * This method checks if the item already exists before adding it.<br>
	 * It returns {@code true} if the item was added successfully.<br>
	 * It returns {@code false} if the item is already in the storage.
	 * @param item The {@link Item} to be stored.
	 * @return {@code true} if successful, otherwise {@code false}.
	 */
	public boolean putItem(Item item)
	{
		if (items.containsKey(item.getObjectId()))
		{
			return false;
		}
		
		items.put(item.getObjectId(), item);
		return true;
	}
	
	/**
	 * Removes an item from the storage based on its unique ID.<br>
	 * This method updates the internal {@code items} map.
	 * @param objId The unique identifier of the item to remove.
	 * @return The {@code Item} that was removed, or {@code null} if it did not exist.
	 */
	public Item removeItem(int objId)
	{
		return items.remove(objId);
	}
	
	/**
	 * Checks if the storage has reached its maximum capacity.<br>
	 * It compares the number of cube items against the {@code limit}.
	 * @return {@code true} if the storage is full, {@code false} otherwise.
	 */
	public boolean isFull()
	{
		return getCubeItems().size() >= limit;
	}
	
	/**
	 * Checks if the storage is full of special cube items.<br>
	 * This method compares the count of special cubes to the {@code specialLimit}.
	 * @return {@code true} if the number of special cubes reaches or exceeds the limit, {@code false} otherwise.
	 */
	public boolean isFullSpecialCube()
	{
		return getSpecialCubeItems().size() >= specialLimit;
	}
	
	/**
	 * Retrieves all items that belong to special cubes.<br>
	 * This method filters the current storage for items with a positive {@code extraInventoryId}.
	 * @return A {@code List} of {@link Item} objects matching the criteria.
	 */
	public List<Item> getSpecialCubeItems()
	{
		return items.values().stream().filter(item -> item.getItemTemplate().getExtraInventoryId() > 0).collect(Collectors.toList());
	}
	
	/**
	 * Retrieves all items from the cube.<br>
	 * This method filters the {@code items} map for specific types.
	 * @return A {@code List} of {@link Item} objects found in the cube.
	 */
	public List<Item> getCubeItems()
	{
		return items.values().stream().filter(item -> item.getItemTemplate().getExtraInventoryId() < 1).collect(Collectors.toList());
	}
	
	/**
	 * Calculates the number of available slots in the storage.<br>
	 * This value is based on the total {@code limit} minus the current count of cube items.
	 * @return The number of free slots remaining.
	 */
	public int getFreeSlots()
	{
		return limit - getCubeItems().size();
	}
	
	/**
	 * Calculates the number of empty slots available for special cube items.<br>
	 * It subtracts the current count of {@link Item} objects from {@code getSpecialCubeItems} from the {@code specialLimit}.
	 * @return The number of remaining free slots for special cubes.
	 */
	public int getSpecialCubeFreeSlots()
	{
		return specialLimit - getSpecialCubeItems().size();
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return items.size();
	}
}
