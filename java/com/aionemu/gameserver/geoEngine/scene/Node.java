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
package com.aionemu.gameserver.geoEngine.scene;

import java.util.ArrayList;
import java.util.List;
import javax.activation.UnsupportedDataTypeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.geoEngine.bounding.BoundingVolume;
import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.math.Matrix3f;
import com.aionemu.gameserver.geoEngine.math.Ray;
import com.aionemu.gameserver.geoEngine.math.Triangle;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.mesh.DoorGeometry;

/**
 * Represents an internal node within a scene graph structure.<br>
 * This class manages a collection of children and merges them into a single bound for efficient culling.<br>
 * It allows for any number of child {@link Node} objects to be attached.
 * @author Mark Powell
 * @author Gregg Patton
 * @author Joshua Slack
 */
public class Node extends Spatial
{
	private static final Logger logger = LoggerFactory.getLogger(Node.class);
	/**
	 * This node's children.
	 */
	protected ArrayList<Spatial> children = new ArrayList<>(1);
	protected short collisionFlags;
	
	/**
	 * Creates a new instance of the {@link Node} class.<br>
	 * This is the default constructor for initializing a scene graph node.
	 */
	public Node()
	{
	}
	
	/**
	 * Creates a new {@link Node} with a specific name.<br>
	 * This constructor initializes the default collision flags.
	 * @param name The unique identifier for this node.
	 */
	public Node(String name)
	{
		super(name);
		collisionFlags = CollisionIntention.ALL.getId();
	}
	
	/**
	 * Returns the total number of children attached to this {@link Node}.<br>
	 * This value corresponds to the size of the internal children list.
	 * @return The count of child nodes.
	 */
	public int getQuantity()
	{
		return children.size();
	}
	
	/**
	 * Calculates the total number of triangles for this node and all its children.<br>
	 * It sums the results from {@code getTriangleCount} for every child in the list.
	 * @return The total sum of triangles as an {@code int}.
	 */
	@Override
	public int getTriangleCount()
	{
		int count = 0;
		if (children != null)
		{
			for (int i = 0; i < children.size(); i++)
			{
				count += children.get(i).getTriangleCount();
			}
		}
		
		return count;
	}
	
	/**
	 * Calculates the total number of vertices for this node and all its children.<br>
	 * It sums the results of {@code getVertexCount} for every child in the list.
	 * @return The total vertex count as an {@code int}.
	 */
	@Override
	public int getVertexCount()
	{
		int count = 0;
		if (children != null)
		{
			for (int i = 0; i < children.size(); i++)
			{
				count += children.get(i).getVertexCount();
			}
		}
		
		return count;
	}
	
	/**
	 * Adds a {@link Spatial} object as a child to this node.<br>
	 * It checks for intersections with existing children to determine placement.<br>
	 * If the child is a {@link DoorGeometry}, it is added to the internal doors map.
	 * @param child The {@link Spatial} object to attach.
	 * @return Always returns 0.
	 */
	public int attachChild(Spatial child)
	{
		if (child == null)
		{
			throw new NullPointerException();
		}
		
		if ((child.getParent() != this) && (child != this))
		{
			if (child.getParent() != null)
			{
				child.getParent().detachChild(child);
			}
			
			child.setParent(this);
			children.add(child);
		}
		
		return children.size();
	}
	
	/**
	 * Adds a {@link Spatial} object to the list of children at a specific position.<br>
	 * This method automatically handles removing the child from its previous parent if it has one.<br>
	 * It ensures that the child is correctly linked to this node as its new parent.
	 * @param child The {@link Spatial} object to add to the scene graph.
	 * @param index The position in the children list where the new child should be inserted.
	 * @return The new size of the children list after the addition.
	 */
	public int attachChildAt(Spatial child, int index)
	{
		if (child == null)
		{
			throw new NullPointerException();
		}
		
		if ((child.getParent() != this) && (child != this))
		{
			if (child.getParent() != null)
			{
				child.getParent().detachChild(child);
			}
			
			child.setParent(this);
			children.add(index, child);
		}
		
		return children.size();
	}
	
