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

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.player.FatigueService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This class handles the processing of fatigue-related tasks retrieved from the database.<br>
 * It manages player fatigue updates and interacts with the {@link FatigueService} to ensure correct game state.<br>
 * It is responsible for executing scheduled actions related to character exhaustion.
 * @author Alcpawnd
 */
public class FatigueHandler extends TaskFromDBHandler
{
	private int countDown;
	private static final Logger log = LoggerFactory.getLogger(FatigueHandler.class);
	private final Calendar calendar = Calendar.getInstance();
	
	/**
	 * Validates the current configuration and input parameters.<br>
	 * It checks if today is {@code Wednesday}.<br>
	 * It ensures that exactly one integer parameter is provided.
	 * @return {@code true} if all conditions are met, {@code false} otherwise.
	 */
	@Override
	public boolean isValid()
	{
		if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY)
		{
			return false;
		}
		
		if (params.length == 1)
		{
			try
			{
				countDown = Integer.parseInt(params[0]);
				
				return true;
			}
			catch (NumberFormatException e)
			{
				log.warn("Invalid parameters for FatigueHandler. Only valid integers allowed - not registered", e);
			}
		}
		
		log.warn("FatigueHandler has more than 1 parameters - not registered");
		return false;
	}
	
	/**
	 * Starts the fatigue reset process.<br>
	 * Sends a notification message to all players.<br>
	 * Schedules the {@code resetFatigue} method to run after the countdown.
	 */
	@Override
	public void trigger()
	{
		log.info("Task[" + taskId + "] launched : fatigue reset got started !");
		
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, "Automatic Task: The fatigue will be reset in " + countDown + " seconds !");
			}
		});
		
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				FatigueService.getInstance().resetFatigue();
			}
		}, countDown * 1000);
	}
	
}
