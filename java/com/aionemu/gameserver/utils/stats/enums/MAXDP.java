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
 * Defines the maximum Defense Points {@code DP} values for different character types.<br>
 * This enumeration is used to cap the defensive statistics of entities in the game.
 * @author ATracer
 */
public enum MAXDP
{
	WARRIOR(100),
	GLADIATOR(100),
	TEMPLAR(100),
	SCOUT(100),
	ASSASSIN(100),
	RANGER(100),
	MAGE(100),
	SORCERER(100),
	SPIRIT_MASTER(100),
	PRIEST(100),
	CLERIC(100),
	CHANTER(100),
	ENGINEER(100),
	RIDER(100),
	GUNNER(100),
	ARTIST(100),
	PAINTER(100),
	BARD(100);
	
	private final int value;
	
	/**
	 * This is a private constructor for the {@link MAXDP} enum.<br>
	 * It assigns an initial integer value to each constant.
	 * @param value The numeric value associated with the specific class.
	 */
	private MAXDP(int value)
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
