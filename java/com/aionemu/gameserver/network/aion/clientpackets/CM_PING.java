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

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PONG;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Handles the {@code CM_PING} packet received from the client.<br>
 * It is used to check the connection status between the client and the server.<br>
 * The server responds to this request by sending an {@link SM_PONG} packet.
 * @author -Nemesiss- modified by Undertrey
 */
public class CM_PING extends AionClientPacket
{
	/**
	 * Creates a new instance of the {@code CM_PING} packet.<br>
	 * This packet is used to handle ping requests from the client.<br>
	 * It initializes the packet with the required states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} associated with the request.
	 * @param restStates A variable number of additional {@link State} objects.
	 */
	public CM_PING(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		readH(); // unk
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final long lastMS = getConnection().getLastPingTimeMS();
		
		if ((lastMS > 0) && (player != null))
		{
			final long pingInterval = System.currentTimeMillis() - lastMS;
			
			// PingInterval should be 3min (180000ms)
			if ((pingInterval < (SecurityConfig.PING_INTERVAL * 1000)) && SecurityConfig.SECURITY_ENABLE) // client timer cheat
			{
				AuditLogger.info(player, "Possible client timer cheat kicking player: " + pingInterval + ", ip=" + getConnection().getIP());
				PacketSendUtility.sendMessage(player, "You have been triggered Speed Hack detection so you're disconnected.");
				getConnection().closeNow();
			}
		}
		
		getConnection().setLastPingTimeMS(System.currentTimeMillis());
		sendPacket(new SM_PONG());
	}
}
