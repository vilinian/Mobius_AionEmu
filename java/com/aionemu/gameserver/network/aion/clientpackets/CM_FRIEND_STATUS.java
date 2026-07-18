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

import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_STATUS;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This packet is received from the client when a user updates their friend list status.<br>
 * It triggers an update to the {@link Player} object and notifies other players via {@link SM_FRIEND_STATUS}.
 * @author Ben
 */
public class CM_FRIEND_STATUS extends AionClientPacket
{
	private final Logger log = LoggerFactory.getLogger(CM_FRIEND_STATUS.class);
	
	// The users new status
	private byte status;
	
	/**
	 * Creates a new instance of the {@link CM_FRIEND_STATUS} packet.<br>
	 * This constructor initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if they exist.
	 */
	public CM_FRIEND_STATUS(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		status = (byte) readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		Status statusEnum = Status.getByValue(status);
		if (statusEnum == null)
		{
			log.warn("received unknown status id " + status);
			statusEnum = Status.ONLINE;
		}
		
		activePlayer.getFriendList().setStatus(statusEnum, activePlayer.getCommonData());
		PacketSendUtility.sendPacket(activePlayer, new SM_FRIEND_STATUS(status));
	}
}
