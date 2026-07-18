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

import com.aionemu.gameserver.geoEngine.math.FastMath;
import com.aionemu.gameserver.geoEngine.utils.BufferUtils;

/**
 * This class represents a buffer of vertex data used for rendering 3D objects.<br>
 * It manages the underlying {@code java.nio.Buffer} to store coordinates and attributes.<br>
 * It extends {@link GLObject} to integrate with the scene's graphical components.
 */
public class VertexBuffer extends GLObject
{
	/**
	 * Type of buffer. Specifies the actual attribute it defines.
	 */
	public static enum Type
	{
		/**
		 * Position of the vertex (3 floats)
		 */
		Position,
		/**
		 * The size of the point when using point buffers.
		 */
		Size,
		/**
		 * Normal vector, normalized.
		 */
		Normal,
		/**
		 * Texture coordinate
		 */
		TexCoord,
		/**
		 * Color and Alpha (4 floats)
		 */
		Color,
		/**
		 * Tangent vector, normalized.
		 */
		Tangent,
		/**
		 * Binormal vector, normalized.
		 */
		Binormal,
		/**
		 * Specifies the source data for various vertex buffers when interleaving is used.
		 */
		InterleavedData,
		/**
		 * Specifies the index buffer, must contain integer data.
		 */
		Index,
		/**
		 * Inital vertex position, used with animation
		 */
		BindPosePosition,
		/**
		 * Inital vertex normals, used with animation
		 */
		BindPoseNormal,
		/**
		 * Bone weights, used with animation
		 */
		BoneWeight,
		/**
		 * Bone indices, used with animation
		 */
		BoneIndex,
		/**
		 * Texture coordinate #2
		 */
		TexCoord2;
	}
	
	/**
	 * The usage of the VertexBuffer, specifies how often the buffer is used. This can determine if a vertex buffer is placed in VRAM or held in video memory, but no garantees are made- it's only a hint.
	 */
	public static enum Usage
	{
		/**
		 * Mesh data is sent once and very rarely updated.
		 */
		Static,
		/**
		 * Mesh data is updated occasionally (once per frame or less).
		 */
		Dynamic,
		/**
		 * Mesh data is updated every frame.
		 */
		Stream,
		/**
		 * Mesh data is not sent to GPU at all. It is only used by the CPU.
		 */
		CpuOnly;
	}
	
	public static enum Format
	{
		// Floating point formats
		
		Half(2),
		Float(4),
		Double(8),
		// Integer formats
		Byte(1),
		UnsignedByte(1),
		Short(2),
		UnsignedShort(2),
		Int(4),
		UnsignedInt(4);
		
		private int componentSize = 0;
		
		Format(int componentSize)
		{
			this.componentSize = componentSize;
		}
		
		/**
		 * @return Size in bytes of this data type.
		 */
		public int getComponentSize()
		{
			return componentSize;
		}
	}
	
	protected int offset = 0;
	protected int stride = 0;
	protected int components = 0;
	/**
	 * derived from components * format.getComponentSize()
	 */
	protected transient int componentsLength = 0;
	protected Buffer data = null;
	protected transient ByteBuffer mappedData;
	protected Usage usage;
	protected Type bufType;
	protected Format format;
	protected boolean normalized = false;
	protected transient boolean dataSizeChanged = false;
	
	/**
	 * Creates a new {@link VertexBuffer} instance with a specific attribute type.<br>
	 * This constructor initializes the buffer to represent data such as positions, normals, or colors.
	 * @param type The {@code Type} of the vertex data this buffer will hold.
	 */
	public VertexBuffer(Type type)
	{
		super(GLObject.Type.VertexBuffer);
		bufType = type;
	}
	
	/**
	 * Creates a new instance of {@link VertexBuffer}.<br>
	 * This constructor initializes the object with the default type.
	 */
	public VertexBuffer()
	{
		super(GLObject.Type.VertexBuffer);
	}
	
