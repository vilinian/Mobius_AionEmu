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
package com.aionemu.gameserver.model.templates.robot;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.RobotBounds;

/**
 * This class represents the configuration data for a robot template.<br>
 * It stores essential properties and attributes used to define robot behavior and appearance in the game world.
 * @author Ever'
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RobotInfo", propOrder =
{
	"bound"
})
public class RobotInfo
{
	protected RobotBounds bound;
	@XmlAttribute
	protected Integer type;
	@XmlAttribute(required = true)
	protected int id;
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	/**
	 * Retrieves the unique identifier for the robot.<br>
	 * This value is used to identify specific automated entities.
	 * @return The {@code int} ID of the robot.
	 */
	public int getRobotId()
	{
		return id;
	}
	
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
	 * Retrieves the boundary information for this robot.<br>
	 * This method returns the {@code RobotBounds} object associated with the robot.
	 * @return the {@code RobotBounds} of the robot.
	 */
	public RobotBounds getBound()
	{
		return bound;
	}
}
