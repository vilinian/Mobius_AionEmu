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
package com.aionemu.gameserver.geoEngine.scene;

import com.aionemu.gameserver.geoEngine.bounding.BoundingVolume;
import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.math.Matrix3f;
import com.aionemu.gameserver.geoEngine.math.Matrix4f;
import com.aionemu.gameserver.geoEngine.math.Ray;
import com.aionemu.gameserver.geoEngine.math.Triangle;
import com.aionemu.gameserver.geoEngine.math.Vector3f;

/**
 * Represents a basic geometric shape within the game world.<br>
 * This class provides the foundation for spatial calculations and collision detection.<br>
 * It extends {@link Spatial} to handle positioning and transformations.
 */
public class Geometry extends Spatial
{
	/**
	 * The mesh contained herein
	 */
	protected Mesh mesh;
	protected Matrix4f cachedWorldMat = new Matrix4f();
	
	/**
	 * Creates a new instance of the {@link Geometry} class.<br>
	 * This constructor is intended for serialization purposes only.<br>
	 * Do not use this constructor to create objects in your code.
	 */
	public Geometry()
	{
	}
	
	/**
	 * Creates a new {@link Geometry} instance with a specific name.<br>
	 * This constructor initializes the object using the parent class logic.<br>
	 * It is primarily used for creating named geometry objects.
	 * @param name The unique identifier for this geometry object.
	 */
	public Geometry(String name)
	{
		super(name);
	}
	
	/**
	 * Creates a new {@link Geometry} instance with a specific name and mesh.<br>
	 * This constructor initializes the object using the provided {@code Mesh}.<br>
	 * It will throw a {@code NullPointerException} if the {@code mesh} is {@code null}.
	 * @param name The unique name for this geometry.
	 * @param mesh The {@link Mesh} data to associate with this geometry.
	 */
	public Geometry(String name, Mesh mesh)
	{
		this(name);
		if (mesh == null)
		{
			throw new NullPointerException();
		}
		
		this.mesh = mesh;
	}
	
	/**
	 * Returns the total number of vertices in the current mesh.<br>
	 * This method calls {@code getVertexCount} to retrieve the count.
	 * @return The number of vertices as an {@code int}.
	 */
	@Override
	public int getVertexCount()
	{
		return mesh.getVertexCount();
	}
	
	/**
	 * Retrieves the total number of triangles in the current mesh.<br>
	 * This method calls {@code getTriangleCount} to get the value.
	 * @return The total count of triangles as an {@code int}.
	 */
	@Override
	public int getTriangleCount()
	{
		return mesh.getTriangleCount();
	}
	
	/**
	 * Sets the {@code Mesh} for this geometry object.<br>
	 * This updates the internal mesh reference used for rendering and calculations.
	 * @param mesh The {@link Mesh} to be assigned to this geometry.
	 */
	public void setMesh(Mesh mesh)
	{
		this.mesh = mesh;
	}
	
	/**
	 * Retrieves the {@link Mesh} associated with this geometry.<br>
	 * This method returns the underlying 3D model data.
	 * @return The {@code Mesh} object, or {@code null} if no mesh is assigned.
	 */
	public Mesh getMesh()
	{
		return mesh;
	}
	
	/**
	 * Retrieves the bounding volume of the current model.<br>
	 * This method calls {@code getBound} to get the data.
	 * @return The {@code BoundingVolume} associated with the mesh.
	 */
	public BoundingVolume getModelBound()
	{
		return mesh.getBound();
	}
	
	/**
	 * Updates the bounding volume of the model.<br>
	 * This method synchronizes the mesh bounds with the current world matrix.<br>
	 * It ensures that {@code getModelBound} returns an accurate position.
	 */
	@Override
	public void updateModelBound()
	{
		mesh.updateBound();
		worldBound = getModelBound().transform(cachedWorldMat, worldBound);
	}
	
	/**
	 * Retrieves the current world transformation matrix.<br>
	 * This matrix represents the object's position, rotation, and scale in the world.<br>
	 * It returns the {@code cachedWorldMat} field.
	 * @return The {@link Matrix4f} representing the world matrix.
	 */
	public Matrix4f getWorldMatrix()
	{
		return cachedWorldMat;
	}
	
	/**
	 * Sets the bounding volume for the model.<br>
	 * This method updates the {@code mesh} with the provided {@link BoundingVolume}.
	 * @param modelBound The new {@code BoundingVolume} to apply.
	 */
	@Override
	public void setModelBound(BoundingVolume modelBound)
	{
		mesh.setBound(modelBound);
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
		if (other instanceof Ray)
		{
			if (!worldBound.intersects(((Ray) other)))
			{
				return 0;
			}
		}
		
		// Note: BIHTree in the mesh already checks for collisions with the mesh's bounds.
		final int prevSize = results.size();
		final int added = mesh.collideWith(other, cachedWorldMat, worldBound, results);
		final int newSize = results.size();
		for (int i = prevSize; i < newSize; i++)
		{
			results.getCollisionDirect(i).setGeometry(this);
		}
		
		return added;
	}
	
	/**
	 * Updates the world transformation of this {@link Geometry} object.<br>
	 * This method sets the rotation, location, and scale in one step.<br>
	 * It updates the internal {@code cachedWorldMat}.
	 * @param rotation The {@code Matrix3f} representing the object's rotation.
	 * @param loc The {@code Vector3f} representing the object's position.
	 * @param scale The float value used to resize the object.
	 */
	@Override
	public void setTransform(Matrix3f rotation, Vector3f loc, float scale)
	{
		cachedWorldMat.loadIdentity();
		cachedWorldMat.setRotationMatrix(rotation);
		cachedWorldMat.scale(scale);
		cachedWorldMat.setTranslation(loc);
	}
	
	/**
	 * Retrieves the collision flags for this geometry.<br>
	 * This method returns the flags stored in the underlying {@link Mesh}.
	 * @return The collision flags as a {@code short}.
	 */
	@Override
	public short getCollisionFlags()
	{
		return mesh.getCollisionFlags();
	}
	
	/**
	 * Updates the collision flags for the underlying mesh.<br>
	 * This method passes the provided {@code flags} to the internal {@link Mesh} object.
	 * @param flags The new collision bitmask to apply.
	 */
	@Override
	public void setCollisionFlags(short flags)
	{
		mesh.setCollisionFlags(flags);
	}
}
