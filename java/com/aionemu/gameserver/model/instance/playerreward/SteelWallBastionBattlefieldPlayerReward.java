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
 * This class defines the rewards granted to players for participating in the Steel Wall Bastion battlefield.<br>
 * It extends {@link InstancePlayerReward} to handle specific loot and benefits for this instance.
 */
public class SteelWallBastionBattlefieldPlayerReward extends InstancePlayerReward
{
	private int timeBonus;
	private int rewardGp;
	private int bonusAp;
	private int bonusReward2;
	private int rewardAp;
	private float rewardCount;
	private int bonusReward;
	private final Race race;
	private long logoutTime;
	private InstanceBuff boostMorale;
	private int bloodMark;
	private int medalBundle;
	private int bonusGp;
	private final float timeBonusModifier;
	
	/**
	 * Creates a new reward for the Steel Wall Bastion battlefield.<br>
	 * This constructor initializes the time bonus and the player's race.<br>
	 * It also calculates the {@code timeBonusModifier} based on the provided time.
	 * @param object The unique identifier for the reward object.
	 * @param timeBonus The amount of time to be added as a bonus.
	 * @param race The {@link Race} of the player receiving the reward.
	 */
	public SteelWallBastionBattlefieldPlayerReward(Integer object, int timeBonus, Race race)
	{
		super(object);
		this.timeBonus = timeBonus;
		timeBonusModifier = ((float) this.timeBonus / (float) 660000);
		this.race = race;
	}
	
	/**
	 * Sets the value for the second bonus reward.<br>
	 * This updates the {@code bonusReward2} field in this object.
	 * @param bonusReward2 The integer value to assign to the second bonus reward.
	 */
	public void setBonusReward2(int bonusReward2)
	{
		this.bonusReward2 = bonusReward2;
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
	 * Sets the extra gold pieces for this reward.<br>
	 * This updates the {@code bonusGp} field.
	 * @param bonusGp The amount of gold to add as a bonus.
	 */
	public void setBonusGp(int bonusGp)
	{
		this.bonusGp = bonusGp;
	}
	
	/**
	 * Retrieves the second bonus reward value.<br>
	 * This value is stored in the {@code bonusReward2} field.
	 * @return The integer value of the second bonus reward.
	 */
	public int getBonusReward2()
	{
		return bonusReward2;
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
	 * Sets the {@code bloodMark} value for this reward.<br>
	 * This updates the internal state of the reward object.
	 * @param bloodMark The new integer value to assign to the blood mark.
	 */
	public void setBloodMark(int bloodMark)
	{
		this.bloodMark = bloodMark;
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
	 * Sets the amount of AP rewarded to the player.<br>
	 * This updates the {@code rewardAp} field in this object.
	 * @param rewardAp The amount of AP to set.
	 */
	public void setRewardAp(int rewardAp)
	{
		this.rewardAp = rewardAp;
	}
	
	/**
	 * Retrieves the current bonus reward value.<br>
	 * This method returns the {@code bonusReward} integer.
	 * @return The amount of the bonus reward.
	 */
	public int getBonusReward()
	{
		return bonusReward;
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
	 * Calculates the current participation rate.<br>
	 * It divides the {@code getTimeBonus()} value by the total {@code timeBonus}.
	 * @return The calculated participation as a {@code float}.
	 */
	public float getParticipation()
	{
		return (float) getTimeBonus() / timeBonus;
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
	
	/**
	 * Retrieves the total number of medals in the bundle.<br>
	 * This value is stored as an {@code int}.
	 * @return The current {@code medalBundle} count.
	 */
	public int getMedalBundle()
	{
		return medalBundle;
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
	 * Removes the morale boost effect from a player.<br>
	 * This method calls {@code endEffect} on the active buff.
	 * @param player The {@code Player} object to receive the effect removal.
	 */
	public void endBoostMoraleEffect(Player player)
	{
		boostMorale.endEffect(player);
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
	 * Updates the {@code logoutTime} field.<br>
	 * This method sets the value to the current system time in milliseconds.
	 */
	public void updateLogOutTime()
	{
		logoutTime = System.currentTimeMillis();
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
	 * Calculates the total score points for the player.<br>
	 * This method adds the {@code timeBonus} to the value returned by {@code getPoints}.
	 * @return The sum of the time bonus and base points as an {@code int}.
	 */
	public int getScorePoints()
	{
		return timeBonus + getPoints();
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
	 * Sets the amount of gold pieces for the reward.<br>
	 * This updates the {@code rewardGp} field in this object.
	 * @param rewardGp The amount of gold pieces to set.
	 */
	public void setRewardGp(int rewardGp)
	{
		this.rewardGp = rewardGp;
	}
	
	/**
	 * Sets the number of medals in the bundle.<br>
	 * This updates the {@code medalBundle} field for this reward.
	 * @param medalBundle The amount of medals to set.
	 */
	public void setMedalBundle(int medalBundle)
	{
		this.medalBundle = medalBundle;
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
	 * Retrieves the current amount of {@code bloodMark}.<br>
	 * This value represents the reward points earned during the event.
	 * @return The integer value of the {@code bloodMark}.
	 */
	public int getBloodMark()
	{
		return bloodMark;
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
	 * Sets the extra reward value for the player.<br>
	 * This updates the {@code bonusReward} field in this object.
	 * @param bonusReward The amount of the bonus reward to set.
	 */
	public void setBonusReward(int bonusReward)
	{
		this.bonusReward = bonusReward;
	}
}
