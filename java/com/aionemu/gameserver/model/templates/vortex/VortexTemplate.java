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
package com.aionemu.gameserver.model.templates.vortex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * This class represents the data model for a {@code Vortex} entity.<br>
 * It stores configuration settings and properties used by the game server.<br>
 * It is mapped to XML using {@code XmlType}.
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Vortex")
public class VortexTemplate
{
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "defends_race")
	protected Race dRace;
	@XmlAttribute(name = "offence_race")
	protected Race oRace;
	@XmlElement(name = "home_point")
	protected HomePoint home;
	@XmlElement(name = "resurrection_point")
	protected ResurrectionPoint resurrection;
	@XmlElement(name = "start_point")
	protected StartPoint start;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the race that is being defended.<br>
	 * This method returns the {@code Race} object stored in the {@code dRace} field.
	 * @return the defending {@link Race}
	 */
	public Race getDefendersRace()
	{
		return dRace;
	}
	
	/**
	 * Retrieves the race of the invaders.<br>
	 * This method returns the {@code oRace} field from the template.
	 * @return the {@link Race} object representing the invading group.
	 */
	public Race getInvadersRace()
	{
		return oRace;
	}
	
	/**
	 * Retrieves the primary home point for this vortex.<br>
	 * This method returns the {@link HomePoint} object associated with the template.
	 * @return the {@code HomePoint} of the vortex.
	 */
	public HomePoint getHomePoint()
	{
		return home;
	}
	
	/**
	 * Retrieves the designated resurrection point for this vortex.<br>
	 * This method returns the {@link ResurrectionPoint} object associated with the template.
	 * @return the {@code ResurrectionPoint} of the vortex.
	 */
	public ResurrectionPoint getResurrectionPoint()
	{
		return resurrection;
	}
	
	/**
	 * Retrieves the starting point for this vortex.<br>
	 * This method returns the {@link StartPoint} object associated with the template.
	 * @return the {@code StartPoint} of the vortex.
	 */
	public StartPoint getStartPoint()
	{
		return start;
	}
}
