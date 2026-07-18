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
import com.aionemu.gameserver.network.aion.serverpackets.SM_GF_WEBSHOP_TOKEN;

/**
 * This packet handles the request from the client to obtain a webshop token.<br>
 * It is used by the game client to authenticate with the external webshop service.
 * @author Falke_34
 */
public class CM_GF_WEBSHOP_TOKEN extends AionClientPacket
{
	/**
	 * This constructor initializes a new {@code CM_GF_WEBSHOP_TOKEN} packet.<br>
	 * It sets the required network properties for the client request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state associated with the packet.
	 * @param restStates A variable number of additional states for the packet.
	 */
	public CM_GF_WEBSHOP_TOKEN(int opcode, State state, State... restStates)
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
		sendPacket(new SM_GF_WEBSHOP_TOKEN());
	}
}
