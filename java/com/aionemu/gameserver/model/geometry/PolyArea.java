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

import java.util.Collection;

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.model.templates.zone.Point2D;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Represents a free-form area defined by multiple points.<br>
 * This class extends {@link AbstractArea} to handle complex geometric shapes.
 * @author SoulKeeper
 */
public class PolyArea extends AbstractArea
{
	/**
	 * Polygon used to calculate isInside()
	 */
	private final Polygon2D poly;
	
	/**
	 * Creates a new {@link PolyArea} instance using a collection of coordinates.<br>
	 * This method defines the boundaries of a free-form area in the game world.
	 * @param zoneName The name of the zone where this area is located.
	 * @param worldId The unique identifier for the world.
	 * @param points A {@code Collection} of {@link Point2D} objects defining the polygon shape.
	 * @param zMin The minimum height value for the area.
	 * @param zMax The maximum height value for the area.
	 */
	public PolyArea(ZoneName zoneName, int worldId, Collection<Point2D> points, float zMin, float zMax)
	{
		this(zoneName, worldId, points.toArray(new Point2D[points.size()]), zMin, zMax);
	}
	
	/**
	 * Creates a new {@link PolyArea} using an array of coordinates.<br>
	 * This constructor builds a polygon from the provided points.<br>
	 * It requires at least 3 points to form a valid shape.
	 * @param zoneName The name of the zone where this area is located.
	 * @param worldId The unique identifier for the world.
	 * @param points An array of {@link Point2D} objects defining the polygon shape.
	 * @param zMin The minimum height value for this area.
	 * @param zMax The maximum height value for this area.
	 */
	public PolyArea(ZoneName zoneName, int worldId, Point2D[] points, float zMin, float zMax)
	{
		super(zoneName, worldId, zMin, zMax);
		
		if (points.length < 3)
		{
			throw new IllegalArgumentException("Not enough points, needed at least 3 but got " + points.length);
		}
		
		final float[] xPoints = new float[points.length];
		final float[] yPoints = new float[points.length];
		
		for (int i = 0, n = points.length; i < n; i++)
		{
			final Point2D p = points[i];
			xPoints[i] = p.getX();
			yPoints[i] = p.getY();
		}
		
		poly = new Polygon2D(xPoints, yPoints, points.length);
	}
	
	/**
	 * Checks if a 2D point is inside this polygon area.<br>
	 * This method ignores the {@code z} coordinate.<br>
	 * It returns {@code true} if the point is within the boundaries.
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 * @return {@code true} if the point is inside, otherwise {@code false}.
	 */
	@Override
	public boolean isInside2D(float x, float y)
	{
		return poly.contains(x, y);
	}
	
	/**
	 * Calculates the distance from a point to the edge of this area.<br>
	 * It returns {@code 0} if the point is inside the area.<br>
	 * Otherwise, it returns the distance between the point and the closest point on the boundary.
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
		return MathUtil.getDistance(cp.getX(), cp.getY(), x, y);
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
			return MathUtil.getDistance(cp.getX(), cp.getY(), cp.getZ(), x, y, z);
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
		Point2D closestPoint = null;
		double closestDistance = 0;
		for (int i = 0; i < poly.xpoints.length; i++)
		{
			int nextIndex = i + 1;
			if (nextIndex == poly.xpoints.length)
			{
				nextIndex = 0;
			}
			
			final float p1x = poly.xpoints[i];
			final float p1y = poly.ypoints[i];
			final float p2x = poly.xpoints[nextIndex];
			final float p2y = poly.ypoints[nextIndex];
			
			final Point2D point = MathUtil.getClosestPointOnSegment(p1x, p1y, p2x, p2y, x, y);
			
			if (closestPoint == null)
			{
				closestPoint = point;
				closestDistance = MathUtil.getDistance(closestPoint.getX(), closestPoint.getY(), x, y);
			}
			else
			{
				final double newDistance = MathUtil.getDistance(point.getX(), point.getY(), x, y);
				if (newDistance < closestDistance)
				{
					closestPoint = point;
					closestDistance = newDistance;
				}
			}
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
		if ((area.getMinZ() > getMaxZ()) || (area.getMaxZ() < getMinZ()))
		{
			return false;
		}
		
		return poly.intersects(area.getMinX(), area.getMinY(), WorldConfig.WORLD_REGION_SIZE, WorldConfig.WORLD_REGION_SIZE);
	}
}
