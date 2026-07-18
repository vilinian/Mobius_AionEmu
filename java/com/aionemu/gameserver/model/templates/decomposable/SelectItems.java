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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.PlayerClass;

/**
 * This class defines the items that can be selected during a decomposition process.<br>
 * It serves as a data model for mapping specific items to their respective results.
 * @author Alcapwnd
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SelectItems")
public class SelectItems
{
	@XmlAttribute(name = "player_class")
	private PlayerClass playerClass = PlayerClass.ALL;
	@XmlElement(name = "item")
	private List<SelectItem> items;
	
	/**
	 * Retrieves the character class of the player.<br>
	 * This method returns the {@code PlayerClass} associated with this ranking result.
	 * @return The {@code PlayerClass} of the player.
	 */
	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}
	
	/**
	 * Retrieves the list of {@code SelectItem} objects.<br>
	 * This method returns all items associated with this collection.
	 * @return a {@code List} containing all {@link SelectItem} entries.
	 */
	public List<SelectItem> getItems()
	{
		return items;
	}
	
	/**
	 * Adds a new {@code SelectItem} to the internal list.<br>
	 * This method initializes the list if it is currently {@code null}.
	 * @param newItem The {@code SelectItem} object to be added.
	 */
	public void addItem(SelectItem newItem)
	{
		if (items == null)
		{
			items = new ArrayList<>();
		}
		
		items.add(newItem);
	}
}
