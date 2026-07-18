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
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_RESPONSE;
import com.aionemu.gameserver.services.SocialService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the client request to edit a friend's information.<br>
 * This packet is processed by the {@link SocialService} to update the {@code Friend} data.<br>
 * It ensures that the requested changes are validated before being sent back via {@link SM_FRIEND_RESPONSE}.
 * @author Lyras
 */
public class CM_FRIEND_EDIT extends AionClientPacket
{
	private String playerName;
	private String notice;
	
	/**
	 * This constructor initializes a new {@code CM_FRIEND_EDIT} packet.<br>
	 * It sets the required network states for the packet.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_FRIEND_EDIT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		playerName = readS();
		notice = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		final Friend friend = activePlayer.getFriendList().getFriend(playerName);
		if (friend != null)
		{
			PacketSendUtility.sendPacket(activePlayer, new SM_FRIEND_RESPONSE(playerName, SM_FRIEND_RESPONSE.TARGET_NOTE));
			SocialService.setFriendNote(activePlayer, friend, notice);
		}
	}
}
