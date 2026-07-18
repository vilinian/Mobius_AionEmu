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

import com.aionemu.gameserver.model.instance.playerreward.RiftOfOblivionPlayerReward;

/**
 * This class represents the rewards granted for completing the Rift of Oblivion instance.<br>
 * It extends {@link InstanceReward} and specifically handles {@link RiftOfOblivionPlayerReward} data.
 * @author Falke_34
 */
public class RiftOfOblivionReward extends InstanceReward<RiftOfOblivionPlayerReward>
{
	private int points;
	private int npcKills;
	private int rank;
	private int icyOrbOfMemory;
	private boolean isRewarded;
	
	/**
	 * Creates a new {@code RiftOfOblivionReward} object.<br>
	 * This constructor initializes the reward with specific map and instance details.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the instance.
	 */
	public RiftOfOblivionReward(Integer mapId, int instanceId)
	{
		super(mapId, instanceId);
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
	
	/**
	 * Retrieves the current amount of {@code Icy Orb of Memory}.<br>
	 * This value is part of the {@link RiftOfOblivionReward} data.
	 * @return The total number of {@code Icy Orb of Memory} items.
	 */
	public int getIcyOrbOfMemory()
	{
		return icyOrbOfMemory;
	}
	
	/**
	 * Increments the total number of NPC kills.<br>
	 * This updates the {@code npcKills} counter for the current reward instance.
	 */
	public void addNpcKill()
	{
		npcKills++;
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
	 * Retrieves the current number of points for this rank.<br>
	 * This value is used to determine the player's standing in the arena.
	 * @return The current {@code int} value of points.
	 */
	public int getPoints()
	{
		return points;
	}
	
	/**
	 * Retrieves the total number of NPCs killed.<br>
	 * This value is updated by calling {@code addNpcKill}.
	 * @return The current count of NPC kills as an {@code int}.
	 */
	public int getNpcKills()
	{
		return npcKills;
	}
	
	/**
	 * Marks the reward as successfully granted.<br>
	 * This sets the {@code isRewarded} flag to {@code true}.
	 */
	public void setRewarded()
	{
		isRewarded = true;
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
	 * Checks if the reward has been granted.<br>
	 * This method returns {@code true} if the player has received their reward.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if rewarded, {@code false} otherwise.
	 */
	@Override
	public boolean isRewarded()
	{
		return isRewarded;
	}
	
	/**
	 * Updates the amount of {@code icyOrbOfMemory} for this reward.<br>
	 * This method sets the value to the provided {@code int}.
	 * @param icyOrbOfMemory The new quantity of memory orbs.
	 */
	public void setIcyOrbOfMemory(int icyOrbOfMemory)
	{
		this.icyOrbOfMemory = icyOrbOfMemory;
	}
}
