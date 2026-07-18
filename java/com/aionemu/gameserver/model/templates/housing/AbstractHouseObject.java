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
package com.aionemu.gameserver.model.templates.housing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.item.ItemQuality;

/**
 * Represents a base template for objects that can be placed within a house.<br>
 * This class provides common properties and behaviors shared by all types of housing decorations.<br>
 * It serves as the foundation for specific implementations like furniture or ornaments.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractHouseObject")
@XmlSeeAlso(
{
	PlaceableHouseObject.class
})
public abstract class AbstractHouseObject extends VisibleObjectTemplate
{
	@XmlAttribute(name = "talking_distance", required = true)
	protected float talkingDistance;
	@XmlAttribute(required = true)
	protected ItemQuality quality;
	@XmlAttribute(required = true)
	protected HousingCategory category;
	@XmlAttribute(name = "name_id", required = true)
	protected int nameId;
	@XmlAttribute(required = true)
	protected int id;
	@XmlAttribute(name = "can_dye")
	protected boolean canDye;
	
	/**
	 * Retrieves the unique identifier for this gatherable template.<br>
	 * This value corresponds to the {@code id} field.
	 * @return The unique template ID as an {@code int}.
	 */
	@Override
	public int getTemplateId()
	{
		return id;
	}
	
	/**
	 * Retrieves the distance at which players can talk to this object.<br>
	 * This value is fetched from the underlying {@code AbstractHouseObject} template.
	 * @return The talking distance as a {@code float}.
	 */
	public float getTalkingDistance()
	{
		return talkingDistance;
	}
	
	/**
	 * Retrieves the quality level of this house object.<br>
	 * This method calls {@code getQuality} on the underlying template.
	 * @return the {@code ItemQuality} of the object.
	 */
	public ItemQuality getQuality()
	{
		return quality;
	}
	
	/**
	 * Retrieves the category of the house object.<br>
	 * This method calls {@code getCategory} on the underlying template.
	 * @return the {@code HousingCategory} associated with this object.
	 */
	public HousingCategory getCategory()
	{
		return category;
	}
	
	/**
	 * Checks if the object can be dyed.<br>
	 * This method returns the value of the {@code canDye} attribute.
	 * @return {@code true} if the object can be dyed, {@code false} otherwise.
	 */
	public boolean getCanDye()
	{
		return canDye;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	@Override
	public int getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the name of the house object.<br>
	 * This method returns the {@code String} representation of the name.
	 * @return The name of the house object as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return null;
	}
}
