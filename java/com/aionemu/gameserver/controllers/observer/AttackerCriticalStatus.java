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
package com.aionemu.gameserver.controllers.observer;

/**
 * This class manages the critical status of an attacker during combat.<br>
 * It is used by the {@code Observer} system to track specific attack states.
 * @author kecimis
 */
public class AttackerCriticalStatus
{
	private boolean result = false;
	private int count;
	private int value;
	private boolean isPercent;
	
	/**
	 * Creates a new instance of {@link AttackerCriticalStatus}.<br>
	 * This constructor sets the initial status of the result.
	 * @param result The boolean value to set for the result field.
	 */
	public AttackerCriticalStatus(boolean result)
	{
		this.result = result;
	}
	
	/**
	 * Creates a new {@link AttackerCriticalStatus} object.<br>
	 * This constructor initializes the status with specific values.
	 * @param count The number of occurrences for the status.
	 * @param value The numerical amount associated with the status.
	 * @param isPercent A boolean indicating if the {@code value} represents a percentage.
	 */
	public AttackerCriticalStatus(int count, int value, boolean isPercent)
	{
		this.count = count;
		this.value = value;
		this.isPercent = isPercent;
	}
	
	/**
	 * Retrieves the current count value.<br>
	 * This method returns the integer stored in the {@code count} field.
	 * @return The current count as an {@code int}.
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Updates the current count value.<br>
	 * This method sets the {@code count} field to a new integer.
	 * @param count The new value to assign to the count.
	 */
	public void setCount(int count)
	{
		this.count = count;
	}
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return value;
	}
	
	/**
	 * Checks if the status value is represented as a percentage.<br>
	 * This method returns the current state of the {@code isPercent} flag.
	 * @return {@code true} if the value is a percentage, {@code false} otherwise.
	 */
	public boolean isPercent()
	{
		return isPercent;
	}
	
	/**
	 * Checks if the current status represents a successful result.<br>
	 * This method returns the value of the {@code result} field.
	 * @return {@code true} if the result is successful, {@code false} otherwise.
	 */
	public boolean isResult()
	{
		return result;
	}
	
	/**
	 * Updates the {@code result} status of this object.<br>
	 * This method sets the internal state to either {@code true} or {@code false}.
	 * @param result The new boolean value to assign.
	 */
	public void setResult(boolean result)
	{
		this.result = result;
	}
}
