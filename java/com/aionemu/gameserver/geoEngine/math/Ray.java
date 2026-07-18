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

import com.aionemu.gameserver.geoEngine.bounding.BoundingVolume;
import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResult;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.collision.UnsupportedCollisionException;

/**
 * Represents a geometric ray starting from an {@code origin} point and extending infinitely in a specific {@code direction}.<br>
 * It is defined by the equation {@code R(t) = origin + t*direction} where {@code t >= 0}.
 * @author Mark Powell
 * @author Joshua Slack
 */
public final class Ray implements Cloneable, Collidable
{
	/**
	 * The ray's begining point.
	 */
	public Vector3f origin;
	/**
	 * The direction of the ray.
	 */
	public Vector3f direction;
	public float limit = Float.POSITIVE_INFINITY;
	
	// protected static final Vector3f tempVa=new Vector3f();
	// protected static final Vector3f tempVb=new Vector3f();
	// protected static final Vector3f tempVc=new Vector3f();
	// protected static final Vector3f tempVd=new Vector3f();
	
	/**
	 * Creates a new instance of the {@link Ray} class.<br>
	 * The {@code origin} is initialized to {@code (0,0,0)}.<br>
	 * The {@code direction} is initialized to {@code (0,0,0)}.
	 */
	public Ray()
	{
		origin = new Vector3f();
		direction = new Vector3f();
	}
	
	/**
	 * Creates a new {@link Ray} instance.<br>
	 * This constructor sets the starting point and the heading of the ray.
	 * @param origin The starting position of the ray as a {@code Vector3f}.
	 * @param direction The direction vector of the ray as a {@code Vector3f}.
	 */
	public Ray(Vector3f origin, Vector3f direction)
	{
		this.origin = origin;
		this.direction = direction;
	}
	
	/**
	 * <code>intersect</code> determines if the Ray intersects a triangle.
	 * @param t the Triangle to test against.
	 * @return true if the ray collides.
	 */
	// public boolean intersect(Triangle t) {
	// return intersect(t.get(0), t.get(1), t.get(2));
	// }
	/**
	 * <code>intersect</code> determines if the Ray intersects a triangle defined by the specified points.
	 * @param v0 first point of the triangle.
	 * @param v1 second point of the triangle.
	 * @param v2 third point of the triangle.
	 * @return true if the ray collides.
	 */
	// public boolean intersect(Vector3f v0,Vector3f v1,Vector3f v2){
	// return intersectWhere(v0, v1, v2, null);
	// }
	
	/**
	 * Checks if this ray intersects a specific {@link Triangle} at a given location.<br>
	 * This method uses the vertices of the triangle to perform the calculation.
	 * @param t The {@link Triangle} to check against.
	 * @param loc The {@link Vector3f} location where the intersection should occur.
	 * @return {@code true} if the ray intersects the triangle at the location, otherwise {@code false}.
	 */
	public boolean intersectWhere(Triangle t, Vector3f loc)
	{
		return intersectWhere(t.get(0), t.get(1), t.get(2), loc);
	}
	
	/**
	 * Checks if the ray intersects a triangle defined by three vertices.<br>
	 * This method uses the specific coordinates of the triangle to calculate the hit.
	 * @param v0 The first vertex of the triangle.
	 * @param v1 The second vertex of the triangle.
	 * @param v2 The third vertex of the triangle.
	 * @param loc The location point to check for intersection.
	 * @return {@code true} if an intersection occurs, otherwise {@code false}.
	 */
	public boolean intersectWhere(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f loc)
	{
		return intersects(v0, v1, v2, loc, false, false);
	}
	
	/**
	 * Checks if the ray intersects a triangle on its infinite plane.<br>
	 * This method uses the vertices of the {@code Triangle} object.<br>
	 * It determines if the intersection point matches the provided location.
	 * @param t The {@link Triangle} to check against.
	 * @param loc The specific {@code Vector3f} location to verify.
	 * @return {@code true} if the ray intersects the plane at the specified location, otherwise {@code false}.
	 */
	public boolean intersectWherePlanar(Triangle t, Vector3f loc)
	{
		return intersectWherePlanar(t.get(0), t.get(1), t.get(2), loc);
	}
	
	/**
	 * Checks if the ray intersects a plane defined by three vertices.<br>
	 * This method uses the {@code loc} parameter to determine the specific point of intersection.<br>
	 * It returns {@code true} if an intersection occurs and {@code false} otherwise.
	 * @param v0 The first vertex of the triangle defining the plane.
	 * @param v1 The second vertex of the triangle defining the plane.
	 * @param v2 The third vertex of the triangle defining the plane.
	 * @param loc The location point to check for intersection.
	 * @return {@code true} if the ray intersects the plane at the specified location, otherwise {@code false}.
	 */
	public boolean intersectWherePlanar(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f loc)
	{
		return intersects(v0, v1, v2, loc, true, false);
	}
	
