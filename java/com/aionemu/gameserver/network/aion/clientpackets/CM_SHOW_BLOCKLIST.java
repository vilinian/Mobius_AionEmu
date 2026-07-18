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
import com.aionemu.gameserver.network.aion.serverpackets.SM_BLOCK_LIST;

/**
 * This packet is sent to the server when a client requests to view the blocklist.<br>
 * It triggers the server to respond with the relevant {@link SM_BLOCK_LIST} data.
 * @author Ben
 */
public class CM_SHOW_BLOCKLIST extends AionClientPacket
{
	/**
	 * This method creates a new {@code CM_SHOW_BLOCKLIST} packet.<br>
	 * It initializes the packet with the required network states.<br>
	 * Use this to handle requests for viewing the blocklist.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if needed.
	 */
	public CM_SHOW_BLOCKLIST(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		sendPacket(new SM_BLOCK_LIST());
	}
}
