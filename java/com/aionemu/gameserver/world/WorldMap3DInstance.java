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
package com.aionemu.gameserver.world;

import com.aionemu.gameserver.model.challenge.ChallengeTask;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * Represents a 3D instance of a world map within the game environment.<br>
 * This class handles the spatial data and logic for specific {@link ZoneInstance} areas.
 * @author ATracer
 */
public class WorldMap3DInstance extends WorldMapInstance
{
	/**
	 * Creates a new 3D instance of a {@link WorldMap}.<br>
	 * This constructor initializes the map with a specific ID.
	 * @param parent The {@code WorldMap} that this instance belongs to.
	 * @param instanceId The unique identifier for this specific instance.
	 */
	public WorldMap3DInstance(WorldMap parent, int instanceId)
	{
		super(parent, instanceId);
	}
	
	/**
	 * Finds the {@code MapRegion} at a specific coordinate.<br>
	 * This method uses the {@code x} and {@code y} values to identify the region.<br>
	 * It returns the corresponding region from the internal map.
	 * @param x The horizontal coordinate.
	 * @param y The vertical coordinate.
	 * @param z The height coordinate.
	 * @return The {@code MapRegion} object at the given location or {@code null}.
	 */
	@Override
	public MapRegion getRegion(float x, float y, float z)
	{
		final int regionId = RegionUtil.get3dRegionId(x, y, z);
		return regions.get(regionId);
	}
	
	/**
	 * Initializes the map regions for this instance.<br>
	 * This method calculates the world size from {@code getParent}.<br>
	 * It populates the region map and links adjacent neighbors.
	 */
	@Override
	protected void initMapRegions()
	{
		final int size = getParent().getWorldSize();
		final float maxZ = Math.round((float) size / regionSize) * regionSize;
		
		// Create all mapRegion
		for (int x = 0; x <= size; x = x + regionSize)
		{
			for (int y = 0; y <= size; y = y + regionSize)
			{
				for (int z = 0; z < maxZ; z = z + regionSize)
				{
					final int regionId = RegionUtil.get3dRegionId(x, y, z);
					regions.put(regionId, createMapRegion(regionId));
				}
			}
		}
		
		// Add Neighbour
		for (int x = 0; x <= size; x = x + regionSize)
		{
			for (int y = 0; y <= size; y = y + regionSize)
			{
				for (int z = 0; z < maxZ; z = z + regionSize)
				{
					final int regionId = RegionUtil.get3dRegionId(x, y, z);
					final MapRegion mapRegion = regions.get(regionId);
					for (int x2 = x - regionSize; x2 <= (x + regionSize); x2 += regionSize)
					{
						for (int y2 = y - regionSize; y2 <= (y + regionSize); y2 += regionSize)
						{
							for (int z2 = z - regionSize; z2 < (z + regionSize); z2 += regionSize)
							{
								if ((x2 == x) && (y2 == y) && (z2 == z))
								{
									continue;
								}
								
								final int neighbourId = RegionUtil.get3dRegionId(x2, y2, z2);
								final MapRegion neighbour = regions.get(neighbourId);
								if (neighbour != null)
								{
									mapRegion.addNeighbourRegion(neighbour);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Creates a new {@link MapRegion} object based on a specific ID.<br>
	 * This method calculates the coordinates and retrieves the associated zones.<br>
	 * It is used internally to initialize map sections.
	 * @param regionId The unique identifier for the region to create.
	 * @return A new {@code MapRegion} instance.
	 */
	@Override
	protected MapRegion createMapRegion(int regionId)
	{
		final float startX = RegionUtil.getXFrom3dRegionId(regionId);
		final float startY = RegionUtil.getYFrom3dRegionId(regionId);
		final float startZ = RegionUtil.getZFrom3dRegionId(regionId);
		final ZoneInstance[] zones = filterZones(getMapId(), regionId, startX, startY, startZ, startZ + regionSize);
		return new MapRegion(regionId, this, zones);
	}
	
	/**
	 * Checks if this map instance is private to a single player.<br>
	 * This method currently always returns {@code false}.
	 * @return {@code true} if the map is personal, {@code false} otherwise.
	 */
	@Override
	public boolean isPersonal()
	{
		return false;
	}
	
	/**
	 * Retrieves the unique identifier of the owner.<br>
	 * This ID belongs to the player who owns this {@link ChallengeTask}.
	 * @return The {@code int} value representing the owner's ID.
	 */
	@Override
	public int getOwnerId()
	{
		return 0;
	}
}
