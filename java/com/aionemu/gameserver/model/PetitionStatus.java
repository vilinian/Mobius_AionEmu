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
package com.aionemu.gameserver.model;

/**
 * Represents the various states of a player petition.<br>
 * This enum is used to track progress within the {@link com.aionemu.gameserver.model.Petition} system.
 * @author zdead
 */
public enum PetitionStatus
{
	PENDING(0),
	IN_PROGRESS(1),
	REPLIED(2);
	
	private final int element;
	
	/**
	 * Creates a new instance of {@link PetitionStatus}.<br>
	 * This constructor maps the internal integer ID to the enum constant.
	 * @param id The unique identifier for the status.
	 */
	private PetitionStatus(int id)
	{
		element = id;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link PetitionStatus}.<br>
	 * This value is used to map the status to its database representation.
	 * @return The integer ID of the status.
	 */
	public int getElementId()
	{
		return element;
	}
}
