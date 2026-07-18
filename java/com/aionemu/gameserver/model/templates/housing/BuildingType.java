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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different types of buildings available in the housing system.<br>
 * This enum is used to categorize structures within the {@code housing} package.
 * @author Rolandas
 */
@XmlType(name = "BuildingType")
@XmlEnum
public enum BuildingType
{
	PERSONAL_FIELD(2),
	PERSONAL_INS(1);
	
	private final int id;
	
	/**
	 * Creates a new {@link BuildingType} instance.<br>
	 * This constructor assigns the unique identifier to the building type.
	 * @param id The unique integer ID for the building type.
	 */
	BuildingType(int id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
