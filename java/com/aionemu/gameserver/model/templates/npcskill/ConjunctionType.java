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
package com.aionemu.gameserver.model.templates.npcskill;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the types of conjunctions used for NPC skills.<br>
 * This enum determines how different skill effects are combined or linked.
 * @author nrg
 */
@XmlType(name = "ConjunctionType")
@XmlEnum
public enum ConjunctionType
{
	AND,
	OR,
	XOR;
	
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
	 * Converts a {@code String} into its corresponding {@link ConjunctionType}.<br>
	 * This method is useful for parsing values from external data.
	 * @param v The string representation of the conjunction type.
	 * @return The matching {@code ConjunctionType} enum constant.
	 */
	public static ConjunctionType fromValue(String v)
	{
		return valueOf(v);
	}
}
