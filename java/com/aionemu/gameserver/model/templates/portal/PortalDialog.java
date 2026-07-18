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

/**
 * Represents the dialog configuration for a portal in the game world.<br>
 * This class defines the text and options displayed to players when interacting with a {@code Portal}.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalDialog", propOrder =
{
	"portalPath"
})

public class PortalDialog
{
	@XmlElement(name = "portal_path")
	protected List<PortalPath> portalPath;
	@XmlAttribute(name = "npc_id")
	protected int npcId;
	@XmlAttribute(name = "siege_id")
	protected int siegeId;
	@XmlAttribute(name = "teleport_dialog_id")
	protected int teleportDialogId = 1011;
	
	/**
	 * Retrieves the list of paths associated with this {@link PortalDialog}.<br>
	 * This method returns the {@code portalPath} field.
	 * @return a {@code List} of {@link PortalPath} objects.
	 */
	public List<PortalPath> getPortalPath()
	{
		return portalPath;
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
	
	/**
	 * Retrieves the unique identifier for the teleport dialog.<br>
	 * This ID is used to display the correct message when a player uses a portal.
	 * @return The {@code int} value of the teleport dialog ID.
	 */
	public int getTeleportDialogId()
	{
		return teleportDialogId;
	}
}
