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
package system.handlers.quest.gelkmaros;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Handles the quest logic for "Securing Aether as Combat Preparation".<br>
 * This class manages specific quest progression and requirements within the Gelkmaros quest line.
 * @author QuestGenerator by Mariella
 */
public class _70301SecuringAetherAsCombatPreparation extends QuestHandler
{
	private final static int questId = 70301;
	private final static int[] mobs =
	{
		650203,
		650206,
		650204,
		650207,
		650205,
		650208
	};
	
	/**
	 * Initializes the quest handler for securing aether as combat preparation.<br>
	 * This method sets up the quest with the ID {@code 70301}.<br>
	 * It calls the constructor of the {@link QuestHandler} class.
	 */
	public _70301SecuringAetherAsCombatPreparation()
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
		qe.registerQuestNpc(799364).addOnTalkEvent(questId); // Merhen
		qe.registerQuestNpc(799226).addOnTalkEvent(questId); // Valetta
		qe.registerQuestNpc(820172).addOnTalkEvent(questId); // Aether Generator Creation Area
		
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
				case 799364:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case SETPRO1:
						{
							qs.setQuestVar(1);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				case 799226:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1352);
						}
						case SETPRO2:
						{
							qs.setQuestVar(2);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				case 820172:
				{
					switch (dialog)
					{
						// ToDo: check correct action for this npc
						case USE_OBJECT:
						{
							qs.setQuestVar(4);
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
			if (targetId == 799226)
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
	 * @Override public boolean onKillEvent(QuestEnv env) { Player player = env.getPlayer(); QuestState qs = player.getQuestStateList().getQuestState(questId); if (qs != null && qs.getStatus() == QuestStatus.START) { int var = qs.getQuestVarById(0); int var1 = qs.getQuestVarById(1); // (0) Step: 2, Count: 10, Mobs : 650203, 650206, 650204, 650207, 650205, 650208 if (var == 2 && var1 < 9) { return defaultOnKillEvent(env, mobs, var1, var1 + 1, 1); } else { qs.setQuestVar(3); updateQuestStatus(env); } } return false; }
	 */
}
