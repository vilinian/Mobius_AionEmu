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
package com.aionemu.gameserver.model.geometry;

import java.io.Serializable;

import com.aionemu.gameserver.model.templates.zone.Point2D;

/**
 * Represents a coordinate in three-dimensional space.<br>
 * This class is designed to be {@code Serializable} and supports cloning for easy data replication.
 * @author SoulKeeper
 */
@SuppressWarnings("serial")
public class Point3D implements Cloneable, Serializable
{
	/**
	 * X coord of the point
	 */
	private float x;
	/**
	 * Y coord of the point
	 */
	private float y;
	/**
	 * Z coord of the point
	 */
	private float z;
	
	/**
	 * Creates a new {@link Point3D} instance.<br>
	 * All coordinates are initialized to {@code 0.0f}.
	 */
	public Point3D()
	{
	}
	
	/**
	 * Creates a new {@link Point3D} using coordinates from a 2D point.<br>
	 * The {@code x} and {@code y} values are taken from the provided {@code point}.<br>
	 * The {@code z} value is set to the provided float.
	 * @param point The {@code Point2D} containing the horizontal coordinates.
	 * @param z The vertical coordinate for the new point.
	 */
	public Point3D(Point2D point, float z)
	{
		this(point.getX(), point.getY(), z);
	}
	
	/**
	 * Creates a new {@link Point3D} instance by copying the coordinates from another point.<br>
	 * This method uses the values from the provided {@code point} object to initialize this one.
	 * @param point The source {@link Point3D} to copy from.
	 */
	public Point3D(Point3D point)
	{
		this(point.getX(), point.getY(), point.getZ());
	}
	
	/**
	 * Creates a new {@link Point3D} instance.<br>
	 * This constructor sets the coordinates using the provided values.
	 * @param x The X coordinate of the point.
	 * @param y The Y coordinate of the point.
	 * @param z The Z coordinate of the point.
	 */
	public Point3D(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
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
	 * Sets the {@code x} coordinate of this point.<br>
	 * This updates the horizontal position value.
	 * @param x The new {@code float} value for the {@code x} coordinate.
	 */
	public void setX(float x)
	{
		this.x = x;
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
	 * Sets the {@code y} coordinate of this point.<br>
	 * This updates the vertical position value.
	 * @param y The new {@code float} value for the {@code y} coordinate.
	 */
	public void setY(float y)
	{
		this.y = y;
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
	 * Sets the vertical coordinate of this point.<br>
	 * This updates the {@code z} value to the provided amount.
	 * @param z The new {@code float} value for the vertical position.
	 */
	public void setZ(float z)
	{
		this.z = z;
	}
	
	/**
	 * Compares this {@link Point3D} object with another object for equality.<br>
	 * It checks if both objects have the same x, y, and z coordinates.
	 * @param o The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		
		if (!(o instanceof Point3D))
		{
			return false;
		}
		
		final Point3D point3D = (Point3D) o;
		
		return (x == point3D.x) && (y == point3D.y) && (z == point3D.z);
	}
	
	/**
	 * Returns a hash code value for this {@link Point3D} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code x}, {@code y}, and {@code z} fields.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		float result = x;
		result = (31 * result) + y;
		result = (31 * result) + z;
		return (int) (result * 100);
	}
	
	/**
	 * Creates a new copy of this {@link Point3D} object.<br>
	 * The new object has the same coordinates as the original.
	 * @return A new {@code Point3D} instance.
	 */
	@Override
	public Point3D clone()
	{
		return new Point3D(this);
	}
	
	/**
	 * Returns a string representation of the {@code Point3D}.<br>
	 * It displays the class name and the values of the x, y, and z coordinates.
	 * @return A formatted string representing this point.
	 */
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("Point3D");
		sb.append("{x=").append(x);
		sb.append(", y=").append(y);
		sb.append(", z=").append(z);
		sb.append('}');
		return sb.toString();
	}
}
