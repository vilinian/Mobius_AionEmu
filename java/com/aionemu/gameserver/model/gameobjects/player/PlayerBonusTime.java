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

import java.sql.Timestamp;

/**
 * Represents the bonus time data for a {@link Player}.<br>
 * This class manages the duration and expiration of extra playtime granted to users.
 * @author Steve
 * @modified Alex
 */
public class PlayerBonusTime
{
	private Timestamp time;
	private PlayerBonusTimeStatus status;
	
	/**
	 * Creates a new instance of {@link PlayerBonusTime}.<br>
	 * The {@code time} field is initialized to {@code null}.<br>
	 * The {@code status} field is set to {@code PlayerBonusTimeStatus.NORMAL}.
	 */
	public PlayerBonusTime()
	{
		time = null;
		status = PlayerBonusTimeStatus.NORMAL;
	}
	
	/**
	 * Sets the expiration time for the player bonus.<br>
	 * This updates the {@code time} field of this object.
	 * @param time The {@code Timestamp} to set.
	 */
	public void setTime(Timestamp time)
	{
		this.time = time;
	}
	
	/**
	 * Updates the current status of the player bonus time.<br>
	 * This method sets the {@code status} field to a new value.
	 * @param status The new {@link PlayerBonusTimeStatus} to assign.
	 */
	public void setStatus(PlayerBonusTimeStatus status)
	{
		this.status = status;
	}
	
	/**
	 * Retrieves the current bonus time.<br>
	 * This method returns the {@code Timestamp} value stored in this object.
	 * @return The {@code Timestamp} representing the bonus time.
	 */
	public Timestamp getTime()
	{
		return time;
	}
	
	/**
	 * Retrieves the current status of the player's bonus time.<br>
	 * This method returns the {@code PlayerBonusTimeStatus} object.
	 * @return The current {@link PlayerBonusTimeStatus}.
	 */
	public PlayerBonusTimeStatus getStatus()
	{
		return status;
	}
	
	/**
	 * Checks if the current bonus time is active.<br>
	 * This method calls {@code isBonus}.
	 * @return {@code true} if it is a bonus, otherwise {@code false}.
	 */
	public boolean isBonus()
	{
		return getStatus().isBonus();
	}
}
