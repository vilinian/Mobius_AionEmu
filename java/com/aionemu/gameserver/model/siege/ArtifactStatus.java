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
 * Represents the various states of a siege artifact.<br>
 * This enum is used to track whether an artifact is active, inactive, or in another specific state.
 * @author MrPoke
 */
public enum ArtifactStatus
{
	IDLE(0),
	ACTIVATION(1),
	CASTING(2),
	ACTIVATED(3);
	
	private final int id;
	
	/**
	 * Creates a new {@link ArtifactStatus} instance.<br>
	 * This constructor assigns an internal integer ID to the status.
	 * @param id The unique identifier for the status.
	 */
	ArtifactStatus(int id)
	{
		this.id = id;
	}
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return id;
	}
}
