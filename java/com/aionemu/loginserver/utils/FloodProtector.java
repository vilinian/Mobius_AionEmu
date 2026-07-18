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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.network.aion.clientpackets.CM_LOGIN;

/**
 * This class provides mechanisms to protect the login server from flood attacks.<br>
 * It monitors incoming requests and blocks IPs that exceed defined limits.<br>
 * Use this utility to ensure server stability against malicious traffic.
 * @author Mr. Poke
 */
public class FloodProtector
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_LOGIN.class);
	private final Map<String, Long> flood = new ConcurrentHashMap<>();
	private final Map<String, Long> ban = new ConcurrentHashMap<>();
	
	/**
	 * Provides the global instance of the {@link FloodProtector}.<br>
	 * Use this method to access the flood protection system.<br>
	 * This follows the singleton design pattern.
	 * @return The single instance of {@code FloodProtector}.
	 */
	public static FloodProtector getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Checks if a connection attempt from an IP address is too frequent.<br>
	 * This method handles flood protection and temporary bans.<br>
	 * It returns {@code true} if the IP should be blocked.
	 * @param ip The IP address to check.
	 * @return {@code true} if the request is blocked, {@code false} otherwise.
	 */
	public boolean tooFast(String ip)
	{
		final String[] exclIps = Config.EXCLUDED_IP.split(",");
		for (String exclIp : exclIps)
		{
			if (ip.equals(exclIp))
			{
				return false;
			}
		}
		
		final Long banned = ban.get(ip);
		if (banned != null)
		{
			if (System.currentTimeMillis() < banned)
			{
				return true;
			}
			
			ban.remove(ip);
			return false;
		}
		
		final Long time = flood.get(ip);
		if (time == null)
		{
			flood.put(ip, System.currentTimeMillis() + (Config.FAST_RECONNECTION_TIME * 1000));
			return false;
		}
		
		if (time > System.currentTimeMillis())
		{
			log.info("[AUDIT]FloodProtector:" + ip + " IP too fast connection attemp. blocked for " + Config.WRONG_LOGIN_BAN_TIME + " min");
			ban.put(ip, System.currentTimeMillis() + (Config.WRONG_LOGIN_BAN_TIME * 60000));
			return true;
		}
		
		return false;
	}
	
	private static class SingletonHolder
	{
		protected static final FloodProtector instance = new FloodProtector();
	}
}
