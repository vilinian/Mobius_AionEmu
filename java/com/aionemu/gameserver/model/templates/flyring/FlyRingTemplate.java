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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.utils3d.Point3D;

/**
 * Represents the data template for a {@code FlyRing} object in the game world.<br>
 * This class stores configuration details such as its 3D coordinates and properties.
 * @author M@xx
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlyRing")
public class FlyRingTemplate
{
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "map")
	protected int map;
	@XmlAttribute(name = "radius")
	protected float radius;
	@XmlElement(name = "center")
	protected FlyRingPoint center;
	@XmlElement(name = "left")
	protected FlyRingPoint left;
	@XmlElement(name = "right")
	protected FlyRingPoint right;
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the unique identifier for the map. <br>
	 * This value is used to identify which game world the {@link FlyRingTemplate} belongs to.
	 * @return The {@code int} ID of the map.
	 */
	public int getMap()
	{
		return map;
	}
	
	/**
	 * Retrieves the current radius of the {@code BoundingSphere}.
	 * @return The radius as a {@code float}.
	 */
	public float getRadius()
	{
		return radius;
	}
	
	/**
	 * Retrieves the center point of the fly ring.<br>
	 * This method returns the {@code FlyRingPoint} object stored in this template.
	 * @return The {@code FlyRingPoint} representing the center.
	 */
	public FlyRingPoint getCenter()
	{
		return center;
	}
	
	/**
	 * Retrieves the first point of the fly ring.<br>
	 * This method returns the {@code left} coordinate.
	 * @return the {@link FlyRingPoint} representing the first point.
	 */
	public FlyRingPoint getP1()
	{
		return left;
	}
	
	/**
	 * Retrieves the second point of the fly ring.<br>
	 * This method returns the {@code right} point defined in the template.
	 * @return the {@link FlyRingPoint} representing the second position.
	 */
	public FlyRingPoint getP2()
	{
		return right;
	}
	
	/**
	 * Creates a new instance of the {@link FlyRingTemplate} class.<br>
	 * This constructor initializes a default template for fly rings.
	 */
	public FlyRingTemplate()
	{
	}
	
	/**
	 * Creates a new {@link FlyRingTemplate} instance.<br>
	 * This constructor initializes the ring with its spatial coordinates and dimensions.
	 * @param name The display name of the fly ring.
	 * @param mapId The unique identifier for the map where this ring exists.
	 * @param center The 3D coordinates for the center point of the ring.
	 * @param left The 3D coordinates for the left boundary point.
	 * @param right The 3D coordinates for the right boundary point.
	 * @param radius The size of the fly ring radius.
	 */
	public FlyRingTemplate(String name, int mapId, Point3D center, Point3D left, Point3D right, int radius)
	{
		this.name = name;
		map = mapId;
		this.radius = radius;
		this.center = new FlyRingPoint(center);
		this.left = new FlyRingPoint(left);
		this.right = new FlyRingPoint(right);
	}
}
