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
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.road.RoadTemplate;

/**
 * This class serves as a data holder for road information within the game world.<br>
 * It acts as a container for a collection of {@link RoadTemplate} objects loaded from XML configuration files.
 * @author SheppeR
 */
@XmlRootElement(name = "roads")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoadData
{
	@XmlElement(name = "road")
	private List<RoadTemplate> roadTemplates;
	
	/**
	 * Returns the total number of {@link RoadTemplate} objects.<br>
	 * If the list is null, it initializes a new {@code ArrayList} and returns {@code 0}.
	 * @return The count of road templates currently stored.
	 */
	public int size()
	{
		if (roadTemplates == null)
		{
			roadTemplates = new ArrayList<>();
			return 0;
		}
		
		return roadTemplates.size();
	}
	
	/**
	 * Retrieves the list of all road templates.<br>
	 * If the internal list is {@code null}, it returns an empty {@code ArrayList}.
	 * @return a {@code List} of {@link RoadTemplate} objects.
	 */
	public List<RoadTemplate> getRoadTemplates()
	{
		if (roadTemplates == null)
		{
			return new ArrayList<>();
		}
		
		return roadTemplates;
	}
	
	/**
	 * Adds all elements from a collection to the internal list of {@link RoadTemplate} objects.<br>
	 * This method initializes the list if it is currently {@code null}.
	 * @param templates The collection of {@code RoadTemplate} items to add.
	 */
	public void addAll(Collection<RoadTemplate> templates)
	{
		if (roadTemplates == null)
		{
			roadTemplates = new ArrayList<>();
		}
		
		roadTemplates.addAll(templates);
	}
}
