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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.taskmanager.AbstractPeriodicTaskManager;

/**
 * This class handles periodic tasks related to player movement.<br>
 * It ensures that {@link Creature} positions are updated or validated regularly.<br>
 * It extends {@link AbstractPeriodicTaskManager} to run these checks at a fixed interval.
 * @author ATracer
 */
public class PlayerMoveTaskManager extends AbstractPeriodicTaskManager
{
	private final Map<Integer, Creature> movingPlayers = new ConcurrentHashMap<>();
	
	/**
	 * Private constructor for the {@link PlayerMoveTaskManager} class.<br>
	 * This prevents other classes from creating new instances directly.<br>
	 * Use {@code getInstance} to get the singleton instance.
	 */
	private PlayerMoveTaskManager()
	{
		super(200);
	}
	
	/**
	 * Adds a new {@link Creature} to the list of moving players.<br>
	 * This method registers the player so the task manager can track their movement.
	 * @param player The {@code Creature} object to be added.
	 */
	public void addPlayer(Creature player)
	{
		movingPlayers.put(player.getObjectId(), player);
	}
	
	/**
	 * Removes a specific {@link Creature} from the active tracking list.<br>
	 * This method updates the internal map by using the player's unique ID.<br>
	 * Use this when a player is no longer moving or has disconnected.
	 * @param player The {@code Creature} object to be removed.
	 */
	public void removePlayer(Creature player)
	{
		movingPlayers.remove(player.getObjectId());
	}
	
	@Override
	public void run()
	{
		for (Creature player : movingPlayers.values())
		{
			player.getMoveController().moveToDestination();
		}
	}
	
	/**
	 * Gets the single instance of the {@link PlayerMoveTaskManager}.<br>
	 * This method follows the singleton pattern.
	 * @return The global {@code PlayerMoveTaskManager} instance.
	 */
	public static PlayerMoveTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		private static final PlayerMoveTaskManager INSTANCE = new PlayerMoveTaskManager();
	}
}
