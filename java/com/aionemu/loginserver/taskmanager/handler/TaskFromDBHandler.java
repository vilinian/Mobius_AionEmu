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
package com.aionemu.loginserver.taskmanager.handler;

/**
 * This class serves as a base handler for processing tasks retrieved from the database.<br>
 * It provides the necessary logic to execute {@code Task} objects stored in the system.
 * @author nrg
 */
public abstract class TaskFromDBHandler
{
	protected int taskId;
	protected String[] params =
	{
		""
	};
	
	/**
	 * Retrieves the unique identifier for this task.<br>
	 * This ID corresponds to the {@code taskId} defined in the template.
	 * @return The integer ID of the task.
	 */
	public int getTaskId()
	{
		return taskId;
	}
	
	/**
	 * Sets the unique identifier for the task.<br>
	 * This value is used to identify specific tasks in the database.
	 * @param taskId The {@code int} ID of the task to set.
	 */
	public void setTaskId(int taskId)
	{
		this.taskId = taskId;
	}
	
	/**
	 * Retrieves the parameters associated with this task.<br>
	 * This method returns the internal {@code params} array.
	 * @return an array of {@code String} values representing the task parameters.
	 */
	public String[] getParams()
	{
		return params;
	}
	
	/**
	 * Updates the task parameters.<br>
	 * This method sets the {@code params} array to a new value.
	 * @param params The new array of {@code String} values to store.
	 */
	public void setParams(String params[])
	{
		this.params = params;
	}
	
	/**
	 * Check if the task's parameters are valid
	 * @return true if valid, false otherwise
	 */
	public abstract boolean isValid();
	
	/**
	 * Triggers the handlers functions
	 */
	public abstract void trigger();
}
