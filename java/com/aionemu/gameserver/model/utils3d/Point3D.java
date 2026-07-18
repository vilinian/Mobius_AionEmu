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
package com.aionemu.gameserver.model.utils3d;

/**
 * Represents a point in 3D space using {@code x}, {@code y}, and {@code z} coordinates.<br>
 * This class is used for basic spatial calculations within the game world.
 * @author M@xx modified by Wakizashi
 */
public class Point3D
{
	public double x;
	public double y;
	public double z;
	
	/**
	 * Creates a new {@link Point3D} instance.<br>
	 * All coordinates are initialized to {@code 0.0f}.
	 */
	public Point3D()
	{
		x = 0.0;
		y = 0.0;
		z = 0.0;
	}
	
	/**
	 * Creates a new {@link Point3D} instance.<br>
	 * This constructor sets the coordinates for the point.
	 * @param x The coordinate for the x-axis.
	 * @param y The coordinate for the y-axis.
	 * @param z The coordinate for the z-axis.
	 */
	public Point3D(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
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
	 * Calculates the straight-line distance between this point and another.<br>
	 * It uses the {@code Point3D} coordinates to find the Euclidean distance.
	 * @param p The target {@link Point3D} to measure the distance to.
	 * @return The distance as a {@code double}.
	 */
	public double distance(Point3D p)
	{
		final double dx = x - p.x;
		final double dy = y - p.y;
		final double dz = z - p.z;
		return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}
	
	/**
	 * Returns a string representation of this {@code Point3D}.<br>
	 * It displays the {@code x}, {@code y}, and {@code z} coordinates.
	 * @return A formatted string containing the coordinate values.
	 */
	@Override
	public String toString()
	{
		return "x=" + x + ", y=" + y + ", z=" + z;
	}
	
	/**
	 * Retrieves the current X coordinate.<br>
	 * This value represents the position on the horizontal axis.
	 * @return The {@code double} value of the X coordinate.
	 */
	public double getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the current Y coordinate.<br>
	 * This value represents the height in a {@code Point3D}.
	 * @return The value of the {@code y} field as a {@code double}.
	 */
	public double getY()
	{
		return y;
	}
	
	/**
	 * Retrieves the {@code z} coordinate of this {@link Point3D}.<br>
	 * This value represents the height in a 3D space.
	 * @return The current value of the {@code z} field as a {@code double}.
	 */
	public double getZ()
	{
		return z;
	}
}
