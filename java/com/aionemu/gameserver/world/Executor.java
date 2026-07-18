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
package com.aionemu.gameserver.world;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class provides a base for executing asynchronous tasks related to {@link AionObject} instances.<br>
 * It handles the execution logic using the {@link ThreadPoolManager}.
 * @author xavier
 * @param <T>
 */
public abstract class Executor<T extends AionObject>
{
	private static final Logger log = LoggerFactory.getLogger(Executor.class);
	
	public abstract boolean run(T object);
	
	/**
	 * This method processes a collection of objects.<br>
	 * It iterates through each item and calls the {@code run} method.<br>
	 * The process stops if {@code run(T)} returns {@code false}.<br>
	 * Any exceptions caught during execution are logged as warnings.
	 * @param objects The collection of items to be processed.
	 */
	private void runImpl(Collection<T> objects)
	{
		try
		{
			for (T o : objects)
			{
				if (o != null)
				{
					if (!Executor.this.run(o))
					{
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			log.warn(e.getMessage(), e);
		}
	}
	
	/**
	 * Processes a collection of objects.<br>
	 * It decides whether to run the task immediately or in a background thread.
	 * @param objects The collection of {@code T} objects to process.
	 * @param now Set to {@code true} to run the task immediately, or {@code false} to run it asynchronously.
	 */
	public void execute(Collection<T> objects, boolean now)
	{
		if (now)
		{
			runImpl(objects);
		}
		else
		{
			ThreadPoolManager.getInstance().execute(() -> runImpl(objects));
		}
	}
	
	/**
	 * Processes a collection of {@code AionObject} instances.<br>
	 * This method schedules the tasks for later execution.<br>
	 * It calls the overloaded {@code boolean)} method with {@code false}.
	 * @param objects The collection of objects to be processed.
	 */
	public void execute(Collection<T> objects)
	{
		execute(objects, false);
	}
}
