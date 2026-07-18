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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * This handler manages quests that require a player to report their progress to an NPC.<br>
 * It processes the interaction between the {@link Player} and the quest objective.<br>
 * Use this class when a quest step involves delivering an item or completing a task to a specific target.
 * @author MrPoke Like: Sleeping on the Job quest.
 * @modified Rolandas
 */
public class ReportTo extends QuestHandler
{
	private final Set<Integer> startNpcs = new HashSet<>();
	private final Set<Integer> endNpcs = new HashSet<>();
	private final int itemId;
	private final int startDialogId;
	private final int startDialogId2;
	
	/**
	 * Creates a new {@link ReportTo} quest handler.<br>
	 * This constructor initializes the required NPCs and dialog IDs for the quest.<br>
	 * It sets up the specific requirements needed to progress the quest.
	 * @param questId The unique identifier for the quest.
	 * @param startNpcIds A list of NPC IDs where the quest can begin.
	 * @param endNpcIds A list of NPC IDs where the quest can be completed.
	 * @param startDialogId The primary dialog ID to trigger at the start.
	 * @param startDialogId2 The secondary dialog ID to trigger at the start.
	 * @param itemId The specific item ID required for this report.
	 */
	public ReportTo(int questId, List<Integer> startNpcIds, List<Integer> endNpcIds, int startDialogId, int startDialogId2, int itemId)
	{
		super(questId);
		startNpcs.addAll(startNpcIds);
		startNpcs.remove(0);
		if (endNpcIds != null)
		{
			endNpcs.addAll(endNpcIds);
			endNpcs.remove(0);
		}
		
		this.startDialogId = startDialogId;
		this.startDialogId2 = startDialogId2;
		this.itemId = itemId;
	}
	
	/**
	 * Registers the required quest events.<br>
	 * This method tells the system which actions to listen for.<br>
	 * You should add your specific event listeners inside this method.
	 */
	@Override
	public void register()
	{
		Iterator<Integer> iterator = startNpcs.iterator();
		while (iterator.hasNext())
		{
			final int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}
		
		iterator = endNpcs.iterator();
		while (iterator.hasNext())
		{
			final int endNpc = iterator.next();
			qe.registerQuestNpc(endNpc).addOnTalkEvent(getQuestId());
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
		final int targetId = env.getTargetId();
		final DialogAction dialog = env.getDialog();
		final QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
		
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			if (startNpcs.isEmpty() || startNpcs.contains(targetId))
			{
				switch (dialog)
				{
					case QUEST_SELECT:
					{
						if (startDialogId != 0)
						{
							return sendQuestDialog(env, startDialogId);
						}
						
						return sendQuestDialog(env, 1011);
					}
					case QUEST_ACCEPT_1:
					case QUEST_ACCEPT_SIMPLE:
					{
						if (itemId != 0)
						{
							if (giveQuestItem(env, itemId, 1))
							{
								return sendQuestStartDialog(env);
							}
							
							return false;
						}
						
						return sendQuestStartDialog(env);
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
			if (endNpcs.contains(targetId))
			{
				switch (dialog)
				{
					case QUEST_SELECT:
					{
						if (startDialogId2 != 0)
						{
							return sendQuestDialog(env, startDialogId2);
						}
						
						return sendQuestDialog(env, 2375);
					}
					case SELECT_QUEST_REWARD:
					{
						if (itemId != 0)
						{
							if (player.getInventory().getItemCountByItemId(itemId) < 1)
							{
								return sendQuestSelectionDialog(env);
							}
						}
						
						removeQuestItem(env, itemId, 1);
						qs.setQuestVar(1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestEndDialog(env);
					}
					default:
						break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (endNpcs.contains(targetId))
			{
				return sendQuestEndDialog(env);
			}
		}
		
		return false;
	}
}
