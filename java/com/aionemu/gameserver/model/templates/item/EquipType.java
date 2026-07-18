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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different categories of equipment types available in the game.<br>
 * This enum is used to classify items such as weapons, armor, and accessories.
 * @author ATracer
 */
@XmlType(name = "equipType")
@XmlEnum
public enum EquipType
{
	ARMOR,
	WEAPON,
	STIGMA,
	ESTIMA,
	ACCESSORY,
	GRIND,
	NONE;
	
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
	 * Converts a string into its corresponding {@link EquipType}.<br>
	 * This method is useful for parsing data from external sources.
	 * @param v The string representation of the equipment type.
	 * @return The matching {@code EquipType} enum constant.
	 */
	public static EquipType fromValue(String v)
	{
		return valueOf(v);
	}
}
