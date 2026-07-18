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

import com.aionemu.gameserver.model.templates.BindPointTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for {@link BindPointTemplate} objects.<br>
 * It serves as a container for managing bind point information within the game server.
 * @author avol
 */
@XmlRootElement(name = "bind_points")
@XmlAccessorType(XmlAccessType.FIELD)
public class BindPointData
{
	@XmlElement(name = "bind_point")
	private List<BindPointTemplate> bplist;
	/**
	 * A map containing all bind point location templates
	 */
	private final TIntObjectHashMap<BindPointTemplate> bindplistData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code bindplistData} map using the list of {@link BindPointTemplate} objects.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (BindPointTemplate bind : bplist)
		{
			bindplistData.put(bind.getNpcId(), bind);
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return bindplistData.size();
	}
	
	/**
	 * Retrieves the {@link BindPointTemplate} associated with a specific NPC.<br>
	 * This method looks up the template using the provided {@code npcId}.
	 * @param npcId The unique identifier of the NPC.
	 * @return The {@code BindPointTemplate} for the given ID, or {@code null} if not found.
	 */
	public BindPointTemplate getBindPointTemplate(int npcId)
	{
		return bindplistData.get(npcId);
	}
}
