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
 * This class handles the logic for quest {@code 60206} in Heiron.<br>
 * It manages the progression and requirements for the Hidden Fortress quest.<br>
 * It extends {@link QuestHandler} to process specific game events.
 * @author QuestGenerator by Mariella
 */
public class _60206HiddenFortress extends QuestHandler
{
	private final static int questId = 60206;
	
	/**
	 * Initializes the quest handler for the {@code 60206HiddenFortress} quest.<br>
	 * This constructor sets up the internal state using the unique quest ID.
	 */
	public _60206HiddenFortress()
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
		qe.registerQuestNpc(820047).addOnTalkEvent(questId); // First Clue
		qe.registerQuestNpc(820048).addOnTalkEvent(questId); // Second Clue
		qe.registerQuestNpc(820046).addOnTalkEvent(questId); // Waiting Royer
		qe.registerQuestNpc(802050).addOnTalkEvent(questId); // Barantina
		qe.registerQuestItem(182216259, questId); // Translated Kunpapa Document
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
			switch (targetId)
			{
				case 820047:
				{
					switch (dialog)
					{
						// ToDo: check correct action for this npc
						case USE_OBJECT:
						{
							qs.setQuestVar(3);
							updateQuestStatus(env);
							return false;
						}
						default:
							break;
					}
					break;
				}
				case 820048:
				{
					switch (dialog)
					{
						// ToDo: check correct action for this npc
						case USE_OBJECT:
						{
							qs.setQuestVar(4);
							updateQuestStatus(env);
							return false;
						}
						default:
							break;
					}
					break;
				}
				case 820046:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 2375);
						}
						case SETPRO5:
						{
							// giveQuestItem(env, 182216259, 1);
							qs.setQuestVar(5);
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
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 802050)
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
