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
package com.aionemu.gameserver.utils.gametime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.ServerVariablesDAO;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class manages the progression of in-game time.<br>
 * It handles synchronization and updates for the game world clock.
 * @author Ben
 */
public class GameTimeManager
{
	private static final Logger log = LoggerFactory.getLogger(GameTimeManager.class);
	private static GameTime instance;
	private static GameTimeUpdater updater;
	private static boolean clockStarted = false;
	
	static
	{
		final ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		instance = new GameTime(dao.load("time"));
	}
	
	/**
	 * Retrieves the current game time instance.<br>
	 * This method returns the singleton {@code GameTime} object managed by this class.
	 * @return The current {@link GameTime} instance.
	 */
	public static GameTime getGameTime()
	{
		return instance;
	}
	
	/**
	 * Starts the internal game time clock.<br>
	 * This method initializes the {@code GameTimeUpdater}.<br>
	 * It schedules the updater to run every 5000 milliseconds.<br>
	 * It will throw an {@code IllegalStateException} if the clock is already running.
	 */
	public static void startClock()
	{
		if (clockStarted)
		{
			throw new IllegalStateException("[GameTimeService] Clock is already started");
		}
		
		updater = new GameTimeUpdater(getGameTime());
		ThreadPoolManager.getInstance().scheduleAtFixedRate(updater, 0, 5000);
		
		clockStarted = true;
	}
	
	/**
	 * Saves the current game time to the database.<br>
	 * This method uses {@link ServerVariablesDAO} to store the value.
	 * @return {@code true} if the save was successful, {@code false} otherwise.
	 */
	public static boolean saveTime()
	{
		log.debug("Game time saved...");
		return DAOManager.getDAO(ServerVariablesDAO.class).store("time", getGameTime().getTime());
	}
	
	/**
	 * Updates the current game time to a new value.<br>
	 * This method resets the {@code instance} and restarts the clock.<br>
	 * It also clears all tasks from the {@link ThreadPoolManager}.
	 * @param time The new time value to set.
	 */
	public static void reloadTime(int time)
	{
		ThreadPoolManager.getInstance().purge();
		instance = new GameTime(time);
		
		clockStarted = false;
		
		startClock();
		log.info("[GameTimeService] Game time changed by admin and clock restarted...");
	}
}
