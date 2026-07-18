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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.portal.PortalLoc;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for portal location information.<br>
 * It stores and manages the mapping of {@link com.aionemu.gameserver.model.templates.portal.PortalLoc} objects within the game world.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"portalLoc"
})
@XmlRootElement(name = "portal_locs")
public class PortalLocData
{
	@XmlElement(name = "portal_loc")
	protected List<PortalLoc> portalLoc;
	@XmlTransient
	private final TIntObjectHashMap<PortalLoc> portalLocs = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data is loaded.<br>
	 * It copies elements from the {@code portalLoc} list into the internal {@code portalLocs} map.
	 * @param unmarshaller The {@link Unmarshaller} used to read the data.
	 * @param parent The object that contains this data.
	 */
	void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
	{
		for (PortalLoc loc : portalLoc)
		{
			portalLocs.put(loc.getLocId(), loc);
			
		}
		
		portalLoc.clear();
		portalLoc = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return portalLocs.size();
	}
	
	/**
	 * Retrieves a specific {@link PortalLoc} object based on its unique ID.<br>
	 * This method looks up the data in the internal map.
	 * @param locId The unique identifier for the portal location.
	 * @return The {@code PortalLoc} associated with the given {@code locId}, or {@code null} if not found.
	 */
	public PortalLoc getPortalLoc(int locId)
	{
		return portalLocs.get(locId);
	}
}
