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
package com.aionemu.gameserver.model.autogroup;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.taskmanager.AbstractLockManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a player who is currently searching for a party.<br>
 * This class manages the state and synchronization of the {@link Player} during the recruitment process.
 * @author xTz
 */
public class LookingForParty extends AbstractLockManager
{
	private final List<SearchInstance> searchInstances = new ArrayList<>();
	private Player player;
	private long startEnterTime;
	private long penaltyTime;
	
	/**
	 * Creates a new {@code LookingForParty} object for a specific player.<br>
	 * This method initializes the search instance with the provided mask and request type.
	 * @param player The {@link Player} who is looking for a party.
	 * @param instanceMaskId The unique identifier for the instance mask.
	 * @param ert The type of entry request being made.
	 */
	public LookingForParty(Player player, int instanceMaskId, EntryRequestType ert)
	{
		this.player = player;
		searchInstances.add(new SearchInstance(instanceMaskId, ert, ert.isGroupEntry() ? player.getPlayerGroup2().getOnlineMembers() : null));
	}
	
	/**
	 * Removes a specific instance from the current search list.<br>
	 * This method searches for an entry matching the provided {@code instanceMaskId}.<br>
	 * It returns the total number of instances remaining in the list.
	 * @param instanceMaskId The unique identifier of the instance to remove.
	 * @return The new size of the {@code searchInstances} list.
	 */
	public int unregisterInstance(int instanceMaskId)
	{
		super.writeLock();
		try
		{
			for (SearchInstance si : searchInstances)
			{
				if (si.getInstanceMaskId() == instanceMaskId)
				{
					searchInstances.remove(si);
					return searchInstances.size();
				}
			}
			
			return searchInstances.size();
		}
		finally
		{
			super.writeUnlock();
		}
	}
	
	/**
	 * Retrieves a list of all current {@link SearchInstance} objects.<br>
	 * This method returns a new copy of the internal list to prevent direct modification.
	 * @return A {@code List} containing all active {@code SearchInstance} objects.
	 */
	public List<SearchInstance> getSearchInstances()
	{
		final List<SearchInstance> tempList = new ArrayList<>();
		for (SearchInstance si : searchInstances)
		{
			tempList.add(si);
		}
		
		return tempList;
	}
	
	/**
	 * Adds a new instance mask to the search list.<br>
	 * This method creates a {@code SearchInstance} and stores it in the internal list.<br>
	 * It uses the provided {@code EntryRequestType} to determine group membership.
	 * @param instanceMaskId The unique identifier for the instance mask.
	 * @param ert The type of entry request being made.
	 */
	public void addInstanceMaskId(int instanceMaskId, EntryRequestType ert)
	{
		super.writeLock();
		try
		{
			searchInstances.add(new SearchInstance(instanceMaskId, ert, ert.isGroupEntry() ? player.getPlayerGroup2().getOnlineMembers() : null));
		}
		finally
		{
			super.writeUnlock();
		}
	}
	
	/**
	 * Retrieves a specific {@link SearchInstance} based on its ID.<br>
	 * This method searches through the internal list of instances.<br>
	 * It returns {@code null} if no matching instance is found.
	 * @param instanceMaskId The unique identifier for the search instance to find.
	 * @return The corresponding {@code SearchInstance} object or {@code null}.
	 */
	public SearchInstance getSearchInstance(int instanceMaskId)
	{
		super.readLock();
		try
		{
			for (SearchInstance si : searchInstances)
			{
				if (si.getInstanceMaskId() == instanceMaskId)
				{
					return si;
				}
			}
			
			return null;
		}
		finally
		{
			super.readUnlock();
		}
	}
	
	/**
	 * Checks if a specific instance is currently registered.<br>
	 * It searches through the list of search instances.
	 * @param instanceMaskId The unique ID of the instance to check.
	 * @return {@code true} if the instance exists, otherwise {@code false}.
	 */
	public boolean isRegistredInstance(int instanceMaskId)
	{
		for (SearchInstance si : searchInstances)
		{
			if (si.getInstanceMaskId() == instanceMaskId)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the {@link Player} who initiated this wedding.<br>
	 * This method returns the primary participant of the ceremony.
	 * @return The {@code Player} object representing the main character.
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Sets the {@link Player} associated with this task.<br>
	 * This updates the internal {@code player} field.
	 * @param player The {@code Player} object to set.
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Sets the current system time as the {@code penaltyTime}.<br>
	 * This method records when a penalty was applied.<br>
	 * It updates the internal state of the {@link LookingForParty} instance.
	 */
	public void setPenaltyTime()
	{
		penaltyTime = System.currentTimeMillis();
	}
	
	/**
	 * Checks if the player is currently under a penalty.<br>
	 * It determines this by comparing the current time to the {@code penaltyTime}.<br>
	 * The method returns {@code true} if the penalty is still active.
	 * @return {@code true} if there is an active penalty, otherwise {@code false}.
	 */
	public boolean hasPenalty()
	{
		return (System.currentTimeMillis() - penaltyTime) <= 10000;
	}
	
	/**
	 * Sets the current system time as the start entry time.<br>
	 * This method updates the {@code startEnterTime} field using {@code System.currentTimeMillis()}.
	 */
	public void setStartEnterTime()
	{
		startEnterTime = System.currentTimeMillis();
	}
	
	/**
	 * Checks if the player is currently in the starting entry task.<br>
	 * It verifies if the time elapsed since {@code startEnterTime} is less than or equal to 120000 milliseconds.
	 * @return {@code true} if the player is still on the task, otherwise {@code false}.
	 */
	public boolean isOnStartEnterTask()
	{
		return (System.currentTimeMillis() - startEnterTime) <= 120000;
	}
}
