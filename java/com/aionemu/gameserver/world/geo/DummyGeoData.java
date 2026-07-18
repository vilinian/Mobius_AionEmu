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
package com.aionemu.gameserver.world.geo;

import com.aionemu.gameserver.geoEngine.models.GeoMap;

/**
 * This class provides a placeholder implementation of the {@link GeoData} interface.<br>
 * It is used for testing or development purposes when real map data is unavailable.
 * @author ATracer
 */
public class DummyGeoData implements GeoData
{
	public static final DummyGeoMap DUMMY_MAP = new DummyGeoMap("", 0);
	
	/**
	 * Loads the geographical map data into memory.<br>
	 * This method initializes the {@code DummyGeoMap} objects.<br>
	 * It prepares the maps for use by {@code getMap}.
	 */
	@Override
	public void loadGeoMaps()
	{
	}
	
	/**
	 * Retrieves the map associated with a specific world ID.<br>
	 * This method returns the {@link DummyGeoMap} instance.
	 * @param worldId The unique identifier for the world.
	 * @return The {@code GeoMap} object for the given world.
	 */
	@Override
	public GeoMap getMap(int worldId)
	{
		return DUMMY_MAP;
	}
}
