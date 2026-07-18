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
package com.aionemu.gameserver.model.templates.zone;

/**
 * Defines the different categories of zones available within the game world.<br>
 * This enumeration is used to classify areas such as towns, dungeons, or fields.
 * @author MrPoke
 */
public enum ZoneType
{
	FLY(0),
	DAMAGE(1),
	WATER(2),
	SIEGE(3),
	PVP(4);
	
	private final byte value;
	
	/**
	 * Creates a new {@link ZoneType} instance.<br>
	 * This constructor maps the provided integer to the internal {@code value}.
	 * @param value The numeric identifier for the zone type.
	 */
	private ZoneType(int value)
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
