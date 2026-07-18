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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.packet.BaseClientPacket;

/**
 * This class serves as the base for all packets sent from the client to the login server.<br>
 * It extends {@link BaseClientPacket} and handles communication specific to the {@code LoginServerConnection}.
 * @author -Nemesiss-
 */
public abstract class LsClientPacket extends BaseClientPacket<LoginServerConnection> implements Cloneable
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(LsClientPacket.class);
	
	/**
	 * Creates a new instance of a login server client packet.<br>
	 * This constructor initializes the packet with a specific {@code opcode}.<br>
	 * You must manually set the buffer and connection after calling this.
	 * @param opcode The unique identifier for the packet type.
	 */
	protected LsClientPacket(int opcode)
	{
		super(opcode);
	}
	
	@Override
	public void run()
	{
		try
		{
			runImpl();
		}
		catch (Throwable e)
		{
			log.warn("error handling ls (" + getConnection().getIP() + ") message " + this, e);
		}
	}
	
	/**
	 * Sends a packet to the connected server.<br>
	 * This method uses the {@link LoginServerConnection} to transmit data.
	 * @param msg The {@code LsServerPacket} object to be sent.
	 */
	public void sendPacket(LsServerPacket msg)
	{
		getConnection().sendPacket(msg);
	}
	
	/**
	 * Creates a copy of the current {@code LsClientPacket} object.<br>
	 * This method uses the standard {@code clone()} mechanism.<br>
	 * It returns a new instance with the same data.
	 * @return A new {@code LsClientPacket} instance or {@code null} if cloning fails.
	 */
	public LsClientPacket clonePacket()
	{
		try
		{
			return (LsClientPacket) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
}
