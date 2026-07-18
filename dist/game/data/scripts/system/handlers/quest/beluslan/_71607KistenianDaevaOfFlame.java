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
package system.handlers.quest.beluslan;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Handles the quest logic for {@code 71607 Kistenian Daeva of Flame}.<br>
 * This class manages specific quest progression and interactions within the Beluslan region.
 * @author QuestGenerator by Mariella
 */
public class _71607KistenianDaevaOfFlame extends QuestHandler
{
	private final static int questId = 71607;
	
	/**
	 * Initializes the quest handler for Kistenian Daeva of Flame.<br>
	 * This constructor sets up the internal state using {@code 71607}.<br>
	 * It calls the superclass constructor to register the quest ID.
	 */
	public _71607KistenianDaevaOfFlame()
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
		qe.registerQuestNpc(204753).addOnQuestStart(questId); // Kistenian
		qe.registerQuestNpc(204753).addOnTalkEvent(questId); // Kistenian
		qe.registerQuestNpc(806823).addOnQuestStart(questId); // Nightmare-ridden Cheska
		qe.registerQuestNpc(806823).addOnTalkEvent(questId); // Nightmare-ridden Cheska
		qe.registerQuestNpc(790016).addOnQuestStart(questId); // Etun
		qe.registerQuestNpc(790016).addOnTalkEvent(questId); // Etun
		qe.registerQuestItem(182216539, questId); // Blazing Daeva Essence
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
		return defaultOnLvlUpEvent(env, 70200, false);
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
			if (targetId == 204753)
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
				case 204753:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case CHECK_USER_HAS_QUEST_ITEM:
						{
							return checkQuestItems(env, 0, 0, true, 5, 2716);
						}
						case FINISH_DIALOG:
						{
							return sendQuestSelectionDialog(env);
						}
						
						// case QUEST_SELECT: {
						// If var is zero, return the quest dialog for environment 1352.
						// Return the quest dialog for environment 1693.
						// }
						case SETPRO2:
						{
							// giveQuestItem(env, 182216539, 1);
							qs.setQuestVar(2);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				case 806823:
				{
					switch (dialog)
					{
						case SET_SUCCEED:
						{
							qs.setQuestVar(3);
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
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 790016)
			{
				return sendQuestEndDialog(env);
			}
		}
		
		return false;
	}
}
