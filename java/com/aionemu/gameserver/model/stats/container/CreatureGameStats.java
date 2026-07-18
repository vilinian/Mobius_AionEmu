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
package com.aionemu.gameserver.model.stats.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.stats.calc.AdditionStat;
import com.aionemu.gameserver.model.stats.calc.ReverseStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.StatCapUtil;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunctionProxy;

/**
 * This class serves as a container for the game statistics of a {@link Creature}.<br>
 * It manages various stats and provides methods to calculate values based on different modifiers.<br>
 * It is designed to be extended by specific creature types to handle unique attribute behaviors.
 * @author xavier
 * @param <T>
 */
public abstract class CreatureGameStats<T extends Creature>
{
	protected static final Logger log = LoggerFactory.getLogger(CreatureGameStats.class);
	private long lastGeoUpdate = 0;
	private final Map<StatEnum, TreeSet<IStatFunction>> stats;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	protected T owner = null;
	private Stat2 cachedHPStat;
	private Stat2 cachedMPStat;
	
	/**
	 * Initializes a new instance of {@code CreatureGameStats}.<br>
	 * This constructor sets the {@code owner} and initializes the internal stats map.
	 * @param owner The {@link Creature} that owns these statistics.
	 */
	protected CreatureGameStats(T owner)
	{
		this.owner = owner;
		stats = new HashMap<>();
	}
	
