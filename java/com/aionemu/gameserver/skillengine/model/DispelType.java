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
package com.aionemu.gameserver.skillengine.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different types of effects that can be removed from a character.<br>
 * This enum is used by the {@code SkillEngine} to categorize dispel mechanics.
 * @author ATracer
 */
@XmlType(name = "DispelType")
@XmlEnum
public enum DispelType
{
	EFFECTID,
	EFFECTIDRANGE,
	EFFECTTYPE,
	SLOTTYPE;
	
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
	 * Converts a string representation into a {@link DispelType} enum constant.<br>
	 * This method is useful for parsing configuration files or database values.
	 * @param v The string value to convert.
	 * @return The corresponding {@code DispelType} constant.
	 */
	public static DispelType fromValue(String v)
	{
		return valueOf(v);
	}
}
