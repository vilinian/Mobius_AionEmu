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
package system.handlers.quest.ishalgen;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This class handles the logic for quest {@code 70009} "Preventing The Haramel".<br>
 * It manages player interactions and progression steps within this specific mission.<br>
 * It extends {@link QuestHandler} to process game events related to this quest.
 * @author Falke_34
 */
public class _70009PreventingTheHaramelMission extends QuestHandler
{
	private final static int questId = 70009;
	
	/**
	 * Initializes the quest handler for mission {@code 70009}.<br>
	 * This constructor sets up the required quest ID.
	 */
	public _70009PreventingTheHaramelMission()
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
		qe.registerQuestNpc(703488).addOnTalkEvent(questId); // Third Odella Transport Track
		qe.registerQuestNpc(806883).addOnTalkEvent(questId); // Cheska
		qe.registerQuestNpc(820012).addOnTalkEvent(questId);
		qe.registerQuestNpc(799524).addOnTalkEvent(questId); // Gestanerk
		qe.registerQuestNpc(806814).addOnTalkEvent(questId); // Marko
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
	}
	
	/**
	 * Handles logic when a player enters a world event.<br>
	 * It checks if the quest is active and updates progress or spawns NPCs accordingly.
	 * @param env The current quest environment containing player data.
	 * @return Always returns {@code false}.
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
		else if ((player.getWorldId() == 300200000) && (qs.getStatus() != QuestStatus.COMPLETE))
		{
			final int instanceId = player.getInstanceId();
			qs.setQuestVar(2);
			updateQuestStatus(env);
			
			final List<Npc> mobs = new ArrayList<>();
			mobs.add((Npc) QuestService.spawnQuestNpc(300200000, instanceId, 806883, 141.7932f, 22.274172f, 144.2455f, (byte) 0));
			mobs.add((Npc) QuestService.spawnQuestNpc(300200000, instanceId, 799995, 221.85893f, 351.66858f, 141.01141f, (byte) 0));
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
		
		final int var = qs.getQuestVarById(0);
		final int targetId = env.getTargetId();
		final DialogAction dialog = env.getDialog();
		
		if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 703488)
			{
				if (dialog == DialogAction.USE_OBJECT)
				{
					changeQuestStep(env, 0, 1, false);
					return true;
				}
			}
			else if (targetId == 806883)
			{
				if (dialog == DialogAction.QUEST_SELECT)
				{
					return sendQuestDialog(env, 1693);
				}
				else if (dialog == DialogAction.SETPRO3)
				{
					return defaultCloseDialog(env, 2, 3);
				}
			}
			else if (targetId == 820012)
			{
				if (dialog == DialogAction.USE_OBJECT)
				{
					changeQuestStep(env, 3, 4, false);
					return true;
				}
			}
			else if (targetId == 799524)
			{
				if (dialog == DialogAction.QUEST_SELECT)
				{
					return sendQuestDialog(env, var == 7 ? 3398 : 3057);
				}
				else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM)
				{
					return checkQuestItems(env, 6, 7, false, 10000, 10001);
				}
				else if (dialog == DialogAction.SETPRO8)
				{
					return defaultCloseDialog(env, 7, 8);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 806814)
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
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName == ZoneName.get("HARAMEL_TOWER_300200000"))
		{
			qs.setQuestVar(5);
			updateQuestStatus(env);
			return true;
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
