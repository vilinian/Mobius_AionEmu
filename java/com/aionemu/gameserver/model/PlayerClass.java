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

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents the different character classes that a {@link Player} can belong to.<br>
 * This enumeration defines the available职业 types within the game world.
 * @author Luno
 */
@XmlEnum
public enum PlayerClass
{
	WARRIOR(0, true),
	GLADIATOR(1), // fighter
	TEMPLAR(2), // knight
	SCOUT(3, true),
	ASSASSIN(4),
	RANGER(5),
	MAGE(6, true),
	SORCERER(7), // wizard
	SPIRIT_MASTER(8), // elementalist
	PRIEST(9, true),
	CLERIC(10),
	CHANTER(11),
	ENGINEER(12, true),
	RIDER(13),
	GUNNER(14),
	ARTIST(15, true),
	BARD(16),
	PAINTER(17),
	ALL(18);
	
	/**
	 * This id is used on client side
	 */
	private final byte classId;
	/**
	 * This is the mask for this class id, used with bitwise AND in arguments that contain more than one possible class
	 */
	private final int idMask;
	/**
	 * Tells whether player can create new character with this class
	 */
	private final boolean startingClass;
	
	/**
	 * This is a private constructor for the {@link PlayerClass} enum.<br>
	 * It initializes a class that is not a starting class.<br>
	 * It uses the provided {@code classId} to set the internal ID.
	 * @param classId The unique identifier for the player class.
	 */
	private PlayerClass(int classId)
	{
		this(classId, false);
	}
	
	/**
	 * Creates a new {@link PlayerClass} instance with specific properties.<br>
	 * This constructor initializes the ID and the starting status.<br>
	 * It also calculates the bitmask based on the provided ID.
	 * @param classId The unique identifier for the class used by the client.
	 * @param startingClass Determines if a player can choose this as their initial class.
	 */
	private PlayerClass(int classId, boolean startingClass)
	{
		this.classId = (byte) classId;
		this.startingClass = startingClass;
		idMask = (int) Math.pow(2, classId);
	}
	
	/**
	 * Retrieves the unique identifier for this {@link PlayerClass}.<br>
	 * This ID is primarily used by the client side.
	 * @return the {@code byte} value of the class ID.
	 */
	public byte getClassId()
	{
		return classId;
	}
	
	/**
	 * Finds a {@link PlayerClass} based on its unique ID.<br>
	 * This method searches through all available classes.<br>
	 * It returns the matching class if found.
	 * @param classId The byte identifier of the class to find.
	 * @return The corresponding {@code PlayerClass} object.
	 */
	public static PlayerClass getPlayerClassById(byte classId)
	{
		for (PlayerClass pc : values())
		{
			if (pc.getClassId() == classId)
			{
				return pc;
			}
		}
		
		throw new IllegalArgumentException("There is no player class with id " + classId);
	}
	
	/**
	 * Checks if this class is available for new characters.<br>
	 * This method returns the value of the {@code startingClass} field.
	 * @return {@code true} if it is a starting class, {@code false} otherwise.
	 */
	public boolean isStartingClass()
	{
		return startingClass;
	}
	
	/**
	 * This method finds the base starting class for a given {@link PlayerClass}.<br>
	 * It maps advanced classes back to their primary parent categories.
	 * @param pc The {@code PlayerClass} to check.
	 * @return The corresponding starting {@code PlayerClass}.
	 */
	public static PlayerClass getStartingClassFor(PlayerClass pc)
	{
		// TODO: remove that shit, we already have everything in the enum itself!
		switch (pc)
		{
			case ASSASSIN:
			case RANGER:
				return SCOUT;
			case GLADIATOR:
			case TEMPLAR:
				return WARRIOR;
			case CHANTER:
			case CLERIC:
				return PRIEST;
			case SORCERER:
			case SPIRIT_MASTER:
				return MAGE;
			case GUNNER:
			case RIDER:
				return ENGINEER;
			case BARD:
			case PAINTER:
				return ARTIST;
			case SCOUT:
			case WARRIOR:
			case PRIEST:
			case MAGE:
			case ENGINEER:
			case ARTIST:
				return pc;
			default:
				throw new IllegalArgumentException("Given player class is starting class: " + pc);
		}
	}
	
	/**
	 * Converts a string name into its corresponding {@link PlayerClass} enum constant.<br>
	 * This method searches through all available classes to find a match.
	 * @param fieldName The string name of the class to look for.
	 * @return The matching {@code PlayerClass} or {@code null} if no match is found.
	 */
	public static PlayerClass getPlayerClassByString(String fieldName)
	{
		for (PlayerClass pc : values())
		{
			if (pc.toString().equals(fieldName))
			{
				return pc;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the bitmask associated with this {@code PlayerClass}.<br>
	 * This value is used for bitwise AND operations.<br>
	 * It helps identify classes when multiple options are allowed.
	 * @return The integer mask for this class.
	 */
	public int getMask()
	{
		return idMask;
	}
	
	/**
	 * Gets the class type for a specific {@link Player}.<br>
	 * This method determines the category of the player's character.<br>
	 * It currently returns {@code null} for all cases.
	 * @param player The {@code Player} object to check.
	 * @return A {@code String} representing the class type or {@code null}.
	 */
	public String getClassType(Player player)
	{
		switch (player.getPlayerClass())
		{
			case ASSASSIN:
			case RANGER:
			case GLADIATOR:
			case TEMPLAR:
			case PAINTER:
			case GUNNER:
			{
				break;
			}
			case CHANTER:
			case CLERIC:
			case SORCERER:
			case SPIRIT_MASTER:
			case BARD:
			case RIDER:
			{
				break;
			}
			default:
				break;
		}
		
		return null;
	}
}
