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
package com.aionemu.commons.services;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.services.cron.CronServiceException;
import com.aionemu.commons.services.cron.RunnableRunner;
import com.aionemu.commons.utils.GenericValidator;

/**
 * This service manages the scheduling and execution of background tasks using the {@code Quartz} framework.<br>
 * It allows for the registration of jobs that run automatically based on cron expressions.
 * @author SoulKeeper
 */
public final class CronService
{
	private static final Logger log = LoggerFactory.getLogger(CronService.class);
	
	/**
	 * We really should use some dependency injection (Spring?)<br>
	 * Current code is bloated, should be much cleaner
	 */
	private static CronService instance;
	
	private Scheduler scheduler;
	
	private Class<? extends RunnableRunner> runnableRunner;
	
	/**
	 * Retrieves the singleton instance of the {@link CronService}.<br>
	 * This method provides access to the shared service used for scheduling tasks.<br>
	 * Ensure that {@code initSingleton} has been called before using this instance.
	 * @return The global {@code CronService} instance.
	 */
	public static CronService getInstance()
	{
		return instance;
	}
	
	/**
	 * Initializes the singleton instance of {@link CronService}.<br>
	 * This method must be called before using {@code getInstance}.<br>
	 * It throws an exception if the service is already initialized.
	 * @param runableRunner The class type used for running tasks.
	 */
	public static synchronized void initSingleton(Class<? extends RunnableRunner> runableRunner)
	{
		if (instance != null)
		{
			throw new CronServiceException("CronService is already initialized");
		}
		
		final CronService cs = new CronService();
		cs.init(runableRunner);
		instance = cs;
	}
	
	/**
	 * Private constructor to prevent direct instantiation.<br>
	 * This class uses the Singleton pattern.<br>
	 * Use {@code getInstance} to access the service.
	 */
	private CronService()
	{
	}
	
	/**
	 * Initializes the {@link CronService} with a specific runner class.<br>
	 * This method sets up the internal {@code scheduler} and starts it.<br>
	 * It throws an exception if the provided class is {@code null}.
	 * @param runnableRunner The class of the {@link RunnableRunner} to use.
	 */
	public synchronized void init(Class<? extends RunnableRunner> runnableRunner)
	{
		if (scheduler != null)
		{
			return;
		}
		
		if (runnableRunner == null)
		{
			throw new CronServiceException("RunnableRunner class must be defined");
		}
		
		this.runnableRunner = runnableRunner;
		
		final Properties properties = new Properties();
		properties.setProperty("org.quartz.threadPool.threadCount", "1");
		
		try
		{
			scheduler = new StdSchedulerFactory(properties).getScheduler();
			scheduler.start();
		}
		catch (SchedulerException e)
		{
			throw new CronServiceException("Failed to initialize CronService", e);
		}
	}
	
	/**
	 * Shuts down the {@code CronService} and its internal scheduler.<br>
	 * This method stops all scheduled tasks and clears the service references.<br>
	 * It handles any {@code SchedulerException} during the shutdown process.
	 */
	public void shutdown()
	{
		Scheduler localScheduler;
		synchronized (this)
		{
			if (scheduler == null)
			{
				return;
			}
			
			localScheduler = scheduler;
			scheduler = null;
			runnableRunner = null;
		}
		
		try
		{
			localScheduler.shutdown(false);
		}
		catch (SchedulerException e)
		{
			log.error("Failed to shutdown CronService correctly", e);
		}
	}
	
	/**
	 * Schedules a task to run at specific times.<br>
	 * It uses a {@code cronExpression} to determine the timing.<br>
	 * This method wraps the {@code Runnable} into a background job.
	 * @param r The task to be executed.
	 * @param cronExpression The cron string defining the schedule.
	 */
	public void schedule(Runnable r, String cronExpression)
	{
		schedule(r, cronExpression, false);
	}
	
	/**
	 * Schedules a task to run based on a cron expression.<br>
	 * This method registers the {@code Runnable} with the internal scheduler.<br>
	 * It handles both standard and long-running tasks.
	 * @param r The {@code Runnable} task to be executed.
	 * @param cronExpression The cron string defining the schedule.
	 * @param longRunning Set to {@code true} if the task takes a long time to complete.
	 */
	public void schedule(Runnable r, String cronExpression, boolean longRunning)
	{
		try
		{
			final JobDataMap jdm = new JobDataMap();
			jdm.put(RunnableRunner.KEY_RUNNABLE_OBJECT, r);
			jdm.put(RunnableRunner.KEY_PROPERTY_IS_LONGRUNNING_TASK, longRunning);
			jdm.put(RunnableRunner.KEY_CRON_EXPRESSION, cronExpression);
			
			final String jobId = "Started at ms" + System.currentTimeMillis() + "; ns" + System.nanoTime();
			final JobKey jobKey = new JobKey("JobKey:" + jobId);
			final JobDetail jobDetail = JobBuilder.newJob(runnableRunner).usingJobData(jdm).withIdentity(jobKey).build();
			
			final CronScheduleBuilder csb = CronScheduleBuilder.cronSchedule(cronExpression);
			final CronTrigger trigger = TriggerBuilder.newTrigger().withSchedule(csb).build();
			
			scheduler.scheduleJob(jobDetail, trigger);
		}
		catch (Exception e)
		{
			throw new CronServiceException("Failed to start job", e);
		}
	}
	
