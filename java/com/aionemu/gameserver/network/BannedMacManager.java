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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_MACBAN_CONTROL;
import com.aionemu.gameserver.world.World;

/**
 * Manages the list of banned MAC addresses for network security.<br>
 * It provides methods to check if a device is blocked and handles {@link SM_MACBAN_CONTROL} packets.
 * @author KID,Modifly by Newlives@aioncore 2-2-2015
 */
public class BannedMacManager
{
	private static BannedMacManager manager = new BannedMacManager();
	private final Logger log = LoggerFactory.getLogger(BannedMacManager.class);
	
	/**
	 * Gets the singleton instance of the {@link BannedMacManager}.<br>
	 * Use this method to access the global manager for handling banned MAC addresses.
	 * @return The single instance of {@code BannedMacManager}.
	 */
	public static BannedMacManager getInstance()
	{
		return manager;
	}
	
	private final Map<String, BannedMacEntry> bannedList = new ConcurrentHashMap<>();
	
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
			if (player.getClientConnection().getMacAddress().equals(address))
			{
				player.getClientConnection().closeNow();
			}
		}
		
		BannedMacEntry entry;
		if (bannedList.containsKey(address))
		{
			if (bannedList.get(address).isActiveTill(newTime))
			{
				return;
			}
			
			entry = bannedList.get(address);
			entry.updateTime(newTime);
		}
		else
		{
			entry = new BannedMacEntry(address, newTime);
		}
		
		entry.setDetails(details);
		
		bannedList.put(address, entry);
		
		log.info("[BannedMacManager] banned " + address + " to " + entry.getTime().toString() + " for " + details);
		LoginServer.getInstance().sendPacket(new SM_MACBAN_CONTROL((byte) 1, address, newTime, details));
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
		if (bannedList.containsKey(address))
		{
			bannedList.remove(address);
			log.info("[BannedMacManager] unbanned " + address + " for " + details);
			LoginServer.getInstance().sendPacket(new SM_MACBAN_CONTROL((byte) 0, address, 0, details));
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
		if (bannedList.containsKey(address))
		{
			return bannedList.get(address).isActive();
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
		bannedList.put(address, new BannedMacEntry(address, new Timestamp(time), details));
	}
	
	/**
	 * This method is called when the manager finishes loading data.<br>
	 * It logs the total number of banned MAC addresses to the console.
	 */
	public void onEnd()
	{
		log.info("[BannedMacManager] Loaded " + bannedList.size() + " banned mac addresses");
	}
}
