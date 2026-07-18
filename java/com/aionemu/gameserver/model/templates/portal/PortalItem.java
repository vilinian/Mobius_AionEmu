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
package com.aionemu.gameserver.model.templates.portal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the data model for a portal item within the game world.<br>
 * This class holds configuration details used to define specific portal properties.
 * @author AionChs Master
 * @author Schattenlilie
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalItem")
public class PortalItem
{
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "itemid")
	protected int itemid;
	@XmlAttribute(name = "quantity")
	protected int quantity;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the unique identifier for this material.<br>
	 * This value corresponds to the {@code itemid} field.
	 * @return The integer ID of the item.
	 */
	public int getItemid()
	{
		return itemid;
	}
	
	/**
	 * Retrieves the current amount of this item.<br>
	 * This value represents the total count stored in the {@code quantity} field.
	 * @return The number of items.
	 */
	public int getQuantity()
	{
		return quantity;
	}
}
