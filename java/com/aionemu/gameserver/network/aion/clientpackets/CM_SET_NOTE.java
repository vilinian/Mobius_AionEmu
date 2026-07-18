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

import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_NOTE;

/**
 * This packet is received from the client when a player updates their personal note.<br>
 * It handles the request to change the text displayed in the player's profile.
 * @author Ben
 */
public class CM_SET_NOTE extends AionClientPacket
{
	private String note;
	
	/**
	 * This method creates a new {@code CM_SET_NOTE} packet.<br>
	 * It initializes the packet with the required network states.<br>
	 * Use this constructor to prepare a note update request.
	 * @param opcode The unique identifier for the packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if needed.
	 */
	public CM_SET_NOTE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		note = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		
		if (!note.equals(activePlayer.getCommonData().getNote()))
		{
			activePlayer.getCommonData().setNote(note);
			sendPacket(new SM_UPDATE_NOTE(activePlayer.getObjectId(), note)); // Update note
			
			for (Friend friend : activePlayer.getFriendList()) // For all my friends
			{
				final Player frienPlayer = friend.getPlayer();
				if (friend.isOnline() && (frienPlayer != null)) // If the player is online
				{
					friend.getPlayer().getClientConnection().sendPacket(new SM_FRIEND_LIST()); // Send him a new friend list packet
				}
			}
		}
	}
}