	/**
	 * Creates a new {@link VertexBuffer} instance using a specific identifier.<br>
	 * This constructor initializes the object with a unique ID for tracking.
	 * @param id The unique identification number for this buffer.
	 */
	protected VertexBuffer(int id)
	{
		super(GLObject.Type.VertexBuffer, id);
	}
	
	/**
	 * Retrieves the current starting position of the data within the buffer.<br>
	 * This value is used to determine where the vertex attributes begin.
	 * @return The integer value of the {@code offset}.
	 */
	public int getOffset()
	{
		return offset;
	}
	
	/**
	 * Sets the starting position of the data within the buffer.<br>
	 * This value is used to determine where the vertex attributes begin.
	 * @param offset The new starting index for the buffer data.
	 */
	public void setOffset(int offset)
	{
		this.offset = offset;
	}
	
	/**
	 * Retrieves the current stride of the vertex buffer.<br>
	 * The stride represents the byte distance between consecutive vertices.
	 * @return the size of one vertex in bytes.
	 */
	public int getStride()
	{
		return stride;
	}
	
	/**
	 * Sets the byte stride for this vertex buffer.<br>
	 * The stride defines the distance between consecutive vertices in bytes.<br>
	 * Use this to configure how data is read from the underlying buffer.
	 * @param stride The number of bytes between each vertex.
	 */
	public void setStride(int stride)
	{
		this.stride = stride;
	}
	
	/**
	 * Retrieves the raw buffer associated with this {@code VertexBuffer}.<br>
	 * This method returns the underlying {@code Buffer} object.
	 * @return The {@code Buffer} containing the vertex data.
	 */
	public Buffer getData()
	{
		return data;
	}
	
	/**
	 * Retrieves the current {@code ByteBuffer} associated with this vertex buffer.<br>
	 * This is used to access the raw data that has been mapped into memory.
	 * @return the {@code ByteBuffer} containing the mapped data.
	 */
	public ByteBuffer getMappedData()
	{
		return mappedData;
	}
	
	/**
	 * Sets the {@code mappedData} for this vertex buffer.<br>
	 * This method updates the internal reference to a {@code ByteBuffer}.
	 * @param mappedData The {@code ByteBuffer} to be stored.
	 */
	public void setMappedData(ByteBuffer mappedData)
	{
		this.mappedData = mappedData;
	}
	
	/**
	 * Retrieves the current {@code Usage} of this vertex buffer.<br>
	 * This value provides a hint to the graphics driver about how often the data is updated.
	 * @return the {@code Usage} type of the buffer.
	 */
	public Usage getUsage()
	{
		return usage;
	}
	
	/**
	 * Sets the {@code Usage} for this vertex buffer.<br>
	 * This provides a hint to the graphics driver about how often the data will be updated.<br>
	 * It helps determine if the buffer is stored in VRAM or video memory.
	 * @param usage The {@code Usage} type to apply to the buffer.
	 */
	public void setUsage(Usage usage)
	{
		// if (id != -1)
		// throw new UnsupportedOperationException("Data has already been sent. Cannot set usage.");
		
		this.usage = usage;
	}
	
	/**
	 * Sets whether the buffer data is normalized.<br>
	 * This property indicates if the values in the {@code VertexBuffer} are scaled to a standard range.
	 * @param normalized The normalization state to set. Use {@code true} for normalized data and {@code false} otherwise.
	 */
	public void setNormalized(boolean normalized)
	{
		this.normalized = normalized;
	}
	
	/**
	 * Checks if the vertex buffer data is currently normalized.<br>
	 * This indicates whether the values have been scaled to a standard range.
	 * @return {@code true} if the buffer is normalized, {@code false} otherwise.
	 */
	public boolean isNormalized()
	{
		return normalized;
	}
	
	/**
	 * Retrieves the type of this vertex buffer.<br>
	 * This identifies what specific attribute the buffer defines, such as {@code Position} or {@code Normal}.
	 * @return The {@code Type} of the buffer.
	 */
	public Type getBufferType()
	{
		return bufType;
	}
	
	/**
	 * Retrieves the data format of this vertex buffer.<br>
	 * This describes how the underlying data is structured.
	 * @return The {@code Format} associated with this buffer.
	 */
	public Format getFormat()
	{
		return format;
	}
	
