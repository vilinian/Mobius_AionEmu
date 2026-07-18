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
package com.aionemu.gameserver.model.templates.staticdoor;

import java.util.EnumSet;

/**
 * Represents the possible states of a {@code StaticDoor}.<br>
 * This enum defines whether a door is open, closed, or locked.
 * @author Rolandas
 */
public enum StaticDoorState
{
	NONE(0),
	OPENED(1 << 0),
	CLICKABLE(1 << 1),
	CLOSEABLE(1 << 2),
	ONEWAY(1 << 3);
	
	private final int flag;
	
	/**
	 * Creates a new instance of {@link StaticDoorState}.<br>
	 * This constructor assigns the bitwise value to the internal field.
	 * @param flag The integer value representing the door state.
	 */
	private StaticDoorState(int flag)
	{
		this.flag = flag;
	}
	
	/**
	 * Retrieves the bitmask value for this specific {@code StaticDoorState}.<br>
	 * This value is used to check if a state is active.
	 * @return The integer representation of the door state flag.
	 */
	public int getFlag()
	{
		return flag;
	}
	
	/**
	 * Updates the {@code state} set based on the provided bitwise {@code flags}.<br>
	 * This method synchronizes the {@link EnumSet} with the active door properties.<br>
	 * It removes states that are not present in the {@code flags} and adds those that are.
	 * @param flags The integer bitmask representing the active door states.
	 * @param state The {@code EnumSet} of {@link StaticDoorState} to be updated.
	 */
	public static void setStates(int flags, EnumSet<StaticDoorState> state)
	{
		for (StaticDoorState states : StaticDoorState.values())
		{
			if (states == NONE)
			{
				continue;
			}
			
			if ((flags & states.flag) == 0)
			{
				state.remove(states);
			}
			else
			{
				state.add(states);
			}
		}
	}
	
	/**
	 * Converts a set of {@link StaticDoorState} constants into a single integer bitmask.<br>
	 * This method iterates through all possible states and combines their flags.
	 * @param doorStates The set of active door states to process.
	 * @return The resulting integer representing the combined bitmask.
	 */
	public static int getFlags(EnumSet<StaticDoorState> doorStates)
	{
		int result = 0;
		for (StaticDoorState state : StaticDoorState.values())
		{
			if (state == NONE)
			{
				continue;
			}
			
			if (doorStates.contains(state))
			{
				result |= state.flag;
			}
		}
		
		return result;
	}
}
