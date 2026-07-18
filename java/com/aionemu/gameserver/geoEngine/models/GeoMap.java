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
package com.aionemu.gameserver.geoEngine.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.geoEngine.bounding.BoundingBox;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.collision.CollisionResult;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.math.Ray;
import com.aionemu.gameserver.geoEngine.math.Triangle;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.Node;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.geoEngine.scene.mesh.DoorGeometry;

/**
 * Represents a geographical map within the game world.<br>
 * This class serves as a container for spatial data and geometry used by the {@link com.aionemu.gameserver.geoEngine.scene.Spatial} system.
 * @author Mr. Poke
 */
public class GeoMap extends Node
{
	private short[] terrainData;
	private final List<BoundingBox> tmpBox = new ArrayList<>();
	private final Map<String, DoorGeometry> doors = new HashMap<>();
	
	/**
	 * Creates a new {@code GeoMap} instance.<br>
	 * This constructor initializes the collision flags and builds the spatial structure.<br>
	 * It divides the world into sections based on the provided size.
	 * @param name The unique identifier for this map.
	 * @param worldSize The total dimension of the world area.
	 */
	public GeoMap(String name, int worldSize)
	{
		setCollisionFlags((short) (CollisionIntention.ALL.getId() << 8));
		for (int x = 0; x < worldSize; x += 256)
		{
			for (int y = 0; y < worldSize; y += 256)
			{
				final Node geoNode = new Node("");
				geoNode.setCollisionFlags((short) (CollisionIntention.ALL.getId() << 8));
				tmpBox.add(new BoundingBox(new Vector3f(x, y, 0), new Vector3f(x + 256, y + 256, 4000)));
				super.attachChild(geoNode);
			}
		}
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
		if (!GeoDataConfig.GEO_DOORS_ENABLE)
		{
			return null;
		}
		
		final String mesh = meshFile.toUpperCase();
		final Vector3f templatePoint = new Vector3f(x, y, z);
		float distance = Float.MAX_VALUE;
		DoorGeometry foundDoor = null;
		for (Entry<String, DoorGeometry> door : doors.entrySet())
		{
			if (!(door.getKey().startsWith(Integer.toString(worldId)) && door.getKey().endsWith(mesh)))
			{
				continue;
			}
			
			final DoorGeometry checkDoor = doors.get(door.getKey());
			final float doorDistance = checkDoor.getWorldBound().distanceTo(templatePoint);
			if (distance > doorDistance)
			{
				distance = doorDistance;
				foundDoor = checkDoor;
			}
			
			if (checkDoor.getWorldBound().intersects(templatePoint))
			{
				foundDoor = checkDoor;
				break;
			}
		}
		
		if (foundDoor == null)
		{
			// log.warn("Could not find static door: " + worldId + " " + meshFile + " " + templatePoint);
			return null;
		}
		
		foundDoor.setFoundTemplate(true);
		
		// Log the match of static door worldId, meshFile, and templatePoint with foundDoor's name and its distance.
		return foundDoor.getName();
	}
	
	/**
	 * Updates the open or closed state of a specific door.<br>
	 * This method finds a door by its name and applies the new state.
	 * @param instanceId The unique identifier for the current world instance.
	 * @param name The unique name of the door to update.
	 * @param isOpened The desired state of the door, either {@code true} or {@code false}.
	 */
	public void setDoorState(int instanceId, String name, boolean isOpened)
	{
		final DoorGeometry door = doors.get(name);
		if (door != null)
		{
			door.setDoorState(instanceId, isOpened);
		}
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
		int i = 0;
		
		if (child instanceof DoorGeometry)
		{
			doors.put(child.getName(), (DoorGeometry) child);
		}
		
		for (Spatial spatial : getChildren())
		{
			if (tmpBox.get(i).intersects(child.getWorldBound()))
			{
				((Node) spatial).attachChild(child);
			}
			
			i++;
		}
		
		return 0;
	}
	
	/**
	 * Updates the internal terrain data for this {@link GeoMap}.<br>
	 * This method replaces the current array with a new one.
	 * @param terrainData The new array of {@code short} values to store.
	 */
	public void setTerrainData(short[] terrainData)
	{
		this.terrainData = terrainData;
	}
	
