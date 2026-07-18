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
package com.aionemu.gameserver.network.factories;

import com.aionemu.gameserver.network.loginserver.LoginServerConnection.State;
import com.aionemu.gameserver.network.loginserver.LsClientPacket;
import com.aionemu.gameserver.network.loginserver.LsPacketHandler;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_ACCOUNT_RECONNECT_KEY;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_ACOUNT_AUTH_RESPONSE;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_BAN_RESPONSE;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_GS_AUTH_RESPONSE;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_GS_CHARACTER_RESPONSE;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_LS_CONTROL_RESPONSE;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_LS_PING;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_MACBAN_LIST;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_PTRANSFER_RESPONSE;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_REQUEST_KICK_ACCOUNT;

/**
 * This factory class is responsible for creating instances of {@link LsPacketHandler}.<br>
 * It maps specific {@link LsClientPacket} types to their corresponding handlers.<br>
 * Use this class to retrieve the correct logic for processing login server network packets.
 * @author Luno
 */
public class LsPacketHandlerFactory
{
	private final LsPacketHandler handler = new LsPacketHandler();
	
	/**
	 * Gets the singleton instance of this factory.<br>
	 * Use this method to access the global {@link LsPacketHandlerFactory}.
	 * @return The single instance of {@code LsPacketHandlerFactory}.
	 */
	public static LsPacketHandlerFactory getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link LsPacketHandlerFactory} class.<br>
	 * This prevents other classes from creating new instances of this factory.<br>
	 * It is used to initialize the packet handlers during object creation.
	 */
	private LsPacketHandlerFactory()
	{
		addPacket(new CM_ACCOUNT_RECONNECT_KEY(0x03), State.AUTHED);
		addPacket(new CM_ACOUNT_AUTH_RESPONSE(0x01), State.AUTHED);
		addPacket(new CM_GS_AUTH_RESPONSE(0x00), State.CONNECTED);
		addPacket(new CM_REQUEST_KICK_ACCOUNT(0x02), State.AUTHED);
		addPacket(new CM_LS_CONTROL_RESPONSE(0x04), State.AUTHED);
		addPacket(new CM_BAN_RESPONSE(0x05), State.AUTHED);
		addPacket(new CM_GS_CHARACTER_RESPONSE(0x08), State.AUTHED);
		addPacket(new CM_MACBAN_LIST(9), State.AUTHED);
		addPacket(new CM_LS_PING(11), State.AUTHED);
		addPacket(new CM_PTRANSFER_RESPONSE(12), State.AUTHED);
	}
	
	/**
	 * Registers a new packet type with the {@link LsPacketHandler}.<br>
	 * This method maps a {@code LsClientPacket} prototype to specific {@code State} values.<br>
	 * It helps define which packets are valid for different connection states.
	 * @param prototype The base packet class to register.
	 * @param states The list of allowed states for this packet.
	 */
	private void addPacket(LsClientPacket prototype, State... states)
	{
		handler.addPacketPrototype(prototype, states);
	}
	
	/**
	 * Retrieves the main packet handler instance.<br>
	 * This object manages all incoming login server packets.
	 * @return The {@link LsPacketHandler} used by the factory.
	 */
	public LsPacketHandler getPacketHandler()
	{
		return handler;
	}
	
	private static class SingletonHolder
	{
		protected static final LsPacketHandlerFactory instance = new LsPacketHandlerFactory();
	}
}
