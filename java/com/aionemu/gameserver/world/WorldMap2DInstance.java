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
 * This class represents a 2D instance of a world map.<br>
 * It extends {@link WorldMapInstance} to handle specific 2D coordinate logic.
 * @author ATracer
 */
public class WorldMap2DInstance extends WorldMapInstance
{
	private int ownerId;
	
	/**
	 * Creates a new {@link WorldMap2DInstance} for a specific map.<br>
	 * This constructor initializes the instance with a default owner ID of {@code 0}.
	 * @param parent The {@link WorldMap} that this instance belongs to.
	 * @param instanceId The unique identifier for this specific instance.
	 */
	public WorldMap2DInstance(WorldMap parent, int instanceId)
	{
		this(parent, instanceId, 0);
	}
	
	/**
	 * Creates a new {@link WorldMap2DInstance} with a specific owner.<br>
	 * This constructor initializes the instance using the provided parent map and ID.<br>
	 * It also sets the unique identifier for the owner of this instance.
	 * @param parent The {@code WorldMap} that contains this instance.
	 * @param instanceId The unique identification number for this specific instance.
	 * @param ownerId The unique identification number of the player or entity who owns this instance.
	 */
	public WorldMap2DInstance(WorldMap parent, int instanceId, int ownerId)
	{
		super(parent, instanceId);
		this.ownerId = ownerId;
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
		final float startX = RegionUtil.getXFrom2dRegionId(regionId);
		final float startY = RegionUtil.getYFrom2dRegionId(regionId);
		final int size = getParent().getWorldSize();
		final float maxZ = Math.round((float) size / regionSize) * regionSize;
		final ZoneInstance[] zones = filterZones(getMapId(), regionId, startX, startY, 0, maxZ);
		return new MapRegion(regionId, this, zones);
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
		
		// Create all mapRegion
		for (int x = 0; x <= size; x = x + regionSize)
		{
			for (int y = 0; y <= size; y = y + regionSize)
			{
				final int regionId = RegionUtil.get2dRegionId(x, y);
				regions.put(regionId, createMapRegion(regionId));
			}
		}
		
		// Add Neighbour
		for (int x = 0; x <= size; x = x + regionSize)
		{
			for (int y = 0; y <= size; y = y + regionSize)
			{
				final int regionId = RegionUtil.get2dRegionId(x, y);
				final MapRegion mapRegion = regions.get(regionId);
				for (int x2 = x - regionSize; x2 <= (x + regionSize); x2 += regionSize)
				{
					for (int y2 = y - regionSize; y2 <= (y + regionSize); y2 += regionSize)
					{
						if ((x2 == x) && (y2 == y))
						{
							continue;
						}
						
						final int neighbourId = RegionUtil.get2dRegionId(x2, y2);
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
		final int regionId = RegionUtil.get2dRegionId(x, y);
		return regions.get(regionId);
	}
	
	/**
	 * Retrieves the unique identifier of the owner.<br>
	 * This ID belongs to the player who owns this {@link ChallengeTask}.
	 * @return The {@code int} value representing the owner's ID.
	 */
	@Override
	public int getOwnerId()
	{
		return ownerId;
	}
	
	/**
	 * Sets the unique identifier for the owner of this instance.<br>
	 * This updates the {@code ownerId} field within the object.
	 * @param ownerId The new ID to assign to the owner.
	 */
	public void setOwnerId(int ownerId)
	{
		this.ownerId = ownerId;
	}
	
	/**
	 * Checks if this map instance belongs to a specific player.<br>
	 * It returns {@code true} if the {@code ownerId} is not {@code 0}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the map is personal, {@code false} otherwise.
	 */
	@Override
	public boolean isPersonal()
	{
		return ownerId != 0;
	}
}
