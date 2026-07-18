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
package com.aionemu.gameserver.model.templates.world;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a single weather configuration entry within the game world.<br>
 * This class holds data used to define environmental conditions like rain or snow.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeatherEntry")
public class WeatherEntry
{
	/**
	 * Creates a new instance of the {@link WeatherEntry} class.<br>
	 * This constructor initializes a default weather entry object.
	 */
	public WeatherEntry()
	{
	}
	
	/**
	 * Creates a new {@link WeatherEntry} instance.<br>
	 * This constructor sets the initial values for the zone and weather code.
	 * @param zoneId The unique identifier for the world zone.
	 * @param weatherCode The specific numerical code representing the weather type.
	 */
	public WeatherEntry(int zoneId, int weatherCode)
	{
		this.weatherCode = weatherCode;
		this.zoneId = zoneId;
	}
	
	@XmlAttribute(name = "zone_id", required = true)
	private int zoneId;
	@XmlAttribute(name = "code", required = true)
	private int weatherCode;
	@XmlAttribute(name = "rank", required = true)
	private int rank;
	@XmlAttribute(name = "name")
	private String weatherName;
	@XmlAttribute(name = "before")
	private Boolean isBefore;
	@XmlAttribute(name = "after")
	private Boolean isAfter;
	
	/**
	 * Retrieves the unique identifier for the world zone.<br>
	 * This value corresponds to the {@code zoneId} field.
	 * @return The integer ID of the zone.
	 */
	public int getZoneId()
	{
		return zoneId;
	}
	
	/**
	 * Retrieves the unique identifier for the weather type.<br>
	 * This value corresponds to the {@code weatherCode} assigned to this entry.
	 * @return The integer code representing the weather.
	 */
	public int getCode()
	{
		return weatherCode;
	}
	
	/**
	 * Retrieves the current rank of the {@code MCEntry}.<br>
	 * This value is used to determine the item's standing.
	 * @return The integer value representing the rank.
	 */
	public int getRank()
	{
		return rank;
	}
	
	/**
	 * Checks if this weather entry occurs before another point.<br>
	 * Returns {@code true} if the condition is met.<br>
	 * Returns {@code false} if the value is {@code null}.
	 * @return The result of the before check as a {@code Boolean}.
	 */
	public Boolean isBefore()
	{
		if (isBefore == null)
		{
			return false;
		}
		
		return isBefore;
	}
	
	/**
	 * Checks if this weather entry occurs after a specific point.<br>
	 * Returns {@code true} if the condition is met.<br>
	 * Returns {@code false} if the value is {@code null}.
	 * @return The result of the after check as a {@code Boolean}.
	 */
	public Boolean isAfter()
	{
		if (isAfter == null)
		{
			return false;
		}
		
		return isAfter;
	}
	
	/**
	 * Retrieves the name of the current weather.<br>
	 * This method returns the {@code weatherName} string.
	 * @return The name of the weather as a {@code String}.
	 */
	public String getWeatherName()
	{
		return weatherName;
	}
}
