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

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.staticdoor.StaticDoorWorld;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link StaticDoorWorld} templates.<br>
 * It stores and manages the static configuration data for doors within the game world.
 * @author Wakizashi
 */
@XmlRootElement(name = "staticdoor_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class StaticDoorData
{
	@XmlElement(name = "world")
	private List<StaticDoorWorld> staticDorWorlds;
	/**
	 * A map containing all door templates
	 */
	private final TIntObjectHashMap<StaticDoorWorld> staticDoorData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code staticDoorData} map using the list of {@link StaticDoorWorld} templates.<br>
	 * The {@code staticDoorData} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		staticDoorData.clear();
		
		for (StaticDoorWorld world : staticDorWorlds)
		{
			staticDoorData.put(world.getWorld(), world);
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return staticDoorData.size();
	}
	
	/**
	 * Retrieves the {@link StaticDoorWorld} data for a specific world ID.<br>
	 * This method looks up the template in the internal map.
	 * @param world The unique identifier of the world to search for.
	 * @return The {@code StaticDoorWorld} object associated with the given world, or {@code null} if not found.
	 */
	public StaticDoorWorld getStaticDoorWorlds(int world)
	{
		return staticDoorData.get(world);
	}
	
	/**
	 * Retrieves the list of all door worlds.<br>
	 * This method returns the internal collection of {@link StaticDoorWorld} objects.
	 * @return a {@code List} containing all {@code StaticDoorWorld} entries.
	 */
	public List<StaticDoorWorld> getStaticDorWorlds()
	{
		return staticDorWorlds;
	}
	
	/**
	 * Sets the list of {@link StaticDoorWorld} objects.<br>
	 * This method updates the internal data and triggers a refresh.
	 * @param staticDorWorlds The list of worlds to set.
	 */
	public void setStaticDorWorlds(List<StaticDoorWorld> staticDorWorlds)
	{
		this.staticDorWorlds = staticDorWorlds;
		afterUnmarshal(null, null);
	}
}
