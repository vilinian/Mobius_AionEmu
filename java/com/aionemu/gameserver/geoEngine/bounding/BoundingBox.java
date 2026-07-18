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

import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResult;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.collision.UnsupportedCollisionException;
import com.aionemu.gameserver.geoEngine.math.Array3f;
import com.aionemu.gameserver.geoEngine.math.FastMath;
import com.aionemu.gameserver.geoEngine.math.Matrix3f;
import com.aionemu.gameserver.geoEngine.math.Matrix4f;
import com.aionemu.gameserver.geoEngine.math.Plane;
import com.aionemu.gameserver.geoEngine.math.Ray;
import com.aionemu.gameserver.geoEngine.math.Triangle;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.Mesh;
import com.aionemu.gameserver.geoEngine.utils.BufferUtils;

// import com.jme.scene.TriMesh;

/**
 * This class defines an axis-aligned cube that acts as a container for a group of geometry vertices.<br>
 * It represents the volume using a center point and extents along the x, y, and z axes.<br>
 * You can define these dimensions by calling {@code com.aionemu.gameserver.geoEngine.math.Vector3f)} or {@code averagePoints}.
 * @author Joshua Slack
 */
public class BoundingBox extends BoundingVolume
{
	float xExtent, yExtent, zExtent;
	
	/**
	 * Creates a new instance of the {@link BoundingBox} class.<br>
	 * This constructor initializes a default bounding box object.
	 */
	public BoundingBox()
	{
	}
	
	/**
	 * Creates a new {@code BoundingBox} with a specific center and extents.<br>
	 * The box is defined by its position in 3D space and its size along each axis.
	 * @param c The center point of the box as a {@code Vector3f}.
	 * @param x The extent of the box along the x-axis.
	 * @param y The extent of the box along the y-axis.
	 * @param z The extent of the box along the z-axis.
	 */
	public BoundingBox(@SuppressWarnings("javadoc") Vector3f c, @SuppressWarnings("javadoc") float x, @SuppressWarnings("javadoc") float y, @SuppressWarnings("javadoc") float z)
	{
		center.set(c);
		xExtent = x;
		yExtent = y;
		zExtent = z;
	}
	
	/**
	 * Creates a new {@code BoundingBox} by copying the data from an existing one.<br>
	 * This method copies the center and all extents from the {@code source}.
	 * @param source The {@link BoundingBox} to copy from.
	 */
	public BoundingBox(BoundingBox source)
	{
		center.set(source.center);
		xExtent = source.xExtent;
		yExtent = source.yExtent;
		zExtent = source.zExtent;
	}
	
	/**
	 * Creates a new {@code BoundingBox} using minimum and maximum coordinates.<br>
	 * This defines the boundaries of the box in 3D space.
	 * @param min The minimum corner of the box as a {@code Vector3f}.
	 * @param max The maximum corner of the box as a {@code Vector3f}.
	 */
	public BoundingBox(Vector3f min, Vector3f max)
	{
		setMinMax(min, max);
	}
	
	/**
	 * Retrieves the type of this bounding volume.<br>
	 * This method returns the {@code AABB} constant.
	 * @return The {@code Type} representing an axis-aligned bounding box.
	 */
	@Override
	public Type getType()
	{
		return Type.AABB;
	}
	
	/**
	 * Calculates the bounding box dimensions based on a set of points.<br>
	 * This method updates the internal extents by calling {@code containAABB}.
	 * @param points The {@code FloatBuffer} containing the vertex data.
	 */
	@Override
	public void computeFromPoints(FloatBuffer points)
	{
		containAABB(points);
	}
	
