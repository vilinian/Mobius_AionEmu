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
package system.handlers.quest.poeta;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * This class handles the logic for quest {@code 60001A} New Age.<br>
 * It manages specific quest progression and interactions within the {@link QuestHandler} framework.
 * @author QuestGenerator by Mariella
 */
public class _60001ANewAge extends QuestHandler
{
	private final static int questId = 60001;
	
	/**
	 * Initializes a new instance of the {@code _60001ANewAge} quest handler.<br>
	 * This constructor sets up the quest with the ID {@code 60001}.
	 */
	public _60001ANewAge()
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
		qe.registerQuestNpc(790001).addOnTalkEvent(questId); // Pernos
		qe.registerQuestNpc(820134).addOnTalkEvent(questId); // Guide to Rare Poisons
		qe.registerQuestNpc(820135).addOnTalkEvent(questId); // Day of the Storm
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
			if (targetId == 790001)
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
			else if (targetId == 820134)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 1352);
					case SELECT_ACTION_1353:
						return sendQuestDialog(env, 1353);
					case SELECT_ACTION_1354:
						return sendQuestDialog(env, 1354);
					case SELECT_ACTION_1355:
						return sendQuestDialog(env, 1355);
					case SETPRO2:
						qs.setQuestVar(2);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					default:
						break;
				}
			}
			else if (targetId == 820135)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 1693);
					case SELECT_ACTION_1694:
						return sendQuestDialog(env, 1694);
					case SELECT_ACTION_1695:
						return sendQuestDialog(env, 1695);
					case SELECT_ACTION_1696:
						return sendQuestDialog(env, 1696);
					case SELECT_ACTION_1697:
						return sendQuestDialog(env, 1697);
					case SELECT_ACTION_1698:
						return sendQuestDialog(env, 1698);
					case SET_SUCCEED:
						qs.setQuestVar(3);
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
			if (targetId == 790001)
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
