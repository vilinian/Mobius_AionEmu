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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.QuestsData;
import com.aionemu.gameserver.model.templates.quest.QuestCategory;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * This class manages a collection of {@link QuestState} objects for a player.<br>
 * It tracks the current progress and status of all active or completed quests.
 * @author MrPoke
 */
public class QuestStateList
{
	private static final Logger log = LoggerFactory.getLogger(QuestStateList.class);
	private final SortedMap<Integer, QuestState> _quests;
	private final QuestsData _questData = DataManager.QUEST_DATA;
	
	/**
	 * Creates a new instance of {@code QuestStateList}.<br>
	 * This initializes an empty collection for tracking quest states.
	 */
	public QuestStateList()
	{
		_quests = new TreeMap<>();
	}
	
	/**
	 * Adds a new quest to the player's quest list.<br>
	 * This method ensures that no duplicate {@code questId} values are added.
	 * @param questId The unique identifier for the quest.
	 * @param questState The state information for the quest.
	 * @return {@code true} if the quest was added successfully, or {@code false} if it already exists.
	 */
	public synchronized boolean addQuest(int questId, QuestState questState)
	{
		if (_quests.containsKey(questId))
		{
			log.warn("Duplicate quest. ");
			return false;
		}
		
		_quests.put(questId, questState);
		return true;
	}
	
	/**
	 * Removes a quest from the player's active list.<br>
	 * This method checks if the {@code questId} exists in the collection.<br>
	 * It returns {@code true} if the removal was successful.<br>
	 * It returns {@code false} if the quest was not found.
	 * @param questId The unique identifier of the quest to remove.
	 * @return {@code true} if removed, otherwise {@code false}.
	 */
	public synchronized boolean removeQuest(int questId)
	{
		if (_quests.containsKey(questId))
		{
			_quests.remove(questId);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the current state of a specific quest.<br>
	 * This method looks up the {@code QuestState} using the provided ID.
	 * @param questId The unique identifier for the quest.
	 * @return The {@code QuestState} associated with the ID, or {@code null} if not found.
	 */
	public QuestState getQuestState(int questId)
	{
		return _quests.get(questId);
	}
	
	/**
	 * Retrieves all quest states currently in the list.<br>
	 * This method returns a {@code Collection} of every {@link QuestState}.
	 * @return A collection containing all {@code QuestState} objects.
	 */
	public Collection<QuestState> getAllQuestState()
	{
		return _quests.values();
	}
	
	/**
	 * Retrieves all quests that have been completed.<br>
	 * This method filters the internal quest list for items with a {@code QuestStatus.COMPLETE} status.
	 * @return A {@link List} containing all finished {@link QuestState} objects.
	 */
	public List<QuestState> getAllFinishedQuests()
	{
		final List<QuestState> completeQuestList = new ArrayList<>();
		for (QuestState qs : _quests.values())
		{
			if (qs.getStatus() == QuestStatus.COMPLETE)
			{
				completeQuestList.add(qs);
			}
		}
		
		return completeQuestList;
	}
	
	/*
	 * Issue #13 fix Used by the QuestService to check the amount of normal quests in the player's list
	 * @author vlog
	 */
	/**
	 * Returns the total number of normal quests.<br>
	 * This method calls {@code getNormalQuests} to retrieve the collection.<br>
	 * It then returns the size of that collection.
	 * @return The count of normal quests as an {@code int}.
	 */
	public int getNormalQuestListSize()
	{
		return getNormalQuests().size();
	}
	
	/*
	 * Issue #13 fix Returns the list of normal quests
	 * @author vlog
	 */
	/**
	 * Retrieves a list of all active normal quests.<br>
	 * This method filters out completed, locked, or event-specific quests.<br>
	 * It only includes items categorized as {@code QUEST}.
	 * @return A {@code Collection} of {@link QuestState} objects for normal quests.
	 */
	public Collection<QuestState> getNormalQuests()
	{
		final Collection<QuestState> l = new ArrayList<>();
		
		for (QuestState qs : getAllQuestState())
		{
			final QuestCategory qc = _questData.getQuestById(qs.getQuestId()).getCategory();
			final String name = _questData.getQuestById(qs.getQuestId()).getName();
			final QuestStatus s = qs.getStatus();
			
			if ((s != QuestStatus.COMPLETE) && (s != QuestStatus.LOCKED) && (s != QuestStatus.NONE) && (qc == QuestCategory.QUEST) && !name.startsWith("[Event]"))
			{
				l.add(qs);
			}
		}
		
		return l;
	}
	
	/*
	 * Returns true if there is a quest in the list with this id Used by the QuestService
	 * @author vlog
	 */
	/**
	 * Checks if a specific quest exists in the current list.<br>
	 * This method looks for the {@code questId} within the internal collection.
	 * @param questId The unique identifier of the quest to check.
	 * @return {@code true} if the quest is found, otherwise {@code false}.
	 */
	public boolean hasQuest(int questId)
	{
		return _quests.containsKey(questId);
	}
	
	/*
	 * Change the old value of the quest status to the new one Used by the QuestService
	 * @author vlog
	 */
	/**
	 * Updates the status of a specific quest.<br>
	 * This method finds a quest by its unique identifier and applies a new {@code QuestStatus}.
	 * @param key The unique ID of the quest to update.
	 * @param newStatus The new {@code QuestStatus} to assign to the quest.
	 */
	public void changeQuestStatus(Integer key, QuestStatus newStatus)
	{
		_quests.get(key).setStatus(newStatus);
	}
	
	/**
	 * Returns the total number of quests in this list.<br>
	 * This method calls {@code getQuests} to determine the count.
	 * @return The number of quest states currently stored.
	 */
	public int size()
	{
		return _quests.size();
	}
	
	/**
	 * Retrieves the complete collection of quests.<br>
	 * The results are sorted by their unique ID.
	 * @return a {@code SortedMap} containing quest IDs and their corresponding {@link QuestState}.
	 */
	public SortedMap<Integer, QuestState> getQuests()
	{
		return _quests;
	}
}
