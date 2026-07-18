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

import static java.lang.Math.max;

import java.nio.FloatBuffer;

import com.aionemu.gameserver.geoEngine.bounding.BoundingBox;
import com.aionemu.gameserver.geoEngine.bounding.BoundingVolume;
import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.collision.UnsupportedCollisionException;
import com.aionemu.gameserver.geoEngine.math.FastMath;
import com.aionemu.gameserver.geoEngine.math.Matrix4f;
import com.aionemu.gameserver.geoEngine.math.Ray;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.CollisionData;
import com.aionemu.gameserver.geoEngine.scene.Mesh;
import com.aionemu.gameserver.geoEngine.scene.VertexBuffer.Type;
import com.aionemu.gameserver.geoEngine.scene.mesh.IndexBuffer;

/**
 * Represents a Bounding Interval Hierarchy (BIH) for efficient collision detection.<br>
 * This class provides spatial partitioning to speed up queries against complex {@link Mesh} objects.<br>
 * It allows the engine to quickly determine if a {@link Ray} or other volume intersects with geometry.
 */
public class BIHTree implements CollisionData
{
	public static final int MAX_TREE_DEPTH = 100;
	public static final int MAX_TRIS_PER_NODE = 21;
	private BIHNode root;
	private int maxTrisPerNode;
	private int numTris;
	private float[] pointData;
	private int[] triIndices;
	private transient float[] bihSwapTmp;
	private static final TriangleAxisComparator[] comparators = new TriangleAxisComparator[3];
	
	static
	{
		comparators[0] = new TriangleAxisComparator(0);
		comparators[1] = new TriangleAxisComparator(1);
		comparators[2] = new TriangleAxisComparator(2);
	}
	
	/**
	 * Initializes the triangle data for the {@code BIHTree}.<br>
	 * This method extracts vertex positions from the {@code FloatBuffer} and indices from the {@code IndexBuffer}.<br>
	 * It populates the internal {@code pointData} array and creates a list of initial {@code triIndices}.
	 * @param vb The buffer containing the vertex data.
	 * @param ib The buffer containing the triangle indices.
	 */
	private void initTriList(FloatBuffer vb, IndexBuffer ib)
	{
		pointData = new float[numTris * 3 * 3];
		int p = 0;
		for (int i = 0; i < (numTris * 3); i += 3)
		{
			int vert = ib.get(i) * 3;
			pointData[p++] = vb.get(vert++);
			pointData[p++] = vb.get(vert++);
			pointData[p++] = vb.get(vert);
			
			vert = ib.get(i + 1) * 3;
			pointData[p++] = vb.get(vert++);
			pointData[p++] = vb.get(vert++);
			pointData[p++] = vb.get(vert);
			
			vert = ib.get(i + 2) * 3;
			pointData[p++] = vb.get(vert++);
			pointData[p++] = vb.get(vert++);
			pointData[p++] = vb.get(vert);
		}
		
		triIndices = new int[numTris];
		for (int i = 0; i < numTris; i++)
		{
			triIndices[i] = i;
		}
	}
	
	/**
	 * Creates a new {@link BIHTree} instance for a specific {@link Mesh}.<br>
	 * This constructor initializes the internal triangle list from the mesh data.<br>
	 * It requires a valid mesh and a positive number of triangles per node.
	 * @param mesh The {@link Mesh} used to build the tree.
	 * @param maxTrisPerNode The maximum number of triangles allowed in each node.
	 */
	public BIHTree(Mesh mesh, int maxTrisPerNode)
	{
		this.maxTrisPerNode = maxTrisPerNode;
		
		if ((maxTrisPerNode < 1) || (mesh == null))
		{
			throw new IllegalArgumentException();
		}
		
		bihSwapTmp = new float[9];
		
		final FloatBuffer vb = (FloatBuffer) mesh.getBuffer(Type.Position).getData();
		final IndexBuffer ib = mesh.getIndexBuffer();
		
		numTris = ib.size() / 3;
		initTriList(vb, ib);
	}
	
