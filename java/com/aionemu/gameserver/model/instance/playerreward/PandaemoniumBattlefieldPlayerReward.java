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
 * Represents the rewards granted to players for participating in the Pandaemonium Battlefield.<br>
 * This class extends {@link InstancePlayerReward} to handle specific loot logic for this instance.
 * @author Falke_34
 */
public class PandaemoniumBattlefieldPlayerReward extends InstancePlayerReward
{
	private boolean isRewarded;
	private int premiumFrigidaLegionSupplyBox;
	private int majorFrigidaLegionSupplyBox;
	private int greaterFrigidaLegionSupplyBox;
	private int lesserFrigidaLegionSupplyBox;
	private int minorFrigidaLegionSupplyBox;
	private int premiumFrigidaLegionLootBox;
	private int majorFrigidaLegionLootBox;
	private int greaterFrigidaLegionLootBox;
	private int lesserFrigidaLegionLootBox;
	private int minorFrigidaLegionLootBox;
	
	/**
	 * Creates a new instance of {@link PandaemoniumBattlefieldPlayerReward}.<br>
	 * This constructor initializes the reward state to {@code false}.<br>
	 * It passes the provided object to the superclass constructor.
	 * @param object The underlying data object for this reward.
	 */
	public PandaemoniumBattlefieldPlayerReward(Integer object)
	{
		super(object);
		isRewarded = false;
	}
	
	/**
	 * Checks if the reward has been granted.<br>
	 * This method returns {@code true} if the player has received their reward.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if rewarded, {@code false} otherwise.
	 */
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
	 * Retrieves the count of Premium Frigida Legion Supply Boxes.<br>
	 * This value is part of the {@link PandaemoniumBattlefieldPlayerReward} rewards.
	 * @return The number of premium supply boxes.
	 */
	public int getPremiumFrigidaLegionSupplyBox()
	{
		return premiumFrigidaLegionSupplyBox;
	}
	
	/**
	 * Retrieves the count of Major Frigida Legion Supply Boxes.<br>
	 * This value is part of the {@link PandaemoniumBattlefieldPlayerReward} rewards.
	 * @return The number of major supply boxes.
	 */
	public int getMajorFrigidaLegionSupplyBox()
	{
		return majorFrigidaLegionSupplyBox;
	}
	
	/**
	 * Retrieves the count of Greater Frigida Legion Supply Boxes.<br>
	 * This value is part of the {@link PandaemoniumBattlefieldPlayerReward} rewards.
	 * @return The number of Greater Frigida Legion Supply Boxes.
	 */
	public int getGreaterFrigidaLegionSupplyBox()
	{
		return greaterFrigidaLegionSupplyBox;
	}
	
	/**
	 * Retrieves the count of Lesser Frigida Legion Supply Boxes.<br>
	 * This value is part of the {@link PandaemoniumBattlefieldPlayerReward} rewards.
	 * @return The number of lesser supply boxes.
	 */
	public int getLesserFrigidaLegionSupplyBox()
	{
		return lesserFrigidaLegionSupplyBox;
	}
	
	/**
	 * Retrieves the count of Minor Frigida Legion Supply Boxes.<br>
	 * This value is part of the {@link PandaemoniumBattlefieldPlayerReward} rewards.
	 * @return The number of minor supply boxes.
	 */
	public int getMinorFrigidaLegionSupplyBox()
	{
		return minorFrigidaLegionSupplyBox;
	}
	
	/**
	 * Sets the quantity of premium Frigida Legion Supply Boxes.<br>
	 * This updates the {@code premiumFrigidaLegionSupplyBox} field.
	 * @param premiumFrigidaLegionSupplyBox The number of boxes to set.
	 */
	public void setPremiumFrigidaLegionSupplyBox(int premiumFrigidaLegionSupplyBox)
	{
		this.premiumFrigidaLegionSupplyBox = premiumFrigidaLegionSupplyBox;
	}
	
	/**
	 * Sets the quantity of Major Frigida Legion Supply Boxes.<br>
	 * This updates the {@code majorFrigidaLegionSupplyBox} field.
	 * @param majorFrigidaLegionSupplyBox The number of boxes to set.
	 */
	public void setMajorFrigidaLegionSupplyBox(int majorFrigidaLegionSupplyBox)
	{
		this.majorFrigidaLegionSupplyBox = majorFrigidaLegionSupplyBox;
	}
	
	/**
	 * Sets the quantity of Greater Frigida Legion Supply Boxes.<br>
	 * This updates the {@code greaterFrigidaLegionSupplyBox} field.
	 * @param greaterFrigidaLegionSupplyBox The number of boxes to set.
	 */
	public void setGreaterFrigidaLegionSupplyBox(int greaterFrigidaLegionSupplyBox)
	{
		this.greaterFrigidaLegionSupplyBox = greaterFrigidaLegionSupplyBox;
	}
	
