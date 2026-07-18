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
package com.aionemu.commons.network.util;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.concurrent.PriorityThreadFactory;
import com.aionemu.commons.utils.concurrent.RunnableWrapper;

/**
 * This class manages the lifecycle and execution of thread pools for network operations.<br>
 * It provides a centralized way to handle concurrent tasks using {@link ThreadPoolExecutor}.<br>
 * Use this manager to ensure efficient resource allocation across the application.
 * @author -Nemesiss-, Rolandas
 */
public class ThreadPoolManager implements Executor
{
	/**
	 * PriorityThreadFactory creating new threads for ThreadPoolManager
	 */
	
	private static class SingletonHolder
	{
		protected static final ThreadPoolManager instance = new ThreadPoolManager();
	}
	
	/**
	 * Logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(ThreadPoolManager.class);
	
	/**
	 * Retrieves the global instance of the {@link ThreadPoolManager}.<br>
	 * This method uses the singleton pattern to ensure only one manager exists.<br>
	 * Use this to access shared thread pool services across the application.
	 * @return The single {@code ThreadPoolManager} instance.
	 */
	public static ThreadPoolManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * STPE for normal scheduled tasks
	 */
	private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	private final ScheduledExecutorService scheduledThreadPool;
	/**
	 * TPE for execution of gameserver client packets
	 */
	private final ThreadPoolExecutor generalPacketsThreadPoolExecutor;
	private final ExecutorService generalPacketsThreadPool;
	
	/**
	 * Private constructor for the {@link ThreadPoolManager} class.<br>
	 * This constructor initializes the internal thread pools and starts the deadlock detector.<br>
	 * It prevents other classes from creating new instances of this manager.
	 */
	private ThreadPoolManager()
	{
		new DeadLockDetector(60, DeadLockDetector.RESTART).start();
		
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4, new PriorityThreadFactory("ScheduledThreadPool", Thread.NORM_PRIORITY));
		scheduledThreadPool = scheduledThreadPoolExecutor;
		
		generalPacketsThreadPoolExecutor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
		generalPacketsThreadPool = generalPacketsThreadPoolExecutor;
	}
	
	/**
	 * Executes a given task in the packet thread pool.<br>
	 * This method wraps the {@code pkt} and submits it for processing.
	 * @param pkt The {@code Runnable} task to be executed.
	 */
	@Override
	public void execute(Runnable pkt)
	{
		generalPacketsThreadPool.execute(new RunnableWrapper(pkt));
	}
	
	/**
	 * Retrieves the executor service used for processing network packets.<br>
	 * This thread pool handles incoming packet tasks.
	 * @return the {@code ListeningExecutorService} instance.
	 */
	public ExecutorService getPacketsThreadPool()
	{
		return generalPacketsThreadPool;
	}
	
	/**
	 * Schedules a task to be executed after a specific delay.<br>
	 * The delay is measured in milliseconds.<br>
	 * If the provided {@code delay} is less than {@code 0}, it will be treated as {@code 0}.
	 * @param r The {@link Runnable} task to execute.
	 * @param delay The time to wait before running the task in milliseconds.
	 * @return A {@link ScheduledFuture} representing the pending result of the task, or {@code null} if execution is rejected.
	 */
	
	public ScheduledFuture<?> schedule(Runnable r, long delay)
	{
		try
		{
			if (delay < 0)
			{
				delay = 0;
			}
			
			return scheduledThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			return null; /* shutdown, ignore */
		}
	}
	
	/**
	 * Schedules a task to run repeatedly at a fixed rate.<br>
	 * The first execution starts after the {@code initial} delay.<br>
	 * Subsequent executions occur every {@code delay} milliseconds.
	 * @param r The task to be executed.
	 * @param initial The initial delay in milliseconds before starting.
	 * @param delay The period between successive executions in milliseconds.
	 * @return A {@link ScheduledFuture} representing the result of the task, or {@code null} if execution is rejected.
	 */
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay)
	{
		try
		{
			if (delay < 0)
			{
				delay = 0;
			}
			
			if (initial < 0)
			{
				initial = 0;
			}
			
			return scheduledThreadPool.scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			return null;
		}
	}
	
	/**
	 * Shuts down the internal thread pools.<br>
	 * This method stops both the {@code scheduledThreadPool} and {@code generalPacketsThreadPool}.<br>
	 * It waits for up to 2 seconds for all tasks to complete.
	 */
	public void shutdown()
	{
		try
		{
			scheduledThreadPool.shutdown();
			generalPacketsThreadPool.shutdown();
			scheduledThreadPool.awaitTermination(2, TimeUnit.SECONDS);
			generalPacketsThreadPool.awaitTermination(2, TimeUnit.SECONDS);
			log.info("All ThreadPools are now stopped.");
		}
		catch (InterruptedException e)
		{
			log.error("Can't shutdown ThreadPoolManager", e);
		}
	}
}
