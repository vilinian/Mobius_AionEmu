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
package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;

/**
 * This class handles the subtraction logic for calculating character statistics.<br>
 * It is used by {@link StatFunction} to derive a new value from two existing {@code Stat2} objects.
 * @author ATracer
 */
public class StatSubFunction extends StatFunction
{
	/**
	 * Updates the base value of a {@link Stat2} object.<br>
	 * This method calculates a new value based on the owner's agility.<br>
	 * It uses the internal modifier to adjust the result.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		if (isBonus())
		{
			stat.addToBonus(-getValue());
		}
		else
		{
			stat.addToBase(-getValue());
		}
	}
	
	/**
	 * Returns the execution priority of this function.<br>
	 * This value determines the order in which functions are applied.<br>
	 * The priority depends on whether the function is a bonus.
	 * @return The integer priority level.
	 */
	@Override
	public int getPriority()
	{
		return isBonus() ? 50 : 30;
	}
}
