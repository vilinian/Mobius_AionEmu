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

import com.aionemu.gameserver.model.templates.WarehouseExpandTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for {@link WarehouseExpandTemplate} items.<br>
 * It represents the configuration and properties of warehouse expanders in the game.
 * @author spufy
 */
@XmlRootElement(name = "warehouse_expander")
@XmlAccessorType(XmlAccessType.FIELD)
public class WarehouseExpandData
{
	@XmlElement(name = "warehouse_npc")
	private List<WarehouseExpandTemplate> clist;
	private final TIntObjectHashMap<WarehouseExpandTemplate> npctlistData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code npctlistData} map using the list of {@link WarehouseExpandTemplate} objects.<br>
	 * The {@code npctlistData} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (WarehouseExpandTemplate npc : clist)
		{
			npctlistData.put(npc.getNpcId(), npc);
		}
	}
	
	/**
	 * Returns the total number of teleporter templates stored in this container.<br>
	 * This method calls {@code size} to retrieve the count.
	 * @return The number of items currently held in the data map.
	 */
	public int size()
	{
		return npctlistData.size();
	}
	
	/**
	 * Retrieves a specific warehouse expander template.<br>
	 * This method looks up the data using the provided unique identifier.
	 * @param id The unique ID of the warehouse expander to find.
	 * @return The {@code WarehouseExpandTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public WarehouseExpandTemplate getWarehouseExpandListTemplate(int id)
	{
		return npctlistData.get(id);
	}
}
