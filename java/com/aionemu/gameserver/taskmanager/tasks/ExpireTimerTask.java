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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.taskmanager.AbstractPeriodicTaskManager;

/**
 * This task periodically checks for objects that have expired based on their timer.<br>
 * It iterates through all entities implementing {@link IExpirable} to handle expiration logic.
 * @author Mr. Poke
 */
public class ExpireTimerTask extends AbstractPeriodicTaskManager
{
	private final Map<IExpirable, Player> expirables = new HashMap<>();
	
	/**
	 * Creates a new instance of the {@code ExpireTimerTask}.<br>
	 * This task manages items that need to expire over time.<br>
	 * It initializes with a default period of {@code 1000} milliseconds.
	 */
	public ExpireTimerTask()
	{
		super(1000);
	}
	
	/**
	 * Provides the global instance of the {@link ExpireTimerTask}.<br>
	 * This method follows the singleton pattern.
	 * @return The single shared instance of {@code ExpireTimerTask}.
	 */
	public static ExpireTimerTask getInstance()
	{
		return SingletonHolder._instance;
	}
	
	/**
	 * Adds a new item to the expiration tracking system.<br>
	 * This method links an {@code IExpirable} object to a specific {@link Player}.<br>
	 * It ensures that the task is registered correctly for processing.
	 * @param expirable The item or effect that will expire.
	 * @param player The player who owns this expiration.
	 */
	public void addTask(IExpirable expirable, Player player)
	{
		writeLock();
		try
		{
			expirables.put(expirable, player);
		}
		finally
		{
			writeUnlock();
		}
	}
	
	/**
	 * Removes all expiration tasks associated with a specific player.<br>
	 * This method updates the internal {@code expirables} map.<br>
	 * It ensures that the {@code player} is no longer tracked by this task.
	 * @param player The {@link Player} object to remove from the task manager.
	 */
	public void removePlayer(Player player)
	{
		writeLock();
		try
		{
			for (Iterator<Map.Entry<IExpirable, Player>> i = expirables.entrySet().iterator(); i.hasNext();)
			{
				final Map.Entry<IExpirable, Player> entry = i.next();
				if (entry.getValue() == player)
				{
					i.remove();
				}
			}
		}
		finally
		{
			writeUnlock();
		}
	}
	
	@Override
	public void run()
	{
		writeLock();
		try
		{
			final int timeNow = (int) (System.currentTimeMillis() / 1000);
			for (Iterator<Map.Entry<IExpirable, Player>> i = expirables.entrySet().iterator(); i.hasNext();)
			{
				final Map.Entry<IExpirable, Player> entry = i.next();
				final IExpirable expirable = entry.getKey();
				final Player player = entry.getValue();
				final int min = (expirable.getExpireTime() - timeNow);
				if ((min < 0) && expirable.canExpireNow())
				{
					expirable.expireEnd(player);
					i.remove();
					continue;
				}
				
				switch (min)
				{
					case 1800:
					case 900:
					case 600:
					case 300:
					case 60:
						expirable.expireMessage(player, min / 60);
						break;
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
		protected static final ExpireTimerTask _instance = new ExpireTimerTask();
	}
}
