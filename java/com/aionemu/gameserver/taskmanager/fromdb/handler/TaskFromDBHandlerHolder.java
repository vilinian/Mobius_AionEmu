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
package com.aionemu.gameserver.taskmanager.fromdb.handler;

/**
 * This class serves as a holder for {@link TaskFromDBHandler} instances.<br>
 * It provides a centralized way to manage and access different database task handlers.
 * @author nrg
 */
public enum TaskFromDBHandlerHolder
{
	SHUTDOWN(ShutdownHandler.class),
	FATIGUE(FatigueHandler.class),
	RESTART(RestartHandler.class);
	
	private final Class<? extends TaskFromDBHandler> taskClass;
	
	/**
	 * This is a private constructor for the {@link TaskFromDBHandlerHolder} enum.<br>
	 * It assigns the specific handler class to each enum constant.
	 * @param taskClass The {@code Class} type of the {@link TaskFromDBHandler} implementation.
	 */
	private TaskFromDBHandlerHolder(Class<? extends TaskFromDBHandler> taskClass)
	{
		this.taskClass = taskClass;
	}
	
	/**
	 * Retrieves the specific handler class associated with this enum constant.<br>
	 * This method returns a subclass of {@link TaskFromDBHandler}.
	 * @return The {@code Class} object for the corresponding task handler.
	 */
	public Class<? extends TaskFromDBHandler> getTaskClass()
	{
		return taskClass;
	}
}
