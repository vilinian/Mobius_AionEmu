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

import com.aionemu.gameserver.model.templates.teleport.HotspotTeleportTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for a hotspot teleporter.<br>
 * It maps specific coordinates to their corresponding {@link HotspotTeleportTemplate}.
 * @author Alcapwnd
 */
@XmlRootElement(name = "hotspot_teleport")
@XmlAccessorType(XmlAccessType.FIELD)
public class HotspotTeleporterData
{
	@XmlElement(name = "hotspot_template")
	private List<HotspotTeleportTemplate> tlist;
	/**
	 * A map containing all teleport location templates
	 */
	private final TIntObjectHashMap<HotspotTeleportTemplate> loctlistData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code loctlistData} map using the list of {@link HotspotTeleportTemplate} templates.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (HotspotTeleportTemplate loc : tlist)
		{
			loctlistData.put(loc.getLocId(), loc);
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return loctlistData.size();
	}
	
	/**
	 * Retrieves a specific teleport template from the data map.<br>
	 * This method uses the provided {@code id} to find the matching object.
	 * @param id The unique identifier of the hotspot template.
	 * @return The {@link HotspotTeleportTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public HotspotTeleportTemplate getHotspotTemplate(int id)
	{
		return loctlistData.get(id);
	}
}
