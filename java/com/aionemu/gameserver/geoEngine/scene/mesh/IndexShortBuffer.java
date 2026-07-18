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
package com.aionemu.gameserver.geoEngine.scene.mesh;

import java.nio.Buffer;
import java.nio.ShortBuffer;

/**
 * This class provides a specialized buffer for storing short integer indices used in mesh data.<br>
 * It extends {@link IndexBuffer} to handle {@code java.nio.ShortBuffer} operations efficiently.
 * @author lex
 */
public class IndexShortBuffer extends IndexBuffer
{
	private final ShortBuffer buf;
	
	/**
	 * Creates a new {@link IndexShortBuffer} using the provided data.<br>
	 * This method copies the reference from the input {@code ShortBuffer}.
	 * @param buffer The {@code ShortBuffer} to be used for indexing.
	 */
	public IndexShortBuffer(ShortBuffer buffer)
	{
		buf = buffer;
	}
	
	/**
	 * Retrieves the value at a specific index from the internal buffer.<br>
	 * This method masks the result to return only the lowest 16 bits.
	 * @param i The index of the element to retrieve.
	 * @return The integer value found at position {@code i}.
	 */
	@Override
	public int get(int i)
	{
		return buf.get(i) & 0x0000FFFF;
	}
	
	/**
	 * Updates the buffer at a specific position.<br>
	 * This method replaces the existing value with a new one.
	 * @param i The index of the element to update.
	 * @param value The new integer value to store.
	 */
	@Override
	public void put(int i, int value)
	{
		buf.put(i, (short) value);
	}
	
	/**
	 * Returns the total number of elements in the buffer.<br>
	 * This value is determined by the {@code limit()} of the underlying {@code ByteBuffer}.
	 * @return The size of the index buffer.
	 */
	@Override
	public int size()
	{
		return buf.limit();
	}
	
	/**
	 * Retrieves the underlying {@code java.nio.Buffer} object.<br>
	 * This method returns the internal {@code ByteBuffer} used by this instance.
	 * @return The internal {@code Buffer} object.
	 */
	@Override
	public Buffer getBuffer()
	{
		return buf;
	}
}
