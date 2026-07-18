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
package com.aionemu.gameserver.model.team2.common.service;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.alliance.events.AssignViceCaptainEvent.AssignType;
import com.aionemu.gameserver.model.team2.common.events.TeamCommand;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.team2.league.LeagueMember;
import com.aionemu.gameserver.model.team2.league.LeagueService;

/**
 * This service handles the processing of {@link TeamCommand} actions for players.<br>
 * It manages team-related logic such as group, league, and alliance interactions.<br>
 * Use this class to execute commands that affect a player's team status or permissions.
 * @author ATracer
 */
public class PlayerTeamCommandService
{
	/**
	 * Executes a specific {@link TeamCommand} for a player.<br>
	 * This method validates the command against the provided {@code playerObjId}.<br>
	 * It then calls the internal {@code execute} method to process the action.
	 * @param player The {@link Player} who is initiating the command.
	 * @param command The {@link TeamCommand} type to be performed.
	 * @param playerObjId The unique identifier for the target object or 0 if none.
	 */
	public static void executeCommand(Player player, TeamCommand command, int playerObjId)
	{
		final Player teamSubjective = getTeamSubjective(player, playerObjId);
		
		// if playerObjId is not 0 - subjective should not be active player
		if (!((playerObjId == 0) || teamSubjective.getObjectId().equals(playerObjId) || (command == TeamCommand.LEAGUE_EXPEL)))
		{
			throw new IllegalArgumentException("Wrong command detected " + command);
		}
		execute(player, command, teamSubjective);
	}
	
	/**
	 * Processes a specific team command for a given player.<br>
	 * This method handles logic for group, alliance, and league actions.
	 * @param player The {@code Player} who initiated the action.
	 * @param eventCode The {@code TeamCommand} type to be executed.
	 * @param teamSubjective The {@code Player} representing the target of the command.
	 */
	private static void execute(Player player, TeamCommand eventCode, Player teamSubjective)
	{
		switch (eventCode)
		{
			case GROUP_BAN_MEMBER:
				PlayerGroupService.banPlayer(teamSubjective, player);
				break;
			case GROUP_SET_LEADER:
				PlayerGroupService.changeLeader(teamSubjective);
				break;
			case GROUP_REMOVE_MEMBER:
				PlayerGroupService.removePlayer(teamSubjective);
				break;
			case GROUP_START_MENTORING:
				PlayerGroupService.startMentoring(player);
				break;
			case GROUP_END_MENTORING:
				PlayerGroupService.stopMentoring(player);
				break;
			case ALLIANCE_LEAVE:
				PlayerAllianceService.removePlayer(player);
				break;
			case ALLIANCE_BAN_MEMBER:
				PlayerAllianceService.banPlayer(teamSubjective, player);
				break;
			case ALLIANCE_SET_CAPTAIN:
				PlayerAllianceService.changeLeader(teamSubjective);
				break;
			case ALLIANCE_CHECKREADY_CANCEL:
			case ALLIANCE_CHECKREADY_START:
			case ALLIANCE_CHECKREADY_AUTOCANCEL:
			case ALLIANCE_CHECKREADY_NOTREADY:
			case ALLIANCE_CHECKREADY_READY:
				PlayerAllianceService.checkReady(player, eventCode);
				break;
			case ALLIANCE_SET_VICECAPTAIN:
				PlayerAllianceService.changeViceCaptain(teamSubjective, AssignType.PROMOTE);
				break;
			case ALLIANCE_UNSET_VICECAPTAIN:
				PlayerAllianceService.changeViceCaptain(teamSubjective, AssignType.DEMOTE);
				break;
			case LEAGUE_LEAVE:
				LeagueService.removeAlliance(player.getPlayerAlliance2());
				break;
			case LEAGUE_EXPEL:
				LeagueService.expelAlliance(teamSubjective, player);
				break;
			default:
				break;
		}
	}
	
	/**
	 * Retrieves the team leader or a specific member based on an ID.<br>
	 * It checks if the {@code player} is in a team or league.<br>
	 * If no specific member is found, it returns the original {@code player}.
	 * @param player The {@link Player} object to check.
	 * @param playerObjId The unique ID of the target member.
	 * @return The {@link Player} object representing the team leader or member.
	 */
	private static Player getTeamSubjective(Player player, int playerObjId)
	{
		if (playerObjId == 0)
		{
			return player;
		}
		
		if (player.isInTeam())
		{
			final TeamMember<Player> member = player.getCurrentTeam().getMember(playerObjId);
			if (member != null)
			{
				return member.getObject();
			}
			
			if (player.isInLeague())
			{
				final LeagueMember subjective = player.getPlayerAlliance2().getLeague().getMember(playerObjId);
				if (subjective != null)
				{
					return subjective.getObject().getLeaderObject();
				}
			}
		}
		
		return player;
	}
}
