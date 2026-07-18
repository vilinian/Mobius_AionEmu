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

/**
 * Represents a single item entry within the {@code Wardrobe} system.<br>
 * This class stores the specific data and properties for an individual wardrobe object.
 */
public class WardrobeEntry
{
	private final int itemId;
	private final int slot;
	private final int reskin_count;
	
	/**
	 * Creates a new instance of {@link WardrobeEntry}.<br>
	 * This constructor initializes the item details for the wardrobe.
	 * @param itemId The unique identifier for the item.
	 * @param slot The specific slot position in the wardrobe.
	 * @param reskin_count The number of available reskins for this item.
	 */
	public WardrobeEntry(int itemId, int slot, int reskin_count)
	{
		this.itemId = itemId;
		this.slot = slot;
		this.reskin_count = reskin_count;
	}
	
	/**
	 * Retrieves the number of reskins for this wardrobe entry.<br>
	 * This value is stored in the {@code reskin_count} field.
	 * @return The total count of reskins as an {@code int}.
	 */
	public int getReskinCount()
	{
		return reskin_count;
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
}
