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

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.configs.CommonsConfig;

/**
 * This class provides a wrapper for the {@code Executor} interface.<br>
 * It simplifies the execution of tasks by handling common logic or configurations.<br>
 * Use this class when you need to wrap standard execution behavior with additional utility features.
 * @author NB4L1
 */
public class ExecuteWrapper implements Executor
{
	private static final Logger log = LoggerFactory.getLogger(ExecuteWrapper.class);
	
	/**
	 * Runs the provided {@code Runnable} task.<br>
	 * This method uses the default maximum runtime settings.<br>
	 * It is a convenience wrapper for the overloaded {@code long)} method.
	 * @param runnable The task to be executed.
	 */
	@Override
	public void execute(Runnable runnable)
	{
		execute(runnable, Long.MAX_VALUE);
	}
	
	/**
	 * Executes a {@code Runnable} task and logs its performance.<br>
	 * It tracks the time taken to complete the task.<br>
	 * If the execution exceeds the specified limit, a warning is logged.
	 * @param runnable The task to be executed.
	 * @param maximumRuntimeInMillisecWithoutWarning The threshold in milliseconds for logging a warning.
	 */
	public static void execute(Runnable runnable, long maximumRuntimeInMillisecWithoutWarning)
	{
		final long begin = System.nanoTime();
		
		try
		{
			runnable.run();
		}
		catch (Throwable t)
		{
			log.warn("Exception in a Runnable execution:", t);
		}
		finally
		{
			final long runtimeInNanosec = System.nanoTime() - begin;
			final Class<? extends Runnable> clazz = runnable.getClass();
			
			if (CommonsConfig.RUNNABLESTATS_ENABLE)
			{
				RunnableStatsManager.handleStats(clazz, runtimeInNanosec);
			}
			
			final long runtimeInMillisec = TimeUnit.NANOSECONDS.toMillis(runtimeInNanosec);
			if (runtimeInMillisec > maximumRuntimeInMillisecWithoutWarning)
			{
				final StringBuilder tb = new StringBuilder();
				tb.append(clazz);
				tb.append(" - execution time: ");
				tb.append(runtimeInMillisec);
				tb.append("msec");
				log.warn(tb.toString());
			}
		}
	}
}
