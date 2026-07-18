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
package com.aionemu.gameserver.model.templates.siegelocation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the rewards granted to players for completing a {@code SiegelLocation} in the Luna area.<br>
 * This class holds the data necessary to define specific items or bonuses associated with these locations.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LunaReward")
public class LunaReward
{
	@XmlAttribute(name = "itemid")
	protected int itemId;
	
	@XmlAttribute(name = "l_count")
	protected int lCount;
	
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
	 * Retrieves the count of items for this reward.<br>
	 * This method returns the value stored in the {@code lCount} field.
	 * @return The total number of items as an {@code int}.
	 */
	public int getLount()
	{
		return lCount;
	}
}
