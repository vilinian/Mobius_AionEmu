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
package com.aionemu.gameserver.network.aion.gmhandler;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the command to increase or decrease a player's level.<br>
 * This class allows Game Masters to modify character levels via the {@link AbstractGMHandler}.
 * @author Alcapwnd
 */
public class CmdLevelUpDown extends AbstractGMHandler
{
	public enum LevelUpDownState
	{
		UP,
		DOWN
	}
	
	private final LevelUpDownState state;
	
	/**
	 * Creates a new instance of {@code CmdLevelUpDown}.<br>
	 * This constructor initializes the command with an admin and specific level change parameters.<br>
	 * It automatically triggers the {@code run} method to execute the logic.
	 * @param admin The {@link Player} who is executing the command.
	 * @param params The string containing additional arguments for the command.
	 * @param state The {@code LevelUpDownState} indicating if the level goes up or down.
	 */
	public CmdLevelUpDown(Player admin, String params, LevelUpDownState state)
	{
		super(admin, params);
		this.state = state;
		run();
	}
	
	/**
	 * Executes the command to change a player's character level.<br>
	 * It adjusts the level based on the {@code state} and the provided {@code params}.<br>
	 * The method validates that the new level stays within allowed limits.
	 */
	public void run()
	{
		final Player t = target != null ? target : admin;
		final Integer level = Integer.parseInt(params);
		
		if (state == LevelUpDownState.DOWN)
		{
			if ((t.getCommonData().getLevel() - level) >= 1)
			{
				final int newLevel = t.getCommonData().getLevel() - level;
				t.getCommonData().setLevel(newLevel);
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "The value of <level> will minus calculated to the current player level!");
			}
		}
		else if (state == LevelUpDownState.UP)
		{
			if ((t.getCommonData().getLevel() + level) <= GSConfig.PLAYER_MAX_LEVEL)
			{
				final int newLevel = t.getCommonData().getLevel() + level;
				t.getCommonData().setLevel(newLevel);
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "The value of <level> will plus calculated to the current player level!");
			}
		}
	}
	
}
