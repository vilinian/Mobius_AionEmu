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
package com.aionemu.gameserver.model.challenge;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.templates.challenge.ChallengeQuestTemplate;
import com.aionemu.gameserver.model.templates.challenge.ChallengeTaskTemplate;

/**
 * Represents an individual task within a {@link ChallengeQuestTemplate}.<br>
 * This class stores the specific requirements and progress for a player's challenge.<br>
 * It maps data from {@link ChallengeTaskTemplate} to active game instances.
 * @author ViAl
 */
public class ChallengeTask
{
	private final int taskId;
	private final int ownerId;
	private final Map<Integer, ChallengeQuest> quests;
	private final Timestamp completeTime;
	private final ChallengeTaskTemplate template;
	
	/**
	 * Creates a new {@link ChallengeTask} instance from data.<br>
	 * This constructor is primarily used for loading tasks from the database.<br>
	 * It initializes the task properties and retrieves the associated template.
	 * @param taskId The unique identifier for the challenge task.
	 * @param ownerId The unique identifier of the player who owns this task.
	 * @param quests A map containing the quest IDs and their corresponding {@link ChallengeQuest} objects.
	 * @param completeTime The timestamp indicating when the task was finished.
	 */
	public ChallengeTask(int taskId, int ownerId, Map<Integer, ChallengeQuest> quests, Timestamp completeTime)
	{
		this.taskId = taskId;
		this.ownerId = ownerId;
		this.quests = quests;
		this.completeTime = completeTime;
		template = DataManager.CHALLENGE_DATA.getTaskByTaskId(taskId);
	}
	
	/**
	 * Creates a new {@code ChallengeTask} instance.<br>
	 * This constructor initializes the task using a provided template.<br>
	 * It sets up the initial quest list and assigns the owner ID.
	 * @param ownerId The unique identifier of the player who owns this task.
	 * @param template The {@link ChallengeTaskTemplate} used to define the task details.
	 */
	public ChallengeTask(int ownerId, ChallengeTaskTemplate template)
	{
		taskId = template.getId();
		this.ownerId = ownerId;
		final Map<Integer, ChallengeQuest> quests = new HashMap<>();
		for (ChallengeQuestTemplate qt : template.getQuests())
		{
			final ChallengeQuest quest = new ChallengeQuest(qt, 0);
			quest.setPersistentState(PersistentState.NEW);
			quests.put(qt.getId(), quest);
		}
		
		this.quests = quests;
		completeTime = new Timestamp(1000);
		this.template = template;
	}
	
	/**
	 * Retrieves the unique identifier for this task.<br>
	 * This ID corresponds to the {@code taskId} defined in the template.
	 * @return The integer ID of the task.
	 */
	public int getTaskId()
	{
		return taskId;
	}
	
	/**
	 * Retrieves the unique identifier of the owner.<br>
	 * This ID belongs to the player who owns this {@link ChallengeTask}.
	 * @return The {@code int} value representing the owner's ID.
	 */
	public int getOwnerId()
	{
		return ownerId;
	}
	
	/**
	 * Returns the total number of quests associated with this task.<br>
	 * It calculates the size of the {@code quests} map.
	 * @return The count of quests as an {@code int}.
	 */
	public int getQuestsCount()
	{
		return quests.size();
	}
	
	/**
	 * Retrieves all quests associated with this task.<br>
	 * The results are stored in a {@code Map}.
	 * @return A {@code Map} where the key is the quest ID and the value is the {@link ChallengeQuest}.
	 */
	public Map<Integer, ChallengeQuest> getQuests()
	{
		return quests;
	}
	
	/**
	 * Retrieves a specific quest from the task.<br>
	 * It looks up the quest using the provided {@code questId}.
	 * @param questId The unique identifier for the quest.
	 * @return The {@link ChallengeQuest} object, or {@code null} if not found.
	 */
	public ChallengeQuest getQuest(int questId)
	{
		return quests.get(questId);
	}
	
	/**
	 * Retrieves the time when the task was finished.<br>
	 * This returns a {@code Timestamp} object representing the completion date.
	 * @return The {@code Timestamp} of the completed task.
	 */
	public Timestamp getCompleteTime()
	{
		return completeTime;
	}
	
	/**
	 * Updates the {@code completeTime} to the current system time.<br>
	 * This method is thread-safe because it is {@code synchronized}.
	 */
	public synchronized void updateCompleteTime()
	{
		completeTime.setTime(System.currentTimeMillis());
	}
	
	/**
	 * Retrieves the {@code ChallengeTaskTemplate} associated with this task.<br>
	 * This provides access to the base configuration data for the challenge.
	 * @return The {@link ChallengeTaskTemplate} object.
	 */
	public ChallengeTaskTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Checks if all quests in this task are finished.<br>
	 * It compares the current completion count against the maximum allowed repeats for every quest.
	 * @return {@code true} if every quest is complete, otherwise {@code false}.
	 */
	public boolean isCompleted()
	{
		boolean isCompleted = true;
		for (ChallengeQuest quest : quests.values())
		{
			if (quest.getCompleteCount() < quest.getMaxRepeats())
			{
				isCompleted = false;
				break;
			}
		}
		
		return isCompleted;
	}
}
