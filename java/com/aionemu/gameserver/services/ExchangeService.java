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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.trade.Exchange;
import com.aionemu.gameserver.model.trade.ExchangeItem;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_ADD_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_ADD_KINAH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_CONFIRMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_REQUEST;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;
import com.aionemu.gameserver.taskmanager.tasks.TemporaryTradeTimeTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Handles the logic for player-to-player item and currency trading.<br>
 * This service manages {@link Exchange} objects to facilitate safe transactions between players. It ensures that items are correctly moved, validated, and synchronized across the server.
 * @author ATracer
 */
public class ExchangeService
{
	private static final Logger log = LoggerFactory.getLogger("EXCHANGE_LOG");
	private final Map<Integer, Exchange> exchanges = new HashMap<>();
	private final ExchangePeriodicTaskManager saveManager;
	
	/**
	 * Provides the global instance of the {@link ExchangeService}.<br>
	 * This method follows the singleton pattern to ensure only one service exists.
	 * @return The single instance of {@code ExchangeService}.
	 */
	public static ExchangeService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link ExchangeService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * It initializes the internal {@code saveManager}.
	 */
	private ExchangeService()
	{
		final int DELAY_EXCHANGE_SAVE = 5000;
		saveManager = new ExchangePeriodicTaskManager(DELAY_EXCHANGE_SAVE);
	}
	
	/**
	 * Starts a new trade session between two players.<br>
	 * This method validates the participants and creates an {@link Exchange} object for both.<br>
	 * It also sends a request packet to notify each player of the trade.
	 * @param player1 The first player involved in the exchange.
	 * @param player2 The second player involved in the exchange.
	 */
	public void registerExchange(Player player1, Player player2)
	{
		if (!validateParticipants(player1, player2))
		{
			return;
		}
		
		player1.setTrading(true);
		player2.setTrading(true);
		
		exchanges.put(player1.getObjectId(), new Exchange(player1, player2));
		exchanges.put(player2.getObjectId(), new Exchange(player2, player1));
		
		PacketSendUtility.sendPacket(player2, new SM_EXCHANGE_REQUEST(player1.getName()));
		PacketSendUtility.sendPacket(player1, new SM_EXCHANGE_REQUEST(player2.getName()));
	}
	
	/**
	 * Checks if both players are allowed to trade.<br>
	 * This method uses {@code canTrade} for each participant.
	 * @param player1 The first player to check.
	 * @param player2 The second player to check.
	 * @return {@code true} if both players can trade, {@code false} otherwise.
	 */
	private boolean validateParticipants(Player player1, Player player2)
	{
		return RestrictionsManager.canTrade(player1) && RestrictionsManager.canTrade(player2);
	}
	
	/**
	 * Retrieves the partner player for a given {@code Player}.<br>
	 * It looks up the active {@link Exchange} associated with the player.<br>
	 * If no exchange exists, it returns {@code null}.
	 * @param player The {@code Player} to check for an active exchange.
	 * @return The partner {@code Player} or {@code null} if none is found.
	 */
	private Player getCurrentParter(Player player)
	{
		final Exchange exchange = exchanges.get(player.getObjectId());
		return exchange != null ? exchange.getTargetPlayer() : null;
	}
	
	/**
	 * Retrieves the {@link Exchange} object associated with a specific player.<br>
	 * It uses the unique object ID of the {@code player} to look up the trade.
	 * @param player The {@code Player} whose current exchange is being requested.
	 * @return The {@code Exchange} instance for the player, or {@code null} if none exists.
	 */
	private Exchange getCurrentExchange(Player player)
	{
		return exchanges.get(player.getObjectId());
	}
	
	/**
	 * Retrieves the {@link Exchange} object for the current trading partner.<br>
	 * This method finds the partner of the given {@code player}.<br>
	 * It then returns the exchange associated with that partner.<br>
	 * If no partner exists, it returns {@code null}.
	 * @param player The {@code Player} whose partner's exchange is being requested.
	 * @return The {@link Exchange} of the trading partner or {@code null} if none exists.
	 */
	public Exchange getCurrentParnterExchange(Player player)
	{
		final Player partner = getCurrentParter(player);
		return partner != null ? getCurrentExchange(partner) : null;
	}
	
	/**
	 * Checks if a specific player is currently participating in an exchange.<br>
	 * This method calls {@code getCurrentExchange} to verify the status.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player is in an exchange, otherwise {@code false}.
	 */
	public boolean isPlayerInExchange(Player player)
	{
		return getCurrentExchange(player) != null;
	}
	
