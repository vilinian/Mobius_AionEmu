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
package system.handlers.quest.ishalgen;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * Handles the logic for quest {@code 70005} regarding Markos's special mission.<br>
 * This class manages specific quest progression and interactions within the Ishalgen region.
 * @author Falke_34
 * @author FrozenKiller
 */
public class _70005MarkosSpecialMission extends QuestHandler
{
	private final static int questId = 70005;
	
	/**
	 * Initializes the quest handler for Markos Special Mission.<br>
	 * This constructor sets the {@code questId} to {@code 70005}.<br>
	 * It calls the superclass constructor to register the quest.
	 */
	public _70005MarkosSpecialMission()
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
		qe.registerQuestNpc(203516).addOnTalkEvent(questId); // Ulgorn
		qe.registerQuestNpc(203519).addOnTalkEvent(questId); // Nobekk
		qe.registerQuestNpc(806906).addOnTalkEvent(questId); // Active Cheska
		qe.registerQuestNpc(203533).addOnTalkEvent(questId); // Motgar
		qe.registerQuestNpc(651765).addOnKillEvent(questId);
		qe.registerQuestNpc(651833).addOnKillEvent(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnLevelUp(questId);
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
			if (targetId == 203516)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1012:
						return sendQuestDialog(env, 1012);
					case SETPRO1:
						qs.setQuestVar(1);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					default:
						break;
				}
			}
			else if (targetId == 203519)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 1352);
					case SELECT_ACTION_1353:
						return sendQuestDialog(env, 1353);
					case SETPRO2:
						qs.setQuestVar(2);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					default:
						break;
				}
			}
			else if (targetId == 806906)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 2034);
					case SELECT_ACTION_2035:
						return sendQuestDialog(env, 2035);
					case SELECT_ACTION_2036:
						return sendQuestDialog(env, 2036);
					case SET_SUCCEED:
						qs.setQuestVar(4);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					default:
						break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 203533)
			{
				switch (action)
				{
					case USE_OBJECT:
						return sendQuestDialog(env, 10002);
					case SELECT_QUEST_REWARD:
						return sendQuestDialog(env, 5);
					case SELECTED_QUEST_NOREWARD:
						return sendQuestEndDialog(env);
					default:
						break;
				}
			}
			
		}
		
		return false;
	}
	
	/**
	 * This method is triggered when a player kills a target.<br>
	 * It checks if the kill meets the requirements for the quest.<br>
	 * Use this to progress the quest state.
	 * @param env The {@link QuestEnv} object containing current quest data.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			switch (env.getTargetId())
			{
				case 651765:
				case 651833:
					if (qs.getQuestVarById(1) < 2)
					{
						qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
						updateQuestStatus(env);
						return true;
					}
					else if (qs.getQuestVarById(1) == 2)
					{
						qs.setQuestVar(3);
						updateQuestStatus(env);
						return true;
					}
					
			}
		}
		
		return false;
	}
}
