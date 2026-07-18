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
package system.handlers.quest.lakrum;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * This class handles the quest logic for the {@code Efims Notice} quest.<br>
 * It manages specific quest transitions and actions within the Lakrum region.
 * @author QuestGenerator by Mariella
 */
public class _62655EfimsNotice extends QuestHandler
{
	private final static int questId = 62655;
	private final static int[] mobs =
	{
		886123,
		886124,
		886125,
		886126,
		886127,
		886128,
		886129,
		886130,
		886131,
		886132,
		886387,
		886388,
		886389,
		886390,
		886391,
		886133,
		886134,
		886135,
		886136,
		886137,
		886138,
		886139,
		886140,
		886141,
		886142,
		886392,
		886393,
		886394,
		886395,
		886396,
		886163,
		886164,
		886165,
		886166,
		886167,
		886168,
		886169,
		886170,
		886171,
		886172,
		886407,
		886408,
		886409,
		886410,
		886411,
		886173,
		886174,
		886175,
		886176,
		886177,
		886178,
		886179,
		886180,
		886181,
		886182,
		886412,
		886413,
		886414,
		886415,
		886416
	};
	
	/**
	 * Initializes the quest handler for quest ID {@code 62655}.<br>
	 * This constructor calls the superclass to register the specific quest identifier.
	 */
	public _62655EfimsNotice()
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
		qe.registerQuestNpc(836614).addOnQuestStart(questId); // Euron
		qe.registerQuestNpc(836614).addOnTalkEvent(questId); // Euron
		
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
		return defaultOnLvlUpEvent(env, 60506, false);
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
			if (targetId == 836614)
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
				case 836614:
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
			if (targetId == 836614)
			{
				return sendQuestEndDialog(env);
			}
		}
		
		return false;
	}
	/*
	 * @Override public boolean onKillEvent(QuestEnv env) { Player player = env.getPlayer(); QuestState qs = player.getQuestStateList().getQuestState(questId); if (qs != null && qs.getStatus() == QuestStatus.START) { int var = qs.getQuestVarById(0); int var1 = qs.getQuestVarById(1); // (0) Step: 1, Count: 10, Mobs : 886123, 886124, 886125, 886126, 886127, 886128, 886129, 886130, 886131, 886132, 886387, 886388, 886389, 886390, 886391, 886133, 886134, 886135, 886136, 886137, 886138, 886139, 886140, 886141, 886142, 886392, 886393, 886394, 886395, 886396, 886163, 886164, 886165, 886166, 886167, 886168, 886169, 886170, 886171, 886172, 886407, 886408, 886409, 886410, 886411, 886173, 886174, 886175, 886176, 886177, 886178, 886179, 886180, 886181, 886182, 886412, 886413, 886414, 886415, 886416 if (var == 1 && var1 < 9) { return defaultOnKillEvent(env, mobs, var1, var1 + 1, 1); } else { qs.setQuestVar(2); updateQuestStatus(env); } } return false; }
	 */
}
