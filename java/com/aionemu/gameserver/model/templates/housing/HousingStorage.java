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
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the storage system associated with a player's house.<br>
 * This class defines the properties and data for housing-related inventory items.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HousingStorage")
public class HousingStorage extends PlaceableHouseObject
{
	@XmlAttribute(name = "warehouse_id", required = true)
	protected int warehouseId;
	
	/**
	 * Retrieves the unique identifier for the warehouse.<br>
	 * This value is stored in the {@code warehouseId} field.
	 * @return The integer ID of the warehouse.
	 */
	public int getWarehouseId()
	{
		return warehouseId;
	}
	
	/**
	 * Retrieves the unique identifier for this object type.<br>
	 * This value is used to identify the chair in the game world.
	 * @return The {@code byte} ID of the housing chair, which is always {@code 5}.
	 */
	@Override
	public byte getTypeId()
	{
		return 2;
	}
}
