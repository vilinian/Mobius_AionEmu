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

import com.aionemu.gameserver.model.templates.chest.ChestTemplate;

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link ChestTemplate} objects.<br>
 * It manages the collection of chest templates loaded from XML configuration files.<br>
 * Use this class to access global chest data within the game server.
 * @author Wakizashi
 */
@XmlRootElement(name = "chest_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChestData
{
	@XmlElement(name = "chest")
	private List<ChestTemplate> chests;
	/**
	 * A map containing all npc templates
	 */
	private final TIntObjectHashMap<ChestTemplate> chestData = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<ArrayList<ChestTemplate>> instancesMap = new TIntObjectHashMap<>();
	private final THashMap<String, ChestTemplate> namedChests = new THashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code chestData}, {@code instancesMap}, and {@code namedChests} maps using the list of {@link ChestTemplate} objects.<br>
	 * The maps are cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		chestData.clear();
		instancesMap.clear();
		namedChests.clear();
		
		for (ChestTemplate chest : chests)
		{
			chestData.put(chest.getNpcId(), chest);
			if ((chest.getName() != null) && !chest.getName().isEmpty())
			{
				namedChests.put(chest.getName(), chest);
			}
		}
	}
	
	/**
	 * Returns the total number of chest templates stored in this data holder.<br>
	 * This method calls {@code size} to retrieve the count.
	 * @return The number of chest templates currently available.
	 */
	public int size()
	{
		return chestData.size();
	}
	
	/**
	 * Retrieves a {@link ChestTemplate} based on the provided NPC ID.<br>
	 * This method looks up the template in the internal data map.
	 * @param npcId The unique identifier for the NPC.
	 * @return The {@code ChestTemplate} associated with the ID, or {@code null} if not found.
	 */
	public ChestTemplate getChestTemplate(int npcId)
	{
		return chestData.get(npcId);
	}
	
	/**
	 * Retrieves the list of all chest templates.<br>
	 * This method returns the internal {@code chests} collection.
	 * @return a {@code List} containing all {@link ChestTemplate} objects.
	 */
	public List<ChestTemplate> getChests()
	{
		return chests;
	}
	
	/**
	 * Sets the list of {@link ChestTemplate} objects for this data holder.<br>
	 * This method updates the internal chest list and triggers the {@code Object)} method.
	 * @param chests The list of {@code ChestTemplate} objects to set.
	 */
	public void setChests(List<ChestTemplate> chests)
	{
		this.chests = chests;
		afterUnmarshal(null, null);
	}
}
