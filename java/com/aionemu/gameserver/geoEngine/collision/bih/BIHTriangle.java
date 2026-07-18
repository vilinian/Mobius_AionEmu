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
package com.aionemu.gameserver.geoEngine.collision.bih;

import com.aionemu.gameserver.geoEngine.math.FastMath;
import com.aionemu.gameserver.geoEngine.math.Vector3f;

/**
 * Represents a single triangle within a Bounding Interval Hierarchy (BIH) structure.<br>
 * This class is used to perform efficient collision detection by defining the spatial boundaries of geometry.<br>
 * It stores the vertices and bounding information for individual triangles in the {@code BIH} tree.
 */
public final class BIHTriangle
{
	private final Vector3f pointa = new Vector3f();
	private final Vector3f pointb = new Vector3f();
	private final Vector3f pointc = new Vector3f();
	private final Vector3f center = new Vector3f();
	
	/**
	 * Creates a new {@link BIHTriangle} using three vertices.<br>
	 * This constructor initializes the triangle points and calculates its center.
	 * @param p1 The first vertex of the triangle as a {@code Vector3f}.
	 * @param p2 The second vertex of the triangle as a {@code Vector3f}.
	 * @param p3 The third vertex of the triangle as a {@code Vector3f}.
	 */
	public BIHTriangle(Vector3f p1, Vector3f p2, Vector3f p3)
	{
		pointa.set(p1);
		pointb.set(p2);
		pointc.set(p3);
		center.set(pointa);
		center.addLocal(pointb).addLocal(pointc).multLocal(FastMath.ONE_THIRD);
	}
	
	/**
	 * Retrieves the first vertex of this triangle.<br>
	 * This returns the {@code pointa} coordinate.
	 * @return the {@link Vector3f} representing the first point.
	 */
	public Vector3f get1()
	{
		return pointa;
	}
	
	/**
	 * Retrieves the second vertex of this triangle.<br>
	 * This method returns the {@code Vector3f} object stored in {@code pointb}.
	 * @return The second vertex as a {@link Vector3f}.
	 */
	public Vector3f get2()
	{
		return pointb;
	}
	
	/**
	 * Retrieves the third vertex of this triangle.<br>
	 * This method returns the {@code Vector3f} object stored in {@code pointc}.
	 * @return The third vertex as a {@link Vector3f}.
	 */
	public Vector3f get3()
	{
		return pointc;
	}
	
	/**
	 * Retrieves the center position of this bounding volume.<br>
	 * This method returns the {@code Vector3f} representing the middle point.
	 * @return The center of the volume as a {@link Vector3f}.
	 */
	public Vector3f getCenter()
	{
		return center;
	}
	
	/**
	 * Calculates the surface normal of this triangle.<br>
	 * This method uses the cross product of two edges.<br>
	 * The resulting vector is normalized to a length of 1.0.
	 * @return The {@code Vector3f} representing the unit normal.
	 */
	public Vector3f getNormal()
	{
		final Vector3f normal = new Vector3f(pointb);
		normal.subtractLocal(pointa).crossLocal(pointc.x - pointa.x, pointc.y - pointa.y, pointc.z - pointa.z);
		normal.normalizeLocal();
		return normal;
	}
	
	/**
	 * Retrieves the minimum or maximum coordinate of the triangle.<br>
	 * This method finds the boundary value along a specific axis.
	 * @param axis The index of the axis to check (0 for x, 1 for y, 2 for z).
	 * @param left Set to {@code true} to get the minimum value, or {@code false} to get the maximum.
	 * @return The extreme coordinate as a {@code float}.
	 */
	public float getExtreme(int axis, boolean left)
	{
		float v1, v2, v3;
		switch (axis)
		{
			case 0:
				v1 = pointa.x;
				v2 = pointb.x;
				v3 = pointc.x;
				break;
			case 1:
				v1 = pointa.y;
				v2 = pointb.y;
				v3 = pointc.y;
				break;
			case 2:
				v1 = pointa.z;
				v2 = pointb.z;
				v3 = pointc.z;
				break;
			default:
				assert false;
				return 0;
		}
		
		if (left)
		{
			if (v1 < v2)
			{
				if (v1 < v3)
				{
					return v1;
				}
				
				return v3;
			}
			
			if (v2 < v3)
			{
				return v2;
			}
			
			return v3;
		}
		
		if (v1 > v2)
		{
			if (v1 > v3)
			{
				return v1;
			}
			
			return v3;
		}
		
		if (v2 > v3)
		{
			return v2;
		}
		
		return v3;
	}
}
