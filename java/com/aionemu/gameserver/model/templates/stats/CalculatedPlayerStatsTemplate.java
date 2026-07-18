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

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.utils.stats.ClassStats;

/**
 * This class provides a template for calculating dynamic player statistics.<br>
 * It handles the logic to derive specific values based on {@link PlayerClass} data.<br>
 * Use this class when stats need to be computed rather than stored as static values.
 * @author ATracer
 */
public class CalculatedPlayerStatsTemplate extends PlayerStatsTemplate
{
	private final PlayerClass playerClass;
	
	/**
	 * Creates a new instance of {@code CalculatedPlayerStatsTemplate}.<br>
	 * This constructor initializes the template using a specific {@link PlayerClass}.
	 * @param playerClass The {@code PlayerClass} used to calculate the stats.
	 */
	public CalculatedPlayerStatsTemplate(PlayerClass playerClass)
	{
		this.playerClass = playerClass;
	}
	
	/**
	 * Retrieves the accuracy value for the player's class.<br>
	 * This method uses {@code getAccuracyFor} to find the correct value.
	 * @return The accuracy integer associated with the current {@code PlayerClass}.
	 */
	@Override
	public int getAccuracy()
	{
		return ClassStats.getAccuracyFor(playerClass);
	}
	
	/**
	 * Retrieves the agility value for the current player class.<br>
	 * This method uses {@link ClassStats} to calculate the result.
	 * @return The agility integer value.
	 */
	@Override
	public int getAgility()
	{
		return ClassStats.getAgilityFor(playerClass);
	}
	
	/**
	 * Retrieves the health value for the player's class.<br>
	 * This method uses {@code getHealthFor} to find the correct value.
	 * @return The current health as an {@code int}.
	 */
	@Override
	public int getHealth()
	{
		return ClassStats.getHealthFor(playerClass);
	}
	
	/**
	 * Retrieves the knowledge value for the current player class.<br>
	 * This method uses {@link ClassStats} to find the correct value.
	 * @return The knowledge value as an {@code int}.
	 */
	@Override
	public int getKnowledge()
	{
		return ClassStats.getKnowledgeFor(playerClass);
	}
	
	/**
	 * Retrieves the power value for the current player class.<br>
	 * This method uses {@code getPowerFor} to calculate the result.
	 * @return The integer power value.
	 */
	@Override
	public int getPower()
	{
		return ClassStats.getPowerFor(playerClass);
	}
	
	/**
	 * Retrieves the Will stat for the current player class.<br>
	 * This value is fetched from the {@link ClassStats} utility.
	 * @return The integer value of the Will stat.
	 */
	@Override
	public int getWill()
	{
		return ClassStats.getWillFor(playerClass);
	}
	
	/**
	 * Retrieves the block value for the current player class.<br>
	 * This method uses {@code getBlockFor} to find the correct value.
	 * @return The integer value of the block stat.
	 */
	@Override
	public int getBlock()
	{
		return ClassStats.getBlockFor(playerClass);
	}
	
	/**
	 * Retrieves the critical spell rate for a specific player class.<br>
	 * This value is fetched from the {@link ClassStats} utility.
	 * @return The integer value of the critical spell rate.
	 */
	public int getCritSpell()
	{
		return ClassStats.getCritSpellFor(playerClass);
	}
	
	/**
	 * Retrieves the evasion value for the current player class.<br>
	 * This method uses {@code getEvasionFor} to find the correct stat.
	 * @return The total evasion amount as an {@code int}.
	 */
	@Override
	public int getEvasion()
	{
		return ClassStats.getEvasionFor(playerClass);
	}
	
	/**
	 * Retrieves the current flying speed of the ride.<br>
	 * This value is used to determine how fast a player moves while in flight.
	 * @return The {@code float} value representing the flying speed.
	 */
	@Override
	public float getFlySpeed()
	{
		// TODO Auto-generated method stub
		return ClassStats.getFlySpeedFor(playerClass);
	}
	
	/**
	 * Retrieves the magic accuracy value for the current player class.<br>
	 * This method uses {@code getMagicAccuracyFor} to find the correct value.
	 * @return The magic accuracy as an {@code int}.
	 */
	@Override
	public int getMagicAccuracy()
	{
		return ClassStats.getMagicAccuracyFor(playerClass);
	}
	
	/**
	 * Retrieves the main hand accuracy for a specific player class.<br>
	 * This value is determined by the {@link PlayerClass}.
	 * @return The main hand accuracy as an {@code int}.
	 */
	@Override
	public int getMainHandAccuracy()
	{
		return ClassStats.getMainHandAccuracyFor(playerClass);
	}
	
	/**
	 * Retrieves the main hand attack value for the player's class.<br>
	 * This method uses {@code getMainHandAttackFor} to calculate the result.
	 * @return The integer value of the main hand attack.
	 */
	@Override
	public int getMainHandAttack()
	{
		return ClassStats.getMainHandAttackFor(playerClass);
	}
	
	/**
	 * Retrieves the critical hit rate for the main hand weapon.<br>
	 * This value is determined based on the player's class.
	 * @return The calculated main hand critical hit rate as an {@code int}.
	 */
	@Override
	public int getMainHandCritRate()
	{
		return ClassStats.getMainHandCritRateFor(playerClass);
	}
	
	/**
	 * Retrieves the maximum health points for the player class.<br>
	 * This method uses a hardcoded level of {@code 10}.<br>
	 * It calls the {@code int)} method.
	 * @return The maximum health points as an {@code int}.
	 */
	@Override
	public int getMaxHp()
	{
		return ClassStats.getMaxHpFor(playerClass, 10); // level is hardcoded
	}
	
	/**
	 * Retrieves the maximum MP of the owner.<br>
	 * This method calls {@code getOwner} to access the game stats.
	 * @return The current value of the maximum MP as an {@code int}.
	 */
	@Override
	public int getMaxMp()
	{
		return 1000;
	}
	
	/**
	 * Retrieves the parry value for the player's class.<br>
	 * This value represents the chance to deflect an incoming attack.
	 * @return The current {@code int} parry value.
	 */
	@Override
	public int getParry()
	{
		return ClassStats.getParryFor(playerClass);
	}
	
	/**
	 * Retrieves the running speed of the player.<br>
	 * This value is determined based on the {@link PlayerClass}.
	 * @return The {@code float} value representing the run speed.
	 */
	@Override
	public float getRunSpeed()
	{
		return ClassStats.getSpeedFor(playerClass);
	}
	
	/**
	 * Retrieves the spell resistance value for the current player class.<br>
	 * This method uses {@code getSpellResistFor} to find the correct value.
	 * @return The integer value of the spell resistance.
	 */
	@Override
	public int getSpellResist()
	{
		return ClassStats.getSpellResistFor(playerClass);
	}
	
	/**
	 * Retrieves the strike resistance value for the player's class.<br>
	 * This method uses {@link ClassStats} to calculate the specific value.
	 * @return The strike resistance as an {@code int}.
	 */
	@Override
	public int getStrikeResist()
	{
		return ClassStats.getStrikeResistFor(playerClass);
	}
	
	/**
	 * Retrieves the walking speed of the player.<br>
	 * This value determines how fast the character moves while walking.
	 * @return The {@code float} value representing the walk speed.
	 */
	@Override
	public float getWalkSpeed()
	{
		return 1.5f;
	}
}
