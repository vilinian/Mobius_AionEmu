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
package com.aionemu.gameserver.services.player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerAchievementActionDAO;
import com.aionemu.gameserver.dao.PlayerAchievementDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementAction;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementState;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.model.gameobjects.player.achievement.PlayerAchievement;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionTemplate;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.model.templates.achievement.AchievementEventTemplate;
import com.aionemu.gameserver.model.templates.achievement.AchievementItems;
import com.aionemu.gameserver.model.templates.achievement.AchievementRepeat;
import com.aionemu.gameserver.model.templates.achievement.AchievementTemplate;
import com.aionemu.gameserver.model.templates.achievement.ActionRequiredType;
import com.aionemu.gameserver.model.templates.achievement.ActionsItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ACHIEVEMENT_COMPLETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ACHIEVEMENT_EVENT_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ACHIEVEMENT_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ACHIEVEMENT_UPDATE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the logic for player achievements within the game server.<br>
 * This service handles tracking progress, awarding rewards, and notifying players of completed {@link AchievementTemplate} goals.
 */
public class AchievementService
{
	private final Logger log = LoggerFactory.getLogger(AchievementService.class);
	private final List<AchievementTemplate> daily = new ArrayList<>();
	private final List<AchievementTemplate> weekly = new ArrayList<>();
	private final PlayerAchievementDAO dao = DAOManager.getDAO(PlayerAchievementDAO.class);
	private final PlayerAchievementActionDAO dao2 = DAOManager.getDAO(PlayerAchievementActionDAO.class);
	private Timestamp lastUpdate;
	@SuppressWarnings("unused")
	private Timestamp lastUpdateEvent;
	private final List<AchievementEventTemplate> activeEventEly = new ArrayList<>();
	private final List<AchievementEventTemplate> activeEventAsmo = new ArrayList<>();
	
