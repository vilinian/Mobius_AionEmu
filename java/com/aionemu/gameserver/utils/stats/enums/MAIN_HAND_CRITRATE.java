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
 * Defines the types of critical hit rates applicable to main-hand weapons.<br>
 * This enum is used by {@code stats} to categorize different crit rate statistics.
 * @author ATracer
 */
public enum MAIN_HAND_CRITRATE
{
	WARRIOR(2),
	GLADIATOR(2),
	TEMPLAR(2),
	SCOUT(3),
	ASSASSIN(3),
	RANGER(3),
	MAGE(1),
	SORCERER(2),
	SPIRIT_MASTER(2),
	PRIEST(2),
	CLERIC(2),
	CHANTER(1),
	ENGINEER(3),
	RIDER(2),
	GUNNER(3),
	ARTIST(1),
	PAINTER(2),
	BARD(2);
	
	private final int value;
	
	/**
	 * Initializes the critical rate for a specific class.<br>
	 * This sets the internal {@code value} field.
	 * @param value The integer value representing the crit rate.
	 */
	private MAIN_HAND_CRITRATE(int value)
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
