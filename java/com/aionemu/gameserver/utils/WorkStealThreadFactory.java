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

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.concurrent.PriorityThreadFactory;

/**
 * This factory creates threads specifically for use with a {@link ForkJoinPool}.<br>
 * It allows the system to implement work-stealing algorithms efficiently.<br>
 * It extends {@link PriorityThreadFactory} to maintain thread priority settings.
 * @author Rolandas
 */
public class WorkStealThreadFactory extends PriorityThreadFactory implements ForkJoinWorkerThreadFactory
{
	/**
	 * Creates a new {@link WorkStealThreadFactory} with a custom name prefix.<br>
	 * This factory is used to create threads for the {@link ForkJoinPool}.<br>
	 * The threads will be created with {@code Thread.NORM_PRIORITY}.
	 * @param namePrefix The string to prepend to the names of the new threads.
	 */
	public WorkStealThreadFactory(String namePrefix)
	{
		super(namePrefix, Thread.NORM_PRIORITY);
	}
	
	/**
	 * Sets the default {@link ForkJoinPool} used by this factory.<br>
	 * If the provided {@code pool} is {@code null}, it defaults to {@code ForkJoinPool#commonPool()}.<br>
	 * This method updates the internal state of the thread factory.
	 * @param pool The {@link ForkJoinPool} to use as the default.
	 */
	public void setDefaultPool(ForkJoinPool pool)
	{
		if (pool == null)
		{
			pool = ForkJoinPool.commonPool();
		}
		
		super.setDefaultPool(pool);
	}
	
	/**
	 * Retrieves the default {@link ForkJoinPool} used by this factory.<br>
	 * This method calls the {@code getDefaultPool()} method of the parent class.
	 * @return The current {@code ForkJoinPool} instance.
	 */
	@Override
	public ForkJoinPool getDefaultPool()
	{
		return (ForkJoinPool) super.getDefaultPool();
	}
	
	/**
	 * Creates a new {@link ForkJoinWorkerThread} for the given pool.<br>
	 * This method is used by the {@code ForkJoinPool} to generate worker threads.<br>
	 * It returns a new instance of {@code WorkStealThread}.
	 * @param pool The {@code ForkJoinPool} that will manage the new thread.
	 * @return A new {@code ForkJoinWorkerThread} instance.
	 */
	@Override
	public ForkJoinWorkerThread newThread(ForkJoinPool pool)
	{
		return new WorkStealThread(pool);
	}
	
	private static class WorkStealThread extends ForkJoinWorkerThread
	{
		private static final Logger log = LoggerFactory.getLogger(WorkStealThread.class);
		
		public WorkStealThread(ForkJoinPool pool)
		{
			super(pool);
		}
		
		@Override
		protected void onStart()
		{
			super.onStart();
		}
		
		@Override
		protected void onTermination(Throwable exception)
		{
			if (exception != null)
			{
				log.error("Error - Thread: " + getName() + " terminated abnormaly: " + exception);
			}
			
			super.onTermination(exception);
		}
	}
}
