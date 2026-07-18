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
package system.handlers.quest.heiron;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * This class handles the logic for quest {@code 61610} "Lighting The Trade Route".<br>
 * It manages player interactions and progress requirements for this specific quest.<br>
 * It extends {@link QuestHandler} to provide custom behavior for quest objectives.
 * @author QuestGenerator by Mariella
 */
public class _61610LightingTheTradeRoute extends QuestHandler
{
	private final static int questId = 61610;
	
	/**
	 * Initializes the quest handler for {@code 61610}.<br>
	 * This method sets up the quest logic for Lighting The Trade Route.
	 */
	public _61610LightingTheTradeRoute()
	{
		super(questId);
	}
	
	/**
	 * Registers the required quest events.<br>
	 * This method tells the system which actions to listen for.<br>
	 * You should add your specific event listeners inside this method.
	 */
	@Override
	public void register()
	{
		qe.registerQuestNpc(703506).addOnQuestStart(questId); // Fire of Life
		qe.registerQuestNpc(703506).addOnTalkEvent(questId); // Fire of Life
		qe.registerQuestNpc(703507).addOnQuestStart(questId); // Fire of Fate
		qe.registerQuestNpc(703507).addOnTalkEvent(questId); // Fire of Fate
		qe.registerQuestNpc(703508).addOnQuestStart(questId); // Fire of the Vengeful Ghost
		qe.registerQuestNpc(703508).addOnTalkEvent(questId); // Fire of the Vengeful Ghost
		qe.registerQuestNpc(703509).addOnQuestStart(questId); // Fire of Solace
		qe.registerQuestNpc(703509).addOnTalkEvent(questId); // Fire of Solace
		qe.registerQuestNpc(703510).addOnQuestStart(questId); // Fire of Cleverness
		qe.registerQuestNpc(703510).addOnTalkEvent(questId); // Fire of Cleverness
		qe.registerQuestNpc(703511).addOnQuestStart(questId); // Fire of Vitality
		qe.registerQuestNpc(703511).addOnTalkEvent(questId); // Fire of Vitality
		qe.registerQuestNpc(820143).addOnQuestStart(questId); // Doarunerk
		qe.registerQuestNpc(820143).addOnTalkEvent(questId); // Doarunerk
	}
	
	/**
	 * This method handles the logic when a player levels up.<br>
	 * It checks if the level up triggers specific quest progress.<br>
	 * It calls {@code defaultOnLvlUpEvent} to process the event.
	 * @param env The current quest environment context.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		return defaultOnLvlUpEvent(env, 60200, false);
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
		
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			if (targetId == 703511)
			{
				switch (dialog)
				{
					case QUEST_SELECT:
					{
						return sendQuestDialog(env, 4762);
					}
					case QUEST_ACCEPT_1:
					case QUEST_ACCEPT_SIMPLE:
					{
						return sendQuestStartDialog(env);
					}
					case QUEST_REFUSE_SIMPLE:
					{
						return closeDialogWindow(env);
					}
					default:
						break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			switch (targetId)
			{
				case 703506:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case FINISH_DIALOG:
						{
							return sendQuestSelectionDialog(env);
						}
						default:
							break;
					}
					break;
				}
				case 703507:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case FINISH_DIALOG:
						{
							return sendQuestSelectionDialog(env);
						}
						default:
							break;
					}
					break;
				}
				case 703508:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case FINISH_DIALOG:
						{
							return sendQuestSelectionDialog(env);
						}
						default:
							break;
					}
					break;
				}
				case 703509:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case FINISH_DIALOG:
						{
							return sendQuestSelectionDialog(env);
						}
						default:
							break;
					}
					break;
				}
				case 703510:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case FINISH_DIALOG:
						{
							return sendQuestSelectionDialog(env);
						}
						default:
							break;
					}
					break;
				}
				case 703511:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case FINISH_DIALOG:
						{
							return sendQuestSelectionDialog(env);
						}
						default:
							break;
					}
					break;
				}
				default:
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 820143)
			{
				return sendQuestEndDialog(env);
			}
		}
		
		return false;
	}
}
