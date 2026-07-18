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

import com.aionemu.gameserver.model.templates.item.DecomposableItemInfo;
import com.aionemu.gameserver.model.templates.item.ExtractedItemsCollection;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for items that can be decomposed into other materials.<br>
 * It maps item IDs to their corresponding {@link DecomposableItemInfo} objects.<br>
 * Use this class to manage and retrieve decomposition recipes within the game server.
 * @author antness
 */
@XmlRootElement(name = "decomposable_items")
@XmlAccessorType(XmlAccessType.FIELD)
public class DecomposableItemsData
{
	@XmlElement(name = "decomposable")
	private List<DecomposableItemInfo> decomposableItemsTemplates;
	private final TIntObjectHashMap<List<ExtractedItemsCollection>> decomposableItemsInfo = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code decomposableItemsInfo} map using the list of {@link DecomposableItemInfo} templates.<br>
	 * The {@code decomposableItemsInfo} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		decomposableItemsInfo.clear();
		for (DecomposableItemInfo template : decomposableItemsTemplates)
		{
			decomposableItemsInfo.put(template.getItemId(), template.getItemsCollections());
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return decomposableItemsInfo.size();
	}
	
	/**
	 * Retrieves the collection of extracted items for a specific item ID.<br>
	 * This method looks up data in the internal map using the provided {@code itemId}.
	 * @param itemId The unique identifier of the item to look up.
	 * @return A {@code List} of {@link ExtractedItemsCollection} objects, or {@code null} if not found.
	 */
	public List<ExtractedItemsCollection> getInfoByItemId(int itemId)
	{
		return decomposableItemsInfo.get(itemId);
	}
}
