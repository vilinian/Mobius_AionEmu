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
package com.aionemu.gameserver.model.gameobjects.player.fame;

import com.aionemu.gameserver.services.player.PlayerFameService;

/**
 * Represents the fame system for a player character.<br>
 * This class manages and stores fame values associated with specific entities or actions.<br>
 * It works in conjunction with {@link PlayerFameService} to handle fame logic.
 */
public class PlayerFame
{
	private final int id;
	private int level;
	private long exp;
	private long expLoss;
	private final FameEnum fameEnum;
	private final int playerId;
	
	/**
	 * Creates a new instance of {@code PlayerFame}.<br>
	 * This constructor initializes the fame data for a specific player.<br>
	 * It also determines the {@link FameEnum} based on the provided {@code id}.
	 * @param id The unique identifier for the fame type.
	 * @param level The current level of the player's fame.
	 * @param exp The current experience points.
	 * @param expLoss The amount of experience lost.
	 * @param playerId The unique identifier of the player.
	 */
	public PlayerFame(int id, int level, long exp, long expLoss, int playerId)
	{
		this.id = id;
		this.level = level;
		this.exp = exp;
		this.expLoss = expLoss;
		fameEnum = FameEnum.getFameById(id);
		this.playerId = playerId;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
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
	 * Retrieves the current experience points of the player.<br>
	 * This value represents the total accumulated {@code exp}.
	 * @return The current experience points as a {@code long}.
	 */
	public long getExp()
	{
		return exp;
	}
	
	/**
	 * Sets the experience points for the player.<br>
	 * This method updates the {@code level} based on the new value.<br>
	 * It also caps the experience at the maximum allowed amount.<br>
	 * If the player is online, it sends a status update packet.
	 * @param exp The new experience value to set.
	 */
	public void setExp(long exp)
	{
		this.exp = exp;
	}
	
	/**
	 * Retrieves the maximum experience points allowed for the current level.<br>
	 * This method calls {@code getExpForLevel} to find the limit.
	 * @return The maximum experience as a {@code Long}.
	 */
	public Long getMaxExp()
	{
		return PlayerFameService.getInstance().getExpForLevel(level);
	}
	
	/**
	 * Retrieves the {@code FameEnum} associated with this player.<br>
	 * This value represents the current fame type.
	 * @return The {@link FameEnum} of the player.
	 */
	public FameEnum getFameEnum()
	{
		return fameEnum;
	}
	
	/**
	 * Retrieves the amount of experience lost by the player.<br>
	 * This value is stored in the {@code expLoss} field.
	 * @return The total experience loss as a {@code long}.
	 */
	public long getExpLoss()
	{
		return expLoss;
	}
	
	/**
	 * Updates the experience loss value for this player.<br>
	 * This method sets the {@code expLoss} field to a new value.
	 * @param expLoss The new amount of experience lost.
	 */
	public void setExpLoss(long expLoss)
	{
		this.expLoss = expLoss;
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
}
