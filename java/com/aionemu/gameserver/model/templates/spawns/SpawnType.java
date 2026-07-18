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
package com.aionemu.gameserver.model.templates.spawns;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different types of monster spawns available in the game world.<br>
 * This enum is used to categorize how entities appear and behave within the {@link com.aionemu.gameserver.model.templates.spawns.Spawn} system.
 * @author Rolandas
 */
@XmlType(name = "SpawnType")
@XmlEnum
public enum SpawnType
{
	MANAGER,
	TELEPORT,
	SIGN;
	
	/**
	 * Private constructor for the {@link SpawnType} enum.<br>
	 * This prevents the creation of new instances using {@code new}.
	 */
	private SpawnType()
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
	 * Converts a {@code String} into its corresponding {@link SpawnType}.<br>
	 * This method is used to map text values back to the enum constants.
	 * @param v The string value to convert.
	 * @return The matching {@code SpawnType} constant.
	 */
	public static SpawnType fromValue(String v)
	{
		return valueOf(v);
	}
}
