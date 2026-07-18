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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.concurrent.RunnableStatsManager;

/**
 * Provides a base implementation for managing periodic tasks using a First-In-First-Out (FIFO) queue.<br>
 * This class ensures that tasks of type {@code T} are executed in the order they were added.<br>
 * It extends {@link AbstractPeriodicTaskManager} to handle recurring execution logic.
 * @author lord_rex and MrPoke based on l2j-free engines.
 * @param <T>
 */
public abstract class AbstractFIFOPeriodicTaskManager<T> extends AbstractPeriodicTaskManager
{
	protected static final Logger log = LoggerFactory.getLogger(AbstractFIFOPeriodicTaskManager.class);
	private final Set<T> queue = new LinkedHashSet<>();
	private final Set<T> activeTasks = new LinkedHashSet<>();
	
	/**
	 * Creates a new instance of this task manager.<br>
	 * It initializes the periodic execution interval.
	 * @param period The time interval between each task execution.
	 */
	public AbstractFIFOPeriodicTaskManager(int period)
	{
		super(period);
	}
	
	/**
	 * Adds a new task to the internal queue.<br>
	 * This method ensures thread safety by using a write lock.<br>
	 * The added item will be processed during the next execution cycle.
	 * @param t The task object to be added to the queue.
	 */
	public void add(T t)
	{
		writeLock();
		try
		{
			queue.add(t);
		}
		finally
		{
			writeUnlock();
		}
	}
	
	@Override
	public void run()
	{
		writeLock();
		try
		{
			activeTasks.addAll(queue);
			
			queue.clear();
		}
		finally
		{
			writeUnlock();
		}
		
		for (Iterator<T> it = activeTasks.iterator(); it.hasNext();)
		{
			final T task = it.next();
			it.remove();
			final long begin = System.nanoTime();
			
			try
			{
				callTask(task);
			}
			catch (RuntimeException e)
			{
				log.warn("", e);
			}
			finally
			{
				RunnableStatsManager.handleStats(task.getClass(), getCalledMethodName(), System.nanoTime() - begin);
			}
		}
	}
	
	protected abstract void callTask(T task);
	
	protected abstract String getCalledMethodName();
}
