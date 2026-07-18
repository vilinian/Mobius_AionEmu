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
package com.aionemu.gameserver.model.templates;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the radius of a bound area within the game world.<br>
 * This class defines the spatial limits for specific bounded zones.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundRadius")
public class BoundRadius
{
	@XmlAttribute
	private float front;
	@XmlAttribute
	private float side;
	@XmlAttribute
	private float upper;
	private float collision;
	public static final BoundRadius DEFAULT = new BoundRadius(0f, 0f, 0f);
	
	/**
	 * Creates a new instance of {@code BoundRadius}.<br>
	 * This constructor initializes the object with default values.
	 */
	public BoundRadius()
	{
	}
	
	/**
	 * Creates a new {@link BoundRadius} instance.<br>
	 * This constructor sets the dimensions for the front, side, and upper bounds.<br>
	 * It also automatically calculates the collision value based on the provided inputs.
	 * @param front The distance for the front bound.
	 * @param side The distance for the side bound.
	 * @param upper The distance for the upper bound.
	 */
	public BoundRadius(float front, float side, float upper)
	{
		this.front = front;
		this.side = side;
		this.upper = upper;
		calculateCollision(front, side);
	}
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It calculates the {@code collision} value based on the {@code front} and {@code side} attributes.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current instance.
	 */
	protected void afterUnmarshal(Unmarshaller u, Object parent)
	{
		calculateCollision(front, side);
	}
	
	/**
	 * Calculates the collision value based on dimensions.<br>
	 * This method updates the {@code collision} field.
	 * @param front The front dimension of the bound radius.
	 * @param side The side dimension of the bound radius.
	 */
	protected void calculateCollision(float front, float side)
	{
		collision = (float) Math.sqrt(side * front);
	}
	
	/**
	 * Retrieves the front dimension of the bound radius.<br>
	 * This value represents the depth of the object.
	 * @return The {@code float} value of the front dimension.
	 */
	public float getFront()
	{
		return front;
	}
	
	/**
	 * Retrieves the side dimension of the bound radius.<br>
	 * This value represents the width of the object.
	 * @return The {@code float} value of the side.
	 */
	public float getSide()
	{
		return side;
	}
	
	/**
	 * Retrieves the upper boundary value.<br>
	 * This value represents the height of the {@code BoundRadius}.
	 * @return The current {@code float} value for the upper bound.
	 */
	public float getUpper()
	{
		return upper;
	}
	
	/**
	 * Retrieves the collision value for this object.<br>
	 * This value is obtained from the bound radius of the {@code VisibleObjectTemplate}.
	 * @return The collision value as a {@code float}.
	 */
	public float getCollision()
	{
		return collision;
	}
}