	/**
	 * Creates a new {@link BIHTree} using the default settings.<br>
	 * This constructor uses the constant {@code MAX_TRIS_PER_NODE} for tree construction.
	 * @param mesh The {@link Mesh} used to build the collision tree.
	 */
	public BIHTree(Mesh mesh)
	{
		this(mesh, MAX_TRIS_PER_NODE);
	}
	
	/**
	 * Creates a new empty instance of {@code BIHTree}.<br>
	 * This constructor initializes the object without any mesh data.<br>
	 * You should call {@code construct} after initialization to build the tree.
	 */
	public BIHTree()
	{
	}
	
	/**
	 * Builds the {@code BIHTree} structure from existing triangle data.<br>
	 * This method initializes the root node and organizes the spatial hierarchy.
	 */
	public void construct()
	{
		final BoundingBox sceneBbox = createBox(0, numTris - 1);
		root = createNode(0, numTris - 1, sceneBbox, 0);
	}
	
	/**
	 * Creates a {@link BoundingBox} for a range of triangles.<br>
	 * It calculates the minimum and maximum bounds from indices {@code l} to {@code r}.
	 * @param l The starting index of the triangle range.
	 * @param r The ending index of the triangle range.
	 * @return A new {@link BoundingBox} containing all triangles in the specified range.
	 */
	private BoundingBox createBox(int l, int r)
	{
		final Vector3f min = Vector3f.newInstance();
		final Vector3f max = Vector3f.newInstance();
		min.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		max.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
		
		final Vector3f v1 = Vector3f.newInstance();
		final Vector3f v2 = Vector3f.newInstance();
		final Vector3f v3 = Vector3f.newInstance();
		
		for (int i = l; i <= r; i++)
		{
			getTriangle(i, v1, v2, v3);
			BoundingBox.checkMinMax(min, max, v1);
			BoundingBox.checkMinMax(min, max, v2);
			BoundingBox.checkMinMax(min, max, v3);
		}
		
		final BoundingBox bbox = new BoundingBox(min, max);
		Vector3f.recycle(min);
		Vector3f.recycle(max);
		Vector3f.recycle(v1);
		Vector3f.recycle(v2);
		Vector3f.recycle(v3);
		return bbox;
	}
	
	/**
	 * Retrieves the actual triangle index from the internal array.<br>
	 * This method maps a local position to the global {@code triIndices}.
	 * @param triIndex The local index within the current tree structure.
	 * @return The corresponding global triangle index.
	 */
	int getTriangleIndex(int triIndex)
	{
		return triIndices[triIndex];
	}
	
	/**
	 * Sorts triangles within a specific range based on their position relative to a split value.<br>
	 * This method rearranges the triangle indices to organize them into two groups.<br>
	 * It uses a partitioning logic similar to the quicksort algorithm.
	 * @param l The starting index of the triangle range.
	 * @param r The ending index of the triangle range.
	 * @param split The coordinate value used to divide the triangles.
	 * @param axis The dimension along which the split is performed.
	 * @return The final pivot index where the two groups meet.
	 */
	private int sortTriangles(int l, int r, float split, int axis)
	{
		int pivot = l;
		int j = r;
		
		final Vector3f v1 = Vector3f.newInstance(), v2 = Vector3f.newInstance(), v3 = Vector3f.newInstance();
		
		while (pivot <= j)
		{
			getTriangle(pivot, v1, v2, v3);
			v1.addLocal(v2).addLocal(v3).multLocal(FastMath.ONE_THIRD);
			if (v1.get(axis) > split)
			{
				swapTriangles(pivot, j);
				--j;
			}
			else
			{
				++pivot;
			}
		}
		
		Vector3f.recycle(v1);
		Vector3f.recycle(v2);
		Vector3f.recycle(v3);
		pivot = ((pivot == l) && (j < pivot)) ? j : pivot;
		return pivot;
	}
	
