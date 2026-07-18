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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This service handles geographical data operations for the game world.<br>
 * It provides methods to manage {@link com.aionemu.gameserver.model.gameobjects.VisibleObject} positions and collision checks.<br>
 * Use this class to interact with the global map coordinates and spatial queries.
 * @author ATracer
 */
public class GeoService
{
	private static final Logger log = LoggerFactory.getLogger(GeoService.class);
	private GeoData geoData;
	
	/**
	 * Sets up the geographic data for the game world.<br>
	 * This method determines which {@code GeoData} implementation to use based on the configuration.<br>
	 * It then loads all necessary maps into memory.
	 */
	public void initializeGeo()
	{
		switch (getConfiguredGeoType())
		{
			case GEO_MESHES:
				geoData = new RealGeoData();
				break;
			case NO_GEO:
				geoData = new DummyGeoData();
				break;
		}
		
		log.info("Configured Geo type: " + getConfiguredGeoType());
		geoData.loadGeoMaps();
	}
	
	/**
	 * Updates the open or closed status of a specific door.<br>
	 * This method checks if geo features are enabled before applying changes.
	 * @param worldId The unique identifier for the game world.
	 * @param instanceId The unique identifier for the specific instance.
	 * @param name The unique name of the door to update.
	 * @param isOpened The new state of the door. Use {@code true} for open and {@code false} for closed.
	 */
	public void setDoorState(int worldId, int instanceId, String name, boolean isOpened)
	{
		if (GeoDataConfig.GEO_ENABLE)
		{
			geoData.getMap(worldId).setDoorState(instanceId, name, isOpened);
		}
	}
	
	/**
	 * Calculates the adjusted {@code z} coordinate for an object moving behind another.<br>
	 * This method uses the default height based on whether geo is enabled.
	 * @param worldId The unique identifier of the world.
	 * @param x The current x-coordinate.
	 * @param y The current y-coordinate.
	 * @param z The current z-coordinate.
	 * @param instanceId The unique identifier of the instance.
	 * @return The calculated {@code float} value for the new z-coordinate.
	 */
	public float getZAfterMoveBehind(int worldId, float x, float y, float z, int instanceId)
	{
		if (GeoDataConfig.GEO_ENABLE)
		{
			return getZ(worldId, x, y, z, 0, instanceId);
		}
		
		return getZ(worldId, x, y, z, 0.5f, instanceId);
	}
	
	/**
	 * Retrieves the height value for a specific object.<br>
	 * This method looks up the {@code z} coordinate from the map data.<br>
	 * It uses the coordinates and instance ID provided by the {@link VisibleObject}.
	 * @param object The {@code VisibleObject} to check.
	 * @return The height as a {@code float}.
	 */
	public float getZ(VisibleObject object)
	{
		return geoData.getMap(object.getWorldId()).getZ(object.getX(), object.getY(), object.getZ(), object.getInstanceId());
	}
	
	/**
	 * Retrieves the adjusted height value for a specific location.<br>
	 * This method calculates the {@code z} coordinate based on world data.<br>
	 * It applies an offset if geodata is disabled.
	 * @param worldId The unique identifier for the world map.
	 * @param x The horizontal coordinate.
	 * @param y The vertical coordinate.
	 * @param z The initial height value.
	 * @param defaultUp The height offset to apply when geodata is disabled.
	 * @param instanceId The unique identifier for the specific map instance.
	 * @return The calculated {@code float} height value.
	 */
	public float getZ(int worldId, float x, float y, float z, float defaultUp, int instanceId)
	{
		float newZ = geoData.getMap(worldId).getZ(x, y, z, instanceId);
		if (!GeoDataConfig.GEO_ENABLE)
		{
			newZ += defaultUp;
		} /*
			 * else { newZ += 0.5f; }
			 */
		
		return newZ;
	}
	
	/**
	 * Retrieves the height value for a specific location.<br>
	 * This method looks up the {@code z} coordinate based on the provided map and position.
	 * @param worldId The unique identifier of the world.
	 * @param x The horizontal coordinate.
	 * @param y The vertical coordinate.
	 * @return The height value as a {@code float}.
	 */
	public float getZ(int worldId, float x, float y)
	{
		return geoData.getMap(worldId).getZ(x, y);
	}
	
	/**
	 * Retrieves the name of a specific door based on location and mesh type.<br>
	 * This method checks if doors are enabled in {@code GeoDataConfig}.<br>
	 * It finds the closest matching door for the given coordinates.
	 * @param worldId The unique identifier for the world.
	 * @param meshFile The name of the mesh file to match.
	 * @param x The X coordinate of the point.
	 * @param y The Y coordinate of the point.
	 * @param z The Z coordinate of the point.
	 * @return The {@code String} name of the door, or {@code null} if no door is found.
	 */
	public String getDoorName(int worldId, String meshFile, float x, float y, float z)
	{
		return geoData.getMap(worldId).getDoorName(worldId, meshFile, x, y, z);
	}
	
