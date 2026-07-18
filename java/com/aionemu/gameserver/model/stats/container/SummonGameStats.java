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

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.templates.stats.SummonStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the game statistics for {@link Summon} entities.<br>
 * It extends {@link CreatureGameStats} to provide specific logic for summons.
 * @author ATracer
 */
public class SummonGameStats extends CreatureGameStats<Summon>
{
	private int cachedSpeed;
	private final SummonStatsTemplate statsTemplate;
	
	/**
	 * Creates a new {@link SummonGameStats} instance for a specific summon.<br>
	 * This constructor initializes the statistics using a provided template.
	 * @param owner The {@link Summon} that owns these statistics.
	 * @param statsTemplate The {@link SummonStatsTemplate} used to define the base stats.
	 */
	public SummonGameStats(Summon owner, SummonStatsTemplate statsTemplate)
	{
		super(owner);
		this.statsTemplate = statsTemplate;
	}
	
	/**
	 * Updates the visual representation of stats and speed.<br>
	 * This method calls {@code updateStatsAndSpeedVisually}.
	 */
	@Override
	protected void onStatsChange()
	{
		updateStatsAndSpeedVisually();
	}
	
	/**
	 * Updates the visual representation of player statistics.<br>
	 * This method also triggers a check for speed-related stats.<br>
	 * It calls {@code updateStatsVisually} and {@code checkSpeedStats}.
	 */
	public void updateStatsAndSpeedVisually()
	{
		updateStatsVisually();
		checkSpeedStats();
	}
	