	/**
	 * Updates the minimum or maximum bounds of a {@link BoundingBox}.<br>
	 * This method modifies either the min or max coordinate based on the provided axis.
	 * @param bbox The {@link BoundingBox} to be updated.
	 * @param doMin If {@code true}, updates the minimum value; if {@code false}, updates the maximum value.
	 * @param axis The index of the axis to modify (e.g., 0, 1, or 2).
	 * @param value The new coordinate value to set.
	 */
	private void setMinMax(BoundingBox bbox, boolean doMin, int axis, float value)
	{
		final Vector3f min = bbox.getMin(null);
		final Vector3f max = bbox.getMax(null);
		
		if (doMin)
		{
			min.set(axis, value);
		}
		else
		{
			max.set(axis, value);
		}
		
		bbox.setMinMax(min, max);
	}
	
	/**
	 * Retrieves a specific coordinate from a {@link BoundingBox}.<br>
	 * It returns either the minimum or maximum value based on the provided axis.
	 * @param bbox The bounding box to query.
	 * @param doMin If {@code true}, retrieves the minimum value. If {@code false}, retrieves the maximum value.
	 * @param axis The index of the axis to check (e.g., 0, 1, or 2).
	 * @return The float value of the requested coordinate.
	 */
	private float getMinMax(BoundingBox bbox, boolean doMin, int axis)
	{
		if (doMin)
		{
			return bbox.getMin(null).get(axis);
		}
		
		return bbox.getMax(null).get(axis);
	}
	
	// private BIHNode createNode2(int l, int r, BoundingBox nodeBbox, int depth){
	// if ((r - l) < maxTrisPerNode || depth > 100)
	// return createLeaf(l, r);
	//
	// BoundingBox currentBox = createBox(l, r);
	// int axis = depth % 3;
	// float split = currentBox.getCenter().get(axis);
	//
	// TriangleAxisComparator comparator = comparators[axis];
	// Arrays.sort(tris, l, r, comparator);
	// int splitIndex = -1;
	//
	// float leftPlane, rightPlane = Float.POSITIVE_INFINITY;
	// leftPlane = tris[l].getExtreme(axis, false);
	// for (int i = l; i <= r; i++){
	// BIHTriangle tri = tris[i];
	// if (splitIndex == -1){
	// float v = tri.getCenter().get(axis);
	// if (v > split){
	// if (i == 0){
	// No left plane split index is -2.
	// }else{
	// splitIndex = i;
	// Assign the first triangle to the right plane.
	// }
	// }else{
	// Assign the triangle's extreme value on the specified axis to the left float variable.
	// if (ex > leftPlane)
	// leftPlane = ex;
	// }
	// }else{
	// float ex = tri.getExtreme(axis, true);
	// if (ex < rightPlane)
	// rightPlane = ex;
	// }
	// }
	//
	// if (splitIndex < 0){
	// splitIndex = (r - l) / 2;
	//
	// leftPlane = Float.NEGATIVE_INFINITY;
	// rightPlane = Float.POSITIVE_INFINITY;
	//
	// for (int i = l; i < splitIndex; i++){
	// float ex = tris[i].getExtreme(axis, false);
	// if (ex > leftPlane){
	// leftPlane = ex;
	// }
	// }
	// for (int i = splitIndex; i <= r; i++){
	// float ex = tris[i].getExtreme(axis, true);
	// if (ex < rightPlane){
	// rightPlane = ex;
	// }
	// }
	// }
	//
	// BIHNode node = new BIHNode(axis);
	// node.leftPlane = leftPlane;
	// node.rightPlane = rightPlane;
	//
	// node.leftIndex = l;
	// node.rightIndex = r;
	//
	// BoundingBox leftBbox = new BoundingBox(currentBox);
	// setMinMax(leftBbox, false, axis, split);
	// node.left = createNode2(l, splitIndex-1, leftBbox, depth+1);
	//
	// BoundingBox rightBbox = new BoundingBox(currentBox);
	// setMinMax(rightBbox, true, axis, split);
	// node.right = createNode2(splitIndex, r, rightBbox, depth+1);
	//
	// return node;
	// }
	
