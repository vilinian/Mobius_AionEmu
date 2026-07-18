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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.Calendar;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * Represents the abyss rank data for a player character.<br>
 * This class manages the progression and state of a player's {@link AbyssRankEnum} status.
 * @author ATracer, Divinity
 */
public final class AbyssRank
{
	private int dailyAP;
	private int dailyGP;
	private int weeklyAP;
	private int weeklyGP;
	private int currentAp;
	private int currentGp;
	private AbyssRankEnum rank;
	private int topRanking;
	private PersistentState persistentState;
	private int dailyKill;
	private int weeklyKill;
	private int allKill;
	private int maxRank;
	private int lastKill;
	private int lastAP;
	private int lastGP;
	private long lastUpdate;
	
	/**
	 * Constructs a new {@code AbyssRank} object with all required statistics.<br>
	 * This constructor initializes the player's rank data and triggers an update.
	 * @param dailyAP The amount of daily AP.
	 * @param dailyGP The amount of daily GP.
	 * @param weeklyAP The amount of weekly AP.
	 * @param weeklyGP The amount of weekly GP.
	 * @param ap The current total AP.
	 * @param gp The current total GP.
	 * @param rank The integer ID used to determine the {@link AbyssRankEnum} rank.
	 * @param topRanking The player's position in the top ranking.
	 * @param dailyKill The number of kills achieved today.
	 * @param weeklyKill The number of kills achieved this week.
	 * @param allKill The total number of kills ever achieved.
	 * @param maxRank The highest rank ever reached by the player.
	 * @param lastKill The ID or count of the most recent kill.
	 * @param lastAP The AP value from the previous update.
	 * @param lastGP
	 * @param lastUpdate
	 */
	public AbyssRank(int dailyAP, int dailyGP, int weeklyAP, int weeklyGP, int ap, int gp, int rank, int topRanking, int dailyKill, int weeklyKill, int allKill, int maxRank, int lastKill, int lastAP, int lastGP, long lastUpdate)
	{
		this.dailyAP = dailyAP;
		this.dailyGP = dailyGP;
		this.weeklyAP = weeklyAP;
		this.weeklyGP = weeklyGP;
		currentAp = ap;
		currentGp = gp;
		this.rank = AbyssRankEnum.getRankById(rank);
		this.topRanking = topRanking;
		this.dailyKill = dailyKill;
		this.weeklyKill = weeklyKill;
		this.allKill = allKill;
		this.maxRank = maxRank;
		this.lastKill = lastKill;
		this.lastAP = lastAP;
		this.lastGP = lastGP;
		this.lastUpdate = lastUpdate;
		
		doUpdate();
	}
	
	public enum AbyssRankUpdateType
	{
		PLAYER_ELYOS(1),
		PLAYER_ASMODIANS(2),
		LEGION_ELYOS(4),
		LEGION_ASMODIANS(8);
		
		private final int id;
		
		AbyssRankUpdateType(int id)
		{
			this.id = id;
		}
		
		public int value()
		{
			return id;
		}
	}
	
