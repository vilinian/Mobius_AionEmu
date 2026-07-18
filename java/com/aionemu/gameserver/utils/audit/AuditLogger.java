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
package com.aionemu.gameserver.utils.audit;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.PunishmentConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides a centralized system for logging security-related events and administrative actions.<br>
 * It helps administrators monitor server activity by recording significant game events to the audit logs.
 * @author MrPoke
 */
public class AuditLogger
{
	private static final Logger log = LoggerFactory.getLogger("AUDIT_LOG");
	
	/**
	 * Logs an information message for a specific {@link Player}.<br>
	 * This method checks if audit logging is enabled before writing the entry.<br>
	 * It also triggers punishment actions if they are currently active.
	 * @param player The {@code Player} object to log information for.
	 * @param message The text message to be recorded in the logs.
	 */
	public static void info(Player player, String message)
	{
		Objects.requireNonNull(player, "Player should not be null or use different info method");
		if (LoggingConfig.LOG_AUDIT)
		{
			info(player.getName(), player.getObjectId(), message);
		}
		
		if (PunishmentConfig.PUNISHMENT_ENABLE)
		{
			AutoBan.punishment(player, message);
		}
	}
	
	/**
	 * Logs an information message to the audit log.<br>
	 * This method also broadcasts the message if enabled in {@code SecurityConfig}.
	 * @param playerName The name of the player involved.
	 * @param objectId The unique identifier for the game object.
	 * @param message The specific information to be logged and broadcast.
	 */
	public static void info(String playerName, int objectId, String message)
	{
		message += " Player name: " + playerName + " objectId: " + objectId;
		log.info(message);
		
		if (SecurityConfig.GM_AUDIT_MESSAGE_BROADCAST)
		{
			GMService.getInstance().broadcastMesage(message);
		}
	}
}
