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
package com.aionemu.gameserver.model.templates.windstreams;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.flypath.FlyPathType;

/**
 * Represents a two-dimensional coordinate within the game world.<br>
 * This class is used to define specific positions for {@code Windstream} data.<br>
 * It provides a simple way to store and manage spatial information in 2D space.
 * @author LokiReborn
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Location2D")
public class Location2D
{
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "state")
	protected int state;
	@XmlAttribute(name = "fly_path")
	protected FlyPathType flyPath;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the current state of the creature.<br>
	 * This value represents the internal status of the object.
	 * @return The current state as an {@code int}.
	 */
	public int getState()
	{
		return state;
	}
	
	/**
	 * Updates the current state of the creature.<br>
	 * This method sets the {@code state} field to a new value.
	 * @param state The new integer value for the creature's state.
	 */
	public void setState(int state)
	{
		this.state = state;
	}
	
	/**
	 * Retrieves the current {@code FlyPathType} for this location.<br>
	 * This method returns the path type associated with the {@code Location2D}.
	 * @return the {@code FlyPathType} of the location.
	 */
	public FlyPathType getFlyPathType()
	{
		return flyPath;
	}
}
