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
package com.aionemu.gameserver.world.knownlist;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/**
 * Manages a list of known creatures that are aware of the player's presence.<br>
 * This class extends {@link KnownList} to handle specific logic for creature visibility and interaction.
 * @author ATracer
 */
public class CreatureAwareKnownList extends KnownList
{
	/**
	 * Creates a new {@link CreatureAwareKnownList} for a specific object.<br>
	 * This list tracks creatures that the owner is aware of.<br>
	 * It initializes the parent class with the provided owner.
	 * @param owner The {@code VisibleObject} that owns this known list.
	 */
	public CreatureAwareKnownList(VisibleObject owner)
	{
		super(owner);
	}
	
	/**
	 * Checks if the system recognizes a specific object.<br>
	 * This method determines if the {@code newObject} is an instance of {@link Creature}.
	 * @param newObject The object to check.
	 * @return {@code true} if the object is a creature, otherwise {@code false}.
	 */
	@Override
	protected boolean isAwareOf(VisibleObject newObject)
	{
		return newObject instanceof Creature;
	}
}
