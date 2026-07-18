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
package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.stats.calc.StatOwner;

/**
 * Represents an item stone used to provide various statistics to a character.<br>
 * This class implements {@link StatOwner} to manage the bonuses associated with the stone.
 * @author ATracer modified by Wakizashi
 */
public class ItemStone implements StatOwner
{
	private final int itemObjId;
	private final int itemId;
	private int slot;
	private PersistentState persistentState;
	
	public static enum ItemStoneType
	{
		MANASTONE,
		GODSTONE,
		FUSIONSTONE,
		IDIANSTONE,
		ODIANSTONE,
		RUNESTONE;
	}
	
	/**
	 * Creates a new instance of an {@link ItemStone}.<br>
	 * This constructor initializes all required fields for the stone.
	 * @param itemObjId The unique object identifier for the item.
	 * @param itemId The base item ID.
	 * @param slot The specific equipment slot index.
	 * @param persistentState The state of the item in the database.
	 */
	public ItemStone(int itemObjId, int itemId, int slot, PersistentState persistentState)
	{
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.slot = slot;
		this.persistentState = persistentState;
	}
	
	/**
	 * Retrieves the unique object identifier for this item.<br>
	 * This ID distinguishes specific instances of items in the game world.
	 * @return The {@code int} value representing the unique object ID.
	 */
	public int getItemObjId()
	{
		return itemObjId;
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
	 * Retrieves the current slot position of this wardrobe entry.<br>
	 * This value is used to identify where the item is located in the inventory.
	 * @return The {@code int} value representing the slot index.
	 */
	public int getSlot()
	{
		return slot;
	}
	
	/**
	 * Updates the equipment slot identifier.<br>
	 * This method sets the {@code slot} value for this object.<br>
	 * It also marks the persistent state as requiring an update.
	 * @param slot The new integer value for the equipment slot.
	 */
	public void setSlot(int slot)
	{
		this.slot = slot;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this object.<br>
	 * This method prevents changing from {@code PersistentState.NEW} to {@code PersistentState.UPDATE_REQUIRED}.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch (persistentState)
		{
			case DELETED:
				if (this.persistentState == PersistentState.NEW)
				{
					this.persistentState = PersistentState.NOACTION;
				}
				else
				{
					this.persistentState = PersistentState.DELETED;
				}
				break;
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
				{
					break;
				}
			default:
				this.persistentState = persistentState;
		}
	}
}
