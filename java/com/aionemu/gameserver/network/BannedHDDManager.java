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
package com.aionemu.gameserver.network;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.BannedHddDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.World;

/**
 * Manages the logic for handling players with banned hard drive identifiers.<br>
 * It provides methods to check if a {@link Player} is restricted based on their HDD.<br>
 * This class interacts with {@link BannedHddDAO} to retrieve and update ban records.
 * @author Alex
 */
public class BannedHDDManager
{
	private final Logger log = LoggerFactory.getLogger(BannedHDDManager.class);
	
	private static class SingletonHolder
	{
		protected static final BannedHDDManager hddmanager = new BannedHDDManager();
	}
	
	/**
	 * Provides the global instance of the {@link BannedHDDManager}.<br>
	 * Use this method to access the manager from anywhere in your code.<br>
	 * This follows the singleton design pattern.
	 * @return The single instance of {@code BannedHDDManager}.
	 */
	public static BannedHDDManager getInstance()
	{
		return SingletonHolder.hddmanager;
	}
	
	private final BannedHddDAO dao = DAOManager.getDAO(BannedHddDAO.class);
	
	/**
	 * Initializes a new instance of the {@link BannedHDDManager}.<br>
	 * This constructor loads the current ban list from the database.<br>
	 * It also removes any expired bans during the startup process.
	 */
	public BannedHDDManager()
	{
		dao.cleanExpiredBans();
		bannedHddList = dao.load();
		log.info("Loaded " + bannedHddList.size() + " banned hdd list.");
	}
	
	private Map<String, BannedHDDEntry> bannedHddList = new ConcurrentHashMap<>();
	
	/**
	 * Bans a specific hardware ID from accessing the server.<br>
	 * This method disconnects any active players with the matching {@code address}.<br>
	 * It updates or creates a new ban entry in the database.
	 * @param address The unique hardware identifier to block.
	 * @param newTime The timestamp when the ban will expire.
	 * @param details A description of why the address was banned.
	 */
	public void banAddress(String address, long newTime, String details)
	{
		for (Player player : World.getInstance().getAllPlayers())
		{
			if (player.getClientConnection().getHddSerial().equals(address))
			{
				player.getClientConnection().closeNow();
			}
		}
		
		BannedHDDEntry entry;
		if (bannedHddList.containsKey(address))
		{
			if (bannedHddList.get(address).isActiveTill(newTime))
			{
				return;
			}
			
			entry = bannedHddList.get(address);
			entry.updateTime(newTime);
		}
		else
		{
			entry = new BannedHDDEntry(address, newTime);
		}
		
		entry.setDetails(details);
		
		bannedHddList.put(address, entry);
		dao.update(entry);
		log.info("[BannedHDDManager] banned " + address + " to " + entry.getTime().toString() + " for " + details);
	}
	
	/**
	 * Removes an IP address from the ban list.<br>
	 * This method updates both the memory cache and the database.<br>
	 * It logs the action with the provided details.
	 * @param address The {@code String} representing the IP to unban.
	 * @param details A {@code String} describing why the address is being unbanned.
	 * @return {@code true} if the address was successfully removed, or {@code false} if it was not found.
	 */
	public boolean unbanAddress(String address, String details)
	{
		if (bannedHddList.containsKey(address))
		{
			bannedHddList.remove(address);
			dao.remove(address);
			log.info("[BannedHDDManager] unbanned " + address + " for " + details);
			
			// LoginServer.getInstance().sendPacket(new SM_MACBAN_CONTROL((byte) 0, address, 0, details));
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific hardware ID is currently banned.<br>
	 * This method looks up the {@code address} in the internal list.<br>
	 * It returns {@code true} if the entry exists and is active.
	 * @param address The unique identifier of the hardware to check.
	 * @return {@code true} if the device is banned, otherwise {@code false}.
	 */
	public boolean isBanned(String address)
	{
		if (bannedHddList.containsKey(address))
		{
			log.info("HDD_SERIAL: " + address + " is such banned list!");
			return bannedHddList.get(address).isActive();
		}
		
		return false;
	}
	
	/**
	 * Loads a specific entry into the banned list.<br>
	 * This method updates the internal cache with data from the database.
	 * @param address The unique identifier for the hardware device.
	 * @param time The expiration timestamp for the ban.
	 * @param details Additional information regarding the reason for the ban.
	 */
	public void dbLoad(String address, long time, String details)
	{
		bannedHddList.put(address, new BannedHDDEntry(address, new Timestamp(time), details));
	}
}
