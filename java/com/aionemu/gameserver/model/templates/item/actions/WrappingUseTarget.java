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
 * Defines the target types for wrapping items.<br>
 * This enum determines which entity can be affected by a {@code WrappingAction}.
 */
@XmlType(name = "WrappingUseTarget")
@XmlEnum
public enum WrappingUseTarget
{
	ACCESSORY,
	ARMOR,
	WEAPON;
	
	/**
	 * Private constructor for the {@link WrappingUseTarget} enum.<br>
	 * This prevents the creation of new instances using {@code new}.
	 */
	private WrappingUseTarget()
	{
	}
	
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
	 * Converts a {@code String} into its corresponding {@link WrappingUseTarget} enum constant.<br>
	 * This method is useful for parsing input data from external sources.
	 * @param v The string representation of the target type.
	 * @return The matching {@code WrappingUseTarget} value.
	 */
	public static WrappingUseTarget fromValue(String v)
	{
		return valueOf(v);
	}
}
