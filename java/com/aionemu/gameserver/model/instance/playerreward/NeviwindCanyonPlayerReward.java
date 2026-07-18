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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceBuff;

/**
 * This class defines the specific rewards granted to a {@link Player} for completing the Neviwind Canyon instance.<br>
 * It extends {@link InstancePlayerReward} to handle reward logic unique to this content.
 * @author Falke_34
 */
public class NeviwindCanyonPlayerReward extends InstancePlayerReward
{
	private int timeBonus;
	private long logoutTime;
	private final float timeBonusModifier;
	private final Race race;
	private int rewardAp;
	private int rewardGp;
	private int rewardExp;
	private int bonusAp;
	private int bonusGp;
	private int bonusExp;
	private int brokenSpinel;
	private float rewardCount;
	private int idEternityWarStigma;
	private int coinIdEternityWar01;
	private int cashMinionContract01;
	private InstanceBuff boostMorale;
	
	/**
	 * Creates a new reward instance for the Neviwind Canyon event.<br>
	 * This constructor initializes the base object and sets the time bonus.<br>
	 * It also calculates the modifier based on the provided time.
	 * @param object The unique identifier for the reward object.
	 * @param timeBonus The total time bonus value to be applied.
	 * @param race The {@link Race} of the player receiving the reward.
	 */
	public NeviwindCanyonPlayerReward(Integer object, int timeBonus, Race race)
	{
		super(object);
		this.timeBonus = timeBonus;
		timeBonusModifier = ((float) this.timeBonus / (float) 660000);
		this.race = race;
	}
	
	/**
	 * Calculates the current participation rate.<br>
	 * It divides the {@code getTimeBonus()} value by the total {@code timeBonus}.
	 * @return The calculated participation as a {@code float}.
	 */
	public float getParticipation()
	{
		return (float) getTimeBonus() / timeBonus;
	}
	
	/**
	 * Calculates the total score points for the player.<br>
	 * This method adds the {@code timeBonus} to the value returned by {@code getPoints}.
	 * @return The sum of the time bonus and base points as an {@code int}.
	 */
	public int getScorePoints()
	{
		return timeBonus + getPoints();
	}
	
	/**
	 * Retrieves the current time bonus value.<br>
	 * It ensures that a negative value is never returned.
	 * @return The {@code int} value of the time bonus or {@code 0}.
	 */
	public int getTimeBonus()
	{
		return timeBonus > 0 ? timeBonus : 0;
	}
	
	/**
	 * Updates the {@code logoutTime} field.<br>
	 * This method sets the value to the current system time in milliseconds.
	 */
	public void updateLogOutTime()
	{
		logoutTime = System.currentTimeMillis();
	}
	
