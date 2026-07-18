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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHAT_INIT;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This packet is sent by the client to authenticate the chat system.<br>
 * The client sends this packet only once during the connection process.
 * @author Luno
 */
public class CM_CHAT_AUTH extends AionClientPacket
{
	/**
	 * This constructor initializes a new {@code CM_CHAT_AUTH} packet.<br>
	 * It sets the required network states for the chat authentication process.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} of the connection.
	 * @param restStates A variable number of additional {@link State} values.
	 */
	public CM_CHAT_AUTH(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		@SuppressWarnings("unused")
		final int objectId = readD(); // lol NC
		@SuppressWarnings("unused")
		final byte[] macAddress = readB(6);
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		// Chat server removed - reply with empty token so the client stops re-asking.
		PacketSendUtility.sendPacket(player, new SM_CHAT_INIT(new byte[0]));
	}
}
