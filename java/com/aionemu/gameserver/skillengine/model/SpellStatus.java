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
 * Represents the various states a spell can have during its execution.<br>
 * This enum is used by the {@link com.aionemu.gameserver.skillengine.SkillEngine} to track progress and lifecycle.<br>
 * It helps determine if a spell is currently active, completed, or failed.
 * @author ATracer
 */
public enum SpellStatus
{
	/**
	 * Spell Status 1 : stumble 2 : knockback 4 : open aerial 8 : close aerial 16 : spin 32 : block 64 : parry 128 : dodge 256 : resist
	 */
	NONE(0),
	STUMBLE(1),
	STAGGER(2),
	OPENAERIAL(4),
	CLOSEAERIAL(8),
	SPIN(16),
	BLOCK(32),
	PARRY(64),
	DODGE(128),
	RESIST(256); // TODO SNARE
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link SpellStatus}.<br>
	 * This constructor maps the status to its unique numeric identifier.
	 * @param id The integer value representing the specific spell status.
	 */
	private SpellStatus(int id)
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
