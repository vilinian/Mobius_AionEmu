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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.loginserver.dao.BannedMacDAO;
import com.aionemu.loginserver.model.base.BannedMacEntry;

/**
 * This class manages the logic for handling banned MAC addresses.<br>
 * It provides methods to check, add, and remove entries from the {@link BannedMacDAO}.<br>
 * Use this manager to enforce hardware-level restrictions on user connections.
 * @author KID
 */
public class BannedMacManager
{
	private static BannedMacManager manager = new BannedMacManager();
	private Map<String, BannedMacEntry> bannedList = new ConcurrentHashMap<>();
	
	/**
	 * Gets the singleton instance of the {@link BannedMacManager}.<br>
	 * Use this method to access the global manager for handling banned MAC addresses.
	 * @return The single instance of {@code BannedMacManager}.
	 */
	public static BannedMacManager getInstance()
	{
		return manager;
	}
	
	private final BannedMacDAO dao = DAOManager.getDAO(BannedMacDAO.class);
	
	/**
	 * Creates a new instance of the {@link BannedMacManager}.<br>
	 * This constructor loads all banned MAC addresses from the database into memory.
	 */
	public BannedMacManager()
	{
		bannedList = dao.load();
	}
	
	/**
	 * Removes a specific address from the ban list.<br>
	 * This method updates both the internal memory and the database.
	 * @param address The unique identifier of the address to unban.
	 * @param details Additional information regarding the ban record.
	 */
	public void unban(String address, String details)
	{
		if (bannedList.containsKey(address))
		{
			bannedList.remove(address);
			dao.remove(address);
		}
	}
	
	/**
	 * Bans a specific MAC address for a set duration.<br>
	 * This method updates the {@code bannedList} and saves it to the database.
	 * @param address The unique identifier of the MAC address to ban.
	 * @param time The expiration timestamp for the ban.
	 * @param details A description explaining why the ban was issued.
	 */
	public void ban(String address, long time, String details)
	{
		final BannedMacEntry mac = new BannedMacEntry(address, new Timestamp(time), details);
		bannedList.put(address, mac);
		dao.update(mac);
	}
	
	/**
	 * Retrieves the internal map of all banned MAC addresses.<br>
	 * This method returns the {@code bannedList} used by this manager.
	 * @return A {@link Map} containing {@code String} keys and {@link BannedMacEntry} values.
	 */
	public Map<String, BannedMacEntry> getMap()
	{
		return bannedList;
	}
}
