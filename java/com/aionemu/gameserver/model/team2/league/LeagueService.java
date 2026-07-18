/*
 * This file is part of the Mobius AionEmu project.
 * 
 * Mobius AionEmu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Mobius AionEmu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.team2.league;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.league.events.LeagueDisbandEvent;
import com.aionemu.gameserver.model.team2.league.events.LeagueEnteredEvent;
import com.aionemu.gameserver.model.team2.league.events.LeagueInvite;
import com.aionemu.gameserver.model.team2.league.events.LeagueLeftEvent;
import com.aionemu.gameserver.model.team2.league.events.LeagueLeftEvent.LeaveReson;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the logic and operations for the league system.<br>
 * This service handles player interactions such as joining, leaving, and disbanding leagues.<br>
 * It coordinates events related to {@link PlayerAlliance} and league memberships.
 * @author ATracer, Mobius
 */
public class LeagueService
{
	private static final Logger log = LoggerFactory.getLogger(LeagueService.class);
	private static final Map<Integer, League> leagues = new ConcurrentHashMap<>();
	
	/**
	 * Handles league cleanup when an alliance is disbanded.<br>
	 * Any league that the alliance was a member of receives a {@link LeagueLeftEvent}.
	 * @param alliance The {@link PlayerAlliance} that was disbanded.
	 */
	public static void onAllianceDisbanded(PlayerAlliance alliance)
	{
		try
		{
			for (League league : leagues.values())
			{
				if (league.hasMember(alliance.getTeamId()))
				{
					league.onEvent(new LeagueLeftEvent(league, alliance));
				}
			}
		}
		catch (Throwable t)
		{
			log.error("Error during alliance disband listen", t);
		}
	}
	
