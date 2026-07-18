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

/**
 * Represents an abstraction of a native graphics library object.<br>
 * This class tracks and manages objects on the native side of the {@code scene} engine.
 */
public abstract class GLObject implements Cloneable
{
	/**
	 * The ID of the object, usually depends on its type. Typically returned from calls like glGenTextures, glGenBuffers, etc.
	 */
	protected int id = -1;
	/**
	 * A reference to a "handle". By hard referencing a certain object, it's possible to find when a certain GLObject is no longer used, and to delete its instance from the graphics library.
	 */
	protected Object handleRef = null;
	/**
	 * True if the data represented by this GLObject has been changed and needs to be updated before used.
	 */
	protected boolean updateNeeded = true;
	/**
	 * The type of the GLObject, usually specified by a subclass.
	 */
	protected final Type type;
	
	public static enum Type
	{
		/**
		 * Vertex buffers are used to describe geometry data and it's attributes.
		 */
		VertexBuffer,
		/**
		 * ShaderSource is a shader source code that controls the output of a certain rendering pipeline, like vertex position or fragment color.
		 */
		ShaderSource,
		/**
		 * A Shader is an aggregation of ShaderSources, collectively they cooperate to control the vertex and fragment processor.
		 */
		Shader,
	}
	
	/**
	 * Creates a new instance of a {@link GLObject}.<br>
	 * This constructor initializes the object with a specific {@code Type}.<br>
	 * It also creates a new internal handle reference.
	 * @param type The category of the graphics object to create.
	 */
	public GLObject(Type type)
	{
		this.type = type;
		handleRef = new Object();
	}
	
	/**
	 * Creates a new instance of a {@link GLObject} with a specific type and ID.<br>
	 * This constructor is used to initialize the internal state of the object.
	 * @param type The {@code Type} of the graphics object.
	 * @param id The unique identifier for the object.
	 */
	protected GLObject(Type type, int id)
	{
		this.type = type;
		this.id = id;
	}
	
	/**
	 * Sets the unique identifier for this {@link GLObject}.<br>
	 * This method ensures that an ID is only assigned once.<br>
	 * It will throw an {@code IllegalStateException} if the ID is already set.
	 * @param id The new integer value to assign as the object's ID.
	 */
	public void setId(int id)
	{
		if (this.id != -1)
		{
			throw new IllegalStateException("ID has already been set for this GL object.");
		}
		
		this.id = id;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Marks the object as needing an update.<br>
	 * This sets the {@code updateNeeded} flag to {@code true}.<br>
	 * Use this when the underlying data has changed.
	 */
	public void setUpdateNeeded()
	{
		updateNeeded = true;
	}
	
	/**
	 * Resets the {@code updateNeeded} flag to {@code false}.<br>
	 * This tells the system that the object data is current.<br>
	 * Use this after you have finished modifying the object.
	 */
	public void clearUpdateNeeded()
	{
		updateNeeded = false;
	}
	
	/**
	 * Checks if the object data has changed.<br>
	 * This determines if an update is required before use.
	 * @return {@code true} if an update is needed, {@code false} otherwise.
	 */
	public boolean isUpdateNeeded()
	{
		return updateNeeded;
	}
	
	/**
	 * Returns a string representation of this {@code GLObject}.<br>
	 * It combines the object type name and its hash code.
	 * @return A formatted string containing the type and hex hash.
	 */
	@Override
	public String toString()
	{
		return type.name() + " " + Integer.toHexString(hashCode());
	}
	
	/**
	 * Creates a copy of this {@code GLObject}.<br>
	 * The new object is initialized with a fresh {@code handleRef}.<br>
	 * It resets the {@code id} to -1 and sets {@code updateNeeded} to {@code true}.
	 * @return A new instance of {@code GLObject}.
	 */
	@Override
	protected GLObject clone()
	{
		try
		{
			final GLObject obj = (GLObject) super.clone();
			obj.handleRef = new Object();
			obj.id = -1;
			obj.updateNeeded = true;
			return obj;
		}
		catch (CloneNotSupportedException ex)
		{
			throw new AssertionError();
		}
	}
	
	// Overrides the equals method to compare objects.
	// if (this == other)
	// return true;
	// if (!(other instanceof GLObject))
	// return false;
	//
	// }
	// Specialized calls to be used by object manager only.
	
	/**
	 * Called when the GL context is restarted to reset all IDs. Prevents "white textures" on display restart.
	 */
	public abstract void resetObject();
	
	/**
	 * Creates a shallow clone of this GL Object. The deleteObject method should be functional for this object.
	 * @return
	 */
	public abstract GLObject createDestructableClone();
}
