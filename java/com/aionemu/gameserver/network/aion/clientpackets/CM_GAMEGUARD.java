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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.antihack.AntiHackService;

/**
 * Handles the {@code CM_GAMEGUARD} packet received from the client.<br>
 * This packet is used to communicate with the game's anti-cheat system.<br>
 * It interacts with the {@link AntiHackService} to manage security checks.
 * @author Alcapwnd
 */
public class CM_GAMEGUARD extends AionClientPacket
{
	private int size;
	private static final Logger log = LoggerFactory.getLogger(CM_GAMEGUARD.class);
	
	/**
	 * This constructor initializes a new {@code CM_GAMEGUARD} packet.<br>
	 * It passes the required network data to the parent class.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state associated with the packet.
	 * @param restStates A variable number of additional states for the packet.
	 */
	public CM_GAMEGUARD(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		size = readD();
		readB(size);
	}
	
	@Override
	protected void runImpl()
	{
		log.info("AION Bin size from client: " + size);
		final Player player = getConnection().getActivePlayer();
		AntiHackService.checkAionBin(size, player);
	}
	
}
