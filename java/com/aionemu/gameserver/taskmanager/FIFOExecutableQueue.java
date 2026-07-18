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

import java.util.concurrent.locks.ReentrantLock;

import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class provides a base implementation for a first-in, first-out (FIFO) queue of executable tasks.<br>
 * It allows the {@link ThreadPoolManager} to process tasks in the order they were received.<br>
 * Subclasses should implement the logic for adding and retrieving items from the queue.
 * @author NB4L1
 */
public abstract class FIFOExecutableQueue implements Runnable
{
	private static final byte NONE = 0;
	private static final byte QUEUED = 1;
	private static final byte RUNNING = 2;
	private final ReentrantLock lock = new ReentrantLock();
	private volatile byte state = NONE;
	
	/**
	 * This method prepares the task for execution.<br>
	 * It checks if the current {@code state} is {@code NONE}.<br>
	 * If valid, it updates the status and submits this task to the {@link ThreadPoolManager}.
	 */
	protected void execute()
	{
		lock();
		try
		{
			if (state != NONE)
			{
				return;
			}
			
			state = QUEUED;
		}
		finally
		{
			unlock();
		}
		
		ThreadPoolManager.getInstance().execute(this);
	}
	
	/**
	 * Acquires the internal {@code ReentrantLock}.<br>
	 * This ensures thread safety during execution.<br>
	 * It prevents other threads from accessing protected resources.
	 */
	public void lock()
	{
		lock.lock();
	}
	
	/**
	 * Releases the {@code ReentrantLock} held by this queue.<br>
	 * This method allows other threads to access the shared resources.<br>
	 * It should be called after the task is finished executing.
	 */
	public void unlock()
	{
		lock.unlock();
	}
	
	@Override
	public void run()
	{
		try
		{
			while (!isEmpty())
			{
				setState(QUEUED, RUNNING);
				
				try
				{
					while (!isEmpty())
					{
						removeAndExecuteFirst();
					}
				}
				finally
				{
					setState(RUNNING, QUEUED);
				}
			}
		}
		finally
		{
			setState(QUEUED, NONE);
		}
	}
	
	/**
	 * Updates the current state of the queue.<br>
	 * This method ensures the transition is valid by checking the {@code expected} value.<br>
	 * It uses a lock to prevent concurrent modifications.
	 * @param expected The current state that must be present before updating.
	 * @param value The new state to set.
	 */
	private void setState(byte expected, byte value)
	{
		lock();
		try
		{
			if (state != expected)
			{
				throw new IllegalStateException("state: " + state + ", expected: " + expected);
			}
		}
		finally
		{
			state = value;
			
			unlock();
		}
	}
	
	protected abstract boolean isEmpty();
	
	protected abstract void removeAndExecuteFirst();
}
