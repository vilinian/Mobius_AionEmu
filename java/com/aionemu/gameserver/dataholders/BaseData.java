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

import com.aionemu.gameserver.model.base.BaseLocation;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * This class serves as a container for base location data.<br>
 * It holds the collection of {@link BaseLocation} objects used by the server.<br>
 * It is primarily used to map and manage template information.
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "base_locations")
public class BaseData
{
	@XmlElement(name = "base_location")
	private List<BaseTemplate> baseTemplates;
	@XmlTransient
	private final Map<Integer, BaseLocation> base = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code base} map using the list of {@link BaseTemplate} templates.<br>
	 * The {@code base} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (BaseTemplate template : baseTemplates)
		{
			base.put(template.getId(), new BaseLocation(template));
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return base.size();
	}
	
	/**
	 * Retrieves the collection of all base locations.<br>
	 * This method returns a {@code FastMap} where the keys are unique identifiers.
	 * @return A {@code FastMap} containing {@link BaseLocation} objects.
	 */
	public Map<Integer, BaseLocation> getBaseLocations()
	{
		return base;
	}
}
