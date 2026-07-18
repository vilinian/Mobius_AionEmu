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
 * Defines the different types of areas where housing can be placed.<br>
 * This enum is used to categorize specific locations within the game world.
 * @author Rolandas
 */
@XmlType(name = "PlaceArea")
@XmlEnum
public enum PlaceArea
{
	ALL,
	INTERIOR,
	EXTERIOR;
	
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
	 * Creates a {@link PlaceArea} constant from a string.<br>
	 * This method converts the provided {@code value} into its corresponding enum type.
	 * @param value The string representation of the area to convert.
	 * @return The matching {@link PlaceArea} constant.
	 */
	public static PlaceArea fromValue(String value)
	{
		return valueOf(value);
	}
}
