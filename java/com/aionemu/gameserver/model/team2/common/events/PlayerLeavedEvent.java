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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamEvent;
import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import java.util.function.Predicate;

/**
 * Represents an event triggered when a {@link Player} leaves a team.<br>
 * This class provides the necessary context for handling team membership changes.
 * @author ATracer
 * @param <TM>
 * @param <T>
 */
public abstract class PlayerLeavedEvent<TM extends TeamMember<Player>, T extends TemporaryPlayerTeam<TM>> implements Predicate<TM>, TeamEvent
{
	public static enum LeaveReson
	{
		BAN,
		LEAVE,
		LEAVE_TIMEOUT,
		DISBAND;
	}
	
	protected final T team;
	protected final Player leavedPlayer;
	protected final LeaveReson reason;
	protected final TM leavedTeamMember;
	protected final String banPersonName;
	
	/**
	 * Creates a new {@link PlayerLeavedEvent} for a player who left voluntarily.<br>
	 * This constructor uses the default reason of {@code LEAVE}.
	 * @param alliance The team that the player is leaving.
	 * @param player The player who has left the team.
	 */
	public PlayerLeavedEvent(T alliance, Player player)
	{
		this(alliance, player, LeaveReson.LEAVE);
	}
	
	/**
	 * Creates a new {@link PlayerLeavedEvent} with a specific reason.<br>
	 * This constructor is used when the ban name is not required.
	 * @param alliance The {@code T} team object associated with the event.
	 * @param player The {@code Player} who has left the team.
	 * @param reason The {@code LeaveReson} explaining why the player left.
	 */
	public PlayerLeavedEvent(T alliance, Player player, LeaveReson reason)
	{
		this(alliance, player, reason, "");
	}
	
	/**
	 * Creates a new event for when a player leaves a team.<br>
	 * This constructor initializes the event with specific details about the departure.
	 * @param team The {@code T} type team that the player is leaving.
	 * @param player The {@link Player} object of the person who left.
	 * @param reason The {@code LeaveReson} explaining why the player left.
	 * @param banPersonName The name of the person responsible for a ban, if applicable.
	 */
	public PlayerLeavedEvent(T team, Player player, LeaveReson reason, String banPersonName)
	{
		this.team = team;
		leavedPlayer = player;
		this.reason = reason;
		leavedTeamMember = team.getMember(player.getObjectId());
		this.banPersonName = banPersonName;
	}
	
	/**
	 * Checks if the player is still part of the team.<br>
	 * It verifies that the {@code leavedPlayer} exists within the {@code team}.
	 * @return {@code true} if the member is found, otherwise {@code false}.
	 */
	@Override
	public boolean checkCondition()
	{
		return team.hasMember(leavedPlayer.getObjectId());
	}
}
