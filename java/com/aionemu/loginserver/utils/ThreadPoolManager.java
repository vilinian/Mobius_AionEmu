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
package com.aionemu.loginserver.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.concurrent.AionRejectedExecutionHandler;
import com.aionemu.commons.utils.concurrent.RunnableWrapper;
import com.aionemu.commons.utils.concurrent.ScheduledFutureWrapper;

/**
 * This class manages the lifecycle and configuration of {@code ThreadPoolExecutor} instances.<br>
 * It provides a centralized way to handle concurrent tasks across the login server.<br>
 * Use this class to ensure consistent thread management and resource allocation.
 * @author -Nemesiss-, NB4L1, MrPoke, lord_rex
 */
public final class ThreadPoolManager
{
	private static final Logger log = LoggerFactory.getLogger(ThreadPoolManager.class);
	public static final long MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING = 5000;
	private static final long MAX_DELAY = TimeUnit.NANOSECONDS.toMillis(Long.MAX_VALUE - System.nanoTime()) / 2;
	private final ScheduledThreadPoolExecutor scheduledPool;
	private final ThreadPoolExecutor instantPool;
	private final ThreadPoolExecutor longRunningPool;
	
	/**
	 * Private constructor for the {@link ThreadPoolManager} class.<br>
	 * This constructor initializes the internal thread pools and starts the deadlock detector.<br>
	 * It prevents other classes from creating new instances of this manager.
	 */
	private ThreadPoolManager()
	{
		final int threadpoolsize = 2 + (Runtime.getRuntime().availableProcessors() * 4);
		final int instantPoolSize = Math.max(1, threadpoolsize / 3);
		
		scheduledPool = new ScheduledThreadPoolExecutor(threadpoolsize - instantPoolSize);
		scheduledPool.setRejectedExecutionHandler(new AionRejectedExecutionHandler());
		scheduledPool.prestartAllCoreThreads();
		
		instantPool = new ThreadPoolExecutor(instantPoolSize, instantPoolSize, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100000));
		instantPool.setRejectedExecutionHandler(new AionRejectedExecutionHandler());
		instantPool.prestartAllCoreThreads();
		
		longRunningPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
		longRunningPool.setRejectedExecutionHandler(new AionRejectedExecutionHandler());
		longRunningPool.prestartAllCoreThreads();
		
		scheduleAtFixedRate(() -> purge(), 150000, 150000);
		
