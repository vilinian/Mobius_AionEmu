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
package com.aionemu.gameserver.model.templates.challenge;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * Represents the data template for a challenge task in the game.<br>
 * This class defines the properties and requirements needed to create a specific quest or objective.<br>
 * It serves as a blueprint for {@code ChallengeTask}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChallengeTask", propOrder =
{
	"quest",
	"contrib",
	"reward"
})
public class ChallengeTaskTemplate
{
	@XmlElement(required = true)
	protected List<ChallengeQuestTemplate> quest;
	protected List<ContributionReward> contrib;
	@XmlElement(required = true)
	protected ChallengeReward reward;
	@XmlAttribute
	protected Boolean repeat;
	@XmlAttribute(name = "town_residence")
	protected Boolean townResidence;
	@XmlAttribute(name = "name_id")
	protected Integer nameId;
	@XmlAttribute(name = "max_level", required = true)
	protected int maxLevel;
	@XmlAttribute(name = "min_level", required = true)
	protected int minLevel;
	@XmlAttribute(name = "prev_task")
	protected Integer prevTask;
	@XmlAttribute(required = true)
	protected Race race;
	@XmlAttribute(required = true)
	protected ChallengeType type;
	@XmlAttribute(required = true)
	protected int id;
	
	/**
	 * Retrieves the list of quests associated with this challenge task.<br>
	 * This method returns the {@code quest} field from the template.
	 * @return a {@code List} of {@link ChallengeQuestTemplate} objects.
	 */
	public List<ChallengeQuestTemplate> getQuests()
	{
		return quest;
	}
	
	/**
	 * Retrieves the list of contribution rewards for this challenge.<br>
	 * This method returns all {@link ContributionReward} objects associated with the task.
	 * @return a {@code List} of {@code ContributionReward} objects.
	 */
	public List<ContributionReward> getContrib()
	{
		return contrib;
	}
	
	/**
	 * Retrieves the reward associated with this challenge task.<br>
	 * This method returns the {@code ChallengeReward} object defined in the template.
	 * @return the {@code ChallengeReward} for this task.
	 */
	public ChallengeReward getReward()
	{
		return reward;
	}
	
	/**
	 * Checks if this challenge task can be repeated.<br>
	 * It returns {@code true} if the {@code repeat} attribute is set to {@code true}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if repeatable, {@code false} otherwise.
	 */
	public boolean isRepeatable()
	{
		return (repeat != null) && (repeat == true);
	}
	
	/**
	 * Checks if the challenge task is a town residence.<br>
	 * This method returns {@code true} if the {@code townResidence} attribute is set to {@code true}.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if it is a town residence, {@code false} otherwise.
	 */
	public boolean isTownResidence()
	{
		return (townResidence != null) && (townResidence == true);
	}
	
	/**
	 * Retrieves the unique identifier for the quest name.<br>
	 * This value is used to identify the quest in the database.
	 * @return The {@code Integer} ID of the quest name.
	 */
	public Integer getNameId()
	{
		return nameId;
	}
	
	/**
	 * Gets the maximum level allowed for this challenge task.<br>
	 * This value is retrieved from the {@code maxLevel} field.
	 * @return The maximum level as an {@code int}.
	 */
	public int getMaxLevel()
	{
		return maxLevel;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return minLevel;
	}
	
	/**
	 * Retrieves the ID of the previous task.<br>
	 * This value is used to determine the sequence of challenges.
	 * @return The {@code Integer} ID of the previous task, or {@code null} if none exists.
	 */
	public Integer getPrevTask()
	{
		return prevTask;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the category of this challenge.<br>
	 * This identifies what kind of task it is.
	 * @return the {@code ChallengeType} of this template.
	 */
	public ChallengeType getType()
	{
		return type;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
