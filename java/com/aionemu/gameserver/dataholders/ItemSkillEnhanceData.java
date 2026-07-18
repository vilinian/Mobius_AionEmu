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
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.item.ItemSkillEnhance;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for item skill enhancements.<br>
 * It maps {@code ItemSkillEnhance} templates to their respective properties.<br>
 * Use this class to manage how skills are enhanced on specific items.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "item_skill_enhances")
public class ItemSkillEnhanceData
{
	@XmlElement(name = "item_skill_enhance", required = true)
	protected List<ItemSkillEnhance> skillEnhances;
	@XmlTransient
	private final TIntObjectHashMap<ItemSkillEnhance> custom = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code custom} map using the list of {@link ItemSkillEnhance} objects.<br>
	 * The {@code custom} map is updated with entries from the {@code skillEnhances} list.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (ItemSkillEnhance itemSkillEnhance : skillEnhances)
		{
			getCustomMap().put(itemSkillEnhance.getId(), itemSkillEnhance);
		}
	}
	
	/**
	 * Retrieves the internal map of custom skill enhancements.<br>
	 * This method provides access to the {@code custom} collection.
	 * @return a {@link TIntObjectHashMap} containing {@link ItemSkillEnhance} objects.
	 */
	private TIntObjectHashMap<ItemSkillEnhance> getCustomMap()
	{
		return custom;
	}
	
	/**
	 * Retrieves a specific {@link ItemSkillEnhance} based on its unique ID.<br>
	 * This method looks for the skill in the custom data map.
	 * @param skillId The unique identifier of the skill to find.
	 * @return The {@code ItemSkillEnhance} object, or {@code null} if not found.
	 */
	public ItemSkillEnhance getSkillEnhance(int skillId)
	{
		return custom.get(skillId);
	}
	
	/**
	 * Returns the number of elements in the custom map.<br>
	 * This method calls {@code getCustomMap} to retrieve the internal collection.
	 * @return The total count of custom achievement action templates.
	 */
	public int size()
	{
		return custom.size();
	}
}
