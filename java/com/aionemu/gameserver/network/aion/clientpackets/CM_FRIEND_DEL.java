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

import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SocialService;

/**
 * Handles the client request to remove a friend from the player's list.<br>
 * This packet processes the removal logic via the {@link SocialService}.
 * @author Ben
 */
public class CM_FRIEND_DEL extends AionClientPacket
{
	private String targetName;
	private static Logger log = LoggerFactory.getLogger(CM_FRIEND_DEL.class);
	
	/**
	 * Handles the request to delete a friend from the player's list.<br>
	 * This packet is received from the client to remove a specific social connection.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} of the packet.
	 * @param restStates Additional {@link State} values associated with the packet.
	 */
	public CM_FRIEND_DEL(int opcode, State state, State... restStates)
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
		final Friend target = activePlayer.getFriendList().getFriend(targetName);
		if (target == null)
		{
			log.warn(activePlayer.getName() + " tried to delete friend " + targetName + " who is not his friend");
			sendPacket(SM_SYSTEM_MESSAGE.STR_BUDDYLIST_NOT_IN_LIST);
		}
		else
		{
			SocialService.deleteFriend(activePlayer, target.getOid());
		}
	}
}
