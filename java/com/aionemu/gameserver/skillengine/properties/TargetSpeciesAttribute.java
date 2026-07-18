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
package com.aionemu.gameserver.skillengine.properties;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the types of species attributes that can be targeted by skills.<br>
 * This enum is used to filter or identify specific target categories within the {@code skillengine} system.
 * @author kecimis
 */
@XmlType(name = "TargetSpeciesAttribute")
@XmlEnum
public enum TargetSpeciesAttribute
{
	NONE,
	ALL,
	PC,
	NPC;
	
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
	 * Converts a {@code String} into its corresponding {@link TargetSpeciesAttribute}.<br>
	 * This method is useful for parsing configuration values.
	 * @param v The string representation of the attribute.
	 * @return The matching {@code TargetSpeciesAttribute} enum constant.
	 */
	public static TargetSpeciesAttribute fromValue(String v)
	{
		return valueOf(v);
	}
}
