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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Represents the static data and configuration for a game zone.<br>
 * This class serves as a template used to define world properties and layout.<br>
 * It is mapped from XML data to populate {@link com.aionemu.gameserver.world.zone.ZoneName} information.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Zone")
public class ZoneTemplate
{
	@XmlElement
	protected Points points;
	@XmlElement
	protected Cylinder cylinder;
	@XmlElement
	protected Sphere sphere;
	@XmlElement
	protected Semisphere semisphere;
	@XmlAttribute
	protected int flags = -1;
	@XmlAttribute
	protected int priority;
	@XmlTransient
	private String name;
	@XmlTransient
	private ZoneName zoneName;
	
	/**
	 * Retrieves the internal name of the zone.<br>
	 * This value is used for XML identification purposes.
	 * @return The {@code String} representation of the zone name.
	 */
	@XmlAttribute(name = "name")
	public String getXmlName()
	{
		return name;
	}
	
	/**
	 * Sets the internal name for this zone.<br>
	 * This method updates both the {@code name} field and the {@link ZoneName} object.
	 * @param name The new name to assign to the zone.
	 */
	protected void setXmlName(String name)
	{
		zoneName = ZoneName.createOrGet(name);
		this.name = zoneName.name();
	}
	
	@XmlAttribute
	protected int mapid;
	@XmlAttribute(name = "siege_id")
	protected List<Integer> siegeId;
	@XmlAttribute(name = "town_id")
	private int townId;
	@XmlAttribute(name = "area_type")
	protected AreaType areaType = AreaType.POLYGON;
	@XmlAttribute(name = "zone_type")
	protected ZoneClassName zoneType = ZoneClassName.SUB;
	
	/**
	 * Retrieves the {@code Points} associated with this zone.<br>
	 * This method returns the coordinate data for the area.
	 * @return the {@code Points} object representing the zone's location.
	 */
	public Points getPoints()
	{
		return points;
	}
	
	/**
	 * Retrieves the {@code Cylinder} object associated with this zone.<br>
	 * This method returns the geometric shape of the area.
	 * @return the {@link Cylinder} object or {@code null} if it is not set.
	 */
	public Cylinder getCylinder()
	{
		return cylinder;
	}
	
	/**
	 * Retrieves the {@code Sphere} object associated with this zone.<br>
	 * This method returns the spherical boundary data for the template.
	 * @return The {@link Sphere} object, or {@code null} if it is not defined.
	 */
	public Sphere getSphere()
	{
		return sphere;
	}
	
	/**
	 * Retrieves the {@code Semisphere} associated with this zone.<br>
	 * This method returns the geometric shape of the area.
	 * @return the {@link Semisphere} object.
	 */
	public Semisphere getSemisphere()
	{
		return semisphere;
	}
	
	/**
	 * Retrieves the priority level of this zone.<br>
	 * This value determines the order in which zones are processed.
	 * @return The integer priority value.
	 */
	public int getPriority()
	{
		return priority;
	}
	
	/**
	 * Retrieves the name of the zone.<br>
	 * This method returns a {@link ZoneName} object.
	 * @return the {@code ZoneName} associated with this template.
	 */
	public ZoneName getName()
	{
		return zoneName;
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * Returns {@code 0} if no map is assigned.
	 * @return The {@code int} ID of the map.
	 */
	public int getMapid()
	{
		return mapid;
	}
	
	/**
	 * Retrieves the type of the area.<br>
	 * This method returns the {@code AreaType} associated with this zone.
	 * @return the current {@code AreaType}
	 */
	public AreaType getAreaType()
	{
		return areaType;
	}
	
	/**
	 * Retrieves the specific type of the zone.<br>
	 * This method returns the {@code ZoneClassName} associated with this template.
	 * @return The {@code ZoneClassName} of the zone.
	 */
	public ZoneClassName getZoneType()
	{
		return zoneType;
	}
	
	/**
	 * Retrieves the list of unique identifiers for sieges associated with this zone.<br>
	 * This method returns a {@code List<Integer>} containing all relevant IDs.
	 * @return A {@code List<Integer>} of siege IDs.
	 */
	public List<Integer> getSiegeId()
	{
		return siegeId;
	}
	
	/**
	 * Retrieves the raw flag value for this map.<br>
	 * This value represents specific configuration settings.
	 * @return The {@code int} value of the flags.
	 */
	public int getFlags()
	{
		return flags;
	}
	
	/**
	 * Retrieves the unique identifier for the town associated with this object.<br>
	 * This value is used to determine which town area the entity belongs to.
	 * @return the {@code int} ID of the town.
	 */
	public int getTownId()
	{
		return townId;
	}
}
