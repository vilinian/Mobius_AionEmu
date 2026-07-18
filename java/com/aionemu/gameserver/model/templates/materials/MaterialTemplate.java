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
package com.aionemu.gameserver.model.templates.materials;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the data model for a material template in the game.<br>
 * This class stores configuration details used to define various materials.<br>
 * It is mapped to XML data for easy modification of game content.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaterialTemplate", propOrder =
{
	"skills"
})
public class MaterialTemplate
{
	@XmlElement(name = "skill", required = true)
	protected List<MaterialSkill> skills;
	@XmlAttribute(name = "skill_obstacle")
	protected Integer skillObstacle;
	@XmlAttribute(required = true)
	protected int id;
	
	/**
	 * Retrieves the list of skills associated with this material.<br>
	 * This method returns all {@link MaterialSkill} objects linked to the template.
	 * @return a {@code List} of {@link MaterialSkill} objects.
	 */
	public List<MaterialSkill> getSkills()
	{
		return skills;
	}
	
	/**
	 * Retrieves the obstacle value associated with a skill.<br>
	 * This value is used to determine difficulty levels.
	 * @return The {@code Integer} value of the skill obstacle.
	 */
	public Integer getSkillObstacle()
	{
		return skillObstacle;
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
