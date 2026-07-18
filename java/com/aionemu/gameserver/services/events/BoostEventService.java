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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.templates.event.BoostEvents;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BOOST_EVENTS;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service manages the logic for in-game boost events.<br>
 * It handles the application and tracking of various {@link BoostEvents} for players.<br>
 * It allows the server to provide temporary stat modifications during specific game activities.
 */
public class BoostEventService implements StatOwner
{
	// private static BoostEventBonus bonus;
	private static final Logger log = LoggerFactory.getLogger(BoostEventService.class);
	public Map<Integer, BoostEvents> data;
	public HashMap<Integer, BoostEvents> activeEvents = new HashMap<>();
	
	/**
	 * Creates a new instance of the {@link BoostEventService}.<br>
	 * This constructor initializes the {@code data} map.
	 */
	public BoostEventService()
	{
		data = new HashMap<>(1);
	}
	
	/**
	 * Initializes the boost event data.<br>
	 * This method loads all events from {@link DataManager}.<br>
	 * It populates the internal event maps when the service starts.
	 */
	public void onStart()
	{
		final Map<Integer, BoostEvents> all = DataManager.BOOST_EVENT_DATA.getAll();
		if (all.size() != 0)
		{
			getBoostEvent(all);
		}
	}
	
	/**
	 * Sends the current boost events to a specific player.<br>
	 * This method uses {@link PacketSendUtility} to deliver the data.<br>
	 * It retrieves the active boosts via {@code getCurrentBoost}.
	 * @param player The {@code Player} object that will receive the packet.
	 */
	public void sendPacket(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_BOOST_EVENTS((HashMap<Integer, BoostEvents>) getCurrentBoost()));
	}
	
	/**
	 * Retrieves the list of currently active boost events.<br>
	 * This method checks all events in {@code data}.<br>
	 * It filters for events where the current time is between the start and end dates.
	 * @return A {@code Map} containing the IDs and details of active {@link BoostEvents}.
	 */
	private Map<Integer, BoostEvents> getCurrentBoost()
	{
		for (BoostEvents boostEvents : data.values())
		{
			if (boostEvents.getStartDate().isBefore(java.time.ZonedDateTime.now()) && boostEvents.getEndDate().isAfter(java.time.ZonedDateTime.now()))
			{
				activeEvents.put(boostEvents.getId(), boostEvents);
			}
		}
		
		return activeEvents;
	}
	
	/**
	 * Retrieves or adds a specific boost event to the data map.<br>
	 * This method checks if the {@code eventId} already exists in the {@code data} collection.<br>
	 * If it is missing, it adds the provided {@code boostEvents} object.
	 * @param eventId The unique identifier for the boost event.
	 * @param boostEvents The {@link BoostEvents} object to be stored.
	 */
	private void getBoostEvent(int eventId, BoostEvents boostEvents)
	{
		if (data.containsKey(eventId))
		{
			return;
		}
		
		data.put(eventId, boostEvents);
	}
	
	/**
	 * This method populates the internal data map.<br>
	 * It iterates through all {@code BoostEvents} to initialize them.<br>
	 * It logs a message when the initialization is complete.
	 * @param map The source map containing {@code BoostEvents} to be added.
	 */
	private void getBoostEvent(Map<Integer, BoostEvents> map)
	{
		data.putAll(map);
		for (BoostEvents boostEvents : data.values())
		{
			getBoostEvent(boostEvents.getId(), boostEvents);
		}
		
		log.info("[BoostEventService] BoostEventService initialized");
	}
	
	/**
	 * Retrieves the singleton instance of the {@link BoostEventService}.<br>
	 * Use this method to access the global service for boost events.
	 * @return The single shared instance of {@code BoostEventService}.
	 */
	public static BoostEventService getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final BoostEventService INSTANCE = new BoostEventService();
	}
}
