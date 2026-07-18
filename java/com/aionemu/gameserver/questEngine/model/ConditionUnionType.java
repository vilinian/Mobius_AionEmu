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
package com.aionemu.gameserver.questEngine.model;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Defines the logical operations used to combine multiple quest conditions.<br>
 * It determines whether conditions must all be met or if only one needs to be true.<br>
 * This enum is used by the {@code QuestCondition} system.
 * @author Mr. Poke
 */
@XmlEnum
public enum ConditionUnionType
{
	AND,
	OR;
	
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
	 * Converts a string into a {@link ConditionUnionType}.<br>
	 * This method is used to map text values to the correct enum constant.
	 * @param v The string representation of the type.
	 * @return The corresponding {@code ConditionUnionType} object.
	 */
	public static ConditionUnionType fromValue(String v)
	{
		return valueOf(v);
	}
}
