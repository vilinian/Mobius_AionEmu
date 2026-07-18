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
package com.aionemu.gameserver.services.mail;

/**
 * Defines the different levels for the Abyss Siege event.<br>
 * This enum is used to categorize rewards and requirements based on siege difficulty.
 * @author Rolandas
 */
public enum AbyssSiegeLevel
{
	NONE(0),
	HERO_DECORATION(1),
	MEDAL(2),
	ELITE_SOLDIER(3),
	VETERAN_SOLDIER(4),
	TITLE(5); // TODO
	
	private final int value;
	
	/**
	 * Creates a new {@link AbyssSiegeLevel} instance.<br>
	 * Sets the internal integer value for this level.
	 * @param value The numeric ID assigned to the siege level.
	 */
	AbyssSiegeLevel(int value)
	{
		this.value = value;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link AbyssSiegeLevel}.<br>
	 * This value corresponds to the internal type code.
	 * @return The integer ID of the level.
	 */
	public int getId()
	{
		return value;
	}
	
	/**
	 * Retrieves an {@link AbyssSiegeLevel} based on its unique identifier.<br>
	 * This method searches through all available levels to find a match.<br>
	 * It throws an exception if the provided {@code id} does not exist.
	 * @param id The integer ID of the level to retrieve.
	 * @return The corresponding {@link AbyssSiegeLevel} object.
	 */
	public static AbyssSiegeLevel getLevelById(int id)
	{
		for (AbyssSiegeLevel al : values())
		{
			if (al.getId() == id)
			{
				return al;
			}
		}
		
		throw new IllegalArgumentException("There is no AbyssSiegeLevel with ID " + id);
	}
}
