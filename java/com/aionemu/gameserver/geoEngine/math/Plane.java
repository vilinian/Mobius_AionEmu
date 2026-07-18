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
package com.aionemu.gameserver.geoEngine.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a geometric plane defined by the equation where {@code Normal} dot (x,y,z) equals a constant.<br>
 * This class provides methods to calculate the pseudo-distance of a point from the plane.<br>
 * The distance value can be negative if the point is located on the non-normal side of the plane.
 * @author Mark Powell
 * @author Joshua Slack
 */
public class Plane implements Cloneable
{
	private static final Logger logger = LoggerFactory.getLogger(Plane.class);
	
	public static enum Side
	{
		None,
		Positive,
		Negative
	}
	
	/**
	 * Vector normal to the plane.
	 */
	protected Vector3f normal;
	/**
	 * Constant of the plane. See formula in class definition.
	 */
	protected float constant;
	
	/**
	 * Creates a new instance of the {@link Plane} class.<br>
	 * This constructor initializes the plane with a default normal of {@code (0,0,0)}.<br>
	 * The constant value is set to {@code 0}.
	 */
	public Plane()
	{
		normal = new Vector3f();
	}
	
	/**
	 * Creates a new {@link Plane} instance with specific dimensions.<br>
	 * This constructor sets the plane's orientation and position.<br>
	 * If the provided {@code normal} is {@code null}, it defaults to a new {@code Vector3f}.
	 * @param normal The direction vector perpendicular to the plane surface.
	 * @param constant The scalar value used in the plane equation.
	 */
	public Plane(Vector3f normal, float constant)
	{
		if (normal == null)
		{
			logger.warn("Normal was null, created default normal.");
			normal = new Vector3f();
		}
		
		this.normal = normal;
		this.constant = constant;
	}
	
	/**
	 * Sets the normal vector for this {@link Plane}.<br>
	 * If the provided {@code normal} is {@code null}, a new default vector is used.<br>
	 * This method updates the internal state of the plane's orientation.
	 * @param normal The {@code Vector3f} representing the direction perpendicular to the plane.
	 */
	public void setNormal(Vector3f normal)
	{
		if (normal == null)
		{
			logger.warn("Normal was null, created default normal.");
			normal = new Vector3f();
		}
		
		this.normal.set(normal);
	}
	
	/**
	 * Sets the normal vector of this {@link Plane}.<br>
	 * This method updates the internal {@code normal} field.<br>
	 * If the current {@code normal} is {@code null}, a new {@code Vector3f} is created.
	 * @param x The x-coordinate of the normal.
	 * @param y The y-coordinate of the normal.
	 * @param z The z-coordinate of the normal.
	 */
	public void setNormal(float x, float y, float z)
	{
		if (normal == null)
		{
			logger.warn("Normal was null, created default normal.");
			normal = new Vector3f();
		}
		
		normal.set(x, y, z);
	}
	
	/**
	 * Retrieves the normal vector of this {@link Plane}.<br>
	 * This vector represents the direction perpendicular to the plane surface.
	 * @return The {@code Vector3f} representing the plane's normal.
	 */
	public Vector3f getNormal()
	{
		return normal;
	}
	
	/**
	 * Updates the {@code constant} value of this plane.<br>
	 * This value is used in the formula for calculating point distances.
	 * @param constant The new {@code float} value to set.
	 */
	public void setConstant(float constant)
	{
		this.constant = constant;
	}
	
	/**
	 * Retrieves the constant value of this {@link Plane}.<br>
	 * This value is used in the plane equation formula.
	 * @return The {@code float} constant value.
	 */
	public float getConstant()
	{
		return constant;
	}
	
	/**
	 * Finds the point on this plane closest to a given position.<br>
	 * It uses the {@code store} vector to perform the calculation.<br>
	 * This method modifies the internal state of the provided {@code store}.
	 * @param point The 3D coordinates to check.
	 * @param store A {@code Vector3f} used to store and return the result.
	 * @return The resulting {@code Vector3f} representing the closest point.
	 */
	public Vector3f getClosestPoint(Vector3f point, Vector3f store)
	{
		// float t = constant - normal.dot(point);
		// return store.set(normal).multLocal(t).addLocal(point);
		final float t = (constant - normal.dot(point)) / normal.dot(normal);
		return store.set(normal).multLocal(t).addLocal(point);
	}
	
	/**
	 * Finds the point on this plane that is nearest to the given {@code point}.<br>
	 * This method calculates the projection of the input onto the plane surface.
	 * @param point The {@code Vector3f} position to check.
	 * @return A new {@code Vector3f} representing the closest location on the plane.
	 */
	public Vector3f getClosestPoint(Vector3f point)
	{
		return getClosestPoint(point, new Vector3f());
	}
	
