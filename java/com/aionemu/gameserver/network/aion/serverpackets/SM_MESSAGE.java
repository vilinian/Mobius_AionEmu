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

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the transmission of chat messages and other related text communications.<br>
 * It is used to send information from the server to the client regarding player interactions.
 * @author -Nemesiss-, Sweetkr
 */
public class SM_MESSAGE extends AionServerPacket
{
	/**
	 * Player.
	 */
	private Player player;
	/**
	 * Object that is saying smth or null.
	 */
	private final int senderObjectId;
	/**
	 * Message.
	 */
	private final String message;
	/**
	 * Name of the sender
	 */
	private final String senderName;
	/**
	 * Sender race
	 */
	private Race race;
	/**
	 * Chat type
	 */
	private final ChatType chatType;
	/**
	 * Sender coordinates
	 */
	private float x;
	private float y;
	private float z;
	
	/**
	 * Creates a new {@code SM_MESSAGE} packet.<br>
	 * This constructor initializes the message with data from a {@link Player}.<br>
	 * It sets the sender details and coordinates automatically.
	 * @param player The {@code Player} who is sending the message.
	 * @param message The text content of the message.
	 * @param chatType The category of the chat message.
	 */
	public SM_MESSAGE(Player player, String message, ChatType chatType)
	{
		this.player = player;
		senderObjectId = player.getObjectId();
		senderName = player.getName();
		this.message = message;
		race = player.getRace();
		this.chatType = chatType;
		x = player.getX();
		y = player.getY();
		z = player.getZ();
	}
	
	/**
	 * Creates a new {@code SM_MESSAGE} packet.<br>
	 * This constructor initializes the message with specific sender details.<br>
	 * It is used to send chat or other system messages to players.
	 * @param senderObjectId The unique ID of the object sending the message.
	 * @param senderName The display name of the sender.
	 * @param message The actual text content of the message.
	 * @param chatType The category of the chat, such as global or local.
	 */
	public SM_MESSAGE(int senderObjectId, String senderName, String message, ChatType chatType)
	{
		this.senderObjectId = senderObjectId;
		this.senderName = senderName;
		this.message = message;
		this.chatType = chatType;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		boolean canRead = true;
		
		if (race != null)
		{
			canRead = chatType.isSysMsg() || CustomConfig.SPEAKING_BETWEEN_FACTIONS || (player.getAccessLevel() > 0) || ((con.getActivePlayer() != null) && (con.getActivePlayer().getAccessLevel() > 0));
		}
		
		writeC(chatType.toInteger()); // type
		
		/*
		 * 0 : all 1 : elyos 2 : asmodians
		 */
		writeC(canRead ? 0 : race.getRaceId() + 1);
		writeD(senderObjectId); // sender object id
		
		switch (chatType)
		{
			case NORMAL:
			case WHITE:
			case YELLOW:
			case BRIGHT_YELLOW:
			case WHITE_CENTER:
			case YELLOW_CENTER:
			case BRIGHT_YELLOW_CENTER:
			case BRIGHT_YELLOW_CENTER_NEW:
				writeH(0x00); // unknown
				writeS(message);
				break;
			case SHOUT:
				writeS(senderName);
				writeS(message);
				writeF(x);
				writeF(y);
				writeF(z);
				break;
			case ALLIANCE:
			case GROUP:
			case GROUP_LEADER:
			case LEGION:
			case WHISPER:
			case LEAGUE:
			case LEAGUE_ALERT:
			case CH1:
			case CH2:
			case CH3:
			case CH4:
			case CH5:
			case CH6:
			case CH7:
			case CH8:
			case CH9:
			case CH10:
			case COMMAND:
			case UNION_WAR:
			case GMRESPONSE:
				writeS(senderName);
				writeS(message);
				break;
			default:
				break;
		}
	}
}
