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

import com.aionemu.gameserver.model.templates.collection.CollectionTemplate;

/**
 * This class serves as a data holder for {@link CollectionTemplate} objects.<br>
 * It is used to manage and store collection-related information within the game server.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "collection_templates")
public class CollectionData
{
	@XmlElement(name = "collection_template")
	private List<CollectionTemplate> collectionTemplates;
	@XmlTransient
	private final Map<Integer, CollectionTemplate> templates = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code templates} map using the list of {@link CollectionTemplate} objects.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (CollectionTemplate template : collectionTemplates)
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
	 * Retrieves a specific collection template from the data store.<br>
	 * This method uses the unique identifier to find the matching object.
	 * @param lumielId The unique ID of the template to retrieve.
	 * @return The {@code CollectionTemplate} associated with the provided ID, or {@code null} if not found.
	 */
	public CollectionTemplate getTemplate(int lumielId)
	{
		return templates.get(lumielId);
	}
}
