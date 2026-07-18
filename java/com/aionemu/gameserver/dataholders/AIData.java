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

import com.aionemu.gameserver.model.ai.Ai;
import com.aionemu.gameserver.model.templates.ai.AITemplate;

/**
 * This class serves as a data holder for {@link AITemplate} objects.<br>
 * It is used to manage and store AI-related configuration data within the game server.<br>
 * It facilitates the mapping of AI templates from XML files into the system.
 * @author xTz
 */
@XmlRootElement(name = "ai_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class AIData
{
	@XmlElement(name = "ai", type = Ai.class)
	private List<Ai> templates;
	@XmlTransient
	private final Map<Integer, AITemplate> aiTemplate = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code aiTemplate} map using the list of {@link Ai} templates.<br>
	 * The {@code aiTemplate} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		aiTemplate.clear();
		for (Ai template : templates)
		{
			aiTemplate.put(template.getNpcId(), new AITemplate(template));
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return aiTemplate.size();
	}
	
	/**
	 * Retrieves the collection of AI templates.<br>
	 * This method returns a {@code FastMap} where keys are unique identifiers.
	 * @return A {@code FastMap} containing {@link AITemplate} objects.
	 */
	public Map<Integer, AITemplate> getAiTemplate()
	{
		return aiTemplate;
	}
}
