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
package com.aionemu.gameserver.ai2.event;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * This class serves as a thread-safe buffer for logging {@link AIEventType} occurrences.<br>
 * It allows the system to queue and process AI events asynchronously using a {@code LinkedBlockingDeque}.
 * @author ATracer
 */
public class AIEventLog extends LinkedBlockingDeque<AIEventType>
{
	private static final long serialVersionUID = -7234174243343636729L;
	
	/**
	 * Creates a new instance of the {@link AIEventLog} class.<br>
	 * This constructor initializes the log with default settings.<br>
	 * It uses the default capacity provided by the parent class.
	 */
	public AIEventLog()
	{
		super();
	}
	
	/**
	 * Creates a new {@link AIEventLog} with a specific size.<br>
	 * This sets the maximum number of events the log can hold.
	 * @param capacity The maximum number of elements allowed in the log.
	 */
	public AIEventLog(int capacity)
	{
		super(capacity);
	}
	
	/**
	 * Adds an {@code AIEventType} to the front of the log.<br>
	 * If the log is full, it removes the oldest event first.<br>
	 * This ensures there is always room for a new event.
	 * @param e The {@code AIEventType} to add to the beginning.
	 * @return Always returns {@code true}.
	 */
	@Override
	public synchronized boolean offerFirst(AIEventType e)
	{
		if (remainingCapacity() == 0)
		{
			removeLast();
		}
		
		super.offerFirst(e);
		return true;
	}
}
