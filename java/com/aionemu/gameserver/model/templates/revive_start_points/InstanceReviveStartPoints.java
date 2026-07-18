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
package com.aionemu.gameserver.model.templates.revive_start_points;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the configuration for revive start points within an instance.<br>
 * It maps data from the XML templates to the game server's internal model.
 * @author Falke_34
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceReviveStartPoints")
public class InstanceReviveStartPoints
{
	@XmlAttribute(name = "world_id")
	protected int worldId;
	
	@XmlAttribute(name = "name")
	protected String name;
	
	@XmlAttribute(name = "x")
	protected float x;
	
	@XmlAttribute(name = "y")
	protected float y;
	
	@XmlAttribute(name = "z")
	protected float z;
	
	@XmlAttribute(name = "h")
	protected byte h;
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This method returns the {@code worldId} associated with these revive start points.
	 * @return The integer ID of the world.
	 */
	public int getReviveWorld()
	{
		return worldId;
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
	 * Sets the x-coordinate for this instance exit.<br>
	 * This updates the {@code x} field with a new value.
	 * @param value The new {@code float} coordinate to set.
	 */
	public void setX(float value)
	{
		x = value;
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
	 * Sets the Y coordinate of the instance exit.<br>
	 * This updates the vertical position in the world.
	 * @param value The new {@code float} value for the Y coordinate.
	 */
	public void setY(float value)
	{
		y = value;
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
	 * Sets the vertical coordinate of the exit point.<br>
	 * This updates the {@code z} field with a new value.
	 * @param value The new {@code float} value for the height.
	 */
	public void setZ(float value)
	{
		z = value;
	}
	
	/**
	 * Retrieves the horizontal rotation value.<br>
	 * This value represents the orientation of the {@code SummonGroup}.
	 * @return The current {@code byte} value for {@code h}.
	 */
	public byte getH()
	{
		return h;
	}
	
	/**
	 * Sets the {@code h} coordinate for this instance exit.<br>
	 * This updates the internal field with the provided {@code byte} value.
	 * @param value The new value to assign to the {@code h} property.
	 */
	public void setH(byte value)
	{
		h = value;
	}
}
