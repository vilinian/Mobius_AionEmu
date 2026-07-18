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
package com.aionemu.gameserver.model.templates.flypath;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a single location point within a {@code FlyPath}.<br>
 * This class stores the coordinates and data required to define a specific waypoint for flying paths.
 * @author KID
 */
@XmlRootElement(name = "flypath_location")
@XmlAccessorType(XmlAccessType.NONE)
public class FlyPathEntry
{
	@XmlAttribute(name = "id", required = true)
	private short id;
	@XmlAttribute(name = "sx", required = true)
	private float startX;
	@XmlAttribute(name = "sy", required = true)
	private float startY;
	@XmlAttribute(name = "sz", required = true)
	private float startZ;
	@XmlAttribute(name = "sworld", required = true)
	private int sworld;
	@XmlAttribute(name = "ex", required = true)
	private float endX;
	@XmlAttribute(name = "ey", required = true)
	private float endY;
	@XmlAttribute(name = "ez", required = true)
	private float endZ;
	@XmlAttribute(name = "eworld", required = true)
	private int eworld;
	@XmlAttribute(name = "time", required = true)
	private float time;
	
	/**
	 * Retrieves the unique identifier for this {@link FlyPathEntry}.<br>
	 * This value is used to distinguish between different paths.
	 * @return The {@code short} ID of the entry.
	 */
	public short getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the X coordinate of the starting point.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the start X coordinate.
	 */
	public float getStartX()
	{
		return startX;
	}
	
	/**
	 * Retrieves the starting Y coordinate of the fly path.<br>
	 * This value represents the vertical position at the beginning point.
	 * @return the {@code float} value of the starting Y coordinate.
	 */
	public float getStartY()
	{
		return startY;
	}
	
	/**
	 * Retrieves the starting Z coordinate of the fly path.<br>
	 * This value represents the height at the beginning point.
	 * @return the {@code float} value of the starting Z coordinate.
	 */
	public float getStartZ()
	{
		return startZ;
	}
	
	/**
	 * Retrieves the X coordinate of the destination point.<br>
	 * This value represents the final horizontal position in the path.
	 * @return The {@code float} value of the end X coordinate.
	 */
	public float getEndX()
	{
		return endX;
	}
	
	/**
	 * Retrieves the Y coordinate of the destination point.<br>
	 * This value represents the height at the end of the fly path.
	 * @return The {@code float} value of the end Y coordinate.
	 */
	public float getEndY()
	{
		return endY;
	}
	
	/**
	 * Retrieves the Z coordinate of the destination point.<br>
	 * This value represents the height at the end of the fly path.
	 * @return The {@code float} value of the end Z coordinate.
	 */
	public float getEndZ()
	{
		return endZ;
	}
	
	/**
	 * Retrieves the unique identifier for the starting world.<br>
	 * This value corresponds to the {@code sworld} attribute.
	 * @return The ID of the world where the fly path begins.
	 */
	public int getStartWorldId()
	{
		return sworld;
	}
	
	/**
	 * Retrieves the unique identifier for the destination world.<br>
	 * This value corresponds to the {@code eworld} attribute.
	 * @return The ID of the end world.
	 */
	public int getEndWorldId()
	{
		return eworld;
	}
	
	/**
	 * Retrieves the duration of the fly path in milliseconds.<br>
	 * This method converts the {@code time} value from seconds to milliseconds.
	 * @return The total time as an {@code int}.
	 */
	public int getTimeInMs()
	{
		return (int) (time * 1000);
	}
}
