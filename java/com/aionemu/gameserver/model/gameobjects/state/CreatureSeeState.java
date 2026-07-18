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
 * Represents the visibility states of a {@link com.aionemu.gameserver.model.gameobjects.Creature}.<br>
 * This enum defines whether a creature is currently visible to other entities in the game world.
 * @author Sweetkr
 */
public enum CreatureSeeState
{
	NORMAL(0), // Normal
	SEARCH1(1), // See-Through: Hide I
	SEARCH2(2), // See-Through: Hide II
	SEARCH5(5), // npc stealth
	SEARCH10(10), // 3.0 npc stealth
	ADMIN(128);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link CreatureSeeState}.<br>
	 * This constructor maps the state to a specific numeric identifier.
	 * @param id The unique integer value for this state.
	 */
	private CreatureSeeState(int id)
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