	/**
	 * Adds a specific amount of Kinah to the current exchange.<br>
	 * This method validates if the player has enough funds available.<br>
	 * It updates both players and the {@link Exchange} object.
	 * @param activePlayer The {@code Player} who is initiating the addition.
	 * @param itemCount The amount of Kinah to be added to the trade.
	 */
	public void addKinah(Player activePlayer, long itemCount)
	{
		final Exchange currentExchange = getCurrentExchange(activePlayer);
		if ((currentExchange == null) || currentExchange.isLocked() || (itemCount < 1))
		{
			return;
		}
		
		// count total amount in inventory
		long availableCount = activePlayer.getInventory().getKinah();
		
		// count amount that was already added to exchange
		availableCount -= currentExchange.getKinahCount();
		
		final long countToAdd = availableCount > itemCount ? itemCount : availableCount;
		
		if (countToAdd > 0)
		{
			final Player partner = getCurrentParter(activePlayer);
			PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_ADD_KINAH(countToAdd, 0));
			PacketSendUtility.sendPacket(partner, new SM_EXCHANGE_ADD_KINAH(countToAdd, 1));
			currentExchange.addKinah(countToAdd);
			if (LoggingConfig.LOG_PLAYER_EXCHANGE)
			{
				log.info("[PLAYER EXCHANGE] > [Player: " + activePlayer.getName() + "] exchanged [Item: 182400001" + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "] [Item Name: Kinah]" : "]") + " [Count: " + countToAdd + "] with [Partner: " + partner.getName() + "]");
			}
		}
	}
	
	/**
	 * Adds a specific item to the current trade between two players.<br>
	 * This method validates if the item is tradeable and if the exchange list has space.<br>
	 * It updates the {@link Exchange} object and sends packets to both participants.
	 * @param activePlayer The player attempting to add the item.
	 * @param itemObjId The unique identifier of the item to be added.
	 * @param itemCount The quantity of the item to add.
	 */
	public void addItem(Player activePlayer, int itemObjId, long itemCount)
	{
		final Item item = activePlayer.getInventory().getItemByObjId(itemObjId);
		if (item == null)
		{
			return;
		}
		
		final Player partner = getCurrentParter(activePlayer);
		if (partner == null)
		{
			return;
		}
		
		if (!TemporaryTradeTimeTask.getInstance().canTrade(item, partner.getObjectId()))
		{
			if (!item.isTradeable(activePlayer) || (!item.isTradeable(activePlayer) && ((item.getPackCount() <= item.getItemTemplate().getPackCount()) && !item.isPacked())))
			{
				return;
			}
		}
		
		if ((itemCount < 1) || (itemCount > item.getItemCount()))
		{
			return;
		}
		
		final Exchange currentExchange = getCurrentExchange(activePlayer);
		
		if ((currentExchange == null) || currentExchange.isLocked() || currentExchange.isExchangeListFull() || !AdminService.getInstance().canOperate(activePlayer, partner, item, "trade"))
		{
			return;
		}
		
		ExchangeItem exchangeItem = currentExchange.getItems().get(item.getObjectId());
		
		long actuallAddCount = 0;
		
		// item was not added previosly
		if (exchangeItem == null)
		{
			Item newItem = null;
			if (itemCount < item.getItemCount())
			{
				newItem = ItemFactory.newItem(item.getItemId(), itemCount);
			}
			else
			{
				newItem = item;
			}
			
			exchangeItem = new ExchangeItem(itemObjId, itemCount, newItem);
			currentExchange.addItem(itemObjId, exchangeItem);
			actuallAddCount = itemCount;
		} // item was already added
		else
		{
			// If a player adds an item count exceeding the maximum limit, it may occur due to exploits.
			if (item.getItemCount() == exchangeItem.getItemCount())
			{
				return;
			}
			
			final long possibleToAdd = item.getItemCount() - exchangeItem.getItemCount();
			actuallAddCount = itemCount > possibleToAdd ? possibleToAdd : itemCount;
			exchangeItem.addCount(actuallAddCount);
		}
		
		PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_ADD_ITEM(0, exchangeItem.getItem(), activePlayer));
		PacketSendUtility.sendPacket(partner, new SM_EXCHANGE_ADD_ITEM(1, exchangeItem.getItem(), partner));
		
		final Item exchangedItem = exchangeItem.getItem();
		
		if (LoggingConfig.LOG_PLAYER_EXCHANGE)
		{
			log.info("[PLAYER EXCHANGE] > [Player: " + activePlayer.getName() + "] exchanged [Item: " + exchangedItem.getItemId() + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "] [Item Name: " + exchangedItem.getItemName() + "]" : "]") + " [Count: " + exchangeItem.getItemCount() + " with [Partner: " + partner.getName() + "]");
		}
	}
	
	/**
	 * Locks the current exchange for the specified player.<br>
	 * This method secures the trade and notifies the partner.
	 * @param activePlayer The {@code Player} who is initiating the lock.
	 */
	public void lockExchange(Player activePlayer)
	{
		final Exchange exchange = getCurrentExchange(activePlayer);
		if (exchange != null)
		{
			exchange.lock();
			final Player currentParter = getCurrentParter(activePlayer);
			PacketSendUtility.sendPacket(currentParter, new SM_EXCHANGE_CONFIRMATION(3));
		}
	}
	
	/**
	 * Cancels the current exchange for a specific player.<br>
	 * This method cleans up the trade data and notifies the partner.
	 * @param activePlayer The {@code Player} who is initiating the cancellation.
	 */
	public void cancelExchange(Player activePlayer)
	{
		final Player currentParter = getCurrentParter(activePlayer);
		cleanupExchanges(activePlayer, currentParter);
		if (currentParter != null)
		{
			PacketSendUtility.sendPacket(currentParter, new SM_EXCHANGE_CONFIRMATION(1));
		}
	}
	
	/**
	 * Finalizes the trade for the specified player.<br>
	 * This method checks if the {@code activePlayer} is online and has an active exchange.<br>
	 * It confirms the exchange and notifies the trading partner.<br>
	 * If both players have confirmed, it executes the final trade logic.
	 * @param activePlayer The {@link Player} who is initiating the confirmation.
	 */
	public void confirmExchange(Player activePlayer)
	{
		if ((activePlayer == null) || !activePlayer.isOnline())
		{
			return;
		}
		
		final Exchange currentExchange = getCurrentExchange(activePlayer);
		
		// TODO: Why is exchange null =/
		if (currentExchange == null)
		{
			return;
		}
		
		currentExchange.confirm();
		
		final Player currentPartner = getCurrentParter(activePlayer);
		PacketSendUtility.sendPacket(currentPartner, new SM_EXCHANGE_CONFIRMATION(2));
		
		if (getCurrentExchange(currentPartner).isConfirmed())
		{
			performTrade(activePlayer, currentPartner);
		}
	}
	
	/**
	 * Executes the final trade logic between two players.<br>
	 * This method validates the exchange and moves items between inventories.<br>
	 * It also handles packet delivery and saves the transaction results.
	 * @param activePlayer The player who initiated or is currently interacting with the trade.
	 * @param currentPartner The other player involved in the trade.
	 */
	private void performTrade(Player activePlayer, Player currentPartner)
	{
		// TODO: Message here; release item ID if returned.
		if (!validateExchange(activePlayer, currentPartner))
		{
			cleanupExchanges(activePlayer, currentPartner);
			return;
		}
		
		final Exchange exchange1 = getCurrentExchange(activePlayer);
		final Exchange exchange2 = getCurrentExchange(currentPartner);
		
		cleanupExchanges(activePlayer, currentPartner);
		
		if (!removeItemsFromInventory(activePlayer, exchange1) || !removeItemsFromInventory(currentPartner, exchange2))
		{
			AuditLogger.info(activePlayer, "Exchange kinah exploit partner: " + currentPartner.getName());
			return;
		}
		
		PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_CONFIRMATION(0));
		PacketSendUtility.sendPacket(currentPartner, new SM_EXCHANGE_CONFIRMATION(0));
		
		putItemToInventory(currentPartner, exchange1, exchange2);
		putItemToInventory(activePlayer, exchange2, exchange1);
		
		saveManager.add(new ExchangeOpSaveTask(exchange1.getActiveplayer().getObjectId(), exchange2.getActiveplayer().getObjectId(), exchange1.getItemsToUpdate(), exchange2.getItemsToUpdate()));
	}
	
	/**
	 * Removes the exchange session for both players.<br>
	 * This method updates the trading status to {@code false}.<br>
	 * It cleans up entries in the internal exchanges map.
	 * @param activePlayer The first player involved in the trade.
	 * @param currentPartner The second player involved in the trade.
	 */
	private void cleanupExchanges(Player activePlayer, Player currentPartner)
	{
		if (activePlayer != null)
		{
			exchanges.remove(activePlayer.getObjectId());
			activePlayer.setTrading(false);
		}
		
		if (currentPartner != null)
		{
			exchanges.remove(currentPartner.getObjectId());
			currentPartner.setTrading(false);
		}
	}
	
	/**
	 * Removes items and Kinah from the player's inventory based on an {@code Exchange}.<br>
	 * This method validates that all required items exist before processing.<br>
	 * It updates the exchange state and sends deletion packets to the client.
	 * @param player The {@link Player} whose inventory will be modified.
	 * @param exchange The {@link Exchange} object containing the items to remove.
	 * @return {@code true} if all items were successfully removed, {@code false} otherwise.
	 */
	private boolean removeItemsFromInventory(Player player, Exchange exchange)
	{
		final Storage inventory = player.getInventory();
		
		for (ExchangeItem exchangeItem : exchange.getItems().values())
		{
			final Item item = exchangeItem.getItem();
			final Item itemInInventory = inventory.getItemByObjId(exchangeItem.getItemObjId());
			if (itemInInventory == null)
			{
				AuditLogger.info(player, "Try to trade unexisting item.");
				return false;
			}
			
			final long itemCount = exchangeItem.getItemCount();
			
			if (itemCount < itemInInventory.getItemCount())
			{
				inventory.decreaseItemCount(itemInInventory, itemCount);
				exchange.addItemToUpdate(itemInInventory);
			}
			else
			{
				// remove from source inventory only
				inventory.remove(itemInInventory);
				exchangeItem.setItem(itemInInventory);
				
				// release when only part stack was added in the beginning -> full stack in the end
				if (item.getObjectId() != exchangeItem.getItemObjId())
				{
					ItemService.releaseItemId(item);
				}
				
				PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(itemInInventory.getObjectId()));
			}
		}
		
		if (!player.getInventory().tryDecreaseKinah(exchange.getKinahCount()))
		{
			return false;
		}
		
		exchange.addItemToUpdate(player.getInventory().getKinahItem());
		return true;
	}
	
	/**
	 * Checks if the current trade is valid for both players.<br>
	 * It verifies that each player has enough inventory space to receive items from the other.
	 * @param activePlayer The player who initiated the action.
	 * @param currentPartner The player currently trading with the active player.
	 * @return {@code true} if both players have sufficient space, otherwise {@code false}.
	 */
	private boolean validateExchange(Player activePlayer, Player currentPartner)
	{
		final Exchange exchange1 = getCurrentExchange(activePlayer);
		final Exchange exchange2 = getCurrentExchange(currentPartner);
		
		return validateInventorySize(activePlayer, exchange2) && validateInventorySize(currentPartner, exchange1);
	}
	
	/**
	 * Checks if the {@code activePlayer} has enough space in their inventory.<br>
	 * It compares free slots to the number of items in the {@code exchange}.
	 * @param activePlayer The player whose inventory is being checked.
	 * @param exchange The trade object containing the items to be received.
	 * @return {@code true} if there is enough space, otherwise {@code false}.
	 */
	private boolean validateInventorySize(Player activePlayer, Exchange exchange)
	{
		final int numberOfFreeSlots = activePlayer.getInventory().getFreeSlots();
		return numberOfFreeSlots >= exchange.getItems().size();
	}
	
	/**
	 * Transfers items and kinah from one exchange to a player's inventory.<br>
	 * This method updates the inventory of the {@code Player}.<br>
	 * It also marks the items as updated in the target {@code Exchange}.
	 * @param player The {@code Player} who will receive the items.
	 * @param exchange1 The source {@code Exchange} containing the items to be moved.
	 * @param exchange2 The destination {@code Exchange} that needs to be updated.
	 */
	private void putItemToInventory(Player player, Exchange exchange1, Exchange exchange2)
	{
		for (ExchangeItem exchangeItem : exchange1.getItems().values())
		{
			final Item itemToPut = exchangeItem.getItem();
			itemToPut.setEquipmentSlot(0);
			player.getInventory().add(itemToPut);
			exchange2.addItemToUpdate(itemToPut);
		}
		
		final long kinahToExchange = exchange1.getKinahCount();
		if (kinahToExchange > 0)
		{
			player.getInventory().increaseKinah(exchange1.getKinahCount());
			exchange2.addItemToUpdate(player.getInventory().getKinahItem());
		}
	}
	
	/**
	 * Frequent running save task
	 */
	public static final class ExchangePeriodicTaskManager extends AbstractFIFOPeriodicTaskManager<ExchangeOpSaveTask>
	{
		private static final String CALLED_METHOD_NAME = "exchangeOperation()";
		
		/**
		 * @param period
		 */
		public ExchangePeriodicTaskManager(int period)
		{
			super(period);
		}
		
		@Override
		protected void callTask(ExchangeOpSaveTask task)
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
	 * This class is used for storing all items in one shot after any exchange operation
	 */
	public static final class ExchangeOpSaveTask implements Runnable
	{
		private final int player1Id;
		private final int player2Id;
		private final List<Item> player1Items;
		private final List<Item> player2Items;
		
		/**
		 * @param player1Id
		 * @param player2Id
		 * @param player1Items
		 * @param player2Items
		 */
		public ExchangeOpSaveTask(int player1Id, int player2Id, List<Item> player1Items, List<Item> player2Items)
		{
			this.player1Id = player1Id;
			this.player2Id = player2Id;
			this.player1Items = player1Items;
			this.player2Items = player2Items;
		}
		
		@Override
		public void run()
		{
			DAOManager.getDAO(InventoryDAO.class).store(player1Items, player1Id);
			DAOManager.getDAO(InventoryDAO.class).store(player2Items, player2Id);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final ExchangeService instance = new ExchangeService();
	}
}
