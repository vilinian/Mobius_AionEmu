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

import java.util.NoSuchElementException;

/**
 * Defines the types of experience point (XP) rewards available in the game.<br>
 * This enumeration is used to categorize different reward sources for character progression.
 * @author ATracer
 */
public enum XPRewardEnum
{
	MINUS_11(-11, 0),
	MINUS_10(-10, 1),
	MINUS_9(-9, 10),
	MINUS_8(-8, 20),
	MINUS_7(-7, 30),
	MINUS_6(-6, 40),
	MINUS_5(-5, 50),
	MINUS_4(-4, 70),
	MINUS_3(-3, 90),
	MINUS_2(-2, 100),
	MINUS_1(-1, 100),
	ZERO(0, 100),
	PLUS_1(1, 105),
	PLUS_2(2, 110),
	PLUS_3(3, 115),
	PLUS_4(4, 120);
	
	private final int xpRewardPercent;
	private final int levelDifference;
	
	/**
	 * Creates a new instance of {@link XPRewardEnum}.<br>
	 * This constructor initializes the reward values based on level gaps.
	 * @param levelDifference The difference between levels as an {@code int}.
	 * @param xpRewardPercent The percentage of experience points to award as an {@code int}.
	 */
	private XPRewardEnum(int levelDifference, int xpRewardPercent)
	{
		this.levelDifference = levelDifference;
		this.xpRewardPercent = xpRewardPercent;
	}
	
	/**
	 * Returns the percentage of the reward.<br>
	 * This value is used to calculate loot based on the {@code DropRewardEnum}.
	 * @return The integer value for the reward percent.
	 */
	public int rewardPercent()
	{
		return xpRewardPercent;
	}
	
	/**
	 * Calculates the experience point reward based on a level difference.<br>
	 * This method looks up the correct percentage from {@link XPRewardEnum}.<br>
	 * It handles values outside the standard range by using the nearest limits.
	 * @param levelDifference The difference in levels between two characters.
	 * @return The experience point reward as an integer percentage.
	 */
	public static int xpRewardFrom(int levelDifference)
	{
		if (levelDifference < MINUS_11.levelDifference)
		{
			return MINUS_11.xpRewardPercent;
		}
		
		if (levelDifference > PLUS_4.levelDifference)
		{
			return PLUS_4.xpRewardPercent;
		}
		
		for (XPRewardEnum xpReward : values())
		{
			if (xpReward.levelDifference == levelDifference)
			{
				return xpReward.xpRewardPercent;
			}
		}
		
		throw new NoSuchElementException("XP reward for such level difference was not found");
	}
}
