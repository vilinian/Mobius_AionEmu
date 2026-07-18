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
package com.aionemu.gameserver.model.templates.panels;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the data structure for a skill panel in the game.<br>
 * This class holds information regarding skills displayed to the player.<br>
 * It is used by the {@code panels} system to manage UI elements.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillPanel")
public class SkillPanel
{
	@XmlAttribute(name = "panel_id")
	protected byte id;
	@XmlAttribute(name = "panel_skills")
	protected List<Integer> skills;
	
	/**
	 * Retrieves the unique identifier for this panel.<br>
	 * This value is used to identify specific skill panels in the system.
	 * @return The {@code int} value of the panel ID.
	 */
	public int getPanelId()
	{
		return id;
	}
	
	/**
	 * Retrieves the list of skills associated with this {@link SkillPanel}.<br>
	 * This method returns a {@code List} of skill identifiers.
	 * @return A {@code List} of {@code Integer} values representing the skills.
	 */
	public List<Integer> getSkills()
	{
		return null;
	}
	
	/**
	 * Checks if a specific skill and level are available in this panel.<br>
	 * It searches through the {@code skills} list for a matching entry.
	 * @param skillId The unique identifier for the skill.
	 * @param level The required level of the skill.
	 * @return {@code true} if the skill and level are found, otherwise {@code false}.
	 */
	public boolean canUseSkill(int skillId, int level)
	{
		for (Integer skill : skills)
		{
			if (((skill >> 8) == skillId) && ((skill & 0xFF) == level))
			{
				return true;
			}
		}
		
		return false;
	}
}
