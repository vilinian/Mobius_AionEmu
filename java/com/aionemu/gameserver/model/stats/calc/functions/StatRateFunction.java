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
 * This class provides logic for calculating statistics that are expressed as rates.<br>
 * It handles the mathematical transformations required to convert base values into percentage-based modifiers.<br>
 * It extends {@link StatFunction} to integrate with the core calculation system.
 * @author ATracer
 */
public class StatRateFunction extends StatFunction
{
	/**
	 * Creates a new instance of the {@code StatRateFunction}.<br>
	 * This constructor initializes the function with default values.<br>
	 * Use this when you need a basic rate calculation.
	 */
	public StatRateFunction()
	{
	}
	
	/**
	 * Creates a new {@link StatRateFunction} with specific properties.<br>
	 * This constructor initializes the name, numerical value, and bonus status.
	 * @param name The {@code StatEnum} identifying the statistic type.
	 * @param value The integer amount to apply to the stat.
	 * @param bonus A boolean indicating if this value is treated as a bonus.
	 */
	public StatRateFunction(StatEnum name, int value, boolean bonus)
	{
		super(name, value, bonus);
	}
	
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
			stat.addToBonus((int) ((stat.getBase() * getValue()) / 100f));
		}
		else
		{
			stat.setBase((int) (stat.getBase() * stat.calculatePercent(getValue())));
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
		return isBonus() ? 40 : 20;
	}
	
	/**
	 * Returns a string representation of this {@code StatRateFunction}.<br>
	 * It includes the base information from the parent class.
	 * @return A formatted string representing the current function.
	 */
	@Override
	public String toString()
	{
		return "StatRateFunction [" + super.toString() + "]";
	}
}
