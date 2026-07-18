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
package system.handlers.quest.lakrum;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Handles the quest logic for {@code 72541_ObservingTheBalaur}.<br>
 * This class manages the progression and requirements for players observing the Balaur.<br>
 * It extends {@link QuestHandler} to process specific quest events.
 * @author QuestGenerator by Mariella
 */
public class _72541ObservingTheBalaur extends QuestHandler
{
	private final static int questId = 72541;
	
	/**
	 * Initializes the quest handler for {@code 72541ObservingTheBalaur}.<br>
	 * This constructor sets up the internal state for this specific quest.<br>
	 * It calls the superclass constructor with the quest ID {@code 72541}.
	 */
	public _72541ObservingTheBalaur()
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
		qe.registerQuestNpc(836650).addOnQuestStart(questId); // Borgand
		qe.registerQuestNpc(836650).addOnTalkEvent(questId); // Borgand
		qe.registerQuestNpc(836651).addOnQuestStart(questId); // Baudurin
		qe.registerQuestNpc(836651).addOnTalkEvent(questId); // Baudurin
		qe.registerQuestNpc(836652).addOnQuestStart(questId); // Hildmir
		qe.registerQuestNpc(836652).addOnTalkEvent(questId); // Hildmir
		qe.registerQuestNpc(836653).addOnQuestStart(questId); // Magrell
		qe.registerQuestNpc(836653).addOnTalkEvent(questId); // Magrell
		qe.registerQuestItem(182216461, questId); // Borgand's Report
		qe.registerQuestItem(182216462, questId); // Baudurin's Report
		qe.registerQuestItem(182216463, questId); // Hildmir's Report
		qe.registerQuestItem(182216464, questId); // Magrell's Report
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
		return defaultOnLvlUpEvent(env, 70506, false);
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
			if (targetId == 836650)
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
				case 836650:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case SETPRO1:
						{
							// giveQuestItem(env, 182216461, 1);
							qs.setQuestVar(1);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						case CHECK_USER_HAS_QUEST_ITEM:
						{
							return checkQuestItems(env, 0, 0, true, 5, 2716);
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
				case 836651:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1352);
						}
						case SETPRO2:
						{
							// giveQuestItem(env, 182216461, 1);
							qs.setQuestVar(2);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				case 836652:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1693);
						}
						case SETPRO3:
						{
							// giveQuestItem(env, 182216461, 1);
							qs.setQuestVar(3);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				case 836653:
				{
					switch (dialog)
					{
						case SET_SUCCEED:
						{
							qs.setQuestVar(4);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return closeDialogWindow(env);
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
		
		return false;
	}
}
