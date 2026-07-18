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
import com.aionemu.gameserver.network.aion.serverpackets.SM_BLOCK_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SocialService;
import com.aionemu.gameserver.world.World;

/**
 * Handles the client request to add a player to the block list.<br>
 * This packet processes the {@code blockId} and communicates with the {@link SocialService}.<br>
 * It sends an {@link SM_BLOCK_RESPONSE} back to the sender upon completion.
 * @author Ben
 */
public class CM_BLOCK_ADD extends AionClientPacket
{
	private static Logger log = LoggerFactory.getLogger(CM_BLOCK_ADD.class);
	private String targetName;
	private String reason;
	
	/**
	 * This method initializes a new {@link CM_BLOCK_ADD} packet.<br>
	 * It sets the required network states for the request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_BLOCK_ADD(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		targetName = readS();
		reason = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		
		final Player targetPlayer = World.getInstance().findPlayer(targetName);
		
		// Trying to block self
		if (activePlayer.getName().equalsIgnoreCase(targetName))
		{
			sendPacket(new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.CANT_BLOCK_SELF, targetName));
		} // List full
		else if (activePlayer.getBlockList().isFull())
		{
			sendPacket(new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.LIST_FULL, targetName));
		} // Player offline
		else if (targetPlayer == null)
		{
			sendPacket(new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.TARGET_NOT_FOUND, targetName));
		} // Player is your friend
		else if (activePlayer.getFriendList().getFriend(targetPlayer.getObjectId()) != null)
		{
			sendPacket(SM_SYSTEM_MESSAGE.STR_BLOCKLIST_NO_BUDDY);
		} // Player already blocked
		else if (activePlayer.getBlockList().contains(targetPlayer.getObjectId()))
		{
			sendPacket(SM_SYSTEM_MESSAGE.STR_BLOCKLIST_ALREADY_BLOCKED);
		} // Try and block player
		else if (!SocialService.addBlockedUser(activePlayer, targetPlayer, reason))
		{
			log.error("Failed to add " + targetPlayer.getName() + " to the block list for " + activePlayer.getName() + " - check database setup.");
		}
		
	}
}
