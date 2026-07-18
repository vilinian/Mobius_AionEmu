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
package com.aionemu.gameserver.model.limiteditems;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Represents an item that is restricted by specific limitations within the game.<br>
 * This class stores data for items that are not available to all players or under all conditions.
 * @author xTz
 */
public class LimitedItem
{
	private int itemId;
	private int sellLimit;
	private int buyLimit;
	private int defaultSellLimit;
	private String salesTime;
	private final TIntObjectHashMap<Integer> buyCounts = new TIntObjectHashMap<>();
	
	/**
	 * Creates a new instance of {@link LimitedItem}.<br>
	 * This constructor initializes the object with default values.
	 */
	public LimitedItem()
	{
	}
	
	/**
	 * Creates a new {@link LimitedItem} instance.<br>
	 * This constructor initializes the item with specific limits and timing.
	 * @param itemId The unique identifier for the item.
	 * @param sellLimit The maximum number of items available to sell.
	 * @param buyLimit The maximum number of items a player can purchase.
	 * @param salesTime The scheduled time when the sale occurs.
	 */
	public LimitedItem(int itemId, int sellLimit, int buyLimit, String salesTime)
	{
		this.itemId = itemId;
		this.sellLimit = sellLimit;
		this.buyLimit = buyLimit;
		defaultSellLimit = sellLimit;
		this.salesTime = salesTime;
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
	 * Updates the purchase count for a specific player.<br>
	 * This method stores the {@code count} associated with the {@code playerObjectId}.<br>
	 * It uses the {@code getBuyCount} map to keep track of data.
	 * @param playerObjectId The unique ID of the player.
	 * @param count The number of items purchased by the player.
	 */
	public void setBuyCount(int playerObjectId, int count)
	{
		buyCounts.putIfAbsent(playerObjectId, count);
	}
	
	/**
	 * Retrieves the map of purchase counts.<br>
	 * This map tracks how many times each player has bought this item.
	 * @return a {@code TIntObjectHashMap<Integer>} containing the purchase data.
	 */
	public TIntObjectHashMap<Integer> getBuyCount()
	{
		return buyCounts;
	}
	
	/**
	 * Sets the unique identifier for this item.<br>
	 * This updates the {@code itemId} field of the {@link LimitedItem} object.
	 * @param itemId The new ID to assign to the item.
	 */
	public void setItem(int itemId)
	{
		this.itemId = itemId;
	}
	
	/**
	 * Retrieves the maximum number of items that can be sold.<br>
	 * This value is stored in the {@code sellLimit} field.
	 * @return The current {@code int} limit for selling this item.
	 */
	public int getSellLimit()
	{
		return sellLimit;
	}
	
	/**
	 * Retrieves the maximum number of times this item can be purchased.<br>
	 * This value is used to restrict sales for {@link LimitedItem}.
	 * @return The current purchase limit as an {@code int}.
	 */
	public int getBuyLimit()
	{
		return buyLimit;
	}
	
	/**
	 * Resets the item limits to their original values.<br>
	 * This method sets {@code sellLimit} to the value of {@code defaultSellLimit}.<br>
	 * It also clears all entries in the {@code buyCounts} map.
	 */
	public void setToDefault()
	{
		sellLimit = defaultSellLimit;
		buyCounts.clear();
	}
	
	/**
	 * Sets the maximum number of items that can be sold.<br>
	 * This updates the {@code sellLimit} field for this {@link LimitedItem}.
	 * @param sellLimit The new limit for selling items.
	 */
	public void setSellLimit(int sellLimit)
	{
		this.sellLimit = sellLimit;
	}
	
	/**
	 * Retrieves the standard limit for selling a {@link LimitedItem}.<br>
	 * This value is used when no specific limit is set.
	 * @return The default sell limit as an {@code int}.
	 */
	public int getDefaultSellLimit()
	{
		return defaultSellLimit;
	}
	
	/**
	 * Retrieves the scheduled time for this limited item sale.<br>
	 * This value is stored as a {@code String}.
	 * @return The sale time string.
	 */
	public String getSalesTime()
	{
		return salesTime;
	}
}
