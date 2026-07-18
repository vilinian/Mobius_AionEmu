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
package com.aionemu.gameserver.world.zone;

import java.util.List;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the various attributes and properties associated with a {@code Zone}.<br>
 * This enum is used to categorize specific characteristics of world zones in the game.<br>
 * It maps human-readable attribute names to their internal representations.
 * @author Rolandas
 */
@XmlType(name = "ZoneAttributes")
@XmlEnum(String.class)
public enum ZoneAttributes
{
	BIND(1 << 0),
	RECALL(1 << 1),
	GLIDE(1 << 2),
	FLY(1 << 3),
	RIDE(1 << 4),
	FLY_RIDE(1 << 5),
	@XmlEnumValue("PVP")
	PVP_ENABLED(1 << 6), // Only for PvP type zones
	@XmlEnumValue("DUEL_SAME_RACE")
	DUEL_SAME_RACE_ENABLED(1 << 7), // Only for Duel type zones
	@XmlEnumValue("DUEL_OTHER_RACE")
	DUEL_OTHER_RACE_ENABLED(1 << 8); // Only for Duel type zones
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link ZoneAttributes}.<br>
	 * This constructor assigns the bitwise identifier to the object.
	 * @param id The integer value representing the specific attribute flag.
	 */
	private ZoneAttributes(int id)
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
	
	/**
	 * Converts a list of {@link ZoneAttributes} into a single integer bitmask.<br>
	 * This method combines all active flags from the provided list.
	 * @param flagValues The list of {@code ZoneAttributes} to convert.
	 * @return The resulting integer bitmask representing all flags.
	 */
	public static Integer fromList(List<ZoneAttributes> flagValues)
	{
		int result = 0;
		for (ZoneAttributes attribute : ZoneAttributes.values())
		{
			if (flagValues.contains(attribute))
			{
				result |= attribute.getId();
			}
		}
		
		return result;
	}
}
