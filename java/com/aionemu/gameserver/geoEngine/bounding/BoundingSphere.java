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
package com.aionemu.gameserver.geoEngine.bounding;

import java.nio.FloatBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResult;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.collision.UnsupportedCollisionException;
import com.aionemu.gameserver.geoEngine.math.FastMath;
import com.aionemu.gameserver.geoEngine.math.Matrix4f;
import com.aionemu.gameserver.geoEngine.math.Plane;
import com.aionemu.gameserver.geoEngine.math.Ray;
import com.aionemu.gameserver.geoEngine.math.Triangle;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.utils.BufferUtils;

/**
 * Represents a spherical bounding volume used to contain a group of vertices for a piece of geometry.<br>
 * This class defines a center point and a radius, which can be calculated using {@code com.aionemu.gameserver.geoEngine.math.Vector3f)} or {@code averagePoints}. It is commonly used to simplify collision detection and spatial queries.
 * @author Mark Powell
 */
public class BoundingSphere extends BoundingVolume
{
	private static final Logger logger = LoggerFactory.getLogger(BoundingSphere.class);
	float radius;
	private static final float RADIUS_EPSILON = 1f + 0.00001f;
	
	/**
	 * Creates a new instance of the {@link BoundingSphere} class.<br>
	 * This constructor initializes a default sphere object.
	 */
	public BoundingSphere()
	{
	}
	
	/**
	 * Creates a new {@link BoundingSphere} with a specific radius and center.<br>
	 * This constructor initializes the sphere using the provided dimensions.
	 * @param r The radius of the sphere.
	 * @param c The center position as a {@code Vector3f}.
	 */
	public BoundingSphere(float r, Vector3f c)
	{
		center.set(c);
		radius = r;
	}
	
	/**
	 * Retrieves the type of this bounding volume.<br>
	 * This method returns the {@code Sphere} constant.
	 * @return The {@code Type} representing a sphere.
	 */
	@Override
	public Type getType()
	{
		return Type.Sphere;
	}
	
	/**
	 * Retrieves the current radius of the {@code BoundingSphere}.
	 * @return The radius as a {@code float}.
	 */
	public float getRadius()
	{
		return radius;
	}
	
	/**
	 * Sets the size of the sphere's radius.<br>
	 * This updates the internal {@code radius} field.
	 * @param radius The new value for the sphere radius.
	 */
	public void setRadius(float radius)
	{
		this.radius = radius;
	}
	
	/**
	 * Calculates the bounding sphere using the Welzl algorithm.<br>
	 * This method updates the internal radius and center based on the provided points.
	 * @param points The {@code FloatBuffer} containing the vertex data.
	 */
	@Override
	public void computeFromPoints(FloatBuffer points)
	{
		calcWelzl(points);
	}
	
	/**
	 * Calculates the bounding sphere dimensions based on a range of triangles.<br>
	 * This method updates the {@code radius} and center using the vertices from the provided array.
	 * @param tris The array of {@link Triangle} objects to process.
	 * @param start The starting index in the triangle array.
	 * @param end The ending index (exclusive) in the triangle array.
	 */
	public void computeFromTris(Triangle[] tris, int start, int end)
	{
		if ((end - start) <= 0)
		{
			return;
		}
		
		final Vector3f[] vertList = new Vector3f[(end - start) * 3];
		
		int count = 0;
		for (int i = start; i < end; i++)
		{
			vertList[count++] = tris[i].get(0);
			vertList[count++] = tris[i].get(1);
			vertList[count++] = tris[i].get(2);
		}
		
		averagePoints(vertList);
	}
	
	//
	// /**
	// ComputeFromTris creates a new Bounding Box from a given set of triangles and is used in OBBTree calculations.
	// *
	// The parameters include indices, mesh, start, and end.
	// */
	// public void computeFromTris(int[] indices, Mesh mesh, int start, int end) {
	// if (end - start <= 0) {
	// return;
	// }
	//
	// Vector3f[] vertList = new Vector3f[(end - start) * 3];
	//
	// int count = 0;
	// for (int i = start; i < end; i++) {
	// mesh.getTriangle(indices[i], verts);
	// vertList[count++] = new Vector3f(verts[0]);
	// vertList[count++] = new Vector3f(verts[1]);
	// vertList[count++] = new Vector3f(verts[2]);
	// }
	//
	// averagePoints(vertList);
	// }
	
