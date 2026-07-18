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
package com.aionemu.gameserver.model.templates.lumiel_transform;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the data template for {@code Lumiel} materials.<br>
 * It defines the properties and attributes used to configure material instances in the game.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "LumielMaterialTemplate")
public class LumielMaterialTemplate
{
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "lumiel_id")
	protected int lumielId;
	@XmlAttribute(name = "item_id")
	protected int itemId;
	@XmlAttribute(name = "point")
	protected int point;
	
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
	 * Retrieves the unique identifier for the Lumiel material.<br>
	 * This value corresponds to the {@code lumiel_id} attribute.
	 * @return The {@code int} value of the Lumiel ID.
	 */
	public int getLumielId()
	{
		return lumielId;
	}
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Retrieves the current point value.<br>
	 * This method returns the {@code point} attribute from the template.
	 * @return The integer value of the point.
	 */
	public int getPoint()
	{
		return point;
	}
}
