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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.windstreams.WindstreamTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link WindstreamTemplate} information.<br>
 * It is used to manage and store the collection of windstreams within the game server.
 * @author LokiReborn
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "windstreams")
public class WindstreamData
{
	@XmlElement(name = "windstream")
	private List<WindstreamTemplate> wts;
	private TIntObjectHashMap<WindstreamTemplate> windstreams;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code windstreams} map using the list of {@link WindstreamTemplate} templates.<br>
	 * The {@code wts} list is set to {@code null} after the map is built.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		windstreams = new TIntObjectHashMap<>();
		for (WindstreamTemplate wt : wts)
		{
			windstreams.put(wt.getMapid(), wt);
		}
		
		wts = null;
	}
	
	/**
	 * Retrieves a {@link WindstreamTemplate} based on the provided map identifier.<br>
	 * This method looks up the template in the internal data map.
	 * @param mapId The unique ID of the map to search for.
	 * @return The {@code WindstreamTemplate} associated with the given {@code mapId}, or {@code null} if not found.
	 */
	public WindstreamTemplate getStreamTemplate(int mapId)
	{
		return windstreams.get(mapId);
	}
	
	/**
	 * Returns the total number of windstreams.<br>
	 * This method calls {@code size} to get the count.
	 * @return The number of items currently stored in the collection.
	 */
	public int size()
	{
		return windstreams.size();
	}
}
