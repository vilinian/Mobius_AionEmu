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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FATIGUE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldType;

/**
 * Manages the fatigue system for players in the game world.<br>
 * It handles the calculation and synchronization of player fatigue values.<br>
 * This service ensures that {@link Player} objects receive updated {@code SM_FATIGUE} packets.
 * @author Alcapwnd
 */
public class FatigueService
{
	private int isFull = 0;
	private int fatigueRecover = 0;
	private int effectEnabled = 0;
	private int iconSet = 256;
	private String message = null;
	private final List<Future<?>> delays = new ArrayList<>();
	private final List<Player> players = new ArrayList<>();
	private static final Logger log = LoggerFactory.getLogger(FatigueService.class);
	private final Calendar calendar = Calendar.getInstance();
	
	/**
	 * Private constructor for the {@link FatigueService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * It logs a message to indicate that the service has started.
	 */
	private FatigueService()
	{
		GameServer.log.info("[FatigueService] started ...");
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It handles any initialization logic required for the player's session.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		if ((player == null) || (player.getLevel() < 10))
		{
			return;
		}
		
		switch (calendar.get(Calendar.DAY_OF_WEEK))
		{
			case Calendar.WEDNESDAY:
				if ((player.getCommonData().getFatigueReset() == 0) && (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY))
				{
					player.getCommonData().setFatigue(0);
					player.getCommonData().setFatigueRecover(1);
					player.getCommonData().setFatigueReset(1);
				}
				break;
			case Calendar.MONDAY:
			case Calendar.TUESDAY:
			case Calendar.THURSDAY:
			case Calendar.FRIDAY:
			case Calendar.SATURDAY:
			case Calendar.SUNDAY:
				if (player.getCommonData().getFatigueReset() == 1)
				{
					player.getCommonData().setFatigueReset(0);
				}
				break;
		}
		
		checkFatigueLost(player);
		
		if (player.getCommonData().getFatigue() == 100)
		{
			isFull = 1;
		}
		else
		{
			message = fatigueMessage(player.getCommonData().getFatigue());
		}
		
		if (isFull == 1)
		{
			fatigueRecover = 0/* player.getCommonData().getFatigueRecover() */;
			effectEnabled = 1;
		}
		else
		{
			fatigueRecover = 0; // only send if fatigue isFull
			effectEnabled = 0;
			PacketSendUtility.sendBrightYellowMessage(player, message);
		}
		
		if (CustomConfig.FATIGUE_SYSTEM_ENABLED)
		{
			iconSet = 256;
		}
		else
		{
			iconSet = 0;
		}
		
		PacketSendUtility.sendPacket(player, new SM_FATIGUE(effectEnabled, isFull, fatigueRecover, iconSet));
		players.add(player);
		log.info("[FatigueService] Added player " + player.getName() + " to fatigue update pool");
		load();
	}
	
	/**
	 * Removes a {@link Player} from the active tracking list.<br>
	 * This happens when a player logs out of the game.<br>
	 * It updates the internal pool to stop sending fatigue updates to that player.
	 * @param player The {@code Player} object who is logging out.
	 */
	public void onPlayerLogout(Player player)
	{
		players.remove(player);
		log.info("[FatigueService] Removed player " + player.getName() + " from fatigue update pool");
	}
	
	/**
	 * Checks if a {@link Player} has lost fatigue based on their offline time.<br>
	 * This method calculates the hours spent offline and reduces the fatigue value accordingly.<br>
	 * It ensures that the final fatigue value does not drop below {@code 0}.
	 * @param player The {@link Player} object to check for fatigue loss.
	 */
	public void checkFatigueLost(Player player)
	{
		final long lastOnline = player.getCommonData().getLastOnline().getTime();
		final long secondsOffline = (System.currentTimeMillis() / 1000) - (lastOnline / 1000);
		final double hours = secondsOffline / 3600d;
		
		final int currentFatigue = player.getCommonData().getFatigue();
		
		// TODO check this calculation! we need to figure out if this is ok
		if ((hours > 1) && (hours < 3))
		{
			player.getCommonData().setFatigue(currentFatigue - 5);
		}
		else if ((hours > 3) && (hours < 5))
		{
			player.getCommonData().setFatigue(currentFatigue - 10);
		}
		else if ((hours > 5) && (hours < 6))
		{
			player.getCommonData().setFatigue(currentFatigue - 20);
		}
		else if ((hours > 6) && (hours < 10))
		{
			player.getCommonData().setFatigue(currentFatigue - 40);
		}
		else if ((hours > 10) && (hours < 24))
		{
			player.getCommonData().setFatigue(currentFatigue - 50);
		}
		else if (hours > 24)
		{
			player.getCommonData().setFatigue(currentFatigue - 100);
		}
		
		if (player.getCommonData().getFatigue() < 0)
		{
			player.getCommonData().setFatigue(0);
		}
	}
	
