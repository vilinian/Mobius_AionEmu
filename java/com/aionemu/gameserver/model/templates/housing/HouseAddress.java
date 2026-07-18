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
package com.aionemu.gameserver.model.templates.housing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;

/**
 * Represents the geographical location data for a player's house.<br>
 * This class stores coordinates and other spatial information used by the {@code House} system.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "address")
public class HouseAddress
{
	@XmlAttribute(name = "exit_z")
	protected Float exitZ;
	@XmlAttribute(name = "exit_y")
	protected Float exitY;
	@XmlAttribute(name = "exit_x")
	protected Float exitX;
	@XmlAttribute(name = "exit_map")
	protected Integer exitMap;
	@XmlAttribute(required = true)
	protected float z;
	@XmlAttribute(required = true)
	protected float y;
	@XmlAttribute(required = true)
	protected float x;
	@XmlAttribute(name = "town", required = true)
	private int townId;
	@XmlAttribute(required = true)
	protected int map;
	@XmlAttribute(required = true)
	protected int id;
	
	/**
	 * Retrieves the Z coordinate of the exit point.<br>
	 * This value is used to determine the vertical position when leaving a house.
	 * @return The {@code Float} value representing the exit Z coordinate.
	 */
	public Float getExitZ()
	{
		return exitZ;
	}
	
	/**
	 * Retrieves the Y coordinate of the exit point.<br>
	 * This value is used to determine where a player leaves the house.
	 * @return the {@code Float} value of the exit Y coordinate.
	 */
	public Float getExitY()
	{
		return exitY;
	}
	
	/**
	 * Retrieves the X coordinate of the exit point.<br>
	 * This value is used to determine where a player leaves the house.
	 * @return the {@code Float} value of the exit X coordinate.
	 */
	public Float getExitX()
	{
		return exitX;
	}
	
	/**
	 * Retrieves the unique identifier for the map where the house exits.<br>
	 * This value is used to determine the destination coordinates.
	 * @return the {@code Integer} ID of the exit map.
	 */
	public Integer getExitMapId()
	{
		return exitMap;
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
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return y;
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
	 * Retrieves the unique identifier for the map.<br>
	 * This value is assigned during the creation of the {@link AssembledNpc}.
	 * @return The {@code int} representing the map ID.
	 */
	public int getMapId()
	{
		return map;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the unique identifier for the town associated with this object.<br>
	 * This value is used to determine which town area the entity belongs to.
	 * @return the {@code int} ID of the town.
	 */
	public int getTownId()
	{
		return townId;
	}
}
