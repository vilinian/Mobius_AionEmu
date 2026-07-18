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

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.dataholders.DataManager;

/**
 * Represents the configuration data for a specific zone in the game world.<br>
 * This class extends {@link ZoneTemplate} to provide detailed properties for global map areas.
 * @author Rolandas
 */
public class WorldZoneTemplate extends ZoneTemplate
{
	/**
	 * Creates a new {@link WorldZoneTemplate} instance.<br>
	 * This constructor initializes the zone boundaries based on the provided size.<br>
	 * It also loads specific map data using the {@code mapId}.
	 * @param size The dimension used to calculate the zone boundaries.
	 * @param mapId The unique identifier for the map to load.
	 */
	public WorldZoneTemplate(int size, Integer mapId)
	{
		final float maxZ = Math.round((float) size / WorldConfig.WORLD_REGION_SIZE) * WorldConfig.WORLD_REGION_SIZE;
		points = new Points(-1, maxZ + 1);
		Point2D point = new Point2D();
		point.x = -1;
		point.y = -1;
		points.getPoint().add(point);
		point = new Point2D();
		point.x = -1;
		point.y = size + 1;
		points.getPoint().add(point);
		point = new Point2D();
		point.x = size + 1;
		point.y = size + 1;
		points.getPoint().add(point);
		point = new Point2D();
		point.x = size + 1;
		point.y = -1;
		points.getPoint().add(point);
		zoneType = ZoneClassName.DUMMY;
		mapid = mapId;
		flags = DataManager.WORLD_MAPS_DATA.getTemplate(mapId).getFlags();
		setXmlName(mapId.toString());
	}
}
