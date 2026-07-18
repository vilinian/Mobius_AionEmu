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
package com.aionemu.gameserver.model.templates.road;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;
import com.aionemu.gameserver.model.utils3d.Point3D;

/**
 * Represents the data template for a road in the game world.<br>
 * This class stores configuration details such as its 3D coordinates and pathing information.
 * @author SheppeR
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Road")
public class RoadTemplate
{
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "map")
	protected int map;
	@XmlAttribute(name = "radius")
	protected float radius;
	@XmlElement(name = "center")
	protected RoadPoint center;
	@XmlElement(name = "p1")
	protected RoadPoint p1;
	@XmlElement(name = "p2")
	protected RoadPoint p2;
	@XmlElement(name = "roadexit")
	protected RoadExit roadExit;
	
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
	 * Retrieves the center point of the road.<br>
	 * This method returns the {@code RoadPoint} stored in the {@code center} field.
	 * @return The {@link RoadPoint} representing the center.
	 */
	public RoadPoint getCenter()
	{
		return center;
	}
	
	/**
	 * Retrieves the first point of the road.<br>
	 * This method returns the {@code p1} coordinate.
	 * @return the {@link RoadPoint} representing the first point.
	 */
	public RoadPoint getP1()
	{
		return p1;
	}
	
	/**
	 * Retrieves the second point of the road.<br>
	 * This method returns the {@code RoadPoint} stored in the {@code p2} field.
	 * @return the {@code RoadPoint} representing the second end of the road.
	 */
	public RoadPoint getP2()
	{
		return p2;
	}
	
	/**
	 * Retrieves the {@link RoadExit} associated with this road.<br>
	 * This method returns the exit point data for the template.
	 * @return the {@code RoadExit} object or {@code null} if no exit is defined.
	 */
	public RoadExit getRoadExit()
	{
		return roadExit;
	}
	
	/**
	 * Creates a new instance of the {@link RoadTemplate} class.<br>
	 * This constructor initializes a default road template with null values.
	 */
	public RoadTemplate()
	{
	}
	
	/**
	 * Creates a new {@link RoadTemplate} with the specified coordinates.<br>
	 * This constructor initializes the road name, map ID, and three key points.<br>
	 * The radius is automatically set to {@code 6}.
	 * @param name The display name of the road.
	 * @param mapId The unique identifier for the map where the road exists.
	 * @param center The central {@link Point3D} of the road.
	 * @param p1 The first coordinate point of the road.
	 * @param p2 The second coordinate point of the road.
	 */
	public RoadTemplate(String name, int mapId, Point3D center, Point3D p1, Point3D p2)
	{
		this.name = name;
		map = mapId;
		radius = 6;
		this.center = new RoadPoint(center);
		this.p1 = new RoadPoint(p1);
		this.p2 = new RoadPoint(p2);
	}
}
