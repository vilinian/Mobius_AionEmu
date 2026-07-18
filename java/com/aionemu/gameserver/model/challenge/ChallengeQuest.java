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

import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.templates.challenge.ChallengeQuestTemplate;

/**
 * Represents an active instance of a {@link ChallengeQuestTemplate} for a player.<br>
 * This class tracks the current progress and state of a specific challenge quest.
 * @author ViAl
 */
public class ChallengeQuest
{
	private final ChallengeQuestTemplate template;
	private int completeCount;
	private PersistentState persistentState;
	
	/**
	 * Creates a new instance of a {@link ChallengeQuest}.<br>
	 * This constructor initializes the quest with a specific template and completion count.
	 * @param template The {@code ChallengeQuestTemplate} used to define the quest details.
	 * @param completeCount The initial number of times this quest has been completed.
	 */
	public ChallengeQuest(ChallengeQuestTemplate template, int completeCount)
	{
		this.template = template;
		this.completeCount = completeCount;
	}
	
	/**
	 * Retrieves the unique identifier for this quest.<br>
	 * This value is obtained from the {@link ChallengeQuestTemplate}.
	 * @return The {@code int} ID of the quest.
	 */
	public int getQuestId()
	{
		return template.getId();
	}
	
	/**
	 * Retrieves the quest template associated with this challenge.<br>
	 * This method returns the {@link ChallengeQuestTemplate} object.
	 * @return The {@code ChallengeQuestTemplate} for this quest.
	 */
	public ChallengeQuestTemplate getQuestTemplate()
	{
		return template;
	}
	
	/**
	 * Retrieves the maximum number of times this challenge can be repeated.<br>
	 * This value is fetched from the {@link ChallengeQuestTemplate}.
	 * @return The maximum repeat count as an {@code int}.
	 */
	public int getMaxRepeats()
	{
		return template.getRepeatCount();
	}
	
	/**
	 * Retrieves the score awarded for completing a single quest.<br>
	 * This value is fetched from the {@link ChallengeQuestTemplate}.
	 * @return The integer score assigned to this quest.
	 */
	public int getScorePerQuest()
	{
		return template.getScore();
	}
	
	/**
	 * Retrieves the total number of times this challenge has been completed.<br>
	 * This value is updated by calling {@code increaseCompleteCount}.
	 * @return The current count of completions as an {@code int}.
	 */
	public int getCompleteCount()
	{
		return completeCount;
	}
	
	/**
	 * Increases the total number of completed quests.<br>
	 * This method updates the {@code completeCount} field.<br>
	 * It also sets the {@link PersistentState} to {@code UPDATE_REQUIRED}.
	 */
	public synchronized void increaseCompleteCount()
	{
		completeCount++;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this quest.<br>
	 * This method prevents changing from {@code PersistentState.NEW} to {@code PersistentState.UPDATE_REQUIRED}.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		if ((this.persistentState == PersistentState.NEW) && (persistentState == PersistentState.UPDATE_REQUIRED))
		{
			return;
		}
		
		this.persistentState = persistentState;
	}
}