	/**
	 * Calculates the smallest enclosing sphere using Welzl's algorithm.<br>
	 * This method processes a set of points to find the minimum bounding volume.<br>
	 * It uses an internal recursive helper to determine the optimal radius and center.
	 * @param points A {@code FloatBuffer} containing the coordinates of the points.
	 */
	public void calcWelzl(FloatBuffer points)
	{
		if (center == null)
		{
			center = new Vector3f();
		}
		
		final FloatBuffer buf = BufferUtils.createFloatBuffer(points.limit());
		points.rewind();
		buf.put(points);
		buf.flip();
		recurseMini(buf, buf.limit() / 3, 0, 0);
	}
	
	/**
	 * Recursively calculates the minimum bounding sphere using Welzl's algorithm.<br>
	 * This method updates the {@code radius} and {@code center} based on a set of points.
	 * @param points The {@link FloatBuffer} containing all vertex coordinates.
	 * @param p The number of points to process in the current recursion.
	 * @param b The number of points currently on the boundary of the sphere.
	 * @param ap The starting index within the buffer for the current point set.
	 */
	private void recurseMini(FloatBuffer points, int p, int b, int ap)
	{
		final Vector3f tempA = Vector3f.newInstance();
		final Vector3f tempB = Vector3f.newInstance();
		final Vector3f tempC = Vector3f.newInstance();
		final Vector3f tempD = Vector3f.newInstance();
		
		try
		{
			switch (b)
			{
				case 0:
					radius = 0;
					center.set(0, 0, 0);
					break;
				case 1:
					radius = 1f - RADIUS_EPSILON;
					BufferUtils.populateFromBuffer(center, points, ap - 1);
					break;
				case 2:
					BufferUtils.populateFromBuffer(tempA, points, ap - 1);
					BufferUtils.populateFromBuffer(tempB, points, ap - 2);
					setSphere(tempA, tempB);
					break;
				case 3:
					BufferUtils.populateFromBuffer(tempA, points, ap - 1);
					BufferUtils.populateFromBuffer(tempB, points, ap - 2);
					BufferUtils.populateFromBuffer(tempC, points, ap - 3);
					setSphere(tempA, tempB, tempC);
					break;
				case 4:
					BufferUtils.populateFromBuffer(tempA, points, ap - 1);
					BufferUtils.populateFromBuffer(tempB, points, ap - 2);
					BufferUtils.populateFromBuffer(tempC, points, ap - 3);
					BufferUtils.populateFromBuffer(tempD, points, ap - 4);
					setSphere(tempA, tempB, tempC, tempD);
					return;
			}
			
			for (int i = 0; i < p; i++)
			{
				BufferUtils.populateFromBuffer(tempA, points, i + ap);
				if ((tempA.distanceSquared(center) - (radius * radius)) > (RADIUS_EPSILON - 1f))
				{
					for (int j = i; j > 0; j--)
					{
						BufferUtils.populateFromBuffer(tempB, points, j + ap);
						BufferUtils.populateFromBuffer(tempC, points, (j - 1) + ap);
						BufferUtils.setInBuffer(tempC, points, j + ap);
						BufferUtils.setInBuffer(tempB, points, (j - 1) + ap);
					}
					
					recurseMini(points, i, b + 1, ap + 1);
				}
			}
		}
		finally
		{
			Vector3f.recycle(tempA);
			Vector3f.recycle(tempB);
			Vector3f.recycle(tempC);
			Vector3f.recycle(tempD);
		}
	}
	
