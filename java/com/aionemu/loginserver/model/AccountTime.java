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

/**
 * This class stores various time-related statistics for a user account.<br>
 * It tracks data such as the last login time, session duration, and accumulated online or rest time for the current day.
 * @author EvilSpirit
 */
public class AccountTime
{
	/**
	 * Time the account has last logged in
	 */
	private Timestamp lastLoginTime;
	/**
	 * Time after the account will expired
	 */
	private Timestamp expirationTime;
	/**
	 * Time when the penalty will end
	 */
	private Timestamp penaltyEnd;
	/**
	 * The duration of the session
	 */
	private long sessionDuration;
	/**
	 * Accumulated Online Time
	 */
	private long accumulatedOnlineTime;
	/**
	 * Accumulated Rest Time
	 */
	private long accumulatedRestTime;
	
	/**
	 * Creates a new instance of {@link AccountTime}.<br>
	 * This constructor sets the {@code lastLoginTime} to the current system time.
	 */
	public AccountTime()
	{
		lastLoginTime = new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * Retrieves the date and time of the account's most recent login.<br>
	 * This method returns the value stored in the {@code lastLoginTime} field.
	 * @return The {@code Timestamp} representing the last login time, or {@code null} if it has not been set.
	 */
	public Timestamp getLastLoginTime()
	{
		return lastLoginTime;
	}
	
	/**
	 * Updates the last login time for the account.<br>
	 * This method sets the {@code lastLoginTime} field to the provided value.
	 * @param lastLoginTime The new {@code Timestamp} to store.
	 */
	public void setLastLoginTime(Timestamp lastLoginTime)
	{
		this.lastLoginTime = lastLoginTime;
	}
	
	/**
	 * Retrieves the current duration of the user session.<br>
	 * This value represents how long the account has been active in the current session.
	 * @return The total session duration as a {@code long}.
	 */
	public long getSessionDuration()
	{
		return sessionDuration;
	}
	
	/**
	 * Updates the duration of the current session.<br>
	 * This method sets the {@code sessionDuration} field.
	 * @param sessionDuration The length of time for the session in milliseconds.
	 */
	public void setSessionDuration(long sessionDuration)
	{
		this.sessionDuration = sessionDuration;
	}
	
	/**
	 * Retrieves the total time the account has spent online.<br>
	 * The value is stored in milliseconds.
	 * @return the total accumulated online time as a {@code long}.
	 */
	public long getAccumulatedOnlineTime()
	{
		return accumulatedOnlineTime;
	}
	
	/**
	 * Updates the total time the account has spent online.<br>
	 * The value is stored in milliseconds.
	 * @param accumulatedOnlineTime The new online time in {@code long} format.
	 */
	public void setAccumulatedOnlineTime(long accumulatedOnlineTime)
	{
		this.accumulatedOnlineTime = accumulatedOnlineTime;
	}
	
	/**
	 * Returns the total time the account has spent offline.<br>
	 * The value is stored in milliseconds.
	 * @return the total rest time as a {@code long}.
	 */
	public long getAccumulatedRestTime()
	{
		return accumulatedRestTime;
	}
	
	/**
	 * Updates the total offline time for the account.<br>
	 * This value is stored in milliseconds.
	 * @param accumulatedRestTime The new rest time to set.
	 */
	public void setAccumulatedRestTime(long accumulatedRestTime)
	{
		this.accumulatedRestTime = accumulatedRestTime;
	}
	
	/**
	 * Retrieves the time when the account will expire.<br>
	 * This method returns the {@code expirationTime} value.
	 * @return The {@code Timestamp} representing the expiration date.
	 */
	public Timestamp getExpirationTime()
	{
		return expirationTime;
	}
	
	/**
	 * Sets the date and time when the account will expire.<br>
	 * This updates the {@code expirationTime} field.
	 * @param expirationTime The {@code Timestamp} representing the expiration date.
	 */
	public void setExpirationTime(Timestamp expirationTime)
	{
		this.expirationTime = expirationTime;
	}
	
	/**
	 * Retrieves the timestamp for when a penalty ends.<br>
	 * This method returns the {@code penaltyEnd} value.
	 * @return The {@code Timestamp} representing the end of the penalty.
	 */
	public Timestamp getPenaltyEnd()
	{
		return penaltyEnd;
	}
	
	/**
	 * Sets the end time for an account penalty.<br>
	 * This updates the {@code penaltyEnd} field with a new {@code Timestamp}.
	 * @param penaltyEnd The timestamp representing when the penalty expires.
	 */
	public void setPenaltyEnd(Timestamp penaltyEnd)
	{
		this.penaltyEnd = penaltyEnd;
	}
}
