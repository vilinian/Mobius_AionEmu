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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;

/**
 * Represents the spatial coordinates of an object within the game world.<br>
 * This class stores and manages {@code x}, {@code y}, and {@code z} values for positioning.
 * @author -Nemesiss-
 */
public class WorldPosition
{
	/**
	 * Creates a new {@link WorldPosition} object.<br>
	 * This constructor initializes the position using a specific map identifier.
	 * @param mapId The unique ID of the map to associate with this position.
	 */
	public WorldPosition(int mapId)
	{
		this.mapId = mapId;
	}
	
	/**
	 * Creates a new {@link WorldPosition} with specific coordinates.<br>
	 * This constructor sets the map, position, and heading values.
	 * @param mapId The unique identifier for the map.
	 * @param x The horizontal coordinate on the X axis.
	 * @param y The vertical coordinate on the Y axis.
	 * @param z The depth coordinate on the Z axis.
	 * @param h The heading value of the object.
	 */
	public WorldPosition(int mapId, float x, float y, float z, byte h)
	{
		this.mapId = mapId;
		this.x = x;
		this.y = y;
		this.z = z;
		heading = h;
	}
	
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(WorldPosition.class);
	/**
	 * Map id.
	 */
	private int mapId;
	/**
	 * Map Region.
	 */
	private MapRegion mapRegion;
	/**
	 * World position x
	 */
	private float x;
	/**
	 * World position y
	 */
	private float y;
	/**
	 * World position z
	 */
	private float z;
	/**
	 * Value from 0 to 120 (120==0 actually)
	 */
	private byte heading;
	/**
	 * indicating if object is spawned or not.
	 */
	private boolean isSpawned = false;
	
	/**
	 * Gets the unique identifier for the map.<br>
	 * This method returns the {@code int} value of the current map ID.
	 * @return The map ID as an {@code int}.
	 */
	public int getMapId()
	{
		if (mapId == 0)
		{
			log.warn("WorldPosition has (mapId == 0) " + toString());
		}
		
		return mapId;
	}
	
	/**
	 * Sets the unique identifier for the map.<br>
	 * This updates the {@code mapId} field of this {@link WorldPosition} instance.
	 * @param mapId The new ID to assign to the map.
	 */
	public void setMapId(int mapId)
	{
		this.mapId = mapId;
	}
	
	/**
	 * Retrieves the X coordinate of the bookmark.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return y;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return z;
	}
	
	/**
	 * Retrieves the current {@link MapRegion} for this position.<br>
	 * This method returns {@code null} if the object has not been spawned.
	 * @return The {@code MapRegion} associated with this position, or {@code null}.
	 */
	public MapRegion getMapRegion()
	{
		return isSpawned ? mapRegion : null;
	}
	
	/**
	 * Gets the unique identifier of the instance.<br>
	 * This value is retrieved from the parent of the current {@link MapRegion}.
	 * @return The {@code int} ID of the instance.
	 */
	public int getInstanceId()
	{
		return mapRegion.getParent().getInstanceId();
	}
	
	/**
	 * Returns the total number of instances for this map.<br>
	 * This count includes the base twins and beginner twins defined in the {@link WorldMapTemplate}.
	 * @return The total instance count as an {@code int}.
	 */
	public int getInstanceCount()
	{
		return mapRegion.getParent().getParent().getInstanceCount();
	}
	
	/**
	 * Checks if the current position belongs to an instance map.<br>
	 * This method looks up the parent region properties.
	 * @return {@code true} if it is an instance map, {@code false} otherwise.
	 */
	public boolean isInstanceMap()
	{
		return mapRegion.getParent().getParent().isInstanceType();
	}
	
	/**
	 * Checks if the current map region is active.<br>
	 * This method considers the {@code WorldConfig.WORLD_ACTIVE_TRACE} setting.<br>
	 * It returns {@code true} if the trace is disabled or if the region is active.
	 * @return {@code true} if the region is active, {@code false} otherwise.
	 */
	public boolean isMapRegionActive()
	{
		return mapRegion.isMapRegionActive();
	}
	
