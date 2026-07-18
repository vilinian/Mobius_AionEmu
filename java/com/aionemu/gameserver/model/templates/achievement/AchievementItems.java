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
package com.aionemu.gameserver.model.templates.achievement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the items associated with a specific achievement.<br>
 * It serves as a data model for mapping rewards to {@code Achievement}.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "AchievementItems")
public class AchievementItems
{
	@XmlAttribute(name = "item_id")
	protected Integer itemId;
	@XmlAttribute(name = "count")
	protected Integer count;
	
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
	 * Retrieves the total number of items associated with this reward.<br>
	 * This method returns the value stored in the {@code count} field.
	 * @return The quantity as an {@code Integer}.
	 */
	public Integer getCount()
	{
		return count;
	}
}
