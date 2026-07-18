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
package com.aionemu.gameserver.model.team.legion;

import java.sql.Timestamp;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a request from a {@link Player} to join a specific legion.<br>
 * This object carries the necessary data required to process the joining logic.
 * @author CoolyT
 */
public class LegionJoinRequest
{
	private int legionId = 0;
	private int playerId = 0;
	private String playerName = "";
	private int playerClass = 0;
	private int race = 0;
	private int level = 0;
	private int genderId = 0;
	private String msg = "";
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
	/**
	 * Creates a new {@link LegionJoinRequest} object.<br>
	 * This constructor initializes the request with data from a {@code Player}.<br>
	 * It maps player attributes to the internal fields of this class.
	 * @param legionId The unique identifier for the target legion.
	 * @param player The {@code Player} object providing character details.
	 * @param msg A custom message sent with the join request.
	 */
	public LegionJoinRequest(int legionId, Player player, String msg)
	{
		this.legionId = legionId;
		playerId = player.getObjectId();
		playerName = player.getName();
		playerClass = player.getPlayerClass().ordinal();
		race = player.getRace().getRaceId();
		level = player.getLevel();
		genderId = player.getGender().getGenderId();
		this.msg = msg;
	}
	
	/**
	 * Creates a new instance of {@code LegionJoinRequest}.<br>
	 * This constructor initializes the request with default values.<br>
	 * Use this when you need to build a join request manually.
	 */
	public LegionJoinRequest()
	{
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
	 * Sets the unique identifier for the legion.<br>
	 * This value is used to identify which legion owns this location.
	 * @param legionId The {@code int} ID of the legion.
	 */
	public void setLegionId(int legionId)
	{
		this.legionId = legionId;
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
	 * Sets the unique identifier for the player.<br>
	 * This updates the {@code playerId} field in this request object.
	 * @param playerId The unique ID of the player to set.
	 */
	public void setPlayerId(int playerId)
	{
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
	 * Sets the name of the player.<br>
	 * This updates the {@code playerName} field in this request object.
	 * @param playerName The new name for the player.
	 */
	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}
	
	/**
	 * Retrieves the class ID of the player.<br>
	 * This value is stored in the {@code playerClass} field.
	 * @return The integer ID representing the player's class.
	 */
	public int getPlayerClass()
	{
		return playerClass;
	}
	
	/**
	 * Sets the character class for the player.<br>
	 * This updates the {@code playerClass} field in this request object.
	 * @param playerClass The unique identifier for the player's class.
	 */
	public void setPlayerClass(int playerClass)
	{
		this.playerClass = playerClass;
	}
	
	/**
	 * Retrieves the race of the player.<br>
	 * This value is stored as an {@code int}.
	 * @return The race identifier.
	 */
	public int getRace()
	{
		return race;
	}
	
	/**
	 * Sets the character's race.<br>
	 * This updates the {@code race} field of the current object.
	 * @param race The unique identifier for the character's race.
	 */
	public void setRace(int race)
	{
		this.race = race;
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Sets the current level of the collection.<br>
	 * This updates the {@code level} field with the new value.
	 * @param level The new level to assign to this collection.
	 */
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	/**
	 * Retrieves the unique identifier for this {@code Gender}.<br>
	 * This value corresponds to the internal ID used by the game server.
	 * @return The integer ID of the gender.
	 */
	public int getGenderId()
	{
		return genderId;
	}
	
	/**
	 * Sets the unique identifier for the player's gender.<br>
	 * This value is used to determine the character's appearance.
	 * @param genderId The {@code int} value representing the gender.
	 */
	public void setGenderId(int genderId)
	{
		this.genderId = genderId;
	}
	
	/**
	 * Retrieves the message associated with this join request.<br>
	 * This returns the {@code String} value stored in the {@code msg} field.
	 * @return The message string.
	 */
	public String getMsg()
	{
		return msg;
	}
	
	/**
	 * Sets the message for the {@code LegionJoinRequest}.<br>
	 * This updates the internal {@code msg} field.
	 * @param msg The new message to set.
	 */
	public void setMsg(String msg)
	{
		this.msg = msg;
	}
	
	/**
	 * Retrieves the date and time of the request.<br>
	 * This value is stored as a {@code Timestamp}.
	 * @return the {@code Timestamp} representing when the request was created.
	 */
	public Timestamp getDate()
	{
		return timestamp;
	}
	
	/**
	 * Updates the date of the request.<br>
	 * This method sets the {@code timestamp} field to a new value.
	 * @param timestamp The {@code Timestamp} object to set as the date.
	 */
	public void setDate(Timestamp timestamp)
	{
		this.timestamp = timestamp;
	}
	
}
