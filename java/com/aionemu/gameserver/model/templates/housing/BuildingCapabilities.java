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
package com.aionemu.gameserver.model.templates.housing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the functional capabilities of a housing building.<br>
 * It maps specific features and permissions associated with different building types.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "caps")
public class BuildingCapabilities
{
	@XmlAttribute(required = true)
	protected boolean addon;
	@XmlAttribute(required = true)
	protected int emblemId;
	@XmlAttribute(required = true)
	protected boolean floor;
	@XmlAttribute(required = true)
	protected boolean room;
	@XmlAttribute(required = true)
	protected int interior;
	@XmlAttribute(required = true)
	protected int exterior;
	
	/**
	 * Checks if the building is allowed to have an addon.<br>
	 * This method returns the value of the {@code addon} field.
	 * @return {@code true} if addons are permitted, {@code false} otherwise.
	 */
	public boolean canHaveAddon()
	{
		return addon;
	}
	
	/**
	 * Retrieves the unique identifier for the legion emblem.<br>
	 * This value is used to identify which emblem is currently active.
	 * @return the {@code int} ID of the emblem.
	 */
	public int getEmblemId()
	{
		return emblemId;
	}
	
	/**
	 * Checks if the building is allowed to change its floor.<br>
	 * This method returns the value of the {@code floor} attribute.
	 * @return {@code true} if the floor can be changed, {@code false} otherwise.
	 */
	public boolean canChangeFloor()
	{
		return floor;
	}
	
	/**
	 * Checks if the building allows changing rooms.<br>
	 * This method returns the value of the {@code room} attribute.
	 * @return {@code true} if rooms can be changed, {@code false} otherwise.
	 */
	public boolean canChangeRoom()
	{
		return room;
	}
	
	/**
	 * Checks if the building allows for interior changes.<br>
	 * This method returns the current interior configuration value.
	 * @return The integer value representing the allowed interior change type.
	 */
	public int canChangeInterior()
	{
		return interior;
	}
	
	/**
	 * Checks if the building's exterior can be modified.<br>
	 * This method returns the current status of the {@code exterior} property.
	 * @return The integer value representing the exterior change capability.
	 */
	public int canChangeExterior()
	{
		return exterior;
	}
}
