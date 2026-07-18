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
package com.aionemu.gameserver.model.gameobjects.player.fame;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * Defines the different types of fame available for players.<br>
 * This enumeration is used to categorize and manage player reputation statuses.
 */
public enum FameEnum
{
	
	INGGISON(1, 210050000, 1831919),
	GELKMAROS(2, 220070000, 1831921),
	SILENTERA_CANYON(3, 600010000, 1831923),
	LAKRUM(4, 800050000, 1831927),
	DUMAHA(5, 800060000, 1831929),
	CRIMSON_KATALAM(6, 800030000, 1831925),
	CRIMSON_DANARIA(6, 800040000, 1831925),
	TEMPUS_FUGIT(7, 800070000, 1831931);
	
	private final int value;
	private final int worldId;
	private final DescriptionId descriptionId;
	
	/**
	 * Constructs a new {@link FameEnum} instance.<br>
	 * This private constructor initializes the fame data.
	 * @param value The unique identifier for the fame type.
	 * @param worlId The world ID associated with this fame.
	 * @param descriptionId The raw integer ID used to create a {@link DescriptionId}.
	 */
	private FameEnum(int value, int worlId, int descriptionId)
	{
		this.value = value;
		this.descriptionId = new DescriptionId(descriptionId);
		worldId = worlId;
	}
	
	/**
	 * Retrieves a {@link FameEnum} constant based on its unique ID.<br>
	 * This method searches through all available values.<br>
	 * It throws an exception if the provided ID is not found.
	 * @param value The integer ID of the fame to retrieve.
	 * @return The corresponding {@code FameEnum} object.
	 */
	public static FameEnum getFameById(int value)
	{
		for (FameEnum pc : FameEnum.values())
		{
			if (pc.getValue() != value)
			{
				continue;
			}
			
			return pc;
		}
		
		throw new IllegalArgumentException("There is no fame class with id " + value);
	}
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return value;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return worldId;
	}
	
	/**
	 * Retrieves the unique identifier for the fame description.<br>
	 * This ID is used to fetch the correct text in the game.
	 * @return the {@code DescriptionId} associated with this fame.
	 */
	public DescriptionId getDescriptionId()
	{
		return descriptionId;
	}
}
