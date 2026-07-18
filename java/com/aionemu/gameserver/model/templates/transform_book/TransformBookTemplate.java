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
package com.aionemu.gameserver.model.templates.transform_book;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the data template for a transformation book in the game.<br>
 * This class defines the properties and requirements needed to create a {@code TransformBook} instance.
 */
@XmlRootElement(name = "transform_book_template")
@XmlAccessorType(XmlAccessType.NONE)
public class TransformBookTemplate
{
	@XmlAttribute(name = "id", required = true)
	private int id;
	@XmlAttribute(name = "name")
	private String name;
	@XmlAttribute(name = "grade", required = true)
	private int grade;
	@XmlAttribute(name = "skill_id", required = true)
	private int skillId;
	@XmlAttribute(name = "compose_value")
	private int composeValue;
	
	/**
	 * Creates a new instance of {@link TransformBookTemplate}.<br>
	 * Initializes the object with default values.
	 */
	public TransformBookTemplate()
	{
		name = "";
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the current grade of the reward.<br>
	 * This value is stored as an {@code int}.
	 * @return The integer value of the grade.
	 */
	public int getGrade()
	{
		return grade;
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillId;
	}
	
	/**
	 * Retrieves the composition value for this template.<br>
	 * This value is used to determine the requirements for crafting.
	 * @return The {@code int} value of the composition requirement.
	 */
	public int getComposeValue()
	{
		return composeValue;
	}
}