	/**
	 * Updates the sphere properties based on four points.<br>
	 * This method calculates a new center and radius using the provided coordinates.<br>
	 * It handles cases where the denominator is zero by resetting the values to {@code 0}.
	 * @param O The origin point for the calculation.
	 * @param A The first point defining the sphere boundary.
	 * @param B The second point defining the sphere boundary.
	 * @param C The third point defining the sphere boundary.
	 */
	private void setSphere(Vector3f O, Vector3f A, Vector3f B, Vector3f C)
	{
		final Vector3f a = A.subtract(O);
		final Vector3f b = B.subtract(O);
		final Vector3f c = C.subtract(O);
		
		final float Denominator = 2.0f * (((a.x * ((b.y * c.z) - (c.y * b.z))) - (b.x * ((a.y * c.z) - (c.y * a.z)))) + (c.x * ((a.y * b.z) - (b.y * a.z))));
		if (Denominator == 0)
		{
			center.set(0, 0, 0);
			radius = 0;
		}
		else
		{
			final Vector3f o = a.cross(b).multLocal(c.lengthSquared()).addLocal(c.cross(a).multLocal(b.lengthSquared())).addLocal(b.cross(c).multLocal(a.lengthSquared())).divideLocal(Denominator);
			
			radius = o.length() * RADIUS_EPSILON;
			O.add(o, center);
		}
	}
	
	/**
	 * This method updates the sphere's center and radius based on three points.<br>
	 * It calculates the circumsphere of a triangle defined by {@code A}, {@code B}, and {@code C}.<br>
	 * The origin point {@code O} is used as a reference for the calculation.
	 * @param O The origin vector used for coordinate calculations.
	 * @param A The first vertex of the triangle.
	 * @param B The second vertex of the triangle.
	 */
	private void setSphere(Vector3f O, Vector3f A, Vector3f B)
	{
		final Vector3f a = A.subtract(O);
		final Vector3f b = B.subtract(O);
		final Vector3f acrossB = a.cross(b);
		
		final float Denominator = 2.0f * acrossB.dot(acrossB);
		
		if (Denominator == 0)
		{
			center.set(0, 0, 0);
			radius = 0;
		}
		else
		{
			final Vector3f o = acrossB.cross(a).multLocal(b.lengthSquared()).addLocal(b.cross(acrossB).multLocal(a.lengthSquared())).divideLocal(Denominator);
			radius = o.length() * RADIUS_EPSILON;
			O.add(o, center);
		}
	}
	
	/**
	 * Updates the sphere's center and radius based on two points.<br>
	 * This method calculates the midpoint between {@code O} and {@code A}.<br>
	 * It also sets the radius using the distance between these two points.
	 * @param O The first point used for calculation.
	 * @param A The second point used for calculation.
	 */
	private void setSphere(Vector3f O, Vector3f A)
	{
		radius = (FastMath.sqrt((((A.x - O.x) * (A.x - O.x)) + ((A.y - O.y) * (A.y - O.y)) + ((A.z - O.z) * (A.z - O.z))) / 4f) + RADIUS_EPSILON) - 1f;
		center.interpolate(O, A, .5f);
	}
	
	/**
	 * Calculates the center and radius of this sphere based on an array of points.<br>
	 * The center is determined by finding the average position of all provided {@code Vector3f} objects.<br>
	 * The radius is set to the distance from the new center to the furthest point in the array.
	 * @param points An array of {@code Vector3f} coordinates used to define the sphere.
	 */
	public void averagePoints(Vector3f[] points)
	{
		logger.info("Bounding Sphere calculated using average points.");
		center = points[0];
		
		for (int i = 1; i < points.length; i++)
		{
			center.addLocal(points[i]);
		}
		
		final float quantity = 1.0f / points.length;
		center.multLocal(quantity);
		
		float maxRadiusSqr = 0;
		for (int i = 0; i < points.length; i++)
		{
			final Vector3f diff = points[i].subtract(center);
			final float radiusSqr = diff.lengthSquared();
			if (radiusSqr > maxRadiusSqr)
			{
				maxRadiusSqr = radiusSqr;
			}
		}
		
		radius = ((float) Math.sqrt(maxRadiusSqr) + RADIUS_EPSILON) - 1f;
		
	}
	
