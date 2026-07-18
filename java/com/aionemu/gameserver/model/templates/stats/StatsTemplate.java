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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.PlayerClass;

/**
 * This class serves as a data container for {@code Stats}.<br>
 * It defines the base structure for various statistics within the game.<br>
 * Use this class to manage and store template-based stat information.
 * @author Aquanox, Dr2co
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "stats_template")
public abstract class StatsTemplate
{
	@XmlAttribute(name = "maxHp")
	private int maxHp;
	@XmlAttribute(name = "maxMp")
	private int maxMp;
	@XmlAttribute(name = "evasion")
	private int evasion;
	@XmlAttribute(name = "block")
	private int block;
	@XmlAttribute(name = "parry")
	private int parry;
	@XmlAttribute(name = "main_hand_attack")
	private int mainHandAttack;
	@XmlAttribute(name = "main_hand_accuracy")
	private int mainHandAccuracy;
	@XmlAttribute(name = "main_hand_crit_rate")
	private int mainHandCritRate;
	@XmlAttribute(name = "magic_accuracy")
	private int magicAccuracy;
	@XmlAttribute(name = "crit_spell")
	private int critSpell;
	@XmlAttribute(name = "strike_resist")
	private int strikeResist;
	@XmlAttribute(name = "spell_resist")
	private int spellResist;
	@XmlElement
	protected CreatureSpeeds speeds;
	
	/* ======================================= */
	/**
	 * Retrieves the maximum health points.<br>
	 * This value is stored in the {@code maxHp} field.
	 * @return The maximum health points as an {@code int}.
	 */
	public int getMaxHp()
	{
		return maxHp;
	}
	
	/**
	 * Sets the maximum health points for this template.<br>
	 * This updates the {@code maxHp} field.
	 * @param maxHp The new value for maximum health.
	 */
	public void setMaxHp(int maxHp)
	{
		this.maxHp = maxHp;
	}
	
	/**
	 * Retrieves the maximum MP of the template.<br>
	 * This value represents the upper limit of mana points for the creature.
	 * @return The current value of the maximum MP as an {@code int}.
	 */
	public int getMaxMp()
	{
		return maxMp;
	}
	
	/**
	 * Sets the maximum mana points for the template.<br>
	 * This updates the {@code maxMp} field.
	 * @param maxMp The new value for maximum mana points.
	 */
	public void setMaxMp(int maxMp)
	{
		this.maxMp = maxMp;
	}
	
	/* ======================================= */
	/**
	 * Retrieves the walking speed of the creature.<br>
	 * This value is used to determine how fast the creature moves while walking.
	 * @return The {@code float} value representing the walk speed, or {@code 0} if speeds are null.
	 */
	public float getWalkSpeed()
	{
		return speeds == null ? 0 : speeds.getWalkSpeed();
	}
	
	/**
	 * Retrieves the current running speed of the creature.<br>
	 * This value is used to determine how fast the creature moves while running.
	 * @return The {@code float} value representing the run speed.
	 */
	public float getRunSpeed()
	{
		return speeds == null ? 0 : speeds.getRunSpeed();
	}
	
	/**
	 * Gets the walking speed for a creature in a group.<br>
	 * This value is used when multiple creatures move together.
	 * @return the {@code float} value of the group walk speed.
	 */
	public float getGroupWalkSpeed()
	{
		return getWalkSpeed();
	}
	
	/**
	 * Gets the movement speed of a creature during combat.<br>
	 * This value is used when the creature is running while fighting.
	 * @return The {@code float} value representing the fight run speed.
	 */
	public float getRunSpeedFight()
	{
		return getRunSpeed();
	}
	
	/**
	 * Gets the speed of a creature when running in a group during combat.<br>
	 * This value is used to calculate movement velocity for fighting mobs.
	 * @return The {@code float} value of the group run fight speed.
	 */
	public float getGroupRunSpeedFight()
	{
		return getRunSpeed();
	}
	
	/**
	 * Retrieves the current flying speed of the ride.<br>
	 * This value is used to determine how fast a player moves while in flight.
	 * @return The {@code float} value representing the flying speed.
	 */
	public float getFlySpeed()
	{
		return speeds == null ? 0 : speeds.getFlySpeed();
	}
	
	/**
	 * Sets the walking speed for a creature.<br>
	 * This method updates the {@code speeds} field with the provided value.
	 * @param walkSpeed The new {@link CreatureSpeeds} object to assign.
	 */
	public void setWalkSpeed(CreatureSpeeds walkSpeed)
	{
		speeds = walkSpeed;
	}
	
	/**
	 * Sets the running speed for a creature.<br>
	 * This method updates the {@code speeds} field with the provided value.
	 * @param runSpeed The new {@link CreatureSpeeds} to apply.
	 */
	public void setRunSpeed(CreatureSpeeds runSpeed)
	{
		speeds = runSpeed;
	}
	
	/* ======================================= */
	/**
	 * Retrieves the current evasion value.<br>
	 * This value represents the amount of evasion assigned to this template.
	 * @return The evasion value as an {@code int}.
	 */
	public int getEvasion()
	{
		return evasion;
	}
	
	/**
	 * Sets the evasion value for this template.<br>
	 * This updates the internal {@code evasion} field.
	 * @param evasion The new evasion value to set.
	 */
	public void setEvasion(int evasion)
	{
		this.evasion = evasion;
	}
	
	/**
	 * Retrieves the current block value.<br>
	 * This value is stored in the {@code block} field.
	 * @return The integer value of the block stat.
	 */
	public int getBlock()
	{
		return block;
	}
	
	/**
	 * Sets the block value for this template.<br>
	 * This updates the {@code block} field with a new integer value.
	 * @param block The new block value to set.
	 */
	public void setBlock(int block)
	{
		this.block = block;
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
	 * Sets the parry value for this template.<br>
	 * This updates the {@code parry} field used in combat calculations.
	 * @param parry The new parry value to assign.
	 */
	public void setParry(int parry)
	{
		this.parry = parry;
	}
	
	/**
	 * Retrieves the current strike resistance value.<br>
	 * This value is stored in the {@code strikeResist} field.
	 * @return The strike resistance as an {@code int}.
	 */
	public int getStrikeResist()
	{
		return strikeResist;
	}
	
	/**
	 * Sets the strike resistance value for this template.<br>
	 * This value determines how much physical damage is reduced.
	 * @param resist The new {@code int} value for strike resistance.
	 */
	public void setStrikeResist(int resist)
	{
		strikeResist = resist;
	}
	
	/**
	 * Retrieves the current spell resistance value.<br>
	 * This value represents how much magic damage is reduced.
	 * @return The integer value of the spell resistance.
	 */
	public int getSpellResist()
	{
		return spellResist;
	}
	
	/**
	 * Sets the spell resistance value for this template.<br>
	 * This updates the {@code spellResist} field.
	 * @param resist The new integer value for spell resistance.
	 */
	public void setSpellResist(int resist)
	{
		spellResist = resist;
	}
	
	/* ======================================= */
	/**
	 * Retrieves the main hand attack value.<br>
	 * This returns the {@code int} value stored in the template.
	 * @return The integer value of the main hand attack.
	 */
	public int getMainHandAttack()
	{
		return mainHandAttack;
	}
	
	/**
	 * Retrieves the main hand accuracy for a specific player class.<br>
	 * This value is determined by the {@link PlayerClass}.
	 * @return The main hand accuracy as an {@code int}.
	 */
	public int getMainHandAccuracy()
	{
		return mainHandAccuracy;
	}
	
	/**
	 * Retrieves the critical hit rate for the main hand weapon.<br>
	 * This value is determined based on the player's class.
	 * @return The calculated main hand critical hit rate as an {@code int}.
	 */
	public int getMainHandCritRate()
	{
		return mainHandCritRate;
	}
	
	/* ======================================= */
	/**
	 * Retrieves the magic accuracy value.<br>
	 * This value represents the character's ability to land magic attacks.
	 * @return The magic accuracy as an {@code int}.
	 */
	public int getMagicAccuracy()
	{
		return magicAccuracy;
	}
	
	/**
	 * Retrieves the magic critical hit rate.<br>
	 * This value corresponds to the {@code crit_spell} attribute.
	 * @return the current magic critical hit rate as an {@code int}.
	 */
	public int getMCritical()
	{
		return critSpell;
	}
}