	/**
	 * Calculates the height value at a specific coordinate.<br>
	 * This method uses {@code terrainData} to find the ground level.<br>
	 * It returns the {@code z} coordinate of the nearest collision point.
	 * @param x The horizontal position on the map.
	 * @param y The vertical position on the map.
	 * @return The calculated height as a {@code float}.
	 */
	public float getZ(float x, float y)
	{
		final CollisionResults results = new CollisionResults(CollisionIntention.PHYSICAL.getId(), false, 1);
		final Vector3f pos = new Vector3f(x, y, 4000);
		final Vector3f dir = new Vector3f(x, y, 0);
		final Float limit = pos.distance(dir);
		dir.subtractLocal(pos).normalizeLocal();
		final Ray r = new Ray(pos, dir);
		r.setLimit(limit);
		collideWith(r, results);
		Vector3f terrain = null;
		if (terrainData.length == 1)
		{
			terrain = new Vector3f(x, y, terrainData[0] / 32f);
		}
		else
		{
			terrain = terraionCollision(x, y, r);
		}
		
		if (terrain != null)
		{
			final CollisionResult result = new CollisionResult(terrain, Math.max(0, Math.max(4000 - terrain.z, terrain.z)));
			results.addCollision(result);
		}
		
		if (results.size() == 0)
		{
			return 0;
		}
		
		return results.getClosestCollision().getContactPoint().z;
	}
	
	/**
	 * Retrieves the adjusted {@code z} coordinate for a specific location.<br>
	 * This method calculates collisions and returns the nearest surface height.<br>
	 * It considers both terrain data and physical obstacles.
	 * @param x The horizontal {@code x} coordinate.
	 * @param y The horizontal {@code y} coordinate.
	 * @param z The initial vertical {@code z} coordinate.
	 * @param instanceId The unique identifier for the current instance.
	 * @return The calculated {@code z} value as a {@code float}.
	 */
	public float getZ(float x, float y, float z, int instanceId)
	{
		final CollisionResults results = new CollisionResults(CollisionIntention.PHYSICAL.getId(), false, instanceId);
		final Vector3f pos = new Vector3f(x, y, z + 2);
		final Vector3f dir = new Vector3f(x, y, z - 100);
		final Float limit = pos.distance(dir);
		dir.subtractLocal(pos).normalizeLocal();
		final Ray r = new Ray(pos, dir);
		r.setLimit(limit);
		collideWith(r, results);
		Vector3f terrain = null;
		if (terrainData.length == 1)
		{
			if (terrainData[0] != 0)
			{
				terrain = new Vector3f(x, y, terrainData[0] / 32f);
			}
		}
		else
		{
			terrain = terraionCollision(x, y, r);
		}
		
		if ((terrain != null) && (terrain.z > 0) && (terrain.z < (z + 2)))
		{
			final CollisionResult result = new CollisionResult(terrain, Math.abs((z - terrain.z) + 2));
			results.addCollision(result);
		}
		
		if (results.size() == 0)
		{
			return z;
		}
		
		return results.getClosestCollision().getContactPoint().z;
	}
	
	/**
	 * Calculates the closest collision point between a starting position and a target destination.<br>
	 * This method handles terrain checks, object collisions, and movement logic based on flight status.<br>
	 * It returns the adjusted position where the entity should stop or move to.
	 * @param x The current X coordinate of the entity.
	 * @param y The current Y coordinate of the entity.
	 * @param z The current Z coordinate of the entity.
	 * @param targetX The desired destination X coordinate.
	 * @param targetY The desired destination Y coordinate.
	 * @param targetZ The desired destination Z coordinate.
	 * @param changeDirection A boolean flag indicating if the movement should adjust for direction changes.
	 * @param fly A boolean flag indicating if the entity is currently flying.
	 * @param instanceId The unique identifier for the current game instance.
	 * @param intentions The byte representing the movement intentions of the entity.
	 * @return A {@code Vector3f} representing the final calculated collision point or position.
	 */
	public Vector3f getClosestCollision(float x, float y, float z, float targetX, float targetY, float targetZ, boolean changeDirection, boolean fly, int instanceId, byte intentions)
	{
		float zChecked1 = 0;
		float zChecked2 = 0;
		if (!fly && changeDirection)
		{
			zChecked1 = z;
			z = getZ(x, y, z + 2, instanceId);
		}
		
		z += 1f;
		targetZ += 1f;
		final Vector3f start = new Vector3f(x, y, z);
		final Vector3f end = new Vector3f(targetX, targetY, targetZ);
		final Vector3f pos = new Vector3f(x, y, z);
		final Vector3f dir = new Vector3f(targetX, targetY, targetZ);
		
		final CollisionResults results = new CollisionResults(intentions, false, instanceId);
		
		final Float limit = pos.distance(dir);
		dir.subtractLocal(pos).normalizeLocal();
		final Ray r = new Ray(pos, dir);
		r.setLimit(limit);
		final Vector3f terrain = calculateTerrainCollision(start.x, start.y, start.z, end.x, end.y, end.z, r);
		if (terrain != null)
		{
			final CollisionResult result = new CollisionResult(terrain, terrain.distance(pos));
			results.addCollision(result);
		}
		
		collideWith(r, results);
		
		float geoZ = 0;
		if (results.size() == 0)
		{
			if (fly)
			{
				return end;
			}
			
			if ((zChecked1 > 0) && (targetX == x) && (targetY == y) && ((targetZ - 1f) == zChecked1))
			{
				geoZ = z - 1f;
			}
			else
			{
				zChecked2 = targetZ;
				geoZ = getZ(targetX, targetY, targetZ + 2, instanceId);
			}
			
			if (Math.abs(geoZ - targetZ) < start.distance(end))
			{
				return end.setZ(geoZ);
			}
			
			return start;
		}
		
		Vector3f contactPoint = results.getClosestCollision().getContactPoint();
		final float distance = results.getClosestCollision().getDistance();
		if (distance < 1)
		{
			return start;
		}
		
		// -1m
		contactPoint = contactPoint.subtract(dir);
		if (!fly && changeDirection)
		{
			if ((zChecked1 > 0) && (contactPoint.x == x) && (contactPoint.y == y) && (contactPoint.z == zChecked1))
			{
				contactPoint.z = z - 1f;
			}
			else if ((zChecked2 > 0) && (contactPoint.x == targetX) && (contactPoint.y == targetY) && (contactPoint.z == zChecked2))
			{
				contactPoint.z = geoZ;
			}
			else
			{
				contactPoint.z = getZ(contactPoint.x, contactPoint.y, contactPoint.z + 2, instanceId);
			}
		}
		
		if (!fly && (Math.abs(start.z - contactPoint.z) > distance))
		{
			return start;
		}
		
		return contactPoint;
	}
	
