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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a template for a collection of transformations within the game.<br>
 * This class defines the data structure used to group multiple {@code TransformTemplate} objects.
 */
@XmlRootElement(name = "transform_collection_template")
@XmlAccessorType(XmlAccessType.NONE)
public class TransformCollectionTemplate
{
	@XmlElement(name = "physical_bonus")
	protected CollectionAttr physicalAttr;
	@XmlElement(name = "magical_bonus")
	protected CollectionAttr magicalAttr;
	@XmlElement(name = "required")
	protected CollectionRequired required;
	@XmlAttribute(name = "id", required = true)
	private int id;
	@XmlAttribute(name = "need_count")
	private int needCount;
	@XmlAttribute(name = "reward_skill")
	private int rewarSkill;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the magical bonus attribute for this template.<br>
	 * This method returns the {@code magicalAttr} field.
	 * @return the {@link CollectionAttr} object containing magical bonuses.
	 */
	public CollectionAttr getMagicalAttr()
	{
		return magicalAttr;
	}
	
	/**
	 * Retrieves the physical attributes of this collection.<br>
	 * This method returns the {@code CollectionAttr} object associated with physical bonuses.
	 * @return the {@code CollectionAttr} for physical attributes.
	 */
	public CollectionAttr getPhysicalAttr()
	{
		return physicalAttr;
	}
	
	/**
	 * Retrieves the requirements for this collection.<br>
	 * This method returns the {@code CollectionRequired} object associated with the template.
	 * @return the {@code CollectionRequired} object.
	 */
	public CollectionRequired getRequired()
	{
		return required;
	}
	
	/**
	 * Retrieves the number of items required for this collection.<br>
	 * This value is stored in the {@code need_count} attribute.
	 * @return The total count of items needed.
	 */
	public int getNeedCount()
	{
		return needCount;
	}
	
	/**
	 * Retrieves the skill ID granted by this transformation.<br>
	 * This value is stored in the {@code reward_skill} attribute.
	 * @return The integer ID of the rewarded skill.
	 */
	public int getRewarSkill()
	{
		return rewarSkill;
	}
}
