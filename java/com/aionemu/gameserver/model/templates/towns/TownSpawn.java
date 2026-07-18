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

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Represents a specific spawn point within a town.<br>
 * This class stores the coordinates and configuration for where NPCs or players can appear in a {@code Town}.
 * @author ViAl
 */
@XmlType(name = "town_spawn")
public class TownSpawn
{
	@XmlAttribute(name = "town_id")
	private int townId;
	@XmlElement(name = "town_level")
	private List<TownLevel> townLevels;
	private final TIntObjectHashMap<TownLevel> townLevelsData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code townLevelsData} map using the list of {@link TownLevel} objects.<br>
	 * The {@code townLevels} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		townLevelsData.clear();
		
		for (TownLevel level : townLevels)
		{
			townLevelsData.put(level.getLevel(), level);
		}
		
		townLevels.clear();
		townLevels = null;
	}
	
	/**
	 * Retrieves the unique identifier for the town associated with this object.<br>
	 * This value is used to determine which town area the entity belongs to.
	 * @return the {@code int} ID of the town.
	 */
	public int getTownId()
	{
		return townId;
	}
	
	/**
	 * Retrieves the {@link TownLevel} data for a specific level.<br>
	 * This method looks up the information in the internal map.
	 * @param level The integer level to search for.
	 * @return The {@code TownLevel} object associated with the given level, or {@code null} if not found.
	 */
	public TownLevel getSpawnsForLevel(int level)
	{
		return townLevelsData.get(level);
	}
	
	/**
	 * Retrieves all the levels associated with this town.<br>
	 * This method returns a collection of {@link TownLevel} objects.
	 * @return A {@code Collection} of {@code TownLevel} data.
	 */
	public Collection<TownLevel> getTownLevels()
	{
		return townLevelsData.valueCollection();
	}
}
