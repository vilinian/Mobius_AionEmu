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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.dao.SurveyControllerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.survey.SurveyItem;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * This service manages the logic for in-game surveys.<br>
 * It handles survey item interactions and processes player responses.<br>
 * Use this class to coordinate between {@link ItemTemplate} data and user feedback.
 * @author KID
 */
public class SurveyService
{
	private static final Logger log = LoggerFactory.getLogger(SurveyService.class);
	private final Map<Integer, SurveyItem> activeItems;
	private final String htmlTemplate;
	
	/**
	 * Checks if a specific survey is currently active.<br>
	 * This method verifies the existence of the {@code survId} in the active items list.<br>
	 * If it is active, it triggers a request for the {@link Player}.
	 * @param player The {@code Player} who is interacting with the survey.
	 * @param survId The unique identifier for the survey.
	 * @return {@code true} if the survey is active, otherwise {@code false}.
	 */
	public boolean isActive(Player player, int survId)
	{
		final boolean avail = activeItems.containsKey(survId);
		if (avail)
		{
			requestSurvey(player, survId);
		}
		
		return avail;
	}
	
	/**
	 * Initializes the {@code SurveyService}.<br>
	 * It sets up the active items map.<br>
	 * It loads the survey HTML template from {@link HTMLCache}.<br>
	 * It schedules a recurring task for updates.
	 */
	public SurveyService()
	{
		activeItems = new ConcurrentHashMap<>();
		htmlTemplate = HTMLCache.getInstance().getHTML("surveyTemplate.xhtml");
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new TaskUpdate(), 2000, SecurityConfig.SURVEY_DELAY * 60000);
	}
	
	/**
	 * Requests a survey reward for a specific player.<br>
	 * This method checks if the survey is active and belongs to the {@code Player}.<br>
	 * It verifies inventory space before granting the item reward.
	 * @param player The {@code Player} who is requesting the reward.
	 * @param survId The unique identifier for the survey.
	 */
	public void requestSurvey(Player player, int survId)
	{
		final SurveyItem item = activeItems.get(survId);
		if (item == null)
		{
			// There is no survey underway.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300684));
			return;
		}
		
		if (item.ownerId != player.getObjectId())
		{
			// There is no remaining survey to take part in.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300037));
			return;
		}
		
		ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(item.itemId);
		if (template == null)
		{
			return;
		}
		
		if (player.getInventory().isFull(template.getExtraInventoryId()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
			log.warn("[SurveyController] player " + player.getName() + " tried to receive item with full inventory.");
			return;
		}
		
		if (DAOManager.getDAO(SurveyControllerDAO.class).useItem(item.uniqueId))
		{
			ItemService.addItem(player, item.itemId, item.count);
			if (item.itemId == ItemId.KINAH.value()) // You received %num0 Kinah as reward for the survey.
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300945, item.count));
			}
			else if (item.count == 1) // You received %0 item as reward for the survey.
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300945, new DescriptionId(template.getNameId())));
			}
			else // You received %num1 %0 items as reward for the survey.
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300946, item.count, new DescriptionId(template.getNameId())));
			}
			
			template = null;
			activeItems.remove(survId);
		}
	}
	
	/**
	 * Updates the list of active survey items from the database.<br>
	 * It identifies players with new surveys and notifies them.<br>
	 * This method calls {@code showAvailable} for each affected player.
	 */
	public void taskUpdate()
	{
		final List<SurveyItem> newList = DAOManager.getDAO(SurveyControllerDAO.class).getAllNew();
		if (newList.size() == 0)
		{
			return;
		}
		
		final List<Integer> players = new ArrayList<>();
		int cnt = 0;
		for (SurveyItem item : newList)
		{
			activeItems.put(item.uniqueId, item);
			cnt++;
			if (!players.contains(item.ownerId))
			{
				players.add(item.ownerId);
			}
		}
		
		log.info("[SurveyController] found new " + cnt + " items for " + players.size() + " players.");
		for (int ownerId : players)
		{
			final Player player = World.getInstance().findPlayer(ownerId);
			if (player != null)
			{
				showAvailable(player);
			}
		}
	}
	
	/**
	 * Displays the available survey items to a specific player.<br>
	 * This method checks all active surveys for ownership by the {@code player}.<br>
	 * It sends the formatted HTML data to the client using {@code sendData}.
	 * @param player The {@code Player} who will receive the survey information.
	 */
	public void showAvailable(Player player)
	{
		for (SurveyItem item : activeItems.values())
		{
			if (item.ownerId != player.getObjectId())
			{
				continue;
			}
			
			String context = htmlTemplate;
			context = context.replace("%itemid%", item.itemId + "");
			context = context.replace("%itemcount%", item.count + "");
			context = context.replace("%html%", item.html);
			context = context.replace("%radio%", item.radio);
			
			HTMLService.sendData(player, item.uniqueId, context);
		}
	}
	
	public class TaskUpdate implements Runnable
	{
		@Override
		public void run()
		{
			log.info("[SurveyController] update task start.");
			taskUpdate();
		}
	}
	
	private static class SingletonHolder
	{
		protected static final SurveyService instance = new SurveyService();
	}
	
	/**
	 * Provides the global instance of the {@link SurveyService}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the survey system from anywhere in the code.
	 * @return The single shared instance of {@code SurveyService}.
	 */
	public static SurveyService getInstance()
	{
		return SingletonHolder.instance;
	}
}
