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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.GoodsListData;
import com.aionemu.gameserver.dataholders.TradeListData;
import com.aionemu.gameserver.model.limiteditems.LimitedItem;
import com.aionemu.gameserver.model.limiteditems.LimitedTradeNpc;
import com.aionemu.gameserver.model.templates.goods.GoodsList;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate.TradeTab;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the trading logic for limited items within the game.<br>
 * It handles specific trade types based on {@code BuyLimit} and {@code SellLimit} values.<br>
 * This service ensures that players can only interact with {@code LimitedItemTradeNpc} according to defined constraints.
 * @author xTz
 *         <p/>
 *         TYPE_A: BuyLimit == 0 && SellLimit != 0 TYPE_B: BuyLimit != 0 && SellLimit == 0 TYPE_C: BuyLimit != 0 && SellLimit != 0
 */
public class LimitedItemTradeService
{
	private static final Logger log = LoggerFactory.getLogger(LimitedItemTradeService.class);
	private final GoodsListData goodsListData = DataManager.GOODSLIST_DATA;
	private final TradeListData tradeListData = DataManager.TRADE_LIST_DATA;
	private final Map<Integer, LimitedTradeNpc> limitedTradeNpcs = new ConcurrentHashMap<>();
	
	/**
	 * Initializes the limited item trade service.<br>
	 * This method populates the {@code limitedTradeNpcs} map from data files.<br>
	 * It also schedules tasks to reset items using {@link CronService}.
	 */
	public void start()
	{
		for (int npcId : tradeListData.getTradeListTemplate().keys())
		{
			for (TradeTab list : tradeListData.getTradeListTemplate(npcId).getTradeTablist())
			{
				final GoodsList goodsList = goodsListData.getGoodsListById(list.getId());
				if (goodsList == null)
				{
					log.warn("[LimitedItemTradService] No goodslist for tradelist of npc " + npcId);
					continue;
				}
				
				final List<LimitedItem> limitedItems = goodsList.getLimitedItems();
				if (limitedItems.isEmpty())
				{
					continue;
				}
				
				if (!limitedTradeNpcs.containsKey(npcId))
				{
					limitedTradeNpcs.putIfAbsent(npcId, new LimitedTradeNpc(limitedItems));
				}
				else
				{
					limitedTradeNpcs.get(npcId).putLimitedItems(limitedItems);
				}
			}
		}
		
		for (LimitedTradeNpc limitedTradeNpc : limitedTradeNpcs.values())
		{
			for (LimitedItem limitedItem : limitedTradeNpc.getLimitedItems())
			{
				CronService.getInstance().schedule(new Runnable()
				{
					
					@Override
					public void run()
					{
						limitedItem.setToDefault();
					}
				}, limitedItem.getSalesTime());
			}
		}
		
		log.info("[LimitedItemTradService] Scheduled Limited Items based on cron expression size: " + limitedTradeNpcs.size());
	}
	
	/**
	 * Retrieves a specific {@link LimitedItem} from a trade NPC.<br>
	 * This method searches for an item by its ID within the specified NPC's list.
	 * @param itemId The unique identifier of the item to find.
	 * @param npcId The unique identifier of the NPC providing the items.
	 * @return The {@code LimitedItem} if found, or {@code null} if it does not exist.
	 */
	public LimitedItem getLimitedItem(int itemId, int npcId)
	{
		if (limitedTradeNpcs.containsKey(npcId))
		{
			for (LimitedItem limitedItem : limitedTradeNpcs.get(npcId).getLimitedItems())
			{
				if (limitedItem.getItemId() == itemId)
				{
					return limitedItem;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if a specific NPC is allowed to trade limited items.<br>
	 * This method looks up the {@code npcId} in the internal registry.
	 * @param npcId The unique identifier of the NPC to check.
	 * @return {@code true} if the NPC is a limited trade NPC, otherwise {@code false}.
	 */
	public boolean isLimitedTradeNpc(int npcId)
	{
		return limitedTradeNpcs.containsKey(npcId);
	}
	
	/**
	 * Retrieves a {@link LimitedTradeNpc} object based on the provided ID.<br>
	 * This method looks up the NPC in the internal cache.<br>
	 * It returns {@code null} if no matching NPC is found.
	 * @param npcId The unique identifier of the NPC to retrieve.
	 * @return The {@link LimitedTradeNpc} associated with the given ID, or {@code null}.
	 */
	public LimitedTradeNpc getLimitedTradeNpc(int npcId)
	{
		return limitedTradeNpcs.get(npcId);
	}
	
	/**
	 * Provides the global instance of the {@link LimitedItemTradeService}.<br>
	 * Use this method to access the service from anywhere in the code.<br>
	 * This follows the singleton design pattern.
	 * @return The single instance of {@code LimitedItemTradeService}.
	 */
	public static LimitedItemTradeService getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final LimitedItemTradeService INSTANCE = new LimitedItemTradeService();
	}
}
