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
package com.aionemu.gameserver.model.team2.common.legacy;

/**
 * Represents the different types of events related to player alliances.<br>
 * This enum is used to categorize alliance-related actions within the legacy system.
 * @author Sarynth
 */
public enum PlayerAllianceEvent
{
	LEAVE(0),
	LEAVE_TIMEOUT(0),
	BANNED(0),
	MOVEMENT(1),
	DISCONNECTED(3),
	JOIN(5),
	ENTER_OFFLINE(7),
	// Similar to 0, 1, 3 -- only the initial information block.
	UNK(9),
	RECONNECT(13),
	ENTER(13),
	UPDATE(13),
	MEMBER_GROUP_CHANGE(5),
	// Extra? Unused?
	APPOINT_VICE_CAPTAIN(13),
	DEMOTE_VICE_CAPTAIN(13),
	APPOINT_CAPTAIN(13);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link PlayerAllianceEvent}.<br>
	 * This constructor assigns the unique identifier to the enum constant.
	 * @param id The integer value associated with the event type.
	 */
	private PlayerAllianceEvent(int id)
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
