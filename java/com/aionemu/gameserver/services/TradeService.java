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
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.GoodsListData;
import com.aionemu.gameserver.dataholders.TradeListData;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.limiteditems.LimitedItem;
import com.aionemu.gameserver.model.templates.goods.GoodsList;
import com.aionemu.gameserver.model.templates.item.AcquisitionType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.TradeinItem;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate.TradeTab;
import com.aionemu.gameserver.model.trade.TradeItem;
import com.aionemu.gameserver.model.trade.TradeList;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.PlayerLimitService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.OverfowException;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.SafeMath;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Manages the core trading logic between {@link Player} entities.<br>
 * This service handles trade creation, item validation, and transaction processing.
 * @author ATracer, Rama, Wakizashi, xTz
 * @Reworked GiGatR00n
 * @Reworked Sayem && Dezam
 * @Reworked pralinka
 * @modified Kill3r
 */
public class TradeService
{
	private static final Logger log = LoggerFactory.getLogger(TradeService.class);
	private static final TradeListData tradeListData = DataManager.TRADE_LIST_DATA;
	private static final GoodsListData goodsListData = DataManager.GOODSLIST_DATA;
	
	/**
	 * Processes a purchase from an {@link Npc} shop for a {@link Player}.<br>
	 * This method validates trade restrictions, checks item availability, and handles currency deduction.<br>
	 * It also manages limited item counts and updates the player inventory.
	 * @param npc The {@link Npc} providing the items.
	 * @param player The {@link Player} making the purchase.
	 * @param tradeList The {@link TradeList} containing the items to be bought.
	 * @return {@code true} if the transaction was successful, {@code false} otherwise.
	 */
	public static boolean performBuyFromShop(Npc npc, Player player, TradeList tradeList)
	{
		if (!RestrictionsManager.canTrade(player))
		{
			return false;
		}
		
		if (!validateBuyItems(npc, tradeList, player))
		{
			PacketSendUtility.sendMessage(player, "Some items are not allowed to be sold by this npc.");
			return false;
		}
		
		final Storage inventory = player.getInventory();
		
		final int tradeModifier = tradeListData.getTradeListTemplate(npc.getNpcId()).getSellPriceRate();
		
		// 1. check kinah
		if (!tradeList.calculateBuyListPrice(player, tradeModifier) || !tradeList.calculateRewardBuyListPrice(player))
		{
			return false;
		}
		
		// 2. check free slots, need to check retail behaviour
		final int freeSlots = inventory.getFreeSlots();
		if (freeSlots < tradeList.size())
		{
			return false; // TODO message
		}
		
		final long tradeListPrice = tradeList.getRequiredKinah();
		
		// check if soldOutItem
		LimitedItem item = null;
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			item = LimitedItemTradeService.getInstance().getLimitedItem(tradeItem.getItemId(), npc.getNpcId());
			if (item != null)
			{
				if ((item.getBuyLimit() == 0) && (item.getDefaultSellLimit() != 0))
				{
					// type A
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if ((item.getSellLimit() - tradeItem.getCount()) < 0)
					{
						return false;
					}
					
					item.setSellLimit(item.getSellLimit() - (int) tradeItem.getCount());
				}
				else if ((item.getBuyLimit() != 0) && (item.getDefaultSellLimit() == 0))
				{
					// type B
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if ((item.getBuyLimit() - tradeItem.getCount()) < 0)
					{
						return false;
					}
					
					if (item.getBuyCount().containsKey(player.getObjectId()))
					{
						if (item.getBuyCount().get(player.getObjectId()) < item.getBuyLimit())
						{
							item.getBuyCount().put(player.getObjectId(), item.getBuyCount().get(player.getObjectId()) + (int) tradeItem.getCount());
						}
						else
						{
							return false;
						}
					}
				}
				else if ((item.getBuyLimit() != 0) && (item.getDefaultSellLimit() != 0))
				{
					// type C
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if (((item.getBuyLimit() - tradeItem.getCount()) < 0) || ((item.getSellLimit() - tradeItem.getCount()) < 0))
					{
						return false;
					}
					
					if (item.getBuyCount().containsKey(player.getObjectId()))
					{
						if (item.getBuyCount().get(player.getObjectId()) < item.getBuyLimit())
						{
							item.getBuyCount().put(player.getObjectId(), item.getBuyCount().get(player.getObjectId()) + (int) tradeItem.getCount());
						}
						else
						{
							return false;
						}
					}
					
					item.setSellLimit(item.getSellLimit() - (int) tradeItem.getCount());
				}
			}
			
			final Map<Integer, Long> requiredItems = tradeList.getRequiredItems();
			for (Integer itemId : requiredItems.keySet())
			{
				if (!player.getInventory().decreaseByItemId(itemId, requiredItems.get(itemId)))
				{
					AuditLogger.info(player, "Possible hack. Didn't removed items on buy in rewardshop.");
					return false;
				}
			}
			
			final long count = ItemService.addItem(player, tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount());
			if (count != 0)
			{
				log.warn(String.format("CHECKPOINT: itemservice couldnt add all items on buy: %d %d %d %d", player.getObjectId(), tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount(), count));
				inventory.decreaseKinah(tradeListPrice);
				return false;
			}
		}
		
