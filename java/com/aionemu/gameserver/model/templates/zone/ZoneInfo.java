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
package com.aionemu.gameserver.model.templates.zone;

import com.aionemu.gameserver.model.geometry.Area;

/**
 * This class holds the configuration and metadata for a specific game zone.<br>
 * It provides information about the {@link Area} and other properties associated with a map region.
 * @author MrPoke
 */
public class ZoneInfo
{
	private final Area area;
	private final ZoneTemplate zoneTemplate;
	
	/**
	 * Creates a new {@link ZoneInfo} instance.<br>
	 * This constructor links an {@code Area} with its corresponding {@link ZoneTemplate}.
	 * @param area The physical boundaries of the zone.
	 * @param zoneTemplate The configuration data for the zone.
	 */
	public ZoneInfo(Area area, ZoneTemplate zoneTemplate)
	{
		this.area = area;
		this.zoneTemplate = zoneTemplate;
	}
	
	/**
	 * Retrieves the {@code Area} associated with this zone.<br>
	 * This method returns the geometry data for the current zone.
	 * @return The {@link Area} object representing the zone boundaries.
	 */
	public Area getArea()
	{
		return area;
	}
	
	/**
	 * Retrieves the {@code ZoneTemplate} associated with this zone.<br>
	 * This method returns the template data for the current area.
	 * @return The {@link ZoneTemplate} object.
	 */
	public ZoneTemplate getZoneTemplate()
	{
		return zoneTemplate;
	}
}
