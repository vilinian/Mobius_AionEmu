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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a trade exchange between two {@link Player} entities.<br>
 * It manages the items and data involved in a specific trading session.
 * @author ATracer
 */
public class Exchange
{
	private final Player activeplayer;
	private final Player targetPlayer;
	private boolean confirmed;
	private boolean locked;
	private long kinahCount;
	private final Map<Integer, ExchangeItem> items = new HashMap<>();
	private final List<Item> itemsToUpdate = new ArrayList<>();
	
	/**
	 * Creates a new {@link Exchange} instance.<br>
	 * This method initializes the trade between two players.
	 * @param activeplayer The player who starts the trade.
	 * @param targetPlayer The player who receives the trade request.
	 */
	public Exchange(Player activeplayer, Player targetPlayer)
	{
		super();
		this.activeplayer = activeplayer;
		this.targetPlayer = targetPlayer;
	}
	
	/**
	 * Finalizes the current exchange process.<br>
	 * Sets the {@code confirmed} flag to {@code true}.<br>
	 * This method should be called when both players agree to the trade.
	 */
	public void confirm()
	{
		confirmed = true;
	}
	
	/**
	 * Checks if the exchange has been confirmed.<br>
	 * This method returns {@code true} if the user has finalized the trade.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if confirmed, {@code false} otherwise.
	 */
	public boolean isConfirmed()
	{
		return confirmed;
	}
	
	/**
	 * Sets the exchange status to locked.<br>
	 * This prevents further modifications to the current trade.<br>
	 * The {@code locked} field is set to {@code true}.
	 */
	public void lock()
	{
		locked = true;
	}
	
	/**
	 * Checks if the minion is currently in a locked state.<br>
	 * This status determines if certain actions can be performed on the object.
	 * @return {@code true} if the minion is locked, {@code false} otherwise.
	 */
	public boolean isLocked()
	{
		return locked;
	}
	
	/**
	 * Adds a new {@code ExchangeItem} to the current exchange list.<br>
	 * This method uses the {@code parentItemObjId} as the unique key.
	 * @param parentItemObjId The unique identifier for the item.
	 * @param exchangeItem The {@code ExchangeItem} object to be added.
	 */
	public void addItem(int parentItemObjId, ExchangeItem exchangeItem)
	{
		items.put(parentItemObjId, exchangeItem);
	}
	
	/**
	 * Adds a specific amount of currency to the exchange.<br>
	 * This updates the internal {@code kinahCount} value.
	 * @param countToAdd The number of Kinah to add.
	 */
	public void addKinah(long countToAdd)
	{
		kinahCount += countToAdd;
	}
	
	/**
	 * Retrieves the player who initiated the exchange.<br>
	 * This method returns the {@code activeplayer} object.
	 * @return the {@link Player} currently performing the action.
	 */
	public Player getActiveplayer()
	{
		return activeplayer;
	}
	
	/**
	 * Retrieves the player who is currently being targeted in this exchange.<br>
	 * This method returns the {@code Player} object associated with the trade.
	 * @return The {@link Player} that is the target of the exchange.
	 */
	public Player getTargetPlayer()
	{
		return targetPlayer;
	}
	
	/**
	 * Retrieves the total amount of Kinah in the current exchange.<br>
	 * This value represents the currency sum added via {@code addKinah}.
	 * @return The current count of Kinah as a {@code long}.
	 */
	public long getKinahCount()
	{
		return kinahCount;
	}
	
	/**
	 * Retrieves the collection of items in the current exchange.<br>
	 * The map uses {@code Integer} IDs as keys to identify each {@link ExchangeItem}.
	 * @return a {@code Map} containing all {@link ExchangeItem} objects.
	 */
	public Map<Integer, ExchangeItem> getItems()
	{
		return items;
	}
	
	/**
	 * Checks if the current exchange list has reached its maximum capacity.<br>
	 * The limit is set to {@code 18} items.
	 * @return {@code true} if there are more than {@code 18} items, otherwise {@code false}.
	 */
	public boolean isExchangeListFull()
	{
		return items.size() > 18;
	}
	
	/**
	 * Retrieves the list of {@link Item} objects that need to be updated.<br>
	 * This list contains all items added via {@code addItemToUpdate}.
	 * @return a {@code List} containing the items to be updated.
	 */
	public List<Item> getItemsToUpdate()
	{
		return itemsToUpdate;
	}
	
	/**
	 * Adds an {@code Item} to the list of items that need updating.<br>
	 * This method tracks changes made during the exchange process.
	 * @param item The {@code Item} object to be added to the update list.
	 */
	public void addItemToUpdate(Item item)
	{
		itemsToUpdate.add(item);
	}
}
