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
package com.aionemu.gameserver.model.gameobjects.player.ranking;

import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * Represents the ranking data for a player in the Arena of Discipline.<br>
 * This class stores persistent information related to their competitive standing.
 */
public class ArenaOfDisciplineRank
{
	private int rank;
	private int bestRank;
	
	private int points;
	private int lastPoints;
	private int highPoints;
	private int lowPoints;
	
	private int possitionMatch;
	
	private PersistentState persistentState;
	
	/**
	 * Creates a new instance of {@link ArenaOfDisciplineRank}.<br>
	 * This constructor initializes all ranking and point statistics.
	 * @param rank The current rank of the player.
	 * @param bestRank The highest rank ever achieved by the player.
	 * @param points The current total points.
	 * @param lastPoints The points from the previous match.
	 * @param highPoints The maximum points recorded in a single match.
	 * @param lowPoints The minimum points recorded in a single match.
	 * @param possitionMatch The position achieved in the most recent match.
	 */
	public ArenaOfDisciplineRank(int rank, int bestRank, int points, int lastPoints, int highPoints, int lowPoints, int possitionMatch)
	{
		this.rank = rank;
		this.bestRank = bestRank;
		this.points = points;
		this.lastPoints = lastPoints;
		this.highPoints = highPoints;
		this.lowPoints = lowPoints;
		this.possitionMatch = possitionMatch;
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
	 * Retrieves the highest rank achieved by the player.<br>
	 * This value is stored in the {@code bestRank} field.
	 * @return The integer value of the best rank.
	 */
	public int getBestRank()
	{
		return bestRank;
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
	 * Retrieves the points from the most recent match.<br>
	 * This value is updated every time a new game ends.
	 * @return The {@code int} value of the last recorded points.
	 */
	public int getLastPoints()
	{
		return lastPoints;
	}
	
	/**
	 * Retrieves the highest score achieved by the player.<br>
	 * This value represents the peak points recorded in the ranking history.
	 * @return The maximum number of points as an {@code int}.
	 */
	public int getHighPoints()
	{
		return highPoints;
	}
	
	/**
	 * Retrieves the lowest point value recorded.<br>
	 * This value is stored in the {@code lowPoints} field.
	 * @return The minimum number of points achieved.
	 */
	public int getLowPoints()
	{
		return lowPoints;
	}
	
	/**
	 * Retrieves the current match position.<br>
	 * This value represents where the player stands in a specific match.
	 * @return The {@code int} value of the match position.
	 */
	public int getPossitionMatch()
	{
		return possitionMatch;
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
	 * Updates the best rank for this player.<br>
	 * This method sets the {@code bestRank} field to a new value.
	 * @param rank The new rank value to store as the best rank.
	 */
	public void setBestRank(int rank)
	{
		bestRank = rank;
	}
	
	/**
	 * Updates the current points for this rank.<br>
	 * This method sets the {@code points} field to a new value.
	 * @param pts The new point value to assign.
	 */
	public void setPoints(int pts)
	{
		points = pts;
	}
	
	/**
	 * Updates the value of {@code lastPoints}.<br>
	 * This method stores the most recent points earned.
	 * @param pts The new point value to set.
	 */
	public void setLastPoints(int pts)
	{
		lastPoints = pts;
	}
	
	/**
	 * Updates the highest points achieved by the player.<br>
	 * This method sets the {@code highPoints} field to a new value.
	 * @param pts The new high score to record.
	 */
	public void setHighPoints(int pts)
	{
		highPoints = pts;
	}
	
	/**
	 * Updates the lowest points recorded for this rank.<br>
	 * This method sets the {@code lowPoints} field to a new value.
	 * @param pts The new value for the low points.
	 */
	public void setLowPoints(int pts)
	{
		lowPoints = pts;
	}
	
	/**
	 * Updates the match position for this rank.<br>
	 * This method sets the {@code possitionMatch} field to a new value.
	 * @param pos The new position value to set.
	 */
	public void setPossitionMatch(int pos)
	{
		possitionMatch = pos;
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
}
