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
 * Represents a two-dimensional coordinate in the game world.<br>
 * This class is used to define positions for various zone elements.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Point2D")
public class Point2D
{
	@XmlAttribute(name = "y")
	protected float y;
	@XmlAttribute(name = "x")
	protected float x;
	
	/**
	 * Creates a new {@link Point2D} instance.<br>
	 * Sets the coordinates using the provided values.
	 * @param x The horizontal coordinate.
	 * @param y The vertical coordinate.
	 */
	public Point2D(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new instance of {@link Point2D}.<br>
	 * The coordinates are initialized to their default values.
	 */
	public Point2D()
	{
		super();
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
}
