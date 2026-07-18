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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import com.aionemu.gameserver.geoEngine.bounding.BoundingBox;
import com.aionemu.gameserver.geoEngine.bounding.BoundingVolume;
import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.collision.bih.BIHTree;
import com.aionemu.gameserver.geoEngine.math.Matrix4f;
import com.aionemu.gameserver.geoEngine.math.Triangle;
import com.aionemu.gameserver.geoEngine.math.Vector2f;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.VertexBuffer.Format;
import com.aionemu.gameserver.geoEngine.scene.VertexBuffer.Type;
import com.aionemu.gameserver.geoEngine.scene.VertexBuffer.Usage;
import com.aionemu.gameserver.geoEngine.scene.mesh.IndexBuffer;
import com.aionemu.gameserver.geoEngine.scene.mesh.IndexByteBuffer;
import com.aionemu.gameserver.geoEngine.scene.mesh.IndexIntBuffer;
import com.aionemu.gameserver.geoEngine.scene.mesh.IndexShortBuffer;
import com.aionemu.gameserver.geoEngine.utils.BufferUtils;
import com.aionemu.gameserver.geoEngine.utils.IntMap;
import com.aionemu.gameserver.geoEngine.utils.IntMap.Entry;

/**
 * Represents a 3D geometric mesh used within the game scene.<br>
 * This class manages vertex and index data for rendering and collision detection.<br>
 * It provides the structural foundation for complex objects in the {@code scene} engine.
 */
public class Mesh
{
	// TODO: Document this enum
	public enum Mode
	{
		Points,
		Lines,
		LineLoop,
		LineStrip,
		Triangles,
		TriangleStrip,
		TriangleFan,
		Hybrid
	}
	
	// private static final int BUFFERS_SIZE = VertexBuffer.Type.BoneIndex.ordinal() + 1;
	/**
	 * The bounding volume that contains the mesh entirely. By default a BoundingBox (AABB).
	 */
	private BoundingVolume meshBound = new BoundingBox();
	private CollisionData collisionTree = null;
	// private EnumMap<VertexBuffer.Type, VertexBuffer> buffers = new EnumMap<Type,
	// VertexBuffer>(VertexBuffer.Type.class);
	// private VertexBuffer[] buffers = new VertexBuffer[BUFFERS_SIZE];
	private final IntMap<VertexBuffer> buffers = new IntMap<>();
	private float pointSize = 1;
	private float lineWidth = 1;
	private transient int vertexArrayID = -1;
	private int vertCount = -1;
	private int elementCount = -1;
	private int maxNumWeights = -1; // only if using skeletal animation
	private int[] modeStart;
	private Mode mode = Mode.Triangles;
	private short collisionFlags = -1;
	
	/**
	 * Creates a new instance of the {@link Mesh} class.<br>
	 * This constructor initializes a default mesh object with no specific data.
	 */
	public Mesh()
	{
	}
	
	/**
	 * Retrieves the starting index for each rendering mode.<br>
	 * This array defines where different geometry types begin in the buffer.
	 * @return an {@code int[]} containing the start indices.
	 */
	public int[] getModeStart()
	{
		return modeStart;
	}
	
	/**
	 * Sets the starting index for the mesh modes.<br>
	 * This method updates the {@code modeStart} field with the provided array.
	 * @param modeStart The array of integers representing the start positions.
	 */
	public void setModeStart(int[] modeStart)
	{
		this.modeStart = modeStart;
	}
	
	/**
	 * Retrieves the current rendering mode of this {@code Mesh}.<br>
	 * This indicates how the vertices are connected to form geometry.
	 * @return The current {@code Mode} of the mesh.
	 */
	public Mode getMode()
	{
		return mode;
	}
	
	/**
	 * Sets the rendering {@code Mode} for this mesh.<br>
	 * This method updates the internal state and calls {@code updateCounts}.
	 * @param mode The new {@code Mode} to apply.
	 */
	public void setMode(Mode mode)
	{
		this.mode = mode;
		updateCounts();
	}
	
