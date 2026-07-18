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
package com.aionemu.gameserver.model.instance.instancereward;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;

/**
 * Represents a reward granted to players upon completing an instance.<br>
 * This class serves as a generic container for various types of {@link InstancePlayerReward}.
 * @author xTz
 * @param <T>
 */
public class InstanceReward<T extends InstancePlayerReward>
{
	protected List<T> instanceRewards = new ArrayList<>();
	private InstanceScoreType instanceScoreType = InstanceScoreType.START_PROGRESS;
	protected Integer mapId;
	protected int instanceId;
	
	/**
	 * Creates a new {@link InstanceReward} object.<br>
	 * This constructor initializes the reward with specific location data.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the instance.
	 */
	public InstanceReward(Integer mapId, int instanceId)
	{
		this.mapId = mapId;
		this.instanceId = instanceId;
	}
	
	/**
	 * Retrieves the list of rewards for this instance.<br>
	 * This method returns the {@code List} containing all reward objects.
	 * @return A {@link List} of items of type {@code T}.
	 */
	public List<T> getInstanceRewards()
	{
		return instanceRewards;
	}
	
	/**
	 * Checks if a specific player is associated with any rewards in this instance.<br>
	 * It iterates through the {@code instanceRewards} list to find a match.
	 * @param object The unique identifier of the player to check.
	 * @return {@code true} if the player has a reward, otherwise {@code false}.
	 */
	public boolean containPlayer(Integer object)
	{
		for (InstancePlayerReward instanceReward : instanceRewards)
		{
			if (instanceReward.getOwner().equals(object))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes a specific reward from the list of instance rewards.<br>
	 * This method checks if the {@code reward} exists before trying to delete it.<br>
	 * If the item is not found, no action is taken.
	 * @param reward The {@code T} type reward object to be removed.
	 */
	public void removePlayerReward(T reward)
	{
		if (instanceRewards.contains(reward))
		{
			instanceRewards.remove(reward);
		}
	}
	
	/**
	 * Retrieves the reward for a specific player.<br>
	 * It searches through all rewards in this instance.<br>
	 * The method compares the owner of each reward to the provided {@code object}.
	 * @param object The unique identifier of the player to look up.
	 * @return The {@link InstancePlayerReward} for the player, or {@code null} if no reward is found.
	 */
	public InstancePlayerReward getPlayerReward(Integer object)
	{
		for (InstancePlayerReward instanceReward : instanceRewards)
		{
			if (instanceReward.getOwner().equals(object))
			{
				return instanceReward;
			}
		}
		
		return null;
	}
	
	/**
	 * Adds a new reward to the instance rewards list.<br>
	 * This method updates the internal {@code instanceRewards} collection.
	 * @param reward The {@code InstancePlayerReward} object to add.
	 */
	public void addPlayerReward(T reward)
	{
		instanceRewards.add(reward);
	}
	
	/**
	 * Sets the score type for this reward instance.<br>
	 * This updates the internal {@code instanceScoreType} field.
	 * @param instanceScoreType The new {@link InstanceScoreType} to assign.
	 */
	public void setInstanceScoreType(InstanceScoreType instanceScoreType)
	{
		this.instanceScoreType = instanceScoreType;
	}
	
	/**
	 * Retrieves the current score type for this instance.<br>
	 * This value determines how progress is tracked.
	 * @return the {@code InstanceScoreType} of this reward.
	 */
	public InstanceScoreType getInstanceScoreType()
	{
		return instanceScoreType;
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * This value corresponds to the {@code mapId} field.
	 * @return The {@code Integer} ID of the map.
	 */
	public Integer getMapId()
	{
		return mapId;
	}
	
	/**
	 * Retrieves the unique identifier for this instance.<br>
	 * This ID was provided during the construction of {@link CollisionResults}.
	 * @return The {@code int} value representing the instance ID.
	 */
	public int getInstanceId()
	{
		return instanceId;
	}
	
	/**
	 * Checks if the reward has been granted.<br>
	 * This method returns {@code true} if the player has received their reward.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if rewarded, {@code false} otherwise.
	 */
	public boolean isRewarded()
	{
		return instanceScoreType.isEndProgress();
	}
	
	/**
	 * Checks if the current instance score type is in the preparing state.<br>
	 * This method delegates the check to the {@code isPreparing} method.
	 * @return {@code true} if the status is preparing, otherwise {@code false}.
	 */
	public boolean isPreparing()
	{
		return instanceScoreType.isPreparing();
	}
	
	/**
	 * Checks if the current instance score type is {@code START_PROGRESS}.<br>
	 * This method returns {@code true} if it matches, otherwise it returns {@code false}.
	 * @return {@code true} if this is a start progress type, {@code false} otherwise.
	 */
	public boolean isStartProgress()
	{
		return instanceScoreType.isStartProgress();
	}
	
	/**
	 * Removes all rewards from the internal list.<br>
	 * The {@code instanceRewards} collection will be empty after this call.
	 */
	public void clear()
	{
		instanceRewards.clear();
	}
	
	/**
	 * Returns the current {@code InstanceReward} object.<br>
	 * This method returns the current instance of the class.
	 * @return The current {@code InstanceReward} object.
	 */
	protected InstanceReward<?> getInstanceReward()
	{
		return this;
	}
}
