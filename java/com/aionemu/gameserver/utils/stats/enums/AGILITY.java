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
 * Represents the {@code AGILITY} stat type within the game server.<br>
 * This enum is used to identify and handle agility-related mechanics.
 * @author ATracer
 */
public enum AGILITY
{
	WARRIOR(100),
	GLADIATOR(100),
	TEMPLAR(110),
	SCOUT(100),
	ASSASSIN(100),
	RANGER(100),
	MAGE(95),
	SORCERER(100),
	SPIRIT_MASTER(100),
	PRIEST(100),
	CLERIC(90),
	CHANTER(90),
	ENGINEER(110),
	RIDER(100),
	GUNNER(110),
	ARTIST(100),
	PAINTER(105),
	BARD(100);
	
	private final int value;
	
	/**
	 * Creates a new {@link AGILITY} constant.<br>
	 * Sets the internal numerical value for this specific type.
	 * @param value The integer value to assign to this enum member.
	 */
	private AGILITY(int value)
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
