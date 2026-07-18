/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Aion-Lightning is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.cubics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerCubicsDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class manages a list of {@link Player} objects associated with specific Magic Crystals.<br>
 * It provides functionality to handle and retrieve player data within the cubics system.
 * @author Phantom_KNA
 */
public final class PlayerMCList implements MCList<Player>
{
	private final Map<Integer, PlayerMCEntry> entry = new HashMap<>(0);
	
	/**
	 * Creates a new instance of the {@code PlayerMCList} class.<br>
	 * This initializes an empty list to store {@link Player} entries.
	 */
	public PlayerMCList()
	{
	}
	
	/**
	 * Creates a new {@link PlayerMCList} from a provided list of entries.<br>
	 * This constructor populates the internal map using each {@code PlayerMCEntry}.
	 * @param list The {@code List} of {@code PlayerMCEntry} objects to initialize the collection with.
	 */
	public PlayerMCList(List<PlayerMCEntry> list)
	{
		this();
		for (PlayerMCEntry playerMCEntry : list)
		{
			entry.put(playerMCEntry.getCubeId(), playerMCEntry);
		}
	}
	
	/**
	 * Retrieves all entries from the current MC list.<br>
	 * This method returns an array of {@link PlayerMCEntry} objects.
	 * @return An array containing all {@code PlayerMCEntry} elements.
	 */
	public PlayerMCEntry[] getAllMC()
	{
		final ArrayList<PlayerMCEntry> list = new ArrayList<>();
		list.clear();
		list.addAll(entry.values());
		return (PlayerMCEntry[]) list.toArray(new PlayerMCEntry[list.size()]);
	}
	
	/**
	 * Retrieves all basic MC entries from the list.<br>
	 * This method returns an array of {@link PlayerMCEntry} objects.
	 * @return An array containing all {@code PlayerMCEntry} values.
	 */
	public PlayerMCEntry[] getBasicMC()
	{
		return entry.values().toArray(new PlayerMCEntry[entry.size()]);
	}
	
	/**
	 * Adds a new cubic item to the player's list.<br>
	 * This method creates a new entry with default persistent state.
	 * @param crature The {@link Player} who owns the cubic.
	 * @param cubeid The unique identifier for the cubic.
	 * @param rank The rank of the cubic.
	 * @param level The level of the cubic.
	 * @param stat_value The value of the statistic provided by the cubic.
	 * @param category The category classification of the cubic.
	 * @return {@code true} if the addition was successful, {@code false} otherwise.
	 */
	@Override
	public boolean add(Player crature, int cubeid, int rank, int level, int stat_value, int category)
	{
		return add(crature, cubeid, rank, level, stat_value, category, PersistentState.NEW);
	}
	
	/**
	 * Adds a new entry to the player's cubic list.<br>
	 * This method updates both the internal map and the database.<br>
	 * It is synchronized to ensure thread safety during the operation.
	 * @param player The {@link Player} who owns the cubic.
	 * @param cubeid The unique identifier for the cubic.
	 * @param rank The rank of the cubic.
	 * @param level The level of the cubic.
	 * @param stat_value The specific statistic value assigned to the cubic.
	 * @param category The category classification of the cubic.
	 * @param paramPersistentState The {@link PersistentState} associated with this entry.
	 * @return {@code true} if the operation was successful.
	 */
	private synchronized boolean add(Player player, int cubeid, int rank, int level, int stat_value, int category, PersistentState paramPersistentState)
	{
		entry.put(cubeid, new PlayerMCEntry(cubeid, rank, level, stat_value, category, paramPersistentState));
		DAOManager.getDAO(PlayerCubicsDAO.class).store(player.getObjectId(), cubeid, rank, level, stat_value, category);
		return true;
	}
	
	/**
	 * Removes a specific item from the player's list.<br>
	 * This method updates the internal map and deletes the record from the database.
	 * @param player The {@link Player} who owns the item.
	 * @param cubeid The unique identifier of the item to remove.
	 * @return {@code true} if the item was successfully removed, otherwise {@code false}.
	 */
	@Override
	public synchronized boolean remove(Player player, int cubeid)
	{
		final PlayerMCEntry playerMCEntry = entry.get(cubeid);
		if (playerMCEntry != null)
		{
			playerMCEntry.setPersistentState(PersistentState.DELETED);
			entry.remove(cubeid);
			DAOManager.getDAO(PlayerCubicsDAO.class).delete(player.getObjectId(), cubeid);
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
