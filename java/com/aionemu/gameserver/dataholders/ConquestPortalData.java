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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.portal.ConquestPortal;

/**
 * This class serves as a data holder for {@link ConquestPortal} information.<br>
 * It is used to manage and store the configuration of conquest portals within the game server.
 * @author CoolyT
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "conquest_portals")
public class ConquestPortalData
{
	@XmlElement(name = "portal")
	public List<ConquestPortal> portals = new ArrayList<>();
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return portals.size();
	}
	
	/**
	 * Finds a specific {@link ConquestPortal} based on its NPC ID.<br>
	 * It searches through the internal list of portals.
	 * @param id The unique identifier for the NPC.
	 * @return The matching {@code ConquestPortal} object or {@code null} if not found.
	 */
	public ConquestPortal getPortalbyNpcId(int id)
	{
		for (ConquestPortal portal : portals)
		{
			if (portal.npcId == id)
			{
				return portal;
			}
			
		}
		
		return null;
	}
	
	/**
	 * Retrieves the list of all conquest portals.<br>
	 * This method returns the {@code portals} collection from the data holder.
	 * @return A {@link List} containing all {@link ConquestPortal} objects.
	 */
	public List<ConquestPortal> getPortals()
	{
		return portals;
	}
}
