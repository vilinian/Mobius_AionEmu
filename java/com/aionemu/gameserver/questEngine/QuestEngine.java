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
package com.aionemu.gameserver.questEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.QuestsData;
import com.aionemu.gameserver.dataholders.XMLQuests;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.quest.HandlerSideDrop;
import com.aionemu.gameserver.model.templates.quest.InventoryItem;
import com.aionemu.gameserver.model.templates.quest.QuestCategory;
import com.aionemu.gameserver.model.templates.quest.QuestDrop;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.quest.QuestNpc;
import com.aionemu.gameserver.model.templates.rewards.BonusType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_REPEAT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.ConstantSpawnHandler;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.QuestHandlerLoader;
import com.aionemu.gameserver.questEngine.handlers.models.XMLQuest;
import com.aionemu.gameserver.questEngine.model.QuestActionType;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneName;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Manages the core logic and execution of all in-game quests.<br>
 * This class handles quest progression, requirements, and interactions for {@link Player} objects.<br>
 * It coordinates between {@link QuestTemplate} data and active quest states.
 * @author MrPoke, Hilgert
 * @modified vlog
 */
public class QuestEngine implements GameEngine
{
	private static final Logger log = LoggerFactory.getLogger(QuestEngine.class);
	private static final Map<Integer, QuestHandler> questHandlers = new HashMap<>();
	private static ScriptManager scriptManager = new ScriptManager();
	private final TIntObjectHashMap<QuestNpc> questNpcs = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<TIntArrayList> questItemRelated = new TIntObjectHashMap<>();
	private final TIntArrayList questHouseItems = new TIntArrayList();
	private final TIntObjectHashMap<TIntArrayList> questItems = new TIntObjectHashMap<>();
	private final TIntArrayList questOnEnterZoneMissionEnd = new TIntArrayList();
	private final TIntArrayList questOnLevelUp = new TIntArrayList();
	private final TIntArrayList questOnDie = new TIntArrayList();
	private final TIntArrayList questOnLogOut = new TIntArrayList();
	private final TIntArrayList questOnEnterWorld = new TIntArrayList();
	private final Map<ZoneName, TIntArrayList> questOnEnterZone = new HashMap<>();
	private final Map<ZoneName, TIntArrayList> questOnLeaveZone = new HashMap<>();
	private final Map<String, TIntArrayList> questOnPassFlyingRings = new HashMap<>();
	private final TIntObjectHashMap<TIntArrayList> questOnMovieEnd = new TIntObjectHashMap<>();
	private final List<Integer> questOnTimerEnd = new ArrayList<>();
	private final List<Integer> onInvisibleTimerEnd = new ArrayList<>();
	private final Map<AbyssRankEnum, TIntArrayList> questOnKillRanked = new HashMap<>();
	private final Map<Integer, TIntArrayList> questOnKillInWorld = new HashMap<>();
	private final TIntObjectHashMap<TIntArrayList> questOnUseSkill = new TIntObjectHashMap<>();
	private final Map<Integer, DialogAction> dialogMap = new HashMap<>();
	private final Map<Integer, Integer> questOnFailCraft = new HashMap<>();
	private final Map<Integer, Set<Integer>> questOnEquipItem = new HashMap<>();
	private final TIntObjectHashMap<TIntArrayList> questCanAct = new TIntObjectHashMap<>();
	private final List<Integer> questOnDredgionReward = new ArrayList<>();
	private final List<Integer> questOnKamarReward = new ArrayList<>();
	private final List<Integer> questOnOphidanReward = new ArrayList<>();
	private final List<Integer> questOnBastionReward = new ArrayList<>();
	private final Map<BonusType, TIntArrayList> questOnBonusApply = new HashMap<>();
	private final TIntArrayList reachTarget = new TIntArrayList();
	private final TIntArrayList lostTarget = new TIntArrayList();
	private final TIntArrayList questOnEnterWindStream = new TIntArrayList();
	private final TIntArrayList questRideAction = new TIntArrayList();
	private final TIntArrayList questOnCreativityPoint = new TIntArrayList();
	
	// TEST
	private final List<Integer> questsToRepeat = new ArrayList<>();
	
	/**
	 * Private constructor for the {@link QuestEngine} class.<br>
	 * This prevents direct instantiation of the engine.<br>
	 * Use {@code getInstance} to access the singleton instance.
	 */
	private QuestEngine()
	{
	}
	
