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
package com.aionemu.gameserver.model.templates.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a collection of items resulting from an action.<br>
 * It is used to group multiple {@link com.aionemu.gameserver.model.templates.item.ItemTemplate} objects together.<br>
 * It serves as a data structure for handling loot or crafting results.
 * @author antness
 */
@XmlType(name = "ResultedItemsCollection")
public class ResultedItemsCollection
{
	@XmlElement(name = "item")
	protected ArrayList<ResultedItem> items;
	@XmlElement(name = "random_item")
	protected ArrayList<RandomItem> randomItems;
	
	/**
	 * Retrieves the collection of {@link ResultedItem} objects.<br>
	 * This method returns an empty list if the internal list is {@code null}.
	 * @return a {@code Collection} of {@code ResultedItem} objects.
	 */
	public Collection<ResultedItem> getItems()
	{
		return items != null ? items : Collections.<ResultedItem> emptyList();
	}
	
	/**
	 * Retrieves the list of {@link RandomItem} objects.<br>
	 * This method returns an empty {@code List} if no items exist.
	 * @return A {@code List} containing all random items.
	 */
	public List<RandomItem> getRandomItems()
	{
		if (randomItems != null)
		{
			return randomItems;
		}
		
		return new ArrayList<>();
	}
}
