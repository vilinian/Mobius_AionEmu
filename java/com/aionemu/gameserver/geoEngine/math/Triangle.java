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
package com.aionemu.gameserver.geoEngine.math;

/**
 * Represents a geometric triangle in 3D space.<br>
 * This class stores the information for a triangle defined by three {@link Vector3f} objects.
 * @author Mark Powell
 * @author Joshua Slack
 */
public class Triangle extends AbstractTriangle
{
	private Vector3f pointa = new Vector3f();
	private Vector3f pointb = new Vector3f();
	private Vector3f pointc = new Vector3f();
	private transient Vector3f center;
	private transient Vector3f normal;
	private float projection;
	private int index;
	
	/**
	 * Creates a new instance of the {@link Triangle} class.<br>
	 * This constructor initializes a default triangle with empty coordinates.
	 */
	public Triangle()
	{
	}
	
	/**
	 * Creates a new {@link Triangle} using three specific points.<br>
	 * This constructor initializes the internal vertices of the triangle.
	 * @param p1 The first vertex of the triangle as a {@code Vector3f}.
	 * @param p2 The second vertex of the triangle as a {@code Vector3f}.
	 * @param p3 The third vertex of the triangle as a {@code Vector3f}.
	 */
	public Triangle(Vector3f p1, Vector3f p2, Vector3f p3)
	{
		pointa.set(p1);
		pointb.set(p2);
		pointc.set(p3);
	}
	
	/**
	 * Retrieves a specific vertex of the triangle.<br>
	 * The index corresponds to the points defined in this class.
	 * @param i The index of the point to retrieve. Use {@code 0}, {@code 1}, or {@code 2}.
	 * @return The {@link Vector3f} at the specified index, or {@code null} if the index is invalid.
	 */
	public Vector3f get(int i)
	{
		switch (i)
		{
			case 0:
				return pointa;
			case 1:
				return pointb;
			case 2:
				return pointc;
			default:
				return null;
		}
	}
	
	/**
	 * Retrieves the first vertex of this triangle.<br>
	 * This returns the {@code pointa} coordinate.
	 * @return the {@link Vector3f} representing the first point.
	 */
	@Override
	public Vector3f get1()
	{
		return pointa;
	}
	
	/**
	 * Retrieves the second vertex of this triangle.<br>
	 * This method returns the {@code Vector3f} object stored in {@code pointb}.
	 * @return The second vertex as a {@link Vector3f}.
	 */
	@Override
	public Vector3f get2()
	{
		return pointb;
	}
	
	/**
	 * Retrieves the third vertex of this triangle.<br>
	 * This method returns the {@code Vector3f} object stored in {@code pointc}.
	 * @return The third vertex as a {@link Vector3f}.
	 */
	@Override
	public Vector3f get3()
	{
		return pointc;
	}
	
	/**
	 * Updates a specific vertex of the triangle.<br>
	 * This method replaces one of the three points based on the provided index.
	 * @param i The index of the point to update. It must be 0, 1, or 2.
	 * @param point The new {@code Vector3f} value for the selected vertex.
	 */
	public void set(int i, Vector3f point)
	{
		switch (i)
		{
			case 0:
				pointa.set(point);
				break;
			case 1:
				pointb.set(point);
				break;
			case 2:
				pointc.set(point);
				break;
		}
	}
	
	/**
	 * Updates the coordinates of a specific vertex in this triangle.<br>
	 * The method sets the {@code x}, {@code y}, and {@code z} values for one of the three points.
	 * @param i The index of the point to update. Must be 0, 1, or 2.
	 * @param x The new x-coordinate.
	 * @param y The new y-coordinate.
	 * @param z The new z-coordinate.
	 */
	public void set(int i, float x, float y, float z)
	{
		switch (i)
		{
			case 0:
				pointa.set(x, y, z);
				break;
			case 1:
				pointb.set(x, y, z);
				break;
			case 2:
				pointc.set(x, y, z);
				break;
		}
	}
	
	/**
	 * Sets the first point of this triangle.<br>
	 * This method updates {@code pointa} using the provided value.
	 * @param v The {@code Vector3f} to set as the first point.
	 */
	public void set1(Vector3f v)
	{
		pointa.set(v);
	}
	
	/**
	 * Sets the second point of the triangle.<br>
	 * This method updates {@code pointb} using the provided {@code Vector3f}.
	 * @param v The new position for the second vertex.
	 */
	public void set2(Vector3f v)
	{
		pointb.set(v);
	}
	
	/**
	 * Sets the third point of the triangle.<br>
	 * This method updates {@code pointc} using the provided {@code Vector3f}.
	 * @param v The new coordinates for the third vertex.
	 */
	public void set3(Vector3f v)
	{
		pointc.set(v);
	}
	
	/**
	 * Sets the three vertices of this triangle.<br>
	 * This method updates {@code pointa}, {@code pointb}, and {@code pointc}.
	 * @param v1 The first vertex position.
	 * @param v2 The second vertex position.
	 * @param v3 The third vertex position.
	 */
	@Override
	public void set(Vector3f v1, Vector3f v2, Vector3f v3)
	{
		pointa.set(v1);
		pointb.set(v2);
		pointc.set(v3);
	}
	
