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
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.trade.RepurchaseList;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Handles the logic for players to sell items back to NPCs.<br>
 * It manages {@link RepurchaseList} data and interacts with {@link ItemService} to process transactions.
 * @author xTz
 */
public class RepurchaseService
{
	private final Map<Integer, List<Item>> repurchaseItems = new HashMap<>();
	
	/**
	 * Adds a list of items to the repurchase queue for a specific player.<br>
	 * This method updates the {@code repurchaseItems} collection.
	 * @param player The {@link Player} who owns the items.
	 * @param items The {@code List} of {@link Item} objects to be added.
	 */
	public void addRepurchaseItems(Player player, List<Item> items)
	{
		repurchaseItems.computeIfAbsent(player.getObjectId(), k -> new ArrayList<>()).addAll(items);
	}
	
	/**
	 * Removes all items from the repurchase list for a specific player.<br>
	 * This method clears any data associated with the {@code Player} object ID.
	 * @param player The {@link Player} whose repurchase items should be removed.
	 */
	public void removeRepurchaseItems(Player player)
	{
		repurchaseItems.remove(player.getObjectId());
	}
	
	/**
	 * Removes a specific {@code Item} from the repurchase list of a {@link Player}.<br>
	 * This method updates the internal storage used by {@link RepurchaseService}.
	 * @param player The {@link Player} who owns the items.
	 * @param item The {@code Item} to be removed from the list.
	 */
	public void removeRepurchaseItem(Player player, Item item)
	{
		final List<Item> items = repurchaseItems.get(player.getObjectId());
		if (items != null)
		{
			items.remove(item);
		}
	}
	
	/**
	 * Retrieves the list of items saved for repurchase by a specific player.<br>
	 * It uses the {@code playerObjectId} to look up the data in the internal map.<br>
	 * If no items are found, it returns an empty {@code Collection}.
	 * @param playerObjectId The unique ID of the player.
	 * @return A {@code Collection} of {@link Item} objects.
	 */
	public Collection<Item> getRepurchaseItems(int playerObjectId)
	{
		final List<Item> items = repurchaseItems.get(playerObjectId);
		return items != null ? items : Collections.<Item> emptyList();
	}
	
	/**
	 * Retrieves a specific item from the repurchase list of a player.<br>
	 * It searches for an {@code Item} that matches the provided {@code itemObjectId}.
	 * @param player The {@link Player} who owns the repurchase items.
	 * @param itemObjectId The unique ID of the item to find.
	 * @return The matching {@code Item} object, or {@code null} if it is not found.
	 */
	public Item getRepurchaseItem(Player player, int itemObjectId)
	{
		final Collection<Item> items = getRepurchaseItems(player.getObjectId());
		for (Item item : items)
		{
			if (item.getObjectId() == itemObjectId)
			{
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Processes the purchase of items from a shop for a specific player.<br>
	 * It checks if the {@link Player} can trade and has enough kinah.<br>
	 * Items are added to the inventory and removed from the repurchase list.<br>
	 * Logs errors if the player lacks funds or attempts to abuse the system.
	 * @param player The {@link Player} who is making the purchase.
	 * @param repurchaseList The {@link RepurchaseList} containing items to be bought.
	 */
	public void repurchaseFromShop(Player player, RepurchaseList repurchaseList)
	{
		if (!RestrictionsManager.canTrade(player))
		{
			return;
		}
		
		final Storage inventory = player.getInventory();
		for (Item repurchaseItem : repurchaseList.getRepurchaseItems())
		{
			final List<Item> items = repurchaseItems.getOrDefault(player.getObjectId(), Collections.emptyList());
			if (items.contains(repurchaseItem))
			{
				if (inventory.tryDecreaseKinah(repurchaseItem.getRepurchasePrice()))
				{
					ItemService.addItem(player, repurchaseItem);
					removeRepurchaseItem(player, repurchaseItem);
				}
				else
				{
					AuditLogger.info(player, "Player try repurchase item: " + repurchaseItem.getItemId() + " count: " + repurchaseItem.getItemCount() + " whithout kinah");
				}
			}
			else
			{
				AuditLogger.info(player, "Player might be abusing CM_BUY_ITEM try dupe item: " + repurchaseItem.getItemId() + " count: " + repurchaseItem.getItemCount());
			}
		}
	}
	
	/**
	 * Provides the global instance of the {@link RepurchaseService}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the service from anywhere in the application.
	 * @return The single instance of {@code RepurchaseService}.
	 */
	public static RepurchaseService getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RepurchaseService INSTANCE = new RepurchaseService();
	}
}
