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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.housing.Building;
import com.aionemu.gameserver.model.templates.housing.HouseAddress;
import com.aionemu.gameserver.model.templates.housing.HouseType;
import com.aionemu.gameserver.model.templates.housing.HousingLand;

/**
 * This class serves as a data holder for house information within the game.<br>
 * It stores properties related to {@link Building} and {@link HouseAddress} objects.<br>
 * Use this class to manage and retrieve persistent housing data.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"lands"
})
@XmlRootElement(name = "house_lands")
public class HouseData
{
	@XmlElement(name = "land")
	protected List<HousingLand> lands;
	@XmlTransient
	Map<Integer, HousingLand> landsById = new HashMap<>();
	@XmlTransient
	Map<Integer, Set<HousingLand>> landsByEntryWorldId = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code landsById} and {@code landsByEntryWorldId} maps using the list of {@link HousingLand} objects.<br>
	 * The {@code lands} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (lands == null)
		{
			return;
		}
		
		for (HousingLand land : lands)
		{
			landsById.put(land.getId(), land);
			for (HouseAddress address : land.getAddresses())
			{
				Integer exitMapId = address.getExitMapId();
				if (exitMapId == null)
				{
					exitMapId = address.getMapId();
				}
				
				Set<HousingLand> landList = landsByEntryWorldId.get(exitMapId);
				if (landList == null)
				{
					landList = new HashSet<>();
					landsByEntryWorldId.put(exitMapId, landList);
				}
				
				landList.add(land);
			}
		}
		
		lands.clear();
		lands = null;
	}
	
	/**
	 * Retrieves all housing lands associated with a specific world ID.<br>
	 * This method looks up the data in the {@code landsByEntryWorldId} map.
	 * @param worldId The unique identifier for the world.
	 * @return A {@code Set} of {@link HousingLand} objects, or {@code null} if no lands are found.
	 */
	public Set<HousingLand> getLandsForWorldId(int worldId)
	{
		return landsByEntryWorldId.get(worldId);
	}
	
	/**
	 * Finds a specific {@link HousingLand} based on the world and house size.<br>
	 * It searches through all buildings in the specified {@code worldId}.<br>
	 * The method returns the first land that contains a building matching the {@code houseSize}.
	 * @param worldId The unique identifier for the game world.
	 * @param houseSize The required size of the house to match.
	 * @return The matching {@link HousingLand} object, or {@code null} if no match is found.
	 */
	public HousingLand getLandForHouse(int worldId, HouseType houseSize)
	{
		final Set<HousingLand> worldHouseAreas = landsByEntryWorldId.get(worldId);
		if (worldHouseAreas == null)
		{
			return null;
		}
		
		for (HousingLand land : worldHouseAreas)
		{
			for (Building building : land.getBuildings())
			{
				if (houseSize.value().equals(building.getSize()))
				{
					return land;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a specific {@link HousingLand} object from the data map.<br>
	 * It uses the provided unique identifier to find the land.
	 * @param landId The unique ID of the land to retrieve.
	 * @return The {@code HousingLand} associated with the given ID, or {@code null} if not found.
	 */
	public HousingLand getLand(int landId)
	{
		return landsById.get(landId);
	}
	
	/**
	 * Retrieves all housing land records.<br>
	 * This method returns the values from the internal {@code landsById} map.
	 * @return a {@code Collection} of {@link HousingLand} objects.
	 */
	public Collection<HousingLand> getLands()
	{
		return landsById.values();
	}
	
	/**
	 * Returns the total number of housing lands.<br>
	 * This count is based on the internal {@code landsById} map.
	 * @return The number of elements in the collection.
	 */
	public int size()
	{
		return landsById.size();
	}
}
