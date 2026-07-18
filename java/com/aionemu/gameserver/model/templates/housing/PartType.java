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
 * Defines the different types of parts used in housing construction.<br>
 * This enum is used to categorize various components within the {@code housing} system.
 * @author Rolandas
 */
@XmlType(name = "PartType")
@XmlEnum
public enum PartType
{
	ROOF(1, 1),
	OUTWALL(2, 2),
	FRAME(3, 3),
	DOOR(4, 4),
	GARDEN(5, 5),
	FENCE(6, 6),
	INWALL_ANY(8, 13),
	INFLOOR_ANY(14, 19),
	ADDON(27, 27);
	
	private final int lineNrStart;
	private final int lineNrEnd;
	
	/**
	 * Creates a new {@link PartType} instance.<br>
	 * This constructor maps the part to a specific range of lines.
	 * @param packetLineStart The starting line number for this part type.
	 * @param packetLineEnd The ending line number for this part type.
	 */
	private PartType(int packetLineStart, int packetLineEnd)
	{
		lineNrStart = packetLineStart;
		lineNrEnd = packetLineEnd;
	}
	
	/**
	 * Returns the starting line number for this {@code PartType}.<br>
	 * This value is used to identify where a part begins in a packet.
	 * @return The integer representing the start line number.
	 */
	public int getStartLineNr()
	{
		return lineNrStart;
	}
	
	/**
	 * Returns the ending line number for this {@code PartType}.<br>
	 * This value corresponds to the end of the range defined in the configuration.
	 * @return The integer representing the end line number.
	 */
	public int getEndLineNr()
	{
		return lineNrEnd;
	}
	
	/**
	 * Finds the {@link PartType} associated with a specific line number.<br>
	 * It checks if the provided {@code lineNr} falls within the range of any part type.
	 * @param lineNr The line number to check.
	 * @return The matching {@code PartType} or {@code null} if no match is found.
	 */
	public static PartType getForLineNr(int lineNr)
	{
		for (PartType type : PartType.values())
		{
			if ((type.getStartLineNr() <= lineNr) && (type.getEndLineNr() >= lineNr))
			{
				return type;
			}
		}
		
		return null;
	}
}
