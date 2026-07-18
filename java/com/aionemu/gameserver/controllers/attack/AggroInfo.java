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
package com.aionemu.gameserver.controllers.attack;

import com.aionemu.gameserver.model.gameobjects.AionObject;

/**
 * This class stores information regarding the aggression levels of a creature.<br>
 * It tracks specific metrics such as the hate value and damage dealt by an {@link AionObject}.
 * @author ATracer, Sarynth
 */
public class AggroInfo
{
	private final AionObject attacker;
	private int hate;
	private int damage;
	
	/**
	 * Creates a new {@link AggroInfo} instance.<br>
	 * This object tracks the hate and damage caused by an attacker.
	 * @param attacker The {@code AionObject} that is performing the attack.
	 */
	AggroInfo(AionObject attacker)
	{
		this.attacker = attacker;
	}
	
	/**
	 * Retrieves the object that initiated the attack.<br>
	 * This method returns the {@link AionObject} stored in this instance.
	 * @return The {@code AionObject} representing the attacker.
	 */
	public AionObject getAttacker()
	{
		return attacker;
	}
	
	/**
	 * Increases the total damage value of this {@link AggroInfo} instance.<br>
	 * The new amount is added to the current {@code damage}.<br>
	 * If the result is less than {@code 0}, it is set to {@code 0}.
	 * @param damage The amount of damage to add.
	 */
	public void addDamage(int damage)
	{
		this.damage += damage;
		if (this.damage < 0)
		{
			this.damage = 0;
		}
	}
	
	/**
	 * Increases the total hate value of the object.<br>
	 * The {@code hate} value will never drop below {@code 1}.
	 * @param damage The amount of damage to add to the current hate.
	 */
	public void addHate(int damage)
	{
		hate += damage;
		if (hate < 1)
		{
			hate = 1;
		}
	}
	
	/**
	 * Retrieves the current hate value.<br>
	 * This value represents the amount of aggression accumulated by a creature.
	 * @return The {@code int} value of the hate.
	 */
	public int getHate()
	{
		return hate;
	}
	
	/**
	 * Updates the current hate value.<br>
	 * This method sets the {@code hate} field to a new value.
	 * @param hate The new integer value for hate.
	 */
	public void setHate(int hate)
	{
		this.hate = hate;
	}
	
	/**
	 * Retrieves the total damage value.<br>
	 * This value is updated using {@code addDamage}.
	 * @return The current amount of damage as an {@code int}.
	 */
	public int getDamage()
	{
		return damage;
	}
	
	/**
	 * Sets the amount of damage for this {@link AggroInfo} instance.<br>
	 * This updates the internal {@code damage} field.
	 * @param damage The new damage value to set.
	 */
	public void setDamage(int damage)
	{
		this.damage = damage;
	}
}
