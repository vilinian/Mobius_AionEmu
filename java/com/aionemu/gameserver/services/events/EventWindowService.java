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
package com.aionemu.gameserver.services.events;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerEventsWindowDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.event.EventsWindow;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EVENT_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EVENT_WINDOW_ITEMS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Manages the logic for displaying and updating event windows to players.<br>
 * This service handles sending {@code SM_EVENT_WINDOW} packets and managing associated items.<br>
 * It interacts with {@link PlayerEventsWindowDAO} to persist window data.
 * @author Ghostfur (Aion-Unique)
 * @rework FrozenKiller
 */
public class EventWindowService
{
	private static final Logger log = LoggerFactory.getLogger(EventWindowService.class);
	private final Map<Integer, EventsWindow> allEvents = DataManager.EVENTS_WINDOW.getAllEvents();
	private final HashMap<Integer, EventsWindow> activeEvents = new HashMap<>();
	private final HashMap<Integer, EventsWindow> activeEventsForPlayer = new HashMap<>();
	private final Map<Integer, EventsWindow> sendActiveEventsForPlayer = new ConcurrentHashMap<>();
	private long tStart = 0; // Start Time.
	private long tEnd = 0; // End Time.
	
	/**
	 * Prepares the event window data for use.<br>
	 * This method logs the start and end times of all loaded events.<br>
	 * It checks if {@code allEvents} contains any data before processing.
	 */
	public void initialize()
	{
		if (allEvents.size() != 0)
		{
			for (EventsWindow eventsWindow : allEvents.values())
			{
				log.info("[EventWindowService] Start " + eventsWindow.getPeriodStart() + " End " + eventsWindow.getPeriodEnd());
			}
		}
	}
	
	/**
	 * Retrieves all currently active {@link EventsWindow} objects for a specific player.<br>
	 * This method filters events based on the current time and the {@code Player} level requirements.
	 * @param player The {@code Player} object to check for eligible events.
	 * @return A {@code Map} containing the IDs and corresponding {@link EventsWindow} objects.
	 */
	public Map<Integer, EventsWindow> getActiveEvents(Player player)
	{
		for (EventsWindow eventsWindow : allEvents.values())
		{
			if (activeEvents.containsKey(eventsWindow.getId()) || !eventsWindow.getPeriodStart().isBefore(java.time.ZonedDateTime.now()) || !eventsWindow.getPeriodEnd().isAfter(java.time.ZonedDateTime.now()))
			{
				continue;
			}
			
			if ((player.getLevel() >= eventsWindow.getMinLevel()) && (player.getLevel() <= eventsWindow.getMaxLevel()))
			{
				activeEventsForPlayer.put(eventsWindow.getId(), eventsWindow);
				log.info("[EventWindowService] Start " + eventsWindow.getPeriodStart() + " End " + eventsWindow.getPeriodEnd());
			}
		}
		
		return activeEventsForPlayer;
	}
	
	/**
	 * Handles the initialization of event windows for a player when they log in.<br>
	 * This method retrieves active events and updates their progress in the database.<br>
	 * It schedules rewards and sends the necessary packets to the {@code Player}.
	 * @param player The {@code Player} object who is currently logging into the game.
	 */
	public void onLogin(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		getActiveEvents(player);
		final int accountId = player.getPlayerAccount().getId();
		final PlayerEventsWindowDAO playerEventsWindowDAO = DAOManager.getDAO(PlayerEventsWindowDAO.class);
		for (EventsWindow eventsWindow : activeEventsForPlayer.values())
		{
			final int elapsed = playerEventsWindowDAO.getElapsed(accountId, eventsWindow.getId());
			final int recivedCount = playerEventsWindowDAO.getRewardRecivedCount(accountId, eventsWindow.getId());
			if (!eventsWindow.getPeriodStart().isBefore(java.time.ZonedDateTime.now()) || !eventsWindow.getPeriodEnd().isAfter(java.time.ZonedDateTime.now()))
			{
				continue;
			}
			
			sendActiveEventsForPlayer.put(eventsWindow.getId(), eventsWindow);
			if (!playerEventsWindowDAO.getEventsWindow(accountId).contains(eventsWindow.getId()))
			{
				playerEventsWindowDAO.insert(accountId, eventsWindow.getId(), new Timestamp(System.currentTimeMillis()));
			}
			else
			{
				playerEventsWindowDAO.store(accountId, eventsWindow.getId(), new Timestamp(System.currentTimeMillis()), elapsed); // Temp for updating TiemStamp
			}
			
			log.info("Start counting id " + eventsWindow.getId() + " time " + eventsWindow.getRemainingTime() + " minute(s)");
			ThreadPoolManager.getInstance().schedule(() ->
			{
				if (player.isOnline())
				{
					if (recivedCount == eventsWindow.getMaxCountOfDay())
					{
						sendActiveEventsForPlayer.remove(eventsWindow.getId());
						return;
					}
					
					playerEventsWindowDAO.setRewardRecivedCount(accountId, eventsWindow.getId(), (recivedCount + 1)); // It also Set elapsed to 0 and updates TimeStamp (MySQL5PlayerEventsWindowDAO)
					final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(eventsWindow.getItemId());
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GET_HCOIN_07(itemTemplate.getNameId()));
					ItemService.addItem(player, eventsWindow.getItemId(), eventsWindow.getCount());
					restartTimer(player, eventsWindow.getId());
					PacketSendUtility.sendPacket(player, new SM_EVENT_WINDOW_ITEMS(sendActiveEventsForPlayer.values()));
				}
			}, (eventsWindow.getRemainingTime() - elapsed) * 60000);
		}
		
