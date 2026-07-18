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
package com.aionemu.gameserver.model.gameobjects.state;

/**
 * Represents the various visual states of a {@link com.aionemu.gameserver.model.gameobjects.Creature}.<br>
 * This enum is used to determine how a creature should be rendered in the game world.<br>
 * It helps manage animations and appearances based on current actions or conditions.
 * @author Sweetkr
 */
public enum CreatureVisualState
{
	VISIBLE(0), // Normal
	HIDE1(1), // Hide I
	HIDE2(2), // Hide II
	HIDE3(3), // Hide by Artifact?
	HIDE5(5), // No idea :D
	HIDE10(10), // Hide from Npc?
	HIDE13(13), // Hide from Npc?
	HIDE20(20), // Hide from Npc?
	BLINKING(64), // Blinking when entering to zone
	INVISIBLE(128); // Invisible
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link CreatureVisualState}.<br>
	 * This constructor maps the state to its internal numeric identifier.
	 * @param id The unique integer ID for the visual state.
	 */
	private CreatureVisualState(int id)
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
