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
package com.aionemu.loginserver.network.aion;

import java.nio.ByteBuffer;

import com.aionemu.commons.network.packet.BaseServerPacket;

/**
 * This class serves as the base class for all packets sent from the Login Server to the Aion Server.<br>
 * It extends {@link BaseServerPacket} to provide common functionality for network communication.
 * @author -Nemesiss-
 */
public abstract class AionServerPacket extends BaseServerPacket
{
	/**
	 * Creates a new instance of an {@link AionServerPacket}.<br>
	 * This constructor initializes the packet with a specific opcode.
	 * @param opcode The unique identifier for this packet type.
	 */
	protected AionServerPacket(int opcode)
	{
		super(opcode);
	}
	
	/**
	 * This method writes the packet data to the network connection.<br>
	 * It handles the necessary byte buffer operations and encryption.
	 * @param con The {@link LoginConnection} used to send the data.
	 */
	public void write(LoginConnection con)
	{
		buf.putShort((short) 0);
		buf.put((byte) getOpcode());
		writeImpl(con);
		buf.flip();
		buf.putShort((short) 0);
		final ByteBuffer b = buf.slice();
		
		final short size = (short) (con.encrypt(b) + 2);
		buf.putShort(0, size);
		buf.position(0).limit(size);
	}
	
	/**
	 * Write data that this packet represents to given byte buffer.
	 * @param con
	 */
	protected abstract void writeImpl(LoginConnection con);
}
