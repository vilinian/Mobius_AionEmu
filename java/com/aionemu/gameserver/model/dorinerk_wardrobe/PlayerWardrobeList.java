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
package com.aionemu.gameserver.model.dorinerk_wardrobe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerWardrobeDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a collection of wardrobe items associated with a {@link Player}.<br>
 * This class manages the list of persistent wardrobe data for individual players.<br>
 * It provides methods to retrieve and handle wardrobe entries from the database.
 */
public final class PlayerWardrobeList implements WardrobeList<Player>
{
	private final Map<Integer, PlayerWardrobeEntry> entry;
	
	/**
	 * Creates a new empty instance of {@code PlayerWardrobeList}.<br>
	 * This constructor initializes the internal storage for wardrobe entries.
	 */
	public PlayerWardrobeList()
	{
		entry = new HashMap<>(0);
	}
	
	/**
	 * Creates a new {@link PlayerWardrobeList} from a list of entries.<br>
	 * This constructor populates the internal map using the provided items.
	 * @param entries The list of {@code PlayerWardrobeEntry} objects to add.
	 */
	public PlayerWardrobeList(List<PlayerWardrobeEntry> entries)
	{
		this();
		for (PlayerWardrobeEntry e : entries)
		{
			entry.put(e.getItemId(), e);
		}
	}
	
	/**
	 * Retrieves every item currently stored in the player's wardrobe.<br>
	 * This method converts the internal map values into an array.
	 * @return An array of {@link PlayerWardrobeEntry} objects containing all items.
	 */
	public PlayerWardrobeEntry[] getAllWardrobe()
	{
		final List<PlayerWardrobeEntry> allWardrobe = new ArrayList<>();
		allWardrobe.addAll(entry.values());
		return allWardrobe.toArray(new PlayerWardrobeEntry[allWardrobe.size()]);
	}
	
	/**
	 * Retrieves the basic wardrobe entries for a player.<br>
	 * This method returns all items currently stored in the {@code entry} map.
	 * @return An array of {@link PlayerWardrobeEntry} objects.
	 */
	public PlayerWardrobeEntry[] getBasicWardrobe()
	{
		return entry.values().toArray(new PlayerWardrobeEntry[entry.size()]);
	}
	
	/**
	 * Adds a new item to the wardrobe of a specific {@link Player}.<br>
	 * This method checks if the action is successful.
	 * @param player The {@code Player} who owns the wardrobe.
	 * @param itemId The unique identifier for the item.
	 * @param slot The index where the item should be placed.
	 * @param reskin_count The number of reskins applied to the item.
	 * @return {@code true} if the item was added successfully, otherwise {@code false}.
	 */
	@Override
	public boolean addItem(Player player, int itemId, int slot, int reskin_count)
	{
		return addItem(player, itemId, slot, reskin_count, PersistentState.NEW);
	}
	
	/**
	 * Adds a new item to the player's wardrobe.<br>
	 * This method updates both the internal map and the database.<br>
	 * It ensures thread safety using {@code synchronized}.
	 * @param player The {@link Player} who owns the wardrobe.
	 * @param itemId The unique identifier for the item.
	 * @param slot The specific inventory slot for the item.
	 * @param reskin_count The number of available reskins.
	 * @param state The {@code PersistentState} associated with the item.
	 * @return {@code true} if the item was added successfully.
	 */
	private synchronized boolean addItem(Player player, int itemId, int slot, int reskin_count, PersistentState state)
	{
		entry.put(itemId, new PlayerWardrobeEntry(itemId, slot, reskin_count, state));
		DAOManager.getDAO(PlayerWardrobeDAO.class).store(player.getObjectId(), itemId, slot, reskin_count);
		return true;
	}
	
	/**
	 * Removes an item from the wardrobe of a specific {@link Player}.<br>
	 * This method updates the internal list and deletes the record from the database.
	 * @param player The {@code Player} who owns the wardrobe.
	 * @param itemId The unique identifier of the item to remove.
	 * @return {@code true} if the item was successfully found and removed, otherwise {@code false}.
	 */
	@Override
	public synchronized boolean removeItem(Player player, int itemId)
	{
		final PlayerWardrobeEntry entries = entry.get(itemId);
		if (entries != null)
		{
			entries.setPersistentState(PersistentState.DELETED);
			entry.remove(itemId);
			DAOManager.getDAO(PlayerWardrobeDAO.class).delete(player.getObjectId(), itemId);
		}
		
		return entry != null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	@Override
	public int size()
	{
		return entry.size();
	}
	
}
