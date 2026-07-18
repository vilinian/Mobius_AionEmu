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
package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the possible targets for an {@code ApExtract} action.<br>
 * This enum helps determine which entity or object is being interacted with during extraction.
 * @author Rolandas
 */
@XmlType(name = "ApExtractTarget")
@XmlEnum
public enum ApExtractTarget
{
	ACCESSORY,
	ARMOR,
	EQUIPMENT,
	WEAPON,
	WING,
	OTHER,
	ALL;
	
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
	 * Converts a string into its corresponding {@link ApExtractTarget} enum constant.<br>
	 * This method is useful for parsing input data from external sources.
	 * @param v The string representation of the target type.
	 * @return The matching {@code ApExtractTarget} object.
	 */
	public static ApExtractTarget fromValue(String v)
	{
		return valueOf(v);
	}
}
