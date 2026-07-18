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
 * Defines the types of actions that can be performed on a target entity.<br>
 * This enum is used by {@code ItemAction} to determine how an item interacts with a specific target.
 * @author Rolandas
 */
@XmlType(name = "UseTarget")
@XmlEnum
public enum UseTarget
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
	 * Converts a string into its corresponding {@link UseTarget} enum constant.<br>
	 * This method is useful for parsing input data.
	 * @param v The string representation of the target type.
	 * @return The matching {@code UseTarget} object.
	 */
	public static UseTarget fromValue(String v)
	{
		return valueOf(v);
	}
}
