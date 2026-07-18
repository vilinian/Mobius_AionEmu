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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.QuestsData;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.DropNpc;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.player.npcFaction.NpcFaction;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.common.legacy.LootRuleType;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.quest.CollectItem;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.model.templates.quest.HandlerSideDrop;
import com.aionemu.gameserver.model.templates.quest.InventoryItem;
import com.aionemu.gameserver.model.templates.quest.InventoryItems;
import com.aionemu.gameserver.model.templates.quest.QuestBonuses;
import com.aionemu.gameserver.model.templates.quest.QuestCategory;
import com.aionemu.gameserver.model.templates.quest.QuestDrop;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.quest.QuestMentorType;
import com.aionemu.gameserver.model.templates.quest.QuestRepeatCycle;
import com.aionemu.gameserver.model.templates.quest.QuestTarget;
import com.aionemu.gameserver.model.templates.quest.QuestWorkItems;
import com.aionemu.gameserver.model.templates.quest.Rewards;
import com.aionemu.gameserver.model.templates.quest.XMLStartCondition;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.models.WorkOrdersData;
import com.aionemu.gameserver.questEngine.handlers.models.XMLQuest;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.player.PlayerFameService;
import com.aionemu.gameserver.services.reward.BonusService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * Manages the core quest logic and lifecycle for players within the game server.<br>
 * This service handles quest progression, rewards, and interactions with {@link QuestEngine}.<br>
 * It serves as a primary interface for updating player quest states and processing objectives.
 * @author Mr. Poke
 * @modified vlog, bobobear, xTz, Rolandas
 */
public final class QuestService
{
	static QuestsData questsData = DataManager.QUEST_DATA;
	private static final Logger log = LoggerFactory.getLogger("QUEST_LOG");
	private static Map<Integer, List<QuestDrop>> questDrop = new HashMap<>();
	
	/**
	 * Completes the current quest for the player.<br>
	 * This method uses a default reward value of {@code 0}.<br>
	 * It calls the overloaded {@code int)} method.
	 * @param env The environment containing the current quest data.
	 * @return {@code true} if the quest was finished successfully, or {@code false} otherwise.
	 */
	public static boolean finishQuest(QuestEnv env)
	{
		return finishQuest(env, 0);
	}
	
