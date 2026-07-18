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
package com.aionemu.gameserver.model.templates.towns;

import java.util.Collection;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class manages the spawn points for NPCs and players within a specific town.<br>
 * It maps coordinates to their respective spawn data using a {@code TIntObjectHashMap}.<br>
 * Use this class to handle spatial distribution of entities in town areas.
 * @author ViAl
 */
@XmlType(name = "town_spawn_map")
public class TownSpawnMap
{
	@XmlAttribute(name = "map_id")
	private int mapId;
	@XmlElement(name = "town_spawn")
	private List<TownSpawn> townSpawns;
	private final TIntObjectHashMap<TownSpawn> townSpawnsData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code townSpawnsData} map using the list of {@link TownSpawn} objects.<br>
	 * The {@code townSpawnsData} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		townSpawnsData.clear();
		
		for (TownSpawn town : townSpawns)
		{
			townSpawnsData.put(town.getTownId(), town);
		}
		
		townSpawns.clear();
		townSpawns = null;
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * This value is assigned during the creation of the {@link AssembledNpc}.
	 * @return The {@code int} representing the map ID.
	 */
	public int getMapId()
	{
		return mapId;
	}
	
	/**
	 * Retrieves a specific {@link TownSpawn} based on its unique ID.<br>
	 * This method looks up the data in the internal map.
	 * @param townId The unique identifier for the town spawn.
	 * @return The {@code TownSpawn} object associated with the given ID, or {@code null} if not found.
	 */
	public TownSpawn getTownSpawn(int townId)
	{
		return townSpawnsData.get(townId);
	}
	
	/**
	 * Retrieves all {@link TownSpawn} objects from the map.<br>
	 * This method returns a collection of every spawn point defined in this map.
	 * @return A {@code Collection} containing all {@code TownSpawn} instances.
	 */
	public Collection<TownSpawn> getTownSpawns()
	{
		return townSpawnsData.valueCollection();
	}
}
