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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * This class represents the statistical properties of a weapon.<br>
 * It holds data such as damage, speed, and other attributes used by item.
 * @author ATracer
 */
public class WeaponStats
{
	@XmlAttribute(name = "min_damage")
	protected int minDamage;
	@XmlAttribute(name = "max_damage")
	protected int maxDamage;
	@XmlAttribute(name = "attack_speed")
	protected int attackSpeed;
	@XmlAttribute(name = "physical_critical")
	protected int physicalCritical;
	@XmlAttribute(name = "physical_accuracy")
	protected int physicalAccuracy;
	@XmlAttribute
	protected int parry;
	@XmlAttribute(name = "magical_accuracy")
	protected int magicalAccuracy;
	@XmlAttribute(name = "boost_magical_skill")
	protected int boostMagicalSkill;
	@XmlAttribute(name = "attack_range")
	protected int attackRange;
	@XmlAttribute(name = "hit_count")
	protected int hitCount;
	@XmlAttribute(name = "reduce_max")
	protected int reduceMax;
	
	/**
	 * Retrieves the minimum damage value for this weapon.<br>
	 * This value is used to calculate the lower bound of an attack.
	 * @return The {@code int} value of the minimum damage.
	 */
	public int getMinDamage()
	{
		return minDamage;
	}
	
	/**
	 * Retrieves the maximum damage value for this weapon.<br>
	 * This value is stored in the {@code maxDamage} field.
	 * @return The highest possible damage as an {@code int}.
	 */
	public int getMaxDamage()
	{
		return maxDamage;
	}
	
	/**
	 * Calculates the average damage of a weapon.<br>
	 * This method takes the sum of {@code getMinDamage} and {@code getMaxDamage}.<br>
	 * It then divides that total by {@code 2}.
	 * @return The calculated mean damage as an {@code int}.
	 */
	public int getMeanDamage()
	{
		return (minDamage + maxDamage) / 2;
	}
	
	/**
	 * Retrieves the current attack speed of the weapon.<br>
	 * This value is used to determine how fast the weapon strikes.
	 * @return the {@code int} value of the attack speed.
	 */
	public int getAttackSpeed()
	{
		return attackSpeed;
	}
	
	/**
	 * Retrieves the physical critical hit rate.<br>
	 * This value is used to calculate critical damage for weapons.
	 * @return The {@code int} value of the physical critical stat.
	 */
	public int getPhysicalCritical()
	{
		return physicalCritical;
	}
	
	/**
	 * Retrieves the physical accuracy value of the weapon.<br>
	 * This value determines how likely an attack is to hit a target.
	 * @return The {@code int} value for physical accuracy.
	 */
	public int getPhysicalAccuracy()
	{
		return physicalAccuracy;
	}
	
	/**
	 * Retrieves the parry value for this weapon.<br>
	 * This value represents the chance to deflect an incoming attack.
	 * @return The current {@code int} parry value.
	 */
	public int getParry()
	{
		return parry;
	}
	
	/**
	 * Retrieves the magical accuracy value for this weapon.<br>
	 * This value determines how accurately magic attacks hit a target.
	 * @return The {@code int} value of the magical accuracy.
	 */
	public int getMagicalAccuracy()
	{
		return magicalAccuracy;
	}
	
	/**
	 * Retrieves the magical skill boost value.<br>
	 * This value is used to increase a character's magic power.
	 * @return The {@code int} value of the magical skill boost.
	 */
	public int getBoostMagicalSkill()
	{
		return boostMagicalSkill;
	}
	
	/**
	 * Retrieves the maximum distance a weapon can reach.<br>
	 * This value is stored in the {@code attackRange} field.
	 * @return The current attack range as an {@code int}.
	 */
	public int getAttackRange()
	{
		return attackRange;
	}
	
	/**
	 * Retrieves the total number of hits for this weapon.<br>
	 * This value is stored in the {@code hitCount} field.
	 * @return The current {@code hitCount} as an {@code int}.
	 */
	public int getHitCount()
	{
		return hitCount;
	}
	
	/**
	 * Retrieves the maximum reduction value.<br>
	 * This value is stored in the {@code reduceMax} field.
	 * @return The current {@code reduceMax} integer value.
	 */
	public int getReduceMax()
	{
		return reduceMax;
	}
}
