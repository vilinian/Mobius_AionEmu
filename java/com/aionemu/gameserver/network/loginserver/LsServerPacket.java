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
package com.aionemu.gameserver.network.loginserver;

import java.nio.ByteBuffer;

import com.aionemu.commons.network.packet.BaseServerPacket;

/**
 * This class serves as the base class for all packets sent from the {@code GameServer} to the {@code LsServer}.<br>
 * It provides common functionality for handling login server communication.
 * @author -Nemesiss-
 */
public abstract class LsServerPacket extends BaseServerPacket
{
	/**
	 * Creates a new instance of a packet for the login server.<br>
	 * It initializes the packet with a specific identification number.
	 * @param opcode The unique ID used to identify this type of packet.
	 */
	protected LsServerPacket(int opcode)
	{
		super(opcode);
	}
	
	/**
	 * This method writes the packet data to a {@link LoginServerConnection}.<br>
	 * It prepares the {@code ByteBuffer} with the correct header and opcode.<br>
	 * It then calls the internal {@code writeImpl} method to finish the process.
	 * @param con The connection used to send the packet.
	 * @param buffer The buffer where the data will be written.
	 */
	public void write(LoginServerConnection con, ByteBuffer buffer)
	{
		setBuf(buffer);
		buf.putShort((short) 0);
		buf.put((byte) getOpcode());
		writeImpl(con);
		buf.flip();
		buf.putShort((short) buf.limit());
		buf.position(0);
	}
	
	/**
	 * Write data that this packet represents to given byte buffer.
	 * @param con
	 */
	protected abstract void writeImpl(LoginServerConnection con);
}
