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

import javax.xml.bind.annotation.XmlEnum;

/**
 * Represents the gender of a creature in the game.<br>
 * This enum defines types such as {@code MALE} and {@code FEMALE}.
 * @author SoulKeeper
 */
@XmlEnum
public enum Gender
{
	/**
	 * Males
	 */
	MALE(0),
	/**
	 * Females
	 */
	FEMALE(1),
	/**
	 * Dummy for create
	 */
	DUMMY(8);
	
	/**
	 * id of gender
	 */
	private final int genderId;
	
	/**
	 * Creates a new {@link Gender} instance.<br>
	 * Sets the internal {@code genderId} value.
	 * @param genderId The unique identifier for the gender.
	 */
	private Gender(int genderId)
	{
		this.genderId = genderId;
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
	 * Retrieves a {@link Gender} constant based on its unique ID.<br>
	 * This method searches through all available values.<br>
	 * It throws an exception if the provided ID is not found.
	 * @param genderId The integer ID of the gender to find.
	 * @return The corresponding {@code Gender} enum value.
	 */
	public static Gender getGenderById(int genderId)
	{
		for (Gender gender : values())
		{
			if (gender.getGenderId() == genderId)
			{
				return gender;
			}
		}
		
		throw new IllegalArgumentException("There is no player gender with id " + genderId);
	}
	
	/**
	 * Converts a string representation into a {@link Gender} constant.<br>
	 * This method searches through all available values.<br>
	 * It returns the matching enum or {@code null} if no match is found.
	 * @param fieldName The string name of the gender to look up.
	 * @return The corresponding {@code Gender} object or {@code null}.
	 */
	public static Gender getGenderByString(String fieldName)
	{
		for (Gender gender : values())
		{
			if (gender.toString().equals(fieldName))
			{
				return gender;
			}
		}
		
		return null;
	}
}
