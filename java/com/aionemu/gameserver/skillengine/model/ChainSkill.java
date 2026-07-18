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
package com.aionemu.gameserver.skillengine.model;

import com.aionemu.gameserver.skillengine.condition.ChainCondition;

/**
 * Represents a skill that can be chained with other skills in the game engine.<br>
 * This model stores the logic and requirements for executing consecutive ability sequences.
 * @author kecimis
 */
public class ChainSkill
{
	private String category;
	private int chainCount = 0;
	private long useTime;
	
	/**
	 * Creates a new instance of {@link ChainSkill}.<br>
	 * This constructor initializes the skill with its basic properties.
	 * @param category The name of the skill category.
	 * @param chainCount The number of times the skill can be chained.
	 * @param useTime The time required to use the skill in milliseconds.
	 */
	public ChainSkill(String category, int chainCount, long useTime)
	{
		this.category = category;
		this.chainCount = chainCount;
		this.useTime = useTime;
	}
	
	/**
	 * Updates the skill category and resets its properties.<br>
	 * This method sets {@code category} to the provided value.<br>
	 * It also resets {@code chainCount} to 0.<br>
	 * Finally, it updates {@code useTime} to the current system time.
	 * @param category The new name for the skill category.
	 */
	public void updateChainSkill(String category)
	{
		this.category = category;
		chainCount = 0;
		useTime = System.currentTimeMillis();
	}
	
	/**
	 * Retrieves the category associated with this {@link ChainCondition}.<br>
	 * This value is used to group different types of conditions.
	 * @return The category name as a {@code String}.
	 */
	public String getCategory()
	{
		return category;
	}
	
	/**
	 * Sets the category for this {@link ChainSkill}.<br>
	 * This updates the internal {@code category} field.
	 * @param name The new name for the category.
	 */
	public void setCategory(String name)
	{
		category = name;
	}
	
	/**
	 * Retrieves the current number of chains for this skill.<br>
	 * This value represents how many times a skill has been chained.
	 * @return The total {@code int} count of chains.
	 */
	public int getChainCount()
	{
		return chainCount;
	}
	
	/**
	 * Sets the number of chains for this {@link ChainSkill}.<br>
	 * This updates the internal {@code chainCount} value.
	 * @param chainCount The new number of chains to set.
	 */
	public void setChainCount(int chainCount)
	{
		this.chainCount = chainCount;
	}
	
	/**
	 * This method increases the {@code chainCount} by 1.<br>
	 * It updates the current count of the skill chain.
	 */
	public void increaseChainCount()
	{
		chainCount++;
	}
	
	/**
	 * Retrieves the total time used by this {@link ChainSkill}.<br>
	 * This value is stored as a {@code long}.
	 * @return The current use time.
	 */
	public long getUseTime()
	{
		return useTime;
	}
	
	/**
	 * Sets the time required to use this skill.<br>
	 * This updates the {@code useTime} field of the {@link ChainSkill} object.
	 * @param useTime The duration in milliseconds for the skill.
	 */
	public void setUseTime(long useTime)
	{
		this.useTime = useTime;
	}
}
