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
package com.aionemu.gameserver.questEngine.model;

import java.sql.Timestamp;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.challenge.ChallengeQuestTemplate;

/**
 * Represents the current progress and status of a quest for a specific player.<br>
 * This class tracks individual requirements and completion data associated with a {@link QuestTemplate}.
 * @author MrPoke
 * @modified vlog, Rolandas
 */
public class QuestState
{
	private final int questId;
	private final QuestVars questVars;
	private QuestStatus status;
	private int completeCount;
	private Timestamp completeTime;
	private Timestamp nextRepeatTime;
	private Integer reward;
	private PersistentState persistentState;
	private static final Logger log = LoggerFactory.getLogger(QuestState.class);
	
	/**
	 * Creates a new instance of {@link QuestState}.<br>
	 * This constructor initializes all quest progress data.<br>
	 * It sets the initial persistent state to {@code PersistentState.NEW}.
	 * @param questId The unique identifier for the quest.
	 * @param status The current progression status of the quest.
	 * @param questVars The raw variables associated with this quest.
	 * @param completeCount The number of times the quest has been finished.
	 * @param nextRepeatTime The timestamp for when the quest can be repeated.
	 * @param reward The reward granted upon completion.
	 * @param completeTime The timestamp of the last completion.
	 */
	public QuestState(int questId, QuestStatus status, int questVars, int completeCount, Timestamp nextRepeatTime, Integer reward, Timestamp completeTime)
	{
		this.questId = questId;
		this.status = status;
		this.questVars = new QuestVars(questVars);
		this.completeCount = completeCount;
		this.nextRepeatTime = nextRepeatTime;
		this.reward = reward;
		this.completeTime = completeTime;
		persistentState = PersistentState.NEW;
	}
	
	/**
	 * Retrieves the variables associated with this quest.<br>
	 * This method returns the {@code QuestVars} object for the current state.
	 * @return The {@code QuestVars} instance.
	 */
	public QuestVars getQuestVars()
	{
		return questVars;
	}
	
	/**
	 * Updates a specific variable within the quest state.<br>
	 * This method sets the value for a given ID and marks the state as requiring an update.
	 * @param id The unique identifier of the quest variable.
	 * @param var The new integer value to assign to the variable.
	 */
	public void setQuestVarById(int id, int var)
	{
		questVars.setVarById(id, var);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the value of a specific quest variable.<br>
	 * This method looks up the data using the provided {@code id}.<br>
	 * It delegates the request to the {@code getQuestVars} collection.
	 * @param id The unique identifier for the quest variable.
	 * @return The integer value associated with the given {@code id}.
	 */
	public int getQuestVarById(int id)
	{
		return questVars.getVarById(id);
	}
	
	/**
	 * Updates the current quest variable.<br>
	 * This method also marks the state as requiring an update.
	 * @param var The new value to set for the quest variable.
	 */
	public void setQuestVar(int var)
	{
		questVars.setVar(var);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the current progress of the quest.<br>
	 * This method returns the {@code QuestStatus} for this specific quest instance.
	 * @return The current {@link QuestStatus} of the quest.
	 */
	public QuestStatus getStatus()
	{
		return status;
	}
	
	/**
	 * Updates the current status of the quest.<br>
	 * This method automatically updates the completion time if the new status is {@code QuestStatus.COMPLETE}.<br>
	 * It also marks the persistent state as requiring an update.
	 * @param status The new {@link QuestStatus} to assign to this quest.
	 */
	public void setStatus(QuestStatus status)
	{
		if ((status == QuestStatus.COMPLETE) && (this.status != QuestStatus.COMPLETE))
		{
			updateCompleteTime();
		}
		
		this.status = status;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
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
	 * Sets the completion timestamp for this quest.<br>
	 * This updates the {@code completeTime} field with the provided value.
	 * @param time The {@code Timestamp} representing when the quest was finished.
	 */
	public void setCompleteTime(Timestamp time)
	{
		completeTime = time;
	}
	
	/**
	 * Updates the {@code completeTime} field.<br>
	 * This method sets the time to the current system time.<br>
	 * It uses the current instance of {@code Calendar}.
	 */
	public void updateCompleteTime()
	{
		completeTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
	}
	
	/**
	 * Retrieves the unique identifier for this quest.<br>
	 * This value is obtained from the {@link ChallengeQuestTemplate}.
	 * @return The {@code int} ID of the quest.
	 */
	public int getQuestId()
	{
		return questId;
	}
	
	/**
	 * Updates the number of times a quest has been completed.<br>
	 * This method also marks the state as requiring an update.
	 * @param completeCount The new count to set for completed quests.
	 */
	public void setCompleteCount(int completeCount)
	{
		this.completeCount = completeCount;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
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
	 * Sets the time when the quest can be repeated.<br>
	 * This updates the {@code nextRepeatTime} field in this {@link QuestState}.
	 * @param nextRepeatTime The new {@code Timestamp} for the repeat time.
	 */
	public void setNextRepeatTime(Timestamp nextRepeatTime)
	{
		this.nextRepeatTime = nextRepeatTime;
	}
	
	/**
	 * Retrieves the scheduled time for the next quest repetition.<br>
	 * This value is used to determine when a player can start the quest again.
	 * @return The {@code Timestamp} of the next repeat time or {@code null}.
	 */
	public Timestamp getNextRepeatTime()
	{
		return nextRepeatTime;
	}
	
	/**
	 * Sets the reward value for this quest.<br>
	 * This method also updates the {@link PersistentState}.
	 * @param reward The new reward amount to assign.
	 */
	public void setReward(Integer reward)
	{
		this.reward = reward;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the reward value for a finished quest.<br>
	 * This method returns the {@code reward} amount associated with the quest.
	 * @return The reward as an {@code Integer}.
	 */
	public Integer getReward()
	{
		if (reward == null)
		{
			log.warn("No reward for the quest " + String.valueOf(questId));
		}
		else
		{
			return reward;
		}
		
		return 0;
	}
	
	/**
	 * Checks if the quest is eligible to be repeated.<br>
	 * This method validates the current status, completion count, and time restrictions.<br>
	 * It ensures that all requirements from the {@link QuestTemplate} are met.
	 * @return {@code true} if the quest can be started again, otherwise {@code false}.
	 */
	public boolean canRepeat()
	{
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		if ((status != QuestStatus.NONE) && ((status != QuestStatus.COMPLETE) || ((completeCount >= template.getMaxRepeatCount()) && (template.getMaxRepeatCount() != 255))))
		{
			return false;
		}
		
		if (questVars.getQuestVars() != 0)
		{
			return false;
		}
		
		if (template.isTimeBased() && (nextRepeatTime != null))
		{
			final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			if (currentTime.before(nextRepeatTime))
			{
				return false;
			}
		}
		
		return true;
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
		switch (persistentState)
		{
			case DELETED:
				if (this.persistentState == PersistentState.NEW)
				{
					throw new IllegalArgumentException("Cannot change state to DELETED from NEW");
				}
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
				{
					break;
				}
			default:
				this.persistentState = persistentState;
		}
	}
}
