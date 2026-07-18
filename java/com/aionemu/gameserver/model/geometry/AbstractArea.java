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
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Provides basic method implementations for area-related geometry.<br>
 * This class is intended to be subclassed whenever possible.<br>
 * If a specific implementation is not needed, {@link com.aionemu.gameserver.model.geometry.Area} should be implemented directly.
 */
public abstract class AbstractArea implements Area
{
	/**
	 * Minimal z of area
	 */
	private final float minZ;
	/**
	 * Maximal Z of area
	 */
	private final float maxZ;
	private final ZoneName zoneName;
	private final int worldId;
	
	/**
	 * Creates a new instance of an {@link AbstractArea}.<br>
	 * This constructor initializes the area with specific spatial boundaries.<br>
	 * It validates that {@code minZ} is not greater than {@code maxZ}.
	 * @param zoneName The name of the zone associated with this area.
	 * @param worldId The unique identifier for the world.
	 * @param minZ The minimum vertical coordinate.
	 * @param maxZ The maximum vertical coordinate.
	 */
	protected AbstractArea(ZoneName zoneName, int worldId, float minZ, float maxZ)
	{
		if (minZ > maxZ)
		{
			throw new IllegalArgumentException("minZ(" + minZ + ") > maxZ(" + maxZ + ")");
		}
		
		this.minZ = minZ;
		this.maxZ = maxZ;
		this.zoneName = zoneName;
		this.worldId = worldId;
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
	 * Checks if a specific 3D coordinate is within the boundaries of this area.<br>
	 * This method validates both the horizontal and vertical positions.<br>
	 * It calls {@code float, float)} internally.
	 * @param point The {@code Point3D} object to check.
	 * @return {@code true} if the point is inside the area, otherwise {@code false}.
	 */
	@Override
	public boolean isInside3D(Point3D point)
	{
		return isInside3D(point.getX(), point.getY(), point.getZ());
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
		return isInsideZ(z) && isInside2D(x, y);
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
	 * Calculates the distance between this area and a given 2D point.<br>
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
	 * Finds the nearest point on this area to a given location.<br>
	 * It calculates the closest {@code Point3D} based on the area boundaries.
	 * @param point The target {@link Point3D} to check.
	 * @return The closest {@link Point3D} within or on the boundary of this area.
	 */
	@Override
	public Point3D getClosestPoint(Point3D point)
	{
		return getClosestPoint(point.getX(), point.getY(), point.getZ());
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
		final Point2D closest2d = getClosestPoint(x, y);
		
		float zCoord;
		
		if (isInsideZ(z))
		{
			zCoord = z;
		}
		else if (z < getMinZ())
		{
			zCoord = getMinZ();
		}
		else
		{
			zCoord = getMaxZ();
		}
		
		return new Point3D(closest2d.getX(), closest2d.getY(), zCoord);
	}
	
	/**
	 * Retrieves the minimum Z coordinate of the area.<br>
	 * This value represents the lowest vertical bound.
	 * @return The {@code float} value of the minimum Z.
	 */
	@Override
	public float getMinZ()
	{
		return minZ;
	}
	
	/**
	 * Retrieves the maximum height of the area.<br>
	 * This value represents the highest {@code z} coordinate.
	 * @return The maximum {@code float} value for the {@code z} axis.
	 */
	@Override
	public float getMaxZ()
	{
		return maxZ;
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
