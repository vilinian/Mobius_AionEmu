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
package com.aionemu.gameserver.model.templates.event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import java.time.ZonedDateTime;

import com.aionemu.gameserver.utils.gametime.DateTimeUtil;

/**
 * This class manages the configuration for various server-wide boost events.<br>
 * It defines the timing and properties for temporary gameplay enhancements.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoostEvents")
public class BoostEvents
{
	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "name", required = true)
	protected String name;
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	@XmlAttribute(name = "buff_value", required = true)
	protected int buffValue;
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
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the unique buffer identifier.<br>
	 * This value is stored in the {@code buffId} field.
	 * @return The integer ID of the buff.
	 */
	public int getBuffId()
	{
		return buffId;
	}
	
	/**
	 * Retrieves the current value of the boost effect.<br>
	 * This value corresponds to the {@code buff_value} attribute.
	 * @return The integer value of the buff.
	 */
	public int getBuffValue()
	{
		return buffValue;
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
	
	/**
	 * Checks if the event is currently active.<br>
	 * This method compares the current time against the {@code startDate} and {@code endDate}.
	 * @return {@code true} if the current time is between the start and end dates, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return getStartDate().isBefore(java.time.ZonedDateTime.now()) && getEndDate().isAfter(java.time.ZonedDateTime.now());
	}
	
	/**
	 * Checks if the event has finished.<br>
	 * This method returns {@code true} if the current time is past the end date.<br>
	 * It internally calls the {@code isActive} method to determine the status.
	 * @return {@code true} if the event is no longer active, {@code false} otherwise.
	 */
	public boolean isExpired()
	{
		return !isActive();
	}
}
