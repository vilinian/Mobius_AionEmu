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
import com.aionemu.gameserver.model.team.legion.LegionMemberEx;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet updates the client about changes to a member within a {@link com.aionemu.gameserver.model.team.legion.LegionMemberEx}.<br>
 * It is used to synchronize legion membership data between the server and the player.
 * @author Simple
 */
public class SM_LEGION_UPDATE_MEMBER extends AionServerPacket
{
	private static final byte OFFLINE = 0x00;
	private static final byte ONLINE = 0x01;
	private Player player;
	private LegionMemberEx LM;
	private int msgId;
	private String text;
	private final byte isOnline;
	
	/**
	 * Updates the legion member information for a specific player.<br>
	 * This packet sends a message to the client regarding a member's status.<br>
	 * It automatically determines if the member is online or offline based on the {@code Player} state.
	 * @param player The {@link Player} object receiving the update.
	 * @param msgId The unique identifier for the message type.
	 * @param text The content of the message to display.
	 */
	public SM_LEGION_UPDATE_MEMBER(Player player, int msgId, String text)
	{
		this.player = player;
		this.msgId = msgId;
		this.text = text;
		isOnline = player.isOnline() ? ONLINE : OFFLINE;
	}
	
	/**
	 * Creates a new {@code SM_LEGION_UPDATE_MEMBER} packet.<br>
	 * This updates the status of a legion member for other players.<br>
	 * It sets the online status based on the provided {@code LM} object.
	 * @param LM The {@link LegionMemberEx} object to update.
	 * @param msgId The unique identifier for the message.
	 * @param text The text content to be displayed in the update.
	 */
	public SM_LEGION_UPDATE_MEMBER(LegionMemberEx LM, int msgId, String text)
	{
		this.LM = LM;
		this.msgId = msgId;
		this.text = text;
		isOnline = LM.isOnline() ? ONLINE : OFFLINE;
	}
	
	/**
	 * Creates a new packet to update legion member status.<br>
	 * This constructor sets the {@code player} and marks them as offline.
	 * @param player The {@link Player} object associated with this update.
	 */
	public SM_LEGION_UPDATE_MEMBER(Player player)
	{
		this.player = player;
		isOnline = OFFLINE;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		if (player != null)
		{
			writeD(player.getObjectId());
			writeC(player.getLegionMember().getRank().getRankId());
			writeC(player.getCommonData().getPlayerClass().getClassId());
			writeC(player.getLevel());
			writeD(player.getPosition().getMapId());
			writeC(isOnline);
			writeD(player.isOnline() ? 0 : player.getLastOnline());
			writeD(NetworkConfig.GAMESERVER_ID);
			writeD(msgId);
			writeS(text);
		}
		else if (LM != null)
		{
			writeD(LM.getObjectId());
			writeC(LM.getRank().getRankId());
			writeC(LM.getPlayerClass().getClassId());
			writeC(LM.getLevel());
			writeD(LM.getWorldId());
			writeC(isOnline);
			writeD(LM.isOnline() ? 0 : LM.getLastOnline());
			writeD(NetworkConfig.GAMESERVER_ID);
			writeD(msgId);
			writeS(text);
		}
	}
}
