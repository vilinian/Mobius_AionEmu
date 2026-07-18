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

/**
 * Represents the template for an action required to complete an achievement.<br>
 * This class defines the specific criteria or triggers that a player must fulfill.<br>
 * It is used by {@link com.aionemu.gameserver.model.templates.achievement.AchievementTemplate} to manage progression.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "achievement_action_template")
public class AchievementActionTemplate
{
	@XmlElement(name = "required")
	protected AchievementRequired required;
	@XmlElement(name = "rewards")
	protected ActionRewards rewards;
	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "title")
	protected Integer title;
	@XmlAttribute(name = "type")
	protected AchievementActionType type;
	@XmlAttribute(name = "maxvalue")
	protected Integer maxvalue;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
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
	 * Retrieves the rewards associated with this achievement action.<br>
	 * This method returns the {@code ActionRewards} object stored in the template.
	 * @return the {@link ActionRewards} object.
	 */
	public ActionRewards getRewards()
	{
		return rewards;
	}
	
	/**
	 * Retrieves the requirements for this achievement action.<br>
	 * This method returns the {@code AchievementRequired} object associated with the template.
	 * @return the {@code AchievementRequired} object.
	 */
	public AchievementRequired getRequired()
	{
		return required;
	}
	
	/**
	 * Retrieves the action type for this achievement template.<br>
	 * This value determines how the achievement is triggered.
	 * @return the {@code AchievementActionType} of this template.
	 */
	public AchievementActionType getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the maximum value for this achievement action.<br>
	 * This value is stored in the {@code maxvalue} field.
	 * @return the maximum value as an {@code Integer}
	 */
	public Integer getMaxvalue()
	{
		return maxvalue;
	}
}