	/**
	 * Applies a transformation to a {@link BoundingVolume}.<br>
	 * This method calculates the new center and extents based on the provided matrix.<br>
	 * It uses the {@code store} object as a template for the result.
	 * @param trans The {@code Matrix4f} transformation to apply.
	 * @param store The {@link BoundingVolume} to use as a base for the result. If null or not an AABB, a new one is created.
	 * @return The transformed {@link BoundingVolume}.
	 */
	@Override
	public BoundingVolume transform(Matrix4f trans, BoundingVolume store)
	{
		BoundingSphere sphere;
		if ((store == null) || (store.getType() != BoundingVolume.Type.Sphere))
		{
			sphere = new BoundingSphere(1, new Vector3f(0, 0, 0));
		}
		else
		{
			sphere = (BoundingSphere) store;
		}
		
		trans.mult(center, sphere.center);
		final Vector3f axes = new Vector3f(1, 1, 1);
		trans.mult(axes, axes);
		final float ax = getMaxAxis(axes);
		sphere.radius = (FastMath.abs(ax * radius) + RADIUS_EPSILON) - 1f;
		return sphere;
	}
	
	/**
	 * Finds the largest absolute component of a scale vector.<br>
	 * This method compares the {@code x}, {@code y}, and {@code z} values.<br>
	 * It returns the maximum value among them.
	 * @param scale The {@code Vector3f} containing the scaling factors.
	 * @return The largest absolute float value from the input vector.
	 */
	private float getMaxAxis(Vector3f scale)
	{
		final float x = FastMath.abs(scale.x);
		final float y = FastMath.abs(scale.y);
		final float z = FastMath.abs(scale.z);
		
		if (x >= y)
		{
			if (x >= z)
			{
				return x;
			}
			
			return z;
		}
		
		if (y >= z)
		{
			return y;
		}
		
		return z;
	}
	
	/**
	 * Determines which side of a {@link Plane} this bounding sphere is on.<br>
	 * It compares the distance from the center to the plane against the radius.
	 * @param plane The {@link Plane} used for the calculation.
	 * @return The {@code Side} representing Positive, Negative, or None.
	 */
	@Override
	public Plane.Side whichSide(Plane plane)
	{
		final float distance = plane.pseudoDistance(center);
		
		if (distance <= -radius)
		{
			return Plane.Side.Negative;
		}
		else if (distance >= radius)
		{
			return Plane.Side.Positive;
		}
		else
		{
			return Plane.Side.None;
		}
	}
	
	/**
	 * Combines the current bounding volume with another {@link BoundingVolume}.<br>
	 * This method creates a new volume that encompasses both objects.<br>
	 * It returns {@code null} if the provided volume type is not supported.
	 * @param volume The {@link BoundingVolume} to merge with this one.
	 * @return A new {@link BoundingVolume} containing both volumes, or {@code null}.
	 */
	@Override
	public BoundingVolume merge(BoundingVolume volume)
	{
		if (volume == null)
		{
			return this;
		}
		
		switch (volume.getType())
		{
			case Sphere:
			{
				final BoundingSphere sphere = (BoundingSphere) volume;
				final float temp_radius = sphere.getRadius();
				final Vector3f temp_center = sphere.center;
				final BoundingSphere rVal = new BoundingSphere();
				return merge(temp_radius, temp_center, rVal);
			}
			
			case AABB:
			{
				final BoundingBox box = (BoundingBox) volume;
				final Vector3f radVect = new Vector3f(box.xExtent, box.yExtent, box.zExtent);
				final Vector3f temp_center = box.center;
				final BoundingSphere rVal = new BoundingSphere();
				return merge(radVect.length(), temp_center, rVal);
			}
			
			// case OBB: {
			// OrientedBoundingBox box = (OrientedBoundingBox) volume;
			// BoundingSphere rVal = (BoundingSphere) this.clone(null);
			// return rVal.mergeOBB(box);
			// }
			default:
				return null;
			
		}
	}
	
	/**
	 * Merges the current bounding volume with another local volume.<br>
	 * This method combines the dimensions of two {@code AABB} types.<br>
	 * It returns {@code null} if the provided volume type is not supported.
	 * @param volume The {@link BoundingVolume} to merge into this one.
	 * @return A new {@link BoundingVolume} representing the merged area, or {@code null}.
	 */
	@Override
	public BoundingVolume mergeLocal(BoundingVolume volume)
	{
		if (volume == null)
		{
			return this;
		}
		
		switch (volume.getType())
		{
			case Sphere:
			{
				final BoundingSphere sphere = (BoundingSphere) volume;
				final float temp_radius = sphere.getRadius();
				final Vector3f temp_center = sphere.center;
				return merge(temp_radius, temp_center, this);
			}
			
			case AABB:
			{
				final BoundingBox box = (BoundingBox) volume;
				final Vector3f radVect = Vector3f.newInstance();
				radVect.set(box.xExtent, box.yExtent, box.zExtent);
				final Vector3f temp_center = box.center;
				final float len = radVect.length();
				Vector3f.recycle(radVect);
				return merge(len, temp_center, this);
			}
			
			// case OBB: {
			// return mergeOBB((OrientedBoundingBox) volume);
			// }
			default:
				return null;
		}
	}
	
