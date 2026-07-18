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
package com.aionemu.gameserver.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.util.ThreadUncaughtExceptionHandler;
import com.aionemu.commons.utils.concurrent.AionRejectedExecutionHandler;
import com.aionemu.commons.utils.concurrent.PriorityThreadFactory;
import com.aionemu.commons.utils.concurrent.RunnableWrapper;
import com.aionemu.gameserver.configs.main.ThreadConfig;

/**
 * Manages the lifecycle and execution of various thread pools used across the game server.<br>
 * It provides a centralized way to handle concurrent tasks using {@link ThreadPoolExecutor}.<br>
 * This class ensures that background operations are executed efficiently according to the {@code ThreadConfig} settings.
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
	private final ForkJoinPool workStealingPool;
	
	/**
	 * Private constructor for the {@link ThreadPoolManager} class.<br>
	 * This constructor initializes the internal thread pools and starts the deadlock detector.<br>
	 * It prevents other classes from creating new instances of this manager.
	 */
	private ThreadPoolManager()
	{
		final int instantPoolSize = Math.max(1, ThreadConfig.BASE_THREAD_POOL_SIZE) * Runtime.getRuntime().availableProcessors();
		
		instantPool = new ThreadPoolExecutor(instantPoolSize, instantPoolSize, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100000), new PriorityThreadFactory("InstantPool", ThreadConfig.USE_PRIORITIES ? 7 : Thread.NORM_PRIORITY));
		instantPool.setRejectedExecutionHandler(new AionRejectedExecutionHandler());
		instantPool.prestartAllCoreThreads();
		
		scheduledPool = new ScheduledThreadPoolExecutor(Math.max(1, ThreadConfig.EXTRA_THREAD_PER_CORE) * Runtime.getRuntime().availableProcessors());
		scheduledPool.setRejectedExecutionHandler(new AionRejectedExecutionHandler());
		scheduledPool.prestartAllCoreThreads();
		
		longRunningPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		longRunningPool.setRejectedExecutionHandler(new AionRejectedExecutionHandler());
		longRunningPool.prestartAllCoreThreads();
		
		final WorkStealThreadFactory forkJoinThreadFactory = new WorkStealThreadFactory("ForkJoinPool");
		workStealingPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), forkJoinThreadFactory, new ThreadUncaughtExceptionHandler(), true);
		forkJoinThreadFactory.setDefaultPool(workStealingPool);
		
		final Thread maintainThread = new Thread(() -> purge(), "ThreadPool Purge Task");
		
		maintainThread.setDaemon(true);
		scheduleAtFixedRate(maintainThread, 150000, 150000);
		
		log.info("ThreadPoolManager: Initialized with " + scheduledPool.getPoolSize() + " scheduler, " + instantPool.getPoolSize() + " instant, " + longRunningPool.getPoolSize() + " long running, and forking " + workStealingPool.getPoolSize() + " thread(s).");
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
			super(runnable, ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING);
		}
	}
	
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
		return scheduledPool.schedule(r, delay, TimeUnit.MILLISECONDS);
	}
	
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
		return scheduledPool.scheduleAtFixedRate(r, delay, period, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Retrieves the internal {@link ForkJoinPool} used for work-stealing tasks.<br>
	 * This pool is managed by the {@code ThreadPoolManager}.
	 * @return The active {@code ForkJoinPool} instance.
	 */
	public ForkJoinPool getForkingPool()
	{
		return workStealingPool;
	}
	
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
	 * Clears out finished tasks from the internal thread pools.<br>
	 * This method calls {@code purge()} on all managed executors.<br>
	 * It helps free up memory by removing completed jobs.
	 */
	public void purge()
	{
		scheduledPool.purge();
		instantPool.purge();
		longRunningPool.purge();
		// workStealingPool is already maintaining needed threads
	}
	
	/**
	 * Shuts down all internal thread pools.<br>
	 * This method stops the {@code scheduledPool}, {@code instantPool}, {@code longRunningPool}, and {@code workStealingPool}.<br>
	 * It waits for tasks to complete before forcing a shutdown on the remaining pools.
	 */
	public void shutdown()
	{
		final long begin = System.currentTimeMillis();
		
		log.info("ThreadPoolManager: Shutting down.");
		log.info("\t... executing " + getTaskCount(scheduledPool) + " scheduled tasks.");
		log.info("\t... executing " + getTaskCount(instantPool) + " instant tasks.");
		log.info("\t... executing " + getTaskCount(longRunningPool) + " long running tasks.");
		log.info("\t... " + (workStealingPool.getQueuedTaskCount() + workStealingPool.getQueuedSubmissionCount()) + " forking tasks left.");
		
		scheduledPool.shutdown();
		instantPool.shutdown();
		longRunningPool.shutdown();
		workStealingPool.shutdown();
		
		boolean success = false;
		try
		{
			success |= awaitTermination(5000);
			
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
		log.info("\t... " + (workStealingPool.getQueuedTaskCount() + workStealingPool.getQueuedSubmissionCount()) + " forking tasks left.");
		workStealingPool.shutdownNow();
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
		list.add("Work forking pool:");
		list.add("=================================================");
		list.add("\tgetActiveCount: ...... " + workStealingPool.getActiveThreadCount());
		list.add("\tgetPoolSize: ......... " + workStealingPool.getPoolSize());
		list.add("\tgetStealCount: ........" + workStealingPool.getStealCount());
		list.add("\tgetQueuedTaskCount: .. " + workStealingPool.getQueuedTaskCount());
		list.add("\tgetRunningThreadCount: " + workStealingPool.getRunningThreadCount());
		
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
			
			if ((!workStealingPool.awaitTermination(10, TimeUnit.MILLISECONDS) && (workStealingPool.getActiveThreadCount() > 0)) || (!longRunningPool.awaitTermination(10, TimeUnit.MILLISECONDS) && (longRunningPool.getActiveCount() > 0)))
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
