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
package com.aionemu.gameserver.geoEngine.collision;

import java.util.EnumSet;

/**
 * Defines the different types of collision behaviors for entities in the game world.<br>
 * This enum helps the {@code CollisionEngine} determine how objects should interact with each other.
 * @author Rolandas
 */
public enum CollisionIntention
{
	NONE(0),
	PHYSICAL(1 << 0), // Physical collision
	MATERIAL(1 << 1), // Mesh materials with skills
	SKILL(1 << 2), // Skill obstacles
	WALK(1 << 3), // Walk/NoWalk obstacles
	DOOR(1 << 4), // Doors which have a state opened/closed
	EVENT(1 << 5), // Appear on event only
	MOVEABLE(1 << 6), // Ships, shugo boxes
	
	// This property allows nodes to enumerate their child geometries; nodes without this property will not have their children enumerated for collisions.
	// to speed up processing
	ALL(PHYSICAL.getId() | MATERIAL.getId() | SKILL.getId() | WALK.getId() | DOOR.getId() | EVENT.getId() | MOVEABLE.getId());
	
	private final byte id;
	
	/**
	 * Creates a new instance of {@link CollisionIntention}.<br>
	 * This constructor assigns the internal bitmask ID.
	 * @param id The integer value representing the collision flag.
	 */
	private CollisionIntention(int id)
	{
		this.id = (byte) id;
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
	 * Converts an integer bitmask into a set of {@link CollisionIntention} flags.<br>
	 * This method checks which bits are set in the provided {@code value}.<br>
	 * It ignores the {@code NONE} and {@code ALL} constants during conversion.
	 * @param value The integer bitmask representing collision intentions.
	 * @return An {@link EnumSet} containing the corresponding {@link CollisionIntention} flags.
	 */
	public static EnumSet<CollisionIntention> getFlagsFormValue(int value)
	{
		final EnumSet<CollisionIntention> result = EnumSet.noneOf(CollisionIntention.class);
		for (CollisionIntention m : CollisionIntention.values())
		{
			if ((value & m.getId()) == m.getId())
			{
				if ((m == NONE) || (m == ALL))
				{
					continue;
				}
				
				result.add(m);
			}
		}
		
		return result;
	}
	
	/**
	 * Converts an integer bitmask into a readable string.<br>
	 * This method identifies which {@link CollisionIntention} flags are set.<br>
	 * It ignores the {@code NONE} and {@code ALL} values during conversion.
	 * @param value The integer bitmask to convert.
	 * @return A comma-separated string of the active collision intentions.
	 */
	public static String toString(int value)
	{
		String str = "";
		for (CollisionIntention m : CollisionIntention.values())
		{
			if ((m == NONE) || (m == ALL))
			{
				continue;
			}
			
			if ((value & m.getId()) == m.getId())
			{
				str += m.toString();
				str += ", ";
			}
		}
		
		if (str.length() > 0)
		{
			str = str.substring(0, str.length() - 2);
		}
		
		return str;
	}
}
