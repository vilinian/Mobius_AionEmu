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

import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;

/**
 * This class serves as a data holder for {@link FlyRingTemplate} objects.<br>
 * It is used to manage and store the collection of fly ring configurations loaded from XML files.
 * @author M@xx
 */
@XmlRootElement(name = "fly_rings")
@XmlAccessorType(XmlAccessType.FIELD)
public class FlyRingData
{
	@XmlElement(name = "fly_ring")
	private List<FlyRingTemplate> flyRingTemplates;
	
	/**
	 * Returns the number of {@link FlyRingTemplate} objects in this collection.<br>
	 * If the list is null, it initializes a new {@code ArrayList} and returns 0.
	 * @return The total count of fly ring templates.
	 */
	public int size()
	{
		if (flyRingTemplates == null)
		{
			flyRingTemplates = new ArrayList<>();
			return 0;
		}
		
		return flyRingTemplates.size();
	}
	
	/**
	 * Retrieves the list of all {@link FlyRingTemplate} objects.<br>
	 * If the internal list is {@code null}, it returns an empty {@code ArrayList}.
	 * @return A {@code List} containing the fly ring templates.
	 */
	public List<FlyRingTemplate> getFlyRingTemplates()
	{
		if (flyRingTemplates == null)
		{
			return new ArrayList<>();
		}
		
		return flyRingTemplates;
	}
	
	/**
	 * Adds all elements from a collection to the internal list.<br>
	 * This method initializes the list if it is currently {@code null}.
	 * @param templates The collection of {@link FlyRingTemplate} objects to add.
	 */
	public void addAll(Collection<FlyRingTemplate> templates)
	{
		if (flyRingTemplates == null)
		{
			flyRingTemplates = new ArrayList<>();
		}
		
		flyRingTemplates.addAll(templates);
	}
}
