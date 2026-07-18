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

/**
 * This class serves as the base superclass for all network packets.<br>
 * It provides common functionality shared by all packet types in the system.
 * @author Aquanox
 */
public abstract class BasePacket
{
	/**
	 * Default packet string representation pattern.
	 * @see java.util.Formatter
	 * @see String#format(String, Object[])
	 */
	public static final String TYPE_PATTERN = "[%s] 0x%02X %s";
	
	/**
	 * Packet type field.
	 */
	private final PacketType packetType;
	
	/**
	 * Packet opcode field
	 */
	private int opcode;
	
	/**
	 * Creates a new instance of a {@link BasePacket}.<br>
	 * This constructor initializes the packet with a specific type and ID.
	 * @param packetType The {@code PacketType} of the packet.
	 * @param opcode The unique integer identifier for the packet.
	 */
	protected BasePacket(PacketType packetType, int opcode)
	{
		this.packetType = packetType;
		this.opcode = opcode;
	}
	
	/**
	 * Initializes a new instance of a {@link BasePacket}.<br>
	 * This constructor sets the internal {@code packetType} field.
	 * @param packetType The type of the packet to be created.
	 */
	protected BasePacket(PacketType packetType)
	{
		this.packetType = packetType;
	}
	
	/**
	 * Sets the unique identifier for this packet.<br>
	 * This updates the {@code opcode} field of the current instance.
	 * @param opcode The new integer ID to assign to the packet.
	 */
	protected void setOpcode(int opcode)
	{
		this.opcode = opcode;
	}
	
	/**
	 * Retrieves the unique identifier for this packet.<br>
	 * This value is stored in the {@code opcode} field.
	 * @return The integer value of the packet's opcode.
	 */
	public int getOpcode()
	{
		return opcode;
	}
	
	/**
	 * Retrieves the type of this packet.<br>
	 * This method returns the {@code PacketType} associated with the current instance.
	 * @return The {@code PacketType} of the packet.
	 */
	public PacketType getPacketType()
	{
		return packetType;
	}
	
	/**
	 * Returns the simple name of the packet class.<br>
	 * This is useful for logging and debugging purposes.
	 * @return The {@code String} name of the current class.
	 */
	public String getPacketName()
	{
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Enumeration of packet types.
	 */
	public static enum PacketType
	{
		/** Server packet */
		SERVER("S"),
		
		/** Client packet */
		CLIENT("C");
		
		/**
		 * String representing packet type.
		 */
		private final String name;
		
		/**
		 * Constructor.
		 * @param name
		 */
		private PacketType(String name)
		{
			this.name = name;
		}
		
		/**
		 * Returns packet type name.
		 * @return packet type name.
		 */
		public String getName()
		{
			return name;
		}
	}
	
	/**
	 * Returns a string representation of the packet.<br>
	 * This method uses {@code TYPE_PATTERN} to format the output.<br>
	 * It includes the packet type, opcode, and name.
	 * @return A formatted string representing this packet.
	 */
	@Override
	public String toString()
	{
		return String.format(TYPE_PATTERN, getPacketType().getName(), getOpcode(), getPacketName());
	}
}
