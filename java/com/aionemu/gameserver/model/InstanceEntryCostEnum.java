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
 * Defines the types of costs required to enter a specific game instance.<br>
 * This enumeration is used by {@code Instance} to manage access requirements.
 */
@XmlEnum
public enum InstanceEntryCostEnum
{
	
	KINAH(0),
	PC_COIN(1),
	LUNA(2);
	
	private final int typeId;
	
	/**
	 * Creates a new instance of {@link InstanceEntryCostEnum}.<br>
	 * This constructor maps an integer ID to the correct enum constant.
	 * @param type The unique identifier for the entry cost.
	 */
	private InstanceEntryCostEnum(int type)
	{
		typeId = type;
	}
	
	/**
	 * Retrieves an {@link InstanceEntryCostEnum} based on its numeric ID.<br>
	 * This method searches through all available enum values.<br>
	 * It throws an {@code IllegalArgumentException} if the ID is not found.
	 * @param type The integer ID of the cost type to look up.
	 * @return The matching {@code InstanceEntryCostEnum} constant.
	 */
	public static InstanceEntryCostEnum getCotstId(int type)
	{
		for (InstanceEntryCostEnum pc : values())
		{
			if (pc.getTypeId() == type)
			{
				return pc;
			}
		}
		
		throw new IllegalArgumentException("There is no InstanceEntryCostEnum with id " + type);
	}
	
	/**
	 * Retrieves the unique identifier for this {@link InstanceEntryCostEnum}.<br>
	 * This value corresponds to the internal ID used by the game engine.
	 * @return The integer ID of the entry cost type.
	 */
	public int getTypeId()
	{
		return typeId;
	}
}
