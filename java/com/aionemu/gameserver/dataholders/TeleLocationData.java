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

import com.aionemu.gameserver.model.templates.teleport.TelelocationTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for a specific teleport location.<br>
 * It maps {@code TelelocationTemplate} objects to their corresponding IDs using a {@code TIntObjectHashMap}.
 * @author orz
 */
@XmlRootElement(name = "teleport_location")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeleLocationData
{
	@XmlElement(name = "teleloc_template")
	private List<TelelocationTemplate> tlist;
	/**
	 * A map containing all teleport location templates
	 */
	private final TIntObjectHashMap<TelelocationTemplate> loctlistData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code loctlistData} map using the list of {@link TelelocationTemplate} objects.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (TelelocationTemplate loc : tlist)
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
	 * Retrieves a specific teleport location template from the data map.<br>
	 * This method uses the provided unique identifier to find the correct object.
	 * @param id The unique integer ID of the telelocation template.
	 * @return The {@link TelelocationTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public TelelocationTemplate getTelelocationTemplate(int id)
	{
		return loctlistData.get(id);
	}
}