	/**
	 * Calculates the bounding box dimensions based on a range of triangles.<br>
	 * This method updates the {@code center} and extents using the vertices from the provided array.
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
		
		final Vector3f min = Vector3f.newInstance();
		final Vector3f max = Vector3f.newInstance();
		min.set(new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
		max.set(new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY));
		
		Vector3f point;
		for (int i = start; i < end; i++)
		{
			point = tris[i].get(0);
			checkMinMax(min, max, point);
			point = tris[i].get(1);
			checkMinMax(min, max, point);
			point = tris[i].get(2);
			checkMinMax(min, max, point);
		}
		
		center.set(min.addLocal(max));
		center.multLocal(0.5f);
		
		xExtent = max.x - center.x;
		yExtent = max.y - center.y;
		zExtent = max.z - center.z;
		Vector3f.recycle(min);
		Vector3f.recycle(max);
	}
	
	/**
	 * Calculates the bounding box dimensions based on a range of triangles in a mesh.<br>
	 * This method iterates through indices to find the minimum and maximum coordinates.<br>
	 * It then updates the center and extents of this {@code BoundingBox}.
	 * @param indices The array of triangle indices to process.
	 * @param mesh The {@link Mesh} containing the geometry data.
	 * @param start The starting index in the {@code indices} array.
	 * @param end The ending index in the {@code indices} array.
	 */
	public void computeFromTris(int[] indices, Mesh mesh, int start, int end)
	{
		if ((end - start) <= 0)
		{
			return;
		}
		
		final Vector3f vect1 = Vector3f.newInstance();
		final Vector3f vect2 = Vector3f.newInstance();
		final Triangle triangle = Triangle.newInstance();
		
		final Vector3f min = vect1.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		final Vector3f max = vect2.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
		Vector3f point;
		
		for (int i = start; i < end; i++)
		{
			mesh.getTriangle(indices[i], triangle);
			point = triangle.get(0);
			checkMinMax(min, max, point);
			point = triangle.get(1);
			checkMinMax(min, max, point);
			point = triangle.get(2);
			checkMinMax(min, max, point);
		}
		
		center.set(min.addLocal(max));
		center.multLocal(0.5f);
		
		xExtent = max.x - center.x;
		yExtent = max.y - center.y;
		zExtent = max.z - center.z;
		Vector3f.recycle(vect1);
		Vector3f.recycle(vect2);
		Triangle.recycle(triangle);
	}
	
	/**
	 * Updates the boundaries of a bounding box to include a new point.<br>
	 * This method checks if {@code point} is outside the current range defined by {@code min} and {@code max}.<br>
	 * If it is outside, the corresponding coordinates in {@code min} or {@code max} are updated.
	 * @param min The minimum corner of the bounding box.
	 * @param max The maximum corner of the bounding box.
	 * @param point The new point to check against the boundaries.
	 */
	public static void checkMinMax(Vector3f min, Vector3f max, Vector3f point)
	{
		if (point.x < min.x)
		{
			min.x = point.x;
		}
		
		if (point.x > max.x)
		{
			max.x = point.x;
		}
		
		if (point.y < min.y)
		{
			min.y = point.y;
		}
		
		if (point.y > max.y)
		{
			max.y = point.y;
		}
		
		if (point.z < min.z)
		{
			min.z = point.z;
		}
		
		if (point.z > max.z)
		{
			max.z = point.z;
		}
	}
	
	/**
	 * Updates the bounding box dimensions based on a set of points.<br>
	 * This method calculates the new center and extents from the provided {@code FloatBuffer}.<br>
	 * It ignores the input if it is {@code null} or contains fewer than three floats.
	 * @param points The buffer containing the 3D coordinates to process.
	 */
	public void containAABB(FloatBuffer points)
	{
		if (points == null)
		{
			return;
		}
		
		points.rewind();
		if (points.remaining() <= 2) // we need at least a 3 float vector
		{
			return;
		}
		
		final Vector3f vect1 = Vector3f.newInstance();
		BufferUtils.populateFromBuffer(vect1, points, 0);
		float minX = vect1.x, minY = vect1.y, minZ = vect1.z;
		float maxX = vect1.x, maxY = vect1.y, maxZ = vect1.z;
		
		for (int i = 1, len = points.remaining() / 3; i < len; i++)
		{
			BufferUtils.populateFromBuffer(vect1, points, i);
			if (vect1.x < minX)
			{
				minX = vect1.x;
			}
			else if (vect1.x > maxX)
			{
				maxX = vect1.x;
			}
			
			if (vect1.y < minY)
			{
				minY = vect1.y;
			}
			else if (vect1.y > maxY)
			{
				maxY = vect1.y;
			}
			
			if (vect1.z < minZ)
			{
				minZ = vect1.z;
			}
			else if (vect1.z > maxZ)
			{
				maxZ = vect1.z;
			}
		}
		
		Vector3f.recycle(vect1);
		
		center.set(minX + maxX, minY + maxY, minZ + maxZ);
		center.multLocal(0.5f);
		
		xExtent = maxX - center.x;
		yExtent = maxY - center.y;
		zExtent = maxZ - center.z;
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
		BoundingBox box;
		if ((store == null) || (store.getType() != Type.AABB))
		{
			box = new BoundingBox();
		}
		else
		{
			box = (BoundingBox) store;
		}
		
		final float w = trans.multProj(center, box.center);
		box.center.divideLocal(w);
		
		final Matrix3f transMatrix = Matrix3f.newInstance();
		trans.toRotationMatrix(transMatrix);
		
		// Make the rotation matrix all positive to get the maximum x/y/z extent
		transMatrix.absoluteLocal();
		final Vector3f vect1 = Vector3f.newInstance();
		vect1.set(xExtent, yExtent, zExtent);
		transMatrix.mult(vect1, vect1);
		
		// Assign the biggest rotations after scales.
		box.xExtent = FastMath.abs(vect1.getX());
		box.yExtent = FastMath.abs(vect1.getY());
		box.zExtent = FastMath.abs(vect1.getZ());
		Vector3f.recycle(vect1);
		Matrix3f.recycle(transMatrix);
		
		return box;
	}
	
