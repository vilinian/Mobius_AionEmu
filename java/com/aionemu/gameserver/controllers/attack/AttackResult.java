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

import com.aionemu.gameserver.skillengine.model.HitType;

/**
 * This class represents the outcome of an attack action.<br>
 * It contains data regarding whether a hit was successful and its associated {@link HitType}.
 * @author ATracer modified by Sippolo, kecimis
 */
public class AttackResult
{
	private int damage;
	private final AttackStatus attackStatus;
	private HitType hitType = HitType.EVERYHIT;
	/**
	 * shield effects related
	 */
	private int shieldType;
	private int reflectedDamage = 0;
	private int reflectedSkillId = 0;
	private int protectedSkillId = 0;
	private int protectedDamage = 0;
	private int protectorId = 0;
	private int shieldMp = 0;
	private boolean launchSubEffect = true;
	
	/**
	 * Creates a new {@link AttackResult} object.<br>
	 * This constructor initializes the damage and status of an attack.
	 * @param damage The amount of damage dealt by the attack.
	 * @param attackStatus The current status of the attack.
	 */
	public AttackResult(int damage, AttackStatus attackStatus)
	{
		this.damage = damage;
		this.attackStatus = attackStatus;
	}
	
	/**
	 * Creates a new {@code AttackResult} with specific damage and status.<br>
	 * This constructor also sets the hit type for the attack.
	 * @param damage The amount of damage dealt by the attack.
	 * @param attackStatus The current status of the attack.
	 * @param type The {@link HitType} category for this attack.
	 */
	public AttackResult(int damage, AttackStatus attackStatus, HitType type)
	{
		this(damage, attackStatus);
		hitType = type;
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
	
	/**
	 * Retrieves the current status of the attack.<br>
	 * This method returns the {@code AttackStatus} object associated with this result.
	 * @return The {@code AttackStatus} of the attack.
	 */
	public AttackStatus getAttackStatus()
	{
		return attackStatus;
	}
	
	/**
	 * Retrieves the type of damage for this attack.<br>
	 * This value determines how the hit is processed by the game engine.
	 * @return The {@link HitType} associated with this {@code AttackResult}.
	 */
	public HitType getDamageType()
	{
		return hitType;
	}
	
	/**
	 * Sets the damage type for this attack result.<br>
	 * This updates the {@code hitType} field.
	 * @param type The new {@link HitType} to assign.
	 */
	public void setDamageType(HitType type)
	{
		hitType = type;
	}
	
	/**
	 * shield effects related
	 */
	/**
	 * Retrieves the type of the shield associated with this attack result.<br>
	 * This value is used to determine specific shield behaviors.
	 * @return The {@code int} value representing the shield type.
	 */
	public int getShieldType()
	{
		return shieldType;
	}
	
	/**
	 * Sets the type of the shield.<br>
	 * This method uses a bitwise OR operation to update the {@code shieldType}.
	 * @param shieldType The new shield type value to apply.
	 */
	public void setShieldType(int shieldType)
	{
		this.shieldType |= shieldType;
	}
	
	/**
	 * Retrieves the amount of damage reflected during an attack.<br>
	 * This value is stored in the {@code reflectedDamage} field.
	 * @return The total amount of reflected damage as an {@code int}.
	 */
	public int getReflectedDamage()
	{
		return reflectedDamage;
	}
	
	/**
	 * Sets the amount of damage to be reflected.<br>
	 * This value is used when an attack hits a reflective surface or shield.
	 * @param reflectedDamage The amount of damage to reflect.
	 */
	public void setReflectedDamage(int reflectedDamage)
	{
		this.reflectedDamage = reflectedDamage;
	}
	
	/**
	 * Retrieves the ID of the skill that was reflected.<br>
	 * This value is used to identify which specific skill caused a reflection effect.
	 * @return The {@code int} identifier for the reflected skill.
	 */
	public int getReflectedSkillId()
	{
		return reflectedSkillId;
	}
	
	/**
	 * Sets the unique identifier for the reflected skill.<br>
	 * This value is used to identify which skill caused a reflection effect.
	 * @param skillId The {@code int} ID of the skill to be reflected.
	 */
	public void setReflectedSkillId(int skillId)
	{
		reflectedSkillId = skillId;
	}
	
	/**
	 * Retrieves the unique identifier for the skill providing protection.<br>
	 * This value is used to identify which specific skill triggered a protective effect.
	 * @return The {@code int} ID of the protected skill.
	 */
	public int getProtectedSkillId()
	{
		return protectedSkillId;
	}
	
	/**
	 * Sets the unique identifier for a protected skill.<br>
	 * This value is used to determine which skills are shielded.
	 * @param skillId The {@code int} ID of the protected skill.
	 */
	public void setProtectedSkillId(int skillId)
	{
		protectedSkillId = skillId;
	}
	
	/**
	 * Retrieves the amount of damage that was blocked or mitigated.<br>
	 * This value is used to track protection effects during an attack.
	 * @return The total {@code protectedDamage} as an {@code int}.
	 */
	public int getProtectedDamage()
	{
		return protectedDamage;
	}
	
	/**
	 * Sets the amount of damage that is protected.<br>
	 * This value is used to determine how much damage is blocked by a protector.
	 * @param protectedDamage The amount of damage to be protected.
	 */
	public void setProtectedDamage(int protectedDamage)
	{
		this.protectedDamage = protectedDamage;
	}
	
	/**
	 * Retrieves the unique identifier for the protector.<br>
	 * This value is used to identify which entity provided protection.
	 * @return The {@code int} ID of the protector.
	 */
	public int getProtectorId()
	{
		return protectorId;
	}
	
	/**
	 * Sets the unique identifier for the protector.<br>
	 * This value is used to identify which entity provides protection.
	 * @param protectorId The {@code int} ID of the protector.
	 */
	public void setProtectorId(int protectorId)
	{
		this.protectorId = protectorId;
	}
	
	/**
	 * Checks if the attack should trigger a sub-effect.<br>
	 * This is used to determine if additional effects are launched after an attack.
	 * @return {@code true} if a sub-effect is launched, {@code false} otherwise.
	 */
	public boolean isLaunchSubEffect()
	{
		return launchSubEffect;
	}
	
	/**
	 * Sets whether a sub-effect should be triggered during the attack.<br>
	 * Use {@code true} to enable the effect.<br>
	 * Use {@code false} to disable the effect.
	 * @param launchSubEffect The boolean value determining if the sub-effect launches.
	 */
	public void setLaunchSubEffect(boolean launchSubEffect)
	{
		this.launchSubEffect = launchSubEffect;
	}
	
	/**
	 * Retrieves the amount of MP used by a shield.<br>
	 * This value is part of the {@link AttackResult} data.
	 * @return The current shield MP as an {@code int}.
	 */
	public int getShieldMp()
	{
		return shieldMp;
	}
	
	/**
	 * Sets the amount of MP used for a shield.<br>
	 * This updates the {@code shieldMp} field in this {@link AttackResult} instance.
	 * @param shieldMp The amount of MP to set for the shield.
	 */
	public void setShieldMp(int shieldMp)
	{
		this.shieldMp = shieldMp;
	}
}
