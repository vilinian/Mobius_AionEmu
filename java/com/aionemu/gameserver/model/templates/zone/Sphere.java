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
package com.aionemu.gameserver.model.templates.zone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a spherical zone area within the game world.<br>
 * This class defines the spatial boundaries and properties for a {@code Zone} based on a sphere shape.
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sphere")
public class Sphere
{
	@XmlAttribute
	protected Float x;
	@XmlAttribute
	protected Float y;
	@XmlAttribute
	protected Float z;
	@XmlAttribute
	protected Float r;
	
	/**
	 * Creates a new instance of the {@link Sphere} class.<br>
	 * This constructor initializes a sphere with default values.
	 */
	public Sphere()
	{
	}
	
	/**
	 * Creates a new {@link Sphere} instance.<br>
	 * This constructor sets the position and size of the sphere.
	 * @param x The x-coordinate of the center point.
	 * @param y The y-coordinate of the center point.
	 * @param z The z-coordinate of the center point.
	 * @param radius The radius of the sphere.
	 */
	public Sphere(float x, float y, float z, float radius)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		r = radius;
	}
	
	/**
	 * Retrieves the X coordinate of the door.<br>
	 * This value represents the horizontal position in the world.
	 * @return the {@code Float} value of the X coordinate.
	 */
	public Float getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the vertical position of the door.<br>
	 * This value represents the {@code y} coordinate in the 3D space.
	 * @return The {@code Float} value of the {@code y} coordinate.
	 */
	public Float getY()
	{
		return y;
	}
	
	/**
	 * Retrieves the vertical coordinate of the door.<br>
	 * This value represents the {@code z} position in the 3D world.
	 * @return The {@code Float} value of the {@code z} coordinate.
	 */
	public Float getZ()
	{
		return z;
	}
	
	/**
	 * Retrieves the radius of the {@code Cylinder}.<br>
	 * This value represents the distance from the center to the edge.
	 * @return The radius as a {@code Float}.
	 */
	public Float getR()
	{
		return r;
	}
}