	/**
	 * Checks if this ray intersects a triangle or quad defined by three vertices.<br>
	 * It calculates the intersection point and optional barycentric weights.
	 * @param v0 The first vertex of the triangle or quad.
	 * @param v1 The second vertex of the triangle or quad.
	 * @param v2 The third vertex of the triangle or quad.
	 * @param store A {@code Vector3f} to store the intersection result.
	 * @param doPlanar If {@code true}, stores barycentric weights in the {@code store} vector.
	 * @param quad If {@code false}, treats the shape as a triangle; if {@code true}, treats it as a quad.
	 * @return {@code true} if an intersection occurs, otherwise {@code false}.
	 */
	private boolean intersects(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f store, boolean doPlanar, boolean quad)
	{
		final Vector3f tempVa = Vector3f.newInstance(), tempVb = Vector3f.newInstance(), tempVc = Vector3f.newInstance(), tempVd = Vector3f.newInstance();
		
		final Vector3f diff = origin.subtract(v0, tempVa);
		final Vector3f edge1 = v1.subtract(v0, tempVb);
		final Vector3f edge2 = v2.subtract(v0, tempVc);
		final Vector3f norm = edge1.cross(edge2, tempVd);
		
		float dirDotNorm = direction.dot(norm);
		float sign;
		if (dirDotNorm > FastMath.FLT_EPSILON)
		{
			sign = 1;
		}
		else if (dirDotNorm < -FastMath.FLT_EPSILON)
		{
			sign = -1f;
			dirDotNorm = -dirDotNorm;
		}
		else
		{
			// ray and triangle/quad are parallel
			return false;
		}
		
		final float dirDotDiffxEdge2 = sign * direction.dot(diff.cross(edge2, edge2));
		if (dirDotDiffxEdge2 >= 0.0f)
		{
			final float dirDotEdge1xDiff = sign * direction.dot(edge1.crossLocal(diff));
			
			if (dirDotEdge1xDiff >= 0.0f)
			{
				if (!quad ? (dirDotDiffxEdge2 + dirDotEdge1xDiff) <= dirDotNorm : dirDotEdge1xDiff <= dirDotNorm)
				{
					final float diffDotNorm = -sign * diff.dot(norm);
					if (diffDotNorm >= 0.0f)
					{
						// this method always returns
						Vector3f.recycle(tempVa);
						Vector3f.recycle(tempVb);
						Vector3f.recycle(tempVc);
						Vector3f.recycle(tempVd);
						
						// Return true if the storage vector is null when checking if a ray intersects a triangle.
						if (store == null)
						{
							return true;
						}
						
						// else fill in.
						final float inv = 1f / dirDotNorm;
						final float t = diffDotNorm * inv;
						if (!doPlanar)
						{
							store.set(origin).addLocal(direction.x * t, direction.y * t, direction.z * t);
						}
						else
						{
							// These weights can be used to determine interpolated values, such as texture coordinates.
							// eg. texcoord s,t at intersection point:
							// s = w0*s0 + w1*s1 + w2*s2;
							// t = w0*t0 + w1*t1 + w2*t2;
							final float w1 = dirDotDiffxEdge2 * inv;
							final float w2 = dirDotEdge1xDiff * inv;
							
							// float w0 = 1.0f - w1 - w2;
							store.set(t, w1, w2);
						}
						
						return true;
					}
				}
			}
		}
		
		Vector3f.recycle(tempVa);
		Vector3f.recycle(tempVb);
		Vector3f.recycle(tempVc);
		Vector3f.recycle(tempVd);
		return false;
	}
	
