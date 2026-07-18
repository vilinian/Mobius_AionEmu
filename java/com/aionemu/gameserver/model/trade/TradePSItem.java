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

import com.aionemu.gameserver.model.gameobjects.BrokerItem;

/**
 * This class extends {@link TradeItem} to provide specialized behavior for these types of items.
 * @author Simple
 */
public class TradePSItem extends TradeItem
{
	private int itemObjId;
	private long price;
	
	/**
	 * Creates a new {@link TradePSItem} instance.<br>
	 * This constructor initializes the item with its unique ID and price.
	 * @param itemObjId The unique object identifier for the item.
	 * @param itemId The base identification number for the item type.
	 * @param count The total quantity of the item.
	 * @param price The cost associated with this trade item.
	 */
	public TradePSItem(int itemObjId, int itemId, long count, long price)
	{
		super(itemId, count);
		setPrice(price);
		setItemObjId(itemObjId);
	}
	
	/**
	 * Updates the price of this {@link BrokerItem}.<br>
	 * This method sets the new value for the item's cost.
	 * @param price The new price to assign to the item.
	 */
	public void setPrice(long price)
	{
		this.price = price;
	}
	
	/**
	 * Retrieves the current price of the item.<br>
	 * This value represents the cost in the game currency.
	 * @return the {@code long} price of the item.
	 */
	public long getPrice()
	{
		return price;
	}
	
	/**
	 * Sets the unique object identifier for this trade item.<br>
	 * This updates the {@code itemObjId} field of the current instance.
	 * @param itemObjId The new integer ID to assign to the item.
	 */
	public void setItemObjId(int itemObjId)
	{
		this.itemObjId = itemObjId;
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
}
