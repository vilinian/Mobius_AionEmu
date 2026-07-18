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

import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is used to add a new member to a legion.<br>
 * It handles the synchronization of membership data between the server and the client.
 * @author Simple
 */
public class SM_LEGION_ADD_MEMBER extends AionServerPacket
{
	private final Player player;
	private final boolean isMember;
	private final int msgId;
	private final String text;
	
	/**
	 * This method creates a new {@code SM_LEGION_ADD_MEMBER} packet.<br>
	 * It handles adding a member to a legion.
	 * @param player The {@link Player} object receiving the packet.
	 * @param isMember A boolean indicating if the action adds a member.
	 * @param msgId The unique identifier for the message.
	 * @param text The custom text content to display in the message.
	 */
	public SM_LEGION_ADD_MEMBER(Player player, boolean isMember, int msgId, String text)
	{
		this.player = player;
		this.isMember = isMember;
		this.msgId = msgId;
		this.text = text;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(player.getObjectId());
		writeS(player.getName());
		writeC(player.getLegionMember().getRank().getRankId());
		writeC(isMember ? 0x01 : 0x00); // is New Member?
		writeC(player.getCommonData().getPlayerClass().getClassId());
		writeC(player.getLevel());
		writeD(player.getPosition().getMapId());
		writeC(player.isOnline() ? 1 : 0);
		writeD(0); // Unk
		writeD(NetworkConfig.GAMESERVER_ID);
		writeD(msgId);
		writeS(text);
	}
}
