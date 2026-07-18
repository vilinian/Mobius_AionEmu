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
package com.aionemu.gameserver.questEngine.handlers.template;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestActionType;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class handles the distribution of rewards for fountain-related quests.<br>
 * It processes quest logic when a player interacts with a fountain object.<br>
 * It extends {@link QuestHandler} to manage specific reward actions.
 * @author Wakizashi, vlog, Bobobear
 * @reworked Luzien
 */
public class FountainRewards extends QuestHandler
{
	private final int questId;
	private final Set<Integer> startNpcs = new HashSet<>();
	
	/**
	 * Creates a new instance of {@link FountainRewards}.<br>
	 * This constructor initializes the rewards for a specific quest.<br>
	 * It also sets up the starting NPCs required for this reward type.
	 * @param questId The unique identifier for the quest.
	 * @param startNpcIds A list of IDs representing the starting NPCs.
	 */
	public FountainRewards(int questId, List<Integer> startNpcIds)
	{
		super(questId);
		this.questId = questId;
		startNpcs.addAll(startNpcIds);
		startNpcs.remove(0);
	}
	
	/**
	 * Registers the required quest events.<br>
	 * This method tells the system which actions to listen for.<br>
	 * You should add your specific event listeners inside this method.
	 */
	@Override
	public void register()
	{
		final Iterator<Integer> iterator = startNpcs.iterator();
		while (iterator.hasNext())
		{
			final int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}
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
		final int targetId = env.getTargetId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final DialogAction dialog = env.getDialog();
		
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			if (startNpcs.contains(targetId))
			{
				// Coin Fountain
				switch (dialog)
				{
					case USE_OBJECT:
					{
						if (!QuestService.inventoryItemCheck(env, true))
						{
							return true;
						}
						
						if ((targetId == 730241) || (targetId == 730242))
						{
							// hotfix for inggison and gelkmaros
							return sendQuestDialog(env, 1011);
						}
						
						return sendQuestSelectionDialog(env);
					}
					case SETPRO1:
					{
						if (QuestService.collectItemCheck(env, false))
						{
							if (!player.getInventory().isFullSpecialCube())
							{
								if (QuestService.startQuest(env))
								{
									changeQuestStep(env, 0, 0, true);
									return sendQuestDialog(env, 5);
								}
							}
							else
							{
								PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
								return sendQuestSelectionDialog(env);
							}
						}
						else
						{
							return sendQuestSelectionDialog(env);
						}
					}
					default:
						break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			// Coin Fountain
			if (startNpcs.contains(targetId))
			{
				// Coin Fountain
				if (dialog == DialogAction.SELECTED_QUEST_NOREWARD)
				{
					if (QuestService.collectItemCheck(env, true))
					{
						return sendQuestEndDialog(env);
					}
				}
				else
				{
					return QuestService.abandonQuest(player, questId);
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the player can perform a specific quest action.<br>
	 * This method verifies that the target ID is in the allowed start NPCs list.
	 * @param env The current quest environment containing player and quest data.
	 * @param questEventType The type of quest event being triggered.
	 * @param objects Variable arguments for additional context needed by the action.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects)
	{
		if (startNpcs.contains(env.getTargetId()))
		{
			// Coin Fountain
			return true;
		}
		
		return false;
	}
}
