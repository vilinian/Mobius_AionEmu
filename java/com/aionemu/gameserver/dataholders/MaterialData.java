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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.materials.MaterialSkill;
import com.aionemu.gameserver.model.templates.materials.MaterialTemplate;

/**
 * This class serves as a data holder for material information.<br>
 * It stores and manages the properties of materials used within the game world.<br>
 * It is primarily used to map raw data into {@link MaterialTemplate} objects.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"materialTemplates"
})
@XmlRootElement(name = "material_templates")
public class MaterialData
{
	@XmlElement(name = "material")
	protected List<MaterialTemplate> materialTemplates;
	@XmlTransient
	Map<Integer, MaterialTemplate> materialsById = new HashMap<>();
	@XmlTransient
	Set<Integer> skillIds = new HashSet<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code materialsById} map and the {@code skillIds} set from the list of templates.<br>
	 * The {@code materialTemplates} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (materialTemplates == null)
		{
			return;
		}
		
		for (MaterialTemplate template : materialTemplates)
		{
			materialsById.put(template.getId(), template);
			if (template.getSkills() != null)
			{
				skillIds.addAll(template.getSkills().stream().map(MaterialSkill::getId).collect(Collectors.toList()));
			}
		}
		
		materialTemplates.clear();
		materialTemplates = null;
	}
	
	/**
	 * Retrieves a specific {@link MaterialTemplate} using its unique ID.<br>
	 * This method looks up the template in the internal data map.
	 * @param materialId The unique identifier for the material.
	 * @return The corresponding {@code MaterialTemplate} object, or {@code null} if not found.
	 */
	public MaterialTemplate getTemplate(int materialId)
	{
		return materialsById.get(materialId);
	}
	
	/**
	 * Checks if a specific skill is associated with material gathering.<br>
	 * This method looks up the {@code skillId} in the internal set of skills.
	 * @param skillId The unique identifier for the skill to check.
	 * @return {@code true} if the skill is a material skill, otherwise {@code false}.
	 */
	public boolean isMaterialSkill(int skillId)
	{
		return skillIds.contains(skillId);
	}
	
	/**
	 * Returns the number of material templates stored in this object.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return materialsById.size();
	}
}