	/**
	 * Checks for collisions at a specific location.<br>
	 * This method determines if an object can move to the given coordinates.<br>
	 * It uses the {@code VisibleObject} data and movement intentions to calculate results.
	 * @param object The {@link VisibleObject} performing the movement.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param z The target Z coordinate.
	 * @param changeDirection Set to {@code true} if the object should flip its direction on hit.
	 * @param intentions The movement intent flags for the calculation.
	 * @return A {@link CollisionResults} object containing the collision data.
	 */
	public CollisionResults getCollisions(VisibleObject object, float x, float y, float z, boolean changeDirection, byte intentions)
	{
		return geoData.getMap(object.getWorldId()).getCollisions(object.getX(), object.getY(), object.getZ(), x, y, z, changeDirection, false, object.getInstanceId(), intentions);
	}
	
	/**
	 * Checks if a {@link VisibleObject} can see another {@link VisibleObject}.<br>
	 * This method uses the geo data to determine line of sight.<br>
	 * It returns {@code true} if visibility is not blocked or if the feature is disabled.
	 * @param object The object performing the check.
	 * @param target The object being observed.
	 * @return {@code true} if the object can see the target, {@code false} otherwise.
	 */
	public boolean canSee(VisibleObject object, VisibleObject target)
	{
		if (!GeoDataConfig.CANSEE_ENABLE)
		{
			return true;
		}
		
		final float limit = (float) (MathUtil.getDistance(object, target) - target.getObjectTemplate().getBoundRadius().getCollision());
		if (limit <= 0)
		{
			return true;
		}
		
		return geoData.getMap(object.getWorldId()).canSee(object.getX(), object.getY(), object.getZ() + (object.getObjectTemplate().getBoundRadius().getUpper() / 2), target.getX(), target.getY(), target.getZ() + (target.getObjectTemplate().getBoundRadius().getUpper() / 2), limit, object.getInstanceId());
	}
	
	/**
	 * Checks if a point is visible from another point in the game world.<br>
	 * This method uses the {@code geoData} to perform visibility calculations.<br>
	 * It returns {@code true} if the path is clear and {@code false} otherwise.
	 * @param worldId The unique identifier for the map.
	 * @param x The X coordinate of the starting point.
	 * @param y The Y coordinate of the starting point.
	 * @param z The Z coordinate of the starting point.
	 * @param x1 The X coordinate of the target point.
	 * @param y1 The Y coordinate of the target point.
	 * @param z1 The Z coordinate of the target point.
	 * @param limit The maximum distance for visibility checking.
	 * @param instanceId The unique identifier for the map instance.
	 * @return {@code true} if the target is visible, {@code false} otherwise.
	 */
	public boolean canSee(int worldId, float x, float y, float z, float x1, float y1, float z1, float limit, int instanceId)
	{
		return geoData.getMap(worldId).canSee(x, y, z, x1, y1, z1, limit, instanceId);
	}
	
	/**
	 * Checks if the geo system is currently enabled.<br>
	 * This value is retrieved from {@link GeoDataConfig}.
	 * @return {@code true} if geo is enabled, {@code false} otherwise.
	 */
	public boolean isGeoOn()
	{
		return GeoDataConfig.GEO_ENABLE;
	}
	
	/**
	 * Finds the nearest collision point for a specific creature.<br>
	 * This method checks coordinates to determine where an object hits geometry.<br>
	 * It uses the {@link Creature} state and provided coordinates to calculate the result.
	 * @param object The {@code Creature} performing the movement.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param z The target Z coordinate.
	 * @param changeDirection Whether to flip the direction upon hitting a collision.
	 * @param intentions The current movement intentions of the creature.
	 * @return A {@code Vector3f} representing the closest collision point.
	 */
	public Vector3f getClosestCollision(Creature object, float x, float y, float z, boolean changeDirection, byte intentions)
	{
		return geoData.getMap(object.getWorldId()).getClosestCollision(object.getX(), object.getY(), object.getZ(), x, y, z, changeDirection, object.isInFlyingState(), object.getInstanceId(), intentions);
	}
	
	/**
	 * Retrieves the current geographic type based on the system configuration.<br>
	 * It checks if {@code GeoDataConfig.GEO_ENABLE} is set to {@code true}.
	 * @return The active {@link GeoType} for the server.
	 */
	public GeoType getConfiguredGeoType()
	{
		if (GeoDataConfig.GEO_ENABLE)
		{
			return GeoType.GEO_MESHES;
		}
		
		return GeoType.NO_GEO;
	}
	
	/**
	 * Provides access to the singleton instance of {@link GeoService}.<br>
	 * Use this method to get the global service for geographic data.
	 * @return The single instance of {@code GeoService}.
	 */
	public static GeoService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static final class SingletonHolder
	{
		protected static final GeoService instance = new GeoService();
	}
}
