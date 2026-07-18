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
package com.aionemu.gameserver.model.team.legion;

import java.sql.Timestamp;

/**
 * This class stores the historical data for a {@link com.aionemu.gameserver.model.team.legion.Legion}.<br>
 * It tracks changes and events related to legion progression over time.
 * @author Simple, xTz
 */
public class LegionHistory
{
	private final LegionHistoryType legionHistoryType;
	private String name = "";
	private final Timestamp time;
	private final int tabId;
	private String description = "";
	
	/**
	 * Creates a new instance of {@code LegionHistory}.<br>
	 * This constructor initializes all fields for a history record.
	 * @param legionHistoryType The type of the history entry.
	 * @param name The display name of the history event.
	 * @param time The timestamp when the event occurred.
	 * @param tabId The unique identifier for the UI tab.
	 * @param description A brief summary of the history details.
	 */
	public LegionHistory(LegionHistoryType legionHistoryType, String name, Timestamp time, int tabId, String description)
	{
		this.legionHistoryType = legionHistoryType;
		this.name = name;
		this.time = time;
		this.tabId = tabId;
		this.description = description;
	}
	
	/**
	 * Retrieves the history type of the {@code LegionHistory}.<br>
	 * This identifies what category of history this record belongs to.
	 * @return The {@code LegionHistoryType} associated with this instance.
	 */
	public LegionHistoryType getLegionHistoryType()
	{
		return legionHistoryType;
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
	 * Retrieves the current bonus time.<br>
	 * This method returns the {@code Timestamp} value stored in this object.
	 * @return The {@code Timestamp} representing the bonus time.
	 */
	public Timestamp getTime()
	{
		return time;
	}
	
	/**
	 * Retrieves the unique identifier for the tab.<br>
	 * This value is used to organize history entries.
	 * @return The {@code int} value of the tab ID.
	 */
	public int getTabId()
	{
		return tabId;
	}
	
	/**
	 * Retrieves the detailed description of the support request.<br>
	 * This method returns the {@code String} stored in the {@code description} field.
	 * @return The description text as a {@code String}.
	 */
	public String getDescription()
	{
		return description;
	}
}