	/**
	 * Removes a specific child from this node.<br>
	 * This method updates the internal children list.<br>
	 * It returns the original index of the removed child.
	 * @param child The {@code Spatial} object to remove from the scene graph.
	 * @return The zero-based index of the removed child, or -1 if it was not found.
	 */
	public int detachChild(Spatial child)
	{
		if (child == null)
		{
			throw new NullPointerException();
		}
		
		if (child.getParent() == this)
		{
			final int index = children.indexOf(child);
			if (index != -1)
			{
				detachChildAt(index);
			}
			
			return index;
		}
		
		return -1;
	}
	
	/**
	 * Removes a child node from this scene graph based on its name.<br>
	 * This method searches the {@code children} list for a match.<br>
	 * It returns the index of the removed child or -1 if not found.
	 * @param childName The unique name of the child to remove.
	 * @return The index of the detached child, or -1 if no match exists.
	 */
	public int detachChildNamed(String childName)
	{
		if (childName == null)
		{
			throw new NullPointerException();
		}
		
		for (int x = 0, max = children.size(); x < max; x++)
		{
			final Spatial child = children.get(x);
			if (childName.equals(child.getName()))
			{
				detachChildAt(x);
				return x;
			}
		}
		
		return -1;
	}
	
	/**
	 * Removes a child from the node at the specified position.<br>
	 * This method updates the parent reference of the removed {@code Spatial}.
	 * @param index The position in the children list to remove from.
	 * @return The {@code Spatial} object that was removed, or {@code null} if nothing was found.
	 */
	public Spatial detachChildAt(int index)
	{
		final Spatial child = children.remove(index);
		if (child != null)
		{
			child.setParent(null);
		}
		
		return child;
	}
	
	/**
	 * Removes all {@link Spatial} objects from the current node.<br>
	 * This method clears the internal list of children.<br>
	 * It logs a message to confirm that all children were removed.
	 */
	public void detachAllChildren()
	{
		for (int i = children.size() - 1; i >= 0; i--)
		{
			detachChildAt(i);
		}
		
		logger.info("All children removed.");
	}
	
	/**
	 * Finds the position of a child in the list.<br>
	 * It returns the index of the specified {@code Spatial}.<br>
	 * If the object is not found, it returns -1.
	 * @param sp The {@code Spatial} object to search for.
	 * @return The zero-based index of the child or -1 if not found.
	 */
	public int getChildIndex(Spatial sp)
	{
		return children.indexOf(sp);
	}
	
	/**
	 * Swaps the positions of two children in the internal list.<br>
	 * This method exchanges the elements at the specified indices.
	 * @param index1 The first position to swap.
	 * @param index2 The second position to swap.
	 */
	public void swapChildren(int index1, int index2)
	{
		final Spatial c2 = children.get(index2);
		final Spatial c1 = children.remove(index1);
		children.add(index1, c2);
		children.remove(index2);
		children.add(index2, c1);
	}
	
	/**
	 * Retrieves a child node from the collection.<br>
	 * This method uses the provided index to find a {@link Spatial}.
	 * @param i The index of the child to retrieve.
	 * @return The {@code Spatial} object at the specified position.
	 */
	public Spatial getChild(int i)
	{
		return children.get(i);
	}
	
