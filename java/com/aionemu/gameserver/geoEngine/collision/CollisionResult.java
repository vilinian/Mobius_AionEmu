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
package com.aionemu.gameserver.geoEngine.collision;

import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.Geometry;

/**
 * Represents the outcome of a collision detection between two {@link Geometry} objects.<br>
 * It stores spatial data such as the contact point and normal vector for physics calculations.
 * @author Kirill
 */
public class CollisionResult implements Comparable<CollisionResult>
{
	private Geometry geometry;
	private Vector3f contactPoint;
	private Vector3f contactNormal;
	private float distance;
	
	/**
	 * Creates a new {@link CollisionResult} object.<br>
	 * This constructor initializes the collision data with specific values.
	 * @param contactPoint The {@code Vector3f} position where the collision occurred.
	 * @param distance The float value representing the distance of the collision.
	 */
	public CollisionResult(Vector3f contactPoint, float distance)
	{
		this.contactPoint = contactPoint;
		this.distance = distance;
	}
	
	/**
	 * Creates a new, empty instance of {@link CollisionResult}.<br>
	 * All fields are initialized to their default values.
	 */
	public CollisionResult()
	{
	}
	
	/**
	 * Sets the specific location where a collision occurred.<br>
	 * This updates the {@code contactPoint} field of this {@link CollisionResult}.
	 * @param point The new {@code Vector3f} coordinates for the contact point.
	 */
	public void setContactPoint(Vector3f point)
	{
		contactPoint = point;
	}
	
	/**
	 * Sets the distance value for this {@link CollisionResult}.<br>
	 * This updates the internal {@code distance} field.
	 * @param dist The new distance value to set.
	 */
	public void setDistance(float dist)
	{
		distance = dist;
	}
	
	/**
	 * Compares this {@code CollisionResult} with another one.<br>
	 * It determines the order based on the {@code distance} value.
	 * @param other The other {@code CollisionResult} to compare against.
	 * @return A negative integer if this distance is less than the other, a positive integer if it is greater, or zero if they are equal.
	 */
	@Override
	public int compareTo(CollisionResult other)
	{
		if (distance < other.distance)
		{
			return -1;
		}
		else if (distance > other.distance)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * Sets the normal vector of the contact point.<br>
	 * This updates the {@code contactNormal} field with the provided value.
	 * @param norm The {@code Vector3f} representing the new contact normal.
	 */
	public void setContactNormal(Vector3f norm)
	{
		contactNormal = norm;
	}
	
	/**
	 * Sets the {@code Geometry} object for this collision result.<br>
	 * This updates the internal geometry field with the provided value.
	 * @param geom The {@link Geometry} to set.
	 */
	public void setGeometry(Geometry geom)
	{
		geometry = geom;
	}
	
	/**
	 * Retrieves the normal vector of the contact point.<br>
	 * This is used to determine the direction of a collision.
	 * @return The {@code Vector3f} representing the contact normal.
	 */
	public Vector3f getContactNormal()
	{
		return contactNormal;
	}
	
	/**
	 * Retrieves the specific point where a collision occurred.<br>
	 * This returns the {@code Vector3f} position of the contact.
	 * @return The {@code Vector3f} representing the contact point.
	 */
	public Vector3f getContactPoint()
	{
		return contactPoint;
	}
	
	/**
	 * Retrieves the {@code Geometry} object associated with this collision.<br>
	 * This method returns the shape that was hit during the collision check.
	 * @return The {@link Geometry} of the collided object.
	 */
	public Geometry getGeometry()
	{
		return geometry;
	}
	
	/**
	 * Returns the distance value of the collision.<br>
	 * This value is retrieved from the {@code distance} field.
	 * @return The distance as a {@code float}.
	 */
	public float getDistance()
	{
		return distance;
	}
}
