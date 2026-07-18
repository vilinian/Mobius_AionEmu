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
 * Defines the different categories of achievements available to players.<br>
 * This enum is used to classify and identify specific types of player milestones.
 */
public enum AchievementType
{
	
	DAILY(1),
	WEEKLY(2),
	EVENT_MAIN(4),
	EVENT_SUB(5);
	
	private final int value;
	
	/**
	 * Creates a new instance of {@link AchievementType}.<br>
	 * This constructor assigns the internal integer value.
	 * @param value The numeric identifier for the achievement type.
	 */
	private AchievementType(int value)
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
	 * Converts a string name into its corresponding {@link AchievementType}.<br>
	 * This method searches through all available types.<br>
	 * It returns {@code null} if no match is found.
	 * @param fieldName The string name of the achievement type to look up.
	 * @return The matching {@code AchievementType} or {@code null}.
	 */
	public static AchievementType getAchievementTypeByString(String fieldName)
	{
		for (AchievementType at : AchievementType.values())
		{
			if (!at.toString().equals(fieldName))
			{
				continue;
			}
			
			return at;
		}
		
		return null;
	}
	
	/**
	 * Finds an {@link AchievementType} based on its unique ID.<br>
	 * This method searches through all available types to find a match.<br>
	 * It throws an exception if the provided {@code classId} is not found.
	 * @param classId The integer ID of the player class to look up.
	 * @return The matching {@link AchievementType} object.
	 */
	public static AchievementType getPlayerClassById(int classId)
	{
		for (AchievementType pc : AchievementType.values())
		{
			if (pc.getValue() != classId)
			{
				continue;
			}
			
			return pc;
		}
		
		throw new IllegalArgumentException("There is no player class with id " + classId);
	}
}
