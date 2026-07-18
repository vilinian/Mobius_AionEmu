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
package com.aionemu.gameserver.model.account;

/**
 * This class stores the total online and rest time for a specific user account.<br>
 * It provides data related to how much time an account has spent active or inactive.
 * @author EvilSpirit
 */
public class AccountTime
{
	/**
	 * Accumulated online time in millis
	 */
	private long accumulatedOnlineTime;
	/**
	 * Accumulated rest(offline) time in millis
	 */
	private long accumulatedRestTime;
	
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
	 * Retrieves the total time the account has spent online.<br>
	 * This value is converted from milliseconds into hours.<br>
	 * It uses the {@code getAccumulatedOnlineTime} value for calculation.
	 * @return The total number of accumulated online hours as an {@code int}.
	 */
	public int getAccumulatedOnlineHours()
	{
		return toHours(accumulatedOnlineTime);
	}
	
	/**
	 * Retrieves the total time the account has spent online.<br>
	 * This value is converted from milliseconds into minutes.<br>
	 * It uses the {@code toMinutes} helper method for conversion.
	 * @return The total accumulated online time in minutes.
	 */
	public int getAccumulatedOnlineMinutes()
	{
		return toMinutes(accumulatedOnlineTime);
	}
	
	/**
	 * Retrieves the total rest time for the account.<br>
	 * This value is converted from milliseconds into hours.<br>
	 * It uses the {@code getAccumulatedRestTime} value as a base.
	 * @return The total number of accumulated rest hours as an {@code int}.
	 */
	public int getAccumulatedRestHours()
	{
		return toHours(accumulatedRestTime);
	}
	
	/**
	 * Retrieves the total rest time for the account.<br>
	 * This value is converted from milliseconds into minutes.<br>
	 * It uses the {@code getAccumulatedRestTime} value as the source.
	 * @return The total number of accumulated rest minutes as an {@code int}.
	 */
	public int getAccumulatedRestMinutes()
	{
		return toMinutes(accumulatedRestTime);
	}
	
	/**
	 * Converts a duration from milliseconds to hours.<br>
	 * This method performs integer division to find the total number of full hours.
	 * @param millis The time in {@code long} milliseconds to convert.
	 * @return The equivalent time as an {@code int} representing total hours.
	 */
	private static int toHours(long millis)
	{
		return (int) (millis / 1000) / 3600;
	}
	
	/**
	 * Converts a duration from milliseconds to minutes.<br>
	 * This method calculates the remaining minutes within an hour.
	 * @param millis The time in {@code long} milliseconds.
	 * @return The resulting value as an {@code int}.
	 */
	private static int toMinutes(long millis)
	{
		return (int) ((millis / 1000) % 3600) / 60;
	}
}
