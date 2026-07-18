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
package com.aionemu.gameserver.model.instance.playerreward;

/**
 * Represents a reward granted to a player for completing an instance.<br>
 * This model stores the specific items or benefits associated with instance completion.
 * @author xTz
 */
public class InstancePlayerReward
{
	private int points;
	private int playerPvPKills;
	private int playerMonsterKills;
	protected Integer object;
	
	/**
	 * Creates a new {@link InstancePlayerReward} instance.<br>
	 * This constructor initializes the reward with a specific object.
	 * @param object The {@code Integer} value to assign to this reward.
	 */
	public InstancePlayerReward(Integer object)
	{
		this.object = object;
	}
	
	/**
	 * Retrieves the owner of this reward.<br>
	 * This method returns the {@code Integer} value stored in the internal object field.
	 * @return The owner as an {@code Integer}, or {@code null} if no owner is assigned.
	 */
	public Integer getOwner()
	{
		return object;
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
	 * Retrieves the total number of Player vs Player (PvP) kills.<br>
	 * This value is stored in the {@code playerPvPKills} field.
	 * @return The count of PvP kills as an {@code int}.
	 */
	public int getPvPKills()
	{
		return playerPvPKills;
	}
	
	/**
	 * Retrieves the total number of monsters killed by the player.<br>
	 * This value is stored in the {@code playerMonsterKills} field.
	 * @return The count of monster kills as an {@code int}.
	 */
	public int getMonsterKills()
	{
		return playerMonsterKills;
	}
	
	/**
	 * Adds a specific amount of points to the current total.<br>
	 * This updates the internal {@code points} field.
	 * @param points The number of points to add.
	 */
	public void addPoints(int points)
	{
		this.points += points;
		if (this.points < 0)
		{
			this.points = 0;
		}
	}
	
	/**
	 * Increments the player's PvP kill count.<br>
	 * This method updates the {@code playerPvPKills} field by adding 1 to its current value.
	 */
	public void addPvPKillToPlayer()
	{
		playerPvPKills++;
	}
	
	/**
	 * Increments the monster kill count for the player.<br>
	 * This method updates the {@code playerMonsterKills} field.
	 */
	public void addMonsterKillToPlayer()
	{
		playerMonsterKills++;
	}
}
