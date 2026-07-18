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
 * Handles the client request to create a new macro.<br>
 * This packet is sent by the player to register a new macro in the game system.
 * @author SoulKeeper
 */
public class CM_MACRO_CREATE extends AionClientPacket
{
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_MACRO_CREATE.class);
	/**
	 * Macro number. Fist is 1, second is 2. Starting from 1, not from 0
	 */
	private int macroPosition;
	/**
	 * XML that represents the macro
	 */
	private String macroXML;
	
	/**
	 * Creates a new instance of the {@link CM_MACRO_CREATE} packet.<br>
	 * This method initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state associated with the request.
	 * @param restStates Additional states that may be included in the packet.
	 */
	public CM_MACRO_CREATE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		macroPosition = readC();
		macroXML = readS();
	}
	
	@Override
	protected void runImpl()
	{
		log.debug(String.format("Created Macro #%d: %s", macroPosition, macroXML));
		
		PlayerService.addMacro(getConnection().getActivePlayer(), macroPosition, macroXML);
		
		sendPacket(SM_MACRO_RESULT.SM_MACRO_CREATED);
	}
}
