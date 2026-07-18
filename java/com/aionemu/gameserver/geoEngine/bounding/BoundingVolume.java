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
package com.aionemu.gameserver.geoEngine.bounding;

import java.nio.FloatBuffer;

import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.math.Matrix4f;
import com.aionemu.gameserver.geoEngine.math.Plane;
import com.aionemu.gameserver.geoEngine.math.Ray;
import com.aionemu.gameserver.geoEngine.math.Vector3f;

/**
 * This class defines an interface for handling the containment of a collection of points.<br>
 * It serves as a base for various geometric shapes used in collision detection.<br>
 * Implementations are used to determine if objects occupy the same space.
 * @author Mark Powell
 */
public abstract class BoundingVolume implements Collidable
{
	public enum Type
	{
		Sphere,
		AABB,
		OBB,
		Capsule;
	}
	
	protected int checkPlane = 0;
	Vector3f center = new Vector3f();
	
	/**
	 * Creates a new instance of a {@link BoundingVolume}.<br>
	 * This is the default constructor for the class.
	 */
	public BoundingVolume()
	{
	}
	
	/**
	 * Creates a new {@link BoundingVolume} instance.<br>
	 * This constructor initializes the volume using a specific center point.
	 * @param center The {@code Vector3f} position for the center of the volume.
	 */
	public BoundingVolume(Vector3f center)
	{
		this.center.set(center);
	}
	
	/**
	 * Retrieves the current plane index used for collision checks.<br>
	 * This value is stored in the {@code checkPlane} field.
	 * @return The integer value of the checked plane.
	 */
	public int getCheckPlane()
	{
		return checkPlane;
	}
	
	/**
	 * Updates the {@code checkPlane} value for this bounding volume.<br>
	 * This method sets the internal plane identifier used during collision checks.
	 * @param value The new integer value to assign to {@code checkPlane}.
	 */
	public void setCheckPlane(int value)
	{
		checkPlane = value;
	}
	
	/**
	 * getType returns the type of bounding volume this is.
	 * @return
	 */
	public abstract Type getType();
	
	/**
	 * <code>transform</code> alters the location of the bounding volume by a rotation, translation and a scalar.
	 * @param trans the transform to affect the bound.
	 * @param store sphere to store result in
	 * @return the new bounding volume.
	 */
	public abstract BoundingVolume transform(Matrix4f trans, BoundingVolume store);
	
	/**
	 * <code>whichSide</code> returns the side on which the bounding volume lies on a plane. Possible values are POSITIVE_SIDE, NEGATIVE_SIDE, and NO_SIDE.
	 * @param plane the plane to check against this bounding volume.
	 * @return the side on which this bounding volume lies.
	 */
	public abstract Plane.Side whichSide(Plane plane);
	
	/**
	 * <code>computeFromPoints</code> generates a bounding volume that encompasses a collection of points.
	 * @param points the points to contain.
	 */
	public abstract void computeFromPoints(FloatBuffer points);
	
	/**
	 * <code>merge</code> combines two bounding volumes into a single bounding volume that contains both this bounding volume and the parameter volume.
	 * @param volume the volume to combine.
	 * @return the new merged bounding volume.
	 */
	public abstract BoundingVolume merge(BoundingVolume volume);
	
	/**
	 * <code>mergeLocal</code> combines two bounding volumes into a single bounding volume that contains both this bounding volume and the parameter volume. The result is stored locally.
	 * @param volume the volume to combine.
	 * @return this
	 */
	public abstract BoundingVolume mergeLocal(BoundingVolume volume);
	
	/**
	 * <code>clone</code> creates a new BoundingVolume object containing the same data as this one.
	 * @param store where to store the cloned information. if null or wrong class, a new store is created.
	 * @return the new BoundingVolume
	 */
	public abstract BoundingVolume clone(BoundingVolume store);
	
	/**
	 * Retrieves the center position of this bounding volume.<br>
	 * This method returns the {@code Vector3f} representing the middle point.
	 * @return The center of the volume as a {@link Vector3f}.
	 */
	public Vector3f getCenter()
	{
		return center;
	}
	
