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
 * This class handles the quest logic for {@code 62541 Observation of the Balaur}.<br>
 * It manages the progression and requirements for this specific Lakrum region quest.
 * @author QuestGenerator by Mariella
 */
public class _62541ObservationOfTheBalaur extends QuestHandler
{
	private final static int questId = 62541;
	
	/**
	 * Initializes the quest handler for Observation of the Balaur.<br>
	 * This constructor sets up the quest with ID {@code 62541}.<br>
	 * It calls the superclass constructor to register the quest.
	 */
	public _62541ObservationOfTheBalaur()
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
		qe.registerQuestNpc(836630).addOnQuestStart(questId); // Borenos
		qe.registerQuestNpc(836630).addOnTalkEvent(questId); // Borenos
		qe.registerQuestNpc(836631).addOnQuestStart(questId); // Nobe
		qe.registerQuestNpc(836631).addOnTalkEvent(questId); // Nobe
		qe.registerQuestNpc(836632).addOnQuestStart(questId); // Lenis
		qe.registerQuestNpc(836632).addOnTalkEvent(questId); // Lenis
		qe.registerQuestNpc(836633).addOnQuestStart(questId); // Demaope
		qe.registerQuestNpc(836633).addOnTalkEvent(questId); // Demaope
		qe.registerQuestItem(182216433, questId); // Borenos' Report
		qe.registerQuestItem(182216434, questId); // Nobe's Report
		qe.registerQuestItem(182216435, questId); // Lenis' Report
		qe.registerQuestItem(182216436, questId); // Demaope's Report
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
		return defaultOnLvlUpEvent(env, 60506, false);
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
			if (targetId == 836630)
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
				case 836630:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case SETPRO1:
						{
							// giveQuestItem(env, 182216433, 1);
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
				case 836631:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1352);
						}
						case SETPRO2:
						{
							// giveQuestItem(env, 182216433, 1);
							qs.setQuestVar(2);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				case 836632:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1693);
						}
						case SETPRO3:
						{
							// giveQuestItem(env, 182216433, 1);
							qs.setQuestVar(3);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				case 836633:
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
