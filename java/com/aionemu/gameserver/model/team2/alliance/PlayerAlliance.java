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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.utils.idfactory.IDFactory;

/**
 * Represents an alliance formed by players within the game.<br>
 * This class manages a collection of {@link PlayerAllianceMember} objects to track membership.<br>
 * It extends {@link TemporaryPlayerTeam} to handle team-based logic for alliances.
 * @author ATracer
 */
public class PlayerAlliance extends TemporaryPlayerTeam<PlayerAllianceMember>
{
	private final Map<Integer, PlayerAllianceGroup> groups = new HashMap<>();
	private final List<Integer> viceCaptainIds = new CopyOnWriteArrayList<>();
	private int allianceReadyStatus;
	private final TeamType type;
	private League league;
	
	/**
	 * Creates a new {@link PlayerAlliance} instance.<br>
	 * This constructor initializes the alliance with a leader and a specific team type.<br>
	 * It also sets up the default alliance groups.
	 * @param leader The {@code PlayerAllianceMember} who will lead the alliance.
	 * @param type The {@code TeamType} assigned to this alliance.
	 */
	public PlayerAlliance(PlayerAllianceMember leader, TeamType type)
	{
		super(IDFactory.getInstance().nextId());
		this.type = type;
		initializeTeam(leader);
		for (int groupId = 1000; groupId <= 1003; groupId++)
		{
			groups.put(groupId, new PlayerAllianceGroup(this, groupId));
		}
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
		final PlayerAllianceGroup openAllianceGroup = getOpenAllianceGroup();
		openAllianceGroup.addMember(member);
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
		member.getPlayerAllianceGroup().removeMember(member);
	}
	
	/**
	 * Checks if the alliance has reached its maximum capacity.<br>
	 * It returns {@code true} if the current size is equal to {@code 24}.
	 * @return {@code true} if the alliance is full, {@code false} otherwise.
	 */
	@Override
	public boolean isFull()
	{
		return size() == 24;
	}
	
	/**
	 * Retrieves the minimum experience level required for a player.<br>
	 * This value is used to filter eligible members.
	 * @return the minimum experience level as an {@code int}.
	 */
	@Override
	public int getMinExpPlayerLevel()
	{
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
		return 0;
	}
	
	/**
	 * Finds the first available group within the alliance.<br>
	 * It checks groups with IDs from {@code 1000} to {@code 1003}.<br>
	 * This method returns a group that is not full.
	 * @return The first non-full {@link PlayerAllianceGroup} found.
	 */
	public PlayerAllianceGroup getOpenAllianceGroup()
	{
		lock();
		try
		{
			for (int groupId = 1000; groupId <= 1003; groupId++)
			{
				final PlayerAllianceGroup playerAllianceGroup = groups.get(groupId);
				if (!playerAllianceGroup.isFull())
				{
					return playerAllianceGroup;
				}
			}
		}
		finally
		{
			unlock();
		}
		
		throw new IllegalStateException("All alliance groups are full.");
	}
	
	/**
	 * Retrieves a specific {@link PlayerAllianceGroup} from the alliance.<br>
	 * This method uses the provided unique identifier to find the group.<br>
	 * It will throw an exception if the ID does not exist.
	 * @param allianceGroupId The unique ID of the group to retrieve.
	 * @return The {@code PlayerAllianceGroup} associated with the given ID.
	 */
	public PlayerAllianceGroup getAllianceGroup(Integer allianceGroupId)
	{
		final PlayerAllianceGroup allianceGroup = groups.get(allianceGroupId);
		Objects.requireNonNull(allianceGroup, "No such alliance group " + allianceGroupId);
		return allianceGroup;
	}
	
	/**
	 * Retrieves the list of unique identifiers for all vice captains.<br>
	 * These IDs correspond to members with special permissions within the alliance.
	 * @return a {@code List<Integer>} containing the vice captain IDs.
	 */
	public List<Integer> getViceCaptainIds()
	{
		return viceCaptainIds;
	}
	
	/**
	 * Checks if a specific player is a vice captain.<br>
	 * This method looks up the {@code Player} ID in the internal list of vice captains.
	 * @param player The {@link Player} to check.
	 * @return {@code true} if the player is a vice captain, otherwise {@code false}.
	 */
	public boolean isViceCaptain(Player player)
	{
		return viceCaptainIds.contains(player.getObjectId());
	}
	
	/**
	 * Checks if a player has a leadership role in the alliance.<br>
	 * This returns {@code true} if the player is either the leader or a vice captain.
	 * @param player The {@link Player} to check.
	 * @return {@code true} if the player is a captain, otherwise {@code false}.
	 */
	public boolean isSomeCaptain(Player player)
	{
		return isLeader(player) || isViceCaptain(player);
	}
	
	/**
	 * Retrieves the current readiness status of the alliance.<br>
	 * This value indicates if the alliance is prepared for its next action.
	 * @return The {@code int} value representing the alliance ready status.
	 */
	public int getAllianceReadyStatus()
	{
		return allianceReadyStatus;
	}
	
	/**
	 * Updates the current ready status of the alliance.<br>
	 * This value is used to track if the group is prepared for activities.
	 * @param allianceReadyStatus The new {@code int} status to assign.
	 */
	public void setAllianceReadyStatus(int allianceReadyStatus)
	{
		this.allianceReadyStatus = allianceReadyStatus;
	}
	
	/**
	 * Retrieves the {@link League} associated with this alliance.<br>
	 * This method returns the current league object.
	 * @return the {@code League} of this alliance.
	 */
	public League getLeague()
	{
		return league;
	}
	
	/**
	 * Sets the {@link League} for this alliance.<br>
	 * This updates the internal league field of the {@code PlayerAlliance}.
	 * @param league The {@code League} object to assign.
	 */
	public void setLeague(League league)
	{
		this.league = league;
	}
	
	/**
	 * Checks if the alliance is currently part of a league.<br>
	 * This method returns {@code true} if the {@link League} object is not {@code null}.
	 * @return {@code true} if the alliance belongs to a league, {@code false} otherwise.
	 */
	public boolean isInLeague()
	{
		return league != null;
	}
	
	/**
	 * Returns the total number of groups in this alliance.<br>
	 * This method counts the entries in the internal {@code groups} map.
	 * @return The size of the group collection as an {@code int}.
	 */
	public int groupSize()
	{
		return groups.size();
	}
	
	/**
	 * Retrieves all the alliance groups belonging to this {@link PlayerAlliance}.<br>
	 * This method returns a collection of all active groups.
	 * @return A {@code Collection} containing all {@code PlayerAllianceGroup} objects.
	 */
	public Collection<PlayerAllianceGroup> getGroups()
	{
		return groups.values();
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
