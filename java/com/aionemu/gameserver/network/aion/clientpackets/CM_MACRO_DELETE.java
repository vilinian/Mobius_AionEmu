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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MACRO_RESULT;
import com.aionemu.gameserver.services.player.PlayerService;

/**
 * This packet handles the request from the client to delete a specific macro.<br>
 * The client provides an index, and the server removes the corresponding entry from the player's macro list.
 * @author SoulKeeper
 */
public class CM_MACRO_DELETE extends AionClientPacket
{
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_MACRO_DELETE.class);
	/**
	 * Macro id that has to be deleted
	 */
	private int macroPosition;
	
	/**
	 * Initializes a new instance of the {@code CM_MACRO_DELETE} packet.<br>
	 * This constructor passes the network states to the parent class.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state associated with the connection.
	 * @param restStates Additional states used by the {@link AionClientPacket} class.
	 */
	public CM_MACRO_DELETE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		macroPosition = readC();
	}
	
	@Override
	protected void runImpl()
	{
		log.debug("Request to delete macro #" + macroPosition);
		
		PlayerService.removeMacro(getConnection().getActivePlayer(), macroPosition);
		
		sendPacket(SM_MACRO_RESULT.SM_MACRO_DELETED);
	}
}
