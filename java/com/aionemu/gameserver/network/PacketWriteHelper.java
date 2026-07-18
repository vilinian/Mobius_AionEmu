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
package com.aionemu.gameserver.network;

import java.nio.ByteBuffer;

/**
 * This utility class provides helper methods for writing data into a {@code ByteBuffer}.<br>
 * It simplifies the process of constructing network packets for the game server.
 * @author -Nemesiss-
 */
public abstract class PacketWriteHelper
{
	protected abstract void writeMe(ByteBuffer buf);
	
	/**
	 * Writes an integer value into the provided {@code ByteBuffer}.<br>
	 * This method uses the {@code putInt} operation.
	 * @param buf The {@code ByteBuffer} where the data will be stored.
	 * @param value The {@code int} value to write.
	 */
	protected void writeD(ByteBuffer buf, int value)
	{
		buf.putInt(value);
	}
	
	/**
	 * Writes a short integer to the provided {@code ByteBuffer}.<br>
	 * This method casts the {@code int} value to a {@code short} before writing.
	 * @param buf The {@code ByteBuffer} where the data will be stored.
	 * @param value The {@code int} value to be written as a short.
	 */
	protected void writeH(ByteBuffer buf, int value)
	{
		buf.putShort((short) value);
	}
	
	/**
	 * Writes a single character to the provided {@code ByteBuffer}.<br>
	 * This method casts the {@code int} value to a {@code byte} before writing.
	 * @param buf The {@code ByteBuffer} where the data will be stored.
	 * @param value The integer value representing the character to write.
	 */
	protected void writeC(ByteBuffer buf, int value)
	{
		buf.put((byte) value);
	}
	
	/**
	 * Writes a {@code double} value into the provided buffer.<br>
	 * This method uses the {@code putDouble} operation on the {@link ByteBuffer}.
	 * @param buf The {@code ByteBuffer} where the data will be stored.
	 * @param value The {@code double} value to write.
	 */
	protected void writeDF(ByteBuffer buf, double value)
	{
		buf.putDouble(value);
	}
	
	/**
	 * Writes a {@code float} value into the provided {@code ByteBuffer}.<br>
	 * This method uses the {@code putFloat} operation.
	 * @param buf The {@code ByteBuffer} where the data will be stored.
	 * @param value The {@code float} value to write.
	 */
	protected void writeF(ByteBuffer buf, float value)
	{
		buf.putFloat(value);
	}
	
	/**
	 * Writes a {@code long} value into the provided buffer.<br>
	 * This method uses the {@code putLong} operation.
	 * @param buf The {@code ByteBuffer} where the data will be stored.
	 * @param value The {@code long} value to write to the buffer.
	 */
	protected void writeQ(ByteBuffer buf, long value)
	{
		buf.putLong(value);
	}
	
	/**
	 * Writes a {@code String} to the provided {@code ByteBuffer}.<br>
	 * It appends a null character at the end of the text.<br>
	 * If the input is {@code null}, it writes only the null character.
	 * @param buf The buffer where the data will be stored.
	 * @param text The string to write into the buffer.
	 */
	protected void writeS(ByteBuffer buf, String text)
	{
		if (text == null)
		{
			buf.putChar('\000');
		}
		else
		{
			final int len = text.length();
			for (int i = 0; i < len; i++)
			{
				buf.putChar(text.charAt(i));
			}
			
			buf.putChar('\000');
		}
	}
	
	/**
	 * Writes a byte array into the provided buffer.<br>
	 * This method copies all elements from {@code data} into {@code buf}.
	 * @param buf The {@link ByteBuffer} to write to.
	 * @param data The {@code byte[]} containing the information to be written.
	 */
	protected void writeB(ByteBuffer buf, byte[] data)
	{
		buf.put(data);
	}
	
	/**
	 * This method skips a specific number of bytes in the buffer.<br>
	 * It fills the {@code ByteBuffer} with empty data.<br>
	 * Use this when you need to pad the packet or skip over certain fields.
	 * @param buf The {@code ByteBuffer} to modify.
	 * @param bytes The number of bytes to skip.
	 */
	protected void skip(ByteBuffer buf, int bytes)
	{
		buf.put(new byte[bytes]);
	}
}
