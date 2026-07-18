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
package com.aionemu.loginserver.controller;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.NetworkUtils;
import com.aionemu.loginserver.dao.BannedIpDAO;
import com.aionemu.loginserver.model.BannedIP;

/**
 * This class manages all IP banning activities within the login server.<br>
 * It provides methods to handle requests related to {@link BannedIP} records.
 * @author SoulKeeper
 */
public class BannedIpController
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(BannedIpController.class);
	/**
	 * List of banned ip adresses
	 */
	private static Set<BannedIP> banList;
	
	/**
	 * Initializes the IP banning system.<br>
	 * This method clears existing data and loads new records from the database.<br>
	 * It prepares the {@code banList} for use by other methods.
	 */
	public static void start()
	{
		clean();
		load();
	}
	
	/**
	 * Removes expired IP bans from the database.<br>
	 * This method calls {@code getDAO} to perform the cleanup.
	 */
	private static void clean()
	{
		getDAO().cleanExpiredBans();
	}
	
	/**
	 * Loads the banned IP addresses from the database.<br>
	 * This method populates the internal {@code banList}.<br>
	 * It ensures that all current bans are available for checking.
	 */
	public static void load()
	{
		reload();
	}
	
	/**
	 * Reloads the list of banned IP addresses from the database.<br>
	 * This method updates the {@code banList} set with current data.<br>
	 * It logs the total number of bans loaded to the console.
	 */
	public static void reload()
	{
		// we are not going to make ip ban every minute, so it's ok to simplify a concurrent code a bit
		banList = getDAO().getAllBans();
		log.info("BannedIpController loaded " + banList.size() + " IP bans.");
	}
	
	/**
	 * Checks if a specific IP address is currently banned.<br>
	 * This method compares the input against the active ban list.<br>
	 * It returns {@code true} if a match is found.
	 * @param ip The IP address string to check.
	 * @return {@code true} if the IP is banned, otherwise {@code false}.
	 */
	public static boolean isBanned(String ip)
	{
		for (BannedIP ipBan : banList)
		{
			if (ipBan.isActive() && NetworkUtils.checkIPMatching(ipBan.getMask(), ip))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Adds a new IP address to the banned list.<br>
	 * This method calls {@code java.sql.Timestamp)} with a {@code null} expiration.
	 * @param ip The IP address string to ban.
	 * @return {@code true} if the ban was successful, or {@code false} otherwise.
	 */
	public static boolean banIp(String ip)
	{
		return banIp(ip, null);
	}
	
	/**
	 * Bans a specific IP address until a certain time.<br>
	 * This method prevents local addresses like {@code 127.0.0.1} from being blocked.<br>
	 * It saves the ban to the database using {@link BannedIpDAO}.
	 * @param ip The IP address string to block.
	 * @param expireTime The timestamp when the ban should end.
	 * @return {@code true} if the ban was successful, or {@code false} otherwise.
	 */
	public static boolean banIp(String ip, Timestamp expireTime)
	{
		if (ip.equals("127.0.0.1"))
		{
			return false;
		}
		
		final BannedIP ipBan = new BannedIP();
		ipBan.setMask(ip);
		ipBan.setTimeEnd(expireTime);
		banList.add(ipBan);
		try
		{
			getDAO().insert(ipBan);
			return true;
		}
		catch (Exception e)
		{
			log.warn("Ip " + ip + " is already banned.");
			return false;
		}
	}
	
	/**
	 * Adds a new ban to the database or updates an existing one.<br>
	 * It checks if the {@code BannedIP} object has an ID.<br>
	 * If the ID is {@code null}, it inserts a new record.<br>
	 * Otherwise, it updates the current record.
	 * @param ipBan The {@link BannedIP} object to save or update.
	 * @return {@code true} if the database operation succeeded, {@code false} otherwise.
	 */
	public static boolean addOrUpdateBan(BannedIP ipBan)
	{
		if (ipBan.getId() == null)
		{
			if (getDAO().insert(ipBan))
			{
				banList.add(ipBan);
				return true;
			}
			
			return false;
		}
		
		return getDAO().update(ipBan);
	}
	
	/**
	 * Removes a specific IP address from the ban list.<br>
	 * This method updates both the memory cache and the database.
	 * @param ip The {@code String} representation of the IP to unban.
	 * @return {@code true} if the IP was successfully removed, otherwise {@code false}.
	 */
	public static boolean unbanIp(String ip)
	{
		final Iterator<BannedIP> it = banList.iterator();
		while (it.hasNext())
		{
			final BannedIP ipBan = it.next();
			if (ipBan.getMask().equals(ip))
			{
				if (getDAO().remove(ipBan))
				{
					it.remove();
					return true;
				}
				break;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the database access object for banned IP addresses.<br>
	 * This method uses {@link DAOManager} to fetch the correct instance.
	 * @return The {@code BannedIpDAO} instance used for database operations.
	 */
	private static BannedIpDAO getDAO()
	{
		return DAOManager.getDAO(BannedIpDAO.class);
	}
}
