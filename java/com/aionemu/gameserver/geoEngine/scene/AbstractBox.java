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

import com.aionemu.gameserver.geoEngine.math.Vector3f;

/**
 * Represents an eight-sided axis-aligned box defined by a minimal and maximal point.<br>
 * This class provides the base structure for boxes without controlling how geometry data is generated.<br>
 * For geometry generation logic, see {@link Box}.
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public abstract class AbstractBox extends Mesh
{
	public final Vector3f center = new Vector3f(0f, 0f, 0f);
	public float xExtent, yExtent, zExtent;
	
	/**
	 * Initializes a new instance of the {@link AbstractBox} class.<br>
	 * This constructor sets up the basic properties for an eight-sided box.
	 */
	public AbstractBox()
	{
		super();
	}
	
	/**
	 * Calculates the eight vertices of the box.<br>
	 * This method uses the {@code center} and the three extents to find each corner point.
	 * @return an array of {@link Vector3f} objects representing the box vertices.
	 */
	protected Vector3f[] computeVertices()
	{
		final Vector3f[] axes =
		{
			Vector3f.UNIT_X.mult(xExtent),
			Vector3f.UNIT_Y.mult(yExtent),
			Vector3f.UNIT_Z.mult(zExtent)
		};
		return new Vector3f[]
		{
			center.subtract(axes[0]).subtractLocal(axes[1]).subtractLocal(axes[2]),
			center.add(axes[0]).subtractLocal(axes[1]).subtractLocal(axes[2]),
			center.add(axes[0]).addLocal(axes[1]).subtractLocal(axes[2]),
			center.subtract(axes[0]).addLocal(axes[1]).subtractLocal(axes[2]),
			center.add(axes[0]).subtractLocal(axes[1]).addLocal(axes[2]),
			center.subtract(axes[0]).subtractLocal(axes[1]).addLocal(axes[2]),
			center.add(axes[0]).addLocal(axes[1]).addLocal(axes[2]),
			center.subtract(axes[0]).addLocal(axes[1]).addLocal(axes[2])
		};
	}
	
	/**
	 * Convert the indices into the list of vertices that define the box's geometry.
	 */
	protected abstract void duUpdateGeometryIndices();
	
	/**
	 * Update the normals of each of the box's planes.
	 */
	protected abstract void duUpdateGeometryNormals();
	
	/**
	 * Update the position of the vertices that define the box.
	 * <p/>
	 * These eight points are determined from the minimum and maximum point.
	 */
	protected abstract void duUpdateGeometryVertices();
	
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
	 * Updates the internal geometry of this box.<br>
	 * This method refreshes the vertices, normals, and indices.<br>
	 * It ensures the visual representation matches the current dimensions.
	 */
	public void updateGeometry()
	{
		duUpdateGeometryVertices();
		duUpdateGeometryNormals();
		duUpdateGeometryIndices();
	}
	
	/**
	 * Updates the geometry of this box using a new center and dimensions.<br>
	 * This method sets the {@code center}, {@code xExtent}, {@code yExtent}, and {@code zExtent}.<br>
	 * It then calls {@code updateGeometry} to refresh the internal data.
	 * @param center The new {@code Vector3f} position for the box center.
	 * @param x The new width of the box along the X axis.
	 * @param y The new height of the box along the Y axis.
	 * @param z The new depth of the box along the Z axis.
	 */
	public void updateGeometry(Vector3f center, float x, float y, float z)
	{
		if (center != null)
		{
			this.center.set(center);
		}
		
		xExtent = x;
		yExtent = y;
		zExtent = z;
		updateGeometry();
	}
	
	/**
	 * Updates the geometry of this box based on its boundaries.<br>
	 * It calculates the new center and dimensions from the provided points.<br>
	 * This method then calls {@code float, float, float)} to refresh the mesh.
	 * @param minPoint The minimum corner of the box as a {@code Vector3f}.
	 * @param maxPoint The maximum corner of the box as a {@code Vector3f}.
	 */
	public void updateGeometry(Vector3f minPoint, Vector3f maxPoint)
	{
		center.set(maxPoint).addLocal(minPoint).multLocal(0.5f);
		final float x = maxPoint.x - center.x;
		final float y = maxPoint.y - center.y;
		final float z = maxPoint.z - center.z;
		updateGeometry(center, x, y, z);
	}
}
