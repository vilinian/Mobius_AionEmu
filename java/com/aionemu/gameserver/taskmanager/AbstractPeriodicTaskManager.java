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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.taskmanager.AbstractLockManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.GameServer.StartupHook;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Provides a base structure for tasks that need to execute repeatedly at specific intervals.<br>
 * It handles the core logic for periodic execution and integrates with {@link ThreadPoolManager} for thread safety.
 * @author lord_rex and MrPoke based on l2j-free engines. This can be used for periodic calls.
 */
public abstract class AbstractPeriodicTaskManager extends AbstractLockManager implements Runnable, StartupHook
{
	protected static final Logger log = LoggerFactory.getLogger(AbstractPeriodicTaskManager.class);
	private final int period;
	
	/**
	 * Creates a new instance of a periodic task manager.<br>
	 * This constructor registers the task with {@link GameServer}.<br>
	 * It sets the interval for how often the task runs.
	 * @param period The time interval between each task execution.
	 */
	public AbstractPeriodicTaskManager(int period)
	{
		this.period = period;
		
		GameServer.addStartupHook(this);
		
		log.debug("[PeriodicTaskManager] " + getClass().getSimpleName() + ": Initialized.");
	}
	
	/**
	 * Initializes the periodic task.<br>
	 * This method schedules this task to run using {@link ThreadPoolManager}.<br>
	 * It uses a random delay based on the {@code period} value.
	 */
	@Override
	public void onStartup()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000 + Rnd.get(period), Rnd.get(period - 5, period + 5));
	}
	
	@Override
	public abstract void run();
}
