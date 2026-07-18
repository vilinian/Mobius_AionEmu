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

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.player.PlayerEnterWorldService;

/**
 * This packet handles the request from the client to enter the game world.<br>
 * It checks if a specific character, identified by its {@code oid}, is allowed to log in.
 * @author -Nemesiss-, Avol
 */
public class CM_ENTER_WORLD extends AionClientPacket
{
	/**
	 * Object Id of player that is entering world
	 */
	private int objectId;
	
	/**
	 * This method initializes a new {@code CM_ENTER_WORLD} packet.<br>
	 * It handles the request for a character to enter the game world.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} of the connection.
	 * @param restStates A variable number of additional {@link State} values.
	 */
	public CM_ENTER_WORLD(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final AionConnection client = getConnection();
		PlayerEnterWorldService.startEnterWorld(objectId, client);
	}
}