	/**
	 * Finds a child node by its name.<br>
	 * This method searches through all children and their descendants recursively.<br>
	 * It returns {@code null} if no match is found or if the input is {@code null}.
	 * @param name The unique name of the child to find.
	 * @return The matching {@link Spatial} object, or {@code null} if not found.
	 */
	public Spatial getChild(String name)
	{
		if (name == null)
		{
			return null;
		}
		
		for (int x = 0, cSize = getQuantity(); x < cSize; x++)
		{
			final Spatial child = children.get(x);
			if (name.equals(child.getName()))
			{
				return child;
			}
			else if (child instanceof Node)
			{
				final Spatial out = ((Node) child).getChild(name);
				if (out != null)
				{
					return out;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if a specific {@code Spatial} object exists in the children hierarchy.<br>
	 * This method searches through all direct and nested children of this node.
	 * @param spat The {@code Spatial} object to search for.
	 * @return {@code true} if the child is found, otherwise {@code false}.
	 */
	public boolean hasChild(Spatial spat)
	{
		if (children.contains(spat))
		{
			return true;
		}
		
		for (int i = 0, max = getQuantity(); i < max; i++)
		{
			final Spatial child = children.get(i);
			if ((child instanceof Node) && ((Node) child).hasChild(spat))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the list of child {@link Spatial} objects. <br>
	 * This method returns all nodes attached to this node.
	 * @return A {@code List} containing all children.
	 */
	public List<Spatial> getChildren()
	{
		return children;
	}
	
	/**
	 * Updates the geometry of children at specific positions.<br>
	 * This method passes the change request to the parent node.
	 * @param geometry The {@code Geometry} object to apply.
	 * @param index1 The first child index.
	 * @param index2 The second child index.
	 */
	public void childChange(Geometry geometry, int index1, int index2)
	{
		// just pass to parent
		if (parent != null)
		{
			parent.childChange(geometry, index1, index2);
		}
	}
	
	/**
	 * Checks if this node collides with another object.<br>
	 * This method handles collisions with {@link Ray} and {@link Triangle} types.<br>
	 * It updates the provided {@code results} object if a collision occurs.
	 * @param other The {@link Collidable} object to check against.
	 * @param results The {@link CollisionResults} container to store any detected hits.
	 * @return Returns 1 if a collision occurred, or 0 if no collision was found.
	 */
	@Override
	public int collideWith(Collidable other, CollisionResults results)
	{
		if ((getIntentions() & results.getIntentions()) == 0)
		{
			return 0;
		}
		
		if (other instanceof Ray)
		{
			if ((worldBound == null) || !worldBound.intersects(((Ray) other)))
			{
				return 0;
			}
		}
		
		int total = 0;
		for (int i = 0; i < children.size(); i++)
		{
			final Spatial child = children.get(i);
			if (child instanceof Geometry)
			{
				// Not used material IDs do not have collision intentions, and not all material meshes have physical collisions; TODO: implement event mesh collisions.
				if (((child.getIntentions() & results.getIntentions()) == 0) || ((child.getIntentions() & CollisionIntention.EVENT.getId()) != 0) || (((results.getIntentions() & CollisionIntention.MATERIAL.getId()) != 0) && (child.getMaterialId() <= 0)))
				{
					continue;
				}
			}
			
			total += child.collideWith(other, results);
			if ((total > 0) && results.isOnlyFirst())
			{
				break;
			}
		}
		
		return total;
	}
	
	/**
	 * Finds all descendants that match a specific type and name pattern.<br>
	 * This method searches through the children of this {@link Node} recursively.<br>
	 * It filters results based on the provided class and regular expression.
	 * @param <T>
	 * @param spatialSubclass The class type to filter by.
	 * @param nameRegex The regular expression to match against the descendant names.
	 * @return A list of matching descendants as a list of type {@code T}.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Spatial> List<T> descendantMatches(Class<T> spatialSubclass, String nameRegex)
	{
		final List<T> newList = new ArrayList<>();
		if (getQuantity() < 1)
		{
			return newList;
		}
		
		for (int i = 0; i < children.size(); i++)
		{
			final Spatial child = children.get(i);
			if (child.matches(spatialSubclass, nameRegex))
			{
				newList.add((T) child);
			}
			
			if (child instanceof Node)
			{
				newList.addAll(((Node) child).descendantMatches(spatialSubclass, nameRegex));
			}
		}
		
		return newList;
	}
	
	/**
	 * Finds all descendants that match a specific type.<br>
	 * This method searches through the children of this {@link Node}.<br>
	 * It returns a list of objects that are instances of the provided class.
	 * @param <T>
	 * @param spatialSubclass The class type to filter the descendants by.
	 * @return A list of matching descendant objects.
	 */
	public <T extends Spatial> List<T> descendantMatches(Class<T> spatialSubclass)
	{
		return descendantMatches(spatialSubclass, null);
	}
	
	/**
	 * Finds all descendants that match a specific name pattern.<br>
	 * This method searches through the children of this {@link Node}.<br>
	 * It returns a list of matching {@code Spatial} objects.
	 * @param <T>
	 * @param nameRegex The regular expression used to filter names.
	 * @return A list of matching descendant objects.
	 */
	public <T extends Spatial> List<T> descendantMatches(String nameRegex)
	{
		return descendantMatches(null, nameRegex);
	}
	
	/**
	 * Sets the bounding volume for all children of this node.<br>
	 * This method updates each child with a clone of the provided {@link BoundingVolume}.
	 * @param modelBound The new {@code BoundingVolume} to apply to all children.
	 */
	@Override
	public void setModelBound(BoundingVolume modelBound)
	{
		if (children != null)
		{
			for (int i = 0, max = children.size(); i < max; i++)
			{
				children.get(i).setModelBound(modelBound != null ? modelBound.clone(null) : null);
			}
		}
	}
	
	/**
	 * Updates the bounding box of this node based on its children.<br>
	 * This method iterates through all child {@link Spatial} objects and updates their bounds.<br>
	 * It then merges these bounds into the local {@code worldBound}.
	 */
	@Override
	public void updateModelBound()
	{
		BoundingVolume resultBound = null;
		if (children != null)
		{
			for (int i = 0, max = children.size(); i < max; i++)
			{
				final Spatial child = children.get(i);
				child.updateModelBound();
				if (resultBound != null)
				{
					// merge current world bound with child world bound
					resultBound.mergeLocal(child.getWorldBound());
				}
				else
				{
					// set world bound to first non-null child world bound
					if (child.getWorldBound() != null)
					{
						resultBound = child.getWorldBound().clone(worldBound);
					}
				}
			}
		}
		
		worldBound = resultBound;
	}
	
	/**
	 * Updates the transformation for all children of this node.<br>
	 * This method applies the rotation, location, and scale to every child in the {@code children} list.
	 * @param rotation The {@code Matrix3f} representing the object's rotation.
	 * @param loc The {@code Vector3f} representing the object's position.
	 * @param scale The float value used to resize the object.
	 */
	@Override
	public void setTransform(Matrix3f rotation, Vector3f loc, float scale)
	{
		if (children != null)
		{
			for (int i = 0; i < children.size(); i++)
			{
				children.get(i).setTransform(rotation, loc, scale);
			}
		}
	}
	
	/**
	 * Creates a deep copy of this {@code Node}.<br>
	 * This method recursively clones all children attached to the node.
	 * @return A new {@code Node} instance that is a copy of the current one.
	 * @throws CloneNotSupportedException If the object cannot be cloned.
	 */
	@Override
	public Node clone() throws CloneNotSupportedException
	{
		final Node node = new Node(name);
		node.collisionFlags = collisionFlags;
		for (Spatial spatial : children)
		{
			if (spatial instanceof Geometry)
			{
				final Geometry geom = new Geometry(spatial.getName(), ((Geometry) spatial).getMesh());
				node.attachChild(geom);
			}
			else if (spatial instanceof Node)
			{
				node.attachChild(((Node) (spatial)).clone());
			}
			else
			{
				new UnsupportedDataTypeException();
			}
		}
		
		return node;
	}
	
	/**
	 * Retrieves the collision flags for this geometry.<br>
	 * This method returns the flags stored in the underlying {@link Mesh}.
	 * @return The collision flags as a {@code short}.
	 */
	@Override
	public short getCollisionFlags()
	{
		return collisionFlags;
	}
	
	/**
	 * Sets the collision flags for this node.<br>
	 * This updates the internal {@code collisionFlags} field with the provided value.
	 * @param flags The new bitmask to use for collision detection.
	 */
	@Override
	public void setCollisionFlags(short flags)
	{
		collisionFlags = flags;
	}
}
