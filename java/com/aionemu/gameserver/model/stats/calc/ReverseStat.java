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
 * This class handles the calculation of stats that have an inverse relationship with base values.<br>
 * It extends {@link Stat2} to provide specific logic for reversed stat modifiers.
 * @author ATracer
 */
public class ReverseStat extends Stat2
{
	/**
	 * Creates a new {@link ReverseStat} instance.<br>
	 * This constructor initializes the stat with a specific base value for a creature.
	 * @param stat The type of {@link StatEnum} to be used.
	 * @param base The initial integer value for the stat.
	 * @param owner The {@link Creature} that owns this stat.
	 */
	public ReverseStat(StatEnum stat, int base, Creature owner)
	{
		super(stat, base, owner);
	}
	
	/**
	 * Creates a new {@link ReverseStat} instance.<br>
	 * This constructor initializes the stat with a specific base value and rate.
	 * @param stat The type of statistic to track.
	 * @param base The initial base value for this statistic.
	 * @param owner The {@code Creature} that owns this statistic.
	 * @param bonusRate The multiplier applied to the bonus calculation.
	 */
	public ReverseStat(StatEnum stat, int base, Creature owner, float bonusRate)
	{
		super(stat, base, owner, bonusRate);
	}
	
	/**
	 * Subtracts a value from the current {@code base} statistic.<br>
	 * The resulting value will not drop below {@code 0}.
	 * @param base The amount to subtract from the base value.
	 */
	@Override
	public void addToBase(int base)
	{
		this.base -= base;
		if (this.base < 0)
		{
			this.base = 0;
		}
	}
	
	/**
	 * Subtracts a calculated value from the current bonus.<br>
	 * This method updates the internal state based on the {@code bonusRate}.
	 * @param bonus The amount to be processed.
	 */
	@Override
	public void addToBonus(int bonus)
	{
		this.bonus -= bonusRate * bonus;
	}
	
	/**
	 * Calculates a percentage value based on a given change.<br>
	 * This method converts the {@code delta} into a multiplier.<br>
	 * It subtracts {@code delta} from {@code 100} and divides by {@code 100f}.<br>
	 * The result is clamped to a minimum of {@code 0}.
	 * @param delta The amount of change to apply.
	 * @return The calculated percentage as a {@code float}.
	 */
	@Override
	public float calculatePercent(int delta)
	{
		final float percent = (100 - delta) / 100f;
		
		// TODO need double check here for negatives
		return percent < 0 ? 0 : percent;
	}
}