		log.info("ThreadPoolManager: Initialized with " + scheduledPool.getPoolSize() + " scheduler, " + instantPool.getPoolSize() + " instant, " + longRunningPool.getPoolSize() + " long running thread(s).");
	}
	
	/**
	 * Checks if the provided {@code delay} is within valid limits.<br>
	 * It ensures the value is not negative.<br>
	 * It caps the value at {@code MAX_DELAY}.
	 * @param delay The requested delay in milliseconds.
	 * @return The validated delay as a {@code long}.
	 */
	private long validate(long delay)
	{
		return Math.max(0, Math.min(MAX_DELAY, delay));
	}
	
	private static final class ThreadPoolRunnableWrapper extends RunnableWrapper
	{
		private ThreadPoolRunnableWrapper(Runnable runnable)
		{
			super(runnable, MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING);
		}
	}
	
	// ===========================================================================================
	/**
	 * Schedules a task to run after a specific delay.<br>
	 * The task is wrapped and executed by the internal {@code scheduledPool}.
	 * @param r The {@code Runnable} task to execute.
	 * @param delay The delay in milliseconds before starting the task.
	 * @return A {@link ScheduledFuture} representing the pending result of the task.
	 */
	public ScheduledFuture<?> schedule(Runnable r, long delay)
	{
		r = new ThreadPoolRunnableWrapper(r);
		delay = validate(delay);
		
		return new ScheduledFutureWrapper(scheduledPool.schedule(r, delay, TimeUnit.MILLISECONDS));
	}
	
	/**
	 * Schedules a task to be executed after a specific delay.<br>
	 * This method is used for handling visual effects.<br>
	 * It delegates the work to the {@code long)} method.
	 * @param r The {@code Runnable} task to execute.
	 * @param delay The time to wait before starting the task in milliseconds.
	 * @return A {@code ScheduledFuture<?>} representing the pending result of the task.
	 */
	public ScheduledFuture<?> scheduleEffect(Runnable r, long delay)
	{
		return schedule(r, delay);
	}
	
	// ===========================================================================================
	/**
	 * Schedules a task to run repeatedly at a fixed rate.<br>
	 * The first execution starts after the specified {@code delay}.<br>
	 * Subsequent executions occur every {@code period} milliseconds.
	 * @param r The {@code Runnable} task to execute.
	 * @param delay The initial delay before starting the task in milliseconds.
	 * @param period The time between successive executions in milliseconds.
	 * @return A {@link ScheduledFuture} representing the pending result of the task.
	 */
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long delay, long period)
	{
		r = new ThreadPoolRunnableWrapper(r);
		delay = validate(delay);
		period = validate(period);
		
		return new ScheduledFutureWrapper(scheduledPool.scheduleAtFixedRate(r, delay, period, TimeUnit.MILLISECONDS));
	}
	
	/**
	 * Schedules a task to run repeatedly at a fixed rate.<br>
	 * This method uses the {@code scheduledPool} to manage execution.<br>
	 * It provides a way to perform periodic actions with an initial delay.
	 * @param r The {@code Runnable} task to execute.
	 * @param delay The initial delay before starting the first execution.
	 * @param period The time interval between successive executions.
	 * @return A {@link ScheduledFuture} representing the pending result of the task.
	 */
	public ScheduledFuture<?> scheduleEffectAtFixedRate(Runnable r, long delay, long period)
	{
		return scheduleAtFixedRate(r, delay, period);
	}
	
	// ===========================================================================================
	/**
	 * Runs a task in the instant thread pool.<br>
	 * This method wraps the provided {@code Runnable} before execution.<br>
	 * It is suitable for quick tasks that should finish almost immediately.
	 * @param r The {@code Runnable} task to be executed.
	 */
	public void execute(Runnable r)
	{
		r = new ThreadPoolRunnableWrapper(r);
		
		instantPool.execute(r);
	}
	
	/**
	 * Runs a task using the default thread pool.<br>
	 * This method calls {@code execute} to process the provided task.
	 * @param r The {@code Runnable} task to be executed.
	 */
	public void executeTask(Runnable r)
	{
		execute(r);
	}
	
	/**
	 * Runs a task in the long-running thread pool.<br>
	 * This method wraps the {@code Runnable} before execution.<br>
	 * It is intended for tasks that take a significant amount of time to complete.
	 * @param r The {@code Runnable} task to be executed.
	 */
	public void executeLongRunning(Runnable r)
	{
		r = new RunnableWrapper(r);
		
		longRunningPool.execute(r);
	}
	
	// ===========================================================================================
	/**
	 * Submits a task for execution using the {@code instantPool}.<br>
	 * This method wraps the provided {@code Runnable} before submission.
	 * @param r The {@code Runnable} task to be executed.
	 * @return A {@code Future} representing the pending result of the task.
	 */
	public Future<?> submit(Runnable r)
	{
		r = new ThreadPoolRunnableWrapper(r);
		
		return instantPool.submit(r);
	}
	
	/**
	 * This method runs a task in the background.<br>
	 * It uses a specific pool for tasks that take a long time to finish.<br>
	 * The task is wrapped to ensure it handles errors correctly.
	 * @param r The {@code Runnable} task to be executed.
	 * @return A {@code Future} object representing the pending result of the task.
	 */
	public Future<?> submitLongRunning(Runnable r)
	{
		r = new RunnableWrapper(r);
		
		return longRunningPool.submit(r);
	}
	
	// ===========================================================================================
	/**
	 * Executes a specific packet task using the standard thread pool.<br>
	 * This method wraps the {@code pkt} and passes it to the {@code execute} method.
	 * @param pkt The {@code Runnable} task representing the packet to be processed.
	 */
	public void executeLsPacket(Runnable pkt)
	{
		execute(pkt);
	}
	
	/**
	 * Schedules a task to be executed after a specific delay.<br>
	 * This method wraps the {@code long)} logic for task management.
	 * @param r The {@code Runnable} task to execute.
	 * @param delay The time to wait before starting the task in milliseconds.
	 * @return A {@code ScheduledFuture<?>} representing the pending result of the task.
	 */
	public ScheduledFuture<?> scheduleTaskManager(Runnable r, long delay)
	{
		return schedule(r, delay);
	}
	
	/**
	 * Clears out finished tasks from the internal thread pools.<br>
	 * This method calls {@code purge()} on all managed executors.<br>
	 * It helps free up memory by removing completed jobs.
	 */
	public void purge()
	{
		scheduledPool.purge();
		instantPool.purge();
		longRunningPool.purge();
	}
	
	/**
	 * Shuts down all internal thread pools.<br>
	 * This method stops the {@code scheduledPool}, {@code instantPool}, and {@code longRunningPool}.<br>
	 * It waits for active tasks to complete before finishing.
	 */
	public void shutdown()
	{
		final long begin = System.currentTimeMillis();
		
		log.info("ThreadPoolManager: Shutting down.");
		log.info("\t... executing " + getTaskCount(scheduledPool) + " scheduled tasks.");
		log.info("\t... executing " + getTaskCount(instantPool) + " instant tasks.");
		log.info("\t... executing " + getTaskCount(longRunningPool) + " long running tasks.");
		
		scheduledPool.shutdown();
		instantPool.shutdown();
		longRunningPool.shutdown();
		
		boolean success = false;
		try
		{
			success = awaitTermination(5000);
			
			scheduledPool.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
			scheduledPool.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
			
			success |= awaitTermination(10000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		log.info("\t... success: " + success + " in " + (System.currentTimeMillis() - begin) + " msec.");
		log.info("\t... " + getTaskCount(scheduledPool) + " scheduled tasks left.");
		log.info("\t... " + getTaskCount(instantPool) + " instant tasks left.");
		log.info("\t... " + getTaskCount(longRunningPool) + " long running tasks left.");
	}
	
	/**
	 * Calculates the total number of tasks in a {@code ThreadPoolExecutor}.<br>
	 * It sums the size of the task queue and the count of active threads.
	 * @param tp The {@code ThreadPoolExecutor} to check.
	 * @return The total count of pending and active tasks.
	 */
	private int getTaskCount(ThreadPoolExecutor tp)
	{
		return tp.getQueue().size() + tp.getActiveCount();
	}
	
	/**
	 * Retrieves the current status of all thread pools.<br>
	 * This method collects metrics like active counts and queue sizes.<br>
	 * It provides a snapshot of the {@code scheduledPool}, {@code instantPool}, {@code longRunningPool}, and {@code workStealingPool}.
	 * @return A {@code List} of strings containing the formatted statistics.
	 */
	public List<String> getStats()
	{
		final List<String> list = new ArrayList<>();
		
		list.add("");
		list.add("Scheduled pool:");
		list.add("=================================================");
		list.add("\tgetActiveCount: ...... " + scheduledPool.getActiveCount());
		list.add("\tgetCorePoolSize: ..... " + scheduledPool.getCorePoolSize());
		list.add("\tgetPoolSize: ......... " + scheduledPool.getPoolSize());
		list.add("\tgetLargestPoolSize: .. " + scheduledPool.getLargestPoolSize());
		list.add("\tgetMaximumPoolSize: .. " + scheduledPool.getMaximumPoolSize());
		list.add("\tgetCompletedTaskCount: " + scheduledPool.getCompletedTaskCount());
		list.add("\tgetQueuedTaskCount: .. " + scheduledPool.getQueue().size());
		list.add("\tgetTaskCount: ........ " + scheduledPool.getTaskCount());
		list.add("");
		list.add("Instant pool:");
		list.add("=================================================");
		list.add("\tgetActiveCount: ...... " + instantPool.getActiveCount());
		list.add("\tgetCorePoolSize: ..... " + instantPool.getCorePoolSize());
		list.add("\tgetPoolSize: ......... " + instantPool.getPoolSize());
		list.add("\tgetLargestPoolSize: .. " + instantPool.getLargestPoolSize());
		list.add("\tgetMaximumPoolSize: .. " + instantPool.getMaximumPoolSize());
		list.add("\tgetCompletedTaskCount: " + instantPool.getCompletedTaskCount());
		list.add("\tgetQueuedTaskCount: .. " + instantPool.getQueue().size());
		list.add("\tgetTaskCount: ........ " + instantPool.getTaskCount());
		list.add("");
		list.add("Long running pool:");
		list.add("=================================================");
		list.add("\tgetActiveCount: ...... " + longRunningPool.getActiveCount());
		list.add("\tgetCorePoolSize: ..... " + longRunningPool.getCorePoolSize());
		list.add("\tgetPoolSize: ......... " + longRunningPool.getPoolSize());
		list.add("\tgetLargestPoolSize: .. " + longRunningPool.getLargestPoolSize());
		list.add("\tgetMaximumPoolSize: .. " + longRunningPool.getMaximumPoolSize());
		list.add("\tgetCompletedTaskCount: " + longRunningPool.getCompletedTaskCount());
		list.add("\tgetQueuedTaskCount: .. " + longRunningPool.getQueue().size());
		list.add("\tgetTaskCount: ........ " + longRunningPool.getTaskCount());
		list.add("");
		
		return list;
	}
	
	/**
	 * Waits for all thread pools to finish their current tasks.<br>
	 * It checks the status of multiple internal executors.<br>
	 * The method returns {@code true} if all tasks complete within the time limit.<br>
	 * It returns {@code false} if the timeout is reached before completion.
	 * @param timeoutInMillisec The maximum time to wait in milliseconds.
	 * @return {@code true} if all pools terminated, otherwise {@code false}.
	 * @throws InterruptedException
	 */
	private boolean awaitTermination(long timeoutInMillisec) throws InterruptedException
	{
		final long begin = System.currentTimeMillis();
		
		while ((System.currentTimeMillis() - begin) < timeoutInMillisec)
		{
			if ((!scheduledPool.awaitTermination(10, TimeUnit.MILLISECONDS) && (scheduledPool.getActiveCount() > 0)) || (!instantPool.awaitTermination(10, TimeUnit.MILLISECONDS) && (instantPool.getActiveCount() > 0)))
			{
				continue;
			}
			
			if (!longRunningPool.awaitTermination(10, TimeUnit.MILLISECONDS) && (longRunningPool.getActiveCount() > 0))
			{
				continue;
			}
			
			return true;
		}
		
		return false;
	}
	
	private static final class SingletonHolder
	{
		private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
	}
	
	/**
	 * Retrieves the global instance of the {@link ThreadPoolManager}.<br>
	 * This method uses the singleton pattern to ensure only one manager exists.<br>
	 * Use this to access shared thread pool services across the application.
	 * @return The single {@code ThreadPoolManager} instance.
	 */
	public static ThreadPoolManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
}
