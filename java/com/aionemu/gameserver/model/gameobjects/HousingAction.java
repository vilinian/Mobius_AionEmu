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

import com.aionemu.gameserver.model.EmotionType;

/**
 * Defines the types of actions that can be performed within a housing system.<br>
 * This enum is used to identify specific interactions for {@link com.aionemu.gameserver.model.gameobjects.HousingAction}.
 * @author Ever'
 */
public enum HousingAction
{
	UNK(-1),
	ENTER_DECORATION(1),
	EXIT_DECORATION(2),
	ADD_ITEM(3),
	DELETE_ITEM(4),
	SPAWN_OBJECT(5),
	MOVE_OBJECT(6),
	DESPAWN_OBJECT(7),
	ENTER_RENOVATION(14),
	EXIT_RENOVATION(15),
	CHANGE_APPEARANCE(16);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link HousingAction}.<br>
	 * This constructor assigns the unique identifier to the object.
	 * @param id The integer value representing the action type.
	 */
	private HousingAction(int id)
	{
		this.id = id;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link EmotionType}.<br>
	 * This value corresponds to the internal ID used by the game engine.
	 * @return The integer ID of the emotion type.
	 */
	public int getTypeId()
	{
		return id;
	}
	
	/**
	 * Retrieves a {@link HousingAction} based on its unique ID.<br>
	 * This method searches through all available actions.<br>
	 * It returns {@code UNK} if no matching ID is found.
	 * @param id The integer ID of the action to find.
	 * @return The corresponding {@code HousingAction} or {@code UNK}.
	 */
	public static HousingAction getActionTypeById(int id)
	{
		for (HousingAction actionType : values())
		{
			if (actionType.getTypeId() == id)
			{
				return actionType;
			}
		}
		
		return UNK;
	}
}
