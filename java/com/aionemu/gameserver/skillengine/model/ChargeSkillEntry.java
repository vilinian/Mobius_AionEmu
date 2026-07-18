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
package com.aionemu.gameserver.skillengine.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a skill entry that can be charged by a player.<br>
 * This model stores the configuration data for skills requiring a charge mechanic.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeSkill", propOrder =
{
	"skills"
})
public class ChargeSkillEntry
{
	@XmlElement(name = "skill", required = true)
	protected List<ChargedSkill> skills;
	@XmlAttribute(required = true)
	protected int id;
	@XmlAttribute(name = "min_time", required = true)
	protected int minTime;
	
	/**
	 * Retrieves the list of {@link ChargedSkill} objects.<br>
	 * This method returns all skills associated with this entry.
	 * @return a {@code List} containing all {@code ChargedSkill} objects.
	 */
	public List<ChargedSkill> getSkills()
	{
		return skills;
	}
	
	/**
	 * Retrieves the minimum time for this skill.<br>
	 * This value is stored in the {@code minTime} field.
	 * @return the minimum time as an {@code int}.
	 */
	public int getMinTime()
	{
		return minTime;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
