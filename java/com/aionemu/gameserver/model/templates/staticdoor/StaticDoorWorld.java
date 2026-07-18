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
package com.aionemu.gameserver.model.templates.staticdoor;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.StaticDoor;

/**
 * Represents the world-related data for a {@link StaticDoor}.<br>
 * It stores configuration details regarding the door's placement and properties in the game world.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "World")
public class StaticDoorWorld
{
	@XmlAttribute(name = "world")
	protected int world;
	@XmlElement(name = "staticdoor")
	protected List<StaticDoorTemplate> staticDoorTemplate;
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is stored in the {@code world} field.
	 * @return the current world ID as an {@code int}.
	 */
	public int getWorld()
	{
		return world;
	}
	
	/**
	 * Retrieves all door templates for the current world.<br>
	 * This method returns a {@code List} of {@link StaticDoorTemplate} objects.
	 * @return A list containing all {@code StaticDoorTemplate} entries.
	 */
	public List<StaticDoorTemplate> getStaticDoors()
	{
		return staticDoorTemplate;
	}
}
