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

/**
 * This class defines the rewards granted for completing the {@code ShugoEmperorVault} instance.<br>
 * It extends {@link InstanceReward} to handle specific loot logic for this content.
 * @author Lyras
 */
@SuppressWarnings("rawtypes")
public class ShugoEmperorVaultReward extends InstanceReward
{
	private int points;
	private int rank = 7;
	private int keys = 0;
	
	/**
	 * Creates a new {@link ShugoEmperorVaultReward} object.<br>
	 * This constructor initializes the reward with specific map and instance data.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the instance.
	 */
	public ShugoEmperorVaultReward(Integer mapId, int instanceId)
	{
		super(mapId, instanceId);
	}
	
	/**
	 * Adds a specific amount of points to the current total.<br>
	 * This updates the internal {@code points} field.
	 * @param points The number of points to add.
	 */
	public void addPoints(int points)
	{
		this.points += points;
	}
	
	/**
	 * Retrieves the current number of points for this rank.<br>
	 * This value is used to determine the player's standing in the arena.
	 * @return The current {@code int} value of points.
	 */
	public int getPoints()
	{
		return points;
	}
	
	/**
	 * Adds a specific number of keys to the current total.<br>
	 * This method updates the {@code keys} field by adding the provided value.
	 * @param keys The number of keys to add.
	 */
	public void addKeys(int keys)
	{
		this.keys += keys;
	}
	
	/**
	 * Retrieves the current number of keys.<br>
	 * This method returns the value stored in the {@code keys} field.
	 * @return The total count of keys as an {@code int}.
	 */
	public int getKeys()
	{
		return keys;
	}
	
	/**
	 * Updates the current ranking of the player.<br>
	 * This method sets the {@code rank} field to a new value.
	 * @param rank The new integer value for the rank.
	 */
	public void setRank(int rank)
	{
		this.rank = rank;
	}
	
	/**
	 * Retrieves the current rank of the {@code MCEntry}.<br>
	 * This value is used to determine the item's standing.
	 * @return The integer value representing the rank.
	 */
	public int getRank()
	{
		return rank;
	}
}
