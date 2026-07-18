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

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.BrokerService;

/**
 * Handles the client request to search for brokers.<br>
 * This packet is processed by the {@link BrokerService} to find available services.
 * @author namedrisk
 */
public class CM_BROKER_SEARCH extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int brokerId;
	private int sortType;
	private int page;
	private int mask;
	private int itemCount;
	private List<Integer> itemList;
	
	/**
	 * This method initializes a new {@code CM_BROKER_SEARCH} packet.<br>
	 * It sets the required network states for the request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the connection.
	 */
	public CM_BROKER_SEARCH(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		brokerId = readD();
		sortType = readC(); // 1 - name; 2 - level; 4 - totalPrice; 6 - price for piece
		page = readH();
		mask = readH();
		itemCount = readH();
		itemList = new ArrayList<>();
		
		for (int index = 0; index < itemCount; index++)
		{
			itemList.add(readD());
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		BrokerService.getInstance().showRequestedItems(player, mask, sortType, page, itemList);
	}
}