	/**
	 * Determines which side of a {@link Plane} this bounding box is on.<br>
	 * It compares the distance from the center to the plane against the box extents.
	 * @param plane The {@link Plane} used for the calculation.
	 * @return The {@code Side} representing Positive, Negative, or None.
	 */
	@Override
	public Plane.Side whichSide(Plane plane)
	{
		final float radius = FastMath.abs(xExtent * plane.getNormal().getX()) + FastMath.abs(yExtent * plane.getNormal().getY()) + FastMath.abs(zExtent * plane.getNormal().getZ());
		
		final float distance = plane.pseudoDistance(center);
		
		// changed to < and > to prevent floating point precision problems
		if (distance < -radius)
		{
			return Plane.Side.Negative;
		}
		else if (distance > radius)
		{
			return Plane.Side.Positive;
		}
		else
		{
			return Plane.Side.None;
		}
	}
	
	/**
	 * Combines the current bounding volume with another <link Javadoc>BoundingVolume</link>.<br>
	 * This method creates a new volume that encompasses both objects.<br>
	 * It returns {@code null} if the provided volume type is not supported.
	 * @param volume The <link Javadoc>BoundingVolume</link to merge with this one.
	 * @return A new <link Javadoc>BoundingVolume</link containing both volumes, or {@code null}.
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
			case AABB:
			{
				final BoundingBox vBox = (BoundingBox) volume;
				return merge(vBox.center, vBox.xExtent, vBox.yExtent, vBox.zExtent, new BoundingBox(new Vector3f(0, 0, 0), 0, 0, 0));
			}
			
			// case OBB: {
			// OrientedBoundingBox box = (OrientedBoundingBox) volume;
			// BoundingBox rVal = (BoundingBox) this.clone(null);
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
			case AABB:
			{
				final BoundingBox vBox = (BoundingBox) volume;
				return merge(vBox.center, vBox.xExtent, vBox.yExtent, vBox.zExtent, this);
			}
			
			// case OBB: {
			// return mergeOBB((OrientedBoundingBox) volume);
			// }
			default:
				return null;
		}
	}
	
	/**
	 * Merges this AABB with the given OBB.
	 * @param volume the OBB to merge this AABB with.
	 * @return This AABB extended to fit the given OBB.
	 */
	// private BoundingBox mergeOBB(OrientedBoundingBox volume) {
	// if (!volume.correctCorners)
	// volume.computeCorners();
	//
	// TempVars vars = TempVars.get();
	// Vector3f min = vars.compVect1.set(center.x - xExtent, center.y - yExtent,
	// center.z - zExtent);
	// Vector3f max = vars.compVect2.set(center.x + xExtent, center.y + yExtent,
	// center.z + zExtent);
	//
	// for (int i = 1; i < volume.vectorStore.length; i++) {
	// Vector3f temp = volume.vectorStore[i];
	// if (temp.x < min.x)
	// min.x = temp.x;
	// else if (temp.x > max.x)
	// max.x = temp.x;
	//
	// if (temp.y < min.y)
	// min.y = temp.y;
	// else if (temp.y > max.y)
	// max.y = temp.y;
	//
	// if (temp.z < min.z)
	// min.z = temp.z;
	// else if (temp.z > max.z)
	// max.z = temp.z;
	// }
	//
	// center.set(min.addLocal(max));
	// center.multLocal(0.5f);
	//
	// xExtent = max.x - center.x;
	// yExtent = max.y - center.y;
	// zExtent = max.z - center.z;
	// return this;
	// }
	
