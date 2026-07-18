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
package com.aionemu.gameserver.model.team2.league;

import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;

/**
 * Represents a member within a league structure.<br>
 * This class extends {@link TeamMember} to specifically handle players associated with a {@link PlayerAlliance}.
 * @author ATracer
 */
public class LeagueMember implements TeamMember<PlayerAlliance>
{
	private final PlayerAlliance alliance;
	private final int leaguePosition;
	
	/**
	 * Creates a new {@link LeagueMember} instance.<br>
	 * This constructor initializes the member with an alliance and a specific rank.
	 * @param alliance The {@code PlayerAlliance} associated with this member.
	 * @param position The numerical league position of the member.
	 */
	public LeagueMember(PlayerAlliance alliance, int position)
	{
		this.alliance = alliance;
		leaguePosition = position;
	}
	
	/**
	 * Retrieves the unique identifier for the associated {@link PlayerAlliance}.<br>
	 * This ID identifies the alliance within the game system.
	 * @return The {@code Integer} object ID of the alliance.
	 */
	@Override
	public Integer getObjectId()
	{
		return alliance.getObjectId();
	}
	
	/**
	 * Retrieves the name of the league member.<br>
	 * This method returns the {@code String} name from the associated {@link PlayerAlliance}.
	 * @return The name of the league member as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return alliance.getName();
	}
	
	/**
	 * Retrieves the {@link PlayerAlliance} associated with this league member.<br>
	 * This method returns the primary object stored in this instance.
	 * @return The {@code PlayerAlliance} object.
	 */
	@Override
	public PlayerAlliance getObject()
	{
		return alliance;
	}
	
	/**
	 * Retrieves the current rank of the member in the league.<br>
	 * This value is assigned during the creation of a {@link LeagueMember}.
	 * @return The integer position of the member.
	 */
	public int getLeaguePosition()
	{
		return leaguePosition;
	}
}
