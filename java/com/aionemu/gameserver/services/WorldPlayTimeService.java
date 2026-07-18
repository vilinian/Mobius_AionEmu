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
package com.aionemu.gameserver.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WORLD_PLAYTIME;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the tracking and broadcasting of total world play time.<br>
 * This service handles periodic updates to inform players about the current session duration.<br>
 * It utilizes {@link ThreadPoolManager} to execute background tasks for these notifications.
 */
public class WorldPlayTimeService
{
	
	@SuppressWarnings("unused")
	private Future<?> checkPlayTimeTask;
	private final Map<Integer, Player> players = new ConcurrentHashMap<>();
	
	/**
	 * Starts the world play time service.<br>
	 * This method triggers the initial {@code checkWorldPlayTime} task.<br>
	 * It prepares the service for active use.
	 */
	public void onStart()
	{
		checkWorldPlayTime();
	}
	
	/**
	 * Handles special logic when a {@link Player} enters specific world IDs.<br>
	 * It sends {@code SM_FLAG_INFO} packets to all players based on spawned NPCs in those worlds.<br>
	 * This method also triggers a zone update for the player controller.
	 * @param player The {@link Player} object that entered the world.
	 */
	public void onEnterWorld(Player player)
	{
		switch (player.getWorldId())
		{
			case 800030000:
			case 800040000:
			case 800050000:
			case 800060000:
			case 800070000:
			{
				if (players.containsKey(player.getObjectId()))
				{
					break;
				}
				
				players.put(player.getObjectId(), player);
				break;
			}
			default:
			{
				if (!players.containsKey(player.getObjectId()))
				{
					break;
				}
				
				players.remove(player.getObjectId());
			}
		}
	}
	
	/**
	 * This method starts a background task to track player playtime.<br>
	 * It updates the world play time for every {@link Player}.<br>
	 * Players with zero time are teleported to their starting locations.
	 */
	public void checkWorldPlayTime()
	{
		checkPlayTimeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate((Runnable) () ->
		{
			for (Player player : players.values())
			{
				if (player.getCommonData().getWorldPlayTime() == 0)
				{
					player.setWorldPlayTime(0);
					PacketSendUtility.sendPacket(player, new SM_WORLD_PLAYTIME(player));
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1405813, new Object[0]));
					if (player.getRace() == Race.ELYOS)
					{
						TeleportService2.teleportTo(player, 210050000, 1306.0f, 238.0f, 595.0f, (byte) 17);
						continue;
					}
					
					TeleportService2.teleportTo(player, 220070000, 1788.0f, 2917.0f, 554.0f, (byte) 99);
					continue;
				}
				
				player.getCommonData().setWorldPlayTime(player.getCommonData().getWorldPlayTime() - 1);
				PacketSendUtility.sendPacket(player, new SM_WORLD_PLAYTIME(player));
			}
		}, 60000, 60000);
	}
	
	/**
	 * Gets the singleton instance of the {@link WorldPlayTimeService}.<br>
	 * Use this method to access the global service for tracking world play time.
	 * @return The active instance of {@code WorldPlayTimeService}.
	 */
	public static WorldPlayTimeService getInstance()
	{
		return NewSingletonHolder.INSTANCE;
	}
	
	private static class NewSingletonHolder
	{
		private static final WorldPlayTimeService INSTANCE = new WorldPlayTimeService();
	}
}
