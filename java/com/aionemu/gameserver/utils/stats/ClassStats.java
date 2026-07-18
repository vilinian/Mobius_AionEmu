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
package com.aionemu.gameserver.utils.stats;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.utils.stats.enums.ACCURACY;
import com.aionemu.gameserver.utils.stats.enums.AGILITY;
import com.aionemu.gameserver.utils.stats.enums.ATTACK_RANGE;
import com.aionemu.gameserver.utils.stats.enums.ATTACK_SPEED;
import com.aionemu.gameserver.utils.stats.enums.BLOCK;
import com.aionemu.gameserver.utils.stats.enums.CRIT_SPELL;
import com.aionemu.gameserver.utils.stats.enums.EARTH_RESIST;
import com.aionemu.gameserver.utils.stats.enums.EVASION;
import com.aionemu.gameserver.utils.stats.enums.FIRE_RESIST;
import com.aionemu.gameserver.utils.stats.enums.FLY_SPEED;
import com.aionemu.gameserver.utils.stats.enums.HEALTH;
import com.aionemu.gameserver.utils.stats.enums.KNOWLEDGE;
import com.aionemu.gameserver.utils.stats.enums.MAGICAL_OFF_HAND_ATTACK;
import com.aionemu.gameserver.utils.stats.enums.MAGIC_ACCURACY;
import com.aionemu.gameserver.utils.stats.enums.MAIN_HAND_ACCURACY;
import com.aionemu.gameserver.utils.stats.enums.MAIN_HAND_ATTACK;
import com.aionemu.gameserver.utils.stats.enums.MAIN_HAND_CRITRATE;
import com.aionemu.gameserver.utils.stats.enums.MAXHP;
import com.aionemu.gameserver.utils.stats.enums.PARRY;
import com.aionemu.gameserver.utils.stats.enums.POWER;
import com.aionemu.gameserver.utils.stats.enums.SPEED;
import com.aionemu.gameserver.utils.stats.enums.SPELL_RESIST;
import com.aionemu.gameserver.utils.stats.enums.STRIKE_RESIST;
import com.aionemu.gameserver.utils.stats.enums.WATER_RESIST;
import com.aionemu.gameserver.utils.stats.enums.WILL;
import com.aionemu.gameserver.utils.stats.enums.WIND_RESIST;

/**
 * This class manages the base statistics for different character classes.<br>
 * It provides a centralized way to access attributes like {@code HEALTH} and {@code POWER}.<br>
 * You can use it to retrieve specific values associated with a {@link PlayerClass}.
 * @author ATracer
 */
public class ClassStats
{
	/**
	 * Calculates the maximum health for a specific character class.<br>
	 * This method uses the {@link PlayerClass} type to determine the correct stats.<br>
	 * It returns the value based on the provided {@code level}.
	 * @param playerClass The class of the player.
	 * @param level The current level of the player.
	 * @return The maximum health as an {@code int}.
	 */
	public static int getMaxHpFor(PlayerClass playerClass, int level)
	{
		return MAXHP.valueOf(playerClass.toString()).getMaxHpFor(level);
	}
	
