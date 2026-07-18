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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.ai2.AbstractAI;

/**
 * Represents the skills associated with a specific material.<br>
 * This class defines how materials can be used or processed within the game world.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaterialSkill")
public class MaterialSkill
{
	@XmlAttribute
	protected MaterialActTime time;
	@XmlAttribute(required = true)
	protected float frequency;
	@XmlAttribute
	protected MaterialTarget target;
	@XmlAttribute(required = true)
	protected int level;
	@XmlAttribute(required = true)
	protected int id;
	
	/**
	 * Retrieves the action time for this material skill.<br>
	 * This method returns the {@code MaterialActTime} object associated with the skill.
	 * @return the {@code MaterialActTime} of the current skill.
	 */
	public MaterialActTime getTime()
	{
		return time;
	}
	
	/**
	 * Retrieves the frequency of the material skill.<br>
	 * This value represents how often the skill occurs.
	 * @return The {@code float} value of the frequency.
	 */
	public float getFrequency()
	{
		return frequency;
	}
	
	/**
	 * Retrieves the target for this material skill.<br>
	 * If no specific target is defined, it returns {@code MaterialTarget.ALL}.
	 * @return the {@link MaterialTarget} associated with this skill.
	 */
	public MaterialTarget getTarget()
	{
		if (target == null)
		{
			return MaterialTarget.ALL;
		}
		
		return target;
	}
	
	/**
	 * Retrieves the current level of the skill.<br>
	 * This value is used to determine the power of the action performed by the {@link AbstractAI}.
	 * @return The integer level of the skill.
	 */
	public int getSkillLevel()
	{
		return level;
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
