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
package com.aionemu.loginserver.taskmanager.handler.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.loginserver.Shutdown;
import com.aionemu.loginserver.taskmanager.handler.TaskFromDBHandler;

/**
 * Handles the logic for restarting the server when a task is triggered from the database.<br>
 * It extends {@link TaskFromDBHandler} to provide specific behavior for restart commands.
 * @author Divinity, nrg
 */
public class RestartHandler extends TaskFromDBHandler
{
	private static final Logger log = LoggerFactory.getLogger(RestartHandler.class);
	
	/**
	 * Initiates the server restart process.<br>
	 * Sets the {@code setRestartOnly} flag to {@code true}.<br>
	 * Calls the {@code start} method to begin the shutdown sequence.
	 */
	@Override
	public void trigger()
	{
		log.info("Task[" + taskId + "] launched : restarting the server !");
		
		final Shutdown shutdown = Shutdown.getInstance();
		shutdown.setRestartOnly(true);
		shutdown.start();
	}
	
	/**
	 * Checks if the current task is valid.<br>
	 * This method always returns {@code true}.
	 * @return {@code true} if the task is valid.
	 */
	@Override
	public boolean isValid()
	{
		return true;
	}
}
