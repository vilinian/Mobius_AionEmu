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
package com.aionemu.gameserver.geoEngine.scene.mesh;

import java.util.BitSet;

import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.math.Ray;
import com.aionemu.gameserver.geoEngine.math.Triangle;
import com.aionemu.gameserver.geoEngine.scene.Geometry;
import com.aionemu.gameserver.geoEngine.scene.Mesh;
import com.aionemu.gameserver.geoEngine.scene.Spatial;

/**
 * Represents the geometric data for doors within the game world.<br>
 * This class extends {@link Geometry} to provide specific collision and spatial properties for door objects.
 * @author MrPoke, Rolandas
 */
public class DoorGeometry extends Geometry
{
	BitSet instances = new BitSet();
	private boolean foundTemplate = false;
	
	/**
	 * Creates a new {@link DoorGeometry} object.<br>
	 * This constructor initializes the door with a specific name and visual mesh.
	 * @param name The unique identifier for this door geometry.
	 * @param mesh The {@code Mesh} data used to render the door.
	 */
	public DoorGeometry(String name, Mesh mesh)
	{
		super(name, mesh);
	}
	
	/**
	 * Updates the open or closed state of a specific door instance.<br>
	 * This method modifies the internal {@code BitSet} to track the status.
	 * @param instanceId The unique identifier for the door instance.
	 * @param isOpened The new state to set, where {@code true} means open and {@code false} means closed.
	 */
	public void setDoorState(int instanceId, boolean isOpened)
	{
		instances.set(instanceId, isOpened);
	}
	
	/**
	 * Checks if this geometry collides with another object.<br>
	 * This method handles collisions with {@link Ray} and {@link Triangle} types.<br>
	 * It updates the provided {@code results} object if a collision occurs.
	 * @param other The {@link Collidable} object to check against.
	 * @param results The {@link CollisionResults} container to store any detected hits.
	 * @return Returns 1 if a collision occurred, or 0 if no collision was found.
	 */
	@Override
	public int collideWith(Collidable other, CollisionResults results)
	{
		if (foundTemplate && instances.get(results.getInstanceId()))
		{
			return 0;
		}
		
		if (other instanceof Ray)
		{
			// no collision if inside arena spheres, so just check volume
			return getWorldBound().collideWith(other, results);
		}
		
		return super.collideWith(other, results);
	}
	
	/**
	 * Checks if the template for this door has been located.<br>
	 * This is used to determine if the geometry data is ready.
	 * @return {@code true} if the template was found, {@code false} otherwise.
	 */
	public boolean isFoundTemplate()
	{
		return foundTemplate;
	}
	
	/**
	 * Updates the status of whether a template has been located.<br>
	 * This method sets the {@code foundTemplate} flag to either {@code true} or {@code false}.
	 * @param foundTemplate The new value to set for the template discovery status.
	 */
	public void setFoundTemplate(boolean foundTemplate)
	{
		this.foundTemplate = foundTemplate;
	}
	
	/**
	 * Updates the bounding box of the model.<br>
	 * This method removes child {@link Spatial} objects that have no children of their own.<br>
	 * It then calls the superclass {@code updateModelBound()} method.
	 */
	@Override
	public void updateModelBound()
	{
		// duplicate call distorts world bounds, thus do only once
		if (worldBound == null)
		{
			mesh.updateBound();
			worldBound = getModelBound().transform(cachedWorldMat, worldBound);
		}
	}
}
