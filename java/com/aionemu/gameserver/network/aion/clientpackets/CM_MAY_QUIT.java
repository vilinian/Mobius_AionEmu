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

/**
 * This packet is sent by the client to notify the server that the player may quit the game.<br>
 * It typically indicates a pending disconnection state within 10 seconds.
 * @author xavier Packet sent by client when player may quit game in 10 seconds
 */
public class CM_MAY_QUIT extends AionClientPacket
{
	/**
	 * This method creates a new {@link CM_MAY_QUIT} packet.<br>
	 * It is used when the client notifies the server that it may quit in 10 seconds.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@code State} associated with the action.
	 * @param restStates A variable number of additional {@code State} objects.
	 */
	public CM_MAY_QUIT(int opcode, State state, State... restStates)
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
		// Nothing to do
	}
}