	/**
	 * Calculates the center of the bounding volume.<br>
	 * This method copies the internal {@code center} value into a provided object.<br>
	 * It returns the same object passed as an argument.
	 * @param store The {@code Vector3f} object where the result will be stored.
	 * @return The same {@code Vector3f} instance as the {@code store} parameter.
	 */
	public Vector3f getCenter(Vector3f store)
	{
		store.set(center);
		return store;
	}
	
	/**
	 * Updates the center position of this {@link BoundingVolume}.<br>
	 * This method replaces the current {@code center} with a new value.
	 * @param newCenter The new {@code Vector3f} to set as the center.
	 */
	public void setCenter(Vector3f newCenter)
	{
		center = newCenter;
	}
	
	/**
	 * Calculates the distance between this volume's center and a given point.<br>
	 * This method uses the {@code distance} calculation.
	 * @param point The {@code Vector3f} position to measure from.
	 * @return The distance as a {@code float}.
	 */
	public float distanceTo(Vector3f point)
	{
		return center.distance(point);
	}
	
	/**
	 * Calculates the squared distance between this volume's center and a given point.<br>
	 * This method is faster than {@code distanceTo} because it avoids a square root operation.<br>
	 * It is useful for performance-heavy proximity checks.
	 * @param point The {@code Vector3f} position to measure from.
	 * @return The squared distance as a {@code float}.
	 */
	public float distanceSquaredTo(Vector3f point)
	{
		return center.distanceSquared(point);
	}
	
	/**
	 * Find the distance from the nearest edge of this Bounding Volume to the given point.
	 * @param point The point to get the distance to
	 * @return distance
	 */
	public abstract float distanceToEdge(Vector3f point);
	
	/**
	 * determines if this bounding volume and a second given volume are intersecting. Intersecting being: one volume contains another, one volume overlaps another or one volume touches another.
	 * @param bv the second volume to test against.
	 * @return true if this volume intersects the given volume.
	 */
	public abstract boolean intersects(BoundingVolume bv);
	
	/**
	 * determines if a ray intersects this bounding volume.
	 * @param ray the ray to test.
	 * @return true if this volume is intersected by a given ray.
	 */
	public abstract boolean intersects(Ray ray);
	
	/**
	 * determines if this bounding volume and a given bounding sphere are intersecting.
	 * @param bs the bounding sphere to test against.
	 * @return true if this volume intersects the given bounding sphere.
	 */
	public abstract boolean intersectsSphere(BoundingSphere bs);
	
	/**
	 * determines if this bounding volume and a given bounding box are intersecting.
	 * @param bb the bounding box to test against.
	 * @return true if this volume intersects the given bounding box.
	 */
	public abstract boolean intersectsBoundingBox(BoundingBox bb);
	
	/**
	 * determines if this bounding volume and a given bounding box are intersecting.
	 * @param bb the bounding box to test against.
	 * @return true if this volume intersects the given bounding box.
	 */
	// public abstract boolean intersectsOrientedBoundingBox(OrientedBoundingBox bb);
	
	/**
	 * determines if a given point is contained within this bounding volume.
	 * @param point the point to check
	 * @return true if the point lies within this bounding volume.
	 */
	public abstract boolean contains(Vector3f point);
	
	/**
	 * Determines if a given point intersects (touches or is inside) this bounding volume.
	 * @param point the point to check
	 * @return true if the point lies within this bounding volume.
	 */
	public abstract boolean intersects(Vector3f point);
	
	public abstract float getVolume();
	
	/**
	 * Creates a deep copy of this {@link BoundingVolume} object.<br>
	 * This method copies all fields including the internal {@code center} vector.
	 * @return A new {@code BoundingVolume} instance with the same properties.
	 */
	@Override
	public BoundingVolume clone()
	{
		try
		{
			final BoundingVolume clone = (BoundingVolume) super.clone();
			clone.center = center.clone();
			return clone;
		}
		catch (CloneNotSupportedException ex)
		{
			throw new AssertionError();
		}
	}
}
