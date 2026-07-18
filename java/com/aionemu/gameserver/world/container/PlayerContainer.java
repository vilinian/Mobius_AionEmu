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
package com.aionemu.gameserver.world.container;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This class serves as a container for managing {@link Player} objects.<br>
 * It allows for efficient storage and retrieval of players using their {@code objectId} and name.
 * @author -Nemesiss-
 */
public class PlayerContainer implements Iterable<Player>
{
	private static final Logger log = LoggerFactory.getLogger(PlayerContainer.class);
	/**
	 * Map<ObjectId,Player>
	 */
	private final Map<Integer, Player> playersById = new ConcurrentHashMap<>();
	/**
	 * Map<Name,Player>
	 */
	private final Map<String, Player> playersByName = new ConcurrentHashMap<>();
	
	/**
	 * Adds a {@link Player} to the container.<br>
	 * This method stores the player by their ID and name.<br>
	 * It throws an exception if the player already exists.
	 * @param player The {@code Player} object to add.
	 */
	public void add(Player player)
	{
		if ((playersById.put(player.getObjectId(), player) != null) || (playersByName.put(player.getName(), player) != null))
		{
			throw new DuplicateAionObjectException();
		}
	}
	
	/**
	 * Removes a {@link Player} from the container.<br>
	 * This method deletes the player using both their object ID and name.
	 * @param player The {@code Player} object to be removed.
	 */
	public void remove(Player player)
	{
		playersById.remove(player.getObjectId());
		playersByName.remove(player.getName());
	}
	
	/**
	 * Retrieves a {@link Player} based on their unique ID.<br>
	 * This method looks up the player in the internal storage.
	 * @param objectId The unique identifier of the player to find.
	 * @return The {@code Player} object if found, or {@code null} otherwise.
	 */
	public Player get(int objectId)
	{
		return playersById.get(objectId);
	}
	
	/**
	 * Retrieves a {@link Player} object based on their name.<br>
	 * This method searches the internal map for the provided string.
	 * @param name The unique name of the player to find.
	 * @return The {@code Player} object if found, or {@code null} if no match exists.
	 */
	public Player get(String name)
	{
		return playersByName.get(name);
	}
	
	/**
	 * Returns an {@link Iterator} to loop through all players.<br>
	 * This allows you to visit every {@link Player} in the container.
	 * @return An {@code Iterator} of {@link Player} objects.
	 */
	@Override
	public Iterator<Player> iterator()
	{
		return playersById.values().iterator();
	}
	
	/**
	 * Iterates through all {@link Player} objects in the current location.<br>
	 * Applies the provided {@code Visitor} to each non-null player found.<br>
	 * Logs an error if any exception occurs during the process.
	 * @param visitor The {@code Visitor} to apply to every player.
	 */
	public void doOnAllPlayers(Visitor<Player> visitor)
	{
		try
		{
			for (Player player : playersById.values())
			{
				if (player != null)
				{
					if (player.getRace().isPlayerRace())
					{
						visitor.visit(player);
					}
					else
					{
						continue;
					}
				}
			}
		}
		catch (Exception ex)
		{
			log.error("Exception when running visitor on all players" + ex);
		}
	}
	
	/**
	 * Retrieves all players currently stored in the container.<br>
	 * This method returns the values from the internal {@code playersById} map.
	 * @return A {@code Collection} of all {@link Player} objects.
	 */
	public Collection<Player> getAllPlayers()
	{
		return playersById.values();
	}
}