	/**
	 * Calculates the intersection distance between this ray and a triangle.<br>
	 * The method uses the vertices of the triangle to determine the hit point.<br>
	 * If no intersection occurs, it returns {@code Float.POSITIVE_INFINITY}.
	 * @param v0 The first vertex of the triangle.
	 * @param v1 The second vertex of the triangle.
	 * @param v2 The third vertex of the triangle.
	 * @return The distance from the ray origin to the intersection point, or {@code Float.POSITIVE_INFINITY} if no hit is found.
	 */
	public float intersects(Vector3f v0, Vector3f v1, Vector3f v2)
	{
		final float edge1X = v1.x - v0.x;
		final float edge1Y = v1.y - v0.y;
		final float edge1Z = v1.z - v0.z;
		
		final float edge2X = v2.x - v0.x;
		final float edge2Y = v2.y - v0.y;
		final float edge2Z = v2.z - v0.z;
		
		final float normX = ((edge1Y * edge2Z) - (edge1Z * edge2Y));
		final float normY = ((edge1Z * edge2X) - (edge1X * edge2Z));
		final float normZ = ((edge1X * edge2Y) - (edge1Y * edge2X));
		
		float dirDotNorm = (direction.x * normX) + (direction.y * normY) + (direction.z * normZ);
		
		final float diffX = origin.x - v0.x;
		final float diffY = origin.y - v0.y;
		final float diffZ = origin.z - v0.z;
		
		float sign;
		if (dirDotNorm > FastMath.FLT_EPSILON)
		{
			sign = 1;
		}
		else if (dirDotNorm < -FastMath.FLT_EPSILON)
		{
			sign = -1f;
			dirDotNorm = -dirDotNorm;
		}
		else
		{
			// ray and triangle/quad are parallel
			return Float.POSITIVE_INFINITY;
		}
		
		float diffEdge2X = ((diffY * edge2Z) - (diffZ * edge2Y));
		float diffEdge2Y = ((diffZ * edge2X) - (diffX * edge2Z));
		float diffEdge2Z = ((diffX * edge2Y) - (diffY * edge2X));
		
		final float dirDotDiffxEdge2 = sign * ((direction.x * diffEdge2X) + (direction.y * diffEdge2Y) + (direction.z * diffEdge2Z));
		
		if (dirDotDiffxEdge2 >= 0.0f)
		{
			diffEdge2X = ((edge1Y * diffZ) - (edge1Z * diffY));
			diffEdge2Y = ((edge1Z * diffX) - (edge1X * diffZ));
			diffEdge2Z = ((edge1X * diffY) - (edge1Y * diffX));
			
			final float dirDotEdge1xDiff = sign * ((direction.x * diffEdge2X) + (direction.y * diffEdge2Y) + (direction.z * diffEdge2Z));
			
			if (dirDotEdge1xDiff >= 0.0f)
			{
				if ((dirDotDiffxEdge2 + dirDotEdge1xDiff) <= dirDotNorm)
				{
					final float diffDotNorm = -sign * ((diffX * normX) + (diffY * normY) + (diffZ * normZ));
					if (diffDotNorm >= 0.0f)
					{
						// Fill in the ray intersection with the triangle.
						final float inv = 1f / dirDotNorm;
						final float t = diffDotNorm * inv;
						return t;
					}
				}
			}
		}
		
		return Float.POSITIVE_INFINITY;
	}
	
	/**
	 * Checks if this ray intersects a planar quadrilateral.<br>
	 * The quadrilateral is defined by four vertices.<br>
	 * This method uses the internal {@code intersects} logic for planar quad shapes.
	 * @param v0 The first vertex of the quadrilateral.
	 * @param v1 The second vertex of the quadrilateral.
	 * @param v2 The third vertex of the quadrilateral.
	 * @param loc The location point to check against.
	 * @return {@code true} if an intersection occurs, {@code false} otherwise.
	 */
	public boolean intersectWherePlanarQuad(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f loc)
	{
		return intersects(v0, v1, v2, loc, true, true);
	}
	
	/**
	 * Checks if this ray intersects with a given {@link Plane}.<br>
	 * The intersection point is stored in the provided {@code loc} vector.<br>
	 * This method returns {@code false} if the ray is parallel to the plane or points away from it.
	 * @param p The {@link Plane} to check against.
	 * @param loc The {@link Vector3f} where the intersection point will be stored.
	 * @return {@code true} if an intersection occurs, otherwise {@code false}.
	 */
	public boolean intersectsWherePlane(Plane p, Vector3f loc)
	{
		final float denominator = p.getNormal().dot(direction);
		
		if ((denominator > -FastMath.FLT_EPSILON) && (denominator < FastMath.FLT_EPSILON))
		{
			return false; // coplanar
		}
		
		final float numerator = -(p.getNormal().dot(origin) - p.getConstant());
		final float ratio = numerator / denominator;
		
		if (ratio < FastMath.FLT_EPSILON)
		{
			return false; // intersects behind origin
		}
		
		loc.set(direction).multLocal(ratio).addLocal(origin);
		
		return true;
	}
	
