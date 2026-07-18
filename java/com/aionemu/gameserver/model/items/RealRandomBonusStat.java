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
package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * Represents a random bonus statistic applied to an item.<br>
 * This class handles the logic for generating and storing variable stats that are not fixed.<br>
 * It is used by {@code Item} to manage dynamic attributes.
 */
public class RealRandomBonusStat
{
	private final StatEnum stat;
	private final int value;
	boolean fusion;
	
	/**
	 * Creates a new instance of {@code RealRandomBonusStat}.<br>
	 * This constructor initializes the statistics for an item.
	 * @param stat The type of statistic to apply from {@link StatEnum}.
	 * @param value The numerical amount of the statistic.
	 * @param fusion Set to {@code true} if this is a fusion bonus, otherwise {@code false}.
	 */
	public RealRandomBonusStat(StatEnum stat, int value, boolean fusion)
	{
		this.stat = stat;
		this.value = value;
		this.fusion = fusion;
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
	
	/**
	 * Retrieves the {@code StatEnum} associated with this bonus.<br>
	 * This method returns the specific type of statistic for the random bonus.
	 * @return The {@link StatEnum} value.
	 */
	public StatEnum getStat()
	{
		return stat;
	}
	
	/**
	 * Checks if this bonus stat is a result of a fusion.<br>
	 * This method returns the value of the {@code fusion} field.
	 * @return {@code true} if it is a fusion, {@code false} otherwise.
	 */
	public boolean isFusion()
	{
		return fusion;
	}
}
