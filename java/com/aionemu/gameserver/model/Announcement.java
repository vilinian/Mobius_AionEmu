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
 * This class represents a system-wide announcement message.<br>
 * It holds the data required to display information to all players in the game.
 * @author Divinity
 */
public class Announcement
{
	private int id;
	private final String faction;
	private final String announce;
	private final String chatType;
	private final int delay;
	
	/**
	 * Creates a new {@link Announcement} object.<br>
	 * This constructor initializes the message content and delivery settings.<br>
	 * It automatically sets the faction to {@code ALL} if the provided value is not {@code ELYOS} or {@code ASMODIANS}.
	 * @param announce The text content of the announcement.
	 * @param faction The target faction, such as {@code ELYOS}, {@code ASMODIANS}, or {@code ALL}.
	 * @param chatType The type of chat channel to use for this message.
	 * @param delay The time in milliseconds to wait before sending the announcement.
	 */
	public Announcement(String announce, String faction, String chatType, int delay)
	{
		this.announce = announce;
		
		// Checking the right syntax
		if (!faction.equalsIgnoreCase("ELYOS") && !faction.equalsIgnoreCase("ASMODIANS"))
		{
			faction = "ALL";
		}
		
		this.faction = faction;
		this.chatType = chatType;
		this.delay = delay;
	}
	
	/**
	 * Creates a new {@link Announcement} object with a specific ID.<br>
	 * This constructor validates the faction name and sets all required fields.
	 * @param id The unique identifier for the announcement.
	 * @param announce The text content to be displayed.
	 * @param faction The target group, such as "ELYOS", "ASMODIANS", or "ALL".
	 * @param chatType The type of chat channel used for the message.
	 * @param delay The time interval before the announcement is shown.
	 */
	public Announcement(int id, String announce, String faction, String chatType, int delay)
	{
		this.id = id;
		this.announce = announce;
		
		// Checking the right syntax
		if (!faction.equalsIgnoreCase("ELYOS") && !faction.equalsIgnoreCase("ASMODIANS"))
		{
			faction = "ALL";
		}
		
		this.faction = faction;
		this.chatType = chatType;
		this.delay = delay;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link Announcement}.<br>
	 * Returns -1 if no ID has been assigned.
	 * @return The integer ID of the announcement.
	 */
	public int getId()
	{
		if (id != 0)
		{
			return id;
		}
		
		return -1;
	}
	
	/**
	 * Retrieves the text content of the announcement.<br>
	 * This method returns the {@code String} stored in the {@code announce} field.
	 * @return The announcement message as a {@code String}.
	 */
	public String getAnnounce()
	{
		return announce;
	}
	
	/**
	 * Retrieves the name of the faction associated with this announcement.<br>
	 * This value is stored as a {@code String}.
	 * @return The faction name.
	 */
	public String getFaction()
	{
		return faction;
	}
	
	/**
	 * Converts the faction string into a {@link Race} enum value.<br>
	 * This method checks if the faction is {@code ELYOS} or {@code ASMODIANS}.<br>
	 * It returns {@code null} if no match is found.
	 * @return The corresponding {@link Race} constant or {@code null}.
	 */
	public Race getFactionEnum()
	{
		if (faction.equalsIgnoreCase("ELYOS"))
		{
			return Race.ELYOS;
		}
		else if (faction.equalsIgnoreCase("ASMODIANS"))
		{
			return Race.ASMODIANS;
		}
		
		return null;
	}
	
	/**
	 * Retrieves the type of the announcement.<br>
	 * This method returns the value stored in the {@code chatType} field.
	 * @return The string representation of the announcement type.
	 */
	public String getType()
	{
		return chatType;
	}
	
	/**
	 * Retrieves the specific {@code ChatType} for this announcement.<br>
	 * This method converts the internal string value into a corresponding enum constant.
	 * @return The {@link ChatType} associated with this announcement.
	 */
	public ChatType getChatType()
	{
		if (chatType.equalsIgnoreCase("Yellow"))
		{
			return ChatType.YELLOW;
		}
		else if (chatType.equalsIgnoreCase("White"))
		{
			return ChatType.WHITE_CENTER;
		}
		else if (chatType.equalsIgnoreCase("Yellow"))
		{
			return ChatType.YELLOW_CENTER;
		}
		else if (chatType.equalsIgnoreCase("Shout"))
		{
			return ChatType.SHOUT;
		}
		else if (chatType.equalsIgnoreCase("Orange"))
		{
			return ChatType.GROUP_LEADER;
		}
		else
		{
			return ChatType.BRIGHT_YELLOW_CENTER;
		}
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
}
