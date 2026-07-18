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
package com.aionemu.loginserver.dao;

import java.util.ArrayList;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.loginserver.taskmanager.trigger.TaskFromDBTrigger;

/**
 * This class provides the Data Access Object (DAO) logic for retrieving tasks from the database.<br>
 * It serves as a base implementation for handling {@link TaskFromDBTrigger} data.<br>
 * Subclasses should implement specific queries to fetch task information.
 * @author Divinity, nrg
 */
public abstract class TaskFromDBDAO implements DAO
{
	/**
	 * Return all tasks from DB
	 * @return all tasks
	 */
	public abstract ArrayList<TaskFromDBTrigger> getAllTasks();
	
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return TaskFromDBDAO.class.getName();
	}
}
