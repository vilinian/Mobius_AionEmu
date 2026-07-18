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

import com.aionemu.gameserver.controllers.attack.AttackStatus;

/**
 * This class observes and handles critical status updates for attackers.<br>
 * It extends {@link AttackCalcObserver} to monitor specific attack states.
 * @author kecimis
 */
public class AttackerCriticalStatusObserver extends AttackCalcObserver
{
	protected AttackerCriticalStatus acStatus = null;
	protected AttackStatus status;
	
	/**
	 * Creates a new instance of {@code AttackerCriticalStatusObserver}.<br>
	 * This constructor initializes the observer with specific attack criteria.<br>
	 * It sets up the internal {@link AttackerCriticalStatus} object.
	 * @param status The {@code AttackStatus} type to monitor.
	 * @param count The number of times the condition must be met.
	 * @param value The numerical value associated with the status.
	 * @param isPercent Determines if the {@code value} represents a percentage.
	 */
	public AttackerCriticalStatusObserver(AttackStatus status, int count, int value, boolean isPercent)
	{
		this.status = status;
		acStatus = new AttackerCriticalStatus(count, value, isPercent);
	}
	
	/**
	 * Retrieves the current count from the {@code acStatus} object.<br>
	 * This method calls {@code getCount} to get the value.
	 * @return The current count as an {@code int}.
	 */
	public int getCount()
	{
		return acStatus.getCount();
	}
	
	/**
	 * Reduces the current count by {@code 1}.<br>
	 * This method updates the value stored in {@code acStatus}.
	 */
	public void decreaseCount()
	{
		acStatus.setCount((acStatus.getCount() - 1));
	}
}
