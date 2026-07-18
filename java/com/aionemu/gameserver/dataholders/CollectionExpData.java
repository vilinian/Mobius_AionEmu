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
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.collection.CollectionExpTemplate;
import com.aionemu.gameserver.model.templates.collection.CollectionType;

/**
 * This class holds the data for collection experience templates.<br>
 * It serves as a container for {@link CollectionExpTemplate} objects loaded from configuration files.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "collection_exp_templates")
public class CollectionExpData
{
	@XmlElement(name = "collection_exp_template")
	private List<CollectionExpTemplate> collectionExpTemplates;
	
	@XmlTransient
	private final Map<CollectionType, List<CollectionExpTemplate>> expTemplateMap = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code expTemplateMap} using the list of {@link CollectionExpTemplate} objects.<br>
	 * The {@code expTemplateMap} is built by grouping templates based on their grade.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (CollectionExpTemplate template : collectionExpTemplates)
		{
			if (expTemplateMap.containsKey(template.getGrade()))
			{
				expTemplateMap.get(template.getGrade()).add(template);
			}
			else
			{
				final List<CollectionExpTemplate> exp = new ArrayList<>();
				exp.add(template);
				expTemplateMap.put(template.getGrade(), exp);
			}
		}
	}
	
	/**
	 * Retrieves a specific collection experience template.<br>
	 * It searches for a template matching the given {@code level} and {@code grade}.
	 * @param level The required level of the template.
	 * @param grade The type of the collection.
	 * @return The matching {@link CollectionExpTemplate} or {@code null} if not found.
	 */
	public CollectionExpTemplate getTemplate(int level, CollectionType grade)
	{
		CollectionExpTemplate template = null;
		
		for (CollectionExpTemplate expTemplate : expTemplateMap.get(grade))
		{
			if (expTemplate.getLevel() == level)
			{
				template = expTemplate;
			}
		}
		
		return template;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return expTemplateMap.size();
	}
}