	// /**
	// * Merges this sphere with the given OBB.
	// *
	// The OBB to merge.
	// * @return This sphere, after merging.
	// */
	// private BoundingSphere mergeOBB(OrientedBoundingBox volume) {
	// Compute edge points from the OBB if the volume corners are incorrect.
	// volume.computeCorners();
	// _mergeBuf.rewind();
	// for (int i = 0; i < 8; i++) {
	// _mergeBuf.put(volume.vectorStore[i].x);
	// _mergeBuf.put(volume.vectorStore[i].y);
	// _mergeBuf.put(volume.vectorStore[i].z);
	// }
	//
	// Remember the old radius and center.
	// Vector3f oldCenter = _compVect2.set( center );
	//
	// Compute the new radius and center from OBB points using computeFromPoints(_mergeBuf).
	// Vector3f newCenter = _compVect3.set( center );
	// float newRadius = radius;
	//
	// Restore the old center and radius.
	// radius = oldRadius;
	//
	// Merge OBB points results in merging the new radius, new center, and current object.
	//
	// return this;
	// }
	
	/**
	 * Merges the current sphere with another sphere based on provided dimensions.<br>
	 * This method calculates a new bounding volume that encompasses both spheres.<br>
	 * It updates the {@code rVal} object to store the resulting center and radius.
	 * @param temp_radius The radius of the second sphere.
	 * @param temp_center The center position of the second sphere.
	 * @param rVal The {@link BoundingSphere} instance used to store the merged result.
	 * @return The updated {@link BoundingVolume} containing both spheres.
	 */
	private BoundingVolume merge(float temp_radius, Vector3f temp_center, BoundingSphere rVal)
	{
		final Vector3f vect1 = Vector3f.newInstance();
		final Vector3f diff = temp_center.subtract(center, vect1);
		final float lengthSquared = diff.lengthSquared();
		final float radiusDiff = temp_radius - radius;
		
		final float fRDiffSqr = radiusDiff * radiusDiff;
		
		if (fRDiffSqr >= lengthSquared)
		{
			if (radiusDiff <= 0.0f)
			{
				Vector3f.recycle(vect1);
				return this;
			}
			
			Vector3f rCenter = rVal.center;
			if (rCenter == null)
			{
				rVal.setCenter(rCenter = new Vector3f());
			}
			
			rCenter.set(temp_center);
			rVal.setRadius(temp_radius);
			Vector3f.recycle(vect1);
			return rVal;
		}
		
		final float length = (float) Math.sqrt(lengthSquared);
		
		Vector3f rCenter = rVal.center;
		if (rCenter == null)
		{
			rVal.setCenter(rCenter = new Vector3f());
		}
		
		if (length > RADIUS_EPSILON)
		{
			final float coeff = (length + radiusDiff) / (2.0f * length);
			rCenter.set(center.addLocal(diff.multLocal(coeff)));
		}
		else
		{
			rCenter.set(center);
		}
		
		rVal.setRadius(0.5f * (length + radius + temp_radius));
		Vector3f.recycle(vect1);
		return rVal;
	}
	
	/**
	 * Creates a copy of the current {@link BoundingVolume}.<br>
	 * It attempts to reuse the provided {@code store} if it is a sphere.<br>
	 * If the {@code store} is {@code null} or not a sphere, a new instance is created.
	 * @param store The object to use as a template for cloning.
	 * @return A new or reused {@link BoundingSphere} instance.
	 */
	@Override
	public BoundingVolume clone(BoundingVolume store)
	{
		if ((store != null) && (store.getType() == Type.Sphere))
		{
			final BoundingSphere rVal = (BoundingSphere) store;
			if (null == rVal.center)
			{
				rVal.center = new Vector3f();
			}
			
			rVal.center.set(center);
			rVal.radius = radius;
			rVal.checkPlane = checkPlane;
			return rVal;
		}
		
		return new BoundingSphere(radius, (center != null ? (Vector3f) center.clone() : null));
	}
	
