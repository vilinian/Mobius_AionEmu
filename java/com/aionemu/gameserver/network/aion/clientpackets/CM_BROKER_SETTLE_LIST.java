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
 * This packet handles the settlement of a broker's list.<br>
 * It is used to process and finalize transactions within the {@link BrokerService}.
 * @author kosyachok
 */
public class CM_BROKER_SETTLE_LIST extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int npcId;
	
	/**
	 * This method initializes a new {@code CM_BROKER_SETTLE_LIST} packet.<br>
	 * It sets the required network states for the packet.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates A variable number of additional states for the connection.
	 */
	public CM_BROKER_SETTLE_LIST(int opcode, State state, State... restStates)
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
		
		BrokerService.getInstance().showSettledItems(player);
	}
}
