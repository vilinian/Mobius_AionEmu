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
import com.aionemu.gameserver.network.aion.serverpackets.SM_MAY_LOGIN_INTO_GAME;

/**
 * This packet represents a request from the {@code AionClient} asking for permission to log into the game.<br>
 * It is used by the client to initiate the transition into the active gameplay state.
 * @author -Nemesiss-
 */
public class CM_MAY_LOGIN_INTO_GAME extends AionClientPacket
{
	/**
	 * This method handles the request to log into the game.<br>
	 * It processes the primary {@code State} and any additional {@code State} values provided.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary login state.
	 * @param restStates A variable number of additional states.
	 */
	public CM_MAY_LOGIN_INTO_GAME(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		// empty
	}
	
	@Override
	protected void runImpl()
	{
		final AionConnection client = getConnection();
		
		// TODO! check if may login into game [play time etc]
		client.sendPacket(new SM_MAY_LOGIN_INTO_GAME());
	}
}
