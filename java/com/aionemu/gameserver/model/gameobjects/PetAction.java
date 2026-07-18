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

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Defines the various types of actions a {@link com.aionemu.gameserver.model.gameobjects.Pet} can perform.<br>
 * This enum is used to identify specific behaviors and states for pet entities in the game world.
 * @author ATracer
 */
public enum PetAction
{
	ADOPT(1),
	SURRENDER(2),
	SPAWN(3),
	DISMISS(4),
	TALK_WITH_MERCHANT(6),
	TALK_WITH_MINDER(7),
	FOOD(9),
	RENAME(10),
	MOOD(12),
	UNKNOWN(255);
	
	private static TIntObjectHashMap<PetAction> petActions;
	
	static
	{
		petActions = new TIntObjectHashMap<>();
		for (PetAction action : values())
		{
			petActions.put(action.getActionId(), action);
		}
	}
	
	private final int actionId;
	
	/**
	 * Creates a new {@link PetAction} instance.<br>
	 * This constructor maps the internal ID to the enum constant.
	 * @param actionId The unique integer identifier for the pet action.
	 */
	private PetAction(int actionId)
	{
		this.actionId = actionId;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link MinionAction}.<br>
	 * This ID is used to map actions between the client and server.
	 * @return The integer value of the {@code actionId}.
	 */
	public int getActionId()
	{
		return actionId;
	}
	
	/**
	 * Retrieves a {@link PetAction} based on its unique ID.<br>
	 * If the ID is not found, it returns {@code PetAction#UNKNOWN}.
	 * @param actionId The integer ID of the action to find.
	 * @return The corresponding {@code PetAction} or {@code UNKNOWN}.
	 */
	public static PetAction getActionById(int actionId)
	{
		final PetAction action = petActions.get(actionId);
		return action != null ? action : UNKNOWN;
	}
}
