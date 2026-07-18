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

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.teleport.ScrollItem;
import com.aionemu.gameserver.model.templates.teleport.ScrollItemLocationList;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds data for items that provide multiple return points.<br>
 * It maps specific item IDs to their corresponding {@link ScrollItemLocationList} configurations.
 */
@XmlRootElement(name = "item_multi_returns")
@XmlAccessorType(XmlAccessType.FIELD)
public class MultiReturnItemData
{
	@XmlElement(name = "item")
	private List<ScrollItem> ItemList;
	private final TIntObjectHashMap<List<ScrollItemLocationList>> ItemLocationList = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It populates the internal {@code ItemLocationList} map.<br>
	 * It clears any existing data before processing the new list.
	 * @param Unmarshaller the {@link Unmarshaller} used to read the data.
	 * @param Object the object being unmarshalled.
	 */
	void afterUnmarshal(Unmarshaller Unmarshaller, Object Object)
	{
		ItemLocationList.clear();
		final Iterator<ScrollItem> Iterator = ItemList.iterator();
		while (Iterator.hasNext())
		{
			final ScrollItem ScrollItem = Iterator.next();
			ItemLocationList.put(ScrollItem.getId(), ScrollItem.getLocationList());
		}
	}
	
	/**
	 * Returns the total number of items in the location list.<br>
	 * This method calls {@code size} to get the count.
	 * @return The size of the internal item location map.
	 */
	public int size()
	{
		return ItemLocationList.size();
	}
	
	/**
	 * Finds a specific {@link ScrollItem} based on its unique identifier.<br>
	 * This method searches through the internal list of items.<br>
	 * It returns the matching item if it exists in the data.
	 * @param id The unique integer ID of the scroll to find.
	 * @return The matching {@code ScrollItem} object, or {@code null} if no match is found.
	 */
	public ScrollItem getScrollItembyId(int id)
	{
		final Iterator<ScrollItem> Iterator = ItemList.iterator();
		while (Iterator.hasNext())
		{
			final ScrollItem ScrollItem = Iterator.next();
			if (ScrollItem.getId() == id)
			{
				return ScrollItem;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the list of all scroll items.<br>
	 * This method returns the {@code ItemList} stored in this object.
	 * @return a {@code List} containing all {@link ScrollItem} objects.
	 */
	public List<ScrollItem> getScrollItems()
	{
		return ItemList;
	}
}
