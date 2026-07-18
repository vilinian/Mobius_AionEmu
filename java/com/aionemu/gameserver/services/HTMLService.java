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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dao.GuideDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.guide.Guide;
import com.aionemu.gameserver.model.templates.Guides.GuideTemplate;
import com.aionemu.gameserver.model.templates.Guides.SurveyTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTIONNAIRE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This service provides functionality to send raw {@code html} content to the game client.<br>
 * It handles the processing and delivery of formatted messages and templates.
 * @author lhw, xTz
 */
public class HTMLService
{
	private static final Logger log = LoggerFactory.getLogger("ITEM_HTML_LOG");
	
	/**
	 * Converts a {@link GuideTemplate} object into an HTML string.<br>
	 * This method retrieves the base template from {@link HTMLCache}.<br>
	 * It replaces placeholders with data from the provided template.
	 * @param template The {@code GuideTemplate} containing the data to be injected.
	 * @return The final formatted HTML string.
	 */
	public static String getHTMLTemplate(GuideTemplate template)
	{
		String context = HTMLCache.getInstance().getHTML("guideTemplate.xhtml");
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<reward_items multi_count='").append(template.getRewardCount()).append("'>\n");
		for (SurveyTemplate survey : template.getSurveys())
		{
			sb.append("<item_id count='").append(survey.getCount()).append("'>").append(survey.getItemId()).append("</item_id>\n");
		}
		sb.append("</reward_items>\n");
		context = context.replace("%reward%", sb);
		context = context.replace("%radio%", template.getSelect().isEmpty() ? " " : template.getSelect());
		context = context.replace("%html%", template.getMessage().isEmpty() ? " " : template.getMessage());
		context = context.replace("%rewardInfo%", template.getRewardInfo().isEmpty() ? " " : template.getRewardInfo());
		return context;
	}
	