	/**
	 * Reduces the fatigue recovery count for a specific player.<br>
	 * This method updates the {@code Player} data and triggers a fatigue check.<br>
	 * It ensures the value does not drop below {@code 0}.
	 * @param player The {@link Player} whose fatigue count will be modified.
	 * @param count The amount to subtract from the current recovery count.
	 */
	public void removeRecoverCount(Player player, int count)
	{
		if ((player == null) || (player.getLevel() < 10) || (player.getCommonData().getFatigueRecover() < count))
		{
			return;
		}
		
		player.getCommonData().setFatigueRecover(-count);
		if (player.getCommonData().getFatigueRecover() < 0)
		{
			player.getCommonData().setFatigueRecover(0);
		}
		
		player.getCommonData().setFatigue(0);
		checkFatigue(player);
	}
	
	/**
	 * Checks the current fatigue status of a {@link Player}.<br>
	 * It updates the local state and sends an {@code SM_FATIGUE} packet to the client.<br>
	 * This method only runs for players with a level of 10 or higher.
	 * @param player The {@code Player} object to check.
	 */
	public void checkFatigue(Player player)
	{
		if ((player == null) || (player.getLevel() < 10))
		{
			return;
		}
		
		if (player.getCommonData().getFatigue() == 100)
		{
			isFull = 1;
		}
		else
		{
			message = fatigueMessage(player.getCommonData().getFatigue());
		}
		
		if (isFull == 1)
		{
			fatigueRecover = 0/* player.getCommonData().getFatigueRecover() */;
			effectEnabled = 1;
		}
		else
		{
			fatigueRecover = 0; // only send if fatigue isFull
			effectEnabled = 0;
			PacketSendUtility.sendBrightYellowMessage(player, message);
		}
		
		if (CustomConfig.FATIGUE_SYSTEM_ENABLED)
		{
			iconSet = 256;
		}
		else
		{
			iconSet = 0;
		}
		
		PacketSendUtility.sendPacket(player, new SM_FATIGUE(effectEnabled, isFull, fatigueRecover, iconSet));
	}
	
	/**
	 * Initializes the fatigue tracking tasks for all active players.<br>
	 * Schedules a recurring task using {@link ThreadPoolManager} to update player fatigue.<br>
	 * The task runs every {@code 180000} milliseconds for eligible players.
	 */
	private void load()
	{
		for (Player player : players)
		{
			delays.add(ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
			{
				if ((player == null) || (player.getLevel() < 10) || (player.getWorldId() == 110010000) || (player.getWorldId() == 120010000))
				{// SANCTUM, PANDAEMONIUM
					return;
				}
				
				if ((player.getWorldType() == WorldType.ABYSS) || (player.getStore() != null))
				{
					return;
				}
				
				final int currentFatigue = player.getCommonData().getFatigue();
				player.getCommonData().setFatigue(currentFatigue + 1);
				if (player.getCommonData().getFatigue() > 100)
				{
					player.getCommonData().setFatigue(100);
				}
				
				checkFatigue(player);
				
			}, 180 * 1000, 180 * 1000)); // every 3 minutes check this
		}
	}
	
	/**
	 * Generates a status message based on the current fatigue level.<br>
	 * It checks if the {@code count} is 0 to return an empty message.<br>
	 * Otherwise, it returns a percentage string.
	 * @param count The current fatigue amount as an {@code int}.
	 * @return A {@code String} containing the fatigue status message.
	 */
	public String fatigueMessage(int count)
	{
		if (count == 0)
		{
			return "Your fatigue is empty";
		}
		
		return "Your fatigue reached " + count + " %";
	}
	
	/**
	 * Resets the fatigue values for all online players.<br>
	 * This method clears the internal player list and updates every {@link Player}.<br>
	 * It sets the fatigue to {@code 0} and resets recovery flags.<br>
	 * Finally, it calls the {@code load} method to refresh data.
	 */
	public void resetFatigue()
	{
		players.clear(); // need to clear it before start
		
		// Reset fatigue
		Iterator<Player> onlinePlayers;
		onlinePlayers = World.getInstance().getPlayersIterator();
		while (onlinePlayers.hasNext())
		{
			final Player activePlayer = onlinePlayers.next();
			try
			{
				activePlayer.getCommonData().setFatigue(0);
				activePlayer.getCommonData().setFatigueRecover(1);
				activePlayer.getCommonData().setFatigueReset(1);
				players.add(activePlayer);
			}
			catch (Exception e)
			{
				log.error("[FatigueService] Error while reset player fatigue " + e.getMessage());
			}
		}
		
		log.info("[FatigueService] All players fatigue are reseted...");
		load();
		log.info("[FatigueService] Fatigue got reseted...");
	}
	
	/**
	 * Gets the single instance of the {@link FatigueService}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the global fatigue management system.
	 * @return The active {@code FatigueService} instance.
	 */
	public static FatigueService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final FatigueService instance = new FatigueService();
	}
}
