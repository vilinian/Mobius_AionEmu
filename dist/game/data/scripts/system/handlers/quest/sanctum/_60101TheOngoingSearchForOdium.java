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
package system.handlers.quest.sanctum;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Handles the logic for the quest {@code 60101} titled "The Ongoing Search for Odium".<br>
 * This class manages player progression and interactions specific to this quest.<br>
 * It extends {@link QuestHandler} to process various quest-related events.
 * @author FrozenKiller
 */
public class _60101TheOngoingSearchForOdium extends QuestHandler
{
	private final static int questId = 60101;
	
	/**
	 * Initializes the quest handler for {@code 60101TheOngoingSearchForOdium}.<br>
	 * This constructor sets up the quest with its unique ID.
	 */
	public _60101TheOngoingSearchForOdium()
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
		qe.registerQuestNpc(798600).addOnTalkEvent(questId); // Eremitia
		qe.registerQuestNpc(805362).addOnTalkEvent(questId); // Kaisinel
		qe.registerQuestNpc(203726).addOnTalkEvent(questId); // Polyidus
		qe.registerQuestNpc(820016).addOnTalkEvent(questId); // Royer 2
		qe.registerOnEnterZone(ZoneName.get("NEW_HEIRON_GATE_210040000"), questId);
		qe.registerOnLogOut(questId);
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
		return defaultOnLvlUpEvent(env, 60100, false);
	}
	
	/**
	 * This method handles the logic when a player enters a specific zone.<br>
	 * It checks if the player is currently on the correct quest step.<br>
	 * If the conditions are met, it updates the quest status to {@code REWARD}.
	 * @param env The current quest environment containing player data.
	 * @param zoneName The name of the zone the player just entered.
	 * @return {@code true} if the quest progress was updated, otherwise {@code false}.
	 */
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName)
	{
		final Player player = env.getPlayer();
		if (player == null)
		{
			return false;
		}
		
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			final int var = qs.getQuestVarById(0);
			if ((var == 3) && (zoneName == ZoneName.get("NEW_HEIRON_GATE_210040000")))
			{
				changeQuestStep(env, 3, 4, true);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Handles the logic when a player logs out.<br>
	 * This method checks if the quest progress can be updated.<br>
	 * It updates {@code QuestState} variables based on specific conditions.
	 * @param env The {@link QuestEnv} containing the current quest environment.
	 * @return {@code true} if the logout event was handled successfully, otherwise {@code false}.
	 */
	@Override
	public boolean onLogOutEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
		{
			return false;
		}
		
		final int var = qs.getQuestVarById(0);
		if (var <= 2)
		{
			qs.setQuestVar(0);
			updateQuestStatus(env);
			return true;
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
		final int targetId = env.getTargetId();
		
		if (qs == null)
		{
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START)
		{
			switch (targetId)
			{
				case 798600:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env, 1011);
						case SETPRO1:
							qs.setQuestVar(1);
							updateQuestStatus(env);
							TeleportService2.teleportTo(player, 110020000, 483f, 497f, 499f, (byte) 69, TeleportAnimation.BEAM_ANIMATION);
							return closeDialogWindow(env);
						default:
							break;
					}
					break;
				}
				case 805362:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env, 1352);
						case SETPRO2:
							qs.setQuestVar(2);
							updateQuestStatus(env);
							TeleportService2.teleportTo(player, 110010000, 1334.7222F, 1511.4169f, 569.03876f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
							return closeDialogWindow(env);
						default:
							break;
					}
					break;
				}
				case 203726:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env, 1693);
						case SETPRO3:
							qs.setQuestVar(3);
							updateQuestStatus(env);
							return closeDialogWindow(env);
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
			if (targetId == 820016)
			{
				switch (dialog)
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