	/**
	 * Retrieves the current rotation direction of the object.<br>
	 * This value is stored as a {@code byte}.
	 * @return The heading value of the object.
	 */
	public byte getHeading()
	{
		return heading;
	}
	
	/**
	 * Retrieves the {@link World} associated with this position.<br>
	 * This method gets the world by accessing the underlying {@link MapRegion}.
	 * @return The {@code World} object.
	 */
	public World getWorld()
	{
		return mapRegion.getWorld();
	}
	
	/**
	 * Retrieves the {@link WorldMapInstance} associated with this position.<br>
	 * This method looks up the parent instance from the current {@code MapRegion}.
	 * @return The {@code WorldMapInstance} for this location, or {@code null} if none exists.
	 */
	public WorldMapInstance getWorldMapInstance()
	{
		return mapRegion.getParent();
	}
	
	/**
	 * Checks if the current position of this object is a valid spawn point.<br>
	 * This method delegates the check to the {@code isSpawned} method.
	 * @return {@code true} if the position is spawned, {@code false} otherwise.
	 */
	public boolean isSpawned()
	{
		return isSpawned;
	}
	
	/**
	 * Updates the spawn status of this position.<br>
	 * Sets the {@code isSpawned} field to the provided value.
	 * @param val The new spawn state to set.
	 */
	void setIsSpawned(boolean val)
	{
		isSpawned = val;
	}
	
	/**
	 * Sets the {@code MapRegion} for this position.<br>
	 * This updates the internal {@code mapRegion} field.
	 * @param r The new {@link MapRegion} to assign.
	 */
	void setMapRegion(MapRegion r)
	{
		mapRegion = r;
	}
	
	/**
	 * Updates the spatial coordinates and heading of the object.<br>
	 * This method only updates values that are not {@code null}.
	 * @param newX The new {@code Float} value for the X coordinate.
	 * @param newY The new {@code Float} value for the Y coordinate.
	 * @param newZ The new {@code Float} value for the Z coordinate.
	 * @param newHeading The new {@code Byte} value for the heading.
	 */
	public void setXYZH(Float newX, Float newY, Float newZ, Byte newHeading)
	{
		if (newX != null)
		{
			x = newX;
		}
		
		if (newY != null)
		{
			y = newY;
		}
		
		if (newZ != null)
		{
			z = newZ;
		}
		
		if (newHeading != null)
		{
			heading = newHeading;
		}
	}
	
	/**
	 * Sets the vertical coordinate of this point.<br>
	 * This updates the {@code z} value to the provided amount.
	 * @param z The new {@code float} value for the vertical position.
	 */
	public void setZ(float z)
	{
		this.z = z;
	}
	
	/**
	 * Sets the heading of the position.<br>
	 * This updates the internal {@code heading} field.
	 * @param h The new heading value to set.
	 */
	public void setH(byte h)
	{
		heading = h;
	}
	
	/**
	 * Compares this object with another object for equality.<br>
	 * It checks if both objects are of type {@code WorldPosition}.<br>
	 * Two positions are equal if they have the same map ID, coordinates, and heading.
	 * @param obj The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof WorldPosition))
		{
			return false;
		}
		
		final WorldPosition other = (WorldPosition) obj;
		return (mapId == other.mapId) && (x == other.x) && (y == other.y) && (z == other.z) && (heading == other.heading);
	}
	
	/**
	 * Returns a string representation of the {@code WorldPosition}.<br>
	 * This includes coordinates, heading, and spawn status.
	 * @return A formatted string describing this position.
	 */
	@Override
	public String toString()
	{
		return "WorldPosition [heading=" + heading + ", isSpawned=" + isSpawned + ", mapRegion=" + mapRegion + ", x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
