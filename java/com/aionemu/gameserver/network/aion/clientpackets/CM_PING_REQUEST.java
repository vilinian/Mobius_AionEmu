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
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PING_RESPONSE;

/**
 * Represents a request sent by the client to the server. <br>
 * This packet is triggered when a player types the {@code /ping} command in the game chat.
 * @author dragoon112
 */
public class CM_PING_REQUEST extends AionClientPacket
{
	/**
	 * Creates a new instance of the {@link CM_PING_REQUEST} packet.<br>
	 * This packet is used to handle ping requests from the client.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional states associated with the request.
	 */
	public CM_PING_REQUEST(int opcode, State state, State... restStates)
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
		sendPacket(new SM_PING_RESPONSE());
	}
}
