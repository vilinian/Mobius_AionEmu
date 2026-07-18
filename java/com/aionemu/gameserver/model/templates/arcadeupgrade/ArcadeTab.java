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
package com.aionemu.gameserver.model.templates.arcadeupgrade;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a collection of arcade upgrade items grouped within a specific tab.<br>
 * This class serves as a data model for organizing upgrades in the user interface.
 * @author Raziel
 */
@XmlType(name = "ArcadeTab")
public class ArcadeTab
{
	@XmlAttribute(name = "id")
	private int id;
	@XmlElement(name = "item")
	private List<ArcadeTabItem> arcadeTabItem;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the list of items associated with this arcade tab.<br>
	 * This method returns all {@link ArcadeTabItem} objects stored in the current tab.
	 * @return a {@code List} of {@link ArcadeTabItem} objects.
	 */
	public List<ArcadeTabItem> getArcadeTabItems()
	{
		return arcadeTabItem;
	}
}
