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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.spawns.Spawn;
import com.aionemu.gameserver.model.templates.towns.TownLevel;
import com.aionemu.gameserver.model.templates.towns.TownSpawn;
import com.aionemu.gameserver.model.templates.towns.TownSpawnMap;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for town spawn points within the game world.<br>
 * It serves as a container to manage {@link TownSpawn} objects and their associated maps.
 * @author ViAl
 */
@XmlRootElement(name = "town_spawns_data")
public class TownSpawnsData
{
	@XmlElement(name = "spawn_map")
	private List<TownSpawnMap> spawnMap;
	private final TIntObjectHashMap<TownSpawnMap> spawnMapsData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code spawnMapsData} map using the list of {@link TownSpawnMap} objects.<br>
	 * The {@code spawnMap} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		spawnMapsData.clear();
		
		for (TownSpawnMap map : spawnMap)
		{
			spawnMapsData.put(map.getMapId(), map);
		}
		
		spawnMap.clear();
		spawnMap = null;
	}
	
	/**
	 * Calculates the total number of spawns across all towns.<br>
	 * This method iterates through every {@link TownSpawnMap} in the data.<br>
	 * It sums up the size of the spawn lists for every town level.
	 * @return The total count of all spawns as an {@code int}.
	 */
	public int getSpawnsCount()
	{
		int counter = 0;
		for (TownSpawnMap spawnMap : spawnMapsData.valueCollection())
		{
			for (TownSpawn townSpawn : spawnMap.getTownSpawns())
			{
				for (TownLevel townLevel : townSpawn.getTownLevels())
				{
					counter += townLevel.getSpawns().size();
				}
			}
		}
		
		return counter;
	}
	
	/**
	 * Retrieves a list of {@link Spawn} objects for a specific town and level.<br>
	 * This method searches through the available {@code spawnMapsData}.<br>
	 * It returns {@code null} if no matching data is found.
	 * @param townId The unique identifier for the town.
	 * @param townLevel The level of the town to filter by.
	 * @return A {@code List} of {@link Spawn} objects or {@code null}.
	 */
	public List<Spawn> getSpawns(int townId, int townLevel)
	{
		for (TownSpawnMap spawnMap : spawnMapsData.valueCollection())
		{
			if (spawnMap.getTownSpawn(townId) != null)
			{
				final TownSpawn townSpawn = spawnMap.getTownSpawn(townId);
				return townSpawn.getSpawnsForLevel(townLevel).getSpawns();
			}
		}
		
		return null;
	}
	
	/**
	 * Finds the world ID associated with a specific town.<br>
	 * It searches through all available {@link TownSpawnMap} objects.<br>
	 * Returns 0 if no matching town is found.
	 * @param townId The unique identifier of the town to look up.
	 * @return The integer ID of the world map where the town is located.
	 */
	public int getWorldIdForTown(int townId)
	{
		for (TownSpawnMap spawnMap : spawnMapsData.valueCollection())
		{
			if (spawnMap.getTownSpawn(townId) != null)
			{
				return spawnMap.getMapId();
			}
		}
		
		return 0;
	}
}