	/**
	 * Retrieves the maximum number of weights allowed for a vertex.<br>
	 * This value is used during mesh processing and animation calculations.
	 * @return The maximum number of weights as an {@code int}.
	 */
	public int getMaxNumWeights()
	{
		return maxNumWeights;
	}
	
	/**
	 * Sets the maximum number of weights for a mesh.<br>
	 * This value determines how many bone weights can be applied to each vertex.
	 * @param maxNumWeights The maximum number of weights to allow.
	 */
	public void setMaxNumWeights(int maxNumWeights)
	{
		this.maxNumWeights = maxNumWeights;
	}
	
	/**
	 * Retrieves the size of the points in this mesh.<br>
	 * This value is used when the {@code getMode} is set to {@code Mode#Points}.
	 * @return The current point size as a {@code float}.
	 */
	public float getPointSize()
	{
		return pointSize;
	}
	
	/**
	 * Sets the size of the points for this {@code Mesh}.<br>
	 * This value is used when the mesh is rendered in a point-based mode.
	 * @param pointSize The size to apply to each point.
	 */
	public void setPointSize(float pointSize)
	{
		this.pointSize = pointSize;
	}
	
	/**
	 * Retrieves the current width of the lines in this mesh.<br>
	 * This value is used for rendering purposes.
	 * @return The thickness of the line as a {@code float}.
	 */
	public float getLineWidth()
	{
		return lineWidth;
	}
	
	/**
	 * Sets the thickness of the lines for this {@code Mesh}.<br>
	 * This value affects how wide the rendered lines appear.
	 * @param lineWidth The width of the line to set.
	 */
	public void setLineWidth(float lineWidth)
	{
		this.lineWidth = lineWidth;
	}
	
	/**
	 * Marks the mesh as static for rendering.<br>
	 * This method sets the {@code Usage} of all internal vertex buffers to {@code Usage.Static}.
	 */
	@SuppressWarnings("unchecked")
	public void setStatic()
	{
		for (Entry<VertexBuffer> entry : buffers)
		{
			entry.getValue().setUsage(Usage.Static);
		}
	}
	
	/**
	 * Enables streaming for all vertex buffers in this mesh.<br>
	 * This sets the {@code Usage} of each buffer to {@code Usage.Stream}.<br>
	 * It is used when data changes frequently every frame.
	 */
	@SuppressWarnings("unchecked")
	public void setStreamed()
	{
		for (Entry<VertexBuffer> entry : buffers)
		{
			entry.getValue().setUsage(Usage.Stream);
		}
	}
	
	/**
	 * Combines all vertex buffers into a single interleaved buffer.<br>
	 * This method organizes data so that all attributes for a single vertex are stored contiguously.<br>
	 * It updates the offsets and strides of each {@link VertexBuffer} to point into the new shared buffer.
	 */
	@SuppressWarnings("unchecked")
	public void setInterleaved()
	{
		final ArrayList<VertexBuffer> vbs = new ArrayList<>();
		for (Entry<VertexBuffer> entry : buffers)
		{
			vbs.add(entry.getValue());
		}
		
		// ArrayList<VertexBuffer> vbs = new ArrayList<VertexBuffer>(buffers.values());
		// index buffer not included when interleaving
		vbs.remove(getBuffer(Type.Index));
		
		int stride = 0; // aka bytes per vertex
		for (int i = 0; i < vbs.size(); i++)
		{
			final VertexBuffer vb = vbs.get(i);
			
			// if (vb.getFormat() != Format.Float){
			// Throw an UnsupportedOperationException because the vertex buffer contains non-float data and cannot be interleaved.
			// }
			
			stride += vb.componentsLength;
			vb.getData().clear(); // reset position & limit (used later)
		}
		
		final VertexBuffer allData = new VertexBuffer(Type.InterleavedData);
		final ByteBuffer dataBuf = BufferUtils.createByteBuffer(stride * getVertexCount());
		allData.setupData(Usage.Static, -1, Format.UnsignedByte, dataBuf);
		setBuffer(allData);
		
		for (int vert = 0; vert < getVertexCount(); vert++)
		{
			for (int i = 0; i < vbs.size(); i++)
			{
				final VertexBuffer vb = vbs.get(i);
				switch (vb.getFormat())
				{
					case Float:
						final FloatBuffer fb = (FloatBuffer) vb.getData();
						for (int comp = 0; comp < vb.components; comp++)
						{
							dataBuf.putFloat(fb.get());
						}
						break;
					case Byte:
					case UnsignedByte:
						final ByteBuffer bb = (ByteBuffer) vb.getData();
						for (int comp = 0; comp < vb.components; comp++)
						{
							dataBuf.put(bb.get());
						}
						break;
					case Half:
					case Short:
					case UnsignedShort:
						final ShortBuffer sb = (ShortBuffer) vb.getData();
						for (int comp = 0; comp < vb.components; comp++)
						{
							dataBuf.putShort(sb.get());
						}
						break;
					case Int:
					case UnsignedInt:
						final IntBuffer ib = (IntBuffer) vb.getData();
						for (int comp = 0; comp < vb.components; comp++)
						{
							dataBuf.putInt(ib.get());
						}
						break;
					default:
						break;
				}
			}
		}
		
		int offset = 0;
		for (VertexBuffer vb : vbs)
		{
			vb.setOffset(offset);
			vb.setStride(stride);
			
			// discard old buffer
			vb.setupData(vb.usage, vb.components, vb.format, null);
			offset += vb.componentsLength;
		}
	}
	
