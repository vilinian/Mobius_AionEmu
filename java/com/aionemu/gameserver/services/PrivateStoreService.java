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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PrivateStore;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.trade.TradeItem;
import com.aionemu.gameserver.model.trade.TradeList;
import com.aionemu.gameserver.model.trade.TradePSItem;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PRIVATE_STORE_NAME;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the logic for private stores within the game world.<br>
 * This service handles item transactions and interactions between players using {@link PrivateStore}.
 * @author Simple
 */
public class PrivateStoreService
{
	private static final Logger log = LoggerFactory.getLogger("EXCHANGE_LOG");
	
	/**
	 * Processes the sale of items from a private store to a buyer.<br>
	 * This method validates participants and checks if the buyer has enough kinah.<br>
	 * It transfers the items to the buyer and updates the currency for both players.
	 * @param seller The {@link Player} who owns the private store.
	 * @param buyer The {@link Player} purchasing the items.
	 * @param tradeList The {@link TradeList} containing the items to be sold.
	 */
	public static void sellStoreItem(Player seller, Player buyer, TradeList tradeList)
	{
		/**
		 * 1. Check if we are busy with two valid participants
		 */
		if (!validateParticipants(seller, buyer))
		{
			return;
		}
		
		/**
		 * Define store to make life easier
		 */
		final PrivateStore store = seller.getStore();
		
		/**
		 * 2. Load all item object id's and validate if seller really owns them
		 */
		tradeList = loadObjIds(seller, tradeList);
		if (tradeList == null)
		{
			return; // Invalid items found or store was empty
		}
		
		/**
		 * 3. Check free slots
		 */
		final Storage inventory = buyer.getInventory();
		final int freeSlots = (inventory.getLimit() - inventory.getItemsWithKinah().size()) + 1;
		if (freeSlots < tradeList.size())
		{
			return; // TODO message
		}
		
		/**
		 * Create total price and items
		 */
		final long price = getTotalPrice(store, tradeList);
		
		// Kinah exploit fix
		if (price < 0)
		{
			return;
		}
		
		/**
		 * Check if player has enough kinah
		 */
		if (buyer.getInventory().getKinah() >= price)
		{
			for (TradeItem tradeItem : tradeList.getTradeItems())
			{
				final Item item = getItemByObjId(seller, tradeItem.getItemId());
				if (item != null)
				{
					final TradePSItem storeItem = store.getTradeItemByObjId(tradeItem.getItemId());
					
					// Fix "Private store stackable items dupe" by Asanka
					if (item.getItemCount() < tradeItem.getCount())
					{
						PacketSendUtility.sendMessage(buyer, "You cannot buy more than player can sell.");
						return;
					}
					
					// Decrease/remove item from store and add them to buyer
					decreaseItemFromPlayer(seller, item, tradeItem);
					ItemService.addItem(buyer, item.getItemId(), tradeItem.getCount(), item);
					if (storeItem.getCount() == tradeItem.getCount())
					{
						store.removeItem(storeItem.getItemObjId());
					}
					
					// Log the trade
					log.info("[PRIVATE STORE] > [Seller: " + seller.getName() + "] sold [Item: " + item.getItemId() + "][Amount: " + item.getItemCount() + "] to [Buyer: " + buyer.getName() + "] for [Price: " + price + "]");
				}
			}
			
			// Decrease kinah for buyer and Increase kinah for seller
			decreaseKinahAmount(buyer, price);
			increaseKinahAmount(seller, price);
			
			/**
			 * Remove item from store and check if last item
			 */
			if (store.getSoldItems().size() == 0)
			{
				closePrivateStore(seller);
			}
		}
	}
	
	/**
	 * Opens a private store for the specified player.<br>
	 * This method sets the store message and broadcasts the name to other players.<br>
	 * It handles faction visibility based on the {@code CustomConfig} settings.
	 * @param activePlayer The {@link Player} who is opening the store.
	 * @param name The display name for the private store. If {@code null}, an empty string is used.
	 */
	public static void openPrivateStore(Player activePlayer, String name)
	{
		final int senderRace = activePlayer.getRace().getRaceId();
		final Player playerActive = activePlayer;
		if (name != null)
		{
			activePlayer.getStore().setStoreMessage(name);
			if (CustomConfig.SPEAKING_BETWEEN_FACTIONS)
			{
				PacketSendUtility.broadcastPacket(playerActive, new SM_PRIVATE_STORE_NAME(playerActive.getObjectId(), name), true);
			}
			else
			{
				PacketSendUtility.broadcastPacket(playerActive, new SM_PRIVATE_STORE_NAME(playerActive.getObjectId(), name), true, object -> (((senderRace == object.getRace().getRaceId()) && !object.getBlockList().contains(playerActive.getObjectId())) || object.isGM()));
				PacketSendUtility.broadcastPacket(playerActive, new SM_PRIVATE_STORE_NAME(playerActive.getObjectId(), ""), false, object -> (senderRace != object.getRace().getRaceId()) && !object.getBlockList().contains(playerActive.getObjectId()) && !object.isGM());
			}
		}
		else
		{
			PacketSendUtility.broadcastPacket(playerActive, new SM_PRIVATE_STORE_NAME(playerActive.getObjectId(), ""), true);
		}
	}
	
