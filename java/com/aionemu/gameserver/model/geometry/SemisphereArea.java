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

import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Represents a geometric area shaped like a hemisphere.<br>
 * This class extends {@link SphereArea} to provide specific spatial calculations for half-sphere regions.
 * @author Rolandas
 */
public class SemisphereArea extends SphereArea
{
	/**
	 * Creates a new {@link SemisphereArea} instance.<br>
	 * This constructor initializes the area with specific spatial coordinates and dimensions.
	 * @param zoneName The name of the zone where this area is located.
	 * @param worldId The unique identifier for the game world.
	 * @param x The X coordinate of the center point.
	 * @param y The Y coordinate of the center point.
	 * @param z The Z coordinate of the center point.
	 * @param r The radius of the hemisphere.
	 */
	public SemisphereArea(ZoneName zoneName, int worldId, float x, float y, float z, float r)
	{
		super(zoneName, worldId, x, y, z, r);
	}
	
	/**
	 * Checks if a specific 3D coordinate is within the boundaries of this area.<br>
	 * This method validates both the horizontal and vertical positions.<br>
	 * It calls {@code float, float)} internally.
	 * @param point The {@code Point3D} object to check.
	 * @return {@code true} if the point is inside the area, otherwise {@code false}.
	 */
	@Override
	public boolean isInside3D(Point3D point)
	{
		return (z < point.getZ()) && MathUtil.isIn3dRange(x, y, z, point.getX(), point.getY(), point.getZ(), r);
	}
	
	/**
	 * Checks if a specific point in 3D space is within this area.<br>
	 * It validates the {@code x}, {@code y}, and {@code z} coordinates.
	 * @param x The horizontal coordinate.
	 * @param y The vertical coordinate.
	 * @param z The depth coordinate.
	 * @return {@code true} if the point is inside the area, otherwise {@code false}.
	 */
	@Override
	public boolean isInside3D(float x, float y, float z)
	{
		return (this.z < z) && MathUtil.isIn3dRange(x, y, z, this.x, this.y, this.z, r);
	}
	
	/**
	 * Checks if the vertical position of a point is within this area.<br>
	 * This method uses {@code isInsideZ} to validate the height.
	 * @param point The {@code Point3D} object to check.
	 * @return {@code true} if the Z coordinate is inside the range, {@code false} otherwise.
	 */
	@Override
	public boolean isInsideZ(Point3D point)
	{
		return isInsideZ(point.getZ());
	}
	
	/**
	 * Retrieves the minimum Z coordinate of the area.<br>
	 * This value represents the lowest vertical bound.
	 * @return The {@code float} value of the minimum Z.
	 */
	@Override
	public float getMinZ()
	{
		return z;
	}
	
	/**
	 * Retrieves the maximum height of the area.<br>
	 * This value represents the highest {@code z} coordinate.
	 * @return The maximum {@code float} value for the {@code z} axis.
	 */
	@Override
	public float getMaxZ()
	{
		return z + r;
	}
	
	/**
	 * Calculates the distance between this area and a given 3D point.<br>
	 * This method uses the {@code x}, {@code y}, and {@code z} coordinates.<br>
	 * It returns the straight-line distance as a {@code double}.
	 * @param point The {@link Point3D} to measure from.
	 * @return The calculated distance to the provided point.
	 */
	@Override
	public double getDistance3D(Point3D point)
	{
		return getDistance3D(point.getX(), point.getY(), point.getZ());
	}
	
	/**
	 * Calculates the shortest distance from a 3D point to this hemisphere area.<br>
	 * It returns {@code 0.0} if the point is inside the area.
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 * @param z The z-coordinate of the point.
	 * @return The distance as a {@code double}.
	 */
	@Override
	public double getDistance3D(float x, float y, float z)
	{
		final double distance = MathUtil.getDistance(x, y, z, this.x, this.y, this.z) - r;
		if (z < this.z)
		{
			return distance;
		}
		
		return distance > 0 ? distance : 0;
	}
	
	/**
	 * Checks if this {@link SemisphereArea} overlaps with a {@link RectangleArea}.<br>
	 * It compares the vertical bounds and the 3D distance.
	 * @param area The {@code RectangleArea} to check against.
	 * @return {@code true} if the areas intersect, otherwise {@code false}.
	 */
	@Override
	public boolean intersectsRectangle(RectangleArea area)
	{
		if (((area.getMaxZ() >= z) || (z <= area.getMinZ())) && (area.getDistance3D(x, y, z) <= r))
		{
			return true;
		}
		
		return false;
	}
}
