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
package com.aionemu.gameserver.model.templates.ride;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.Bounds;

/**
 * This class represents the configuration data for a mount or ride.<br>
 * It stores essential properties such as movement speed and physical boundaries.<br>
 * Use this model to define how different rides behave within the game world.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RideInfo", propOrder =
{
	"bounds"
})
public class RideInfo
{
	protected Bounds bounds;
	@XmlAttribute(name = "cost_fp")
	protected Integer costFp;
	@XmlAttribute(name = "start_fp")
	protected int startFp;
	@XmlAttribute(name = "sprint_speed")
	protected float sprintSpeed;
	@XmlAttribute(name = "fly_speed")
	protected float flySpeed;
	@XmlAttribute(name = "move_speed")
	protected float moveSpeed;
	@XmlAttribute
	protected Integer type;
	@XmlAttribute(required = true)
	protected int id;
	
	/**
	 * Retrieves the {@link Bounds} object for this ride.<br>
	 * This method returns the spatial boundaries associated with the ride data.
	 * @return The {@code Bounds} object, or {@code null} if no bounds are defined.
	 */
	public Bounds getBounds()
	{
		return bounds;
	}
	
	/**
	 * Retrieves the cost in {@code fp} for this ride.<br>
	 * This value is used to determine the price of using the ride.
	 * @return the {@code Integer} value representing the cost.
	 */
	public Integer getCostFp()
	{
		return costFp;
	}
	
	/**
	 * Retrieves the starting {@code fp} value.<br>
	 * This value is used to determine the initial state of a ride.
	 * @return the current {@code startFp} value.
	 */
	public int getStartFp()
	{
		return startFp;
	}
	
	/**
	 * Retrieves the current sprinting speed of the ride.<br>
	 * This value is used to calculate how fast a character moves while sprinting.
	 * @return the {@code float} value representing the sprint speed.
	 */
	public float getSprintSpeed()
	{
		return sprintSpeed;
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
	 * Retrieves the movement speed of the ride.<br>
	 * This value is used to determine how fast the entity moves.
	 * @return the {@code float} value representing the move speed.
	 */
	public float getMoveSpeed()
	{
		return moveSpeed;
	}
	
	/**
	 * Retrieves the specific category or classification of the ride.<br>
	 * This value identifies what kind of ride this object represents.
	 * @return the {@code Integer} value representing the ride type.
	 */
	public Integer getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the unique identifier for this NPC.<br>
	 * This value is stored in the {@code id} field.
	 * @return The integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return id;
	}
	
	/**
	 * Checks if the ride is allowed to sprint.<br>
	 * It returns {@code true} if the {@code sprintSpeed} is not {@code 0}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if sprinting is enabled, {@code false} otherwise.
	 */
	public boolean canSprint()
	{
		return sprintSpeed != 0;
	}
}
