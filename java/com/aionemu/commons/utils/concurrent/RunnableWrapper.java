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

/**
 * This class acts as a wrapper for any {@link Runnable} instance.<br>
 * It allows for the execution of tasks while providing additional utility or context.<br>
 * Use this class to simplify how different tasks are handled by thread executors.
 * @author -Nemesiss-
 */
public class RunnableWrapper implements Runnable
{
	private final Runnable runnable;
	private final long maxRuntimeMsWithoutWarning;
	
	/**
	 * Creates a new {@link RunnableWrapper} instance.<br>
	 * This constructor wraps a provided {@code Runnable}.<br>
	 * It sets the default maximum runtime to {@code Long.MAX_VALUE}.
	 * @param runnable The {@code Runnable} task to be wrapped.
	 */
	public RunnableWrapper(Runnable runnable)
	{
		this(runnable, Long.MAX_VALUE);
	}
	
	/**
	 * Creates a new {@link RunnableWrapper} instance.<br>
	 * This constructor initializes the task and its time limit.
	 * @param runnable The {@code Runnable} task to be executed.
	 * @param maxRuntimeMsWithoutWarning The maximum time in milliseconds allowed before a warning is issued.
	 */
	public RunnableWrapper(Runnable runnable, long maxRuntimeMsWithoutWarning)
	{
		this.runnable = runnable;
		this.maxRuntimeMsWithoutWarning = maxRuntimeMsWithoutWarning;
	}
	
	@Override
	public void run()
	{
		ExecuteWrapper.execute(runnable, maxRuntimeMsWithoutWarning);
	}
}
