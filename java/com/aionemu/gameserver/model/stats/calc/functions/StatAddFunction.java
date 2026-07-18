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
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This class handles the addition of {@link Stat2} values during stat calculations.<br>
 * It provides a basic implementation for summing statistics within the calculation engine.
 * @author ATracer
 */
public class StatAddFunction extends StatFunction
{
	/**
	 * Creates a new instance of the {@code StatAddFunction}.<br>
	 * This class is used to add values to specific character statistics.<br>
	 * It provides a default constructor for initializing the function.
	 */
	public StatAddFunction()
	{
	}
	
	/**
	 * Creates a new {@code StatAddFunction} instance.<br>
	 * This function adds a specific amount to a character's statistic.
	 * @param name The {@link StatEnum} type of the statistic to modify.
	 * @param value The numerical amount to add to the statistic.
	 * @param bonus Whether this addition should be treated as a bonus.
	 */
	public StatAddFunction(StatEnum name, int value, boolean bonus)
	{
		super(name, value, bonus);
	}
	
	/**
	 * Updates the {@link Stat2} object based on this function's value.<br>
	 * It adds the value to the bonus if {@code isBonus()} is true.<br>
	 * Otherwise, it adds the value to the base stat.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		if (isBonus())
		{
			stat.addToBonus(getValue());
		}
		else
		{
			stat.addToBase(getValue());
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
	
	/**
	 * Returns a string representation of this function.<br>
	 * It includes the base class information and the specific function name.
	 * @return A formatted string representing this {@code StatAddFunction}.
	 */
	@Override
	public String toString()
	{
		return "StatAddFunction [" + super.toString() + "]";
	}
}
