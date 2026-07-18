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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.AConnection;

/**
 * This is the base class for all client packets in the network system.<br>
 * It provides common functionality shared by every packet sent from the client to the server.
 * @author -Nemesiss-
 * @param <T> AConnection - owner of this client packet.
 */
public abstract class BaseClientPacket<T extends AConnection>extends BasePacket implements Runnable
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(BaseClientPacket.class);
	/**
	 * Owner of this packet.
	 */
	private T client;
	/**
	 * ByteBuffer that contains this packet data
	 */
	private ByteBuffer buf;
	
	/**
	 * Creates a new instance of a client packet.<br>
	 * This constructor initializes the packet with a specific {@code opcode}.<br>
	 * It also assigns the provided data buffer to this packet.
	 * @param buf The {@code ByteBuffer} containing the packet data.
	 * @param opcode The unique identifier for this type of packet.
	 */
	public BaseClientPacket(ByteBuffer buf, int opcode)
	{
		this(opcode);
		this.buf = buf;
	}
	
	/**
	 * Creates a new instance of a client packet.<br>
	 * This constructor initializes the packet with a specific {@code opcode}.<br>
	 * It uses the {@code CLIENT} type for identification.
	 * @param opcode The unique identifier for this packet type.
	 */
	public BaseClientPacket(int opcode)
	{
		super(PacketType.CLIENT, opcode);
	}
	
	/**
	 * Sets the data buffer for this packet.<br>
	 * This method updates the internal {@code ByteBuffer} used to read or write packet data.
	 * @param buf The {@code ByteBuffer} to use as the packet's data container.
	 */
	public void setBuffer(ByteBuffer buf)
	{
		this.buf = buf;
	}
	
	/**
	 * Sets the connection owner for this packet.<br>
	 * This method links the packet to a specific {@link AConnection}.
	 * @param client The {@code T} type connection object to assign.
	 */
	public void setConnection(T client)
	{
		this.client = client;
	}
	
	/**
	 * Reads the data from the internal buffer.<br>
	 * This method calls {@code readImpl} to process the packet content.<br>
	 * It logs a warning if there are leftover bytes after reading.
	 * @return {@code true} if the packet was read successfully, or {@code false} if an error occurred.
	 */
	public boolean read()
	{
		try
		{
			readImpl();
			
			if (getRemainingBytes() > 0)
			{
				log.debug("Packet " + this + " not fully readed!");
			}
			
			return true;
		}
		catch (Exception re)
		{
			log.error("Reading failed for packet " + this, re);
			return false;
		}
	}
	
	/**
	 * Data reading implementation
	 */
	protected abstract void readImpl();
	
	/**
	 * Returns the number of bytes left in the buffer.<br>
	 * This method checks the {@code ByteBuffer} for remaining data.
	 * @return The count of remaining bytes as an {@code int}.
	 */
	public int getRemainingBytes()
	{
		return buf.remaining();
	}
	
	/**
	 * Reads a {@code int} value from the current buffer.<br>
	 * This method is used to extract data of type {@code D}.<br>
	 * It returns {@code 0} if an error occurs during reading.
	 * @return The integer value read from the buffer.
	 */
	protected int readD()
	{
		try
		{
			return buf.getInt();
		}
		catch (Exception e)
		{
			log.error("Missing D for: " + this);
		}
		
		return 0;
	}
	
	/**
	 * Reads a single unsigned byte from the current buffer.<br>
	 * This method is used to retrieve a {@code C} type value.<br>
	 * It returns {@code 0} if an error occurs during reading.
	 * @return The integer value of the read byte.
	 */
	protected int readC()
	{
		try
		{
			return buf.get() & 0xFF;
		}
		catch (Exception e)
		{
			log.error("Missing C for: " + this);
		}
		
		return 0;
	}
	
	/**
	 * Reads a single {@code byte} from the current buffer.<br>
	 * This method is used to extract data during packet parsing.<br>
	 * It logs an error if the buffer is empty or missing data.
	 * @return The {@code byte} read from the buffer, or {@code 0} if an error occurs.
	 */
	protected byte readSC()
	{
		try
		{
			return buf.get();
		}
		catch (Exception e)
		{
			log.error("Missing C for: " + this);
		}
		
		return 0;
	}
	
	/**
	 * Reads a {@code short} value from the current buffer.<br>
	 * This method is used to parse packet data.<br>
	 * It returns {@code 0} if an error occurs during reading.
	 * @return The {@code short} value read from the buffer.
	 */
	protected short readSH()
	{
		try
		{
			return buf.getShort();
		}
		catch (Exception e)
		{
			log.error("Missing H for: " + this);
		}
		
		return 0;
	}
	
	/**
	 * Reads a short value from the buffer as an unsigned integer.<br>
	 * This method handles the {@code H} packet type.<br>
	 * It returns {@code 0} if an error occurs during reading.
	 * @return The unsigned short value read from the buffer.
	 */
	protected int readH()
	{
		try
		{
			return buf.getShort() & 0xFFFF;
		}
		catch (Exception e)
		{
			log.error("Missing H for: " + this);
		}
		
		return 0;
	}
	
	/**
	 * Reads a {@code double} value from the current buffer.<br>
	 * This method is used to extract data during packet parsing.<br>
	 * It returns {@code 0} if an error occurs while reading.
	 * @return The {@code double} value read from the buffer.
	 */
	protected double readDF()
	{
		try
		{
			return buf.getDouble();
		}
		catch (Exception e)
		{
			log.error("Missing DF for: " + this);
		}
		
		return 0;
	}
	
	/**
	 * Reads a {@code float} value from the current buffer.<br>
	 * This method is used to extract floating-point data during packet parsing.<br>
	 * If an error occurs, it logs the issue and returns {@code 0}.
	 * @return The {@code float} value read from the buffer.
	 */
	protected float readF()
	{
		try
		{
			return buf.getFloat();
		}
		catch (Exception e)
		{
			log.error("Missing F for: " + this);
		}
		
		return 0;
	}
	
	/**
	 * Reads a {@code long} value from the current buffer.<br>
	 * This method is used to extract 8-byte data types.<br>
	 * It returns {@code 0} if an error occurs during reading.
	 * @return The {@code long} value read from the buffer.
	 */
	protected long readQ()
	{
		try
		{
			return buf.getLong();
		}
		catch (Exception e)
		{
			log.error("Missing Q for: " + this);
		}
		
		return 0;
	}
	
	/**
	 * Reads a string from the current buffer.<br>
	 * It reads characters until it encounters a null character.<br>
	 * If an error occurs, it logs the issue for this packet.
	 * @return The string read from the buffer as a {@code String}.
	 */
	protected String readS()
	{
		final StringBuffer sb = new StringBuffer();
		char ch;
		try
		{
			while ((ch = buf.getChar()) != 0)
			{
				sb.append(ch);
			}
		}
		catch (Exception e)
		{
			log.error("Missing S for: " + this);
		}
		
		return sb.toString();
	}
	
	/**
	 * Reads a sequence of bytes from the internal buffer.<br>
	 * The method extracts data into a new {@code byte[]} array.<br>
	 * It logs an error if there are not enough bytes to read.
	 * @param length The number of bytes to read from the buffer.
	 * @return A {@code byte[]} containing the requested data.
	 */
	protected byte[] readB(int length)
	{
		final byte[] result = new byte[length];
		try
		{
			buf.get(result);
		}
		catch (Exception e)
		{
			log.error("Missing byte[] for: " + this);
		}
		
		return result;
	}
	
	/**
	 * Reads a sequence of bytes from the buffer based on a hex string.<br>
	 * This method removes all whitespace from the input {@code string}.<br>
	 * It converts each pair of hex characters into a single byte.
	 * @param string The hex string representing the bytes to read.
	 * @return A {@code byte[]} containing the parsed data.
	 */
	protected byte[] readB(String string)
	{
		final String finalString = string.replaceAll("\\s+", "");
		final byte[] bytes = new byte[finalString.length() / 2];
		for (int i = 0; i < bytes.length; ++i)
		{
			bytes[i] = (byte) Integer.parseInt(finalString.substring(2 * i, (2 * i) + 2), 16);
		}
		
		try
		{
			buf.get(bytes);
		}
		catch (Exception e)
		{
			BaseClientPacket.log.error("Missing byte[] for: " + this);
		}
		
		return bytes;
	}
	
	/**
	 * Execute this packet action.
	 */
	protected abstract void runImpl();
	
	/**
	 * Retrieves the connection associated with this packet.<br>
	 * This method returns the owner of the current {@code BaseClientPacket}.
	 * @return The {@code AConnection} object linked to this packet.
	 */
	public T getConnection()
	{
		return client;
	}
}
