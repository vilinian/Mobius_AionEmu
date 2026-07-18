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
package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.instance.InstanceCoolTimeType;
import com.aionemu.gameserver.model.instance.InstanceType;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * Represents the cooldown configuration for specific types of game instances.<br>
 * This class defines how long a player must wait before re-entering an {@link InstanceType}.
 * @author VladimirZ
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceCooltime")
public class InstanceCooltime
{
	@XmlElement(name = "type")
	protected InstanceCoolTimeType coolTimeType;
	@XmlElement(name = "typevalue")
	protected String typevalue;
	@XmlElement(name = "ent_cool_time")
	protected Integer entCoolTime;
	@XmlElement(name = "indun_type")
	protected InstanceType indun_type;
	@XmlElement(name = "max_member_light")
	protected Integer maxMemberLight;
	@XmlElement(name = "max_member_dark")
	protected Integer maxMemberDark;
	@XmlElement(name = "enter_min_level_light")
	protected Integer enterMinLevelLight;
	@XmlElement(name = "enter_max_level_light")
	protected Integer enterMaxLevelLight;
	@XmlElement(name = "enter_min_level_dark")
	protected Integer enterMinLevelDark;
	@XmlElement(name = "enter_max_level_dark")
	protected Integer enterMaxLevelDark;
	@XmlElement(name = "alarm_unit_score")
	protected Integer alarmUnitScore;
	@XmlElement(name = "can_enter_mentor")
	protected boolean can_enter_mentor;
	@XmlElement(name = "enter_guild")
	protected boolean enter_guild;
	@XmlElement(name = "maxcount")
	protected Integer max_count;
	@XmlElement(name = "component")
	protected int component;
	@XmlElement(name = "component_count")
	protected int componentCount;
	@XmlElement(name = "price")
	protected long price;
	@XmlElement(name = "luna")
	protected long luna;
	@XmlElement(name = "sync_id")
	protected int syncId;
	
	@XmlAttribute(required = true)
	protected int id;
	@XmlAttribute(required = true)
	protected int worldId;
	@XmlAttribute(required = true)
	protected Race race;
	
	/**
	 * Retrieves the cooldown type for this instance.<br>
	 * This value determines how the cooling period is applied.
	 * @return the {@code InstanceCoolTimeType} of the current instance.
	 */
	public InstanceCoolTimeType getCoolTimeType()
	{
		return coolTimeType;
	}
	
	/**
	 * Retrieves the value associated with the instance cool time type.<br>
	 * This method returns the {@code typevalue} string field.
	 * @return The string representation of the type value.
	 */
	public String getTypeValue()
	{
		return typevalue;
	}
	
	/**
	 * Retrieves the instance type associated with this cooltime.<br>
	 * This method returns the {@link InstanceType} value stored in the object.
	 * @return the {@code InstanceType} of the instance
	 */
	public InstanceType getTypeInstance()
	{
		return indun_type;
	}
	
	/**
	 * Retrieves the entity cooldown time.<br>
	 * This value represents the wait time for an instance.
	 * @return the {@code Integer} value of the cooldown time.
	 */
	public Integer getEntCoolTime()
	{
		return entCoolTime;
	}
	
	/**
	 * Retrieves the maximum number of light members allowed.<br>
	 * This value is stored in the {@code maxMemberLight} field.
	 * @return the maximum number of light members as an {@code Integer}.
	 */
	public Integer getMaxMemberLight()
	{
		return maxMemberLight;
	}
	
	/**
	 * Retrieves the maximum number of dark members allowed.<br>
	 * This value is stored in the {@code maxMemberDark} field.
	 * @return the maximum count of dark members as an {@code Integer}.
	 */
	public Integer getMaxMemberDark()
	{
		return maxMemberDark;
	}
	
	/**
	 * Retrieves the minimum level required for Light players to enter. This value is used to restrict access based on character level.
	 * @return the minimum level as an {@code Integer}.
	 */
	public Integer getEnterMinLevelLight()
	{
		return enterMinLevelLight;
	}
	
	/**
	 * Retrieves the maximum level allowed for Light players.<br>
	 * This value is used to restrict entry based on character level.
	 * @return the maximum level as an {@code Integer}.
	 */
	public Integer getEnterMaxLevelLight()
	{
		return enterMaxLevelLight;
	}
	
	/**
	 * Retrieves the minimum level required for dark players to enter. This value is stored in the {@code enterMinLevelDark} field.
	 * @return the minimum level as an {@code Integer}.
	 */
	public Integer getEnterMinLevelDark()
	{
		return enterMinLevelDark;
	}
	
	/**
	 * Retrieves the maximum level allowed for Dark players.<br>
	 * This value is used to restrict entry based on character level.
	 * @return the maximum level as an {@code Integer}.
	 */
	public Integer getEnterMaxLevelDark()
	{
		return enterMaxLevelDark;
	}
	
	/**
	 * Retrieves the score for an alarm unit.<br>
	 * This value is used to determine specific instance requirements.
	 * @return the {@code Integer} score of the alarm unit.
	 */
	public Integer getAlarmUnitScore()
	{
		return alarmUnitScore;
	}
	
	/**
	 * Checks if the instance allows mentor entry.
	 * @return {@code true} if mentors can enter, {@code false} otherwise.
	 */
	public boolean getCanEnterMentor()
	{
		return can_enter_mentor;
	}
	
	/**
	 * Checks if the instance allows guild entry.<br>
	 * This method returns the value of the {@code enter_guild} field.
	 * @return {@code true} if guilds can enter, {@code false} otherwise.
	 */
	public boolean CanEnterLegion()
	{
		return enter_guild;
	}
	
	/**
	 * Retrieves the maximum number of entries allowed.<br>
	 * This value is stored in the {@code max_count} field.
	 * @return the maximum entry count as an {@code Integer}.
	 */
	public Integer getMaxEntriesCount()
	{
		return max_count;
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
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return worldId;
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
	 * Retrieves the unique identifier for the component.<br>
	 * This value is used to distinguish between different components.
	 * @return The {@code int} value of the component.
	 */
	public int getComponent()
	{
		return component;
	}
	
	/**
	 * Retrieves the total number of components.<br>
	 * This method returns the value stored in the {@code componentCount} field.
	 * @return The integer count of components.
	 */
	public int getComponentCount()
	{
		return componentCount;
	}
	
	/**
	 * Retrieves the current price of the item.<br>
	 * This value represents the cost in the game currency.
	 * @return the {@code long} price of the item.
	 */
	public long getPrice()
	{
		return price;
	}
	
	/**
	 * Retrieves the current amount of Luna.<br>
	 * This value is stored in the {@code luna} field.
	 * @return The total number of Luna as a {@code long}.
	 */
	public long getLuna()
	{
		return luna;
	}
	
	/**
	 * Retrieves the unique synchronization identifier.<br>
	 * This value is used to keep data consistent across different systems.
	 * @return The {@code int} value of the synchronization ID.
	 */
	public int getSyncId()
	{
		return syncId;
	}
}