	/**
	 * Recursively constructs a {@link BIHNode} for the Bounding Interval Hierarchy.<br>
	 * This method splits triangles into child nodes based on spatial bounds and depth limits.
	 * @param l The starting index of the triangle range.
	 * @param r The ending index of the triangle range.
	 * @param nodeBbox The bounding box for the current node.
	 * @param depth The current recursion depth level.
	 * @return A new {@link BIHNode} representing a leaf or an internal branch.
	 */
	private BIHNode createNode(int l, int r, BoundingBox nodeBbox, int depth)
	{
		if (((r - l) < maxTrisPerNode) || (depth > MAX_TREE_DEPTH))
		{
			return new BIHNode(l, r);
		}
		
		final BoundingBox currentBox = createBox(l, r);
		
		final Vector3f exteriorExt = nodeBbox.getExtent(null);
		final Vector3f interiorExt = currentBox.getExtent(null);
		exteriorExt.subtractLocal(interiorExt);
		
		int axis = 0;
		if (exteriorExt.x > exteriorExt.y)
		{
			if (exteriorExt.x > exteriorExt.z)
			{
				axis = 0;
			}
			else
			{
				axis = 2;
			}
		}
		else
		{
			if (exteriorExt.y > exteriorExt.z)
			{
				axis = 1;
			}
			else
			{
				axis = 2;
			}
		}
		
		if (exteriorExt.equals(Vector3f.ZERO))
		{
			axis = 0;
		}
		
		// Arrays.sort(tris, l, r, comparators[axis]);
		final float split = currentBox.getCenter().get(axis);
		int pivot = sortTriangles(l, r, split, axis);
		if ((pivot == l) || (pivot == r))
		{
			pivot = (r + l) / 2;
		}
		
		// If one of the partitions is empty, continue with recursion: same level but different bbox
		if (pivot < l)
		{
			// Only right
			final BoundingBox rbbox = new BoundingBox(currentBox);
			setMinMax(rbbox, true, axis, split);
			return createNode(l, r, rbbox, depth + 1);
		}
		else if (pivot > r)
		{
			// Only left
			final BoundingBox lbbox = new BoundingBox(currentBox);
			setMinMax(lbbox, false, axis, split);
			return createNode(l, r, lbbox, depth + 1);
		}
		else
		{
			// Build the node
			final BIHNode node = new BIHNode(axis);
			
			// Left child
			final BoundingBox lbbox = new BoundingBox(currentBox);
			setMinMax(lbbox, false, axis, split);
			
			// The left node right border is the plane most right
			node.setLeftPlane(getMinMax(createBox(l, max(l, pivot - 1)), false, axis));
			node.setLeftChild(createNode(l, max(l, pivot - 1), lbbox, depth + 1)); // Recursive call
			
			// Right Child
			final BoundingBox rbbox = new BoundingBox(currentBox);
			setMinMax(rbbox, true, axis, split);
			
			// The right node left border is the plane most left
			node.setRightPlane(getMinMax(createBox(pivot, r), true, axis));
			node.setRightChild(createNode(pivot, r, rbbox, depth + 1)); // Recursive call
			
			return node;
		}
	}
	
	/**
	 * Retrieves the vertices of a specific triangle from the internal data.<br>
	 * The coordinates are copied into the provided {@code Vector3f} objects.
	 * @param index The unique identifier for the triangle to retrieve.
	 * @param v1 The first vertex of the triangle to be populated.
	 * @param v2 The second vertex of the triangle to be populated.
	 * @param v3 The third vertex of the triangle to be populated.
	 */
	public void getTriangle(int index, Vector3f v1, Vector3f v2, Vector3f v3)
	{
		int pointIndex = index * 9;
		
		v1.x = pointData[pointIndex++];
		v1.y = pointData[pointIndex++];
		v1.z = pointData[pointIndex++];
		
		v2.x = pointData[pointIndex++];
		v2.y = pointData[pointIndex++];
		v2.z = pointData[pointIndex++];
		
		v3.x = pointData[pointIndex++];
		v3.y = pointData[pointIndex++];
		v3.z = pointData[pointIndex++];
	}
	
