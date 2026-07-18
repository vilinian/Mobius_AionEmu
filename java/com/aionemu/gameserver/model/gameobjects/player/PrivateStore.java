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

import java.util.LinkedHashMap;

import com.aionemu.gameserver.model.trade.TradePSItem;

/**
 * Represents a player's private store for managing personal items.<br>
 * It handles the storage and retrieval of {@link TradePSItem} objects.<br>
 * This class allows players to organize their inventory into specific categories.
 * @author Xav Modified by Simple
 */
public class PrivateStore
{
	private final Player owner;
	private LinkedHashMap<Integer, TradePSItem> items;
	private String storeMessage;
	
	/**
	 * Creates a new {@code PrivateStore} instance.<br>
	 * This method assigns the provided {@link Player} as the store owner.<br>
	 * It also initializes an empty list of items for sale.
	 * @param owner The {@code Player} who will own this store.
	 */
	public PrivateStore(Player owner)
	{
		this.owner = owner;
		items = new LinkedHashMap<>();
	}
	
	/**
	 * Retrieves the {@link Player} that owns this object.<br>
	 * This method casts the result of the parent class's owner retrieval to a {@code Player}.
	 * @return The {@code Player} associated with this object.
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * Retrieves all items currently listed for sale in the private store.<br>
	 * The items are returned in a {@code LinkedHashMap} to preserve their order.
	 * @return A {@code LinkedHashMap} containing the sold items.
	 */
	public LinkedHashMap<Integer, TradePSItem> getSoldItems()
	{
		return items;
	}
	
	/**
	 * Adds a new item to the private store.<br>
	 * This method maps the {@code TradePSItem} to its unique ID.<br>
	 * It updates the internal collection of items for sale.
	 * @param itemObjId The unique identifier for the item.
	 * @param tradeItem The {@link TradePSItem} object to be added.
	 */
	public void addItemToSell(int itemObjId, TradePSItem tradeItem)
	{
		items.put(itemObjId, tradeItem);
	}
	
	/**
	 * Removes an item from the private store.<br>
	 * This method looks for a specific {@code itemObjId}.<br>
	 * It updates the internal list of items if the ID exists.
	 * @param itemObjId The unique identifier of the item to remove.
	 */
	public void removeItem(int itemObjId)
	{
		if (items.containsKey(itemObjId))
		{
			final LinkedHashMap<Integer, TradePSItem> newItems = new LinkedHashMap<>();
			for (int itemObjIds : items.keySet())
			{
				if (itemObjId != itemObjIds)
				{
					newItems.put(itemObjIds, items.get(itemObjIds));
				}
			}
			
			items = newItems;
		}
	}
	
	/**
	 * Retrieves a {@link TradePSItem} from the private store.<br>
	 * It uses the unique object ID to find the specific item.
	 * @param itemObjId The unique identifier of the item to find.
	 * @return The {@code TradePSItem} associated with the ID, or {@code null} if not found.
	 */
	public TradePSItem getTradeItemByObjId(int itemObjId)
	{
		return items.get(itemObjId);
	}
	
	/**
	 * Updates the message displayed for this private store.<br>
	 * This sets the {@code storeMessage} field to a new value.
	 * @param storeMessage The new text to display in the store.
	 */
	public void setStoreMessage(String storeMessage)
	{
		this.storeMessage = storeMessage;
	}
	
	/**
	 * Retrieves the current message displayed by the private store.<br>
	 * This is the text shown to other players when they view this store.
	 * @return The {@code String} containing the store message or {@code null}.
	 */
	public String getStoreMessage()
	{
		return storeMessage;
	}
}
