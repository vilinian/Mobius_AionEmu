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

/**
 * Represents the current status of a player's bonus time.<br>
 * This enum is used to track whether a player has active, pending, or expired bonus time.
 * @author Steve
 * @modified Alex
 */
public enum PlayerBonusTimeStatus
{
	NORMAL(1),
	NEW(2),
	RETURN(3),
	BONUS(RETURN.id | NEW.id);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link PlayerBonusTimeStatus}.<br>
	 * This constructor assigns the unique identifier to the status.
	 * @param id The integer value representing the status ID.
	 */
	private PlayerBonusTimeStatus(int id)
	{
		this.id = id;
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
	 * Checks if the current status is a bonus type.<br>
	 * It compares the current {@code id} against the {@code BONUS} bitmask.
	 * @return {@code true} if the status is a bonus, otherwise {@code false}.
	 */
	public boolean isBonus()
	{
		return (BONUS.id & getId()) == getId();
	}
}