	/**
	 * Merges the current bounding box with another box defined by a center and extents.<br>
	 * This method updates the internal dimensions to encompass both volumes.<br>
	 * It modifies the provided {@code BoundingBox} object directly.
	 * @param boxCenter The center point of the second box.
	 * @param boxX The x-axis extent of the second box.
	 * @param boxY The y-axis extent of the second box.
	 * @param boxZ The z-axis extent of the second box.
	 * @param rVal The {@code BoundingBox} instance to be updated and returned.
	 * @return The modified {@code BoundingBox} object.
	 */
	private BoundingBox merge(Vector3f boxCenter, float boxX, float boxY, float boxZ, BoundingBox rVal)
	{
		final Vector3f vect1 = Vector3f.newInstance();
		final Vector3f vect2 = Vector3f.newInstance();
		
		vect1.x = center.x - xExtent;
		if (vect1.x > (boxCenter.x - boxX))
		{
			vect1.x = boxCenter.x - boxX;
		}
		
		vect1.y = center.y - yExtent;
		if (vect1.y > (boxCenter.y - boxY))
		{
			vect1.y = boxCenter.y - boxY;
		}
		
		vect1.z = center.z - zExtent;
		if (vect1.z > (boxCenter.z - boxZ))
		{
			vect1.z = boxCenter.z - boxZ;
		}
		
		vect2.x = center.x + xExtent;
		if (vect2.x < (boxCenter.x + boxX))
		{
			vect2.x = boxCenter.x + boxX;
		}
		
		vect2.y = center.y + yExtent;
		if (vect2.y < (boxCenter.y + boxY))
		{
			vect2.y = boxCenter.y + boxY;
		}
		
		vect2.z = center.z + zExtent;
		if (vect2.z < (boxCenter.z + boxZ))
		{
			vect2.z = boxCenter.z + boxZ;
		}
		
		center.set(vect2).addLocal(vect1).multLocal(0.5f);
		
		xExtent = vect2.x - center.x;
		yExtent = vect2.y - center.y;
		zExtent = vect2.z - center.z;
		
		Vector3f.recycle(vect1);
		Vector3f.recycle(vect2);
		return rVal;
	}
	
	/**
	 * Creates a copy of this {@code BoundingBox}.<br>
	 * It attempts to reuse the provided {@code store} if it is an AABB.<br>
	 * Otherwise, it creates and returns a new instance.
	 * @param store The volume to use as a template for cloning.
	 * @return A new or reused {@code BoundingBox} instance.
	 */
	@Override
	public BoundingBox clone(BoundingVolume store)
	{
		if ((store != null) && (store.getType() == Type.AABB))
		{
			final BoundingBox rVal = (BoundingBox) store;
			rVal.center.set(center);
			rVal.xExtent = xExtent;
			rVal.yExtent = yExtent;
			rVal.zExtent = zExtent;
			rVal.checkPlane = checkPlane;
			return rVal;
		}
		
		final BoundingBox rVal = new BoundingBox(center.clone(), xExtent, yExtent, zExtent);
		return rVal;
	}
	
	/**
	 * Returns a string representation of the {@code BoundingBox}.<br>
	 * This includes the class name and its dimensions.
	 * @return A formatted string containing the center and extents.
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " [Center: " + center + "  xExtent: " + xExtent + "  yExtent: " + yExtent + "  zExtent: " + zExtent + "]";
	}
	
	/**
	 * Checks if this {@code BoundingBox} overlaps with a given {@code BoundingSphere}.<br>
	 * This method uses the center and extents of the box to determine collision.
	 * @param bs The {@code BoundingSphere} to check against.
	 * @return {@code true} if the objects intersect, otherwise {@code false}.
	 */
	@SuppressWarnings("javadoc")
	@Override
	public boolean intersectsSphere(BoundingSphere bs)
	{
		return ((FastMath.abs(center.x - bs.center.x) < (bs.getRadius() + xExtent)) && (FastMath.abs(center.y - bs.center.y) < (bs.getRadius() + yExtent)) && (FastMath.abs(center.z - bs.center.z) < (bs.getRadius() + zExtent)));
	}
	
	/**
	 * Checks if this bounding box overlaps with another volume.<br>
	 * It delegates the calculation to the {@code intersectsBoundingBox} method of the provided object.
	 * @param bv The {@link BoundingVolume} to check against.
	 * @return {@code true} if the volumes intersect, {@code false} otherwise.
	 */
	@SuppressWarnings("javadoc")
	@Override
	public boolean intersects(BoundingVolume bv)
	{
		return bv.intersectsBoundingBox(this);
	}
	
