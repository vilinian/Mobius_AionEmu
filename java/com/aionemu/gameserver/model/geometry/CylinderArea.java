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

import com.aionemu.gameserver.model.templates.zone.Point2D;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Represents a cylindrical geometric area in the game world.<br>
 * It extends {@link AbstractArea} to provide specific spatial logic for cylinders.
 * @author SoulKeeper
 */
public class CylinderArea extends AbstractArea
{
	/**
	 * Center of cylinder
	 */
	private final float centerX;
	/**
	 * Center of cylinder
	 */
	private final float centerY;
	/**
	 * Cylinder radius
	 */
	private final float radius;
	
	/**
	 * Creates a new {@link CylinderArea} using a {@code Point2D} for the center.<br>
	 * This constructor initializes the cylinder with specific spatial boundaries.
	 * @param zoneName The name of the zone where the area is located.
	 * @param worldId The unique identifier for the world.
	 * @param center The {@code Point2D} representing the horizontal center of the cylinder.
	 * @param radius The radius of the cylinder.
	 * @param minZ The minimum height value of the cylinder.
	 * @param maxZ The maximum height value of the cylinder.
	 */
	public CylinderArea(ZoneName zoneName, int worldId, Point2D center, float radius, float minZ, float maxZ)
	{
		this(zoneName, worldId, center.getX(), center.getY(), radius, minZ, maxZ);
	}
	
	/**
	 * Creates a new {@link CylinderArea} using specific coordinates.<br>
	 * This constructor defines the center point and size of the cylinder.<br>
	 * It initializes the area within a specific zone and world.
	 * @param zoneName The name of the zone where the area is located.
	 * @param worldId The unique identifier for the world.
	 * @param x The X coordinate of the center point.
	 * @param y The Y coordinate of the center point.
	 * @param radius The radius of the cylinder.
	 * @param minZ The minimum Z coordinate boundary.
	 * @param maxZ The maximum Z coordinate boundary.
	 */
	public CylinderArea(ZoneName zoneName, int worldId, float x, float y, float radius, float minZ, float maxZ)
	{
		super(zoneName, worldId, minZ, maxZ);
		centerX = x;
		centerY = y;
		this.radius = radius;
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
		return MathUtil.getDistance(centerX, centerY, x, y) < radius;
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
		
		return Math.abs(MathUtil.getDistance(centerX, centerY, x, y) - radius);
	}
	
	/**
	 * Calculates the shortest distance from a 3D point to this cylinder area.<br>
	 * It returns {@code 0.0} if the point is inside the area.
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 * @param z The z-coordinate of the point.
	 * @return The distance as a {@code double}.
	 */
	@Override
	public double getDistance3D(float x, float y, float z)
	{
		if (isInside3D(x, y, z))
		{
			return 0;
		}
		else if (isInsideZ(z))
		{
			return getDistance2D(x, y);
		}
		else
		{
			if (z < getMinZ())
			{
				return MathUtil.getDistance(centerX, centerY, getMinZ(), x, y, z);
			}
			
			return MathUtil.getDistance(centerX, centerY, getMaxZ(), x, y, z);
		}
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
		
		final float vX = x - centerX;
		final float vY = y - centerY;
		final double magV = MathUtil.getDistance(centerX, centerY, x, y);
		final double pointX = centerX + ((vX / magV) * radius);
		final double pointY = centerY + ((vY / magV) * radius);
		return new Point2D((float) pointX, (float) pointY);
	}
	
	/**
	 * Checks if this cylinder area overlaps with a {@link RectangleArea}.<br>
	 * It compares the vertical bounds and the 2D distance.
	 * @param area The {@code RectangleArea} to check against.
	 * @return {@code true} if the areas intersect, otherwise {@code false}.
	 */
	@Override
	public boolean intersectsRectangle(RectangleArea area)
	{
		if ((area.getMinZ() > getMaxZ()) || (area.getMaxZ() < getMinZ()))
		{
			return false;
		}
		
		if (area.getDistance2D(centerX, centerY) < radius)
		{
			return true;
		}
		
		return false;
	}
}
