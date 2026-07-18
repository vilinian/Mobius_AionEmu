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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.challenge.ChallengeQuestTemplate;

/**
 * Represents the conditions required to complete a quest.<br>
 * This class defines the criteria that must be met for a {@code Quest} to be marked as finished.
 * @author antness
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinishedQuest", propOrder =
{
	"questId",
	"reward"
})
public class FinishedQuestCond
{
	@XmlAttribute(name = "quest_id", required = true)
	protected int questId;
	@XmlAttribute(name = "reward")
	protected int reward;
	
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
	 * Retrieves the reward value for a finished quest.<br>
	 * This method returns the {@code reward} amount associated with the quest.
	 * @return The reward as an {@code Integer}.
	 */
	public Integer getReward()
	{
		return reward;
	}
}
