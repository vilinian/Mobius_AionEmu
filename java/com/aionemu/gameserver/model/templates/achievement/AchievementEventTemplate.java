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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import java.time.ZonedDateTime;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.MinionAction;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.utils.gametime.DateTimeUtil;

/**
 * Represents a template for an achievement event in the game.<br>
 * It defines the criteria and data required to trigger specific achievements.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "achievement_event_template")
public class AchievementEventTemplate
{
	@XmlElement(name = "event_item")
	protected AchievementEventRewards rewards;
	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "event_section")
	protected int section;
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
	@XmlAttribute(name = "active")
	protected Boolean active;
	@XmlAttribute(name = "complete_point")
	protected int completePoint;
	@XmlAttribute(name = "action_id")
	protected int actionId;
	@XmlAttribute(name = "start", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar startDate;
	@XmlAttribute(name = "end", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar endDate;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the rewards associated with this achievement event.<br>
	 * This method returns the {@code AchievementEventRewards} object.
	 * @return the {@code AchievementEventRewards} for this template.
	 */
	public AchievementEventRewards getRewards()
	{
		return rewards;
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
	 * Checks if the achievement event is currently enabled.<br>
	 * This method returns the status of the {@code active} attribute.
	 * @return {@code true} if the event is active, or {@code false} otherwise.
	 */
	public Boolean getActive()
	{
		return active;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link MinionAction}.<br>
	 * This ID is used to map actions between the client and server.
	 * @return The integer value of the {@code actionId}.
	 */
	public int getActionId()
	{
		return actionId;
	}
	
	/**
	 * Retrieves the completion point for this achievement event.<br>
	 * This value represents the required progress to finish the task.
	 * @return The {@code int} value of the completion point.
	 */
	public int getCompletePoint()
	{
		return completePoint;
	}
	
	/**
	 * Retrieves the section number for this achievement event.<br>
	 * This value corresponds to the {@code event_section} attribute.
	 * @return The current section as an {@code int}.
	 */
	public int getSection()
	{
		return section;
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
	
	/**
	 * Retrieves the start date of the achievement event.<br>
	 * This method converts the internal {@code XMLGregorianCalendar} to a {@link java.time.ZonedDateTime} object.
	 * @return The {@code ZonedDateTime} representing when the event begins.
	 */
	public ZonedDateTime getStartDate()
	{
		return DateTimeUtil.getDateTime(startDate.toGregorianCalendar());
	}
	
	/**
	 * Retrieves the end date of the achievement event.<br>
	 * This method converts the internal {@code XMLGregorianCalendar} to a {@link java.time.ZonedDateTime} object.
	 * @return The {@code ZonedDateTime} representing when the event ends.
	 */
	public ZonedDateTime getEndDate()
	{
		return DateTimeUtil.getDateTime(endDate.toGregorianCalendar());
	}
}
