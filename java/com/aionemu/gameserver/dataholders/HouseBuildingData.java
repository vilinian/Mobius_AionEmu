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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.housing.Building;

/**
 * This class serves as a data holder for house building information.<br>
 * It stores and manages the properties associated with {@link Building} templates.<br>
 * Use this class to access configuration data related to player housing.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"buildings"
})
public class HouseBuildingData
{
	@XmlElement(name = "building")
	protected List<Building> buildings;
	@XmlTransient
	Map<Integer, Building> buildingById = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code buildingById} map using the list of {@link Building} objects.<br>
	 * The {@code buildings} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (buildings == null)
		{
			return;
		}
		
		for (Building building : buildings)
		{
			buildingById.put(building.getId(), building);
		}
		
		buildings.clear();
		buildings = null;
	}
	
	/**
	 * Retrieves a {@link Building} object based on its unique ID.<br>
	 * This method looks up the data in the internal map.
	 * @param buildingId The unique integer identifier for the building.
	 * @return The {@code Building} associated with the provided ID, or {@code null} if not found.
	 */
	public Building getBuilding(int buildingId)
	{
		return buildingById.get(buildingId);
	}
	
	/**
	 * Returns the number of buildings in this data holder.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of buildings currently stored.
	 */
	public int size()
	{
		return buildingById.size();
	}
}
