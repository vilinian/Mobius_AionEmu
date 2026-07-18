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

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This utility class provides methods to handle and validate maximum limits for character statistics.<br>
 * It ensures that {@link StatEnum} values do not exceed their predefined caps during calculations.
 * @author ATracer
 */
public class StatCapUtil
{
	protected static final Logger log = LoggerFactory.getLogger(StatCapUtil.class);
	static final int LOWER_CAP = Short.MIN_VALUE; // -32767
	static final int UPPER_CAP = Short.MAX_VALUE; // 32767
	
	static class StatLimits
	{
		public final int lowerCap;
		public final int upperCap;
		
		public StatLimits()
		{
			lowerCap = LOWER_CAP;
			upperCap = UPPER_CAP;
		}
		
		public StatLimits(int lowerCap, int upperCap)
		{
			this.lowerCap = lowerCap;
			this.upperCap = upperCap;
		}
	}
	
	static HashMap<StatEnum, Integer> minValues = new HashMap<>();
	static HashMap<StatEnum, Integer> maxValues = new HashMap<>();
	static HashMap<StatEnum, StatLimits> limits = new HashMap<>();
	static
	{
		for (StatEnum stat : StatEnum.values())
		{
			minValues.put(stat, getLowerCap(stat));
			maxValues.put(stat, getUpperCap(stat));
			limits.put(stat, new StatLimits(getLowerCap(stat), getUpperCap(stat)));
		}
	}
	
	/**
	 * Calculates the base value for a specific statistic.<br>
	 * This method updates the {@code Stat2} object based on its type and player status.<br>
	 * It handles special logic for speed types and applies caps using {@code int, int)}.
	 * @param stat The {@code Stat2} object to update.
	 * @param isPlayer A byte flag where {@code 1} represents a player and {@code 2} represents an admin.
	 */
	public static void calculateBaseValue(Stat2 stat, byte isPlayer)
	{
		final int lowerCap = getLowerCap(stat.getStat());
		int upperCap = getUpperCap(stat.getStat());
		
		if (stat.getStat() == StatEnum.ATTACK_SPEED)
		{
			final int base = stat.getBase() / 2;
			if ((stat.getBonus() > 0) && (base < stat.getBonus()))
			{
				stat.setBonus(base);
			}
			else if ((stat.getBonus() < 0) && (base < -stat.getBonus()))
			{
				stat.setBonus(-base);
			}
		}
		else if ((stat.getStat() == StatEnum.SPEED) || (stat.getStat() == StatEnum.FLY_SPEED) || (stat.getStat() == StatEnum.FLYBOOST_SPEED))
		{
			if (isPlayer == 2)
			{
				upperCap = Integer.MAX_VALUE;
			}
		}
		
		calculate(stat, lowerCap, upperCap);
		if (isPlayer != 1)
		{
			// not admins, and not npcs
			final int newValue = stat.getCurrent();
			if (newValue < LOWER_CAP)
			{
				minValues.put(stat.getStat(), newValue);
			}
			
			if (newValue > UPPER_CAP)
			{
				maxValues.put(stat.getStat(), newValue);
			}
		}
	}
	
	/**
	 * Retrieves the minimum allowed value for a specific statistic.<br>
	 * This method looks up the value in the internal {@code minValues} map.
	 * @param stat The {@link StatEnum} type to check.
	 * @return The minimum integer value for the given statistic.
	 */
	public static int getMinValue(StatEnum stat)
	{
		return minValues.get(stat);
	}
	
	/**
	 * Retrieves the maximum allowed value for a specific statistic.<br>
	 * This method looks up the limit based on the provided {@code StatEnum}.
	 * @param stat The {@link StatEnum} type to check.
	 * @return The maximum integer value for the given statistic.
	 */
	public static int getMaxValue(StatEnum stat)
	{
		return maxValues.get(stat);
	}
	
	/**
	 * Retrieves the minimum allowed value for a specific statistic.<br>
	 * This method checks the {@code limits} map first.<br>
	 * If no limit is defined, it returns {@code 0} for common stats or {@code Short.MIN_VALUE} otherwise.
	 * @param stat The {@link StatEnum} to check.
	 * @return The lower cap value as an {@code int}.
	 */
	public static int getLowerCap(StatEnum stat)
	{
		if (limits.containsKey(stat))
		{
			return limits.get(stat).lowerCap;
		}
		
		int value = LOWER_CAP;
		switch (stat)
		{
			case MAIN_HAND_POWER:
			case MAIN_HAND_ACCURACY:
			case MAIN_HAND_CRITICAL:
			case OFF_HAND_POWER:
			case OFF_HAND_ACCURACY:
			case OFF_HAND_CRITICAL:
			case MAGICAL_RESIST:
			case PHYSICAL_CRITICAL_RESIST:
			case EVASION:
			case PHYSICAL_DEFENSE:
			case MAGICAL_DEFEND:
			case PHYSICAL_ACCURACY:
			case MAGICAL_ACCURACY:
			case SPEED:
			case FLY_SPEED:
			case FLYBOOST_SPEED:
			case MAXHP:
			case MAXMP:
				value = 0;
				break;
			default:
				break;
		}
		
		return value;
	}
	
	/**
	 * Retrieves the maximum allowed value for a specific statistic.<br>
	 * This method checks the {@code limits} map first.<br>
	 * If no limit is found, it returns a default value based on the {@code StatEnum} type.
	 * @param stat The {@link StatEnum} to check for its upper cap.
	 * @return The maximum integer value allowed for the given statistic.
	 */
	public static int getUpperCap(StatEnum stat)
	{
		if (limits.containsKey(stat))
		{
			return limits.get(stat).upperCap;
		}
		
		int value = UPPER_CAP;
		switch (stat)
		{
			case SPEED:
				value = 12000;
				break;
			case FLY_SPEED:
			case FLYBOOST_SPEED:
				value = 16000;
				break;
			case PVP_ATTACK_RATIO:
			case PVP_ATTACK_RATIO_PHYSICAL:
			case PVP_ATTACK_RATIO_MAGICAL:
			case PVP_DEFEND_RATIO:
			case PVP_DEFEND_RATIO_PHYSICAL:
			case PVP_DEFEND_RATIO_MAGICAL:
				value = 900;
				break;
			case MAXHP:
			case MAXMP:
			case HEAL_BOOST:
			case HEAL_SKILL_BOOST:
			case PHYSICAL_ACCURACY:
			case PHYSICAL_CRITICAL:
			case BOOST_MAGICAL_SKILL:
			case BOOST_DURATION_BUFF:
			case BOOST_SPELL_ATTACK:
				value = Integer.MAX_VALUE;
				break;
			default:
				break;
		}
		
		return value;
	}
	
	/**
	 * Adjusts the bonus value of a {@link Stat2} object based on limits.<br>
	 * This method ensures the current value stays within the specified range.<br>
	 * It updates the bonus if the value exceeds {@code upperCap} or falls below {@code lowerCap}.
	 * @param stat2 The {@link Stat2} object to be updated.
	 * @param lowerCap The minimum allowed value for the statistic.
	 * @param upperCap The maximum allowed value for the statistic.
	 */
	private static void calculate(Stat2 stat2, int lowerCap, int upperCap)
	{
		if (stat2.getCurrent() > upperCap)
		{
			stat2.setBonus(upperCap - stat2.getBase());
		}
		else if (stat2.getCurrent() < lowerCap)
		{
			stat2.setBonus(lowerCap - stat2.getBase());
		}
	}
}
