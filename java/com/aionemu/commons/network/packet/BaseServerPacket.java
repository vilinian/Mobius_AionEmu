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
package com.aionemu.commons.network.packet;

import java.nio.ByteBuffer;

import com.aionemu.commons.utils.PrintUtils;

/**
 * This is the base class for all server packets.<br>
 * It provides common functionality shared by every packet in the {@code packet} package.
 * @author -Nemesiss-
 */
public abstract class BaseServerPacket extends BasePacket
{
	/**
	 * ByteBuffer that contains this packet data
	 */
	public ByteBuffer buf;
	
	/**
	 * Initializes a new instance of a server packet.<br>
	 * This constructor sets the unique {@code opcode} for the packet.<br>
	 * It calls the superclass constructor to register the packet type as {@code SERVER}.
	 * @param opcode The unique identifier for this specific packet type.
	 */
	protected BaseServerPacket(int opcode)
	{
		super(PacketType.SERVER, opcode);
	}
	
	/**
	 * Initializes a new instance of this packet.<br>
	 * This constructor sets the packet type to {@code SERVER}.<br>
	 * It is used for internal initialization by subclasses.
	 */
	protected BaseServerPacket()
	{
		super(PacketType.SERVER);
	}
	
	/**
	 * Sets the internal buffer for this packet.<br>
	 * This method updates the {@code buf} field with the provided data.
	 * @param buf The {@code ByteBuffer} to use for packet data.
	 */
	public void setBuf(ByteBuffer buf)
	{
		this.buf = buf;
	}
	
	/**
	 * Writes an {@code int} value into the packet buffer.<br>
	 * This method updates the internal {@code buf} field.
	 * @param value The integer to write.
	 */
	public void writeD(int value)
	{
		buf.putInt(value);
	}
	
	/**
	 * Writes a short integer to the packet buffer.<br>
	 * This method converts the {@code int} value into a {@code short}.<br>
	 * It updates the internal {@link ByteBuffer} used by this packet.
	 * @param value The integer value to be written as a short.
	 */
	public void writeH(int value)
	{
		buf.putShort((short) value);
	}
	
	/**
	 * Writes a single byte to the packet buffer.<br>
	 * This method converts the {@code int} value into a {@code byte}.<br>
	 * It updates the internal {@code buf} field.
	 * @param value The integer value to be written as a byte.
	 */
	public void writeC(int value)
	{
		buf.put((byte) value);
	}
	
	/**
	 * Writes a {@code double} value into the packet buffer.<br>
	 * This method updates the internal {@code buf} field.
	 * @param value The {@code double} value to write.
	 */
	public void writeDF(double value)
	{
		buf.putDouble(value);
	}
	
	/**
	 * Writes a {@code float} value into the packet buffer.<br>
	 * This method updates the internal {@code buf} field.
	 * @param value The {@code float} value to write.
	 */
	public void writeF(float value)
	{
		buf.putFloat(value);
	}
	
	/**
	 * Writes a {@code long} value into the packet buffer.<br>
	 * This method uses {@code writeD} logic for 64-bit integers.
	 * @param value The {@code long} value to write.
	 */
	public void writeQ(long value)
	{
		buf.putLong(value);
	}
	
	/**
	 * Writes a {@code String} to the packet buffer.<br>
	 * It appends a null character at the end of the text.<br>
	 * If the input is {@code null}, it writes only the null character.
	 * @param text The string to write to the buffer.
	 */
	public void writeS(String text)
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
	 * Writes a byte array into the packet buffer.<br>
	 * This method adds the contents of {@code data} to the current {@code buf}.
	 * @param data The byte array to be written.
	 */
	public void writeB(byte[] data)
	{
		buf.put(data);
	}
	
	/**
	 * Writes a string as raw bytes to the packet buffer.<br>
	 * This method converts the {@code String} into a byte array using hex format.<br>
	 * It then calls the internal {@code writeB} method.
	 * @param bytes The hex string representing the bytes to write.
	 */
	public void writeB(String bytes)
	{
		this.writeB(PrintUtils.hex2bytes(bytes));
	}
}
