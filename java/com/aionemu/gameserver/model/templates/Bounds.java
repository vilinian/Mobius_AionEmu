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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the spatial boundaries for game objects or areas.<br>
 * This class extends {@link BoundRadius} to define specific limits in the game world.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bounds")
public class Bounds extends BoundRadius
{
	/**
	 * Creates a new instance of the {@code Bounds} class.<br>
	 * This constructor initializes the object with default values.
	 */
	public Bounds()
	{
	}
	
	/**
	 * Creates a new {@link Bounds} object with specific dimensions.<br>
	 * This constructor sets the spatial limits for an area.
	 * @param front The distance from the front edge.
	 * @param side The width of the side boundary.
	 * @param upper The height of the upper limit.
	 * @param altitude The vertical position or elevation.
	 */
	public Bounds(float front, float side, float upper, float altitude)
	{
		super(front, side, upper);
		this.altitude = altitude;
	}
	
	@XmlAttribute
	private Float altitude;
	
	/**
	 * Retrieves the current altitude value.<br>
	 * This method returns the height of the {@code Bounds} object.
	 * @return The altitude as a {@code Float}.
	 */
	public Float getAltitude()
	{
		return altitude;
	}
}
