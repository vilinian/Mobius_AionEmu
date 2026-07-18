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
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.lumiel_transform.LumielMaterialTemplate;

/**
 * This class serves as a data holder for {@link LumielMaterialTemplate} objects.<br>
 * It is used to manage and store material-related information within the game server.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "lumiel_material_templates")
public class LumielMaterialData
{
	@XmlElement(name = "lumiel_material_template")
	private List<LumielMaterialTemplate> materialTemplate;
	@XmlTransient
	private final Map<Integer, LumielMaterialTemplate> templates = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code templates} map using the list of {@link LumielMaterialTemplate} objects.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (LumielMaterialTemplate template : materialTemplate)
		{
			templates.put(template.getId(), template);
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return templates.size();
	}
	
	/**
	 * Retrieves a specific {@link LumielMaterialTemplate} based on unique IDs.<br>
	 * This method searches the internal collection for a matching pair of identifiers.
	 * @param lumielId The unique identifier for the Lumiel.
	 * @param itemId The unique identifier for the item.
	 * @return The matching {@code LumielMaterialTemplate} object, or {@code null} if no match is found.
	 */
	public LumielMaterialTemplate getTemplate(int lumielId, int itemId)
	{
		LumielMaterialTemplate lumiel = null;
		for (LumielMaterialTemplate template : templates.values())
		{
			if ((template.getLumielId() != lumielId) || (template.getItemId() != itemId))
			{
				continue;
			}
			
			lumiel = template;
		}
		
		return lumiel;
	}
}
