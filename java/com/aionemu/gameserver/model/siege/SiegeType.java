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

import com.aionemu.gameserver.model.InstanceEntryCostEnum;

/**
 * Defines the different types of siege events available in the game.<br>
 * This enum is used to categorize various {@code Siege} instances.
 * @author Sarynth
 */
public enum SiegeType
{
	// Standard
	
	FORTRESS(0),
	ARTIFACT(1),
	// Balauria Commanders?
	BOSSRAID_LIGHT(2),
	BOSSRAID_DARK(3),
	// Unk
	INDUN(4),
	UNDERPASS(5),
	SOURCE(6);
	
	private final int typeId;
	
	/**
	 * Creates a new instance of {@link SiegeType}.<br>
	 * This constructor maps the internal ID to the specific siege type.
	 * @param id The unique integer identifier for the siege type.
	 */
	private SiegeType(int id)
	{
		typeId = id;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link InstanceEntryCostEnum}.<br>
	 * This value corresponds to the internal ID used by the game engine.
	 * @return The integer ID of the entry cost type.
	 */
	public int getTypeId()
	{
		return typeId;
	}
}
