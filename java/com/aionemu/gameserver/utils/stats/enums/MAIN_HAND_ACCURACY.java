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
 * Defines the accuracy types associated with a character's main hand weapon.<br>
 * This enumeration is used to categorize different accuracy statistics in the game.
 * @author ATracer
 */
public enum MAIN_HAND_ACCURACY
{
	WARRIOR(0),
	GLADIATOR(0),
	TEMPLAR(0),
	SCOUT(0),
	ASSASSIN(0),
	RANGER(0),
	MAGE(0),
	SORCERER(0),
	SPIRIT_MASTER(0),
	PRIEST(0),
	CLERIC(0),
	CHANTER(0),
	ENGINEER(0),
	RIDER(0),
	GUNNER(0),
	ARTIST(0),
	PAINTER(0),
	BARD(0);
	
	private final int value;
	
	/**
	 * Creates a new instance of the {@link MAIN_HAND_ACCURACY} enum.<br>
	 * This method sets the internal numerical value for the accuracy type.
	 * @param value The integer value to assign to this constant.
	 */
	private MAIN_HAND_ACCURACY(int value)
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
