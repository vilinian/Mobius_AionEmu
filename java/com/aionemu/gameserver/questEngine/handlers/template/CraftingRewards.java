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
package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class handles the distribution of rewards for crafting-related quests.<br>
 * It processes logic to grant items or skills to a {@link Player} upon completion.<br>
 * It extends {@link QuestHandler} to integrate with the quest engine.
 * @author Bobobear
 */
public class CraftingRewards extends QuestHandler
{
	private final int questId;
	private final int startNpcId;
	private final int skillId;
	private final int levelReward;
	private final int questMovie;
	private final int endNpcId;
	
	/**
	 * Initializes a new {@link CraftingRewards} handler.<br>
	 * This constructor sets up the requirements for crafting rewards.<br>
	 * It handles the logic for determining the end NPC ID.
	 * @param questId The unique identifier for the quest.
	 * @param startNpcId The ID of the starting NPC.
	 * @param skillId The ID of the skill being rewarded.
	 * @param levelReward The required level to receive the reward.
	 * @param endNpcId The ID of the ending NPC, or 0 if it should default to {@code startNpcId}.
	 * @param questMovie The ID of the movie associated with this quest.
	 */
	public CraftingRewards(int questId, int startNpcId, int skillId, int levelReward, int endNpcId, int questMovie)
	{
		super(questId);
		this.questId = questId;
		this.startNpcId = startNpcId;
		this.skillId = skillId;
		this.levelReward = levelReward;
		if (endNpcId != 0)
		{
			this.endNpcId = endNpcId;
		}
		else
		{
			this.endNpcId = startNpcId;
		}
		
		this.questMovie = questMovie;
	}
	
	/**
	 * Registers the required quest events.<br>
	 * This method tells the system which actions to listen for.<br>
	 * You should add your specific event listeners inside this method.
	 */
	@Override
	public void register()
	{
		qe.registerQuestNpc(startNpcId).addOnQuestStart(questId);
		qe.registerQuestNpc(startNpcId).addOnTalkEvent(questId);
		if (questMovie != 0)
		{
			qe.registerOnMovieEndQuest(questMovie, questId);
		}
		
		if (endNpcId != startNpcId)
		{
			qe.registerQuestNpc(endNpcId).addOnTalkEvent(questId);
		}
	}
	
	/**
	 * Handles dialog events for the quest.<br>
	 * This method checks the current {@link QuestState} and {@code targetId}.<br>
	 * It determines which dialog to send based on the {@link DialogAction}.
	 * @param env The environment containing player data and current quest context.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final DialogAction dialog = env.getDialog();
		final int targetId = env.getTargetId();
		final PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
		
		if (skill != null)
		{
			final int playerSkillLevel = skill.getSkillLevel();
			if (!canLearn(player) && (playerSkillLevel != levelReward))
			{
				return false;
			}
		}
		
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			if (targetId == startNpcId)
			{
				switch (dialog)
				{
					case QUEST_SELECT:
					{
						return sendQuestDialog(env, 1011);
					}
					default:
					{
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == endNpcId)
			{
				switch (dialog)
				{
					case QUEST_SELECT:
					{
						return sendQuestDialog(env, 2375);
					}
					case SELECT_QUEST_REWARD:
					{
						qs.setQuestVar(0);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						if (questMovie != 0)
						{
							playQuestMovie(env, questMovie);
						}
						else
						{
							player.getSkillList().addSkill(player, skillId, levelReward);
						}
						
						return sendQuestEndDialog(env);
					}
					default:
						break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == endNpcId)
			{
				switch (dialog)
				{
					case QUEST_SELECT:
					{
						return sendQuestEndDialog(env);
					}
					default:
					{
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the {@link Player} is eligible to learn a new crafting skill.<br>
	 * This method validates requirements based on the {@code levelReward} value.<br>
	 * It uses {@link CraftSkillUpdateService} for specific expert or master checks.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can learn the skill, otherwise {@code false}.
	 */
	private boolean canLearn(Player player)
	{
		return levelReward == 400 ? CraftSkillUpdateService.canLearnMoreExpertCraftingSkill(player) : levelReward == 500 ? CraftSkillUpdateService.canLearnMoreMasterCraftingSkill(player) : true;
	}
	
	/**
	 * Handles the logic that occurs when a movie finishes playing.<br>
	 * It checks if the player should receive a reward based on the {@code movieId}.<br>
	 * This method is triggered by the quest engine to progress the story.
	 * @param env The current quest environment containing player data.
	 * @param movieId The unique identifier of the movie that just ended.
	 * @return {@code true} if the quest was successfully completed, otherwise {@code false}.
	 */
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getStatus() == QuestStatus.REWARD)
		{
			if ((movieId == questMovie) && canLearn(player))
			{
				player.getSkillList().addSkill(player, skillId, levelReward);
				player.getRecipeList().autoLearnRecipe(player, skillId, levelReward);
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330064, false));
				return true;
			}
		}
		
		return false;
	}
}
