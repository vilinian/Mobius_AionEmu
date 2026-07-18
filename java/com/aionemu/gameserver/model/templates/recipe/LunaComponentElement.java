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
package com.aionemu.gameserver.model.templates.recipe;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents an individual component element within a {@code Recipe}.<br>
 * This class defines the specific requirements and data for items used in crafting processes.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LunaComponentElement")
public class LunaComponentElement
{
	@XmlAttribute
	protected int itemid;
	
	@XmlAttribute
	protected int quantity;
	
	/**
	 * Retrieves the unique identifier for this component.<br>
	 * This value corresponds to the {@code itemid} field.
	 * @return The {@code Integer} ID of the item.
	 */
	public Integer getItemid()
	{
		return itemid;
	}
	
	/**
	 * Retrieves the total amount of this component.<br>
	 * This value is stored in the {@code quantity} field.
	 * @return The number of items as an {@code Integer}.
	 */
	public Integer getQuantity()
	{
		return quantity;
	}
}
