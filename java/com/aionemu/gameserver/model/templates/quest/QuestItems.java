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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the collection of items associated with a specific quest.<br>
 * It maps data from the {@code Quest} template to define required or rewarded objects.
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestItems")
public class QuestItems
{
	@XmlAttribute(name = "item_id")
	protected Integer itemId;
	@XmlAttribute
	protected long count = 1L;
	
	/**
	 * Creates a new instance of {@link QuestItems}.<br>
	 * This constructor is used by the XML unmarshaller.<br>
	 * It initializes the {@code count} to {@code 1L}.
	 */
	public QuestItems()
	{
		count = 1L;
	}
	
	/**
	 * Creates a new {@link QuestItems} object.<br>
	 * This constructor sets the item identity and quantity.
	 * @param itemId The unique identifier for the quest item.
	 * @param count The total number of items required.
	 */
	public QuestItems(int itemId, long count)
	{
		this.itemId = itemId;
		this.count = count;
	}
	
	/**
	 * Retrieves the unique identifier for the item.<br>
	 * This value is used to identify which item is being rewarded.
	 * @return The {@code Integer} ID of the item, or {@code null} if not set.
	 */
	public Integer getItemId()
	{
		return itemId;
	}
	
	/**
	 * Retrieves the current quantity of the item.<br>
	 * This value is updated by the {@code calculateCount} method.
	 * @return The total number of items as a {@code long}.
	 */
	public long getCount()
	{
		return count;
	}
}
