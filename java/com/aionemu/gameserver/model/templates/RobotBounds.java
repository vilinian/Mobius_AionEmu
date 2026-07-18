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
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the spatial boundaries for a robot entity.<br>
 * This class defines the area where a robot can move or interact within the game world.
 * @author Ever'
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RobotBounds")
public class RobotBounds extends BoundRadius
{
	/**
	 * Creates a new instance of {@link RobotBounds}.<br>
	 * This constructor initializes the object with default values.
	 */
	public RobotBounds()
	{
	}
	
	/**
	 * Creates a new {@link RobotBounds} object with specific dimensions.<br>
	 * This constructor initializes the boundaries for a robot.
	 * @param front The distance from the front of the robot.
	 * @param side The distance from the side of the robot.
	 * @param upper The height or upper limit of the robot.
	 */
	public RobotBounds(float front, float side, float upper)
	{
		super(front, side, upper);
	}
}
