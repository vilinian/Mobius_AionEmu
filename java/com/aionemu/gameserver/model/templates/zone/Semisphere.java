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
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a hemispherical area within the game world.<br>
 * This class extends {@link Sphere} to define specific spatial boundaries for zones.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Semisphere")
public class Semisphere extends Sphere
{
	/**
	 * Creates a new instance of the {@link Semisphere} class.<br>
	 * This constructor initializes the object with default values.
	 */
	public Semisphere()
	{
		super();
	}
	
	/**
	 * Creates a new {@link Semisphere} instance.<br>
	 * This constructor initializes the position and size of the hemisphere.<br>
	 * It calls the superclass constructor to set the base values.
	 * @param x The x-coordinate of the center point.
	 * @param y The y-coordinate of the center point.
	 * @param z The z-coordinate of the center point.
	 * @param radius The radius of the hemisphere.
	 */
	public Semisphere(float x, float y, float z, float radius)
	{
		super(x, y, z, radius);
	}
}
