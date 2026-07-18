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
package com.aionemu.gameserver.model.templates.item.bonuses;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the types of statistics that can be modified by item bonuses.<br>
 * This enum is used to categorize different attributes like strength or agility in the game world.
 * @author Rolandas
 */
@XmlType(name = "StatBonusType")
@XmlEnum
public enum StatBonusType
{
	INVENTORY,
	POLISH;
	
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
	 * Converts a string value into its corresponding {@link StatBonusType}.<br>
	 * This method is useful for parsing data from external sources.
	 * @param v The string representation of the bonus type.
	 * @return The matching {@code StatBonusType} enum constant.
	 */
	public static StatBonusType fromValue(String v)
	{
		return valueOf(v);
	}
}
