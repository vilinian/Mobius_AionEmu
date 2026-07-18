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
package com.aionemu.gameserver.model.ranking;

import com.aionemu.gameserver.model.PlayerClass;

/**
 * This class represents the result of a player ranking query.<br>
 * It holds data such as the {@code PlayerClass} and rank information for display purposes.
 */
public class PlayerRankingResult
{
	private final String playerName;
	private final int oldRank;
	private final int rank;
	private final int pc;
	private final PlayerClass playerClass;
	private final int playerRace;
	private final int playerId;
	
	/**
	 * Creates a new {@code PlayerRankingResult} object.<br>
	 * This constructor initializes all ranking data for a specific player.
	 * @param playerName The name of the player.
	 * @param oldRank The previous rank of the player.
	 * @param rank The current rank of the player.
	 * @param pc The points count for the player.
	 * @param playerClass The {@link PlayerClass} of the player.
	 * @param playerRace The race identifier of the player.
	 * @param playerId The unique ID of the player.
	 */
	public PlayerRankingResult(String playerName, int oldRank, int rank, int pc, PlayerClass playerClass, int playerRace, int playerId)
	{
		this.playerName = playerName;
		this.oldRank = oldRank;
		this.rank = rank;
		this.pc = pc;
		this.playerClass = playerClass;
		this.playerRace = playerRace;
		this.playerId = playerId;
	}
	
	/**
	 * Retrieves the name of the player.<br>
	 * This method returns the {@code String} value stored in the {@code playerName} field.
	 * @return The name of the player as a {@code String}.
	 */
	public String getPlayerName()
	{
		return playerName;
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
	 * Retrieves the current rank of the {@code MCEntry}.<br>
	 * This value is used to determine the item's standing.
	 * @return The integer value representing the rank.
	 */
	public int getRank()
	{
		return rank;
	}
	
	/**
	 * Retrieves the previous rank of the player.<br>
	 * This value is used to compare against the current {@code getRank} result.
	 * @return The integer value representing the old rank.
	 */
	public int getOldRank()
	{
		return oldRank;
	}
	
	/**
	 * Retrieves the race of the player.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique identifier for the player's race.
	 */
	public int getPlayerRace()
	{
		return playerRace;
	}
	
	/**
	 * Retrieves the character class of the player.<br>
	 * This method returns the {@code PlayerClass} associated with this ranking result.
	 * @return The {@code PlayerClass} of the player.
	 */
	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}
	
	/**
	 * Retrieves the current number of points for this rank.<br>
	 * This value is used to determine the player's standing in the arena.
	 * @return The current {@code int} value of points.
	 */
	public int getPoints()
	{
		return pc;
	}
}
