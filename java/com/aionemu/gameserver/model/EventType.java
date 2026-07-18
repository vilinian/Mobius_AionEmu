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
package com.aionemu.gameserver.model;

/**
 * Defines the different types of events that can occur within the game world.<br>
 * This enum is used by {@code EventManager} to categorize and handle specific actions.
 * @author xTz
 */
public enum EventType
{
	NONE(0, ""),
	CHRISTMAS(1 << 0, "christmas"), // 1
	HALLOWEEN(1 << 1, "halloween"), // 2
	VALENTINE(1 << 2, "valentine"), // 4
	BRAXCAFE(1 << 3, "braxcafe"); // 8
	
	private final int id;
	private final String theme;
	
	/**
	 * Creates a new instance of {@link EventType}.<br>
	 * This constructor initializes the internal ID and theme.
	 * @param id The unique integer identifier for the event type.
	 * @param theme The display name or category string for the event.
	 */
	private EventType(int id, String theme)
	{
		this.id = id;
		this.theme = theme;
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
	 * Retrieves the name of the current theme.<br>
	 * This value is used to identify the specific event style.
	 * @return The {@code String} representation of the theme.
	 */
	public String getTheme()
	{
		return theme;
	}
	
	/**
	 * Finds an {@link EventType} based on a given theme name.<br>
	 * It searches through all available types to find a match.<br>
	 * If no match is found, it returns {@code EventType.NONE}.
	 * @param theme The string name of the theme to look for.
	 * @return The matching {@link EventType} or {@code EventType.NONE}.
	 */
	public static EventType getEventType(String theme)
	{
		for (EventType type : values())
		{
			if (theme.equals(type.getTheme()))
			{
				return type;
			}
		}
		
		return EventType.NONE;
	}
}
