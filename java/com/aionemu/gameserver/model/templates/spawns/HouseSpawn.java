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
package com.aionemu.gameserver.model.templates.spawns;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a spawn point for houses within the game world.<br>
 * This class defines the configuration and location data for house entities.<br>
 * It is used by the {@link com.aionemu.gameserver.model.templates.spawns.Spawn} system to manage house placement.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HouseSpawn")
public class HouseSpawn
{
	@XmlAttribute(name = "x", required = true)
	protected float x;
	@XmlAttribute(name = "y", required = true)
	protected float y;
	@XmlAttribute(name = "z", required = true)
	protected float z;
	@XmlAttribute(name = "h")
	protected Byte h;
	@XmlAttribute(name = "static_id")
	private int staticId;
	@XmlAttribute(name = "type", required = true)
	protected SpawnType type;
	
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
		if (h == null)
		{
			return ((byte) 0);
		}
		
		return h;
	}
	
	/**
	 * Sets the {@code h} attribute for this spawn.<br>
	 * This value is used to define specific house properties.
	 * @param value The new {@code Byte} value to assign.
	 */
	public void setH(Byte value)
	{
		h = value;
	}
	
	/**
	 * Retrieves the spawn type of this house.<br>
	 * This method returns the {@code SpawnType} associated with the current instance.
	 * @return The {@code SpawnType} of the house.
	 */
	public SpawnType getType()
	{
		return type;
	}
	
	/**
	 * Sets the spawn type for this house.<br>
	 * This updates the {@code type} field with the provided {@link SpawnType}.
	 * @param value The new {@code SpawnType} to assign.
	 */
	public void setType(SpawnType value)
	{
		type = value;
	}
	
	/**
	 * Retrieves the unique static identifier for this spawn.<br>
	 * This value is used to identify specific objects in the game world.
	 * @return The {@code int} representing the static ID.
	 */
	public int getStaticId()
	{
		return staticId;
	}
	
	/**
	 * Sets the unique identifier for this spawn.<br>
	 * This value is used to identify a specific object in the game world.
	 * @param staticId The {@code int} value to assign as the new ID.
	 */
	public void setStaticId(int staticId)
	{
		this.staticId = staticId;
	}
}
