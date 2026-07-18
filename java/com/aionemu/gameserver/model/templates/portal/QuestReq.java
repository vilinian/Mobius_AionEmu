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
package com.aionemu.gameserver.model.templates.portal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.challenge.ChallengeQuestTemplate;

/**
 * Represents the requirements needed to complete a quest.<br>
 * This class stores data used by {@code Portal} to validate player progress.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestReq")
public class QuestReq
{
	@XmlAttribute(name = "quest_id")
	protected int questId;
	@XmlAttribute(name = "quest_step")
	protected int questStep;
	@XmlAttribute(name = "err_quest")
	protected int errQuest;
	
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
	 * Sets the unique identifier for a quest.<br>
	 * This updates the {@code questId} field in this object.
	 * @param value The new {@code int} ID to assign to the quest.
	 */
	public void setQuestId(int value)
	{
		questId = value;
	}
	
	/**
	 * Retrieves the current step of a specific quest.<br>
	 * This value is used to track progress within a quest sequence.
	 * @return The current {@code int} value for the quest step.
	 */
	public int getQuestStep()
	{
		return questStep;
	}
	
	/**
	 * Updates the current step of a quest.<br>
	 * This method sets the {@code questStep} field to a new value.
	 * @param value The new integer value for the quest step.
	 */
	public void setQuestStep(int value)
	{
		questStep = value;
	}
	
	/**
	 * Retrieves the error quest identifier.<br>
	 * This value is used to identify specific errors related to quests.
	 * @return The {@code int} value of the error quest.
	 */
	public int getErrQuest()
	{
		return errQuest;
	}
}
