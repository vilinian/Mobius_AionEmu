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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.CleaningConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This service provides functionality to remove data for inactive players from the database.<br>
 * It helps maintain database performance by cleaning up old records.
 * @author nrg
 */
public class DatabaseCleaningService
{
	private final Logger log = LoggerFactory.getLogger(DatabaseCleaningService.class);
	private final PlayerDAO dao = DAOManager.getDAO(PlayerDAO.class);
	
	// Singelton
	private static DatabaseCleaningService instance = new DatabaseCleaningService();
	
	// Workers
	private List<Worker> workers;
	
	// Starttime of service
	private long startTime;
	
	/**
	 * Private constructor for the {@link DatabaseCleaningService} class.<br>
	 * This prevents other classes from creating new instances.<br>
	 * It ensures that only one instance exists using the singleton pattern.
	 */
	private DatabaseCleaningService()
	{
		if (CleaningConfig.CLEANING_ENABLE)
		{
			runCleaning();
		}
	}
	
	/**
	 * Executes the routine to remove inactive player data from the database.<br>
	 * It checks if the configured period meets the minimum safety requirements.<br>
	 * If valid, it delegates the deletion tasks to multiple threads.
	 */
	private void runCleaning()
	{
		// Execution time
		log.info("DatabaseCleaningService: Executing database cleaning");
		startTime = System.currentTimeMillis();
		
		// getting period for deletion
		final int periodInDays = CleaningConfig.CLEANING_PERIOD;
		
		// only a security feature
		final int SECURITY_MINIMUM_PERIOD = 30;
		if (periodInDays > SECURITY_MINIMUM_PERIOD)
		{
			delegateToThreads(CleaningConfig.CLEANING_THREADS, dao.getInactiveAccounts(periodInDays, CleaningConfig.CLEANING_LIMIT));
			monitoringProcess();
		}
		else
		{
			log.warn("The configured days for database cleaning is to low. For security reasons the service will only execute with periods over 30 days!");
		}
	}
	
	/**
	 * Monitors the progress of the database cleaning task.<br>
	 * It waits until all workers are ready to process data.<br>
	 * This method logs the number of deleted characters every 10 seconds.
	 */
	private void monitoringProcess()
	{
		while (!allWorkersReady())
		{
			try
			{
				final int WORKER_CHECK_TIME = 10 * 1000;
				Thread.sleep(WORKER_CHECK_TIME);
				log.info("DatabaseCleaningService: Until now " + currentlyDeletedChars() + " chars deleted in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds!");
			}
			catch (InterruptedException ex)
			{
				log.error("DatabaseCleaningService: Got Interrupted!");
			}
		}
	}
	
	/**
	 * Checks if all worker threads are prepared for tasks.<br>
	 * It iterates through the {@code workers} list to verify their status.
	 * @return {@code true} if every worker is ready, otherwise {@code false}.
	 */
	private boolean allWorkersReady()
	{
		for (Worker w : workers)
		{
			if (!w._READY)
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Calculates the total number of characters deleted so far.<br>
	 * It sums the {@code deletedChars} count from all active {@code Worker} objects.
	 * @return The total count of deleted characters.
	 */
	private int currentlyDeletedChars()
	{
		int deletedChars = 0;
		for (Worker w : workers)
		{
			deletedChars += w.deletedChars;
		}
		
		return deletedChars;
	}
	
	/**
	 * This method distributes a set of IDs across multiple worker threads.<br>
	 * It creates new {@code Worker} objects if needed to match the requested thread count.<br>
	 * Each worker is then submitted to the {@link ThreadPoolManager} for execution.
	 * @param numberOfThreads The total number of threads to use for processing.
	 * @param idsToDelegate A {@code Set} of unique IDs to be processed by the workers.
	 */
	private void delegateToThreads(int numberOfThreads, Set<Integer> idsToDelegate)
	{
		workers = new ArrayList<>();
		log.info("DatabaseCleaningService: Executing deletion over " + numberOfThreads + " longrunning threads");
		
		// every id to another worker with maximum of n different workers
		final Iterator<Integer> i = idsToDelegate.iterator();
		for (int workerNo = 0; i.hasNext(); workerNo = ++workerNo % numberOfThreads)
		{
			if (workerNo >= workers.size())
			{
				workers.add(new Worker());
			}
			
			workers.get(workerNo).ids.add(i.next());
		}
		
		// get them working on our longrunning
		for (Worker w : workers)
		{
			ThreadPoolManager.getInstance().executeLongRunning(w);
		}
	}
	
	/**
	 * Provides access to the singleton instance of {@link DatabaseCleaningService}.<br>
	 * Use this method to get the shared service for cleaning inactive player data.
	 * @return The active {@code DatabaseCleaningService} instance.
	 */
	public static DatabaseCleaningService getInstance()
	{
		GameServer.log.info("[DatabaseCleaningService] started ...");
		return instance;
	}
	
	private class Worker implements Runnable
	{
		private final List<Integer> ids = new ArrayList<>();
		private int deletedChars = 0;
		private boolean _READY = false;
		
		@Override
		public void run()
		{
			for (int id : ids)
			{
				deletedChars += PlayerService.deleteAccountsCharsFromDB(id);
			}
			
			_READY = true;
		}
	}
}
