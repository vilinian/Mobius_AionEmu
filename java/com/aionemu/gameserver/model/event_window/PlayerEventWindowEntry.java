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
package com.aionemu.gameserver.model.event_window;

import java.sql.Timestamp;

import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * Represents an entry within a player's event window.<br>
 * This class extends {@link EventWindowEntry} to store specific data for individual events.
 * @author Ghostfur (Aion-Unique)
 */
public class PlayerEventWindowEntry extends EventWindowEntry
{
	private PersistentState persistentState;
	
	/**
	 * Creates a new instance of {@link PlayerEventWindowEntry}.<br>
	 * This constructor initializes the entry with time and state data.
	 * @param time The starting time for the event.
	 * @param timestamp The specific {@code Timestamp} of the event.
	 * @param remaining The amount of time left in the event.
	 * @param persistentState The {@link PersistentState} associated with this entry.
	 */
	public PlayerEventWindowEntry(int time, Timestamp timestamp, int remaining, PersistentState persistentState)
	{
		super(time, timestamp, remaining);
		this.persistentState = persistentState;
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this quest.<br>
	 * This method prevents changing from {@code PersistentState.NEW} to {@code PersistentState.UPDATE_REQUIRED}.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch (persistentState)
		{
			case DELETED:
			{
				if (this.persistentState == PersistentState.NEW)
				{
					this.persistentState = PersistentState.NOACTION;
					break;
				}
				
				this.persistentState = PersistentState.DELETED;
				break;
			}
			case UPDATE_REQUIRED:
			{
				if (this.persistentState == PersistentState.NEW)
				{
					this.persistentState = PersistentState.UPDATE_REQUIRED;
					break;
				}
				break;
			}
			case NOACTION:
			{
				break;
			}
			default:
			{
				this.persistentState = persistentState;
				break;
			}
		}
	}
}
