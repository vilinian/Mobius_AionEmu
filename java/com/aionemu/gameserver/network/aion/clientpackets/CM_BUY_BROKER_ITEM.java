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
 * Handles the client request to purchase an item from a broker.<br>
 * This packet communicates with {@link BrokerService} to process the transaction.
 * @author kosyak
 */
public class CM_BUY_BROKER_ITEM extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int brokerId;
	private int itemUniqueId;
	private long itemCount;
	
	/**
	 * This constructor initializes a new {@link CM_BUY_BROKER_ITEM} packet.<br>
	 * It passes the network states to the parent class.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_BUY_BROKER_ITEM(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		brokerId = readD();
		itemUniqueId = readD();
		itemCount = readQ();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if ((player == null) || (itemCount < 1))
		{
			return;
		}
		
		BrokerService.getInstance().buyBrokerItem(player, itemUniqueId, itemCount);
	}
}
