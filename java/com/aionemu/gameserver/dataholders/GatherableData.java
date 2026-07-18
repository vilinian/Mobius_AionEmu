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

import java.util.Collections;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link GatherableTemplate} objects.<br>
 * It manages the collection of all gatherable items loaded from the configuration files.<br>
 * Use this class to access template data during game logic execution.
 * @author ATracer
 */
@XmlRootElement(name = "gatherable_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class GatherableData
{
	@XmlElement(name = "gatherable_template")
	private List<GatherableTemplate> gatherables;
	/**
	 * A map containing all npc templates
	 */
	private final TIntObjectHashMap<GatherableTemplate> gatherableData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code gatherableData} map using the list of {@link GatherableTemplate} objects.<br>
	 * The materials lists are sorted during this process.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (GatherableTemplate gatherable : gatherables)
		{
			if (gatherable.getMaterials() != null)
			{
				Collections.sort(gatherable.getMaterials().getMaterial());
			}
			
			if (gatherable.getExtraMaterials() != null)
			{
				Collections.sort(gatherable.getExtraMaterials().getMaterial());
			}
			
			gatherableData.put(gatherable.getTemplateId(), gatherable);
		}
		
		gatherables = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return gatherableData.size();
	}
	
	/**
	 * Retrieves a specific {@link GatherableTemplate} based on its unique ID.<br>
	 * This method looks up the template in the internal data map.
	 * @param id The unique identifier of the gatherable template to find.
	 * @return The {@code GatherableTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public GatherableTemplate getGatherableTemplate(int id)
	{
		return gatherableData.get(id);
	}
}
