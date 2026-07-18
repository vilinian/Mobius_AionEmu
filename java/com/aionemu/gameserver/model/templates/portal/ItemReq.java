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
 * Represents the item requirements for a specific portal.<br>
 * This class defines what items are needed to activate or use a {@code Portal}.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemReq")
public class ItemReq
{
	@XmlAttribute(name = "item_id")
	protected int itemId;
	@XmlAttribute(name = "item_count")
	protected int itemCount;
	
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
	 * Sets the unique identifier for the item.<br>
	 * This updates the {@code itemId} field of this object.
	 * @param value The new integer ID to assign to the item.
	 */
	public void setItemId(int value)
	{
		itemId = value;
	}
	
	/**
	 * Retrieves the total number of items for this reward.<br>
	 * This method returns the value stored in the {@code itemCount} field.
	 * @return The integer count of items.
	 */
	public int getItemCount()
	{
		return itemCount;
	}
	
	/**
	 * Updates the total number of items.<br>
	 * This method sets the {@code itemCount} field to a new value.
	 * @param value The new quantity for the item.
	 */
	public void setItemCount(int value)
	{
		itemCount = value;
	}
}