	/**
	 * This method updates the {@code timeBonus} value based on elapsed time.<br>
	 * It calculates the difference between the current system time and {@code logoutTime}.<br>
	 * The result is then subtracted from the current bonus using the {@code timeBonusModifier}.
	 */
	public void updateBonusTime()
	{
		final int offlineTime = (int) (System.currentTimeMillis() - logoutTime);
		timeBonus -= offlineTime * timeBonusModifier;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the total number of rewards.<br>
	 * This method converts the internal {@code rewardCount} value to an {@code int}.
	 * @return The count of rewards as an {@code int}.
	 */
	public int getRewardCount()
	{
		return (int) rewardCount;
	}
	
	/**
	 * Retrieves the number of broken spinels awarded.<br>
	 * This value is stored in the {@code brokenSpinel} field.
	 * @return The total count of broken spinels as an {@code int}.
	 */
	public int getBrokenSpinel()
	{
		return brokenSpinel;
	}
	
	/**
	 * Retrieves the unique identifier for the Eternity War Stigma.<br>
	 * This value is used to identify specific stigma rewards.
	 * @return The {@code int} ID of the Eternity War Stigma.
	 */
	public int getIDEternityWarStigma()
	{
		return idEternityWarStigma;
	}
	
	/**
	 * Retrieves the unique identifier for the Eternity War 01 coin.<br>
	 * This value is stored in the {@code coinIdEternityWar01} field.
	 * @return The integer ID of the coin.
	 */
	public int getCoinIdEternityWar01()
	{
		return coinIdEternityWar01;
	}
	
	/**
	 * Retrieves the amount of {@code cashMinionContract01} for this reward.<br>
	 * This value represents a specific contract quantity.
	 * @return The number of {@code cashMinionContract01} items.
	 */
	public int getCashMinionContract01()
	{
		return cashMinionContract01;
	}
	
	/**
	 * Sets the amount of {@code brokenSpinel} for this reward.<br>
	 * This updates the internal value used by {@code getBrokenSpinel}.
	 * @param brokenSpinel The number of broken spinels to set.
	 */
	public void setBrokenSpinel(int brokenSpinel)
	{
		this.brokenSpinel = brokenSpinel;
	}
	
	/**
	 * Sets the unique identifier for the Eternity War Stigma.<br>
	 * This updates the {@code idEternityWarStigma} field in this reward object.
	 * @param idEternityWarStigma The new ID to assign to the Eternity War Stigma.
	 */
	public void setIDEternityWarStigma(int idEternityWarStigma)
	{
		this.idEternityWarStigma = idEternityWarStigma;
	}
	
	/**
	 * Sets the unique identifier for the first Eternity War coin.<br>
	 * This value is used to track specific rewards in the {@link NeviwindCanyonPlayerReward} instance.
	 * @param coinIdEternityWar01 The {@code int} ID of the coin to set.
	 */
	public void setCoinIdEternityWar01(int coinIdEternityWar01)
	{
		this.coinIdEternityWar01 = coinIdEternityWar01;
	}
	
	/**
	 * Sets the amount for the first cash minion contract.<br>
	 * This updates the {@code cashMinionContract01} field in this reward object.
	 * @param cashMinionContract01 The new value to set for the contract.
	 */
	public void setCashMinionContract01(int cashMinionContract01)
	{
		this.cashMinionContract01 = cashMinionContract01;
	}
	
	/**
	 * Sets the total number of rewards for this instance.<br>
	 * This updates the {@code rewardCount} field.
	 * @param rewardCount The new amount of rewards to set.
	 */
	public void setRewardCount(float rewardCount)
	{
		this.rewardCount = rewardCount;
	}
	
	/**
	 * Retrieves the amount of AP awarded to the player.<br>
	 * This value is stored in the {@code rewardAp} field.
	 * @return The total amount of AP as an {@code int}.
	 */
	public int getRewardAp()
	{
		return rewardAp;
	}
	
	/**
	 * Sets the amount of AP rewarded to the player.<br>
	 * This updates the {@code rewardAp} field in this object.
	 * @param rewardAp The amount of AP to set.
	 */
	public void setRewardAp(int rewardAp)
	{
		this.rewardAp = rewardAp;
	}
	
	/**
	 * Retrieves the additional AP amount for this reward.<br>
	 * This value is used to calculate total rewards.
	 * @return The current {@code bonusAp} value.
	 */
	public int getBonusAp()
	{
		return bonusAp;
	}
	
	/**
	 * Sets the extra AP amount for the player.<br>
	 * This updates the {@code bonusAp} field in this reward object.
	 * @param bonusAp The amount of additional AP to assign.
	 */
	public void setBonusAp(int bonusAp)
	{
		this.bonusAp = bonusAp;
	}
	
	/**
	 * Retrieves the amount of gold pieces awarded.<br>
	 * This value is stored in the {@code rewardGp} field.
	 * @return The total gold pieces as an {@code int}.
	 */
	public int getRewardGp()
	{
		return rewardGp;
	}
	
	/**
	 * Sets the amount of gold pieces for the reward.<br>
	 * This updates the {@code rewardGp} field in this object.
	 * @param rewardGp The amount of gold pieces to set.
	 */
	public void setRewardGp(int rewardGp)
	{
		this.rewardGp = rewardGp;
	}
	
	/**
	 * Retrieves the extra gold points granted to the player.<br>
	 * This value is calculated based on the {@code bonusGp} field.
	 * @return The amount of bonus gold points as an {@code int}.
	 */
	public int getBonusGp()
	{
		return bonusGp;
	}
	
	/**
	 * Sets the extra gold pieces for this reward.<br>
	 * This updates the {@code bonusGp} field.
	 * @param bonusGp The amount of gold to add as a bonus.
	 */
	public void setBonusGp(int bonusGp)
	{
		this.bonusGp = bonusGp;
	}
	
	/**
	 * Retrieves the experience points awarded to the player.<br>
	 * This value is stored in the {@code rewardExp} field.
	 * @return The amount of experience points as an {@code int}.
	 */
	public int getRewardExp()
	{
		return rewardExp;
	}
	
	/**
	 * Sets the experience points for the player reward.<br>
	 * This updates the {@code rewardExp} field in this object.
	 * @param rewardExp The amount of experience to set.
	 */
	public void setRewardExp(int rewardExp)
	{
		this.rewardExp = rewardExp;
	}
	
	/**
	 * Retrieves the extra experience points awarded to the player.<br>
	 * This value is calculated based on specific rewards for {@link NeviwindCanyonPlayerReward}.
	 * @return The amount of bonus experience as an {@code int}.
	 */
	public int getBonusExp()
	{
		return bonusExp;
	}
	
	/**
	 * Sets the extra experience points for the player.<br>
	 * This value is stored in the {@code bonusExp} field.
	 * @param bonusExp The amount of additional experience to award.
	 */
	public void setBonusExp(int bonusExp)
	{
		this.bonusExp = bonusExp;
	}
	
	/**
	 * Checks if the reward currently has an active morale boost.<br>
	 * It verifies if the {@code boostMorale} buff is valid.
	 * @return {@code true} if the buff exists, otherwise {@code false}.
	 */
	public boolean hasBoostMorale()
	{
		return boostMorale.hasInstanceBuff();
	}
	
	/**
	 * Applies a morale boost effect to the specified player.<br>
	 * This method uses the {@code boostMorale} buff to grant a temporary bonus.<br>
	 * The duration of the effect is set to {@code 20000} milliseconds.
	 * @param player The {@link Player} who will receive the morale boost.
	 */
	public void applyBoostMoraleEffect(Player player)
	{
		boostMorale.applyEffect(player, 20000);
	}
	
	/**
	 * Removes the morale boost effect from a player.<br>
	 * This method calls {@code endEffect} on the active buff.
	 * @param player The {@code Player} object to receive the effect removal.
	 */
	public void endBoostMoraleEffect(Player player)
	{
		boostMorale.endEffect(player);
	}
	
	/**
	 * Calculates the remaining time for the morale boost.<br>
	 * It checks if the current duration is between {@code 0} and {@code 20} seconds.<br>
	 * If it is, it returns the difference to reach {@code 20}.<br>
	 * Otherwise, it returns {@code 0}.
	 * @return The remaining time as an {@code int}.
	 */
	public int getRemaningTime()
	{
		final int time = boostMorale.getRemaningTime();
		if ((time >= 0) && (time < 20))
		{
			return 20 - time;
		}
		
		return 0;
	}
}
