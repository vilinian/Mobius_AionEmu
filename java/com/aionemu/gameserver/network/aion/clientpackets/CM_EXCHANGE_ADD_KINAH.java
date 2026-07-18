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
 * Handles the client request to add {@code Kinah} to an exchange.<br>
 * This packet is processed by the {@link ExchangeService}.
 * @author Avol
 */
public class CM_EXCHANGE_ADD_KINAH extends AionClientPacket
{
	public int unk;
	public int itemCount;
	
	/**
	 * This method initializes a new {@code CM_EXCHANGE_ADD_KINAH} packet.<br>
	 * It sets the required network states for the exchange action.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state required.
	 * @param restStates Additional connection states that must be met.
	 */
	public CM_EXCHANGE_ADD_KINAH(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		itemCount = readD();
		unk = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		ExchangeService.getInstance().addKinah(activePlayer, itemCount);
	}
}
