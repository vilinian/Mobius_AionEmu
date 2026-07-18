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
package com.aionemu.gameserver.model.templates.collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * This class defines the experience point configuration for a specific collection.<br>
 * It stores data related to how much {@code exp} is gained when completing a collection task.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "CollectionExpTemplate")
public class CollectionExpTemplate
{
	@XmlElement(name = "modifiers", required = false)
	private ModifiersTemplate modifiers;
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "level")
	protected int level;
	@XmlAttribute(name = "exp")
	protected int exp;
	@XmlAttribute(name = "grade")
	protected CollectionType grade;
	
	/**
	 * Retrieves the {@code ModifiersTemplate} associated with this collection.<br>
	 * This method returns the modifier data used for experience calculations.
	 * @return the {@link ModifiersTemplate} object or {@code null} if no modifiers exist.
	 */
	public ModifiersTemplate getModifiers()
	{
		return modifiers;
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
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the current experience points for this collection.<br>
	 * This value represents the progress toward the next level.
	 * @return The current {@code int} value of experience.
	 */
	public int getExp()
	{
		return exp;
	}
	
	/**
	 * Retrieves the current grade of the collection.<br>
	 * This value is stored in the {@code grade} field.
	 * @return The {@link CollectionType} representing the grade.
	 */
	public CollectionType getGrade()
	{
		return grade;
	}
}
