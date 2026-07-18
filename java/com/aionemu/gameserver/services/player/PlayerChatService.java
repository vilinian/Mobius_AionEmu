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
package com.aionemu.gameserver.services.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles all player-related chat communications within the game.<br>
 * It manages message distribution and ensures that {@link ChatType} messages are processed correctly.
 * @author Source
 */
public class PlayerChatService
{
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger("CHAT_LOG");
	
	/**
	 * Checks if a {@link Player} is sending messages too quickly.<br>
	 * This method compares the player's message count against the limit in {@code SecurityConfig}.<br>
	 * If flooding occurs, it gags the player and sends a system message.
	 * @param player The {@code Player} object to check for flooding behavior.
	 * @return {@code true} if the player is currently flooding, otherwise {@code false}.
	 */
	public static boolean isFlooding(Player player)
	{
		player.setLastMessageTime();
		
		if (player.floodMsgCount() > SecurityConfig.FLOOD_MSG)
		{
			player.setGagged(true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FLOODING);
			player.getController().cancelTask(TaskId.GAG);
			player.getController().addTask(TaskId.GAG, ThreadPoolManager.getInstance().schedule((Runnable) () ->
			{
				player.setGagged(false);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CAN_CHAT_NOW);
			}, 2 * 60000L));
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Logs a chat message sent by a {@link Player}.<br>
	 * The log format changes based on the provided {@code ChatType}.<br>
	 * This method writes information to the server logs.
	 * @param player The {@code Player} who sent the message.
	 * @param type The category of the chat message.
	 * @param message The content of the message to be logged.
	 */
	public static void chatLogging(Player player, ChatType type, String message)
	{
		switch (type)
		{
			case GROUP:
				log.info(String.format("[MESSAGE] - GROUP <%d>: [%s]> %s", player.getCurrentTeamId(), player.getName(), message));
				break;
			case ALLIANCE:
				log.info(String.format("[MESSAGE] - ALLIANCE <%d>: [%s]> %s", player.getCurrentTeamId(), player.getName(), message));
				break;
			case GROUP_LEADER:
				log.info(String.format("[MESSAGE] - LEADER_ALERT: [%s]> %s", player.getName(), message));
				break;
			case LEGION:
				log.info(String.format("[MESSAGE] - LEGION <%s>: [%s]> %s", player.getLegion().getLegionName(), player.getName(), message));
				break;
			case LEAGUE:
			case LEAGUE_ALERT:
				log.info(String.format("[MESSAGE] - LEAGUE <%s>: [%s]> %s", player.getCurrentTeamId(), player.getName(), message));
				break;
			case NORMAL:
			case SHOUT:
				if (player.getRace() == Race.ASMODIANS)
				{
					log.info(String.format("[MESSAGE] - ALL (ASMO): [%s]> %s", player.getName(), message));
				}
				else
				{
					log.info(String.format("[MESSAGE] - ALL (ELYOS): [%s]> %s", player.getName(), message));
				}
				break;
			default:
				if (player.isGM())
				{
					log.info(String.format("[MESSAGE] - ALL (GM): [%s]> %s", player.getName(), message));
				}
				break;
		}
	}
}
