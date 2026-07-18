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
package com.aionemu.gameserver.model.templates.itemset;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents an individual component of a larger item set.<br>
 * This class defines the properties and data for specific parts within an {@code ItemSet}.
 * @author ATracer
 */
@XmlRootElement(name = "ItemPart")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemPart
{
	@XmlAttribute
	protected int itemid;
	
	/**
	 * Retrieves the unique identifier for this material.<br>
	 * This value corresponds to the {@code itemid} field.
	 * @return The integer ID of the item.
	 */
	public int getItemid()
	{
		return itemid;
	}
}
