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
package com.aionemu.gameserver.skillengine.properties;

import java.util.SortedMap;
import java.util.TreeMap;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This class represents a property that defines the maximum count for a specific skill.<br>
 * It is used by the {@link com.aionemu.gameserver.skillengine.model.Skill} system to limit usage.<br>
 * It helps manage how many times an effect or action can be applied to a {@link com.aionemu.gameserver.model.gameobjects.Creature}.
 * @author MrPoke
 */
public class MaxCountProperty
{
	/**
	 * Configures the maximum target count for a specific {@link Skill}.<br>
	 * This method updates the skill's affected list based on the provided {@code Properties}.<br>
	 * It sorts targets by distance and limits the count if the target type is set to {@code AREA}.
	 * @param skill The {@link Skill} object to be updated.
	 * @param properties The {@code Properties} containing the configuration data.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	public static boolean set(Skill skill, Properties properties)
	{
		final TargetRangeAttribute value = properties.getTargetType();
		final int maxcount = properties.getTargetMaxCount();
		
		switch (value)
		{
			case AREA:
				int areaCounter = 0;
				final Creature firstTarget = skill.getFirstTarget();
				if (firstTarget == null)
				{
					return false;
				}
				
				final SortedMap<Double, Creature> sortedMap = new TreeMap<>();
				for (Creature creature : skill.getEffectedList())
				{
					sortedMap.put(MathUtil.getDistance(firstTarget, creature), creature);
				}
				
				skill.getEffectedList().clear();
				for (Creature creature : sortedMap.values())
				{
					if (areaCounter >= maxcount)
					{
						break;
					}
					
					skill.getEffectedList().add(creature);
					areaCounter++;
				}
			default:
				break;
		}
		
		return true;
	}
}
