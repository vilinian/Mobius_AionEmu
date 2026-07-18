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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Manages a list of objects that are specifically visible to a {@link Player}.<br>
 * It helps the server track which entities should be rendered for a specific user.
 * @author ATracer
 */
public class PlayerAwareKnownList extends KnownList
{
	/**
	 * Creates a new {@link PlayerAwareKnownList} for a specific object.<br>
	 * This list tracks objects that the owner is aware of.<br>
	 * It initializes the parent class with the provided owner.
	 * @param owner The {@code VisibleObject} that owns this list.
	 */
	public PlayerAwareKnownList(VisibleObject owner)
	{
		super(owner);
	}
	
	/**
	 * Checks if the system recognizes a specific object.<br>
	 * This method determines if the {@code newObject} is an instance of {@link Player}.
	 * @param newObject The object to check.
	 * @return {@code true} if the object is a player, otherwise {@code false}.
	 */
	@Override
	protected boolean isAwareOf(VisibleObject newObject)
	{
		return newObject instanceof Player;
	}
}
