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
package com.aionemu.gameserver.model.team2;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.aionemu.gameserver.model.gameobjects.AionObject;

/**
 * Represents a generic team structure within the game world.<br>
 * This abstract class provides a base implementation for managing {@code M} members and their associated data.<br>
 * It serves as a foundation for various team types in the {@code com.aionemu.gameserver.model.team2} package.
 * @author ATracer
 * @param <M>
 * @param <TM>
 */
public abstract class GeneralTeam<M extends AionObject, TM extends TeamMember<M>>extends AionObject implements Team<M, TM>
{
	private final static Logger log = LoggerFactory.getLogger(GeneralTeam.class);
	protected final Map<Integer, TM> members = new ConcurrentHashMap<>();
	protected final Lock teamLock = new ReentrantLock();
	private TM leader;
	private final MemberTransformFunction<TM, M> TRANSFORM_FUNCTION = new MemberTransformFunction<>();
	
	/**
	 * Creates a new instance of a {@link GeneralTeam}.<br>
	 * This constructor initializes the team with a specific object identifier.
	 * @param objId The unique identifier for this team object.
	 */
	public GeneralTeam(Integer objId)
	{
		super(objId);
	}
	
	/**
	 * Processes a {@link TeamEvent} for this team.<br>
	 * It checks if the event conditions are met before handling it.<br>
	 * If the condition fails, it logs a warning message.
	 * @param event The {@code TeamEvent} to be processed.
	 */
	@Override
	public void onEvent(TeamEvent event)
	{
		lock();
		try
		{
			if (event.checkCondition())
			{
				event.handleEvent();
			}
			else
			{
				log.warn("[TEAM2] skipped event: {} group: {}", event, this);
			}
		}
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Retrieves a team member based on their unique ID.<br>
	 * This method looks up the {@code TM} object in the internal map.<br>
	 * It returns {@code null} if no member is found.
	 * @param objectId The unique identifier of the member to find.
	 * @return The {@code TM} object associated with the ID, or {@code null}.
	 */
	@Override
	public TM getMember(Integer objectId)
	{
		return members.get(objectId);
	}
	
	/**
	 * Checks if a specific member exists in the team.<br>
	 * It looks for the member using the provided {@code objectId}.
	 * @param objectId The unique identifier of the member to find.
	 * @return {@code true} if the member is found, otherwise {@code false}.
	 */
	@Override
	public boolean hasMember(Integer objectId)
	{
		return members.get(objectId) != null;
	}
	
	/**
	 * Adds a new member to the team.<br>
	 * This method updates the internal {@code members} map.<br>
	 * It ensures that the {@code member} is not {@code null}.<br>
	 * It also checks that the member is not already in the team.
	 * @param member The {@code TM} object to add to the team.
	 */
	@Override
	public void addMember(TM member)
	{
		Objects.requireNonNull(member, "Team member should be not null");
		if (!(members.get(member.getObjectId()) == null))
		{
			throw new IllegalStateException("Team member is already added");
		}
		members.put(member.getObjectId(), member);
	}
	
	/**
	 * Removes a specific member from the team.<br>
	 * This method updates the internal members map.<br>
	 * It ensures that the {@code member} is not {@code null}.<br>
	 * It also checks if the member currently exists in the team.
	 * @param member The {@code TM} object to be removed from the team.
	 */
	@Override
	public void removeMember(TM member)
	{
		Objects.requireNonNull(member, "Team member should be not null");
		if (!(members.get(member.getObjectId()) != null))
		{
			throw new IllegalStateException("Team member is already removed");
		}
		members.remove(member.getObjectId());
	}
	
	/**
	 * Removes a team member from the group.<br>
	 * This method uses the {@code objectId} to find and remove the corresponding member.
	 * @param objectId The unique identifier of the object to be removed.
	 */
	@Override
	public void removeMember(Integer objectId)
	{
		removeMember(members.get(objectId));
	}
	
	/**
	 * Checks if all members of the team satisfy a specific condition.<br>
	 * This method iterates through every {@code TM} in the {@code members} map.<br>
	 * It returns early if any member fails to meet the criteria defined by the {@code predicate}.
	 * @param predicate The condition to check against each {@code TM} member.
	 */
	public void apply(Predicate<TM> predicate)
	{
		lock();
		try
		{
			for (TM member : members.values())
			{
				if (!predicate.test(member))
				{
					return;
				}
			}
		}
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Applies a {@code Predicate} to all objects belonging to the team members.<br>
	 * This method iterates through every member and checks their associated object.<br>
	 * It returns early if any object fails to satisfy the condition.
	 * @param predicate The condition to check against each member's object.
	 */
	public void applyOnMembers(Predicate<M> predicate)
	{
		lock();
		try
		{
			for (TM member : members.values())
			{
				if (!predicate.test(member.getObject()))
				{
					return;
				}
			}
		}
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Filters the team members based on a specific condition.<br>
	 * This method uses the provided {@code Predicate} to select items from the internal member list.
	 * @param predicate The condition used to filter the {@code TM} objects.
	 * @return A {@code Collection} containing only the members that match the criteria.
	 */
	@Override
	public Collection<TM> filter(Predicate<TM> predicate)
	{
		return members.values().stream().filter(predicate).collect(Collectors.toList());
	}
	
	/**
	 * Filters the team members based on a specific condition.<br>
	 * This method transforms the internal member objects into type {@code M}.<br>
	 * It returns only those that satisfy the provided {@code predicate}.
	 * @param predicate The condition used to filter the members.
	 * @return A collection of members that match the criteria.
	 */
	@Override
	public Collection<M> filterMembers(Predicate<M> predicate)
	{
		return members.values().stream().map(TRANSFORM_FUNCTION).filter(predicate).collect(Collectors.toList());
	}
	
	/**
	 * Retrieves all members of the team.<br>
	 * This method returns a collection containing every member object.
	 * @return A {@code Collection} of all members of type {@code M}.
	 */
	@Override
	public Collection<M> getMembers()
	{
		return filterMembers(m -> true);
	}
	
	/**
	 * Returns the number of members in this team.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of members currently in the team.
	 */
	@Override
	public int size()
	{
		return members.size();
	}
	
	/**
	 * Retrieves the unique identifier for this team.<br>
	 * This method returns the value stored in {@code getObjectId()}.
	 * @return the {@code Integer} ID of the team.
	 */
	@Override
	public Integer getTeamId()
	{
		return getObjectId();
	}
	
	/**
	 * Retrieves the class name of this team.<br>
	 * This method returns the {@code String} value for {@code GeneralTeam.class.getName()}.
	 * @return The name of the team class as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return GeneralTeam.class.getName();
	}
	
	/**
	 * Retrieves the current leader of the team.<br>
	 * This method returns the {@code TM} object representing the leader.
	 * @return The current leader of the team.
	 */
	public TM getLeader()
	{
		return leader;
	}
	
	/**
	 * Retrieves the {@code AionObject} associated with the team leader.<br>
	 * This method calls {@code getLeader} to find the leader first.
	 * @return The {@code M} object of the current leader.
	 */
	public M getLeaderObject()
	{
		return leader.getObject();
	}
	
	/**
	 * Checks if a specific member is the current leader of the team.<br>
	 * It compares the {@code objectId} of the provided member with the leader's ID.
	 * @param member The {@code M} object to check.
	 * @return {@code true} if the member is the leader, {@code false} otherwise.
	 */
	public boolean isLeader(M member)
	{
		return leader.getObject().getObjectId().equals(member.getObjectId());
	}
	
	/**
	 * Updates the current team leader to a new member.<br>
	 * This method requires that an existing leader is already assigned.<br>
	 * It replaces the old {@code leader} with the provided {@code member}.
	 * @param member The new {@code TM} object to become the team leader.
	 */
	public void changeLeader(TM member)
	{
		Objects.requireNonNull(leader, "Leader should already be set");
		Objects.requireNonNull(member, "New leader should not be null");
		leader = member;
	}
	
	/**
	 * Sets the leader for this team.<br>
	 * This method is only allowed to be called if the current {@code leader} is {@code null}.<br>
	 * It assigns the provided {@code member} as the new team leader.
	 * @param member The {@code TM} object to be set as the leader.
	 */
	protected void setLeader(TM member)
	{
		if (!(leader == null))
		{
			throw new IllegalStateException("Leader should be not initialized");
		}
		Objects.requireNonNull(member, "Leader should not be null");
		leader = member;
	}
	
	/**
	 * Acquires the {@code teamLock} to ensure thread safety.<br>
	 * This method should be used before modifying shared team data.<br>
	 * It prevents multiple threads from accessing the same data simultaneously.
	 */
	protected void lock()
	{
		teamLock.lock();
	}
	
	/**
	 * Releases the {@code teamLock}.<br>
	 * This method should be called after finishing a thread-safe operation.<br>
	 * It allows other threads to access the team data.
	 */
	protected void unlock()
	{
		teamLock.unlock();
	}
	
	private static final class MemberTransformFunction<TM extends TeamMember<M>, M> implements Function<TM, M>
	{
		@Override
		public M apply(TM member)
		{
			return member.getObject();
		}
	}
}
