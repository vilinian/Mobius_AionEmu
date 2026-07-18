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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.portal.PortalDialog;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.model.templates.portal.PortalScroll;
import com.aionemu.gameserver.model.templates.portal.PortalUse;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@code Portal2} configuration information.<br>
 * It stores and manages the properties required to define portal behaviors in the game world.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"portalUse",
	"portalDialog",
	"portalScroll"
})
@XmlRootElement(name = "portal_templates2")
public class Portal2Data
{
	@XmlElement(name = "portal_use")
	protected List<PortalUse> portalUse;
	@XmlElement(name = "portal_dialog")
	protected List<PortalDialog> portalDialog;
	@XmlElement(name = "portal_scroll")
	protected List<PortalScroll> portalScroll;
	@XmlTransient
	private final TIntObjectHashMap<PortalUse> portalUses = new TIntObjectHashMap<>();
	@XmlTransient
	private final TIntObjectHashMap<PortalDialog> portalDialogs = new TIntObjectHashMap<>();
	@XmlTransient
	private final Map<String, PortalScroll> portalScrolls = new HashMap<>();
	
	/**
	 * This method is called after the XML data is loaded.<br>
	 * It copies elements from the {@code portalUse}, {@code portalDialog}, and {@code portalScroll} lists into internal maps.
	 * @param unmarshaller The {@link Unmarshaller} used to read the data.
	 * @param parent The object that contains this data.
	 */
	void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
	{
		if (portalUse != null)
		{
			for (PortalUse portal : portalUse)
			{
				portalUses.put(portal.getNpcId(), portal);
			}
		}
		
		if (portalDialog != null)
		{
			for (PortalDialog portal : portalDialog)
			{
				portalDialogs.put(portal.getNpcId(), portal);
			}
		}
		
		if (portalScroll != null)
		{
			for (PortalScroll portal : portalScroll)
			{
				portalScrolls.put(portal.getName(), portal);
			}
		}
	}
	
	/**
	 * Returns the total number of portal elements.<br>
	 * This counts all entries in {@code portalScrolls}, {@code portalDialogs}, and {@code portalUses}.
	 * @return The sum of all portal templates currently loaded.
	 */
	public int size()
	{
		return portalScrolls.size() + portalDialogs.size() + portalUses.size();
	}
	
	/**
	 * Retrieves a specific {@link PortalPath} based on the NPC and dialog details.<br>
	 * This method checks if the provided {@code race} matches the path or is set to {@code Race.PC_ALL}.<br>
	 * It returns {@code null} if no matching path is found.
	 * @param npcId The unique identifier for the NPC.
	 * @param dialogId The specific ID of the dialog to match.
	 * @param race The character race used to filter the result.
	 * @return The matching {@link PortalPath} object or {@code null}.
	 */
	public PortalPath getPortalDialog(int npcId, int dialogId, Race race)
	{
		final PortalDialog portal = portalDialogs.get(npcId);
		if (portal != null)
		{
			for (PortalPath path : portal.getPortalPath())
			{
				if ((path.getDialog() == dialogId) && (race.equals(path.getRace()) || path.getRace().equals(Race.PC_ALL)))
				{
					return path;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if a specific NPC is associated with any portal data.<br>
	 * It returns {@code true} if the NPC has an entry in either {@code portalUses} or {@code portalDialogs}.<br>
	 * Otherwise, it returns {@code false}.
	 * @param npcId The unique identifier of the NPC to check.
	 * @return {@code true} if the NPC is a portal NPC, {@code false} otherwise.
	 */
	public boolean isPortalNpc(int npcId)
	{
		return (portalUses.get(npcId) != null) || (portalDialogs.get(npcId) != null);
	}
	
	/**
	 * Retrieves the {@code PortalUse} data for a specific NPC.<br>
	 * This method looks up the information using the provided {@code npcId}.
	 * @param npcId The unique identifier of the NPC.
	 * @return The {@code PortalUse} object associated with the ID, or {@code null} if not found.
	 */
	public PortalUse getPortalUse(int npcId)
	{
		return portalUses.get(npcId);
	}
	
	/**
	 * Retrieves a {@link PortalScroll} object based on its unique name.<br>
	 * This method looks up the scroll in the internal data map.
	 * @param name The unique identifier for the portal scroll.
	 * @return The corresponding {@code PortalScroll} object or {@code null} if not found.
	 */
	public PortalScroll getPortalScroll(String name)
	{
		return portalScrolls.get(name);
	}
	
	/**
	 * Retrieves the unique identifier for a teleport dialog associated with an NPC.<br>
	 * This method looks up the {@code PortalDialog} using the provided {@code npcId}.<br>
	 * If no dialog is found, it returns the default value of {@code 1011}.
	 * @param npcId The unique identifier of the NPC to check.
	 * @return The teleport dialog ID associated with the NPC.
	 */
	public int getTeleportDialogId(int npcId)
	{
		final PortalDialog portal = portalDialogs.get(npcId);
		return portal == null ? 1011 : portal.getTeleportDialogId();
	}
}