	/**
	 * Returns the number of components in this buffer.<br>
	 * This value represents how many data elements exist per vertex.
	 * @return The total count of components as an {@code int}.
	 */
	public int getNumComponents()
	{
		return components;
	}
	
	/**
	 * Returns the total number of elements in this buffer.<br>
	 * This value is calculated based on the data capacity and component count.<br>
	 * It accounts for the {@code Format.Half} type by dividing the result by 2.
	 * @return The total number of elements as an {@code int}.
	 */
	public int getNumElements()
	{
		int elements = data.capacity() / components;
		if (format == Format.Half)
		{
			elements /= 2;
		}
		
		return elements;
	}
	
	/**
	 * Initializes the buffer with specific configuration settings.<br>
	 * This method sets the {@code Usage}, component count, and {@code Format}.<br>
	 * It also assigns the raw {@code Buffer} data to this object.
	 * @param usage The intended frequency of buffer updates.
	 * @param components The number of components per vertex.
	 * @param format The data format used for the buffer.
	 * @param data The source {@link Buffer} containing the vertex information.
	 */
	public void setupData(Usage usage, int components, Format format, Buffer data)
	{
		if (id != -1)
		{
			throw new UnsupportedOperationException("Data has already been sent. Cannot setupData again.");
		}
		
		this.data = data;
		this.components = components;
		this.usage = usage;
		this.format = format;
		componentsLength = components * format.getComponentSize();
		setUpdateNeeded();
	}
	
	/**
	 * Updates the internal buffer of this {@code VertexBuffer}.<br>
	 * This method replaces the current {@code Buffer} with a new one.<br>
	 * It marks the renderer to update the data if the capacity changes.
	 * @param data The new {@code Buffer} to use for this vertex buffer.
	 */
	public void updateData(Buffer data)
	{
		if (id != -1)
		{
			// request to update data is okay
		}
		
		// will force renderer to call glBufferData again
		if (this.data.capacity() != data.capacity())
		{
			dataSizeChanged = true;
		}
		
		this.data = data;
		setUpdateNeeded();
	}
	
	/**
	 * Checks if the size of the buffer data has changed.<br>
	 * This is used to determine if the graphics buffer needs an update.
	 * @return {@code true} if the data size has changed, {@code false} otherwise.
	 */
	public boolean hasDataSizeChanged()
	{
		return dataSizeChanged;
	}
	
	/**
	 * Resets the {@code updateNeeded} flag to {@code false}.<br>
	 * This tells the system that the object data is current.<br>
	 * Use this after you have finished modifying the object.
	 */
	@Override
	public void clearUpdateNeeded()
	{
		super.clearUpdateNeeded();
		dataSizeChanged = false;
	}
	
	/**
	 * Converts the buffer data from {@code Float} to {@code Half} precision.<br>
	 * This method updates the internal format and recalculates the component length.<br>
	 * It replaces the existing data with a new {@code ByteBuffer}.<br>
	 * The {@code setUpdateNeeded} flag is triggered after conversion.
	 */
	public void convertToHalf()
	{
		if (id != -1)
		{
			throw new UnsupportedOperationException("Data has already been sent.");
		}
		
		if (format != Format.Float)
		{
			throw new IllegalStateException("Format must be float!");
		}
		
		final int numElements = data.capacity() / components;
		format = Format.Half;
		componentsLength = components * format.getComponentSize();
		
		final ByteBuffer halfData = BufferUtils.createByteBuffer(componentsLength * numElements);
		halfData.rewind();
		
		final FloatBuffer floatData = (FloatBuffer) data;
		floatData.rewind();
		
		for (int i = 0; i < floatData.capacity(); i++)
		{
			final float f = floatData.get(i);
			final short half = FastMath.convertFloatToHalf(f);
			halfData.putShort(half);
		}
		
		data = halfData;
		setUpdateNeeded();
		dataSizeChanged = true;
	}
	
