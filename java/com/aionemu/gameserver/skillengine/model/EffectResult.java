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
 * Represents the outcome of a skill effect execution.<br>
 * This enum is used by the {@code skillengine} to determine if an action succeeded or failed.
 * @author Cheatkiller
 */
public enum EffectResult
{
	NORMAL(0),
	ABSORBED(1),
	CONFLICT(2);
	
	private final int id;
	
	/**
	 * This is a private constructor for the {@link EffectResult} enum.<br>
	 * It initializes the internal {@code id} field.
	 * @param id The unique integer identifier for the effect result.
	 */
	private EffectResult(int id)
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
