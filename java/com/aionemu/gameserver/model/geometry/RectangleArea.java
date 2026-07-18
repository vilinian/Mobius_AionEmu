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

import java.awt.Point;
import java.awt.Rectangle;

import com.aionemu.gameserver.model.templates.zone.Point2D;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Represents a rectangular geographic area within the game world.<br>
 * This is the most widely used type of area for defining spatial boundaries.
 * @author SoulKeeper
 */
public class RectangleArea extends AbstractArea
{
	/**
	 * Min x point
	 */
	private final float minX;
	
	/**
	 * Retrieves the minimum X coordinate of this {@link RectangleArea}.<br>
	 * This value represents the leftmost boundary of the area.
	 * @return The smallest x-coordinate as a {@code float}.
	 */
	public float getMinX()
	{
		return minX;
	}
	
	/**
	 * Retrieves the maximum X coordinate of this {@code RectangleArea}.<br>
	 * This value represents the rightmost boundary of the area.
	 * @return The maximum X coordinate as a {@code float}.
	 */
	public float getMaxX()
	{
		return maxX;
	}
	
	/**
	 * Gets the minimum Y coordinate of this {@link RectangleArea}.<br>
	 * This value represents the bottom edge of the rectangle.
	 * @return the smallest Y value as a {@code float}.
	 */
	public float getMinY()
	{
		return minY;
	}
	
	/**
	 * Retrieves the maximum Y coordinate of this {@code RectangleArea}.<br>
	 * This value represents the upper boundary on the vertical axis.
	 * @return The maximum Y coordinate as a {@code float}.
	 */
	public float getMaxY()
	{
		return maxY;
	}
	
	/**
	 * Max x point
	 */
	private final float maxX;
	/**
	 * Min y point
	 */
	private final float minY;
	/**
	 * Max y point
	 */
	private final float maxY;
	
	/**
	 * Creates a new {@link RectangleArea} using four corner points.<br>
	 * This method calculates the boundaries based on the provided coordinates.
	 * @param zoneName The name of the game zone.
	 * @param worldId The unique identifier for the world.
	 * @param p1 The first corner point.
	 * @param p2 The second corner point.
	 * @param p3 The third corner point.
	 * @param p4 The fourth corner point.
	 * @param minZ The minimum height value.
	 * @param maxZ The maximum height value.
	 */
	public RectangleArea(ZoneName zoneName, int worldId, Point p1, Point p2, Point p3, Point p4, int minZ, int maxZ)
	{
		super(zoneName, worldId, minZ, maxZ);
		
		final Rectangle r = new Rectangle();
		r.add(p1);
		r.add(p2);
		r.add(p3);
		r.add(p4);
		
		minX = (int) r.getMinX();
		maxX = (int) r.getMaxX();
		minY = (int) r.getMinY();
		maxY = (int) r.getMaxY();
	}
	
	/**
	 * Creates a new {@link RectangleArea} using specific coordinate boundaries.<br>
	 * This constructor defines the 2D bounds and height of the area.
	 * @param zoneName The name of the zone associated with this area.
	 * @param worldId The unique identifier for the game world.
	 * @param minX The minimum X coordinate.
	 * @param minY The minimum Y coordinate.
	 * @param maxX The maximum X coordinate.
	 * @param maxY The maximum Y coordinate.
	 * @param minZ The minimum Z coordinate.
	 * @param maxZ The maximum Z coordinate.
	 */
	public RectangleArea(ZoneName zoneName, int worldId, float minX, float minY, float maxX, float maxY, float minZ, float maxZ)
	{
		super(zoneName, worldId, minZ, maxZ);
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}
	
	/**
	 * Checks if a 2D point is within the rectangle area.<br>
	 * This method ignores the {@code z} coordinate.<br>
	 * It returns {@code true} if the point is inside the area.
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 * @return {@code true} if the point is inside, otherwise {@code false}.
	 */
	@Override
	public boolean isInside2D(float x, float y)
	{
		return (x >= minX) && (x <= maxX) && (y >= minY) && (y <= maxY);
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
		if (!isInside2D(x, y))
		{
			return false;
		}
		
		return super.isInside3D(x, y, z);
	}
	
	/**
	 * Calculates the distance from a point to the edge of this area.<br>
	 * It returns {@code 0} if the point is inside the area.<br>
	 * Otherwise, it returns the distance to the closest point on the boundary.
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
		
		final Point2D cp = getClosestPoint(x, y);
		return MathUtil.getDistance(x, y, cp.getX(), cp.getY());
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
			final Point3D cp = getClosestPoint(x, y, z);
			return MathUtil.getDistance(x, y, z, cp.getX(), cp.getY(), cp.getZ());
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
		
		// bottom edge
		Point2D closestPoint = MathUtil.getClosestPointOnSegment(minX, minY, maxX, minY, x, y);
		double distance = MathUtil.getDistance(x, y, closestPoint.getX(), closestPoint.getY());
		
		// top edge
		Point2D cp = MathUtil.getClosestPointOnSegment(minX, maxY, maxX, maxY, x, y);
		double d = MathUtil.getDistance(x, y, cp.getX(), cp.getY());
		if (d < distance)
		{
			closestPoint = cp;
			distance = d;
		}
		
		// left edge
		cp = MathUtil.getClosestPointOnSegment(minX, minY, minX, maxY, x, y);
		d = MathUtil.getDistance(x, y, cp.getX(), cp.getY());
		if (d < distance)
		{
			closestPoint = cp;
			distance = d;
		}
		
		// Right edge
		cp = MathUtil.getClosestPointOnSegment(maxX, minY, maxX, maxY, x, y);
		d = MathUtil.getDistance(x, y, cp.getX(), cp.getY());
		if (d < distance)
		{
			closestPoint = cp;
			// distance = d;
		}
		
		return closestPoint;
	}
	
	/**
	 * Checks if this area overlaps with a {@link RectangleArea}.<br>
	 * It compares the vertical bounds and the 2D distance.
	 * @param area The {@code RectangleArea} to check against.
	 * @return {@code true} if the areas intersect, otherwise {@code false}.
	 */
	@Override
	public boolean intersectsRectangle(RectangleArea area)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
