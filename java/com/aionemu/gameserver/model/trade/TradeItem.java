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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * Represents an individual item within a trade transaction.<br>
 * It links a specific {@link ItemTemplate} to its quantity and properties during trading.
 * @author ATracer
 */
public class TradeItem
{
	private final int itemId;
	private long count;
	private ItemTemplate itemTemplate;
	
	/**
	 * Creates a new {@link TradeItem} instance.<br>
	 * This constructor initializes the item with a specific ID and quantity.
	 * @param itemId The unique identifier for the item.
	 * @param count The number of items to include in the trade.
	 */
	public TradeItem(int itemId, long count)
	{
		super();
		this.itemId = itemId;
		this.count = count;
	}
	
	/**
	 * Retrieves the {@link ItemTemplate} for this drop.<br>
	 * It returns the cached {@code template} if it exists.<br>
	 * If the {@code template} is {@code null}, it fetches the data from {@link DataManager}.
	 * @return The {@link ItemTemplate} associated with this drop.
	 */
	public ItemTemplate getItemTemplate()
	{
		return itemTemplate;
	}
	
	/**
	 * Sets the {@code ItemTemplate} for this trade item.<br>
	 * This method updates the internal template used to define the item properties.
	 * @param itemTemplate The {@link ItemTemplate} to assign to this object.
	 */
	public void setItemTemplate(ItemTemplate itemTemplate)
	{
		this.itemTemplate = itemTemplate;
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
	 * Retrieves the current quantity of the item.<br>
	 * This value is updated by the {@code calculateCount} method.
	 * @return The total number of items as a {@code long}.
	 */
	public long getCount()
	{
		return count;
	}
	
	/**
	 * Reduces the current quantity of this trade item.<br>
	 * The new count is calculated by subtracting {@code decreaseCount} from the current value.<br>
	 * This method only updates the count if {@code decreaseCount} is less than the current amount.
	 * @param decreaseCount The amount to subtract from the total count.
	 */
	public void decreaseCount(long decreaseCount)
	{
		// TODO probably <= count ?
		if (decreaseCount < count)
		{
			count = count - decreaseCount;
		}
	}
}
