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

import com.aionemu.gameserver.geoEngine.bounding.BoundingVolume;
import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Matrix3f;
import com.aionemu.gameserver.geoEngine.math.Vector3f;

/**
 * This class serves as the base class for all nodes within the scene graph.<br>
 * It manages parent links, local transforms, and world transforms.<br>
 * Classes like {@link Node} and {@link Geometry} inherit from this class.
 * @author Mark Powell
 * @author Joshua Slack
 * @author Rolandas - added materials
 */
public abstract class Spatial implements Collidable, Cloneable
{
	public enum CullHint
	{
		/**
		 * Do whatever our parent does. If no parent, we'll default to dynamic.
		 */
		Inherit,
		/**
		 * Do not draw if we are not at least partially within the view frustum of the renderer's camera.
		 */
		Dynamic,
		/**
		 * Always cull this from view.
		 */
		Always,
		/**
		 * Never cull this from view. Note that we will still get culled if our parent is culled.
		 */
		Never;
	}
	
	/**
	 * Spatial's bounding volume relative to the world.
	 */
	protected BoundingVolume worldBound;
	/**
	 * This spatial's name.
	 */
	protected String name;
	/**
	 * Spatial's parent, or null if it has none.
	 */
	protected transient Node parent;
	
	/**
	 * Creates a new instance of the {@link Spatial} class.<br>
	 * This is the default constructor for all scene graph nodes.
	 */
	public Spatial()
	{
	}
	
	/**
	 * Creates a new {@link Spatial} instance with a specific name.<br>
	 * The name is stored using the {@code intern()} method to save memory.<br>
	 * If the provided name is {@code null}, the internal name remains {@code null}.
	 * @param name The unique identifier for this spatial node.
	 */
	public Spatial(String name)
	{
		this();
		if (name != null)
		{
			this.name = name.intern();
		}
	}
	
	/**
	 * Sets the name for this {@link Spatial} object.<br>
	 * The provided {@code String} is interned if it is not {@code null}.
	 * @param name The new name to assign to the spatial.
	 */
	public void setName(String name)
	{
		if (name != null)
		{
			this.name = name.intern();
		}
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the parent {@link Node} of this spatial.<br>
	 * This method helps navigate up the scene graph hierarchy.<br>
	 * It returns {@code null} if no parent exists.
	 * @return The parent {@link Node}, or {@code null}.
	 */
	public Node getParent()
	{
		return parent;
	}
	
	/**
	 * Sets the parent node for this {@link Spatial} object.<br>
	 * This establishes a link in the scene graph hierarchy.<br>
	 * Pass {@code null} if the node has no parent.
	 * @param parent The {@code Node} to set as the parent.
	 */
	protected void setParent(Node parent)
	{
		this.parent = parent;
	}
	
	/**
	 * Removes this node from its current parent.<br>
	 * This method calls {@code detachChild} if a parent exists.
	 * @return {@code true} if the node was successfully removed, or {@code false} if it had no parent.
	 */
	public boolean removeFromParent()
	{
		if (parent != null)
		{
			parent.detachChild(this);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific {@link Node} is an ancestor of this node.<br>
	 * This method searches up the scene graph hierarchy.
	 * @param ancestor The {@code Node} to search for in the parent chain.
	 * @return {@code true} if the node is found, {@code false} otherwise.
	 */
	public boolean hasAncestor(Node ancestor)
	{
		if (parent == null)
		{
			return false;
		}
		else if (parent.equals(ancestor))
		{
			return true;
		}
		else
		{
			return parent.hasAncestor(ancestor);
		}
	}
	
	/**
	 * <code>updateModelBound</code> recalculates the bounding object for this Spatial.
	 */
	public abstract void updateModelBound();
	
	/**
	 * <code>setModelBound</code> sets the bounding object for this Spatial.
	 * @param modelBound the bounding object for this spatial.
	 */
	public abstract void setModelBound(BoundingVolume modelBound);
	
	/**
	 * @return The sum of all verticies under this Spatial.
	 */
	public abstract int getVertexCount();
	
	/**
	 * @return The sum of all triangles under this Spatial.
	 */
	public abstract int getTriangleCount();
	
	/**
	 * Retrieves the material identifier for this mesh.<br>
	 * This value is extracted from the internal collision flags.
	 * @return The material ID as a {@code byte}.
	 */
	public byte getMaterialId()
	{
		return (byte) (getCollisionFlags() & 0xFF);
	}
	
	/**
	 * Retrieves the intention flags from the collision data.<br>
	 * This value is extracted by shifting the {@code getCollisionFlags()} result.
	 * @return The {@code byte} value representing the intentions.
	 */
	public byte getIntentions()
	{
		return (byte) (getCollisionFlags() >> 8);
	}
	
	public abstract short getCollisionFlags();
	
	public abstract void setCollisionFlags(short flags);
	
	/**
	 * Checks if this {@link Spatial} object matches specific criteria.<br>
	 * It verifies the object's class type and its name against a regular expression.
	 * @param spatialSubclass The expected subclass of {@link Spatial}. If {@code null}, any class is accepted.
	 * @param nameRegex The regular expression to match against the name of this object. If {@code null}, any name is accepted.
	 * @return {@code true} if the object matches both criteria, otherwise {@code false}.
	 */
	public boolean matches(Class<? extends Spatial> spatialSubclass, String nameRegex)
	{
		if ((spatialSubclass != null) && !spatialSubclass.isInstance(this))
		{
			return false;
		}
		
		if ((nameRegex != null) && ((name == null) || !name.matches(nameRegex)))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Retrieves the bounding volume of this spatial in world coordinates.<br>
	 * This is useful for collision detection and visibility checks.
	 * @return the {@code BoundingVolume} representing the object's size in the world.
	 */
	public BoundingVolume getWorldBound()
	{
		return worldBound;
	}
	
	/**
	 * Returns a string representation of the {@code Spatial} object.<br>
	 * This includes the name, class type, and collision intentions.
	 * @return A formatted string describing this spatial node.
	 */
	@Override
	public String toString()
	{
		return name + " (" + this.getClass().getSimpleName() + ") use " + CollisionIntention.toString(getIntentions());
	}
	
	public abstract void setTransform(Matrix3f rotation, Vector3f loc, float scale);
	
	/**
	 * Creates a new copy of this {@link Spatial} object.<br>
	 * This method performs a shallow copy of the current instance.
	 * @return A new {@code Spatial} object that is a copy of this one.
	 */
	@Override
	public Spatial clone() throws CloneNotSupportedException
	{
		return (Spatial) super.clone();
	}
}
