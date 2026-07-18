package com.aionemu.gameserver.model.templates.event;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;

import java.time.ZonedDateTime;

import com.aionemu.gameserver.utils.gametime.DateTimeUtil;

/**
 * Represents the data structure for an event window in the game.<br>
 * This class handles the configuration and properties of specific events.<br>
 * It is mapped to XML for easy configuration via external files.
 * @author Ghostfur (Aion-Unique)
 */
@XmlRootElement(name = "atreian_passport")
@XmlAccessorType(value = XmlAccessType.NONE)
public class EventsWindow
{
	@XmlAttribute(name = "id", required = true)
	private int id;
	
	@XmlAttribute(name = "item", required = true)
	private int item;
	
	@XmlAttribute(name = "count", required = true)
	private long count;
	
	@XmlAttribute(name = "period_start", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar pStart;
	
	@XmlAttribute(name = "period_end", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar pEnd;
	
	@XmlAttribute(name = "remaining_time", required = true)
	private int remaining_time;
	
	@XmlAttribute(name = "min_level", required = true)
	private int min_level;
	
	@XmlAttribute(name = "max_level", required = true)
	private int max_level;
	
	@XmlAttribute(name = "dailyMaxCount", required = true)
	private int dailyMaxCount;
	
	private Timestamp lastStamp;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the unique identifier for the item associated with this event.<br>
	 * This value corresponds to the {@code item} attribute in the data model.
	 * @return The {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return item;
	}
	
	/**
	 * Retrieves the current quantity of the item.<br>
	 * This value is updated by the {@code calculateCount} method.
	 * @return The total number of items as a {@code long}.
	 */
	public long getCount()
	{
		return count;
	}
	
	/**
	 * Retrieves the maximum number of items allowed per day.<br>
	 * This value is stored in the {@code dailyMaxCount} field.
	 * @return The maximum count for the current day as an {@code int}.
	 */
	public int getMaxCountOfDay()
	{
		return dailyMaxCount;
	}
	
	/**
	 * Retrieves the start time of the event period.<br>
	 * This method converts the internal {@code XMLGregorianCalendar} to a {@link java.time.ZonedDateTime} object.
	 * @return The {@code ZonedDateTime} representing when the period begins.
	 */
	public ZonedDateTime getPeriodStart()
	{
		return DateTimeUtil.getDateTime(pStart.toGregorianCalendar());
	}
	
	/**
	 * Retrieves the end date and time of the event period.<br>
	 * This method converts the internal {@code XMLGregorianCalendar} to a {@link java.time.ZonedDateTime} object.
	 * @return The {@code ZonedDateTime} representing when the event ends.
	 */
	public ZonedDateTime getPeriodEnd()
	{
		return DateTimeUtil.getDateTime(pEnd.toGregorianCalendar());
	}
	
	/**
	 * Retrieves the current remaining time for the event.<br>
	 * This value is stored in the {@code remaining_time} field.
	 * @return The amount of time left as an {@code int}.
	 */
	public int getRemainingTime()
	{
		return remaining_time;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return min_level;
	}
	
	/**
	 * Retrieves the maximum level allowed for this event.<br>
	 * This value is stored in the {@code max_level} field.
	 * @return The maximum level as an {@code int}.
	 */
	public int getMaxLevel()
	{
		return max_level;
	}
	
	/**
	 * Retrieves the most recent timestamp for this entry.<br>
	 * This value represents when the event was last updated.
	 * @return the {@code Timestamp} of the last update.
	 */
	public Timestamp getLastStamp()
	{
		return lastStamp;
	}
	
	/**
	 * Updates the last recorded {@code Timestamp} for this player.<br>
	 * This value is used to track the most recent activity or update time.
	 * @param timestamp The new {@code Timestamp} to assign to the last stamp.
	 */
	public void setLastStamp(Timestamp timestamp)
	{
		lastStamp = timestamp;
	}
}
