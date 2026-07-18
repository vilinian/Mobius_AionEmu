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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;

/**
 * Represents the configuration data for a teleportation destination.<br>
 * This template defines where players are moved to when using a specific teleport.<br>
 * It is mapped from the {@code teleloc_template} XML element.
 * @author orz
 */
@XmlRootElement(name = "teleloc_template")
@XmlAccessorType(XmlAccessType.NONE)
public class TelelocationTemplate
{
	/**
	 * Location Id.
	 */
	@XmlAttribute(name = "loc_id", required = true)
	private int locId;
	@XmlAttribute(name = "mapid", required = true)
	private int mapid = 0;
	/**
	 * location name.
	 */
	@XmlAttribute(name = "name", required = true)
	private String name = "";
	@XmlAttribute(name = "name_id", required = true)
	private int nameId;
	@XmlAttribute(name = "posX")
	private float x = 0;
	@XmlAttribute(name = "posY")
	private float y = 0;
	@XmlAttribute(name = "posZ")
	private float z = 0;
	@XmlAttribute(name = "heading")
	private int heading = 0;
	
	/**
	 * Retrieves the location identifier for this event drop.<br>
	 * This value corresponds to the {@code loc_id} attribute.
	 * @return The unique integer ID of the location.
	 */
	public int getLocId()
	{
		return locId;
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * This value is assigned during the creation of the {@link AssembledNpc}.
	 * @return The {@code int} representing the map ID.
	 */
	public int getMapId()
	{
		return mapid;
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
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	public int getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the X coordinate of the bookmark.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return y;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return z;
	}
	
	/**
	 * Retrieves the rotation angle of the teleport point.<br>
	 * This value represents the direction the player faces upon arrival.
	 * @return The {@code int} value of the heading.
	 */
	public int getHeading()
	{
		return heading;
	}
}
