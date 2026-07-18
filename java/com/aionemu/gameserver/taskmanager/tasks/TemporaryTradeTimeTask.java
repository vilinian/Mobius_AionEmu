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
package com.aionemu.gameserver.taskmanager.tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.taskmanager.AbstractPeriodicTaskManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * Manages the expiration of temporary trade windows between players.<br>
 * It periodically checks for active trades and handles the cleanup process when time runs out.
 * @author Mr. Poke
 */
public class TemporaryTradeTimeTask extends AbstractPeriodicTaskManager
{
	private final Map<Item, Collection<Integer>> items = new HashMap<>();
	private final Map<Integer, Item> itemById = new HashMap<>();
	
	/**
	 * Initializes a new instance of the {@code TemporaryTradeTimeTask}.<br>
	 * This task manages temporary trade timers for items.<br>
	 * It sets the default execution interval to {@code 1000} milliseconds.
	 */
	public TemporaryTradeTimeTask()
	{
		super(1000);
	}
	
	/**
	 * Provides the single global instance of {@link TemporaryTradeTimeTask}.<br>
	 * Use this method to access the task manager from anywhere in the code.<br>
	 * This follows the singleton design pattern.
	 * @return The active {@code TemporaryTradeTimeTask} instance.
	 */
	public static TemporaryTradeTimeTask getInstance()
	{
		return SingletonHolder._instance;
	}
	
	/**
	 * Adds a new item to the trade tracking system.<br>
	 * This method maps an {@code Item} to a collection of player IDs.<br>
	 * It ensures thread safety by using internal locks.
	 * @param item The {@code Item} object to be added.
	 * @param players A {@code Collection} of integers representing the player IDs involved.
	 */
	public void addTask(Item item, Collection<Integer> players)
	{
		writeLock();
		try
		{
			items.put(item, players);
			itemById.put(item.getObjectId(), item);
		}
		finally
		{
			writeUnlock();
		}
	}
	
	/**
	 * Checks if a specific player is allowed to trade an item.<br>
	 * This method verifies if the {@code playerObjectId} is in the list of authorized players for the given {@code Item}.
	 * @param item The {@code Item} being checked.
	 * @param playerObjectId The unique ID of the player.
	 * @return {@code true} if the player can trade the item, otherwise {@code false}.
	 */
	public boolean canTrade(Item item, int playerObjectId)
	{
		final Collection<Integer> players = items.get(item);
		if (players == null)
		{
			return false;
		}
		
		return players.contains(playerObjectId);
	}
	
	/**
	 * Checks if a specific {@link Item} is currently in the trade list.<br>
	 * This method uses a read lock to ensure thread safety during the check.
	 * @param item The {@code Item} object to search for.
	 * @return {@code true} if the item exists in the collection, otherwise {@code false}.
	 */
	public boolean hasItem(Item item)
	{
		readLock();
		try
		{
			return items.containsKey(item);
		}
		finally
		{
			readUnlock();
		}
	}
	
	/**
	 * Retrieves an {@link Item} based on its unique ID.<br>
	 * This method uses a thread-safe read lock to access the internal map.
	 * @param objectId The unique identifier of the item to find.
	 * @return The {@code Item} associated with the provided ID, or {@code null} if not found.
	 */
	public Item getItem(int objectId)
	{
		readLock();
		try
		{
			return itemById.get(objectId);
		}
		finally
		{
			readUnlock();
		}
	}
	
	@Override
	public void run()
	{
		writeLock();
		try
		{
			for (Iterator<Map.Entry<Item, Collection<Integer>>> it = items.entrySet().iterator(); it.hasNext();)
			{
				final Map.Entry<Item, Collection<Integer>> entry = it.next();
				final Item item = entry.getKey();
				final int time = (item.getTemporaryExchangeTime() - (int) (System.currentTimeMillis() / 1000));
				if (time == 60)
				{
					for (int playerId : entry.getValue())
					{
						final Player player = World.getInstance().findPlayer(playerId);
						if (player != null)
						{
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_END_OF_EXCHANGE_TIME(item.getNameId(), time));
						}
					}
				}
				else if (time <= 0)
				{
					for (int playerId : entry.getValue())
					{
						final Player player = World.getInstance().findPlayer(playerId);
						if (player != null)
						{
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_EXCHANGE_TIME_OVER(item.getNameId()));
						}
					}
					
					item.setTemporaryExchangeTime(0);
					it.remove();
					itemById.remove(item.getObjectId());
				}
			}
		}
		finally
		{
			writeUnlock();
		}
	}
	
	private static class SingletonHolder
	{
		protected static final TemporaryTradeTimeTask _instance = new TemporaryTradeTimeTask();
	}
}
