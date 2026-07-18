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

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class acts as a container for all {@link WorldMapTemplate} objects.<br>
 * These maps are loaded from the {@code data/static_data/world_maps.xml} file.
 * @author Luno
 */
@XmlRootElement(name = "world_maps")
@XmlAccessorType(XmlAccessType.NONE)
public class WorldMapsData implements Iterable<WorldMapTemplate>
{
	@XmlElement(name = "map")
	protected List<WorldMapTemplate> worldMaps;
	protected TIntObjectHashMap<WorldMapTemplate> worldIdMap = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It populates the {@code worldIdMap} using data from {@code worldMaps}.<br>
	 * It maps each map ID to its corresponding {@link WorldMapTemplate} instance.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current instance.
	 */
	protected void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (WorldMapTemplate map : worldMaps)
		{
			worldIdMap.put(map.getMapId(), map);
		}
	}
	
	/**
	 * Returns an {@link Iterator} to loop through all available maps.<br>
	 * This allows you to access each {@link WorldMapTemplate} one by one.
	 * @return An {@code Iterator} of {@link WorldMapTemplate} objects.
	 */
	@Override
	public Iterator<WorldMapTemplate> iterator()
	{
		return worldMaps.iterator();
	}
	
	/**
	 * Returns the total number of world maps.<br>
	 * This method checks if {@code worldMaps} is {@code null}.<br>
	 * If it is null, it returns {@code 0}.<br>
	 * Otherwise, it returns the size of the list.
	 * @return The count of world map templates.
	 */
	public int size()
	{
		return worldMaps == null ? 0 : worldMaps.size();
	}
	
	/**
	 * Retrieves a specific map template from the data store.<br>
	 * This method uses the unique identifier provided to find the correct object.
	 * @param worldId The unique integer ID of the world map.
	 * @return The {@link WorldMapTemplate} associated with the given ID, or {@code null} if not found.
	 */
	public WorldMapTemplate getTemplate(int worldId)
	{
		return worldIdMap.get(worldId);
	}
}
