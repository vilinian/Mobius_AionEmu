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
package com.aionemu.gameserver.model.gameobjects;

/**
 * Defines the different types of letters available in the game.<br>
 * This enum is used to categorize various mail and message objects within the {@code gameobjects} package.
 * @author ginho1
 */
public enum LetterType
{
	NORMAL(0),
	EXPRESS(1),
	BLACKCLOUD(2);
	
	/**
	 * Creates a new instance of {@link LetterType}.<br>
	 * This constructor assigns the unique identifier to the enum constant.
	 * @param id The integer value representing the letter type.
	 */
	private LetterType(int id)
	{
		this.id = id;
	}
	
	private final int id;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Finds a {@link LetterType} based on its unique identifier.<br>
	 * This method searches through all available types.<br>
	 * It throws an exception if the ID is not found.
	 * @param id The integer ID of the letter type to find.
	 * @return The corresponding {@code LetterType} object.
	 */
	public static LetterType getLetterTypeById(int id)
	{
		for (LetterType lt : values())
		{
			if (lt.id == id)
			{
				return lt;
			}
		}
		
		throw new IllegalArgumentException("Unsupported revive type: " + id);
	}
}
