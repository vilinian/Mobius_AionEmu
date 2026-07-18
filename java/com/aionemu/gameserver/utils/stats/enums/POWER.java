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
package com.aionemu.gameserver.utils.stats.enums;

/**
 * Defines the different types of power attributes used within the game statistics system.<br>
 * This enumeration is used to categorize various power-related effects and modifiers.
 * @author ATracer
 */
public enum POWER
{
	WARRIOR(110),
	GLADIATOR(115),
	TEMPLAR(115),
	SCOUT(100),
	ASSASSIN(110),
	RANGER(90),
	MAGE(90),
	SORCERER(90),
	SPIRIT_MASTER(90),
	PRIEST(95),
	CLERIC(105),
	CHANTER(110),
	ENGINEER(100),
	RIDER(115),
	GUNNER(100),
	ARTIST(95),
	PAINTER(100),
	BARD(95);
	
	private final int value;
	
	/**
	 * This is a private constructor for the {@link POWER} enum.<br>
	 * It assigns a numeric power level to each specific class type.<br>
	 * The {@code value} represents the base strength of the character.
	 * @param value The integer value assigned to this power level.
	 */
	private POWER(int value)
	{
		this.value = value;
	}
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return value;
	}
}
