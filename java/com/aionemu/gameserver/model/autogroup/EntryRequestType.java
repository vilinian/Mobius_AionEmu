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
package com.aionemu.gameserver.model.autogroup;

import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;

/**
 * Defines the types of requests used when a player attempts to join an auto-group.<br>
 * This enum helps the system identify different entry conditions and actions.
 * @author xTz
 */
public enum EntryRequestType
{
	NEW_GROUP_ENTRY((byte) 0),
	QUICK_GROUP_ENTRY((byte) 1),
	GROUP_ENTRY((byte) 2);
	
	private final byte id;
	
	/**
	 * Creates a new instance of {@link EntryRequestType}.<br>
	 * This constructor assigns the unique identifier to the enum constant.
	 * @param id The numeric ID assigned to this request type.
	 */
	private EntryRequestType(byte id)
	{
		this.id = id;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link CollisionIntention}.<br>
	 * This value is used to represent the collision type as a byte.
	 * @return The {@code byte} ID of the current enum constant.
	 */
	public byte getId()
	{
		return id;
	}
	
	/**
	 * Checks if the entry type is a quick group entry.<br>
	 * This method returns {@code true} if the internal ID matches {@code 1}.
	 * @return {@code true} if this is a quick group entry, otherwise {@code false}.
	 */
	public boolean isQuickGroupEntry()
	{
		return id == 1;
	}
	
	/**
	 * Checks if the entry type is a standard group entry.<br>
	 * This method returns {@code true} if the ID matches {@code 2}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if this is a group entry, {@code false} otherwise.
	 */
	public boolean isGroupEntry()
	{
		return id == 2;
	}
	
	/**
	 * Finds an {@link EntryRequestType} based on its unique identifier.<br>
	 * This method searches through all available types to find a match.
	 * @param id The {@code byte} ID of the type to find.
	 * @return The matching {@code EntryRequestType} or {@code null} if no match is found.
	 */
	public static EntryRequestType getTypeById(byte id)
	{
		for (EntryRequestType ert : values())
		{
			if (ert.getId() == id)
			{
				return ert;
			}
		}
		
		return null;
	}
}
