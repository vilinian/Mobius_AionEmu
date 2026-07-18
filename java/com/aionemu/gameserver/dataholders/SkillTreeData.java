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
package com.aionemu.gameserver.dataholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the configuration data for skill trees within the game.<br>
 * It maps {@link PlayerClass} and {@link Race} types to their respective {@link SkillLearnTemplate} sets.
 * @author ATracer
 */
@XmlRootElement(name = "skill_tree")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillTreeData
{
	@XmlElement(name = "skill")
	private List<SkillLearnTemplate> skillTemplates;
	
	private final TIntObjectHashMap<ArrayList<SkillLearnTemplate>> templates = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<ArrayList<SkillLearnTemplate>> templatesById = new TIntObjectHashMap<>();
	
	// [race, [stack, [lvl, skill_id]]]
	@XmlTransient
	private final HashMap<Race, HashMap<String, HashMap<Integer, Integer>>> stigmaTree = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the internal template maps using the {@code skillTemplates} list.<br>
	 * Each {@link SkillLearnTemplate} in the list is processed by {@code addTemplate}.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (SkillLearnTemplate template : skillTemplates)
		{
			addTemplate(template);
		}
		
		// skillTemplates = null;
	}
	
	/**
	 * Adds a {@code SkillLearnTemplate} to the internal data structures.<br>
	 * This method organizes templates by their hash and unique skill ID.<br>
	 * It ensures that new templates are stored in both the {@code templates} and {@code templatesById} maps.
	 * @param template The {@code SkillLearnTemplate} object to be added.
	 */
	private void addTemplate(SkillLearnTemplate template)
	{
		Race race = template.getRace();
		if (race == null)
		{
			race = Race.PC_ALL;
		}
		
		final int hash = makeHash(template.getClassId().ordinal(), race.ordinal(), template.getMinLevel());
		ArrayList<SkillLearnTemplate> value = templates.get(hash);
		if (value == null)
		{
			value = new ArrayList<>();
			templates.put(hash, value);
		}
		
		value.add(template);
		
		value = templatesById.get(template.getSkillId());
		if (value == null)
		{
			value = new ArrayList<>();
			templatesById.put(template.getSkillId(), value);
		}
		
		value.add(template);
	}
	
	/**
	 * Retrieves the collection of skill templates.<br>
	 * This map uses an integer key to organize lists of {@link SkillLearnTemplate}.
	 * @return a {@code TIntObjectHashMap} containing all loaded skill templates.
	 */
	public TIntObjectHashMap<ArrayList<SkillLearnTemplate>> getTemplates()
	{
		return templates;
	}
	
	/**
	 * Retrieves the list of skills available for a specific character.<br>
	 * This method checks for class-specific, race-specific, and general templates.<br>
	 * It combines all applicable {@link SkillLearnTemplate} objects into one array.
	 * @param playerClass The {@code PlayerClass} of the character.
	 * @param level The current level of the character.
	 * @param race The {@code Race} of the character.
	 * @return An array of {@code SkillLearnTemplate} objects available to learn.
	 */
	public SkillLearnTemplate[] getTemplatesFor(PlayerClass playerClass, int level, Race race)
	{
		final List<SkillLearnTemplate> newSkills = new ArrayList<>();
		
		final List<SkillLearnTemplate> classRaceSpecificTemplates = templates.get(makeHash(playerClass.ordinal(), race.ordinal(), level));
		final List<SkillLearnTemplate> classSpecificTemplates = templates.get(makeHash(playerClass.ordinal(), Race.PC_ALL.ordinal(), level));
		final List<SkillLearnTemplate> generalTemplates = templates.get(makeHash(PlayerClass.ALL.ordinal(), Race.PC_ALL.ordinal(), level));
		
		if (classRaceSpecificTemplates != null)
		{
			newSkills.addAll(classRaceSpecificTemplates);
		}
		
		if (classSpecificTemplates != null)
		{
			newSkills.addAll(classSpecificTemplates);
		}
		
		if (generalTemplates != null)
		{
			newSkills.addAll(generalTemplates);
		}
		
		return newSkills.toArray(new SkillLearnTemplate[newSkills.size()]);
	}
	
	/**
	 * Retrieves all {@link SkillLearnTemplate} objects associated with a specific skill ID.<br>
	 * This method searches the internal map for templates matching the provided {@code skillId}.<br>
	 * It returns an empty array if no matches are found.
	 * @param skillId The unique identifier of the skill to search for.
	 * @return An array of {@link SkillLearnTemplate} objects that match the given ID.
	 */
	public SkillLearnTemplate[] getTemplatesForSkill(int skillId)
	{
		final List<SkillLearnTemplate> searchSkills = new ArrayList<>();
		
		final List<SkillLearnTemplate> byId = templatesById.get(skillId);
		if (byId != null)
		{
			searchSkills.addAll(byId);
		}
		
		return searchSkills.toArray(new SkillLearnTemplate[searchSkills.size()]);
	}
	
	/**
	 * Checks if a specific skill exists in the learned skills list.<br>
	 * It looks up the {@code skillId} within the internal template map.
	 * @param skillId The unique identifier for the skill to check.
	 * @return {@code true} if the skill is found, otherwise {@code false}.
	 */
	public boolean isLearnedSkill(int skillId)
	{
		return templatesById.get(skillId) != null;
	}
	
	/**
	 * Calculates the total number of skill templates.<br>
	 * It sums the sizes of all lists stored in the {@code templates} map.
	 * @return The total count of all skill templates.
	 */
	public int size()
	{
		int size = 0;
		for (Integer key : templates.keys())
		{
			size += templates.get(key).size();
		}
		
		return size;
	}
	
	/**
	 * Generates a unique hash code for a specific character configuration.<br>
	 * This method combines the {@code classId}, {@code race}, and {@code level} into a single integer.<br>
	 * The resulting value is used to identify unique skill sets.
	 * @param classId The unique identifier for the player class.
	 * @param race The numerical ID of the character race.
	 * @param level The current level of the character.
	 * @return A unique {@code int} hash representing the combination of all three parameters.
	 */
	private static int makeHash(int classId, int race, int level)
	{
		int result = classId << 8;
		result = (result | race) << 8;
		return result | level;
	}
	
	/**
	 * Initializes the {@code stigmaTree} map using data from {@code skillTemplates}.<br>
	 * This method organizes skills by race, stack name, and level.<br>
	 * It clears the {@code skillTemplates} list after processing is complete.
	 */
	public void setStigmaTree()
	{
		for (SkillLearnTemplate skillLearnTemplate : skillTemplates)
		{
			final String skillStack = DataManager.SKILL_DATA.getSkillTemplate(skillLearnTemplate.getSkillId()).getStack();
			final int skillRealLvl = DataManager.SKILL_DATA.getSkillTemplate(skillLearnTemplate.getSkillId()).getLvl();
			final ArrayList<Race> addRaceList = new ArrayList<>();
			if (skillLearnTemplate.getRace() == Race.PC_ALL)
			{
				addRaceList.add(Race.ASMODIANS);
				addRaceList.add(Race.ELYOS);
			}
			else
			{
				addRaceList.add(skillLearnTemplate.getRace());
			}
			
			for (Race addRace : addRaceList)
			{
				if (stigmaTree.get(addRace) != null)
				{
					if (stigmaTree.get(addRace).get(skillStack) != null)
					{
						stigmaTree.get(addRace).get(skillStack).put(skillRealLvl, skillLearnTemplate.getSkillId());
					}
					else
					{
						final HashMap<Integer, Integer> skillMap = new HashMap<>();
						skillMap.put(skillRealLvl, skillLearnTemplate.getSkillId());
						stigmaTree.get(addRace).put(skillStack, skillMap);
					}
				}
				else
				{
					final HashMap<String, HashMap<Integer, Integer>> stackMap = new HashMap<>();
					final HashMap<Integer, Integer> skillMap = new HashMap<>();
					skillMap.put(skillLearnTemplate.getSkillId(), skillRealLvl);
					stackMap.put(skillStack, skillMap);
					stigmaTree.put(addRace, stackMap);
				}
			}
		}
		
		skillTemplates = null;
	}
	
	/**
	 * Retrieves the complete stigma tree data.<br>
	 * This map organizes skills by {@link Race}, skill names, and levels.
	 * @return A nested {@code HashMap} containing the stigma tree structure.
	 */
	public HashMap<Race, HashMap<String, HashMap<Integer, Integer>>> getStigmaTree()
	{
		return stigmaTree;
	}
}
