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

import com.aionemu.gameserver.model.templates.factions.NpcFactionTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link NpcFactionTemplate} objects.<br>
 * It manages the collection of NPC factions loaded from configuration files.<br>
 * It provides easy access to faction data throughout the game server.
 * @author vlog
 */
@XmlRootElement(name = "npc_factions")
@XmlAccessorType(XmlAccessType.FIELD)
public class NpcFactionsData
{
	@XmlElement(name = "npc_faction", required = true)
	protected List<NpcFactionTemplate> npcFactionsData;
	private final TIntObjectHashMap<NpcFactionTemplate> factionsById = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<NpcFactionTemplate> factionsByNpcId = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code factionsById} and {@code factionsByNpcId} maps using the list of {@link NpcFactionTemplate} objects.<br>
	 * The maps are cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		factionsById.clear();
		for (NpcFactionTemplate template : npcFactionsData)
		{
			factionsById.put(template.getId(), template);
			if (template.getNpcId() != 0)
			{
				factionsByNpcId.put(template.getNpcId(), template);
			}
		}
	}
	
	/**
	 * Retrieves a specific faction template using its unique identifier.<br>
	 * This method looks up the data in the internal map.
	 * @param id The unique integer ID of the faction to find.
	 * @return The {@link NpcFactionTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public NpcFactionTemplate getNpcFactionById(int id)
	{
		return factionsById.get(id);
	}
	
	/**
	 * Retrieves a faction template based on a specific NPC ID.<br>
	 * This method looks up the data in the internal map.
	 * @param id The unique identifier of the NPC.
	 * @return The {@link NpcFactionTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public NpcFactionTemplate getNpcFactionByNpcId(int id)
	{
		return factionsByNpcId.get(id);
	}
	
	/**
	 * Retrieves the list of all NPC faction templates.<br>
	 * This method returns the internal data loaded from the configuration.
	 * @return a {@code List} containing all {@link NpcFactionTemplate} objects.
	 */
	public List<NpcFactionTemplate> getNpcFactionsData()
	{
		return npcFactionsData;
	}
	
	/**
	 * Returns the total number of NPC factions.<br>
	 * This method retrieves the size from the {@code npcFactionsData} list.
	 * @return The count of NPC faction templates.
	 */
	public int size()
	{
		return npcFactionsData.size();
	}
}
