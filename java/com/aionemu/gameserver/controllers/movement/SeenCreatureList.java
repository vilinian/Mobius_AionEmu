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
package com.aionemu.gameserver.controllers.movement;

import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This class manages a collection of {@link Creature} objects that have been detected by the player.<br>
 * It helps the server track which entities are currently visible to prevent unnecessary updates.
 * @author Rolandas
 */
public class SeenCreatureList
{
	private Map<Integer, Creature> seenCreatures;
	
	/**
	 * Adds a {@link Creature} to the list of seen creatures.<br>
	 * This method checks if the creature is already present.<br>
	 * It returns {@code true} if the addition was successful.<br>
	 * It returns {@code false} if the creature was already in the list.
	 * @param creature The {@code Creature} object to add.
	 * @return {@code true} if added, otherwise {@code false}.
	 */
	public boolean add(Creature creature)
	{
		if (seenCreatures == null)
		{
			seenCreatures = new HashMap<>();
		}
		
		return seenCreatures.putIfAbsent(creature.getObjectId(), creature) == null;
	}
	
	/**
	 * Removes a specific {@link Creature} from the list.<br>
	 * This method checks if the creature exists before trying to delete it.
	 * @param creature The {@code Creature} object to be removed.
	 * @return {@code true} if the creature was successfully removed, {@code false} otherwise.
	 */
	public boolean remove(Creature creature)
	{
		if (seenCreatures == null)
		{
			return false;
		}
		
		return seenCreatures.remove(creature.getObjectId()) != null;
	}
	
	/**
	 * Removes all elements from the set.<br>
	 * The {@code size()} will become 0 after this call.<br>
	 * This method does not affect other collections.
	 */
	public void clear()
	{
		if (seenCreatures != null)
		{
			seenCreatures.clear();
		}
	}
	
	/**
	 * Checks if a specific {@link Creature} is in the list.<br>
	 * It returns {@code true} if the creature exists.<br>
	 * It returns {@code false} otherwise.
	 * @param creature The {@code Creature} to search for.
	 * @return {@code true} if found, {@code false} if not found.
	 */
	public boolean contains(Creature creature)
	{
		if (seenCreatures == null)
		{
			return false;
		}
		
		return seenCreatures.containsKey(creature.getObjectId());
	}
}
