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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.condition.Conditions;

/**
 * This class represents a functional calculation used to modify character statistics.<br>
 * It provides the logic for applying specific modifiers to {@link Stat2} values during calculations.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleModifier")
public class StatFunction implements IStatFunction
{
	@XmlAttribute(name = "name")
	protected StatEnum stat;
	@XmlAttribute
	private boolean bonus;
	@XmlAttribute
	protected int value;
	@XmlAttribute(name = "class_type")
	protected String classType;
	@XmlElement(name = "conditions")
	private Conditions conditions;
	
	/**
	 * Creates a new instance of the {@code StatFunction} class.<br>
	 * This is the default constructor for initializing a basic stat modifier.
	 */
	public StatFunction()
	{
	}
	
	/**
	 * Creates a new {@code StatFunction} with specific properties.<br>
	 * This constructor initializes the base statistics for the function.
	 * @param stat The {@link StatEnum} type that this function modifies.
	 * @param value The numerical amount to apply to the statistic.
	 * @param bonus Set to {@code true} if this is a positive bonus, or {@code false} otherwise.
	 */
	public StatFunction(StatEnum stat, int value, boolean bonus)
	{
		this.stat = stat;
		this.value = value;
		this.bonus = bonus;
	}
	
	/**
	 * Compares this {@code StatFunction} with another one.<br>
	 * It first compares the priority of both objects.<br>
	 * If priorities are equal, it uses the hash codes for comparison.
	 * @param o The other {@link IStatFunction} to compare against.
	 * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(IStatFunction o)
	{
		final int result = getPriority() - o.getPriority();
		if (result == 0)
		{
			return hashCode() - o.hashCode();
		}
		
		return result;
	}
	
	/**
	 * Retrieves the type of the class associated with this function.<br>
	 * This value is stored in the {@code classType} field.
	 * @return The name of the class type as a {@code String}.
	 */
	public String getClassType()
	{
		return classType;
	}
	
	/**
	 * Retrieves the owner of this {@code StatFunction}.<br>
	 * This method currently returns {@code null}.
	 * @return The {@link StatOwner} associated with this function, or {@code null}.
	 */
	@Override
	public StatOwner getOwner()
	{
		return null;
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
	 * Checks if this stat function provides a bonus.<br>
	 * This method returns the value of the {@code bonus} field.
	 * @return {@code true} if it is a bonus, otherwise {@code false}.
	 */
	@Override
	public boolean isBonus()
	{
		return bonus;
	}
	
	/**
	 * Returns the execution priority of this function.<br>
	 * This value determines the order in which functions are applied.<br>
	 * The current priority is set to {@code 16}.
	 * @return The integer priority level.
	 */
	@Override
	public int getPriority()
	{
		return 0x10;
	}
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	@Override
	public int getValue()
	{
		return value;
	}
	
	/**
	 * Checks if the current {@link StatFunction} is valid for a specific statistic.<br>
	 * It evaluates the conditions attached to the function.<br>
	 * If no conditions exist, it returns {@code true}.
	 * @param stat The {@code Stat2} object to check against.
	 * @param statFunction The {@link IStatFunction} being validated.
	 * @return {@code true} if the conditions are met or missing, {@code false} otherwise.
	 */
	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction)
	{
		return conditions != null ? conditions.validate(stat, statFunction) : true;
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
	}
	
	/**
	 * Returns a string representation of the {@code StatFunction}.<br>
	 * This string includes the class name and key attributes.
	 * @return A formatted string describing this object.
	 */
	@Override
	public String toString()
	{
		return this.getClass().getName() + " [stat=" + stat + ", bonus=" + bonus + ", value=" + value + ", priority=" + getPriority() + "]";
	}
	
	/**
	 * Creates a new {@link StatFunction} instance with specific requirements.<br>
	 * This method sets the {@code conditions} field of the current object.<br>
	 * It returns the same object to allow for easy chaining.
	 * @param conditions The {@code Conditions} to apply to this function.
	 * @return The current {@link StatFunction} instance.
	 */
	public StatFunction withConditions(Conditions conditions)
	{
		this.conditions = conditions;
		return this;
	}
	
	/**
	 * Checks if this function has any specific requirements.<br>
	 * It returns {@code true} if the {@link Conditions} object is not {@code null}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if conditions exist, {@code false} otherwise.
	 */
	@Override
	public boolean hasConditions()
	{
		return conditions != null;
	}
	
	/**
	 * Combines standard modifiers with random bonuses into a single list.<br>
	 * This method merges values for matching stats and preserves unique entries.<br>
	 * It handles {@code StatAddFunction} and {@code StatRateFunction} types specifically.
	 * @param modifiers The base list of {@link StatFunction} objects to process.
	 * @param rndBonuses The list of random bonuses to merge with the base modifiers.
	 * @return A new {@code List<StatFunction>} containing the merged results.
	 */
	public static List<StatFunction> mergeRandomBonuses(List<StatFunction> modifiers, List<StatFunction> rndBonuses)
	{
		if (modifiers == null)
		{
			modifiers = new ArrayList<>();
		}
		
		if (rndBonuses == null)
		{
			return modifiers;
		}
		
		final List<StatFunction> allModifiers = new ArrayList<>();
		final EnumSet<StatEnum> rndNames = EnumSet.noneOf(StatEnum.class);
		
		for (IStatFunction func : rndBonuses)
		{
			rndNames.add(func.getName());
		}
		
		// add values to original stats
		for (StatFunction modifier : modifiers)
		{
			if (!rndNames.contains(modifier.getName()) || !modifier.isBonus() || modifier.hasConditions())
			{
				allModifiers.add(modifier);
				continue;
			}
			
			IStatFunction rndBonus = null;
			for (IStatFunction func : rndBonuses)
			{
				if (func.getName() == modifier.getName())
				{
					rndBonus = func;
					rndNames.remove(func.getName());
					break;
				}
			}
			
			if (rndBonus == null)
			{
				allModifiers.add(modifier);
				continue;
			}
			
			final int finalValue = modifier.getValue() + rndBonus.getValue();
			
			if (modifier instanceof StatAddFunction)
			{
				if (finalValue != 0)
				{
					allModifiers.add(new StatAddFunction(modifier.getName(), finalValue, true));
				}
			}
			else if (modifier instanceof StatRateFunction)
			{
				if (finalValue != 0)
				{
					allModifiers.add(new StatRateFunction(modifier.getName(), finalValue, true));
				}
			}
			else
			{
				allModifiers.add(modifier);
			}
		}
		
		// add new stat values
		for (StatFunction modifier : rndBonuses)
		{
			if (rndNames.contains(modifier.getName()))
			{
				allModifiers.add(modifier);
			}
		}
		
		return allModifiers;
	}
	
	/**
	 * Generates a random integer value.<br>
	 * This method is currently a placeholder and returns {@code 0}.
	 * @return the generated random number as an {@code int}
	 */
	@Override
	public int getRandomNumber()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
}