	/**
	 * Sets the quantity of Lesser Frigida Legion Supply Boxes.<br>
	 * This updates the {@code lesserFrigidaLegionSupplyBox} field.
	 * @param lesserFrigidaLegionSupplyBox The number of boxes to set.
	 */
	public void setLesserFrigidaLegionSupplyBox(int lesserFrigidaLegionSupplyBox)
	{
		this.lesserFrigidaLegionSupplyBox = lesserFrigidaLegionSupplyBox;
	}
	
	/**
	 * Sets the quantity of minor Frigida Legion Supply Boxes.<br>
	 * This updates the {@code minorFrigidaLegionSupplyBox} field.
	 * @param minorFrigidaLegionSupplyBox The number of boxes to set.
	 */
	public void setMinorFrigidaLegionSupplyBox(int minorFrigidaLegionSupplyBox)
	{
		this.minorFrigidaLegionSupplyBox = minorFrigidaLegionSupplyBox;
	}
	
	/**
	 * Retrieves the count of Premium Frigida Legion Loot Boxes.<br>
	 * This value is part of the {@link PandaemoniumBattlefieldPlayerReward} reward set.
	 * @return The number of premium loot boxes awarded.
	 */
	public int getPremiumFrigidaLegionLootBox()
	{
		return premiumFrigidaLegionLootBox;
	}
	
	/**
	 * Retrieves the count of Major Frigida Legion Loot Boxes.<br>
	 * This value is part of the {@link PandaemoniumBattlefieldPlayerReward} reward set.
	 * @return The number of major loot boxes awarded to the player.
	 */
	public int getMajorFrigidaLegionLootBox()
	{
		return majorFrigidaLegionLootBox;
	}
	
	/**
	 * Retrieves the count of Greater Frigida Legion Loot Boxes.<br>
	 * This value is part of the {@link PandaemoniumBattlefieldPlayerReward} rewards.
	 * @return The number of Greater Frigida Legion Loot Boxes.
	 */
	public int getGreaterFrigidaLegionLootBox()
	{
		return greaterFrigidaLegionLootBox;
	}
	
	/**
	 * Retrieves the count of Lesser Frigida Legion Loot Boxes.<br>
	 * This value is part of the {@link PandaemoniumBattlefieldPlayerReward} rewards.
	 * @return The number of lesser loot boxes awarded.
	 */
	public int getLesserFrigidaLegionLootBox()
	{
		return lesserFrigidaLegionLootBox;
	}
	
	/**
	 * Retrieves the count of Minor Frigida Legion Loot Boxes.<br>
	 * This value is part of the {@link PandaemoniumBattlefieldPlayerReward} reward data.
	 * @return The number of minor loot boxes awarded to the player.
	 */
	public int getMinorFrigidaLegionLootBox()
	{
		return minorFrigidaLegionLootBox;
	}
	
	/**
	 * Sets the quantity of premium Frigida Legion loot boxes.<br>
	 * This updates the {@code premiumFrigidaLegionLootBox} field.
	 * @param premiumFrigidaLegionLootBox The number of loot boxes to set.
	 */
	public void setPremiumFrigidaLegionLootBox(int premiumFrigidaLegionLootBox)
	{
		this.premiumFrigidaLegionLootBox = premiumFrigidaLegionLootBox;
	}
	
	/**
	 * Sets the quantity of Major Frigida Legion Loot Boxes.<br>
	 * This updates the {@code majorFrigidaLegionLootBox} field.
	 * @param majorFrigidaLegionLootBox The number of loot boxes to set.
	 */
	public void setMajorFrigidaLegionLootBox(int majorFrigidaLegionLootBox)
	{
		this.majorFrigidaLegionLootBox = majorFrigidaLegionLootBox;
	}
	
	/**
	 * Sets the quantity of Greater Frigida Legion Loot Boxes.<br>
	 * This updates the {@code greaterFrigidaLegionLootBox} field.
	 * @param greaterFrigidaLegionLootBox The number of loot boxes to set.
	 */
	public void setGreaterFrigidaLegionLootBox(int greaterFrigidaLegionLootBox)
	{
		this.greaterFrigidaLegionLootBox = greaterFrigidaLegionLootBox;
	}
	
	/**
	 * Sets the amount of Lesser Frigida Legion Loot Boxes.<br>
	 * This updates the {@code lesserFrigidaLegionLootBox} field.
	 * @param lesserFrigidaLegionLootBox The number of loot boxes to set.
	 */
	public void setLesserFrigidaLegionLootBox(int lesserFrigidaLegionLootBox)
	{
		this.lesserFrigidaLegionLootBox = lesserFrigidaLegionLootBox;
	}
	
	/**
	 * Sets the quantity of {@code minorFrigidaLegionLootBox} items.<br>
	 * This updates the reward value for the player.
	 * @param minorFrigidaLegionLootBox The number of loot boxes to set.
	 */
	public void setMinorFrigidaLegionLootBox(int minorFrigidaLegionLootBox)
	{
		this.minorFrigidaLegionLootBox = minorFrigidaLegionLootBox;
	}
}