	/**
	 * Stops a scheduled task.<br>
	 * This method finds the {@code JobDetail} associated with the provided {@code Runnable}.<br>
	 * It then calls {@code cancel} to stop it.
	 * @param r The {@code Runnable} task to cancel.
	 */
	public void cancel(Runnable r)
	{
		final Map<Runnable, JobDetail> map = getRunnables();
		final JobDetail jd = map.get(r);
		cancel(jd);
	}
	
	/**
	 * Stops and removes a specific job from the scheduler.<br>
	 * This method uses the {@code JobKey} from the provided {@code JobDetail}.<br>
	 * It will throw a {@link CronServiceException} if the deletion fails.
	 * @param jd The {@code JobDetail} to be cancelled.
	 */
	public void cancel(JobDetail jd)
	{
		if (jd == null)
		{
			return;
		}
		
		if (jd.getKey() == null)
		{
			throw new CronServiceException("JobDetail should have JobKey");
		}
		
		try
		{
			scheduler.deleteJob(jd.getKey());
		}
		catch (SchedulerException e)
		{
			throw new CronServiceException("Failed to delete Job", e);
		}
	}
	
	/**
	 * Retrieves all currently scheduled job details from the {@code scheduler}.<br>
	 * Returns an empty collection if no jobs are found or if the {@code scheduler} is null.<br>
	 * Throws a {@link CronServiceException} if an error occurs during retrieval.
	 * @return A {@code Collection} of all active {@link JobDetail} objects.
	 */
	protected Collection<JobDetail> getJobDetails()
	{
		if (scheduler == null)
		{
			return Collections.emptySet();
		}
		
		try
		{
			final Set<JobKey> keys = scheduler.getJobKeys(null);
			
			if (GenericValidator.isBlankOrNull(keys))
			{
				return Collections.emptySet();
			}
			
			final Set<JobDetail> result = new HashSet<>(keys.size());
			for (JobKey jk : keys)
			{
				result.add(scheduler.getJobDetail(jk));
			}
			
			return result;
		}
		catch (Exception e)
		{
			throw new CronServiceException("Can't get all active job details", e);
		}
	}
	
	/**
	 * Retrieves a mapping of all active {@code Runnable} objects to their corresponding {@code JobDetail}.<br>
	 * This method filters the internal job details to find those containing a valid runnable object.
	 * @return an unmodifiable map where keys are {@code Runnable} instances and values are {@code JobDetail} objects.
	 */
	public Map<Runnable, JobDetail> getRunnables()
	{
		final Collection<JobDetail> jobDetails = getJobDetails();
		if (GenericValidator.isBlankOrNull(jobDetails))
		{
			return Collections.emptyMap();
		}
		
		final Map<Runnable, JobDetail> result = new HashMap<>();
		for (JobDetail jd : jobDetails)
		{
			if (GenericValidator.isBlankOrNull(jd.getJobDataMap()))
			{
				continue;
			}
			
			if (jd.getJobDataMap().containsKey(RunnableRunner.KEY_RUNNABLE_OBJECT))
			{
				result.put((Runnable) jd.getJobDataMap().get(RunnableRunner.KEY_RUNNABLE_OBJECT), jd);
			}
		}
		
		return Collections.unmodifiableMap(result);
	}
	
	/**
	 * Retrieves all {@link Trigger} objects associated with a specific {@code JobDetail}.<br>
	 * This method looks up the triggers using the unique key of the provided job.
	 * @param jd The {@code JobDetail} to look up.
	 * @return A list of {@link Trigger} objects for the given job.
	 */
	public List<? extends Trigger> getJobTriggers(JobDetail jd)
	{
		return getJobTriggers(jd.getKey());
	}
	
	/**
	 * Retrieves all {@link Trigger} objects associated with a specific job key.<br>
	 * This method returns an empty list if the {@code scheduler} is not initialized.<br>
	 * It throws a {@link CronServiceException} if the underlying scheduler fails to find the triggers.
	 * @param jk The unique {@code JobKey} used to identify the job.
	 * @return A {@code List} of all triggers for the given job key.
	 */
	public List<? extends Trigger> getJobTriggers(JobKey jk)
	{
		if (scheduler == null)
		{
			return Collections.emptyList();
		}
		
		try
		{
			return scheduler.getTriggersOfJob(jk);
		}
		catch (SchedulerException e)
		{
			throw new CronServiceException("Can't get triggers for JobKey " + jk, e);
		}
	}
}
