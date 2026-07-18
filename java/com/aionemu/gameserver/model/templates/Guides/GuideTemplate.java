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
package com.aionemu.gameserver.model.templates.Guides;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Petition;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;

/**
 * Represents a template for in-game guides.<br>
 * This class stores the data required to display instructions or information to players.<br>
 * It serves as a data model for {@code Guide}.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuideTemplate")
public class GuideTemplate
{
	@XmlAttribute(name = "level")
	private int level;
	@XmlAttribute(name = "classType")
	private PlayerClass classType;
	@XmlAttribute(name = "title")
	private String title;
	@XmlAttribute(name = "race")
	private Race race;
	@XmlElement(name = "reward_info")
	private String rewardInfo = "";
	@XmlElement(name = "message")
	private String message = "";
	@XmlElement(name = "select")
	private String select = "";
	@XmlElement(name = "survey")
	private List<SurveyTemplate> surveys;
	@XmlAttribute(name = "rewardCount")
	private int rewardCount;
	@XmlTransient
	private boolean isActivated = true;
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the character class of the player.<br>
	 * This method returns the {@code PlayerClass} associated with this ranking result.
	 * @return The {@code PlayerClass} of the player.
	 */
	public PlayerClass getPlayerClass()
	{
		return classType;
	}
	
	/**
	 * Retrieves the title of the {@link Petition}.<br>
	 * This returns the name given to the petition.
	 * @return The {@code String} representing the title.
	 */
	public String getTitle()
	{
		return title;
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
	 * Retrieves the list of survey templates associated with this guide.<br>
	 * This method returns all {@code SurveyTemplate} objects stored in the template.
	 * @return a {@code List} of {@link SurveyTemplate} objects.
	 */
	public List<SurveyTemplate> getSurveys()
	{
		return surveys;
	}
	
	/**
	 * Retrieves the current message associated with this {@code FindGroup} object.<br>
	 * This method returns the string value stored in the {@code message} field.
	 * @return The message as a {@code String}.
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Retrieves the selection text from the guide template.<br>
	 * This value is stored in the {@code select} field.
	 * @return the {@code String} representation of the selection.
	 */
	public String getSelect()
	{
		return select;
	}
	
	/**
	 * Retrieves the description of the rewards for this guide.<br>
	 * This information is stored in the {@code rewardInfo} field.
	 * @return a {@code String} containing the reward details.
	 */
	public String getRewardInfo()
	{
		return rewardInfo;
	}
	
	/**
	 * Retrieves the total number of rewards.<br>
	 * This method converts the internal {@code rewardCount} value to an {@code int}.
	 * @return The count of rewards as an {@code int}.
	 */
	public int getRewardCount()
	{
		return rewardCount;
	}
	
	/**
	 * Checks if this guide template is currently active.<br>
	 * Returns {@code true} if it is enabled.<br>
	 * Returns {@code false} if it is disabled.
	 * @return the activation status of the template
	 */
	public boolean isActivated()
	{
		return isActivated;
	}
	
	/**
	 * Updates the activation status of this guide template.<br>
	 * This method sets whether the guide is currently active or not.
	 * @param isActivated The new status to set as {@code true} or {@code false}.
	 */
	public void setActivated(boolean isActivated)
	{
		this.isActivated = isActivated;
	}
}
