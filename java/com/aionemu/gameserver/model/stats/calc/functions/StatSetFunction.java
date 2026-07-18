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
 * This class handles the calculation of statistics based on specific sets.<br>
 * It allows for defining how {@link Stat2} values are modified by various stat sets.
 * @author ATracer
 */
public class StatSetFunction extends StatFunction
{
	/**
	 * Creates a new instance of the {@code StatSetFunction}.<br>
	 * This class is used to set specific values for character statistics.<br>
	 * It extends the base {@link StatFunction} class.
	 */
	public StatSetFunction()
	{
	}
	
	/**
	 * Creates a new {@code StatSetFunction} with a specific name and value.<br>
	 * This function sets a fixed amount for a given statistic.
	 * @param name The {@link StatEnum} representing the statistic to modify.
	 * @param value The integer value to assign to the statistic.
	 */
	public StatSetFunction(StatEnum name, int value)
	{
		super(name, value, false);
	}
	
	/**
	 * Updates the value of a {@link Stat2} object.<br>
	 * It sets the bonus or base value depending on whether this function is a bonus type.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		if (isBonus())
		{
			stat.setBonus(getValue());
		}
		else
		{
			stat.setBase(getValue());
		}
	}
	
	/**
	 * Returns the execution priority of this function.<br>
	 * This value determines the order in which functions are applied.<br>
	 * The current priority is set to {@code 30}.
	 * @return The integer priority level.
	 */
	@Override
	public int getPriority()
	{
		return isBonus() ? Integer.MAX_VALUE : Integer.MAX_VALUE - 10;
	}
	
	/**
	 * Returns a string representation of this {@code StatSetFunction}.<br>
	 * It includes the base function description.
	 * @return A formatted string representing this object.
	 */
	@Override
	public String toString()
	{
		return "StatSetFunction [" + super.toString() + "]";
	}
}
