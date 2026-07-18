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

import com.aionemu.gameserver.model.templates.itemset.ItemPart;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link ItemSetTemplate} objects.<br>
 * It manages the collection of item sets loaded from the game configuration files.<br>
 * Use this class to access and retrieve specific item set data throughout the server.
 * @author ATracer
 */
@XmlRootElement(name = "item_sets")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemSetData
{
	@XmlElement(name = "itemset")
	protected List<ItemSetTemplate> itemsetList;
	private TIntObjectHashMap<ItemSetTemplate> sets;
	
	// This map provides faster searching of the item template set by mapping each item ID to its associated template.
	private TIntObjectHashMap<ItemSetTemplate> setItems;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code sets} and {@code setItems} maps using the list of {@link ItemSetTemplate} objects.<br>
	 * The {@code itemsetList} is set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		sets = new TIntObjectHashMap<>();
		setItems = new TIntObjectHashMap<>();
		
		for (ItemSetTemplate set : itemsetList)
		{
			sets.put(set.getId(), set);
			
			// Add reference to the ItemSetTemplate from
			for (ItemPart part : set.getItempart())
			{
				setItems.put(part.getItemid(), set);
			}
		}
		
		itemsetList = null;
	}
	
	/**
	 * Retrieves a specific {@link ItemSetTemplate} using its unique ID.<br>
	 * This method looks up the template in the internal data map.
	 * @param itemSetId The unique identifier for the item set.
	 * @return The {@code ItemSetTemplate} associated with the provided ID, or {@code null} if not found.
	 */
	public ItemSetTemplate getItemSetTemplate(int itemSetId)
	{
		return sets.get(itemSetId);
	}
	
	/**
	 * Retrieves the {@link ItemSetTemplate} associated with a specific item ID.<br>
	 * This method uses the internal map to find the template quickly.
	 * @param itemId The unique identifier of the item to look up.
	 * @return The {@code ItemSetTemplate} linked to the provided {@code itemId}, or {@code null} if no match is found.
	 */
	public ItemSetTemplate getItemSetTemplateByItemId(int itemId)
	{
		return setItems.get(itemId);
	}
	
	/**
	 * Returns the total number of item sets.<br>
	 * This method returns the count of elements stored in the internal map.
	 * @return The size of the collection.
	 */
	public int size()
	{
		return sets.size();
	}
}
