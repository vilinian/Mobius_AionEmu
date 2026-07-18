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
package com.aionemu.gameserver.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.administration.DeveloperConfig;

/**
 * This service handles the logging of network packets received by the server.<br>
 * It allows developers to monitor and debug communication between the client and the server.<br>
 * The logging behavior is controlled via the {@link DeveloperConfig} settings.
 * @author Alcapwnd
 */
public class PacketLoggerService
{
	private static final Logger log = LoggerFactory.getLogger(PacketLoggerService.class);
	
	/**
	 * Logs a client message packet to the server console.<br>
	 * This action only occurs if {@code DeveloperConfig.SHOW_PACKETS} is set to {@code true}.
	 * @param name The name of the packet to be logged.
	 */
	public void logPacketCM(String name)
	{
		if (DeveloperConfig.SHOW_PACKETS)
		{
			log.info("[PACKET CLIENT] " + name);
		}
	}
	
	/**
	 * Logs a Server Message packet to the console.<br>
	 * This action only occurs if {@code DeveloperConfig.SHOW_PACKETS} is set to {@code true}.
	 * @param name The name of the packet to log.
	 */
	public void logPacketSM(String name)
	{
		if (DeveloperConfig.SHOW_PACKETS)
		{
			log.info("[PACKET SERVER] " + name);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final PacketLoggerService instance = new PacketLoggerService();
	}
	
	/**
	 * Retrieves the singleton instance of the {@link PacketLoggerService}.<br>
	 * Use this method to access the logging service from anywhere in your code.
	 * @return The global instance of {@code PacketLoggerService}.
	 */
	public static PacketLoggerService getInstance()
	{
		return SingletonHolder.instance;
	}
}
