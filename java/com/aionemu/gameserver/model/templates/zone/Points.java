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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a collection of points within a specific zone.<br>
 * This class is used to define spatial coordinates or markers for the game world.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Points")
public class Points
{
	@XmlElement(required = true)
	protected List<Point2D> point;
	@XmlAttribute(name = "top")
	protected float top;
	@XmlAttribute(name = "bottom")
	protected float bottom;
	
	/**
	 * Creates a new instance of the {@link Points} class.<br>
	 * This constructor initializes an empty object with default values.
	 */
	public Points()
	{
	}
	
	/**
	 * Creates a new {@link Points} object with specific boundaries.<br>
	 * Sets the vertical limits for the point set.
	 * @param bottom The lower boundary value.
	 * @param top The upper boundary value.
	 */
	public Points(float bottom, float top)
	{
		this.bottom = bottom;
		this.top = top;
	}
	
	/**
	 * Retrieves the list of {@link Point2D} objects.<br>
	 * If the internal list is {@code null}, a new {@code ArrayList} is created.
	 * @return A {@code List} containing {@code Point2D} objects.
	 */
	public List<Point2D> getPoint()
	{
		if (point == null)
		{
			point = new ArrayList<>();
		}
		
		return point;
	}
	
	/**
	 * Retrieves the upper boundary value.<br>
	 * This value represents the {@code top} coordinate of the points.
	 * @return The {@code float} value of the top position.
	 */
	public float getTop()
	{
		return top;
	}
	
	/**
	 * Retrieves the bottom coordinate of the {@code Points} object.<br>
	 * This value represents the lower boundary.
	 * @return The {@code float} value of the bottom coordinate.
	 */
	public float getBottom()
	{
		return bottom;
	}
}
