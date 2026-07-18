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
package com.aionemu.gameserver.model.templates.teleport;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a teleport scroll item within the game world.<br>
 * This class holds the data required to define specific teleportation properties for an item.
 */
@XmlType(name = "ScrollItem")
public class ScrollItem
{
	@XmlAttribute(name = "id")
	private int id;
	@XmlAttribute(name = "name")
	private String name;
	@XmlElement(name = "loc")
	private List<ScrollItemLocationList> LocationList;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves a specific location from the {@code LocationList}.<br>
	 * It uses the provided {@code id} to find the correct entry.
	 * @param id The index of the location to retrieve.
	 * @return The {@code ScrollItemLocationList} at the given index, or {@code null} if the list is empty.
	 */
	public ScrollItemLocationList getLocDatabyId(int id)
	{
		if (LocationList != null)
		{
			return LocationList.get(id);
		}
		
		return null;
	}
	
	/**
	 * Retrieves the list of locations for this {@link ScrollItem}.<br>
	 * This method returns all location data associated with the item.
	 * @return a {@code List} of {@code ScrollItemLocationList} objects.
	 */
	public List<ScrollItemLocationList> getLocationList()
	{
		return LocationList;
	}
}
