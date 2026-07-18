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
package system.handlers.quest.haramel;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * This class handles the quest logic for preventing Aether tapping within a specific instance.<br>
 * It manages the progression and requirements for quest {@code 61100}.
 * @author QuestGenerator by Mariella
 * @rework FrozenKiller
 */
public class _61100InstancePreventionOfAethertapping extends QuestHandler
{
	private final static int questId = 61100;
	private final static int[] mobs =
	{
		700950
	};
	
	/**
	 * Initializes the quest handler for instance prevention of aether tapping.<br>
	 * This constructor sets up the quest with ID {@code 61100}.
	 */
	public _61100InstancePreventionOfAethertapping()
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
		qe.registerQuestNpc(700950).addOnTalkEvent(questId); // Aether Cart
		qe.registerQuestNpc(703531).addOnTalkEvent(questId); // Reprocessed Odella
		qe.registerQuestNpc(703532).addOnTalkEvent(questId); // Reprocessed Odella
		qe.registerQuestNpc(703533).addOnTalkEvent(questId); // Reprocessed Odella
		
		for (int mob : mobs)
		{
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
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
		if ((qs == null) && (player.getWorldId() == 300200000))
		{
			if (QuestService.startQuest(env))
			{
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
		final DialogAction dialog = env.getDialog();
		final Npc npc = (Npc) player.getTarget();
		final int targetId = env.getTargetId();
		
		if (qs == null)
		{
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START)
		{
			final int var = qs.getQuestVarById(0);
			final int var1 = qs.getQuestVarById(1);
			switch (targetId)
			{
				case 700950:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							return sendQuestDialog(env, 1011);
						}
						case SET_SUCCEED:
						{
							qs.setQuestVar(1);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				case 703531:
				case 703532:
				case 703533:
				{
					switch (dialog)
					{
						case USE_OBJECT:
						{
							if ((var == 1) && (var1 < 2))
							{
								qs.setQuestVarById(1, var1 + 1);
								updateQuestStatus(env);
								npc.getController().onDie(player);
								return false;
							}
							
							qs.setQuestVar(2);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return true;
						}
						default:
							break;
					}
				}
				default:
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 0)
			{
				if (dialog == DialogAction.QUEST_AUTO_REWARD)
				{
					return sendQuestDialog(env, 5);
				}
				
				return sendQuestEndDialog(env);
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
			final int var1 = qs.getQuestVarById(1);
			
			// (0) Step: 0, Count: 3, Mobs : 700950
			
			if ((var1 >= 0) && (var1 <= 1))
			{
				return defaultOnKillEvent(env, mobs, var1, var1 + 1, 1);
			}
			
			qs.setQuestVarById(0, 1);
			qs.setQuestVarById(1, 0);
			updateQuestStatus(env);
			return true;
		}
		
		return false;
	}
}
