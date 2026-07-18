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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents a collection of {@code ItemTransform} objects.<br>
 * It is used to manage and store multiple item transformations within the game data.<br>
 * It serves as an XML-compatible container for processing lists of transformed items.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ItemTransformList")
public class ItemTransformList
{
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "transform_id")
	protected List<Integer> transformId;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the list of transformation IDs for this item.<br>
	 * If the list is {@code null}, it returns a new empty {@code ArrayList}.
	 * @return A {@code List<Integer>} containing all transform IDs.
	 */
	public List<Integer> getTransformId()
	{
		if (transformId == null)
		{
			transformId = new ArrayList<>();
		}
		
		return transformId;
	}
}
