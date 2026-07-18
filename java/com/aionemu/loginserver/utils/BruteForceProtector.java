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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.loginserver.configs.Config;

/**
 * This class provides mechanisms to protect the login server against brute-force attacks.<br>
 * It tracks failed login attempts and enforces delays or blocks based on configured limits.
 * @author Mr. Poke
 */
public class BruteForceProtector
{
	private final Map<String, FailedLoginInfo> failedConnections = new ConcurrentHashMap<>();
	
	class FailedLoginInfo
	{
		private int count;
		private final long time;
		
		/**
		 * @param count
		 * @param time
		 */
		public FailedLoginInfo(int count, long time)
		{
			super();
			this.count = count;
			this.time = time;
		}
		
		public void increseCount()
		{
			count++;
		}
		
		/**
		 * @return the count
		 */
		public int getCount()
		{
			return count;
		}
		
		/**
		 * @return the time
		 */
		public long getTime()
		{
			return time;
		}
	}
	
	/**
	 * Gets the single shared instance of the {@link BruteForceProtector}.<br>
	 * This method follows the singleton pattern.
	 * @return The global {@code BruteForceProtector} instance.
	 */
	public static BruteForceProtector getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Records a failed connection attempt from a specific IP address.<br>
	 * This method updates the failure count or resets it based on time limits.<br>
	 * It helps prevent brute force attacks by tracking repeated failures.
	 * @param ip The IP address of the client to track.
	 * @return {@code true} if the IP is now banned, otherwise {@code false}.
	 */
	public boolean addFailedConnect(String ip)
	{
		final FailedLoginInfo failed = failedConnections.get(ip);
		if ((failed == null) || ((System.currentTimeMillis() - failed.getTime()) > (Config.WRONG_LOGIN_BAN_TIME * 1000 * 60)))
		{
			failedConnections.put(ip, new FailedLoginInfo(1, System.currentTimeMillis()));
		}
		else
		{
			if (failed.getCount() >= Config.LOGIN_TRY_BEFORE_BAN)
			{
				failedConnections.remove(ip);
				return true;
			}
			
			failed.increseCount();
		}
		
		return false;
	}
	
	private static class SingletonHolder
	{
		protected static final BruteForceProtector instance = new BruteForceProtector();
	}
}
