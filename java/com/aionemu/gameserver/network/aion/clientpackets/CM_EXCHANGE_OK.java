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
 * This packet is sent to the client to confirm a successful exchange.<br>
 * It notifies the player that their trade request has been completed successfully.
 * @author -Avol-
 */
public class CM_EXCHANGE_OK extends AionClientPacket
{
	/**
	 * This constructor initializes a new {@link CM_EXCHANGE_OK} packet.<br>
	 * It sets the required network properties for the exchange operation.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state associated with the packet.
	 * @param restStates A variable number of additional states for the packet.
	 */
	public CM_EXCHANGE_OK(int opcode, State state, State... restStates)
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
		final Player activePlayer = getConnection().getActivePlayer();
		ExchangeService.getInstance().confirmExchange(activePlayer);
	}
}
