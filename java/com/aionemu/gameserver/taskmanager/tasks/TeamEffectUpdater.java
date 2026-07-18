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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.taskmanager.AbstractIterativePeriodicTaskManager;

/**
 * This task periodically updates the movement effects for players in groups or alliances.<br>
 * It ensures that team-based visual effects are synchronized across all members.<br>
 * It extends {@link AbstractIterativePeriodicTaskManager} to process {@code Player} objects.
 * @author Sarynth Supports PlayerGroup and PlayerAlliance movement updating.
 */
public final class TeamEffectUpdater extends AbstractIterativePeriodicTaskManager<Player>
{
	private static final class SingletonHolder
	{
		private static final TeamEffectUpdater INSTANCE = new TeamEffectUpdater();
	}
	
	/**
	 * Provides the global instance of the {@link TeamEffectUpdater}.<br>
	 * Use this method to access the singleton manager.
	 * @return The single instance of {@code TeamEffectUpdater}.
	 */
	public static TeamEffectUpdater getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	/**
	 * Initializes a new instance of the {@code TeamEffectUpdater}.<br>
	 * This class handles updates for player group and alliance movements.<br>
	 * It sets the task interval to {@code 500} milliseconds.
	 */
	public TeamEffectUpdater()
	{
		super(500);
	}
	
	/**
	 * This method updates the group and alliance status for a {@code Player}.<br>
	 * It checks if the {@code Player} is online before updating.<br>
	 * The task is stopped after execution to be re-added only when effects change.
	 * @param player The {@code Player} object to process.
	 */
	@Override
	protected void callTask(Player player)
	{
		if (player.isOnline())
		{
			if (player.isInGroup2())
			{
				PlayerGroupService.updateGroup(player, GroupEvent.UPDATE);
			}
			
			if (player.isInAlliance2())
			{
				PlayerAllianceService.updateAlliance(player, PlayerAllianceEvent.UPDATE);
			}
		}
		
		// Remove task from list. It will be re-added if player effect changes again.
		stopTask(player);
	}
	
	/**
	 * Returns the name of the method that was called by this task.<br>
	 * This is used for internal tracking and logging purposes.
	 * @return The {@code String} name of the executed method.
	 */
	@Override
	protected String getCalledMethodName()
	{
		return "teamEffectUpdate()";
	}
}