	/**
	 * Returns a string representation of this {@code BoundingSphere}.<br>
	 * It includes the class name, the radius, and the center point.
	 * @return A formatted string describing the sphere properties.
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " [Radius: " + radius + " Center: " + center + "]";
	}
	
	/**
	 * Checks if this sphere overlaps with another volume.<br>
	 * It delegates the calculation to the {@code intersectsSphere} method of the provided object.
	 * @param bv The {@link BoundingVolume} to check against.
	 * @return {@code true} if the volumes intersect, {@code false} otherwise.
	 */
	@Override
	public boolean intersects(BoundingVolume bv)
	{
		return bv.intersectsSphere(this);
	}
	
	/**
	 * Checks if this {@code BoundingSphere} overlaps with a given {@code BoundingSphere}.<br>
	 * This method compares the distance between centers against the sum of both radii.
	 * @param bs The {@code BoundingSphere} to check against.
	 * @return {@code true} if the objects intersect, otherwise {@code false}.
	 */
	@Override
	public boolean intersectsSphere(BoundingSphere bs)
	{
		assert Vector3f.isValidVector(center) && Vector3f.isValidVector(bs.center);
		
		final Vector3f vect1 = Vector3f.newInstance();
		final Vector3f diff = center.subtract(bs.center, vect1);
		final float rsum = getRadius() + bs.getRadius();
		final boolean eq = (diff.dot(diff) <= (rsum * rsum));
		Vector3f.recycle(vect1);
		return eq;
	}
	
