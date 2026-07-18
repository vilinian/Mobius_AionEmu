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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.network.loginserver.LoginServerConnection.State;

/**
 * This class handles the processing of incoming network packets for the login server.<br>
 * It routes data to the appropriate logic based on the packet type received from a {@link LoginServerConnection}.
 * @author -Nemesiss-
 * @author Luno
 */
public class LsPacketHandler
{
	/**
	 * logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(LsPacketHandler.class);
	private static Map<State, Map<Integer, LsClientPacket>> packetPrototypes = new HashMap<>();
	
	/**
	 * Processes an incoming network packet from a client.<br>
	 * It identifies the packet type based on the first byte of {@code data}.<br>
	 * The method then retrieves the correct {@link LsClientPacket} for the current state.
	 * @param data The raw bytes received from the network.
	 * @param client The connection object representing the current client.
	 * @return The parsed {@code LsClientPacket} object.
	 */
	public LsClientPacket handle(ByteBuffer data, LoginServerConnection client)
	{
		final State state = client.getState();
		final int id = data.get() & 0xff;
		
		return getPacket(state, id, data, client);
	}
	
	/**
	 * Registers a new {@link LsClientPacket} prototype for specific server states.<br>
	 * This method maps the packet's opcode to its definition across multiple {@code State} types.
	 * @param packetPrototype The {@code LsClientPacket} object to register.
	 * @param states A variable number of {@code State} values where this packet is valid.
	 */
	public void addPacketPrototype(LsClientPacket packetPrototype, State... states)
	{
		for (State state : states)
		{
			Map<Integer, LsClientPacket> pm = packetPrototypes.get(state);
			if (pm == null)
			{
				pm = new HashMap<>();
				packetPrototypes.put(state, pm);
			}
			
			pm.put(packetPrototype.getOpcode(), packetPrototype);
		}
	}
	
	/**
	 * Retrieves and initializes a packet based on the current state and ID.<br>
	 * It clones a prototype from the {@code packetPrototypes} map.<br>
	 * If no prototype exists, it calls {@code int)}.
	 * @param state The current connection {@link State}.
	 * @param id The unique identifier for the packet.
	 * @param buf The {@code ByteBuffer} containing raw data.
	 * @param con The active {@link LoginServerConnection}.
	 * @return A new {@code LsClientPacket} instance or {@code null} if not found.
	 */
	private LsClientPacket getPacket(State state, int id, ByteBuffer buf, LoginServerConnection con)
	{
		LsClientPacket prototype = null;
		
		final Map<Integer, LsClientPacket> pm = packetPrototypes.get(state);
		if (pm != null)
		{
			prototype = pm.get(id);
		}
		
		if (prototype == null)
		{
			unknownPacket(state, id);
			return null;
		}
		
		final LsClientPacket res = prototype.clonePacket();
		res.setBuffer(buf);
		res.setConnection(con);
		
		return res;
	}
	
	/**
	 * Logs a warning when an unrecognized packet is received.<br>
	 * This method handles cases where the {@code id} does not match any known prototype.<br>
	 * It records the packet {@code id} and the current {@link State}.
	 * @param state The current connection state of the client.
	 * @param id The unique identifier of the received packet.
	 */
	private void unknownPacket(State state, int id)
	{
		log.warn(String.format("Unknown packet recived from Login Server: 0x%02X state=%s", id, state.toString()));
	}
}
