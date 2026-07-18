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
 * This class represents the game statistics for trap-type {@link Npc} entities.<br>
 * It extends {@link NpcGameStats} to provide specific data handling for traps.
 * @author ATracer
 */
public class TrapGameStats extends NpcGameStats
{
	/**
	 * Creates a new instance of {@code TrapGameStats}.<br>
	 * This constructor initializes the stats for a specific NPC.
	 * @param owner The {@link Npc} that owns these statistics.
	 */
	public TrapGameStats(Npc owner)
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
			case MAGICAL_ACCURACY:
				// bonus is calculated from stat bonus of master (only green value)
				stat.setBonusRate(0.7f); // TODO: retail formula?
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			default:
				break;
			
		}
		
		return stat;
	}
}
