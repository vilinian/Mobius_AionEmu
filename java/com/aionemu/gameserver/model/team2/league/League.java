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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.GeneralTeam;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceMember;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.idfactory.IDFactory;

import java.util.function.Predicate;

/**
 * Represents a league structure within the game world.<br>
 * This class manages a collection of {@link PlayerAlliance} members and handles league-specific logic.
 * @author ATracer
 */
public class League extends GeneralTeam<PlayerAlliance, LeagueMember>
{
	private LootGroupRules lootGroupRules = new LootGroupRules();
	private static final LeagueMemberComparator MEMBER_COMPARATOR = new LeagueMemberComparator();
	
	/**
	 * Creates a new {@link League} instance.<br>
	 * This constructor sets up the team using the provided leader.<br>
	 * It initializes the league with a unique ID and calls {@code initializeTeam}.
	 * @param leader The {@code LeagueMember} who will lead this league.
	 */
	public League(LeagueMember leader)
	{
		super(IDFactory.getInstance().nextId());
		initializeTeam(leader);
	}
	
	/**
	 * Sets up the initial state of the team.<br>
	 * This method assigns the provided {@code leader} to the team.<br>
	 * It calls {@code setLeader} internally.
	 * @param leader The {@code LeagueMember} who will lead the team.
	 */
	protected void initializeTeam(LeagueMember leader)
	{
		setLeader(leader);
	}
	
	/**
	 * Retrieves all members of the league who are currently online.<br>
	 * This method returns a {@code Collection} of {@link PlayerAlliance} objects.<br>
	 * It provides a list of active participants for the current team.
	 * @return A collection containing all online {@code PlayerAlliance} members.
	 */
	@Override
	public Collection<PlayerAlliance> getOnlineMembers()
	{
		return getMembers();
	}
	
	/**
	 * Adds a new member to the {@link League}.<br>
	 * This method updates the member's league reference.
	 * @param member The {@code LeagueMember} to be added.
	 */
	@Override
	public void addMember(LeagueMember member)
	{
		super.addMember(member);
		member.getObject().setLeague(this);
	}
	
	/**
	 * Removes a specific member from the {@link League}.<br>
	 * This method updates the team list and clears the league association for the member.
	 * @param member The {@code LeagueMember} to be removed.
	 */
	@Override
	public void removeMember(LeagueMember member)
	{
		super.removeMember(member);
		member.getObject().setLeague(null);
	}
	
	/**
	 * Sends a network packet to all members of this team.<br>
	 * This method uses {@code applyOnMembers} to distribute the data.
	 * @param packet The {@code AionServerPacket} to be sent.
	 */
	@Override
	public void sendPacket(AionServerPacket packet)
	{
		for (PlayerAlliance alliance : getMembers())
		{
			alliance.sendPacket(packet);
		}
	}
	
	/**
	 * Sends a specific packet to members of the league who meet a condition.<br>
	 * This method filters {@link PlayerAlliance} objects using the provided {@code predicate}.<br>
	 * Only alliances that return {@code true} will receive the {@code packet}.
	 * @param packet The {@code AionServerPacket} to be sent to the players.
	 * @param predicate A condition used to filter which {@link PlayerAlliance} members should receive the packet.
	 */
	@Override
	public void sendPacket(AionServerPacket packet, Predicate<PlayerAlliance> predicate)
	{
		for (PlayerAlliance alliance : getMembers())
		{
			if (predicate.test(alliance))
			{
				alliance.sendPacket(packet, (Player p) -> true);
			}
		}
	}
	
	/**
	 * Gets the total number of members in this league.<br>
	 * This counts all members regardless of their online status.
	 * @return The total count of members as an {@code int}.
	 */
	@Override
	public int onlineMembers()
	{
		return getMembers().size();
	}
	
	/**
	 * Retrieves the {@code Race} of the league leader.<br>
	 * This method calls {@code getLeaderObject} to find the leader and returns their race.
	 * @return The {@link Race} of the league leader.
	 */
	@Override
	public Race getRace()
	{
		return getLeaderObject().getRace();
	}
	
	/**
	 * Checks if the league has reached its maximum capacity.<br>
	 * It returns {@code true} if the current size is equal to {@code 8}.
	 * @return {@code true} if the league is full, {@code false} otherwise.
	 */
	@Override
	public boolean isFull()
	{
		return size() == 8;
	}
	
	/**
	 * Retrieves the current {@link LootGroupRules} for this team.<br>
	 * This provides the rules used to determine how loot is distributed.
	 * @return the {@code LootGroupRules} object.
	 */
	public LootGroupRules getLootGroupRules()
	{
		return lootGroupRules;
	}
	
	/**
	 * Sets the loot rules for this team.<br>
	 * This method updates the {@code lootGroupRules} field.
	 * @param lootGroupRules The new {@code LootGroupRules} object to apply.
	 */
	public void setLootGroupRules(LootGroupRules lootGroupRules)
	{
		this.lootGroupRules = lootGroupRules;
	}
	
	/**
	 * Retrieves a list of all members in the league.<br>
	 * The members are sorted using {@code LeagueMemberComparator}.
	 * @return A {@code Collection} of {@link LeagueMember} objects sorted by their rank.
	 */
	public Collection<LeagueMember> getSortedMembers()
	{
		final ArrayList<LeagueMember> newArrayList = new ArrayList<>(members.values());
		Collections.sort(newArrayList, MEMBER_COMPARATOR);
		return newArrayList;
	}
	
	/**
	 * Finds a {@link Player} within the league based on their unique ID.<br>
	 * This method searches through all members to locate the matching object.<br>
	 * It returns {@code null} if no player is found with the provided ID.
	 * @param playerObjId The unique identifier of the player to find.
	 * @return The {@link Player} object if found, otherwise {@code null}.
	 */
	public Player getPlayerMember(Integer playerObjId)
	{
		for (PlayerAlliance member : getMembers())
		{
			final PlayerAllianceMember playerMember = member.getMember(playerObjId);
			if (playerMember != null)
			{
				return playerMember.getObject();
			}
		}
		
		return null;
	}
	
	static class LeagueMemberComparator implements Comparator<LeagueMember>
	{
		@Override
		public int compare(LeagueMember o1, LeagueMember o2)
		{
			return o1.getLeaguePosition() > o2.getLeaguePosition() ? 1 : -1;
		}
	}
}
