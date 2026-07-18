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
 * This class serves as a base for calculating various character statistics.<br>
 * It provides the core logic used by {@link Creature} to determine their attributes.
 * @author ATracer
 */
public abstract class Stat2
{
	float bonusRate = 1f;
	int base;
	int bonus;
	private final Creature owner;
	protected final StatEnum stat;
	
	/**
	 * Creates a new {@link Stat2} instance with a default bonus rate.<br>
	 * This constructor initializes the stat with a base value of {@code 1f}.
	 * @param stat The type of statistic to create.
	 * @param base The initial base value for this statistic.
	 * @param owner The {@link Creature} that owns this statistic.
	 */
	public Stat2(StatEnum stat, int base, Creature owner)
	{
		this(stat, base, owner, 1);
	}
	
	/**
	 * Creates a new {@link Stat2} instance with specific values.<br>
	 * This constructor initializes the stat type, base value, and owner.<br>
	 * It also sets the initial bonus rate for the calculation.
	 * @param stat The {@link StatEnum} type of this statistic.
	 * @param base The starting base value for the statistic.
	 * @param owner The {@link Creature} that owns this statistic.
	 * @param bonusRate The multiplier used to calculate bonuses.
	 */
	public Stat2(StatEnum stat, int base, Creature owner, float bonusRate)
	{
		this.stat = stat;
		this.base = base;
		this.owner = owner;
		this.bonusRate = bonusRate;
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
	 * Retrieves the base value of the statistic.<br>
	 * This value represents the initial amount before any bonuses are applied.
	 * @return The {@code int} base value.
	 */
	public int getBase()
	{
		return base;
	}
	
	/**
	 * Sets the base value for this statistic.<br>
	 * This updates the {@code base} field of the current object.
	 * @param base The new integer value to set as the base.
	 */
	public void setBase(int base)
	{
		this.base = base;
	}
	
	public abstract void addToBase(int base);
	
	/**
	 * Retrieves the current bonus value for this statistic.<br>
	 * This value is used to calculate the final stat amount.
	 * @return The {@code int} value of the bonus.
	 */
	public int getBonus()
	{
		return bonus;
	}
	
	/**
	 * Calculates the current value of the statistic.<br>
	 * It adds the {@code base} value to the {@code bonus} value.
	 * @return The sum of the {@code base} and {@code bonus} values.
	 */
	public int getCurrent()
	{
		return base + bonus;
	}
	
	/**
	 * Updates the bonus value for this statistic.<br>
	 * This method sets the {@code bonus} field to a new value.
	 * @param bonus The new integer value to assign to the bonus.
	 */
	public void setBonus(int bonus)
	{
		this.bonus = bonus;
	}
	
	/**
	 * Retrieves the current multiplier for this statistic.<br>
	 * This value is used to calculate the final bonus amount.
	 * @return The {@code float} value of the bonus rate.
	 */
	public float getBonusRate()
	{
		return bonusRate;
	}
	
	/**
	 * Sets the multiplier for the bonus value.<br>
	 * This updates the {@code bonusRate} field of this {@link Stat2} instance.
	 * @param bonusRate The new rate to apply as a float.
	 */
	public void setBonusRate(float bonusRate)
	{
		this.bonusRate = bonusRate;
	}
	
	public abstract void addToBonus(int bonus);
	
	public abstract float calculatePercent(int delta);
	
	/**
	 * Retrieves the {@link Creature} that owns this AI instance.
	 * @return the {@code Creature} owner of this AI.
	 */
	public Creature getOwner()
	{
		return owner;
	}
	
	/**
	 * Returns a string representation of this {@link Stat2} object.<br>
	 * It displays the name of the {@code stat}, the {@code base} value, and the {@code bonus} value.
	 * @return A formatted string containing the statistics information.
	 */
	@Override
	public String toString()
	{
		return "[" + stat.name() + " base=" + base + ", bonus=" + bonus + "]";
	}
}
