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
import com.aionemu.gameserver.services.PrivateStoreService;

/**
 * Handles the client packet for requesting or updating a private store name.<br>
 * This class allows players to interact with {@link PrivateStoreService} to manage their shop identity.
 * @author Simple
 */
public class CM_PRIVATE_STORE_NAME extends AionClientPacket
{
	private String name;
	
	/**
	 * Creates a new instance of the {@link CM_PRIVATE_STORE_NAME} packet.<br>
	 * This constructor initializes the packet with its required network properties.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@code State} associated with the packet.
	 * @param restStates A variable number of additional {@code State} objects.
	 */
	public CM_PRIVATE_STORE_NAME(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		name = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		PrivateStoreService.openPrivateStore(activePlayer, name);
	}
}
