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
package com.aionemu.gameserver.model.event_window;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerEventsWindowDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Manages a collection of event windows associated with {@link Player} objects.<br>
 * This class provides methods to track and retrieve active events for players in the game.
 * @author Ghostfur (Aion-Unique)
 */
public class PlayerEventWindowList implements EventWindowList<Player>
{
	private final Map<Integer, PlayerEventWindowEntry> entry = new HashMap<>(0);
	
	/**
	 * Creates a new {@code PlayerEventWindowList} from a provided list.<br>
	 * This constructor populates the internal map using entries from the input collection.
	 * @param list The {@code List} of {@link PlayerEventWindowEntry} objects to initialize the window list.
	 */
	public PlayerEventWindowList(List<PlayerEventWindowEntry> list)
	{
		for (PlayerEventWindowEntry playerEventWindowEntry : list)
		{
			entry.put(playerEventWindowEntry.getId(), playerEventWindowEntry);
		}
	}
	
	/**
	 * Retrieves all entries from the current list.<br>
	 * This method converts the internal map values into an array.
	 * @return An array containing all {@link PlayerEventWindowEntry} objects.
	 */
	public PlayerEventWindowEntry[] getAll()
	{
		final ArrayList<PlayerEventWindowEntry> arrayList = new ArrayList<>(entry.values());
		return arrayList.toArray(new PlayerEventWindowEntry[arrayList.size()]);
	}
	
	/**
	 * Retrieves all basic entries from the event window list.<br>
	 * This method converts the internal map values into an array.
	 * @return An array of {@link PlayerEventWindowEntry} objects.
	 */
	public PlayerEventWindowEntry[] getBasic()
	{
		return entry.values().toArray(new PlayerEventWindowEntry[entry.size()]);
	}
	
	/**
	 * Adds a new event window entry for a specific player.<br>
	 * This method updates the internal map and saves the data to the database.
	 * @param player The {@link Player} object associated with the event.
	 * @param remaining The amount of time remaining for the event.
	 * @param timestamp The {@code Timestamp} when the event occurred.
	 * @param Time The specific time value for the event.
	 * @param persistentState The {@link PersistentState} of the player.
	 * @return {@code true} if the entry was added successfully.
	 */
	private synchronized boolean add(Player player, int remaining, Timestamp timestamp, int Time, PersistentState persistentState)
	{
		entry.put(remaining, new PlayerEventWindowEntry(remaining, timestamp, Time, persistentState));
		DAOManager.getDAO(PlayerEventsWindowDAO.class).store(player.getPlayerAccount().getId(), remaining, timestamp, Time);
		return true;
	}
	
	/**
	 * Adds a new event window entry for a specific {@link Player}.<br>
	 * This method creates an entry with the default {@code PersistentState}.
	 * @param player The {@code Player} object associated with the event.
	 * @param remaining The number of seconds remaining for the event.
	 * @param timestamp The exact time the event occurred.
	 * @param Time The specific time value for the event window.
	 * @return {@code true} if the entry was added successfully, otherwise {@code false}.
	 */
	@Override
	public boolean add(Player player, int remaining, Timestamp timestamp, int Time)
	{
		return add(player, remaining, timestamp, Time, PersistentState.NEW);
	}
	
	/**
	 * Removes a specific event window for a {@link Player}.<br>
	 * This method updates the internal list and deletes the record from the database.
	 * @param player The {@code Player} object associated with the event.
	 * @param remaining The amount of time remaining used as the unique identifier.
	 * @return {@code true} if an entry was found and removed, otherwise {@code false}.
	 */
	@Override
	public synchronized boolean remove(Player player, int remaining)
	{
		final PlayerEventWindowEntry playerEventWindowEntry = entry.get(remaining);
		if (playerEventWindowEntry != null)
		{
			playerEventWindowEntry.setPersistentState(PersistentState.DELETED);
			entry.remove(remaining);
			DAOManager.getDAO(PlayerEventsWindowDAO.class).delete(player.getPlayerAccount().getId(), remaining);
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
