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
 * This class handles the logic for the quest {@code 70206TheFortressOfTheBakarmaLegion}.<br>
 * It manages all quest progression, objectives, and interactions for this specific mission.
 * @author QuestGenerator by Mariella
 */
public class _70206TheFortressOfTheBakarmaLegion extends QuestHandler
{
	private final static int questId = 70206;
	private final static int[] mobs =
	{
		702424,
		702041
	};
	
	/**
	 * Initializes the quest handler for {@code 70206TheFortressOfTheBakarmaLegion}.<br>
	 * This constructor sets up the quest with the ID {@code 70206}.<br>
	 * It calls the superclass constructor to register the quest.
	 */
	public _70206TheFortressOfTheBakarmaLegion()
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
		qe.registerQuestNpc(703492).addOnTalkEvent(questId); // Cheska's First Clue
		qe.registerQuestNpc(702424).addOnTalkEvent(questId); // Barrier Neutraliser
		qe.registerQuestNpc(703493).addOnTalkEvent(questId); // Cheska's Second Clue
		qe.registerQuestNpc(702041).addOnTalkEvent(questId); // Invasion weapon of the 45th Army
		qe.registerQuestNpc(806827).addOnTalkEvent(questId); // Waiting Cheska
		qe.registerQuestNpc(832820).addOnTalkEvent(questId); // Albrich
		qe.registerQuestItem(182216408, questId); // Cheska's First Letter
		qe.registerQuestItem(182216409, questId); // Cheska's Second Letter
		
		for (int mob : mobs)
		{
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
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
		
		if (qs == null)
		{
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START)
		{
			switch (targetId)
			{
				case 703492:
				{
					switch (dialog)
					{
						// ToDo: check correct action for this npc
						case USE_OBJECT:
						{
							qs.setQuestVar(1);
							updateQuestStatus(env);
							return false;
						}
						default:
							break;
					}
					break;
				}
				case 702424:
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
				case 703493:
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
				case 702041:
				{
					switch (dialog)
					{
						// ToDo: check correct action for this npc
						case USE_OBJECT:
						{
							qs.setQuestVar(6);
							updateQuestStatus(env);
							return false;
						}
						default:
							break;
					}
					break;
				}
				case 806827:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 3057);
						}
						case SETPRO7:
						{
							// giveQuestItem(env, 182216408, 1);
							qs.setQuestVar(7);
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
			if (targetId == 832820)
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
	/*
	 * @Override public boolean onKillEvent(QuestEnv env) { Player player = env.getPlayer(); QuestState qs = player.getQuestStateList().getQuestState(questId); if (qs != null && qs.getStatus() == QuestStatus.START) { int var = qs.getQuestVarById(0); int var1 = qs.getQuestVarById(1); // (0) Step: 2, Count: 3, Mobs : 702424 // (1) Step: 5, Count: 3, Mobs : 702041 switch (var) { case 2: { // ??? } case 5: { // ??? } default: break; } return false; } return false; }
	 */
}