	/**
	 * Initializes the daily achievement data.<br>
	 * This method resets the mission progress and generates required events.<br>
	 * It calls {@code generateAchievements} if no daily data exists.<br>
	 * It also triggers {@code generateAchievementsEvent}.
	 */
	public void init()
	{
		final Timestamp date = new Timestamp(System.currentTimeMillis());
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		log.info("Lugbug Mission Reset");
		if (daily.size() == 0)
		{
			generateAchievements();
			lastUpdate = new Timestamp(System.currentTimeMillis());
		}
		
		generateAchievementsEvent();
		lastUpdateEvent = new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * This method identifies active achievement events based on the current time.<br>
	 * It filters templates from {@code ACHIEVEMENT_EVENT_DATA}.<br>
	 * The results are sorted into specific lists for Elyos and Asmodian races.
	 */
	public void generateAchievementsEvent()
	{
		final Timestamp date = new Timestamp(System.currentTimeMillis());
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		final List<AchievementEventTemplate> achievements = DataManager.ACHIEVEMENT_EVENT_DATA.getAchievements();
		for (AchievementEventTemplate template : achievements)
		{
			if (template.getActive() && template.getStartDate().isBefore(java.time.ZonedDateTime.now()) && template.getEndDate().isAfter(java.time.ZonedDateTime.now()))
			{
				if (template.getRace() == Race.ELYOS)
				{
					activeEventEly.add(template);
				}
				else if (template.getRace() == Race.ASMODIANS)
				{
					activeEventAsmo.add(template);
				}
				else
				{
					activeEventEly.add(template);
					activeEventAsmo.add(template);
				}
			}
		}
	}
	
	/**
	 * This method populates the daily and weekly achievement lists.<br>
	 * It checks the current day of the week to filter {@code AchievementTemplate} objects.<br>
	 * It also adds all achievements that have a repeat type of {@code AchievementRepeat.ALL}.
	 */
	public void generateAchievements()
	{
		final Timestamp date = new Timestamp(System.currentTimeMillis());
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		final List<AchievementTemplate> achievements = DataManager.ACHIEVEMENT_DATA.getDaily();
		final List<AchievementTemplate> achievements2 = DataManager.ACHIEVEMENT_DATA.getWeekly();
		for (AchievementTemplate template : achievements2)
		{
			if (template.getRepeat() == AchievementRepeat.ALL)
			{
				weekly.add(template);
			}
		}
		
		switch (calendar.get(7))
		{
			case 2:
			{
				for (AchievementTemplate template : achievements)
				{
					if (template.getRepeat() == AchievementRepeat.MON)
					{
						daily.add(template);
					}
				}
				break;
			}
			case 3:
			{
				for (AchievementTemplate template : achievements)
				{
					if (template.getRepeat() == AchievementRepeat.TUE)
					{
						daily.add(template);
					}
				}
				break;
			}
			case 4:
			{
				for (AchievementTemplate template : achievements)
				{
					if (template.getRepeat() == AchievementRepeat.WED)
					{
						daily.add(template);
					}
				}
				break;
			}
			case 5:
			{
				for (AchievementTemplate template : achievements)
				{
					if (template.getRepeat() == AchievementRepeat.THU)
					{
						daily.add(template);
					}
				}
				break;
			}
			case 6:
			{
				for (AchievementTemplate template : achievements)
				{
					if (template.getRepeat() == AchievementRepeat.FRI)
					{
						daily.add(template);
					}
				}
				break;
			}
			case 7:
			{
				for (AchievementTemplate template : achievements)
				{
					if (template.getRepeat() == AchievementRepeat.SAT)
					{
						daily.add(template);
					}
				}
				break;
			}
			case 1:
			{
				for (AchievementTemplate template : achievements)
				{
					if (template.getRepeat() == AchievementRepeat.SUN)
					{
						daily.add(template);
					}
				}
				break;
			}
		}
	}
	
	/**
	 * Initializes and updates the player's achievements when they enter the world.<br>
	 * This method loads daily and weekly achievement templates based on the player's level and race.<br>
	 * It also checks for active event achievements and sends the updated lists to the client.
	 * @param player The {@link Player} object that entered the world.
	 */
	public void onEnterWorld(Player player)
	{
		final Timestamp now = new Timestamp(lastUpdate.getTime());
		final Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now.getTime());
		c.add(5, 1);
		c.set(11, 9);
		c.set(12, 0);
		c.set(13, 0);
		final Timestamp dailyExprire = new Timestamp(c.getTime().getTime());
		dao.loadAchievements(player);
		if (player.getPlayerAchievements().size() == 0)
		{
			AchievementTemplate template;
			if (player.getLevel() <= 75)
			{
				template = getAllraceDaily();
				final PlayerAchievement daily = new PlayerAchievement(template.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire);
				for (Integer actions : template.getActions().getIds())
				{
					final AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
					final AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire, daily.getObjectId());
					daily.getActionMap().put(action.getObjectId(), action);
					dao2.storeAction(player, action);
				}
				
				player.getPlayerAchievements().put(daily.getId(), daily);
				dao.storeAchievement(player, daily);
			}
			else
			{
				if (player.getRace() == Race.ELYOS)
				{
					template = getElyosDaily();
				}
				else
				{
					template = getAsmoDaily();
				}
				
				final PlayerAchievement daily = new PlayerAchievement(template.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire);
				for (Integer actions : template.getActions().getIds())
				{
					final AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
					final AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire, daily.getObjectId());
					daily.getActionMap().put(action.getObjectId(), action);
					dao2.storeAction(player, action);
				}
				
				player.getPlayerAchievements().put(daily.getId(), daily);
				dao.storeAchievement(player, daily);
			}
			