	/**
	 * Sends a survey to all players in the world.<br>
	 * This method generates a unique {@code messageId}.<br>
	 * It uses {@code int, String)} to deliver the content.
	 * @param html The HTML string to be displayed to the players.
	 */
	public static void pushSurvey(String html)
	{
		final int messageId = IDFactory.getInstance().nextId();
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				sendData(player, messageId, html);
			}
		});
	}
	
	/**
	 * Displays a raw HTML string to a specific player.<br>
	 * This method generates a new unique message ID automatically.<br>
	 * It uses the {@code int, String)} method to deliver the content.
	 * @param player The {@code Player} who will receive the HTML message.
	 * @param html The {@code String} containing the HTML content to display.
	 */
	public static void showHTML(Player player, String html)
	{
		sendData(player, IDFactory.getInstance().nextId(), html);
	}
	
	/**
	 * Sends a raw HTML string to a specific player.<br>
	 * This method splits the {@code html} into multiple packets if it is too large.<br>
	 * It uses the {@code SM_QUESTIONNAIRE} packet type to deliver the content.
	 * @param player The {@link Player} who will receive the message.
	 * @param messageId The unique identifier for the message.
	 * @param html The HTML content to be sent to the client.
	 */
	public static void sendData(Player player, int messageId, String html)
	{
		final byte packet_count = (byte) Math.ceil((html.length() / (Short.MAX_VALUE - 8)) + 1);
		if (packet_count < 256)
		{
			for (byte i = 0; i < packet_count; i++)
			{
				try
				{
					int from = i * (Short.MAX_VALUE - 8), to = (i + 1) * (Short.MAX_VALUE - 8);
					if (from < 0)
					{
						from = 0;
					}
					
					if (to > html.length())
					{
						to = html.length();
					}
					
					final String sub = html.substring(from, to);
					if ((player != null) && (player.getClientConnection() != null))
					{
						player.getClientConnection().sendPacket(new SM_QUESTIONNAIRE(messageId, i, packet_count, sub));
					}
					else
					{
						log.warn("sendData failed. null player or connection");
						
						// TODO
						break;
					}
				}
				catch (Exception e)
				{
					log.error("htmlservice.sendData", e);
				}
			}
		}
	}
	
	/**
	 * Displays an HTML message to a specific {@link Player}.<br>
	 * This method checks if the {@code messageId} is greater than {@code 1000000}.<br>
	 * If it is, it calls {@code int, String)} to process the content.
	 * @param player The {@code Player} who will receive the message.
	 * @param html The HTML string to be displayed.
	 * @param messageId The unique identifier for the message.
	 */
	public static void showHTML(Player player, String html, int messageId)
	{
		if (messageId > 1000000)
		{
			sendData(player, messageId, html);
		}
	}
	
	/**
	 * Sends relevant guide HTML to a {@link Player}.<br>
	 * This method checks if the {@code player} level is greater than 1.<br>
	 * It sends all activated templates based on the player's class, race, and level.
	 * @param player The {@code Player} who will receive the guide messages.
	 */
	public static void sendGuideHtml(Player player)
	{
		if (player.getLevel() > 1)
		{
			final GuideTemplate[] surveyTemplate = DataManager.GUIDE_HTML_DATA.getTemplatesFor(player.getPlayerClass(), player.getRace(), player.getLevel());
			
			for (GuideTemplate template : surveyTemplate)
			{
				if (!template.isActivated())
				{
					continue;
				}
				
				final int id = IDFactory.getInstance().nextId();
				sendData(player, id, getHTMLTemplate(template));
				DAOManager.getDAO(GuideDAO.class).saveGuide(id, player, template.getTitle());
			}
		}
	}
	
	/**
	 * Sends a specific guide HTML to a {@link Player}.<br>
	 * This method looks up the template using the provided {@code title}.<br>
	 * It saves the guide entry in the database before sending it.
	 * @param player The {@link Player} who will receive the message.
	 * @param title The unique title of the guide to display.
	 */
	public static void sendGuideHtml(Player player, String title)
	{
		final GuideTemplate template = DataManager.GUIDE_HTML_DATA.getTemplateByTitle(title);
		if (template != null)
		{
			final int id = IDFactory.getInstance().nextId();
			DAOManager.getDAO(GuideDAO.class).saveGuide(id, player, title);
			sendData(player, id, getHTMLTemplate(template));
		}
	}
	
	/**
	 * Handles logic when a {@link Player} logs into the server.<br>
	 * This method loads and sends active guides to the player.<br>
	 * It retrieves guides from the database using the player's object ID.
	 * @param player The {@code Player} object that is currently logging in.
	 */
	public static void onPlayerLogin(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		final List<Guide> guides = DAOManager.getDAO(GuideDAO.class).loadGuides(player.getObjectId());
		
		for (Guide guide : guides)
		{
			final GuideTemplate template = DataManager.GUIDE_HTML_DATA.getTemplateByTitle(guide.getTitle());
			if (template != null)
			{
				if (template.isActivated())
				{
					sendData(player, guide.getGuideId(), getHTMLTemplate(template));
				}
			}
			else
			{
				log.warn("Null guide template for title: {}", guide.getTitle());
			}
		}
	}
	
	/**
	 * Grants rewards to a {@link Player} based on a specific guide.<br>
	 * This method checks if the player is eligible and has enough inventory space.<br>
	 * It adds items to the player and removes the completed guide from the database.
	 * @param player The {@link Player} who will receive the reward.
	 * @param messageId The unique identifier for the guide or survey.
	 * @param items A {@link List} of item IDs to be processed as rewards.
	 */
	public static void getReward(Player player, int messageId, List<Integer> items)
	{
		if ((player == null) || (messageId < 1) || SurveyService.getInstance().isActive(player, messageId))
		{
			return;
		}
		
		final Guide guide = DAOManager.getDAO(GuideDAO.class).loadGuide(player.getObjectId(), messageId);
		
		if (guide != null)
		{
			final GuideTemplate template = DataManager.GUIDE_HTML_DATA.getTemplateByTitle(guide.getTitle());
			if ((template == null) || (items.size() > template.getRewardCount()))
			{
				return;
			}
			
			if (items.size() > player.getInventory().getFreeSlots())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
				return;
			}
			
			List<SurveyTemplate> templates;
			if (template.getSurveys().size() != template.getRewardCount())
			{
				templates = getSurveyTemplates(template.getSurveys(), items);
			}
			else
			{
				templates = template.getSurveys();
			}
			
			if (templates.isEmpty())
			{
				return;
			}
			
			for (SurveyTemplate item : templates)
			{
				ItemService.addItem(player, item.getItemId(), item.getCount());
				if (LoggingConfig.LOG_ITEM)
				{
					log.info(String.format("[ITEM] Item Guide ID/Count - %d/%d to player %s.", item.getItemId(), item.getCount(), player.getName()));
				}
			}
			
			DAOManager.getDAO(GuideDAO.class).deleteGuide(guide.getGuideId());
			items.clear();
		}
	}
	
	/**
	 * This method filters a list of survey templates based on specific items.<br>
	 * It returns only the templates that match the provided item IDs.
	 * @param surveys The full list of {@code SurveyTemplate} objects to filter.
	 * @param items The list of {@code Integer} IDs used for filtering.
	 * @return A filtered {@code List} of matching {@code SurveyTemplate} objects.
	 */
	private static List<SurveyTemplate> getSurveyTemplates(List<SurveyTemplate> surveys, List<Integer> items)
	{
		final List<SurveyTemplate> templates = new ArrayList<>();
		for (SurveyTemplate survey : surveys)
		{
			if (items.contains(survey.getItemId()))
			{
				templates.add(survey);
			}
		}
		
		return templates;
	}
	
	/**
	 * Generates an HTML string for a poll survey.<br>
	 * This method builds the XML-like structure required by the client.
	 * @param title The text to display as the poll introduction.
	 * @param message The main question or description of the survey.
	 * @param select_text An array of strings representing the radio button options.
	 * @param itemId The unique identifier for the item associated with the poll.
	 * @param itemCount The total number of items available in the poll.
	 * @return A formatted HTML string containing the survey data.
	 */
	public static String HTMLTemplate(String title, String message, String[] select_text, int itemId, int itemCount)
	{
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<poll>\n");
		sb.append("<poll_introduction>\n");
		sb.append("	<![CDATA[<font color='4CB1E5'>" + title + "</font>]]>\n");
		sb.append("</poll_introduction>\n");
		sb.append("<poll_title>\n");
		sb.append("	<font color='ffc519'></font>\n");
		sb.append("</poll_title>\n");
		sb.append("<start_date>2010-08-08 00:00</start_date>\n");
		sb.append("<end_date>2010-09-14 01:00</end_date>\n");
		sb.append("<servers></servers>\n");
		sb.append("<order_num></order_num>\n");
		sb.append("<race></race>\n");
		sb.append("<main_class></main_class>\n");
		sb.append("<world_id></world_id>\n");
		sb.append("<item_id>");
		sb.append(itemId);
		sb.append("</item_id>\n");
		sb.append("<item_cnt>");
		sb.append(itemCount);
		sb.append("</item_cnt>\n");
		sb.append("<level>1~65</level>\n");
		sb.append("<questions>\n");
		sb.append("	<question>\n");
		sb.append("		<title>\n");
		sb.append("			<![CDATA[\n");
		sb.append("<br><br>");
		sb.append(message);
		sb.append("<br><br><br>\n");
		sb.append("			]]>\n");
		sb.append("		</title>\n");
		sb.append("		<select>\n");
		for (String select : select_text)
		{
			sb.append("<input type='radio'>");
			sb.append(select);
			sb.append("</input>\n");
		}
		sb.append("		</select>\n");
		sb.append("	</question>\n");
		sb.append("</questions>\n");
		sb.append("</poll>\n");
		
		return sb.toString();
	}
}
