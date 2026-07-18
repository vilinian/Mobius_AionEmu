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
package com.aionemu.gameserver.utils.stats;

/**
 * Defines the various types of experience point (XP) loss that can occur in the game.<br>
 * This enumeration is used to categorize different reasons for losing {@code XP} during gameplay.
 * @author ATracer, Jangan
 */
public enum XPLossEnum
{
	/*
	 * LEVEL_6(6, 1.0), LEVEL_30(30, 1.0), LEVEL_40(40, 0.35),
	 */
	LEVEL_50(50, 0.25),
	LEVEL_55(55, 0.25),
	LEVEL_60(60, 0.25),
	LEVEL_65(65, 0.25),
	LEVEL_70(70, 0.25),
	LEVEL_75(75, 0.25),
	LEVEL_80(80, 0.25);
	
	private final int level;
	private final double param;
	
	/**
	 * Creates a new instance of {@link XPLossEnum}.<br>
	 * This constructor initializes the level and parameter values.
	 * @param level The character level associated with this loss type.
	 * @param param The multiplier used to calculate experience loss.
	 */
	private XPLossEnum(int level, double param)
	{
		this.level = level;
		this.param = param;
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the experience loss parameter for this level.<br>
	 * This value is used by {@code long)} to calculate penalties.
	 * @return The {@code double} value of the parameter.
	 */
	public double getParam()
	{
		return param;
	}
	
	/**
	 * Calculates the amount of experience lost based on a character's level.<br>
	 * This method checks the {@code XPLossEnum} to find the correct penalty.<br>
	 * It returns 0 if the level is below 50 or exceeds defined limits.
	 * @param level The current level of the character.
	 * @param expNeed The total amount of experience required.
	 * @return The calculated experience loss as a {@code long}.
	 */
	public static long getExpLoss(int level, long expNeed)
	{
		if (level < 50)
		{
			return 0;
		}
		
		for (XPLossEnum xpLossEnum : values())
		{
			if (level <= xpLossEnum.getLevel())
			{
				return Math.round((expNeed / 100) * xpLossEnum.getParam());
			}
		}
		
		return 0;
	}
}
