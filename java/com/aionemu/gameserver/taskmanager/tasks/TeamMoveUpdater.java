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
 * This task periodically updates the positions of players within groups and alliances.<br>
 * It ensures that team-based movements are synchronized across the game world.
 * @author Sarynth Supports PlayerGroup and PlayerAlliance movement updating.
 */
public final class TeamMoveUpdater extends AbstractIterativePeriodicTaskManager<Player>
{
	private static final class SingletonHolder
	{
		private static final TeamMoveUpdater INSTANCE = new TeamMoveUpdater();
	}
	
	/**
	 * Provides the global instance of the {@link TeamMoveUpdater}.<br>
	 * Use this method to access the shared task manager.
	 * @return The singleton instance of {@code TeamMoveUpdater}.
	 */
	public static TeamMoveUpdater getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	/**
	 * Initializes a new instance of the {@code TeamMoveUpdater}.<br>
	 * This class handles movement updates for players in groups and alliances.<br>
	 * It sets the task interval to {@code 2000} milliseconds.
	 */
	public TeamMoveUpdater()
	{
		super(2000);
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
		if (player.isInGroup2())
		{
			PlayerGroupService.updateGroup(player, GroupEvent.MOVEMENT);
		}
		
		if (player.isInAlliance2())
		{
			PlayerAllianceService.updateAlliance(player, PlayerAllianceEvent.MOVEMENT);
		}
		
		// Remove task from list. It will be re-added if player moves again.
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
		return "teamMoveUpdate()";
	}
}
