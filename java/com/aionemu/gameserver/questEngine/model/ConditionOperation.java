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
 * Defines the types of logical operations used to evaluate quest conditions.<br>
 * These operations determine how multiple {@code Condition} objects are combined.<br>
 * Examples include {@code AND}, {@code OR}, and {@code NOT}.
 * @author Mr. Poke
 */
@XmlEnum
public enum ConditionOperation
{
	EQUAL,
	GREATER,
	GREATER_EQUAL,
	LESSER,
	LESSER_EQUAL,
	NOT_EQUAL,
	IN,
	NOT_IN;
	
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
	 * Creates a {@link ConditionOperation} from a string representation.<br>
	 * This method converts the provided text into the corresponding enum constant.
	 * @param v The string name of the operation.
	 * @return The matching {@code ConditionOperation} object.
	 */
	public static ConditionOperation fromValue(String v)
	{
		return valueOf(v);
	}
}
