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

import com.aionemu.gameserver.model.templates.item.ItemMinionList;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for the list of minions associated with items.<br>
 * It is used to map item IDs to their corresponding {@link com.aionemu.gameserver.model.templates.item.ItemMinionList} objects during data loading.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "minions_list")
public class ItemMinionListData
{
	@XmlElement(name = "minion_list", required = true)
	protected List<ItemMinionList> minionlist;
	@XmlTransient
	private final TIntObjectHashMap<ItemMinionList> custom = new TIntObjectHashMap<>();
	
	/**
	 * Retrieves a specific minion list based on its unique identifier.<br>
	 * This method looks up the data in the internal custom map.
	 * @param id The unique integer ID of the minion to find.
	 * @return The {@code ItemMinionList} associated with the given {@code id}, or {@code null} if not found.
	 */
	public ItemMinionList getMinionList(int id)
	{
		return custom.get(id);
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code custom} map using the list of {@link ItemMinionList} objects.<br>
	 * The {@code custom} map is updated with each item from the {@code minionlist}.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (ItemMinionList it : minionlist)
		{
			getCustomMap().put(it.getId(), it);
		}
	}
	
	/**
	 * Retrieves the internal map of custom minion data.<br>
	 * This method provides access to the {@code custom} collection.
	 * @return a {@link TIntObjectHashMap} containing {@link ItemMinionList} objects.
	 */
	private TIntObjectHashMap<ItemMinionList> getCustomMap()
	{
		return custom;
	}
	
	/**
	 * Returns the number of elements in the custom map.<br>
	 * This method calls {@code getCustomMap} to retrieve the internal collection.
	 * @return The total count of custom achievement action templates.
	 */
	public int size()
	{
		return custom.size();
	}
}
