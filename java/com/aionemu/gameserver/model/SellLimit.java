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

import java.util.NoSuchElementException;

import com.aionemu.gameserver.configs.main.CustomConfig;

/**
 * Represents the maximum limits for selling items in the game.<br>
 * This enum is used to define and manage {@code sellLimit} constraints.
 * @author synchro2
 * @rework Phantom_KNA
 */
public enum SellLimit
{
	LIMIT_1_30(1, 30, CustomConfig.SELL_LIMIT_KINAH_LV1_LV30),
	LIMIT_31_40(31, 40, CustomConfig.SELL_LIMIT_KINAH_LV31_LV40),
	LIMIT_41_55(41, 55, CustomConfig.SELL_LIMIT_KINAH_LV41_LV55),
	LIMIT_56_60(56, 60, CustomConfig.SELL_LIMIT_KINAH_LV56_LV60),
	LIMIT_61_65(61, 65, CustomConfig.SELL_LIMIT_KINAH_LV61_LV65),
	LIMIT_66_75(66, 80, CustomConfig.SELL_LIMIT_KINAH_LV66_LV75);
	
	private final int playerMinLevel;
	private final int playerMaxLevel;
	private final long limit;
	
	/**
	 * Initializes a new {@link SellLimit} instance.<br>
	 * This constructor sets the level range and the corresponding sell limit.
	 * @param playerMinLevel The minimum character level for this limit.
	 * @param playerMaxLevel The maximum character level for this limit.
	 * @param limit The maximum amount allowed to be sold.
	 */
	private SellLimit(int playerMinLevel, int playerMaxLevel, long limit)
	{
		this.playerMinLevel = playerMinLevel;
		this.playerMaxLevel = playerMaxLevel;
		this.limit = limit;
	}
	
	/**
	 * Calculates the maximum amount a player can sell based on their current level.<br>
	 * This method checks which {@link SellLimit} range matches the provided {@code playerLevel}.
	 * @param playerLevel The current level of the player.
	 * @return The maximum sell limit as a {@code long}.
	 */
	public static long getSellLimit(int playerLevel)
	{
		for (SellLimit sellLimit : values())
		{
			if ((sellLimit.playerMinLevel <= playerLevel) && (sellLimit.playerMaxLevel >= playerLevel))
			{
				return sellLimit.limit;
			}
		}
		
		throw new NoSuchElementException("Sell limit for player level: " + playerLevel + " was not found");
	}
}
