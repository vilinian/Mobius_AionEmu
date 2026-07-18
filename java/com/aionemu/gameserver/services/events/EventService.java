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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Future;

import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.dao.EventItemsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EventType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.event.EventTemplate;
import com.aionemu.gameserver.model.templates.quest.XMLStartCondition;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Manages the lifecycle and execution of in-game events.<br>
 * This service handles event scheduling, data retrieval, and synchronization with {@link World}.<br>
 * It ensures that all active events are processed correctly according to the server configuration.
 * @author Rolandas
 */
public class EventService
{
	Logger log = LoggerFactory.getLogger(EventService.class);
	private boolean isStarted = false;
	private Future<?> checkTask = null;
	private final List<EventTemplate> activeEvents;
	TIntObjectHashMap<List<EventTemplate>> eventsForStartQuest = new TIntObjectHashMap<>();
	TIntObjectHashMap<List<EventTemplate>> eventsForMaintainQuest = new TIntObjectHashMap<>();
	
	private static class SingletonHolder
	{
		protected static final EventService instance = new EventService();
	}
	
	/**
	 * Provides access to the singleton instance of {@link EventService}.<br>
	 * Use this method to get the global service for managing game events.
	 * @return The single shared instance of {@code EventService}.
	 */
	public static EventService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link EventService} class.<br>
	 * This constructor initializes the active events list from {@code DataManager}.<br>
	 * It also triggers the initial update of the quest map.
	 */
	private EventService()
	{
		activeEvents = Collections.synchronizedList(DataManager.EVENT_DATA.getActiveEvents());
		updateQuestMap();
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It handles any initialization logic required for the player's session.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		final List<Integer> activeStartQuests = new ArrayList<>();
		final List<Integer> activeMaintainQuests = new ArrayList<>();
		TIntObjectHashMap<List<EventTemplate>> map1 = null;
		TIntObjectHashMap<List<EventTemplate>> map2 = null;
		
		synchronized (activeEvents)
		{
			for (EventTemplate et : activeEvents)
			{
				if (et.isActive())
				{
					activeStartQuests.addAll(et.getStartableQuests());
					activeMaintainQuests.addAll(et.getMaintainableQuests());
				}
			}
			
			map1 = new TIntObjectHashMap<>(eventsForStartQuest);
			map2 = new TIntObjectHashMap<>(eventsForMaintainQuest);
		}
		
		StartOrMaintainQuests(player, activeStartQuests.listIterator(), map1, true);
		StartOrMaintainQuests(player, activeMaintainQuests.listIterator(), map2, false);
		
		activeStartQuests.clear();
		activeMaintainQuests.clear();
		map1.clear();
		map2.clear();
	}
	
	/**
	 * Processes quests for a player to either start them or maintain their active state.<br>
	 * This method checks requirements like level, race, class, and gender before updating quest statuses.<br>
	 * It handles recurring events by resetting completed quests if the event start date is valid.
	 * @param player The {@link Player} object for whom the quests are being processed.
	 * @param questList A {@link ListIterator} containing the IDs of the quests to check.
	 * @param templateMap A {@link TIntObjectHashMap} mapping quest IDs to their associated {@link EventTemplate} lists.
	 * @param start A boolean flag indicating whether to initiate new quests or maintain existing ones.
	 */
	void StartOrMaintainQuests(Player player, ListIterator<Integer> questList, TIntObjectHashMap<List<EventTemplate>> templateMap, boolean start)
	{
		while (questList.hasNext())
		{
			final int questId = questList.next();
			final QuestState qs = player.getQuestStateList().getQuestState(questId);
			final QuestEnv cookie = new QuestEnv(null, player, questId, 0);
			QuestStatus status = qs == null ? QuestStatus.START : qs.getStatus();
			
			if (QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel()))
			{
				final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
				if (template.getRacePermitted() != null)
				{
					if (template.getRacePermitted().ordinal() != player.getCommonData().getRace().ordinal())
					{
						continue;
					}
				}
				
				if (template.getClassPermitted().size() != 0)
				{
					if (!template.getClassPermitted().contains(player.getCommonData().getPlayerClass()))
					{
						continue;
					}
				}
				
				if (template.getGenderPermitted() != null)
				{
					if (template.getGenderPermitted().ordinal() != player.getGender().ordinal())
					{
						continue;
					}
				}
				
				final int amountOfStartConditions = template.getXMLStartConditions().size();
				int fulfilledStartConditions = 0;
				if (amountOfStartConditions != 0)
				{
					for (XMLStartCondition startCondition : template.getXMLStartConditions())
					{
						if (startCondition.check(player, false))
						{
							fulfilledStartConditions++;
						}
					}
					
					if (fulfilledStartConditions < 1)
					{
						continue;
					}
				}
				
				if (qs != null)
				{
					if ((qs.getCompleteTime() != null) || (status == QuestStatus.COMPLETE))
					{
						ZonedDateTime completed = null;
						if (qs.getCompleteTime() == null)
						{
							completed = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(0), java.time.ZoneId.systemDefault());
						}
						else
						{
							completed = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(qs.getCompleteTime().getTime()), java.time.ZoneId.systemDefault());
						}
						
						if (templateMap.containsKey(questId))
						{
							for (EventTemplate et : templateMap.get(questId))
							{
								// recurring event, reset it
								if (et.getStartDate().isAfter(completed))
								{
									if (start)
									{
										status = QuestStatus.START;
										qs.setQuestVar(0);
										qs.setCompleteCount(0);
										qs.setStatus(status);
									}
									break;
								}
							}
						}
					}
					
					// re-register quests
					if (status == QuestStatus.COMPLETE)
					{
						PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, status, qs.getQuestVars().getQuestVars()));
					}
					else
					{
						QuestService.startEventQuest(cookie, status);
					}
				}
				else if (start)
				{
					QuestService.startEventQuest(cookie, status);
				}
			}
		}
	}
	
	/**
	 * Checks if the event has been started.<br>
	 * This method returns {@code true} if the event is currently active.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the event has started, {@code false} otherwise.
	 */
	public boolean isStarted()
	{
		return isStarted;
	}
	
	/**
	 * This method schedules a task to remove specific items from the database.<br>
	 * It identifies active events that have an inventory drop with a set clean time.<br>
	 * The {@link CronService} is used to execute the deletion at the specified interval.<br>
	 * It also removes the maximum allowed count of these items from all players in the world.
	 */
	public void startCronCleanBase()
	{
		int size = 0;
		if (activeEvents != null)
		{
			for (EventTemplate et : activeEvents)
			{
				if (et.isActive() && (et.getInventoryDrop() != null) && (et.getInventoryDrop().getCleanTime() != 0))
				{
					final int itemId = et.getInventoryDrop().getDropItem();
					CronService.getInstance().schedule(() ->
					{
						DAOManager.getDAO(EventItemsDAO.class).deleteItems(itemId);
						for (Player p : World.getInstance().getAllPlayers())
						{
							p.removeItemMaxThisCount(itemId);
						}
					}, "0 0 " + et.getInventoryDrop().getCleanTime() + " ? * *");
					size++;
				}
			}
		}
		
		log.info("[EventService] EventCron: clean limits in db " + size + " size.");
	}
	
	/**
	 * Starts the event service background tasks.<br>
	 * This method sets the {@code isStarted} flag to {@code true}.<br>
	 * It schedules a recurring task to call {@code checkEvents} every 5 minutes.<br>
	 * If the service was already running, it cancels the previous task first.
	 */
	public void start()
	{
		if (isStarted)
		{
			checkTask.cancel(false);
		}
		
		isStarted = true;
		
		final int CHECK_TIME_PERIOD = 1000 * 60 * 5;
		checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> checkEvents(), 0, CHECK_TIME_PERIOD);
	}
	
	/**
	 * Stops the active event service.<br>
	 * This method cancels the {@code checkTask} if it is currently running.<br>
	 * It sets the {@code isStarted} flag to {@code false}.<br>
	 * The {@code checkTask} reference is then set to {@code null}.
	 */
	public void stop()
	{
		if (isStarted)
		{
			checkTask.cancel(false);
		}
		
		checkTask = null;
		isStarted = false;
	}
	
	/**
	 * Periodically refreshes the list of active events.<br>
	 * It starts new events from {@code DataManager.EVENT_DATA}.<br>
	 * It stops events that are expired or no longer exist.<br>
	 * This method also clears internal maps and calls {@code updateQuestMap}.
	 */
	private void checkEvents()
	{
		final List<EventTemplate> newEvents = new ArrayList<>();
		final List<EventTemplate> allEvents = DataManager.EVENT_DATA.getAllEvents();
		
		for (EventTemplate et : allEvents)
		{
			if (et.isActive())
			{
				newEvents.add(et);
				et.Start();
			}
		}
		
		synchronized (activeEvents)
		{
			for (EventTemplate et : activeEvents)
			{
				if (et.isExpired() || !DataManager.EVENT_DATA.Contains(et.getName()))
				{
					et.Stop();
				}
			}
			
			activeEvents.clear();
			eventsForStartQuest.clear();
			eventsForMaintainQuest.clear();
			activeEvents.addAll(newEvents);
			updateQuestMap();
		}
		
		newEvents.clear();
		allEvents.clear();
	}
	
	/**
	 * Refreshes the internal mapping of quests to active events.<br>
	 * This method populates {@code eventsForStartQuest} and {@code eventsForMaintainQuest}.<br>
	 * It iterates through all items in the {@code activeEvents} list.
	 */
	private void updateQuestMap()
	{
		for (EventTemplate et : activeEvents)
		{
			for (int qId : et.getStartableQuests())
			{
				if (!eventsForStartQuest.containsKey(qId))
				{
					eventsForStartQuest.put(qId, new ArrayList<>());
				}
				
				eventsForStartQuest.get(qId).add(et);
			}
			
			for (int qId : et.getMaintainableQuests())
			{
				if (!eventsForMaintainQuest.containsKey(qId))
				{
					eventsForMaintainQuest.put(qId, new ArrayList<>());
				}
				
				eventsForMaintainQuest.get(qId).add(et);
			}
		}
	}
	
	/**
	 * Checks if a specific quest is currently active.<br>
	 * This method looks for the {@code questId} in both start and maintain quest maps.
	 * @param questId The unique identifier of the quest to check.
	 * @return {@code true} if the quest is active, otherwise {@code false}.
	 */
	public boolean checkQuestIsActive(int questId)
	{
		synchronized (activeEvents)
		{
			if (eventsForStartQuest.containsKey(questId) || eventsForMaintainQuest.containsKey(questId))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the current active event type.<br>
	 * This method checks if the {@code EventService} is enabled in the configuration.<br>
	 * It iterates through all active events to find a valid theme.<br>
	 * If no active event with a valid theme is found, it returns {@code NONE}.
	 * @return The current {@link EventType} or {@code NONE} if none are active.
	 */
	public EventType getEventType()
	{
		if (EventsConfig.ENABLE_EVENT_SERVICE)
		{
			for (EventTemplate et : activeEvents)
			{
				final String theme = et.getTheme();
				if (theme != null)
				{
					final EventType type = EventType.getEventType(theme);
					if (et.isActive() && !type.equals(EventType.NONE))
					{
						return type;
					}
				}
			}
		}
		
		return EventType.NONE;
	}
	
	/**
	 * Retrieves a list of all currently active events.<br>
	 * This method returns a copy of the internal {@code activeEvents} collection.
	 * @return A {@code List} containing all {@link EventTemplate} objects that are active.
	 */
	public List<EventTemplate> getActiveEvents()
	{
		return activeEvents;
	}
}
