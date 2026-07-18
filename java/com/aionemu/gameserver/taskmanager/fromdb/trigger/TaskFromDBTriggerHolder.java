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
package com.aionemu.gameserver.taskmanager.fromdb.trigger;

/**
 * This class acts as a holder for triggers loaded from the database.<br>
 * It allows the system to manage and access {@link com.aionemu.gameserver.taskmanager.fromdb.trigger.TaskFromDBTrigger} instances efficiently.
 * @author nrg
 */
public enum TaskFromDBTriggerHolder
{
	FIXED_IN_TIME(FixedInTimeTrigger.class),
	AFTER_RESTART(AfterRestartTrigger.class);
	
	private final Class<? extends TaskFromDBTrigger> triggerClass;
	
	/**
	 * This is a private constructor for the {@link TaskFromDBTriggerHolder} enum.<br>
	 * It initializes the internal {@code triggerClass} field.
	 * @param triggerClass The specific {@link TaskFromDBTrigger} class to associate with this holder.
	 */
	private TaskFromDBTriggerHolder(Class<? extends TaskFromDBTrigger> triggerClass)
	{
		this.triggerClass = triggerClass;
	}
	
	/**
	 * Retrieves the specific class associated with this trigger type.<br>
	 * This method returns a subclass of {@link TaskFromDBTrigger}.
	 * @return The {@code Class} object for the trigger.
	 */
	public Class<? extends TaskFromDBTrigger> getTriggerClass()
	{
		return triggerClass;
	}
}
