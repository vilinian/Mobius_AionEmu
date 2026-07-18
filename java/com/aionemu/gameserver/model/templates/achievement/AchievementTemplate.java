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
package com.aionemu.gameserver.model.templates.achievement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;

/**
 * Represents the configuration data for a specific achievement type.<br>
 * This template defines the requirements and properties used to award achievements to players.<br>
 * It serves as a blueprint for {@link com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType} objects.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "achievement_template")
public class AchievementTemplate
{
	@XmlElement(name = "rewards")
	protected AchievementRewards rewards;
	@XmlElement(name = "actions")
	protected AchievementAction actions;
	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "title")
	protected Integer title;
	@XmlAttribute(name = "type")
	protected AchievementType type;
	@XmlAttribute(name = "repeat")
	protected AchievementRepeat repeat;
	@XmlAttribute(name = "race")
	protected Race race;
	@XmlAttribute(name = "minlevel")
	protected Integer minlevel;
	@XmlAttribute(name = "maxlevel")
	protected Integer maxlevel;
	@XmlAttribute(name = "completecount")
	protected Integer completecount;
	
	/**
	 * Retrieves the rewards associated with this achievement.<br>
	 * This method returns the {@code AchievementRewards} object.
	 * @return the {@code AchievementRewards} for this template.
	 */
	public AchievementRewards getRewards()
	{
		return rewards;
	}
	
	/**
	 * Retrieves the actions associated with this achievement.<br>
	 * This method returns the {@code AchievementAction} object defined in the template.
	 * @return the {@code AchievementAction} for this achievement.
	 */
	public AchievementAction getActions()
	{
		return actions;
	}
	
	/**
	 * Retrieves the repetition status of this achievement event.<br>
	 * This method returns the {@code AchievementRepeat} value associated with the template.
	 * @return The {@code AchievementRepeat} object for this event.
	 */
	public AchievementRepeat getRepeat()
	{
		return repeat;
	}
	
	/**
	 * Retrieves the category of this achievement.<br>
	 * This method returns the {@code AchievementType} associated with the action.
	 * @return The {@code AchievementType} of this object.
	 */
	public AchievementType getType()
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
	
	/**
	 * Retrieves the total number of completions required for this achievement.<br>
	 * This value is stored in the {@code completecount} field.
	 * @return The number of times the achievement must be completed as an {@code Integer}.
	 */
	public Integer getCompletecount()
	{
		return completecount;
	}
	
	/**
	 * Retrieves the maximum level allowed for this achievement event.<br>
	 * This value is stored in the {@code maxlevel} field.
	 * @return The maximum level as an {@code Integer}.
	 */
	public Integer getMaxlevel()
	{
		return maxlevel;
	}
	
	/**
	 * Retrieves the minimum level required for this achievement event.<br>
	 * This value is used to check if a player meets the level requirement.
	 * @return The {@code Integer} representing the minimum level.
	 */
	public Integer getMinlevel()
	{
		return minlevel;
	}
	
	/**
	 * Retrieves the title of the achievement action.<br>
	 * This value is stored as an {@code Integer}.
	 * @return The title of the achievement action, or {@code null} if it is not set.
	 */
	public Integer getTitle()
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
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
}
