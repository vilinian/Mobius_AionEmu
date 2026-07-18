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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * Provides a simple implementation of a first-in, first-out (FIFO) queue for executable tasks.<br>
 * This class serves as a base for managing task execution in the order they are received.
 * @author NB4L1
 * @param <T>
 */
public abstract class FIFOSimpleExecutableQueue<T> extends FIFOExecutableQueue
{
	private final Deque<T> queue = new ArrayDeque<>();
	
	/**
	 * Adds an item to the queue and starts processing.<br>
	 * This method ensures that {@code t} is added safely before calling {@code execute}.
	 * @param t The task to be executed.
	 */
	public void execute(T t)
	{
		synchronized (queue)
		{
			queue.addLast(t);
		}
		
		execute();
	}
	
	/**
	 * Adds all items from a collection to the queue.<br>
	 * This method then triggers the execution of the tasks.
	 * @param c The {@code Collection} of items to be added.
	 */
	public void executeAll(Collection<T> c)
	{
		synchronized (queue)
		{
			queue.addAll(c);
		}
		
		execute();
	}
	
	/**
	 * Removes a specific item from the queue.<br>
	 * This method searches for {@code t} and deletes it if found.<br>
	 * It is thread-safe because it uses a synchronized block.
	 * @param t The object to be removed from the queue.
	 */
	public void remove(T t)
	{
		synchronized (queue)
		{
			queue.remove(t);
		}
	}
	
	/**
	 * Checks if the internal queue contains any items.<br>
	 * This method is thread-safe and uses a {@code synchronized} block.
	 * @return {@code true} if the queue has no elements, {@code false} otherwise.
	 */
	@Override
	protected boolean isEmpty()
	{
		synchronized (queue)
		{
			return queue.isEmpty();
		}
	}
	
	/**
	 * Removes and returns the first element from the queue.<br>
	 * This method is thread-safe because it uses a {@code synchronized} block.<br>
	 * It calls the underlying {@code removeFirst()} method on the internal list.
	 * @return The element removed from the front of the queue, or {@code null} if the queue is empty.
	 */
	protected T removeFirst()
	{
		synchronized (queue)
		{
			return queue.removeFirst();
		}
	}
	
	@Override
	protected abstract void removeAndExecuteFirst();
}
