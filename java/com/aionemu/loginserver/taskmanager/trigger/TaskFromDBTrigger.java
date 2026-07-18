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
package com.aionemu.loginserver.taskmanager.trigger;

import com.aionemu.loginserver.taskmanager.handler.TaskFromDBHandler;

/**
 * This class handles the logic for triggering tasks that are stored in the database.<br>
 * It serves as a base implementation for {@link TaskFromDBHandler} to execute scheduled actions.
 * @author nrg
 */
public abstract class TaskFromDBTrigger implements Runnable
{
	protected TaskFromDBHandler handlerToTrigger;
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
		return handlerToTrigger.getTaskId();
	}
	
	/**
	 * Retrieves the {@link TaskFromDBHandler} associated with this trigger.<br>
	 * This method returns the specific handler that should be executed.
	 * @return the {@code TaskFromDBHandler} instance to be triggered.
	 */
	public TaskFromDBHandler getHandlerToTrigger()
	{
		return handlerToTrigger;
	}
	
	/**
	 * Sets the specific {@link TaskFromDBHandler} that should be executed.<br>
	 * This defines which task logic will run when this trigger is activated.
	 * @param handlerToTrigger The {@code TaskFromDBHandler} instance to assign.
	 */
	public void setHandlerToTrigger(TaskFromDBHandler handlerToTrigger)
	{
		this.handlerToTrigger = handlerToTrigger;
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
	 * Updates the parameters for this trigger.<br>
	 * This method replaces the current {@code params} array with a new one.
	 * @param params The new array of {@code String} values to set.
	 */
	public void setParams(String[] params)
	{
		this.params = params;
	}
	
	/**
	 * Checks if this trigger is currently valid for execution.<br>
	 * It verifies that the {@link TaskFromDBHandler} is not {@code null}.<br>
	 * It also checks the internal state of the handler and the trigger itself.
	 * @return {@code true} if all conditions are met, {@code false} otherwise.
	 */
	public boolean isValid()
	{
		return (handlerToTrigger != null) && isValidTrigger() && handlerToTrigger.isValid();
	}
	
	public abstract boolean isValidTrigger();
	
	public abstract void initTrigger();
	
	@Override
	public void run()
	{
		handlerToTrigger.trigger();
	}
}
