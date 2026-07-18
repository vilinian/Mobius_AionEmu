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
package com.aionemu.gameserver.model.siege;

/**
 * Defines the different types of spawn points available for siege events.<br>
 * This enum is used to categorize how and where entities appear during a {@code Siege} instance.
 */
public enum SiegeSpawnType
{
	PEACE(0),
	GUARD(1),
	ARTIFACT(2),
	PROTECTOR(3),
	MINE(4),
	PORTAL(5),
	GENERATOR(6),
	SPRING(7),
	RACEPROTECTOR(8);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link SiegeSpawnType}.<br>
	 * This constructor assigns the unique identifier to the enum.
	 * @param id The unique integer ID for this spawn type.
	 */
	private SiegeSpawnType(int id)
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