	/**
	 * Calculates the center point of this {@code Triangle}.<br>
	 * It uses the three vertices to find the average position.<br>
	 * The result is stored in the internal {@code center} field.
	 */
	public void calculateCenter()
	{
		if (center == null)
		{
			center = new Vector3f(pointa);
		}
		else
		{
			center.set(pointa);
		}
		
		center.addLocal(pointb).addLocal(pointc).multLocal(FastMath.ONE_THIRD);
	}
	
	/**
	 * Calculates the surface normal of this triangle.<br>
	 * This method updates the {@code normal} field based on the current vertices.<br>
	 * It uses a cross product calculation to determine the direction.
	 */
	public void calculateNormal()
	{
		if (normal == null)
		{
			normal = new Vector3f(pointb);
		}
		else
		{
			normal.set(pointb);
		}
		
		normal.subtractLocal(pointa).crossLocal(pointc.x - pointa.x, pointc.y - pointa.y, pointc.z - pointa.z);
		normal.normalizeLocal();
	}
	
	/**
	 * Gets the center point of this triangle.<br>
	 * If the center has not been calculated yet, it calls {@code calculateCenter}.
	 * @return The center position as a {@link Vector3f}.
	 */
	public Vector3f getCenter()
	{
		if (center == null)
		{
			calculateCenter();
		}
		
		return center;
	}
	
	/**
	 * Sets the center point of this {@link Triangle}.<br>
	 * This method updates the internal {@code center} field.
	 * @param center The new {@code Vector3f} to use as the center.
	 */
	public void setCenter(Vector3f center)
	{
		this.center = center;
	}
	
	/**
	 * Retrieves the surface normal of this triangle.<br>
	 * If the {@code normal} is {@code null}, it calls {@code calculateNormal} to compute it first.
	 * @return The {@code Vector3f} representing the unit normal.
	 */
	public Vector3f getNormal()
	{
		if (normal == null)
		{
			calculateNormal();
		}
		
		return normal;
	}
	
	/**
	 * Sets the normal vector for this {@link Triangle}.<br>
	 * This updates the internal orientation of the triangle.
	 * @param normal The {@code Vector3f} representing the direction perpendicular to the surface.
	 */
	public void setNormal(Vector3f normal)
	{
		this.normal = normal;
	}
	
	/**
	 * Retrieves the current projection value of this {@code Triangle}.<br>
	 * This value is used for spatial calculations.
	 * @return The {@code float} projection value.
	 */
	public float getProjection()
	{
		return projection;
	}
	
	/**
	 * Sets the projection value for this {@code Triangle}.<br>
	 * This updates the internal {@code projection} field.
	 * @param projection The new float value to assign to the triangle.
	 */
	public void setProjection(float projection)
	{
		this.projection = projection;
	}
	
	/**
	 * Retrieves the current index of this {@code Triangle}.<br>
	 * This value is used to identify the triangle in a collection.
	 * @return The integer value of the {@code index}.
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 * Sets the unique identifier for this {@code Triangle}.<br>
	 * This value is used to identify the triangle in a collection.
	 * @param index The new integer value to assign to the triangle.
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	/**
	 * Calculates the surface normal of a triangle defined by three vertices.<br>
	 * The result is stored in and returned as a {@code Vector3f}.
	 * @param v1 The first vertex of the triangle.
	 * @param v2 The second vertex of the triangle.
	 * @param v3 The third vertex of the triangle.
	 * @param store The destination vector to store the result.
	 * @return The normalized surface normal as a {@code Vector3f}.
	 */
	public static Vector3f computeTriangleNormal(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f store)
	{
		if (store == null)
		{
			store = new Vector3f(v2);
		}
		else
		{
			store.set(v2);
		}
		
		store.subtractLocal(v1).crossLocal(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
		return store.normalizeLocal();
	}
	
	/**
	 * Retrieves the specific class type of this triangle.<br>
	 * This is useful for identifying subclasses of {@link Triangle}.
	 * @return The {@code Class} object representing the current instance type.
	 */
	public Class<? extends Triangle> getClassTag()
	{
		return this.getClass();
	}
	
	/**
	 * Creates a deep copy of this {@link Triangle} object.<br>
	 * This method clones all internal {@code Vector3f} points.
	 * @return A new {@code Triangle} instance with the same values.
	 */
	@Override
	public Triangle clone()
	{
		try
		{
			final Triangle t = (Triangle) super.clone();
			t.pointa = pointa.clone();
			t.pointb = pointb.clone();
			t.pointc = pointc.clone();
			return t;
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError();
		}
	}
	
	/**
	 * Resets all fields to their default values.<br>
	 * This method clears the coordinates of {@code pointa}, {@code pointb}, and {@code pointc}.<br>
	 * It also sets {@code center} and {@code normal} to {@code null}.<br>
	 * Use this to clear the current state of the {@link Triangle} instance.
	 */
	public void reset()
	{
		pointa.reset();
		pointb.reset();
		pointc.reset();
		center = null;
		normal = null;
		projection = 0;
		index = 0;
	}
	
	/**
	 * Creates a new instance of the {@link Triangle} class.
	 * @return A new {@code Triangle} instance.
	 */
	public static Triangle newInstance()
	{
		return new Triangle();
	}
	
	/**
	 * Recycles the provided {@code Triangle} instance.
	 * @param instance The {@code Triangle} object to be recycled.
	 */
	public static void recycle(Triangle instance)
	{
		// pooling removed
	}
}
