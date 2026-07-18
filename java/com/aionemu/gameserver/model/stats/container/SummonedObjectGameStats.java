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
package com.aionemu.gameserver.model.stats.container;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.stats.calc.AdditionStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;

/**
 * This class represents the game statistics for summoned objects.<br>
 * It extends {@link NpcGameStats} to provide specific data handling for summons.
 * @author ATracer
 */
public class SummonedObjectGameStats extends NpcGameStats
{
	/**
	 * Creates a new {@code SummonedObjectGameStats} instance.<br>
	 * This constructor initializes the stats for a summoned object.<br>
	 * It links the statistics to a specific owner.
	 * @param owner The {@link Npc} that owns this summoned object.
	 */
	public SummonedObjectGameStats(Npc owner)
	{
		super(owner);
	}
	
	/**
	 * Retrieves the calculated value of a specific statistic.<br>
	 * This method creates an {@link AdditionStat} using the provided base value.<br>
	 * It then returns the final result including all modifiers.
	 * @param statEnum The type of statistic to retrieve.
	 * @param base The base value for the statistic.
	 * @return The calculated {@link Stat2} object.
	 */
	@Override
	public Stat2 getStat(StatEnum statEnum, int base)
	{
		final Stat2 stat = super.getStat(statEnum, base);
		if (owner.getMaster() == null)
		{
			return stat;
		}
		
		switch (statEnum)
		{
			case BOOST_MAGICAL_SKILL:
			case MAGICAL_ATTACK:
			case MAGICAL_ACCURACY:
			case MAGICAL_RESIST:
				stat.setBonusRate(0.2f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_ACCURACY:
				stat.setBonusRate(0.2f);
				owner.getMaster().getGameStats().getItemStatBoost(StatEnum.MAIN_HAND_ACCURACY, stat);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_ATTACK:
				stat.setBonusRate(0.2f);
				owner.getMaster().getGameStats().getItemStatBoost(StatEnum.MAIN_HAND_POWER, stat);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			default:
				break;
			
		}
		
		return stat;
	}
	
	/**
	 * Gets the main hand magical attack stat for this summoned object.<br>
	 * It retrieves the value from the owner's template stats.
	 * @return The {@code Stat2} value for magical attack.
	 */
	@Override
	public Stat2 getMainHandMAttack()
	{
		final int power = owner.getObjectTemplate().getStatsTemplate().getPower();
		return getStat(StatEnum.MAGICAL_ATTACK, power);
	}
}
