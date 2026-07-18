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
package com.aionemu.gameserver.model.items;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * Represents a random bonus applied to an item's statistics.<br>
 * This class handles the logic for calculating variable stats that are not fixed values.<br>
 * It is used by {@link StatFunction} to determine dynamic attribute modifiers.
 */
public class RealRandomBonus
{
	private int itemObjectId;
	private List<RealRandomBonusStat> stats;
	private final List<StatFunction> functions;
	private final List<StatFunction> fusionFunctions;
	
	/**
	 * Creates a new {@link RealRandomBonus} instance.<br>
	 * This constructor initializes the item ID and processes the provided stats.<br>
	 * It automatically populates the internal function lists based on the stat types.
	 * @param itemObjectId The unique identifier for the item.
	 * @param stats A list of {@link RealRandomBonusStat} objects to be processed.
	 */
	public RealRandomBonus(int itemObjectId, List<RealRandomBonusStat> stats)
	{
		functions = new ArrayList<>();
		fusionFunctions = new ArrayList<>();
		this.itemObjectId = itemObjectId;
		this.stats = stats;
		for (RealRandomBonusStat stat : getStats())
		{
			switch (stat.getStat())
			{
				case ATTACK_SPEED:
				case SPEED:
				{
					final int value = (stat.getStat() == StatEnum.ATTACK_SPEED) ? (-stat.getValue()) : stat.getValue();
					if (stat.isFusion())
					{
						fusionFunctions.add(new StatRateFunction(stat.getStat(), value, true));
						continue;
					}
					
					functions.add(new StatRateFunction(stat.getStat(), value, true));
					continue;
				}
				default:
				{
					if (stat.isFusion())
					{
						fusionFunctions.add(new StatAddFunction(stat.getStat(), stat.getValue(), true));
						continue;
					}
					
					functions.add(new StatAddFunction(stat.getStat(), stat.getValue(), true));
					continue;
				}
			}
		}
	}
	
	/**
	 * Updates the internal list of {@code functions}.<br>
	 * This method clears existing data and rebuilds it based on current stats.<br>
	 * It determines whether to use a {@code StatAddFunction} or a {@code StatRateFunction} for each stat.
	 */
	public void recalcStats()
	{
		functions.clear();
		for (RealRandomBonusStat stat : getStats())
		{
			switch (stat.getStat())
			{
				case ATTACK_SPEED:
				case SPEED:
				{
					final int value = (stat.getStat() == StatEnum.ATTACK_SPEED) ? (-stat.getValue()) : stat.getValue();
					functions.add(new StatRateFunction(stat.getStat(), value, true));
					continue;
				}
				default:
				{
					functions.add(new StatAddFunction(stat.getStat(), stat.getValue(), true));
					continue;
				}
			}
		}
	}
	
	/**
	 * Retrieves the list of bonus statistics for this object.<br>
	 * This method returns the {@code stats} field as a {@link List}.
	 * @return A {@code List} of {@link RealRandomBonusStat} objects.
	 */
	public List<RealRandomBonusStat> getStats()
	{
		return stats;
	}
	
	/**
	 * Updates the list of statistics for this {@link RealRandomBonus}.<br>
	 * This method replaces the current {@code stats} with a new list.
	 * @param stats The new list of {@code RealRandomBonusStat} objects to assign.
	 */
	public void setStats(List<RealRandomBonusStat> stats)
	{
		this.stats = stats;
	}
	
	/**
	 * Retrieves the unique identifier for the item.<br>
	 * This ID is used to identify which item this bonus belongs to.
	 * @return The {@code int} value of the item object ID.
	 */
	public int getItemObjectId()
	{
		return itemObjectId;
	}
	
	/**
	 * Sets the unique identifier for the item.<br>
	 * This updates the {@code itemObjectId} field of this object.
	 * @param itemObjectId The new ID to assign to the item.
	 */
	public void setItemObjectId(int itemObjectId)
	{
		this.itemObjectId = itemObjectId;
	}
	
	/**
	 * Retrieves the list of stat functions for this bonus.<br>
	 * These functions are used to calculate base statistics.
	 * @return a {@code List} of {@link StatFunction} objects.
	 */
	public List<StatFunction> getFunctions()
	{
		return functions;
	}
	
	/**
	 * Retrieves the list of functions used for fusion stats.<br>
	 * These functions are stored in the {@code fusionFunctions} field.
	 * @return a {@code List} containing all {@link StatFunction} objects.
	 */
	public List<StatFunction> getFusionFunctions()
	{
		return fusionFunctions;
	}
}
