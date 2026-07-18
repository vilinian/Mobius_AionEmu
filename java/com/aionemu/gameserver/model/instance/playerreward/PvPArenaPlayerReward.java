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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceBuff;
import com.aionemu.gameserver.model.instance.instancereward.ArgentManorReward;
import com.aionemu.gameserver.model.instance.instancereward.SealedHallOfKnowledgeReward;

/**
 * Represents the rewards granted to a {@link Player} for participating in the PvP Arena.<br>
 * This class extends {@link InstancePlayerReward} to handle specific arena-related loot and bonuses.
 * @author xTz
 */
public class PvPArenaPlayerReward extends InstancePlayerReward
{
	private int position;
	private int timeBonus;
	private final float timeBonusModifier;
	private int basicAP;
	private int rankingAP;
	private int scoreAP;
	private int basicGP;
	private int rankingGP;
	private int scoreGP;
	private int basicCrucible;
	private int rankingCrucible;
	private int scoreCrucible;
	private int basicCourage;
	private int rankingCourage;
	private int scoreCourage;
	private int opportunity;
	private int gloryTicket;
	private int mithrilMedal;
	private int platinumMedal;
	private int gloriousInsignia;
	private int basicInfinity;
	private int rankingInfinity;
	private int scoreInfinity;
	private int lifeSerum;
	private long logoutTime;
	private boolean isRewarded = false;
	private final InstanceBuff boostMorale;
	
	/**
	 * Creates a new {@link PvPArenaPlayerReward} instance.<br>
	 * This constructor initializes the reward with base points and calculates the time bonus modifier.<br>
	 * It also sets up the morale boost buff based on the provided ID.
	 * @param object The unique identifier for the reward object.
	 * @param timeBonus The amount of time to be added as a bonus.
	 * @param buffId The unique identifier for the {@link InstanceBuff} to apply.
	 */
	public PvPArenaPlayerReward(Integer object, int timeBonus, byte buffId)
	{
		super(object);
		super.addPoints(13000);
		this.timeBonus = timeBonus;
		timeBonusModifier = ((float) this.timeBonus / (float) 660000);
		boostMorale = new InstanceBuff(buffId);
	}
	
	/**
	 * Retrieves the current rank of the player.<br>
	 * This value represents the numerical position in the arena rewards.
	 * @return The integer value of the {@code position}.
	 */
	public int getPosition()
	{
		return position;
	}
	
