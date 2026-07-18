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
 * Defines the types of attacks performed using a main-hand weapon.<br>
 * This enumeration is used to categorize different combat actions for statistics and mechanics.
 * @author ATracer
 */
public enum MAIN_HAND_ATTACK
{
	WARRIOR(19),
	GLADIATOR(19),
	TEMPLAR(19),
	SCOUT(18),
	ASSASSIN(19),
	RANGER(18),
	MAGE(16),
	SORCERER(16),
	SPIRIT_MASTER(16),
	PRIEST(17),
	CLERIC(19),
	CHANTER(19),
	ENGINEER(18),
	RIDER(19),
	GUNNER(18),
	ARTIST(16),
	PAINTER(16),
	BARD(16);
	
	private final int value;
	
	/**
	 * This is a private constructor for the {@link MAIN_HAND_ATTACK} enum.<br>
	 * It assigns an internal numeric value to each attack type.
	 * @param value The integer value associated with the specific attack.
	 */
	private MAIN_HAND_ATTACK(int value)
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
