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

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.petskill.PetSkillTemplate;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for all {@link PetSkillTemplate} objects.<br>
 * It acts as a container to manage and access pet skill information loaded from XML files.
 * @author ATracer
 */
@XmlRootElement(name = "pet_skill_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class PetSkillData
{
	@XmlElement(name = "pet_skill")
	private List<PetSkillTemplate> petSkills;
	/**
	 * A map containing all npc skill templates
	 */
	private final TIntObjectHashMap<TIntIntHashMap> petSkillData = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<TIntArrayList> petSkillsMap = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code petSkillData} and {@code petSkillsMap} maps using the list of {@link PetSkillTemplate} objects.<br>
	 * The maps are updated or created as needed during this process.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PetSkillTemplate petSkill : petSkills)
		{
			TIntIntHashMap orderSkillMap = petSkillData.get(petSkill.getOrderSkill());
			if (orderSkillMap == null)
			{
				orderSkillMap = new TIntIntHashMap();
				petSkillData.put(petSkill.getOrderSkill(), orderSkillMap);
			}
			
			orderSkillMap.put(petSkill.getPetId(), petSkill.getSkillId());
			
			TIntArrayList skillList = petSkillsMap.get(petSkill.getPetId());
			if (skillList == null)
			{
				skillList = new TIntArrayList();
				petSkillsMap.put(petSkill.getPetId(), skillList);
			}
			
			skillList.add(petSkill.getSkillId());
		}
	}
	
	/**
	 * Returns the total number of pet skill templates.<br>
	 * This method calls {@code size} to get the count.
	 * @return The size of the internal data map.
	 */
	public int size()
	{
		return petSkillData.size();
	}
	
	/**
	 * Retrieves the specific order of a pet skill for a given NPC.<br>
	 * This method looks up the value in the {@code petSkillData} map.
	 * @param orderSkill The unique identifier for the skill order.
	 * @param petNpcId The unique identifier for the pet NPC.
	 * @return The integer value representing the skill order.
	 */
	public int getPetOrderSkill(int orderSkill, int petNpcId)
	{
		return petSkillData.get(orderSkill).get(petNpcId);
	}
	
	/**
	 * Checks if a specific pet possesses a certain skill.<br>
	 * This method looks up the skills associated with a {@code petNpcId}.<br>
	 * It returns {@code true} if the {@code skillId} is found in that list.
	 * @param petNpcId The unique identifier for the pet NPC.
	 * @param skillId The unique identifier for the skill to check.
	 * @return {@code true} if the pet has the skill, otherwise {@code false}.
	 */
	public boolean petHasSkill(int petNpcId, int skillId)
	{
		return petSkillsMap.get(petNpcId).contains(skillId);
	}
}
