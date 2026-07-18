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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.FastTrackConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SERVER_IDS;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.transfers.FastTrack;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldType;

/**
 * Manages the fast travel system for players within the game world.<br>
 * It handles {@link FastTrack} requests and coordinates with {@link TeleportService2}.<br>
 * This service ensures that players can move between locations quickly based on {@code FastTrackConfig} settings.
 * @author Eloann - Enomine, Alcapwnd
 */
public class FastTrackService
{
	private static final FastTrackService instance = new FastTrackService();
	private final Logger log = LoggerFactory.getLogger(FastTrackService.class);
	private final Map<Integer, Player> accountsOnFast = new HashMap<>(1);
	
	/**
	 * Gets the singleton instance of the {@link FastTrackService}.<br>
	 * This method provides a global access point to the service.
	 * @return The single shared instance of {@code FastTrackService}.
	 */
	public static FastTrackService getInstance()
	{
		return instance;
	}
	
	/**
	 * Verifies if a {@link Player} is allowed to use the fast track service.<br>
	 * It checks if the player level is below or equal to {@code FastTrackConfig.FASTTRACK_MAX_LEVEL}.<br>
	 * If authorized, it sends the required server ID packet to the player.
	 * @param player The {@code Player} object to check for authorization.
	 */
	public void checkAuthorizationRequest(Player player)
	{
		final int upto = FastTrackConfig.FASTTRACK_MAX_LEVEL;
		if (player.getLevel() > upto)
		{
			return;
		}
		
		PacketSendUtility.sendPacket(player, new SM_SERVER_IDS(new FastTrack(FastTrackConfig.FASTTRACK_SERVER_ID, true, 1, upto)));
	}
	
	/**
	 * Moves the {@code player} to the fast track server.<br>
	 * This method uses {@code moveFastTrack}.<br>
	 * It sets the destination to the ID defined in {@code FastTrackConfig}.
	 * @param player The {@code Player} object to move.
	 */
	public void handleMoveThere(Player player)
	{
		TeleportService2.moveFastTrack(player, FastTrackConfig.FASTTRACK_SERVER_ID, false);
	}
	
	/**
	 * Moves the {@code player} back to the original location.<br>
	 * This method uses {@code int, boolean)}.<br>
	 * It sets the destination to the ID defined in {@code FastTrackConfig}.
	 * @param player The {@code Player} object to move.
	 */
	public void handleMoveBack(Player player)
	{
		TeleportService2.moveFastTrack(player, FastTrackConfig.FASTTRACK_SERVER_ID, true);
	}
	
	/**
	 * Validates and processes a player's movement to or from the Fast Track server.<br>
	 * This method handles account registration for the service and manages teleportation logic.<br>
	 * It also sends feedback messages to the {@link Player}.
	 * @param player The {@code Player} object being moved.
	 * @param accId The unique account identifier for the move request.
	 * @param back A boolean indicating if the player is returning to the standard server.
	 */
	public void checkFastTrackMove(Player player, int accId, boolean back)
	{
		if (back)
		{
			accountsOnFast.remove(accId);
			player.setOnFastTrack(false);
			PacketSendUtility.sendYellowMessage(player, "You joined the standard server!");
			fastTrackBonus(player, true);
		}
		else
		{
			if (accountsOnFast.containsKey(accId))
			{
				log.warn("Fast Track Service: Player " + player.getName() + " tried to move twice to ft server!");
				accountsOnFast.remove(accId);
				handleMoveBack(player);
				PacketSendUtility.sendYellowMessage(player, "You got teleported back to the normal server because you tried to enter the fast track server twice!");
			}
			
			if (accountsOnFast.containsKey(accId) && !accountsOnFast.containsValue(player))
			{
				log.warn("Fast Track Service: Player " + player.getName() + " got wrong accid???");
				handleMoveBack(player);
				PacketSendUtility.sendYellowMessage(player, "You got teleported back to the normal server because something went wrong!");
			}
			
			accountsOnFast.put(accId, player);
			player.setOnFastTrack(true);
			PacketSendUtility.sendYellowMessage(player, "You joined the fast track server!");
			fastTrackBonus(player, false);
		}
	}
	
	/**
	 * Updates the bonus status for a specific {@link Player}.<br>
	 * This method toggles whether the player receives fast track benefits.
	 * @param player The {@code Player} object to modify.
	 * @param off Set to {@code true} to disable the bonus or {@code false} to enable it.
	 */
	public void fastTrackBonus(Player player, boolean off)
	{
	}
	
	/**
	 * Checks if a specific world type is designated as a PvP zone.<br>
	 * This method returns {@code true} for {@code BALAUREA} and {@code ABYSS}.
	 * @param wt The {@code WorldType} to check.
	 * @return {@code true} if the world is a PvP zone, otherwise {@code false}.
	 */
	public boolean isPvPZone(WorldType wt)
	{
		return (wt == WorldType.BALAUREA) || (wt == WorldType.ABYSS);
	}
	
	/**
	 * Checks if a specific world type is considered a normal zone.<br>
	 * This method returns {@code true} for {@code ASMODAE}, {@code ELYSEA}, or {@code NONE}.
	 * @param wt The {@code WorldType} to check.
	 * @return {@code true} if the zone is normal, otherwise {@code false}.
	 */
	public boolean isNormalZone(WorldType wt)
	{
		return (wt == WorldType.ASMODAE) || (wt == WorldType.ELYSEA) || (wt == WorldType.NONE);
	}
}
