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
package com.aionemu.gameserver.model.templates.materials;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the specific time of day when a material can be active.<br>
 * This enum is used to define temporal constraints for {@code Material} objects.
 * @author Rolandas
 */
@XmlType(name = "DayTime")
@XmlEnum
public enum MaterialActTime
{
	DAY,
	NIGHT;
	
	/**
	 * Returns the name of this {@code ChallengeType}.<br>
	 * This is useful for getting a human-readable string representation.
	 * @return The name of the enum constant as a {@code String}.
	 */
	public String value()
	{
		return name();
	}
	
	/**
	 * Converts a {@code String} into a {@link MaterialActTime} enum constant.<br>
	 * This method uses the standard {@code valueOf} logic to find the matching name.
	 * @param value The string representation of the time.
	 * @return The corresponding {@link MaterialActTime} object.
	 */
	public static MaterialActTime fromValue(String value)
	{
		return valueOf(value);
	}
}
