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
package com.aionemu.gameserver.model.templates.quest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.questEngine.QuestEngine;

/**
 * Represents an NPC that is associated with a specific quest.<br>
 * This class stores the data required to link {@link NpcTemplate} entities to quest objectives.
 * @author MrPoke
 */
public class QuestNpc
{
	private static final Logger log = LoggerFactory.getLogger(QuestNpc.class);
	private final List<Integer> onQuestStart;
	private final List<Integer> onKillEvent;
	private final List<Integer> onTalkEvent;
	private final List<Integer> onAttackEvent;
	private final List<Integer> onAddAggroListEvent;
	private final List<Integer> onAtDistanceEvent;
	private final int npcId;
	
	/**
	 * Creates a new instance of {@link QuestNpc}.<br>
	 * This constructor initializes all event lists to empty states.
	 * @param npcId The unique identifier for the NPC.
	 */
	public QuestNpc(int npcId)
	{
		this.npcId = npcId;
		onQuestStart = new ArrayList<>(0);
		onKillEvent = new ArrayList<>(0);
		onTalkEvent = new ArrayList<>(0);
		onAttackEvent = new ArrayList<>(0);
		onAddAggroListEvent = new ArrayList<>(0);
		onAtDistanceEvent = new ArrayList<>(0);
	}
	
	/**
	 * Registers an NPC as a valid actor for a specific quest.<br>
	 * This method checks if the NPC has the {@code quest_use_item} AI type.<br>
	 * If it does, it calls {@code int)}.
	 * @param questId The unique identifier of the quest.
	 * @param npcId The unique identifier of the NPC.
	 */
	private void registerCanAct(int questId, int npcId)
	{
		final NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(npcId);
		if (template == null)
		{
			log.warn("[QuestEngine] No such NPC template for " + npcId + " in Q" + questId);
			return;
		}
		
		final String aiName = DataManager.NPC_DATA.getNpcTemplate(npcId).getAi();
		if ("quest_use_item".equals(aiName))
		{
			QuestEngine.getInstance().registerCanAct(questId, npcId);
		}
	}
	
	/**
	 * Adds a quest ID to the list of quests triggered when this NPC's quest starts.<br>
	 * This method ensures that the {@code questId} is only added if it is not already present.
	 * @param questId The unique identifier for the quest to be linked to this NPC.
	 */
	public void addOnQuestStart(int questId)
	{
		if (!onQuestStart.contains(questId))
		{
			onQuestStart.add(questId);
		}
	}
	
	/**
	 * Retrieves the list of quest IDs triggered when a quest starts.<br>
	 * This method returns all IDs associated with the {@code onQuestStart} event.
	 * @return A {@code List<Integer>} containing the relevant quest IDs.
	 */
	public List<Integer> getOnQuestStart()
	{
		return onQuestStart;
	}
	
	/**
	 * Registers a quest to trigger when this NPC is attacked.<br>
	 * This method adds the {@code questId} to the internal attack event list.<br>
	 * It ensures that no duplicate IDs are added.
	 * @param questId The unique identifier of the quest to link to the attack event.
	 */
	public void addOnAttackEvent(int questId)
	{
		if (!onAttackEvent.contains(questId))
		{
			onAttackEvent.add(questId);
		}
	}
	
	/**
	 * Retrieves the list of quest IDs associated with an attack event.<br>
	 * This method returns all quests that trigger when this NPC is attacked.
	 * @return a {@code List<Integer>} containing the relevant quest IDs.
	 */
	public List<Integer> getOnAttackEvent()
	{
		return onAttackEvent;
	}
	
	/**
	 * Registers a quest that triggers when this NPC is killed.<br>
	 * This method adds the {@code questId} to the internal kill event list.<br>
	 * It also calls {@code int)} to enable the quest action.
	 * @param questId The unique identifier of the quest to associate with the kill event.
	 */
	public void addOnKillEvent(int questId)
	{
		if (!onKillEvent.contains(questId))
		{
			onKillEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}
	
	/**
	 * Retrieves the list of quest IDs associated with a kill event.<br>
	 * This method returns all quests that trigger when this NPC is killed.
	 * @return A {@code List<Integer>} containing the relevant quest IDs.
	 */
	public List<Integer> getOnKillEvent()
	{
		return onKillEvent;
	}
	
	/**
	 * Adds a quest to the list of talk events for this NPC.<br>
	 * This method ensures that the quest is registered correctly.<br>
	 * It checks if the {@code questId} is already present before adding it.
	 * @param questId The unique identifier of the quest to add.
	 */
	public void addOnTalkEvent(int questId)
	{
		if (!onTalkEvent.contains(questId))
		{
			onTalkEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}
	
	/**
	 * Retrieves the list of quest IDs associated with talking to this NPC.<br>
	 * This method returns all quests that trigger when a player interacts with the entity.
	 * @return A {@code List<Integer>} containing the relevant quest IDs.
	 */
	public List<Integer> getOnTalkEvent()
	{
		return onTalkEvent;
	}
	
	/**
	 * Adds a quest to the list of events triggered when an NPC joins an aggro list.<br>
	 * This method checks if the {@code questId} is already present before adding it.<br>
	 * It also calls {@code int)} to register the action.
	 * @param questId The unique identifier of the quest to add.
	 */
	public void addOnAddAggroListEvent(int questId)
	{
		if (!onAddAggroListEvent.contains(questId))
		{
			onAddAggroListEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}
	
	/**
	 * Retrieves the list of quest IDs associated with an aggro list event.<br>
	 * This method returns the {@code onAddAggroListEvent} collection.
	 * @return a {@code List<Integer>} containing the relevant quest IDs.
	 */
	public List<Integer> getOnAddAggroListEvent()
	{
		return onAddAggroListEvent;
	}
	
	/**
	 * Adds a quest to the list of events triggered by distance.<br>
	 * This method registers the {@code questId} if it is not already present.<br>
	 * It also calls {@code int)} to enable interaction.
	 * @param questId The unique identifier for the quest to add.
	 */
	public void addOnAtDistanceEvent(int questId)
	{
		if (!onAtDistanceEvent.contains(questId))
		{
			onAtDistanceEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}
	
	/**
	 * Retrieves the list of quest IDs triggered by distance events.<br>
	 * This method returns the {@code onAtDistanceEvent} collection.
	 * @return a {@code List<Integer>} containing all associated quest IDs.
	 */
	public List<Integer> getOnDistanceEvent()
	{
		return onAtDistanceEvent;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
}