	/**
	 * Reduces the size of the internal data to a specific number of elements.<br>
	 * This method creates a new buffer with the reduced capacity and updates the current data.<br>
	 * It sets the {@code dataSizeChanged} flag to {@code true}.
	 * @param numElements The total number of elements to keep in the buffer.
	 */
	public void compact(int numElements)
	{
		final int total = components * numElements;
		data.clear();
		switch (format)
		{
			case Byte:
			case UnsignedByte:
			case Half:
				final ByteBuffer bbuf = (ByteBuffer) data;
				bbuf.limit(total);
				final ByteBuffer bnewBuf = BufferUtils.createByteBuffer(total);
				bnewBuf.put(bbuf);
				data = bnewBuf;
				break;
			case Short:
			case UnsignedShort:
				final ShortBuffer sbuf = (ShortBuffer) data;
				sbuf.limit(total);
				final ShortBuffer snewBuf = BufferUtils.createShortBuffer(total);
				snewBuf.put(sbuf);
				data = snewBuf;
				break;
			case Int:
			case UnsignedInt:
				final IntBuffer ibuf = (IntBuffer) data;
				ibuf.limit(total);
				final IntBuffer inewBuf = BufferUtils.createIntBuffer(total);
				inewBuf.put(ibuf);
				data = inewBuf;
				break;
			case Float:
				final FloatBuffer fbuf = (FloatBuffer) data;
				fbuf.limit(total);
				final FloatBuffer fnewBuf = BufferUtils.createFloatBuffer(total);
				fnewBuf.put(fbuf);
				data = fnewBuf;
				break;
			default:
				throw new UnsupportedOperationException("Unrecognized buffer format: " + format);
		}
		
		data.clear();
		setUpdateNeeded();
		dataSizeChanged = true;
	}
	
	/**
	 * Copies a single element from this buffer to another {@link VertexBuffer}.<br>
	 * The source and destination buffers must have the same format and component count.<br>
	 * This method handles different data types like Byte, Short, Int, and Float automatically.
	 * @param inIndex The index of the element to copy from this buffer.
	 * @param outVb The target {@link VertexBuffer} where the data will be placed.
	 * @param outIndex The index where the element should be written in the target buffer.
	 */
	public void copyElement(int inIndex, VertexBuffer outVb, int outIndex)
	{
		if ((outVb.format != format) || (outVb.components != components))
		{
			throw new IllegalArgumentException("Buffer format mismatch. Cannot copy");
		}
		
		int inPos = inIndex * components;
		int outPos = outIndex * components;
		int elementSz = components;
		if (format == Format.Half)
		{
			// because half is stored as bytebuf but its 2 bytes long
			inPos *= 2;
			outPos *= 2;
			elementSz *= 2;
		}
		
		data.clear();
		outVb.data.clear();
		
		switch (format)
		{
			case Byte:
			case UnsignedByte:
			case Half:
				final ByteBuffer bin = (ByteBuffer) data;
				final ByteBuffer bout = (ByteBuffer) outVb.data;
				bin.position(inPos).limit(inPos + elementSz);
				bout.position(outPos).limit(outPos + elementSz);
				bout.put(bin);
				break;
			case Short:
			case UnsignedShort:
				final ShortBuffer sin = (ShortBuffer) data;
				final ShortBuffer sout = (ShortBuffer) outVb.data;
				sin.position(inPos).limit(inPos + elementSz);
				sout.position(outPos).limit(outPos + elementSz);
				sout.put(sin);
				break;
			case Int:
			case UnsignedInt:
				final IntBuffer iin = (IntBuffer) data;
				final IntBuffer iout = (IntBuffer) outVb.data;
				iin.position(inPos).limit(inPos + elementSz);
				iout.position(outPos).limit(outPos + elementSz);
				iout.put(iin);
				break;
			case Float:
				final FloatBuffer fin = (FloatBuffer) data;
				final FloatBuffer fout = (FloatBuffer) outVb.data;
				fin.position(inPos).limit(inPos + elementSz);
				fout.position(outPos).limit(outPos + elementSz);
				fout.put(fin);
				break;
			default:
				throw new UnsupportedOperationException("Unrecognized buffer format: " + format);
		}
		
		data.clear();
		outVb.data.clear();
	}
	
