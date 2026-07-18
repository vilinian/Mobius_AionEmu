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
 * Defines the types of health statistics available in the game.<br>
 * This enum is used to categorize different {@code HEALTH} values throughout the server.
 * @author ATracer
 */
public enum HEALTH
{
	WARRIOR(110),
	GLADIATOR(115),
	TEMPLAR(100),
	SCOUT(100),
	ASSASSIN(100),
	RANGER(90),
	MAGE(90),
	SORCERER(90),
	SPIRIT_MASTER(90),
	PRIEST(95),
	CLERIC(110),
	CHANTER(105),
	ENGINEER(100),
	RIDER(90),
	GUNNER(100),
	ARTIST(90),
	PAINTER(100),
	BARD(95);
	
	private final int value;
	
	/**
	 * Creates a new {@link HEALTH} constant.<br>
	 * Sets the internal health value for this specific type.
	 * @param value The integer value assigned to this health type.
	 */
	private HEALTH(int value)
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
