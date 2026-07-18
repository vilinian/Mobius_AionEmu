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
package com.aionemu.gameserver.model;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Represents the different types of attendance for game events.<br>
 * This enum is used to categorize how players participate in specific activities.
 * @author Alcapwnd
 */
@XmlEnum
public enum AttendType
{
	NONE(0),
	BASIC(1),
	ANNIVERSARY(2);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link AttendType}.<br>
	 * This constructor assigns the unique identifier to the enum constant.
	 * @param id The integer value representing the specific attendance type.
	 */
	private AttendType(int id)
	{
		this.id = id;
	}
	
	/**
	 * Retrieves an {@link AttendType} based on its unique identifier.<br>
	 * If no match is found, it returns {@code AttendType.NONE}.
	 * @param id The integer ID of the login type to find.
	 * @return The corresponding {@code AttendType} or {@code NONE}.
	 */
	public static AttendType getLoginTypeById(int id)
	{
		for (AttendType attendType : values())
		{
			if (attendType.getId() == id)
			{
				return attendType;
			}
		}
		
		return AttendType.NONE;
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
