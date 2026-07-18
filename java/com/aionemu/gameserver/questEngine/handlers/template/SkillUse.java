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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.models.QuestSkillData;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * This class handles quest progression triggered by a player using a specific skill.<br>
 * It checks the {@code QuestSkillData} to determine if the action satisfies the quest requirements.
 * @author vlog, modified Bobobear
 */
public class SkillUse extends QuestHandler
{
	private final int questId;
	private final int startNpc;
	private final int endNpc;
	private final Map<List<Integer>, QuestSkillData> qsd;
	
	/**
	 * Initializes a new {@link SkillUse} handler for a specific quest.<br>
	 * This constructor sets up the required NPC IDs and skill data.
	 * @param questId The unique identifier for the quest.
	 * @param startNpc The ID of the starting NPC.
	 * @param endNpc The ID of the ending NPC. Use {@code 0} to use the same as {@code startNpc}.
	 * @param qsd A {@link Map} containing the quest skill data mapping.
	 */
	public SkillUse(int questId, int startNpc, int endNpc, Map<List<Integer>, QuestSkillData> qsd)
	{
		super(questId);
		this.questId = questId;
		this.startNpc = startNpc;
		if (endNpc != 0)
		{
			this.endNpc = endNpc;
		}
		else
		{
			this.endNpc = startNpc;
		}
		
		this.qsd = qsd;
	}
	
	/**
	 * Registers the required quest events.<br>
	 * This method tells the system which actions to listen for.<br>
	 * You should add your specific event listeners inside this method.
	 */
	@Override
	public void register()
	{
		qe.registerQuestNpc(startNpc).addOnQuestStart(questId);
		qe.registerQuestNpc(startNpc).addOnTalkEvent(questId);
		if (endNpc != startNpc)
		{
			qe.registerQuestNpc(endNpc).addOnTalkEvent(questId);
		}
		
		for (List<Integer> skillIds : qsd.keySet())
		{
			final Iterator<Integer> iterator = skillIds.iterator();
			while (iterator.hasNext())
			{
				final int SkillId = iterator.next();
				qe.registerQuestSkill(SkillId, questId);
			}
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
		
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			if (targetId == startNpc)
			{
				if (dialog == DialogAction.QUEST_SELECT)
				{
					return sendQuestDialog(env, 4762);
				}
				
				return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			// TODO: check skill use count, see MonsterHunt.java how to get total count
			final int var = qs.getQuestVarById(0);
			if (targetId == endNpc)
			{
				if (dialog == DialogAction.QUEST_SELECT)
				{
					return sendQuestDialog(env, 10002);
				}
				else if (dialog == DialogAction.SELECT_QUEST_REWARD)
				{
					changeQuestStep(env, var, var, true); // reward
					return sendQuestDialog(env, 5);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == endNpc)
			{
				return sendQuestEndDialog(env);
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a player used a specific skill to progress a quest.<br>
	 * This method updates the {@code QuestState} variables if the skill is valid.<br>
	 * It returns {@code true} if the quest progresses successfully.
	 * @param env The current quest environment context.
	 * @param skillId The unique identifier of the skill used by the player.
	 * @return {@code true} if the skill was recognized and updated the quest, otherwise {@code false}.
	 */
	@Override
	public boolean onUseSkillEvent(QuestEnv env, int skillId)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			for (QuestSkillData qd : qsd.values())
			{
				if (qd.getSkillIds().contains(skillId))
				{
					int endVar = qd.getEndVar();
					int varId = qd.getVarNum();
					int total = 0;
					do
					{
						final int currentVar = qs.getQuestVarById(varId);
						total += currentVar << ((varId - qd.getVarNum()) * 6);
						endVar >>= 6;
						varId++;
					}
					while (endVar > 0);
					total += 1;
					if (total <= qd.getEndVar())
					{
						for (int varsUsed = qd.getVarNum(); varsUsed < varId; varsUsed++)
						{
							final int value = total & 0x3F;
							total >>= 6;
							qs.setQuestVarById(varsUsed, value);
						}
						
						updateQuestStatus(env);
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
