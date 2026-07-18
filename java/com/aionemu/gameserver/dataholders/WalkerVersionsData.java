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

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.walker.RouteParent;
import com.aionemu.gameserver.model.templates.walker.RouteVersion;

/**
 * This class serves as a data holder for walker version information.<br>
 * It stores and manages {@link RouteVersion} objects used by the game server.<br>
 * It is primarily used to handle XML data related to walker routes.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"routeGroups"
})
@XmlRootElement(name = "walker_versions")
public class WalkerVersionsData
{
	@XmlElement(name = "walk_parent")
	protected List<RouteParent> routeGroups;
	@XmlTransient
	protected HashMap<String, String> walkParents = new HashMap<>();
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It populates the {@code walkParents} map using data from {@code routeGroups}.<br>
	 * It links each {@code RouteVersion} ID to its corresponding {@code RouteParent} ID.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current instance.
	 */
	protected void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (RouteParent group : routeGroups)
		{
			for (RouteVersion version : group.getRouteVersion())
			{
				walkParents.put(version.getId(), group.getId());
			}
		}
	}
	
	/**
	 * Checks if a specific route has an associated version.<br>
	 * It returns {@code true} if the {@code routeId} exists in the internal map.<br>
	 * If the {@code routeId} is {@code null}, it returns {@code false}.
	 * @param routeId The unique identifier of the route to check.
	 * @return {@code true} if the route is versioned, otherwise {@code false}.
	 */
	public boolean isRouteVersioned(String routeId)
	{
		if (routeId == null)
		{
			return false;
		}
		
		return walkParents.containsKey(routeId);
	}
	
	/**
	 * Retrieves the version identifier for a specific route.<br>
	 * This method looks up the {@code routeId} in the internal map.
	 * @param routeId The unique identifier of the route to look up.
	 * @return The version ID as a {@code String}, or {@code null} if not found.
	 */
	public String getRouteVersionId(String routeId)
	{
		if (routeId == null)
		{
			return null;
		}
		
		return walkParents.get(routeId);
	}
	
	/**
	 * Returns the total number of route parents.<br>
	 * This method calls {@code walkParents.size()} to get the count.
	 * @return The size of the internal map.
	 */
	public int size()
	{
		return walkParents.size();
	}
}
