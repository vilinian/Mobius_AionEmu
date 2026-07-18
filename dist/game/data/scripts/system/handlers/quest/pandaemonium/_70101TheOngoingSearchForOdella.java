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
package system.handlers.quest.pandaemonium;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * Handles the logic for quest {@code 70101} "The Ongoing Search for Odella".<br>
 * This class manages player interactions and progression steps within this specific quest.<br>
 * It extends {@link QuestHandler} to process game events related to the quest.
 * @author Falke_34
 */
public class _70101TheOngoingSearchForOdella extends QuestHandler
{
	private final static int questId = 70101;
	
	/**
	 * Initializes the quest handler for {@code 70101TheOngoingSearchForOdella}.<br>
	 * This constructor sets up the quest with its unique ID.<br>
	 * It calls the superclass constructor to register the quest.
	 */
	public _70101TheOngoingSearchForOdella()
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
		qe.registerQuestNpc(798800).addOnTalkEvent(questId);
		qe.registerQuestNpc(204191).addOnTalkEvent(questId);
		qe.registerQuestNpc(806820).addOnTalkEvent(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
	}
	
	/**
	 * Checks if a player should start the quest when entering a specific world.<br>
	 * This method triggers for players who do not have the quest active.<br>
	 * It uses {@code startQuest} to begin the quest.
	 * @param env The current quest environment containing player data.
	 * @return {@code true} if the quest was successfully started, otherwise {@code false}.
	 */
	@Override
	public boolean onEnterWorldEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
		{
			env.setQuestId(questId);
			QuestService.startQuest(env);
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			if (player.getWorldId() == 220040000)
			{
				qs.setQuestVar(2);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return true;
			}
		}
		
		return false;
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
		if (qs == null)
		{
			return false;
		}
		
		final int targetId = env.getTargetId();
		final DialogAction action = env.getDialog();
		
		if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 798800)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1011:
						return sendQuestDialog(env, 1012);
					case SETPRO1:
						qs.setQuestVar(1);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					default:
						break;
				}
			}
			else if (targetId == 204919)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 2034);
					case SELECT_ACTION_2035:
						return sendQuestDialog(env, 2035);
					case SET_SUCCEED:
						qs.setQuestVar(3);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					default:
						break;
				}
			}
			
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 806820)
			{
				switch (action)
				{
					case USE_OBJECT:
						return sendQuestDialog(env, 10002);
					case SELECT_QUEST_REWARD:
						return sendQuestDialog(env, 5);
					case SELECTED_QUEST_NOREWARD:
						TeleportService2.teleportTo(player, 120020000, 1518.9551f, 1400.9744f, 271.72427f, (byte) 70, TeleportAnimation.JUMP_ANIMATION);
						return sendQuestEndDialog(env);
					default:
						break;
				}
			}
			
		}
		
		return false;
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
		return defaultOnLvlUpEvent(env);
	}
}
