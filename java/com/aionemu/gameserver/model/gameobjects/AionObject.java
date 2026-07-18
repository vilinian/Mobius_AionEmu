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

import java.util.function.Function;

import com.aionemu.gameserver.model.autogroup.AGPlayer;

/**
 * This serves as the base class for all in-game objects that players can interact with.<br>
 * Examples include {@link com.aionemu.gameserver.model.gameobjects.AionObject} types such as NPCs, monsters, players, and items.<br>
 * Each instance is uniquely identified by its {@code objectId}.
 * @author -Nemesiss-, SoulKeeper
 */
public abstract class AionObject
{
	public static Function<AionObject, Integer> OBJECT_TO_ID_TRANSFORMER = new Function<>()
	{
		@Override
		public Integer apply(AionObject input)
		{
			return input != null ? input.getObjectId() : null;
		}
	};
	/**
	 * Unique id, for all game objects such as: items, players, monsters.
	 */
	private final Integer objectId;
	
	/**
	 * Creates a new instance of an {@link AionObject}.<br>
	 * This constructor sets the unique identifier for the object.
	 * @param objId The unique ID to assign to this object.
	 */
	public AionObject(Integer objId)
	{
		objectId = objId;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link AGPlayer}.<br>
	 * This ID is used to identify the player in the game world.
	 * @return The {@code Integer} object ID.
	 */
	public Integer getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Returns name of the object.<br>
	 * Unique for players, common for NPCs, items, etc
	 * @return name of the object
	 */
	public abstract String getName();
}
