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
package com.aionemu.gameserver.model.templates.arcadeupgrade;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a collection of items within an arcade upgrade tab.<br>
 * It serves as a data model for mapping specific items to their respective categories.
 * @author Raziel
 */
@XmlType(name = "ArcadeTabItemList")
public class ArcadeTabItemList
{
	@XmlAttribute(name = "item_id")
	protected int item_id;
	@XmlAttribute(name = "normalcount")
	protected int normalcount;
	@XmlAttribute(name = "frenzycount")
	protected int frenzycount;
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return item_id;
	}
	
	/**
	 * Retrieves the number of normal items.<br>
	 * This value is stored in the {@code normalcount} field.
	 * @return The total count of normal items as an {@code int}.
	 */
	public int getNormalCount()
	{
		return normalcount;
	}
	
	/**
	 * Retrieves the current number of frenzy counts.<br>
	 * This value tracks how many times the frenzy has been activated.
	 * @return The current {@code int} value of {@code frenzyCount}.
	 */
	public int getFrenzyCount()
	{
		return frenzycount;
	}
}
