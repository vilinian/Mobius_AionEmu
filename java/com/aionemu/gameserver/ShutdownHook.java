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
package com.aionemu.gameserver;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.ExitCode;
import com.aionemu.commons.utils.concurrent.RunnableStatsManager;
import com.aionemu.commons.utils.concurrent.RunnableStatsManager.SortBy;
import com.aionemu.gameserver.configs.main.ShutdownConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.PeriodicSaveService;
import com.aionemu.gameserver.services.player.PlayerLeaveWorldService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;

/**
 * This class handles the graceful shutdown procedure of the game server.<br>
 * It ensures that all active services and player data are saved before the application exits. It listens for termination signals to trigger a clean cleanup process.
 * @author lord_rex
 */
public class ShutdownHook extends Thread
{
	private static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);
	
	/**
	 * Retrieves the singleton instance of the {@link ShutdownHook}.<br>
	 * This method ensures that only one shutdown hook exists in the application.
	 * @return The global {@code ShutdownHook} instance.
	 */
	public static ShutdownHook getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void run()
	{
		if (ShutdownConfig.HOOK_MODE == 1)
		{
			shutdownHook(ShutdownConfig.HOOK_DELAY, ShutdownConfig.ANNOUNCE_INTERVAL, ShutdownMode.SHUTDOWN);
		}
		else if (ShutdownConfig.HOOK_MODE == 2)
		{
			shutdownHook(ShutdownConfig.HOOK_DELAY, ShutdownConfig.ANNOUNCE_INTERVAL, ShutdownMode.RESTART);
		}
	}
	
	public static enum ShutdownMode
	{
		NONE("terminating"),
		SHUTDOWN("shutting down"),
		RESTART("restarting");
		
		private final String text;
		
		private ShutdownMode(String text)
		{
			this.text = text;
		}
		
		public String getText()
		{
			return text;
		}
	}
	
	/**
	 * Sends a system message to all online players.<br>
	 * This informs them that the server will shut down in a specific amount of time.<br>
	 * It iterates through all {@link Player} objects in the {@link World}.
	 * @param seconds The number of seconds remaining until the shutdown.
	 */
	private void sendShutdownMessage(int seconds)
	{
		try
		{
			final Iterator<Player> onlinePlayers = World.getInstance().getPlayersIterator();
			if (!onlinePlayers.hasNext())
			{
				return;
			}
			while (onlinePlayers.hasNext())
			{
				final Player player = onlinePlayers.next();
				if ((player != null) && (player.getClientConnection() != null))
				{
					player.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.STR_SERVER_SHUTDOWN(String.valueOf(seconds)));
				}
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}
	}
	
	/**
	 * Updates the shutdown progress status for all online players.<br>
	 * This method iterates through every {@link Player} in the {@link World}.<br>
	 * It sets the {@code setInShutdownProgress} flag on each player controller.
	 * @param status The current shutdown state to apply, such as {@code true} or {@code false}.
	 */
	private void sendShutdownStatus(boolean status)
	{
		try
		{
			final Iterator<Player> onlinePlayers = World.getInstance().getPlayersIterator();
			if (!onlinePlayers.hasNext())
			{
				return;
			}
			while (onlinePlayers.hasNext())
			{
				final Player player = onlinePlayers.next();
				if ((player != null) && (player.getClientConnection() != null))
				{
					player.getController().setInShutdownProgress(status);
				}
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}
	}
	
	/**
	 * This method handles the sequence of events when the server shuts down.<br>
	 * It notifies players, saves data, and cleans up all active services.<br>
	 * The process follows a countdown based on the provided duration and interval.
	 * @param duration The total time in seconds to wait before shutting down.
	 * @param interval The number of seconds between each status update.
	 * @param mode The {@code ShutdownMode} which determines the shutdown message and exit code.
	 */
	private void shutdownHook(int duration, int interval, ShutdownMode mode)
	{
		for (int i = duration; i >= interval; i -= interval)
		{
			try
			{
				if (World.getInstance().getPlayersIterator().hasNext())
				{
					log.info("Runtime is " + mode.getText() + " in " + i + " seconds.");
					sendShutdownMessage(i);
					sendShutdownStatus(ShutdownConfig.SAFE_REBOOT);
				}
				else
				{
					log.info("Runtime is " + mode.getText() + " now ...");
					break; // fast exit.
				}
				
				if (i > interval)
				{
					sleep(interval * 1000);
				}
				else
				{
					sleep(i * 1000);
				}
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
		
		// Disconnect login server from game.
		LoginServer.getInstance().gameServerDisconnected();
		
		// Disconnect all players.
		Iterator<Player> onlinePlayers;
		onlinePlayers = World.getInstance().getPlayersIterator();
		while (onlinePlayers.hasNext())
		{
			final Player activePlayer = onlinePlayers.next();
			try
			{
				PlayerLeaveWorldService.startLeaveWorld(activePlayer);
			}
			catch (Exception e)
			{
				log.error("Error while saving player " + e.getMessage());
			}
		}
		
		log.info("All players are disconnected...");
		
		RunnableStatsManager.dumpClassStats(SortBy.AVG);
		PeriodicSaveService.getInstance().onShutdown();
		
		// Save game time.
		GameTimeManager.saveTime();
		
		// Shutdown of cron service
		CronService.getInstance().shutdown();
		
		// ThreadPoolManager shutdown
		ThreadPoolManager.getInstance().shutdown();
		
		// Do system exit.
		if (mode == ShutdownMode.RESTART)
		{
			Runtime.getRuntime().halt(ExitCode.CODE_RESTART);
		}
		else
		{
			Runtime.getRuntime().halt(ExitCode.CODE_NORMAL);
		}
		
		log.info("Runtime is " + mode.getText() + " now...");
	}
	
	/**
	 * Initiates the server shutdown sequence.<br>
	 * This method coordinates the timing and messaging for closing the game world.
	 * @param delay The number of seconds to wait before starting the final shutdown.
	 * @param announceInterval The frequency in seconds to send status updates to players.
	 * @param mode The {@code ShutdownMode} to determine how the server handles active connections.
	 */
	public void doShutdown(int delay, int announceInterval, ShutdownMode mode)
	{
		shutdownHook(delay, announceInterval, mode);
	}
	
	private static final class SingletonHolder
	{
		private static final ShutdownHook INSTANCE = new ShutdownHook();
	}
}
