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
package com.aionemu.gameserver.model.actions;

import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This class handles the various actions and behaviors performed by {@link Creature} objects.<br>
 * It defines how creatures interact with the game world and other entities.
 * @author xTz
 */
public class CreatureActions
{
	/**
	 * Retrieves the name of a specific {@link Creature}.<br>
	 * This method calls the {@code getName()} method on the provided object.
	 * @param creature The {@code Creature} object to check.
	 * @return The name of the {@code Creature} as a {@code String}.
	 */
	public static String getName(Creature creature)
	{
		return creature.getName();
	}
	
	/**
	 * Checks if a specific {@link Creature} is currently dead.<br>
	 * This method calls the internal life stats of the object.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is dead, {@code false} otherwise.
	 */
	public static boolean isAlreadyDead(Creature creature)
	{
		return creature.getLifeStats().isAlreadyDead();
	}
	
	/**
	 * Removes a {@link Creature} from the game world.<br>
	 * This method calls the {@code onDelete()} method on the creature's controller.<br>
	 * It checks if the {@code creature} is {@code null} before proceeding.
	 * @param creature The {@code Creature} object to be deleted.
	 */
	public static void delete(Creature creature)
	{
		if (creature != null)
		{
			creature.getController().onDelete();
		}
	}
}
