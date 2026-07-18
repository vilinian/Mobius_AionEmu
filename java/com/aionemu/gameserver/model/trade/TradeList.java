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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.Acquisition;
import com.aionemu.gameserver.model.templates.item.AcquisitionType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the list of items included in a trade between players.<br>
 * It provides methods to add, remove, and track {@link ItemTemplate} objects during a transaction.
 * @author ATracer modified by Wakizashi
 */
public class TradeList
{
	private int sellerObjId;
	private final List<TradeItem> tradeItems = new ArrayList<>();
	private long requiredKinah;
	private int requiredAp;
	private final Map<Integer, Long> requiredItems = new HashMap<>();
	
	/**
	 * Creates a new instance of {@code TradeList}.<br>
	 * This initializes an empty list for trade items.<br>
	 * Use this constructor to start a fresh trade session.
	 */
	public TradeList()
	{
	}
	
	/**
	 * Creates a new {@link TradeList} instance.<br>
	 * This constructor initializes the list with a specific seller ID.
	 * @param sellerObjId The unique identifier for the seller object.
	 */
	public TradeList(int sellerObjId)
	{
		this.sellerObjId = sellerObjId;
	}
	
	/**
	 * Adds a new item to the buy list.<br>
	 * This method creates a {@code TradeItem} object and stores it in the internal list.<br>
	 * It checks if the {@code itemId} exists in the data manager before adding.
	 * @param itemId The unique identifier for the item template.
	 * @param count The number of items to add to the list.
	 */
	public void addBuyItem(int itemId, long count)
	{
		final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (itemTemplate != null)
		{
			final TradeItem tradeItem = new TradeItem(itemId, count);
			tradeItem.setItemTemplate(itemTemplate);
			tradeItems.add(tradeItem);
		}
	}
	
	/**
	 * Adds a Player Shop item to the current trade list.<br>
	 * This method creates a new {@code TradeItem} and stores it in the internal list.
	 * @param itemId The unique identifier for the item.
	 * @param count The quantity of the item to add.
	 */
	public void addPSItem(int itemId, long count)
	{
		final TradeItem tradeItem = new TradeItem(itemId, count);
		tradeItems.add(tradeItem);
	}
	
	/**
	 * Adds a new item to the sell list.<br>
	 * This method creates a {@code TradeItem} and stores it in the internal list.
	 * @param itemObjId The unique identifier for the item.
	 * @param count The quantity of the item to add.
	 */
	public void addSellItem(int itemObjId, long count)
	{
		final TradeItem tradeItem = new TradeItem(itemObjId, count);
		tradeItems.add(tradeItem);
	}
	
	/**
	 * Calculates the total cost for all items in the buy list.<br>
	 * It checks if the {@link Player} has enough Kinah to complete the purchase.<br>
	 * The calculation applies a percentage modifier based on the player's race.
	 * @param player The {@code Player} object who is attempting to buy the items.
	 * @param modifier An integer used to adjust the final price calculation.
	 * @return {@code true} if the player has sufficient Kinah, otherwise {@code false}.
	 */
	public boolean calculateBuyListPrice(Player player, int modifier)
	{
		final long availableKinah = player.getInventory().getKinah();
		requiredKinah = 0;
		
		for (TradeItem tradeItem : tradeItems)
		{
			requiredKinah += (PricesService.getKinahForBuy(tradeItem.getItemTemplate().getPrice(), player.getRace()) * tradeItem.getCount() * modifier) / 100;
		}
		
		return availableKinah >= requiredKinah;
	}
	
