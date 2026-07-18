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
 * Represents the specific types of locations where housing can be placed.<br>
 * This enum is used to define valid coordinates or zones for player residences.
 * @author Rolandas
 */
@XmlType(name = "PlaceLocation")
@XmlEnum
public enum PlaceLocation
{
	FLOOR,
	STACK,
	WALL;
	
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
	 * Creates a {@link PlaceLocation} from a string representation.<br>
	 * This method converts the provided {@code value} into its corresponding enum constant.
	 * @param value The string name of the location to convert.
	 * @return The matching {@code PlaceLocation} object.
	 */
	public static PlaceLocation fromValue(String value)
	{
		return valueOf(value);
	}
}
