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
 * Represents the rewards granted to players for participating in a siege.<br>
 * This class defines the data structure for items or bonuses associated with {@code SiegeLocation}.
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SiegeReward")
public class SiegeReward
{
	@XmlAttribute(name = "top")
	protected int top;
	@XmlAttribute(name = "itemid")
	protected int itemId;
	@XmlAttribute(name = "m_count")
	protected int mCount;
	
	/**
	 * Retrieves the top value for this reward.<br>
	 * This method returns the {@code top} attribute from the {@link SiegeReward} object.
	 * @return The integer value of the top field.
	 */
	public int getTop()
	{
		return top;
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
	 * Retrieves the current count value.<br>
	 * This method returns the integer stored in the {@code mCount} field.
	 * @return The current count as an {@code int}.
	 */
	public int getCount()
	{
		return mCount;
	}
}
