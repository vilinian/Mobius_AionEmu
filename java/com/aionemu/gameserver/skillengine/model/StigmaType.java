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
 * Represents the different types of {@code Stigma} available in the game.<br>
 * This enumeration is used by the {@code SkillEngine} to categorize skill effects.
 * @author Cheatkiller
 */
@XmlType(name = "StigmaType")
@XmlEnum
public enum StigmaType
{
	NONE(0),
	BASIC(1),
	ADVANCED(2),
	MAJOR(3);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link StigmaType}.<br>
	 * This constructor assigns the internal identifier to the enum constant.
	 * @param id The unique integer value for this stigma type.
	 */
	private StigmaType(int id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
