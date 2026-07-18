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
 * This packet is sent to notify clients that a member has left a {@code Legion}.<br>
 * It handles the synchronization of legion membership status across the network.
 * @author Simple
 */
public class SM_LEGION_LEAVE_MEMBER extends AionServerPacket
{
	private final String name;
	private String name1;
	private final int playerObjId;
	private final int msgId;
	
	/**
	 * Creates a new {@code SM_LEGION_LEAVE_MEMBER} packet.<br>
	 * This packet is used when a member leaves a legion.
	 * @param msgId The unique identifier for the message type.
	 * @param playerObjId The unique ID of the player object.
	 * @param name The name of the character involved.
	 */
	public SM_LEGION_LEAVE_MEMBER(int msgId, int playerObjId, String name)
	{
		this.msgId = msgId;
		this.playerObjId = playerObjId;
		this.name = name;
	}
	
	/**
	 * Creates a new {@code SM_LEGION_LEAVE_MEMBER} packet.<br>
	 * This packet handles the logic for a member leaving a legion.
	 * @param msgId The unique identifier for the message type.
	 * @param playerObjId The object ID of the player involved.
	 * @param name The primary name associated with the action.
	 * @param name1 The secondary name associated with the action.
	 */
	public SM_LEGION_LEAVE_MEMBER(int msgId, int playerObjId, String name, String name1)
	{
		this.msgId = msgId;
		this.playerObjId = playerObjId;
		this.name = name;
		this.name1 = name1;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(playerObjId);
		writeC(0x00); // isMember ? 1 : 0
		writeD(0x00); // unix time for log off
		writeD(msgId);
		writeS(name);
		writeS(name1);
	}
}
