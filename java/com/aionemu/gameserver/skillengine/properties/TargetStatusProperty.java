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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * Represents a property that defines the status effects applied to a target.<br>
 * This class is used by {@link Skill} to manage how {@link AbnormalState} effects are handled.
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetStatusProperty")
public class TargetStatusProperty
{
	/**
	 * Checks if the first target of a {@link Skill} has any required abnormal states.<br>
	 * This method verifies the status of the primary target against a list of statuses from {@code Properties}.<br>
	 * It returns {@code true} if at least one matching state is found on the target.
	 * @param skill The {@link Skill} to check.
	 * @param properties The {@code Properties} containing the required status strings.
	 * @return {@code true} if a match is found, otherwise {@code false}.
	 */
	public static boolean set(Skill skill, Properties properties)
	{
		if (skill.getEffectedList().size() != 1)
		{
			return false;
		}
		
		final List<String> targetStatus = properties.getTargetStatus();
		
		final Creature effected = skill.getFirstTarget();
		boolean result = false;
		
		for (String status : targetStatus)
		{
			if (effected.getEffectController().isAbnormalSet(AbnormalState.valueOf(status)))
			{
				result = true;
			}
		}
		
		return result;
	}
}