	/**
	 * Sends a league invitation to another player.<br>
	 * This method checks if the {@code inviter} is allowed to invite the {@code invited} player.<br>
	 * If successful, it displays an invitation window for the recipient.
	 * @param inviter The player who is sending the invitation.
	 * @param invited The player who will receive the invitation.
	 */
	public static void inviteToLeague(Player inviter, Player invited)
	{
		if (canInvite(inviter, invited))
		{
			final LeagueInvite invite = new LeagueInvite(inviter, invited);
			if (invited.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_MSGBOX_UNION_INVITE_ME, invite))
			{
				if (invited.isInAlliance2())
				{
					PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_UNION_INVITE_HIM(invited.getName(), inviter.getName()));
				}
				
				PacketSendUtility.sendPacket(invited, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_MSGBOX_UNION_INVITE_ME, 0, 0, inviter.getName()));
			}
		}
	}
	
	/**
	 * Checks if a player is allowed to send an alliance invitation.<br>
	 * This method validates instance status and zone restrictions for both players.<br>
	 * It also verifies team membership rules for defense type alliances.
	 * @param inviter The {@code Player} sending the invitation.
	 * @param invited The {@code Player} receiving the invitation.
	 * @return {@code true} if the invitation is allowed, otherwise {@code false}.
	 */
	public static boolean canInvite(Player inviter, Player invited)
	{
		if (inviter.isInInstance())
		{
			if (AutoGroupService.getInstance().isAutoInstance(inviter.getInstanceId()))
			{
				// You cannot use invite, leave or kick commands related to your group or alliance in this region.
				PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_CANT_OPERATE_PARTY_COMMAND);
				return false;
			}
		}
		
		if (invited.isInInstance())
		{
			if (AutoGroupService.getInstance().isAutoInstance(invited.getInstanceId()))
			{
				// You cannot use invite, leave or kick commands related to your group or alliance in this region.
				PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_CANT_OPERATE_PARTY_COMMAND);
				return false;
			}
		}
		
		return RestrictionsManager.canInviteToLeague(inviter, invited);
	}
	
	/**
	 * Creates a new {@link League} for the player who sends the invitation.<br>
	 * This method initializes the league with the inviter's {@code PlayerAlliance}.<br>
	 * It also registers the new league into the global system.
	 * @param inviter The {@code Player} who is creating the league.
	 * @param invited The {@code Player} who received the invitation.
	 * @return The newly created {@code League} object.
	 */
	public static League createLeague(Player inviter, Player invited)
	{
		final PlayerAlliance alliance = inviter.getPlayerAlliance2();
		Objects.requireNonNull(alliance, "League can not be null");
		final League newLeague = new League(new LeagueMember(alliance, 0));
		leagues.put(newLeague.getTeamId(), newLeague);
		addAlliance(newLeague, alliance);
		return newLeague;
	}
	
	/**
	 * Adds a {@link PlayerAlliance} to a specific {@link League}.<br>
	 * This method triggers the {@code LeagueEnteredEvent} for the new alliance.
	 * @param league The {@code League} object where the alliance will be added.
	 * @param alliance The {@code PlayerAlliance} object to join the league.
	 */
	public static void addAlliance(League league, PlayerAlliance alliance)
	{
		Objects.requireNonNull(league, "League should not be null");
		league.onEvent(new LeagueEnteredEvent(league, alliance));
	}
	
	/**
	 * Adds a {@link PlayerAlliance} to a specific {@link League}.<br>
	 * This method updates the member count of the league.
	 * @param league The {@code League} object where the alliance will be added.
	 * @param alliance The {@code PlayerAlliance} object to join the league.
	 */
	public static void addAllianceToLeague(League league, PlayerAlliance alliance)
	{
		league.addMember(new LeagueMember(alliance, league.size()));
	}
	
	/**
	 * Removes a specific alliance from its current league.<br>
	 * This method triggers the {@link LeagueLeftEvent} for the alliance.<br>
	 * It checks if the provided {@code alliance} is {@code null} before proceeding.
	 * @param alliance The {@code PlayerAlliance} object to be removed.
	 */
	public static void removeAlliance(PlayerAlliance alliance)
	{
		if (alliance != null)
		{
			final League league = alliance.getLeague();
			Objects.requireNonNull(league, "League should not be null");
			league.onEvent(new LeagueLeftEvent(league, alliance));
		}
	}
	
	/**
	 * Removes a player from the current league.<br>
	 * This action is performed by an alliance leader who also leads the league.<br>
	 * The method triggers a {@link LeagueLeftEvent} for the removed alliance.
	 * @param expelledPlayer The {@code Player} to be removed from the league.
	 * @param expelGiver The {@code Player} performing the expulsion action.
	 */
	public static void expelAlliance(Player expelledPlayer, Player expelGiver)
	{
		Objects.requireNonNull(expelledPlayer, "Expelled player should not be null");
		Objects.requireNonNull(expelGiver, "ExpelGiver player should not be null");
		if (!(expelGiver.isInLeague()))
		{
			throw new IllegalArgumentException("Expelled player should be in league");
		}
		if (!(expelledPlayer.isInLeague()))
		{
			throw new IllegalArgumentException("ExpelGiver should be in league");
		}
		if (!(expelGiver.getPlayerAlliance2().getLeague().isLeader(expelGiver.getPlayerAlliance2())))
		{
			throw new IllegalArgumentException("ExpelGiver alliance should be the leader of league");
		}
		if (!(expelGiver.getPlayerAlliance2().isLeader(expelGiver)))
		{
			throw new IllegalArgumentException("ExpelGiver should be the leader of alliance");
		}
		final PlayerAlliance alliance = expelGiver.getPlayerAlliance2();
		final League league = alliance.getLeague();
		league.onEvent(new LeagueLeftEvent(league, expelledPlayer.getPlayerAlliance2(), LeaveReson.EXPEL));
	}
	
	/**
	 * Disbands a specific {@link League}.<br>
	 * This method removes the league from the active list.<br>
	 * It triggers a {@code LeagueDisbandEvent} for the league.<br>
	 * The league must have one or fewer online members to be disbanded.
	 * @param league The {@code League} object to be disbanded.
	 */
	public static void disband(League league)
	{
		if (!(league.onlineMembers() <= 1))
		{
			throw new IllegalStateException("Can't disband league with more than one online member");
		}
		leagues.remove(league.getTeamId());
		league.onEvent(new LeagueDisbandEvent(league));
	}
}
