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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents the data structure for teleport location identifiers.<br>
 * It maps specific IDs to their corresponding coordinates within the game world.<br>
 * Use this model to handle {@code teleport} data loaded from configuration files.
 * @author ATracer
 */
@XmlRootElement(name = "locations")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeleLocIdData
{
	@XmlElement(name = "telelocation")
	private List<TeleportLocation> locids;
	
	/**
	 * Retrieves the list of all teleport locations.<br>
	 * This method returns the {@code locids} collection from the data object.
	 * @return A {@code List} of {@link TeleportLocation} objects.
	 */
	public List<TeleportLocation> getTelelocations()
	{
		return locids;
	}
	
	/**
	 * Finds a specific teleport location by its ID.<br>
	 * It searches through the list of all available locations.
	 * @param value The unique ID of the {@code TeleportLocation} to find.
	 * @return The matching {@code TeleportLocation} object, or {@code null} if no match is found.
	 */
	public TeleportLocation getTeleportLocation(int value)
	{
		for (TeleportLocation t : locids)
		{
			if ((t != null) && (t.getLocId() == value))
			{
				return t;
			}
		}
		
		return null;
	}
}
