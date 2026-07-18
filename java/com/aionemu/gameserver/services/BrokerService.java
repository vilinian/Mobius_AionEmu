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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.dao.BrokerDAO;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.broker.BrokerItemMask;
import com.aionemu.gameserver.model.broker.BrokerMessages;
import com.aionemu.gameserver.model.broker.BrokerPlayerCache;
import com.aionemu.gameserver.model.broker.BrokerRace;
import com.aionemu.gameserver.model.gameobjects.BrokerItem;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BROKER_SERVICE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.services.mail.SystemMailService;
import com.aionemu.gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Manages the broker system for handling item trading and listing.<br>
 * This service handles requests from {@link Player} objects to interact with brokers.<br>
 * It coordinates data between {@link BrokerDAO}, {@link InventoryDAO}, and various game world objects.
 * @author kosyachok
 * @author ATracer
 * @author Antraxx
 */
public class BrokerService
{
	private final Map<Integer, BrokerItem> elyosBrokerItems = new ConcurrentHashMap<>();
	private final Map<Integer, BrokerItem> elyosSettledItems = new ConcurrentHashMap<>();
	private final Map<Integer, BrokerItem> asmodianBrokerItems = new ConcurrentHashMap<>();
	private final Map<Integer, BrokerItem> asmodianSettledItems = new ConcurrentHashMap<>();
	private static final Logger log = LoggerFactory.getLogger("EXCHANGE_LOG");
	private final BrokerPeriodicTaskManager saveManager;
	private final Map<Integer, BrokerPlayerCache> playerBrokerCache = new ConcurrentHashMap<>();
	
