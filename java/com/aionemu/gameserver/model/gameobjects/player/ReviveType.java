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
package com.aionemu.gameserver.model.gameobjects.player;

/**
 * Defines the different types of revival methods available for players.<br>
 * This enum is used to categorize how a character can be brought back to life in the game world.
 */
public enum ReviveType
{
	/**
	 * Revive to bindpoint
	 */
	BIND_REVIVE(0),
	/**
	 * Revive from rebirth effect
	 */
	REBIRTH_REVIVE(1),
	/**
	 * Self-Rez Stone
	 */
	ITEM_SELF_REVIVE(2),
	/**
	 * Revive from skill
	 */
	SKILL_REVIVE(3),
	/**
	 * Revive to Kisk
	 */
	KISK_REVIVE(4),
	/**
	 * Revive to Instance Start point
	 */
	INSTANCE_REVIVE(6),
	/**
	 * Revive to Obelisk
	 */
	OBELISK_REVIVE(8),
	/**
	 * Revive to actual world where player Died
	 */
	START_POINT_REVIVE(11),
	/**
	 * Revive with Luna
	 */
	LUNA_REVIVE(12);
	
	private final int typeId;
	
	/**
	 * Creates a new instance of {@link ReviveType}.<br>
	 * This constructor assigns the unique identifier to the enum.
	 * @param typeId The integer ID representing the specific revive method.
	 */
	private ReviveType(int typeId)
	{
		this.typeId = typeId;
	}
	
	/**
	 * Retrieves the unique identifier for this revive type.<br>
	 * This value corresponds to the internal ID used by the server.
	 * @return The {@code int} representation of the {@link ReviveType}.
	 */
	public int getReviveTypeId()
	{
		return typeId;
	}
	
	/**
	 * Retrieves a {@link ReviveType} based on its unique ID.<br>
	 * This method searches through all available types.<br>
	 * It throws an {@code IllegalArgumentException} if the ID is not found.
	 * @param id The integer ID of the revive type to find.
	 * @return The corresponding {@code ReviveType} object.
	 */
	public static ReviveType getReviveTypeById(int id)
	{
		for (ReviveType rt : values())
		{
			if (rt.typeId == id)
			{
				return rt;
			}
		}
		
		throw new IllegalArgumentException("Unsupported revive type: " + id);
	}
}
