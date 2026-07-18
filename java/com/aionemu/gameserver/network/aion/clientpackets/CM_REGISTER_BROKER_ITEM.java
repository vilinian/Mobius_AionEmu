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
 * Handles the registration of an item with the {@link BrokerService}.<br>
 * This packet is sent by the client to list a specific item for sale or trade.<br>
 * It processes the request and updates the broker's available inventory.
 * @author kosyak
 */
public class CM_REGISTER_BROKER_ITEM extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int brokerId;
	private int itemUniqueId;
	private long price;
	private long itemCount;
	private boolean partSale;
	
	/**
	 * Registers a new item with the broker system.<br>
	 * This method initializes a {@link CM_REGISTER_BROKER_ITEM} packet.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_REGISTER_BROKER_ITEM(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		brokerId = readD();
		itemUniqueId = readD();
		price = readQ();
		itemCount = readQ();
		partSale = (readC() == 1);
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		if (player.isTrading() || (price < 1) || (itemCount < 1))
		{
			return;
		}
		
		BrokerService.getInstance().registerItem(player, itemUniqueId, itemCount, price, partSale);
	}
}