	/**
	 * Calculates the reflection of a point across this plane.<br>
	 * The result is stored in the provided {@code store} object.<br>
	 * This method uses the {@code pseudoDistance} to determine the offset.
	 * @param point The original position to reflect.
	 * @param store The {@code Vector3f} where the reflected coordinates will be saved.
	 * @return The same {@code store} object containing the new coordinates.
	 */
	public Vector3f reflect(Vector3f point, Vector3f store)
	{
		if (store == null)
		{
			store = new Vector3f();
		}
		
		final float d = pseudoDistance(point);
		store.set(normal).negateLocal().multLocal(d * 2f);
		store.addLocal(point);
		return store;
	}
	
	/**
	 * Calculates the distance from a point to this plane.<br>
	 * This value is "pseudo" because it can be negative.<br>
	 * A negative result means the point is on the opposite side of the normal.
	 * @param point The {@code Vector3f} position to check.
	 * @return The calculated pseudo distance as a {@code float}.
	 */
	public float pseudoDistance(Vector3f point)
	{
		return normal.dot(point) - constant;
	}
	
	/**
	 * Determines which side of the plane a specific point is located on.<br>
	 * This method uses {@code pseudoDistance} to check the position.
	 * @param point The {@code Vector3f} coordinates to check.
	 * @return The {@code Side} value representing the point's location.
	 */
	public Side whichSide(Vector3f point)
	{
		final float dis = pseudoDistance(point);
		if (dis < 0)
		{
			return Side.Negative;
		}
		else if (dis > 0)
		{
			return Side.Positive;
		}
		else
		{
			return Side.None;
		}
	}
	
	/**
	 * Checks if a specific point lies on this plane.<br>
	 * It uses {@code pseudoDistance} to determine the position.<br>
	 * The result is {@code true} if the distance is near zero.
	 * @param point The {@code Vector3f} coordinates to check.
	 * @return {@code true} if the point is on the plane, otherwise {@code false}.
	 */
	public boolean isOnPlane(Vector3f point)
	{
		final float dist = pseudoDistance(point);
		if ((dist < FastMath.FLT_EPSILON) && (dist > -FastMath.FLT_EPSILON))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Updates the plane's properties using three points from a triangle.<br>
	 * This method extracts vertices from the provided {@link AbstractTriangle}.<br>
	 * It automatically calculates the new normal and constant values.
	 * @param t The {@code AbstractTriangle} containing the points to define the plane.
	 */
	public void setPlanePoints(AbstractTriangle t)
	{
		setPlanePoints(t.get1(), t.get2(), t.get3());
	}
	
	/**
	 * Sets the plane's normal and calculates its constant value.<br>
	 * This method uses a specific point as the origin to define the plane.<br>
	 * It updates the internal {@code normal} field based on the provided vector.
	 * @param origin The position of a point that lies on the plane.
	 * @param normal The direction vector perpendicular to the plane surface.
	 */
	public void setOriginNormal(Vector3f origin, Vector3f normal)
	{
		this.normal.set(normal);
		constant = (normal.x * origin.x) + (normal.y * origin.y) + (normal.z * origin.z);
	}
	
	/**
	 * Sets the plane's orientation using three points.<br>
	 * This method calculates the {@code normal} and {@code constant} based on the provided vertices.
	 * @param v1 The first point of the plane.
	 * @param v2 The second point of the plane.
	 * @param v3 The third point of the plane.
	 */
	public void setPlanePoints(Vector3f v1, Vector3f v2, Vector3f v3)
	{
		normal.set(v2).subtractLocal(v1);
		normal.crossLocal(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z).normalizeLocal();
		constant = normal.dot(v1);
	}
	
	/**
	 * Returns a string representation of this {@code Plane}.<br>
	 * It includes the class name, the {@code normal}, and the {@code constant}.
	 * @return A formatted string describing the plane.
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " [Normal: " + normal + " - Constant: " + constant + "]";
	}
	
	/**
	 * Returns the class type of the current {@link Plane} instance.<br>
	 * This is useful for identifying specific subclasses at runtime.
	 * @return The {@code Class} object representing the type of this plane.
	 */
	public Class<? extends Plane> getClassTag()
	{
		return this.getClass();
	}
	
	/**
	 * Creates a deep copy of this {@link Plane} object.<br>
	 * This method clones the internal {@code normal} vector as well.
	 * @return A new {@code Plane} instance that is an exact copy of this one.
	 */
	@Override
	public Plane clone()
	{
		try
		{
			final Plane p = (Plane) super.clone();
			p.normal = normal.clone();
			return p;
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError();
		}
	}
}