	/**
	 * Calculates the total number of elements based on the buffer size and current mode.<br>
	 * This method handles different geometry types like triangles, lines, and points.
	 * @param bufSize The total size of the buffer.
	 * @return The calculated count of elements.
	 */
	private int computeNumElements(int bufSize)
	{
		switch (mode)
		{
			case Triangles:
				return bufSize / 3;
			case TriangleFan:
			case TriangleStrip:
				return bufSize - 2;
			case Points:
				return bufSize;
			case Lines:
				return bufSize / 2;
			case LineLoop:
				return bufSize;
			case LineStrip:
				return bufSize - 1;
			default:
				throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * Updates the internal vertex and element counts for this {@link Mesh}.<br>
	 * It calculates the count based on the available position and index buffers.<br>
	 * This method must be called before setting the mesh to interleaved mode.
	 */
	public void updateCounts()
	{
		if (getBuffer(Type.InterleavedData) != null)
		{
			throw new IllegalStateException("Should update counts before interleave");
		}
		
		final VertexBuffer pb = getBuffer(Type.Position);
		final VertexBuffer ib = getBuffer(Type.Index);
		if (pb != null)
		{
			vertCount = pb.getData().capacity() / pb.getNumComponents();
		}
		
		if (ib != null)
		{
			elementCount = computeNumElements(ib.getData().capacity());
		}
		else
		{
			elementCount = computeNumElements(vertCount);
		}
	}
	
	/**
	 * Retrieves the number of triangles for a specific level of detail.<br>
	 * This method uses the {@code lod} parameter to select the correct count.
	 * @param lod The level of detail index to query.
	 * @return The total number of triangles at the specified level.
	 */
	public int getTriangleCount(int lod)
	{
		return elementCount;
	}
	
	/**
	 * Retrieves the total number of triangles in the current mesh.<br>
	 * This method calls {@code getTriangleCount} to get the value.
	 * @return The total count of triangles as an {@code int}.
	 */
	public int getTriangleCount()
	{
		return elementCount;
	}
	
	/**
	 * Returns the total number of vertices in the current mesh.<br>
	 * This method calls {@code getVertexCount} to retrieve the count.
	 * @return The number of vertices as an {@code int}.
	 */
	public int getVertexCount()
	{
		return vertCount;
	}
	
	/**
	 * Sets the total number of triangles for this {@code Mesh}.<br>
	 * This updates the internal {@code elementCount} field.
	 * @param count The new number of triangles to set.
	 */
	public void setTriangleCount(int count)
	{
		elementCount = count;
	}
	
	/**
	 * Sets the total number of vertices for this {@link Mesh}.<br>
	 * This value is used to define the size of the vertex data.
	 * @param count The number of vertices to set.
	 */
	public void setVertexCount(int count)
	{
		vertCount = count;
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
		final VertexBuffer pb = getBuffer(Type.Position);
		final VertexBuffer ib = getBuffer(Type.Index);
		
		if (pb.getFormat() == Format.Float)
		{
			final FloatBuffer fpb = (FloatBuffer) pb.getData();
			
			if (ib.getFormat() == Format.UnsignedShort)
			{
				// accepted format for buffers
				final ShortBuffer sib = (ShortBuffer) ib.getData();
				
				// aquire triangle's vertex indices
				final int vertIndex = index * 3;
				final int vert1 = sib.get(vertIndex);
				final int vert2 = sib.get(vertIndex + 1);
				final int vert3 = sib.get(vertIndex + 2);
				
				BufferUtils.populateFromBuffer(v1, fpb, vert1);
				BufferUtils.populateFromBuffer(v2, fpb, vert2);
				BufferUtils.populateFromBuffer(v3, fpb, vert3);
			}
		}
	}
	
	/**
	 * Retrieves a specific triangle from the mesh and populates the provided object.<br>
	 * This method uses the {@code index} to find the correct geometry data.<br>
	 * The retrieved data is stored directly into the {@code tri} parameter.
	 * @param index The position of the triangle in the mesh sequence.
	 * @param tri The {@link Triangle} object to be populated with data.
	 */
	public void getTriangle(int index, Triangle tri)
	{
		getTriangle(index, tri.get1(), tri.get2(), tri.get3());
		tri.setIndex(index);
	}
	
	/**
	 * Retrieves the vertex indices for a specific triangle.<br>
	 * The values are stored into the provided {@code int[]} array.
	 * @param index The position of the triangle in the mesh.
	 * @param indices An array to store the three vertex indices.
	 */
	public void getTriangle(int index, int[] indices)
	{
		final VertexBuffer ib = getBuffer(Type.Index);
		if (ib.getFormat() == Format.UnsignedShort)
		{
			// accepted format for buffers
			final ShortBuffer sib = (ShortBuffer) ib.getData();
			
			// aquire triangle's vertex indices
			final int vertIndex = index * 3;
			indices[0] = sib.get(vertIndex);
			indices[1] = sib.get(vertIndex + 1);
			indices[2] = sib.get(vertIndex + 2);
		}
	}
	
	/**
	 * Retrieves the unique identifier for this {@link Mesh}.<br>
	 * This value corresponds to the internal vertex array ID.
	 * @return The integer ID of the mesh.
	 */
	public int getId()
	{
		return vertexArrayID;
	}
	
	/**
	 * Sets the unique identifier for this {@link GLObject}.<br>
	 * This method ensures that an ID is only assigned once.<br>
	 * It will throw an {@code IllegalStateException} if the ID is already set.
	 * @param id The new integer value to assign as the object's ID.
	 */
	public void setId(int id)
	{
		if (vertexArrayID != -1)
		{
			throw new IllegalStateException("ID has already been set.");
		}
		
		vertexArrayID = id;
	}
	
	/**
	 * Initializes the collision data for this {@link Mesh}.<br>
	 * It builds a {@link com.aionemu.gameserver.geoEngine.collision.bih.BIHTree} to handle spatial queries.<br>
	 * This method does nothing if the {@code collisionTree} is already initialized.
	 */
	public void createCollisionData()
	{
		if (collisionTree != null)
		{
			return;
		}
		
		final BIHTree tree = new BIHTree(this);
		tree.construct();
		collisionTree = tree;
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
	public int collideWith(Collidable other, Matrix4f worldMatrix, BoundingVolume worldBound, CollisionResults results)
	{
		if (collisionTree == null)
		{
			createCollisionData();
		}
		
		return collisionTree.collideWith(other, worldMatrix, worldBound, results);
	}
	
	/**
	 * Sets the data buffer for a specific vertex buffer type.<br>
	 * This method initializes or updates the {@code VertexBuffer} with new data.<br>
	 * It automatically calls {@code updateCounts} after the update.
	 * @param type The {@code Type} of the vertex buffer to set.
	 * @param components The number of components per vertex.
	 * @param buf The {@code FloatBuffer} containing the raw data.
	 */
	public void setBuffer(Type type, int components, FloatBuffer buf)
	{
		VertexBuffer vb = buffers.get(type.ordinal());
		if (vb == null)
		{
			if (buf == null)
			{
				return;
			}
			
			vb = new VertexBuffer(type);
			vb.setupData(Usage.Dynamic, components, Format.Float, buf);
			
			// buffers.put(type, vb);
			buffers.put(type.ordinal(), vb);
		}
		else
		{
			vb.setupData(Usage.Dynamic, components, Format.Float, buf);
		}
		
		updateCounts();
	}
	
	/**
	 * Sets the data buffer for a specific vertex attribute.<br>
	 * This method creates a new {@code FloatBuffer} from the provided array.
	 * @param type The {@code Type} of the buffer to set.
	 * @param components The number of components per vertex.
	 * @param buf The raw float array containing the buffer data.
	 */
	public void setBuffer(Type type, int components, float[] buf)
	{
		setBuffer(type, components, BufferUtils.createFloatBuffer(buf));
	}
	
	/**
	 * Sets the data buffer for a specific vertex attribute.<br>
	 * This method initializes a new {@link VertexBuffer} if one does not already exist.<br>
	 * It updates the internal counts of the mesh after setting the data.
	 * @param type The {@code Type} of the buffer to set.
	 * @param components The number of components per vertex for this type.
	 * @param buf The {@code IntBuffer} containing the raw data.
	 */
	public void setBuffer(Type type, int components, IntBuffer buf)
	{
		VertexBuffer vb = buffers.get(type.ordinal());
		if (vb == null)
		{
			vb = new VertexBuffer(type);
			vb.setupData(Usage.Dynamic, components, Format.UnsignedInt, buf);
			buffers.put(type.ordinal(), vb);
			updateCounts();
		}
	}
	
	/**
	 * Sets the data buffer for a specific vertex attribute.<br>
	 * This method converts the provided array into an {@code IntBuffer}.
	 * @param type The {@code Type} of the buffer to set.
	 * @param components The number of components per vertex.
	 * @param buf The raw integer array containing the buffer data.
	 */
	public void setBuffer(Type type, int components, int[] buf)
	{
		setBuffer(type, components, BufferUtils.createIntBuffer(buf));
	}
	
	/**
	 * Sets the data buffer for a specific vertex attribute.<br>
	 * This method initializes a new {@link VertexBuffer} if one does not already exist.<br>
	 * It uses the provided components and format to configure the buffer.
	 * @param type The {@link Type} of the vertex attribute to set.
	 * @param components The number of components per vertex for this type.
	 * @param buf The {@code ShortBuffer} containing the raw data.
	 */
	public void setBuffer(Type type, int components, ShortBuffer buf)
	{
		VertexBuffer vb = buffers.get(type.ordinal());
		if (vb == null)
		{
			vb = new VertexBuffer(type);
			vb.setupData(Usage.Dynamic, components, Format.UnsignedShort, buf);
			buffers.put(type.ordinal(), vb);
			updateCounts();
		}
	}
	
	/**
	 * Sets the data buffer for a specific vertex attribute.<br>
	 * This method creates a new {@code ByteBuffer} from the provided array.<br>
	 * It updates the internal mesh data based on the specified type and component count.
	 * @param type The {@code Type} of the buffer to set.
	 * @param components The number of components per vertex for this buffer.
	 * @param buf The raw byte array containing the buffer data.
	 */
	public void setBuffer(Type type, int components, byte[] buf)
	{
		setBuffer(type, components, BufferUtils.createByteBuffer(buf));
	}
	
	/**
	 * Sets the data buffer for a specific vertex attribute.<br>
	 * This method initializes a new {@link VertexBuffer} if one does not already exist.<br>
	 * It updates the internal counts of the mesh after setting the data.
	 * @param type The {@code Type} of the buffer to set.
	 * @param components The number of components per vertex for this type.
	 * @param buf The {@code ByteBuffer} containing the raw data.
	 */
	public void setBuffer(Type type, int components, ByteBuffer buf)
	{
		VertexBuffer vb = buffers.get(type.ordinal());
		if (vb == null)
		{
			vb = new VertexBuffer(type);
			vb.setupData(Usage.Dynamic, components, Format.UnsignedByte, buf);
			buffers.put(type.ordinal(), vb);
			updateCounts();
		}
	}
	
	/**
	 * Sets the vertex buffer for this mesh.<br>
	 * This method assigns a {@link VertexBuffer} to the internal buffer map.<br>
	 * It throws an {@code IllegalArgumentException} if a buffer of the same type already exists.
	 * @param vb The {@link VertexBuffer} to associate with this mesh.
	 */
	public void setBuffer(VertexBuffer vb)
	{
		if (buffers.containsKey(vb.getBufferType().ordinal()))
		{
			throw new IllegalArgumentException("Buffer type already set: " + vb.getBufferType());
		}
		
		buffers.put(vb.getBufferType().ordinal(), vb);
	}
	
	/**
	 * Removes a specific buffer from the internal collection.<br>
	 * This method clears the data associated with the provided {@code type}.
	 * @param type The {@code Type} of the buffer to remove.
	 */
	public void clearBuffer(VertexBuffer.Type type)
	{
		buffers.remove(type.ordinal());
	}
	
	/**
	 * Sets the data buffer for a specific vertex attribute.<br>
	 * This method assigns a {@code short[]} array to a buffer of the given {@link Type}.<br>
	 * It automatically converts the input array into a {@code ShortBuffer}.
	 * @param type The category of the vertex data, such as position or normal.
	 * @param components The number of components per vertex for this attribute.
	 * @param buf The raw short array containing the buffer data.
	 */
	public void setBuffer(Type type, int components, short[] buf)
	{
		setBuffer(type, components, BufferUtils.createShortBuffer(buf));
	}
	
	/**
	 * Retrieves a specific {@link VertexBuffer} from the mesh.<br>
	 * The buffer is selected based on the provided {@code Type}.
	 * @param type The {@code Type} of the vertex buffer to retrieve.
	 * @return The requested {@link VertexBuffer} object.
	 */
	public VertexBuffer getBuffer(Type type)
	{
		return buffers.get(type.ordinal());
	}
	
	/**
	 * Retrieves the {@code FloatBuffer} data for a specific vertex buffer type.<br>
	 * This method calls {@code getBuffer} to find the correct buffer.<br>
	 * It returns {@code null} if the requested buffer does not exist.
	 * @param type The {@code VertexBuffer.Type} of the buffer to retrieve.
	 * @return The {@code FloatBuffer} associated with the given type, or {@code null}.
	 */
	public FloatBuffer getFloatBuffer(Type type)
	{
		final VertexBuffer vb = getBuffer(type);
		if (vb == null)
		{
			return null;
		}
		
		return (FloatBuffer) vb.getData();
	}
	
	/**
	 * Retrieves the {@code ShortBuffer} data for a specific vertex buffer type.<br>
	 * This method calls {@code getBuffer} to find the correct buffer.<br>
	 * It returns {@code null} if no buffer exists for the requested type.
	 * @param type The {@code VertexBuffer.Type} to retrieve data from.
	 * @return The {@code ShortBuffer} associated with the given type, or {@code null}.
	 */
	public ShortBuffer getShortBuffer(Type type)
	{
		final VertexBuffer vb = getBuffer(type);
		if (vb == null)
		{
			return null;
		}
		
		return (ShortBuffer) vb.getData();
	}
	
	/**
	 * Retrieves the index buffer for this mesh.<br>
	 * This method returns a specific implementation of {@link IndexBuffer} based on the underlying data type.<br>
	 * It will return {@code null} if no index buffer exists.
	 * @return The {@code IndexBuffer} associated with the mesh, or {@code null} if it is not available.
	 */
	public IndexBuffer getIndexBuffer()
	{
		final VertexBuffer vb = getBuffer(Type.Index);
		if (vb == null)
		{
			return null;
		}
		
		final Buffer buf = vb.getData();
		if (buf instanceof ByteBuffer)
		{
			return new IndexByteBuffer((ByteBuffer) buf);
		}
		else if (buf instanceof ShortBuffer)
		{
			return new IndexShortBuffer((ShortBuffer) buf);
		}
		else if (buf instanceof IntBuffer)
		{
			return new IndexIntBuffer((IntBuffer) buf);
		}
		else
		{
			throw new UnsupportedOperationException("Index buffer type unsupported: " + buf.getClass());
		}
	}
	
	/**
	 * Scales the texture coordinates of the mesh.<br>
	 * This method multiplies each coordinate by the provided scale factors.<br>
	 * It requires the mesh to have float-based 2D texture coordinates.
	 * @param scaleFactor The {@code Vector2f} containing the X and Y scale values.
	 */
	public void scaleTextureCoordinates(Vector2f scaleFactor)
	{
		final VertexBuffer tc = getBuffer(Type.TexCoord);
		if (tc == null)
		{
			throw new IllegalStateException("The mesh has no texture coordinates");
		}
		
		if (tc.getFormat() != VertexBuffer.Format.Float)
		{
			throw new UnsupportedOperationException("Only float texture coord format is supported");
		}
		
		if (tc.getNumComponents() != 2)
		{
			throw new UnsupportedOperationException("Only 2D texture coords are supported");
		}
		
		final FloatBuffer fb = (FloatBuffer) tc.getData();
		fb.clear();
		for (int i = 0; i < (fb.capacity() / 2); i++)
		{
			float x = fb.get();
			float y = fb.get();
			fb.position(fb.position() - 2);
			x *= scaleFactor.getX();
			y *= scaleFactor.getY();
			fb.put(x).put(y);
		}
		
		fb.clear();
	}
	
	/**
	 * Updates the bounding volume of this mesh.<br>
	 * It initializes a new {@link BoundingBox} if one does not exist.<br>
	 * It computes the bounds using data from the position buffer.
	 */
	public void updateBound()
	{
		final VertexBuffer posBuf = getBuffer(VertexBuffer.Type.Position);
		if (meshBound == null)
		{
			meshBound = new BoundingBox();
		}
		
		if (posBuf != null)
		{
			meshBound.computeFromPoints((FloatBuffer) posBuf.getData());
		}
	}
	
	/**
	 * Retrieves the bounding volume for this {@code Mesh}.<br>
	 * This volume defines the spatial area occupied by the geometry.
	 * @return The {@link BoundingVolume} associated with this mesh.
	 */
	public BoundingVolume getBound()
	{
		return meshBound;
	}
	
	/**
	 * Sets the bounding volume for this {@link Mesh}.<br>
	 * This defines the area that contains the entire model.
	 * @param modelBound The {@code BoundingVolume} to use as the new bound.
	 */
	public void setBound(BoundingVolume modelBound)
	{
		meshBound = modelBound;
	}
	
	/**
	 * Retrieves the collection of vertex buffers associated with this {@link Mesh}.<br>
	 * The map uses integer keys to identify each buffer.
	 * @return An {@code IntMap} containing all {@link VertexBuffer} objects.
	 */
	public IntMap<VertexBuffer> getBuffers()
	{
		return buffers;
	}
	
	/**
	 * Retrieves the collision flags for this geometry.<br>
	 * This method returns the flags stored in the underlying {@link Mesh}.
	 * @return The collision flags as a {@code short}.
	 */
	public short getCollisionFlags()
	{
		return collisionFlags;
	}
	
	/**
	 * Sets the collision flags for this {@link Mesh}.<br>
	 * These flags determine how collisions are handled.
	 * @param collisionFlags The bitmask of collision properties to apply.
	 */
	public void setCollisionFlags(short collisionFlags)
	{
		this.collisionFlags = collisionFlags;
	}
	
	/**
	 * Retrieves the material identifier for this mesh.<br>
	 * This value is extracted from the internal collision flags.
	 * @return The material ID as a {@code byte}.
	 */
	public byte getMaterialId()
	{
		return (byte) (collisionFlags & 0xFF);
	}
	
	/**
	 * Retrieves the intention flags for this collision result.<br>
	 * This value was set during the creation of the {@link CollisionResults} object.
	 * @return The {@code byte} value representing the intentions.
	 */
	public byte getIntentions()
	{
		return (byte) (collisionFlags >> 8);
	}
}
