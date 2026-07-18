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

import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;

import gnu.trove.map.hash.TShortObjectHashMap;

/**
 * This class holds the data for a specific fly path template.<br>
 * It maps {@link FlyPathEntry} objects to their respective IDs using a {@code TShortObjectHashMap}.
 * @author KID
 */
@XmlRootElement(name = "flypath_template")
@XmlAccessorType(XmlAccessType.FIELD)
public class FlyPathData
{
	@XmlElement(name = "flypath_location")
	private List<FlyPathEntry> list;
	private final TShortObjectHashMap<FlyPathEntry> loctlistData = new TShortObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code loctlistData} map using the list of {@link FlyPathEntry} objects.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (FlyPathEntry loc : list)
		{
			loctlistData.put(loc.getId(), loc);
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
	 * Retrieves a specific fly path template from the data map.<br>
	 * This method uses the provided {@code id} to find the matching entry.
	 * @param id The unique identifier for the fly path template.
	 * @return The {@link FlyPathEntry} associated with the given {@code id}, or {@code null} if not found.
	 */
	public FlyPathEntry getPathTemplate(short id)
	{
		return loctlistData.get(id);
	}
}
