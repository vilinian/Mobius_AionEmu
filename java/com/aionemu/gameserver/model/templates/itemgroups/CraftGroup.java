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
package com.aionemu.gameserver.model.templates.itemgroups;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.rewards.CraftReward;

/**
 * Represents a collection of items used for crafting purposes.<br>
 * This class serves as a base template for defining groups of materials or products within the crafting system.
 * @author Rolandas
 */
public abstract class CraftGroup extends BonusItemGroup
{
	@XmlTransient
	private Map<Integer, Map<IntRange, List<CraftReward>>> dataHolder;
	
	/**
	 * Retrieves the rewards associated with a specific skill.<br>
	 * This method looks up the {@code skillId} in the internal data holder.<br>
	 * It returns an array of {@link ItemRaceEntry} objects.<br>
	 * If no rewards are found, it returns an empty array.
	 * @param skillId The unique identifier for the skill to check.
	 * @return An array of {@code ItemRaceEntry} rewards or an empty array if none exist.
	 */
	public ItemRaceEntry[] getRewards(Integer skillId)
	{
		if (!dataHolder.containsKey(skillId))
		{
			return new ItemRaceEntry[0];
		}
		
		final List<ItemRaceEntry> result = new ArrayList<>();
		for (List<CraftReward> items : dataHolder.get(skillId).values())
		{
			result.addAll(items);
		}
		
		return result.toArray(new ItemRaceEntry[0]);
	}
	
	/**
	 * Retrieves the rewards for a specific skill and point value.<br>
	 * This method checks if the {@code skillId} exists in the data holder.<br>
	 * It returns all matching {@link CraftReward} entries based on the provided {@code skillPoints}.
	 * @param skillId The unique identifier for the skill.
	 * @param skillPoints The specific point value to check against the range.
	 * @return An array of {@code ItemRaceEntry} objects, or an empty array if no rewards are found.
	 */
	public ItemRaceEntry[] getRewards(Integer skillId, Integer skillPoints)
	{
		if (!dataHolder.containsKey(skillId))
		{
			return new ItemRaceEntry[0];
		}
		
		final List<ItemRaceEntry> result = new ArrayList<>();
		for (Entry<IntRange, List<CraftReward>> entry : dataHolder.get(skillId).entrySet())
		{
			if (!entry.getKey().containsInteger(skillPoints))
			{
				continue;
			}
			
			result.addAll(entry.getValue());
		}
		
		return result.toArray(new ItemRaceEntry[0]);
	}
	
	/**
	 * Retrieves the internal data structure for craft rewards.<br>
	 * This map links skill IDs to their respective reward ranges.
	 * @return a {@code Map} containing the nested reward data.
	 */
	public Map<Integer, Map<IntRange, List<CraftReward>>> getDataHolder()
	{
		return dataHolder;
	}
	
	/**
	 * Sets the internal data holder for craft rewards.<br>
	 * This method updates the {@code dataHolder} field with a new map.<br>
	 * Use this to initialize or change the reward data structure.
	 * @param dataHolder The new {@code Map} containing the reward data.
	 */
	public void setDataHolder(Map<Integer, Map<IntRange, List<CraftReward>>> dataHolder)
	{
		this.dataHolder = dataHolder;
	}
}