	/**
	 * Completes a quest and grants the associated rewards to the player.<br>
	 * This method checks if the quest is ready for completion based on its status.<br>
	 * It handles standard rewards, extended rewards, and special logic for specific quest IDs.
	 * @param env The {@code QuestEnv} containing the current quest context.
	 * @param reward The index of the reward to be granted from the quest template.
	 * @return {@code true} if the quest was successfully finished, or {@code false} otherwise.
	 */
	public static boolean finishQuest(QuestEnv env, int reward)
	{
		final Player player = env.getPlayer();
		final int id = env.getQuestId();
		final QuestState qs = player.getQuestStateList().getQuestState(id);
		Rewards rewards = new Rewards();
		Rewards extendedRewards = new Rewards();
		final QuestTemplate template = questsData.getQuestById(id);
		
		if ((qs == null) || ((template.getCategory() != QuestCategory.TUTORIAL) && (qs.getStatus() != QuestStatus.REWARD)))
		{
			return false;
		}
		
		if ((template.getCategory() == QuestCategory.EPISODE) && (template.getCategory() == QuestCategory.GUIDE) && (template.getCategory() == QuestCategory.MISSION) && (qs.getCompleteCount() != 0))
		{
			return false; // prevent repeatable reward because of wrong quest handling
		}
		
		final List<QuestItems> questItems = new ArrayList<>();
		if (!template.getExtendedRewards().isEmpty())
		{
			if (qs.getCompleteCount() == (template.getMaxRepeatCount() - 1))
			{
				// This is the last time
				questItems.addAll(getRewardItems(env, template, true, reward));
				extendedRewards = template.getExtendedRewards().get(0);
			}
		}
		
		if (!template.getRewards().isEmpty() || !template.getBonus().isEmpty())
		{
			questItems.addAll(getRewardItems(env, template, false, reward));
			rewards = template.getRewards().get(reward);
		}
		
		if (id == 80899)
		{
			if ((qs.getCompleteCount() + 1) <= 15)
			{
				final int randomItem = Rnd.get(1, 3);
				switch (randomItem)
				{
					case 1:
					{
						ItemService.addItem(player, 188055604, 1);
						break;
					}
					case 2:
					{
						ItemService.addItem(player, 188055605, 1);
						break;
					}
					case 3:
					{
						ItemService.addItem(player, 188055606, 1);
						break;
					}
				}
			}
			else
			{
				qs.setCompleteCount(0);
				final int randomItem = Rnd.get(1, 3);
				switch (randomItem)
				{
					case 1:
					{
						ItemService.addItem(player, 188055604, 1);
						break;
					}
					case 2:
					{
						ItemService.addItem(player, 188055605, 1);
						break;
					}
					case 3:
					{
						ItemService.addItem(player, 188055606, 1);
						break;
					}
				}
				
				ItemService.addItem(player, 188055606, 1);
			}
			
			giveReward(env, rewards);
			giveReward(env, extendedRewards);
			return setFinishingState(env, template, reward);
		}
		
		if (ItemService.addQuestItems(player, questItems))
		{
			giveReward(env, rewards);
			giveReward(env, extendedRewards);
			if (template.getCategory() == QuestCategory.CHALLENGE_TASK)
			{
				ChallengeTaskService.getInstance().onChallengeQuestFinish(player, id);
			}
			
			return setFinishingState(env, template, reward);
		}
		
		if (player.getInventory().isFull())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_WAREHOUSE_FULL_INVENTORY);
			return false;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the list of items a player receives upon completing a quest.<br>
	 * This method determines rewards based on whether the quest is extended and the specific reward index.<br>
	 * It also handles class-specific rewards, selectable rewards from dialogs, and additional bonuses.
	 * @param env The current quest environment containing player and quest data.
	 * @param template The template defining the quest rewards and properties.
	 * @param extended A boolean flag indicating if the quest uses extended reward logic.
	 * @param reward The index of the reward to be granted from the template.
	 * @return A {@code List} of {@code QuestItems} to be given to the player.
	 */
	private static List<QuestItems> getRewardItems(QuestEnv env, QuestTemplate template, boolean extended, int reward)
	{
		final Player player = env.getPlayer();
		final int id = env.getQuestId();
		final List<QuestItems> questItems = new ArrayList<>();
		Rewards rewards;
		if (extended)
		{
			rewards = template.getExtendedRewards().get(0);
		}
		else
		{
			rewards = template.getRewards().get(reward);
		}
		
		questItems.addAll(rewards.getRewardItem());
		final int dialogId = env.getDialogId();
		if ((dialogId != DialogAction.SELECTED_QUEST_NOREWARD.id()) && (dialogId != 0) && !extended)
		{
			final QuestState qs = player.getQuestStateList().getQuestState(id);
			final boolean isLastRepeat = (qs.getCompleteCount() == (template.getMaxRepeatCount() - 1)) && (template.getMaxRepeatCount() < 255);
			if ((isLastRepeat && template.isUseSingleClassReward()) || template.isUseRepeatedClassReward())
			{
				QuestItems classRewardItem = null;
				final PlayerClass playerClass = player.getCommonData().getPlayerClass();
				final int selRewIndex = dialogId != DialogAction.QUEST_AUTO_REWARD.id() ? dialogId - 8 : 0; // For now InstantReward = only 1 selectable reward TODO more ?
				switch (playerClass)
				{
					case ASSASSIN:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getAssassinSelectableReward(), selRewIndex);
						break;
					}
					case CHANTER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getChanterSelectableReward(), selRewIndex);
						break;
					}
					case CLERIC:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getPriestSelectableReward(), selRewIndex);
						break;
					}
					case GLADIATOR:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getFighterSelectableReward(), selRewIndex);
						break;
					}
					case RANGER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getRangerSelectableReward(), selRewIndex);
						break;
					}
					case SORCERER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getWizardSelectableReward(), selRewIndex);
						break;
					}
					case SPIRIT_MASTER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getElementalistSelectableReward(), selRewIndex);
						break;
					}
					case TEMPLAR:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getKnightSelectableReward(), selRewIndex);
						break;
					}
					case GUNNER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getGunnerSelectableReward(), selRewIndex);
						break;
					}
					case BARD:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getBardSelectableReward(), selRewIndex);
						break;
					}
					case PAINTER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getPainterSelectableReward(), selRewIndex);
						break;
					}
					case RIDER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getRiderSelectableReward(), selRewIndex);
						break;
					}
					default:
						break;
				}
				
				if (classRewardItem != null)
				{
					questItems.add(classRewardItem);
				}
			}
			else
			{
				QuestItems selectebleRewardItem = null;
				if (((dialogId - 8) >= 0) && ((dialogId - 8) < rewards.getSelectableRewardItem().size()))
				{
					selectebleRewardItem = rewards.getSelectableRewardItem().get(dialogId - 8);
				}
				else
				{
					log.error("The SelectableRewardItem list has no element with the given index (dialogId - 8) of " + (dialogId - 8) + ". See quest id " + env.getQuestId());
				}
				
				if (selectebleRewardItem != null)
				{
					questItems.add(selectebleRewardItem);
				}
			}
		}
		else if ((dialogId == DialogAction.SELECTED_QUEST_NOREWARD.id()) && (dialogId != 0) && !extended)
		{
			final QuestState qs = player.getQuestStateList().getQuestState(id);
			final boolean isLastRepeat = (qs.getCompleteCount() == (template.getMaxRepeatCount() - 1)) && (template.getMaxRepeatCount() < 255);
			if ((isLastRepeat && template.isUseSingleClassReward()) || template.isUseRepeatedClassReward())
			{
				QuestItems classRewardItem = null;
				final PlayerClass playerClass = player.getCommonData().getPlayerClass();
				final int selRewIndex = env.getExtendedRewardIndex() - 8;
				switch (playerClass)
				{
					case ASSASSIN:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getAssassinSelectableReward(), selRewIndex);
						break;
					}
					case CHANTER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getChanterSelectableReward(), selRewIndex);
						break;
					}
					case CLERIC:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getPriestSelectableReward(), selRewIndex);
						break;
					}
					case GLADIATOR:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getFighterSelectableReward(), selRewIndex);
						break;
					}
					case RANGER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getRangerSelectableReward(), selRewIndex);
						break;
					}
					case SORCERER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getWizardSelectableReward(), selRewIndex);
						break;
					}
					case SPIRIT_MASTER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getElementalistSelectableReward(), selRewIndex);
						break;
					}
					case TEMPLAR:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getKnightSelectableReward(), selRewIndex);
						break;
					}
					case GUNNER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getGunnerSelectableReward(), selRewIndex);
						break;
					}
					case BARD:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getBardSelectableReward(), selRewIndex);
						break;
					}
					case PAINTER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getPainterSelectableReward(), selRewIndex);
						break;
					}
					case RIDER:
					{
						classRewardItem = getQuestItemsbyClass(id, template.getRiderSelectableReward(), selRewIndex);
						break;
					}
					default:
						break;
				}
				
				if (classRewardItem != null)
				{
					questItems.add(classRewardItem);
				}
			}
		}
		else if ((dialogId == DialogAction.SELECTED_QUEST_NOREWARD.id()) && extended && !rewards.getSelectableRewardItem().isEmpty())
		{
			QuestItems selectebleRewardItem = null;
			final int index = env.getExtendedRewardIndex();
			if (((index - 8) >= 0) && ((index - 8) < rewards.getSelectableRewardItem().size()))
			{
				selectebleRewardItem = rewards.getSelectableRewardItem().get(index - 8);
			}
			else if (((index - 1) >= 0) && ((index - 1) < rewards.getSelectableRewardItem().size()))
			{
				selectebleRewardItem = rewards.getSelectableRewardItem().get(index - 1);
			}
			else
			{
				log.error("The extended SelectableRewardItem list has no element with the given index (extendedRewardIndex - 8) of " + (index - 8) + ". See quest id " + env.getQuestId() + ". The size is: " + rewards.getSelectableRewardItem().size());
			}
			
			if (selectebleRewardItem != null)
			{
				questItems.add(selectebleRewardItem);
			}
		}
		
		if (!template.getBonus().isEmpty())
		{
			final QuestBonuses bonus = template.getBonus().get(0);
			
			// Handler can add additional bonuses on repeat (for event quests no data)
			final HandlerResult result = QuestEngine.getInstance().onBonusApplyEvent(env, bonus.getType(), questItems);
			if (result != HandlerResult.FAILED)
			{
				final QuestItems additional = BonusService.getInstance().getQuestBonus(player, template);
				if (additional != null)
				{
					questItems.add(additional);
				}
			}
		}
		
		return questItems;
	}
	
	/**
	 * Distributes various rewards to the player based on the provided quest environment.<br>
	 * This method handles gold, experience, titles, abyss points, and other special bonuses.<br>
	 * It also updates minion energy and expands inventory if specified in the {@code Rewards} object.<br>
	 * Finally, it sends a statistics update packet to the player.
	 * @param env The current quest environment containing player and target data.
	 * @param rewards The collection of rewards to be granted to the player.
	 */
	private static void giveReward(QuestEnv env, Rewards rewards)
	{
		final Player player = env.getPlayer();
		if (rewards.getGold() != null)
		{
			player.getInventory().increaseKinah((long) (player.getRates().getQuestKinahRate() * rewards.getGold()), ItemUpdateType.INC_KINAH_QUEST);
		}
		
		if (rewards.getExp() != null)
		{
			final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(env.getTargetId());
			if ((env.getQuestId() == 10521) || (env.getQuestId() == 20521))
			{
				player.getCommonData().addExp(rewards.getExp(), RewardType.QUEST, npcTemplate != null ? npcTemplate.getNameId() : 0, env.getQuestId());
			}
			else
			{
				player.getCommonData().addExp(rewards.getExp(), RewardType.QUEST, npcTemplate != null ? npcTemplate.getNameId() : 0);
			}
		}
		
		if (rewards.getTitle() != null)
		{
			player.getTitleList().addTitle(rewards.getTitle(), true, 0);
		}
		
		if (rewards.getRewardAbyssPoint() != null)
		{
			AbyssPointsService.addAp(player, (int) (player.getRates().getQuestApRate() * rewards.getRewardAbyssPoint()));
		}
		
		if (rewards.getRewardGloryPoint() != null)
		{
			AbyssPointsService.addGp(player, (int) (player.getRates().getQuestApRate() * rewards.getRewardGloryPoint()));
		}
		
		// Growth Energy 5.x
		if (rewards.getExpBoost() != null)
		{
			player.getCommonData().addGrowthEnergy(1060000 * rewards.getExpBoost());
		}
		
		// TODO - Creativity Points 5.x
		if (rewards.getRewardCP() != null)
		{
		}
		
		if (rewards.getFameExp() != null)
		{
			PlayerFameService.getInstance().addFameExp(player, rewards.getFameExp().intValue());
		}
		
		if (player.getMinion() != null)
		{
			MinionService.getInstance().onUpdateEnergy(player, 50);
			MinionService.getInstance().addMinionGrowth(player, 50);
		}
		
		if (rewards.getExtendInventory() != null)
		{
			if (rewards.getExtendInventory() == 1)
			{
				CubeExpandService.expand(player, false);
			}
			else if (rewards.getExtendInventory() == 2)
			{
				WarehouseService.expand(player);
			}
		}
		
		// Send for: "Aura Of Growth & Berdin's Favor"
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
	}
	
	/**
	 * Updates the quest status to complete and handles all final logic.<br>
	 * This method removes required items from the player inventory.<br>
	 * It also updates rewards, repeat timers, and triggers achievements.
	 * @param env The current environment containing the player and quest data.
	 * @param template The template defining the quest properties.
	 * @param reward The amount of reward to assign to the completed quest.
	 * @return {@code true} if the state was updated successfully, otherwise {@code false}.
	 */
	private static boolean setFinishingState(QuestEnv env, QuestTemplate template, int reward)
	{
		final Player player = env.getPlayer();
		final int id = env.getQuestId();
		final QuestState qs = player.getQuestStateList().getQuestState(id);
		
		// remove all worker list item if finished.
		final QuestWorkItems qwi = questsData.getQuestById(id).getQuestWorkItems();
		if (qwi != null)
		{
			long count = 0;
			for (QuestItems qi : qwi.getQuestWorkItem())
			{
				if (qi != null)
				{
					count = player.getInventory().getItemCountByItemId(qi.getItemId());
					if (count > 0)
					{
						if (!player.getInventory().decreaseByItemId(qi.getItemId(), count))
						{
							return false;
						}
					}
				}
			}
		}
		
		qs.setStatus(QuestStatus.COMPLETE);
		qs.setQuestVar(0);
		qs.setReward(reward);
		qs.setCompleteCount(qs.getCompleteCount() + 1);
		if (((template.getRepeatCycle() != null) && (player.getAccessLevel() == 0)) || (template.getQuestCoolTime() > 0))
		{
			qs.setNextRepeatTime(countNextRepeatTime(player, template));
		}
		else if (template.isTimeBased() && (player.getAccessLevel() > 0))
		{
			PacketSendUtility.sendMessage(player, "You're GM! So system won't apply countNextRepeatTime()");
		}
		
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		QuestEngine.getInstance().onLvlUp(env);
		AchievementService.getInstance().onUpdateAchievementAction(player, template.getId(), 1, AchievementActionType.QUEST);
		if (template.getNpcFactionId() != 0)
		{
			player.getNpcFactions().completeQuest(template);
		}
		
		if (template.getId() == id)
		{
			switch (id)
			{
				case 19001:
				{
					player.getSkillList().addSkill(player, 30002, 400);
					break;
				}
				case 19003:
				{
					player.getSkillList().addSkill(player, 30003, 400);
					break;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Retrieves a specific quest item based on the provided class selection list.<br>
	 * It checks if the given index is within the valid range of the list.<br>
	 * If the index is invalid, it logs an error and returns {@code null}.
	 * @param id The unique identifier for the quest.
	 * @param classSelRew A list of {@link QuestItems} associated with a specific class selection.
	 * @param selRewIndex The index of the reward to retrieve from the list.
	 * @return The {@link QuestItems} at the specified index, or {@code null} if the index is out of bounds.
	 */
	private static QuestItems getQuestItemsbyClass(int id, List<QuestItems> classSelRew, int selRewIndex)
	{
		if ((selRewIndex >= 0) && (selRewIndex < classSelRew.size()))
		{
			return classSelRew.get(selRewIndex);
		}
		
		log.error("Wrong selectable reward index " + selRewIndex + " for quest " + id);
		
		return null;
	}
	
	/**
	 * Calculates the next available time a player can repeat a specific quest.<br>
	 * This method handles daily, cooldown-based, and weekly cycle logic.<br>
	 * It also sends a system message to the {@link Player} regarding the wait time.
	 * @param player The {@link Player} who is attempting to repeat the quest.
	 * @param template The {@link QuestTemplate} containing the quest rules.
	 * @return A {@link Timestamp} representing the next allowed completion time.
	 */
	private static Timestamp countNextRepeatTime(Player player, QuestTemplate template)
	{
		final int questCooltime = template.getQuestCoolTime();
		final ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime repeatDate = ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 9, 0, 0, 0, java.time.ZoneId.systemDefault());
		if (template.isDaily())
		{
			if (now.isAfter(repeatDate))
			{
				repeatDate = repeatDate.plusHours(24);
			}
			
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400855, "9"));
		}
		else if (template.getQuestCoolTime() > 0)
		{
			repeatDate = repeatDate.plusSeconds(template.getQuestCoolTime());
			
			// This quest can be re-attempted in %DURATIONDAY0s.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402676, +questCooltime));
		}
		else
		{
			int daysToAdd = 7;
			int startDay = 7;
			for (QuestRepeatCycle weekDay : template.getRepeatCycle())
			{
				final int diff = weekDay.getDay() - repeatDate.getDayOfWeek().getValue();
				if ((diff > 0) && (diff < daysToAdd))
				{
					daysToAdd = diff;
				}
				
				if (startDay > weekDay.getDay())
				{
					startDay = weekDay.getDay();
				}
			}
			
			if (startDay == daysToAdd)
			{
				daysToAdd = 7;
			}
			else if ((daysToAdd == 7) && (startDay < 7))
			{
				daysToAdd = (7 - repeatDate.getDayOfWeek().getValue()) + startDay;
			}
			
			repeatDate = repeatDate.plusDays(daysToAdd);
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400857, new DescriptionId(1800667), "9"));
		}
		
		return new Timestamp(repeatDate.toInstant().toEpochMilli());
	}
	
	/**
	 * Verifies if the player meets all requirements to begin a quest.<br>
	 * This method calls {@code boolean)} internally.<br>
	 * It returns {@code false} if an unexpected error occurs during the check.
	 * @param env The environment containing current quest data.
	 * @param warn Whether to display a warning message to the player.
	 * @return {@code true} if all conditions are met, otherwise {@code false}.
	 */
	public static boolean checkStartConditions(QuestEnv env, boolean warn)
	{
		try
		{
			return checkStartConditionsImpl(env, warn);
		}
		catch (Exception ex)
		{
			log.error("QE: exception in checkStartCondition", ex);
		}
		
		return false;
	}
	
	/**
	 * Performs the internal logic to verify if a player meets all requirements to start a quest.<br>
	 * This method checks race, level, class, gender, rank, titles, and specific XML conditions.<br>
	 * It also validates inventory items and required skill levels based on the quest template.
	 * @param env The environment containing the current player and quest data.
	 * @param warn If {@code true}, it sends a system message to the player explaining why they cannot start the quest.
	 * @return {@code true} if all conditions are met, otherwise {@code false}.
	 */
	private static boolean checkStartConditionsImpl(QuestEnv env, boolean warn)
	{
		final Player player = env.getPlayer();
		final QuestTemplate template = questsData.getQuestById(env.getQuestId());
		
		if (template == null)
		{
			return false;
		}
		
		if (template.getRacePermitted() != null)
		{
			if ((template.getRacePermitted() != player.getRace()) && (template.getRacePermitted() != Race.PC_ALL))
			{
				return false;
			}
		}
		
		// Set the minimum level to 2 so the gray quest arrow appears when a quest is almost available; the quest level will be re-checked in QuestService.startQuest() during the start attempt.
		final int levelDiff = template.getMinlevelPermitted() - player.getLevel();
		if ((levelDiff > 2) && (template.getMinlevelPermitted() != 999))
		{
			return false;
		}
		
		if (warn && (levelDiff > 0) && (template.getMinlevelPermitted() != 999))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_MIN_LEVEL(Integer.toString(template.getMinlevelPermitted())));
			return false;
		}
		
		if ((template.getMaxlevelPermitted() != 0) && (player.getLevel() > template.getMaxlevelPermitted()))
		{
			if (warn)
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_MAX_LEVEL(Integer.toString(template.getMaxlevelPermitted())));
			}
			
			return false;
		}
		
		if (!template.getClassPermitted().isEmpty())
		{
			if (!template.getClassPermitted().contains(player.getCommonData().getPlayerClass()))
			{
				if (warn)
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_CLASS);
				}
				
				return false;
			}
		}
		
		if (template.getGenderPermitted() != null)
		{
			if (template.getGenderPermitted() != player.getGender())
			{
				if (warn)
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_GENDER);
				}
				
				return false;
			}
		}
		
		if (template.getRequiredRank() != 0)
		{
			if (player.getAbyssRank().getRank().getId() < template.getRequiredRank())
			{
				if (warn)
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_MIN_RANK(AbyssRankEnum.getRankById(template.getRequiredRank()).getDescriptionId()));
				}
				
				return false;
			}
		}
		
		if (template.getTitleId() != 0)
		{
			if (!player.getTitleList().contains(template.getTitleId()))
			{
				if (warn)
				{
					// You can only receive this quest when you have the %0 title.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300588, template.getTitleId()));
				}
				
				return false;
			}
		}
		
		final int requiredStartConditions = template.isMaster() ? 6 - CraftConfig.MAX_MASTER_CRAFTING_SKILLS : template.isExpert() ? 8 - CraftConfig.MAX_EXPERT_CRAFTING_SKILLS : 1;
		int fulfilledStartConditions = 0;
		if (!template.getXMLStartConditions().isEmpty())
		{
			for (XMLStartCondition startCondition : template.getXMLStartConditions())
			{
				if (startCondition.check(player, warn))
				{
					fulfilledStartConditions++;
				}
			}
			
			if (((env.getQuestId() >= 19601) && (env.getQuestId() <= 19630)) || ((env.getQuestId() >= 29601) && (env.getQuestId() <= 29630)))
			{
				// Workaround for Wisplight Abbey & Fatebound Abbey Quests
				return true;
			}
			else if (fulfilledStartConditions < requiredStartConditions)
			{
				return false;
			}
		}
		
		if (!inventoryItemCheck(env, warn))
		{
			return false;
		}
		
		if (template.getCombineSkill() != null)
		{
			final List<Integer> skills = new ArrayList<>(); // skills to check
			if (template.getCombineSkill() == -1) // any skill
			{
				skills.add(30002);
				skills.add(30003);
				skills.add(40001);
				skills.add(40002);
				skills.add(40003);
				skills.add(40004);
				skills.add(40007);
				skills.add(40008);
				skills.add(40010);
				skills.add(40011);
			}
			else
			{
				skills.add(template.getCombineSkill());
			}
			
			boolean result = false;
			for (int skillId : skills)
			{
				final PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
				if ((skill != null) && (skill.getSkillLevel() >= template.getCombineSkillPoint()))
				{
					if (template.getCategory().equals(QuestCategory.TASK) && ((skill.getSkillLevel() - 40) > template.getCombineSkillPoint()))
					{
						continue;
					}
					
					result = true;
					break;
				}
			}
			
			if (!result)
			{
				return false;
			}
		}
		
		if (warn && (template.getNpcFactionId() != 0) && !template.isTimeBased())
		{
			if (!player.getNpcFactions().canStartQuest(template))
			{
				AuditLogger.info(player, "try start guild daily quest before time");
				return false;
			}
		}
		
		// Check for updating nearby quests
		final QuestState qs = player.getQuestStateList().getQuestState(template.getId());
		if ((qs != null) && (qs.getStatus() != QuestStatus.NONE))
		{
			if (!qs.canRepeat())
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Starts a quest for the player based on the current environment.<br>
	 * This method initializes the quest state using the provided {@code QuestStatus}.<br>
	 * It automatically determines if a warning should be shown based on the dialog ID.
	 * @param env The environment containing quest data and player information.
	 * @param status The specific status to set for the quest.
	 * @return {@code true} if the quest was started successfully, {@code false} otherwise.
	 */
	public static boolean startQuest(QuestEnv env, QuestStatus status)
	{
		return startQuest(env, status, env.getDialogId() != 0, 0);
	}
	
	/*
	 * Check the starting conditions and start a quest Reworked 12.06.2011
	 * @author vlog
	 */
	
	/**
	 * Starts a quest for the player based on the provided environment and status.<br>
	 * This method validates requirements like level, faction, and quest limits.<br>
	 * It updates the player's quest state and sends the necessary packets.
	 * @param env The {@code QuestEnv} containing current quest context.
	 * @param status The new {@code QuestStatus} to apply to the quest.
	 * @param warn Whether to trigger a warning check during validation.
	 * @param step The specific quest step to set.
	 * @return {@code true} if the quest started successfully, {@code false} otherwise.
	 */
	public static boolean startQuest(QuestEnv env, QuestStatus status, boolean warn, int step)
	{
		final Player player = env.getPlayer();
		final int id = env.getQuestId();
		final QuestStateList qsl = player.getQuestStateList();
		final QuestState qs = qsl.getQuestState(id);
		final QuestTemplate template = questsData.getQuestById(env.getQuestId());
		if (template == null)
		{
			log.info("[QuestService] Can't find Quest: " + env.getQuestId() + " in quest_data.xml");
			return false;
		}
		
		if (template.getNpcFactionId() != 0)
		{
			final NpcFaction faction = player.getNpcFactions().getNpcFactinById(template.getNpcFactionId());
			if (!faction.isActive() || (faction.getQuestId() != env.getQuestId()))
			{
				AuditLogger.info(player, "Possible packet hack learn Guild quest");
				return false;
			}
		}
		
		if (!checkStartConditions(env, true) || ((player.getLevel() < template.getMinlevelPermitted()) && (template.getMinlevelPermitted() != 999)))
		{
			return false;
		}
		
		if (!template.isNoCount() && !checkQuestListSize(qsl) && !player.havePermission(MembershipConfig.QUEST_LIMIT_DISABLED))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300622, template.getName()));
			return false;
		}
		
		if (qs != null)
		{
			if (!qs.canRepeat())
			{
				return false;
			}
			
			qs.setStatus(status);
		}
		else
		{
			player.getQuestStateList().addQuest(id, new QuestState(id, status, step, 0, null, 0, null));
		}
		
		if ((template.getNpcFactionId() != 0) && !template.isTimeBased())
		{
			player.getNpcFactions().startQuest(template);
		}
		
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(id, status.value(), step));
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		return true;
	}
	
	/*
	 * Check the starting conditions and start a quest Reworked 12.06.2011
	 * @author vlog
	 */
	/**
	 * Starts a new quest for the player based on the current environment.<br>
	 * This method automatically determines if a warning is needed and sets the initial step to {@code 0}.<br>
	 * It calls the overloaded {@code QuestStatus, boolean, int)} method.
	 * @param env The environment containing quest data and player information.
	 * @return {@code true} if the quest was started successfully, or {@code false} otherwise.
	 */
	public static boolean startQuest(QuestEnv env)
	{
		return startQuest(env, QuestStatus.START, env.getDialogId() != 0, 0);
	}
	
	/**
	 * Starts a new quest for the player based on the current environment.<br>
	 * This method initializes the quest state and moves to the specified step.
	 * @param env The {@code QuestEnv} object containing the current quest context.
	 * @param step The specific step number to set for the quest.
	 * @return {@code true} if the quest was started successfully, {@code false} otherwise.
	 */
	public static boolean startQuest(QuestEnv env, int step)
	{
		return startQuest(env, QuestStatus.START, env.getDialogId() != 0, step);
	}
	
	/**
	 * Initializes a new quest for the player in the current environment.<br>
	 * This method adds the quest to the {@code Player} quest state list.<br>
	 * It also sends an {@code SM_QUEST_ACTION} packet to notify the client.
	 * @param env The {@link QuestEnv} containing the player and quest details.
	 * @param status The {@link QuestStatus} to assign to the new quest.
	 */
	public static void startMission(QuestEnv env, QuestStatus status)
	{
		final Player player = env.getPlayer();
		final int questId = env.getQuestId();
		
		if (player.getQuestStateList().getQuestState(questId) != null)
		{
			return;
		}
		
		player.getQuestStateList().addQuest(questId, new QuestState(questId, status, 0, 0, null, 0, null));
		
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, status.value(), 0));
	}
	
	/**
	 * Verifies if the player meets all requirements to progress in a quest.<br>
	 * This method checks for valid race, gender, and class permissions.<br>
	 * It also validates if the player possesses the required skill levels.
	 * @param env The {@code QuestEnv} object containing current quest and player data.
	 * @return {@code true} if all conditions are met; {@code false} otherwise.
	 */
	public static boolean checkMissionStatConditions(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestTemplate template = questsData.getQuestById(env.getQuestId());
		
		// Check if the template exists and if the race is permitted.
		if ((template == null) || ((template.getRacePermitted() != null) && (template.getRacePermitted() != player.getRace())))
		{
			return false;
		}
		
		// Check the permitted gender class.
		if (((template.getClassPermitted().size() != 0) && !template.getClassPermitted().contains(player.getCommonData().getPlayerClass())) || ((template.getGenderPermitted() != null) && (template.getGenderPermitted() != player.getGender())))
		{
			return false;
		}
		
		// Check required skills
		if (template.getCombineSkill() != null)
		{
			final List<Integer> skills = new ArrayList<>(); // skills to check
			if (template.getCombineSkill() == -1) // any skill
			{
				skills.add(30002);
				skills.add(30003);
				skills.add(40001);
				skills.add(40002);
				skills.add(40003);
				skills.add(40004);
				skills.add(40007);
				skills.add(40008);
				skills.add(40010);
				skills.add(40011);
			}
			else
			{
				skills.add(template.getCombineSkill());
			}
			
			boolean result = false;
			for (int skillId : skills)
			{
				final PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
				if ((skill != null) && (skill.getSkillLevel() >= template.getCombineSkillPoint()) && ((skill.getSkillLevel() - 40) <= template.getCombineSkillPoint()))
				{
					result = true;
					break;
				}
			}
			
			if (!result)
			{
				return false;
			}
		}
		
		// Everything is ok
		return true;
	}
	
	/**
	 * Starts an event-type quest for a player.<br>
	 * This method validates the player's level, race, class, and gender against the quest requirements.<br>
	 * If valid, it updates the player's quest state and refreshes nearby quests.
	 * @param env The environment containing the quest ID and player data.
	 * @param questStatus The status to assign to the quest.
	 * @return {@code true} if the quest was successfully started, or {@code false} otherwise.
	 */
	public static boolean startEventQuest(QuestEnv env, QuestStatus questStatus)
	{
		final QuestTemplate template = questsData.getQuestById(env.getQuestId());
		if (template.getCategory() != QuestCategory.EVENT)
		{
			return false;
		}
		
		final int id = env.getQuestId();
		final Player player = env.getPlayer();
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(id, questStatus, 0));
		if (((player.getLevel() < template.getMinlevelPermitted()) && (template.getMinlevelPermitted() != 999)) || ((template.getMaxlevelPermitted() != 0) && (player.getLevel() > template.getMaxlevelPermitted())))
		{
			return false;
		}
		
		if (template.getRacePermitted() != null)
		{
			if ((template.getRacePermitted() != player.getRace()) && (template.getRacePermitted() != Race.PC_ALL))
			{
				return false;
			}
		}
		
		if (!template.getClassPermitted().isEmpty())
		{
			if (!template.getClassPermitted().contains(player.getCommonData().getPlayerClass()))
			{
				return false;
			}
		}
		
		if (template.getGenderPermitted() != null)
		{
			if (template.getGenderPermitted() != player.getGender())
			{
				return false;
			}
		}
		
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs == null)
		{
			qs = new QuestState(template.getId(), questStatus, 0, 0, null, 0, null);
			player.getQuestStateList().addQuest(id, qs);
		}
		else
		{
			if (template.getMaxRepeatCount() >= qs.getCompleteCount())
			{
				qs.setStatus(questStatus);
				qs.setQuestVar(0);
			}
		}
		
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		return true;
	}
	
	/*
	 * Check the player's quest list size for starting a new one
	 * @param quest state list
	 */
	/**
	 * Checks if a player can accept a new quest.<br>
	 * It verifies that the current list size plus one does not exceed {@code CustomConfig.BASIC_QUEST_SIZE_LIMIT}.
	 * @param qsl The {@link QuestStateList} of the player.
	 * @return {@code true} if the limit is not exceeded, {@code false} otherwise.
	 */
	private static boolean checkQuestListSize(QuestStateList qsl)
	{
		// The player's quest list size + the new one to start
		return (qsl.getNormalQuestListSize() + 1) <= CustomConfig.BASIC_QUEST_SIZE_LIMIT;
	}
	
	/**
	 * Completes the current quest for a player.<br>
	 * This method updates the {@code QuestStatus} based on whether it is a tutorial or a standard quest.<br>
	 * It also triggers necessary zone and nearby quest updates.
	 * @param env The environment containing the player and quest information.
	 * @return {@code true} if the quest was successfully updated, otherwise {@code false}.
	 */
	public static boolean completeQuest(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final int id = env.getQuestId();
		final QuestState qs = player.getQuestStateList().getQuestState(id);
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(env.getQuestId());
		
		if ((qs == null) || (qs.getStatus() != QuestStatus.START))
		{
			return false;
		}
		
		if (template.getCategory() == QuestCategory.TUTORIAL)
		{
			qs.setStatus(QuestStatus.COMPLETE);
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
		else
		{
			qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
			qs.setStatus(QuestStatus.REWARD);
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
		
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		return true;
	}
	
	/**
	 * Checks if the player has enough items to complete a quest requirement.<br>
	 * It verifies both {@code CollectItems} and {@code InventoryItems} from the quest template.<br>
	 * If {@code removeItem} is {@code true}, it will subtract the required amounts from the player's inventory.
	 * @param env The environment containing the current quest context.
	 * @param removeItem Whether to remove items from the player after a successful check.
	 * @return {@code true} if requirements are met, {@code false} otherwise.
	 */
	public static boolean collectItemCheck(QuestEnv env, boolean removeItem)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(env.getQuestId());
		if ((qs == null) && removeItem)
		{
			return false;
		}
		
		final QuestTemplate template = questsData.getQuestById(env.getQuestId());
		final CollectItems collectItems = template.getCollectItems();
		if (collectItems == null)
		{
			// check inventoryItem to prevent exploits
			final InventoryItems inventoryItems = template.getInventoryItems();
			if (inventoryItems == null)
			{
				return true;
			}
			
			for (InventoryItem inventoryItem : inventoryItems.getInventoryItem())
			{
				final int itemId = inventoryItem.getItemId();
				if (player.getInventory().getItemCountByItemId(itemId) < 1)
				{
					return false;
				}
			}
			
			if (removeItem)
			{
				for (InventoryItem inventoryItem : inventoryItems.getInventoryItem())
				{
					player.getInventory().decreaseByItemId(inventoryItem.getItemId(), 1);
				}
			}
			
			return true;
		}
		
		for (CollectItem collectItem : collectItems.getCollectItem())
		{
			final int itemId = collectItem.getItemId();
			final long count = itemId == ItemId.KINAH.value() ? player.getInventory().getKinah() : player.getInventory().getItemCountByItemId(itemId);
			if (collectItem.getCount() > count)
			{
				return false;
			}
		}
		
		if (removeItem)
		{
			for (CollectItem collectItem : collectItems.getCollectItem())
			{
				if (collectItem.getItemId() == 182400001)
				{
					player.getInventory().decreaseKinah(collectItem.getCount());
				}
				else
				{
					player.getInventory().decreaseByItemId(collectItem.getItemId(), collectItem.getCount());
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the player has all required items for a quest.<br>
	 * It verifies the inventory against the {@code QuestTemplate}.<br>
	 * If an item is missing, it may send a system message to the player.
	 * @param env The environment containing the current quest and player data.
	 * @param showWarning Whether to display a warning message if items are missing.
	 * @return {@code true} if all required items are present, {@code false} otherwise.
	 */
	public static boolean inventoryItemCheck(QuestEnv env, boolean showWarning)
	{
		final Player player = env.getPlayer();
		final QuestTemplate template = questsData.getQuestById(env.getQuestId());
		final InventoryItems inventoryItems = template.getInventoryItems();
		if (inventoryItems == null)
		{
			return true;
		}
		
		int requiredItemNameId = 0;
		for (InventoryItem inventoryItem : inventoryItems.getInventoryItem())
		{
			final Item item = player.getInventory().getFirstItemByItemId(inventoryItem.getItemId());
			if (item == null)
			{
				requiredItemNameId = DataManager.ITEM_DATA.getItemTemplate(inventoryItem.getItemId()).getNameId();
				break;
			}
		}
		
		if ((requiredItemNameId != 0) && showWarning)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_INVENTORY_ITEM(new DescriptionId(requiredItemNameId)));
		}
		
		return requiredItemNameId == 0;
	}
	
	/**
	 * Creates and spawns a new quest NPC into the game world.<br>
	 * This method uses {@code spawnObject} to place the entity.
	 * @param worldId The unique identifier for the world map.
	 * @param instanceId The specific instance ID for the object.
	 * @param templateId The ID of the NPC template to use.
	 * @param x The X coordinate for spawning.
	 * @param y The Y coordinate for spawning.
	 * @param z The Z coordinate for spawning.
	 * @param heading The direction the NPC faces in degrees.
	 * @return The newly created {@code VisibleObject} instance.
	 */
	public static VisibleObject spawnQuestNpc(int worldId, int instanceId, int templateId, float x, float y, float z, byte heading)
	{
		return SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(worldId, templateId, x, y, z, heading), instanceId);
	}
	
	/**
	 * Adds a new NPC spawn to the game world.<br>
	 * This method registers a specific entity at the given coordinates.
	 * @param worldId The unique identifier for the world.
	 * @param instanceId The unique identifier for the map instance.
	 * @param templateId The ID of the NPC template to spawn.
	 * @param x The X coordinate in the world.
	 * @param y The Y coordinate in the world.
	 * @param z The Z coordinate in the world.
	 * @param heading The direction the NPC faces.
	 */
	public static void addNewSpawn(int worldId, int instanceId, int templateId, float x, float y, float z, byte heading)
	{
		addNewSpawn(worldId, instanceId, templateId, x, y, z, (byte) 0, 5);
	}
	
	/**
	 * Adds a new quest NPC to the game world.<br>
	 * This method spawns an {@code Npc} based on the provided template.<br>
	 * If the NPC is not in an instance map, it will be automatically despawned after a set time.
	 * @param worldId The unique identifier for the world.
	 * @param instanceId The unique identifier for the specific instance.
	 * @param templateId The ID of the NPC template to use.
	 * @param x The X coordinate for spawning.
	 * @param y The Y coordinate for spawning.
	 * @param z The Z coordinate for spawning.
	 * @param heading The direction the NPC faces.
	 * @param timeInMin The duration in minutes before the NPC despawns.
	 */
	public static void addNewSpawn(int worldId, int instanceId, int templateId, float x, float y, float z, byte heading, int timeInMin)
	{
		final Npc npc = (Npc) spawnQuestNpc(worldId, instanceId, templateId, x, y, z, (byte) 0);
		if (!npc.getPosition().isInstanceMap())
		{
			despawnQuestNpc(npc, timeInMin);
		}
	}
	
	/**
	 * Schedules the removal of a quest {@link Npc}.<br>
	 * The NPC will be deleted after a specific delay.<br>
	 * This method checks if the NPC is alive before calling its delete logic.
	 * @param npc The {@link Npc} object to be removed from the game world.
	 * @param timeInMin The amount of time to wait in minutes before despawning.
	 */
	private static void despawnQuestNpc(Npc npc, int timeInMin)
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if ((npc != null) && !npc.getLifeStats().isAlreadyDead())
			{
				npc.getController().onDelete();
			}
		}, 60000 * timeInMin);
	}
	
	/**
	 * Calculates and populates the list of quest drops for a specific NPC.<br>
	 * This method determines which players receive items based on group or alliance rules.<br>
	 * It handles random drop chances and updates the provided collection with new items.
	 * @param dropItems The set of {@link DropItem} objects to be populated.
	 * @param index The current counter used to track the number of drops processed.
	 * @param npc The {@link Npc} object from which the drops are being generated.
	 * @param players A collection of {@link Player} objects involved in the current group or alliance.
	 * @param player The primary {@link Player} triggering the drop event.
	 * @return The updated counter value after processing all possible drops.
	 */
	public static int getQuestDrop(Set<DropItem> dropItems, int index, Npc npc, Collection<Player> players, Player player)
	{
		final Collection<QuestDrop> drops = getQuestDrop(npc.getNpcId());
		if (drops.isEmpty())
		{
			return index;
		}
		
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npc.getObjectId());
		for (QuestDrop drop : drops)
		{
			if ((Rnd.get() * 100) > drop.getChance())
			{
				continue;
			}
			
			if ((players != null) && player.isInGroup2())
			{
				final List<Player> pls = new ArrayList<>();
				if (drop.isDropEachMemberGroup())
				{
					for (Player member : players)
					{
						if (isQuestDrop(member, drop))
						{
							pls.add(member);
							dropItems.add(regQuestDropItem(drop, index++, member.getObjectId()));
						}
					}
				}
				else
				{
					for (Player member : players)
					{
						if (isQuestDrop(member, drop))
						{
							pls.add(member);
							break;
						}
					}
				}
				
				if (pls.size() > 0)
				{
					if (!drop.isDropEachMemberGroup())
					{
						dropItems.add(regQuestDropItem(drop, index++, 0));
					}
					
					for (Player p : pls)
					{
						dropNpc.setPlayerObjectId(p.getObjectId());
						if (player.getPlayerGroup2().getLootGroupRules().getLootRule() != LootRuleType.FREEFORALL)
						{
							PacketSendUtility.sendPacket(p, new SM_LOOT_STATUS(npc.getObjectId(), 0));
						}
					}
					
					pls.clear();
				}
			}
			else if ((players != null) && player.isInAlliance2())
			{
				final List<Player> pls = new ArrayList<>();
				if (drop.isDropEachMemberAlliance())
				{
					for (Player member : players)
					{
						if (isQuestDrop(member, drop))
						{
							pls.add(member);
							dropItems.add(regQuestDropItem(drop, index++, member.getObjectId()));
						}
					}
				}
				else
				{
					for (Player member : players)
					{
						if (isQuestDrop(member, drop))
						{
							pls.add(member);
							break;
						}
					}
				}
				
				if (pls.size() > 0)
				{
					if (!drop.isDropEachMemberAlliance())
					{
						dropItems.add(regQuestDropItem(drop, index++, 0));
					}
					
					for (Player p : pls)
					{
						dropNpc.setPlayerObjectId(p.getObjectId());
						if (player.getPlayerAlliance2().getLootGroupRules().getLootRule() != LootRuleType.FREEFORALL)
						{
							PacketSendUtility.sendPacket(p, new SM_LOOT_STATUS(npc.getObjectId(), 0));
						}
					}
					
					pls.clear();
				}
			}
			else
			{
				if (isQuestDrop(player, drop))
				{
					dropItems.add(regQuestDropItem(drop, index++, player.getObjectId()));
				}
			}
		}
		
		return index;
	}
	
	/**
	 * Creates a new {@link DropItem} from a quest drop configuration.<br>
	 * This method initializes the item with a count of 1 and assigns it to a specific winner.
	 * @param drop The source {@link QuestDrop} data used to create the item.
	 * @param index The position index for the dropped item.
	 * @param winner The unique ID of the player who receives the item.
	 * @return A new instance of {@link DropItem}.
	 */
	private static DropItem regQuestDropItem(QuestDrop drop, int index, Integer winner)
	{
		final DropItem item = new DropItem(new Drop(drop.getItemId(), 1, 1, drop.getChance(), false));
		item.setPlayerObjId(winner);
		item.setIndex(index);
		item.setCount(1);
		return item;
	}
	
	/**
	 * Checks if a specific drop is required for the current quest progress.<br>
	 * It verifies the player's quest status, group requirements, and inventory counts.
	 * @param player The {@link Player} who is interacting with the drop.
	 * @param drop The {@link QuestDrop} object being evaluated.
	 * @return {@code true} if the drop is needed for the quest, otherwise {@code false}.
	 */
	private static boolean isQuestDrop(Player player, QuestDrop drop)
	{
		final int questId = drop.getQuestId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() != QuestStatus.START))
		{
			return false;
		}
		
		if (drop.getCollectingStep() != 0)
		{
			if (drop.getCollectingStep() != qs.getQuestVarById(0))
			{
				return false;
			}
		}
		
		final QuestTemplate qt = DataManager.QUEST_DATA.getQuestById(questId);
		if (player.isInAlliance2())
		{
			if (!qt.getTarget().equals(QuestTarget.ALLIANCE))
			{
				return false;
			}
		}
		
		if (qt.getMentorType() == QuestMentorType.MENTE)
		{
			if (!player.isInGroup2())
			{
				return false;
			}
			
			final PlayerGroup group = player.getPlayerGroup2();
			boolean found = false;
			for (Player member : group.getMembers())
			{
				if (member.isMentor() && (MathUtil.getDistance(player, member) < GroupConfig.GROUP_MAX_DISTANCE))
				{
					found = true;
					break;
				}
			}
			
			if (!found)
			{
				return false;
			}
		}
		
		if (drop instanceof HandlerSideDrop)
		{
			return ((HandlerSideDrop) drop).getNeededAmount() > player.getInventory().getItemCountByItemId(drop.getItemId());
		}
		
		final CollectItems collectItems = questsData.getQuestById(questId).getCollectItems();
		if (collectItems == null)
		{
			return true;
		}
		
		for (CollectItem collectItem : collectItems.getCollectItem())
		{
			final int collectItemId = collectItem.getItemId();
			final long count = player.getInventory().getItemCountByItemId(collectItemId);
			if (collectItem.getCount() > count)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a player meets the minimum level to start a quest.<br>
	 * This method compares the {@code playerLevel} against the required level for the given {@code questId}.
	 * @param questId The unique identifier of the quest.
	 * @param playerLevel The current level of the player.
	 * @return {@code true} if the player meets the requirement, {@code false} otherwise.
	 */
	public static boolean checkLevelRequirement(int questId, int playerLevel)
	{
		return playerLevel >= questsData.getQuestById(questId).getMinlevelPermitted();
	}
	
	/**
	 * Calculates the difference between a quest's required level and the current player level.<br>
	 * It returns {@code 999} if the quest does not exist.<br>
	 * It returns {@code 0} if the quest has no specific level requirement.
	 * @param questId The unique identifier for the quest.
	 * @param playerLevel The current level of the player.
	 * @return The difference between the required minimum level and the player's level.
	 */
	public static int getLevelRequirementDiff(int questId, int playerLevel)
	{
		final QuestTemplate template = questsData.getQuestById(questId);
		if (template == null)
		{
			return 999;
		}
		
		if (questsData.getQuestById(questId).getMinlevelPermitted() == 999)
		{
			return 0;
		}
		
		return questsData.getQuestById(questId).getMinlevelPermitted() - playerLevel;
	}
	
	/**
	 * Starts a timer for a specific quest.<br>
	 * This method schedules an action to occur after the specified duration.<br>
	 * It also sends a notification packet to the player.
	 * @param env The environment containing the current quest and player data.
	 * @param timeInSeconds The amount of time to wait in seconds before the timer ends.
	 * @return {@code true} if the timer was successfully started, otherwise {@code false}.
	 */
	public static boolean questTimerStart(QuestEnv env, int timeInSeconds)
	{
		final Player player = env.getPlayer();
		
		// Schedule Action When Timer Finishes
		final Future<?> task = ThreadPoolManager.getInstance().schedule(() -> QuestEngine.getInstance().onQuestTimerEnd(new QuestEnv(null, player, 0, 0)), timeInSeconds * 1000);
		player.getController().addTask(TaskId.QUEST_TIMER, task);
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(env.getQuestId(), timeInSeconds));
		return true;
	}
	
	/**
	 * Starts an invisible timer for a quest.<br>
	 * This method schedules the {@code onInvisibleTimerEnd} action to run after a delay.
	 * @param env The environment containing the player data.
	 * @param timeInSeconds The duration of the timer in seconds.
	 * @return Always returns {@code true}.
	 */
	public static boolean invisibleTimerStart(QuestEnv env, int timeInSeconds)
	{
		final Player player = env.getPlayer();
		
		// Schedule Action When Timer Finishes
		ThreadPoolManager.getInstance().schedule(() -> QuestEngine.getInstance().onInvisibleTimerEnd(new QuestEnv(null, player, 0, 0)), timeInSeconds * 1000);
		return true;
	}
	
	/**
	 * Checks if the quest timer has ended.<br>
	 * This method cancels the active {@code QUEST_TIMER} task for the player.<br>
	 * It also sends a quest action packet to the player.
	 * @param env The environment containing the current quest and player data.
	 * @return Always returns {@code true}.
	 */
	public static boolean questTimerEnd(QuestEnv env)
	{
		final Player player = env.getPlayer();
		
		player.getController().cancelTask(TaskId.QUEST_TIMER);
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(env.getQuestId(), 0));
		return true;
	}
	
	/**
	 * Checks if a {@link Player} can complete a bounty quest.<br>
	 * This method verifies the quest status and rewards the player if eligible.<br>
	 * It returns {@code false} if the quest is null, locked, or already finished.
	 * @param player The {@link Player} attempting to claim the reward.
	 * @param questId The unique identifier for the bounty quest.
	 * @return {@code true} if the quest was successfully completed and rewarded; {@code false} otherwise.
	 */
	public static boolean bountyQuest(Player player, int questId)
	{
		final QuestTemplate template = questsData.getQuestById(questId);
		if ((template == null) || !template.isCanReport())
		{
			return false;
		}
		
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (qs == null)
		{
			return false;
		}
		
		if ((qs.getStatus() == QuestStatus.COMPLETE) || (qs.getStatus() == QuestStatus.LOCKED))
		{
			AuditLogger.info(player, "Bounty from completed quest. quest Id: " + questId);
			return false;
		}
		
		final QuestEnv env = new QuestEnv(player, player, questId, DialogAction.QUEST_AUTO_REWARD.id());
		finishQuest(env);
		player.getController().updateNearbyQuests();
		return true;
	}
	
	/**
	 * Cancels a specific quest for a player.<br>
	 * This method checks if the quest is abandonable and removes related items or tasks.<br>
	 * It updates the player's quest status to {@code NONE}.
	 * @param player The {@link Player} who is abandoning the quest.
	 * @param questId The unique identifier of the quest to cancel.
	 * @return {@code true} if the quest was successfully abandoned, otherwise {@code false}.
	 */
	public static boolean abandonQuest(Player player, int questId)
	{
		final QuestTemplate template = questsData.getQuestById(questId);
		if ((template == null) || template.isCannotGiveup())
		{
			return false;
		}
		
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (qs == null)
		{
			return false;
		}
		
		if ((qs.getStatus() == QuestStatus.COMPLETE) || (qs.getStatus() == QuestStatus.LOCKED))
		{
			AuditLogger.info(player, "Cancel from completed quest. quest Id: " + questId);
			return false;
		}
		
		if (template.getNpcFactionId() != 0)
		{
			player.getNpcFactions().abortQuest(template);
		}
		
		qs.setStatus(QuestStatus.NONE);
		qs.setQuestVar(0);
		
		// remove all worker list item if abandoned
		final QuestWorkItems qwi = template.getQuestWorkItems();
		if (qwi != null)
		{
			long count = 0;
			for (QuestItems qi : qwi.getQuestWorkItem())
			{
				if (qi != null)
				{
					count = player.getInventory().getItemCountByItemId(qi.getItemId());
					if (count > 0)
					{
						player.getInventory().decreaseByItemId(qi.getItemId(), count);
					}
				}
			}
		}
		
		if (template.getCategory() == QuestCategory.TASK)
		{
			WorkOrdersData wod = null;
			for (XMLQuest xmlQuest : DataManager.XML_QUESTS.getQuest())
			{
				if (xmlQuest.getId() == questId)
				{
					if (xmlQuest instanceof WorkOrdersData)
					{
						wod = (WorkOrdersData) xmlQuest;
						break;
					}
				}
			}
			
			if (wod != null)
			{
				player.getRecipeList().deleteRecipe(player, wod.getRecipeId());
			}
		}
		
		if (player.getController().getTask(TaskId.QUEST_TIMER) != null)
		{
			questTimerEnd(new QuestEnv(null, player, questId, 0));
		}
		
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId));
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		return true;
	}
	
	/**
	 * Retrieves the list of drops associated with a specific NPC.<br>
	 * This method looks up the {@code npcId} in the quest drop registry.<br>
	 * It returns an empty collection if no drops are found.
	 * @param npcId The unique identifier for the NPC.
	 * @return A {@code Collection} of {@link QuestDrop} objects associated with the NPC.
	 */
	public static Collection<QuestDrop> getQuestDrop(int npcId)
	{
		if (questDrop.containsKey(npcId))
		{
			return questDrop.get(npcId);
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * Registers a new quest drop for a specific NPC.<br>
	 * If the {@code npcId} already has drops, this adds to the existing list.<br>
	 * If it does not exist, it creates a new entry for that ID.
	 * @param npcId The unique identifier of the NPC.
	 * @param drop The {@code QuestDrop} object to associate with the NPC.
	 */
	public static void addQuestDrop(int npcId, QuestDrop drop)
	{
		questDrop.computeIfAbsent(npcId, k -> new ArrayList<>()).add(drop);
	}
	
	/**
	 * Retrieves a list of players from a group who are eligible for a specific quest drop.<br>
	 * This method checks if the drop is configured to be awarded to each member of the group.<br>
	 * It filters members based on whether their {@link Player} quest state for the given ID is currently started.
	 * @param group The {@link PlayerGroup} containing the players to check.
	 * @param npcId The unique identifier for the NPC associated with the drop.
	 * @param questId The unique identifier for the quest being checked.
	 * @return A {@code List<Player>} of members who qualify for the reward.
	 */
	public static List<Player> getEachDropMembersGroup(PlayerGroup group, int npcId, int questId)
	{
		final List<Player> players = new ArrayList<>();
		for (QuestDrop qd : getQuestDrop(npcId))
		{
			if (qd.isDropEachMemberGroup())
			{
				for (Player player : group.getMembers())
				{
					final QuestState qstel = player.getQuestStateList().getQuestState(questId);
					if ((qstel != null) && (qstel.getStatus() == QuestStatus.START))
					{
						players.add(player);
					}
				}
				break;
			}
		}
		
		return players;
	}
	
	/**
	 * Retrieves a list of members from an alliance who can receive quest drops.<br>
	 * This method checks if the drop for a specific {@code npcId} is set to reward each member.<br>
	 * It identifies players within the {@link PlayerAlliance} who have started the specified {@code questId}.
	 * @param alliance The {@link PlayerAlliance} to check for members.
	 * @param npcId The unique identifier of the NPC associated with the drop.
	 * @param questId The unique identifier of the quest required for the reward.
	 * @return A {@code List} of {@link Player} objects who qualify for the drop.
	 */
	public static List<Player> getEachDropMembersAlliance(PlayerAlliance alliance, int npcId, int questId)
	{
		final List<Player> players = new ArrayList<>();
		for (QuestDrop qd : getQuestDrop(npcId))
		{
			if (qd.isDropEachMemberGroup())
			{
				for (Player player : alliance.getMembers())
				{
					final QuestState qstel = player.getQuestStateList().getQuestState(questId);
					if ((qstel != null) && (qstel.getStatus() == QuestStatus.START))
					{
						players.add(player);
					}
				}
				break;
			}
		}
		
		return players;
	}
}