	/**
	 * Creates a new {@code Buffer} based on the specified format and size.<br>
	 * This method calculates the total capacity required for the data.<br>
	 * It returns different types of buffers depending on the {@code Format}.
	 * @param format The data type used to create the buffer.
	 * @param components The number of components per element, which must be between 1 and 4.
	 * @param numElements The total number of elements in the buffer.
	 * @return A new {@code Buffer} instance ready for use.
	 */
	public static Buffer createBuffer(Format format, int components, int numElements)
	{
		if ((components < 1) || (components > 4))
		{
			throw new IllegalArgumentException("Num components must be between 1 and 4");
		}
		
		final int total = numElements * components;
		
		switch (format)
		{
			case Byte:
			case UnsignedByte:
				return BufferUtils.createByteBuffer(total);
			case Half:
				return BufferUtils.createByteBuffer(total * 2);
			case Short:
			case UnsignedShort:
				return BufferUtils.createShortBuffer(total);
			case Int:
			case UnsignedInt:
				return BufferUtils.createIntBuffer(total);
			case Float:
				return BufferUtils.createFloatBuffer(total);
			case Double:
				return BufferUtils.createDoubleBuffer(total);
			default:
				throw new UnsupportedOperationException("Unrecoginized buffer format: " + format);
		}
	}
	
	/**
	 * Creates a new copy of this {@code VertexBuffer}.<br>
	 * This method performs a deep copy of the underlying data.<br>
	 * The new object is independent of the original instance.
	 * @return A new {@code VertexBuffer} instance with copied data.
	 */
	@Override
	public VertexBuffer clone()
	{
		// Note: The superclass GLObject automatically creates a shallow clone, for example by reusing the ID.
		final VertexBuffer vb = (VertexBuffer) super.clone();
		if (data != null)
		{
			vb.updateData(BufferUtils.clone(data));
		}
		
		return vb;
	}
	
	/**
	 * Creates a new copy of this {@link VertexBuffer}.<br>
	 * The new instance copies all properties from the current buffer.<br>
	 * You can optionally specify a different {@code Type} for the clone.
	 * @param overrideType The {@code Type} to assign to the new buffer, or {@code null} to keep the original type.
	 * @return A new {@link VertexBuffer} instance containing the copied data.
	 */
	public VertexBuffer clone(Type overrideType)
	{
		final VertexBuffer vb = new VertexBuffer(overrideType);
		vb.components = components;
		vb.componentsLength = componentsLength;
		vb.data = BufferUtils.clone(data);
		vb.format = format;
		vb.handleRef = new Object();
		vb.id = -1;
		vb.normalized = normalized;
		vb.offset = offset;
		vb.stride = stride;
		vb.updateNeeded = true;
		vb.usage = usage;
		return vb;
	}
	
	/**
	 * Returns a string representation of the {@link VertexBuffer}.<br>
	 * This includes the class name, format, buffer type, and usage.<br>
	 * If data is present, it also includes the capacity of the data.
	 * @return A formatted string describing this vertex buffer.
	 */
	@Override
	public String toString()
	{
		String dataTxt = null;
		if (data != null)
		{
			dataTxt = ", elements=" + data.capacity();
		}
		
		return getClass().getSimpleName() + "[fmt=" + format.name() + ", type=" + bufType.name() + ", usage=" + usage.name() + dataTxt + "]";
	}
	
	/**
	 * Resets the internal state of this {@code VertexBuffer}.<br>
	 * It sets the buffer ID to {@code -1}.<br>
	 * This method also marks the data as needing an update.
	 */
	@Override
	public void resetObject()
	{
		// assert this.id != -1;
		id = -1;
		setUpdateNeeded();
	}
	
	/**
	 * Creates a new copy of this object that can be destroyed.<br>
	 * This method uses the internal {@code id} to initialize the clone.
	 * @return A new {@link GLObject} instance.
	 */
	@Override
	public GLObject createDestructableClone()
	{
		return new VertexBuffer(id);
	}
}
