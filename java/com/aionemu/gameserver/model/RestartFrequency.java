/**
 * This file is part of aion-lightning <aion-lightning.org>.
 * 
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model;

/**
 * Defines the frequency at which a server restart should occur.<br>
 * This enum is used to manage scheduled maintenance or automated reboots.
 * @author nrg
 */
public enum RestartFrequency
{
	NEVER(0),
	DAILY(1),
	WEEKLY(2),
	MONTHLY(3);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link RestartFrequency}.<br>
	 * This constructor maps the enum to a specific numeric identifier.
	 * @param id The unique integer value for this frequency.
	 */
	private RestartFrequency(int id)
	{
		this.id = id;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link RestartFrequency}.<br>
	 * This value is used to map the enum to a database entry.
	 * @return The integer {@code id} associated with this constant.
	 */
	public int getID()
	{
		return id;
	}
}
