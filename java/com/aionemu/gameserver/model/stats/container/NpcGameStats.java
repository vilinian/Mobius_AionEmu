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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.SummonedObject;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class represents the game statistics for an {@link Npc} entity.<br>
 * It extends {@link CreatureGameStats} to provide specific data handling for non-player characters.
 * @author xavier
 */
public class NpcGameStats extends CreatureGameStats<Npc>
{
	int currentRunSpeed = 0;
	private long lastAttackTime = 0;
	private long lastAttackedTime = 0;
	private long nextAttackTime = 0;
	private long lastSkillTime = 0;
	private long fightStartingTime = 0;
	private int cachedState;
	private AISubState cachedSubState;
	private Stat2 cachedSpeedStat;
	private long lastGeoZUpdate;
	private long lastChangeTarget = 0;
	private int pAccuracy = 0;
	private int mRes = 0;
	
	/**
	 * Creates a new {@code NpcGameStats} instance for a specific NPC.<br>
	 * This constructor initializes the stats container using the provided {@link Npc} owner.
	 * @param owner The {@code Npc} object that owns these game statistics.
	 */
	public NpcGameStats(Npc owner)
	{
		super(owner);
	}
	
	/**
	 * Updates the cached values for speed stats.<br>
	 * This method calls {@code checkSpeedStats}.
	 */
	@Override
	protected void onStatsChange()
	{
		checkSpeedStats();
	}
	
	/**
	 * Updates the cached movement speed for the NPC.<br>
	 * This method checks if the current speed has changed since the last update.<br>
	 * If a change is detected, it broadcasts an update to all relevant clients.
	 */
	private void checkSpeedStats()
	{
		final Stat2 oldSpeed = cachedSpeedStat;
		cachedSpeedStat = null;
		final Stat2 newSpeed = getMovementSpeed();
		cachedSpeedStat = newSpeed;
		if ((oldSpeed == null) || (oldSpeed.getCurrent() != newSpeed.getCurrent()))
		{
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
	}
	
	/**
	 * Retrieves the maximum health point value for the NPC.<br>
	 * This method fetches the stat from the {@code NpcGameStats} instance.
	 * @return The {@code Stat2} object representing the maximum HP.
	 */
	@Override
	public Stat2 getMaxHp()
	{
		return getStat(StatEnum.MAXHP, owner.getObjectTemplate().getStatsTemplate().getMaxHp());
	}
	
	/**
	 * Retrieves the maximum mana points for the NPC.<br>
	 * This method calculates the value based on the current stats template.
	 * @return The {@code Stat2} object containing the maximum MP.
	 */
	@Override
	public Stat2 getMaxMp()
	{
		return getStat(StatEnum.MAXMP, owner.getObjectTemplate().getStatsTemplate().getMaxMp());
	}
	
	/**
	 * Retrieves the current attack speed of the NPC.<br>
	 * This method calculates the value based on the {@code StatEnum.ATTACK_SPEED} and the template delay.
	 * @return The {@code Stat2} object containing the attack speed.
	 */
	@Override
	public Stat2 getAttackSpeed()
	{
		return getStat(StatEnum.ATTACK_SPEED, owner.getObjectTemplate().getAttackDelay());
	}
	
	/**
	 * Retrieves the physical critical resistance stat.<br>
	 * This value determines how well an entity resists critical hits.
	 * @return The {@code Stat2} object containing the strike resistance value.
	 */
	@Override
	public Stat2 getStrikeResist()
	{
		return getStat(StatEnum.PHYSICAL_CRITICAL_RESIST, 0);
	}
	
	/**
	 * Retrieves the physical critical damage reduction stat.<br>
	 * This value represents how much damage from critical hits is reduced.
	 * @return the {@code Stat2} object containing the physical critical damage reduction.
	 */
	@Override
	public Stat2 getStrikeFort()
	{
		return getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, 0);
	}
	
	/**
	 * Retrieves the magical critical resistance stat.<br>
	 * This method returns a {@link Stat2} object representing the value.
	 * @return The current magical critical resistance as a {@code Stat2}.
	 */
	@Override
	public Stat2 getSpellResist()
	{
		return getStat(StatEnum.MAGICAL_CRITICAL_RESIST, 0);
	}
	
