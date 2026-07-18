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
 * This enum defines the different types of knowledge available in the game.<br>
 * It is used to categorize and manage various player progression stats.
 * @author ATracer
 */
public enum KNOWLEDGE
{
	WARRIOR(90),
	GLADIATOR(90),
	TEMPLAR(90),
	SCOUT(90),
	ASSASSIN(90),
	RANGER(120),
	MAGE(115),
	SORCERER(120),
	SPIRIT_MASTER(115),
	PRIEST(100),
	CLERIC(105),
	CHANTER(105),
	ENGINEER(100),
	RIDER(105),
	GUNNER(100),
	ARTIST(115),
	PAINTER(100),
	BARD(115);
	
	private final int value;
	
	/**
	 * Initializes a new {@link KNOWLEDGE} constant.<br>
	 * Sets the internal {@code value} for this specific type.
	 * @param value The integer value associated with the knowledge type.
	 */
	private KNOWLEDGE(int value)
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
