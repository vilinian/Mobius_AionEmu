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
package com.aionemu.gameserver.model.templates.minion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the skill data for a minion.<br>
 * This class defines how specific skills are applied to {@code Minion} entities.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MinionSkill")
public class MinionSkill
{
	@XmlAttribute(name = "skill_id")
	public int skill_id;
	@XmlAttribute(name = "energyCost")
	public int energyCost;
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skill_id;
	}
	
	/**
	 * Retrieves the amount of energy required to use this skill.<br>
	 * This value is stored in the {@code energyCost} field.
	 * @return The total energy cost as an {@code int}.
	 */
	public int getEnergyCost()
	{
		return energyCost;
	}
}
