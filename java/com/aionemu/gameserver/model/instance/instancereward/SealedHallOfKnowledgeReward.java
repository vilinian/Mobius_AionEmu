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

import com.aionemu.gameserver.model.instance.playerreward.SealedHallOfKnowledgePlayerReward;

/**
 * Represents the rewards granted for completing the {@code SealedHallOfKnowledge} instance.<br>
 * This class handles the distribution of {@link SealedHallOfKnowledgePlayerReward} objects to players.
 * @author Eloann
 */
public class SealedHallOfKnowledgeReward extends InstanceReward<SealedHallOfKnowledgePlayerReward>
{
	private int points;
	private int npcKills;
	private int rank = 7;
	private int basicAP;
	private int sillusCrest;
	private int ceraniumMedal;
	private int favorableBundle;
	private int ceraniumFragments;
	private int valorBundle;
	private int mithrilMedal;
	private boolean isRewarded = false;
	
	/**
	 * Creates a new {@link SealedHallOfKnowledgeReward} object.<br>
	 * This constructor initializes the reward with specific map and instance data.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the instance.
	 */
	public SealedHallOfKnowledgeReward(Integer mapId, int instanceId)
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
	 * Increments the total number of NPC kills.<br>
	 * This updates the {@code npcKills} counter for the current reward instance.
	 */
	public void addNpcKill()
	{
		npcKills++;
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
	 * Marks the reward as successfully granted.<br>
	 * This sets the {@code isRewarded} flag to {@code true}.
	 */
	public void setRewarded()
	{
		isRewarded = true;
	}
	
	/**
	 * Retrieves the amount of Basic AP for this reward.<br>
	 * This value is stored in the {@code basicAP} field.
	 * @return The current number of Basic AP as an {@code int}.
	 */
	public int getBasicAP()
	{
		return basicAP;
	}
	
	/**
	 * Sets the basic AP value for this reward.<br>
	 * This updates the {@code basicAP} field with a new integer value.
	 * @param ap The amount of basic AP to set.
	 */
	public void setBasicAP(int ap)
	{
		basicAP = ap;
	}
	
	/**
	 * Retrieves the current amount of {@code sillusCrest}.<br>
	 * This value represents a specific reward for the instance.
	 * @return The number of {@code sillusCrest} items.
	 */
	public int getSillusCrest()
	{
		return sillusCrest;
	}
	
	/**
	 * Retrieves the current amount of {@code ceraniumMedal}.<br>
	 * This value represents the medals earned in this reward instance.
	 * @return The number of {@code ceraniumMedal} items.
	 */
	public int getCeramiumMedal()
	{
		return ceraniumMedal;
	}
	
	/**
	 * Retrieves the number of favorable bundles awarded.<br>
	 * This value is stored in the {@code favorableBundle} field.
	 * @return The total count of favorable bundles.
	 */
	public int getFavorableBundle()
	{
		return favorableBundle;
	}
	
	/**
	 * Retrieves the current number of ceramium fragments.<br>
	 * This value is part of the {@link SealedHallOfKnowledgeReward} data.
	 * @return The total count of ceramium fragments.
	 */
	public int getCeramiumFragments()
	{
		return ceraniumFragments;
	}
	
	/**
	 * Retrieves the current amount of {@code valorBundle}.<br>
	 * This value represents the reward quantity for this specific item.
	 * @return The number of {@code valorBundle} items.
	 */
	public int getValorBundle()
	{
		return valorBundle;
	}
	
	/**
	 * Retrieves the current number of Mithril Medals.<br>
	 * This value is part of the {@link SealedHallOfKnowledgeReward} data.
	 * @return The total count of Mithril Medals as an {@code int}.
	 */
	public int getMithrilMedal()
	{
		return mithrilMedal;
	}
	
	/**
	 * Sets the number of {@code sillusCrest} for this reward.<br>
	 * This updates the internal value used by {@code getSillusCrest}.
	 * @param sillusCrest The amount of {@code sillusCrest} to set.
	 */
	public void setSillusCrest(int sillusCrest)
	{
		this.sillusCrest = sillusCrest;
	}
	
	/**
	 * Updates the number of {@code ceraniumMedal} for this reward.<br>
	 * This method sets the internal value used by {@code getCeramiumMedal}.
	 * @param ceraniumMedal The new amount of medals to set.
	 */
	public void setCeramiumMedal(int ceraniumMedal)
	{
		this.ceraniumMedal = ceraniumMedal;
	}
	
	/**
	 * Sets the number of favorable bundles for this reward.<br>
	 * This updates the {@code favorableBundle} field.
	 * @param favorableBundle The amount of favorable bundles to set.
	 */
	public void setFavorableBundle(int favorableBundle)
	{
		this.favorableBundle = favorableBundle;
	}
	
	/**
	 * Updates the number of {@code ceraniumFragments} for this reward.<br>
	 * This method sets the internal value used by {@code getCeraniumFragments}.
	 * @param ceraniumFragments The new amount of fragments to set.
	 */
	public void setCeramiumFragments(int ceraniumFragments)
	{
		this.ceraniumFragments = ceraniumFragments;
	}
	
	/**
	 * Sets the number of {@code valorBundle} items.<br>
	 * This updates the internal value for this reward instance.
	 * @param valorBundle The amount of {@code valorBundle} to set.
	 */
	public void setValorBundle(int valorBundle)
	{
		this.valorBundle = valorBundle;
	}
	
	/**
	 * Updates the number of {@code mithrilMedal} for this reward.<br>
	 * This method sets the value to the provided integer.
	 * @param mithrilMedal The new amount of medals to set.
	 */
	public void setMithrilMedal(int mithrilMedal)
	{
		this.mithrilMedal = mithrilMedal;
	}
}
