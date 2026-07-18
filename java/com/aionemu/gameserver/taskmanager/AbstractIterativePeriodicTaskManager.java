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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.commons.utils.concurrent.RunnableStatsManager;

/**
 * Provides a base implementation for managing tasks that run periodically and iterate over a collection of items.<br>
 * This class helps handle repetitive logic across multiple entities or objects efficiently.
 * @author NB4L1
 * @param <T>
 */
public abstract class AbstractIterativePeriodicTaskManager<T> extends AbstractPeriodicTaskManager
{
	private final Set<T> startList = ConcurrentHashMap.newKeySet();
	private final Set<T> stopList = ConcurrentHashMap.newKeySet();
	private final Set<T> activeTasks = ConcurrentHashMap.newKeySet();
	
	/**
	 * Initializes the manager with a specific execution interval.<br>
	 * This constructor sets the base period for all periodic tasks.
	 * @param period The time interval between task executions.
	 */
	protected AbstractIterativePeriodicTaskManager(int period)
	{
		super(period);
	}
	
	/**
	 * Checks if a specific task is currently managed by this manager.<br>
	 * It returns {@code true} if the task is in the active or start list.<br>
	 * It returns {@code false} if the task is in the stop list.
	 * @param task The task to check.
	 * @return {@code true} if the task exists and is not stopped, otherwise {@code false}.
	 */
	public boolean hasTask(T task)
	{
		readLock();
		try
		{
			if (stopList.contains(task))
			{
				return false;
			}
			
			return activeTasks.contains(task) || startList.contains(task);
		}
		finally
		{
			readUnlock();
		}
	}
	
	/**
	 * Starts a new task in the manager.<br>
	 * This method adds the {@code task} to the active list.<br>
	 * It also ensures the {@code task} is removed from any stop lists.
	 * @param task The {@code T} object representing the task to start.
	 */
	public void startTask(T task)
	{
		writeLock();
		try
		{
			startList.add(task);
			
			stopList.remove(task);
		}
		finally
		{
			writeUnlock();
		}
	}
	
	/**
	 * Stops a specific task from running.<br>
	 * This method adds the {@code task} to the stop list.<br>
	 * It also removes the {@code task} from the start list.
	 * @param task The task to be stopped.
	 */
	public void stopTask(T task)
	{
		writeLock();
		try
		{
			stopList.add(task);
			
			startList.remove(task);
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
			activeTasks.addAll(startList);
			activeTasks.removeAll(stopList);
			
			startList.clear();
			stopList.clear();
		}
		finally
		{
			writeUnlock();
		}
		
		for (T task : activeTasks)
		{
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
