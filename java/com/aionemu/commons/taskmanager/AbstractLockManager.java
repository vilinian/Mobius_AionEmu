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
package com.aionemu.commons.taskmanager;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Provides a base implementation for managing concurrent access to shared resources.<br>
 * This class serves as a foundation for creating specialized locking mechanisms using {@link ReentrantReadWriteLock}.<br>
 * It helps ensure thread safety across different tasks in the system.
 * @author lord_rex and MrPoke
 */
public abstract class AbstractLockManager
{
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
	private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock(); // Later could be used.
	
	/**
	 * Acquires the exclusive write lock.<br>
	 * This method ensures that only one thread can modify data at a time.<br>
	 * It uses the internal {@code ReentrantReadWriteLock}.
	 */
	public void writeLock()
	{
		writeLock.lock();
	}
	
	/**
	 * Releases the current {@code writeLock}.<br>
	 * This method should be called after completing a write operation.<br>
	 * It allows other threads to acquire the lock.
	 */
	public void writeUnlock()
	{
		writeLock.unlock();
	}
	
	/**
	 * Acquires the {@code readLock} for this manager.<br>
	 * This method ensures that only one thread can write at a time while allowing multiple readers.<br>
	 * It uses the internal {@link ReentrantReadWriteLock}.
	 */
	public void readLock()
	{
		readLock.lock();
	}
	
	/**
	 * Releases the current {@code readLock}.<br>
	 * This method should be called after completing a read operation.<br>
	 * It allows other threads to acquire the lock as needed.
	 */
	public void readUnlock()
	{
		readLock.unlock();
	}
}
