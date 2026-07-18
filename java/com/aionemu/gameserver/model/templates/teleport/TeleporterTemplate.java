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
package com.aionemu.gameserver.model.templates.teleport;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the configuration data for a teleportation point in the game world.<br>
 * This template defines the properties used to initialize {@code Teleporter} objects.
 * @author orz
 */
@XmlRootElement(name = "teleporter_template")
@XmlAccessorType(XmlAccessType.NONE)
public class TeleporterTemplate
{
	@XmlAttribute(name = "npc_ids")
	private List<Integer> npcIds;
	@XmlAttribute(name = "teleportId", required = true)
	private int teleportId = 0;
	@XmlElement(name = "locations")
	private TeleLocIdData teleLocIdData;
	
	/**
	 * Retrieves the list of NPC identifiers for this group.<br>
	 * Returns an empty {@code List} if no IDs are defined.
	 * @return a {@code List<Integer>} containing the NPC IDs.
	 */
	public List<Integer> getNpcIds()
	{
		return npcIds;
	}
	
	/**
	 * Checks if a specific NPC is part of this teleporter.<br>
	 * It looks through the list of IDs provided in {@code getNpcIds}.
	 * @param npcId The unique ID of the NPC to check.
	 * @return {@code true} if the NPC exists in the list, otherwise {@code false}.
	 */
	public boolean containNpc(int npcId)
	{
		return npcIds.contains(npcId);
	}
	
	/**
	 * Retrieves the unique identifier for this teleport.<br>
	 * This value is used to identify specific teleport points in the game.
	 * @return The {@code int} value of the teleport ID.
	 */
	public int getTeleportId()
	{
		return teleportId;
	}
	
	/**
	 * Retrieves the location identification data for this teleporter.<br>
	 * This method returns the {@code TeleLocIdData} object associated with the template.
	 * @return the {@code TeleLocIdData} object.
	 */
	public TeleLocIdData getTeleLocIdData()
	{
		return teleLocIdData;
	}
}