	/**
	 * Adds a list of stat functions to the internal statistics container.<br>
	 * This method registers new effects without applying them immediately.<br>
	 * It uses {@code StatFunctionProxy} if the provided function is an instance of {@link StatFunction}.
	 * @param statOwner The owner associated with the stat functions.
	 * @param functions The list of {@link IStatFunction} objects to add.
	 */
	public void addEffectOnly(StatOwner statOwner, List<? extends IStatFunction> functions)
	{
		lock.writeLock().lock();
		try
		{
			for (IStatFunction function : functions)
			{
				if (!stats.containsKey(function.getName()))
				{
					stats.put(function.getName(), new TreeSet<>());
				}
				
				IStatFunction func = function;
				if (function instanceof StatFunction)
				{
					func = new StatFunctionProxy(statOwner, function);
				}
				
				addFunction(function.getName(), func);
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Adds a list of status effects to a specific owner.<br>
	 * This method updates the stats and triggers a recalculation.
	 * @param statOwner The {@link StatOwner} who will receive the effects.
	 * @param functions The list of {@link IStatFunction} objects to apply.
	 */
	public void addEffect(StatOwner statOwner, List<? extends IStatFunction> functions)
	{
		addEffectOnly(statOwner, functions);
		onStatsChange();
	}
	
	/**
	 * Removes all active stat effects belonging to a specific owner.<br>
	 * This method clears the functions associated with the provided {@code StatOwner}.<br>
	 * It then triggers a refresh of the statistics via {@code onStatsChange}.
	 * @param statOwner The owner whose effects should be removed.
	 */
	public void endEffect(StatOwner statOwner)
	{
		lock.writeLock().lock();
		try
		{
			for (Map.Entry<StatEnum, TreeSet<IStatFunction>> e : stats.entrySet())
			{
				final TreeSet<IStatFunction> value = e.getValue();
				for (Iterator<IStatFunction> iter = value.iterator(); iter.hasNext();)
				{
					final IStatFunction ownedMod = iter.next();
					if ((ownedMod.getOwner() != null) && ownedMod.getOwner().equals(statOwner))
					{
						iter.remove();
					}
				}
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
		
		onStatsChange();
	}
	
	/**
	 * Retrieves the positive value of a specific statistic.<br>
	 * This method returns the current value if it is greater than {@code 0}.<br>
	 * If the value is zero or negative, it returns {@code 0}.
	 * @param statEnum The type of statistic to retrieve.
	 * @param base The base value for the statistic.
	 * @return The positive integer value of the statistic.
	 */
	public int getPositiveStat(StatEnum statEnum, int base)
	{
		final Stat2 stat = getStat(statEnum, base);
		final int value = stat.getCurrent();
		return value > 0 ? value : 0;
	}
	
	/**
	 * Calculates the positive value of a reverse statistic.<br>
	 * This method retrieves the current value using {@code int)}.<br>
	 * If the result is less than or equal to 0, it returns 0.
	 * @param statEnum The type of statistic to retrieve.
	 * @param base The base value for the statistic.
	 * @return The current value if positive, otherwise 0.
	 */
	public int getPositiveReverseStat(StatEnum statEnum, int base)
	{
		final Stat2 stat = getReverseStat(statEnum, base);
		final int value = stat.getCurrent();
		return value > 0 ? value : 0;
	}
	
	/**
	 * Retrieves the calculated value of a specific statistic.<br>
	 * This method creates an {@link AdditionStat} using the provided base value.<br>
	 * It then returns the final result including all modifiers.
	 * @param statEnum The type of statistic to retrieve.
	 * @param base The base value for the statistic.
	 * @return The calculated {@link Stat2} object.
	 */
	public Stat2 getStat(StatEnum statEnum, int base)
	{
		final Stat2 stat = new AdditionStat(statEnum, base, owner);
		return getStat(statEnum, stat);
	}
	
	/**
	 * Calculates a specific statistic for the creature.<br>
	 * This method creates an {@code AdditionStat} using the provided values.<br>
	 * It then returns the final calculated result.
	 * @param statEnum The type of statistic to retrieve.
	 * @param base The base value of the statistic.
	 * @param bonusRate The multiplier applied to the bonus.
	 * @return The resulting {@code Stat2} object.
	 */
	public Stat2 getStat(StatEnum statEnum, int base, float bonusRate)
	{
		final Stat2 stat = new AdditionStat(statEnum, base, owner, bonusRate);
		return getStat(statEnum, stat);
	}
	
	/**
	 * Calculates the reverse value of a specific statistic.<br>
	 * This method creates a {@link ReverseStat} object based on the provided parameters.<br>
	 * It then uses {@code Stat2)} to retrieve the final result.
	 * @param statEnum The type of statistic to calculate.
	 * @param base The base value for the statistic.
	 * @return The calculated {@code Stat2} object representing the reverse statistic.
	 */
	public Stat2 getReverseStat(StatEnum statEnum, int base)
	{
		final Stat2 stat = new ReverseStat(statEnum, base, owner);
		return getStat(statEnum, stat);
	}
	
	/**
	 * Calculates a reverse statistic for the owner.<br>
	 * This method creates a {@link ReverseStat} object using the provided parameters.<br>
	 * It then retrieves the final value by calling {@code Stat2)}.
	 * @param statEnum The type of statistic to calculate.
	 * @param base The base value for the statistic.
	 * @param bonusRate The multiplier applied to the bonus.
	 * @return The calculated {@link Stat2} result.
	 */
	public Stat2 getReverseStat(StatEnum statEnum, int base, float bonusRate)
	{
		final Stat2 stat = new ReverseStat(statEnum, base, owner, bonusRate);
		return getStat(statEnum, stat);
	}
	
	/**
	 * Retrieves a calculated statistic for a specific type.<br>
	 * This method applies all relevant functions to the provided {@code Stat2} object.<br>
	 * It also handles base value calculations based on whether the owner is a player.
	 * @param statEnum The type of statistic to retrieve.
	 * @param stat The initial {@code Stat2} object to be modified and returned.
	 * @return The final calculated {@code Stat2} object.
	 */
	public Stat2 getStat(StatEnum statEnum, Stat2 stat)
	{
		lock.readLock().lock();
		try
		{
			final TreeSet<IStatFunction> functions = getStatsByStatEnum(statEnum);
			if (functions == null)
			{
				return stat;
			}
			
			for (IStatFunction func : functions)
			{
				if (func.validate(stat, func))
				{
					func.apply(stat);
				}
			}
			
			StatCapUtil.calculateBaseValue(stat, owner.isPlayer());
			return stat;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Calculates the boost for a specific statistic based on items.<br>
	 * This method applies effects from {@link Item} and {@link ManaStone} objects.<br>
	 * It returns the modified {@code Stat2} value after applying all valid boosts.
	 * @param statEnum The type of statistic to calculate.
	 * @param stat The base statistic to be modified.
	 * @return The final {@code Stat2} value after item bonuses are applied.
	 */
	public Stat2 getItemStatBoost(StatEnum statEnum, Stat2 stat)
	{
		lock.readLock().lock();
		try
		{
			final TreeSet<IStatFunction> functions = getStatsByStatEnum(statEnum);
			if ((functions == null) || functions.isEmpty())
			{
				return stat;
			}
			
			for (IStatFunction func : functions)
			{
				if (func.validate(stat, func) && ((func.getOwner() instanceof Item) || (func.getOwner() instanceof ManaStone)))
				{
					func.apply(stat);
				}
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
		
		return stat;
	}
	
	public abstract Stat2 getMaxHp();
	
	public abstract Stat2 getMaxMp();
	
	public abstract Stat2 getAttackSpeed();
	
	public abstract Stat2 getMovementSpeed();
	
	public abstract Stat2 getAttackRange();
	
	public abstract Stat2 getPDef();
	
	public abstract Stat2 getMDef();
	
	public abstract Stat2 getMResist();
	
	public abstract Stat2 getPower();
	
	public abstract Stat2 getHealth();
	
	public abstract Stat2 getAccuracy();
	
	public abstract Stat2 getAgility();
	
	public abstract Stat2 getKnowledge();
	
	public abstract Stat2 getWill();
	
	public abstract Stat2 getEvasion();
	
	public abstract Stat2 getParry();
	
	public abstract Stat2 getBlock();
	
	public abstract Stat2 getMainHandPAttack();
	
	public abstract Stat2 getMainHandPCritical();
	
	public abstract Stat2 getMainHandPAccuracy();
	
	public abstract Stat2 getMAttack();
	
	public abstract Stat2 getMainHandMAttack();
	
	public abstract Stat2 getOffHandMAttack();
	
	public abstract Stat2 getMBoost();
	
	public abstract Stat2 getMBResist();
	
	public abstract Stat2 getMAccuracy();
	
	public abstract Stat2 getMCritical();
	
	public abstract Stat2 getHpRegenRate();
	
	public abstract Stat2 getMpRegenRate();
	
	public abstract Stat2 getStrikeResist();
	
	public abstract Stat2 getStrikeFort();
	
	public abstract Stat2 getSpellResist();
	
	public abstract Stat2 getSpellFort();
	
	public abstract Stat2 getBCastingTime();
	
	public abstract Stat2 getConcentration();
	
	public abstract Stat2 getRootResistance();
	
	public abstract Stat2 getSnareResistance();
	
	public abstract Stat2 getBindResistance();
	
	public abstract Stat2 getFearResistance();
	
	public abstract Stat2 getSleepResistance();
	
	public abstract Stat2 getAllSpeed();
	
	public abstract Stat2 getPvpAttack();
	
	public abstract Stat2 getPvpDeff();
	
	// New 7.x
	public abstract Stat2 getPVPAttack();
	
	public abstract Stat2 getPVPDefense();
	
	public abstract Stat2 getPVEAttack();
	
	public abstract Stat2 getPVEDefense();
	
	/**
	 * Retrieves the current magical defense value for a specific element.<br>
	 * This method maps the {@code SkillElement} to its corresponding resistance stat.<br>
	 * It returns the current value of that stat or 0 if no match is found.
	 * @param element The {@code SkillElement} type to check for defense.
	 * @return The integer value of the magical defense for the given element.
	 */
	public int getMagicalDefenseFor(SkillElement element)
	{
		switch (element)
		{
			case EARTH:
				return getStat(StatEnum.EARTH_RESISTANCE, 0).getCurrent();
			case FIRE:
				return getStat(StatEnum.FIRE_RESISTANCE, 0).getCurrent();
			case WATER:
				return getStat(StatEnum.WATER_RESISTANCE, 0).getCurrent();
			case WIND:
				return getStat(StatEnum.WIND_RESISTANCE, 0).getCurrent();
			case LIGHT:
				return getStat(StatEnum.ELEMENTAL_RESISTANCE_LIGHT, 0).getCurrent();
			case DARK:
				return getStat(StatEnum.ELEMENTAL_RESISTANCE_DARK, 0).getCurrent();
			default:
				return 0;
		}
	}
	
	/**
	 * Retrieves the movement speed as a float value.<br>
	 * This method converts the current speed from an integer to a decimal format.<br>
	 * It divides the result of {@code getMovementSpeed} by {@code 1000f}.
	 * @return The movement speed as a {@code float}.
	 */
	public float getMovementSpeedFloat()
	{
		return getMovementSpeed().getCurrent() / 1000f;
	}
	
	/**
	 * Refreshes the internal statistics for the creature.<br>
	 * This method updates all cached values based on current effects.<br>
	 * It ensures that {@code int)} returns accurate data.
	 */
	public void updateStatInfo()
	{
	}
	
	/**
	 * Updates the movement speed information for the creature.<br>
	 * This method refreshes the internal values used by {@code getMovementSpeedFloat}.
	 */
	public void updateSpeedInfo()
	{
	}
	
	/**
	 * Retrieves the list of stat functions for a specific {@code StatEnum}.<br>
	 * This method filters out lower priority functions if high priority ones exist.
	 * @param stat The {@code StatEnum} type to look up.
	 * @return A {@link TreeSet} containing the filtered {@link IStatFunction} objects, or {@code null} if none exist.
	 */
	public TreeSet<IStatFunction> getStatsByStatEnum(StatEnum stat)
	{
		final TreeSet<IStatFunction> allStats = stats.get(stat);
		if (allStats == null)
		{
			return null;
		}
		
		final TreeSet<IStatFunction> tmp = new TreeSet<>();
		List<IStatFunction> setFuncs = null;
		for (IStatFunction func : allStats)
		{
			if (func.getPriority() >= (Integer.MAX_VALUE - 10))
			{
				if (setFuncs == null)
				{
					setFuncs = new ArrayList<>();
				}
				
				setFuncs.add(func);
			}
			else if (setFuncs != null)
			{
				// all StatSetFunctions added
				break;
			}
		}
		
		if (setFuncs == null)
		{
			tmp.addAll(allStats);
		}
		else
		{
			tmp.addAll(setFuncs);
		}
		
		return tmp;
	}
	
	/**
	 * Adds a new calculation function to a specific statistic.<br>
	 * This method updates the internal collection for the given {@code StatEnum}.
	 * @param stat The type of statistic to modify.
	 * @param function The calculation logic to add.
	 */
	private void addFunction(StatEnum stat, IStatFunction function)
	{
		final TreeSet<IStatFunction> allStats = stats.get(stat);
		allStats.add(function);
	}
	
	/**
	 * Checks if the geographic stats need to be updated.<br>
	 * This method verifies if {@code 600} milliseconds have passed since the last update.<br>
	 * If enough time has passed, it updates the internal timestamp and returns {@code true}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if an update occurred, or {@code false} otherwise.
	 */
	public boolean checkGeoNeedUpdate()
	{
		final long currentTime = System.currentTimeMillis();
		if ((currentTime - lastGeoUpdate) > 600)
		{
			lastGeoUpdate = currentTime;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Updates the cached values for HP and MP stats.<br>
	 * This method calls {@code checkHPStats} and {@code checkMPStats}.
	 */
	protected void onStatsChange()
	{
		checkHPStats();
		checkMPStats();
	}
	
	/**
	 * This method updates the cached health points for the owner.<br>
	 * It synchronizes the current HP of the {@link Creature} if a change is detected.<br>
	 * The calculation ensures that the new HP value scales proportionally with the maximum HP.
	 */
	private void checkHPStats()
	{
		final Stat2 oldHP = cachedHPStat;
		cachedHPStat = null;
		final Stat2 newHP = this.getMaxHp();
		cachedHPStat = newHP;
		if (oldHP == null)
		{
			return;
		}
		
		if (oldHP.getCurrent() != newHP.getCurrent())
		{
			final float percent = (1f * newHP.getCurrent()) / oldHP.getCurrent();
			owner.getLifeStats().setCurrentHp(Math.round(owner.getLifeStats().getCurrentHp() * percent));
		}
	}
	
	/**
	 * Updates the cached maximum MP value.<br>
	 * Checks if the current MP needs to be adjusted based on changes to the max MP.<br>
	 * This ensures that the player's current MP scales proportionally when their maximum capacity changes.
	 */
	private void checkMPStats()
	{
		final Stat2 oldMP = cachedMPStat;
		cachedMPStat = null;
		final Stat2 newMP = this.getMaxMp();
		cachedMPStat = newMP;
		if (oldMP == null)
		{
			return;
		}
		
		if (oldMP.getCurrent() != newMP.getCurrent())
		{
			final float percent = (1f * newMP.getCurrent()) / oldMP.getCurrent();
			owner.getLifeStats().setCurrentMp(Math.round(owner.getLifeStats().getCurrentMp() * percent));
		}
	}
}
