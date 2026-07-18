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
package com.aionemu.commons.network.util;

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
 * It periodically checks thread states to identify and report blocked resources.
 * @author -Nemesiss-, ATracer
 */
public class DeadLockDetector extends Thread
{
	private static final Logger log = LoggerFactory.getLogger(DeadLockDetector.class);
	/** What should we do on DeadLock */
	public static final byte NOTHING = 0;
	/** What should we do on DeadLock */
	public static final byte RESTART = 1;
	
	/** how often check for deadlocks */
	private final int sleepTime;
	/**
	 * ThreadMXBean
	 */
	private final ThreadMXBean tmx;
	/** What should we do on DeadLock */
	private final byte doWhenDL;
	
	/**
	 * Initializes a new {@link DeadLockDetector} thread.<br>
	 * This constructor sets the interval for checking deadlocks.<br>
	 * It also defines the action to take if a deadlock is found.
	 * @param sleepTime The delay in milliseconds between checks.
	 * @param doWhenDL The action to perform, such as {@code NOTHING} or {@code RESTART}.
	 */
	public DeadLockDetector(final int sleepTime, byte doWhenDL)
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
					/** deadlock found :/ */
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
							/** this thread is deadlocked but its not guilty */
							continue;
						}
						
						ThreadInfo dl = ti;
						info += "Java-level deadlock:\n";
						info += createShortLockInfo(dl);
						while ((dl = tmx.getThreadInfo(new long[]
						{
							dl.getLockOwnerId()
						}, true, true)[0]).getThreadId() != ti.getThreadId())
						{
							info += createShortLockInfo(dl);
						}
						
						info += "\nDumping all threads:\n";
						for (ThreadInfo dumpedTI : tmx.dumpAllThreads(true, true))
						{
							info += printDumpedThreadInfo(dumpedTI);
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
	
	/**
	 * Converts a {@link ThreadInfo} object into a brief summary string.<br>
	 * This method extracts key details about the thread's current lock status.<br>
	 * It includes information about the waiting lock and its owner.
	 * @param threadInfo The {@code ThreadInfo} to process.
	 * @return A formatted {@code String} containing the summary of the lock info.
	 */
	private String createShortLockInfo(ThreadInfo threadInfo)
	{
		final StringBuilder sb = new StringBuilder("\t");
		sb.append(threadInfo.getThreadName());
		sb.append(" is waiting to lock ");
		sb.append(threadInfo.getLockInfo().toString());
		sb.append(" which is held by ");
		sb.append(threadInfo.getLockOwnerName());
		sb.append(". Locked synchronizers:");
		sb.append(threadInfo.getLockedSynchronizers().length);
		sb.append(" monitors:");
		sb.append(threadInfo.getLockedMonitors().length);
		sb.append("\n");
		return sb.toString();
	}
	
	/**
	 * Converts a {@link ThreadInfo} object into a formatted string.<br>
	 * This method builds a readable summary of the thread's state and stack trace.<br>
	 * It also includes information about any locked monitors.
	 * @param threadInfo The {@code ThreadInfo} object to be processed.
	 * @return A formatted {@code String} containing the dumped thread details.
	 */
	private String printDumpedThreadInfo(ThreadInfo threadInfo)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\"" + threadInfo.getThreadName() + "\"" + " Id=" + threadInfo.getThreadId() + " " + threadInfo.getThreadState() + "\n");
		final StackTraceElement[] stacktrace = threadInfo.getStackTrace();
		for (int i = 0; i < stacktrace.length; i++)
		{
			final StackTraceElement ste = stacktrace[i];
			sb.append("\t" + "at " + ste.toString() + "\n");
			for (MonitorInfo mi : threadInfo.getLockedMonitors())
			{
				if (mi.getLockedStackDepth() == i)
				{
					sb.append("\t-  locked " + mi);
					sb.append('\n');
				}
			}
		}
		
		return sb.toString();
	}
}
