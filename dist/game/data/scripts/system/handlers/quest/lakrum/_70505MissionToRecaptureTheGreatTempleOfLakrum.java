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
import com.aionemu.gameserver.services.QuestService;

/**
 * Handles the logic for quest {@code 70505} "Mission To Recapture The Great Temple Of Lakrum".<br>
 * This class manages the progression and requirements for players completing this specific mission.<br>
 * It extends {@link QuestHandler} to process game events related to this quest.
 * @author QuestGenerator by Mariella
 */
public class _70505MissionToRecaptureTheGreatTempleOfLakrum extends QuestHandler
{
	private final static int questId = 70505;
	private final static int[] mobs =
	{
		655094,
		655095,
		655096,
		655097,
		655098
	};
	
	/**
	 * Initializes the quest handler for the mission to recapture the Great Temple of Lakrum.<br>
	 * This constructor sets the {@code questId} to {@code 70505}.<br>
	 * It calls the superclass constructor to register the quest in the system.
	 */
	public _70505MissionToRecaptureTheGreatTempleOfLakrum()
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
		qe.registerQuestNpc(836566).addOnTalkEvent(questId); // Lurking Demit
		qe.registerQuestNpc(836567).addOnTalkEvent(questId); // Rith
		qe.registerQuestNpc(836554).addOnTalkEvent(questId); // Harun
		
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
		return defaultOnLvlUpEvent(env, 70506, false);
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
				case 836566:
				{
					switch (dialog)
					{
						case CHECK_USER_HAS_QUEST_ITEM:
						{
							if (QuestService.collectItemCheck(env, true))
							{
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return sendQuestDialog(env, 10000);
							}
							
							return sendQuestDialog(env, 10001);
						}
						default:
							break;
					}
					break;
				}
				case 836567:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 2375);
						}
						case SET_SUCCEED:
						{
							qs.setQuestVar(5);
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
			if (targetId == 836554)
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
	 * @Override public boolean onKillEvent(QuestEnv env) { Player player = env.getPlayer(); QuestState qs = player.getQuestStateList().getQuestState(questId); if (qs != null && qs.getStatus() == QuestStatus.START) { int var = qs.getQuestVarById(0); int var1 = qs.getQuestVarById(1); // (0) Step: 1, Count: 10, Mobs : 655094, 655095, 655096, 655097, 655098 if (var == 1 && var1 < 9) { return defaultOnKillEvent(env, mobs, var1, var1 + 1, 1); } else { qs.setQuestVar(2); updateQuestStatus(env); } } return false; }
	 */
}
