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

import com.aionemu.gameserver.model.instance.playerreward.ArgentManorPlayerReward;

/**
 * This class defines the reward structure for the {@code ArgentManor} instance.<br>
 * It manages rewards specifically granted to players upon completing this content.<br>
 * It extends {@link InstanceReward} using {@link ArgentManorPlayerReward} as its specific reward type.
 * @author Falke_34
 */
public class ArgentManorReward extends InstanceReward<ArgentManorPlayerReward>
{
	private int points;
	private int npcKills;
	private int rank;
	private int scoreAP;
	private int lesserBox;
	private int seniorBox;
	private int intermediateBox;
	private boolean isRewarded;
	
	/**
	 * Creates a new {@link ArgentManorReward} object.<br>
	 * This constructor initializes the reward with specific map and instance data.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the instance.
	 */
	public ArgentManorReward(Integer mapId, int instanceId)
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
	 * Retrieves the number of intermediate boxes earned.<br>
	 * This value is stored in the {@code intermediateBox} field.
	 * @return The current count of intermediate boxes as an {@code int}.
	 */
	public int getIntermediateBox()
	{
		return intermediateBox;
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
	 * Updates the number of senior boxes for this reward.<br>
	 * This method sets the {@code seniorBox} field to a new value.
	 * @param seniorBox The new count of senior boxes to set.
	 */
	public void setSeniorBox(int seniorBox)
	{
		this.seniorBox = seniorBox;
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
	 * Retrieves the number of senior boxes earned.<br>
	 * This value is stored in the {@code seniorBox} field.
	 * @return The total count of senior boxes as an {@code int}.
	 */
	public int getSeniorBox()
	{
		return seniorBox;
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
	 * Retrieves the current AP score.<br>
	 * This value represents the points earned in the {@link ArgentManorReward} instance.
	 * @return The current {@code int} score for AP.
	 */
	public int getScoreAP()
	{
		return scoreAP;
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
	 * Sets the number of lesser boxes for this reward.<br>
	 * This updates the {@code lesserBox} field.
	 * @param lesserBox The amount of lesser boxes to set.
	 */
	public void setLesserBox(int lesserBox)
	{
		this.lesserBox = lesserBox;
	}
	
	/**
	 * Updates the {@code scoreAP} value.<br>
	 * This method sets the current score for AP points.
	 * @param ap The new integer value to assign to {@code scoreAP}.
	 */
	public void setScoreAP(int ap)
	{
		scoreAP = ap;
	}
	
	/**
	 * Retrieves the number of lesser boxes obtained.<br>
	 * This value is stored in the {@code lesserBox} field.
	 * @return The total count of lesser boxes as an {@code int}.
	 */
	public int getLesserBox()
	{
		return lesserBox;
	}
	
	/**
	 * Sets the number of intermediate boxes for this reward.<br>
	 * This updates the {@code intermediateBox} field.
	 * @param intermediateBox The new value to set for the intermediate box count.
	 */
	public void setIntermediateBox(int intermediateBox)
	{
		this.intermediateBox = intermediateBox;
	}
}
