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
package com.aionemu.gameserver.model.dorinerk_wardrobe;

import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * Represents an individual item stored in a player's wardrobe.<br>
 * This class extends {@link WardrobeEntry} to manage specific wardrobe data for players.
 */
public class PlayerWardrobeEntry extends WardrobeEntry
{
	private PersistentState persistentState;
	
	/**
	 * Creates a new instance of {@link PlayerWardrobeEntry}.<br>
	 * This constructor initializes the wardrobe item with its specific properties.
	 * @param itemId The unique identifier for the item.
	 * @param slot The index of the wardrobe slot.
	 * @param reskin_count The number of available reskins for this item.
	 * @param persistentState The {@code PersistentState} associated with this entry.
	 */
	public PlayerWardrobeEntry(int itemId, int slot, int reskin_count, PersistentState persistentState)
	{
		super(itemId, slot, reskin_count);
		this.persistentState = persistentState;
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
	 * Updates the {@code persistentState} of this wardrobe entry.<br>
	 * This method applies specific logic to handle state transitions.
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
				if (this.persistentState != PersistentState.NEW)
				{
					this.persistentState = PersistentState.UPDATE_REQUIRED;
				}
				break;
			case NOACTION:
				break;
			default:
				this.persistentState = persistentState;
		}
	}
}