	/**
	 * Retrieves the singleton instance of the {@link QuestEngine}.<br>
	 * This provides a global access point to the quest system.
	 * @return The active {@code QuestEngine} instance.
	 */
	public static QuestEngine getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Handles dialog events for quests.<br>
	 * This method checks if a quest handler exists for the current environment.<br>
	 * It processes the dialog event and returns whether it was successfully handled.
	 * @param env The {@code QuestEnv} containing the current quest context.
	 * @return {@code true} if the dialog event was handled by a quest handler, otherwise {@code false}.
	 */
	public boolean onDialog(QuestEnv env)
	{
		try
		{
			QuestHandler questHandler = null;
			if (env.getQuestId() != 0)
			{
				questHandler = getQuestHandlerByQuestId(env.getQuestId());
				if (questHandler != null)
				{
					if (questHandler.onDialogEvent(env))
					{
						return true;
					}
					
					final QuestTemplate qt = DataManager.QUEST_DATA.getQuestById(env.getQuestId());
					if ((qt != null) && (qt.getCategory() == QuestCategory.CHALLENGE_TASK))
					{
						PacketSendUtility.sendPacket(env.getPlayer(), new SM_SYSTEM_MESSAGE(1400855, 9));
					}
				}
			}
			else
			{
				final Npc npc = (Npc) env.getVisibleObject();
				for (int questId : getQuestNpc(npc == null ? 0 : npc.getNpcId()).getOnTalkEvent())
				{
					questHandler = getQuestHandlerByQuestId(questId);
					if (questHandler != null)
					{
						env.setQuestId(questId);
						if (questHandler.onDialogEvent(env))
						{
							return true;
						}
					}
				}
				
				env.setQuestId(0);
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onDialog - QuestId: " + env.getQuestId() + " Error: ", ex);
			return false;
		}
		
		return false;
	}
	
	/**
	 * This method is called when a player kills an {@link Npc}.<br>
	 * It checks for any active quest events associated with that specific NPC.<br>
	 * If a valid quest handler is found, it executes the corresponding event logic.
	 * @param env The current quest environment containing context about the action.
	 * @return {@code true} if the kill was processed successfully, or {@code false} if an error occurred.
	 */
	public boolean onKill(QuestEnv env)
	{
		try
		{
			final Npc npc = (Npc) env.getVisibleObject();
			for (int questId : getQuestNpc(npc.getNpcId()).getOnKillEvent())
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
				if (questHandler != null)
				{
					env.setQuestId(questId);
					questHandler.onKillEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onKill - QuestId: " + env.getQuestId() + " Error: ", ex);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handles events triggered when a player attacks an {@link Npc}.<br>
	 * This method identifies the correct quest based on the target NPC.<br>
	 * It then executes the associated attack event for that quest.
	 * @param env The environment containing current quest and object data.
	 * @return {@code true} if the event was processed successfully, or {@code false} if an error occurred.
	 */
	public boolean onAttack(QuestEnv env)
	{
		try
		{
			final Npc npc = (Npc) env.getVisibleObject();
			for (int questId : getQuestNpc(npc.getNpcId()).getOnAttackEvent())
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
				if (questHandler != null)
				{
					env.setQuestId(questId);
					questHandler.onAttackEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onAttack - QuestId: " + env.getQuestId() + " Error: ", ex);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handles events that occur when a player levels up.<br>
	 * This method checks for active quests that trigger on level up.<br>
	 * It executes the {@code onLvlUpEvent} for each applicable quest.
	 * @param env The {@link QuestEnv} containing the current context and player data.
	 */
	public void onLvlUp(QuestEnv env)
	{
		try
		{
			final Player player = env.getPlayer();
			for (int index = 0; index < questOnLevelUp.size(); index++)
			{
				QuestHandler questHandler = null;
				final QuestState qs = player.getQuestStateList().getQuestState(questOnLevelUp.get(index));
				if ((qs == null) || (qs.getStatus() != QuestStatus.COMPLETE))
				{
					questHandler = getQuestHandlerByQuestId(questOnLevelUp.get(index));
				}
				
				if (questHandler != null)
				{
					env.setQuestId(questOnLevelUp.get(index));
					questHandler.onLvlUpEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onLvlUp - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * Handles the logic when a player enters a zone that marks the end of a mission.<br>
	 * This method checks if the current quest has a registered end event for this action.<br>
	 * If a valid {@code QuestHandler} is found, it updates the quest ID and triggers the event.
	 * @param env The {@code QuestEnv} object containing the current quest context.
	 */
	public void onEnterZoneMissionEnd(QuestEnv env)
	{
		try
		{
			final int result = questOnEnterZoneMissionEnd.indexOf(env.getQuestId());
			QuestHandler questHandler = null;
			if (result != -1)
			{
				questHandler = getQuestHandlerByQuestId(questOnEnterZoneMissionEnd.get(result));
			}
			
			if (questHandler != null)
			{
				env.setQuestId(questOnEnterZoneMissionEnd.get(result));
				questHandler.onZoneMissionEndEvent(env);
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onEnterZoneMissionEnd - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * This method is triggered when a player character dies.<br>
	 * It iterates through all registered quest handlers that respond to death events.<br>
	 * If a valid handler is found, it executes the {@code onDieEvent} for that specific quest.
	 * @param env The current quest environment containing context about the event.
	 */
	public void onDie(QuestEnv env)
	{
		try
		{
			for (int index = 0; index < questOnDie.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questOnDie.get(index));
				if (questHandler != null)
				{
					env.setQuestId(questOnDie.get(index));
					questHandler.onDieEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onDie - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * Handles quest logic when a player logs out.<br>
	 * This method iterates through all active {@code onLogOut} quests.<br>
	 * It triggers the specific event for each valid quest handler.
	 * @param env The current quest environment context.
	 */
	public void onLogOut(QuestEnv env)
	{
		try
		{
			for (int index = 0; index < questOnLogOut.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questOnLogOut.get(index));
				if (questHandler != null)
				{
					env.setQuestId(questOnLogOut.get(index));
					questHandler.onLogOutEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onLogOut - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * This method is called when an {@link Npc} reaches a specific target.<br>
	 * It checks if the current quest ID matches any required reach targets.<br>
	 * If a match is found, it updates the quest and triggers the corresponding event.
	 * @param env The {@code QuestEnv} object containing the current quest context.
	 */
	public void onNpcReachTarget(QuestEnv env)
	{
		try
		{
			for (int index = 0; index < reachTarget.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(reachTarget.get(index));
				if ((questHandler != null) && (env.getQuestId() == reachTarget.get(index)))
				{
					env.setQuestId(reachTarget.get(index));
					questHandler.onNpcReachTargetEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onNpcReachTarget - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * This method is called when an {@link Npc} loses its target.<br>
	 * It iterates through all lost targets and triggers the corresponding quest events.<br>
	 * If a valid {@code QuestHandler} exists, it updates the environment and executes the event.
	 * @param env The current quest environment context.
	 */
	public void onNpcLostTarget(QuestEnv env)
	{
		try
		{
			for (int index = 0; index < lostTarget.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(lostTarget.get(index));
				if (questHandler != null)
				{
					env.setQuestId(lostTarget.get(index));
					questHandler.onNpcLostTargetEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onNpcLostTarget - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * This method is called when a player passes through a flying ring.<br>
	 * It identifies and triggers the correct quest events for that specific ring.
	 * @param env The current {@code QuestEnv} context for the player.
	 * @param FlyRing The unique identifier of the flying ring passed.
	 */
	public void onPassFlyingRing(QuestEnv env, String FlyRing)
	{
		try
		{
			final TIntArrayList lists = getOnPassFlyingRingsQuests(FlyRing);
			for (int index = 0; index < lists.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(lists.get(index));
				if (questHandler != null)
				{
					env.setQuestId(lists.get(index));
					questHandler.onPassFlyingRingEvent(env, FlyRing);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onPassFlyingRing - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * This method is called when a player enters the game world.<br>
	 * It triggers specific events for all active quests that require this action.<br>
	 * The {@code QuestEngine} iterates through registered quest IDs and executes their logic.
	 * @param env The {@link QuestEnv} object containing the current quest context.
	 */
	public void onEnterWorld(QuestEnv env)
	{
		try
		{
			for (int index = 0; index < questOnEnterWorld.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questOnEnterWorld.get(index));
				if (questHandler != null)
				{
					env.setQuestId(questOnEnterWorld.get(index));
					questHandler.onEnterWorldEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onEnterWorld - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * Handles the logic when a player uses an {@code Item}.<br>
	 * This method checks if the quest is in the {@code START} status.<br>
	 * It also verifies if the player is in the correct zone before processing.
	 * @param env The current quest environment context.
	 * @param item The specific {@code Item} that was used by the player.
	 * @return A {@code HandlerResult} indicating if the action succeeded or failed.
	 */
	public HandlerResult onItemUseEvent(QuestEnv env, Item item)
	{
		try
		{
			final TIntArrayList lists = getItemRelatedQuests(item.getItemTemplate().getTemplateId());
			for (int index = 0; index < lists.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(lists.get(index));
				if (questHandler != null)
				{
					env.setQuestId(lists.get(index));
					final HandlerResult result = questHandler.onItemUseEvent(env, item);
					
					// allow other quests to process, the same item can be used not in one quest
					if (result != HandlerResult.UNKNOWN)
					{
						return result;
					}
				}
			}
			
			return HandlerResult.UNKNOWN;
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onItemUseEvent - QuestId: " + env.getQuestId() + " Error: ", ex);
			return HandlerResult.FAILED;
		}
	}
	
	/**
	 * Handles events triggered when a player uses an item inside a house.<br>
	 * This method iterates through all registered quest house items.<br>
	 * It updates the {@code QuestEnv} with the correct quest ID and triggers the corresponding handler.
	 * @param env The current quest environment context.
	 */
	public void onHouseItemUseEvent(QuestEnv env)
	{
		try
		{
			for (int index = 0; index < questHouseItems.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questHouseItems.get(index));
				if (questHandler != null)
				{
					env.setQuestId(questHouseItems.get(index));
					questHandler.onHouseItemUseEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onHouseItemUseEvent - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * Handles the logic when a player receives an item.<br>
	 * This method checks if the {@code itemId} is associated with any quests.<br>
	 * If a match is found, it updates the quest context and triggers the corresponding event.
	 * @param env The current quest environment context.
	 * @param itemId The unique identifier of the item received.
	 */
	public void onItemGet(QuestEnv env, int itemId)
	{
		if (questItems.containsKey(itemId))
		{
			for (int i = 0; i < questItems.get(itemId).size(); i++)
			{
				final int questId = questItems.get(itemId).get(i);
				final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
				if (questHandler != null)
				{
					env.setQuestId(questId);
					questHandler.onGetItemEvent(env);
				}
			}
		}
	}
	
	/**
	 * Handles quest events triggered when a player kills an enemy in a ranked match.<br>
	 * This method checks the {@code playerRank} to find applicable quests.<br>
	 * It executes the {@code onKillRankedEvent} for each valid quest found.
	 * @param env The current quest environment context.
	 * @param playerRank The rank of the player during the ranked match.
	 * @return {@code true} if the event was processed successfully, or {@code false} if an error occurred.
	 */
	public boolean onKillRanked(QuestEnv env, AbyssRankEnum playerRank)
	{
		try
		{
			if (playerRank != null)
			{
				final TIntArrayList questList = getOnKillRankedQuests(playerRank);
				for (int index = 0; index < questList.size(); index++)
				{
					final int id = questList.get(index);
					final QuestHandler questHandler = getQuestHandlerByQuestId(id);
					if (questHandler != null)
					{
						env.setQuestId(id);
						questHandler.onKillRankedEvent(env);
					}
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onKillRanked - QuestId: " + env.getQuestId() + " Error: ", ex);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handles quest events triggered when a player kills an enemy in a specific world.<br>
	 * This method checks if any quests are registered for the provided {@code worldId}.<br>
	 * It then executes the corresponding quest logic using the {@link QuestEnv} context.
	 * @param env The current quest environment containing player data.
	 * @param worldId The unique identifier of the world where the kill occurred.
	 * @return {@code true} if the event was processed successfully, or {@code false} if an error occurred.
	 */
	public boolean onKillInWorld(QuestEnv env, int worldId)
	{
		try
		{
			if (questOnKillInWorld.containsKey(worldId))
			{
				final TIntArrayList killInWorldQuests = questOnKillInWorld.get(worldId);
				for (int i = 0; i < killInWorldQuests.size(); i++)
				{
					final QuestHandler questHandler = getQuestHandlerByQuestId(killInWorldQuests.get(i));
					if (questHandler != null)
					{
						env.setQuestId(killInWorldQuests.get(i));
						questHandler.onKillInWorldEvent(env);
					}
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onKillInWorld - QuestId: " + env.getQuestId() + " Error: ", ex);
			return false;
		}
		
		return true;
	}
	
	/**
	 * This method is called when a player enters a specific zone.<br>
	 * It checks for quests that trigger upon entering the {@code zoneName}.<br>
	 * If a valid quest is found, it updates the environment and triggers its event.
	 * @param env The current quest environment context.
	 * @param zoneName The name of the zone the player entered.
	 * @return {@code true} if the process completed successfully, or {@code false} if an error occurred.
	 */
	public boolean onEnterZone(QuestEnv env, ZoneName zoneName)
	{
		try
		{
			final TIntArrayList lists = getOnEnterZoneQuests(zoneName);
			for (int index = 0; index < lists.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(lists.get(index));
				if (questHandler != null)
				{
					env.setQuestId(lists.get(index));
					questHandler.onEnterZoneEvent(env, zoneName);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onEnterZone - QuestId: " + env.getQuestId() + " Error: ", ex);
			return false;
		}
		
		return true;
	}
	
	/**
	 * This method is called when a player leaves a specific zone.<br>
	 * It checks if any active quests are triggered by leaving the {@code zoneName}.<br>
	 * If a quest is found, it executes the corresponding event for that quest.
	 * @param env The current quest environment context.
	 * @param zoneName The name of the zone the player is leaving.
	 * @return {@code true} if the process completed successfully, or {@code false} if an error occurred.
	 */
	public boolean onLeaveZone(QuestEnv env, ZoneName zoneName)
	{
		try
		{
			if (questOnLeaveZone.containsKey(zoneName))
			{
				final TIntArrayList leaveZoneList = questOnLeaveZone.get(zoneName);
				for (int i = 0; i < leaveZoneList.size(); i++)
				{
					final QuestHandler questHandler = getQuestHandlerByQuestId(leaveZoneList.get(i));
					if (questHandler != null)
					{
						env.setQuestId(leaveZoneList.get(i));
						questHandler.onLeaveZoneEvent(env, zoneName);
					}
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onLeaveZone - QuestId: " + env.getQuestId() + " Error: ", ex);
			return false;
		}
		
		return true;
	}
	
	/**
	 * This method is called when a movie finishes playing.<br>
	 * It checks for quests that should trigger at the end of a specific movie.<br>
	 * If a quest handler processes the event successfully, it returns {@code true}.
	 * @param env The current quest environment context.
	 * @param movieId The unique identifier of the movie that just ended.
	 * @return {@code true} if any quest was successfully updated, otherwise {@code false}.
	 */
	public boolean onMovieEnd(QuestEnv env, int movieId)
	{
		try
		{
			final TIntArrayList onMovieEndQuests = getOnMovieEndQuests(movieId);
			for (int index = 0; index < onMovieEndQuests.size(); index++)
			{
				env.setQuestId(onMovieEndQuests.get(index));
				final QuestHandler questHandler = getQuestHandlerByQuestId(env.getQuestId());
				if (questHandler != null)
				{
					if (questHandler.onMovieEndEvent(env, movieId))
					{
						return true;
					}
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onMovieEnd - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
		
		return false;
	}
	
	/**
	 * This method is called when a quest timer expires.<br>
	 * It iterates through all quests that have active timers.<br>
	 * For each valid quest, it triggers the {@code onQuestTimerEndEvent} logic.
	 * @param env The current quest environment context.
	 */
	public void onQuestTimerEnd(QuestEnv env)
	{
		for (int questId : questOnTimerEnd)
		{
			final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
			if (questHandler != null)
			{
				env.setQuestId(questId);
				questHandler.onQuestTimerEndEvent(env);
			}
		}
	}
	
	/**
	 * This method is called when an invisible timer finishes.<br>
	 * It iterates through all quest IDs registered for this event.<br>
	 * For each valid ID, it updates the {@code QuestEnv} and triggers the corresponding quest handler.
	 * @param env The current quest environment context.
	 */
	public void onInvisibleTimerEnd(QuestEnv env)
	{
		for (int questId : onInvisibleTimerEnd)
		{
			final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
			if (questHandler != null)
			{
				env.setQuestId(questId);
				questHandler.onQuestTimerEndEvent(env);
			}
		}
	}
	
	/**
	 * Handles the event when a player uses a specific skill.<br>
	 * This method checks if any active quests are linked to the provided {@code skillId}.<br>
	 * If a match is found, it triggers the corresponding quest handler logic.
	 * @param env The current quest environment context.
	 * @param skillId The unique identifier of the skill being used.
	 * @return {@code true} if the process completed successfully, or {@code false} if an error occurred.
	 */
	public boolean onUseSkill(QuestEnv env, int skillId)
	{
		try
		{
			if (questOnUseSkill.containsKey(skillId))
			{
				final TIntArrayList quests = questOnUseSkill.get(skillId);
				for (int i = 0; i < quests.size(); i++)
				{
					final QuestHandler questHandler = getQuestHandlerByQuestId(quests.get(i));
					if (questHandler != null)
					{
						env.setQuestId(quests.get(i));
						questHandler.onUseSkillEvent(env, skillId);
					}
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onUseSkill - QuestId: " + env.getQuestId() + " Error: ", ex);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handles the logic when a player fails to craft an item.<br>
	 * This method checks if the {@code itemId} is linked to a specific quest.<br>
	 * It triggers the failure event if the player does not have the item in their inventory.
	 * @param env The current quest environment context.
	 * @param itemId The unique identifier of the item that failed to be crafted.
	 */
	public void onFailCraft(QuestEnv env, int itemId)
	{
		if (questOnFailCraft.containsKey(itemId))
		{
			final int questId = questOnFailCraft.get(itemId);
			final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
			if (questHandler != null)
			{
				if (env.getPlayer().getInventory().getItemCountByItemId(itemId) == 0)
				{
					env.setQuestId(questId);
					questHandler.onFailCraftEvent(env, itemId);
				}
			}
		}
	}
	
	/**
	 * Handles the event when a player equips an item.<br>
	 * This method checks if any quests are linked to the specific {@code itemId}.<br>
	 * If a match is found, it updates the quest context and triggers the corresponding quest handler.
	 * @param env The current quest environment context.
	 * @param itemId The unique identifier of the item being equipped.
	 */
	public void onEquipItem(QuestEnv env, int itemId)
	{
		if (questOnEquipItem.containsKey(itemId))
		{
			final Set<Integer> questIds = questOnEquipItem.get(itemId);
			for (int questId : questIds)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
				if (questHandler != null)
				{
					env.setQuestId(questId);
					questHandler.onEquipItemEvent(env, itemId);
				}
			}
		}
	}
	
	/**
	 * Checks if a specific quest action can be performed.<br>
	 * This method iterates through all active quests associated with the given {@code templateId}.<br>
	 * It returns {@code false} if any quest handler explicitly blocks the action.
	 * @param env The current quest environment context.
	 * @param templateId The unique identifier for the quest template.
	 * @param questActionType The type of action being attempted.
	 * @param objects Additional objects required for the action check.
	 * @return {@code true} if the action is allowed, or {@code false} if it is blocked.
	 */
	public boolean onCanAct(QuestEnv env, int templateId, QuestActionType questActionType, Object... objects)
	{
		if (questCanAct.containsKey(templateId))
		{
			final TIntArrayList questIds = questCanAct.get(templateId);
			return !questIds.forEach(value ->
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(value);
				if (questHandler != null)
				{
					env.setQuestId(value);
					if (questHandler.onCanAct(env, questActionType, objects))
					{
						return false; // Abort for
					}
				}
				
				return true;
			});
		}
		
		return false;
	}
	
	/**
	 * Handles the logic for rewards given during a Dredgion event.<br>
	 * This method iterates through all quests associated with Dredgion rewards.<br>
	 * It updates the {@code QuestEnv} and triggers the specific reward event for each quest.
	 * @param env The current quest environment context.
	 */
	public void onDredgionReward(QuestEnv env)
	{
		for (int questId : questOnDredgionReward)
		{
			final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
			if (questHandler != null)
			{
				env.setQuestId(questId);
				questHandler.onDredgionRewardEvent(env);
			}
		}
	}
	
	/**
	 * Handles the reward logic for Kamar quests.<br>
	 * This method iterates through all active quest IDs in {@code questOnKamarReward}.<br>
	 * It updates the environment with the current quest ID and triggers the specific handler event.
	 * @param env The quest environment containing the current context.
	 */
	public void onKamarReward(QuestEnv env)
	{
		for (int questId : questOnKamarReward)
		{
			final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
			if (questHandler != null)
			{
				env.setQuestId(questId);
				questHandler.onKamarRewardEvent(env);
			}
		}
	}
	
	/**
	 * Handles the reward logic for Ophidan quests.<br>
	 * This method iterates through all active quest IDs associated with Ophidan rewards.<br>
	 * It updates the {@code QuestEnv} and triggers the specific event for each valid handler.
	 * @param env The current quest environment context.
	 */
	public void onOphidanReward(QuestEnv env)
	{
		for (int questId : questOnOphidanReward)
		{
			final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
			if (questHandler != null)
			{
				env.setQuestId(questId);
				questHandler.onOphidanRewardEvent(env);
			}
		}
	}
	
	/**
	 * Handles the logic for rewards given when a player interacts with a bastion.<br>
	 * This method iterates through all quests that provide bastion rewards.<br>
	 * It updates the {@code QuestEnv} and triggers the specific reward event for each quest.
	 * @param env The current quest environment context.
	 */
	public void onBastionReward(QuestEnv env)
	{
		for (int questId : questOnBastionReward)
		{
			final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
			if (questHandler != null)
			{
				env.setQuestId(questId);
				questHandler.onBastionRewardEvent(env);
			}
		}
	}
	
	/**
	 * Handles events triggered when a bonus is applied to a player.<br>
	 * This method searches for active quests associated with the given {@code BonusType}.<br>
	 * It executes the specific logic defined in the matching quest handler.
	 * @param env The current quest environment context.
	 * @param bonusType The type of bonus being applied.
	 * @param rewardItems The list of items included in the reward.
	 * @return A {@code HandlerResult} indicating if the event was processed successfully.
	 */
	public HandlerResult onBonusApplyEvent(QuestEnv env, BonusType bonusType, List<QuestItems> rewardItems)
	{
		try
		{
			final TIntArrayList lists = getOnBonusApplyQuests(bonusType);
			for (int index = 0; index < lists.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(lists.get(index));
				if (questHandler != null)
				{
					env.setQuestId(lists.get(index));
					return questHandler.onBonusApplyEvent(env, bonusType, rewardItems);
				}
			}
			
			return HandlerResult.UNKNOWN;
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onBonusApply - QuestId: " + env.getQuestId() + " Error: ", ex);
			return HandlerResult.FAILED;
		}
	}
	
	/**
	 * Handles events when an NPC is added to an aggro list.<br>
	 * This method identifies the correct quest based on the visible {@code Npc}.<br>
	 * It then executes the corresponding event for each active quest handler.
	 * @param env The current quest environment context.
	 * @return {@code true} if the process completed successfully, or {@code false} if an error occurred.
	 */
	public boolean onAddAggroList(QuestEnv env)
	{
		try
		{
			final Npc npc = (Npc) env.getVisibleObject();
			for (int questId : getQuestNpc(npc.getNpcId()).getOnAddAggroListEvent())
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
				if (questHandler != null)
				{
					env.setQuestId(questId);
					questHandler.onAddAggroListEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onAddAggroList - QuestId: " + env.getQuestId() + " Error: ", ex);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a player is within a specific distance of a quest NPC.<br>
	 * This method triggers the associated distance events for the active quest.<br>
	 * It returns {@code false} if no valid event exists or the player is too far away.
	 * @param env The current quest environment context.
	 * @return {@code true} if the distance event was successfully processed, otherwise {@code false}.
	 */
	public boolean onAtDistance(QuestEnv env)
	{
		QuestNpc questNpc = null;
		final Npc npc = (Npc) env.getVisibleObject();
		if (!questNpcs.containsKey(npc.getNpcId()))
		{
			return false;
		}
		
		questNpc = getQuestNpc(npc.getNpcId());
		if (getQuestNpc(npc.getNpcId()).getOnDistanceEvent().size() == 0)
		{
			return false;
		}
		
		final Player player = env.getPlayer();
		if (!MathUtil.isIn3dRange(npc, player, 20))
		{
			return false;
		}
		
		try
		{
			for (int questId : questNpc.getOnDistanceEvent())
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questId);
				if (questHandler != null)
				{
					env.setQuestId(questId);
					questHandler.onAtDistanceEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onAtDistance - QuestId: " + env.getQuestId() + " Error: ", ex);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handles events triggered when a player enters a wind stream.<br>
	 * This method iterates through all registered quest handlers for this event.<br>
	 * It updates the current quest ID and executes the corresponding logic.
	 * @param env The {@code QuestEnv} object containing the current quest context.
	 * @param loc The location identifier where the event occurred.
	 */
	public void onEnterWindStream(QuestEnv env, int loc)
	{
		try
		{
			for (int index = 0; index < questOnEnterWindStream.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questOnEnterWindStream.get(index));
				if (questHandler != null)
				{
					env.setQuestId(questOnEnterWindStream.get(index));
					questHandler.onEnterWindStreamEvent(env, loc);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in onEnterWindStram - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * Executes the ride action for all active quests.<br>
	 * This method iterates through quest handlers and triggers their specific logic.<br>
	 * It updates the {@code QuestEnv} with the current quest ID during execution.
	 * @param env The environment containing the current quest state.
	 * @param itemId The unique identifier of the item being used for the ride action.
	 */
	public void rideAction(QuestEnv env, int itemId)
	{
		try
		{
			for (int index = 0; index < questRideAction.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questRideAction.get(index));
				if (questHandler != null)
				{
					env.setQuestId(questRideAction.get(index));
					questHandler.rideAction(env, itemId);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("[QuestEngine] exception in rideAction - QuestId: " + env.getQuestId() + " Error: ", ex);
		}
	}
	
	/**
	 * Handles events triggered when a player reaches a creativity point.<br>
	 * This method iterates through all registered quest IDs for this event.<br>
	 * It updates the {@code QuestEnv} with the current quest ID and executes the corresponding handler.
	 * @param env The environment context containing quest information.
	 */
	public void onCreativityPoint(QuestEnv env)
	{
		try
		{
			for (int index = 0; index < questOnCreativityPoint.size(); index++)
			{
				final QuestHandler questHandler = getQuestHandlerByQuestId(questOnCreativityPoint.get(index));
				if (questHandler != null)
				{
					env.setQuestId(questOnCreativityPoint.get(index));
					questHandler.onCreativityPointEvent(env);
				}
			}
		}
		catch (Exception ex)
		{
			// log.error("QE: exception in onCreativityPoint", ex);
		}
	}
	
	/**
	 * Registers a new {@link QuestNpc} into the system.<br>
	 * If the {@code npcId} is not already present, it creates a new instance.<br>
	 * This method ensures that every unique ID has an associated quest NPC object.
	 * @param npcId The unique identifier for the NPC to register.
	 * @return The registered or newly created {@link QuestNpc} object.
	 */
	public QuestNpc registerQuestNpc(int npcId)
	{
		if (!questNpcs.containsKey(npcId))
		{
			questNpcs.put(npcId, new QuestNpc(npcId));
		}
		
		return questNpcs.get(npcId);
	}
	
	/**
	 * Links a specific item to a quest.<br>
	 * This method updates the internal mapping of items and quests.<br>
	 * It ensures that multiple quests can be associated with the same {@code itemId}.
	 * @param itemId The unique identifier for the item.
	 * @param questId The unique identifier for the quest.
	 */
	public void registerQuestItem(int itemId, int questId)
	{
		if (!questItemRelated.containsKey(itemId))
		{
			final TIntArrayList itemRelatedQuests = new TIntArrayList();
			itemRelatedQuests.add(questId);
			questItemRelated.put(itemId, itemRelatedQuests);
		}
		else
		{
			questItemRelated.get(itemId).add(questId);
		}
	}
	
	/**
	 * Adds a specific quest ID to the list of registered house items.<br>
	 * This method ensures that the {@code questId} is only added once.
	 * @param questId The unique identifier for the quest item.
	 */
	public void registerQuestHouseItem(int questId)
	{
		if (!questHouseItems.contains(questId))
		{
			questHouseItems.add(questId);
		}
	}
	
	/**
	 * Registers an item as a requirement for a specific quest.<br>
	 * This method links the {@code itemId} to the provided {@code questId}.<br>
	 * It updates the internal quest items map used by the engine.
	 * @param itemId The unique identifier of the item.
	 * @param questId The unique identifier of the quest.
	 */
	public void registerGetingItem(int itemId, int questId)
	{
		if (!questItems.containsKey(itemId))
		{
			final TIntArrayList questItemsToReg = new TIntArrayList();
			questItemsToReg.add(questId);
			questItems.put(itemId, questItemsToReg);
		}
		else
		{
			questItems.get(itemId).add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to trigger when a player levels up.<br>
	 * This method adds the {@code questId} to the internal tracking list.<br>
	 * It ensures that each unique ID is only added once.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnLevelUp(int questId)
	{
		if (!questOnLevelUp.contains(questId))
		{
			questOnLevelUp.add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to trigger when entering a zone mission ends.<br>
	 * This method adds the {@code questId} to the internal tracking list.<br>
	 * It ensures that each unique ID is only added once.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnEnterZoneMissionEnd(int questId)
	{
		if (!questOnEnterZoneMissionEnd.contains(questId))
		{
			questOnEnterZoneMissionEnd.add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to trigger an event when entering the world.<br>
	 * This method adds the {@code questId} to the internal tracking list.<br>
	 * It ensures that each unique ID is only added once.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnEnterWorld(int questId)
	{
		if (!questOnEnterWorld.contains(questId))
		{
			questOnEnterWorld.add(questId);
		}
	}
	
	/**
	 * Registers a quest to trigger an action when a player dies.<br>
	 * This method adds the {@code questId} to the internal tracking list.<br>
	 * It ensures that each unique ID is only added once.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnDie(int questId)
	{
		if (!questOnDie.contains(questId))
		{
			questOnDie.add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to trigger logic when a player logs out.<br>
	 * This method adds the {@code questId} to the internal tracking set.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnLogOut(int questId)
	{
		if (!questOnLogOut.contains(questId))
		{
			questOnLogOut.add(questId);
		}
	}
	
	/**
	 * Registers a quest to trigger when a player enters a specific zone.<br>
	 * This method links a {@code questId} to a {@link ZoneName}.<br>
	 * It updates the internal mapping used by the {@link QuestEngine}.
	 * @param zoneName The name of the zone where the event occurs.
	 * @param questId The unique identifier for the quest to register.
	 */
	public void registerOnEnterZone(ZoneName zoneName, int questId)
	{
		if (!questOnEnterZone.containsKey(zoneName))
		{
			final TIntArrayList onEnterZoneQuests = new TIntArrayList();
			onEnterZoneQuests.add(questId);
			questOnEnterZone.put(zoneName, onEnterZoneQuests);
		}
		else
		{
			questOnEnterZone.get(zoneName).add(questId);
		}
	}
	
	/**
	 * Registers a quest to trigger when a player leaves a specific zone.<br>
	 * This method adds the {@code questId} to the list of quests associated with {@code zoneName}.<br>
	 * It ensures that multiple quests can be linked to the same zone.
	 * @param zoneName The name of the zone to monitor for leaving events.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnLeaveZone(ZoneName zoneName, int questId)
	{
		if (!questOnLeaveZone.containsKey(zoneName))
		{
			final TIntArrayList onLeaveZoneQuests = new TIntArrayList();
			onLeaveZoneQuests.add(questId);
			questOnLeaveZone.put(zoneName, onLeaveZoneQuests);
		}
		else
		{
			questOnLeaveZone.get(zoneName).add(questId);
		}
	}
	
	/**
	 * Registers a quest to be triggered by killing enemies for specific ranks.<br>
	 * This method maps the {@code questId} to all ranks starting from the provided {@code playerRank}.<br>
	 * It ensures that quests are correctly associated with the required abyss rank requirements.
	 * @param playerRank The initial {@link AbyssRankEnum} rank to start registration from.
	 * @param questId The unique identifier for the quest being registered.
	 */
	public void registerOnKillRanked(AbyssRankEnum playerRank, int questId)
	{
		for (int rank = playerRank.getId(); rank < 19; rank++)
		{
			if (!questOnKillRanked.containsKey(AbyssRankEnum.getRankById(rank)))
			{
				final TIntArrayList onKillRankedQuests = new TIntArrayList();
				onKillRankedQuests.add(questId);
				questOnKillRanked.put(AbyssRankEnum.getRankById(rank), onKillRankedQuests);
			}
			else
			{
				questOnKillRanked.get(AbyssRankEnum.getRankById(rank)).add(questId);
			}
		}
	}
	
	/**
	 * Registers a quest to be tracked for kills within a specific world.<br>
	 * This method adds the {@code questId} to the list of quests associated with {@code worldId}.<br>
	 * It ensures that multiple quests can be linked to the same world.
	 * @param worldId The unique identifier of the world where the kill event occurs.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnKillInWorld(int worldId, int questId)
	{
		if (!questOnKillInWorld.containsKey(worldId))
		{
			final TIntArrayList killInWorldQuests = new TIntArrayList();
			killInWorldQuests.add(questId);
			questOnKillInWorld.put(worldId, killInWorldQuests);
		}
		else
		{
			questOnKillInWorld.get(worldId).add(questId);
		}
	}
	
	/**
	 * Registers a quest to trigger when a player passes through a flying ring.<br>
	 * This method links a specific {@code String} ring identifier with a {@code int} quest ID.<br>
	 * It updates the internal mapping used by {@code String)}.
	 * @param flyingRing The unique name of the flying ring.
	 * @param questId The unique identifier for the quest to register.
	 */
	public void registerOnPassFlyingRings(String flyingRing, int questId)
	{
		if (!questOnPassFlyingRings.containsKey(flyingRing))
		{
			final TIntArrayList onPassFlyingRingsQuests = new TIntArrayList();
			onPassFlyingRingsQuests.add(questId);
			questOnPassFlyingRings.put(flyingRing, onPassFlyingRingsQuests);
		}
		else
		{
			questOnPassFlyingRings.get(flyingRing).add(questId);
		}
	}
	
	/**
	 * Registers a quest to be triggered when a specific movie finishes.<br>
	 * This method links a {@code questId} to a {@code moveId}.<br>
	 * It ensures that multiple quests can be associated with the same movie.
	 * @param moveId The unique identifier for the movie.
	 * @param questId The unique identifier for the quest.
	 */
	public void registerOnMovieEndQuest(int moveId, int questId)
	{
		if (!questOnMovieEnd.containsKey(moveId))
		{
			final TIntArrayList onMovieEndQuests = new TIntArrayList();
			onMovieEndQuests.add(questId);
			questOnMovieEnd.put(moveId, onMovieEndQuests);
		}
		else
		{
			questOnMovieEnd.get(moveId).add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to trigger an event when its timer expires.<br>
	 * This method adds the {@code questId} to the internal tracking list.<br>
	 * It ensures that each unique ID is only added once.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnQuestTimerEnd(int questId)
	{
		if (!questOnTimerEnd.contains(questId))
		{
			questOnTimerEnd.add(questId);
		}
	}
	
	/**
	 * Registers a quest to be tracked when an invisible timer ends.<br>
	 * This method adds the {@code questId} to the internal tracking set.<br>
	 * It ensures that each unique ID is only added once.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnInvisibleTimerEnd(int questId)
	{
		if (!onInvisibleTimerEnd.contains(questId))
		{
			onInvisibleTimerEnd.add(questId);
		}
	}
	
	/**
	 * Links a specific skill to a quest.<br>
	 * This method adds the {@code questId} to the list of quests associated with {@code skillId}.<br>
	 * It ensures that multiple quests can be linked to the same skill.
	 * @param skillId The unique identifier for the skill.
	 * @param questId The unique identifier for the quest.
	 */
	public void registerQuestSkill(int skillId, int questId)
	{
		if (!questOnUseSkill.containsKey(skillId))
		{
			final TIntArrayList questSkills = new TIntArrayList();
			questSkills.add(questId);
			questOnUseSkill.put(skillId, questSkills);
		}
		else
		{
			questOnUseSkill.get(skillId).add(questId);
		}
	}
	
	/**
	 * Registers a specific item to trigger a quest failure.<br>
	 * This method maps an {@code itemId} to a {@code questId}.<br>
	 * It ensures that the mapping is only created if it does not already exist.
	 * @param itemId The unique identifier of the item.
	 * @param questId The unique identifier of the quest associated with this failure.
	 */
	public void registerOnFailCraft(int itemId, int questId)
	{
		if (!questOnFailCraft.containsKey(itemId))
		{
			questOnFailCraft.put(itemId, questId);
		}
	}
	
	/**
	 * Registers a specific quest to an item ID.<br>
	 * This method links a {@code questId} to an {@code itemId}.<br>
	 * It ensures that multiple quests can be associated with the same item.
	 * @param itemId The unique identifier of the item.
	 * @param questId The unique identifier of the quest.
	 */
	public void registerOnEquipItem(int itemId, int questId)
	{
		if (!questOnEquipItem.containsKey(itemId))
		{
			final Set<Integer> questIds = new HashSet<>();
			questIds.add(questId);
			questOnEquipItem.put(itemId, questIds);
		}
		else
		{
			questOnEquipItem.get(itemId).add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to be active for a given template.<br>
	 * This method links the {@code questId} to the provided {@code templateId}.<br>
	 * It ensures that multiple quests can be associated with the same template.
	 * @param questId The unique identifier of the quest.
	 * @param templateId The unique identifier of the template.
	 */
	public void registerCanAct(int questId, int templateId)
	{
		if (!questCanAct.containsKey(templateId))
		{
			final TIntArrayList questSkills = new TIntArrayList();
			questSkills.add(questId);
			questCanAct.put(templateId, questSkills);
		}
		else
		{
			questCanAct.get(templateId).add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to the Dredgion reward system.<br>
	 * This method adds the {@code questId} to the internal tracking list if it is not already present.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnDredgionReward(int questId)
	{
		if (!questOnDredgionReward.contains(questId))
		{
			questOnDredgionReward.add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to receive rewards from Kamar.<br>
	 * This method adds the {@code questId} to the internal tracking list.<br>
	 * It ensures that each ID is only added once.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnKamarReward(int questId)
	{
		if (!questOnKamarReward.contains(questId))
		{
			questOnKamarReward.add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to receive an Ophidan reward.<br>
	 * This method adds the {@code questId} to the internal tracking list if it is not already present.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnOphidanReward(int questId)
	{
		if (!questOnOphidanReward.contains(questId))
		{
			questOnOphidanReward.add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to receive rewards from the Bastion.<br>
	 * This method adds the {@code questId} to the internal tracking list.<br>
	 * It ensures that each ID is only added once.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnBastionReward(int questId)
	{
		if (!questOnBastionReward.contains(questId))
		{
			questOnBastionReward.add(questId);
		}
	}
	
	/**
	 * Registers a quest to be triggered when a specific bonus is applied.<br>
	 * This method adds the {@code questId} to the list associated with the {@code bonusType}.
	 * @param questId The unique identifier of the quest.
	 * @param bonusType The type of bonus that triggers this registration.
	 */
	public void registerOnBonusApply(int questId, BonusType bonusType)
	{
		if (!questOnBonusApply.containsKey(bonusType))
		{
			final TIntArrayList onBonusApplyQuests = new TIntArrayList();
			onBonusApplyQuests.add(questId);
			questOnBonusApply.put(bonusType, onBonusApplyQuests);
		}
		else
		{
			questOnBonusApply.get(bonusType).add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to trigger an event when its target is reached.<br>
	 * This method adds the {@code questId} to the internal tracking list.<br>
	 * It ensures that each unique ID is only added once.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerAddOnReachTargetEvent(int questId)
	{
		if (!reachTarget.contains(questId))
		{
			reachTarget.add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to receive lost target events.<br>
	 * This method adds the {@code questId} to the internal tracking list.<br>
	 * It ensures that each ID is only added once.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerAddOnLostTargetEvent(int questId)
	{
		if (!lostTarget.contains(questId))
		{
			lostTarget.add(questId);
		}
	}
	
	/**
	 * Registers a quest to trigger when entering the wind stream.<br>
	 * This method adds the {@code questId} to the internal tracking list.<br>
	 * It ensures that no duplicate IDs are added to the collection.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnEnterWindStream(int questId)
	{
		if (!questOnEnterWindStream.contains(questId))
		{
			questOnEnterWindStream.add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to the ride action list.<br>
	 * This method ensures that the {@code questId} is added to the internal tracking set.<br>
	 * It prevents duplicate entries for the same quest.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnRide(int questId)
	{
		if (!questRideAction.contains(questId))
		{
			questRideAction.add(questId);
		}
	}
	
	/**
	 * Registers a specific quest to the creativity point system.<br>
	 * This method adds the {@code questId} to the internal tracking set if it is not already present.
	 * @param questId The unique identifier of the quest to register.
	 */
	public void registerOnCreativityPoint(int questId)
	{
		if (!questOnCreativityPoint.contains(questId))
		{
			questOnCreativityPoint.add(questId);
		}
	}
	
	/**
	 * Retrieves the list of quests associated with a specific bonus type.<br>
	 * It checks the {@code questOnBonusApply} map for any matching entries.<br>
	 * If no quests are found, it returns an empty {@code TIntArrayList}.
	 * @param bonusType The {@link BonusType} to filter quests by.
	 * @return A {@code TIntArrayList} containing the relevant quest IDs.
	 */
	private TIntArrayList getOnBonusApplyQuests(BonusType bonusType)
	{
		if (questOnBonusApply.containsKey(bonusType))
		{
			return questOnBonusApply.get(bonusType);
		}
		
		return new TIntArrayList();
	}
	
	/**
	 * Retrieves a {@link QuestNpc} object based on the provided ID.<br>
	 * If the ID exists in the cache, it returns the stored instance.<br>
	 * Otherwise, it creates and returns a new {@code QuestNpc} object.
	 * @param npcId The unique identifier for the NPC.
	 * @return The corresponding {@link QuestNpc} or a new instance if not found.
	 */
	public QuestNpc getQuestNpc(int npcId)
	{
		if (questNpcs.containsKey(npcId))
		{
			return questNpcs.get(npcId);
		}
		
		return new QuestNpc(npcId);
	}
	
	/**
	 * Retrieves a specific {@link DialogAction} from the internal map.<br>
	 * This method looks up the action using the provided unique identifier.
	 * @param dialogId The unique ID of the dialog to retrieve.
	 * @return The corresponding {@code DialogAction} object, or {@code null} if it does not exist.
	 */
	public DialogAction getDialog(int dialogId)
	{
		if (dialogMap.containsKey(dialogId))
		{
			return dialogMap.get(dialogId);
		}
		
		return null;
	}
	
	/**
	 * Retrieves a list of quests associated with a specific item.<br>
	 * This method checks the {@code questItemRelated} map for the given ID.<br>
	 * It returns an empty {@code TIntArrayList} if no related quests are found.
	 * @param itemId The unique identifier of the item to check.
	 * @return A {@code TIntArrayList} containing the IDs of related quests.
	 */
	private TIntArrayList getItemRelatedQuests(int itemId)
	{
		if (questItemRelated.containsKey(itemId))
		{
			return questItemRelated.get(itemId);
		}
		
		return new TIntArrayList();
	}
	
	/**
	 * Retrieves the list of quests triggered when entering a specific zone.<br>
	 * It checks the {@code questOnEnterZone} map for the provided {@code zoneName}.<br>
	 * If no quests are found, it returns an empty {@code TIntArrayList}.
	 * @param zoneName The name of the zone to check.
	 * @return A list of quest IDs associated with entering the zone.
	 */
	private TIntArrayList getOnEnterZoneQuests(ZoneName zoneName)
	{
		if (questOnEnterZone.containsKey(zoneName))
		{
			return questOnEnterZone.get(zoneName);
		}
		
		return new TIntArrayList();
	}
	
	/**
	 * Retrieves the list of ranked kill quests for a specific rank.<br>
	 * It checks if any quests are mapped to the provided {@code AbyssRankEnum}.<br>
	 * If no quests exist for that rank, it returns an empty {@code TIntArrayList}.
	 * @param playerRank The current rank of the player used to filter quests.
	 * @return A list of quest IDs associated with the given rank.
	 */
	private TIntArrayList getOnKillRankedQuests(AbyssRankEnum playerRank)
	{
		if (questOnKillRanked.containsKey(playerRank))
		{
			return questOnKillRanked.get(playerRank);
		}
		
		return new TIntArrayList();
	}
	
	/**
	 * Retrieves the list of quests associated with a specific flying ring.<br>
	 * It checks the {@code questOnPassFlyingRings} map for the provided key.<br>
	 * If no quests are found, it returns an empty {@code TIntArrayList}.
	 * @param flyingRing The unique identifier for the flying ring.
	 * @return A {@code TIntArrayList} containing the relevant quest IDs.
	 */
	private TIntArrayList getOnPassFlyingRingsQuests(String flyingRing)
	{
		if (questOnPassFlyingRings.containsKey(flyingRing))
		{
			return questOnPassFlyingRings.get(flyingRing);
		}
		
		return new TIntArrayList();
	}
	
	/**
	 * Retrieves the list of quests triggered when a specific movie ends.<br>
	 * It checks the {@code questOnMovieEnd} map for the provided ID.<br>
	 * If no quests are found, it returns an empty {@code TIntArrayList}.
	 * @param moveId The unique identifier of the movie.
	 * @return A {@code TIntArrayList} containing the associated quest IDs.
	 */
	private TIntArrayList getOnMovieEndQuests(int moveId)
	{
		if (questOnMovieEnd.containsKey(moveId))
		{
			return questOnMovieEnd.get(moveId);
		}
		
		return new TIntArrayList();
	}
	
	/**
	 * Retrieves the {@link QuestHandler} associated with a specific ID.<br>
	 * This method looks up the handler in the internal map.
	 * @param questId The unique identifier for the quest.
	 * @return The {@code QuestHandler} object, or {@code null} if not found.
	 */
	private QuestHandler getQuestHandlerByQuestId(int questId)
	{
		return questHandlers.get(questId);
	}
	
	/**
	 * Checks if a specific quest has an assigned handler.<br>
	 * This method looks up the {@code questId} in the internal registry.
	 * @param questId The unique identifier of the quest to check.
	 * @return {@code true} if the quest has a handler, {@code false} otherwise.
	 */
	public boolean isHaveHandler(int questId)
	{
		return questHandlers.containsKey(questId);
	}
	
	/**
	 * Registers a new {@link QuestHandler} into the system.<br>
	 * This method calls the {@code register()} method on the provided handler.<br>
	 * It also checks for and logs warnings if a duplicate {@code questId} exists.
	 * @param questHandler The {@code QuestHandler} instance to be added.
	 */
	public void addQuestHandler(QuestHandler questHandler)
	{
		questHandler.register();
		final int questId = questHandler.getQuestId();
		if (questHandlers.containsKey(questId))
		{
			log.warn("[QuestEngine] Duplicate quest: " + questId);
		}
		
		questHandlers.put(questId, questHandler);
	}
	
	/**
	 * Registers a new side drop for a specific quest.<br>
	 * This method creates a {@code HandlerSideDrop} and adds it to the system.
	 * @param questId The unique identifier of the quest.
	 * @param npcId The unique identifier of the NPC that will drop the item.
	 * @param itemId The unique identifier of the item to be dropped.
	 * @param amount The quantity of the item to give to the player.
	 * @param chance The probability percentage for the drop to occur.
	 */
	public void addHandlerSideQuestDrop(int questId, int npcId, int itemId, int amount, int chance)
	{
		final HandlerSideDrop hsd = new HandlerSideDrop(questId, npcId, itemId, amount, chance);
		QuestService.addQuestDrop(hsd.getNpcId(), hsd);
	}
	
	/**
	 * Registers a side drop for a specific quest.<br>
	 * This method creates a new {@code HandlerSideDrop} and adds it to the active quest drops.
	 * @param questId The unique identifier of the quest.
	 * @param npcId The unique identifier of the NPC that will trigger the drop.
	 * @param itemId The unique identifier of the item to be dropped.
	 * @param amount The quantity of the item to give to the player.
	 * @param chance The probability percentage for the drop to occur.
	 * @param step The specific quest step required to enable this drop.
	 */
	public void addHandlerSideQuestDrop(int questId, int npcId, int itemId, int amount, int chance, int step)
	{
		final HandlerSideDrop hsd = new HandlerSideDrop(questId, npcId, itemId, amount, chance, step);
		QuestService.addQuestDrop(hsd.getNpcId(), hsd);
	}
	
	/**
	 * This method is called when an {@code Npc} spawns in the game world.<br>
	 * It checks if the quest handler for the given {@code questId} requires a specific number of spawns.<br>
	 * If the {@code npcId} is part of that requirement, it removes one instance from the required count.
	 * @param questId The unique identifier for the quest.
	 * @param npcId The unique identifier for the spawned NPC.
	 */
	public void onNotifyNpcSpawned(int questId, int npcId)
	{
		final QuestHandler handler = questHandlers.get(questId);
		if (handler == null)
		{
			return;
		}
		
		final ConstantSpawnHandler checker = handler;
		final HashSet<Integer> allNpcs = checker.getNpcIds();
		if (allNpcs == null)
		{
			return;
		}
		
		allNpcs.remove(npcId);
	}
	
	/**
	 * Identifies and logs quests that have missing NPC spawns.<br>
	 * This method checks all {@code QuestHandler} objects for valid NPC IDs.<br>
	 * If any quest is missing required NPCs, it prints a warning to the log.
	 */
	public void printMissingSpawns()
	{
		final StringBuilder sb = new StringBuilder();
		for (QuestHandler handler : questHandlers.values())
		{
			if (handler == null)
			{
				return;
			}
			
			final HashSet<Integer> allNpcs = handler.getNpcIds();
			if ((allNpcs == null) || (allNpcs.size() == 0))
			{
				return;
			}
			
			sb.append("Q" + handler.getQuestId() + ": ");
			for (Integer npcId : allNpcs)
			{
				sb.append(Integer.toString(npcId) + "; ");
			}
		}
		
		if (sb.length() > 0)
		{
			sb.insert(0, "[QuestEngine] Missing spawn for quest npcs:\n");
			log.warn(sb.toString());
		}
	}
	
	// Loading the QE on start up
	/**
	 * Starts the loading process for quest data and handlers.<br>
	 * This method initializes the {@code ScriptManager} and registers all quests from the XML files.<br>
	 * It also populates the dialog map and updates quest drop information.
	 * @param progressLatch A {@code CountDownLatch} used to track the loading progress. If it is {@code null}, no action is taken.
	 */
	@Override
	public void load(CountDownLatch progressLatch)
	{
		GameServer.log.info("[QuestEngine] Quest engine load started");
		
		final QuestsData questData = DataManager.QUEST_DATA;
		for (QuestTemplate data : questData.getQuestsData())
		{
			for (QuestDrop drop : data.getQuestDrop())
			{
				drop.setQuestId(data.getId());
				QuestService.addQuestDrop(drop.getNpcId(), drop);
			}
			
			if (data.getInventoryItems() != null)
			{
				for (InventoryItem inventoryItem : data.getInventoryItems().getInventoryItem())
				{
					final ItemTemplate item = DataManager.ITEM_DATA.getItemTemplate(inventoryItem.getItemId());
					item.setQuestUpdateItem(true);
				}
			}
		}
		
		scriptManager = new ScriptManager();
		
		final AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new QuestHandlerLoader());
		scriptManager.setGlobalClassListener(acl);
		
		try
		{
			final File questDescription = new File("./data/scripts/system/quest_handlers.xml");
			scriptManager.load(questDescription);
			final XMLQuests xmlQuests = DataManager.XML_QUESTS;
			for (XMLQuest xmlQuest : xmlQuests.getQuest())
			{
				xmlQuest.register(this);
			}
			
			GameServer.log.info("[QuestEngine] Loaded " + questHandlers.size() + " quest handlers.");
		}
		catch (Exception e)
		{
			throw new GameServerError("[QuestEngine] Can't initialize quest handlers.", e);
		}
		finally
		{
			if (progressLatch != null)
			{
				progressLatch.countDown();
			}
		}
		
		addMessageSendingTask();
		for (DialogAction d : DialogAction.values())
		{
			dialogMap.put(d.id(), d);
		}
	}
	
	// reloading the QE by request
	/**
	 * Reloads the quest engine and its associated scripts.<br>
	 * This method initializes the {@code ScriptManager} and registers all quests from {@code XML_QUESTS}.<br>
	 * It also populates the internal dialog map with all available {@code DialogAction} values.
	 * @param progressLatch A {@code CountDownLatch} used to signal when the reload process is complete. If {@code null}, no signal is sent.
	 */
	public void reload(CountDownLatch progressLatch)
	{
		log.info("[QuestEngine] Quest engine reload started");
		
		scriptManager = new ScriptManager();
		
		final AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new QuestHandlerLoader());
		scriptManager.setGlobalClassListener(acl);
		
		try
		{
			final File questDescription = new File("./data/scripts/system/quest_handlers.xml");
			scriptManager.load(questDescription);
			final XMLQuests xmlQuests = DataManager.XML_QUESTS;
			for (XMLQuest xmlQuest : xmlQuests.getQuest())
			{
				xmlQuest.register(this);
			}
			
			log.info("[QuestEngine] ReLoaded " + questHandlers.size() + " quest handlers.");
		}
		catch (Exception e)
		{
			throw new GameServerError("[QuestEngine] Can't initialize quest handlers.", e);
		}
		finally
		{
			if (progressLatch != null)
			{
				progressLatch.countDown();
			}
		}
		
		addMessageSendingTask();
		for (DialogAction d : DialogAction.values())
		{
			dialogMap.put(d.id(), d);
		}
	}
	
	/**
	 * Schedules a recurring task to send daily and weekly quest messages.<br>
	 * This method calculates the next execution time for 9:00 AM.<br>
	 * It uses {@link ThreadPoolManager} to run every 24 hours.<br>
	 * The task notifies players about repeatable quests and updates their local data.
	 */
	private void addMessageSendingTask()
	{
		final Calendar sendingDate = Calendar.getInstance();
		sendingDate.set(Calendar.AM_PM, Calendar.AM);
		sendingDate.set(Calendar.HOUR, 9);
		sendingDate.set(Calendar.MINUTE, 0);
		sendingDate.set(Calendar.SECOND, 0); // current date 09:00
		if (sendingDate.getTime().getTime() < System.currentTimeMillis())
		{
			sendingDate.add(Calendar.HOUR, 24); // next day 09:00
		}
		
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			final SM_SYSTEM_MESSAGE dailyMessage = new SM_SYSTEM_MESSAGE(1400854);
			final SM_SYSTEM_MESSAGE weeklyMessage = new SM_SYSTEM_MESSAGE(1400856);
			for (Player player : World.getInstance().getAllPlayers())
			{
				for (QuestState qs : player.getQuestStateList().getAllQuestState())
				{
					if ((qs != null) && qs.canRepeat())
					{
						questsToRepeat.add(qs.getQuestId());
						final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(qs.getQuestId());
						if (template.isDaily())
						{
							player.getController().updateZone();
							player.getController().updateNearbyQuests();
							PacketSendUtility.sendPacket(player, dailyMessage);
						}
						else if (template.isWeekly())
						{
							player.getController().updateZone();
							player.getController().updateNearbyQuests();
							PacketSendUtility.sendPacket(player, weeklyMessage);
						}
					}
				}
				
				player.getNpcFactions().sendDailyQuest();
				PacketSendUtility.sendPacket(player, new SM_QUEST_REPEAT(questsToRepeat));
				questsToRepeat.clear();
			}
		}, sendingDate.getTimeInMillis() - System.currentTimeMillis(), 1000 * 60 * 60 * 24);
	}
	
	// Clearing the QE on reload admin command
	/**
	 * Shuts down the quest engine and its associated script manager.<br>
	 * This method clears all internal data and sets the {@code scriptManager} to {@code null}.<br>
	 * It logs a message confirming that quests have been shut down.
	 */
	@Override
	public void shutdown()
	{
		scriptManager.shutdown();
		clear();
		scriptManager = null;
		log.info("[QuestEngine] Quests are shutdown...");
	}
	
	/**
	 * Clears all internal quest data and listeners.<br>
	 * This method resets the state of various quest-related collections.<br>
	 * It is used to wipe active quest information from memory.
	 */
	public void clear()
	{
		questNpcs.clear();
		questItemRelated.clear();
		questItems.clear();
		questHouseItems.clear();
		questOnLevelUp.clear();
		questOnEnterZoneMissionEnd.clear();
		questOnEnterWorld.clear();
		questOnDie.clear();
		questOnLogOut.clear();
		questOnEnterZone.clear();
		questOnLeaveZone.clear();
		questOnMovieEnd.clear();
		questOnTimerEnd.clear();
		questOnPassFlyingRings.clear();
		questOnKillRanked.clear();
		questOnUseSkill.clear();
		reachTarget.clear();
		lostTarget.clear();
		questOnEnterWindStream.clear();
		questRideAction.clear();
		questOnCreativityPoint.clear();
		questHandlers.clear();
	}
	
	private static class SingletonHolder
	{
		protected static final QuestEngine instance = new QuestEngine();
	}
}
