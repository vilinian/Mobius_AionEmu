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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;

/**
 * This class manages the configuration data for world weather effects.<br>
 * It defines how different weather types are handled within the game environment.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeatherTable", propOrder =
{
	"zoneData"
})
public class WeatherTable
{
	@XmlElement(name = "table", required = true)
	protected List<WeatherEntry> zoneData;
	@XmlAttribute(name = "weather_count", required = true)
	protected int weatherCount;
	@XmlAttribute(name = "zone_count", required = true)
	protected int zoneCount;
	@XmlAttribute(name = "id", required = true)
	protected int mapId;
	
	/**
	 * Retrieves the list of weather entries for this table.<br>
	 * This method returns all data stored in the {@code zoneData} field.
	 * @return a {@code List} of {@link WeatherEntry} objects.
	 */
	public List<WeatherEntry> getZoneData()
	{
		return zoneData;
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * This value is assigned during the creation of the {@link AssembledNpc}.
	 * @return The {@code int} representing the map ID.
	 */
	public int getMapId()
	{
		return mapId;
	}
	
	/**
	 * Retrieves the total number of zones.<br>
	 * This value is stored in the {@code zoneCount} field.
	 * @return The total count of zones as an {@code int}.
	 */
	public int getZoneCount()
	{
		return zoneCount;
	}
	
	/**
	 * Retrieves the total number of weather entries.<br>
	 * This value is stored in the {@code weatherCount} field.
	 * @return The integer count of weather records.
	 */
	public int getWeatherCount()
	{
		return weatherCount;
	}
	
	/**
	 * Finds the next weather state for a specific zone.<br>
	 * It looks through the {@code getZoneData} list to find a matching entry.<br>
	 * This method returns {@code null} if no following weather is found.
	 * @param entry The current {@code WeatherEntry} to check.
	 * @return The next {@code WeatherEntry} or {@code null}.
	 */
	public WeatherEntry getWeatherAfter(WeatherEntry entry)
	{
		if ((entry.getWeatherName() == null) || entry.isAfter())
		{
			return null;
		}
		
		for (WeatherEntry we : getZoneData())
		{
			if (we.getZoneId() != entry.getZoneId())
			{
				continue;
			}
			
			if (entry.getWeatherName().equals(we.getWeatherName()))
			{
				if (entry.isBefore() && !we.isBefore() && !we.isAfter())
				{
					return we;
				}
				else if (!entry.isBefore() && !entry.isAfter() && we.isAfter())
				{
					return we;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a list of weather entries for a specific zone.<br>
	 * This method filters the data based on the provided {@code zoneId}.
	 * @param zoneId The unique identifier of the zone to search for.
	 * @return A {@code List} of {@link WeatherEntry} objects matching the ID.
	 */
	public List<WeatherEntry> getWeathersForZone(int zoneId)
	{
		final List<WeatherEntry> result = new ArrayList<>();
		for (WeatherEntry entry : getZoneData())
		{
			if (entry.getZoneId() == zoneId)
			{
				result.add(entry);
			}
		}
		
		return result;
	}
}
