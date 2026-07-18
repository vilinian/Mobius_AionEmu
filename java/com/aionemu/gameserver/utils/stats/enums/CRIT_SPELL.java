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
 * Defines the types of critical hits that can be applied to spells.<br>
 * This enumeration is used by the {@code stats} system to categorize spell effects.
 */
public enum CRIT_SPELL
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
	 * Initializes a new {@link CRIT_SPELL} constant.<br>
	 * Sets the internal integer value for this specific spell type.
	 * @param value The numeric value assigned to the spell.
	 */
	private CRIT_SPELL(int value)
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
