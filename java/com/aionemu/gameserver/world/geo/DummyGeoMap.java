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

import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.models.GeoMap;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.geoEngine.scene.mesh.DoorGeometry;

/**
 * This class provides a simplified implementation of {@link GeoMap} for testing purposes.<br>
 * It serves as a placeholder to simulate world geometry without loading actual map data.
 * @author ATracer
 */
public class DummyGeoMap extends GeoMap
{
	/**
	 * Creates a new {@link DummyGeoMap} instance.<br>
	 * This constructor initializes the map with a specific name and size.<br>
	 * It calls the superclass constructor to set up the base properties.
	 * @param name The unique identifier for this map.
	 * @param worldSize The total dimensions of the world area.
	 */
	public DummyGeoMap(String name, int worldSize)
	{
		super(name, worldSize);
	}
	
	/**
	 * Retrieves the {@code z} coordinate for a specific location.<br>
	 * This method returns the vertical value provided in the input.
	 * @param x The horizontal {@code x} coordinate.
	 * @param y The horizontal {@code y} coordinate.
	 * @param z The initial vertical {@code z} coordinate.
	 * @param instanceId The unique identifier for the current instance.
	 * @return The provided {@code z} value as a {@code float}.
	 */
	@Override
	public float getZ(float x, float y, float z, int instanceId)
	{
		return z;
	}
	
	/**
	 * Checks if there is a clear line of sight between two points.<br>
	 * It verifies that no obstacles block the path within a specific distance.
	 * @param x The starting X coordinate.
	 * @param y The starting Y coordinate.
	 * @param z The starting Z coordinate.
	 * @param targetX The destination X coordinate.
	 * @param targetY The destination Y coordinate.
	 * @param targetZ The destination Z coordinate.
	 * @param limit The maximum distance for the visibility check.
	 * @param instanceId The unique identifier for the current instance.
	 * @return {@code true} if the path is clear, {@code false} otherwise.
	 */
	@Override
	public boolean canSee(float x, float y, float z, float targetX, float targetY, float targetZ, float limit, int instanceId)
	{
		return true;
	}
	
	/**
	 * Finds the nearest collision point between two locations.<br>
	 * This method calculates a {@code Vector3f} based on the provided coordinates.<br>
	 * It considers movement rules like flying and direction changes.
	 * @param x The starting X coordinate.
	 * @param y The starting Y coordinate.
	 * @param z The starting Z coordinate.
	 * @param targetX The destination X coordinate.
	 * @param targetY The destination Y coordinate.
	 * @param targetZ The destination Z coordinate.
	 * @param changeDirction Whether the entity can change its direction.
	 * @param fly Whether the entity is currently flying.
	 * @param instanceId The unique ID of the current instance.
	 * @param intentions The current movement intentions of the entity.
	 * @return A {@code Vector3f} representing the closest collision point.
	 */
	@Override
	public Vector3f getClosestCollision(float x, float y, float z, float targetX, float targetY, float targetZ, boolean changeDirction, boolean fly, int instanceId, byte intentions)
	{
		return new Vector3f(targetX, targetY, targetZ);
	}
	
	/**
	 * Updates the status of a specific door in the world.<br>
	 * This method changes whether a door is open or closed.
	 * @param instanceId The unique identifier for the current instance.
	 * @param name The unique name of the door to update.
	 * @param state The new status of the door, such as {@code true} for open or {@code false} for closed.
	 */
	@Override
	public void setDoorState(int instanceId, String name, boolean state)
	{
	}
	
	/**
	 * Adds a {@link Spatial} object as a child to this node.<br>
	 * It checks for intersections with existing children to determine placement.<br>
	 * If the child is a {@link DoorGeometry}, it is added to the internal doors map.
	 * @param child The {@link Spatial} object to attach.
	 * @return Always returns 0.
	 */
	@Override
	public int attachChild(Spatial child)
	{
		return 0;
	}
}
