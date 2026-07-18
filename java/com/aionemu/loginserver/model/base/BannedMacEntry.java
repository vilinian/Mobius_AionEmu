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
package com.aionemu.loginserver.model.base;

import java.sql.Timestamp;

/**
 * Represents an entry in the database for a banned MAC address.<br>
 * This model is used to track and manage hardware-level bans within the login server.
 * @author KID
 */
public class BannedMacEntry
{
	private final String mac;
	private String details;
	private Timestamp timeEnd;
	
	/**
	 * Creates a new {@link BannedMacEntry} instance.<br>
	 * It sets the MAC address and updates the expiration time.
	 * @param address The unique hardware address of the device.
	 * @param newTime The timestamp for when the ban expires.
	 */
	public BannedMacEntry(String address, long newTime)
	{
		mac = address;
		updateTime(newTime);
	}
	
	/**
	 * Creates a new {@link BannedMacEntry} object.<br>
	 * This constructor initializes the MAC address, expiration time, and extra details.
	 * @param address The unique hardware address of the device.
	 * @param time The timestamp when the ban expires.
	 * @param details A description of why the device was banned.
	 */
	public BannedMacEntry(String address, Timestamp time, String details)
	{
		mac = address;
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
	 * Retrieves the MAC address associated with this entry.<br>
	 * This method returns the unique hardware identifier as a {@code String}.
	 * @return The MAC address of the banned device.
	 */
	public String getMac()
	{
		return mac;
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
	 * It returns {@code true} if the ban has not expired or has no end time.
	 * @return {@code true} if the entry is active, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return (timeEnd != null) || (timeEnd.getTime() > System.currentTimeMillis());
	}
	
	/**
	 * Checks if the ban is still active at a specific point in time.<br>
	 * It compares the provided {@code time} against the stored expiration date.
	 * @param time The timestamp to check against.
	 * @return {@code true} if the ban has not expired yet, otherwise {@code false}.
	 */
	public boolean isActiveTill(long time)
	{
		return (timeEnd != null) || (timeEnd.getTime() > time);
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
