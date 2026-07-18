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

/**
 * This class defines the rewards granted to players for participating in the Kamar Battlefield.<br>
 * It extends {@link InstancePlayerReward} to handle specific loot logic for this instance.
 * @author Alcapwnd
 */
public class KamarBattlefieldPlayerReward extends InstancePlayerReward
{
	private int timeBonus;
	private long logoutTime;
	private final float timeBonusModifier;
	private final Race race;
	private int rewardAp;
	private int rewardGp;
	private int bonusAp;
	private int bonusGp;
	private int reward1;
	private int reward2;
	private int bonusReward;
	private int bonusReward2;
	private float rewardCount;
	
	/**
	 * Creates a new {@link KamarBattlefieldPlayerReward} instance.<br>
	 * This constructor initializes the reward with specific time and race data.<br>
	 * It also calculates the internal {@code timeBonusModifier}.
	 * @param object The unique identifier for the player object.
	 * @param timeBonus The amount of bonus time to be applied.
	 * @param race The {@link Race} type of the player.
	 */
	public KamarBattlefieldPlayerReward(Integer object, int timeBonus, Race race)
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
	 * Retrieves the amount of AP awarded to the player.<br>
	 * This value is stored in the {@code rewardAp} field.
	 * @return The total amount of AP as an {@code int}.
	 */
	public int getRewardAp()
	{
		return rewardAp;
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
	 * Sets the bonus {@code AP} value for this reward.<br>
	 * This updates the internal {@code bonusAp} field.
	 * @param ap The amount of bonus {@code AP} to set.
	 */
	public void setBonusAp(int ap)
	{
		bonusAp = ap;
	}
	
	/**
	 * Sets the amount of AP for the reward.<br>
	 * This updates the {@code rewardAp} field.
	 * @param ap The amount of AP to set.
	 */
	public void setRewardAp(int ap)
	{
		rewardAp = ap;
	}
	
	/**
	 * Retrieves the first reward value for the player.<br>
	 * This value is stored as an {@code int}.
	 * @return The value of {@code reward1}.
	 */
	public int getReward1()
	{
		return reward1;
	}
	
	/**
	 * Retrieves the second reward value.<br>
	 * This value is stored in the {@code reward2} field.
	 * @return The integer value of the second reward.
	 */
	public int getReward2()
	{
		return reward2;
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
	 * Retrieves the total number of rewards.<br>
	 * This method converts the internal {@code rewardCount} value to an {@code int}.
	 * @return The count of rewards as an {@code int}.
	 */
	public int getRewardCount()
	{
		return (int) rewardCount;
	}
	
	/**
	 * Sets the value for {@code reward1}.<br>
	 * This updates the first reward amount for the player.
	 * @param reward The new integer value to assign to {@code reward1}.
	 */
	public void setReward1(int reward)
	{
		reward1 = reward;
	}
	
	/**
	 * Sets the value for {@code reward2}.<br>
	 * This updates the second reward amount for the player.
	 * @param reward The new integer value to assign to {@code reward2}.
	 */
	public void setReward2(int reward)
	{
		reward2 = reward;
	}
	
	/**
	 * Sets the bonus reward value for the player.<br>
	 * This updates the {@code bonusReward} field.
	 * @param reward The new integer value for the bonus reward.
	 */
	public void setBonusReward(int reward)
	{
		bonusReward = reward;
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
	 * Retrieves the second bonus reward value.<br>
	 * This value is stored in the {@code bonusReward2} field.
	 * @return The integer value of the second bonus reward.
	 */
	public int getBonusReward2()
	{
		return bonusReward2;
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
}
