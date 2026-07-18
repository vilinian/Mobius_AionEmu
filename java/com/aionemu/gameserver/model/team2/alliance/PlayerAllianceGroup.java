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
package com.aionemu.gameserver.model.team2.alliance;

import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;

/**
 * Represents a group of players belonging to the same alliance.<br>
 * This class manages members within an alliance and extends {@link TemporaryPlayerTeam}.
 * @author ATracer
 */
public class PlayerAllianceGroup extends TemporaryPlayerTeam<PlayerAllianceMember>
{
	private final PlayerAlliance alliance;
	
	/**
	 * Creates a new {@link PlayerAllianceGroup} instance.<br>
	 * This constructor initializes the group with a specific {@link PlayerAlliance}.<br>
	 * It also sets the unique identifier for the group.
	 * @param alliance The {@link PlayerAlliance} associated with this group.
	 * @param objId The unique identification number for the object.
	 */
	public PlayerAllianceGroup(PlayerAlliance alliance, Integer objId)
	{
		super(objId);
		this.alliance = alliance;
	}
	
	/**
	 * Adds a new member to the alliance.<br>
	 * This method updates the {@link TemporaryPlayerTeam} list.<br>
	 * It also adds the member to the current open group.
	 * @param member The {@code PlayerAllianceMember} to be added.
	 */
	@Override
	public void addMember(PlayerAllianceMember member)
	{
		super.addMember(member);
		member.setPlayerAllianceGroup(this);
		member.setAllianceId(getTeamId());
	}
	
	/**
	 * Removes a member from the alliance.<br>
	 * This method also removes the member from their specific {@link PlayerAllianceGroup}.
	 * @param member The {@code PlayerAllianceMember} to be removed.
	 */
	@Override
	public void removeMember(PlayerAllianceMember member)
	{
		super.removeMember(member);
		member.setPlayerAllianceGroup(null);
	}
	
	/**
	 * Checks if the alliance group has reached its maximum capacity.<br>
	 * It returns {@code true} if the current size is equal to 6.
	 * @return {@code true} if the group is full, {@code false} otherwise.
	 */
	@Override
	public boolean isFull()
	{
		return size() == 6;
	}
	
	/**
	 * Retrieves the minimum experience level required for a player.<br>
	 * This value is used to filter eligible members.
	 * @return the minimum experience level as an {@code int}.
	 */
	@Override
	public int getMinExpPlayerLevel()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Retrieves the maximum experience level allowed for a player in this alliance.<br>
	 * This value is used to check if a member meets the required level requirements.
	 * @return The maximum experience level as an {@code int}.
	 */
	@Override
	public int getMaxExpPlayerLevel()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Retrieves the {@link PlayerAlliance} associated with this group.<br>
	 * This method returns the primary alliance object for the current team.
	 * @return The {@code PlayerAlliance} instance.
	 */
	public PlayerAlliance getAlliance()
	{
		return alliance;
	}
}
