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
 * Handles the quest logic for "Readiness to Become an Archon Soldier".<br>
 * This class manages the specific requirements and progression for quest {@code 70004}.
 * @author Falke_34
 * @author FrozenKiller
 */
public class _70004ReadinessToBecomeAnArchonSoldier extends QuestHandler
{
	private final static int questId = 70004;
	
	/**
	 * Initializes the quest handler for the Archon Soldier readiness quest.<br>
	 * This constructor sets the {@code questId} to {@code 70004}.
	 */
	public _70004ReadinessToBecomeAnArchonSoldier()
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
		qe.registerOnEnterWorld(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(806814).addOnTalkEvent(questId); // Marko
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
		@SuppressWarnings("unused")
		final int var = qs.getQuestVarById(0);
		
		if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 806814)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1012:
						return sendQuestDialog(env, 1012);
					case SELECT_ACTION_1013:
						return sendQuestDialog(env, 1013);
					case SELECT_ACTION_1034:
						return sendQuestDialog(env, 1034);
					case SELECT_ACTION_1097:
						return sendQuestDialog(env, 1097);
					case SELECT_ACTION_1182:
						return sendQuestDialog(env, 1182);
					case SELECT_ACTION_1183:
						return sendQuestDialog(env, 1183);
					case SELECT_ACTION_1098:
						return sendQuestDialog(env, 1098);
					case SELECT_ACTION_1119:
						return sendQuestDialog(env, 1119);
					case SET_SUCCEED:
						qs.setQuestVar(3);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 5);
					default:
						break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 806814)
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
}
