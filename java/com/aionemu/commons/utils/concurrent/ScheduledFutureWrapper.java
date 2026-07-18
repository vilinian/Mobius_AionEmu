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

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class provides a wrapper around the {@code ScheduledFuture} interface.<br>
 * It is used to simplify handling and interacting with scheduled tasks in concurrent environments.
 * @author NB4L1
 */
public final class ScheduledFutureWrapper implements ScheduledFuture<Object>
{
	private final ScheduledFuture<?> future;
	
	/**
	 * Creates a new {@link ScheduledFutureWrapper} instance.<br>
	 * This constructor wraps an existing {@code ScheduledFuture} object.<br>
	 * It allows the wrapper to manage and access the underlying task.
	 * @param future The {@code ScheduledFuture} to be wrapped.
	 */
	public ScheduledFutureWrapper(ScheduledFuture<?> future)
	{
		this.future = future;
	}
	
	/**
	 * Returns the time remaining before this task is scheduled to execute.<br>
	 * This method delegates the call to the underlying {@code ScheduledFuture}.
	 * @param unit The time unit of the delay.
	 * @return The amount of time left until execution as a {@code long}.
	 */
	@Override
	public long getDelay(TimeUnit unit)
	{
		return future.getDelay(unit);
	}
	
	/**
	 * Compares this wrapper to another {@link Delayed} object.<br>
	 * This method delegates the comparison to the underlying {@code ScheduledFuture}.
	 * @param o The other {@link Delayed} object to compare against.
	 * @return A negative integer, zero, or a positive integer as the result of the comparison.
	 */
	@Override
	public int compareTo(Delayed o)
	{
		return future.compareTo(o);
	}
	
	/**
	 * Cancels the execution of this task.<br>
	 * This method delegates the request to the underlying {@code ScheduledFuture}.<br>
	 * It ignores the {@code mayInterruptIfRunning} parameter and always passes {@code false}.
	 * @param mayInterruptIfRunning A boolean indicating if running tasks should be interrupted.
	 * @return {@code true} if the task was successfully cancelled, or {@code false} otherwise.
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning)
	{
		return future.cancel(false);
	}
	
	/**
	 * Retrieves the result of the computation.<br>
	 * This method blocks until the task completes.<br>
	 * It delegates the call to the underlying {@code ScheduledFuture}.
	 * @return The result of the computation as an {@code Object}.
	 * @throws InterruptedException If the current thread is interrupted while waiting.
	 * @throws ExecutionException If the computation threw an exception.
	 */
	@Override
	public Object get() throws InterruptedException, ExecutionException
	{
		return future.get();
	}
	
	/**
	 * Retrieves the result of this task.<br>
	 * It waits for a specific amount of time before giving up.<br>
	 * This method blocks the current thread until the result is ready or the timeout occurs.
	 * @param timeout The maximum time to wait.
	 * @param unit The time unit for the {@code timeout} value.
	 * @return The result of the task as an {@code Object}.
	 */
	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
	{
		return future.get(timeout, unit);
	}
	
	/**
	 * Checks if the underlying task has been cancelled.<br>
	 * This method delegates the check to the internal {@code ScheduledFuture}.
	 * @return {@code true} if the task was cancelled, {@code false} otherwise.
	 */
	@Override
	public boolean isCancelled()
	{
		return future.isCancelled();
	}
	
	/**
	 * Checks if the task has finished executing.<br>
	 * This method returns {@code true} if the task is complete or was cancelled.<br>
	 * It returns {@code false} if the task is still running.
	 * @return {@code true} if the task is done, otherwise {@code false}.
	 */
	@Override
	public boolean isDone()
	{
		return future.isDone();
	}
}
