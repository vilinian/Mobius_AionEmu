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

import com.aionemu.gameserver.model.rift.RiftLocation;
import com.aionemu.gameserver.model.templates.rift.RiftTemplate;

/**
 * This class serves as a data holder for {@link RiftTemplate} information.<br>
 * It manages the collection of {@link RiftLocation} objects loaded from XML configuration files.
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "rift_locations")
public class RiftData
{
	@XmlElement(name = "rift_location")
	private List<RiftTemplate> riftTemplates;
	@XmlTransient
	private final Map<Integer, RiftLocation> rift = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code rift} map using the list of {@link RiftTemplate} templates.<br>
	 * The {@code rift} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (RiftTemplate template : riftTemplates)
		{
			rift.put(template.getId(), new RiftLocation(template));
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return rift.size();
	}
	
	/**
	 * Retrieves the collection of all rift locations.<br>
	 * This method returns a {@code FastMap} where keys are IDs and values are {@link RiftLocation} objects.
	 * @return A {@code FastMap} containing all registered rift locations.
	 */
	public Map<Integer, RiftLocation> getRiftLocations()
	{
		return rift;
	}
}
