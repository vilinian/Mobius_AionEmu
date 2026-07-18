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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.housing.Building;
import com.aionemu.gameserver.model.templates.housing.HousePart;

/**
 * This class serves as a data holder for information regarding house parts.<br>
 * It maps {@link HousePart} templates to their respective properties within the game world.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"houseParts"
})
@XmlRootElement(name = "house_parts")
public class HousePartsData
{
	@XmlElement(name = "house_part")
	protected List<HousePart> houseParts;
	@XmlTransient
	Map<String, List<HousePart>> partsByTags = new HashMap<>(5);
	@XmlTransient
	Map<Integer, HousePart> partsById = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code partsById} and {@code partsByTags} maps using the list of {@link HousePart} objects.<br>
	 * The {@code houseParts} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (houseParts == null)
		{
			return;
		}
		
		for (HousePart part : houseParts)
		{
			partsById.put(part.getId(), part);
			final Iterator<String> iterator = part.getTags().iterator();
			while (iterator.hasNext())
			{
				final String tag = iterator.next();
				List<HousePart> parts = partsByTags.get(tag);
				if (parts == null)
				{
					parts = new ArrayList<>();
					partsByTags.put(tag, parts);
				}
				
				parts.add(part);
			}
		}
		
		houseParts.clear();
		houseParts = null;
	}
	
	/**
	 * Retrieves a specific {@link HousePart} using its unique identifier.<br>
	 * This method looks up the part in the internal data map.
	 * @param partId The unique ID of the house part to find.
	 * @return The {@code HousePart} associated with the given ID, or {@code null} if not found.
	 */
	public HousePart getPartById(int partId)
	{
		return partsById.get(partId);
	}
	
	/**
	 * Retrieves all {@link HousePart} objects associated with a specific {@link Building}.<br>
	 * This method looks up the parts using the tag assigned to the building.
	 * @param building The {@code Building} object used to find matching parts.
	 * @return A {@code List} of {@link HousePart} objects that match the building's tag.
	 */
	public List<HousePart> getPartsForBuilding(Building building)
	{
		return partsByTags.get(building.getPartsMatchTag());
	}
	
	/**
	 * Returns the total number of house parts.<br>
	 * This count is based on the internal {@code partsById} map.
	 * @return The size of the collection.
	 */
	public int size()
	{
		return partsById.size();
	}
}
