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

/**
 * Represents a single entry in the network ban list.<br>
 * This class stores information about banned IP addresses or connections.<br>
 * It is used by {@code NetworkManager} to filter incoming traffic.
 * @author Alex
 */
public class NetworkBanEntry
{
	private final String networkip;
	private String details;
	private Timestamp timeEnd;
	
	/**
	 * Creates a new {@link NetworkBanEntry} for a specific IP address.<br>
	 * This constructor sets the initial network address and updates the ban time.
	 * @param address The {@code String} representing the IP address to ban.
	 * @param newTime The {@code long} value representing the new expiration timestamp.
	 */
	public NetworkBanEntry(String address, long newTime)
	{
		networkip = address;
		updateTime(newTime);
	}
	
	/**
	 * Creates a new {@link NetworkBanEntry} object.<br>
	 * This constructor initializes the IP address, expiration time, and extra information.
	 * @param address The network IP address to be banned.
	 * @param time The timestamp when the ban expires.
	 * @param details Additional notes regarding why the user was banned.
	 */
	public NetworkBanEntry(String address, Timestamp time, String details)
	{
		networkip = address;
		timeEnd = time;
		this.details = details;
	}
	
	/**
	 * Updates the description for this entry.<br>
	 * This method stores the provided {@code String} in the {@code details} field.
	 * @param details The new description to set.
	 */
	public void setDetails(String details)
	{
		this.details = details;
	}
	
	/**
	 * Updates the expiration time for this entry.<br>
	 * This method sets the {@code timeEnd} field using a new value.
	 * @param newTime The new timestamp to set.
	 */
	public void updateTime(long newTime)
	{
		timeEnd = new Timestamp(newTime);
	}
	
	/**
	 * Retrieves the IP address associated with this ban entry.<br>
	 * This method returns the value stored in the {@code networkip} field.
	 * @return The {@code String} representing the network IP address.
	 */
	public String getNetworkIP()
	{
		return networkip;
	}
	
	/**
	 * Retrieves the current bonus time.<br>
	 * This method returns the {@code Timestamp} value stored in this object.
	 * @return The {@code Timestamp} representing the bonus time.
	 */
	public Timestamp getTime()
	{
		return timeEnd;
	}
	
	/**
	 * Checks if this ban entry is currently active.<br>
	 * It compares the {@code timeEnd} against the current system time.
	 * @return {@code true} if the ban has not expired, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return (timeEnd != null) && (timeEnd.getTime() > System.currentTimeMillis());
	}
	
	/**
	 * Checks if the ban is still active at a specific point in time.<br>
	 * It compares the provided {@code time} against the stored expiration date.
	 * @param time The timestamp to check against.
	 * @return {@code true} if the ban has not expired yet, otherwise {@code false}.
	 */
	public boolean isActiveTill(long time)
	{
		return (timeEnd != null) && (timeEnd.getTime() > time);
	}
	
	/**
	 * Retrieves the detailed information for this entry.<br>
	 * This method returns the {@code details} string stored in the object.
	 * @return The description of the ban as a {@code String}.
	 */
	public String getDetails()
	{
		return details;
	}
}
