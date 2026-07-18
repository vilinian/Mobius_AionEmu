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
 * Represents an entry in the Hard Drive (HDD) ban list.<br>
 * This class stores information about players who have been banned from the server. It is used by {@code NetworkManager} to check for restricted accounts.
 * @author Alex
 */
public class BannedHDDEntry
{
	private final String hdd_serial;
	private String details;
	private Timestamp timeEnd;
	
	/**
	 * Creates a new {@link BannedHDDEntry} with a specific serial and time.<br>
	 * This constructor initializes the hardware ID and sets the expiration date.
	 * @param address The unique hardware identifier for the device.
	 * @param newTime The timestamp when the ban expires.
	 */
	public BannedHDDEntry(String address, long newTime)
	{
		hdd_serial = address;
		updateTime(newTime);
	}
	
	/**
	 * Creates a new {@link BannedHDDEntry} object.<br>
	 * This constructor initializes the hardware serial, end time, and extra information.
	 * @param address The unique hardware serial string to be stored as {@code hdd_serial}.
	 * @param time The {@code Timestamp} representing when the ban expires.
	 * @param details A description of why the entry was created.
	 */
	public BannedHDDEntry(String address, Timestamp time, String details)
	{
		hdd_serial = address;
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
	 * Retrieves the unique serial number of the hard drive.<br>
	 * This value is stored in the {@code hdd_serial} field.
	 * @return The {@code String} representing the hardware serial.
	 */
	public String getHDDSerial()
	{
		return hdd_serial;
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
