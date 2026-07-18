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
 * Represents the different categories available for housing objects.<br>
 * This enum is used to classify and organize various types of furniture or decorations within a house.
 * @author Rolandas
 */
@XmlType(name = "HousingObjectType")
@XmlEnum
public enum HousingCategory
{
	BED,
	BOOK,
	CARPET,
	CHAIR,
	CURTAIN,
	DECORATION,
	LIGHT,
	NPC,
	OUTLIGHT,
	TABLE;
	
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
	 * Converts a {@code String} into its corresponding {@link HousingCategory}.<br>
	 * This method is useful for parsing data from external sources.
	 * @param value The string representation of the category.
	 * @return The matching {@code HousingCategory} enum constant.
	 */
	public static HousingCategory fromValue(String value)
	{
		return valueOf(value);
	}
}
