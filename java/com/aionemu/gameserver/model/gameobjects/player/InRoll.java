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
package com.aionemu.gameserver.model.gameobjects.player;

/**
 * Represents a player's current roll state within the game world.<br>
 * This class manages data related to active rolling actions for {@link Player} objects.
 * @author xTz
 */
public class InRoll
{
	private int npcId;
	private int itemId;
	private int rollType;
	private int index;
	
	/**
	 * Creates a new instance of {@link InRoll}.<br>
	 * This constructor initializes all required fields.
	 * @param npcId The unique identifier for the NPC.
	 * @param itemId The unique identifier for the item.
	 * @param index The position or sequence number.
	 * @param rollType The specific type of roll to perform.
	 */
	public InRoll(int npcId, int itemId, int index, int rollType)
	{
		this.npcId = npcId;
		this.itemId = itemId;
		this.index = index;
		this.rollType = rollType;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Retrieves the current index of this {@code Triangle}.<br>
	 * This value is used to identify the triangle in a collection.
	 * @return The integer value of the {@code index}.
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 * Retrieves the type of the roll.<br>
	 * This value identifies how the roll is processed.
	 * @return The {@code int} value representing the roll type.
	 */
	public int getRollType()
	{
		return rollType;
	}
	
	/**
	 * Sets the unique identifier for the NPC.<br>
	 * This updates the {@code npcId} field of this {@link InRoll} instance.
	 * @param npcId The new ID to assign to the NPC.
	 */
	public void setNpcId(int npcId)
	{
		this.npcId = npcId;
	}
	
	/**
	 * Sets the unique identifier for the item.<br>
	 * This method updates the {@code itemId} field.
	 * @param itemId The new ID to assign to the item.
	 */
	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}
	
	/**
	 * Updates the {@code index} field of this object.<br>
	 * This method sets the internal value to the provided {@code index}.
	 * @param index The new integer value to assign to the index.
	 */
	public void setIndexd(int index)
	{
		this.index = itemId;
	}
	
	/**
	 * Sets the type of the roll.<br>
	 * This updates the {@code rollType} field for this {@link InRoll} object.
	 * @param rollType The new integer value to assign to the roll type.
	 */
	public void setRollType(int rollType)
	{
		this.rollType = rollType;
	}
}
