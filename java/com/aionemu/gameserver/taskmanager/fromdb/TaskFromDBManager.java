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
package com.aionemu.gameserver.taskmanager.fromdb;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.TaskFromDBDAO;
import com.aionemu.gameserver.taskmanager.fromdb.trigger.TaskFromDBTrigger;

/**
 * This class manages the loading and execution of tasks retrieved from the database.<br>
 * It handles {@link TaskFromDBDAO} operations to fetch data and initializes {@link TaskFromDBTrigger} objects.
 * @author nrg
 */
public class TaskFromDBManager
{
	private static final Logger log = LoggerFactory.getLogger(TaskFromDBManager.class);
	private final ArrayList<TaskFromDBTrigger> tasksList;
	
	/**
	 * Private constructor for initializing the {@code TaskFromDBManager}.<br>
	 * It loads all tasks from the database using {@link TaskFromDBDAO}.<br>
	 * It then registers the task instances into the internal list.
	 */
	private TaskFromDBManager()
	{
		tasksList = getDAO().getAllTasks();
		log.info("Loaded " + tasksList.size() + " task" + (tasksList.size() > 1 ? "s" : "") + " from the database");
		
		registerTaskInstances();
	}
	
	/**
	 * Initializes all valid tasks loaded from the database.<br>
	 * It iterates through the {@code tasksList}.<br>
	 * Each valid {@link TaskFromDBTrigger} calls its {@code initTrigger()} method.
	 */
	private void registerTaskInstances()
	{
		// For all tasks from DB
		for (TaskFromDBTrigger trigger : tasksList)
		{
			if (trigger.isValid())
			{
				trigger.initTrigger();
			}
			else
			{
				log.error("Cannot load task from db with ID: " + trigger.getTaskId());
			}
		}
	}
	
	/**
	 * Retrieves the database access object for tasks.<br>
	 * This method uses {@link DAOManager} to fetch the correct instance.
	 * @return The {@code TaskFromDBDAO} instance used for database operations.
	 */
	private static TaskFromDBDAO getDAO()
	{
		return DAOManager.getDAO(TaskFromDBDAO.class);
	}
	
	/**
	 * Provides the global instance of the {@link TaskFromDBManager}.<br>
	 * This method follows the Singleton pattern.
	 * @return The singleton instance of {@code TaskFromDBManager}.
	 */
	public static TaskFromDBManager getInstance()
	{
		return TaskFromDBManager.SingletonHolder.instance;
	}
	
	/**
	 * SingletonHolder
	 */
	private static class SingletonHolder
	{
		protected static final TaskFromDBManager instance = new TaskFromDBManager();
	}
}
