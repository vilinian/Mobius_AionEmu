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
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.RepurchaseService;

/**
 * Represents a collection of items available for repurchase by a {@link Player}.<br>
 * This class is used by the {@link RepurchaseService} to manage trade transactions.
 * @author xTz
 */
public class RepurchaseList
{
	private final int sellerObjId;
	private final List<Item> repurchases = new ArrayList<>();
	
	/**
	 * Creates a new {@link RepurchaseList} instance.<br>
	 * This constructor initializes the list with a specific seller ID.
	 * @param sellerObjId The unique identifier for the seller object.
	 */
	public RepurchaseList(int sellerObjId)
	{
		this.sellerObjId = sellerObjId;
	}
	
	/**
	 * Adds an item to the repurchase list for a specific player.<br>
	 * This method uses {@link RepurchaseService} to find the item by its ID.<br>
	 * The item is only added if it exists in the system.
	 * @param player The {@code Player} who is performing the action.
	 * @param itemObjectId The unique ID of the item to add.
	 * @param count The amount of the item to include.
	 */
	public void addRepurchaseItem(Player player, int itemObjectId, long count)
	{
		final Item item = RepurchaseService.getInstance().getRepurchaseItem(player, itemObjectId);
		if (item != null)
		{
			repurchases.add(item);
		}
	}
	
	/**
	 * Retrieves the list of items available for repurchase.<br>
	 * This method returns all {@code Item} objects stored in this list.
	 * @return a {@code List} containing all repurchase {@link Item} objects.
	 */
	public List<Item> getRepurchaseItems()
	{
		return repurchases;
	}
	
	/**
	 * Returns the number of items in this list.<br>
	 * This method calls {@code getRepurchaseItems} to determine the count.
	 * @return The total number of repurchase items currently stored.
	 */
	public int size()
	{
		return repurchases.size();
	}
	
	/**
	 * Retrieves the unique identifier of the seller object.<br>
	 * This ID is used to identify which entity owns this {@link RepurchaseList}.
	 * @return The {@code int} value representing the seller's object ID.
	 */
	public int getSellerObjId()
	{
		return sellerObjId;
	}
}
