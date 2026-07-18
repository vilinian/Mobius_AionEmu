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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the data model for how an item is acquired within the game.<br>
 * This class maps to the {@code Acquisition} XML template structure.<br>
 * It defines the rules and conditions for obtaining specific items.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Acquisition")
public class Acquisition
{
	@XmlAttribute(name = "ap", required = false)
	private int ap = 0;
	@XmlAttribute(name = "count", required = false)
	private int itemCount;
	@XmlAttribute(name = "item", required = false)
	private int itemId;
	@XmlAttribute(name = "type", required = true)
	private AcquisitionType type;
	
	/**
	 * Retrieves the acquisition type of this object.<br>
	 * This method returns the {@code AcquisitionType} associated with the current instance.
	 * @return the {@code AcquisitionType} value.
	 */
	public AcquisitionType getType()
	{
		return type;
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
	 * Retrieves the total number of items for this reward.<br>
	 * This method returns the value stored in the {@code itemCount} field.
	 * @return The integer count of items.
	 */
	public int getItemCount()
	{
		return itemCount;
	}
	
	/**
	 * Retrieves the amount of {@code ap} required for this acquisition.<br>
	 * This value is stored as an {@code int}.
	 * @return the required {@code ap} value.
	 */
	public int getRequiredAp()
	{
		return ap;
	}
}