	/**
	 * Adds a specific amount of {@code AP} to the player's totals.<br>
	 * This method updates both daily and weekly values.<br>
	 * It also applies any configured limits from {@link CustomConfig}.<br>
	 * Finally, it checks if the new total qualifies for a rank update.
	 * @param additionalAp The amount of {@code AP} to add.
	 */
	public void addAp(int additionalAp)
	{
		dailyAP += additionalAp;
		if (dailyAP < 0)
		{
			dailyAP = 0;
		}
		
		weeklyAP += additionalAp;
		if (weeklyAP < 0)
		{
			weeklyAP = 0;
		}
		
		int cappedCount = 0;
		if (CustomConfig.ENABLE_AP_CAP)
		{
			cappedCount = (currentAp + additionalAp) > CustomConfig.AP_CAP_VALUE ? (int) (CustomConfig.AP_CAP_VALUE - currentAp) : additionalAp;
		}
		else
		{
			cappedCount = additionalAp;
		}
		
		currentAp += cappedCount;
		if (currentAp < 0)
		{
			currentAp = 0;
		}
		
		final AbyssRankEnum newRank = AbyssRankEnum.getRankForAp(currentAp);
		if (newRank.getId() <= 9)
		{
			setRank(newRank);
		}
		
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Adds a specified amount of GP to the player's stats.<br>
	 * This method updates daily, weekly, and current GP values.<br>
	 * It also checks for any configured GP caps and updates the rank if necessary.
	 * @param additionalGp The amount of GP to add to the totals.
	 */
	public void addGp(int additionalGp)
	{
		dailyGP += additionalGp;
		if (dailyGP < 0)
		{
			dailyGP = 0;
		}
		
		weeklyGP += additionalGp;
		if (weeklyGP < 0)
		{
			weeklyGP = 0;
		}
		
		int GpcappedCount = 0;
		if (CustomConfig.ENABLE_GP_CAP)
		{
			GpcappedCount = (currentGp + additionalGp) > CustomConfig.GP_CAP_VALUE ? (int) (CustomConfig.GP_CAP_VALUE - currentGp) : additionalGp;
		}
		else
		{
			GpcappedCount = additionalGp;
		}
		
		currentGp += GpcappedCount;
		if (currentGp < 0)
		{
			currentGp = 0;
		}
		
		final AbyssRankEnum newRank = AbyssRankEnum.getRankForGp(currentGp);
		
		// Do not set a rank for SUPREME_COMMANDER as it will automatically become Governor; let the Abyss rank handle this via Petruknisme.
		
		if ((newRank.getId() < 18) && (newRank.getId() > 9))
		{
			setRank(newRank);
		}
		
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the amount of daily AP for the player.<br>
	 * This value is stored in the {@code dailyAP} field.
	 * @return The current daily AP as an {@code int}.
	 */
	public int getDailyAP()
	{
		return dailyAP;
	}
	
	/**
	 * Retrieves the amount of daily GP earned.<br>
	 * This value is stored in the {@code dailyGP} field.
	 * @return The current daily GP as an {@code int}.
	 */
	public int getDailyGP()
	{
		return dailyGP;
	}
	
	/**
	 * Retrieves the total amount of weekly AP accumulated.<br>
	 * This value is stored in the {@code weeklyAP} field.
	 * @return The current weekly AP as an {@code int}.
	 */
	public int getWeeklyAP()
	{
		return weeklyAP;
	}
	
	/**
	 * Retrieves the total weekly GP for the player.<br>
	 * This value is stored in the {@code weeklyGP} field.
	 * @return The current amount of weekly GP as an {@code int}.
	 */
	public int getWeeklyGP()
	{
		return weeklyGP;
	}
	
	/**
	 * Retrieves the current amount of AP.<br>
	 * This value represents the player's active AP points.
	 * @return The current {@code int} value of AP.
	 */
	public int getAp()
	{
		return currentAp;
	}
	
	/**
	 * Retrieves the current GP value.<br>
	 * This method returns the amount of GP currently held by the player.
	 * @return The current {@code int} value for GP.
	 */
	public int getGp()
	{
		return currentGp;
	}
	
	/**
	 * Retrieves the current abyss rank of the player.<br>
	 * This method returns the {@code AbyssRankEnum} value associated with this object.
	 * @return The current {@link AbyssRankEnum} rank.
	 */
	public AbyssRankEnum getRank()
	{
		return rank;
	}
	
	/**
	 * Retrieves the current top ranking value.<br>
	 * This method returns the integer stored in the {@code topRanking} field.
	 * @return The current top ranking as an {@code int}.
	 */
	public int getTopRanking()
	{
		return topRanking;
	}
	
	/**
	 * Updates the current ranking position.<br>
	 * This method sets the value of {@code topRanking}.
	 * @param topRanking The new rank integer to assign.
	 */
	public void setTopRanking(int topRanking)
	{
		this.topRanking = topRanking;
	}
	
	/**
	 * Retrieves the number of kills achieved today.<br>
	 * This value is stored in the {@code dailyKill} field.
	 * @return The total count of daily kills as an {@code int}.
	 */
	public int getDailyKill()
	{
		return dailyKill;
	}
	
	/**
	 * Retrieves the total number of kills for the current week.<br>
	 * This value is stored in the {@code weeklyKill} field.
	 * @return The total count of weekly kills as an {@code int}.
	 */
	public int getWeeklyKill()
	{
		return weeklyKill;
	}
	
	/**
	 * Retrieves the total number of kills.<br>
	 * This value represents the cumulative kill count for the player.
	 * @return The total number of kills as an {@code int}.
	 */
	public int getAllKill()
	{
		return allKill;
	}
	
	/**
	 * Updates the kill counters for the player.<br>
	 * This method increments {@code dailyKill}, {@code weeklyKill}, and {@code allKill} by 1.
	 */
	public void setAllKill()
	{
		dailyKill += 1;
		weeklyKill += 1;
		allKill += 1;
	}
	
	/**
	 * Retrieves the highest rank achieved by the player.<br>
	 * This value is stored in the {@code maxRank} field.
	 * @return The maximum rank as an {@code int}.
	 */
	public int getMaxRank()
	{
		return maxRank;
	}
	
	/**
	 * Retrieves the count of the most recent kill.<br>
	 * This value is stored in the {@code lastKill} field.
	 * @return The number of the last kill as an {@code int}.
	 */
	public int getLastKill()
	{
		return lastKill;
	}
	
	/**
	 * Retrieves the value of the last {@code AP} earned.<br>
	 * This method returns the {@code lastAP} field from the current instance.
	 * @return The amount of the last {@code AP}.
	 */
	public int getLastAP()
	{
		return lastAP;
	}
	
	/**
	 * Retrieves the value of the last {@code GP} recorded.<br>
	 * This method returns the {@code lastGP} field from the current instance.
	 * @return The integer value of the last {@code GP}.
	 */
	public int getLastGP()
	{
		return lastGP;
	}
	
	/**
	 * Updates the current {@code AbyssRankEnum} for this object.<br>
	 * This method also updates the {@code maxRank} and sets a persistent state update flag.
	 * @param rank The new {@code AbyssRankEnum} to assign.
	 */
	public void setRank(AbyssRankEnum rank)
	{
		if (rank.getId() > maxRank)
		{
			maxRank = rank.getId();
		}
		
		this.rank = rank;
		
		// TODO top ranking for the rest is 0?
		topRanking = rank.getQuota();
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this object.<br>
	 * This method prevents changing from {@code PersistentState.NEW} to {@code PersistentState.UPDATE_REQUIRED}.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		if ((persistentState != PersistentState.UPDATE_REQUIRED) || (this.persistentState != PersistentState.NEW))
		{
			this.persistentState = persistentState;
		}
	}
	
	/**
	 * Retrieves the timestamp of the most recent update.<br>
	 * This value is stored in milliseconds.
	 * @return The {@code long} value representing the last update time.
	 */
	public long getLastUpdate()
	{
		return lastUpdate;
	}
	
	/**
	 * Updates the player's rank statistics based on time and progress.<br>
	 * This method resets daily and weekly values if the date or week has changed.<br>
	 * It also checks for new maximum ranks achieved while offline.<br>
	 * If any changes occur, it sets the {@link PersistentState} to {@code UPDATE_REQUIRED}.
	 */
	public void doUpdate()
	{
		boolean needUpdate = false;
		final Calendar lastCal = Calendar.getInstance();
		lastCal.setTimeInMillis(lastUpdate);
		
		final Calendar curCal = Calendar.getInstance();
		curCal.setTimeInMillis(System.currentTimeMillis());
		
		// Checking the day - month & year are checked to prevent if a player come back after 1 month, the same day
		if ((lastCal.get(Calendar.DAY_OF_MONTH) != curCal.get(Calendar.DAY_OF_MONTH)) || (lastCal.get(Calendar.MONTH) != curCal.get(Calendar.MONTH)) || (lastCal.get(Calendar.YEAR) != curCal.get(Calendar.YEAR)))
		{
			dailyAP = 0;
			dailyGP = 0;
			dailyKill = 0;
			needUpdate = true;
		}
		
		// Checking the week - year is checked to prevent if a player come back after 1 year, the same week
		if ((lastCal.get(Calendar.WEEK_OF_YEAR) != curCal.get(Calendar.WEEK_OF_YEAR)) || (lastCal.get(Calendar.YEAR) != curCal.get(Calendar.YEAR)))
		{
			lastKill = weeklyKill;
			lastAP = weeklyAP;
			lastGP = weeklyGP;
			weeklyKill = 0;
			weeklyAP = 0;
			weeklyGP = 0;
			needUpdate = true;
		}
		
		// For offline changed ranks
		if (rank.getId() > maxRank)
		{
			maxRank = rank.getId();
			needUpdate = true;
		}
		
		// Finally, update the the last update
		lastUpdate = System.currentTimeMillis();
		
		if (needUpdate)
		{
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
	}
}
