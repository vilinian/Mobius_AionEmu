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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Homing;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.stats.calc.AdditionStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * This class manages the game statistics for {@link Homing} objects.<br>
 * It handles specific attributes and behaviors unique to homing entities.
 * @author Cheatkiller
 */
public class HomingGameStats extends SummonedObjectGameStats
{
	/**
	 * Creates a new {@link HomingGameStats} instance for a specific NPC.<br>
	 * This constructor initializes the stats using the provided owner.
	 * @param owner The {@code Npc} that owns these game statistics.
	 */
	public HomingGameStats(Npc owner)
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
			case MAGICAL_ATTACK:
				stat.setBonusRate(0.2f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			default:
				break;
			
		}
		
		return stat;
	}
	
	/**
	 * Retrieves the main hand magical attack stat for this homing object.<br>
	 * It calculates the power based on the skill level and specific name of the {@link Homing}.<br>
	 * The result is returned as a {@code Stat2} object.
	 * @return The calculated {@code Stat2} for magical attack.
	 */
	@Override
	public Stat2 getMainHandMAttack()
	{
		final Homing homing = (Homing) owner;
		int power = homing.getObjectTemplate().getStatsTemplate().getPower();
		final SkillTemplate skill = DataManager.SKILL_DATA.getSkillTemplate(homing.getSkillId());
		final int skillLvl = skill.getLvl();
		switch (skillLvl)
		{
			case 3:
				if (homing.getName().equalsIgnoreCase("stone energy"))
				{
					power = 316;
				}
				
				if (homing.getName().equalsIgnoreCase("water energy"))
				{
					power = 362;
				}
				break;
			case 4:
				if (homing.getName().equalsIgnoreCase("cyclone servant"))
				{
					power = 1166;
				}
				
				if (homing.getName().equalsIgnoreCase("fire energy"))
				{
					power = 313;
				}
				
				if (homing.getName().equalsIgnoreCase("wind servant"))
				{
					power = 373;
				}
				
				if (homing.getName().equalsIgnoreCase("stone energy"))
				{
					power = 384;
				}
				break;
			case 5:
				if (homing.getName().equalsIgnoreCase("cyclone servant"))
				{
					power = 1221;
				}
				break;
			case 6:
				if (homing.getName().equalsIgnoreCase("cyclone servant"))
				{
					power = 1283;
				}
				break;
			case 7:
				if (homing.getName().equalsIgnoreCase("cyclone servant"))
				{
					power = 1342;
				}
				break;
		}
		
		return getStat(StatEnum.MAGICAL_ATTACK, power);
	}
}
