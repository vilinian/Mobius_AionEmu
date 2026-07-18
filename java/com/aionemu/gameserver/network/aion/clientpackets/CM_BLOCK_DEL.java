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

import com.aionemu.gameserver.model.gameobjects.player.BlockedPlayer;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SocialService;

/**
 * Handles the client request to remove a block on another player.<br>
 * This packet processes the removal of a {@code BlockedPlayer} from the user's list.
 * @author Ben
 */
public class CM_BLOCK_DEL extends AionClientPacket
{
	private static Logger log = LoggerFactory.getLogger(CM_BLOCK_DEL.class);
	private String targetName;
	
	/**
	 * This method creates a new {@link CM_BLOCK_DEL} packet.<br>
	 * It initializes the packet with the required network states.<br>
	 * Use this constructor to handle requests for removing blocks.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_BLOCK_DEL(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		targetName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		
		final BlockedPlayer target = activePlayer.getBlockList().getBlockedPlayer(targetName);
		if (target == null)
		{
			sendPacket(SM_SYSTEM_MESSAGE.STR_BUDDYLIST_NOT_IN_LIST);
		}
		else
		{
			if (!SocialService.deleteBlockedUser(activePlayer, target.getObjId()))
			{
				log.debug("Could not unblock " + targetName + " from " + activePlayer.getName() + " blocklist. Check database setup.");
			}
		}
	}
}
