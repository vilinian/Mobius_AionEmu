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

import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data container for all {@link TeleporterTemplate} instances.<br>
 * It manages the configuration and retrieval of teleporters based on their unique identifiers.
 * @author orz
 */
@XmlRootElement(name = "npc_teleporter")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeleporterData
{
	@XmlElement(name = "teleporter_template")
	private List<TeleporterTemplate> tlist;
	/**
	 * A map containing all trade list templates
	 */
	private final TIntObjectHashMap<TeleporterTemplate> npctlistData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code npctlistData} map using the list of {@link TeleporterTemplate} templates.<br>
	 * The {@code npctlistData} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (TeleporterTemplate template : tlist)
		{
			npctlistData.put(template.getTeleportId(), template);
		}
	}
	
	/**
	 * Returns the total number of teleporter templates stored in this container.<br>
	 * This method calls {@code size} to retrieve the count.
	 * @return The number of items currently held in the data map.
	 */
	public int size()
	{
		return npctlistData.size();
	}
	
	/**
	 * Retrieves a {@link TeleporterTemplate} based on the provided NPC ID.<br>
	 * This method searches through all registered teleporter templates.<br>
	 * It returns the first template that contains the specified {@code npcId}.
	 * @param npcId The unique identifier of the NPC to search for.
	 * @return The matching {@link TeleporterTemplate} or {@code null} if no match is found.
	 */
	public TeleporterTemplate getTeleporterTemplateByNpcId(int npcId)
	{
		for (TeleporterTemplate template : npctlistData.valueCollection())
		{
			if (template.containNpc(npcId))
			{
				return template;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a {@link TeleporterTemplate} using its unique ID.<br>
	 * This method looks up the template in the internal data map.
	 * @param teleportId The unique identifier for the teleporter.
	 * @return The corresponding {@link TeleporterTemplate} or {@code null} if not found.
	 */
	public TeleporterTemplate getTeleporterTemplateByTeleportId(int teleportId)
	{
		return npctlistData.get(teleportId);
	}
}