	/**
	 * Adds specific items to the {@link PrivateStore} of a player.<br>
	 * This method checks if the {@code activePlayer} is currently active.<br>
	 * It creates a store if one does not exist for the player.<br>
	 * Only tradeable items owned by the player are added to the store.
	 * @param activePlayer The {@link Player} who will own the private store.
	 * @param tradePSItems An array of {@code TradePSItem} objects to be added to the store.
	 */
	public static void addItems(Player activePlayer, TradePSItem[] tradePSItems)
	{
		if (CreatureState.ACTIVE.getId() != activePlayer.getState())
		{
			return;
		}
		
		/**
		 * Check if player already has a store, if not create one
		 */
		// TODO synchronization
		if (activePlayer.getStore() == null)
		{
			createStore(activePlayer);
		}
		
		final PrivateStore store = activePlayer.getStore();
		
		/**
		 * Check if player owns itemObjId else don't add item
		 */
		for (int i = 0; i < tradePSItems.length; i++)
		{
			final Item item = getItemByObjId(activePlayer, tradePSItems[i].getItemObjId());
			if ((item != null) && item.isTradeable(activePlayer))
			{
				if (validateItem(store, item, tradePSItems[i]))
				{
					store.addItemToSell(tradePSItems[i].getItemObjId(), tradePSItems[i]);
				}
			}
		}
	}
	
	/**
	 * Checks if an {@link Item} is valid for a {@link PrivateStore}.<br>
	 * It verifies the item ID and count against the {@link TradePSItem}.<br>
	 * It also ensures the item is not already present in the store.
	 * @param store The {@link PrivateStore} containing the items.
	 * @param item The {@link Item} being validated.
	 * @param psItem The {@link TradePSItem} data for the store entry.
	 * @return {@code true} if the item is valid, otherwise {@code false}.
	 */
	private static boolean validateItem(PrivateStore store, Item item, TradePSItem psItem)
	{
		final int itemId = psItem.getItemId();
		final long itemCount = psItem.getCount();
		if ((item.getItemTemplate().getTemplateId() != itemId) || (itemCount > item.getItemCount()) || (itemCount < 1))
		{
			return false;
		}
		
		final TradePSItem addedPsItem = store.getTradeItemByObjId(psItem.getItemObjId());
		return addedPsItem == null;
		
	}
	
	/**
	 * Initializes a new {@link PrivateStore} for the player.<br>
	 * This method checks if the player is resting before proceeding.<br>
	 * It updates the player state to {@code PRIVATE_SHOP}.<br>
	 * It also broadcasts an opening emotion packet.
	 * @param activePlayer The {@code Player} who will own the store.
	 */
	private static void createStore(Player activePlayer)
	{
		if (activePlayer.isInState(CreatureState.RESTING))
		{
			return;
		}
		
		activePlayer.setStore(new PrivateStore(activePlayer));
		activePlayer.setState(CreatureState.PRIVATE_SHOP);
		PacketSendUtility.broadcastPacket(activePlayer, new SM_EMOTION(activePlayer, EmotionType.OPEN_PRIVATESHOP, 0, 0), true);
	}
	
	/**
	 * Closes the private store for a specific player.<br>
	 * This method sets the {@code Player} store to {@code null}.<br>
	 * It also removes the {@code PRIVATE_SHOP} state from the player.<br>
	 * Finally, it broadcasts a close emotion packet to the player.
	 * @param activePlayer The {@link Player} whose private store needs to be closed.
	 */
	public static void closePrivateStore(Player activePlayer)
	{
		activePlayer.setStore(null);
		activePlayer.unsetState(CreatureState.PRIVATE_SHOP);
		PacketSendUtility.broadcastPacket(activePlayer, new SM_EMOTION(activePlayer, EmotionType.CLOSE_PRIVATESHOP, 0, 0), true);
	}
	
	/**
	 * Reduces the quantity of an item from a player's inventory.<br>
	 * This method also updates the count in the {@link PrivateStore}.
	 * @param seller The {@code Player} who owns the item.
	 * @param item The {@code Item} being removed.
	 * @param tradeItem The {@code TradeItem} containing the amount to decrease.
	 */
	private static void decreaseItemFromPlayer(Player seller, Item item, TradeItem tradeItem)
	{
		seller.getInventory().decreaseItemCount(item, tradeItem.getCount());
		seller.getStore().getTradeItemByObjId(item.getObjectId()).decreaseCount(tradeItem.getCount());
	}
	
