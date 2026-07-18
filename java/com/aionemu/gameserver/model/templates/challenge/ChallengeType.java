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
package com.aionemu.gameserver.model.templates.challenge;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different types of challenges available in the game.<br>
 * This enum is used to categorize and identify specific challenge behaviors within the {@code Challenge} system.
 */
@XmlType(name = "ChallengeType")
@XmlEnum
public enum ChallengeType
{
	LEGION(1),
	TOWN(2);
	
	private final int id;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Creates a new instance of {@link ChallengeType}.<br>
	 * This constructor assigns the unique identifier to the enum constant.
	 * @param id The unique integer ID for this challenge type.
	 */
	private ChallengeType(int id)
	{
		this.id = id;
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
	 * Converts a string into its corresponding {@link ChallengeType}.<br>
	 * This method uses the standard {@code valueOf} logic.
	 * @param paramString The string name of the challenge type.
	 * @return The matching {@code ChallengeType} enum constant.
	 */
	public static ChallengeType fromValue(String paramString)
	{
		return valueOf(paramString);
	}
}
