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
package com.aionemu.gameserver.model.templates.decomposable;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.item.ExtractedItemsCollection;

/**
 * This class represents the data structure for items that can be decomposed into other materials.<br>
 * It stores information about what specific items are produced when a {@code DecomposableItem} is processed.
 * @author Alcapwnd
 */
@XmlType(name = "DecomposableItem")
public class DecomposableItemInfo
{
	@XmlAttribute(name = "item_id")
	private int itemId;
	@XmlElement(name = "items")
	private List<ExtractedItemsCollection> itemsCollections;
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Retrieves the list of item collections for this decomposable item.<br>
	 * This method returns all {@link ExtractedItemsCollection} objects associated with the template.
	 * @return A {@code List} of {@link ExtractedItemsCollection} objects.
	 */
	public List<ExtractedItemsCollection> getItemsCollections()
	{
		return itemsCollections;
	}
}