	/**
	 * This method adds a specific amount of currency to a player's inventory.<br>
	 * It updates the {@code Player} object by calling the {@code increaseKinah} method.
	 * @param player The {@link Player} who will receive the money.
	 * @param price The amount of kinah to add.
	 */
	private static void increaseKinahAmount(Player player, long price)
	{
		player.getInventory().increaseKinah(price);
	}
	
	/**
	 * Retrieves an {@link Item} from a player's inventory based on its unique ID.<br>
	 * This method uses the {@code itemObjId} to find the specific object owned by the {@code seller}.
	 * @param seller The {@link Player} whose inventory will be searched.
	 * @param itemObjId The unique identifier of the item to locate.
	 * @return The {@link Item} found in the inventory, or {@code null} if it does not exist.
	 */
	private static Item getItemByObjId(Player seller, int itemObjId)
	{
		return seller.getInventory().getItemByObjId(itemObjId);
	}
	
	/**
	 * Calculates the total price for all items in a trade.<br>
	 * It iterates through the {@code TradeList} and matches items with the {@link PrivateStore}.<br>
	 * The final sum is based on the item price multiplied by its count.
	 * @param store The {@code PrivateStore} containing the item data.
	 * @param tradeList The {@code TradeList} containing the items being traded.
	 * @return The total calculated price as a {@code long}.
	 */
	private static long getTotalPrice(PrivateStore store, TradeList tradeList)
	{
		long totalprice = 0;
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			final TradePSItem item = store.getTradeItemByObjId(tradeItem.getItemId());
			if (item == null)
			{
				continue;
			}
			
			totalprice += item.getPrice() * tradeItem.getCount();
		}
		
		return totalprice;
	}
	
	/**
	 * This method creates a new {@code TradeList} based on the items in a player's store.<br>
	 * It maps the IDs from the existing {@code tradeList} to the correct objects owned by the seller.<br>
	 * The method validates that the seller still owns the required items before returning the result.
	 * @param seller The {@link Player} who currently owns the private store.
	 * @param tradeList The original {@code TradeList} containing the requested item IDs and counts.
	 * @return A new {@code TradeList} with populated objects, or {@code null} if validation fails.
	 */
	private static TradeList loadObjIds(Player seller, TradeList tradeList)
	{
		final PrivateStore store = seller.getStore();
		final TradeList newTradeList = new TradeList();
		
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			int i = 0;
			for (int itemObjId : store.getSoldItems().keySet())
			{
				if (i == tradeItem.getItemId())
				{
					newTradeList.addPSItem(itemObjId, tradeItem.getCount());
				}
				
				i++;
			}
		}
		
		/**
		 * Check if player still owns items
		 */
		if (!validateBuyItems(seller, newTradeList))
		{
			return null;
		}
		
		return newTradeList;
	}
	
	/**
	 * Checks if both players are valid for a transaction.<br>
	 * It ensures that neither {@code Player} is {@code null}.<br>
	 * It verifies that both players are currently online.<br>
	 * It confirms that both players belong to the same race.
	 * @param itemOwner The player who currently owns the item.
	 * @param newOwner The player who will receive the item.
	 * @return {@code true} if all conditions are met, otherwise {@code false}.
	 */
	private static boolean validateParticipants(Player itemOwner, Player newOwner)
	{
		return (itemOwner != null) && (newOwner != null) && itemOwner.isOnline() && newOwner.isOnline() && itemOwner.getRace().equals(newOwner.getRace());
	}
	
	/**
	 * Checks if all items in the trade list exist in the seller's inventory.<br>
	 * This prevents selling fake items that do not belong to the {@code seller}.
	 * @param seller The player who is trying to sell the items.
	 * @param tradeList The list of items being traded.
	 * @return {@code true} if all items are valid, otherwise {@code false}.
	 */
	private static boolean validateBuyItems(Player seller, TradeList tradeList)
	{
		for (TradeItem tradeItem : tradeList.getTradeItems())
		{
			final Item item = seller.getInventory().getItemByObjId(tradeItem.getItemId());
			
			// 1) don't allow to sell fake items;
			if (item == null)
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This method reduces the amount of kinah for a specific player.<br>
	 * It updates the {@code Player} inventory by subtracting the given price.
	 * @param player The {@link Player} who will have their kinah reduced.
	 * @param price The amount of kinah to subtract from the player's balance.
	 */
	private static void decreaseKinahAmount(Player player, long price)
	{
		player.getInventory().decreaseKinah(price);
	}
}
