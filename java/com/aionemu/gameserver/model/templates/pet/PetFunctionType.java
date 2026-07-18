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
package com.aionemu.gameserver.model.templates.pet;

/**
 * Defines the types of functions that a {@code Pet} can perform.<br>
 * This enum is used to categorize different pet abilities and behaviors.
 * @author IlBuono, Rolandas
 */
public enum PetFunctionType
{
	WAREHOUSE(0, true),
	FOOD(1, 64),
	DOPING(2, 256),
	LOOT(3, 8),
	APPEARANCE(1),
	NONE(4, true),
	BUFF(5),
	MERCHANT(6),
	// non writable to packets
	BAG(-1),
	WING(-2);
	
	private final short id;
	private boolean isPlayerFunc = false;
	
	/**
	 * Creates a new {@code PetFunctionType} with a specific ID and player function status.<br>
	 * This constructor allows for manual configuration of the pet's behavior properties.
	 * @param id The unique identifier for the pet function.
	 * @param isPlayerFunc Determines if this function is accessible to players. Set to {@code true} or {@code false}.
	 */
	PetFunctionType(int id, boolean isPlayerFunc)
	{
		this(id);
		this.isPlayerFunc = isPlayerFunc;
	}
	
	/**
	 * Creates a new {@code PetFunctionType} using an ID and a bit count.<br>
	 * This constructor sets the internal ID by shifting the {@code dataBitCount}.<br>
	 * It also marks this type as a player function.
	 * @param id The unique identifier for the pet function.
	 * @param dataBitCount The number of bits used for associated data.
	 */
	PetFunctionType(int id, int dataBitCount)
	{
		this((dataBitCount << 5) | id);
		isPlayerFunc = true;
	}
	
	/**
	 * Creates a new {@link PetFunctionType} using a specific ID.<br>
	 * This method masks the input value to fit into a {@code short}.
	 * @param id The unique identifier for the pet function.
	 */
	PetFunctionType(int id)
	{
		this.id = (short) (id & 0xFFFF);
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Checks if the pet function is intended for player use.<br>
	 * This method returns {@code true} if the function is a player-facing action.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if it is a player function, {@code false} otherwise.
	 */
	public boolean isPlayerFunction()
	{
		return isPlayerFunc;
	}
}