	/**
	 * Retrieves the magical critical damage reduction stat.<br>
	 * This value is used to determine how much magic critical hits are reduced.
	 * @return the {@code Stat2} object representing the spell fort.
	 */
	@Override
	public Stat2 getSpellFort()
	{
		return getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, 0);
	}
	
	/**
	 * Retrieves the casting time stat for the NPC.<br>
	 * This method returns a {@code Stat2} object representing the boost to casting speed.<br>
	 * It uses a default value of {@code 1000} if no specific value is found.
	 * @return The {@code Stat2} object containing the casting time information.
	 */
	@Override
	public Stat2 getBCastingTime()
	{
		return getStat(StatEnum.BOOST_CASTING_TIME, 1000);
	}
	
	/**
	 * Retrieves the concentration statistic for this NPC.<br>
	 * This method calls {@code int)} to fetch the value.
	 * @return the {@code Stat2} object representing the concentration stat.
	 */
	@Override
	public Stat2 getConcentration()
	{
		return getStat(StatEnum.CONCENTRATION, 0);
	}
	
	/**
	 * Retrieves the root resistance stat for this NPC.<br>
	 * This value determines how well the entity resists being rooted.
	 * @return a {@code Stat2} object containing the root resistance value.
	 */
	@Override
	public Stat2 getRootResistance()
	{
		return getStat(StatEnum.ROOT_RESISTANCE, 0);
	}
	
	/**
	 * Retrieves the snare resistance value for the NPC.<br>
	 * This method returns a {@link Stat2} object representing the current resistance.
	 * @return The {@code Stat2} value of the snare resistance.
	 */
	@Override
	public Stat2 getSnareResistance()
	{
		return getStat(StatEnum.SNARE_RESISTANCE, 0);
	}
	
	/**
	 * Retrieves the bind resistance stat for this NPC.<br>
	 * This value is used to determine how well the entity resists being bound.
	 * @return the {@code Stat2} object containing the bind resistance value.
	 */
	@Override
	public Stat2 getBindResistance()
	{
		return getStat(StatEnum.BIND_RESISTANCE, 0);
	}
	
	/**
	 * Retrieves the fear resistance stat for the NPC.<br>
	 * This method returns a {@link Stat2} object representing the current value.<br>
	 * It uses the {@code FEAR_RESISTANCE} enum to fetch the data.
	 * @return The {@code Stat2} object containing the fear resistance value.
	 */
	@Override
	public Stat2 getFearResistance()
	{
		return getStat(StatEnum.FEAR_RESISTANCE, 0);
	}
	
	/**
	 * Retrieves the sleep resistance stat for the NPC.<br>
	 * This method returns a {@code Stat2} object representing the value.<br>
	 * It uses the {@code SLEEP_RESISTANCE} enum to fetch the data.
	 * @return The {@code Stat2} object containing the sleep resistance value.
	 */
	@Override
	public Stat2 getSleepResistance()
	{
		return getStat(StatEnum.SLEEP_RESISTANCE, 0);
	}
	
	/**
	 * Retrieves the total speed statistic for the NPC.<br>
	 * This method calculates the {@code ALLSPEED} stat using a base value of {@code 7500}.
	 * @return The {@link Stat2} object containing the all speed value.
	 */
	@Override
	public Stat2 getAllSpeed()
	{
		final int base = 7500; // TODO current value
		return getStat(StatEnum.ALLSPEED, base);
	}
	
	/**
	 * Retrieves the current movement speed of the NPC.<br>
	 * This value changes based on the owner's state such as flying, walking, or fighting.<br>
	 * The result is cached to improve performance until the state changes.
	 * @return the {@code Stat2} object representing the calculated movement speed.
	 */
	@Override
	public Stat2 getMovementSpeed()
	{
		final int currentState = owner.getState();
		final AISubState currentSubState = owner.getAi2().getSubState();
		final Stat2 cachedSpeed = cachedSpeedStat;
		if ((cachedSpeed != null) && (cachedState == currentState) && (cachedSubState == currentSubState))
		{
			return cachedSpeed;
		}
		
		Stat2 newSpeedStat = null;
		if (owner.isFlying())
		{
			newSpeedStat = getStat(StatEnum.FLY_SPEED, Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeed() * 1.3f * 1000));
		}
		else if (owner.isInState(CreatureState.WEAPON_EQUIPPED))
		{
			float speed = 0;
			if (owner.getWalkerGroup() != null)
			{
				speed = owner.getObjectTemplate().getStatsTemplate().getGroupRunSpeedFight();
			}
			else
			{
				speed = owner.getObjectTemplate().getStatsTemplate().getRunSpeedFight();
			}
			
			newSpeedStat = getStat(StatEnum.SPEED, Math.round(speed * 1000));
		}
		else if (owner.isInState(CreatureState.WALKING))
		{
			float speed = 0;
			if ((owner.getWalkerGroup() != null) && (owner.getAi2().getSubState() == AISubState.WALK_PATH))
			{
				speed = owner.getObjectTemplate().getStatsTemplate().getGroupWalkSpeed();
			}
			else
			{
				speed = owner.getObjectTemplate().getStatsTemplate().getWalkSpeed();
			}
			
			newSpeedStat = getStat(StatEnum.SPEED, Math.round(speed * 1000));
		}
		else
		{
			newSpeedStat = getStat(StatEnum.SPEED, Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeed() * 1000));
		}
		
		cachedState = currentState;
		cachedSpeedStat = newSpeedStat;
		return newSpeedStat;
	}
	
	/**
	 * Retrieves the current attack range for the NPC.<br>
	 * This method calculates the value based on the template data.
	 * @return the {@code Stat2} object containing the calculated attack range.
	 */
	@Override
	public Stat2 getAttackRange()
	{
		return getStat(StatEnum.ATTACK_RANGE, owner.getObjectTemplate().getAttackRange() * 1500);
	}
	
	/**
	 * Retrieves the physical defense statistic for this NPC.<br>
	 * This method fetches the value from the owner's template.
	 * @return the {@code Stat2} object representing physical defense.
	 */
	@Override
	public Stat2 getPDef()
	{
		return getStat(StatEnum.PHYSICAL_DEFENSE, owner.getObjectTemplate().getStatsTemplate().getPdef());
	}
	
	/**
	 * Retrieves the magical defense statistic.<br>
	 * This method returns a {@link Stat2} object representing the value of {@code MAGICAL_DEFEND}.
	 * @return The magical defense stat as a {@code Stat2} object.
	 */
	@Override
	public Stat2 getMDef()
	{
		return getStat(StatEnum.MAGICAL_DEFEND, 0);
	}
	
	/**
	 * Retrieves the magical resistance stat for this NPC.<br>
	 * It calculates a default value based on the owner level if not already set.
	 * @return The {@code Stat2} object representing the magical resistance.
	 */
	@Override
	public Stat2 getMResist()
	{
		if (mRes == 0)
		{
			mRes = Math.round((owner.getLevel() * 17.5f) + 75);
		}
		
		return getStat(StatEnum.MAGICAL_RESIST, mRes);
	}
	
	/**
	 * Retrieves the Magic Skill Boost Resistance stat.<br>
	 * This value is used to determine how much magic skill boosts are resisted.
	 * @return the {@code Stat2} object containing the resistance value.
	 */
	@Override
	public Stat2 getMBResist()
	{
		final int base = 0;
		return getStat(StatEnum.MAGIC_SKILL_BOOST_RESIST, base);
	}
	
	/**
	 * Retrieves the power statistic for the NPC.<br>
	 * This method calls {@code int)} using the {@code POWER} enum.
	 * @return The {@code Stat2} object representing the current power value.
	 */
	@Override
	public Stat2 getPower()
	{
		return getStat(StatEnum.POWER, 100);
	}
	
	/**
	 * Retrieves the current health of the NPC.<br>
	 * This method returns a {@link Stat2} object representing the health value.<br>
	 * It uses a default value of {@code 100} if no specific stat is found.
	 * @return The health statistic as a {@code Stat2} object.
	 */
	@Override
	public Stat2 getHealth()
	{
		return getStat(StatEnum.HEALTH, 100);
	}
	
	/**
	 * Retrieves the accuracy statistic for this NPC.<br>
	 * It returns a {@link Stat2} object representing the current value.
	 * @return The {@code Stat2} object containing the accuracy value.
	 */
	@Override
	public Stat2 getAccuracy()
	{
		return getStat(StatEnum.ACCURACY, 100);
	}
	
	/**
	 * Retrieves the agility statistic for this NPC.<br>
	 * It uses a default value of {@code 100} if no specific value is found.
	 * @return The {@link Stat2} object representing the agility stat.
	 */
	@Override
	public Stat2 getAgility()
	{
		return getStat(StatEnum.AGILITY, 100);
	}
	
	/**
	 * Retrieves the knowledge statistic for the NPC.<br>
	 * This method calls {@code int)} using the {@code StatEnum.KNOWLEDGE} type.
	 * @return the {@code Stat2} object representing the current knowledge value.
	 */
	@Override
	public Stat2 getKnowledge()
	{
		return getStat(StatEnum.KNOWLEDGE, 100);
	}
	
	/**
	 * Retrieves the {@code Will} statistic for this NPC.<br>
	 * It returns a {@link Stat2} object representing the value.
	 * @return The {@code Will} stat as a {@code Stat2} object.
	 */
	@Override
	public Stat2 getWill()
	{
		return getStat(StatEnum.WILL, 100);
	}
	
	/**
	 * Retrieves the current evasion statistic for the NPC.<br>
	 * This method calculates the value using {@code StatEnum#EVASION}.<br>
	 * It ensures that stats are updated if {@code pAccuracy} is zero.
	 * @return The {@code Stat2} object containing the evasion value.
	 */
	@Override
	public Stat2 getEvasion()
	{
		if (pAccuracy == 0)
		{
			calcStats();
		}
		
		return getStat(StatEnum.EVASION, pAccuracy);
	}
	
	/**
	 * Retrieves the parry statistic for the NPC.<br>
	 * This method returns a {@code Stat2} object representing the parry value.<br>
	 * It uses a default value of {@code 100} if no specific value is found.
	 * @return The {@code Stat2} object containing the parry stat.
	 */
	@Override
	public Stat2 getParry()
	{
		return getStat(StatEnum.PARRY, 100);
	}
	
	/**
	 * Retrieves the block stat for this NPC.<br>
	 * This method calls {@code int)} to fetch the value.
	 * @return the {@code Stat2} object representing the block value.
	 */
	@Override
	public Stat2 getBlock()
	{
		return getStat(StatEnum.BLOCK, 0);
	}
	
	/**
	 * Retrieves the physical attack value for the main hand weapon.<br>
	 * This method fetches the stat from the {@link Npc} template.
	 * @return The {@code Stat2} object representing the main hand physical attack.
	 */
	@Override
	public Stat2 getMainHandPAttack()
	{
		return getStat(StatEnum.PHYSICAL_ATTACK, owner.getObjectTemplate().getStatsTemplate().getMainHandAttack());
	}
	
	/**
	 * Retrieves the physical critical hit stat for the main hand.<br>
	 * This method fetches the value associated with {@code StatEnum.PHYSICAL_CRITICAL}.<br>
	 * It uses a default index of {@code 10}.
	 * @return The {@link Stat2} object containing the physical critical stat.
	 */
	@Override
	public Stat2 getMainHandPCritical()
	{
		return getStat(StatEnum.PHYSICAL_CRITICAL, 10);
	}
	
	/**
	 * Retrieves the physical accuracy stat for the main hand.<br>
	 * This method triggers a calculation if {@code pAccuracy} is 0.
	 * @return The {@link Stat2} value of the physical accuracy.
	 */
	@Override
	public Stat2 getMainHandPAccuracy()
	{
		if (pAccuracy == 0)
		{
			calcStats();
		}
		
		return getStat(StatEnum.PHYSICAL_ACCURACY, pAccuracy);
	}
	
	/**
	 * Retrieves the magical attack statistic for this NPC.<br>
	 * It returns a {@code Stat2} object representing the value.
	 * @return The {@code Stat2} value of the magical attack.
	 */
	@Override
	public Stat2 getMAttack()
	{
		return getStat(StatEnum.MAGICAL_ATTACK, 100);
	}
	
	/**
	 * Retrieves the main hand magical attack stat for this NPC.<br>
	 * It fetches the power value from the {@link NpcGameStats} owner's template.<br>
	 * The result is returned as a {@code Stat2} object.
	 * @return The calculated {@code Stat2} for magical attack.
	 */
	@Override
	public Stat2 getMainHandMAttack()
	{
		return getStat(StatEnum.MAGICAL_ATTACK, owner.getObjectTemplate().getStatsTemplate().getPower());
	}
	
	/**
	 * Retrieves the magical attack stat for the off-hand weapon.<br>
	 * This value is based on the power from the {@link Npc} template.
	 * @return the {@code Stat2} object containing the off-hand magical attack.
	 */
	@Override
	public Stat2 getOffHandMAttack()
	{
		return getStat(StatEnum.MAGICAL_ATTACK, owner.getObjectTemplate().getStatsTemplate().getPower());
	}
	
	/**
	 * Retrieves the magical skill boost statistic.<br>
	 * This method returns a {@code Stat2} object representing the boost value.<br>
	 * It uses a base value of {@code 100} for the calculation.
	 * @return The {@code Stat2} object containing the magical skill boost.
	 */
	@Override
	public Stat2 getMBoost()
	{
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, 100);
	}
	
	/**
	 * Retrieves the magical accuracy of the NPC.<br>
	 * This method calculates the value if it is not already cached.<br>
	 * It returns a specific stat based on whether the owner is a {@link SummonedObject}.
	 * @return The {@code Stat2} object representing the magical accuracy.
	 */
	@Override
	public Stat2 getMAccuracy()
	{
		if (pAccuracy == 0)
		{
			calcStats();
		}
		
		// Trap's MAccuracy is being calculated into TrapGameStats and is related to master's MAccuracy
		if (owner instanceof SummonedObject)
		{
			return getStat(StatEnum.MAGICAL_ACCURACY, pAccuracy);
		}
		
		return getMainHandPAccuracy();
	}
	
	/**
	 * Retrieves the magical critical hit statistic.<br>
	 * This method calls {@code int)} using the {@code MAGICAL_CRITICAL} enum.<br>
	 * It provides a default value of {@code 50} if the stat is not found.
	 * @return The {@code Stat2} object representing magical critical hit.
	 */
	@Override
	public Stat2 getMCritical()
	{
		return getStat(StatEnum.MAGICAL_CRITICAL, 50);
	}
	
	/**
	 * Retrieves the health regeneration rate for the NPC.<br>
	 * This value is calculated as one-fourth of the maximum HP.
	 * @return a {@code Stat2} object representing the regeneration rate.
	 */
	@Override
	public Stat2 getHpRegenRate()
	{
		final NpcStatsTemplate nst = owner.getObjectTemplate().getStatsTemplate();
		return getStat(StatEnum.REGEN_HP, nst.getMaxHp() / 4);
	}
	
	/**
	 * Retrieves the MP regeneration rate for an {@link Npc}.<br>
	 * This method is not supported and will throw an exception.
	 * @return a {@code Stat2} object representing the regeneration rate.
	 */
	@Override
	public Stat2 getMpRegenRate()
	{
		throw new IllegalStateException("No mp regen for NPC");
	}
	
	/**
	 * Calculates the time elapsed since the last attack.<br>
	 * The result is returned in seconds.
	 * @return The number of seconds passed since {@code lastAttackTime}.
	 */
	public int getLastAttackTimeDelta()
	{
		return Math.round((System.currentTimeMillis() - lastAttackTime) / 1000f);
	}
	
	/**
	 * Calculates the time elapsed since the NPC was last attacked.<br>
	 * The result is returned in seconds.
	 * @return The number of seconds passed since {@code lastAttackedTime}.
	 */
	public int getLastAttackedTimeDelta()
	{
		return Math.round((System.currentTimeMillis() - lastAttackedTime) / 1000f);
	}
	
	/**
	 * Updates the {@code lastAttackTime} to the current system time.<br>
	 * This method is used to refresh the timestamp of the most recent attack.
	 */
	public void renewLastAttackTime()
	{
		lastAttackTime = System.currentTimeMillis();
	}
	
	/**
	 * Updates the {@code lastAttackedTime} to the current system time.<br>
	 * This method is used to refresh the timestamp of the most recent attack.
	 */
	public void renewLastAttackedTime()
	{
		lastAttackedTime = System.currentTimeMillis();
	}
	
	/**
	 * Checks if the next attack is scheduled to occur in the future.<br>
	 * It verifies that the time until the next attack is greater than {@code 50} milliseconds.
	 * @return {@code true} if an attack is pending, or {@code false} otherwise.
	 */
	public boolean isNextAttackScheduled()
	{
		return (nextAttackTime - System.currentTimeMillis()) > 50;
	}
	
	/**
	 * Records the current system time as the start of a fight.<br>
	 * This updates the {@code fightStartingTime} field using {@code System.currentTimeMillis()}.
	 */
	public void setFightStartingTime()
	{
		fightStartingTime = System.currentTimeMillis();
	}
	
	/**
	 * Retrieves the timestamp when the fight first began.<br>
	 * This value is stored in milliseconds.
	 * @return The starting time of the fight as a {@code long}.
	 */
	public long getFightStartingTime()
	{
		return fightStartingTime;
	}
	
	/**
	 * Sets the timestamp for the next scheduled attack.<br>
	 * This value is used to determine when an {@link Npc} should perform its next action.
	 * @param nextAttackTime The time in milliseconds when the next attack occurs.
	 */
	public void setNextAttackTime(long nextAttackTime)
	{
		this.nextAttackTime = nextAttackTime;
	}
	
	/**
	 * Calculates the time remaining until the next attack can occur.<br>
	 * It compares the current {@code lastAttackTime} against the {@code getAttackSpeed()} stat.<br>
	 * If the speed is {@code 0}, it defaults to a value of {@code 2000}.
	 * @return The number of milliseconds until the next attack is allowed.
	 */
	public int getNextAttackInterval()
	{
		final long attackDelay = System.currentTimeMillis() - lastAttackTime;
		int attackSpeed = getAttackSpeed().getCurrent();
		if (attackSpeed == 0)
		{
			attackSpeed = 2000;
		}
		
		if (owner.getAi2().isLogging())
		{
			AI2Logger.info(owner.getAi2(), "adelay = " + attackDelay + " aspeed = " + attackSpeed);
		}
		
		int nextAttack = 0;
		if (attackDelay < attackSpeed)
		{
			nextAttack = (int) (attackSpeed - attackDelay);
		}
		
		return nextAttack;
	}
	
	/**
	 * Updates the {@code lastSkillTime} to the current system time.<br>
	 * This method marks the moment a skill was last used by the NPC.
	 */
	public void renewLastSkillTime()
	{
		lastSkillTime = System.currentTimeMillis();
	}
	
	// not used at the moment
	/*
	 * public void renewLastSkilledTime() { this.lastSkilledTime = System.currentTimeMillis(); }
	 */
	/**
	 * Updates the {@code lastChangeTarget} timestamp to the current system time.<br>
	 * This method is used to refresh the record of when the target was last modified.
	 */
	public void renewLastChangeTargetTime()
	{
		lastChangeTarget = System.currentTimeMillis();
	}
	
	/**
	 * Calculates the time elapsed since the last skill was used.<br>
	 * The result is returned in seconds.
	 * @return The number of seconds passed since {@code lastSkillTime}.
	 */
	public int getLastSkillTimeDelta()
	{
		return Math.round((System.currentTimeMillis() - lastSkillTime) / 1000f);
	}
	
	// not used at the moment
	/*
	 * public int getLastSkilledTimeDelta() { return Math.round((System.currentTimeMillis() - lastSkilledTime) / 1000f); }
	 */
	/**
	 * Calculates the time elapsed since the target was last changed.<br>
	 * The result is returned in seconds.
	 * @return The difference between the current system time and {@code lastChangeTarget} in seconds.
	 */
	public int getLastChangeTargetTimeDelta()
	{
		return Math.round((System.currentTimeMillis() - lastChangeTarget) / 1000f);
	}
	
	// Only use skills after a minimum cooldown of 3 to 9 seconds; check whether this is a suitable time.
	/**
	 * Checks if the NPC is allowed to use its next skill.<br>
	 * This method compares the time since the last skill against a random cooldown period.
	 * @return {@code true} if the skill can be used, otherwise {@code false}.
	 */
	public boolean canUseNextSkill()
	{
		if (getLastSkillTimeDelta() >= (6 + Rnd.get(-3, 3)))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Updates the movement speed information for the creature.<br>
	 * This method refreshes the internal values used by {@code getMovementSpeedFloat}.
	 */
	@Override
	public void updateSpeedInfo()
	{
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0));
	}
	
	/**
	 * Retrieves the timestamp of the last geographic zone update.<br>
	 * This value is used to track when the NPC last moved between zones.
	 * @return The {@code long} timestamp of the last update.
	 */
	public long getLastGeoZUpdate()
	{
		return lastGeoZUpdate;
	}
	
	/**
	 * Updates the timestamp of the last geographic zone change.<br>
	 * This value is used to track when the entity moved to a new area.
	 * @param lastGeoZUpdate The {@code long} timestamp of the last update.
	 */
	public void setLastGeoZUpdate(long lastGeoZUpdate)
	{
		this.lastGeoZUpdate = lastGeoZUpdate;
	}
	
	/**
	 * Calculates the physical accuracy for the owner.<br>
	 * This method uses the level and rating of the {@link Npc} to determine the final value.<br>
	 * The result is stored in the {@code pAccuracy} field.
	 */
	private void calcStats()
	{
		final int lvl = owner.getLevel();
		double accuracy = (lvl * (33.6f - (0.16 * lvl))) + 5;
		
		final NpcRating npcRating = owner.getObjectTemplate().getRating();
		/**
		 * switch (owner.getObjectTemplate().getRating()) Potentially dangerous use, u need to check the return value *
		 */
		if (npcRating != null)
		{
			switch (npcRating)
			{
				case JUNK:
					accuracy *= 1.00f;
					break;
				case NORMAL:
					accuracy *= 1.05f;
					break;
				case ELITE:
					accuracy *= 1.15f;
					break;
				case HERO:
					accuracy *= 1.25f;
					break;
				case LEGENDARY:
					accuracy *= 1.35f;
					break;
				default:
					break;
			}
		}
		
		/**
		 * mb need default value for accuracy multiplication ??? *
		 */
		pAccuracy = Math.round(owner.getAi2().modifyMaccuracy((int) accuracy));
		/**
		 * (int)Math.round(some) No need cast Math.round return value it is always (int) *
		 */
	}
	
	/**
	 * Retrieves the PvP attack statistic for the NPC.<br>
	 * This method currently returns {@code null}.
	 * @return a {@link Stat2} object representing the PvP attack value.
	 */
	@Override
	public Stat2 getPvpAttack()
	{
		return null;
	}
	
	/**
	 * Retrieves the PvP defense statistic.<br>
	 * This method currently returns {@code null}.
	 * @return the {@code Stat2} object representing PvP defense.
	 */
	@Override
	public Stat2 getPvpDeff()
	{
		return null;
	}
	
	// new 7.x
	/**
	 * Retrieves the PVP attack statistic for the NPC.<br>
	 * This method calls {@code int)} to fetch the value.
	 * @return The {@code Stat2} object representing the PVP attack stat.
	 */
	@Override
	public Stat2 getPVPAttack()
	{
		return getStat(StatEnum.PVP_ATTACK, 0);
	}
	
	/**
	 * Retrieves the PVP Defense statistic for the NPC.<br>
	 * This method returns a {@link Stat2} object representing the defense value.
	 * @return The {@code Stat2} value of the PVP Defense.
	 */
	@Override
	public Stat2 getPVPDefense()
	{
		return getStat(StatEnum.PVP_DEFENSE, 0);
	}
	
	/**
	 * Retrieves the PVE attack statistic for this NPC.<br>
	 * This method calls {@code int)} using the {@code PVE_ATTACK} enum.
	 * @return The {@code Stat2} object representing the PVE attack value.
	 */
	@Override
	public Stat2 getPVEAttack()
	{
		return getStat(StatEnum.PVE_ATTACK, 0);
	}
	
	/**
	 * Retrieves the PVE Defense stat for this NPC.<br>
	 * This method returns a {@link Stat2} object representing the defense value.
	 * @return The {@code Stat2} value of the PVE Defense.
	 */
	@Override
	public Stat2 getPVEDefense()
	{
		return getStat(StatEnum.PVE_DEFENSE, 0);
	}
}
