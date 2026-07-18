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
package com.aionemu.gameserver.model.trade;

import com.aionemu.gameserver.model.gameobjects.Item;

/**
 * Represents an item available for trade within the exchange system.<br>
 * This class holds the data required to display and process {@link Item} objects in a trade window.
 * @author ATracer
 */
public class ExchangeItem
{
	private final int itemObjId;
	private long itemCount;
	private final int itemDesc;
	private Item item;
	
	/**
	 * Creates a new {@link ExchangeItem} instance.<br>
	 * This constructor initializes the item data for an exchange.<br>
	 * It sets the object ID, count, and the {@code Item} object.
	 * @param itemObjId The unique identifier for the item.
	 * @param itemCount The total number of items in this stack.
	 * @param item The {@link Item} object associated with this exchange.
	 */
	public ExchangeItem(int itemObjId, long itemCount, Item item)
	{
		this.itemObjId = itemObjId;
		this.itemCount = itemCount;
		this.item = item;
		itemDesc = item.getItemTemplate().getNameId();
	}
	
	/**
	 * Updates the {@code item} associated with this exchange.<br>
	 * This method replaces the current {@link Item} object.
	 * @param item The new {@code Item} to set.
	 */
	public void setItem(Item item)
	{
		this.item = item;
	}
	
	/**
	 * Increases the total number of items.<br>
	 * This method updates both the local {@code itemCount} and the value inside the {@link Item} object.
	 * @param countToAdd The amount to add to the current count.
	 */
	public void addCount(long countToAdd)
	{
		itemCount += countToAdd;
		item.setItemCount(itemCount);
	}
	
	/**
	 * Retrieves the {@code Item} associated with this broker entry.<br>
	 * This method returns the underlying object stored in the field.
	 * @return the {@link Item} object.
	 */
	public Item getItem()
	{
		return item;
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
	 * Retrieves the total number of items for this broker entry.<br>
	 * This value represents the quantity of the {@link Item}.
	 * @return The current count of items as a {@code long}.
	 */
	public long getItemCount()
	{
		return itemCount;
	}
	
	/**
	 * Retrieves the description ID of the exchange item.<br>
	 * This value is used to identify specific item details.
	 * @return The {@code int} value representing the item description.
	 */
	public int getItemDesc()
	{
		return itemDesc;
	}
}
