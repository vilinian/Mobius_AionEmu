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
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This class acts as a proxy for {@link IStatFunction} implementations.<br>
 * It allows for the dynamic execution of various stat calculation functions.<br>
 * It simplifies how the system handles different types of {@code Stat2} calculations.
 * @author ATracer
 */
public class StatFunctionProxy implements IStatFunction
{
	private final StatOwner owner;
	private final IStatFunction proxiedFunction;
	private final StatEnum stat;
	
	/**
	 * Creates a new {@link StatFunctionProxy} instance.<br>
	 * This constructor links the proxy to a specific {@link StatOwner}.<br>
	 * It also wraps an existing {@link IStatFunction} to delegate its logic.
	 * @param owner The {@link StatOwner} that owns this statistic.
	 * @param statFunction The {@link IStatFunction} being proxied.
	 */
	public StatFunctionProxy(StatOwner owner, IStatFunction statFunction)
	{
		this.owner = owner;
		proxiedFunction = statFunction;
		stat = statFunction.getName();
	}
	
	/**
	 * Creates a new {@link StatFunctionProxy} instance.<br>
	 * This constructor links an owner, a function, and a specific statistic.
	 * @param owner The {@link StatOwner} that owns this proxy.
	 * @param statFunction The {@link IStatFunction} being proxied.
	 * @param statEnum The {@link StatEnum} associated with this proxy.
	 */
	public StatFunctionProxy(StatOwner owner, IStatFunction statFunction, StatEnum statEnum)
	{
		this.owner = owner;
		proxiedFunction = statFunction;
		stat = statEnum;
	}
	
	/**
	 * Retrieves the underlying {@link IStatFunction} that this proxy wraps.<br>
	 * This is useful when you need to access the original logic behind the proxy.
	 * @return The {@code IStatFunction} instance being proxied.
	 */
	public IStatFunction getProxiedFunction()
	{
		return proxiedFunction;
	}
	
	/**
	 * Returns a hash code value for this {@link StatFunctionProxy} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code owner} field.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}
	
	/**
	 * Compares this object with another object for equality.<br>
	 * It checks if both objects are of the same class and have matching owners.
	 * @param obj The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if ((obj == null) || (getClass() != obj.getClass()))
		{
			return false;
		}
		
		final StatFunctionProxy other = (StatFunctionProxy) obj;
		if (owner == null)
		{
			if (other.owner != null)
			{
				return false;
			}
		}
		else if (!owner.equals(other.owner))
		{
			return false;
		}
		
		// if (other.isBonus() != this.isBonus())
		// return false;
		// return other.getRandomNumber() == this.getRandomNumber();
		return true;
	}
	
	/**
	 * Compares this proxy with another {@link IStatFunction}.<br>
	 * This method delegates the comparison to the underlying proxied function.
	 * @param o The other {@link IStatFunction} to compare against.
	 * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(IStatFunction o)
	{
		return proxiedFunction.compareTo(o);
	}
	
	/**
	 * Retrieves the owner of this {@code StatFunction}.<br>
	 * This method currently returns {@code null}.
	 * @return The {@link StatOwner} associated with this function, or {@code null}.
	 */
	@Override
	public StatOwner getOwner()
	{
		return owner;
	}
	
	/**
	 * Retrieves the name of the statistic associated with this function.<br>
	 * This returns a {@link StatEnum} value.
	 * @return The {@code StatEnum} representing the name.
	 */
	@Override
	public StatEnum getName()
	{
		return stat;
	}
	
	/**
	 * Checks if the proxied function is considered a bonus.<br>
	 * This method delegates the check to {@code getProxiedFunction}.
	 * @return {@code true} if it is a bonus, otherwise {@code false}.
	 */
	@Override
	public boolean isBonus()
	{
		return proxiedFunction.isBonus();
	}
	
	/**
	 * Retrieves a random number from the proxied function.<br>
	 * This method delegates the request to {@code getProxiedFunction}.
	 * @return the random number as an {@code int}
	 */
	@Override
	public int getRandomNumber()
	{
		return proxiedFunction.getRandomNumber();
	}
	
	/**
	 * Gets the execution priority of the underlying function.<br>
	 * This value determines the order in which functions are applied.<br>
	 * It returns the priority from the {@link IStatFunction} being proxied.
	 * @return The integer priority level.
	 */
	@Override
	public int getPriority()
	{
		return proxiedFunction.getPriority();
	}
	
	/**
	 * Retrieves the value from the proxied function.<br>
	 * This method delegates the call to {@code getProxiedFunction}.
	 * @return The numerical value returned by the proxied function.
	 */
	@Override
	public int getValue()
	{
		return proxiedFunction.getValue();
	}
	
	/**
	 * Checks if the current {@link IStatFunction} is valid for a specific statistic.<br>
	 * It evaluates the conditions attached to the function.<br>
	 * If no conditions exist, it returns {@code true}.
	 * @param stat The {@code Stat2} object to check against.
	 * @param statFunction The {@link IStatFunction} being validated.
	 * @return {@code true} if the conditions are met or missing, {@code false} otherwise.
	 */
	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction)
	{
		return proxiedFunction.validate(stat, statFunction);
	}
	
	/**
	 * Applies the logic of the proxied function to the provided {@link Stat2} object.<br>
	 * This method delegates the calculation to the internal {@code proxiedFunction}.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		proxiedFunction.apply(stat);
	}
	
	/**
	 * Checks if the proxied function has any specific requirements.<br>
	 * It returns {@code true} if conditions exist.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if conditions exist, {@code false} otherwise.
	 */
	@Override
	public boolean hasConditions()
	{
		return proxiedFunction.hasConditions();
	}
	
	/**
	 * Returns a string representation of this proxy object.<br>
	 * It includes the name, bonus status, value, priority, and owner.
	 * @return A formatted string describing the {@code StatFunctionProxy}.
	 */
	@Override
	public String toString()
	{
		return "Proxy [name=" + proxiedFunction.getName() + ", bonus=" + isBonus() + ", value=" + getValue() + ", priority=" + getPriority() + ", owner=" + owner + "]";
	}
}
