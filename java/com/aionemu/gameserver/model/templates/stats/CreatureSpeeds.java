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
package com.aionemu.gameserver.model.templates.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the movement speed attributes for various creatures in the game.<br>
 * It serves as a data template to manage how different entities move across the world.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreatureSpeeds")
public class CreatureSpeeds
{
	@XmlAttribute(name = "walk")
	private float walkSpeed = 0.7f;
	@XmlAttribute(name = "run")
	private float runSpeed;
	@XmlAttribute(name = "group_walk")
	private float groupWalkSpeed = 1.3f;
	@XmlAttribute(name = "run_fight")
	private float runSpeedFight;
	@XmlAttribute(name = "group_run_fight")
	private float groupRunSpeedFight;
	@XmlAttribute(name = "fly")
	private float flySpeed;
	
	/**
	 * Retrieves the walking speed of the pet.<br>
	 * This value is used to determine how fast the pet moves while walking.
	 * @return The {@code float} value representing the walk speed.
	 */
	public float getWalkSpeed()
	{
		return walkSpeed;
	}
	
	/**
	 * Retrieves the current running speed of the pet.<br>
	 * This value is used to determine how fast the pet moves while running.
	 * @return The {@code float} value representing the run speed.
	 */
	public float getRunSpeed()
	{
		return runSpeed;
	}
	
	/**
	 * Retrieves the current flying speed of the ride.<br>
	 * This value is used to determine how fast a player moves while in flight.
	 * @return The {@code float} value representing the flying speed.
	 */
	public float getFlySpeed()
	{
		return flySpeed;
	}
	
	/**
	 * Gets the walking speed for a creature in a group.<br>
	 * This value is used when multiple creatures move together.
	 * @return the {@code float} value of the group walk speed.
	 */
	public float getGroupWalkSpeed()
	{
		return groupWalkSpeed;
	}
	
	/**
	 * Gets the movement speed of a creature during combat.<br>
	 * This value is used when the creature is running while fighting.
	 * @return The {@code float} value representing the fight run speed.
	 */
	public float getRunSpeedFight()
	{
		return runSpeedFight;
	}
	
	/**
	 * Gets the speed of a creature when running in a group during combat.<br>
	 * This value is used to calculate movement velocity for fighting mobs.
	 * @return The {@code float} value of the group run fight speed.
	 */
	public float getGroupRunSpeedFight()
	{
		return groupRunSpeedFight;
	}
}
