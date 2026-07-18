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

/**
 * Represents a single entry within an {@code EventWindow}.<br>
 * This class stores the data required to display specific event information to the player.
 * @author Ghostfur (Aion-Unique)
 */
public class EventWindowEntry
{
	private final int id;
	private final Timestamp lastStamp;
	private final int elapsed;
	
	/**
	 * Creates a new instance of {@link EventWindowEntry}.<br>
	 * This constructor initializes the entry with an ID, a timestamp, and an elapsed time.
	 * @param id The unique identifier for the event.
	 * @param timestamp The {@code Timestamp} when the event occurred.
	 * @param elapsed The amount of time that has passed since the event.
	 */
	public EventWindowEntry(int id, Timestamp timestamp, int elapsed)
	{
		this.id = id;
		lastStamp = timestamp;
		this.elapsed = elapsed;
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
	 * Retrieves the most recent timestamp for this entry.<br>
	 * This value represents when the event was last updated.
	 * @return the {@code Timestamp} of the last update.
	 */
	public Timestamp getLastStamp()
	{
		return lastStamp;
	}
	
	/**
	 * Retrieves the total time that has passed.<br>
	 * This value is stored in the {@code elapsed} field.
	 * @return The number of units representing the elapsed time.
	 */
	public int getElapsed()
	{
		return elapsed;
	}
}
