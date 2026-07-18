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
package com.aionemu.gameserver.utils.cron;

import com.aionemu.commons.services.cron.RunnableRunner;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class handles the execution of {@link RunnableRunner} tasks using a managed thread pool.<br>
 * It allows for efficient task scheduling by leveraging the {@link ThreadPoolManager}.
 */
public class ThreadPoolManagerRunnableRunner extends RunnableRunner
{
	/**
	 * Executes a task in the background.<br>
	 * This method uses {@link ThreadPoolManager} to run the provided {@code Runnable}.
	 * @param r The {@code Runnable} task to be executed.
	 */
	@Override
	public void executeRunnable(Runnable r)
	{
		ThreadPoolManager.getInstance().execute(r);
	}
	
	/**
	 * This method runs a task that takes a long time to complete.<br>
	 * It uses the {@link ThreadPoolManager} to handle the execution.<br>
	 * The task is passed as a {@code Runnable}.
	 * @param r The {@code Runnable} task to be executed.
	 */
	@Override
	public void executeLongRunningRunnable(Runnable r)
	{
		ThreadPoolManager.getInstance().executeLongRunning(r);
	}
}
