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
package com.aionemu.gameserver.taskmanager.parallel;

import java.util.Collection;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

/**
 * Provides a utility to execute actions on each element of a collection in parallel.<br>
 * This class extends {@link CountedCompleter} to manage concurrent task execution.<br>
 * To use the {@code forEach} method, you must statically import it.
 * @author Rolandas <br>
 *         To use forEach method, statically import the method</tt>
 * @param <E>
 */
public final class ForEach<E> extends CountedCompleter<E>
{
	private static final Logger log = LoggerFactory.getLogger(ForEach.class);
	private static final long serialVersionUID = 7902148320917998146L;
	
	/**
	 * Processes each element in a {@code Collection} using the provided {@code Predicate}.<br>
	 * This method uses the Fork/Join framework to perform operations in parallel.<br>
	 * It returns a {@link ForkJoinTask} that can be joined to wait for completion.
	 * @param <E>
	 * @param list The collection of elements to process.
	 * @param operation The logic to apply to each element.
	 * @return A {@code ForkJoinTask} representing the parallel computation, or {@code null} if the list is empty.
	 */
	public static <E> ForkJoinTask<E> forEach(Collection<E> list, Predicate<E> operation)
	{
		if (list.size() > 0)
		{
			@SuppressWarnings("unchecked")
			final E[] objects = list.toArray((E[]) new Object[list.size()]);
			final CountedCompleter<E> completer = new ForEach<>(null, operation, 0, objects.length, objects);
			return completer;
		}
		
		return null;
	}
	
	/**
	 * Performs an operation on each element of a provided list asynchronously.<br>
	 * This method uses the {@link ForkJoinTask} framework to process elements in parallel.<br>
	 * It divides the work into smaller tasks to improve performance.
	 * @param <E> The type of elements in the list.
	 * @param operation The {@code Predicate} to apply to each element.
	 * @param list The array of elements to be processed.
	 * @return A {@link ForkJoinTask} representing the asynchronous operation, or {@code null} if the list is empty or {@code null}.
	 */
	@SafeVarargs
	public static <E> ForkJoinTask<E> forEach(Predicate<E> operation, E... list)
	{
		if ((list != null) && (list.length > 0))
		{
			final CountedCompleter<E> completer = new ForEach<>(null, operation, 0, list.length, list);
			return completer;
		}
		
		return null;
	}
	
	final E[] list;
	final Predicate<E> operation;
	final int lo, hi;
	
	/**
	 * Initializes a new {@link ForEach} task instance.<br>
	 * This constructor sets up the range and data for parallel processing.
	 * @param rootTask The parent {@code CountedCompleter} task.
	 * @param operation The {@code Predicate} to apply to each element.
	 * @param lo The starting index of the current range.
	 * @param hi The ending index of the current range.
	 * @param list The array of elements to be processed.
	 */
	@SafeVarargs
	private ForEach(CountedCompleter<E> rootTask, Predicate<E> operation, int lo, int hi, E... list)
	{
		super(rootTask);
		this.list = list;
		this.operation = operation;
		this.lo = lo;
		this.hi = hi;
	}
	
	/**
	 * Executes the task logic for a subset of elements.<br>
	 * This method splits the work into smaller tasks using a divide and conquer approach.<br>
	 * It applies the operation to individual items and manages the completion status.
	 */
	@Override
	public void compute()
	{
		final int l = lo;
		int h = hi;
		while ((h - l) >= 2)
		{
			final int mid = (l + h) >>> 1;
			addToPendingCount(1);
			new ForEach<>(this, operation, mid, h, list).fork(); // right child
			h = mid;
		}
		
		if (h > l)
		{
			try
			{
				operation.test(list[l]);
			}
			catch (Throwable ex)
			{
				// Complete without re-throwing an exception; otherwise, call completeExceptionally(ex).
				onExceptionalCompletion(ex, this);
			}
		}
		
		propagateCompletion();
	}
	
	/**
	 * This method is called when the task completes exceptionally.<br>
	 * It logs the error and returns {@code true}.<br>
	 * Returning {@code true} prevents an infinite wait during a {@code join} call.
	 * @param ex The exception that caused the failure.
	 * @param caller The task that triggered this completion.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean onExceptionalCompletion(Throwable ex, CountedCompleter<?> caller)
	{
		log.warn("", ex);
		
		// returning false would result in infinite wait when calling join();
		return true;
	}
}