	/**
	 * Checks if this ray collides with another {@link Collidable} object.<br>
	 * This method handles collisions with {@link BoundingVolume} and {@link AbstractTriangle} types.<br>
	 * It updates the provided {@code results} object if a collision occurs.
	 * @param other The {@link Collidable} object to check against.
	 * @param results The {@link CollisionResults} container to store any detected hits.
	 * @return Returns 1 if a collision occurred, or 0 if no collision was found.
	 */
	@Override
	public int collideWith(Collidable other, CollisionResults results)
	{
		if (other instanceof BoundingVolume)
		{
			final BoundingVolume bv = (BoundingVolume) other;
			return bv.collideWith(this, results);
		}
		else if (other instanceof AbstractTriangle)
		{
			final AbstractTriangle tri = (AbstractTriangle) other;
			final float d = intersects(tri.get1(), tri.get2(), tri.get3());
			if (Float.isInfinite(d) || Float.isNaN(d))
			{
				return 0;
			}
			
			final Vector3f point = new Vector3f(direction).multLocal(d).addLocal(origin);
			results.addCollision(new CollisionResult(point, d));
			return 1;
		}
		else
		{
			throw new UnsupportedCollisionException();
		}
	}
	
	/**
	 * Calculates the squared distance from this ray to a given point.<br>
	 * This method is useful for performance as it avoids a square root operation.
	 * @param point The {@code Vector3f} position to measure from.
	 * @return The squared distance as a {@code float}.
	 */
	public float distanceSquared(Vector3f point)
	{
		final Vector3f tempVa = Vector3f.newInstance(), tempVb = Vector3f.newInstance();
		
		point.subtract(origin, tempVa);
		float rayParam = direction.dot(tempVa);
		if (rayParam > 0)
		{
			origin.add(direction.mult(rayParam, tempVb), tempVb);
		}
		else
		{
			tempVb.set(origin);
			rayParam = 0.0f;
		}
		
		tempVb.subtract(point, tempVa);
		final float len = tempVa.lengthSquared();
		Vector3f.recycle(tempVa);
		Vector3f.recycle(tempVb);
		return len;
	}
	
	/**
	 * Retrieves the starting point of the ray.<br>
	 * This returns the {@code origin} field of this <code class="Ray"></code> object.
	 * @return The {@code Vector3f} representing the ray's origin.
	 */
	public Vector3f getOrigin()
	{
		return origin;
	}
	
	/**
	 * Sets the starting point of the ray.<br>
	 * This method updates the {@code origin} field with the values from the provided vector.
	 * @param origin The new {@code Vector3f} position for the ray's start.
	 */
	public void setOrigin(Vector3f origin)
	{
		this.origin.set(origin);
	}
	
	/**
	 * Retrieves the maximum length of the ray.<br>
	 * This value determines how far the ray extends from its origin.
	 * @return The current {@code float} limit of the ray.
	 */
	public float getLimit()
	{
		return limit;
	}
	
	/**
	 * Sets the maximum length of the ray.<br>
	 * This updates the {@code limit} field.
	 * @param limit The new length for the ray.
	 */
	public void setLimit(float limit)
	{
		this.limit = limit;
	}
	
	/**
	 * Retrieves the current direction of the {@code Ray}.
	 * @return The {@code Vector3f} representing the ray's direction.
	 */
	public Vector3f getDirection()
	{
		return direction;
	}
	
	/**
	 * Sets the direction of this {@code Ray}.<br>
	 * This method updates the internal {@code direction} vector.
	 * @param direction The new {@code Vector3f} to use as the ray's direction.
	 */
	public void setDirection(Vector3f direction)
	{
		this.direction.set(direction);
	}
	
	/**
	 * Updates this ray's properties using another ray.<br>
	 * This method copies the origin and direction from the {@code source}.
	 * @param source The {@link Ray} to copy values from.
	 */
	public void set(Ray source)
	{
		origin.set(source.getOrigin());
		direction.set(source.getDirection());
	}
	
	/**
	 * Returns a string representation of this {@code Ray}.<br>
	 * It includes the class name, the {@code origin}, and the {@code direction}.
	 * @return A formatted string describing the ray.
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " [Origin: " + origin + ", Direction: " + direction + "]";
	}
	
	/**
	 * Retrieves the specific class type of this {@link Ray} instance.<br>
	 * This is useful for identifying the exact subclass being used at runtime.
	 * @return The {@code Class} object representing the current instance's type.
	 */
	public Class<? extends Ray> getClassTag()
	{
		return this.getClass();
	}
	
	/**
	 * Creates a deep copy of this {@code Ray} object.<br>
	 * This method clones both the {@code origin} and {@code direction} vectors.
	 * @return A new {@code Ray} instance with the same values as the original.
	 */
	@Override
	public Ray clone()
	{
		try
		{
			final Ray r = (Ray) super.clone();
			r.direction = direction.clone();
			r.origin = origin.clone();
			return r;
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError();
		}
	}
}
