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
package com.aionemu.gameserver.model.templates.transform_book;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the collection requirements for a transformation book.<br>
 * This class defines what items or conditions must be met to unlock a specific transformation.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollectionRequired")
public class CollectionRequired
{
	@XmlAttribute(name = "ids")
	protected List<Integer> ids;
	
	/**
	 * Retrieves the list of unique identifiers for this action.<br>
	 * If no IDs exist, it returns an empty {@code List}.
	 * @return a {@code List<Integer>} containing the IDs.
	 */
	public List<Integer> getIds()
	{
		return ids;
	}
}
