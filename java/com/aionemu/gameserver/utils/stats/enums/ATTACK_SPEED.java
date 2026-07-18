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
 * Defines the various types of attack speed modifiers available in the game.<br>
 * This enum is used to categorize and handle different speed calculations for character actions.
 * @author ATracer
 */
public enum ATTACK_SPEED
{
	WARRIOR(1500),
	GLADIATOR(1500),
	TEMPLAR(1500),
	SCOUT(1500),
	ASSASSIN(1500),
	RANGER(1500),
	MAGE(1500),
	SORCERER(1500),
	SPIRIT_MASTER(1500),
	PRIEST(1500),
	CLERIC(1500),
	CHANTER(1500),
	ENGINEER(1500),
	RIDER(1500),
	GUNNER(1500),
	ARTIST(1500),
	PAINTER(1500),
	BARD(1500);
	
	private final int value;
	
	/**
	 * Initializes the attack speed for a specific class.<br>
	 * This sets the internal {@code value} field.
	 * @param value The numerical attack speed to assign.
	 */
	private ATTACK_SPEED(int value)
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
