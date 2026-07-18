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
package com.aionemu.gameserver.model.templates.pet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the outcome of a pet feeding action.<br>
 * This class stores data regarding whether the feed was successful and any resulting effects.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetFeedResult")
public class PetFeedResult
{
	@XmlAttribute(required = true)
	protected int item;
	@XmlAttribute
	protected String name;
	
	/**
	 * Retrieves the unique identifier for the item.<br>
	 * This value is stored in the {@code item} field.
	 * @return The integer ID of the item.
	 */
	public int getItem()
	{
		return item;
	}
	
	/**
	 * Returns a string representation of this pet feed result.<br>
	 * It combines the {@code name} and the {@code item} ID.
	 * @return A formatted string containing the name and item number.
	 */
	@Override
	public String toString()
	{
		return name + " (" + item + ")";
	}
}
