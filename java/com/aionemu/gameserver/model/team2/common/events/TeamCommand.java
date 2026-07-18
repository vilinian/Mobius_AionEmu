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
package com.aionemu.gameserver.model.team2.common.events;

import java.util.Objects;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Represents the various types of commands that can be issued to a team.<br>
 * This enum is used by {@code TeamEvent} to identify specific actions.
 * @author ATracer
 */
public enum TeamCommand
{
	GROUP_BAN_MEMBER(2),
	GROUP_SET_LEADER(3),
	GROUP_REMOVE_MEMBER(6),
	GROUP_SET_LFG(9), // TODO confirm
	GROUP_START_MENTORING(10),
	GROUP_END_MENTORING(11),
	ALLIANCE_LEAVE(14),
	ALLIANCE_BAN_MEMBER(16),
	ALLIANCE_SET_CAPTAIN(17),
	ALLIANCE_CHECKREADY_CANCEL(20),
	ALLIANCE_CHECKREADY_START(21),
	ALLIANCE_CHECKREADY_AUTOCANCEL(22),
	ALLIANCE_CHECKREADY_READY(23),
	ALLIANCE_CHECKREADY_NOTREADY(24),
	ALLIANCE_SET_VICECAPTAIN(25),
	ALLIANCE_UNSET_VICECAPTAIN(26),
	ALLIANCE_CHANGE_GROUP(27),
	LEAGUE_LEAVE(29),
	LEAGUE_EXPEL(30);
	
	private static TIntObjectHashMap<TeamCommand> teamCommands;
	
	static
	{
		teamCommands = new TIntObjectHashMap<>();
		for (TeamCommand eventCode : values())
		{
			teamCommands.put(eventCode.getCodeId(), eventCode);
		}
	}
	
	private final int commandCode;
	
	/**
	 * Creates a new instance of {@link TeamCommand}.<br>
	 * This constructor assigns the unique identifier to the object.
	 * @param commandCode The integer ID representing the specific team action.
	 */
	private TeamCommand(int commandCode)
	{
		this.commandCode = commandCode;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link TeamCommand}.<br>
	 * This ID is used to map commands to their specific numeric codes.
	 * @return The integer code associated with this command.
	 */
	public int getCodeId()
	{
		return commandCode;
	}
	
	/**
	 * Retrieves a {@link TeamCommand} based on its unique integer ID.<br>
	 * This method looks up the value in the internal command map.<br>
	 * It throws an exception if the provided code is not found.
	 * @param commandCode The unique integer identifier for the team command.
	 * @return The corresponding {@link TeamCommand} object.
	 */
	public static TeamCommand getCommand(int commandCode)
	{
		final TeamCommand command = teamCommands.get(commandCode);
		Objects.requireNonNull(command, "Invalid team command code " + commandCode);
		return command;
	}
}
