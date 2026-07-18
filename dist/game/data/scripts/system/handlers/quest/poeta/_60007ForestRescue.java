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
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class handles the logic for quest {@code 60007} Forest Rescue.<br>
 * It manages player interactions and state transitions within this specific quest.
 * @author QuestGenerator by Mariella
 */
public class _60007ForestRescue extends QuestHandler
{
	private final static int questId = 60007;
	
	/**
	 * Initializes the quest handler for the Forest Rescue quest.<br>
	 * This constructor sets the {@code questId} to {@code 60007}.<br>
	 * It calls the superclass constructor of {@link QuestHandler}.
	 */
	public _60007ForestRescue()
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
		qe.registerQuestNpc(730008).addOnTalkEvent(questId); // Daminu
		qe.registerQuestNpc(820002).addOnTalkEvent(questId); // Investigating Royer
		qe.registerQuestNpc(820003).addOnTalkEvent(questId); // Implementer Royer
		qe.registerQuestNpc(651867).addOnKillEvent(questId);
		qe.registerQuestNpc(651848).addOnKillEvent(questId);
		qe.registerQuestNpc(700030).addOnTalkEvent(questId); // Odium Cauldron
		qe.registerQuestItem(182216249, questId); // Thin Flute Of Elim
		qe.registerQuestItem(182216250, questId); // Daminu Flute
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
			if (targetId == 730008)
			{
				switch (action)
				{
					case QUEST_SELECT:
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(() ->
						{
							if ((player.getTarget() == null) || (player.getTarget().getObjectId() != targetObjectId))
							{
								return;
							}
							
							PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, targetObjectId), true);
							PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 2));
							PacketSendUtility.sendPacket(env.getPlayer(), new SM_DIALOG_WINDOW(targetObjectId, 1011, env.getQuestId()));
						}, 3000);
					case SETPRO1:
						qs.setQuestVar(1);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					default:
						break;
				}
			}
			else if (targetId == 820002)
			{
				final int var = qs.getQuestVarById(0);
				if (var == 1)
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
						case CHECK_USER_HAS_QUEST_ITEM:
							return checkQuestItems(env, 2, 3, false, 10000, 10001);
						default:
							break;
					}
				}
				else if (var == 2)
				{
					
				}
			}
			else if (targetId == 820003)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 1693);
					case CHECK_USER_HAS_QUEST_ITEM:
						return checkQuestItems(env, 2, 3, false, 10000, 10001);
					default:
						break;
				}
			}
			else if (targetId == 700030)
			{
				switch (action)
				{
					case USE_OBJECT:
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
			if (targetId == 820003)
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
