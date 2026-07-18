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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class defines the base statistics for a pet.<br>
 * It serves as a template to store and manage various attributes of pets in the game.
 * @author IlBuono
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "petstats")
public class PetStatsTemplate
{
	@XmlAttribute(name = "reaction")
	private String reaction;
	@XmlAttribute(name = "run_speed")
	private float runSpeed;
	@XmlAttribute(name = "walk_speed")
	private float walkSpeed;
	@XmlAttribute(name = "height")
	private float height;
	@XmlAttribute(name = "altitude")
	private float altitude;
	
	/**
	 * Retrieves the reaction string for this pet template.<br>
	 * This value is used to determine how the pet behaves.
	 * @return The {@code String} representing the pet's reaction.
	 */
	public String getReaction()
	{
		return reaction;
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
	 * Retrieves the walking speed of the pet.<br>
	 * This value is used to determine how fast the pet moves while walking.
	 * @return The {@code float} value representing the walk speed.
	 */
	public float getWalkSpeed()
	{
		return walkSpeed;
	}
	
	/**
	 * Retrieves the current height of the player.<br>
	 * This value is stored as a {@code float}.
	 * @return The player's height.
	 */
	public float getHeight()
	{
		return height;
	}
	
	/**
	 * Retrieves the current altitude of the minion.<br>
	 * This value is stored in the {@code altitude} field.
	 * @return The altitude as a {@code float}.
	 */
	public float getAltitude()
	{
		return altitude;
	}
}
