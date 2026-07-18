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
package com.aionemu.commons.utils.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.aionemu.commons.network.util.ThreadUncaughtExceptionHandler;

/**
 * A custom implementation of {@link ThreadFactory} used to create threads with specific priorities.<br>
 * It helps manage thread execution order within a concurrent environment.
 * @author -Nemesiss-
 */
public class PriorityThreadFactory implements ThreadFactory
{
	/**
	 * Priority of new threads
	 */
	private final int prio;
	/**
	 * Thread group name
	 */
	private final String name;
	
	/*
	 * Default pool for the thread group, can be null for default
	 */
	private ExecutorService threadPool;
	
	/**
	 * Number of created threads
	 */
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	/**
	 * ThreadGroup for created threads
	 */
	private final ThreadGroup group;
	
	/**
	 * Creates a new instance of {@link PriorityThreadFactory}.<br>
	 * This factory will assign a specific name and priority to every thread it creates.
	 * @param name The name to give to the thread group.
	 * @param prio The priority level for the threads.
	 */
	public PriorityThreadFactory(final String name, int prio)
	{
		this.prio = prio;
		this.name = name;
		group = new ThreadGroup(this.name);
	}
	
	/**
	 * Creates a new {@link PriorityThreadFactory} with a default priority.<br>
	 * This constructor sets the thread priority to {@code Thread.NORM_PRIORITY}.<br>
	 * It also assigns a default pool for the thread group.
	 * @param name The name to give to the threads created by this factory.
	 * @param defaultPool The {@link ExecutorService} to use as the default pool, or {@code null} for none.
	 */
	public PriorityThreadFactory(final String name, ExecutorService defaultPool)
	{
		this(name, Thread.NORM_PRIORITY);
		setDefaultPool(defaultPool);
	}
	
	/**
	 * Sets the default {@code ExecutorService} for this thread factory.<br>
	 * This value is used when a specific pool is not provided.<br>
	 * You can pass {@code null} to use the system default.
	 * @param pool The {@code ExecutorService} to use as the default pool.
	 */
	protected void setDefaultPool(ExecutorService pool)
	{
		threadPool = pool;
	}
	
	/**
	 * Retrieves the default {@code ExecutorService} for this thread group.<br>
	 * It returns {@code null} if no custom pool has been set.
	 * @return The {@code ExecutorService} used as the default pool.
	 */
	protected ExecutorService getDefaultPool()
	{
		return threadPool;
	}
	
	/**
	 * Creates and configures a new {@link Thread}.<br>
	 * This method sets the thread name, priority, and exception handler.
	 * @param r The {@code Runnable} task to be executed by the new thread.
	 * @return A newly created {@code Thread} instance.
	 */
	@Override
	public Thread newThread(Runnable r)
	{
		final Thread t = new Thread(group, r);
		t.setName(name + "-" + threadNumber.getAndIncrement());
		t.setPriority(prio);
		t.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
		return t;
	}
}
