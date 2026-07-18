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
package com.aionemu.gameserver.model.house;

/**
 * Defines the various permission levels available for house management.<br>
 * This enum is used to control what actions a player can perform within a {@link com.aionemu.gameserver.model.house.House}.
 * @author Rolandas
 */
public enum HousePermissions
{
	
	NOT_SET(0),
	SHOW_OWNER(1 << 0),
	DOOR_OPENED_ALL(1 << 8),
	DOOR_OPENED_FRIENDS(2 << 8),
	DOOR_CLOSED(3 << 8);
	
	private final int value;
	
	/**
	 * Creates a new instance of {@link HousePermissions}.<br>
	 * This constructor assigns the bitwise value to the internal field.
	 * @param value The integer value representing the specific permission.
	 */
	private HousePermissions(int value)
	{
		this.value = value;
	}
	
	/**
	 * Retrieves the packet value for this permission.<br>
	 * It adjusts the internal {@code value} based on its magnitude.<br>
	 * This is used to format data for network transmission.
	 * @return The processed {@code byte} value.
	 */
	public byte getPacketValue()
	{
		int result = value;
		if (value > 1)
		{
			result >>= 8;
		}
		
		return (byte) result;
	}
	
	/**
	 * Converts a raw integer value into a {@link HousePermissions} constant.<br>
	 * This method shifts the input {@code value} by 8 bits before comparison.<br>
	 * It returns {@code HousePermissions.NOT_SET} if no match is found.
	 * @param value The raw integer received from the network packet.
	 * @return The corresponding {@link HousePermissions} enum constant.
	 */
	public static HousePermissions getPacketDoorState(int value)
	{
		value <<= 8;
		for (HousePermissions perm : HousePermissions.values())
		{
			if (value == perm.value)
			{
				return perm;
			}
		}
		
		return NOT_SET;
	}
	
	/**
	 * Converts a raw integer value into a {@link HousePermissions} constant.<br>
	 * This method masks the input to only look at specific bits.<br>
	 * It returns {@code NOT_SET} if no matching permission is found.
	 * @param value The raw integer value to check.
	 * @return The corresponding {@code HousePermissions} enum constant.
	 */
	public static HousePermissions getDoorState(int value)
	{
		value &= 0xFF00;
		for (HousePermissions perm : HousePermissions.values())
		{
			if (value == perm.value)
			{
				return perm;
			}
		}
		
		return NOT_SET;
	}
	
	/**
	 * Updates the door state within a house.<br>
	 * This method combines an existing {@code value} with a new {@link HousePermissions} state.<br>
	 * It preserves the lower 8 bits of the original input.
	 * @param value The original integer value containing the base data.
	 * @param doorState The {@link HousePermissions} state to apply to the door.
	 * @return The new combined integer value.
	 */
	public static int setDoorState(int value, HousePermissions doorState)
	{
		final int state = doorState.value & 0xFF00;
		return (value & 0x00FF) | state;
	}
	
	/**
	 * This method determines the notice state based on a bitmask.<br>
	 * It checks if the {@code value} contains the {@code SHOW_OWNER} flag.<br>
	 * If the flag is present, it returns {@code SHOW_OWNER}.<br>
	 * Otherwise, it returns {@code NOT_SET}.
	 * @param value The integer bitmask to check.
	 * @return The corresponding {@code HousePermissions} state.
	 */
	public static HousePermissions getNoticeState(int value)
	{
		if ((value & SHOW_OWNER.value) == SHOW_OWNER.value)
		{
			return SHOW_OWNER;
		}
		
		return NOT_SET;
	}
	
	/**
	 * Updates the notice state for a specific house.<br>
	 * This method modifies the {@code value} based on the provided {@link HousePermissions}.<br>
	 * It returns the new integer result after applying the bitwise operations.
	 * @param value The original integer value to be modified.
	 * @param noticeState The {@code HousePermissions} state to apply.
	 * @return The updated integer value.
	 */
	public static int setNoticeState(int value, HousePermissions noticeState)
	{
		if (noticeState == NOT_SET)
		{
			return value & 0xFF00;
		}
		
		final int state = noticeState.value & 0xFF;
		return state | value;
	}
}