	/**
	 * Checks if this bounding box overlaps with another <code class="BoundingBox">.<br>
	 * It compares the extents of both boxes to see if they occupy the same space.
	 * @param bb The other <code class="BoundingBox"> to check against.
	 * @return {@code true} if the boxes intersect, otherwise {@code false}.
	 */
	@SuppressWarnings("javadoc")
	@Override
	public boolean intersectsBoundingBox(BoundingBox bb)
	{
		assert Vector3f.isValidVector(center) && Vector3f.isValidVector(bb.center);
		
		if (((center.x + xExtent) < (bb.center.x - bb.xExtent)) || ((center.x - xExtent) > (bb.center.x + bb.xExtent)))
		{
			return false;
		}
		else if (((center.y + yExtent) < (bb.center.y - bb.yExtent)) || ((center.y - yExtent) > (bb.center.y + bb.yExtent)))
		{
			return false;
		}
		else if (((center.z + zExtent) < (bb.center.z - bb.zExtent)) || ((center.z - zExtent) > (bb.center.z + bb.zExtent)))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * determines if this bounding box intersects with a given oriented bounding box.
	 * @see com.jme.bounding.BoundingVolume#intersectsOrientedBoundingBox(com.jme.bounding.OrientedBoundingBox)
	 */
	// public boolean intersectsOrientedBoundingBox(OrientedBoundingBox obb) {
	// return obb.intersectsBoundingBox(this);
	// }
	
	/**
	 * Checks if the given {@code Ray} intersects with this bounding box.<br>
	 * This method uses axis-aligned calculations to determine collision.
	 * @param ray The {@code Ray} to test against the box.
	 * @return {@code true} if the ray hits the box, otherwise {@code false}.
	 */
	@SuppressWarnings("javadoc")
	@Override
	public boolean intersects(Ray ray)
	{
		// assert Vector3f.isValidVector(center);
		
		float rhs;
		
		final Vector3f vect1 = Vector3f.newInstance();
		final Vector3f vect2 = Vector3f.newInstance();
		final Vector3f diff = ray.origin.subtract(getCenter(vect2), vect1);
		
		final Array3f fWdU = Array3f.newInstance();
		final Array3f fAWdU = Array3f.newInstance();
		final Array3f fDdU = Array3f.newInstance();
		final Array3f fADdU = Array3f.newInstance();
		final Array3f fAWxDdU = Array3f.newInstance();
		
		fWdU.a = ray.getDirection().dot(Vector3f.UNIT_X);
		fAWdU.a = FastMath.abs(fWdU.a);
		fDdU.a = diff.dot(Vector3f.UNIT_X);
		fADdU.a = FastMath.abs(fDdU.a);
		if ((fADdU.a > xExtent) && ((fDdU.a * fWdU.a) >= 0.0))
		{
			Vector3f.recycle(vect1);
			Vector3f.recycle(vect2);
			Array3f.recycle(fWdU);
			Array3f.recycle(fAWdU);
			Array3f.recycle(fDdU);
			Array3f.recycle(fADdU);
			Array3f.recycle(fAWxDdU);
			return false;
		}
		
		fWdU.b = ray.getDirection().dot(Vector3f.UNIT_Y);
		fAWdU.b = FastMath.abs(fWdU.b);
		fDdU.b = diff.dot(Vector3f.UNIT_Y);
		fADdU.b = FastMath.abs(fDdU.b);
		if ((fADdU.b > yExtent) && ((fDdU.b * fWdU.b) >= 0.0))
		{
			Vector3f.recycle(vect1);
			Vector3f.recycle(vect2);
			Array3f.recycle(fWdU);
			Array3f.recycle(fAWdU);
			Array3f.recycle(fDdU);
			Array3f.recycle(fADdU);
			Array3f.recycle(fAWxDdU);
			return false;
		}
		
		fWdU.c = ray.getDirection().dot(Vector3f.UNIT_Z);
		fAWdU.c = FastMath.abs(fWdU.c);
		fDdU.c = diff.dot(Vector3f.UNIT_Z);
		fADdU.c = FastMath.abs(fDdU.c);
		if ((fADdU.c > zExtent) && ((fDdU.c * fWdU.c) >= 0.0))
		{
			Vector3f.recycle(vect1);
			Vector3f.recycle(vect2);
			Array3f.recycle(fWdU);
			Array3f.recycle(fAWdU);
			Array3f.recycle(fDdU);
			Array3f.recycle(fADdU);
			Array3f.recycle(fAWxDdU);
			return false;
		}
		
		final Vector3f wCrossD = ray.getDirection().cross(diff, vect2);
		
		fAWxDdU.a = FastMath.abs(wCrossD.dot(Vector3f.UNIT_X));
		rhs = (yExtent * fAWdU.c) + (zExtent * fAWdU.b);
		if (fAWxDdU.a > rhs)
		{
			Vector3f.recycle(vect1);
			Vector3f.recycle(vect2);
			Array3f.recycle(fWdU);
			Array3f.recycle(fAWdU);
			Array3f.recycle(fDdU);
			Array3f.recycle(fADdU);
			Array3f.recycle(fAWxDdU);
			return false;
		}
		
		fAWxDdU.b = FastMath.abs(wCrossD.dot(Vector3f.UNIT_Y));
		rhs = (xExtent * fAWdU.c) + (zExtent * fAWdU.a);
		if (fAWxDdU.b > rhs)
		{
			Vector3f.recycle(vect1);
			Vector3f.recycle(vect2);
			Array3f.recycle(fWdU);
			Array3f.recycle(fAWdU);
			Array3f.recycle(fDdU);
			Array3f.recycle(fADdU);
			Array3f.recycle(fAWxDdU);
			return false;
		}
		
		fAWxDdU.c = FastMath.abs(wCrossD.dot(Vector3f.UNIT_Z));
		rhs = (xExtent * fAWdU.b) + (yExtent * fAWdU.a);
		if (fAWxDdU.c > rhs)
		{
			Vector3f.recycle(vect1);
			Vector3f.recycle(vect2);
			Array3f.recycle(fWdU);
			Array3f.recycle(fAWdU);
			Array3f.recycle(fDdU);
			Array3f.recycle(fADdU);
			Array3f.recycle(fAWxDdU);
			return false;
		}
		
		Vector3f.recycle(vect1);
		Vector3f.recycle(vect2);
		Array3f.recycle(fWdU);
		Array3f.recycle(fAWdU);
		Array3f.recycle(fDdU);
		Array3f.recycle(fADdU);
		Array3f.recycle(fAWxDdU);
		return true;
	}
	
	/**
	 * Checks if the given {@code Ray} intersects with this bounding box.<br>
	 * It updates the {@code CollisionResults} object with any hit points found.
	 * @param ray The {@code Ray} used to test for collision.
	 * @param results The {@code CollisionResults} container where hits are stored.
	 * @return The number of intersection points found, which can be 0, 1, or 2.
	 */
	@SuppressWarnings("javadoc")
	private int collideWithRay(@SuppressWarnings("javadoc") Ray ray, @SuppressWarnings("javadoc") CollisionResults results)
	{
		final Vector3f diff = Vector3f.newInstance().set(ray.origin).subtractLocal(center);
		final Vector3f direction = Vector3f.newInstance().set(ray.direction);
		
		final float[] t =
		{
			0f,
			Float.POSITIVE_INFINITY
		};
		
		final float saveT0 = t[0], saveT1 = t[1];
		final boolean notEntirelyClipped = clip(+direction.x, -diff.x - xExtent, t) && clip(-direction.x, +diff.x - xExtent, t) && clip(+direction.y, -diff.y - yExtent, t) && clip(-direction.y, +diff.y - yExtent, t) && clip(+direction.z, -diff.z - zExtent, t) && clip(-direction.z, +diff.z - zExtent, t);
		Vector3f.recycle(diff);
		Vector3f.recycle(direction);
		
		if (notEntirelyClipped && ((t[0] != saveT0) || (t[1] != saveT1)))
		{
			if (t[1] > t[0])
			{
				final float[] distances = t;
				final Vector3f[] points = new Vector3f[]
				{
					new Vector3f(ray.direction).multLocal(distances[0]).addLocal(ray.origin),
					new Vector3f(ray.direction).multLocal(distances[1]).addLocal(ray.origin)
				};
				
				CollisionResult result = new CollisionResult(points[0], distances[0]);
				results.addCollision(result);
				result = new CollisionResult(points[1], distances[1]);
				results.addCollision(result);
				return 2;
			}
			
			final Vector3f point = new Vector3f(ray.direction).multLocal(t[0]).addLocal(ray.origin);
			final CollisionResult result = new CollisionResult(point, t[0]);
			results.addCollision(result);
			return 1;
		}
		
		return 0;
	}
	
	/**
	 * Checks if this bounding box collides with another object.<br>
	 * This method handles collisions with {@link Ray} and {@link Triangle} types.<br>
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
		else if (other instanceof Triangle)
		{
			final Triangle t = (Triangle) other;
			if (intersects(t.get1(), t.get2(), t.get3()))
			{
				final CollisionResult r = new CollisionResult();
				results.addCollision(r);
				return 1;
			}
			
			return 0;
		}
		else
		{
			throw new UnsupportedCollisionException("With: " + other.getClass().getSimpleName());
		}
	}
	
	/**
	 * Checks if this bounding box intersects with a triangle.<br>
	 * The triangle is defined by three vertices.
	 * @param v1 The first vertex of the triangle.
	 * @param v2 The second vertex of the triangle.
	 * @param v3 The third vertex of the triangle.
	 * @return {@code true} if an intersection occurs, otherwise {@code false}.
	 */
	public boolean intersects(Vector3f v1, Vector3f v2, Vector3f v3)
	{
		return Intersection.intersect(this, v1, v2, v3);
	}
	
	/**
	 * Checks if a specific point is inside this bounding box.<br>
	 * It compares the coordinates of the {@code point} against the center and extents.
	 * @param point The {@link Vector3f} position to check.
	 * @return {@code true} if the point is within the boundaries, {@code false} otherwise.
	 */
	@Override
	public boolean contains(Vector3f point)
	{
		return (FastMath.abs(center.x - point.x) < xExtent) && (FastMath.abs(center.y - point.y) < yExtent) && (FastMath.abs(center.z - point.z) < zExtent);
	}
	
	/**
	 * Checks if a specific point is inside this bounding box.<br>
	 * It compares the coordinates of the {@code point} against the center and extents.
	 * @param point The {@link Vector3f} position to check.
	 * @return {@code true} if the point is within the bounds, otherwise {@code false}.
	 */
	@Override
	public boolean intersects(Vector3f point)
	{
		return (FastMath.abs(center.x - point.x) <= xExtent) && (FastMath.abs(center.y - point.y) <= yExtent) && (FastMath.abs(center.z - point.z) <= zExtent);
	}
	
	/**
	 * Calculates the shortest distance from a given point to the edge of this box.<br>
	 * This method determines how far the {@code point} is from the nearest surface.
	 * @param point The {@link Vector3f} position to check.
	 * @return The distance as a {@code float}.
	 */
	@Override
	public float distanceToEdge(Vector3f point)
	{
		// compute coordinates of point in box coordinate system
		final Vector3f closest = point.subtract(center);
		
		// project test point onto box
		float sqrDistance = 0.0f;
		float delta;
		
		if (closest.x < -xExtent)
		{
			delta = closest.x + xExtent;
			sqrDistance += delta * delta;
			closest.x = -xExtent;
		}
		else if (closest.x > xExtent)
		{
			delta = closest.x - xExtent;
			sqrDistance += delta * delta;
			closest.x = xExtent;
		}
		
		if (closest.y < -yExtent)
		{
			delta = closest.y + yExtent;
			sqrDistance += delta * delta;
			closest.y = -yExtent;
		}
		else if (closest.y > yExtent)
		{
			delta = closest.y - yExtent;
			sqrDistance += delta * delta;
			closest.y = yExtent;
		}
		
		if (closest.z < -zExtent)
		{
			delta = closest.z + zExtent;
			sqrDistance += delta * delta;
			closest.z = -zExtent;
		}
		else if (closest.z > zExtent)
		{
			delta = closest.z - zExtent;
			sqrDistance += delta * delta;
			closest.z = zExtent;
		}
		
		return FastMath.sqrt(sqrDistance);
	}
	
	/**
	 * Determines if a line segment intersects the current test plane.<br>
	 * Updates the {@code t} array with the intersection point if it occurs.<br>
	 * Returns {@code false} if the segment is entirely clipped.
	 * @param denom The denominator used in the clipping calculation.
	 * @param numer The numerator used in the clipping calculation.
	 * @param t A float array containing the start and end points of the segment.
	 * @return {@code true} if the segment intersects or touches the plane, {@code false} otherwise.
	 */
	private boolean clip(float denom, float numer, float[] t)
	{
		// Return true if the line segment intersects the current test plane; otherwise, return false as the line segment is entirely clipped.
		if (denom > 0.0f)
		{
			if (numer > (denom * t[1]))
			{
				return false;
			}
			
			if (numer > (denom * t[0]))
			{
				t[0] = numer / denom;
			}
			
			return true;
		}
		else if (denom < 0.0f)
		{
			if (numer > (denom * t[0]))
			{
				return false;
			}
			
			if (numer > (denom * t[1]))
			{
				t[1] = numer / denom;
			}
			
			return true;
		}
		else
		{
			return numer <= 0.0;
		}
	}
	
	/**
	 * Retrieves the dimensions of this bounding box.<br>
	 * The values are set into a {@code Vector3f} object.<br>
	 * If the provided {@code store} is {@code null}, a new instance is created.
	 * @param store The {@code Vector3f} to store the extent values in.
	 * @return The populated {@code Vector3f} containing x, y, and z extents.
	 */
	public Vector3f getExtent(Vector3f store)
	{
		if (store == null)
		{
			store = new Vector3f();
		}
		
		store.set(xExtent, yExtent, zExtent);
		return store;
	}
	
	/**
	 * Gets the size of the bounding box along the x-axis.<br>
	 * This value represents the distance from the center to the edge.
	 * @return The {@code float} extent of the box on the x-axis.
	 */
	public float getXExtent()
	{
		return xExtent;
	}
	
	/**
	 * Gets the size of the bounding box along the Y axis.<br>
	 * This value represents the distance from the center to the edge.
	 * @return The {@code float} value of the Y extent.
	 */
	public float getYExtent()
	{
		return yExtent;
	}
	
	/**
	 * Retrieves the size of the bounding box along the {@code z} axis.<br>
	 * This value represents the distance from the center to the edge.
	 * @return The {@code z} extent as a {@code float}.
	 */
	public float getZExtent()
	{
		return zExtent;
	}
	
	/**
	 * Sets the size of the bounding box along the {@code x} axis.<br>
	 * The value must be a non-negative number.
	 * @param xExtent The new extent for the {@code x} axis.
	 */
	public void setXExtent(float xExtent)
	{
		if (xExtent < 0)
		{
			throw new IllegalArgumentException();
		}
		
		this.xExtent = xExtent;
	}
	
	/**
	 * Sets the vertical extent of the bounding box.<br>
	 * This value represents the distance from the center along the y-axis.<br>
	 * The provided value must be non-negative.
	 * @param yExtent The new y-extent value to set.
	 */
	public void setYExtent(float yExtent)
	{
		if (yExtent < 0)
		{
			throw new IllegalArgumentException();
		}
		
		this.yExtent = yExtent;
	}
	
	/**
	 * Sets the size of the bounding box along the {@code z} axis.<br>
	 * This value must be a non-negative number.
	 * @param zExtent The size to set for the {@code z} extent.
	 */
	public void setZExtent(float zExtent)
	{
		if (zExtent < 0)
		{
			throw new IllegalArgumentException();
		}
		
		this.zExtent = zExtent;
	}
	
	/**
	 * Calculates the minimum corner of the bounding box.<br>
	 * This method subtracts the extents from the center point.<br>
	 * It updates and returns a {@code Vector3f} object.
	 * @param store The {@code Vector3f} to store the result in. If {@code null}, a new instance is created.
	 * @return The resulting minimum corner as a {@code Vector3f}.
	 */
	public Vector3f getMin(Vector3f store)
	{
		if (store == null)
		{
			store = new Vector3f();
		}
		
		store.set(center).subtractLocal(xExtent, yExtent, zExtent);
		return store;
	}
	
	/**
	 * Calculates the maximum corner of this bounding box.<br>
	 * This method adds the extents to the center point.<br>
	 * It updates and returns the provided {@code Vector3f} object.
	 * @param store The {@code Vector3f} to store the result in. If {@code null}, a new instance is created.
	 * @return The updated {@code Vector3f} representing the maximum corner.
	 */
	public Vector3f getMax(Vector3f store)
	{
		if (store == null)
		{
			store = new Vector3f();
		}
		
		store.set(center).addLocal(xExtent, yExtent, zExtent);
		return store;
	}
	
	/**
	 * Sets the boundaries of the bounding box using minimum and maximum coordinates.<br>
	 * This method updates the internal center and extents based on the provided {@code Vector3f} values.
	 * @param min The minimum corner of the bounding box.
	 * @param max The maximum corner of the bounding box.
	 */
	public void setMinMax(Vector3f min, Vector3f max)
	{
		center.set(max).addLocal(min).multLocal(0.5f);
		xExtent = FastMath.abs(max.x - center.x);
		yExtent = FastMath.abs(max.y - center.y);
		zExtent = FastMath.abs(max.z - center.z);
	}
	
	/**
	 * Calculates the total volume of this {@code BoundingBox}.<br>
	 * The result is based on the current {@code xExtent}, {@code yExtent}, and {@code zExtent}.
	 * @return The calculated volume as a {@code float}.
	 */
	@Override
	public float getVolume()
	{
		return (8 * xExtent * yExtent * zExtent);
	}
}
