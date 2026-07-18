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

import com.aionemu.gameserver.model.templates.lumiel_transform.LumielTransformTemplate;

/**
 * This class serves as a data holder for {@link LumielTransformTemplate} objects.<br>
 * It acts as a container to manage and access various templates related to the Lumiel system.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "lumiel_templates")
public class LumielTemplateData
{
	@XmlElement(name = "lumiel_template")
	private List<LumielTransformTemplate> lumielTransformTemplates;
	@XmlTransient
	private final Map<Integer, LumielTransformTemplate> templates = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code templates} map using the list of {@link LumielTransformTemplate} objects.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (LumielTransformTemplate template : lumielTransformTemplates)
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
	 * Retrieves a specific template based on its unique ID.<br>
	 * This method looks up the data in the internal {@code templates} map.
	 * @param lumielId The unique identifier for the template to find.
	 * @return The {@link LumielTransformTemplate} associated with the given ID, or {@code null} if not found.
	 */
	public LumielTransformTemplate getTemplate(int lumielId)
	{
		return templates.get(lumielId);
	}
	
	/**
	 * Retrieves all available {@link LumielTransformTemplate} objects.<br>
	 * The results are organized in a {@code Map} where the key is the unique ID.
	 * @return A {@code Map} containing all loaded templates.
	 */
	public Map<Integer, LumielTransformTemplate> getAllTemplates()
	{
		return templates;
	}
}
