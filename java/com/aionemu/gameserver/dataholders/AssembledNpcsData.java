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

import com.aionemu.gameserver.model.templates.assemblednpc.AssembledNpcTemplate;

/**
 * This class serves as a data holder for all {@link AssembledNpcTemplate} objects.<br>
 * It is used to parse and store assembled NPC configurations from XML files.<br>
 * It provides a centralized collection of NPC templates for the game server.
 * @author xTz
 */
@XmlRootElement(name = "assembled_npcs")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssembledNpcsData
{
	@XmlElement(name = "assembled_npc", type = AssembledNpcTemplate.class)
	private List<AssembledNpcTemplate> templates;
	@XmlTransient
	private final Map<Integer, AssembledNpcTemplate> assembledNpcsTemplates = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code assembledNpcsTemplates} map using the list of {@link AssembledNpcTemplate} templates.<br>
	 * The {@code templates} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (AssembledNpcTemplate template : templates)
		{
			assembledNpcsTemplates.put(template.getNr(), template);
		}
		
		templates.clear();
		templates = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return assembledNpcsTemplates.size();
	}
	
	/**
	 * Retrieves a specific NPC template from the data map.<br>
	 * It uses the provided ID to find the matching object.
	 * @param i The unique identifier for the NPC template.
	 * @return The {@link AssembledNpcTemplate} associated with the ID, or {@code null} if not found.
	 */
	public AssembledNpcTemplate getAssembledNpcTemplate(Integer i)
	{
		return assembledNpcsTemplates.get(i);
	}
}
