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
package com.aionemu.gameserver.model.templates.event;

/**
 * This class defines the maximum number of times an event can occur within a single day.<br>
 * It is used to limit the frequency of specific game events.
 * @author Falke_34
 */
public class MaxCountOfDay
{
	private int thisCount;
	
	/**
	 * Creates a new instance of {@link MaxCountOfDay}.<br>
	 * Sets the initial count for the day.
	 * @param thisCount The starting value for the daily count.
	 */
	public MaxCountOfDay(int thisCount)
	{
		this.thisCount = thisCount;
	}
	
	/**
	 * Retrieves the current count value.<br>
	 * This method returns the integer stored in {@code thisCount}.
	 * @return The current count as an {@code int}.
	 */
	public int getThisCount()
	{
		return thisCount;
	}
	
	/**
	 * Updates the current count value.<br>
	 * This method sets the {@code thisCount} field to a new value.
	 * @param thisCount The new integer value to assign.
	 */
	public void setThisCount(int thisCount)
	{
		this.thisCount = thisCount;
	}
}
