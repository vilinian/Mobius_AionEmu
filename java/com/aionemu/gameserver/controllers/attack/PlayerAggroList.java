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
package com.aionemu.gameserver.controllers.attack;

import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * Manages the list of creatures currently targeting a specific player.<br>
 * It extends {@link AggroList} to handle player-specific aggression logic.
 * @author ATracer
 */
public class PlayerAggroList extends AggroList
{
	/**
	 * Creates a new {@link PlayerAggroList} for a specific creature.<br>
	 * This list tracks which players are currently aggressive toward the owner.
	 * @param owner The {@code Creature} that owns this aggro list.
	 */
	public PlayerAggroList(Creature owner)
	{
		super(owner);
	}
	
	/**
	 * Checks if a specific {@link Creature} is considered an enemy of the owner.<br>
	 * This method returns {@code true} if the creature is not the owner and has a hostile relationship.
	 * @param creature The {@link Creature} to check for awareness.
	 * @return {@code true} if the creature is an enemy, {@code false} otherwise.
	 */
	@Override
	protected boolean isAware(Creature creature)
	{
		return (creature != null) && !creature.getObjectId().equals(owner.getObjectId());
	}
}