	/**
	 * Retrieves the singleton instance of the {@link BrokerService}.<br>
	 * This method provides a global access point to the broker service.
	 * @return The active {@code BrokerService} instance.
	 */
	public static BrokerService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Initializes a new instance of the {@link BrokerService}.<br>
	 * This constructor calls {@code initBrokerService} to set up core components.<br>
	 * It also starts periodic tasks for saving and checking expired items.
	 */
	public BrokerService()
	{
		initBrokerService();
		final int DELAY_BROKER_SAVE = 6000;
		saveManager = new BrokerPeriodicTaskManager(DELAY_BROKER_SAVE);
		final int DELAY_BROKER_CHECK = 60000;
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> checkExpiredItems(), DELAY_BROKER_CHECK, DELAY_BROKER_CHECK);
	}
	
	/**
	 * Initializes the broker service by loading data from the database.<br>
	 * This method populates the item caches for both {@code ASMODIAN} and {@code ELYOS} races.<br>
	 * It distinguishes between active broker items and settled items during the loading process.
	 */
	private void initBrokerService()
	{
		log.debug("Loading broker...");
		int loadedBrokerItemsCount = 0;
		int loadedSettledItemsCount = 0;
		
		final List<BrokerItem> brokerItems = DAOManager.getDAO(BrokerDAO.class).loadBroker();
		
		for (BrokerItem item : brokerItems)
		{
			if (item.getItemBrokerRace() == BrokerRace.ASMODIAN)
			{
				if (item.isSettled())
				{
					asmodianSettledItems.put(item.getItemUniqueId(), item);
					loadedSettledItemsCount++;
				}
				else
				{
					asmodianBrokerItems.put(item.getItemUniqueId(), item);
					loadedBrokerItemsCount++;
				}
			}
			else if (item.getItemBrokerRace() == BrokerRace.ELYOS)
			{
				if (item.isSettled())
				{
					elyosSettledItems.put(item.getItemUniqueId(), item);
					loadedSettledItemsCount++;
				}
				else
				{
					elyosBrokerItems.put(item.getItemUniqueId(), item);
					loadedBrokerItemsCount++;
				}
			}
		}
		
		log.info("[BrokerService] Broker loaded with " + loadedBrokerItemsCount + " broker items and " + loadedSettledItemsCount + " settled items.");
	}
	
	/**
	 * Displays the requested broker items to a specific player.<br>
	 * This method filters and sorts items based on the provided mask and page.<br>
	 * It updates the player cache with the current search results.<br>
	 * Finally, it sends the {@code SM_BROKER_SERVICE} packet to the client.
	 * @param player The {@link Player} who is viewing the broker items.
	 * @param clientMask The bitmask used to filter which items are visible.
	 * @param sortType The integer value defining how the items should be sorted.
	 * @param startPage The page number to display from the results list.
	 * @param itemList A {@link List} of item IDs to include, or {@code null} for all items.
	 */
	public void showRequestedItems(Player player, int clientMask, int sortType, int startPage, List<Integer> itemList)
	{
		BrokerItem[] searchItems = null;
		final int playerBrokerMaskCache = getPlayerMask(player);
		final BrokerItemMask brokerMaskById = BrokerItemMask.getBrokerMaskById(clientMask);
		final boolean isChidrenMask = brokerMaskById.isChildrenMask(playerBrokerMaskCache);
		if ((itemList != null) && (clientMask == 0))
		{
			final Map<Integer, BrokerItem> brokerItems = getRaceBrokerItems(player.getRace());
			if (brokerItems == null)
			{
				return;
			}
			
			searchItems = brokerItems.values().toArray(new BrokerItem[brokerItems.values().size()]);
		}
		else if (((getFilteredItems(player).length == 0) || !isChidrenMask) && (clientMask != 0))
		{
			searchItems = getItemsByMask(player, clientMask, false);
		}
		else if (isChidrenMask)
		{
			searchItems = getItemsByMask(player, clientMask, true);
		}
		else
		{
			searchItems = getFilteredItems(player);
		}
		
		if ((searchItems == null) || (searchItems.length < 0))
		{
			return;
		}
		
		int totalSearchItemsCount = searchItems.length;
		
		getPlayerCache(player).setBrokerSortTypeCache(sortType);
		getPlayerCache(player).setBrokerStartPageCache(startPage);
		
		if (itemList != null)
		{
			final List<BrokerItem> itemsFound = new ArrayList<>();
			for (BrokerItem item : searchItems)
			{
				if (itemList.contains(item.getItemId()))
				{
					itemsFound.add(item);
				}
			}
			
			getPlayerCache(player).setSearchItemsList(itemList);
			searchItems = itemsFound.toArray(new BrokerItem[itemsFound.size()]);
			totalSearchItemsCount = searchItems.length;
			getPlayerCache(player).setBrokerListCache(searchItems);
		}
		else
		{
			getPlayerCache(player).setSearchItemsList(null);
		}
		
		sortBrokerItems(searchItems, sortType);
		searchItems = getRequestedPage(searchItems, startPage);
		
		PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(searchItems, totalSearchItemsCount, startPage));
	}
	
	/**
	 * Retrieves a list of {@code BrokerItem} objects filtered by a specific mask.<br>
	 * This method handles both cached and non-cached data sources based on the provided flag.<br>
	 * It updates the player's cache with the resulting items and the current mask.
	 * @param player The {@link Player} requesting the items.
	 * @param clientMask The bitmask used to filter which items are visible to the client.
	 * @param cached A boolean indicating whether to use the player's local cache.
	 * @return An array of {@code BrokerItem} objects that match the mask, or {@code null} if no data is found.
	 */
	private BrokerItem[] getItemsByMask(Player player, int clientMask, boolean cached)
	{
		final List<BrokerItem> searchItems = new ArrayList<>();
		final BrokerItemMask brokerMask = BrokerItemMask.getBrokerMaskById(clientMask);
		if (cached)
		{
			final BrokerItem[] brokerItems = getFilteredItems(player);
			if (brokerItems == null)
			{
				return null;
			}
			
			for (BrokerItem item : brokerItems)
			{
				if ((item == null) || (item.getItem() == null))
				{
					continue;
				}
				
				if (brokerMask.isMatches(item.getItem()))
				{
					searchItems.add(item);
				}
			}
		}
		else
		{
			final Map<Integer, BrokerItem> brokerItems = getRaceBrokerItems(player.getRace());
			if (brokerItems == null)
			{
				return null;
			}
			
			for (BrokerItem item : brokerItems.values())
			{
				if ((item == null) || (item.getItem() == null))
				{
					continue;
				}
				
				if (brokerMask.isMatches(item.getItem()))
				{
					searchItems.add(item);
				}
			}
		}
		
		final BrokerItem[] items = searchItems.toArray(new BrokerItem[searchItems.size()]);
		getPlayerCache(player).setBrokerListCache(items);
		getPlayerCache(player).setBrokerMaskCache(clientMask);
		
		return items;
	}
	
	/**
	 * Sorts an array of {@code BrokerItem} objects based on a specific type.<br>
	 * This method uses the provided {@code sortType} to determine the sorting logic.
	 * @param brokerItems The array of items to be sorted.
	 * @param sortType The integer value representing the sorting criteria.
	 */
	private void sortBrokerItems(BrokerItem[] brokerItems, int sortType)
	{
		Arrays.sort(brokerItems, BrokerItem.getComparatoryByType(sortType));
	}
	
	/**
	 * Retrieves a specific page of items from the provided array.<br>
	 * This method calculates the starting index based on the {@code startPage}.<br>
	 * It returns an array containing up to 45 items for the requested page.
	 * @param brokerItems The full array of {@link BrokerItem} objects to filter.
	 * @param startPage The zero-based index of the page to retrieve.
	 * @return An array of {@link BrokerItem} objects belonging to the requested page.
	 */
	private BrokerItem[] getRequestedPage(BrokerItem[] brokerItems, int startPage)
	{
		final List<BrokerItem> page = new ArrayList<>();
		final int startingElement = startPage * 9;
		for (int i = startingElement, limit = 0; (i < brokerItems.length) && (limit < 45); i++, limit++)
		{
			page.add(brokerItems[i]);
		}
		
		return page.toArray(new BrokerItem[page.size()]);
	}
	
	/**
	 * Retrieves the broker items associated with a specific {@link Race}.<br>
	 * This method returns a map of items based on the race type.
	 * @param race The {@code Race} to filter the broker items by.
	 * @return A {@code Map} containing the items for the specified race, or {@code null} if not found.
	 */
	private Map<Integer, BrokerItem> getRaceBrokerItems(Race race)
	{
		switch (race)
		{
			case ELYOS:
				return elyosBrokerItems;
			case ASMODIANS:
				return asmodianBrokerItems;
			default:
				return null;
		}
	}
	
	/**
	 * Retrieves the list of settled broker items for a specific race.<br>
	 * This method returns a map where the key is the unique ID and the value is the {@code BrokerItem}.<br>
	 * It handles different races like {@code ELYOS} and {@code ASMODIANS}.
	 * @param race The {@code Race} type to check for settled items.
	 * @return A {@code Map} of unique IDs to {@code BrokerItem} objects, or {@code null} if the race is not supported.
	 */
	private Map<Integer, BrokerItem> getRaceBrokerSettledItems(Race race)
	{
		switch (race)
		{
			case ELYOS:
				return elyosSettledItems;
			case ASMODIANS:
				return asmodianSettledItems;
			default:
				return null;
		}
	}
	
	/**
	 * Processes the purchase of an item from the broker for a specific player.<br>
	 * This method validates the price, inventory space, and item availability before completing the trade.<br>
	 * It handles both full and partial purchases of listed items.
	 * @param player The {@link Player} who is attempting to buy the item.
	 * @param itemUniqueId The unique identifier for the specific broker listing.
	 * @param itemCount The number of items the player wants to purchase.
	 */
	public void buyBrokerItem(Player player, int itemUniqueId, long itemCount)
	{
		final Race playerRace = player.getRace();
		
		final BrokerItem buyingItem = getRaceBrokerItems(playerRace).get(itemUniqueId);
		
		if (!RestrictionsManager.canTrade(player) || (buyingItem == null))
		{
			return;
		}
		
		final long price = buyingItem.getPrice();
		final float unitPrice = (float) price / buyingItem.getItemCount();
		final long allPrice = ((long) unitPrice * itemCount);
		
		if (itemCount > buyingItem.getItemCount())
		{
			return;
		}
		
		if ((buyingItem.isSold() || buyingItem.isSettled()) && (buyingItem.getItem() != null))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_VENDOR_SOLD_OUT(buyingItem.getItem().getNameId()));
			return;
		}
		
		if (SecurityConfig.BROKER_PREBUY_CHECK)
		{
			// wtf?
			if (!(DAOManager.getDAO(BrokerDAO.class).preBuyCheck(itemUniqueId)))
			{
				PacketSendUtility.sendMessage(player, "Sorry, but this item already sold");
				return;
			}
		}
		
		if (buyingItem.getSellerId() == player.getObjectId())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_VENDOR_CAN_NOT_BUY_MY_REGISTER_ITEM);
			return;
		}
		
		synchronized (this)
		{
			if (buyingItem.isSold() || buyingItem.isCanceled())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_VENDOR_SOLD_OUT(buyingItem.getItem().getNameId()));
				return;
			}
			
			final Item item = buyingItem.getItem();
			if (player.getInventory().isFull(item.getItemTemplate().getExtraInventoryId()))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
				return;
			}
			
			if (player.getInventory().getKinah() < allPrice)
			{
				return;
			}
			
			int type = 0;
			Item partSaleItem = null;
			if (itemCount == buyingItem.getItemCount())
			{
				type = 1;
				getRaceBrokerItems(playerRace).remove(itemUniqueId);
				putToSettled(playerRace, buyingItem, true);
			}
			else
			{
				item.setItemCount(buyingItem.getItemCount() - itemCount);
				buyingItem.setItemCount(buyingItem.getItemCount() - itemCount);
				buyingItem.setPrice(price - allPrice);
				type = 0;
				buyingItem.setPersistentState(PersistentState.UPDATE_ITEM_BROKER);
				saveManager.add(new BrokerOpSaveTask(buyingItem, item, null, buyingItem.getSellerId()));
				partSaleItem = buyPart(playerRace, buyingItem, allPrice, itemCount);
			}
			
			player.getInventory().decreaseKinah(allPrice);
			final Item boughtItem = player.getInventory().add(type != 0 ? item : partSaleItem);
			
			if (LoggingConfig.LOG_BROKER_EXCHANGE)
			{
				log.info("[BROKER EXCHANGE] > [Player: " + player.getName() + "] bought [Item: " + buyingItem.getItemId() + "] " + "[Count: " + (type != 0 ? buyingItem.getItemCount() : (partSaleItem == null ? 0 : partSaleItem.getItemCount())) + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "] [Item Name: " + item.getItemName() : "]") + " from [Player: " + buyingItem.getSeller() + "] for [Price: " + allPrice + "]");
			}
			
			// create save task
			final BrokerOpSaveTask bost = new BrokerOpSaveTask(buyingItem, boughtItem, player.getInventory().getKinahItem(), player.getObjectId());
			saveManager.add(bost);
		}
		
		showRequestedItems(player, getPlayerCache(player).getBrokerMaskCache(), getPlayerCache(player).getBrokerSortTypeCache(), getPlayerCache(player).getBrokerStartPageCache(), getPlayerCache(player).getSearchItemList());
	}
	
	/**
	 * Processes the purchase of a specific item part from the broker.<br>
	 * This method creates a new {@code Item}, copies its information, and updates the broker records.<br>
	 * It also notifies the seller that an item has been sold.
	 * @param playerRace The race of the player making the purchase.
	 * @param buyingItem The {@code BrokerItem} being purchased.
	 * @param price The price of the item.
	 * @param itemCount The quantity of the item to be purchased.
	 * @return The newly created {@code Item} object.
	 */
	private Item buyPart(Race playerRace, BrokerItem buyingItem, long price, long itemCount)
	{
		final Item item = buyingItem.getItem();
		final int nameId = item.getNameId();
		BrokerRace brokerRace;
		if (playerRace == Race.ASMODIANS)
		{
			brokerRace = BrokerRace.ASMODIAN;
		}
		else if (playerRace == Race.ELYOS)
		{
			brokerRace = BrokerRace.ELYOS;
		}
		else
		{
			return item;
		}
		
		final Item newItem = ItemFactory.newItem(item.getItemId(), itemCount);
		copyItemInfo(item, newItem);
		final BrokerItem brokerItem = new BrokerItem(newItem, price, buyingItem.getSeller(), buyingItem.getSellerId(), brokerRace, buyingItem.isPartSale());
		brokerItem.setItemCount(itemCount);
		brokerItem.removeItem();
		brokerItem.setPersistentState(PersistentState.NEW);
		saveManager.add(new BrokerOpSaveTask(brokerItem));
		switch (playerRace)
		{
			case ASMODIANS:
				asmodianBrokerItems.put(buyingItem.getItemUniqueId(), buyingItem);
				asmodianSettledItems.put(brokerItem.getItemUniqueId(), brokerItem);
				break;
			case ELYOS:
				elyosBrokerItems.put(buyingItem.getItemUniqueId(), buyingItem);
				elyosSettledItems.put(brokerItem.getItemUniqueId(), brokerItem);
				break;
			default:
				break;
		}
		
		final Player player = World.getInstance().findPlayer(buyingItem.getSellerId());
		if (player != null)
		{
			PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(true, getTotalSettledKinah(player)));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_VENDOR_REGISTER_SOLD_OUT(nameId));
		}
		
		return newItem;
	}
	
	/**
	 * Copies all properties from an existing item to a new one.<br>
	 * This includes sockets, stones, stats, and visual attributes.<br>
	 * It ensures the {@code newItem} matches the state of the {@code oldItem}.
	 * @param oldItem The source item containing the data to copy.
	 * @param newItem The target item that will receive the copied data.
	 */
	private static void copyItemInfo(Item oldItem, Item newItem)
	{
		newItem.setOptionalSocket(oldItem.getOptionalSocket());
		newItem.setItemCreator(oldItem.getItemCreator());
		if (oldItem.hasManaStones())
		{
			for (ManaStone stone : oldItem.getItemStones())
			{
				ItemSocketService.addManaStone(newItem, stone.getItemId());
			}
		}
		
		if (oldItem.getGodStone() != null)
		{
			newItem.addGodStone(oldItem.getGodStone().getItemId());
		}
		
		newItem.setEnchantOrAuthorizeLevel(oldItem.getItemTemplate().getMaxAuthorize() > 0 ? 0 : oldItem.getEnchantOrAuthorizeLevel());
		if (oldItem.isSoulBound())
		{
			newItem.setSoulBound(true);
		}
		
		newItem.setBonusNumber(oldItem.getBonusNumber());
		newItem.setRandomStats(oldItem.getRandomStats());
		newItem.setRandomCount(oldItem.getRandomCount());
		newItem.setIdianStone(oldItem.getIdianStone());
		newItem.setItemColor(oldItem.getItemColor());
		newItem.setItemSkinTemplate(oldItem.getItemSkinTemplate());
		newItem.setColorExpireTime(oldItem.getColorExpireTime());
		
		// Set the expiration time of the new item to match that of the old item.
		newItem.setActivationCount(oldItem.getActivationCount());
		newItem.setEquipped(oldItem.isEquipped());
		newItem.setEquipmentSlot(oldItem.getEquipmentSlot());
		newItem.setItemLocation(oldItem.getItemLocation());
		newItem.setFusionedItem(oldItem.getFusionedItemTemplate());
		newItem.setOptionalFusionSocket(oldItem.getOptionalFusionSocket());
		newItem.setPackCount(oldItem.getPackCount());
		newItem.setEnchantOrAuthorizeLevel(oldItem.getItemTemplate().getMaxAuthorize() > 0 ? oldItem.getEnchantOrAuthorizeLevel() : 0);
		newItem.setPacked(oldItem.isPacked());
		newItem.setAmplified(oldItem.isAmplified());
		newItem.setAmplificationSkill(oldItem.getAmplificationSkill());
	}
	
	/**
	 * Updates the status of a {@code BrokerItem} and moves it to the settled collection.<br>
	 * This method handles both sold items and items that have been marked as settled.<br>
	 * It also updates the seller's information and triggers necessary database saves.
	 * @param race The {@link Race} category of the broker item.
	 * @param brokerItem The {@code BrokerItem} being updated.
	 * @param isSold A boolean indicating if the item was sold or just settled.
	 */
	private void putToSettled(Race race, BrokerItem brokerItem, boolean isSold)
	{
		final int itemNameId = brokerItem.getItem().getNameId();
		
		if (isSold)
		{
			brokerItem.removeItem();
		}
		else
		{
			brokerItem.setSettled();
		}
		
		brokerItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
		
		switch (race)
		{
			case ASMODIANS:
				asmodianSettledItems.put(brokerItem.getItemUniqueId(), brokerItem);
				break;
			case ELYOS:
				elyosSettledItems.put(brokerItem.getItemUniqueId(), brokerItem);
				break;
			default:
				break;
		}
		
		final Player seller = World.getInstance().findPlayer(brokerItem.getSellerId());
		
		saveManager.add(new BrokerOpSaveTask(brokerItem));
		
		if (seller != null)
		{
			PacketSendUtility.sendPacket(seller, new SM_BROKER_SERVICE(true, getTotalSettledKinah(seller)));
			if (isSold)
			{
				PacketSendUtility.sendPacket(seller, SM_SYSTEM_MESSAGE.STR_VENDOR_REGISTER_SOLD_OUT(itemNameId));
			}
		}
	}
	
	/**
	 * Calculates the total number of items registered by a specific player.<br>
	 * It checks all broker items for the player's race and counts those belonging to the {@code Player}.
	 * @param player The {@link Player} whose registration count is being retrieved.
	 * @return The total count of registered items as an {@code int}.
	 */
	private int getRegisteredItemsCount(Player player)
	{
		final int playerId = player.getObjectId();
		int c = 0;
		for (BrokerItem item : getRaceBrokerItems(player.getRace()).values())
		{
			if ((item != null) && (playerId == item.getSellerId()))
			{
				c++;
			}
		}
		
		return c;
	}
	
	/**
	 * Registers an item from a player's inventory into the broker system.<br>
	 * This method validates the item properties and checks if the player has enough Kinah for the commission.<br>
	 * If successful, it removes the item from the player and adds it to the public broker list.
	 * @param player The {@link Player} who is registering the item.
	 * @param itemUniqueId The unique identifier of the item in the inventory.
	 * @param count The number of items to register.
	 * @param price The price per single unit of the item.
	 * @param partSale A boolean indicating if the item can be sold in parts.
	 */
	public void registerItem(Player player, int itemUniqueId, long count, long price, boolean partSale)
	{
		Item itemToRegister = player.getInventory().getItemByObjId(itemUniqueId);
		final Race playerRace = player.getRace();
		
		final long totalPrice = price * count;
		if ((itemToRegister == null) || (count > itemToRegister.getItemCount()) || !RestrictionsManager.canTrade(player) || (price <= 0))
		{
			return;
		}
		
		// check max price for 1 item in stack
		if ((totalPrice / count) > 999999999)
		{
			return;
		}
		
		// check if item is not soulbound
		if (itemToRegister.isSoulBound())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_VENDOR_REGISTER_USED_ITEM);
			return;
		}
		
		// Check Trade Hack
		if (!itemToRegister.isTradeable(player) || !AdminService.getInstance().canOperate(player, null, itemToRegister, "broker"))
		{
			return;
		}
		
		BrokerRace brRace;
		
		if (playerRace == Race.ASMODIANS)
		{
			brRace = BrokerRace.ASMODIAN;
		}
		else if (playerRace == Race.ELYOS)
		{
			brRace = BrokerRace.ELYOS;
		}
		else
		{
			return;
		}
		
		final int registeredItemsCount = getRegisteredItemsCount(player);
		int registrationCommition = 0;
		if (registeredItemsCount > 14)
		{
			PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(BrokerMessages.NO_SPACE_AVAIABLE.getId()));
			return;
		}
		else if (registeredItemsCount > 9)
		{
			registrationCommition = Math.round(totalPrice * 0.04f);
		}
		else
		{
			registrationCommition = Math.round(totalPrice * 0.02f);
		}
		
		if (registrationCommition < 10)
		{
			registrationCommition = 10;
		}
		
		if (player.getInventory().getKinah() < registrationCommition)
		{
			PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(BrokerMessages.NO_ENOUGHT_KINAH.getId()));
			return;
		}
		
		player.getInventory().decreaseKinah(registrationCommition);
		if (itemToRegister.getItemTemplate().isStackable() && (count < itemToRegister.getItemCount()))
		{
			final int itemId = itemToRegister.getItemId();
			player.getInventory().decreaseItemCount(itemToRegister, count);
			itemToRegister = ItemFactory.newItem(itemId, count);
		}
		else
		{
			player.getInventory().remove(itemToRegister);
			PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(itemToRegister.getObjectId()));
		}
		
		itemToRegister.setItemLocation(126);
		
		final BrokerItem newBrokerItem = new BrokerItem(itemToRegister, totalPrice, player.getName(), player.getObjectId(), brRace, partSale);
		
		switch (brRace)
		{
			case ASMODIAN:
				asmodianBrokerItems.put(newBrokerItem.getItemUniqueId(), newBrokerItem);
				break;
			case ELYOS:
				elyosBrokerItems.put(newBrokerItem.getItemUniqueId(), newBrokerItem);
				break;
		}
		
		final BrokerOpSaveTask bost = new BrokerOpSaveTask(newBrokerItem, itemToRegister, player.getInventory().getKinahItem(), player.getObjectId());
		saveManager.add(bost);
		
		PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(newBrokerItem, 0, registeredItemsCount));
	}
	
	/**
	 * Displays the items registered by a specific player in the broker.<br>
	 * This method finds all {@link BrokerItem} objects belonging to the player's race.<br>
	 * It filters for items where the seller ID matches the {@code Player} object ID.<br>
	 * Finally, it sends an {@link SM_BROKER_SERVICE} packet to the player.
	 * @param player The {@code Player} who will receive the list of registered items.
	 */
	public void showRegisteredItems(Player player)
	{
		final Map<Integer, BrokerItem> brokerItems = getRaceBrokerItems(player.getRace());
		
		final List<BrokerItem> registeredItems = new ArrayList<>();
		final int playerId = player.getObjectId();
		
		for (BrokerItem item : brokerItems.values())
		{
			if ((item != null) && (item.getItem() != null) && (playerId == item.getSellerId()))
			{
				registeredItems.add(item);
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(registeredItems.toArray(new BrokerItem[registeredItems.size()])));
	}
	
	/**
	 * Checks if the specified {@link Player} has any items registered in the broker.<br>
	 * This method looks for items where the player is listed as the seller.
	 * @param player The {@code Player} to check.
	 * @return {@code true} if the player has registered items, otherwise {@code false}.
	 */
	public boolean hasRegisteredItems(Player player)
	{
		final Map<Integer, BrokerItem> brokerItems = getRaceBrokerItems(player.getRace());
		for (BrokerItem item : brokerItems.values())
		{
			if ((item != null) && (item.getItem() != null) && (player.getObjectId() == item.getSellerId()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Displays the broker window for a specific item to the player.<br>
	 * This method calculates and sends the average price data based on the item's race.<br>
	 * If no items are found, it sends a default service packet.
	 * @param player The {@link Player} who will view the window.
	 * @param itemObjectId The unique identifier of the item to display.
	 */
	public void showAddItemWindow(Player player, int itemObjectId)
	{
		final Map<Integer, BrokerItem> brokerItems = getRaceBrokerItems(player.getRace());
		final List<BrokerItem> items = new ArrayList<>();
		final int itemId = player.getInventory().getItemByObjId(itemObjectId).getItemId();
		for (BrokerItem item : brokerItems.values())
		{
			if (item.getItemId() == itemId)
			{
				items.add(item);
			}
		}
		
		if (items.size() < 1)
		{
			PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(itemObjectId, 1, 1, 1, true));
		}
		else
		{
			final long[] avgMaxMin = getAvgMaxMinPrice(items);
			PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(itemObjectId, avgMaxMin[0], avgMaxMin[1], avgMaxMin[2], avgMaxMin[1] == avgMaxMin[2]));
		}
	}
	
	/**
	 * Calculates the average, maximum, and minimum prices from a list of {@link BrokerItem} objects.<br>
	 * The method iterates through all provided items to aggregate price data.<br>
	 * It returns an array containing these three values in order.
	 * @param items The list of {@link BrokerItem} objects to process.
	 * @return A {@code long[]} where index 0 is the average, index 1 is the maximum, and index 2 is the minimum price.
	 */
	public long[] getAvgMaxMinPrice(List<BrokerItem> items)
	{
		final long[] avgMaxMin = new long[]
		{
			0,
			0,
			0
		};
		
		for (BrokerItem item : items)
		{
			final long price = item.getPrice();
			avgMaxMin[0] += price;
			
			if (price > avgMaxMin[1])
			{
				avgMaxMin[1] = price;
			}
			
			if (avgMaxMin[2] == 0)
			{
				avgMaxMin[2] = price;
			}
			
			if (price < avgMaxMin[2])
			{
				avgMaxMin[2] = price;
			}
		}
		
		avgMaxMin[0] = avgMaxMin[0] / items.size();
		return avgMaxMin;
	}
	
	/**
	 * Removes a specific item from the broker and returns it to the player.<br>
	 * This method checks if the {@code player} owns the item before processing.<br>
	 * It also verifies that the player has enough inventory space.<br>
	 * If successful, the item is added back to the player's inventory and removed from the broker list.
	 * @param player The {@link Player} who is canceling the item registration.
	 * @param brokerItemId The unique identifier of the item in the broker.
	 */
	public void cancelRegisteredItem(Player player, int brokerItemId)
	{
		final Map<Integer, BrokerItem> brokerItems = getRaceBrokerItems(player.getRace());
		final BrokerItem brokerItem = brokerItems.get(brokerItemId);
		
		if (brokerItem != null)
		{
			if (!brokerItem.getSeller().equals(player.getName()))
			{
				log.info("[AUDIT] Player: {} try get from broker not own item", player.getName());
				return;
			}
			
			if (player.getInventory().isFull(brokerItem.getItem().getItemTemplate().getExtraInventoryId()))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_VENDOR_FULL_INVENTORY);
				return;
			}
			
			synchronized (this)
			{
				player.getInventory().add(brokerItem.getItem());
				brokerItem.setPersistentState(PersistentState.DELETED);
				saveManager.add(new BrokerOpSaveTask(brokerItem));
				brokerItem.setIsCanceled(true);
				brokerItems.remove(brokerItemId);
			}
		}
		
		showRegisteredItems(player);
	}
	
	/**
	 * Displays the settled items for a specific player.<br>
	 * This method retrieves all broker items associated with the {@code Player}'s race.<br>
	 * It filters these items to find those belonging to the current player.<br>
	 * Finally, it sends the list and total Kinah to the client using {@link SM_BROKER_SERVICE}.
	 * @param player The {@code Player} object for whom to display settled items.
	 */
	public void showSettledItems(Player player)
	{
		final Map<Integer, BrokerItem> brokerSettledItems = getRaceBrokerSettledItems(player.getRace());
		final List<BrokerItem> settledItems = new ArrayList<>();
		final int playerId = player.getObjectId();
		long totalKinah = 0;
		for (BrokerItem item : brokerSettledItems.values())
		{
			if ((item != null) && (playerId == item.getSellerId()))
			{
				settledItems.add(item);
				if (item.isSold())
				{
					totalKinah += item.getPrice();
				}
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(settledItems.toArray(new BrokerItem[settledItems.size()]), totalKinah));
	}
	
	/**
	 * Calculates the total amount of money collected by a player.<br>
	 * This method sums up the prices of all sold items belonging to the player.<br>
	 * It checks the settled broker items for the player's specific race.
	 * @param playerCommonData The data object containing information about the player.
	 * @return The total sum of money collected as an {@code int}.
	 */
	public int getCollectedMoney(PlayerCommonData playerCommonData)
	{
		final Map<Integer, BrokerItem> brokerSettledItems = getRaceBrokerSettledItems(playerCommonData.getRace());
		final int playerId = playerCommonData.getPlayerObjId();
		int totalKinah = 0;
		for (BrokerItem item : brokerSettledItems.values())
		{
			if ((item != null) && (playerId == item.getSellerId()))
			{
				if (item.isSold())
				{
					totalKinah += item.getPrice();
				}
			}
		}
		
		return totalKinah;
	}
	
	/**
	 * Calculates the total amount of Kinah earned by a player from sold items.<br>
	 * It checks all settled broker items for the player's race.<br>
	 * Only items sold by this specific {@code Player} are included in the sum.
	 * @param player The {@link Player} whose total earnings need to be calculated.
	 * @return The total sum of Kinah from successfully sold items as a {@code long}.
	 */
	private long getTotalSettledKinah(Player player)
	{
		long totalKinah = 0;
		final int playerId = player.getObjectId();
		for (BrokerItem item : getRaceBrokerSettledItems(player.getRace()).values())
		{
			if ((item != null) && (playerId == item.getSellerId()))
			{
				if (item.isSold())
				{
					totalKinah += item.getPrice();
				}
			}
		}
		
		return totalKinah;
	}
	
	/**
	 * Processes the settlement of broker items for a specific player.<br>
	 * This method distributes sold items to the player's inventory and adds kinah.<br>
	 * It also removes settled items from the active broker list.<br>
	 * Finally, it updates the player's UI with the new status.
	 * @param player The {@code Player} object for whom the account is being settled.
	 */
	public void settleAccount(Player player)
	{
		final Race playerRace = player.getRace();
		final Map<Integer, BrokerItem> brokerSettledItems = getRaceBrokerSettledItems(playerRace);
		final List<BrokerItem> collectedItems = new ArrayList<>();
		final int playerId = player.getObjectId();
		long kinahCollect = 0;
		boolean itemsLeft = false;
		
		for (BrokerItem item : brokerSettledItems.values())
		{
			if (item.getSellerId() == playerId)
			{
				collectedItems.add(item);
			}
		}
		
		for (BrokerItem item : collectedItems)
		{
			if (item.isSold())
			{
				boolean result = false;
				switch (playerRace)
				{
					case ASMODIANS:
						result = asmodianSettledItems.remove(item.getItemUniqueId()) != null;
						break;
					case ELYOS:
						result = elyosSettledItems.remove(item.getItemUniqueId()) != null;
						break;
					default:
						break;
				}
				
				if (result)
				{
					item.setPersistentState(PersistentState.DELETED);
					saveManager.add(new BrokerOpSaveTask(item));
					kinahCollect += item.getPrice();
				}
			}
			else
			{
				if (item.getItem() != null)
				{
					final Item resultItem = player.getInventory().add(item.getItem());
					if (resultItem != null)
					{
						boolean result = false;
						switch (playerRace)
						{
							case ASMODIANS:
								result = asmodianSettledItems.remove(item.getItemUniqueId()) != null;
								break;
							case ELYOS:
								result = elyosSettledItems.remove(item.getItemUniqueId()) != null;
								break;
							default:
								break;
						}
						
						if (result)
						{
							item.setPersistentState(PersistentState.DELETED);
							saveManager.add(new BrokerOpSaveTask(item));
						}
					}
					else
					{
						itemsLeft = true;
					}
				}
				else
				{
					log.warn("Broker settled item missed. ObjID: " + item.getItemUniqueId());
				}
			}
		}
		
		player.getInventory().increaseKinah(kinahCollect);
		
		showSettledItems(player);
		
		if (!itemsLeft)
		{
			PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(false, 0));
		}
	}
	
	/**
	 * Checks for items in the broker that have passed their expiration time.<br>
	 * It processes items for both {@code ASMODIANS} and {@code ELYOS} races.<br>
	 * Expired items are moved to settled status or removed from the active list.
	 */
	private void checkExpiredItems()
	{
		final Map<Integer, BrokerItem> asmoBrokerItems = getRaceBrokerItems(Race.ASMODIANS);
		final Map<Integer, BrokerItem> elyosBrokerItems = getRaceBrokerItems(Race.ELYOS);
		
		final Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
		
		for (BrokerItem item : asmoBrokerItems.values())
		{
			if ((item != null) && (item.getExpireTime().getTime() <= currentTime.getTime()))
			{
				putToSettled(Race.ASMODIANS, item, false);
				
				// this.expireItem(Race.ASMODIANS, item);
				asmodianBrokerItems.remove(item.getItemUniqueId());
			}
		}
		
		for (BrokerItem item : elyosBrokerItems.values())
		{
			if ((item != null) && (item.getExpireTime().getTime() <= currentTime.getTime()))
			{
				// putToSettled(Race.ELYOS, item, false);
				expireItem(Race.ELYOS, item);
				this.elyosBrokerItems.remove(item.getItemUniqueId());
			}
		}
	}
	
	/**
	 * Handles the expiration of a {@code BrokerItem}.<br>
	 * It attempts to send a system mail to the seller.<br>
	 * If the mail is sent, the item is marked as deleted and saved.<br>
	 * Otherwise, it moves the item to the settled list for the given {@code Race}.
	 * @param race The {@link Race} associated with the broker items.
	 * @param item The {@link BrokerItem} that needs to be expired.
	 */
	private void expireItem(Race race, BrokerItem item)
	{
		if (SystemMailService.getInstance().sendSystemMail("$$VENDOR_RETURN_MAIL", "", "", item.getSeller(), item.getItem(), 0, LetterType.NORMAL))
		{
			item.setPersistentState(PersistentState.DELETED);
			saveManager.add(new BrokerOpSaveTask(item));
		}
		else
		{
			putToSettled(race, item, false);
		}
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It handles any initialization logic required for the player's session.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		final Map<Integer, BrokerItem> brokerSettledItems = getRaceBrokerSettledItems(player.getRace());
		final int playerId = player.getObjectId();
		for (BrokerItem item : brokerSettledItems.values())
		{
			if ((item != null) && (playerId == item.getSellerId()))
			{
				PacketSendUtility.sendPacket(player, new SM_BROKER_SERVICE(true, getTotalSettledKinah(player)));
				break;
			}
		}
	}
	
	/**
	 * Retrieves the {@code BrokerPlayerCache} for a specific player.<br>
	 * If no cache exists, it creates a new one and stores it in the map.
	 * @param player The {@link Player} object to look up.
	 * @return The {@code BrokerPlayerCache} associated with the player.
	 */
	private BrokerPlayerCache getPlayerCache(Player player)
	{
		BrokerPlayerCache cacheEntry = playerBrokerCache.get(player.getObjectId());
		if (cacheEntry == null)
		{
			cacheEntry = new BrokerPlayerCache();
			playerBrokerCache.put(player.getObjectId(), cacheEntry);
		}
		
		return cacheEntry;
	}
	
	/**
	 * Removes the broker cache data for a specific {@link Player}.<br>
	 * This method clears the cached items associated with the player's unique ID.
	 * @param player The {@code Player} whose cache needs to be removed.
	 */
	public void removePlayerCache(Player player)
	{
		playerBrokerCache.remove(player.getObjectId());
	}
	
	/**
	 * Retrieves the broker mask cache for a specific {@link Player}.<br>
	 * This value is used to filter items shown in the broker service.
	 * @param player The {@code Player} object to retrieve the mask from.
	 * @return The integer mask associated with the player's broker cache.
	 */
	private int getPlayerMask(Player player)
	{
		return getPlayerCache(player).getBrokerMaskCache();
	}
	
	/**
	 * Retrieves the list of broker items for a specific {@code Player}.<br>
	 * This method fetches the cached items from the {@link BrokerPlayerCache}.
	 * @param player The {@code Player} object to retrieve items for.
	 * @return An array of {@code BrokerItem} objects belonging to the player.
	 */
	private BrokerItem[] getFilteredItems(Player player)
	{
		return getPlayerCache(player).getBrokerListCache();
	}
	
	/**
	 * Frequent running save task
	 */
	public static final class BrokerPeriodicTaskManager extends AbstractFIFOPeriodicTaskManager<BrokerOpSaveTask>
	{
		private static final String CALLED_METHOD_NAME = "brokerOperation()";
		
		/**
		 * @param period
		 */
		public BrokerPeriodicTaskManager(int period)
		{
			super(period);
		}
		
		@Override
		protected void callTask(BrokerOpSaveTask task)
		{
			task.run();
		}
		
		@Override
		protected String getCalledMethodName()
		{
			return CALLED_METHOD_NAME;
		}
	}
	
	/**
	 * This class is used for storing all items in one shot after any broker operation
	 */
	public static final class BrokerOpSaveTask implements Runnable
	{
		private final BrokerItem brokerItem;
		private Item item;
		private Item kinahItem;
		private int playerId;
		
		/**
		 * @param brokerItem
		 * @param item
		 * @param kinahItem
		 * @param playerId
		 */
		private BrokerOpSaveTask(BrokerItem brokerItem, Item item, Item kinahItem, int playerId)
		{
			this.brokerItem = brokerItem;
			this.item = item;
			this.kinahItem = kinahItem;
			this.playerId = playerId;
		}
		
		/**
		 * @param brokerItem
		 */
		public BrokerOpSaveTask(BrokerItem brokerItem)
		{
			this.brokerItem = brokerItem;
		}
		
		@Override
		public void run()
		{
			// first save item for FK consistency
			if (item != null)
			{
				DAOManager.getDAO(InventoryDAO.class).store(item, playerId);
			}
			
			if (brokerItem != null)
			{
				DAOManager.getDAO(BrokerDAO.class).store(brokerItem);
			}
			
			if (kinahItem != null)
			{
				DAOManager.getDAO(InventoryDAO.class).store(kinahItem, playerId);
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final BrokerService instance = new BrokerService();
	}
}
