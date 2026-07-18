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
package com.aionemu.gameserver.model.team.legion;

/**
 * Defines the different types of emblems available for a {@link com.aionemu.gameserver.model.team.legion.Legion}.<br>
 * This enum is used to categorize and identify specific legion visual markers.
 * @author cura
 */
public enum LegionEmblemType
{
	DEFAULT(0x00),
	CUSTOM(0x80);
	
	private final byte value;
	
	/**
	 * Creates a new instance of {@link LegionEmblemType}.<br>
	 * This constructor maps the internal integer to a byte.
	 * @param value The integer value used to define the emblem type.
	 */
	private LegionEmblemType(int value)
	{
		this.value = (byte) value;
	}
	
	/**
	 * Retrieves the underlying numeric value of the {@code LegionEmblemType}.<br>
	 * This is useful for comparing types in low-level logic.
	 * @return The {@code byte} value associated with this enum constant.
	 */
	public byte getValue()
	{
		return value;
	}
}
