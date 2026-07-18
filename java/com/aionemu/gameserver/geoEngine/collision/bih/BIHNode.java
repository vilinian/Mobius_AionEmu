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
import static java.lang.Math.min;

import java.util.ArrayDeque;
import java.util.Deque;

import com.aionemu.gameserver.geoEngine.bounding.BoundingBox;
import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResult;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.math.Matrix4f;
import com.aionemu.gameserver.geoEngine.math.Ray;
import com.aionemu.gameserver.geoEngine.math.Triangle;
import com.aionemu.gameserver.geoEngine.math.Vector3f;

/**
 * Represents a node within a Bounding Interval Hierarchy (BIH) structure.<br>
 * This class is used to organize spatial data for efficient ray tracing and collision detection.<br>
 * It helps the {@link Ray} intersection logic quickly prune areas of the map that do not contain relevant geometry.
 */
public final class BIHNode
{
	private int leftIndex, rightIndex;
	private BIHNode left;
	private BIHNode right;
	private float leftPlane;
	private float rightPlane;
	private int axis;
	
	/**
	 * Creates a new {@code BIHNode} that represents a leaf node.<br>
	 * This constructor initializes the node with specific indices.<br>
	 * It sets the internal {@code axis} to {@code 3} to mark it as a leaf.
	 * @param l The index for the left side of the leaf.
	 * @param r The index for the right side of the leaf.
	 */
	public BIHNode(int l, int r)
	{
		leftIndex = l;
		rightIndex = r;
		axis = 3; // indicates leaf
	}
	
	/**
	 * Creates a new {@link BIHNode} instance.<br>
	 * This constructor initializes the node with a specific spatial axis.<br>
	 * The axis determines which dimension is used for splitting during traversal.
	 * @param axis The integer representing the coordinate axis.
	 */
	public BIHNode(int axis)
	{
		this.axis = axis;
	}
	
	/**
	 * Creates a new instance of {@link BIHNode}.<br>
	 * This constructor initializes the node with default values.
	 */
	public BIHNode()
	{
	}
	
	/**
	 * Retrieves the left child node of this {@link BIHNode}.<br>
	 * This is used to navigate the bounding interval hierarchy.
	 * @return The {@code BIHNode} representing the left child, or {@code null} if it does not exist.
	 */
	public BIHNode getLeftChild()
	{
		return left;
	}
	
	/**
	 * Sets the left child of this {@link BIHNode}.<br>
	 * This method updates the internal reference to the left branch.
	 * @param left The {@code BIHNode} to set as the left child.
	 */
	public void setLeftChild(BIHNode left)
	{
		this.left = left;
	}
	
	/**
	 * Retrieves the position of the left splitting plane.<br>
	 * This value is used to divide the space within this {@link BIHNode}.
	 * @return The {@code float} value representing the left plane.
	 */
	public float getLeftPlane()
	{
		return leftPlane;
	}
	
	/**
	 * Sets the position of the left plane for this {@link BIHNode}.<br>
	 * This value is used during collision detection.
	 * @param leftPlane The new coordinate for the left plane.
	 */
	public void setLeftPlane(float leftPlane)
	{
		this.leftPlane = leftPlane;
	}
	
	/**
	 * Retrieves the child node located on the right side of this {@link BIHNode}.<br>
	 * This is used to traverse the bounding interval hierarchy.
	 * @return The {@code BIHNode} representing the right child, or {@code null} if it does not exist.
	 */
	public BIHNode getRightChild()
	{
		return right;
	}
	
	/**
	 * Sets the right child of this {@link BIHNode}.<br>
	 * This method links a new node to the right side of the hierarchy.
	 * @param right The {@code BIHNode} to set as the right child.
	 */
	public void setRightChild(BIHNode right)
	{
		this.right = right;
	}
	
	/**
	 * Retrieves the value of the right plane for this {@link BIHNode}.<br>
	 * This value is used to define the boundary of the node's spatial split.
	 * @return The {@code float} value representing the right plane.
	 */
	public float getRightPlane()
	{
		return rightPlane;
	}
	
	/**
	 * Sets the value for the {@code rightPlane}.<br>
	 * This value defines the boundary of the right side of this node.
	 * @param rightPlane The new float value for the right plane.
	 */
	public void setRightPlane(float rightPlane)
	{
		this.rightPlane = rightPlane;
	}
	
	public static final class BIHStackData
	{
		private final BIHNode node;
		private final float min, max;
		
		BIHStackData(BIHNode node, float min, float max)
		{
			this.node = node;
			this.min = min;
			this.max = max;
		}
	}
	
