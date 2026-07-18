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
 * Represents the different types of events that can occur within a group.<br>
 * This enum is used to handle legacy group-related actions in the game server.
 * @author Lyahim
 */
public enum GroupEvent
{
	LEAVE(0),
	MOVEMENT(1),
	DISCONNECTED(3),
	JOIN(5),
	ENTER_OFFLINE(7),
	ENTER(13),
	UPDATE(13),
	UNK(9); // to do
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link GroupEvent}.<br>
	 * This constructor assigns the unique identifier to the enum constant.
	 * @param id The integer value used to identify the event type.
	 */
	private GroupEvent(int id)
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
