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

import com.aionemu.gameserver.utils.stats.ClassStats;

/**
 * This class defines the template for player statistics.<br>
 * It serves as a data model for storing and managing various {@link StatsTemplate} attributes specific to players.
 * @author Luno
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "player_stats_template")
public class PlayerStatsTemplate extends StatsTemplate
{
	@XmlAttribute(name = "power")
	private int power;
	@XmlAttribute(name = "health")
	private int health;
	@XmlAttribute(name = "agility")
	private int agility;
	@XmlAttribute(name = "accuracy")
	private int accuracy;
	@XmlAttribute(name = "knowledge")
	private int knowledge;
	@XmlAttribute(name = "will")
	private int will;
	
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
	 * Retrieves the current health value.<br>
	 * This returns the {@code health} attribute of the template.
	 * @return The health as an {@code int}.
	 */
	public int getHealth()
	{
		return health;
	}
	
	/**
	 * Retrieves the current agility value.<br>
	 * This value is stored in the {@code agility} field.
	 * @return The integer value of the agility stat.
	 */
	public int getAgility()
	{
		return agility;
	}
	
	/**
	 * Retrieves the current accuracy value.<br>
	 * This value is stored in the {@code accuracy} field.
	 * @return The integer value of the player's accuracy.
	 */
	public int getAccuracy()
	{
		return accuracy;
	}
	
	/**
	 * Retrieves the knowledge value for the current player class.<br>
	 * This method uses {@link ClassStats} to find the correct value.
	 * @return The knowledge value as an {@code int}.
	 */
	public int getKnowledge()
	{
		return knowledge;
	}
	
	/**
	 * Retrieves the Will stat value.<br>
	 * This value is stored in the {@code will} field.
	 * @return The integer value of the Will stat.
	 */
	public int getWill()
	{
		return will;
	}
}
