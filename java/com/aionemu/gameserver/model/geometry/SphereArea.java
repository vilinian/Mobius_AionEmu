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

import com.aionemu.gameserver.model.templates.base.BaseTemplate;
import com.aionemu.gameserver.model.templates.zone.Point2D;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Represents a spherical area in the game world.<br>
 * This class is used to define a 3D region based on a center point and a radius.<br>
 * It allows for spatial queries and collision checks within a specific volume.
 * @author MrPoke
 */
public class SphereArea implements Area
{
	protected float x;
	protected float y;
	protected float z;
	protected float r;
	protected int worldId;
	protected ZoneName zoneName;
	
	/**
	 * Creates a new {@link SphereArea} instance.<br>
	 * This method initializes the sphere with its position, radius, and location data.
	 * @param zoneName The name of the zone where the area is located.
	 * @param worldId The unique identifier for the world.
	 * @param x The X coordinate of the center point.
	 * @param y The Y coordinate of the center point.
	 * @param z The Z coordinate of the center point.
	 * @param r The radius of the sphere.
	 */
	public SphereArea(ZoneName zoneName, int worldId, float x, float y, float z, float r)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.worldId = worldId;
		this.zoneName = zoneName;
	}
	
	/**
	 * Checks if a 2D coordinate is within the boundaries of this area.<br>
	 * This method ignores the Z-axis height.
	 * @param point The {@link Point2D} to check.
	 * @return {@code true} if the point is inside, {@code false} otherwise.
	 */
	@Override
	public boolean isInside2D(Point2D point)
	{
		return isInside2D(point.getX(), point.getY());
	}
	
	/**
	 * Checks if a 2D point is within the cylinder's radius.<br>
	 * This method ignores the {@code z} coordinate.<br>
	 * It returns {@code true} if the point is inside the area.
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 * @return {@code true} if the point is inside, otherwise {@code false}.
	 */
	@Override
	public boolean isInside2D(float x, float y)
	{
		return MathUtil.getDistance(this.x, this.y, x, y) < r;
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
		return MathUtil.isIn3dRange(x, y, z, point.getX(), point.getY(), point.getZ(), r);
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
		return MathUtil.isIn3dRange(x, y, z, this.x, this.y, this.z, r);
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
	 * Checks if a specific height is within the area boundaries.<br>
	 * It compares the value against {@code getMinZ} and {@code getMaxZ}.
	 * @param z The height value to check.
	 * @return {@code true} if the value is between the min and max limits, otherwise {@code false}.
	 */
	@Override
	public boolean isInsideZ(float z)
	{
		return (z >= getMinZ()) && (z <= getMaxZ());
	}
	
	/**
	 * Calculates the distance between this area and a given {@code Point2D}.<br>
	 * This method uses only the X and Y coordinates for the calculation.
	 * @param point The {@code Point2D} to measure from.
	 * @return The distance as a {@code double}.
	 */
	@Override
	public double getDistance2D(Point2D point)
	{
		return getDistance2D(point.getX(), point.getY());
	}
	
	/**
	 * Calculates the distance from a point to the edge of this cylinder.<br>
	 * It returns {@code 0} if the point is inside the area.<br>
	 * Otherwise, it returns the difference between the distance to the center and the radius.
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 * @return The calculated distance as a {@code double}.
	 */
	@Override
	public double getDistance2D(float x, float y)
	{
		if (isInside2D(x, y))
		{
			return 0;
		}
		
		return Math.abs(MathUtil.getDistance(this.x, this.y, x, y) - r);
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
	 * Calculates the shortest distance from a 3D point to the surface of this sphere.<br>
	 * It returns {@code 0.0} if the point is inside or on the boundary of the area.
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 * @param z The z-coordinate of the point.
	 * @return The distance as a {@code double}.
	 */
	@Override
	public double getDistance3D(float x, float y, float z)
	{
		final double distance = MathUtil.getDistance(x, y, z, this.x, this.y, this.z) - r;
		return distance > 0 ? distance : 0;
	}
	
	/**
	 * Finds the nearest point on this area's boundary.<br>
	 * It takes a 2D coordinate as input.<br>
	 * The result is calculated based on the {@code Point2D} provided.
	 * @param point The {@code Point2D} to check.
	 * @return The closest {@link Point2D} on the area boundary.
	 */
	@Override
	public Point2D getClosestPoint(Point2D point)
	{
		return getClosestPoint(point.getX(), point.getY());
	}
	
	/**
	 * Finds the point on the boundary of this area closest to the given coordinates.<br>
	 * If the coordinates are already inside the area, it returns those coordinates.<br>
	 * Otherwise, it calculates the nearest point on the edge.
	 * @param x The x-coordinate of the target point.
	 * @param y The y-coordinate of the target point.
	 * @return A {@link Point2D} representing the closest location.
	 */
	@Override
	public Point2D getClosestPoint(float x, float y)
	{
		if (isInside2D(x, y))
		{
			return new Point2D(x, y);
		}
		
		final float vX = x - this.x;
		final float vY = y - this.y;
		final double magV = MathUtil.getDistance(this.x, this.y, x, y);
		final double pointX = this.x + ((vX / magV) * r);
		final double pointY = this.y + ((vY / magV) * r);
		return new Point2D((float) pointX, (float) pointY);
	}
	
	/**
	 * Finds the nearest point on this area to a given location.<br>
	 * It calculates the closest {@code Point3D} based on the area boundaries.
	 * @param point The target {@link Point3D} to check.
	 * @return The closest {@link Point3D} within or on the boundary of this area.
	 */
	@Override
	public Point3D getClosestPoint(Point3D point)
	{
		return null;
	}
	
	/**
	 * Finds the point on this area nearest to the given coordinates.<br>
	 * It calculates the closest 2D position and clamps the {@code z} value.
	 * @param x The x coordinate of the target point.
	 * @param y The y coordinate of the target point.
	 * @param z The z coordinate of the target point.
	 * @return A new {@link Point3D} representing the closest location.
	 */
	@Override
	public Point3D getClosestPoint(float x, float y, float z)
	{
		return null;
	}
	
	/**
	 * Retrieves the minimum Z coordinate of the area.<br>
	 * This value represents the lowest vertical bound.
	 * @return The {@code float} value of the minimum Z.
	 */
	@Override
	public float getMinZ()
	{
		return z - r;
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
	 * Checks if this sphere area overlaps with a {@link RectangleArea}.<br>
	 * It compares the 3D distance between the centers.
	 * @param area The {@code RectangleArea} to check against.
	 * @return {@code true} if the areas intersect, otherwise {@code false}.
	 */
	@Override
	public boolean intersectsRectangle(RectangleArea area)
	{
		if (area.getDistance3D(x, y, z) <= r)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	@Override
	public int getWorldId()
	{
		return worldId;
	}
	
	/**
	 * Retrieves the name of the zone associated with this area.<br>
	 * This method returns the {@code ZoneName} object stored in the instance.
	 * @return the {@link ZoneName} of the current area
	 */
	@Override
	public ZoneName getZoneName()
	{
		return zoneName;
	}
}
