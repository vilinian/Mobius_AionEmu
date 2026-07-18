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
import com.aionemu.gameserver.services.QuestService;

/**
 * This handler manages quest objectives that require a player to collect specific items.<br>
 * It tracks the quantity of items gathered and updates the {@link QuestState} accordingly.
 * @author MrPoke
 * @reworked vlog, Rolandas
 */
public class ItemCollecting extends QuestHandler
{
	private final Set<Integer> startNpcs = new HashSet<>();
	private final Set<Integer> actionItems = new HashSet<>();
	private final Set<Integer> endNpcs = new HashSet<>();
	private final int questMovie;
	private final int nextNpcId;
	private final int startDialogId;
	private final int startDialogId2;
	private final int itemId;
	
	/**
	 * Initializes a new {@code ItemCollecting} quest handler.<br>
	 * This constructor sets up the required NPCs, items, and dialog IDs for the quest.<br>
	 * It prepares the data needed to track item collection progress.
	 * @param questId The unique identifier for the quest.
	 * @param startNpcIds A list of NPC IDs where the quest can begin.
	 * @param nextNpcId The ID of the NPC to visit after completing this step.
	 * @param actionItemIds A list of item IDs that trigger specific actions.
	 * @param endNpcIds A list of NPC IDs where the quest can be completed.
	 * @param questMovie The ID of the movie to play during this quest.
	 * @param startDialogId The primary dialog ID for starting the interaction.
	 * @param startDialogId2 The secondary dialog ID for starting the interaction.
	 * @param itemId The specific item ID required for collection.
	 */
	public ItemCollecting(int questId, List<Integer> startNpcIds, int nextNpcId, List<Integer> actionItemIds, List<Integer> endNpcIds, int questMovie, int startDialogId, int startDialogId2, int itemId)
	{
		super(questId);
		startNpcs.addAll(startNpcIds);
		startNpcs.remove(0);
		this.nextNpcId = nextNpcId;
		if (actionItemIds != null)
		{
			actionItems.addAll(actionItemIds);
			actionItems.remove(0);
		}
		
		if (endNpcIds == null)
		{
			endNpcs.addAll(startNpcs);
		}
		else
		{
			endNpcs.addAll(endNpcIds);
			endNpcs.remove(0);
		}
		
		this.questMovie = questMovie;
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
		
		if (nextNpcId != 0)
		{
			qe.registerQuestNpc(nextNpcId).addOnTalkEvent(getQuestId());
		}
		
		iterator = actionItems.iterator();
		while (iterator.hasNext())
		{
			final int actionItem = iterator.next();
			qe.registerQuestNpc(actionItem).addOnTalkEvent(getQuestId());
			qe.registerCanAct(getQuestId(), actionItem);
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
		final QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
		final DialogAction dialog = env.getDialog();
		final int targetId = env.getTargetId();
		
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
					case SETPRO1:
					{
						QuestService.startQuest(env);
						return closeDialogWindow(env);
					}
					case SELECT_ACTION_1012:
					{
						if (questMovie != 0)
						{
							playQuestMovie(env, questMovie);
						}
						
						return sendQuestDialog(env, 1012);
					}
					default:
					{
						if (itemId != 0)
						{
							giveQuestItem(env, itemId, 1);
						}
						
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			final int var = qs.getQuestVarById(0);
			if ((targetId == nextNpcId) && (var == 0))
			{
				switch (dialog)
				{
					case QUEST_SELECT:
					{
						return sendQuestDialog(env, 1352);
					}
					case SETPRO1:
					{
						return defaultCloseDialog(env, 0, 1);
					}
					default:
						break;
				}
			}
			else if (endNpcs.contains(targetId))
			{
				switch (dialog)
				{
					case QUEST_SELECT:
					{
						if (startDialogId2 != 0)
						{
							if (startDialogId2 == 5)
							{
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
							}
							
							return sendQuestDialog(env, startDialogId2);
						}
						
						return sendQuestDialog(env, 2375);
					}
					case CHECK_USER_HAS_QUEST_ITEM:
					{
						if (QuestService.collectItemCheck(env, true))
						{
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						
						if (startDialogId2 != 0)
						{
							// TEMP FIX
							return sendQuestDialog(env, 10001);
						}
						
						return sendQuestDialog(env, 2716);
					}
					case CHECK_USER_HAS_QUEST_ITEM_SIMPLE:
					{
						if (QuestService.collectItemCheck(env, true))
						{
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						
						return closeDialogWindow(env);
					}
					case FINISH_DIALOG:
					{
						return sendQuestSelectionDialog(env);
					}
					case SET_SUCCEED:
					{
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
					case SETPRO1:
					{
						return checkQuestItemsSimple(env, var, var, true, 5, 0, 0);
					}
					case SETPRO2:
					{
						return checkQuestItemsSimple(env, var, var, true, 6, 0, 0);
					}
					case SETPRO3:
					{
						return checkQuestItemsSimple(env, var, var, true, 7, 0, 0);
					}
					default:
						break;
				}
			}
			else if ((targetId != 0) && actionItems.contains(targetId))
			{
				return true; // looting
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (endNpcs.contains(targetId))
			{
				if (itemId != 0)
				{
					removeQuestItem(env, itemId, 1);
				}
				
				return sendQuestEndDialog(env);
			}
		}
		
		return false;
	}
}
