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
 * Handles the {@code CM_EXCHANGE_LOCK} packet sent from the client.<br>
 * This packet is used to request or confirm a lock on an exchange window.<br>
 * It interacts with the {@link ExchangeService} to manage trade permissions.
 * @author -Avol-
 */
public class CM_EXCHANGE_LOCK extends AionClientPacket
{
	/**
	 * Creates a new instance of the {@link CM_EXCHANGE_LOCK} packet.<br>
	 * This constructor initializes the packet with specific states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} associated with the lock.
	 * @param restStates A variable number of additional {@link State} objects.
	 */
	public CM_EXCHANGE_LOCK(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		// nothing
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		ExchangeService.getInstance().lockExchange(activePlayer);
	}
}
