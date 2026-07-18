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
import com.aionemu.gameserver.services.BrokerService;

/**
 * This packet is sent to the client when a player successfully registers with the {@link BrokerService}.<br>
 * It informs the game client that the broker registration process has been completed.
 * @author kosyak
 */
public class CM_BROKER_REGISTERED extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int npcId;
	
	/**
	 * This method handles the registration of a broker.<br>
	 * It initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} associated with the request.
	 * @param restStates A variable number of additional {@link State} objects.
	 */
	public CM_BROKER_REGISTERED(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		npcId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		BrokerService.getInstance().showRegisteredItems(player);
	}
}
