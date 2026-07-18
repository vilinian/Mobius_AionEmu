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
package com.aionemu.loginserver.model;

import java.sql.Timestamp;

import com.aionemu.gameserver.model.templates.mail.MailPart;

/**
 * Represents an IP address that has been banned from accessing the server.<br>
 * It stores the necessary data to identify and block restricted connections.
 * @author SoulKeeper
 */
public class BannedIP
{
	/**
	 * Returns id of ip ban
	 */
	private Integer id;
	/**
	 * Returns ip mask
	 */
	private String mask;
	/**
	 * Returns expiration time
	 */
	private Timestamp timeEnd;
	
	/**
	 * Checks if the IP ban is currently active.<br>
	 * A ban is active if the expiration time is {@code null}.<br>
	 * It is also active if the current time is before the expiration time.
	 * @return {@code true} if the ban is still active, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return (timeEnd == null) || (timeEnd.getTime() > System.currentTimeMillis());
	}
	
	/**
	 * Retrieves the unique identifier for this {@link MailPart}.<br>
	 * This value is used to distinguish different parts of a mail.
	 * @return The {@code Integer} ID of the part, or {@code null} if not set.
	 */
	public Integer getId()
	{
		return id;
	}
	
	/**
	 * Sets the unique identifier.
	 * @param id The identifier to assign.
	 */
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	/**
	 * Retrieves the IP mask associated with this ban.<br>
	 * This value is used to identify the range of blocked addresses.
	 * @return The {@code String} representation of the IP mask.
	 */
	public String getMask()
	{
		return mask;
	}
	
	/**
	 * Sets the IP mask for this banned entry.<br>
	 * This updates the {@code mask} field of the {@link BannedIP} object.
	 * @param mask The new mask string to assign.
	 */
	public void setMask(String mask)
	{
		this.mask = mask;
	}
	
	/**
	 * Retrieves the expiration date and time of the ban.<br>
	 * This value is stored as a {@code Timestamp}.
	 * @return the {@code Timestamp} representing when the ban ends.
	 */
	public Timestamp getTimeEnd()
	{
		return timeEnd;
	}
	
	/**
	 * Sets the expiration date and time for the ban.<br>
	 * This updates the {@code timeEnd} field of this {@link BannedIP} instance.
	 * @param timeEnd The new {@code Timestamp} to set.
	 */
	public void setTimeEnd(Timestamp timeEnd)
	{
		this.timeEnd = timeEnd;
	}
	
	/**
	 * Compares this {@link BannedIP} object with another object for equality.<br>
	 * It checks if both objects represent the same mask.
	 * @param o The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		
		if (!(o instanceof BannedIP))
		{
			return false;
		}
		
		final BannedIP bannedIP = (BannedIP) o;
		
		return !(mask != null ? !mask.equals(bannedIP.mask) : bannedIP.mask != null);
	}
	
	/**
	 * Returns a hash code value for this {@link BannedIP} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code mask} field.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		return mask != null ? mask.hashCode() : 0;
	}
}
