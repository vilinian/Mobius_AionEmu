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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a collection of users that a specific player has blocked.<br>
 * This class manages blocks associated with a player's {@code CommonData}.
 * @author Ben
 */
public class BlockList implements Iterable<BlockedPlayer>
{
	/**
	 * The maximum number of users a block list can contain
	 */
	public static final int MAX_BLOCKS = 10;
	
	// Indexes blocked players by their player ID
	private final Map<Integer, BlockedPlayer> blockedList;
	
	/**
	 * Creates a new instance of {@link BlockList}.<br>
	 * This initializes an empty list for blocked players.<br>
	 * It uses a {@code ConcurrentHashMap} to store the data.
	 */
	public BlockList()
	{
		this(new ConcurrentHashMap<>());
	}
	
	/**
	 * Creates a new {@link BlockList} using an existing map of blocked players.<br>
	 * This constructor copies the provided data into a thread-safe internal map.
	 * @param initialList A {@code Map} containing player IDs and their corresponding {@code BlockedPlayer} objects.
	 */
	public BlockList(Map<Integer, BlockedPlayer> initialList)
	{
		blockedList = new ConcurrentHashMap<>(initialList);
	}
	
	/**
	 * Adds a new player to the block list.<br>
	 * This method stores the {@code BlockedPlayer} using their unique ID.
	 * @param plr The {@code BlockedPlayer} object to be added.
	 */
	public void add(BlockedPlayer plr)
	{
		blockedList.put(plr.getObjId(), plr);
	}
	
	/**
	 * Removes a specific player from the block list.<br>
	 * This method uses the unique ID provided to find and delete the entry.
	 * @param objIdOfPlayer The {@code int} ID of the player to remove.
	 */
	public void remove(int objIdOfPlayer)
	{
		blockedList.remove(objIdOfPlayer);
	}
	
	/**
	 * Finds a {@link BlockedPlayer} by their name.<br>
	 * This method searches the list for a case-insensitive match.
	 * @param name The name of the player to look for.
	 * @return The matching {@code BlockedPlayer} object, or {@code null} if no match is found.
	 */
	public BlockedPlayer getBlockedPlayer(String name)
	{
		final Iterator<BlockedPlayer> iterator = blockedList.values().iterator();
		
		while (iterator.hasNext())
		{
			final BlockedPlayer entry = iterator.next();
			if (entry.getName().equalsIgnoreCase(name))
			{
				return entry;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a {@link BlockedPlayer} based on their unique ID.<br>
	 * This method looks up the player in the internal block list.<br>
	 * It returns {@code null} if no such player is found.
	 * @param playerObjId The unique identifier of the player to find.
	 * @return The {@code BlockedPlayer} object, or {@code null} if not found.
	 */
	public BlockedPlayer getBlockedPlayer(int playerObjId)
	{
		return blockedList.get(playerObjId);
	}
	
	/**
	 * Checks if a specific player is in the block list.<br>
	 * This method uses the {@code playerObjectId} to perform the check.
	 * @param playerObjectId The unique ID of the player to check.
	 * @return {@code true} if the player is blocked, otherwise {@code false}.
	 */
	public boolean contains(int playerObjectId)
	{
		return blockedList.containsKey(playerObjectId);
	}
	
	/**
	 * Returns the number of players in the block list.<br>
	 * This count represents how many unique entries exist in the {@code blockedList}.
	 * @return The total size of the block list as an {@code int}.
	 */
	public int getSize()
	{
		return blockedList.size();
	}
	
	/**
	 * Checks if the block list has reached its maximum capacity.<br>
	 * It compares the current size against {@code MAX_BLOCKS}.
	 * @return {@code true} if the list is full, {@code false} otherwise.
	 */
	public boolean isFull()
	{
		return getSize() >= MAX_BLOCKS;
	}
	
	/**
	 * Returns an {@link Iterator} for all players in the block list.<br>
	 * This allows you to loop through every {@code BlockedPlayer}.
	 * @return An {@code Iterator} containing all blocked players.
	 */
	@Override
	public Iterator<BlockedPlayer> iterator()
	{
		return blockedList.values().iterator();
	}
}
