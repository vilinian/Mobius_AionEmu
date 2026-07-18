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
package com.aionemu.gameserver.model.team2.league.events;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.team2.league.LeagueMember;
import com.aionemu.gameserver.model.team2.league.LeagueService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event is triggered when a {@link LeagueMember} leaves a {@link League}.<br>
 * It handles the necessary logic to update the league status and notify relevant systems.
 * @author ATracer
 */
public class LeagueLeftEvent extends AlwaysTrueTeamEvent implements Predicate<LeagueMember>
{
	private final League league;
	private final PlayerAlliance alliance;
	private final LeaveReson reason;
	
	public static enum LeaveReson
	{
		LEAVE,
		EXPEL,
		DISBAND;
	}
	
	/**
	 * Creates a new {@link LeagueLeftEvent} for a player who voluntarily left a league.<br>
	 * This constructor defaults the reason to {@code LeaveReson.LEAVE}.
	 * @param league The {@link League} that the player is leaving.
	 * @param alliance The {@link PlayerAlliance} associated with the player.
	 */
	public LeagueLeftEvent(League league, PlayerAlliance alliance)
	{
		this(league, alliance, LeaveReson.LEAVE);
	}
	
	/**
	 * Creates a new {@link LeagueLeftEvent} instance.<br>
	 * This event is triggered when a player leaves a {@code League}.<br>
	 * It captures the specific details of the departure.
	 * @param league The {@code League} that the member is leaving.
	 * @param alliance The {@code PlayerAlliance} associated with the member.
	 * @param reason The {@code LeaveReson} explaining why the member left.
	 */
	public LeagueLeftEvent(League league, PlayerAlliance alliance, LeaveReson reason)
	{
		this.league = league;
		this.alliance = alliance;
		this.reason = reason;
	}
	
	/**
	 * Processes the logic for a member leaving or being removed from a league.<br>
	 * This method updates the {@code League} and sends relevant packets to the {@code PlayerAlliance}.<br>
	 * It also triggers specific actions based on whether the reason is {@code LEAVE}, {@code EXPEL}, or {@code DISBAND}.
	 */
	@Override
	public void handleEvent()
	{
		league.removeMember(alliance.getTeamId());
		league.apply(this);
		
		switch (reason)
		{
			case LEAVE:
				alliance.sendPacket(new SM_ALLIANCE_INFO(alliance));
				checkDisband();
				break;
			case EXPEL:
				alliance.sendPacket(new SM_ALLIANCE_INFO(alliance, SM_ALLIANCE_INFO.UNION_BAN_ME, league.getLeaderObject().getLeader().getName()));
				checkDisband();
				break;
			case DISBAND:
				alliance.sendPacket(new SM_ALLIANCE_INFO(alliance));
				break;
		}
	}
	
	/**
	 * Checks if the league should be disbanded.<br>
	 * This method verifies if the number of online members is less than or equal to {@code 1}.<br>
	 * If true, it calls {@code disband} to remove the league.
	 */
	private void checkDisband()
	{
		if (league.onlineMembers() <= 1)
		{
			LeagueService.disband(league);
		}
	}
	
	/**
	 * Processes the removal of a member from a {@link League}.<br>
	 * This method sends necessary network packets to the alliance based on the leave reason.
	 * @param member The {@code LeagueMember} being processed.
	 * @return Always returns {@code true} after successfully sending packets.
	 */
	@Override
	public boolean test(LeagueMember member)
	{
		final PlayerAlliance leagueAlliance = member.getObject();
		leagueAlliance.applyOnMembers(new Predicate<Player>()
		{
			@Override
			public boolean test(Player member)
			{
				switch (reason)
				{
					case LEAVE:
						PacketSendUtility.sendPacket(member, new SM_ALLIANCE_INFO(alliance, SM_ALLIANCE_INFO.UNION_LEAVE, alliance.getLeader().getName()));
						break;
					case EXPEL:
						PacketSendUtility.sendPacket(member, new SM_ALLIANCE_INFO(alliance, SM_ALLIANCE_INFO.UNION_BAN_HIM, alliance.getLeader().getName()));
						break;
					default:
						break;
				}
				
				return true;
			}
		});
		
		return true;
	}
}
