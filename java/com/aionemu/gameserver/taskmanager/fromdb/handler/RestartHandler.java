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
package com.aionemu.gameserver.taskmanager.fromdb.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ShutdownHook;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Handles the logic for restarting the game server from a database task.<br>
 * It coordinates with {@link World} and other components to ensure a clean shutdown and reboot.
 * @author Divinity, nrg
 */
public class RestartHandler extends TaskFromDBHandler
{
	private static final Logger log = LoggerFactory.getLogger(RestartHandler.class);
	private int countDown;
	private int announceInterval;
	private int warnCountDown;
	
	/**
	 * Validates the configuration parameters for this handler.<br>
	 * It checks if there are exactly {@code 3} parameters provided.<br>
	 * It also ensures that all parameters can be parsed as integers.
	 * @return {@code true} if the parameters are valid, {@code false} otherwise.
	 */
	@Override
	public boolean isValid()
	{
		if (params.length == 3)
		{
			try
			{
				countDown = Integer.parseInt(params[0]);
				announceInterval = Integer.parseInt(params[1]);
				warnCountDown = Integer.parseInt(params[2]);
				
				return true;
			}
			catch (NumberFormatException e)
			{
				log.warn("Invalid parameters for RestartHandler. Only valid integers allowed - not registered", e);
			}
		}
		
		log.warn("RestartHandler has more or less than 3 parameters - not registered");
		return false;
	}
	
	/**
	 * Starts the server restart process.<br>
	 * Sends a warning message to all players in the {@link World}.<br>
	 * Schedules the {@code int, ShutdownHook.ShutdownMode)} method to run after the countdown.
	 */
	@Override
	public void trigger()
	{
		log.info("Task[" + taskId + "] launched : restarting the server !");
		
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, "Automatic Task: The server will restart in " + warnCountDown + " seconds ! Please find a safe place and disconnect your character.");
			}
		});
		
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				ShutdownHook.getInstance().doShutdown(countDown, announceInterval, ShutdownHook.ShutdownMode.RESTART);
			}
		}, warnCountDown * 1000);
	}
}