	/**
	 * Retrieves the power value for a specific character class.<br>
	 * This method looks up the {@code POWER} constant based on the provided {@link PlayerClass}.
	 * @param playerClass The class of the player to check.
	 * @return The integer power value associated with the class.
	 */
	public static int getPowerFor(PlayerClass playerClass)
	{
		return POWER.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the base health value for a specific character class.<br>
	 * This method looks up the {@code HEALTH} constant based on the provided {@link PlayerClass}.
	 * @param playerClass The {@code PlayerClass} to check.
	 * @return The integer health value associated with the class.
	 */
	public static int getHealthFor(PlayerClass playerClass)
	{
		return HEALTH.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the agility value for a specific character class.<br>
	 * This method looks up the {@code AGILITY} constant based on the provided {@link PlayerClass}.
	 * @param playerClass The {@code PlayerClass} to check.
	 * @return The integer value of the agility stat.
	 */
	public static int getAgilityFor(PlayerClass playerClass)
	{
		return AGILITY.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the accuracy value for a specific character class.<br>
	 * This method looks up the {@code ACCURACY} constant based on the provided {@link PlayerClass}.
	 * @param playerClass The type of player class to check.
	 * @return The integer accuracy value associated with the class.
	 */
	public static int getAccuracyFor(PlayerClass playerClass)
	{
		return ACCURACY.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the knowledge value for a specific character class.<br>
	 * This method looks up the {@code KNOWLEDGE} constant based on the provided {@link PlayerClass}.
	 * @param playerClass The type of player class to check.
	 * @return The integer value associated with the class's knowledge.
	 */
	public static int getKnowledgeFor(PlayerClass playerClass)
	{
		return KNOWLEDGE.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the {@code WILL} stat value for a specific class.<br>
	 * This method looks up the value based on the {@link PlayerClass} name.
	 * @param playerClass The {@code PlayerClass} to check.
	 * @return The integer value of the {@code WILL} stat.
	 */
	public static int getWillFor(PlayerClass playerClass)
	{
		return WILL.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the main hand attack value for a specific class.<br>
	 * This method looks up the value based on the {@link PlayerClass} name.
	 * @param playerClass The class of the player to check.
	 * @return The integer value of the main hand attack.
	 */
	public static int getMainHandAttackFor(PlayerClass playerClass)
	{
		return MAIN_HAND_ATTACK.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the main hand critical rate for a specific class.<br>
	 * This method looks up the value from the {@code MAIN_HAND_CRITRATE} enum.
	 * @param playerClass The {@link PlayerClass} to check.
	 * @return The integer value of the critical rate.
	 */
	public static int getMainHandCritRateFor(PlayerClass playerClass)
	{
		return MAIN_HAND_CRITRATE.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the main hand accuracy value for a specific character class.<br>
	 * This method looks up the value based on the {@link PlayerClass} type.
	 * @param playerClass The {@code PlayerClass} to check.
	 * @return The integer value of the main hand accuracy.
	 */
	public static int getMainHandAccuracyFor(PlayerClass playerClass)
	{
		return MAIN_HAND_ACCURACY.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the water resistance value for a specific class.<br>
	 * This method looks up the {@code WATER_RESIST} value based on the provided {@link PlayerClass}.
	 * @param playerClass The class of the player to check.
	 * @return The integer value representing water resistance.
	 */
	public static int getWaterResistFor(PlayerClass playerClass)
	{
		return WATER_RESIST.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the wind resistance value for a specific class.<br>
	 * This method looks up the {@code WIND_RESIST} value based on the provided {@link PlayerClass}.
	 * @param playerClass The class of the player to check.
	 * @return The integer value of the wind resistance.
	 */
	public static int getWindResistFor(PlayerClass playerClass)
	{
		return WIND_RESIST.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the earth resistance value for a specific class.<br>
	 * This method looks up the {@code EARTH_RESIST} value based on the provided {@link PlayerClass}.
	 * @param playerClass The class of the player to check.
	 * @return The integer value of the earth resistance.
	 */
	public static int getEarthResistFor(PlayerClass playerClass)
	{
		return EARTH_RESIST.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the fire resistance value for a specific character class.<br>
	 * This method looks up the value from the {@link com.aionemu.gameserver.utils.stats.enums.FIRE_RESIST} enum.
	 * @param playerClass The {@code PlayerClass} object used to determine the resistance.
	 * @return The integer value of the fire resistance for the given class.
	 */
	public static int getFireResistFor(PlayerClass playerClass)
	{
		return FIRE_RESIST.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the magic accuracy value for a specific class.<br>
	 * This method looks up the value in the {@link com.aionemu.gameserver.utils.stats.enums.MAGIC_ACCURACY} enum.
	 * @param playerClass The {@code PlayerClass} to check.
	 * @return The magic accuracy as an {@code int}.
	 */
	public static int getMagicAccuracyFor(PlayerClass playerClass)
	{
		return MAGIC_ACCURACY.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the magical off-hand attack value for a specific class.<br>
	 * This method looks up the value based on the {@link PlayerClass} type.
	 * @param playerClass The class of the player to check.
	 * @return The integer value of the magical off-hand attack.
	 */
	public static int getMagicalOffHandAttackFor(PlayerClass playerClass)
	{
		return MAGICAL_OFF_HAND_ATTACK.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the critical spell value for a specific character class.<br>
	 * This method looks up the value based on the {@link PlayerClass} name.
	 * @param playerClass The type of class to check.
	 * @return The integer value associated with the critical spell for that class.
	 */
	public static int getCritSpellFor(PlayerClass playerClass)
	{
		return CRIT_SPELL.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the spell resistance value for a specific character class.<br>
	 * This method looks up the {@code SPELL_RESIST} value based on the provided {@link PlayerClass}.
	 * @param playerClass The {@code PlayerClass} to check for resistance stats.
	 * @return The integer value of the spell resistance for the given class.
	 */
	public static int getSpellResistFor(PlayerClass playerClass)
	{
		return SPELL_RESIST.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the strike resistance value for a specific class.<br>
	 * This method looks up the {@code STRIKE_RESIST} value based on the provided {@link PlayerClass}.
	 * @param playerClass The class of the player to check.
	 * @return The integer value of the strike resistance.
	 */
	public static int getStrikeResistFor(PlayerClass playerClass)
	{
		return STRIKE_RESIST.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the evasion value for a specific character class.<br>
	 * This method looks up the {@code EVASION} constant based on the provided {@link PlayerClass}.
	 * @param playerClass The class of the player to check.
	 * @return The integer evasion value associated with the class.
	 */
	public static int getEvasionFor(PlayerClass playerClass)
	{
		return EVASION.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the block value for a specific character class.<br>
	 * This method looks up the {@code BLOCK} constant based on the {@link PlayerClass}.
	 * @param playerClass The {@code PlayerClass} to check.
	 * @return The integer value of the block stat.
	 */
	public static int getBlockFor(PlayerClass playerClass)
	{
		return BLOCK.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the parry value for a specific character class.<br>
	 * This method looks up the {@code PARRY} constant based on the provided {@link PlayerClass}.
	 * @param playerClass The class of the player to check.
	 * @return The integer parry value associated with the class.
	 */
	public static int getParryFor(PlayerClass playerClass)
	{
		return PARRY.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the attack range for a specific character class.<br>
	 * This method looks up the value from the {@link com.aionemu.gameserver.utils.stats.enums.ATTACK_RANGE} enum.
	 * @param playerClass The {@code PlayerClass} to check.
	 * @return The integer value of the attack range.
	 */
	public static int getAttackRangeFor(PlayerClass playerClass)
	{
		return ATTACK_RANGE.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the attack speed value for a specific character class.<br>
	 * This method looks up the value in the {@link com.aionemu.gameserver.utils.stats.enums.ATTACK_SPEED} enum.
	 * @param playerClass The {@code PlayerClass} to check.
	 * @return The integer value of the attack speed.
	 */
	public static int getAttackSpeedFor(PlayerClass playerClass)
	{
		return ATTACK_SPEED.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the fly speed value for a specific character class.<br>
	 * This method looks up the {@code FLY_SPEED} based on the provided {@link PlayerClass}.
	 * @param playerClass The {@code PlayerClass} to check.
	 * @return The integer value of the fly speed.
	 */
	public static int getFlySpeedFor(PlayerClass playerClass)
	{
		return FLY_SPEED.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * Retrieves the movement speed for a specific character class.<br>
	 * This method looks up the value in the {@code SPEED} enum based on the provided {@link PlayerClass}.
	 * @param playerClass The class of the player to check.
	 * @return The integer value representing the speed for that class.
	 */
	public static int getSpeedFor(PlayerClass playerClass)
	{
		return SPEED.valueOf(playerClass.toString()).getValue();
	}
}
