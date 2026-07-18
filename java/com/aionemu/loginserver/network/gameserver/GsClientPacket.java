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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.packet.BaseClientPacket;

/**
 * This is the base class for all packets sent from the {@code GameServer} to the Login Server client.<br>
 * It extends {@link BaseClientPacket} and provides a foundation for handling network communication between these components.
 * @author -Nemesiss-
 */
public abstract class GsClientPacket extends BaseClientPacket<GsConnection>
{
	/**
	 * Creates a new instance of a {@link GsClientPacket}.<br>
	 * This is the default constructor for all game server to client packets.<br>
	 * It initializes the packet with a base ID of {@code 0}.
	 */
	public GsClientPacket()
	{
		super(0);
	}
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(GsClientPacket.class);
	
	@Override
	public void run()
	{
		try
		{
			runImpl();
		}
		catch (Throwable e)
		{
			log.warn("error handling gs (" + getConnection().getIP() + ") message " + this, e);
		}
	}
	
	/**
	 * Sends a specific packet to the connected client.<br>
	 * This method uses the {@code GsConnection} to deliver the message.
	 * @param msg The {@code GsServerPacket} object to be sent.
	 */
	protected void sendPacket(GsServerPacket msg)
	{
		getConnection().sendPacket(msg);
	}
}
