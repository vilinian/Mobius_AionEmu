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
package com.aionemu.gameserver.model.templates.quest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a collection of items required for a quest.<br>
 * It maps to the {@code InventoryItems} XML element in the quest templates.<br>
 * Use this model to define which items a player must possess or provide.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventoryItems", propOrder =
{
	"inventoryItem"
})
public class InventoryItems
{
	@XmlElement(name = "inventory_item")
	protected List<InventoryItem> inventoryItem;
	
	/**
	 * Retrieves the list of {@link InventoryItem} objects.<br>
	 * This method returns a live reference to the internal list.<br>
	 * If the list is null, it initializes a new {@code ArrayList}.
	 * @return A {@code List} containing all inventory items.
	 */
	public List<InventoryItem> getInventoryItem()
	{
		if (inventoryItem == null)
		{
			inventoryItem = new ArrayList<>();
		}
		
		return inventoryItem;
	}
}
