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
package com.aionemu.gameserver.geoEngine.utils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.geoEngine.math.Vector2f;
import com.aionemu.gameserver.geoEngine.math.Vector3f;

/**
 * This is a helper class for generating {@code java.nio} buffers from jME data classes.<br>
 * It provides utility methods to convert objects like {@link com.aionemu.gameserver.geoEngine.math.Vector2f} and {@link com.aionemu.gameserver.geoEngine.math.Vector3f} into buffer formats.
 * @author Joshua Slack
 */
public final class BufferUtils
{
	// Temporary data objects for Vector2f.
	// private static final Vector3f _tempVec3 = new Vector3f();
	// private static final ColorRGBA _tempColor = new ColorRGBA();
	// -- TRACKER HASH --
	private static final Map<Buffer, Object> trackingHash = new ConcurrentHashMap<>(new WeakHashMap<Buffer, Object>());
	private static final Object ref = new Object();
	private static final boolean trackDirectMemory = false;
	
	// Generic clone of buffer return.
	/**
	 * Creates a copy of the provided {@code Buffer}.<br>
	 * This method supports various buffer types like {@code FloatBuffer} and {@code ByteBuffer}.<br>
	 * It throws an {@code UnsupportedOperationException} if the buffer type is not recognized.
	 * @param buf The original {@code Buffer} to clone.
	 * @return A new copy of the input {@code Buffer}.
	 */
	public static Buffer clone(Buffer buf)
	{
		if (buf instanceof FloatBuffer)
		{
			return clone((FloatBuffer) buf);
		}
		else if (buf instanceof ShortBuffer)
		{
			return clone((ShortBuffer) buf);
		}
		else if (buf instanceof ByteBuffer)
		{
			return clone((ByteBuffer) buf);
		}
		else if (buf instanceof IntBuffer)
		{
			return clone((IntBuffer) buf);
		}
		else if (buf instanceof DoubleBuffer)
		{
			return clone((DoubleBuffer) buf);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	// -- VECTOR3F METHODS --
	
	/**
	 * Creates a {@code FloatBuffer} from an array of {@link Vector3f} objects.<br>
	 * This method flattens the coordinates into a continuous buffer.<br>
	 * If a vector is {@code null}, it puts zeros into the buffer.
	 * @param data The array of {@link Vector3f} to convert.
	 * @return A new {@code FloatBuffer} containing the flattened data, or {@code null} if the input is {@code null}.
	 */
	public static FloatBuffer createFloatBuffer(Vector3f... data)
	{
		if (data == null)
		{
			return null;
		}
		
		final FloatBuffer buff = createFloatBuffer(3 * data.length);
		for (int x = 0; x < data.length; x++)
		{
			if (data[x] != null)
			{
				buff.put(data[x].x).put(data[x].y).put(data[x].z);
			}
			else
			{
				buff.put(0).put(0).put(0);
			}
		}
		
		buff.flip();
		return buff;
	}
	
	/**
	 * Creates a new {@link FloatBuffer} from an array of primitive floats.<br>
	 * This method copies the provided data into a buffer and prepares it for reading.<br>
	 * It returns {@code null} if the input array is {@code null}.
	 * @param data The array of float values to put into the buffer.
	 * @return A {@link FloatBuffer} containing the provided data, or {@code null} if the input was {@code null}.
	 */
	public static FloatBuffer createFloatBuffer(float... data)
	{
		if (data == null)
		{
			return null;
		}
		
		final FloatBuffer buff = createFloatBuffer(data.length);
		buff.clear();
		buff.put(data);
		buff.flip();
		return buff;
	}
	
	/**
	 * Creates a new {@code FloatBuffer} for storing 3D vertex data.<br>
	 * This method allocates enough space for the specified number of vertices.<br>
	 * Each vertex requires 3 float components.
	 * @param vertices The total number of vertices to allocate space for.
	 * @return A new {@code FloatBuffer} ready for use.
	 */
	public static FloatBuffer createVector3Buffer(int vertices)
	{
		final FloatBuffer vBuff = createFloatBuffer(3 * vertices);
		return vBuff;
	}
	
	/**
	 * This method creates a {@code FloatBuffer} for storing 3D vector data.<br>
	 * It checks if the provided {@code buf} is valid and has the correct size.<br>
	 * If the buffer is invalid or the wrong size, it returns a new one.
	 * @param buf The existing {@code FloatBuffer} to check.
	 * @param vertices The number of vertices to accommodate.
	 * @return A {@code FloatBuffer} ready for use.
	 */
	public static FloatBuffer createVector3Buffer(FloatBuffer buf, int vertices)
	{
		if ((buf != null) && (buf.limit() == (3 * vertices)))
		{
			buf.rewind();
			return buf;
		}
		
		return createFloatBuffer(3 * vertices);
	}
	
	/**
	 * Sets the data contained in the given color into the FloatBuffer at the specified index.
	 * @param color the data to insert
	 * @param buf the buffer to insert into
	 * @param index the postion to place the data; in terms of colors not floats
	 */
	/*
	 * public static void setInBuffer(ColorRGBA color, FloatBuffer buf, int index) { buf.position(index*4); buf.put(color.r); buf.put(color.g); buf.put(color.b); buf.put(color.a); }
	 */
	
	/**
	 * This method writes the coordinates of a {@code Vector3f} into a {@code FloatBuffer}.<br>
	 * It places the values at the position determined by the provided {@code index}.<br>
	 * If the vector is {@code null}, it fills that position with zeros.
	 * @param vector The {@code Vector3f} object containing the coordinates to store.
	 * @param buf The {@code FloatBuffer} where the data will be written.
	 * @param index The starting position index for the three float values.
	 */
	public static void setInBuffer(Vector3f vector, FloatBuffer buf, int index)
	{
		if (buf == null)
		{
			return;
		}
		
		if (vector == null)
		{
			buf.put(index * 3, 0);
			buf.put((index * 3) + 1, 0);
			buf.put((index * 3) + 2, 0);
		}
		else
		{
			buf.put(index * 3, vector.x);
			buf.put((index * 3) + 1, vector.y);
			buf.put((index * 3) + 2, vector.z);
		}
	}
	
	/**
	 * This method copies data from a {@code FloatBuffer} into a {@link Vector3f} object.<br>
	 * It reads three consecutive floats starting at the specified position.<br>
	 * The values are assigned to the x, y, and z components of the vector.
	 * @param vector The {@link Vector3f} object to be populated with data.
	 * @param buf The {@code FloatBuffer} containing the source data.
	 * @param index The starting position in the buffer to begin reading from.
	 */
	public static void populateFromBuffer(Vector3f vector, FloatBuffer buf, int index)
	{
		vector.x = buf.get(index * 3);
		vector.y = buf.get((index * 3) + 1);
		vector.z = buf.get((index * 3) + 2);
	}
	
	/**
	 * Converts a {@code FloatBuffer} into an array of {@link Vector3f} objects.<br>
	 * This method reads three floats at a time to create each vector.<br>
	 * The buffer is cleared before processing starts.
	 * @param buff The {@code FloatBuffer} containing the raw coordinate data.
	 * @return An array of {@code Vector3f} objects extracted from the buffer.
	 */
	public static Vector3f[] getVector3Array(FloatBuffer buff)
	{
		buff.clear();
		final Vector3f[] verts = new Vector3f[buff.limit() / 3];
		for (int x = 0; x < verts.length; x++)
		{
			final Vector3f v = new Vector3f(buff.get(), buff.get(), buff.get());
			verts[x] = v;
		}
		
		return verts;
	}
	
	/**
	 * Copies a {@code Vector3f} from one position to another within a {@code FloatBuffer}.<br>
	 * This method uses the internal copy logic to move three float values at once.
	 * @param buf The {@code FloatBuffer} containing the vector data.
	 * @param fromPos The starting index of the source vector.
	 * @param toPos The starting index of the destination vector.
	 */
	public static void copyInternalVector3(FloatBuffer buf, int fromPos, int toPos)
	{
		copyInternal(buf, fromPos * 3, toPos * 3, 3);
	}
	
	/**
	 * Normalizes a 3D vector stored in a {@code FloatBuffer}.<br>
	 * This method updates the values at the specified {@code index} to have a length of 1.<br>
	 * It uses an internal {@link Vector3f} object to perform the calculation.
	 * @param buf The {@code FloatBuffer} containing the vector data.
	 * @param index The starting position in the buffer where the vector is located.
	 */
	public static void normalizeVector3(FloatBuffer buf, int index)
	{
		final Vector3f tempVec3 = Vector3f.newInstance();
		populateFromBuffer(tempVec3, buf, index);
		tempVec3.normalizeLocal();
		setInBuffer(tempVec3, buf, index);
		Vector3f.recycle(tempVec3);
	}
	
	/**
	 * Adds a {@code Vector3f} to an existing vector in a {@code FloatBuffer}.<br>
	 * This method retrieves the data at the specified {@code index}, performs the addition, and updates the buffer.<br>
	 * It uses a temporary object to perform calculations without modifying the original input.
	 * @param toAdd The {@code Vector3f} values to add to the buffer.
	 * @param buf The {@code FloatBuffer} containing the vector data.
	 * @param index The starting position in the buffer where the addition begins.
	 */
	public static void addInBuffer(Vector3f toAdd, FloatBuffer buf, int index)
	{
		final Vector3f tempVec3 = Vector3f.newInstance();
		populateFromBuffer(tempVec3, buf, index);
		tempVec3.addLocal(toAdd);
		setInBuffer(tempVec3, buf, index);
		Vector3f.recycle(tempVec3);
	}
	
	/**
	 * Multiplies a vector in the buffer by a given multiplier.<br>
	 * This method retrieves the data at {@code index}, performs the multiplication, and updates the buffer.
	 * @param toMult The {@link Vector3f} used as the multiplier.
	 * @param buf The {@link FloatBuffer} containing the vector data.
	 * @param index The starting position in the buffer where the operation begins.
	 */
	public static void multInBuffer(Vector3f toMult, FloatBuffer buf, int index)
	{
		final Vector3f tempVec3 = Vector3f.newInstance();
		populateFromBuffer(tempVec3, buf, index);
		tempVec3.multLocal(toMult);
		setInBuffer(tempVec3, buf, index);
		Vector3f.recycle(tempVec3);
	}
	
	/**
	 * Checks if a {@code Vector3f} matches the data stored in a {@code FloatBuffer}.<br>
	 * It compares the values at the specified {@code index}.
	 * @param check The {@code Vector3f} object to compare against.
	 * @param buf The {@code FloatBuffer} containing the data.
	 * @param index The starting position in the buffer.
	 * @return {@code true} if the values are equal, {@code false} otherwise.
	 */
	public static boolean equals(Vector3f check, FloatBuffer buf, int index)
	{
		final Vector3f tempVec3 = Vector3f.newInstance();
		populateFromBuffer(tempVec3, buf, index);
		final boolean eq = tempVec3.equals(check);
		Vector3f.recycle(tempVec3);
		return eq;
	}
	
	// -- VECTOR2F METHODS -- ////
	
	/**
	 * Creates a {@code FloatBuffer} from an array of {@link Vector2f} objects.<br>
	 * Each vector is stored as two consecutive floats in the buffer.<br>
	 * If a vector is {@code null}, it is treated as {@code 0.0f} for both coordinates.
	 * @param data The array of {@link Vector2f} to convert into a buffer.
	 * @return A new {@code FloatBuffer} containing the flattened vector data, or {@code null} if the input is {@code null}.
	 */
	public static FloatBuffer createFloatBuffer(Vector2f... data)
	{
		if (data == null)
		{
			return null;
		}
		
		final FloatBuffer buff = createFloatBuffer(2 * data.length);
		for (int x = 0; x < data.length; x++)
		{
			if (data[x] != null)
			{
				buff.put(data[x].x).put(data[x].y);
			}
			else
			{
				buff.put(0).put(0);
			}
		}
		
		buff.flip();
		return buff;
	}
	
	/**
	 * Creates a new {@code FloatBuffer} for 2D vector data.<br>
	 * This method allocates space for the specified number of vertices.<br>
	 * Each vertex requires 2 floats to represent its coordinates.
	 * @param vertices The number of vertices to allocate for.
	 * @return A new {@code FloatBuffer} containing the allocated memory.
	 */
	public static FloatBuffer createVector2Buffer(int vertices)
	{
		final FloatBuffer vBuff = createFloatBuffer(2 * vertices);
		return vBuff;
	}
	
	/**
	 * Creates a {@code FloatBuffer} for 2D vectors.<br>
	 * This method checks if the provided {@code buf} is already the correct size.<br>
	 * If it is not valid or has the wrong limit, a new buffer is created.
	 * @param buf The existing {@code FloatBuffer} to check.
	 * @param vertices The number of vertices to allocate for.
	 * @return A {@code FloatBuffer} ready for use.
	 */
	public static FloatBuffer createVector2Buffer(FloatBuffer buf, int vertices)
	{
		if ((buf != null) && (buf.limit() == (2 * vertices)))
		{
			buf.rewind();
			return buf;
		}
		
		return createFloatBuffer(2 * vertices);
	}
	
	// -- INT METHODS --
	
	/**
	 * Creates a new {@link IntBuffer} from an array of integers.<br>
	 * This method copies the provided data into a buffer and flips it for reading.<br>
	 * It returns {@code null} if the input array is {@code null}.
	 * @param data The array of integers to put into the buffer.
	 * @return A new {@link IntBuffer} containing the provided data, or {@code null} if the input is {@code null}.
	 */
	public static IntBuffer createIntBuffer(int... data)
	{
		if (data == null)
		{
			return null;
		}
		
		final IntBuffer buff = createIntBuffer(data.length);
		buff.clear();
		buff.put(data);
		buff.flip();
		return buff;
	}
	
	/**
	 * Converts an {@code IntBuffer} into a standard integer array.<br>
	 * This method clears the buffer before reading all elements.<br>
	 * It returns {@code null} if the provided buffer is {@code null}.
	 * @param buff The {@code IntBuffer} to convert.
	 * @return A new {@code int[]} containing the data from the buffer, or {@code null}.
	 */
	public static int[] getIntArray(IntBuffer buff)
	{
		if (buff == null)
		{
			return null;
		}
		
		buff.clear();
		final int[] inds = new int[buff.limit()];
		for (int x = 0; x < inds.length; x++)
		{
			inds[x] = buff.get();
		}
		
		return inds;
	}
	
	/**
	 * Converts a {@code FloatBuffer} into a primitive float array.<br>
	 * This method clears the buffer before reading all elements.<br>
	 * It returns {@code null} if the provided buffer is {@code null}.
	 * @param buff The {@code FloatBuffer} to convert.
	 * @return A new {@code float[]} containing the data from the buffer, or {@code null}.
	 */
	public static float[] getFloatArray(FloatBuffer buff)
	{
		if (buff == null)
		{
			return null;
		}
		
		buff.clear();
		final float[] inds = new float[buff.limit()];
		for (int x = 0; x < inds.length; x++)
		{
			inds[x] = buff.get();
		}
		
		return inds;
	}
	
	// -- GENERAL DOUBLE ROUTINES --
	
	/**
	 * Creates a new {@link DoubleBuffer} with the specified capacity.<br>
	 * This method allocates direct memory using {@code ByteBuffer}.<br>
	 * The buffer is cleared before being returned.
	 * @param size The number of doubles to allocate in the buffer.
	 * @return A new {@code DoubleBuffer} instance.
	 */
	public static DoubleBuffer createDoubleBuffer(int size)
	{
		final DoubleBuffer buf = ByteBuffer.allocateDirect(8 * size).order(ByteOrder.nativeOrder()).asDoubleBuffer();
		buf.clear();
		if (trackDirectMemory)
		{
			trackingHash.put(buf, ref);
		}
		
		return buf;
	}
	
	/**
	 * Creates a new {@code DoubleBuffer} if the provided buffer is invalid or has the wrong size.<br>
	 * This method checks if the input {@code buf} is not {@code null} and matches the requested {@code size}.<br>
	 * If it matches, the buffer is rewound and returned.<br>
	 * Otherwise, a new buffer of the specified size is created.
	 * @param buf The original {@code DoubleBuffer} to check.
	 * @param size The required capacity for the buffer.
	 * @return A valid {@code DoubleBuffer} with the correct size.
	 */
	public static DoubleBuffer createDoubleBuffer(DoubleBuffer buf, int size)
	{
		if ((buf != null) && (buf.limit() == size))
		{
			buf.rewind();
			return buf;
		}
		
		buf = createDoubleBuffer(size);
		return buf;
	}
	
	/**
	 * Creates a new {@code DoubleBuffer} that is a copy of the provided buffer.<br>
	 * This method resets the position of the original buffer before copying.<br>
	 * It returns {@code null} if the input buffer is {@code null}.
	 * @param buf The source {@code DoubleBuffer} to clone.
	 * @return A new {@code DoubleBuffer} containing the same data, or {@code null}.
	 */
	public static DoubleBuffer clone(DoubleBuffer buf)
	{
		if (buf == null)
		{
			return null;
		}
		
		buf.rewind();
		
		DoubleBuffer copy;
		if (buf.isDirect())
		{
			copy = createDoubleBuffer(buf.limit());
		}
		else
		{
			copy = DoubleBuffer.allocate(buf.limit());
		}
		
		copy.put(buf);
		
		return copy;
	}
	
	// -- GENERAL FLOAT ROUTINES --
	
	/**
	 * Creates a new {@link FloatBuffer} with a specific capacity.<br>
	 * This method allocates direct memory for the buffer.<br>
	 * The buffer is initialized with the native byte order.
	 * @param size The number of floats to allocate in the buffer.
	 * @return A new {@code FloatBuffer} instance.
	 */
	public static FloatBuffer createFloatBuffer(int size)
	{
		final FloatBuffer buf = ByteBuffer.allocateDirect(4 * size).order(ByteOrder.nativeOrder()).asFloatBuffer();
		buf.clear();
		if (trackDirectMemory)
		{
			trackingHash.put(buf, ref);
		}
		
		return buf;
	}
	
	/**
	 * Copies a range of floats within the same {@code FloatBuffer}.<br>
	 * It moves data from a source position to a destination position.
	 * @param buf The {@code FloatBuffer} to copy data from and to.
	 * @param fromPos The starting index of the source data.
	 * @param toPos The starting index where the data will be written.
	 * @param length The number of floats to copy.
	 */
	public static void copyInternal(FloatBuffer buf, int fromPos, int toPos, int length)
	{
		final float[] data = new float[length];
		buf.position(fromPos);
		buf.get(data);
		buf.position(toPos);
		buf.put(data);
	}
	
	/**
	 * Creates a new {@code FloatBuffer} that is a copy of the provided buffer.<br>
	 * This method resets the position of the original buffer before copying.<br>
	 * It returns {@code null} if the input buffer is {@code null}.
	 * @param buf The source {@code FloatBuffer} to clone.
	 * @return A new {@code FloatBuffer} containing the same data, or {@code null}.
	 */
	public static FloatBuffer clone(FloatBuffer buf)
	{
		if (buf == null)
		{
			return null;
		}
		
		buf.rewind();
		
		FloatBuffer copy;
		if (buf.isDirect())
		{
			copy = createFloatBuffer(buf.limit());
		}
		else
		{
			copy = FloatBuffer.allocate(buf.limit());
		}
		
		copy.put(buf);
		
		return copy;
	}
	
	// -- GENERAL INT ROUTINES --
	
	/**
	 * Creates a new {@link IntBuffer} with the specified capacity.<br>
	 * This method allocates direct memory using {@code ByteBuffer}.<br>
	 * The buffer is cleared before being returned.
	 * @param size The number of integers to allocate in the buffer.
	 * @return A new {@code IntBuffer} instance.
	 */
	public static IntBuffer createIntBuffer(int size)
	{
		final IntBuffer buf = ByteBuffer.allocateDirect(4 * size).order(ByteOrder.nativeOrder()).asIntBuffer();
		buf.clear();
		if (trackDirectMemory)
		{
			trackingHash.put(buf, ref);
		}
		
		return buf;
	}
	
	/**
	 * Creates a new {@code IntBuffer} or returns the existing one if it matches the required size.<br>
	 * This method checks if the provided {@code buf} is not {@code null} and has a limit equal to {@code size}.<br>
	 * If the conditions are met, it rewinds the buffer and returns it.<br>
	 * Otherwise, it creates and returns a new {@code IntBuffer} of the specified size.
	 * @param buf The source {@code IntBuffer} to check.
	 * @param size The required capacity for the buffer.
	 * @return A valid {@code IntBuffer} with the correct size.
	 */
	public static IntBuffer createIntBuffer(IntBuffer buf, int size)
	{
		if ((buf != null) && (buf.limit() == size))
		{
			buf.rewind();
			return buf;
		}
		
		buf = createIntBuffer(size);
		return buf;
	}
	
	/**
	 * Creates a new {@code IntBuffer} that is a copy of the provided buffer.<br>
	 * The original buffer is rewound before copying.<br>
	 * If the source is a direct buffer, the result will also be a direct buffer.
	 * @param buf The source {@code IntBuffer} to clone.
	 * @return A new {@code IntBuffer} containing the same data as the input, or {@code null} if the input is {@code null}.
	 */
	public static IntBuffer clone(IntBuffer buf)
	{
		if (buf == null)
		{
			return null;
		}
		
		buf.rewind();
		
		IntBuffer copy;
		if (buf.isDirect())
		{
			copy = createIntBuffer(buf.limit());
		}
		else
		{
			copy = IntBuffer.allocate(buf.limit());
		}
		
		copy.put(buf);
		
		return copy;
	}
	
	// -- GENERAL BYTE ROUTINES --
	
	/**
	 * Creates a new direct {@link ByteBuffer}.<br>
	 * This method uses the native byte order.<br>
	 * The buffer is cleared before being returned.
	 * @param size The capacity of the buffer in bytes.
	 * @return A new {@code ByteBuffer} with the specified size.
	 */
	public static ByteBuffer createByteBuffer(int size)
	{
		final ByteBuffer buf = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
		buf.clear();
		if (trackDirectMemory)
		{
			trackingHash.put(buf, ref);
		}
		
		return buf;
	}
	
	/**
	 * Creates a new {@code ByteBuffer} of the specified size.<br>
	 * If the provided {@code buf} is valid and matches the requested {@code size}, it is returned after being rewound.<br>
	 * Otherwise, a new buffer is allocated.
	 * @param buf The source {@code ByteBuffer} to check.
	 * @param size The required capacity for the buffer.
	 * @return A {@code ByteBuffer} that matches the requested size.
	 */
	public static ByteBuffer createByteBuffer(ByteBuffer buf, int size)
	{
		if ((buf != null) && (buf.limit() == size))
		{
			buf.rewind();
			return buf;
		}
		
		buf = createByteBuffer(size);
		return buf;
	}
	
	/**
	 * Creates a new {@link ByteBuffer} from an array of bytes.<br>
	 * The buffer is populated with the provided data and flipped for reading.
	 * @param data The byte array to be placed into the buffer.
	 * @return A {@link ByteBuffer} containing the input data.
	 */
	public static ByteBuffer createByteBuffer(byte... data)
	{
		final ByteBuffer bb = createByteBuffer(data.length);
		bb.put(data);
		bb.flip();
		return bb;
	}
	
	/**
	 * Converts a {@code String} into a {@link ByteBuffer}.<br>
	 * This method creates a buffer large enough to hold the byte representation of the input.<br>
	 * It automatically flips the buffer so it is ready for reading.
	 * @param data The string to convert into bytes.
	 * @return A {@link ByteBuffer} containing the encoded string data.
	 */
	public static ByteBuffer createByteBuffer(String data)
	{
		final byte[] bytes = data.getBytes();
		final ByteBuffer bb = createByteBuffer(bytes.length);
		bb.put(bytes);
		bb.flip();
		return bb;
	}
	
	/**
	 * Creates a new {@code ByteBuffer} that is a copy of the provided buffer.<br>
	 * This method resets the position of the original buffer before copying.<br>
	 * It handles both direct and non-direct buffers automatically.
	 * @param buf The source {@code ByteBuffer} to clone.
	 * @return A new {@code ByteBuffer} containing the same data, or {@code null} if the input is {@code null}.
	 */
	public static ByteBuffer clone(ByteBuffer buf)
	{
		if (buf == null)
		{
			return null;
		}
		
		buf.rewind();
		
		ByteBuffer copy;
		if (buf.isDirect())
		{
			copy = createByteBuffer(buf.limit());
		}
		else
		{
			copy = ByteBuffer.allocate(buf.limit());
		}
		
		copy.put(buf);
		
		return copy;
	}
	
	// -- GENERAL SHORT ROUTINES --
	
	/**
	 * Creates a new {@link ShortBuffer} with the specified capacity.<br>
	 * This method allocates direct memory using {@code ByteBuffer}.<br>
	 * The buffer is cleared before being returned.
	 * @param size The number of shorts to allocate in the buffer.
	 * @return A new {@code ShortBuffer} instance.
	 */
	public static ShortBuffer createShortBuffer(int size)
	{
		final ShortBuffer buf = ByteBuffer.allocateDirect(2 * size).order(ByteOrder.nativeOrder()).asShortBuffer();
		buf.clear();
		if (trackDirectMemory)
		{
			trackingHash.put(buf, ref);
		}
		
		return buf;
	}
	
	/**
	 * Creates a new {@code ShortBuffer} or returns the existing one if it matches the required size.<br>
	 * This method checks if the provided {@code buf} is not {@code null} and has a limit equal to {@code size}.<br>
	 * If the buffer is valid, it rewinds the position and returns it.<br>
	 * Otherwise, it creates a new {@code ShortBuffer} of the specified size.
	 * @param buf The source {@code ShortBuffer} to check.
	 * @param size The required capacity for the buffer.
	 * @return A valid {@code ShortBuffer} with the correct size.
	 */
	public static ShortBuffer createShortBuffer(ShortBuffer buf, int size)
	{
		if ((buf != null) && (buf.limit() == size))
		{
			buf.rewind();
			return buf;
		}
		
		buf = createShortBuffer(size);
		return buf;
	}
	
	/**
	 * Creates a new {@link ShortBuffer} from an array of shorts.<br>
	 * This method copies the provided data into a buffer and flips it for reading.<br>
	 * It returns {@code null} if the input array is {@code null}.
	 * @param data The array of short values to put into the buffer.
	 * @return A {@link ShortBuffer} containing the provided data, or {@code null} if the input is {@code null}.
	 */
	public static ShortBuffer createShortBuffer(short... data)
	{
		if (data == null)
		{
			return null;
		}
		
		final ShortBuffer buff = createShortBuffer(data.length);
		buff.clear();
		buff.put(data);
		buff.flip();
		return buff;
	}
	
	/**
	 * Creates a new {@code ShortBuffer} that is a copy of the provided buffer.<br>
	 * This method resets the position of the original buffer before copying.<br>
	 * It returns {@code null} if the input buffer is {@code null}.
	 * @param buf The source {@code ShortBuffer} to clone.
	 * @return A new {@code ShortBuffer} containing the same data, or {@code null}.
	 */
	public static ShortBuffer clone(ShortBuffer buf)
	{
		if (buf == null)
		{
			return null;
		}
		
		buf.rewind();
		
		ShortBuffer copy;
		if (buf.isDirect())
		{
			copy = createShortBuffer(buf.limit());
		}
		else
		{
			copy = ShortBuffer.allocate(buf.limit());
		}
		
		copy.put(buf);
		
		return copy;
	}
	
	/**
	 * Checks if the provided {@code FloatBuffer} has enough space for the required elements.<br>
	 * If the buffer is {@code null} or too small, it creates a new one and copies the existing data.
	 * @param buffer The source {@code FloatBuffer} to check.
	 * @param required The number of floats needed in the buffer.
	 * @return A {@code FloatBuffer} that is guaranteed to have at least {@code required} remaining elements.
	 */
	public static FloatBuffer ensureLargeEnough(FloatBuffer buffer, int required)
	{
		if ((buffer == null) || (buffer.remaining() < required))
		{
			final int position = (buffer != null ? buffer.position() : 0);
			final FloatBuffer newVerts = createFloatBuffer(position + required);
			if (buffer != null)
			{
				buffer.rewind();
				newVerts.put(buffer);
				newVerts.position(position);
			}
			
			buffer = newVerts;
		}
		
		return buffer;
	}
	
	/**
	 * Checks if the provided {@code ShortBuffer} has enough space for the required amount.<br>
	 * If it is too small or {@code null}, a new buffer is created and populated.
	 * @param buffer The original {@code ShortBuffer} to check.
	 * @param required The minimum number of elements needed.
	 * @return A {@code ShortBuffer} that contains at least the required space.
	 */
	public static ShortBuffer ensureLargeEnough(ShortBuffer buffer, int required)
	{
		if ((buffer == null) || (buffer.remaining() < required))
		{
			final int position = (buffer != null ? buffer.position() : 0);
			final ShortBuffer newVerts = createShortBuffer(position + required);
			if (buffer != null)
			{
				buffer.rewind();
				newVerts.put(buffer);
				newVerts.position(position);
			}
			
			buffer = newVerts;
		}
		
		return buffer;
	}
	
	/**
	 * Checks if the provided {@code ByteBuffer} has enough space for the required size.<br>
	 * If it is too small or {@code null}, a new buffer is created and populated.
	 * @param buffer The original {@code ByteBuffer} to check.
	 * @param required The number of bytes needed.
	 * @return A {@code ByteBuffer} that is large enough to hold the required data.
	 */
	public static ByteBuffer ensureLargeEnough(ByteBuffer buffer, int required)
	{
		if ((buffer == null) || (buffer.remaining() < required))
		{
			final int position = (buffer != null ? buffer.position() : 0);
			final ByteBuffer newVerts = createByteBuffer(position + required);
			if (buffer != null)
			{
				buffer.rewind();
				newVerts.put(buffer);
				newVerts.position(position);
			}
			
			buffer = newVerts;
		}
		
		return buffer;
	}
	
	/**
	 * Calculates and prints the current memory usage of tracked buffers.<br>
	 * It counts various buffer types and calculates total heap and direct memory.<br>
	 * The results are appended to a {@code StringBuilder}.<br>
	 * If the provided {@code store} is {@code null}, it prints the result to standard output.
	 * @param store The {@code StringBuilder} used to collect the memory statistics.
	 */
	public static void printCurrentDirectMemory(StringBuilder store)
	{
		long totalHeld = 0;
		
		// make a new set to hold the keys to prevent concurrency issues.
		final ArrayList<Buffer> bufs = new ArrayList<>(trackingHash.keySet());
		int fBufs = 0, bBufs = 0, iBufs = 0, sBufs = 0, dBufs = 0;
		int fBufsM = 0, bBufsM = 0, iBufsM = 0, sBufsM = 0, dBufsM = 0;
		for (Buffer b : bufs)
		{
			if (b instanceof ByteBuffer)
			{
				totalHeld += b.capacity();
				bBufsM += b.capacity();
				bBufs++;
			}
			else if (b instanceof FloatBuffer)
			{
				totalHeld += b.capacity() * 4;
				fBufsM += b.capacity() * 4;
				fBufs++;
			}
			else if (b instanceof IntBuffer)
			{
				totalHeld += b.capacity() * 4;
				iBufsM += b.capacity() * 4;
				iBufs++;
			}
			else if (b instanceof ShortBuffer)
			{
				totalHeld += b.capacity() * 2;
				sBufsM += b.capacity() * 2;
				sBufs++;
			}
			else if (b instanceof DoubleBuffer)
			{
				totalHeld += b.capacity() * 8;
				dBufsM += b.capacity() * 8;
				dBufs++;
			}
		}
		
		final long heapMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		final boolean printStout = store == null;
		if (store == null)
		{
			store = new StringBuilder();
		}
		
		store.append("Existing buffers: ").append(bufs.size()).append("\n");
		store.append("(b: ").append(bBufs).append("  f: ").append(fBufs).append("  i: ").append(iBufs).append("  s: ").append(sBufs).append("  d: ").append(dBufs).append(")").append("\n");
		store.append("Total   heap memory held: ").append(heapMem / 1024).append("kb\n");
		store.append("Total direct memory held: ").append(totalHeld / 1024).append("kb\n");
		store.append("(b: ").append(bBufsM / 1024).append("kb  f: ").append(fBufsM / 1024).append("kb  i: ").append(iBufsM / 1024).append("kb  s: ").append(sBufsM / 1024).append("kb  d: ").append(dBufsM / 1024).append("kb)").append("\n");
		if (printStout)
		{
			System.out.println(store.toString());
		}
	}
}
