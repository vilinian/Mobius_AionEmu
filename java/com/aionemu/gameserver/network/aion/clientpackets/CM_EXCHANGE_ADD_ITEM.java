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
import com.aionemu.gameserver.services.ExchangeService;

/**
 * Handles the client request to add an item to the exchange window.<br>
 * This packet is processed by the {@link ExchangeService} to update the current trade list.
 * @author Avol
 */
public class CM_EXCHANGE_ADD_ITEM extends AionClientPacket
{
	public int itemObjId;
	public int itemCount;
	
	/**
	 * Creates a new instance of the {@code CM_EXCHANGE_ADD_ITEM} packet.<br>
	 * This constructor initializes the packet with specific network states.<br>
	 * It passes all provided arguments to the parent class constructor.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates A variable number of additional states for the connection.
	 */
	public CM_EXCHANGE_ADD_ITEM(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		itemObjId = readD();
		itemCount = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		ExchangeService.getInstance().addItem(activePlayer, itemObjId, itemCount);
	}
}
