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
package com.aionemu.gameserver.model.templates.flyring;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.utils3d.Point3D;

/**
 * Represents a specific 3D coordinate for the fly ring path.<br>
 * It stores {@link Point3D} data to define movement points in the game world.
 * @author M@xx
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlyRingPoint")
public class FlyRingPoint
{
	@XmlAttribute(name = "x")
	private float x;
	@XmlAttribute(name = "y")
	private float y;
	@XmlAttribute(name = "z")
	private float z;
	
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
	 * Creates a new instance of {@link FlyRingPoint}.<br>
	 * This constructor initializes the point with default values.
	 */
	public FlyRingPoint()
	{
	}
	
	/**
	 * Creates a new {@link FlyRingPoint} instance from a 3D point.<br>
	 * This constructor copies the coordinates from the provided {@code Point3D}.
	 * @param p The {@code Point3D} object containing the coordinates.
	 */
	public FlyRingPoint(Point3D p)
	{
		x = (float) p.x;
		y = (float) p.y;
		z = (float) p.z;
	}
}
