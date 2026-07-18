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
package com.aionemu.gameserver.model.templates.shield;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;
import com.aionemu.gameserver.model.utils3d.Point3D;

/**
 * Represents the base data template for a shield item in the game.<br>
 * It stores configuration details such as physical properties and visual attributes.<br>
 * This class is used by the server to define how different shields behave.
 * @author M@xx, Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Shield")
public class ShieldTemplate
{
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "map")
	protected int map;
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "radius")
	protected float radius;
	@XmlElement(name = "center")
	protected ShieldPoint center;
	
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
	 * Retrieves the center point of the shield.<br>
	 * This method returns the {@code ShieldPoint} object stored in this template.
	 * @return the {@code ShieldPoint} representing the center.
	 */
	public ShieldPoint getCenter()
	{
		return center;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Creates a new instance of the {@link ShieldTemplate} class.<br>
	 * This constructor initializes a default shield template with null values.
	 */
	public ShieldTemplate()
	{
	}
	
	/**
	 * Creates a new {@link ShieldTemplate} instance.<br>
	 * This constructor initializes the shield with a name, a specific map, and a center point.<br>
	 * The radius is automatically set to {@code 6}.
	 * @param name The display name of the shield.
	 * @param mapId The unique identifier for the map where the shield exists.
	 * @param center The {@link Point3D} coordinates for the shield's center.
	 */
	public ShieldTemplate(String name, int mapId, Point3D center)
	{
		this.name = name;
		map = mapId;
		radius = 6;
		this.center = new ShieldPoint(center);
	}
}
