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
package com.aionemu.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.world.WeatherTable;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the weather data for a specific map.<br>
 * It stores information retrieved from the {@link WeatherTable} to manage environmental effects.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"weatherData"
})
@XmlRootElement(name = "weather")
public class MapWeatherData
{
	@XmlElement(name = "map", required = true)
	private List<WeatherTable> weatherData;
	@XmlTransient
	private TIntObjectHashMap<WeatherTable> mapWeather;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code mapWeather} map using the list of {@link WeatherTable} objects.<br>
	 * The {@code weatherData} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		mapWeather = new TIntObjectHashMap<>();
		
		for (WeatherTable table : weatherData)
		{
			mapWeather.put(table.getMapId(), table);
		}
		
		weatherData.clear();
		weatherData = null;
	}
	
	/**
	 * Retrieves the weather data for a specific map.<br>
	 * This method looks up the {@code WeatherTable} using the provided ID.
	 * @param mapId The unique identifier of the map to look up.
	 * @return The {@link WeatherTable} associated with the given map ID, or {@code null} if not found.
	 */
	public WeatherTable getWeather(int mapId)
	{
		return mapWeather.get(mapId);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return mapWeather.size();
	}
}
