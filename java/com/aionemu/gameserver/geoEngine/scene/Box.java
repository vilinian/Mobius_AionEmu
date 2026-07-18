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

import java.nio.FloatBuffer;

import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.VertexBuffer.Type;
import com.aionemu.gameserver.geoEngine.utils.BufferUtils;

/**
 * Represents a geometric box with solid faces.<br>
 * This class is used to define filled 3D volumes within the scene.
 * @author Mark Powell
 */
public class Box extends AbstractBox
{
	private static final short[] GEOMETRY_INDICES_DATA =
	{
		2,
		1,
		0,
		3,
		2,
		0, // back
		6,
		5,
		4,
		7,
		6,
		4, // right
		10,
		9,
		8,
		11,
		10,
		8, // front
		14,
		13,
		12,
		15,
		14,
		12, // left
		18,
		17,
		16,
		19,
		18,
		16, // top
		22,
		21,
		20,
		23,
		22,
		20 // bottom
	};
	private static final float[] GEOMETRY_NORMALS_DATA =
	{
		0,
		0,
		-1,
		0,
		0,
		-1,
		0,
		0,
		-1,
		0,
		0,
		-1, // back
		1,
		0,
		0,
		1,
		0,
		0,
		1,
		0,
		0,
		1,
		0,
		0, // right
		0,
		0,
		1,
		0,
		0,
		1,
		0,
		0,
		1,
		0,
		0,
		1, // front
		-1,
		0,
		0,
		-1,
		0,
		0,
		-1,
		0,
		0,
		-1,
		0,
		0, // left
		0,
		1,
		0,
		0,
		1,
		0,
		0,
		1,
		0,
		0,
		1,
		0, // top
		0,
		-1,
		0,
		0,
		-1,
		0,
		0,
		-1,
		0,
		0,
		-1,
		0 // bottom
	};
	
	/**
	 * Creates a new {@link Box} centered at the origin.<br>
	 * The dimensions are defined by the extent in each direction from the center.<br>
	 * For example, using {@code 0.5f} for all parameters creates a unit cube.
	 * @param x The size of the box along the x axis in both directions.
	 * @param y The size of the box along the y axis in both directions.
	 * @param z The size of the box along the z axis in both directions.
	 */
	public Box(float x, float y, float z)
	{
		super();
		updateGeometry(Vector3f.ZERO, x, y, z);
	}
	
	/**
	 * Creates a new {@link Box} at a specific location.<br>
	 * This constructor sets the center point and the dimensions of the box.
	 * @param center The {@code Vector3f} position for the center of the box.
	 * @param x The size of the box along the x axis in both directions.
	 * @param y The size of the box along the y axis in both directions.
	 * @param z The size of the box along the z axis in both directions.
	 */
	public Box(Vector3f center, float x, float y, float z)
	{
		super();
		updateGeometry(center, x, y, z);
	}
	
	/**
	 * Creates a new {@link Box} using minimum and maximum coordinates.<br>
	 * This constructor defines the boundaries of the box in 3D space.
	 * @param min The minimum corner of the box as a {@code Vector3f}.
	 * @param max The maximum corner of the box as a {@code Vector3f}.
	 */
	@SuppressWarnings("javadoc")
	public Box(Vector3f min, Vector3f max)
	{
		super();
		updateGeometry(min, max);
	}
	
	/**
	 * Creates a new {@link Box} instance.<br>
	 * This constructor initializes the box with default values.
	 */
	public Box()
	{
		super();
	}
	
	/**
	 * Creates a copy of the current {@code Box} instance.<br>
	 * This method returns a new object with the same dimensions and center.
	 * @return A new {@code Box} object.
	 */
	@Override
	public Box clone()
	{
		return new Box(center.clone(), xExtent, yExtent, zExtent);
	}
	
	/**
	 * Updates the geometry index buffer for this box.<br>
	 * This method checks if the {@code Type.Index} buffer is {@code null}.<br>
	 * If it is missing, it initializes it using {@code GEOMETRY_INDICES_DATA}.
	 */
	@Override
	protected void duUpdateGeometryIndices()
	{
		if (getBuffer(Type.Index) == null)
		{
			setBuffer(Type.Index, 3, BufferUtils.createShortBuffer(GEOMETRY_INDICES_DATA));
		}
	}
	
	/**
	 * Updates the geometry normals for this box.<br>
	 * This method checks if the {@code Normal} buffer is {@code null}.<br>
	 * If it is missing, it initializes the buffer using {@code GEOMETRY_NORMALS_DATA}.
	 */
	@Override
	protected void duUpdateGeometryNormals()
	{
		if (getBuffer(Type.Normal) == null)
		{
			setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(GEOMETRY_NORMALS_DATA));
		}
	}
	
	/**
	 * Updates the geometry vertex data for this box.<br>
	 * This method calculates all vertices and stores them in a {@code FloatBuffer}.<br>
	 * It sets the buffer to the {@code Position} type and updates the bounding volume.
	 */
	@Override
	protected void duUpdateGeometryVertices()
	{
		final FloatBuffer fpb = BufferUtils.createVector3Buffer(24);
		final Vector3f[] v = computeVertices();
		fpb.put(new float[]
		{
			v[0].x,
			v[0].y,
			v[0].z,
			v[1].x,
			v[1].y,
			v[1].z,
			v[2].x,
			v[2].y,
			v[2].z,
			v[3].x,
			v[3].y,
			v[3].z, // back
			v[1].x,
			v[1].y,
			v[1].z,
			v[4].x,
			v[4].y,
			v[4].z,
			v[6].x,
			v[6].y,
			v[6].z,
			v[2].x,
			v[2].y,
			v[2].z, // right
			v[4].x,
			v[4].y,
			v[4].z,
			v[5].x,
			v[5].y,
			v[5].z,
			v[7].x,
			v[7].y,
			v[7].z,
			v[6].x,
			v[6].y,
			v[6].z, // front
			v[5].x,
			v[5].y,
			v[5].z,
			v[0].x,
			v[0].y,
			v[0].z,
			v[3].x,
			v[3].y,
			v[3].z,
			v[7].x,
			v[7].y,
			v[7].z, // left
			v[2].x,
			v[2].y,
			v[2].z,
			v[6].x,
			v[6].y,
			v[6].z,
			v[7].x,
			v[7].y,
			v[7].z,
			v[3].x,
			v[3].y,
			v[3].z, // top
			v[0].x,
			v[0].y,
			v[0].z,
			v[5].x,
			v[5].y,
			v[5].z,
			v[4].x,
			v[4].y,
			v[4].z,
			v[1].x,
			v[1].y,
			v[1].z // bottom
		});
		setBuffer(Type.Position, 3, fpb);
		updateBound();
	}
}
