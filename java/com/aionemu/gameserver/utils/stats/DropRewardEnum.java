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
 * Defines the types of rewards that can be obtained from drops.<br>
 * This enumeration is used to categorize different loot items within the game system.
 */
public enum DropRewardEnum
{
	MINUS_20(-20, 0),
	MINUS_19(-19, 39),
	MINUS_18(-18, 79),
	MINUS_17(-17, 100);
	
	private final int dropRewardPercent;
	private final int levelDifference;
	
	/**
	 * Creates a new instance of {@link DropRewardEnum}.<br>
	 * This constructor sets the required reward values.
	 * @param levelDifference The difference in levels between players.
	 * @param dropRewardPercent The percentage of the reward to apply.
	 */
	private DropRewardEnum(int levelDifference, int dropRewardPercent)
	{
		this.levelDifference = levelDifference;
		this.dropRewardPercent = dropRewardPercent;
	}
	
	/**
	 * Returns the percentage of the reward.<br>
	 * This value is used to calculate loot based on the {@code DropRewardEnum}.
	 * @return The integer value for the reward percent.
	 */
	public int rewardPercent()
	{
		return dropRewardPercent;
	}
	
	/**
	 * Calculates the reward percentage based on a level difference.<br>
	 * This method looks up the correct value from {@link DropRewardEnum}.<br>
	 * It handles cases where the input is outside the defined range.
	 * @param levelDifference The difference in levels to check.
	 * @return The corresponding reward percentage as an {@code int}.
	 */
	public static int dropRewardFrom(int levelDifference)
	{
		if (levelDifference < MINUS_20.levelDifference)
		{
			return MINUS_20.dropRewardPercent;
		}
		
		if (levelDifference > MINUS_17.levelDifference)
		{
			return MINUS_17.dropRewardPercent;
		}
		
		for (DropRewardEnum dropReward : values())
		{
			if (dropReward.levelDifference == levelDifference)
			{
				return dropReward.dropRewardPercent;
			}
		}
		
		throw new NoSuchElementException("Drop reward for such level difference was not found");
	}
}