			if (player.getLevel() <= 75)
			{
				if (!playerHaveAchievement(player, template.getId()))
				{
					final PlayerAchievement weekly = new PlayerAchievement(template.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire);
					for (Integer actions : template.getActions().getIds())
					{
						final AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
						final AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire, weekly.getObjectId());
						weekly.getActionMap().put(action.getObjectId(), action);
						dao2.storeAction(player, action);
					}
					
					player.getPlayerAchievements().put(weekly.getId(), weekly);
					dao.storeAchievement(player, weekly);
				}
			}
			else
			{
				if (player.getRace() == Race.ELYOS)
				{
					template = DataManager.ACHIEVEMENT_DATA.getAchievementId(50);
				}
				else
				{
					template = DataManager.ACHIEVEMENT_DATA.getAchievementId(58);
				}
				
				if (!playerHaveAchievement(player, template.getId()))
				{
					final PlayerAchievement weekly = new PlayerAchievement(template.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire);
					for (Integer actions : template.getActions().getIds())
					{
						final AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
						final AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire, weekly.getObjectId());
						weekly.getActionMap().put(action.getObjectId(), action);
						dao2.storeAction(player, action);
					}
					
					player.getPlayerAchievements().put(weekly.getId(), weekly);
					dao.storeAchievement(player, weekly);
				}
			}
		}
		
		if (player.getPlayerAchievements().size() == 1)
		{
			if (player.getLevel() <= 75)
			{
				final AchievementTemplate template = getAllraceDaily();
				final PlayerAchievement daily = new PlayerAchievement(template.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire);
				for (Integer actions : template.getActions().getIds())
				{
					final AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
					final AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire, daily.getObjectId());
					daily.getActionMap().put(action.getObjectId(), action);
					dao2.storeAction(player, action);
				}
				
				player.getPlayerAchievements().put(daily.getId(), daily);
				dao.storeAchievement(player, daily);
			}
			else
			{
				AchievementTemplate template;
				if (player.getRace() == Race.ELYOS)
				{
					template = getElyosDaily();
				}
				else
				{
					template = getAsmoDaily();
				}
				
				final PlayerAchievement daily = new PlayerAchievement(template.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire);
				for (Integer actions : template.getActions().getIds())
				{
					final AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
					final AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire, daily.getObjectId());
					daily.getActionMap().put(action.getObjectId(), action);
					dao2.storeAction(player, action);
				}
				
				player.getPlayerAchievements().put(daily.getId(), daily);
				dao.storeAchievement(player, daily);
			}
		}
		
		if (player.getPlayerEventAchievements().size() == 0)
		{
			if (player.getRace() == Race.ELYOS)
			{
				for (AchievementEventTemplate templates : getActiveEventEly())
				{
					if ((player.getLevel() >= templates.getMinlevel()) && (player.getLevel() <= templates.getMaxlevel()))
					{
						final PlayerAchievement event = new PlayerAchievement(templates.getId(), templates.getType(), AchievementState.START, 0, new Timestamp(templates.getStartDate().toInstant().toEpochMilli()), new Timestamp(templates.getEndDate().toInstant().toEpochMilli()));
						player.getPlayerEventAchievements().put(event.getId(), event);
						dao.storeAchievement(player, event);
					}
				}
			}
			else
			{
				for (AchievementEventTemplate templates : getActiveEventAsmo())
				{
					if ((player.getLevel() >= templates.getMinlevel()) && (player.getLevel() <= templates.getMaxlevel()))
					{
						final PlayerAchievement event = new PlayerAchievement(templates.getId(), templates.getType(), AchievementState.START, 0, new Timestamp(templates.getStartDate().toInstant().toEpochMilli()), new Timestamp(templates.getEndDate().toInstant().toEpochMilli()));
						player.getPlayerEventAchievements().put(event.getId(), event);
					}
				}
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_LIST(player));
		PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_EVENT_LIST(player));
	}
	
	/**
	 * Updates the progress of a player's achievement actions.<br>
	 * This method checks if the provided {@code value} and {@code count} meet the requirements for an {@code AchievementActionType}.<br>
	 * It updates the step progress, handles rewards, and sends update packets to the {@link Player}.
	 * @param player The {@link Player} who is performing the action.
	 * @param value The specific ID or value required by the achievement template.
	 * @param count The amount to add to the current progress step.
	 * @param type The {@code AchievementActionType} of the action being performed.
	 */
	public void onUpdateAchievementAction(Player player, int value, int count, AchievementActionType type)
	{
		for (PlayerAchievement playerAchievement : player.getPlayerAchievements().values())
		{
			final PlayerAchievement achievements = playerAchievement;
			for (AchievementAction actions : playerAchievement.getActionMap().values())
			{
				final AchievementActionTemplate template = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions.getId());
				if ((template.getType() == type) && (actions.getState() == AchievementState.START))
				{
					for (Integer ids : template.getRequired().getValues())
					{
						if ((ids == value) && (template.getRequired().getType() == ActionRequiredType.SPECIFIC))
						{
							final int progress = actions.getStep() + count;
							if (progress >= template.getMaxvalue())
							{
								actions.setStep(template.getMaxvalue());
								actions.setState(AchievementState.REWARD);
								achievements.setStep(achievements.getStep() + 1);
								PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(actions.getObjectId(), actions.getStep(), AchievementState.REWARD));
								PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(achievements.getObjectId(), achievements.getStep(), AchievementState.START));
								dao.update(player, achievements);
								dao2.update(player, actions);
							}
							else
							{
								actions.setStep(progress);
								PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(actions.getObjectId(), actions.getStep(), AchievementState.START));
								dao2.update(player, actions);
							}
						}
					}
					
					if ((template.getType() != type) || (template.getRequired().getType() != ActionRequiredType.ALL) || (actions.getState() != AchievementState.START))
					{
						continue;
					}
					
					final int progress2 = actions.getStep() + count;
					if (progress2 >= template.getMaxvalue())
					{
						actions.setStep(template.getMaxvalue());
						actions.setState(AchievementState.REWARD);
						achievements.setStep(achievements.getStep() + 1);
						PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(actions.getObjectId(), actions.getStep(), AchievementState.REWARD));
						PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(achievements.getObjectId(), achievements.getStep(), AchievementState.START));
						dao.update(player, achievements);
						dao2.update(player, actions);
					}
					else
					{
						actions.setStep(progress2);
						PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(actions.getObjectId(), actions.getStep(), AchievementState.START));
						dao2.update(player, actions);
					}
				}
			}
		}
	}
	
	/**
	 * Handles the logic for a player leveling up.<br>
	 * This method checks if the {@link Player} has their weekly achievement.<br>
	 * It initializes the achievement and its actions if they are missing.<br>
	 * Finally, it sends updated achievement packets to the client.
	 * @param player The {@code Player} object who leveled up.
	 */
	public void onLeveUplPlayer(Player player)
	{
		final Timestamp now = new Timestamp(lastUpdate.getTime());
		final Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now.getTime());
		c.add(7, 7);
		c.set(11, 9);
		c.set(12, 0);
		c.set(13, 0);
		final Timestamp dailyExprire = new Timestamp(c.getTime().getTime());
		AchievementTemplate template;
		if (player.getRace() == Race.ELYOS)
		{
			template = DataManager.ACHIEVEMENT_DATA.getAchievementId(50);
		}
		else
		{
			template = DataManager.ACHIEVEMENT_DATA.getAchievementId(58);
		}
		
		if (!playerHaveAchievement(player, template.getId()))
		{
			final PlayerAchievement weekly = new PlayerAchievement(template.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire);
			for (Integer actions : template.getActions().getIds())
			{
				final AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
				final AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire, weekly.getObjectId());
				weekly.getActionMap().put(action.getObjectId(), action);
				dao2.storeAction(player, action);
			}
			
			player.getPlayerAchievements().put(weekly.getId(), weekly);
			dao.storeAchievement(player, weekly);
		}
		
		PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_EVENT_LIST(player));
		PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_LIST(player));
	}
	
	/**
	 * Handles the reward distribution for a completed achievement action.<br>
	 * This method updates the action state to {@code COMPLETE}.<br>
	 * It grants items to the {@link Player} based on the template rewards.<br>
	 * Finally, it sends completion packets and updates the database.
	 * @param player The {@link Player} receiving the reward.
	 * @param objectId The unique identifier of the game object involved in the action.
	 * @param templateId The ID of the achievement action template to use for rewards.
	 */
	public void onRewardAction(Player player, int objectId, int templateId)
	{
		final AchievementAction action = getActionbyObj(player, objectId);
		final AchievementActionTemplate template = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(templateId);
		action.setState(AchievementState.COMPLETE);
		for (ActionsItems items : template.getRewards().getAchievementItems())
		{
			ItemService.addItem(player, items.getItemId(), items.getCount());
		}
		
		PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_COMPLETE(templateId, action.getAchievementObjectId(), objectId));
		dao2.update(player, action);
		PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_LIST(player));
	}
	
	/**
	 * Grants rewards to a player for completing a specific achievement.<br>
	 * This method updates the achievement state to {@code COMPLETE}.<br>
	 * It gives all items defined in the template to the {@link Player}.<br>
	 * Finally, it sends completion packets and updates the database.
	 * @param player The {@link Player} who received the reward.
	 * @param templateId The unique identifier for the achievement template.
	 */
	public void onRewardAchievement(Player player, int templateId)
	{
		final PlayerAchievement achievement = getAchievementbyId(player, templateId);
		final AchievementTemplate template = DataManager.ACHIEVEMENT_DATA.getAchievementId(achievement.getId());
		achievement.setState(AchievementState.COMPLETE);
		for (AchievementItems items : template.getRewards().getAchievementItems())
		{
			ItemService.addItem(player, items.getItemId(), items.getCount());
		}
		
		PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_COMPLETE(templateId, achievement.getObjectId(), 0));
		dao.update(player, achievement);
		PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_LIST(player));
	}
	
	/**
	 * Retrieves the daily achievement template for all races.<br>
	 * This method searches through the daily achievements list.<br>
	 * It returns the template where the race is set to {@code Race.PC_ALL}.
	 * @return the {@code AchievementTemplate} for all races or {@code null} if not found.
	 */
	public AchievementTemplate getAllraceDaily()
	{
		AchievementTemplate tmp = null;
		for (AchievementTemplate template : daily)
		{
			if (template.getRace() == Race.PC_ALL)
			{
				tmp = template;
			}
		}
		
		return tmp;
	}
	
	/**
	 * Retrieves the daily achievement template for the Elyos race.<br>
	 * This method searches through all daily templates to find a match.
	 * @return the {@code AchievementTemplate} for Elyos or {@code null} if not found.
	 */
	public AchievementTemplate getElyosDaily()
	{
		AchievementTemplate tmp = null;
		for (AchievementTemplate template : daily)
		{
			if (template.getRace() == Race.ELYOS)
			{
				tmp = template;
			}
		}
		
		return tmp;
	}
	
	/**
	 * Retrieves the daily achievement template for the Asmodian race.<br>
	 * It searches through all daily templates to find a match.
	 * @return the {@code AchievementTemplate} for Asmodians or {@code null} if not found.
	 */
	public AchievementTemplate getAsmoDaily()
	{
		AchievementTemplate tmp = null;
		for (AchievementTemplate template : daily)
		{
			if (template.getRace() == Race.ASMODIANS)
			{
				tmp = template;
			}
		}
		
		return tmp;
	}
	
	/**
	 * Retrieves the {@code AchievementAction} associated with a specific object ID for a player.<br>
	 * This method searches through all of the {@link PlayerAchievement} records owned by the {@code player}.<br>
	 * It returns the action mapped to the provided {@code obj} integer.
	 * @param player The {@code Player} whose achievements are being checked.
	 * @param obj The unique identifier for the object.
	 * @return The {@code AchievementAction} found, or {@code null} if no match exists.
	 */
	public AchievementAction getActionbyObj(Player player, int obj)
	{
		AchievementAction action = null;
		for (PlayerAchievement achievement : player.getPlayerAchievements().values())
		{
			if (achievement.getActionMap().containsKey(obj))
			{
				action = achievement.getActionMap().get(obj);
			}
		}
		
		return action;
	}
	
	/**
	 * Retrieves a specific {@link PlayerAchievement} for a given object ID.<br>
	 * This method checks if the {@code player} has an achievement associated with the provided {@code obj}.<br>
	 * It returns {@code null} if no such achievement exists.
	 * @param player The {@code Player} whose achievements are being checked.
	 * @param obj The unique identifier of the object to look up.
	 * @return The {@code PlayerAchievement} associated with the object, or {@code null}.
	 */
	public PlayerAchievement getAchievementbyObj(Player player, int obj)
	{
		PlayerAchievement achievement = null;
		if (player.getPlayerAchievements().containsKey(obj))
		{
			achievement = player.getPlayerAchievements().get(obj);
		}
		
		return achievement;
	}
	
	/**
	 * Retrieves a specific {@link PlayerAchievement} for a given player.<br>
	 * It searches through all achievements owned by the {@code player}.<br>
	 * Returns {@code null} if no achievement matches the provided {@code id}.
	 * @param player The {@code Player} object to search within.
	 * @param id The unique identifier of the achievement.
	 * @return The matching {@link PlayerAchievement} or {@code null}.
	 */
	public PlayerAchievement getAchievementbyId(Player player, int id)
	{
		PlayerAchievement achievement = null;
		for (PlayerAchievement ach : player.getPlayerAchievements().values())
		{
			if (ach.getId() == id)
			{
				achievement = ach;
			}
		}
		
		return achievement;
	}
	
	/**
	 * Completes a specific achievement event for a player.<br>
	 * This method updates the state of the achievement in the database.<br>
	 * It also handles reward distribution and progress updates for sub-events.<br>
	 * Finally, it sends an updated achievement list packet to the player.
	 * @param player The {@code Player} who completed the event.
	 * @param templateId The unique ID of the achievement template to complete.
	 */
	public void completeAchievementEvent(Player player, int templateId)
	{
		final AchievementEventTemplate template = DataManager.ACHIEVEMENT_EVENT_DATA.getAchievementId(templateId);
		final PlayerAchievement event = getAchievementEventbyId(player, templateId);
		if (template.getType() == AchievementType.EVENT_SUB)
		{
			final int point = template.getCompletePoint();
			final AchievementActionTemplate action = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(template.getActionId());
			for (ActionsItems rewards : action.getRewards().getAchievementItems())
			{
				ItemService.addItem(player, rewards.getItemId(), rewards.getCount());
			}
			
			event.setState(AchievementState.COMPLETE);
			dao.update(player, event);
			final PlayerAchievement main = getAchievementEventMain(player);
			main.setStep(main.getStep() + point);
			dao.update(player, main);
		}
		else
		{
			event.setState(AchievementState.COMPLETE);
			dao.update(player, event);
		}
		
		PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_EVENT_LIST(player));
	}
	
	/**
	 * Retrieves a specific achievement event for a player based on its unique ID.<br>
	 * This method searches through all of the {@link Player} event achievements.<br>
	 * It returns the matching {@code PlayerAchievement} object if found.
	 * @param player The {@code Player} whose achievements are being searched.
	 * @param id The unique identifier of the achievement event to find.
	 * @return The {@code PlayerAchievement} matching the ID, or {@code null} if no match exists.
	 */
	public PlayerAchievement getAchievementEventbyId(Player player, int id)
	{
		PlayerAchievement achievement = null;
		for (PlayerAchievement ach : player.getPlayerEventAchievements().values())
		{
			if (ach.getId() == id)
			{
				achievement = ach;
			}
		}
		
		return achievement;
	}
	
	/**
	 * Retrieves the main event achievement for a specific player.<br>
	 * This method searches through all of the {@link Player}'s event achievements.<br>
	 * It returns the first achievement that matches the {@code EVENT_MAIN} type.
	 * @param player The {@link Player} object to check for achievements.
	 * @return The {@link PlayerAchievement} matching the main event type, or {@code null} if none are found.
	 */
	public PlayerAchievement getAchievementEventMain(Player player)
	{
		PlayerAchievement achievement = null;
		for (PlayerAchievement ach : player.getPlayerEventAchievements().values())
		{
			if (ach.getType() == AchievementType.EVENT_MAIN)
			{
				achievement = ach;
			}
		}
		
		return achievement;
	}
	
	/**
	 * Checks if a specific {@link Player} has earned an achievement.<br>
	 * It searches through all achievements owned by the player.
	 * @param player The {@code Player} object to check.
	 * @param id The unique identifier of the achievement.
	 * @return {@code true} if the player owns the achievement, otherwise {@code false}.
	 */
	public boolean playerHaveAchievement(Player player, int id)
	{
		if (player.getPlayerAchievements().size() == 0)
		{
			return false;
		}
		
		for (PlayerAchievement achievement : player.getPlayerAchievements().values())
		{
			if (achievement.getId() == id)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the timestamp of the most recent update.<br>
	 * This value is stored in the {@code lastUpdate} field.
	 * @return The {@code Timestamp} of the last update.
	 */
	public Timestamp getLastUpdate()
	{
		return lastUpdate;
	}
	
	/**
	 * Retrieves the {@link PlayerAchievementDAO} instance.<br>
	 * This method provides access to the data access object for player achievements.
	 * @return The current {@code PlayerAchievementDAO} instance.
	 */
	public PlayerAchievementDAO getDao()
	{
		return dao;
	}
	
	/**
	 * Retrieves the second {@link PlayerAchievementActionDAO} instance.<br>
	 * This method provides access to a secondary data access object for achievement actions.
	 * @return The {@code PlayerAchievementActionDAO} instance.
	 */
	public PlayerAchievementActionDAO getDao2()
	{
		return dao2;
	}
	
	/**
	 * Retrieves the list of currently active Elyos event templates.<br>
	 * This method returns the {@code activeEventEly} collection.
	 * @return a {@code List} of {@link AchievementEventTemplate} objects.
	 */
	public List<AchievementEventTemplate> getActiveEventEly()
	{
		return activeEventEly;
	}
	
	/**
	 * Retrieves the list of active Asmo achievement events.<br>
	 * This method returns the current event templates for the Asmo race.
	 * @return a {@code List} of {@link AchievementEventTemplate} objects.
	 */
	public List<AchievementEventTemplate> getActiveEventAsmo()
	{
		return activeEventEly;
	}
	
	/**
	 * Retrieves the singleton instance of the {@link AchievementService}.<br>
	 * Use this method to access the global achievement manager.
	 * @return The active {@code AchievementService} instance.
	 */
	public static AchievementService getInstance()
	{
		return NewSingletonHolder.INSTANCE;
	}
	
	private static class NewSingletonHolder
	{
		private static final AchievementService INSTANCE = new AchievementService();
	}
}
