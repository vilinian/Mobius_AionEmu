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
 * Represents a cylindrical zone template within the game world.<br>
 * This class defines the geometric properties and data for cylinder-shaped areas.
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cylinder")
public class Cylinder
{
	@XmlAttribute
	protected Float top;
	@XmlAttribute
	protected Float bottom;
	@XmlAttribute
	protected Float x;
	@XmlAttribute
	protected Float y;
	@XmlAttribute
	protected Float r;
	
	/**
	 * Creates a new instance of the {@link Cylinder} class.<br>
	 * This constructor initializes the object with default values.
	 */
	public Cylinder()
	{
	}
	
	/**
	 * Creates a new {@link Cylinder} object with specific dimensions.<br>
	 * This constructor sets the position, size, and height of the cylinder.
	 * @param x The horizontal position on the map.
	 * @param y The vertical position on the map.
	 * @param radius The distance from the center to the edge.
	 * @param top The upper boundary value.
	 * @param bottom The lower boundary value.
	 */
	public Cylinder(float x, float y, float radius, float top, float bottom)
	{
		this.x = x;
		this.y = y;
		r = radius;
		this.top = top;
		this.bottom = bottom;
	}
	
	/**
	 * Retrieves the top coordinate of the {@code Cylinder}.<br>
	 * This value represents the upper boundary of the shape.
	 * @return The {@code Float} value of the top position.
	 */
	public Float getTop()
	{
		return top;
	}
	
	/**
	 * Retrieves the bottom coordinate of the {@code Cylinder}.<br>
	 * This value represents the lower vertical limit.
	 * @return The {@code Float} value of the bottom position.
	 */
	public Float getBottom()
	{
		return bottom;
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
	 * Retrieves the radius of the {@code Cylinder}.<br>
	 * This value represents the distance from the center to the edge.
	 * @return The radius as a {@code Float}.
	 */
	public Float getR()
	{
		return r;
	}
}
