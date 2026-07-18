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
import com.aionemu.gameserver.services.player.PlayerCollectionService;

/**
 * Handles the registration of a player's collection data.<br>
 * This packet is used to sync or initialize {@link Player} collection information with the server.
 */
public class CM_PLAYER_COLLECTION_REGISTER extends AionClientPacket
{
	public int id;
	public int index;
	public int objectId;
	public int count;
	
	/**
	 * Registers a new player collection.<br>
	 * This method initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if needed.
	 */
	public CM_PLAYER_COLLECTION_REGISTER(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		id = readD();
		index = readC();
		objectId = readD();
		count = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		PlayerCollectionService.getInstance().registerCollection(player, id, index, objectId, count);
	}
}