	/**
	 * Checks for collisions between a {@code Collidable} object and the bounding box of this node.<br>
	 * This method traverses the {@link BIHTree} to find intersecting triangles.<br>
	 * It uses the provided {@code worldMatrix} to transform triangle coordinates if it is not {@code null}.
	 * @param col The {@code Collidable} object to check for collisions.
	 * @param box The {@code BoundingBox} representing the area of interest.
	 * @param worldMatrix The {@code Matrix4f} used for coordinate transformation.
	 * @param tree The {@link BIHTree} containing the triangle data.
	 * @param results The {@code CollisionResults} object to store any found collisions.
	 * @return The total number of triangles that collided with the object.
	 */
	public int intersectWhere(Collidable col, BoundingBox box, Matrix4f worldMatrix, BIHTree tree, CollisionResults results)
	{
		final Deque<BIHStackData> stack = new ArrayDeque<>();
		
		final float[] minExts =
		{
			box.getCenter().x - box.getXExtent(),
			box.getCenter().y - box.getYExtent(),
			box.getCenter().z - box.getZExtent()
		};
		
		final float[] maxExts =
		{
			box.getCenter().x + box.getXExtent(),
			box.getCenter().y + box.getYExtent(),
			box.getCenter().z + box.getZExtent()
		};
		
		stack.push(new BIHStackData(this, 0, 0));
		
		final Triangle t = new Triangle();
		final int cols = 0;
		
		stackloop: while (!stack.isEmpty())
		{
			BIHNode node = stack.pop().node;
			
			while (node.axis != 3)
			{
				final int a = node.axis;
				
				final float maxExt = maxExts[a];
				final float minExt = minExts[a];
				
				if (node.leftPlane < node.rightPlane)
				{
					// If the box is in the gap, we stop there because there is a gap in the middle.
					if ((minExt > node.leftPlane) && (maxExt < node.rightPlane))
					{
						continue stackloop;
					}
				}
				
				if (maxExt < node.rightPlane)
				{
					node = node.left;
				}
				else if (minExt > node.leftPlane)
				{
					node = node.right;
				}
				else
				{
					stack.push(new BIHStackData(node.right, 0, 0));
					node = node.left;
				}
			}
			
			for (int i = node.leftIndex; i <= node.rightIndex; i++)
			{
				tree.getTriangle(i, t.get1(), t.get2(), t.get3());
				if (worldMatrix != null)
				{
					worldMatrix.mult(t.get1(), t.get1());
					worldMatrix.mult(t.get2(), t.get2());
					worldMatrix.mult(t.get3(), t.get3());
				}
				
				/*
				 * Original code had this int added = col.collideWith(t, results, 1); if (added > 0) { cols += added; }
				 */
			}
		}
		
		return cols;
	}
	
	/**
	 * Performs a brute-force intersection test between a ray and the triangles in the tree.<br>
	 * This method traverses the {@link BIHTree} to find all collisions.<br>
	 * It updates the provided {@code CollisionResults} with any hits found.
	 * @param r The {@code Ray} being cast into the scene.
	 * @param worldMatrix The {@code Matrix4f} used to transform triangle vertices.
	 * @param tree The {@link BIHTree} containing the geometry.
	 * @param sceneMin The minimum boundary of the scene.
	 * @param sceneMax The maximum boundary of the scene.
	 * @param results The {@code CollisionResults} object where hits are stored.
	 * @return The total number of collisions detected.
	 */
	public int intersectBrute(Ray r, Matrix4f worldMatrix, BIHTree tree, float sceneMin, float sceneMax, CollisionResults results)
	{
		float tHit = Float.POSITIVE_INFINITY;
		
		final Vector3f v1 = new Vector3f(), v2 = new Vector3f(), v3 = new Vector3f();
		
		int cols = 0;
		
		final Deque<BIHStackData> stack = new ArrayDeque<>();
		stack.push(new BIHStackData(this, 0, 0));
		while (!stack.isEmpty())
		{
			final BIHStackData data = stack.pop();
			BIHNode node = data.node;
			
			while (node.axis != 3)
			{
				// while node is not a leaf
				BIHNode nearNode, farNode;
				nearNode = node.left;
				farNode = node.right;
				
				stack.push(new BIHStackData(farNode, 0, 0));
				node = nearNode;
			}
			
			// a leaf
			for (int i = node.leftIndex; i <= node.rightIndex; i++)
			{
				tree.getTriangle(i, v1, v2, v3);
				
				if (worldMatrix != null)
				{
					worldMatrix.mult(v1, v1);
					worldMatrix.mult(v2, v2);
					worldMatrix.mult(v3, v3);
				}
				
				final float t = r.intersects(v1, v2, v3);
				if (t < tHit)
				{
					tHit = t;
					final Vector3f contactPoint = new Vector3f(r.direction).multLocal(tHit).addLocal(r.origin);
					final CollisionResult cr = new CollisionResult(contactPoint, tHit);
					results.addCollision(cr);
					cols++;
				}
			}
		}
		
		return cols;
	}
	
