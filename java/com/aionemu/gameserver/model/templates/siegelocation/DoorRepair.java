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
 * Represents the data structure for a door repair location.<br>
 * This class defines the properties required to handle repairs at specific points in the game world.
 * @author Falke_34
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DoorRepair")
public class DoorRepair
{
	@XmlAttribute(name = "repair_fee")
	protected int repairFee;
	@XmlAttribute(name = "itemid")
	protected int itemId;
	@XmlAttribute(name = "repair_cooltime")
	protected int repairCooltime;
	
	/**
	 * Retrieves the cost to repair a door.<br>
	 * This value is stored in the {@code repairFee} field.
	 * @return The total fee as an {@code int}.
	 */
	public int getRepairFee()
	{
		return repairFee;
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
	 * Gets the time required between repairs.<br>
	 * This value is converted from seconds to milliseconds.
	 * @return The repair cooltime in {@code long} milliseconds.
	 */
	public long getRepairCooltime()
	{
		return repairCooltime * 1000;
	}
}
