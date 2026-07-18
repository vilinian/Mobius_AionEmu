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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to notify that a duel request has been cancelled.<br>
 * It handles the communication for terminating an ongoing duel invitation process.
 * @author Falke_34 & FrozenKiller
 */
public class SM_DUEL_REQUEST_CANCEL extends AionServerPacket
{
	private final int msgId;
	private final String playerName;
	
	/**
	 * Cancels a duel request for a specific player.<br>
	 * This packet is used to stop an ongoing duel invitation.
	 * @param msgId The unique identifier for the message.
	 * @param playerName The name of the player who requested the duel.
	 */
	public SM_DUEL_REQUEST_CANCEL(int msgId, String playerName)
	{
		this.msgId = msgId;
		this.playerName = playerName;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(0);
		writeD(msgId);
		writeS(playerName);
		writeD(0);
	}
}
