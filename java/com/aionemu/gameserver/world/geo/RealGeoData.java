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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.geoEngine.GeoWorldLoader;
import com.aionemu.gameserver.geoEngine.models.GeoMap;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.utils.Util;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class manages the actual geographical data for the game world.<br>
 * It provides access to {@link GeoMap} objects and handles spatial information used by the engine.
 * @author ATracer
 */
public class RealGeoData implements GeoData
{
	private static final Logger log = LoggerFactory.getLogger(RealGeoData.class);
	private final TIntObjectHashMap<GeoMap> geoMaps = new TIntObjectHashMap<>();
	
	/**
	 * Loads the geographical map data into memory.<br>
	 * This method initializes the {@code DummyGeoMap} objects.<br>
	 * It prepares the maps for use by {@code getMap}.
	 */
	@Override
	public void loadGeoMaps()
	{
		Map<String, Spatial> models = loadMeshes();
		loadWorldMaps(models);
		models.clear();
		models = null;
		log.info("Geodata: " + geoMaps.size() + " geo maps loaded!");
	}
	
	/**
	 * This method loads the world map data into the system.<br>
	 * It iterates through all available templates in {@link DataManager}.<br>
	 * If a map fails to load, it uses a dummy implementation instead.
	 * @param models A {@code Map} containing the {@code Spatial} objects used for loading maps.
	 */
	protected void loadWorldMaps(Map<String, Spatial> models)
	{
		log.info("Loading geo maps..");
		Util.printProgressBarHeader(DataManager.WORLD_MAPS_DATA.size());
		final List<Integer> mapsWithErrors = new ArrayList<>();
		
		for (WorldMapTemplate map : DataManager.WORLD_MAPS_DATA)
		{
			final GeoMap geoMap = new GeoMap(Integer.toString(map.getMapId()), map.getWorldSize());
			try
			{
				if (GeoWorldLoader.loadWorld(map.getMapId(), models, geoMap))
				{
					geoMaps.put(map.getMapId(), geoMap);
				}
			}
			catch (Throwable t)
			{
				mapsWithErrors.add(map.getMapId());
				geoMaps.put(map.getMapId(), DummyGeoData.DUMMY_MAP);
			}
			
			Util.printCurrentProgress();
		}
		
		Util.printEndProgress();
		
		if (mapsWithErrors.size() > 0)
		{
			log.warn("Some maps were not loaded correctly and reverted to dummy implementation: ");
			log.warn(mapsWithErrors.toString());
		}
	}
	
	/**
	 * Loads the 3D mesh data from the configuration file.<br>
	 * This method calls {@code loadMeshs} to retrieve the models.
	 * @return A {@code Map<String, Spatial>} containing the loaded meshes.
	 */
	protected Map<String, Spatial> loadMeshes()
	{
		log.info("Loading meshes..");
		Map<String, Spatial> models = null;
		try
		{
			models = GeoWorldLoader.loadMeshs("data/geo/meshs.geo");
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Problem loading meshes", e);
		}
		
		return models;
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
		final GeoMap geoMap = geoMaps.get(worldId);
		return geoMap != null ? geoMap : DummyGeoData.DUMMY_MAP;
	}
}
