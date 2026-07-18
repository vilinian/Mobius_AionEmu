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
 * Represents an item required for a quest that must be present in the player's inventory.<br>
 * This class defines the data structure for tracking specific items needed to complete quest objectives.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventoryItem")
public class InventoryItem
{
	@XmlAttribute(name = "item_id")
	protected Integer itemId;
	
	/**
	 * Retrieves the unique identifier for the item.<br>
	 * This value is used to identify which item is being rewarded.
	 * @return The {@code Integer} ID of the item, or {@code null} if not set.
	 */
	public Integer getItemId()
	{
		return itemId;
	}
}
