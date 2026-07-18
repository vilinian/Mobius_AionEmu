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
package system.handlers.quest.inggison;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * This class handles the quest logic for "Drana: The Energy Source of the Balaur".<br>
 * It manages specific quest progression and interactions within the {@link QuestService}.
 * @author QuestGenerator by Mariella
 */
public class _60303DranaTheEnergySourceOfTheBalaur extends QuestHandler
{
	private final static int questId = 60303;
	private final static int[] mobs =
	{
		216784,
		650573,
		650574,
		650842
	};
	
	/**
	 * Initializes the quest handler for Drana The Energy Source Of The Balaur.<br>
	 * This constructor sets the {@code questId} to 60303.<br>
	 * It calls the superclass constructor to register the quest.
	 */
	public _60303DranaTheEnergySourceOfTheBalaur()
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
		qe.registerQuestNpc(798958).addOnTalkEvent(questId); // Veille
		
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
		return defaultOnLvlUpEvent(env, 60200, false);
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
			final int var = qs.getQuestVarById(0);
			
			switch (targetId)
			{
				case 798958:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							if (var == 0)
							{
								return sendQuestDialog(env, 1352);
							}
							
							return sendQuestDialog(env, 1693);
						}
						case SETPRO2:
						{
							qs.setQuestVar(2);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						case CHECK_USER_HAS_QUEST_ITEM:
						{
							if (QuestService.collectItemCheck(env, true))
							{
								qs.setQuestVar(4);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 5);
							}
							
							return sendQuestDialog(env, 10001);
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
			if (targetId == 798958)
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
	 * @Override public boolean onKillEvent(QuestEnv env) { Player player = env.getPlayer(); QuestState qs = player.getQuestStateList().getQuestState(questId); if (qs != null && qs.getStatus() == QuestStatus.START) { int var = qs.getQuestVarById(0); int var1 = qs.getQuestVarById(1); // (0) Step: 2, Count: 2, Mobs : 216784, 650573 // (1) Step: 2, Count: 2, Mobs : 650574, 650842 switch (var) { case 2: { // ??? } case 2: { // ??? } default: break; } return false; } return false; }
	 */
}