	/**
	 * Checks if this sphere intersects with a given {@code BoundingBox}.<br>
	 * It compares the distance between centers against the combined extents.
	 * @param bb The {@code BoundingBox} to check against.
	 * @return {@code true} if they intersect, otherwise {@code false}.
	 */
	@Override
	public boolean intersectsBoundingBox(BoundingBox bb)
	{
		assert Vector3f.isValidVector(center) && Vector3f.isValidVector(bb.center);
		
		if ((FastMath.abs(bb.center.x - center.x) < (getRadius() + bb.xExtent)) && (FastMath.abs(bb.center.y - center.y) < (getRadius() + bb.yExtent)) && (FastMath.abs(bb.center.z - center.z) < (getRadius() + bb.zExtent)))
		{
			return true;
		}
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jme.bounding.BoundingVolume#intersectsOrientedBoundingBox(com.jme.bounding.OrientedBoundingBox)
	 */
	// public boolean intersectsOrientedBoundingBox(OrientedBoundingBox obb) {
	// return obb.intersectsSphere(this);
	// }
	
	/**
	 * Checks if the given {@code Ray} intersects with this bounding sphere.<br>
	 * This method calculates the distance between the ray and the sphere center to determine collision.
	 * @param ray The {@code Ray} to test against the sphere.
	 * @return {@code true} if the ray hits the sphere, otherwise {@code false}.
	 */
	@Override
	public boolean intersects(Ray ray)
	{
		assert Vector3f.isValidVector(center);
		
		final Vector3f vect1 = Vector3f.newInstance();
		final Vector3f diff = vect1.set(ray.getOrigin()).subtractLocal(center);
		final float radiusSquared = getRadius() * getRadius();
		final float a = diff.dot(diff) - radiusSquared;
		if (a <= 0.0)
		{
			// in sphere
			Vector3f.recycle(vect1);
			return true;
		}
		
		// outside sphere
		final float b = ray.getDirection().dot(diff);
		if (b >= 0.0)
		{
			Vector3f.recycle(vect1);
			return false;
		}
		
		Vector3f.recycle(vect1);
		return (b * b) >= a;
	}
	
	/**
	 * Checks if a {@link Ray} intersects with this sphere.<br>
	 * It adds any hit points to the provided {@code results} object.
	 * @param ray The {@code Ray} used for the collision test.
	 * @param results The {@code CollisionResults} where hits are stored.
	 * @return The number of intersection points found, such as 0, 1, or 2.
	 */
	public int collideWithRay(Ray ray, CollisionResults results)
	{
		final Vector3f vect1 = Vector3f.newInstance();
		final Vector3f diff = vect1.set(ray.getOrigin()).subtractLocal(center);
		final float a = diff.dot(diff) - (getRadius() * getRadius());
		float a1, discr, root;
		if (a <= 0.0)
		{
			// inside sphere
			a1 = ray.direction.dot(diff);
			discr = (a1 * a1) - a;
			root = FastMath.sqrt(discr);
			
			final float distance = root - a1;
			final Vector3f point = new Vector3f(ray.direction).multLocal(distance).addLocal(ray.origin);
			
			final CollisionResult result = new CollisionResult(point, distance);
			results.addCollision(result);
			Vector3f.recycle(vect1);
			return 1;
		}
		
		a1 = ray.direction.dot(diff);
		if (a1 >= 0.0)
		{
			Vector3f.recycle(vect1);
			return 0;
		}
		
		discr = (a1 * a1) - a;
		if (discr < 0.0)
		{
			Vector3f.recycle(vect1);
			return 0;
		}
		else if (discr >= FastMath.ZERO_TOLERANCE)
		{
			root = FastMath.sqrt(discr);
			float dist = -a1 - root;
			Vector3f point = new Vector3f(ray.direction).multLocal(dist).addLocal(ray.origin);
			results.addCollision(new CollisionResult(point, dist));
			
			dist = -a1 + root;
			point = new Vector3f(ray.direction).multLocal(dist).addLocal(ray.origin);
			results.addCollision(new CollisionResult(point, dist));
			Vector3f.recycle(vect1);
			return 2;
		}
		else
		{
			final float dist = -a1;
			final Vector3f point = new Vector3f(ray.direction).multLocal(dist).addLocal(ray.origin);
			results.addCollision(new CollisionResult(point, dist));
			Vector3f.recycle(vect1);
			return 1;
		}
	}
	
	/**
	 * Checks if this bounding sphere collides with another object.<br>
	 * This method handles collisions with {@link Ray} types.<br>
	 * It updates the provided {@code results} object if a collision occurs.
	 * @param other The {@link Collidable} object to check against.
	 * @param results The {@link CollisionResults} container to store any detected hits.
	 * @return Returns 1 if a collision occurred, or 0 if no collision was found.
	 */
	@Override
	public int collideWith(Collidable other, CollisionResults results)
	{
		if (other instanceof Ray)
		{
			final Ray ray = (Ray) other;
			return collideWithRay(ray, results);
		}
		
		throw new UnsupportedCollisionException();
	}
	
	/**
	 * Checks if a specific point is inside this bounding sphere.<br>
	 * It compares the distance from the {@code point} to the center against the radius.
	 * @param point The {@link Vector3f} position to check.
	 * @return {@code true} if the point is within the boundaries, {@code false} otherwise.
	 */
	@Override
	public boolean contains(Vector3f point)
	{
		return center.distanceSquared(point) < (getRadius() * getRadius());
	}
	
	/**
	 * Checks if a specific point is inside this bounding sphere.<br>
	 * It compares the distance between the {@code point} and the center against the radius.
	 * @param point The {@link Vector3f} position to check.
	 * @return {@code true} if the point is within the bounds, otherwise {@code false}.
	 */
	@Override
	public boolean intersects(Vector3f point)
	{
		return center.distanceSquared(point) <= (getRadius() * getRadius());
	}
	
	/**
	 * Calculates the distance from a given point to the surface of this sphere.<br>
	 * This method subtracts the {@code radius} from the distance between the center and the {@code point}.
	 * @param point The {@link Vector3f} position to check.
	 * @return The distance as a {@code float}.
	 */
	@Override
	public float distanceToEdge(Vector3f point)
	{
		return center.distance(point) - radius;
	}
	
	/**
	 * Calculates the total volume of this {@code BoundingSphere}.<br>
	 * The result is based on the current {@code radius}.
	 * @return The calculated volume as a {@code float}.
	 */
	@Override
	public float getVolume()
	{
		return 4 * FastMath.ONE_THIRD * FastMath.PI * radius * radius * radius;
	}
}
