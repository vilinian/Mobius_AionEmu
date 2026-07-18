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
package com.aionemu.gameserver.model.stats.calc;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This class handles the calculation of additive statistics for a {@link Creature}.<br>
 * It provides a base implementation for stats that are summed together rather than multiplied.<br>
 * It extends the functionality provided by {@link Stat2}.
 * @author ATracer
 */
public class AdditionStat extends Stat2
{
	/**
	 * Creates a new {@link AdditionStat} instance.<br>
	 * This constructor initializes the stat with a base value and an owner.
	 * @param stat The type of statistic to create.
	 * @param base The initial base value for this statistic.
	 * @param owner The {@link Creature} that owns this statistic.
	 */
	public AdditionStat(StatEnum stat, int base, Creature owner)
	{
		super(stat, base, owner);
	}
	
	/**
	 * Creates a new {@link AdditionStat} instance.<br>
	 * This constructor initializes the stat with a specific base value and rate.
	 * @param stat The type of statistic to be modified.
	 * @param base The initial base value for this statistic.
	 * @param owner The {@link Creature} that owns this statistic.
	 * @param bonusRate The multiplier applied to the bonus calculation.
	 */
	public AdditionStat(StatEnum stat, int base, Creature owner, float bonusRate)
	{
		super(stat, base, owner, bonusRate);
	}
	
	/**
	 * Updates the current {@code base} value of this statistic.<br>
	 * It adds the provided amount to the existing total.
	 * @param base The amount to add to the base value.
	 */
	@Override
	public void addToBase(int base)
	{
		this.base += base;
	}
	
	/**
	 * Adds a specific value to the current bonus.<br>
	 * This method updates the internal state of the {@code AdditionStat}.
	 * @param bonus The amount to add to the bonus.
	 */
	@Override
	public void addToBonus(int bonus)
	{
		this.bonus += bonusRate * bonus;
	}
	
	/**
	 * Calculates a percentage value based on a given change.<br>
	 * This method converts the {@code delta} into a multiplier.<br>
	 * It adds {@code 100} to the input and divides by {@code 100f}.
	 * @param delta The amount of change to apply.
	 * @return The calculated percentage as a {@code float}.
	 */
	@Override
	public float calculatePercent(int delta)
	{
		return (100 + delta) / 100f;
	}
}
