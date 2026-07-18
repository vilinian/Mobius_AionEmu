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

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles tasks that are rejected by a {@link ThreadPoolExecutor}.<br>
 * It provides custom logic for managing execution failures when the thread pool is full.
 * @author NB4L1
 */
public final class AionRejectedExecutionHandler implements RejectedExecutionHandler
{
	private static final Logger log = LoggerFactory.getLogger(AionRejectedExecutionHandler.class);
	
	/**
	 * Handles tasks that are rejected by the {@link ThreadPoolExecutor}.<br>
	 * It logs a warning if the executor is not shut down.<br>
	 * The task is then executed based on the current thread priority.
	 * @param r The {@code Runnable} task that was rejected.
	 * @param executor The {@code ThreadPoolExecutor} that rejected the task.
	 */
	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
	{
		if (executor.isShutdown())
		{
			return;
		}
		
		log.warn(r + " from " + executor, new RejectedExecutionException());
		
		if (Thread.currentThread().getPriority() > Thread.NORM_PRIORITY)
		{
			new Thread(r).start();
		}
		else
		{
			r.run();
		}
	}
}
