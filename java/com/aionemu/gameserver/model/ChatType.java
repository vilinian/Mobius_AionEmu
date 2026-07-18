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
 * This enum defines the different types of chat messages supported by {@code Aion}.<br>
 * It is used to categorize communication between players and the server.
 * @author SoulKeeper, Imaginary
 */
public enum ChatType
{
	NORMAL(0x00), // Normal chat (White)
	SHOUT(0x03), // Shout chat (Orange)
	WHISPER(0x04), // Whisper chat (Green)
	GROUP(0x05), // Group chat (Blue)
	ALLIANCE(0x06), // Alliance chat (Aqua)
	GROUP_LEADER(0x07), // Group Leader chat (???)
	LEAGUE(0x08), // League chat (Dark Blue)
	LEAGUE_ALERT(0x09), // League chat (Orange)
	LEGION(0x0A), // Legion chat (Green)
	COMMAND(0x18), // Command chat (Yellow)
	
	CH1(0x0E),
	CH2(0x0F),
	CH3(0x10),
	CH4(0x11),
	CH5(0x12),
	CH6(0x13),
	CH7(0x14),
	CH8(0x15),
	CH9(0x16),
	CH10(0x17),
	/**
	 * Global chat types
	 */
	UNION_WAR(0x19, true), // Union War message (Blue)
	WHITE(0x23, true), // System message (White), visible in "All" chat thumbnail only !
	YELLOW(0x24, true), // System message (Yellow), visible in "All" chat thumbnail only !
	BRIGHT_YELLOW(0x25, true), // System message (Light Yellow), visible in "All" chat thumbnail only !
	WHITE_CENTER(0x26, true), // Periodic Notice (White && Box on screen center)
	YELLOW_CENTER(0x27, true), // Periodic Announcement(Yellow && Box on screen center)
	BRIGHT_YELLOW_CENTER(0x28, true), // System Notice (Light Yellow && Box on screen center)
	BRIGHT_YELLOW_CENTER_NEW(0x29, true), // Bright Yellow Center New
	GMRESPONSE(0x1C); // GM Response
	
	private final int intValue;
	private final boolean sysMsg;
	
	/**
	 * Creates a new {@link ChatType} instance.<br>
	 * This constructor sets the system message flag to {@code false}.
	 * @param intValue The integer value representing the chat type.
	 */
	private ChatType(int intValue)
	{
		this(intValue, false);
	}
	
	/**
	 * Converts the current {@code ChatType} to its integer value.<br>
	 * This method returns the raw numeric ID associated with the enum constant.
	 * @return The integer representation of this chat type.
	 */
	public int toInteger()
	{
		return intValue;
	}
	
	/**
	 * Converts an integer value into its corresponding {@link ChatType}.<br>
	 * This method searches through all available types to find a match.<br>
	 * It throws an exception if the provided number is not recognized.
	 * @param integerValue The numeric ID of the chat type to look up.
	 * @return The matching {@code ChatType} enum constant.
	 * @throws IllegalArgumentException If no {@code ChatType} matches the given value.
	 */
	public static ChatType getChatTypeByInt(int integerValue) throws IllegalArgumentException
	{
		for (ChatType ct : ChatType.values())
		{
			if (ct.toInteger() == integerValue)
			{
				return ct;
			}
		}
		
		throw new IllegalArgumentException("Unsupported chat type: " + integerValue);
	}
	
	/**
	 * Creates a new {@link ChatType} with a specific integer value and system message flag.<br>
	 * This constructor is used for types that require both an ID and a boolean status.
	 * @param intValue The numeric identifier for the chat type.
	 * @param sysMsg The flag indicating if this is a system message.
	 */
	private ChatType(int intValue, boolean sysMsg)
	{
		this.intValue = intValue;
		this.sysMsg = sysMsg;
	}
	
	/**
	 * Checks if the chat type is a system message.<br>
	 * This method returns {@code true} for types like {@code WHITE}.<br>
	 * It returns {@code false} for standard player communications.
	 * @return {@code true} if it is a system message, otherwise {@code false}.
	 */
	public boolean isSysMsg()
	{
		return sysMsg;
	}
}
