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
package com.aionemu.gameserver.taskmanager;

import java.text.ParseException;
import java.util.Date;

import org.quartz.CronExpression;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.dao.ServerVariablesDAO;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class serves as a base for all scheduled tasks in the game server.<br>
 * It provides the foundation for executing logic at specific intervals using {@link CronExpression}.<br>
 * Subclasses should implement the core logic to be executed by the {@link CronService}.
 * @author Rolandas
 */
public abstract class AbstractCronTask implements Runnable
{
	private final String cronExpressionString;
	private final CronExpression runExpression;
	private int runTime;
	private final long period;
	
	/**
	 * Retrieves the start time of the task.<br>
	 * The value is stored in seconds.
	 * @return The {@code int} value representing the run time.
	 */
	public int getRunTime()
	{
		return runTime;
	}
	
	/**
	 * The same as a milliseconds left, but any extended class may specify a little delay. if delay is not needed then it's simple "runTime" minus "now" function
	 * @return
	 */
	abstract protected long getRunDelay();
	
	/**
	 * Performs initial setup for the house maintenance task.<br>
	 * This method logs the start of the initialization process.
	 */
	protected void preInit()
	{
	}
	
	/**
	 * Performs initialization tasks after the service is created.<br>
	 * It parses the registration end expression into a {@code CronExpression}.<br>
	 * It also loads the {@code auctionProlonged} value from the database.
	 */
	protected void postInit()
	{
	}
	
	/**
	 * Retrieves the raw cron expression string.<br>
	 * This value is used to define the task schedule.
	 * @return The {@code String} representation of the cron expression.
	 */
	public String getCronExpressionString()
	{
		return cronExpressionString;
	}
	
	/**
	 * Variable name of the task start time stored in the server_variables DB table
	 * @return
	 */
	abstract protected String getServerTimeVariable();
	
	/**
	 * Retrieves the time interval for this task.<br>
	 * This value represents the duration between executions.
	 * @return The period as a {@code long}.
	 */
	public long getPeriod()
	{
		return period;
	}
	
	/**
	 * Prepares the task before it starts running.<br>
	 * This method calls {@code updateMaintainedHouses} to refresh the house list.<br>
	 * It also logs the number of houses being maintained to the server log.
	 */
	protected void preRun()
	{
	}
	
	/**
	 * The main execution code goes here
	 */
	abstract protected void executeTask();
	
	/**
	 * Is the task allowed to run on its initialization (if runDelay = 0) or only at times defined by cron
	 * @return
	 */
	abstract protected boolean canRunOnInit();
	
	/**
	 * Performs cleanup actions after a task execution.<br>
	 * This method resets the {@code timeProlonged} variable to {@code 0}.<br>
	 * It then saves this value to the database using {@link ServerVariablesDAO}.
	 */
	protected void postRun()
	{
	}
	
	/**
	 * Initializes a new task with a specific schedule.<br>
	 * This constructor parses the provided cron string and sets up the initial run time.<br>
	 * It also calculates the execution period and schedules the first run.
	 * @param cronExpression The cron expression used to define the task schedule.
	 * @throws ParseException If the provided {@code cronExpression} is not a valid format.
	 */
	public AbstractCronTask(String cronExpression) throws ParseException
	{
		if (cronExpression == null)
		{
			throw new NullPointerException("cronExpressionString");
		}
		
		cronExpressionString = cronExpression;
		
		final ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		runTime = dao.load(getServerTimeVariable());
		
		preInit();
		runExpression = new CronExpression(cronExpressionString);
		final Date nextDate = runExpression.getTimeAfter(new Date());
		final Date nextAfterDate = runExpression.getTimeAfter(nextDate);
		period = nextAfterDate.getTime() - nextDate.getTime();
		postInit();
		
		if (getRunDelay() == 0)
		{
			if (canRunOnInit())
			{
				ThreadPoolManager.getInstance().schedule(this, 0);
			}
			else
			{
				saveNextRunTime();
			}
		}
		
		scheduleNextRun();
	}
	
	/**
	 * Schedules the next execution time for this task.<br>
	 * It uses the {@code cronExpressionString} to register with the {@link CronService}.
	 */
	private void scheduleNextRun()
	{
		CronService.getInstance().schedule(this, cronExpressionString, true);
	}
	
	/**
	 * Calculates the next execution time for this task.<br>
	 * It updates the {@code runTime} field based on the {@code runExpression}.<br>
	 * The new value is saved to the database using {@link ServerVariablesDAO}.
	 */
	private void saveNextRunTime()
	{
		final Date nextDate = runExpression.getTimeAfter(new Date());
		final ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		runTime = (int) (nextDate.getTime() / 1000);
		dao.store(getServerTimeVariable(), runTime);
	}
	
	@Override
	public void run()
	{
		if (getRunDelay() > 0)
		{
			ThreadPoolManager.getInstance().schedule(this, getRunDelay());
		}
		else
		{
			preRun();
			
			executeTask();
			saveNextRunTime();
			
			postRun();
		}
	}
}
