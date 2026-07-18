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
package com.aionemu.gameserver.model.gameobjects;

/**
 * Defines the different categories of creatures within the game world.<br>
 * This enum is used to identify and group various {@link Creature} types for logic processing.
 * @author Luno modified by Wakizashi (CHEST)
 */
public enum CreatureType
{
	NULL(-1),
	/**
	 * These are regular monsters
	 */
	ATTACKABLE(0),
	/**
	 * These are Peace npc
	 */
	PEACE(2),
	/**
	 * These are monsters that are pre-aggressive
	 */
	AGGRESSIVE(8),
	// unk
	INVULNERABLE(10),
	/**
	 * These are non attackable NPCs
	 */
	FRIEND(38),
	SUPPORT(54);
	
	private final int someClientSideId;
	
	/**
	 * Creates a new instance of {@link CreatureType}.<br>
	 * This constructor assigns the internal client-side ID.
	 * @param id The unique integer identifier for the creature type.
	 */
	private CreatureType(int id)
	{
		someClientSideId = id;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link CreatureType}.<br>
	 * This value corresponds to the internal type code.
	 * @return The integer ID of the creature type.
	 */
	public int getId()
	{
		return someClientSideId;
	}
	
	/**
	 * Finds a {@link CreatureType} based on its unique ID.<br>
	 * This method searches through all available types.<br>
	 * It returns the matching type or {@code null} if no match is found.
	 * @param id The integer ID of the creature type to look for.
	 * @return The corresponding {@link CreatureType} or {@code null}.
	 */
	public static CreatureType getCreatureType(int id)
	{
		for (CreatureType ct : values())
		{
			if (ct.getId() == id)
			{
				return ct;
			}
		}
		
		return null;
	}
}
