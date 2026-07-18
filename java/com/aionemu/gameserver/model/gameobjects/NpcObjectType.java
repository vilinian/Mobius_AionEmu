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
package com.aionemu.gameserver.model.gameobjects;

/**
 * Defines the different categories of {@code NpcType} available in the game world.<br>
 * This enum is used to classify NPCs for various game logic and filtering systems.
 * @author ATracer
 */
public enum NpcObjectType
{
	NORMAL(1),
	SUMMON(2),
	HOMING(16),
	TRAP(32),
	SKILLAREA(64),
	TOTEM(128), // TODO not implemented
	GROUPGATE(256),
	SERVANT(1024),
	PET(2048); // TODO not used
	
	/**
	 * Creates a new instance of {@link NpcObjectType}.<br>
	 * This constructor assigns the internal ID to the enum constant.
	 * @param id The unique integer identifier for the NPC type.
	 */
	private NpcObjectType(int id)
	{
		this.id = id;
	}
	
	private final int id;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
