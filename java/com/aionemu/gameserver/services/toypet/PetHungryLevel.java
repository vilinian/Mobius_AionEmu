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
package com.aionemu.gameserver.services.toypet;

/**
 * Defines the different hunger levels for a Toy Pet.<br>
 * This enum is used to track and manage the pet's current state of satiety.
 * @author Rolandas
 */
public enum PetHungryLevel
{
	HUNGRY(0),
	CONTENT(1),
	SEMIFULL(2),
	FULL(3);
	
	private final byte value;
	
	/**
	 * Creates a new {@link PetHungryLevel} instance.<br>
	 * This method sets the internal numeric value for the hunger level.
	 * @param value The integer value to assign to this level.
	 */
	PetHungryLevel(int value)
	{
		this.value = (byte) value;
	}
	
	/**
	 * Retrieves the underlying numeric value of the {@code LegionEmblemType}.<br>
	 * This is useful for comparing types in low-level logic.
	 * @return The {@code byte} value associated with this enum constant.
	 */
	public byte getValue()
	{
		return value;
	}
	
	/**
	 * Gets the next {@link PetHungryLevel} in the sequence.<br>
	 * This method cycles through the hunger levels.<br>
	 * It returns {@code HUNGRY} if no higher level exists.
	 * @return The next {@code PetHungryLevel} value.
	 */
	public PetHungryLevel getNextValue()
	{
		final byte levelValue = value;
		switch (levelValue)
		{
			case 0:
				return CONTENT;
			case 1:
				return SEMIFULL;
			case 2:
				return FULL;
			case 3:
				return HUNGRY;
			default:
				return HUNGRY;
		}
	}
	
	/**
	 * Converts an integer ID into a corresponding {@link PetHungryLevel}.<br>
	 * This method maps the {@code value} to the correct enum constant.
	 * @param value The numeric ID of the hunger level.
	 * @return The matching {@code PetHungryLevel} object.
	 */
	public static PetHungryLevel fromId(int value)
	{
		return PetHungryLevel.values()[value];
	}
}
