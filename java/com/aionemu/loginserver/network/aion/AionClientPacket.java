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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.packet.BaseClientPacket;
import com.aionemu.loginserver.model.Account;

/**
 * This class serves as the base class for all packets sent from the {@code Aion} client to the login server.<br>
 * It extends {@link BaseClientPacket} to provide a common structure for network communication.
 * @author -Nemesiss-
 */
public abstract class AionClientPacket extends BaseClientPacket<LoginConnection>
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(AionClientPacket.class);
	
	/**
	 * Creates a new instance of an {@link AionClientPacket}.<br>
	 * This constructor initializes the packet with data from a {@code ByteBuffer}.<br>
	 * It also links the packet to a specific {@link LoginConnection}.
	 * @param buf The buffer containing the raw packet data.
	 * @param client The connection associated with this packet.
	 * @param opcode The unique identifier for the packet type.
	 */
	protected AionClientPacket(ByteBuffer buf, LoginConnection client, int opcode)
	{
		super(buf, opcode);
		setConnection(client);
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
			String name;
			final Account account = getConnection().getAccount();
			if (account != null)
			{
				name = account.getName();
			}
			else
			{
				name = getConnection().getIP();
			}
			
			log.error("error handling client (" + name + ") message " + this, e);
		}
	}
	
	/**
	 * Sends a packet to the connected client.<br>
	 * This method uses {@code getConnection} to transmit the data.
	 * @param msg The {@code AionServerPacket} to be sent.
	 */
	protected void sendPacket(AionServerPacket msg)
	{
		getConnection().sendPacket(msg);
	}
}
