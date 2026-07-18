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
package com.aionemu.loginserver.network.gameserver;

import java.nio.ByteBuffer;

import com.aionemu.commons.network.packet.BaseServerPacket;

/**
 * This is the base class for all server packets sent from the Login Server to the Game Server.<br>
 * It serves as the foundation for handling network communication between these two components.
 * @author -Nemesiss-
 */
public abstract class GsServerPacket extends BaseServerPacket
{
	/**
	 * Creates a new instance of a {@link GsServerPacket}.<br>
	 * This constructor initializes the packet with an ID of {@code 0}.<br>
	 * It is intended for internal use by subclasses.
	 */
	protected GsServerPacket()
	{
		super(0);
	}
	
	/**
	 * Writes the packet data to the provided {@link GsConnection}.<br>
	 * This method prepares the {@code buffer} and calls the internal write logic.
	 * @param con The active connection to send the data through.
	 * @param buffer The {@code ByteBuffer} containing the packet information.
	 */
	public void write(GsConnection con, ByteBuffer buffer)
	{
		setBuf(buffer);
		buf.putShort((short) 0);
		writeImpl(con);
		buf.flip();
		buf.putShort((short) buf.limit());
		buf.position(0);
	}
	
	/**
	 * Write data that this packet represents to given byte buffer.
	 * @param con
	 */
	protected abstract void writeImpl(GsConnection con);
}
