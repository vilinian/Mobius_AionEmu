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
package com.aionemu.gameserver.model.instance;

/**
 * Defines the different types of scores used to evaluate and rank game instances.<br>
 * This enum helps the system categorize various scoring metrics for instance management.
 * @author xTz
 */
public enum InstanceScoreType
{
	PREPARING(1 * 1024 * 1024), // 1048576
	START_PROGRESS(2 * 1024 * 1024), // 2097152
	END_PROGRESS(3 * 1024 * 1024); // 3145728
	
	private final int id;
	
	/**
	 * Creates a new instance of this {@link InstanceScoreType}.<br>
	 * It assigns the provided unique identifier to the enum constant.
	 * @param id The unique integer ID for the score type.
	 */
	private InstanceScoreType(int id)
	{
		this.id = id;
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
	 * Checks if the current instance type is in the preparing state.<br>
	 * This method compares the internal {@code id} to the value {@code 1048576}.
	 * @return {@code true} if the status is preparing, otherwise {@code false}.
	 */
	public boolean isPreparing()
	{
		return id == 1048576;
	}
	
	/**
	 * Checks if the current instance type is {@code START_PROGRESS}.<br>
	 * It compares the internal ID against the value {@code 2097152}.
	 * @return {@code true} if this is the start progress type, otherwise {@code false}.
	 */
	public boolean isStartProgress()
	{
		return id == 2097152;
	}
	
	/**
	 * Checks if the current instance type is {@code END_PROGRESS}.<br>
	 * It compares the internal ID against the value {@code 3145728}.
	 * @return {@code true} if the type is {@code END_PROGRESS}, otherwise {@code false}.
	 */
	public boolean isEndProgress()
	{
		return id == 3145728;
	}
}
