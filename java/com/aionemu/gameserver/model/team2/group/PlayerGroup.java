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
package com.aionemu.gameserver.model.team2.group;

import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.utils.idfactory.IDFactory;

/**
 * Represents a collection of players organized into a group.<br>
 * This class manages {@link PlayerGroupMember} entities within the game world.
 * @author ATracer
 */
public class PlayerGroup extends TemporaryPlayerTeam<PlayerGroupMember>
{
	private final PlayerGroupStats playerGroupStats;
	private final TeamType type;
	
	/**
	 * Creates a new {@link PlayerGroup} with a specific leader and team type.<br>
	 * This constructor initializes the group statistics and sets up the initial team structure.
	 * @param leader The {@code PlayerGroupMember} who will lead this group.
	 * @param type The {@code TeamType} assigned to this group.
	 */
	public PlayerGroup(PlayerGroupMember leader, TeamType type)
	{
		super(IDFactory.getInstance().nextId());
		playerGroupStats = new PlayerGroupStats(this);
		this.type = type;
		initializeTeam(leader);
	}
	
	/**
	 * Adds a new member to the current {@link PlayerGroup}.<br>
	 * This method updates the group statistics.<br>
	 * It also links the member back to this group instance.
	 * @param member The {@code PlayerGroupMember} to be added.
	 */
	@Override
	public void addMember(PlayerGroupMember member)
	{
		super.addMember(member);
		playerGroupStats.onAddPlayer(member);
		member.getObject().setPlayerGroup2(this);
	}
	
	/**
	 * Removes a specific member from the group.<br>
	 * This method updates the {@code playerGroupStats} and clears the member's group reference.
	 * @param member The {@code PlayerGroupMember} to be removed.
	 */
	@Override
	public void removeMember(PlayerGroupMember member)
	{
		super.removeMember(member);
		playerGroupStats.onRemovePlayer(member);
		member.getObject().setPlayerGroup2(null);
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
		return playerGroupStats.getMinExpPlayerLevel();
	}
	
	/**
	 * Retrieves the maximum experience level allowed for a player in this alliance.<br>
	 * This value is used to check if a member meets the required level requirements.
	 * @return The maximum experience level as an {@code int}.
	 */
	@Override
	public int getMaxExpPlayerLevel()
	{
		return playerGroupStats.getMaxExpPlayerLevel();
	}
	
	/**
	 * Retrieves the team type of this alliance.<br>
	 * This method returns the {@code TeamType} assigned to the instance.
	 * @return The {@code TeamType} of the alliance.
	 */
	public TeamType getTeamType()
	{
		return type;
	}
}
