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

/**
 * Represents an item that can be decomposed into other components.<br>
 * This class defines the data structure for items used in decomposition systems.
 * @author Alcapwnd
 */
@XmlType(name = "DecomposableSelectItem")
public class DecomposableSelectItem
{
	@XmlAttribute(name = "item_id")
	private int itemId;
	@XmlElement(name = "items")
	private List<SelectItems> selectItems;
	
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
	 * Retrieves the list of {@link SelectItems}.<br>
	 * This method returns all items associated with this decomposable object.
	 * @return a {@code List} of {@code SelectItems} objects.
	 */
	public List<SelectItems> getItems()
	{
		return selectItems;
	}
	
}