	/**
	 * Swaps the data of two triangles in the internal buffer.<br>
	 * This method exchanges both vertex positions and their corresponding indices.<br>
	 * It uses {@code index1} and {@code index2} to identify the targets.
	 * @param index1 The first triangle index to swap.
	 * @param index2 The second triangle index to swap.
	 */
	public void swapTriangles(int index1, int index2)
	{
		final int p1 = index1 * 9;
		final int p2 = index2 * 9;
		
		// store p1 in tmp
		System.arraycopy(pointData, p1, bihSwapTmp, 0, 9);
		
		// copy p2 to p1
		System.arraycopy(pointData, p2, pointData, p1, 9);
		
		// copy tmp to p2
		System.arraycopy(bihSwapTmp, 0, pointData, p2, 9);
		
		// swap indices
		final int tmp2 = triIndices[index1];
		triIndices[index1] = triIndices[index2];
		triIndices[index2] = tmp2;
	}
	
	/**
	 * Checks for an intersection between a {@code Ray} and the bounding volume.<br>
	 * If a collision is detected, it performs a detailed intersection test with the tree nodes.
	 * @param r The ray used to perform the collision check.
	 * @param worldMatrix The transformation matrix for the object in world space.
	 * @param worldBound The bounding volume of the object.
	 * @param results The object where collision data will be stored.
	 * @return The number of triangles hit by the ray, or 0 if no collision occurs.
	 */
	private int collideWithRay(Ray r, Matrix4f worldMatrix, BoundingVolume worldBound, CollisionResults results)
	{
		final CollisionResults boundResults = new CollisionResults(results.getIntentions(), results.isOnlyFirst(), results.getInstanceId());
		worldBound.collideWith(r, boundResults);
		if (boundResults.size() > 0)
		{
			float tMin = boundResults.getClosestCollision().getDistance();
			float tMax = boundResults.getFarthestCollision().getDistance();
			
			if (tMax <= 0)
			{
				tMax = Float.POSITIVE_INFINITY;
			}
			else if (tMin == tMax)
			{
				tMin = 0;
			}
			
			if (tMin <= 0)
			{
				tMin = 0;
			}
			
			if (r.getLimit() < Float.POSITIVE_INFINITY)
			{
				tMax = Math.min(tMax, r.getLimit());
			}
			
			// return root.intersectBrute(r, worldMatrix, this, tMin, tMax, results);
			return root.intersectWhere(r, worldMatrix, this, tMin, tMax, results);
		}
		
		return 0;
	}
	
	/**
	 * Checks for a collision between this tree and a {@link BoundingVolume}.<br>
	 * It transforms the volume into local space using the provided {@code worldMatrix}.<br>
	 * The method returns the number of triangles that were intersected.
	 * @param bv The bounding volume to check against.
	 * @param worldMatrix The matrix representing the current world transformation.
	 * @param results The object where collision data will be stored.
	 * @return The count of intersecting triangles.
	 */
	private int collideWithBoundingVolume(BoundingVolume bv, Matrix4f worldMatrix, CollisionResults results)
	{
		BoundingBox bbox;
		if (bv instanceof BoundingBox)
		{
			bbox = new BoundingBox((BoundingBox) bv);
		}
		else
		{
			throw new UnsupportedCollisionException();
		}
		
		bbox.transform(worldMatrix.invert(), bbox);
		return root.intersectWhere(bv, bbox, worldMatrix, this, results);
	}
	
	/**
	 * Checks for a collision between this tree and another object.<br>
	 * This method handles both {@code Ray} and {@code BoundingVolume} types.<br>
	 * It returns the number of collisions found or throws an exception if the type is unsupported.
	 * @param other The object to check against, such as a {@code Ray} or {@code BoundingVolume}.
	 * @param worldMatrix The transformation matrix for the world space.
	 * @param worldBound The bounding volume representing the world boundaries.
	 * @param results The object where collision data will be stored.
	 * @return The number of collisions detected.
	 */
	@Override
	public int collideWith(Collidable other, Matrix4f worldMatrix, BoundingVolume worldBound, CollisionResults results)
	{
		if (other instanceof Ray)
		{
			final Ray ray = (Ray) other;
			return collideWithRay(ray, worldMatrix, worldBound, results);
		}
		else if (other instanceof BoundingVolume)
		{
			final BoundingVolume bv = (BoundingVolume) other;
			return collideWithBoundingVolume(bv, worldMatrix, results);
		}
		else
		{
			throw new UnsupportedCollisionException();
		}
	}
}
