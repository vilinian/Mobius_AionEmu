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
 * Defines the different types of rewards that can be granted upon completing a challenge.<br>
 * This enum is used to categorize items, currency, or other benefits within the {@code Challenge} system.
 */
@XmlType(name = "RewardType")
@XmlEnum
public enum RewardType
{
	NONE,
	POINT,
	SPAWN;
	
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
	 * Converts a string into its corresponding {@link RewardType}.<br>
	 * This method is used to map text values back to the enum constants.
	 * @param paramString The string representation of the reward type.
	 * @return The matching {@code RewardType} constant.
	 */
	public static RewardType fromValue(String paramString)
	{
		return valueOf(paramString);
	}
}
