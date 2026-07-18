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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class manages the logic for skill chaining mechanics.<br>
 * It defines how skills can be linked together in a sequence.<br>
 * It is used by the {@code SkillEngine} to process multi-part abilities.
 * @author kecimis
 */
public class ChainSkills
{
	private final Map<String, ChainSkill> multiSkills = new HashMap<>();
	private final ChainSkill chainSkill = new ChainSkill("", 0, 0);
	
	// private Logger log = LoggerFactory.getLogger(ChainSkills.class);
	/**
	 * Retrieves the current number of chains for a specific skill category.<br>
	 * This method checks if the {@code player} has used the {@code template} recently.<br>
	 * It resets the count if the cooldown period has passed.
	 * @param player The {@link Player} object to check.
	 * @param template The {@link SkillTemplate} associated with the skill.
	 * @param category The unique string identifier for the skill category.
	 * @return The current chain count as an {@code int}.
	 */
	public int getChainCount(Player player, SkillTemplate template, String category)
	{
		if (category == null)
		{
			return 0;
		}
		
		final long nullTime = player.getSkillCoolDown(template.getCooldownId());
		if (multiSkills.get(category) != null)
		{
			if ((System.currentTimeMillis() >= nullTime) && (multiSkills.get(category).getUseTime() <= nullTime))
			{
				multiSkills.get(category).setChainCount(0);
			}
			
			return multiSkills.get(category).getChainCount();
		}
		
		return 0;
	}
	
	/**
	 * Retrieves the timestamp of the last time a skill in a specific category was used.<br>
	 * It checks both multi-skills and standard chain skills.<br>
	 * Returns {@code 0} if no skill in that category has been used yet.
	 * @param category The unique identifier for the skill category.
	 * @return The timestamp of the last use as a {@code long}.
	 */
	public long getLastChainUseTime(String category)
	{
		if (multiSkills.get(category) != null)
		{
			return multiSkills.get(category).getUseTime();
		}
		else if (chainSkill.getCategory().equals(category))
		{
			return chainSkill.getUseTime();
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * Checks if a skill chain is currently active for a specific category.<br>
	 * It compares the elapsed time against the current system time.
	 * @param category The unique identifier for the skill category.
	 * @param time The amount of time to add to the last used timestamp.
	 * @return {@code true} if the chain is still active, otherwise {@code false}.
	 */
	public boolean chainSkillEnabled(String category, int time)
	{
		long useTime = 0;
		if (multiSkills.get(category) != null)
		{
			useTime = multiSkills.get(category).getUseTime();
		}
		else if (chainSkill.getCategory().equals(category))
		{
			useTime = chainSkill.getUseTime();
		}
		
		if ((useTime + time) >= System.currentTimeMillis())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Adds or updates a skill in the chain system.<br>
	 * This method handles both single and multi-cast skills based on the provided category.
	 * @param category The unique identifier for the skill category.
	 * @param multiCast Set to {@code true} if the skill is a multi-cast skill, otherwise {@code false}.
	 */
	public void addChainSkill(String category, boolean multiCast)
	{
		if (multiCast)
		{
			if (multiSkills.get(category) != null)
			{
				if (multiCast)
				{
					multiSkills.get(category).increaseChainCount();
				}
				
				multiSkills.get(category).setUseTime(System.currentTimeMillis());
			}
			else
			{
				multiSkills.put(category, new ChainSkill(category, (multiCast ? 1 : 0), System.currentTimeMillis()));
			}
		}
		else
		{
			chainSkill.updateChainSkill(category);
		}
	}
	
	/**
	 * Retrieves all available chain skills.<br>
	 * This includes the default {@code chainSkill} and all entries in {@code multiSkills}.
	 * @return A {@code Collection} of {@link ChainSkill} objects.
	 */
	public Collection<ChainSkill> getChainSkills()
	{
		final Collection<ChainSkill> collection = new ArrayList<>();
		collection.add(chainSkill);
		collection.addAll(multiSkills.values());
		
		return collection;
	}
}
