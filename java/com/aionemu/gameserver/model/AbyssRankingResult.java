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
 * This class represents the result of an abyss ranking calculation.<br>
 * It holds the data needed to display rankings for players in the {@code Abyss} system.
 * @author zdead
 */
public class AbyssRankingResult
{
	private String playerName;
	private int playerAbyssRank;
	private final int oldRankPos;
	private final int rankPos;
	private int ap;
	private int title;
	private PlayerClass playerClass;
	private int playerLevel;
	private int playerId;
	private Gender playerGender;
	private int gp;
	private final String legionName;
	private long cp;
	private int legionId;
	private int legionLevel;
	private int legionMembers;
	
	/**
	 * Creates a new {@link AbyssRankingResult} object.<br>
	 * This constructor initializes all player and ranking statistics.
	 * @param playerName The name of the player.
	 * @param playerAbyssRank The current abyss rank of the player.
	 * @param playerId The unique identifier for the player.
	 * @param ap The amount of AP points owned by the player.
	 * @param gp The amount of GP points owned by the player.
	 * @param title The numerical ID of the player's title.
	 * @param playerClass The {@link PlayerClass} of the character.
	 * @param playerGender The {@link Gender} of the character.
	 * @param playerLevel The current level of the player.
	 * @param legionName The name of the player's legion.
	 * @param oldRankPos The previous position in the ranking.
	 * @param rankPos The new position in the ranking.
	 */
	public AbyssRankingResult(String playerName, int playerAbyssRank, int playerId, int ap, int gp, int title, PlayerClass playerClass, Gender playerGender, int playerLevel, String legionName, int oldRankPos, int rankPos)
	{
		this.playerName = playerName;
		this.playerAbyssRank = playerAbyssRank;
		this.playerId = playerId;
		this.ap = ap;
		this.gp = gp;
		this.title = title;
		this.playerClass = playerClass;
		this.playerGender = playerGender;
		this.playerLevel = playerLevel;
		this.legionName = legionName;
		this.oldRankPos = oldRankPos;
		this.rankPos = rankPos;
	}
	
	/**
	 * Creates a new {@link AbyssRankingResult} object.<br>
	 * This constructor initializes the ranking data for a specific legion.
	 * @param cp The combat power of the legion.
	 * @param legionName The name of the legion.
	 * @param legionId The unique identifier for the legion.
	 * @param legionLevel The current level of the legion.
	 * @param legionMembers The total number of members in the legion.
	 * @param oldRankPos The previous ranking position.
	 * @param rankPos The new ranking position.
	 */
	public AbyssRankingResult(long cp, String legionName, int legionId, int legionLevel, int legionMembers, int oldRankPos, int rankPos)
	{
		this.oldRankPos = oldRankPos;
		this.rankPos = rankPos;
		this.cp = cp;
		this.legionName = legionName;
		this.legionId = legionId;
		this.legionLevel = legionLevel;
		this.legionMembers = legionMembers;
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
	 * Retrieves the current rank of the player in the Abyss.<br>
	 * This value is stored in the {@code playerAbyssRank} field.
	 * @return The integer rank of the player.
	 */
	public int getPlayerAbyssRank()
	{
		return playerAbyssRank;
	}
	
	/**
	 * Retrieves the previous ranking position of the player.<br>
	 * This value represents where the player stood before the current update.
	 * @return The {@code int} value of the old rank position.
	 */
	public int getOldRankPos()
	{
		return oldRankPos;
	}
	
	/**
	 * Retrieves the current ranking position.<br>
	 * This value represents where the player stands in the list.
	 * @return The current {@code int} rank position.
	 */
	public int getRankPos()
	{
		return rankPos;
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
	 * Retrieves the unique identifier for the player's title.<br>
	 * This value is stored in the {@code title} field.
	 * @return The integer ID of the player's title.
	 */
	public int getPlayerTitle()
	{
		return title;
	}
	
	/**
	 * Retrieves the current level of the player.<br>
	 * This value is stored in the {@code playerLevel} field.
	 * @return The integer level of the player.
	 */
	public int getPlayerLevel()
	{
		return playerLevel;
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
	 * Retrieves the gender of the player.<br>
	 * This value is stored in the {@code playerGender} field.
	 * @return the {@link Gender} of the player.
	 */
	public Gender getGender()
	{
		return playerGender;
	}
	
	/**
	 * Retrieves the name of the player's legion.<br>
	 * This value is stored in the {@code legionName} field.
	 * @return The name of the legion as a {@code String}.
	 */
	public String getLegionName()
	{
		return legionName;
	}
	
	/**
	 * Retrieves the Combat Power of the legion.<br>
	 * This value is stored in the {@code cp} field.
	 * @return The current legion Combat Power as a {@code long}.
	 */
	public long getLegionCP()
	{
		return cp;
	}
	
	/**
	 * Retrieves the unique identifier for the player's legion.<br>
	 * This value is stored in the {@code legionId} field.
	 * @return The {@code int} ID of the legion.
	 */
	public int getLegionId()
	{
		return legionId;
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
	 * Retrieves the current level of the player's legion.<br>
	 * This value is stored in the {@code legionLevel} field.
	 * @return The integer level of the legion.
	 */
	public int getLegionLevel()
	{
		return legionLevel;
	}
	
	/**
	 * Retrieves the total number of members in the player's legion.<br>
	 * This value is part of the {@link AbyssRankingResult} data.
	 * @return The count of members as an {@code int}.
	 */
	public int getLegionMembers()
	{
		return legionMembers;
	}
}
