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
package system.handlers.quest.inggison;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * This class handles the quest logic for {@code 61901} specifically for the Royer character.<br>
 * It manages the progression and requirements of this specific quest instance.
 * @author QuestGenerator by Mariella
 */
public class _61901ForRoyer2 extends QuestHandler
{
	private final static int questId = 61901;
	
	/**
	 * Initializes the quest handler for quest {@code 61901}.<br>
	 * This constructor calls the superclass constructor to register the quest ID.
	 */
	public _61901ForRoyer2()
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
		qe.registerQuestNpc(799071).addOnQuestStart(questId); // Naiting
		qe.registerQuestNpc(799071).addOnTalkEvent(questId); // Naiting
		qe.registerQuestNpc(820064).addOnQuestStart(questId); // Exhausted Royer
		qe.registerQuestNpc(820064).addOnTalkEvent(questId); // Exhausted Royer
		qe.registerQuestItem(182216294, questId); // Leg Bandage
		qe.registerQuestItem(182216295, questId); // Head Bandage
		qe.registerQuestItem(182216296, questId); // Hip Bandage
		qe.registerQuestItem(182216297, questId); // Recipe for Royer
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
			if (targetId == 799071)
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
			final int var = qs.getQuestVarById(0);
			switch (targetId)
			{
				case 799071:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							switch (var)
							{
								case 1:
								{
									return sendQuestDialog(env, 1011);
								}
								case 3:
								{
									return sendQuestDialog(env, 2034);
								}
								default:
									break;
							}
						}
						case SET_SUCCEED:
						{
							qs.setQuestVar(4);
							qs.setStatus(QuestStatus.REWARD);
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
				default:
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 820064)
			{
				return sendQuestEndDialog(env);
			}
		}
		
		return false;
	}
}
