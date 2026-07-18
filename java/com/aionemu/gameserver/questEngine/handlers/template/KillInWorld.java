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
import com.aionemu.gameserver.model.rift.RiftLocation;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.RiftService;
import com.aionemu.gameserver.services.VortexService;

/**
 * This class handles quest logic for objectives that require killing specific enemies within a zone.<br>
 * It processes {@code onKillInZone} events defined in the XML configuration to update quest progress.
 * @author vlog, reworked bobobear
 */
public class KillInWorld extends QuestHandler
{
	private final int questId;
	private final Set<Integer> startNpcs = new HashSet<>();
	private final Set<Integer> endNpcs = new HashSet<>();
	private final Set<Integer> worldIds = new HashSet<>();
	private final int killAmount;
	private final int invasionWorldId;
	private final int endDialog;
	
	/**
	 * Initializes a new {@link KillInWorld} quest handler.<br>
	 * This constructor sets up the requirements for killing specific NPCs in certain worlds.
	 * @param questId The unique identifier for the quest.
	 * @param endNpcIds A list of NPC IDs that must be killed to complete the quest.
	 * @param startNpcIds A list of NPC IDs used to determine the starting state.
	 * @param worldIds A list of world IDs where the kills can occur.
	 * @param killAmount The total number of enemies required to be defeated.
	 * @param invasionWorld The ID of the invasion world associated with this quest.
	 * @param endDialog The ID of the dialog to trigger upon completion.
	 */
	public KillInWorld(int questId, List<Integer> endNpcIds, List<Integer> startNpcIds, List<Integer> worldIds, int killAmount, int invasionWorld, int endDialog)
	{
		super(questId);
		if (startNpcIds != null)
		{
			startNpcs.addAll(startNpcIds);
			startNpcs.remove(0);
		}
		
		if (endNpcIds == null)
		{
			endNpcs.addAll(startNpcs);
		}
		else
		{
			endNpcs.addAll(endNpcIds);
			endNpcs.remove(0);
		}
		
		this.questId = questId;
		this.worldIds.addAll(worldIds);
		this.worldIds.remove(0);
		this.killAmount = killAmount;
		invasionWorldId = invasionWorld;
		this.endDialog = endDialog;
	}
	
	/**
	 * Registers the required quest events.<br>
	 * This method tells the system which actions to listen for.<br>
	 * You should add your specific event listeners inside this method.
	 */
	@Override
	public void register()
	{
		Iterator<Integer> iterator = startNpcs.iterator();
		while (iterator.hasNext())
		{
			final int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}
		
		iterator = endNpcs.iterator();
		while (iterator.hasNext())
		{
			final int endNpc = iterator.next();
			qe.registerQuestNpc(endNpc).addOnTalkEvent(getQuestId());
		}
		
		iterator = worldIds.iterator();
		while (iterator.hasNext())
		{
			final int worldId = iterator.next();
			qe.registerOnKillInWorld(worldId, questId);
		}
		
		if (invasionWorldId != 0)
		{
			qe.registerOnEnterWorld(questId);
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
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int targetId = env.getTargetId();
		final DialogAction dialog = env.getDialog();
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			if (startNpcs.isEmpty() || startNpcs.contains(targetId))
			{
				switch (dialog)
				{
					case QUEST_SELECT:
					{
						return sendQuestDialog(env, 4762);
					}
					case QUEST_ACCEPT_1:
					{
						return sendQuestStartDialog(env);
					}
					default:
					{
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (endNpcs.contains(targetId) && (endDialog != 0))
			{
				switch (dialog)
				{
					case USE_OBJECT:
					{
						return sendQuestDialog(env, endDialog);
					}
					case SELECT_QUEST_REWARD:
					{
						return sendQuestDialog(env, 5);
					}
					case SELECTED_QUEST_NOREWARD:
					{
						return sendQuestEndDialog(env);
					}
					default:
						break;
				}
			}
			else
			{
				return sendQuestEndDialog(env);
			}
		}
		
		return false;
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
		final VortexLocation vortexLoc = VortexService.getInstance().getLocationByWorld(invasionWorldId);
		if (player.getWorldId() == invasionWorldId)
		{
			if (((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat()))
			{
				if (((vortexLoc != null) && vortexLoc.isActive()) || (searchOpenRift()))
				{
					return QuestService.startQuest(env);
				}
				else if ((invasionWorldId == 400010000) && player.isSpawned() && player.isProtectionActive())
				{
					return QuestService.startQuest(env);
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if there is an active rift in the specified world.<br>
	 * It iterates through all locations provided by {@link RiftService}.<br>
	 * Returns {@code true} if a matching open rift is found.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if an open rift exists in the invasion world, {@code false} otherwise.
	 */
	private boolean searchOpenRift()
	{
		for (RiftLocation loc : RiftService.getInstance().getRiftLocations().values())
		{
			if ((loc.getWorldId() == invasionWorldId) && loc.isOpened())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This method is triggered when a player kills an enemy in the world.<br>
	 * It allows for custom logic to be executed during this specific event.
	 * @param env The {@code QuestEnv} object containing current quest data.
	 * @return Returns {@code false} by default unless overridden.
	 */
	@Override
	public boolean onKillInWorldEvent(QuestEnv env)
	{
		return defaultOnKillRankedEvent(env, 0, killAmount, true); // reward
	}
}
