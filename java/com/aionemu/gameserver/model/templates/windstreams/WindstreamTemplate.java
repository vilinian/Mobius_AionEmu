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
package com.aionemu.gameserver.model.templates.windstreams;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class serves as a data template for {@link com.aionemu.gameserver.model.templates.windstreams.WindstreamTemplate} objects.<br>
 * It defines the properties and configuration for wind streams within the game world.
 * @author LokiReborn
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WindFlight")
public class WindstreamTemplate
{
	@XmlElement(required = true)
	protected StreamLocations locations;
	@XmlAttribute
	protected int mapid;
	
	/**
	 * Retrieves the {@code StreamLocations} for this template.<br>
	 * This method returns the current location data stored in the object.
	 * @return The {@link StreamLocations} object containing the stream coordinates.
	 */
	public StreamLocations getLocations()
	{
		return locations;
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * Returns {@code 0} if no map is assigned.
	 * @return The {@code int} ID of the map.
	 */
	public int getMapid()
	{
		return mapid;
	}
}
