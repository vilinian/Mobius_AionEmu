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
package com.aionemu.loginserver.network.factories;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.loginserver.network.aion.AionClientPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;
import com.aionemu.loginserver.network.aion.LoginConnection.State;
import com.aionemu.loginserver.network.aion.clientpackets.CM_AUTH_GG;
import com.aionemu.loginserver.network.aion.clientpackets.CM_LOGIN;
import com.aionemu.loginserver.network.aion.clientpackets.CM_PLAY;
import com.aionemu.loginserver.network.aion.clientpackets.CM_SERVER_LIST;
import com.aionemu.loginserver.network.aion.clientpackets.CM_UPDATE_SESSION;

/**
 * This factory class is responsible for creating the appropriate {@link AionClientPacket} handlers.<br>
 * It maps specific packet types to their corresponding logic during network communication.<br>
 * Use this class to initialize and manage how the server processes incoming client requests.
 * @author -Nemesiss-
 */
public class AionPacketHandlerFactory
{
	/**
	 * logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(AionPacketHandlerFactory.class);
	
	/**
	 * Processes incoming raw data to create the correct packet object.<br>
	 * This method identifies the packet based on the current {@code State}.<br>
	 * It returns a new instance of an {@link AionClientPacket} or {@code null} if unknown.
	 * @param data The {@code ByteBuffer} containing the raw network data.
	 * @param client The {@link LoginConnection} representing the current user session.
	 * @return The parsed {@link AionClientPacket} object, or {@code null} if no match is found.
	 */
	public static AionClientPacket handle(ByteBuffer data, LoginConnection client)
	{
		AionClientPacket msg = null;
		final State state = client.getState();
		final int id = data.get() & 0xff;
		
		switch (state)
		{
			case CONNECTED:
			{
				switch (id)
				{
					case 0x07:
						msg = new CM_AUTH_GG(data, client);
						break;
					case 0x08:
						msg = new CM_UPDATE_SESSION(data, client);
						break;
					default:
						unknownPacket(state, id);
				}
				break;
			}
			case AUTHED_GG:
			{
				switch (id)
				{
					case 0x0B:
						msg = new CM_LOGIN(data, client);
						break;
					default:
						unknownPacket(state, id);
				}
				break;
			}
			case AUTHED_LOGIN:
			{
				switch (id)
				{
					case 0x05:
						msg = new CM_SERVER_LIST(data, client);
						break;
					case 0x02:
						msg = new CM_PLAY(data, client);
						break;
					default:
						unknownPacket(state, id);
				}
				break;
			}
		}
		
		return msg;
	}
	
	/**
	 * Handles packets that the system does not recognize.<br>
	 * It logs a warning message with the packet {@code id} and current {@link State}.
	 * @param state The current connection state of the client.
	 * @param id The unique identifier of the unknown packet.
	 */
	private static void unknownPacket(State state, int id)
	{
		log.warn(String.format("Unknown packet recived from Aion client: 0x%02X state=%s", id, state.toString()));
	}
}
