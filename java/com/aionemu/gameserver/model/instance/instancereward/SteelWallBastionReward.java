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

import com.aionemu.gameserver.model.instance.playerreward.SteelWallBastionPlayerReward;

/**
 * This class represents the rewards granted for completing the Steel Wall Bastion instance.<br>
 * It manages a collection of {@link SteelWallBastionPlayerReward} objects.
 * @author Romanz
 */
public class SteelWallBastionReward extends InstanceReward<SteelWallBastionPlayerReward>
{
	private int points;
	private int npcKills;
	private int rank = 7;
	private int basicAP;
	private int powerfulBundlewater;
	private int ceraniumMedal;
	private int powerfulBundleessence;
	private int largeBundlewater;
	private int largeBundleessence;
	private int smallBundlewater;
	private boolean isRewarded = false;
	
	/**
	 * Creates a new {@link SteelWallBastionReward} object.<br>
	 * This constructor initializes the reward with specific map and instance data.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the instance.
	 */
	public SteelWallBastionReward(Integer mapId, int instanceId)
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
	 * Retrieves the current amount of {@code ceraniumMedal}.<br>
	 * This value represents the medals earned in this reward instance.
	 * @return The number of {@code ceraniumMedal} items.
	 */
	public int getCeramiumMedal()
	{
		return ceraniumMedal;
	}
	
	/**
	 * Retrieves the amount of {@code powerfulBundlewater} earned.<br>
	 * This value is part of the reward for the {@link SteelWallBastionReward} instance.
	 * @return The total count of {@code powerfulBundlewater}.
	 */
	public int getPowerfulBundleWater()
	{
		return powerfulBundlewater;
	}
	
	/**
	 * Retrieves the amount of essence from a powerful bundle.<br>
	 * This value is stored in the {@code powerfulBundleessence} field.
	 * @return The number of powerful bundle essences.
	 */
	public int getPowerfulBundleEssence()
	{
		return powerfulBundleessence;
	}
	
	/**
	 * Retrieves the amount of {@code largeBundlewater} for this reward.<br>
	 * This value is used to determine the quantity granted to players.
	 * @return The number of {@code largeBundlewater} items.
	 */
	public int getLargeBundleWater()
	{
		return largeBundlewater;
	}
	
	/**
	 * Retrieves the amount of essence from a large bundle.<br>
	 * This value is stored in the {@code largeBundleessence} field.
	 * @return The number of large bundle essences.
	 */
	public int getLargeBundleEssence()
	{
		return largeBundleessence;
	}
	
	/**
	 * Retrieves the amount of small bundle water.<br>
	 * This value is part of the {@link SteelWallBastionReward} rewards.
	 * @return The number of small bundle water items.
	 */
	public int getSmallBundleWater()
	{
		return smallBundlewater;
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
	 * Sets the amount of {@code powerfulBundlewater} for this reward.<br>
	 * This updates the internal value used by {@code getPowerfulBundleWater}.
	 * @param powerfulBundlewater The number of water units to set.
	 */
	public void setPowerfulBundleWater(int powerfulBundlewater)
	{
		this.powerfulBundlewater = powerfulBundlewater;
	}
	
	/**
	 * Sets the amount of {@code powerfulBundleessence} for this reward.<br>
	 * This updates the internal value used by the {@link SteelWallBastionReward} class.
	 * @param powerfulBundleessence The number of essence to set.
	 */
	public void setPowerfulBundleEssence(int powerfulBundleessence)
	{
		this.powerfulBundleessence = powerfulBundleessence;
	}
	
	/**
	 * Sets the amount of {@code largeBundlewater} for this reward.<br>
	 * This updates the internal value used by {@code getLargeBundleWater}.
	 * @param largeBundlewater The number of water units to set.
	 */
	public void setLargeBundleWater(int largeBundlewater)
	{
		this.largeBundlewater = largeBundlewater;
	}
	
	/**
	 * Sets the amount of essence for the large bundle.<br>
	 * This updates the {@code largeBundleessence} field in this object.
	 * @param largeBundleessence The number of essence to set.
	 */
	public void setLargeBundleEssence(int largeBundleessence)
	{
		this.largeBundleessence = largeBundleessence;
	}
	
	/**
	 * Sets the amount of small bundle water for this reward.<br>
	 * This updates the {@code smallBundlewater} field.
	 * @param smallBundlewater The number of small bundle water to set.
	 */
	public void setSmallBundleWater(int smallBundlewater)
	{
		this.smallBundlewater = smallBundlewater;
	}
}
