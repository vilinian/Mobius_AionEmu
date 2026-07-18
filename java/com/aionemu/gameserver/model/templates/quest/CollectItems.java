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
 * Represents a quest objective that requires the player to gather specific items.<br>
 * It defines the required quantity and types of items needed to complete the task.
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollectItems", propOrder =
{
	"collectItem"
})
public class CollectItems
{
	@XmlElement(name = "collect_item")
	protected List<CollectItem> collectItem;
	
	/**
	 * Retrieves the list of items to be collected.<br>
	 * This method returns a live reference to the {@code collectItem} list.<br>
	 * If the list is null, it initializes a new {@code ArrayList}.
	 * @return A {@code List} of {@link CollectItem} objects.
	 */
	public List<CollectItem> getCollectItem()
	{
		if (collectItem == null)
		{
			collectItem = new ArrayList<>();
		}
		
		return collectItem;
	}
}