	/**
	 * Calculates collisions between a starting point and a target destination.<br>
	 * This method checks for terrain and object obstacles along the path.
	 * @param x The current X coordinate.
	 * @param y The current Y coordinate.
	 * @param z The current Z coordinate.
	 * @param targetX The destination X coordinate.
	 * @param targetY The destination Y coordinate.
	 * @param targetZ The destination Z coordinate.
	 * @param changeDirection Whether the movement should adjust its direction.
	 * @param fly Whether the entity is currently flying.
	 * @param instanceId The unique ID of the current instance.
	 * @param intentions The collision intentions for this movement.
	 * @return A {@link CollisionResults} object containing all detected collisions.
	 */
	public CollisionResults getCollisions(float x, float y, float z, float targetX, float targetY, float targetZ, boolean changeDirection, boolean fly, int instanceId, byte intentions)
	{
		if (!fly && changeDirection)
		{
			z = getZ(x, y, z + 2, instanceId);
		}
		
		z += 1f;
		targetZ += 1f;
		final Vector3f start = new Vector3f(x, y, z);
		final Vector3f end = new Vector3f(targetX, targetY, targetZ);
		final Vector3f pos = new Vector3f(x, y, z);
		final Vector3f dir = new Vector3f(targetX, targetY, targetZ);
		
		final CollisionResults results = new CollisionResults(intentions, false, instanceId);
		
		final Float limit = pos.distance(dir);
		dir.subtractLocal(pos).normalizeLocal();
		final Ray r = new Ray(pos, dir);
		r.setLimit(limit);
		final Vector3f terrain = calculateTerrainCollision(start.x, start.y, start.z, end.x, end.y, end.z, r);
		if (terrain != null)
		{
			final CollisionResult result = new CollisionResult(terrain, terrain.distance(pos));
			results.addCollision(result);
		}
		
		collideWith(r, results);
		return results;
	}
	
	/**
	 * Calculates the collision point with the terrain along a specific path.<br>
	 * This method iterates through segments of the {@code Ray} to find the first hit.<br>
	 * It uses {@code float, Ray)} to check each step.
	 * @param x The starting X coordinate.
	 * @param y The starting Y coordinate.
	 * @param z The starting Z coordinate.
	 * @param targetX The destination X coordinate.
	 * @param targetY The destination Y coordinate.
	 * @param targetZ The destination Z coordinate.
	 * @param ray The {@code Ray} object representing the path of movement.
	 * @return A {@code Vector3f} representing the collision point, or {@code null} if no collision occurs.
	 */
	private Vector3f calculateTerrainCollision(float x, float y, float z, float targetX, float targetY, float targetZ, Ray ray)
	{
		final float x2 = targetX - x;
		final float y2 = targetY - y;
		final int intD = (int) Math.abs(ray.getLimit());
		
		for (float s = 0; s < intD; s += 2)
		{
			final float tempX = x + ((x2 * s) / ray.getLimit());
			final float tempY = y + ((y2 * s) / ray.getLimit());
			final Vector3f result = terraionCollision(tempX, tempY, ray);
			if (result != null)
			{
				return result;
			}
		}
		
		return null;
	}
	
