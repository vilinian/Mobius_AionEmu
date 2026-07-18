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
 * This packet handles the request to retrieve a list of available brokers.<br>
 * It interacts with the {@link BrokerService} to fetch and send broker data to the client.
 * @author kosyachok
 */
public class CM_BROKER_LIST extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int brokerId;
	private int sortType;
	private int page;
	private int listMask;
	
	/**
	 * Creates a new instance of {@link CM_BROKER_LIST}.<br>
	 * This constructor initializes the packet with specific states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state associated with the packet.
	 * @param restStates A variable number of additional states for the packet.
	 */
	public CM_BROKER_LIST(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		brokerId = readD();
		sortType = readC(); // 1 - name; 2 - level; 4 - totalPrice; 6 - price for piece
		page = readH();
		listMask = readH();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		BrokerService.getInstance().showRequestedItems(player, listMask, sortType, page, null);
	}
}