		inventory.decreaseKinah(tradeListPrice);
		
		// TODO message
		return true;
	}
	
	/**
	 * Processes a purchase from the Abyss Shop for a specific player.<br>
	 * This method validates trade restrictions and checks if the player has enough Abyss Points.<br>
	 * It also verifies inventory space before removing required items and adding new ones.<br>
	 * Handles special logic for limited items based on their specific buy and sell limits.
	 * @param npc The {@link Npc} object representing the shop vendor.
	 * @param player The {@link Player} object attempting to make the purchase.
	 * @param tradeList The {@link TradeList} containing the items to be bought.
	 * @return {@code true} if the transaction was successful, otherwise {@code false}.
	 */
	public static boolean performBuyFromAbyssShop(Npc npc, Player player, TradeList tradeList)
	{
		if (!RestrictionsManager.canTrade(player))
		{
			return false;
		}
		
		if (!validateBuyItems(npc, tradeList, player))
		{
			PacketSendUtility.sendMessage(player, "Some items are not allowed to be selled from this npc");
			return false;
		}
		
		final Storage inventory = player.getInventory();
		final int freeSlots = inventory.getFreeSlots();
		
		if (!tradeList.calculateAbyssBuyListPrice(player))
		{
			return false;
		}
		
		if (tradeList.getRequiredAp() < 0)
		{
			AuditLogger.info(player, "Posible client hack. tradeList.getRequiredAp() < 0");
			
			// You do not have enough Abyss Points.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300927));
			return false;
		}
		
		// 2. check free slots, need to check retail behaviour
		if (freeSlots < tradeList.size())
		{
			// You cannot trade as your inventory is full.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300762));
			return false;
		}
		
		AbyssPointsService.addAp(player, -tradeList.getRequiredAp());
		final Map<Integer, Long> requiredItems = tradeList.getRequiredItems();
		for (Integer itemId : requiredItems.keySet())
		{
			if (!player.getInventory().decreaseByItemId(itemId, requiredItems.get(itemId)))
			{
				AuditLogger.info(player, "Possible hack. Not removed items on buy in abyss shop.");
				return false;
			}
		}
		
		LimitedItem item = null;
		
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			item = LimitedItemTradeService.getInstance().getLimitedItem(tradeItem.getItemId(), npc.getNpcId());
			if (item != null)
			{
				if ((item.getBuyLimit() == 0) && (item.getDefaultSellLimit() != 0))
				{
					// type A
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if ((item.getSellLimit() - tradeItem.getCount()) < 0)
					{
						return false;
					}
					
					item.setSellLimit(item.getSellLimit() - (int) tradeItem.getCount());
				}
				else if ((item.getBuyLimit() != 0) && (item.getDefaultSellLimit() == 0))
				{
					// type B
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if ((item.getBuyLimit() - tradeItem.getCount()) < 0)
					{
						return false;
					}
					
					if (item.getBuyCount().containsKey(player.getObjectId()))
					{
						if (item.getBuyCount().get(player.getObjectId()) < item.getBuyLimit())
						{
							item.getBuyCount().put(player.getObjectId(), item.getBuyCount().get(player.getObjectId()) + (int) tradeItem.getCount());
						}
						else
						{
							return false;
						}
					}
				}
				else if ((item.getBuyLimit() != 0) && (item.getDefaultSellLimit() != 0))
				{
					// type C
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if (((item.getBuyLimit() - tradeItem.getCount()) < 0) || ((item.getSellLimit() - tradeItem.getCount()) < 0))
					{
						return false;
					}
					
					if (item.getBuyCount().containsKey(player.getObjectId()))
					{
						if (item.getBuyCount().get(player.getObjectId()) < item.getBuyLimit())
						{
							item.getBuyCount().put(player.getObjectId(), item.getBuyCount().get(player.getObjectId()) + (int) tradeItem.getCount());
						}
						else
						{
							return false;
						}
					}
					
					item.setSellLimit(item.getSellLimit() - (int) tradeItem.getCount());
				}
			}
			
			final long count = ItemService.addItem(player, tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount());
			if (count != 0)
			{
				log.warn(String.format("CHECKPOINT: itemservice couldnt add all items on buy: %d %d %d %d", player.getObjectId(), tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount(), count));
				return false;
			}
			
			if (tradeItem.getCount() > 1) // You have purchased %1 %0s.
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300785, new DescriptionId(tradeItem.getItemTemplate().getNameId()), tradeItem.getCount()));
			}
			else // You have purchased %0.
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300784, new DescriptionId(tradeItem.getItemTemplate().getNameId())));
			}
		}
		
		return true;
	}
	
	/**
	 * Processes a purchase from a reward shop.<br>
	 * This method checks if the {@link Player} can trade and has enough space in their inventory.<br>
	 * It validates required items, handles limited item counts, and adds new items to the player.
	 * @param npc The {@link Npc} representing the shop owner.
	 * @param player The {@link Player} attempting to make the purchase.
	 * @param tradeList The {@link TradeList} containing the items being bought.
	 * @return {@code true} if the transaction was successful, otherwise {@code false}.
	 */
	public static boolean performBuyFromRewardShop(Npc npc, Player player, TradeList tradeList)
	{
		if (!RestrictionsManager.canTrade(player))
		{
			return false;
		}
		
		if (!validateBuyItems(npc, tradeList, player))
		{
			PacketSendUtility.sendMessage(player, "Some items are not allowed to be selled from this npc");
			return false;
		}
		
		final Storage inventory = player.getInventory();
		final int freeSlots = inventory.getFreeSlots();
		
		// Check required items and free slots, then verify retail behavior.
		if (!tradeList.calculateRewardBuyListPrice(player) || (freeSlots < tradeList.size()))
		{
			return false; // TODO message
		}
		
		final Map<Integer, Long> requiredItems = tradeList.getRequiredItems();
		for (Integer itemId : requiredItems.keySet())
		{
			if (!player.getInventory().decreaseByItemId(itemId, requiredItems.get(itemId)))
			{
				AuditLogger.info(player, "Possible hack. Not removed items on buy in rewardshop.");
				return false;
			}
		}
		
		// Check if Item is sold out.
		LimitedItem item = null;
		
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			item = LimitedItemTradeService.getInstance().getLimitedItem(tradeItem.getItemId(), npc.getNpcId());
			if (item != null)
			{
				if ((item.getBuyLimit() == 0) && (item.getDefaultSellLimit() != 0))
				{
					// typA
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if ((item.getSellLimit() - tradeItem.getCount()) < 0)
					{
						return false;
					}
					
					item.setSellLimit(item.getSellLimit() - (int) tradeItem.getCount());
				}
				else if ((item.getBuyLimit() != 0) && (item.getDefaultSellLimit() == 0))
				{
					// type B
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if ((item.getBuyLimit() - tradeItem.getCount()) < 0)
					{
						return false;
					}
					
					if (item.getBuyCount().containsKey(player.getObjectId()))
					{
						if (item.getBuyCount().get(player.getObjectId()) < item.getBuyLimit())
						{
							item.getBuyCount().put(player.getObjectId(), item.getBuyCount().get(player.getObjectId()) + (int) tradeItem.getCount());
						}
						else
						{
							return false;
						}
					}
				}
				else if ((item.getBuyLimit() != 0) && (item.getDefaultSellLimit() != 0))
				{
					// type C
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if (((item.getBuyLimit() - tradeItem.getCount()) < 0) || ((item.getSellLimit() - tradeItem.getCount()) < 0))
					{
						return false;
					}
					
					if (item.getBuyCount().containsKey(player.getObjectId()))
					{
						if (item.getBuyCount().get(player.getObjectId()) < item.getBuyLimit())
						{
							item.getBuyCount().put(player.getObjectId(), item.getBuyCount().get(player.getObjectId()) + (int) tradeItem.getCount());
						}
						else
						{
							return false;
						}
					}
					
					item.setSellLimit(item.getSellLimit() - (int) tradeItem.getCount());
				}
			}
			
			final long count = ItemService.addItem(player, tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount());
			if (count != 0)
			{
				log.warn(String.format("CHECKPOINT: itemservice couldnt add all items on buy: %d %d %d %d", player.getObjectId(), tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount(), count));
				return false;
			}
		}
		
		// TODO message
		return true;
	}
	
	/**
	 * Checks if the items in a {@code TradeList} are valid for purchase from an {@link Npc}.<br>
	 * It verifies that every item exists in the NPC's allowed goods list.<br>
	 * It also ensures that each item count is at least 1.
	 * @param npc The {@link Npc} providing the items.
	 * @param tradeList The {@code TradeList} containing the items to be bought.
	 * @param player The {@link Player} attempting the purchase.
	 * @return {@code true} if all items are valid, otherwise {@code false}.
	 */
	private static boolean validateBuyItems(Npc npc, TradeList tradeList, Player player)
	{
		final TradeListTemplate tradeListTemplate = tradeListData.getTradeListTemplate(npc.getObjectTemplate().getTemplateId());
		
		final Set<Integer> allowedItems = new HashSet<>();
		for (TradeTab tradeTab : tradeListTemplate.getTradeTablist())
		{
			final GoodsList goodsList = goodsListData.getGoodsListById(tradeTab.getId());
			if ((goodsList != null) && (goodsList.getItemIdList() != null))
			{
				allowedItems.addAll(goodsList.getItemIdList());
			}
		}
		
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			if (tradeItem.getCount() < 1)
			{
				AuditLogger.info(player, "BUY packet hack item count < 1!");
				return false;
			}
			
			if (!allowedItems.contains(tradeItem.getItemId()))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Processes the sale of items from a {@link Player} to a shop.<br>
	 * This method checks if the player can trade and validates each item in the {@code tradeList}.<br>
	 * It calculates the reward, updates the inventory, and adds items to the repurchase list.
	 * @param player The {@link Player} who is selling the items.
	 * @param tradeList The {@link TradeList} containing the items to be sold.
	 * @return {@code true} if the sale was successful, or {@code false} otherwise.
	 */
	public static boolean performSellToShop(Player player, TradeList tradeList)
	{
		final Storage inventory = player.getInventory();
		long kinahReward = 0;
		final List<Item> items = new ArrayList<>();
		
		if (!RestrictionsManager.canTrade(player))
		{
			return false;
		}
		
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			final Item item = inventory.getItemByObjId(tradeItem.getItemId());
			
			// 1) don't allow to sell fake items;
			if (item == null)
			{
				return false;
			}
			
			if (!item.isSellable())
			{
				// %0 is not an item that can be sold.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300344, new DescriptionId(item.getNameId())));
				return false;
			}
			
			Item repurchaseItem = null;
			final long sellReward = PricesService.getKinahForSell(item.getItemTemplate().getPrice(), player.getRace());
			long realReward = Math.round(sellReward * tradeItem.getCount());
			if (realReward == 0)
			{
				realReward = 1;
			}
			
			if (!PlayerLimitService.updateSellLimit(player, realReward))
			{
				break;
			}
			
			if ((item.getItemCount() - tradeItem.getCount()) < 0)
			{
				AuditLogger.info(player, "Trade exploit, sell item count big");
				return false;
			}
			else if ((item.getItemCount() - tradeItem.getCount()) == 0)
			{
				inventory.delete(item); // need to be here to avoid exploit by sending packet with many
				
				// items with same unique ids
				repurchaseItem = item;
			}
			else if ((item.getItemCount() - tradeItem.getCount()) > 0)
			{
				repurchaseItem = ItemFactory.newItem(item.getItemId(), tradeItem.getCount());
				inventory.decreaseItemCount(item, tradeItem.getCount());
			}
			else
			{
				return false;
			}
			
			kinahReward += realReward;
			repurchaseItem.setRepurchasePrice(realReward);
			items.add(repurchaseItem);
		}
		
		RepurchaseService.getInstance().addRepurchaseItems(player, items);
		inventory.increaseKinah(kinahReward);
		
		return true;
	}
	
	/**
	 * Processes a purchase from an NPC that requires trading in specific items.<br>
	 * This method validates the player's inventory, distance to the NPC, and required trade-in materials.<br>
	 * It also checks for anti-cheat discrepancies between client packets and server data.<br>
	 * If all conditions are met, it removes the trade-in items and grants the purchased item to the {@code Player}.
	 * @param player The {@code Player} attempting to perform the trade.
	 * @param npcObjectId The unique object ID of the NPC.
	 * @param itemId The ID of the item being purchased.
	 * @param count The quantity of the item to purchase.
	 * @param TradeinListCount The number of different types of items required for trade-in.
	 * @param TradeinItemObjectId1 The object ID of the first trade-in item in the inventory.
	 * @param TradeinItemObjectId2 The object ID of the second trade-in item in the inventory.
	 * @param TradeinItemObjectId3 The object ID of the third trade-in item in the inventory. @
	 * @return
	 */
	public static boolean performBuyFromTradeInTrade(Player player, int npcObjectId, int itemId, int count, int TradeinListCount, int TradeinItemObjectId1, int TradeinItemObjectId2, int TradeinItemObjectId3)
	{
		if (!RestrictionsManager.canTrade(player))
		{
			return false;
		}
		
		if (player.getInventory().isFull())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
			return false;
		}
		
		final VisibleObject visibleObject = player.getKnownList().getObject(npcObjectId);
		if ((visibleObject == null) || !(visibleObject instanceof Npc) || (MathUtil.getDistance(visibleObject, player) > 10))
		{
			return false;
		}
		
		final int npcId = ((Npc) visibleObject).getNpcId();
		final TradeListTemplate tradeInList = tradeListData.getTradeInListTemplate(npcId);
		if (tradeInList == null)
		{
			return false;
		}
		
		boolean valid = false;
		for (TradeTab tab : tradeInList.getTradeTablist())
		{
			final GoodsList goodList = goodsListData.getGoodsInListById(tab.getId());
			if (goodList.getItemIdList().contains(itemId))
			{
				valid = true;
				break;
			}
		}
		
		if (!valid)
		{
			return false;
		}
		
		final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (itemTemplate.getMaxStackCount() < count)
		{
			return false;
		}
		/*** Start Implementing Anti-Cheat System ***/
		/*
		 * It has two means: 1. ItemTemplates has not updated yet 2. Client Packet Hack
		 */
		if (!(itemTemplate.getTradeinList().getTradeinItem().size() == TradeinListCount))
		{
			AuditLogger.info(player, "Possible Hack. The Tradein list count(" + TradeinListCount + ") is difference of Server ItemTemplates.");
			return false;
		}
		
		Item item1, item2, item3;
		item1 = player.getInventory().getItemByObjId(TradeinItemObjectId1);
		item2 = player.getInventory().getItemByObjId(TradeinItemObjectId2);
		item3 = player.getInventory().getItemByObjId(TradeinItemObjectId3);
		
		for (TradeinItem treadInList : itemTemplate.getTradeinList().getTradeinItem())
		{
			switch (TradeinListCount)
			{
				case 1:
					if (item1.getItemId() != treadInList.getId())
					{
						AuditLogger.info(player, "Packet Hack. The Tradein items which sent by client are not same as Server.");
						return false;
					}
					break;
				case 2:
					if ((item1.getItemId() != treadInList.getId()) && (item2.getItemId() != treadInList.getId()))
					{
						AuditLogger.info(player, "Packet Hack. The Tradein items which sent by client are not same as Server.");
						return false;
					}
					break;
				case 3:
					if ((item1.getItemId() != treadInList.getId()) && (item2.getItemId() != treadInList.getId()) && (item3.getItemId() != treadInList.getId()))
					{
						AuditLogger.info(player, "Packet Hack. The Tradein items which sent by client are not same as Server.");
						return false;
					}
					break;
			}
		}
		/*** End Implementing Anti-Cheat System ***/
		try
		{
			// here
			for (TradeinItem treadInList : itemTemplate.getTradeinList().getTradeinItem())
			{
				if (player.getInventory().getItemCountByItemId(treadInList.getId()) < SafeMath.multSafe(treadInList.getCount(), count))
				{
					return false;
				}
			}
			
			if (itemTemplate.getAcquisition() != null)
			{
				if ((itemTemplate.getAcquisition().getType() == AcquisitionType.AP) && (itemTemplate.getAcquisition().getRequiredAp() > player.getAbyssRank().getAp()))
				{
					return false;
				}
			}
			
			// and here
			for (TradeinItem treadInList : itemTemplate.getTradeinList().getTradeinItem())
			{
				if (!player.getInventory().decreaseByItemId(treadInList.getId(), SafeMath.multSafe(treadInList.getCount(), count)))
				{
					return false;
				}
			}
			
			if (itemTemplate.getAcquisition() != null)
			{
				if (itemTemplate.getAcquisition().getType() == AcquisitionType.AP)
				{
					if (itemTemplate.getAcquisition().getRequiredAp() < player.getAbyssRank().getAp())
					{
						final TradeinItem Tii = itemTemplate.getTradeinList().getFirstTradeInItem();
						final ItemTemplate tradeInTemplate = DataManager.ITEM_DATA.getItemTemplate(Tii.getId());
						
						final int reqAp = itemTemplate.getAcquisition().getRequiredAp() - tradeInTemplate.getAcquisition().getRequiredAp();
						AbyssPointsService.addAp(player, -reqAp);
					}
				}
			}
			
			if (itemTemplate.getTradeinList().getAp() != 0)
			{
				AbyssPointsService.addAp(player, -itemTemplate.getTradeinList().getAp());
			}
		}
		catch (OverfowException e)
		{
			AuditLogger.info(player, "OverfowException using tradeInTrade " + e.getMessage());
			return false;
		}
		
		ItemService.addItem(player, itemId, count);
		return true;
	}
	
	/**
	 * Processes the sale of items to a shop in exchange for Abyss Points.<br>
	 * This method checks if the {@link Player} can trade and validates the items in the {@link TradeList}.<br>
	 * It verifies that each item exists in the inventory and matches the provided {@link TradeListTemplate}.<br>
	 * If successful, it removes the items from the player and grants the calculated Abyss Points.
	 * @param player The {@link Player} performing the sale.
	 * @param tradeList The list of items to be sold.
	 * @param purchaseTemplate The template defining the valid shop items and price rates.
	 * @return {@code true} if the transaction was successful, or {@code false} otherwise.
	 */
	public static boolean performSellForAPToShop(Player player, TradeList tradeList, TradeListTemplate purchaseTemplate)
	{
		if (!RestrictionsManager.canTrade(player))
		{
			return false;
		}
		
		final Storage inventory = player.getInventory();
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			final int itemObjectId = tradeItem.getItemId();
			final long count = tradeItem.getCount();
			final Item item = inventory.getItemByObjId(itemObjectId);
			if (item == null)
			{
				return false;
			}
			
			final int itemId = item.getItemId();
			boolean valid = false;
			for (TradeTab tab : purchaseTemplate.getTradeTablist())
			{
				final GoodsList goodList = goodsListData.getGoodsPurchaseListById(tab.getId());
				if (goodList.getItemIdList().contains(itemId))
				{
					valid = true;
					break;
				}
			}
			
			if (!valid)
			{
				return false;
			}
			
			if (inventory.decreaseByObjectId(itemObjectId, count))
			{
				int price = item.getItemTemplate().getAcquisition().getRequiredAp() * (int) count;
				
				// int modifier = 0;
				if (player.getTarget() instanceof Npc)
				{
					// Here modifier = getPriceModifier((Npc) player.getTarget());
					final TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getPurchaseTemplate(((Npc) player.getTarget()).getNpcId());
					price *= ((double) tradeListTemplate.getBuyPriceRate() / 1000);
				}
				
				// if(modifier != 0){
				// price /= modifier;
				// }
				
				AbyssPointsService.addAp(player, price);
			}
		}
		
		return true;
	}
	
	/**
	 * Processes the sale of broken AP items for Abyss Points.<br>
	 * This method checks if the {@link Player} can trade and validates their inventory.<br>
	 * It calculates the total reward based on item templates and updates the player's points.
	 * @param player The {@link Player} who is performing the sale.
	 * @param tradeList The {@link TradeList} containing the items to be sold.
	 * @return {@code true} if the transaction was successful, or {@code false} otherwise.
	 */
	public static boolean performSellBrokenAPItems(Player player, TradeList tradeList)
	{
		int apReward = 0;
		if (!RestrictionsManager.canTrade(player))
		{
			return false;
		}
		
		final Storage inventory = player.getInventory();
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			final int itemObjectId = tradeItem.getItemId();
			final long count = tradeItem.getCount();
			final Item item = inventory.getItemByObjId(itemObjectId);
			if (item == null)
			{
				return false;
			}
			
			final int itemId = item.getItemId();
			if (inventory.decreaseByItemId(itemId, count))
			{
				final int templateAP = (item.getItemTemplate().getAcquisition().getRequiredAp() * (int) count) / 5;
				apReward += templateAP;
			}
		}
		
		AbyssPointsService.addAp(player, apReward);
		return true;
	}
	
	/**
	 * Calculates the price modifier for a specific {@link Npc}.<br>
	 * This method checks the title ID of the NPC template.<br>
	 * It returns different values based on the shop type.
	 * @param n The {@code Npc} object to check.
	 * @return The integer price modifier value.
	 */
	public static int getPriceModifier(Npc n)
	{
		if (n.getObjectTemplate().getTitleId() == 463222)
		{
			return 10;
		}
		
		if ((n.getObjectTemplate().getTitleId() == 463648) || (n.getObjectTemplate().getTitleId() == 463490))
		{
			return 0;
		}
		
		return 0;
	}
	
	/**
	 * Processes the sale of items from a {@link Player} to a shop for Kinah.<br>
	 * This method checks if the player can trade and validates that all items in the {@code tradeList} exist in their inventory.<br>
	 * It also verifies that each item is part of the allowed {@code purchaseTemplate}.<br>
	 * If successful, it removes the items from the inventory and adds the corresponding Kinah.
	 * @param player The {@link Player} attempting to sell the items.
	 * @param tradeList The list of items being sold.
	 * @param purchaseTemplate The template defining which items are valid for this shop.
	 * @return {@code true} if the sale was successful, or {@code false} otherwise.
	 */
	public static boolean performSellForKinahToShop(Player player, TradeList tradeList, TradeListTemplate purchaseTemplate)
	{
		if (!RestrictionsManager.canTrade(player))
		{
			return false;
		}
		
		final Storage inventory = player.getInventory();
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			final int itemObjectId = tradeItem.getItemId();
			final long count = tradeItem.getCount();
			final Item item = inventory.getItemByObjId(itemObjectId);
			if (item == null)
			{
				return false;
			}
			
			final long purchaseListPrice = PricesService.getKinahForSell(item.getItemTemplate().getPrice(), player.getRace());
			final int itemId = item.getItemId();
			boolean valid = false;
			for (TradeTab tab : purchaseTemplate.getTradeTablist())
			{
				final GoodsList goodList = goodsListData.getGoodsPurchaseListById(tab.getId());
				if (goodList.getItemIdList().contains(itemId))
				{
					valid = true;
					break;
				}
			}
			
			if (!valid)
			{
				return false;
			}
			
			if (inventory.decreaseByObjectId(itemObjectId, count))
			{
				inventory.increaseKinah(purchaseListPrice);
			}
		}
		
		return true;
	}
	
	/**
	 * Retrieves the global list of trade data.<br>
	 * This method provides access to {@link TradeListData}.
	 * @return The {@code TradeListData} object containing all trade information.
	 */
	public static TradeListData getTradeListData()
	{
		return tradeListData;
	}
	
	/**
	 * Retrieves the global list of goods data.<br>
	 * This method provides access to the {@code GoodsListData} object used by the system.
	 * @return The current {@code GoodsListData} instance.
	 */
	public static GoodsListData getGoodsListData()
	{
		return goodsListData;
	}
}
