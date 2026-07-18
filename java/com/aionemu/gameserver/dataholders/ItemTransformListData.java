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

import com.aionemu.gameserver.model.templates.item.ItemTransformList;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for item transformations.<br>
 * It serves as a container to map items to their respective {@link ItemTransformList} objects.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "transforms_list")
public class ItemTransformListData
{
	@XmlElement(name = "transform_list", required = true)
	protected List<ItemTransformList> transformlist;
	@XmlTransient
	private final TIntObjectHashMap<ItemTransformList> custom = new TIntObjectHashMap<>();
	
	/**
	 * Retrieves a specific transformation list based on its unique identifier.<br>
	 * This method looks up the value in the internal custom map.
	 * @param id The unique integer ID of the item transform to retrieve.
	 * @return The {@link ItemTransformList} associated with the given {@code id}, or {@code null} if not found.
	 */
	public ItemTransformList getTransformList(int id)
	{
		return custom.get(id);
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code custom} map using the items in the {@code transformlist}.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (ItemTransformList it : transformlist)
		{
			getCustomMap().put(it.getId(), it);
		}
	}
	
	/**
	 * Retrieves the internal map of custom transformations.<br>
	 * This method provides access to the {@code custom} field.
	 * @return a {@link TIntObjectHashMap} containing {@link ItemTransformList} objects.
	 */
	private TIntObjectHashMap<ItemTransformList> getCustomMap()
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