	/**
	 * Checks if the {@link Player} meets the requirements for Abyss buy list items.<br>
	 * This method validates both the required Abyss Points and necessary items.<br>
	 * It returns {@code false} if any requirements are missing or insufficient.
	 * @param player The {@link Player} whose inventory and rank will be checked.
	 * @return {@code true} if all requirements are met, otherwise {@code false}.
	 */
	public boolean calculateAbyssBuyListPrice(Player player)
	{
		final int ap = player.getAbyssRank().getAp();
		
		requiredAp = 0;
		requiredItems.clear();
		
		for (TradeItem tradeItem : tradeItems)
		{
			final Acquisition aquisition = tradeItem.getItemTemplate().getAcquisition();
			if ((aquisition == null) || ((aquisition.getType() != AcquisitionType.ABYSS) && (aquisition.getType() != AcquisitionType.AP)))
			{
				continue;
			}
			
			requiredAp += aquisition.getRequiredAp() * tradeItem.getCount();
			
			final int abysItemId = aquisition.getItemId();
			if (abysItemId == 0) // no abyss required item (medals, etc))
			{
				continue;
			}
			
			long alreadyAddedCount = 0;
			if (requiredItems.containsKey(abysItemId))
			{
				alreadyAddedCount = requiredItems.get(abysItemId);
			}
			
			if (alreadyAddedCount == 0)
			{
				requiredItems.put(abysItemId, (long) aquisition.getItemCount());
			}
			else
			{
				requiredItems.put(abysItemId, alreadyAddedCount + (aquisition.getItemCount() * tradeItem.getCount()));
			}
		}
		
		if (ap < requiredAp)
		{
			// You do not have enough Abyss Points.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300927));
			return false;
		}
		
		for (Integer itemId : requiredItems.keySet())
		{
			final long count = player.getInventory().getItemCountByItemId(itemId);
			if ((requiredItems.get(itemId) < 1) || (count < requiredItems.get(itemId)))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the {@link Player} has enough items to complete a reward list.<br>
	 * This method clears existing requirements and calculates totals for rewards and coupons.<br>
	 * It returns {@code false} if any required item count is insufficient.
	 * @param player The {@link Player} whose inventory will be checked.
	 * @return {@code true} if the player meets all requirements, otherwise {@code false}.
	 */
	public boolean calculateRewardBuyListPrice(Player player)
	{
		requiredItems.clear();
		
		for (TradeItem tradeItem : tradeItems)
		{
			final Acquisition aquisition = tradeItem.getItemTemplate().getAcquisition();
			if ((aquisition == null) || ((aquisition.getType() != AcquisitionType.REWARD) && (aquisition.getType() != AcquisitionType.COUPON)))
			{
				continue;
			}
			
			final int itemId = aquisition.getItemId();
			long alreadyAddedCount = 0;
			if (requiredItems.containsKey(itemId))
			{
				alreadyAddedCount = requiredItems.get(itemId);
			}
			
			if (alreadyAddedCount == 0)
			{
				requiredItems.put(itemId, aquisition.getItemCount() * tradeItem.getCount());
			}
			else
			{
				requiredItems.put(itemId, alreadyAddedCount + (aquisition.getItemCount() * tradeItem.getCount()));
			}
		}
		
		for (Integer itemId : requiredItems.keySet())
		{
			final long count = player.getInventory().getItemCountByItemId(itemId);
			if ((requiredItems.get(itemId) < 1) || (count < requiredItems.get(itemId)))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Retrieves the list of items available in this trade.<br>
	 * This method returns all {@link TradeItem} objects currently stored in the list.
	 * @return a {@code List} containing all {@code TradeItem} objects.
	 */
	public List<TradeItem> getTradeItems()
	{
		return tradeItems;
	}
	
	/**
	 * Returns the total number of items in the trade list.<br>
	 * This method delegates to {@code getTradeItems} to count the elements.
	 * @return The number of {@code TradeItem} objects currently in the list.
	 */
	public int size()
	{
		return tradeItems.size();
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
	
	/**
	 * Retrieves the amount of {@code ap} required for this acquisition.<br>
	 * This value is stored as an {@code int}.
	 * @return the required {@code ap} value.
	 */
	public int getRequiredAp()
	{
		return requiredAp;
	}
	
	/**
	 * Retrieves the total amount of {@code Kinah} needed for this trade.<br>
	 * This value is used to determine if a player can afford the items.
	 * @return The total {@code Kinah} required as a {@code long}.
	 */
	public long getRequiredKinah()
	{
		return requiredKinah;
	}
	
	/**
	 * Retrieves the list of items needed for this trade.<br>
	 * The map uses {@code Integer} IDs as keys and {@code Long} counts as values.
	 * @return a {@link Map} containing the required item IDs and their quantities.
	 */
	public Map<Integer, Long> getRequiredItems()
	{
		return requiredItems;
	}
}
