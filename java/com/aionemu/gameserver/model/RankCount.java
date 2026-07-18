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
package com.aionemu.gameserver.model;

/**
 * This class represents the count of ranks associated with a specific entity.<br>
 * It is used to track and manage ranking data within the {@code model} package.
 * @author zdead
 */
public class RankCount
{
	private final int playerId;
	private final int ap;
	private final int gp;
	private final Race race;
	
	/**
	 * Creates a new instance of {@link RankCount}.<br>
	 * This constructor initializes the player data.
	 * @param playerId The unique identifier for the player.
	 * @param ap The amount of AP points.
	 * @param gp The amount of GP points.
	 * @param race The {@code Race} type of the player.
	 */
	public RankCount(int playerId, int ap, int gp, Race race)
	{
		this.playerId = playerId;
		this.ap = ap;
		this.gp = gp;
		this.race = race;
	}
	
	/**
	 * Retrieves the unique identifier for the player.<br>
	 * This value is stored in the {@code playerId} field.
	 * @return The {@code int} ID of the player.
	 */
	public int getPlayerId()
	{
		return playerId;
	}
	
	/**
	 * Retrieves the current AP value for the player.<br>
	 * This value is stored in the {@code ap} field.
	 * @return The player's AP as an {@code int}.
	 */
	public int getPlayerAP()
	{
		return ap;
	}
	
	/**
	 * Retrieves the current GP value for the player.<br>
	 * This method returns the {@code gp} field from the {@link AbyssRankingResult} instance.
	 * @return The player's GP as an {@code int}.
	 */
	public int getPlayerGP()
	{
		return gp;
	}
	
	/**
	 * Retrieves the race of the player.<br>
	 * This method returns the {@code Race} object associated with this rank count.
	 * @return The {@link Race} of the player.
	 */
	public Race getPlayerRace()
	{
		return race;
	}
}
