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

import com.aionemu.gameserver.model.templates.item.ItemCustomSetTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for custom item sets within the game server.<br>
 * It serves as a data container that maps specific items to their respective {@link ItemCustomSetTemplate} configurations.
 */
@XmlRootElement(name = "item_custom_sets")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemCustomSetData
{
	@XmlElement(name = "item_custom_set")
	private List<ItemCustomSetTemplate> itemCustomSetTemplateList;
	@XmlTransient
	private final TIntObjectHashMap<ItemCustomSetTemplate> itemCustomSetTemplateTIntObjectHashMap = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It populates the {@code itemCustomSetTemplateTIntObjectHashMap} using data from {@code itemCustomSetTemplateList}.<br>
	 * Finally, it clears and nullifies the original list to save memory.
	 * @param unmarshaller The {@link Unmarshaller} used to read the XML.
	 * @param o The object that was just unmarshalled.
	 */
	void afterUnmarshal(Unmarshaller unmarshaller, Object o)
	{
		for (ItemCustomSetTemplate itemCustomSetTemplate : itemCustomSetTemplateList)
		{
			itemCustomSetTemplateTIntObjectHashMap.put(itemCustomSetTemplate.getItem_id(), itemCustomSetTemplate);
		}
		
		itemCustomSetTemplateList.clear();
		itemCustomSetTemplateList = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return itemCustomSetTemplateTIntObjectHashMap.size();
	}
	
	/**
	 * Retrieves a specific custom set template based on an item ID.<br>
	 * This method looks up the data in the internal map.
	 * @param itemId The unique identifier of the item to look up.
	 * @return The {@link ItemCustomSetTemplate} associated with the ID, or {@code null} if not found.
	 */
	public ItemCustomSetTemplate getItemCustomSetTemplate(int itemId)
	{
		return itemCustomSetTemplateTIntObjectHashMap.get(itemId);
	}
}
