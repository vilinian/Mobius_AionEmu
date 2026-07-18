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
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.spawns.HouseSpawn;
import com.aionemu.gameserver.model.templates.spawns.HouseSpawns;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for NPC information related to houses.<br>
 * It stores and manages the collection of {@link HouseSpawn} objects within the game world.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"houseSpawnsData"
})
@XmlRootElement(name = "house_npcs")
public class HouseNpcsData
{
	@XmlElement(name = "house")
	protected List<HouseSpawns> houseSpawnsData;
	
	/**
	 * Retrieves the list of all house spawn data.<br>
	 * This method ensures that a non-null {@code List} is returned.
	 * @return A {@code List} of {@link HouseSpawns} objects.
	 */
	public List<HouseSpawns> getHouseSpawns()
	{
		if (houseSpawnsData == null)
		{
			houseSpawnsData = new ArrayList<>();
		}
		
		return houseSpawnsData;
	}
	
	@XmlTransient
	private final TIntObjectHashMap<List<HouseSpawn>> houseSpawnsByAddressId = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code houseSpawnsByAddressId} map using the list of {@link HouseSpawns}.<br>
	 * The {@code houseSpawnsByAddressId} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (HouseSpawns houseSpawns : getHouseSpawns())
		{
			houseSpawnsByAddressId.put(houseSpawns.getAddress(), houseSpawns.getSpawns());
		}
	}
	
	/**
	 * Retrieves a list of {@link HouseSpawn} objects for a specific location.<br>
	 * This method looks up the spawns based on the provided address ID.
	 * @param address The unique identifier for the house address.
	 * @return A {@code List} of {@link HouseSpawn} objects, or {@code null} if no spawns are found.
	 */
	public List<HouseSpawn> getSpawnsByAddress(int address)
	{
		return houseSpawnsByAddressId.get(address);
	}
	
	/**
	 * Calculates the total number of items based on the internal map.<br>
	 * This method multiplies the count of entries by {@code 3}.
	 * @return The calculated size of the data collection.
	 */
	public int size()
	{
		return houseSpawnsByAddressId.size() * 3;
	}
}
