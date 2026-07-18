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
package com.aionemu.gameserver.controllers.attack;

/**
 * Represents the various states of a counter skill during an attack.<br>
 * This enum is used by {@code AttackController} to track skill execution.<br>
 * It helps determine if a counter action is currently active, pending, or completed.
 * @author Ever'
 */
public enum CounterSkillStatus
{
	BLOCK(32),
	PARRY(64),
	DODGE(128),
	RESIST(256);
	
	private final int type;
	
	/**
	 * Creates a new instance of {@link CounterSkillStatus}.<br>
	 * This constructor initializes the internal {@code type} value.
	 * @param type The unique integer identifier for the skill status.
	 */
	private CounterSkillStatus(int type)
	{
		this.type = type;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link AttackStatus}.<br>
	 * This value corresponds to the internal type code.
	 * @return The integer ID of the status.
	 */
	public int getId()
	{
		return type;
	}
}