		PacketSendUtility.sendPacket(player, new SM_EVENT_WINDOW_ITEMS(sendActiveEventsForPlayer.values()));
		PacketSendUtility.sendPacket(player, new SM_EVENT_WINDOW(1, sendActiveEventsForPlayer.size()));
	}
	
	/**
	 * This method restarts the timer for a specific event.<br>
	 * It checks if the {@code player} is online and has not reached the maximum reward count.<br>
	 * If valid, it updates the database and schedules the next reward delivery.
	 * @param player The {@code Player} who will receive the reward.
	 * @param eventId The unique identifier for the event to restart.
	 */
	public void restartTimer(Player player, int eventId)
	{
		final int accountId = player.getPlayerAccount().getId();
		final PlayerEventsWindowDAO playerEventsWindowDAO = DAOManager.getDAO(PlayerEventsWindowDAO.class);
		final int recivedCount = playerEventsWindowDAO.getRewardRecivedCount(accountId, eventId);
		for (EventsWindow eventsWindow : sendActiveEventsForPlayer.values())
		{
			if (!eventsWindow.getPeriodStart().isBefore(java.time.ZonedDateTime.now()) || !eventsWindow.getPeriodEnd().isAfter(java.time.ZonedDateTime.now()))
			{
				continue;
			}
			
			if (eventsWindow.getId() == eventId)
			{
				ThreadPoolManager.getInstance().schedule(() ->
				{
					if (player.isOnline())
					{
						if (recivedCount == eventsWindow.getMaxCountOfDay())
						{
							sendActiveEventsForPlayer.remove(eventsWindow.getId());
							return;
						}
						
						playerEventsWindowDAO.setRewardRecivedCount(accountId, eventsWindow.getId(), (recivedCount + 1));
						final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(eventsWindow.getItemId());
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GET_HCOIN_07(itemTemplate.getNameId()));
						ItemService.addItem(player, eventsWindow.getItemId(), eventsWindow.getCount());
						restartTimer(player, eventId);
					}
				}, eventsWindow.getRemainingTime() * 60000);
			}
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out.<br>
	 * It updates the elapsed time for all active events in the database.<br>
	 * This ensures that event progress is saved before the player disconnects.
	 * @param player The {@code Player} who is logging out.
	 */
	public void onLogout(Player player)
	{
		final int accountId = player.getPlayerAccount().getId();
		final PlayerEventsWindowDAO playerEventsWindowDAO = DAOManager.getDAO(PlayerEventsWindowDAO.class);
		for (EventsWindow eventsWindow : activeEventsForPlayer.values())
		{
			if (playerEventsWindowDAO.getEventsWindow(accountId).contains(eventsWindow.getId()) && player.isOnline())
			{
				tStart = (playerEventsWindowDAO.getLastStamp(accountId, eventsWindow.getId()).getTime() / 1000);
				tEnd = (System.currentTimeMillis() / 1000);
				final int d2 = playerEventsWindowDAO.getElapsed(accountId, eventsWindow.getId());
				final int time = (int) (((tEnd - tStart) / 60) + d2);
				playerEventsWindowDAO.updateElapsed(accountId, eventsWindow.getId(), time);
			}
		}
		
		activeEventsForPlayer.clear();
		sendActiveEventsForPlayer.clear();
	}
	
	private static class SingletonHolder
	{
		protected static final EventWindowService instance = new EventWindowService();
	}
	
	/**
	 * Retrieves the single instance of the {@link EventWindowService}.<br>
	 * This method follows the singleton pattern.
	 * @return The global {@code EventWindowService} instance.
	 */
	public static EventWindowService getInstance()
	{
		return SingletonHolder.instance;
	}
}