	/**
	 * Sets the rank position for this reward.<br>
	 * This updates the {@code position} field of the object.
	 * @param position The new integer value for the rank position.
	 */
	public void setPosition(int position)
	{
		this.position = position;
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
	 * Retrieves the amount of Basic AP for this reward.<br>
	 * This value is stored in the {@code basicAP} field.
	 * @return The current number of Basic AP as an {@code int}.
	 */
	public int getBasicAP()
	{
		return basicAP;
	}
	
	/**
	 * Retrieves the amount of Ranking AP awarded to a player.<br>
	 * This value is specific to the {@link PvPArenaPlayerReward} instance.
	 * @return The current ranking AP as an {@code int}.
	 */
	public int getRankingAP()
	{
		return rankingAP;
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
	 * Sets the basic AP value for this reward.<br>
	 * This updates the {@code basicAP} field with a new integer value.
	 * @param ap The amount of basic AP to set.
	 */
	public void setBasicAP(int ap)
	{
		basicAP = ap;
	}
	
	/**
	 * Sets the amount of ranking AP for the player reward.<br>
	 * This value is used to determine the specific bonus granted based on rank.
	 * @param ap The amount of ranking AP to set.
	 */
	public void setRankingAP(int ap)
	{
		rankingAP = ap;
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
	 * Retrieves the amount of basic {@code GP} awarded.<br>
	 * This value is stored in the {@code basicGP} field.
	 * @return The number of basic {@code GP}.
	 */
	public int getBasicGP()
	{
		return basicGP;
	}
	
	/**
	 * Retrieves the amount of Ranking GP awarded to the player.<br>
	 * This value is stored in the {@code rankingGP} field.
	 * @return The current ranking GP as an {@code int}.
	 */
	public int getRankingGP()
	{
		return rankingGP;
	}
	
	/**
	 * Retrieves the amount of {@code GP} earned from the score.<br>
	 * This value is stored in the {@code scoreGP} field.
	 * @return The total score {@code GP} as an {@code int}.
	 */
	public int getScoreGP()
	{
		return scoreGP;
	}
	
	/**
	 * Sets the amount of basic GP for the player reward.<br>
	 * This updates the {@code basicGP} field in this object.
	 * @param gp The amount of basic GP to set.
	 */
	public void setBasicGP(int gp)
	{
		basicGP = gp;
	}
	
	/**
	 * Sets the amount of ranking GP for the player reward.<br>
	 * This updates the {@code rankingGP} field in this object.
	 * @param gp The amount of GP to set.
	 */
	public void setRankingGP(int gp)
	{
		rankingGP = gp;
	}
	
	/**
	 * Sets the score GP value for the reward.<br>
	 * This updates the {@code scoreGP} field with a new integer value.
	 * @param gp The new amount of score GP to assign.
	 */
	public void setScoreGP(int gp)
	{
		scoreGP = gp;
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
	 * Retrieves the amount of basic crucible points.<br>
	 * This value is part of the {@link PvPArenaPlayerReward} reward data.
	 * @return The current {@code int} value for basic crucible.
	 */
	public int getBasicCrucible()
	{
		return basicCrucible;
	}
	
	/**
	 * Retrieves the amount of crucible points earned from ranking.<br>
	 * This value is stored in the {@code rankingCrucible} field.
	 * @return The number of ranking crucible points.
	 */
	public int getRankingCrucible()
	{
		return rankingCrucible;
	}
	
	/**
	 * Retrieves the crucible score for the player reward.<br>
	 * This value represents the specific score earned in the crucible.
	 * @return the current {@code int} value of the score crucible.
	 */
	public int getScoreCrucible()
	{
		return scoreCrucible;
	}
	
	/**
	 * Sets the base amount of crucible points for a player reward.<br>
	 * This updates the {@code basicCrucible} field in this object.
	 * @param basicCrucible The number of crucible points to assign.
	 */
	public void setBasicCrucible(int basicCrucible)
	{
		this.basicCrucible = basicCrucible;
	}
	
	/**
	 * Sets the amount of ranking crucible rewards.<br>
	 * This updates the {@code rankingCrucible} field for the player reward.
	 * @param rankingCrucible The number of crucible rewards to assign.
	 */
	public void setRankingCrucible(int rankingCrucible)
	{
		this.rankingCrucible = rankingCrucible;
	}
	
	/**
	 * Sets the crucible score for the player reward.<br>
	 * This updates the {@code scoreCrucible} field in this object.
	 * @param scoreCrucible The new integer value for the crucible score.
	 */
	public void setScoreCrucible(int scoreCrucible)
	{
		this.scoreCrucible = scoreCrucible;
	}
	
	/**
	 * Sets the base courage value for the player reward.<br>
	 * This updates the {@code basicCourage} field in this object.
	 * @param basicCourage The new integer value to set for basic courage.
	 */
	public void setBasicCourage(int basicCourage)
	{
		this.basicCourage = basicCourage;
	}
	
	/**
	 * Sets the amount of courage awarded based on ranking.<br>
	 * This updates the {@code rankingCourage} field in this object.
	 * @param rankingCourage The amount of courage to set.
	 */
	public void setRankingCourage(int rankingCourage)
	{
		this.rankingCourage = rankingCourage;
	}
	
	/**
	 * Sets the courage score for the player reward.<br>
	 * This updates the {@code scoreCourage} field in this object.
	 * @param scoreCourage The new value to assign to the courage score.
	 */
	public void setScoreCourage(int scoreCourage)
	{
		this.scoreCourage = scoreCourage;
	}
	
	/**
	 * Retrieves the base amount of courage awarded to a player.<br>
	 * This value is stored in the {@code basicCourage} field.
	 * @return The integer value of the basic courage.
	 */
	public int getBasicCourage()
	{
		return basicCourage;
	}
	
	/**
	 * Retrieves the courage points earned from ranking.<br>
	 * This value is stored in the {@code rankingCourage} field.
	 * @return The amount of ranking courage as an {@code int}.
	 */
	public int getRankingCourage()
	{
		return rankingCourage;
	}
	
	/**
	 * Retrieves the courage score for the player reward.<br>
	 * This value represents the specific amount of {@code scoreCourage} earned.
	 * @return The current courage score as an {@code int}.
	 */
	public int getScoreCourage()
	{
		return scoreCourage;
	}
	
	/**
	 * Retrieves the current opportunity value.<br>
	 * This value is part of the {@link PvPArenaPlayerReward} data.
	 * @return The integer value of the opportunity.
	 */
	public int getOpportunity()
	{
		return opportunity;
	}
	
	/**
	 * Sets the current opportunity value.<br>
	 * This updates the {@code opportunity} field in this reward object.
	 * @param opportunity The new integer value to assign to the opportunity field.
	 */
	public void setOpportunity(int opportunity)
	{
		this.opportunity = opportunity;
	}
	
	/**
	 * Retrieves the number of glory tickets earned.<br>
	 * This value is part of the {@link PvPArenaPlayerReward} reward data.
	 * @return The total count of glory tickets as an {@code int}.
	 */
	public int getGloryTicket()
	{
		return gloryTicket;
	}
	
	/**
	 * Sets the number of glory tickets for this reward.<br>
	 * This updates the {@code gloryTicket} field in the current object.
	 * @param gloryTicket The amount of glory tickets to set.
	 */
	public void setGloryTicket(int gloryTicket)
	{
		this.gloryTicket = gloryTicket;
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
	 * Updates the number of {@code mithrilMedal} for this reward.<br>
	 * This method sets the value to the provided integer.
	 * @param mithrilMedal The new amount of medals to set.
	 */
	public void setMithrilMedal(int mithrilMedal)
	{
		this.mithrilMedal = mithrilMedal;
	}
	
	/**
	 * Retrieves the number of platinum medals awarded.<br>
	 * This value is stored in the {@code platinumMedal} field.
	 * @return The total count of platinum medals as an {@code int}.
	 */
	public int getPlatinumMedal()
	{
		return platinumMedal;
	}
	
	/**
	 * Sets the number of {@code platinumMedal} rewards.<br>
	 * This updates the internal value for this reward instance.
	 * @param platinumMedal The amount of medals to set.
	 */
	public void setplatinumMedal(int platinumMedal)
	{
		this.platinumMedal = platinumMedal;
	}
	
	/**
	 * Retrieves the number of {@code gloriousInsignia} earned.<br>
	 * This value is part of the reward for a player in the arena.
	 * @return The total count of {@code gloriousInsignia}.
	 */
	public int getGloriousInsignia()
	{
		return gloriousInsignia;
	}
	
	/**
	 * Sets the amount of {@code gloriousInsignia} for this reward.<br>
	 * This updates the internal value used by the {@link PvPArenaPlayerReward} class.
	 * @param gloriousInsignia The number of insignia to assign.
	 */
	public void setGloriousInsignia(int gloriousInsignia)
	{
		this.gloriousInsignia = gloriousInsignia;
	}
	
	/**
	 * Retrieves the amount of {@code lifeSerum} awarded to the player.<br>
	 * This value is part of the {@link PvPArenaPlayerReward} data.
	 * @return The current amount of {@code lifeSerum}.
	 */
	public int getLifeSerum()
	{
		return lifeSerum;
	}
	
	/**
	 * Sets the amount of {@code lifeSerum} for the reward.<br>
	 * This updates the internal value used by the {@link PvPArenaPlayerReward} class.
	 * @param lifeSerum The number of {@code lifeSerum} to assign.
	 */
	public void setLifeSerum(int lifeSerum)
	{
		this.lifeSerum = lifeSerum;
	}
	
	/**
	 * Sets the amount of basic infinity rewards.<br>
	 * This updates the {@code basicInfinity} field in this object.
	 * @param basicInfinity The number of basic infinity to assign.
	 */
	public void setBasicInfinity(int basicInfinity)
	{
		this.basicInfinity = basicInfinity;
	}
	
	/**
	 * Sets the amount of ranking infinity rewards.<br>
	 * This updates the {@code rankingInfinity} field in this object.
	 * @param rankingInfinity The number of infinity rewards to set.
	 */
	public void setRankingInfinity(int rankingInfinity)
	{
		this.rankingInfinity = rankingInfinity;
	}
	
	/**
	 * Sets the amount of {@code scoreInfinity} for this reward.<br>
	 * This updates the internal value used by the reward system.
	 * @param scoreInfinity The new integer value to set.
	 */
	public void setScoreInfinity(int scoreInfinity)
	{
		this.scoreInfinity = scoreInfinity;
	}
	
	/**
	 * Retrieves the amount of basic infinity points.<br>
	 * This value is part of the {@link PvPArenaPlayerReward} reward data.
	 * @return The number of basic infinity points as an {@code int}.
	 */
	public int getBasicInfinity()
	{
		return basicInfinity;
	}
	
	/**
	 * Retrieves the amount of Infinity points earned from ranking.<br>
	 * This value is specific to the player's rank in the arena.
	 * @return The {@code int} value of ranking infinity.
	 */
	public int getRankingInfinity()
	{
		return rankingInfinity;
	}
	
	/**
	 * Retrieves the amount of {@code infinity} points earned from the score.<br>
	 * This value is stored in the {@code scoreInfinity} field.
	 * @return The current score for {@code infinity}.
	 */
	public int getScoreInfinity()
	{
		return scoreInfinity;
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