	/**
	 * Calculates the collision point between a {@code Ray} and the terrain.<br>
	 * This method checks for intersections with triangles formed by the terrain data.<br>
	 * It returns the intersection point if one exists.
	 * @param x The horizontal coordinate to check.
	 * @param y The vertical coordinate to check.
	 * @param ray The {@code Ray} used to detect collisions.
	 * @return A {@code Vector3f} representing the collision point, or {@code null} if no collision occurs.
	 */
	private Vector3f terraionCollision(float x, float y, Ray ray)
	{
		y /= 2f;
		x /= 2f;
		final int xInt = (int) x;
		final int yInt = (int) y;
		
		// p1-----p2
		// || ||
		// || ||
		// p3-----p4
		float p1, p2, p3, p4;
		if (terrainData.length == 1)
		{
			p1 = p2 = p3 = p4 = terrainData[0] / 32f;
		}
		else
		{
			final int size = (int) Math.sqrt(terrainData.length);
			try
			{
				final int index = yInt + (xInt * size);
				p1 = terrainData[index] / 32f;
				p2 = terrainData[index + 1] / 32f;
				p3 = terrainData[index + size] / 32f;
				p4 = terrainData[index + size + 1] / 32f;
				
				// check if the terrain quad is removed.
				if (terrainCutoutData != null)
				{
					if (Arrays.binarySearch(terrainCutoutData, index) >= 0)
					{
						// return false;
					}
				}
			}
			catch (Exception e)
			{
				return null;
			}
		}
		
		final Vector3f result = new Vector3f();
		if ((p1 >= 0) && (p2 >= 0) && (p3 >= 0))
		{
			final Triangle tringle1 = new Triangle(new Vector3f(xInt * 2, yInt * 2, p1), new Vector3f(xInt * 2, (yInt + 1) * 2, p2), new Vector3f((xInt + 1) * 2, yInt * 2, p3));
			if (ray.intersectWhere(tringle1, result))
			{
				return result;
			}
		}
		
		if ((p4 >= 0) && (p2 >= 0) && (p3 >= 0))
		{
			final Triangle tringle2 = new Triangle(new Vector3f((xInt + 1) * 2, (yInt + 1) * 2, p4), new Vector3f(xInt * 2, (yInt + 1) * 2, p2), new Vector3f((xInt + 1) * 2, yInt * 2, p3));
			if (ray.intersectWhere(tringle2, result))
			{
				return result;
			}
		}
		
		return null;
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
	public boolean canSee(float x, float y, float z, float targetX, float targetY, float targetZ, float limit, int instanceId)
	{
		targetZ += 1;
		z += 1;
		
		// Another fix can be seen in instances where getZ(targetX, targetY) is greater than targetZ.
		// return false;
		
		final float x2 = x - targetX;
		final float y2 = y - targetY;
		final float distance = (float) Math.sqrt((x2 * x2) + (y2 * y2));
		if (distance > 80f)
		{
			return false;
		}
		
		final int intD = (int) Math.abs(distance);
		
		final Vector3f pos = new Vector3f(x, y, z);
		final Vector3f dir = new Vector3f(targetX, targetY, targetZ);
		dir.subtractLocal(pos).normalizeLocal();
		final Ray r = new Ray(pos, dir);
		r.setLimit(limit);
		for (float s = 2; s < intD; s += 2)
		{
			final float tempX = targetX + ((x2 * s) / distance);
			final float tempY = targetY + ((y2 * s) / distance);
			final Vector3f result = terraionCollision(tempX, tempY, r);
			if (result != null)
			{
				return false;
			}
		}
		
		final CollisionResults results = new CollisionResults((byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId()), false, instanceId);
		final int collisions = collideWith(r, results);
		return ((results.size() == 0) && (collisions == 0));
	}
	
	/**
	 * Updates the bounding box of the model.<br>
	 * This method removes child {@link Spatial} objects that have no children of their own.<br>
	 * It then calls the superclass {@code updateModelBound()} method.
	 */
	@Override
	public void updateModelBound()
	{
		if (getChildren() != null)
		{
			final Iterator<Spatial> i = getChildren().iterator();
			while (i.hasNext())
			{
				final Spatial s = i.next();
				if ((s instanceof Node) && ((Node) s).getChildren().isEmpty())
				{
					i.remove();
				}
			}
		}
		
		super.updateModelBound();
	}
	
	private int[] terrainCutoutData;
	
	/**
	 * Updates the terrain cutout data for this map.<br>
	 * This method creates a sorted copy of the provided array to ensure consistency.
	 * @param cutoutData An {@code int[]} containing the raw cutout values.
	 */
	public void setTerrainCutouts(int[] cutoutData)
	{
		final int[] arr = cutoutData.clone();
		Arrays.sort(arr);
		terrainCutoutData = arr;
	}
}
