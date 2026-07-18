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
 * This class defines the base statistics for non-player characters (NPCs).<br>
 * It serves as a template to store and manage various attributes inherited from {@link StatsTemplate}.
 * @author Luno
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "npc_stats_template")
public class NpcStatsTemplate extends StatsTemplate
{
	@XmlAttribute(name = "pdef")
	private int pdef;
	@XmlAttribute(name = "mdef")
	private int mdef;
	@XmlAttribute(name = "mresist")
	private int mresist;
	@XmlAttribute(name = "crit")
	private int crit;
	@XmlAttribute(name = "accuracy")
	private int accuracy;
	@XmlAttribute(name = "power")
	private int power;
	@XmlAttribute(name = "maxXp")
	private int maxXp;
	
	/**
	 * Gets the walking speed for a creature in a group.<br>
	 * This value is used when multiple creatures move together.
	 * @return the {@code float} value of the group walk speed.
	 */
	@Override
	public float getGroupWalkSpeed()
	{
		return speeds == null ? 0 : speeds.getGroupWalkSpeed();
	}
	
	/**
	 * Gets the movement speed of a creature during combat.<br>
	 * This value is used when the creature is running while fighting.
	 * @return The {@code float} value representing the fight run speed.
	 */
	@Override
	public float getRunSpeedFight()
	{
		return speeds == null ? 0 : speeds.getRunSpeedFight();
	}
	
	/**
	 * Gets the speed of a creature when running in a group during combat.<br>
	 * This value is used to calculate movement velocity for fighting mobs.
	 * @return The {@code float} value of the group run fight speed.
	 */
	@Override
	public float getGroupRunSpeedFight()
	{
		return speeds == null ? 0 : speeds.getGroupRunSpeedFight();
	}
	
	/**
	 * Retrieves the physical defense value of the NPC.<br>
	 * This value is used to calculate damage reduction from physical attacks.
	 * @return The {@code int} value representing physical defense.
	 */
	public int getPdef()
	{
		return pdef;
	}
	
	/**
	 * Retrieves the magic defense value of the NPC.<br>
	 * This value is used to calculate damage reduction from magic attacks.
	 * @return The {@code float} value of the magic defense.
	 */
	public float getMdef()
	{
		return mdef;
	}
	
	/**
	 * Retrieves the magic resistance value of the NPC.<br>
	 * This value is used to calculate damage reduction from magic attacks.
	 * @return The current {@code mresist} value as an {@code int}.
	 */
	public int getMresist()
	{
		return mresist;
	}
	
	/**
	 * Retrieves the critical hit rate for this template.<br>
	 * This value is used to calculate how often a character lands a critical strike.
	 * @return The {@code float} value of the critical hit rate.
	 */
	public float getCrit()
	{
		return crit;
	}
	
	/**
	 * Retrieves the accuracy value for this template.<br>
	 * This value is used to determine hit success rates.
	 * @return The current {@code float} accuracy value.
	 */
	public float getAccuracy()
	{
		return accuracy;
	}
	
	/**
	 * Retrieves the current power value.<br>
	 * This value is stored in the {@code power} field.
	 * @return The integer power value.
	 */
	public int getPower()
	{
		return power;
	}
	
	/**
	 * Sets the power value for this template.<br>
	 * This updates the {@code power} field used in calculations.
	 * @param power The new integer value to set for the power attribute.
	 */
	public void setPower(int power)
	{
		this.power = power;
	}
	
	/**
	 * Retrieves the maximum experience points for this template.<br>
	 * This value is used to determine the XP cap for NPCs.
	 * @return The maximum experience points as an {@code int}.
	 */
	public int getMaxXp()
	{
		return maxXp;
	}
}