	/**
	 * Performs a ray intersection test against the Bounding Interval Hierarchy.<br>
	 * This method traverses the {@link BIHTree} to find all triangles hit by the {@code r}.<br>
	 * It handles coordinate transformations using the provided {@code worldMatrix}.<br>
	 * Results are stored in the {@code results} object.
	 * @param r The ray used for the intersection test.
	 * @param worldMatrix The matrix used to transform coordinates into world space.
	 * @param tree The {@link BIHTree} containing the geometry data.
	 * @param sceneMin The minimum boundary of the scene.
	 * @param sceneMax The maximum boundary of the scene.
	 * @param results The object where collision information will be stored.
	 * @return The total number of collisions detected.
	 */
	public int intersectWhere(Ray r, Matrix4f worldMatrix, BIHTree tree, float sceneMin, float sceneMax, CollisionResults results)
	{
		final Deque<BIHStackData> stack = new ArrayDeque<>();
		
		// float tHit = Float.POSITIVE_INFINITY;
		final Vector3f o = r.getOrigin().clone();
		final Vector3f d = r.getDirection().clone();
		
		final Matrix4f inv = worldMatrix.invert();
		
		inv.mult(r.getOrigin(), r.getOrigin());
		
		// Fixes rotation collision bug
		inv.multNormal(r.getDirection(), r.getDirection());
		// inv.multNormalAcross(r.getDirection(), r.getDirection());
		
		final float[] origins =
		{
			r.getOrigin().x,
			r.getOrigin().y,
			r.getOrigin().z
		};
		
		final float[] invDirections =
		{
			1f / r.getDirection().x,
			1f / r.getDirection().y,
			1f / r.getDirection().z
		};
		
		r.getDirection().normalizeLocal();
		
		final Vector3f v1 = new Vector3f(), v2 = new Vector3f(), v3 = new Vector3f();
		int cols = 0;
		
		stack.push(new BIHStackData(this, sceneMin, sceneMax));
		stackloop: while (!stack.isEmpty())
		{
			final BIHStackData data = stack.pop();
			BIHNode node = data.node;
			float tMin = data.min, tMax = data.max;
			
			if (tMax < tMin)
			{
				continue;
			}
			
			while (node.axis != 3)
			{
				// while node is not a leaf
				final int a = node.axis;
				
				// find the origin and direction value for the given axis
				final float origin = origins[a];
				final float invDirection = invDirections[a];
				
				float tNearSplit, tFarSplit;
				BIHNode nearNode, farNode;
				
				tNearSplit = (node.leftPlane - origin) * invDirection;
				tFarSplit = (node.rightPlane - origin) * invDirection;
				nearNode = node.left;
				farNode = node.right;
				
				if (invDirection < 0)
				{
					final float tmpSplit = tNearSplit;
					tNearSplit = tFarSplit;
					tFarSplit = tmpSplit;
					
					final BIHNode tmpNode = nearNode;
					nearNode = farNode;
					farNode = tmpNode;
				}
				
				if ((tMin > tNearSplit) && (tMax < tFarSplit))
				{
					continue stackloop;
				}
				
				if (tMin > tNearSplit)
				{
					tMin = max(tMin, tFarSplit);
					node = farNode;
				}
				else if (tMax < tFarSplit)
				{
					tMax = min(tMax, tNearSplit);
					node = nearNode;
				}
				else
				{
					stack.push(new BIHStackData(farNode, max(tMin, tFarSplit), tMax));
					tMax = min(tMax, tNearSplit);
					node = nearNode;
				}
			}
			
			// a leaf
			for (int i = node.leftIndex; i <= node.rightIndex; i++)
			{
				tree.getTriangle(i, v1, v2, v3);
				
				float t = r.intersects(v1, v2, v3);
				if (!Float.isInfinite(t))
				{
					if (worldMatrix != null)
					{
						worldMatrix.mult(v1, v1);
						worldMatrix.mult(v2, v2);
						worldMatrix.mult(v3, v3);
						final float t_world = new Ray(o, d).intersects(v1, v2, v3);
						t = t_world;
					}
					
					final Vector3f contactNormal = Triangle.computeTriangleNormal(v1, v2, v3, null);
					final Vector3f contactPoint = new Vector3f(d).multLocal(t).addLocal(o);
					final float worldSpaceDist = o.distance(contactPoint);
					
					// fix invisible walls
					if (worldSpaceDist > r.limit)
					{
						continue;
					}
					
					final CollisionResult cr = new CollisionResult(contactPoint, worldSpaceDist);
					cr.setContactNormal(contactNormal);
					results.addCollision(cr);
					if (results.isOnlyFirst())
					{
						return 1;
					}
					
					cols++;
				}
			}
		}
		
		r.setOrigin(o);
		r.setDirection(d);
		return cols;
	}
}
