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
 * This class monitors and reacts to changes in the {@link AttackStatus}.<br>
 * It allows the system to perform specific actions whenever an attack's state is updated.
 * @author ATracer
 */
public class AttackStatusObserver extends AttackCalcObserver
{
	protected int value;
	protected AttackStatus status;
	
	/**
	 * Creates a new instance of {@link AttackStatusObserver}.<br>
	 * This constructor initializes the observer with specific values.
	 * @param value The numerical value to assign to the observer.
	 * @param status The {@code AttackStatus} state to set for this observer.
	 */
	public AttackStatusObserver(int value, AttackStatus status)
	{
		this.value = value;
		this.status = status;
	}
}
