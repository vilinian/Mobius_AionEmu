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
package com.aionemu.gameserver.world.zone;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * This service manages the level requirements for different world zones.<br>
 * It handles logic related to checking if a {@link Player} meets the criteria to enter a specific area.
 * @author ATracer
 */
public class ZoneLevelService
{
	private static final long DROWN_PERIOD = 2000;
	
	/**
	 * Checks the current height of a {@link Player} against world limits.<br>
	 * This method determines if the player should die or start drowning.<br>
	 * It ignores checks if the {@code Player} is already dead.
	 * @param player The {@code Player} object to check.
	 */
	public static void checkZoneLevels(Player player)
	{
		final World world = World.getInstance();
		final float z = player.getZ();
		
		if (player.getLifeStats().isAlreadyDead())
		{
			return;
		}
		
		if (z < world.getWorldMap(player.getWorldId()).getDeathLevel())
		{
			player.getController().die();
			return;
		}
		
		// TODO need fix character height
		final float playerheight = player.getPlayerAppearance().getHeight() * 1.6f;
		if (z < (world.getWorldMap(player.getWorldId()).getWaterLevel() - playerheight))
		{
			startDrowning(player);
		}
		else
		{
			stopDrowning(player);
		}
	}
	
	/**
	 * Initiates the drowning process for a specific player.<br>
	 * This method checks if the {@code Player} is already drowning.<br>
	 * If not, it schedules a new drowning task.
	 * @param player The {@link Player} object to start the drowning effect on.
	 */
	private static void startDrowning(Player player)
	{
		if (!isDrowning(player))
		{
			scheduleDrowningTask(player);
		}
	}
	
	/**
	 * Stops the drowning effect for a specific player.<br>
	 * This method checks if the {@code player} is currently drowning.<br>
	 * If they are, it cancels the {@code TaskId.DROWN} task from their controller.
	 * @param player The {@link Player} object to update.
	 */
	private static void stopDrowning(Player player)
	{
		if (isDrowning(player))
		{
			player.getController().cancelTask(TaskId.DROWN);
		}
		
	}
	
	/**
	 * Checks if a {@link Player} is currently in the process of drowning.<br>
	 * It verifies if the {@code TaskId.DROWN} task exists for the player.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player is drowning, otherwise {@code false}.
	 */
	private static boolean isDrowning(Player player)
	{
		return player.getController().getTask(TaskId.DROWN) == null ? false : true;
	}
	
	/**
	 * Schedules a repeating task to handle drowning logic for a player.<br>
	 * This method uses {@link ThreadPoolManager} to run the task at a fixed rate.<br>
	 * It reduces the HP of the {@code player} until they die or become invulnerable.
	 * @param player The {@code Player} object to apply the drowning effect to.
	 */
	private static void scheduleDrowningTask(Player player)
	{
		player.getController().addTask(TaskId.DROWN, ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			final int value = Math.round(player.getLifeStats().getMaxHp() / 10);
			
			// TODO retail emotion, attack_status packets sending
			if (!player.getLifeStats().isAlreadyDead())
			{
				if (!player.isInvul())
				{
					player.getLifeStats().reduceHp(value, player);
					player.getLifeStats().sendHpPacketUpdate();
				}
			}
			else
			{
				stopDrowning(player);
			}
		}, 0, DROWN_PERIOD));
	}
}
