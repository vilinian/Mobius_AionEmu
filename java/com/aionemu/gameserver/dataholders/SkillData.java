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

import com.aionemu.gameserver.skillengine.model.SkillTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for all skill-related information.<br>
 * It acts as a container to manage and access {@link SkillTemplate} objects throughout the server.<br>
 * It is primarily used to load and store static skill definitions from XML files.
 * @author ATracer
 */
@XmlRootElement(name = "skill_data")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillData
{
	@XmlElement(name = "skill_template")
	private List<SkillTemplate> skillTemplates;
	@XmlTransient
	private HashMap<Integer, ArrayList<Integer>> cooldownGroups;
	/**
	 * Map that contains skillId - SkillTemplate key-value pair
	 */
	private final TIntObjectHashMap<SkillTemplate> skillData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code skillData} map using the list of {@link SkillTemplate} objects.<br>
	 * The {@code skillData} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		skillData.clear();
		for (SkillTemplate skillTempalte : skillTemplates)
		{
			skillData.put(skillTempalte.getSkillId(), skillTempalte);
		}
	}
	
	/**
	 * Retrieves a specific {@link SkillTemplate} using its unique ID.<br>
	 * This method looks up the data in the internal {@code skillData} map.
	 * @param skillId The unique identifier for the skill to find.
	 * @return The {@code SkillTemplate} associated with the provided ID, or {@code null} if not found.
	 */
	public SkillTemplate getSkillTemplate(int skillId)
	{
		return skillData.get(skillId);
	}
	
	/**
	 * Returns the total number of skills in the data map.<br>
	 * This method calls {@code getSkillData} to retrieve the internal collection.
	 * @return The count of items currently stored in the skill data.
	 */
	public int size()
	{
		return skillData.size();
	}
	
	/**
	 * Retrieves the list of all available skill templates.<br>
	 * This method returns the {@code skillTemplates} collection from the data holder.
	 * @return a {@code List} containing all {@link SkillTemplate} objects.
	 */
	public List<SkillTemplate> getSkillTemplates()
	{
		return skillTemplates;
	}
	
	/**
	 * Sets the list of {@link SkillTemplate} objects for this data holder.<br>
	 * This method also triggers the {@code afterUnmarshal} logic to initialize internal data.
	 * @param skillTemplates The list of templates to assign.
	 */
	public void setSkillTemplates(List<SkillTemplate> skillTemplates)
	{
		this.skillTemplates = skillTemplates;
		afterUnmarshal(null, null);
	}
	
	/**
	 * Sets up the {@code cooldownGroups} map.<br>
	 * This method groups skill IDs by their unique cooldown identifiers.<br>
	 * It processes all entries in the {@code skillTemplates} list.
	 */
	public void initializeCooldownGroups()
	{
		cooldownGroups = new HashMap<>();
		for (SkillTemplate skillTemplate : skillTemplates)
		{
			final int cooldownId = skillTemplate.getCooldownId();
			if (!cooldownGroups.containsKey(cooldownId))
			{
				cooldownGroups.put(cooldownId, new ArrayList<>());
			}
			
			cooldownGroups.get(cooldownId).add(skillTemplate.getSkillId());
		}
	}
	
	/**
	 * Retrieves a list of skill IDs associated with a specific cooldown ID.<br>
	 * This method ensures that the {@code cooldownGroups} map is initialized before searching.
	 * @param cooldownId The unique identifier for the cooldown group.
	 * @return An {@code ArrayList<Integer>} containing the skills, or {@code null} if none exist.
	 */
	public ArrayList<Integer> getSkillsForCooldownId(int cooldownId)
	{
		if (cooldownGroups == null)
		{
			initializeCooldownGroups();
		}
		
		return cooldownGroups.get(cooldownId);
	}
	
	/**
	 * Retrieves the internal map of all skills.<br>
	 * This map uses an {@code Integer} ID as the key to find a {@link SkillTemplate}.
	 * @return A {@code TIntObjectHashMap} containing the skill data.
	 */
	public TIntObjectHashMap<SkillTemplate> getSkillData()
	{
		return skillData;
	}
}