	/**
	 * Updates the visual statistics for the player.<br>
	 * This method adds a broadcast mask to notify clients of changes.<br>
	 * It uses {@code BroadcastMode#UPDATE_STATS} to trigger the update.
	 */
	public void updateStatsVisually()
	{
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_STATS);
	}
	
	/**
	 * Updates the cached movement speed for the NPC.<br>
	 * This method checks if the current speed has changed since the last update.<br>
	 * If a change is detected, it broadcasts an update to all relevant clients.
	 */
	private void checkSpeedStats()
	{
		final int current = getMovementSpeed().getCurrent();
		if (current != cachedSpeed)
		{
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
		
		cachedSpeed = current;
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
	 * Retrieves the calculated value of a specific statistic for a summon.<br>
	 * This method applies special scaling and master-based boosts depending on the {@link StatEnum}.<br>
	 * It returns the final {@link Stat2} object after all modifiers are applied.
	 * @param statEnum The type of statistic to retrieve.
	 * @param base The base value for the statistic.
	 * @return The calculated {@link Stat2} object.
	 */
	@Override
	public Stat2 getStat(StatEnum statEnum, int base)
	{
		final Stat2 stat = super.getStat(statEnum, base);
		if (owner.getMaster() == null)
		{
			return stat;
		}
		
		switch (statEnum)
		{
			case MAXHP:
				stat.setBonusRate(0.5f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case BOOST_MAGICAL_SKILL:
			case MAGICAL_ACCURACY:
				stat.setBonusRate(0.8f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_DEFENSE:
				stat.setBonusRate(0.3f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case EVASION:
			case PARRY:
			case MAGICAL_RESIST:
			case MAGICAL_CRITICAL:
				stat.setBonusRate(0.5f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_ACCURACY:
				stat.setBonusRate(0.5f);
				owner.getMaster().getGameStats().getItemStatBoost(StatEnum.MAIN_HAND_ACCURACY, stat);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_CRITICAL:
				stat.setBonusRate(0.5f);
				owner.getMaster().getGameStats().getItemStatBoost(StatEnum.MAIN_HAND_CRITICAL, stat);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			default:
				break;
		}
		
		return stat;
	}
	
	/**
	 * Retrieves the maximum health point value for the summon.<br>
	 * This method fetches the stat from the {@code SummonGameStats} instance.
	 * @return The {@code Stat2} object representing the maximum HP.
	 */
	@Override
	public Stat2 getMaxHp()
	{
		return getStat(StatEnum.MAXHP, statsTemplate.getMaxHp());
	}
	
	/**
	 * Retrieves the maximum mana points for the summon.<br>
	 * This method calculates the value based on the current stats template.
	 * @return The {@code Stat2} object containing the maximum MP.
	 */
	@Override
	public Stat2 getMaxMp()
	{
		return getStat(StatEnum.MAXHP, statsTemplate.getMaxMp());
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
	 * Retrieves the current movement speed of the NPC.<br>
	 * This value changes based on the owner's state such as flying, walking, or fighting.<br>
	 * The result is cached to improve performance until the state changes.
	 * @return the {@code Stat2} object representing the calculated movement speed.
	 */
	@Override
	public Stat2 getMovementSpeed()
	{
		int bonusSpeed = 0;
		final Player master = owner.getMaster();
		if ((master != null) && (master.isInFlyingState() || master.isInState(CreatureState.GLIDING)))
		{
			bonusSpeed += 3000;
		}
		
		return getStat(StatEnum.SPEED, Math.round(statsTemplate.getRunSpeed() * 1000) + bonusSpeed);
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
		return getStat(StatEnum.PHYSICAL_DEFENSE, statsTemplate.getPdefense());
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
	 * Retrieves the magical resistance stat for this summon.<br>
	 * It returns a {@code Stat2} object based on the template value.
	 * @return The {@code Stat2} object representing the magical resistance.
	 */
	@Override
	public Stat2 getMResist()
	{
		return getStat(StatEnum.MAGICAL_RESIST, statsTemplate.getMresist());
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
		return getStat(StatEnum.PHYSICAL_ACCURACY, 100);
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
		return getStat(StatEnum.EVASION, statsTemplate.getEvasion());
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
		return getStat(StatEnum.PARRY, statsTemplate.getParry());
	}
	
	/**
	 * Retrieves the block stat for this NPC.<br>
	 * This method calls {@code int)} to fetch the value.
	 * @return the {@code Stat2} object representing the block value.
	 */
	@Override
	public Stat2 getBlock()
	{
		return getStat(StatEnum.BLOCK, statsTemplate.getBlock());
	}
	
	/**
	 * Retrieves the physical attack value for the main hand weapon.<br>
	 * This method fetches the stat from the {@link Npc} template.
	 * @return The {@code Stat2} object representing the main hand physical attack.
	 */
	@Override
	public Stat2 getMainHandPAttack()
	{
		return getStat(StatEnum.PHYSICAL_ATTACK, statsTemplate.getMainHandAttack());
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
		return getStat(StatEnum.PHYSICAL_CRITICAL, statsTemplate.getMainHandCritRate());
	}
	
	/**
	 * Retrieves the physical accuracy stat for the main hand.<br>
	 * This method triggers a calculation if {@code pAccuracy} is 0.
	 * @return The {@link Stat2} value of the physical accuracy.
	 */
	@Override
	public Stat2 getMainHandPAccuracy()
	{
		return getStat(StatEnum.PHYSICAL_ACCURACY, statsTemplate.getMainHandAccuracy());
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
	 * Retrieves the main hand magical attack stat for this summon.<br>
	 * It returns the value associated with {@code StatEnum.MAGICAL_ATTACK}.
	 * @return The {@code Stat2} object representing the magical attack.
	 */
	@Override
	public Stat2 getMainHandMAttack()
	{
		return getStat(StatEnum.MAGICAL_ATTACK, 100);
	}
	
	/**
	 * Retrieves the magical attack stat for the off-hand weapon.<br>
	 * This value is based on the power from the {@link Npc} template.
	 * @return the {@code Stat2} object containing the off-hand magical attack.
	 */
	@Override
	public Stat2 getOffHandMAttack()
	{
		return getStat(StatEnum.MAGICAL_ATTACK, 0);
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
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, 0);
	}
	
	/**
	 * Retrieves the magical accuracy of the summon.<br>
	 * This method returns a {@code Stat2} object based on the template value.
	 * @return The {@code Stat2} object representing the magical accuracy.
	 */
	@Override
	public Stat2 getMAccuracy()
	{
		return getStat(StatEnum.MAGICAL_ACCURACY, statsTemplate.getMagicAccuracy());
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
		return getStat(StatEnum.MAGICAL_CRITICAL, statsTemplate.getMcrit());
	}
	
	/**
	 * Retrieves the health regeneration rate for the summon.<br>
	 * This value is calculated based on a percentage of the maximum HP.<br>
	 * The specific multiplier depends on the current mode ID of the owner.
	 * @return a {@code Stat2} object representing the regeneration rate.
	 */
	@Override
	public Stat2 getHpRegenRate()
	{
		final int base = (int) (owner.getLifeStats().getMaxHp() * (owner.getMode().getId() == 2 ? 0.05f : 0.025f));
		return getStat(StatEnum.REGEN_HP, base);
	}
	
	/**
	 * Retrieves the MP regeneration rate for an {@link Summon}.<br>
	 * This method is not supported and will throw an exception.
	 * @return a {@code Stat2} object representing the regeneration rate.
	 */
	@Override
	public Stat2 getMpRegenRate()
	{
		throw new IllegalStateException("No mp regen for Summon");
	}
	
	/**
	 * Updates the summon's statistics for the master player.<br>
	 * This method sends an {@code SM_SUMMON_UPDATE} packet to the owner's master.<br>
	 * It ensures the master sees the most recent data for the summon.
	 */
	@Override
	public void updateStatInfo()
	{
		final Player master = owner.getMaster();
		if (master != null)
		{
			PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(owner));
		}
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
		return null;
	}
	
	/**
	 * Retrieves the PVP Defense statistic for the NPC.<br>
	 * This method returns a {@link Stat2} object representing the defense value.
	 * @return The {@code Stat2} value of the PVP Defense.
	 */
	@Override
	public Stat2 getPVPDefense()
	{
		return null;
	}
	
	/**
	 * Retrieves the PVE attack statistic for this NPC.<br>
	 * This method calls {@code int)} using the {@code PVE_ATTACK} enum.
	 * @return The {@code Stat2} object representing the PVE attack value.
	 */
	@Override
	public Stat2 getPVEAttack()
	{
		return null;
	}
	
	/**
	 * Retrieves the PVE Defense stat for this NPC.<br>
	 * This method returns a {@link Stat2} object representing the defense value.
	 * @return The {@code Stat2} value of the PVE Defense.
	 */
	@Override
	public Stat2 getPVEDefense()
	{
		return null;
	}
}
