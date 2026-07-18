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

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.loginserver.LoginServer;

/**
 * This packet handles requests from the {@code aion} client to perform a fast reconnection.<br>
 * It allows the client to re-establish a connection to the {@link LoginServer}.
 * @author -Nemesiss-
 */
public class CM_RECONNECT_AUTH extends AionClientPacket
{
	/**
	 * Creates a new {@code CM_RECONNECT_AUTH} packet.<br>
	 * This packet is used by the client to request a fast reconnection to the {@link LoginServer}.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional states associated with the reconnection.
	 */
	public CM_RECONNECT_AUTH(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		// empty
	}
	
	@Override
	protected void runImpl()
	{
		final AionConnection client = getConnection();
		
		// TODO! check if may reconnect
		LoginServer.getInstance().requestAuthReconnection(client);
	}
}
