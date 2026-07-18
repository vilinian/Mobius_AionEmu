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
package com.aionemu.gameserver.model.templates.itemgroups;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a collection of items used for feeding purposes.<br>
 * This class serves as a template for defining groups of materials that can be consumed to produce other rewards.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeedItemGroup")
public abstract class FeedItemGroup
{
	@XmlAttribute(name = "group", required = true)
	protected ItemGroupIndex index = ItemGroupIndex.NONE;
	@XmlElement(name = "item")
	private List<ItemRaceEntry> items;
	
	/**
	 * Retrieves the unique identifier for this item group.<br>
	 * This value is used to identify the group within the system.
	 * @return the {@code ItemGroupIndex} of this group.
	 */
	public ItemGroupIndex getIndex()
	{
		return index;
	}
	
	/**
	 * Retrieves the list of {@link ItemRaceEntry} objects.<br>
	 * This method returns a live reference to the internal list.<br>
	 * If the list is null, it initializes a new {@code ArrayList}.
	 * @return A {@code List} containing all items in this group.
	 */
	public List<ItemRaceEntry> getItems()
	{
		if (items == null)
		{
			items = new ArrayList<>();
		}
		
		return items;
	}
}
