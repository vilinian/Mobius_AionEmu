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
import com.aionemu.gameserver.services.QuestService;

/**
 * Handles the quest logic for removing Drana.<br>
 * This class manages specific actions required to complete this objective within the quest system.
 * @author QuestGenerator by Mariella
 */
public class _60304AMethodForRemovingDrana extends QuestHandler
{
	private final static int questId = 60304;
	
	/**
	 * Initializes the quest handler for removing Drana.<br>
	 * This method sets up the internal state for quest {@code 60304}.<br>
	 * It calls the superclass constructor to register the specific quest ID.
	 */
	public _60304AMethodForRemovingDrana()
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
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(820070).addOnTalkEvent(questId); // Marian
		qe.registerQuestNpc(820173).addOnTalkEvent(questId); // Marius
		qe.registerQuestNpc(703518).addOnTalkEvent(questId); // Marian's Alchemist's Table
		qe.registerQuestNpc(798996).addOnTalkEvent(questId); // Iaetia
		qe.registerQuestItem(182216264, questId); // Extracted Digestive Fluid
		qe.registerQuestItem(182216265, questId); // Undiluted Drana Toxin
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
		
		if (qs == null)
		{
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START)
		{
			final int var = qs.getQuestVarById(0);
			
			switch (targetId)
			{
				case 820070:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							if (var == 0)
							{
								return sendQuestDialog(env, 1011);
							}
							
							return sendQuestDialog(env, 1352);
						}
						case SETPRO1:
						{
							// giveQuestItem(env, 182216264, 1);
							qs.setQuestVar(1);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						case CHECK_USER_HAS_QUEST_ITEM:
						{
							if (QuestService.collectItemCheck(env, true))
							{
								// removeQuestItem(env, 182216264, 1);
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return sendQuestDialog(env, 10000);
							}
							
							return sendQuestDialog(env, 10001);
						}
						
						// case QUEST_SELECT: {
						// If var is zero, return the quest dialog for environment 2034.
						// Return the quest dialog for environment 2375.
						// }
						case SETPRO4:
						{
							// giveQuestItem(env, 182216264, 1);
							qs.setQuestVar(4);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				case 820173:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1352);
						}
						case SETPRO2:
						{
							// giveQuestItem(env, 182216264, 1);
							qs.setQuestVar(2);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				case 703518:
				{
					switch (dialog)
					{
						// ToDo: check correct action for this npc
						case USE_OBJECT:
						{
							qs.setQuestVar(5);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return false;
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
			if (targetId == 798996)
			{
				if (dialog == DialogAction.USE_OBJECT)
				{
					return sendQuestDialog(env, 10002);
				}
				
				return sendQuestEndDialog(env);
			}
		}
		
		return false;
	}
}
