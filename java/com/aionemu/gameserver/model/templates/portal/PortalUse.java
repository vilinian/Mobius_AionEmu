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
package com.aionemu.gameserver.model.templates.portal;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * Represents the configuration for a portal's usage rules.<br>
 * It defines which {@link com.aionemu.gameserver.model.Race} types are allowed to use the portal.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalUse")
public class PortalUse
{
	@XmlElement(name = "portal_path")
	protected List<PortalPath> portalPath;
	@XmlAttribute(name = "npc_id")
	protected int npcId;
	@XmlAttribute(name = "siege_id")
	protected int siegeId;
	
	/**
	 * Retrieves the list of paths associated with this {@link PortalUse}.<br>
	 * This method returns all available {@code PortalPath} objects.
	 * @return a {@code List} of {@code PortalPath} objects.
	 */
	public List<PortalPath> getPortalPaths()
	{
		return portalPath;
	}
	
	/**
	 * Retrieves the specific portal path for a given race.<br>
	 * It searches through all available paths in this object.<br>
	 * It returns a path that matches the {@code race} or is set to {@code PC_ALL}.
	 * @param race The {@code Race} type to filter by.
	 * @return The matching {@link PortalPath} object, or {@code null} if no match is found.
	 */
	public PortalPath getPortalPath(Race race)
	{
		if (portalPath != null)
		{
			for (PortalPath path : portalPath)
			{
				if (path.getRace().equals(race) || path.getRace().equals(Race.PC_ALL))
				{
					return path;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
	
	/**
	 * Sets the unique identifier for the NPC.<br>
	 * This updates the {@code npcId} field in this {@link PortalDialog} instance.
	 * @param value The new ID to assign to the NPC.
	 */
	public void setNpcId(int value)
	{
		npcId = value;
	}
	
	/**
	 * Retrieves the unique identifier for this siege.<br>
	 * This ID identifies which specific siege event the {@code SiegeNpc} belongs to.
	 * @return The {@code int} value of the siege ID.
	 */
	public int getSiegeId()
	{
		return siegeId;
	}
	
	/**
	 * Sets the unique identifier for a siege.<br>
	 * This value is used to identify specific siege events.
	 * @param value The {@code int} ID of the siege.
	 */
	public void setSiegeId(int value)
	{
		siegeId = value;
	}
}
