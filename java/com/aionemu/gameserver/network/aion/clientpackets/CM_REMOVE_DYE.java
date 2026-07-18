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
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * Handles the client request to remove a dye from an item.<br>
 * This packet processes the removal of visual effects applied by the player.
 * @author Falke_34
 */
public class CM_REMOVE_DYE extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int itemObjectId;
	
	/**
	 * Handles the removal of a dye from an item.<br>
	 * This packet is sent by the client to update the game state.
	 * @param opcode The unique identifier for this network command.
	 * @param state The primary {@link State} associated with the request.
	 * @param restStates Additional {@link State} objects if required.
	 */
	public CM_REMOVE_DYE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		itemObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
	}
}
