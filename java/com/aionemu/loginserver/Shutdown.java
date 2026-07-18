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
package com.aionemu.loginserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.ExitCode;
import com.aionemu.loginserver.configs.SvStatsConfig;
import com.aionemu.loginserver.dao.SvStatsDAO;
import com.aionemu.loginserver.network.NetConnector;
import com.aionemu.loginserver.utils.ThreadPoolManager;

/**
 * Handles the graceful shutdown procedure for the login server.<br>
 * It ensures that all services and connections are closed properly before the application exits.
 * @author -Nemesiss-, nrg
 */
public class Shutdown extends Thread
{
	/**
	 * Logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(Shutdown.class);
	/**
	 * Instance of Shutdown.
	 */
	private static Shutdown instance = new Shutdown();
	/**
	 * Indicates wether the loginserver should shut dpwn or only restart
	 */
	private static boolean restartOnly = false;
	
	/**
	 * Sets whether the login server should perform a full shutdown or just a restart.<br>
	 * This updates the {@code restartOnly} flag used by the {@code run} method.
	 * @param restartOnly Set to {@code true} for a restart, or {@code false} for a full shutdown.
	 */
	public void setRestartOnly(boolean restartOnly)
	{
		Shutdown.restartOnly = restartOnly;
	}
	
	/**
	 * Provides the global access point for the {@link Shutdown} class.<br>
	 * This method follows the singleton pattern to return the single existing instance.
	 * @return The singleton instance of {@code Shutdown}.
	 */
	public static Shutdown getInstance()
	{
		return instance;
	}
	
	@Override
	public void run()
	{
		try
		{
			NetConnector.getInstance().shutdown();
		}
		catch (Throwable t)
		{
			log.error("Can't shutdown NetConnector", t);
		}
		/* Shuting down DB connections */
		try
		{
			DatabaseFactory.shutdown();
		}
		catch (Throwable t)
		{
			log.error("Can't shutdown DatabaseFactory", t);
		}
		
		// shutdown cron service prior to threadpool shutdown
		CronService.getInstance().shutdown();
		
		/* Shuting down threadpools */
		try
		{
			ThreadPoolManager.getInstance().shutdown();
		}
		catch (Throwable t)
		{
			log.error("Can't shutdown ThreadPoolManager", t);
		}
		
		// Do system exit
		if (restartOnly)
		{
			Runtime.getRuntime().halt(ExitCode.CODE_RESTART);
			if (SvStatsConfig.SVSTATS_ENABLE)
			{
				DAOManager.getDAO(SvStatsDAO.class).update_SvStats_All_Offline(0, 0);
			}
		}
		else
		{
			Runtime.getRuntime().halt(ExitCode.CODE_NORMAL);
			if (SvStatsConfig.SVSTATS_ENABLE)
			{
				DAOManager.getDAO(SvStatsDAO.class).update_SvStats_All_Offline(0, 0);
			}
		}
	}
}
