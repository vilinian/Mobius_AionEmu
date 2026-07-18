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

/**
 * Defines the different types of movements associated with a skill.<br>
 * This enum is used by the {@link com.aionemu.gameserver.skillengine.SkillEngine} to determine how a character moves during an action.
 * @author MrPoke
 */
public enum SkillMoveType
{
	RESIST(0),
	DEFAULT(16),
	PULL(50), // OLD 18 NEW 50 (5.6)
	OPENAERIAL(20),
	KNOCKBACK(28),
	MOVEBEHIND(48),
	STAGGER(112), // 5.1
	STUMBLE(16), // 5.1
	NEWPULL(54); // 5.1
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link SkillMoveType}.<br>
	 * This constructor assigns the unique identifier to the enum constant.
	 * @param id The numeric ID associated with the skill move type.
	 */
	private SkillMoveType(int id)
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
