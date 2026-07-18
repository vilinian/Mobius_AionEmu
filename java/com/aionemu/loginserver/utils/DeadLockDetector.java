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
package com.aionemu.loginserver.utils;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.ExitCode;

/**
 * This class monitors the system for potential deadlocks between threads.<br>
 * It periodically checks thread states to identify and log blocking issues.
 * @author -Nemesiss-
 */
public class DeadLockDetector extends Thread
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(DeadLockDetector.class);
	/**
	 * What should we do on DeadLock
	 */
	public static final byte NOTHING = 0;
	/**
	 * What should we do on DeadLock
	 */
	public static final byte RESTART = 1;
	/**
	 * how often check for deadlocks
	 */
	private final int sleepTime;
	/**
	 * ThreadMXBean
	 */
	private final ThreadMXBean tmx;
	/**
	 * What should we do on DeadLock
	 */
	private final byte doWhenDL;
	
	/**
	 * Creates a new instance of the {@link DeadLockDetector}.<br>
	 * This constructor initializes the thread and sets up the detection logic.
	 * @param sleepTime The interval in seconds between each check.
	 * @param doWhenDL The action to take when a deadlock is detected.
	 */
	public DeadLockDetector(int sleepTime, byte doWhenDL)
	{
		super("DeadLockDetector");
		this.sleepTime = sleepTime * 1000;
		tmx = ManagementFactory.getThreadMXBean();
		this.doWhenDL = doWhenDL;
	}
	
	@Override
	public void run()
	{
		boolean deadlock = false;
		while (!deadlock)
		{
			try
			{
				final long[] ids = tmx.findDeadlockedThreads();
				
				if (ids != null)
				{
					/**
					 * deadlock found :/
					 */
					deadlock = true;
					final ThreadInfo[] tis = tmx.getThreadInfo(ids, true, true);
					String info = "DeadLock Found!\n";
					for (ThreadInfo ti : tis)
					{
						info += ti.toString();
					}
					
					for (ThreadInfo ti : tis)
					{
						final LockInfo[] locks = ti.getLockedSynchronizers();
						final MonitorInfo[] monitors = ti.getLockedMonitors();
						if ((locks.length == 0) && (monitors.length == 0))
						{
							/**
							 * this thread is deadlocked but its not guilty
							 */
							continue;
						}
						
						ThreadInfo dl = ti;
						info += "Java-level deadlock:\n";
						info += "\t" + dl.getThreadName() + " is waiting to lock " + dl.getLockInfo().toString() + " which is held by " + dl.getLockOwnerName() + "\n";
						while ((dl = tmx.getThreadInfo(new long[]
						{
							dl.getLockOwnerId()
						}, true, true)[0]).getThreadId() != ti.getThreadId())
						{
							info += "\t" + dl.getThreadName() + " is waiting to lock " + dl.getLockInfo().toString() + " which is held by " + dl.getLockOwnerName() + "\n";
						}
					}
					
					log.warn(info);
					
					if (doWhenDL == RESTART)
					{
						System.exit(ExitCode.CODE_RESTART);
					}
				}
				
				Thread.sleep(sleepTime);
			}
			catch (Exception e)
			{
				log.warn("DeadLockDetector: " + e, e);
			}
		}
	}
}
