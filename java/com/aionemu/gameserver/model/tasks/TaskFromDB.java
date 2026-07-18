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
package com.aionemu.gameserver.model.tasks;

import java.sql.Timestamp;

/**
 * Represents a task object retrieved from the database.<br>
 * This class maps raw SQL data into a usable {@code Task} model for the game server.
 * @author Divinity
 */
public class TaskFromDB
{
	private final int id;
	private final String name;
	private final String type;
	private final Timestamp lastActivation;
	private final String startTime;
	private final int delay;
	private String params[];
	
	/**
	 * Creates a new instance of {@code TaskFromDB}.<br>
	 * This constructor initializes the task properties from database values.<br>
	 * It also splits the {@code param} string into an array.
	 * @param id The unique identifier for the task.
	 * @param name The display name of the task.
	 * @param type The category or type of the task.
	 * @param lastActivation The timestamp of the last time the task ran.
	 * @param startTime The scheduled start time as a string.
	 * @param delay The delay interval between executions.
	 * @param param A space-separated string of parameters to be split into an array.
	 */
	public TaskFromDB(int id, String name, String type, Timestamp lastActivation, String startTime, int delay, String param)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		this.lastActivation = lastActivation;
		this.startTime = startTime;
		this.delay = delay;
		
		if (param != null)
		{
			params = param.split(" ");
		}
		else
		{
			params = new String[0];
		}
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the type of the task.<br>
	 * This method returns the value stored in the {@code type} field.
	 * @return The string representation of the task type.
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the time when this task was last activated.<br>
	 * This value is stored as a {@code Timestamp}.
	 * @return the {@code Timestamp} of the last activation.
	 */
	public Timestamp getLastActivation()
	{
		return lastActivation;
	}
	
	/**
	 * Retrieves the start time of the task.<br>
	 * This value is stored as a {@code String}.
	 * @return The {@code String} representation of the start time.
	 */
	public String getStartTime()
	{
		return startTime;
	}
	
	/**
	 * Retrieves the time delay for this announcement.<br>
	 * This value is stored as an {@code int}.
	 * @return The current delay value.
	 */
	public int getDelay()
	{
		return delay;
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
}
