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
package com.aionemu.gameserver.model.gameobjects.player.achievement;

/**
 * Represents the possible states of a player's achievement.<br>
 * This enum tracks whether an achievement is locked, in progress, or completed.
 */
public enum AchievementState
{
	
	START(1),
	REWARD(2),
	COMPLETE(3);
	
	private final int value;
	
	/**
	 * Creates a new instance of {@link AchievementState}.<br>
	 * This constructor assigns the internal integer value.
	 * @param value The numeric identifier for the state.
	 */
	private AchievementState(int value)
	{
		this.value = value;
	}
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return value;
	}
	
	/**
	 * Retrieves an {@link AchievementState} based on its unique ID.<br>
	 * This method searches through all available states to find a match.<br>
	 * It throws an exception if the provided {@code classId} is not found.
	 * @param classId The integer ID of the achievement state to retrieve.
	 * @return The matching {@link AchievementState} object.
	 */
	public static AchievementState getPlayerClassById(int classId)
	{
		for (AchievementState pc : AchievementState.values())
		{
			if (pc.getValue() != classId)
			{
				continue;
			}
			
			return pc;
		}
		
		throw new IllegalArgumentException("There is no AchievementState with id " + classId);
	}
}
