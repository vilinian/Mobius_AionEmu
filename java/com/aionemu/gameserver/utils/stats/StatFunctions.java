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

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.FallDamageConfig;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.observer.AttackerCriticalStatus;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Homing;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Servant;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.model.stats.calc.AdditionStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.CreatureGameStats;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.WeaponStats;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Provides utility methods for calculating and manipulating various game statistics.<br>
 * This class contains helper functions to process {@code PlayerGameStats} and {@code CreatureGameStats}.<br>
 * It simplifies complex logic related to stat modifiers, equipment bonuses, and combat attributes.
 * @author ATracer
 * @author alexa026
 * @rework Blackfire
 */
public class StatFunctions
{
	private static final Logger log = LoggerFactory.getLogger(StatFunctions.class);
	private static SkillElement elements = null;
	
	/**
	 * Calculates the base experience value for a reward.<br>
	 * It returns the smaller value between {@code maxXp} and double the {@code maxHp}.
	 * @param maxXp The maximum possible experience points.
	 * @param maxHp The maximum health points of the target.
	 * @return The calculated base experience as an {@code int}.
	 */
	private static int getBaseXp(int maxXp, int maxHp)
	{
		return maxXp > (maxHp * 2) ? maxXp : maxHp * 2;
	}
	
	/**
	 * Calculates the experience points a {@link Player} earns from killing a single {@link Creature}.<br>
	 * This method determines the base XP and applies a percentage based on the level difference.
	 * @param player The {@link Player} who is receiving the reward.
	 * @param target The {@link Creature} that was defeated.
	 * @return The total experience points awarded as a {@code long}.
	 */
	public static long calculateSoloExperienceReward(Player player, Creature target)
	{
		final int playerLevel = player.getCommonData().getLevel();
		final int targetLevel = target.getLevel();
		
		// int baseXP = ((Npc) target).getObjectTemplate().getStatsTemplate().getMaxXp();
		final int baseXP = getBaseXp(((Npc) target).getObjectTemplate().getStatsTemplate().getMaxXp(), ((Npc) target).getObjectTemplate().getStatsTemplate().getMaxHp());
		final int xpPercentage = XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);
		final long rewardXP = Math.round(baseXP * (xpPercentage / 100d));
		return rewardXP;
	}
	
	/**
	 * Calculates the experience reward for a group based on a target creature.<br>
	 * This method determines the base experience and applies a percentage reduction.<br>
	 * The reduction is based on the difference between the target level and the {@code maxLevelInRange}.
	 * @param maxLevelInRange The maximum level range used to calculate the experience penalty.
	 * @param target The {@link Creature} being defeated to provide the reward.
	 * @return The final calculated experience reward as a {@code long}.
	 */
	public static long calculateGroupExperienceReward(int maxLevelInRange, Creature target)
	{
		final int targetLevel = target.getLevel();
		
		// int baseXP = ((Npc) target).getObjectTemplate().getStatsTemplate().getMaxXp();
		final int baseXP = getBaseXp(((Npc) target).getObjectTemplate().getStatsTemplate().getMaxXp(), ((Npc) target).getObjectTemplate().getStatsTemplate().getMaxHp());
		final int xpPercentage = XPRewardEnum.xpRewardFrom(targetLevel - maxLevelInRange);
		final long rewardXP = Math.round(baseXP * (xpPercentage / 100d));
		return rewardXP;
	}
	
	/**
	 * Calculates the amount of DP a {@link Player} receives for defeating a {@link Creature}.<br>
	 * This calculation considers the target level, NPC rating, and player rates.
	 * @param player The player who earned the reward.
	 * @param target The creature that was defeated.
	 * @return The total amount of DP awarded as an {@code int}.
	 */
	public static int calculateSoloDPReward(Player player, Creature target)
	{
		final int playerLevel = player.getCommonData().getLevel();
		final int targetLevel = target.getLevel();
		final NpcRating npcRating = ((Npc) target).getObjectTemplate().getRating();
		
		// TODO: fix to see monster Rating level, NORMAL lvl 1, 2 | ELITE lvl 1, 2 etc..
		// look at:
		// http://www.aionsource.com/forum/mechanic-analysis/42597-character-stats-xp-dp-origin-gerbator-team-july-2009-a.html
		final int baseDP = targetLevel * calculateRatingMultipler(npcRating);
		
		final int xpPercentage = XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);
		final float rate = player.getRates().getDpNpcRate();
		return (int) Math.floor((baseDP * xpPercentage * rate) / 100);
	}
	
	/**
	 * Calculates the amount of AP gained by a {@link Player} from defeating a {@link Creature}.<br>
	 * This method considers the player's abyss rank and the target's NPC rating.<br>
	 * It also checks for specific level differences and special creature names.
	 * @param player The {@link Player} who is gaining the reward.
	 * @param target The {@link Creature} that was defeated.
	 * @return The total amount of AP gained as an {@code int}.
	 */
	public static int calculatePvEApGained(Player player, Creature target)
	{
		final float apPercentage = target instanceof SiegeNpc ? 100f : APRewardEnum.apReward(player.getAbyssRank().getRank().getId());
		final boolean lvlDiff = (player.getCommonData().getLevel() - target.getLevel()) > 10;
		float apNpcRate = ApNpcRating(((Npc) target).getObjectTemplate().getRating());
		
		// TODO: findout why they give 1/4 AP base(normal NpcRate) (5 AP retail)
		if (target.getName().equals("flame hoverstone"))
		{
			apNpcRate = 0.5f;
		}
		
		if (target.getName().equals("controllera") || target.getName().equals("controllerb"))
		{
			apNpcRate = 0f;
		}
		
		return (int) (lvlDiff ? 1 : RewardType.AP_NPC.calcReward(player, (int) Math.floor((15 * apPercentage * apNpcRate) / 100)));
	}
	
	/**
	 * Calculates the amount of AP lost by a player after being defeated in PvP.<br>
	 * This method applies a penalty based on the level difference between the {@code winner} and the {@code defeated} player.
	 * @param defeated The {@link Player} who was defeated in combat.
	 * @param winner The {@link Player} who won the combat.
	 * @return The total amount of AP points lost by the defeated player as an {@code int}.
	 */
	public static int calculatePvPApLost(Player defeated, Player winner)
	{
		int pointsLost = Math.round(defeated.getAbyssRank().getRank().getPointsLost() * defeated.getRates().getApPlayerLossRate());
		
		// Level penalty calculation
		final int difference = winner.getLevel() - defeated.getLevel();
		
		if (difference > 4)
		{
			pointsLost = Math.round(pointsLost * 0.1f);
		}
		else
		{
			switch (difference)
			{
				case 3:
					pointsLost = Math.round(pointsLost * 0.85f);
					break;
				case 4:
					pointsLost = Math.round(pointsLost * 0.65f);
					break;
			}
		}
		
		return pointsLost;
	}
	
	/**
	 * Calculates the amount of Guild Points lost by a defeated player.<br>
	 * This method applies a penalty based on the level difference between the winner and the loser.
	 * @param defeated The {@code Player} who lost the fight.
	 * @param winner The {@code Player} who won the fight.
	 * @return The total amount of Guild Points to be deducted.
	 */
	public static int calculatePvPGpLost(Player defeated, Player winner)
	{
		int pointsLost = Math.round(defeated.getAbyssRank().getRank().getPointsLost() * defeated.getRates().getGpPlayerLossRate());
		
		// Level penalty calculation
		final int difference = winner.getLevel() - defeated.getLevel();
		
		if (difference > 4)
		{
			pointsLost = Math.round(pointsLost * 0.1f);
		}
		else
		{
			switch (difference)
			{
				case 3:
					pointsLost = Math.round(pointsLost * 0.85f);
					break;
				case 4:
					pointsLost = Math.round(pointsLost * 0.65f);
					break;
			}
		}
		
		return pointsLost;
	}
	
	/**
	 * Calculates the amount of Abyss Points gained from a defeated player.<br>
	 * This method applies penalties based on level differences and rank gaps.
	 * @param defeated The {@link Player} who was defeated in combat.
	 * @param maxRank The Abyss Rank of the winning player.
	 * @param maxLevel The maximum level used for comparison.
	 * @return The final amount of points gained as an {@code int}.
	 */
	public static int calculatePvpApGained(Player defeated, int maxRank, int maxLevel)
	{
		int pointsGained = defeated.getAbyssRank().getRank().getPointsGained();
		
		// Level penalty calculation
		final int difference = maxLevel - defeated.getLevel();
		
		if (difference > 4)
		{
			pointsGained = Math.round(pointsGained * 0.1f);
		}
		else if (difference < -3)
		{
			pointsGained = Math.round(pointsGained * 1.3f);
		}
		else
		{
			switch (difference)
			{
				case 3:
					pointsGained = Math.round(pointsGained * 0.85f);
					break;
				case 4:
					pointsGained = Math.round(pointsGained * 0.65f);
					break;
				case -2:
					pointsGained = Math.round(pointsGained * 1.1f);
					break;
				case -3:
					pointsGained = Math.round(pointsGained * 1.2f);
					break;
			}
		}
		
		// Abyss rank penalty calculation
		final int winnerAbyssRank = maxRank;
		final int defeatedAbyssRank = defeated.getAbyssRank().getRank().getId();
		final int abyssRankDifference = winnerAbyssRank - defeatedAbyssRank;
		
		if ((winnerAbyssRank <= 7) && (abyssRankDifference > 0))
		{
			final float penaltyPercent = abyssRankDifference * 0.05f;
			
			pointsGained -= Math.round(pointsGained * penaltyPercent);
		}
		
		return pointsGained;
	}
	
	/**
	 * Calculates the amount of GP gained from defeating a player.<br>
	 * This method applies penalties based on level differences and abyss rank.
	 * @param defeated The {@link Player} who was defeated.
	 * @param maxRank The abyss rank of the winning player.
	 * @param maxLevel The maximum level used for comparison.
	 * @return The final amount of GP gained as an {@code int}.
	 */
	public static int calculatePvpGpGained(Player defeated, int maxRank, int maxLevel)
	{
		int pointsGained = defeated.getAbyssRank().getRank().getPointsGained();
		
		// Level penalty calculation
		final int difference = maxLevel - defeated.getLevel();
		
		if (difference > 4)
		{
			pointsGained = Math.round(pointsGained * 0.1f);
		}
		else if (difference < -3)
		{
			pointsGained = Math.round(pointsGained * 1.3f);
		}
		else
		{
			switch (difference)
			{
				case 3:
					pointsGained = Math.round(pointsGained * 0.85f);
					break;
				case 4:
					pointsGained = Math.round(pointsGained * 0.65f);
					break;
				case -2:
					pointsGained = Math.round(pointsGained * 1.1f);
					break;
				case -3:
					pointsGained = Math.round(pointsGained * 1.2f);
					break;
			}
		}
		
		// Abyss rank penalty calculation
		final int winnerAbyssRank = maxRank;
		final int defeatedAbyssRank = defeated.getAbyssRank().getRank().getId();
		final int abyssRankDifference = winnerAbyssRank - defeatedAbyssRank;
		
		if ((winnerAbyssRank <= 7) && (abyssRankDifference > 0))
		{
			final float penaltyPercent = abyssRankDifference * 0.05f;
			
			pointsGained -= Math.round(pointsGained * penaltyPercent);
		}
		
		return pointsGained;
	}
	
	/**
	 * Calculates the experience points gained from a PvP victory.<br>
	 * This method applies penalties based on level differences and Abyss ranks.
	 * @param defeated The {@link Player} who was defeated in combat.
	 * @param maxRank The Abyss rank of the winning player.
	 * @param maxLevel The maximum level of the winning player.
	 * @return The total experience points awarded to the winner as an {@code int}.
	 */
	public static int calculatePvpXpGained(Player defeated, int maxRank, int maxLevel)
	{
		int pointsGained = 5000;
		
		// Level penalty calculation
		final int difference = maxLevel - defeated.getLevel();
		
		if (difference > 4)
		{
			pointsGained = Math.round(pointsGained * 0.1f);
		}
		else if (difference < -3)
		{
			pointsGained = Math.round(pointsGained * 1.3f);
		}
		else
		{
			switch (difference)
			{
				case 3:
					pointsGained = Math.round(pointsGained * 0.85f);
					break;
				case 4:
					pointsGained = Math.round(pointsGained * 0.65f);
					break;
				case -2:
					pointsGained = Math.round(pointsGained * 1.1f);
					break;
				case -3:
					pointsGained = Math.round(pointsGained * 1.2f);
					break;
			}
		}
		
		// Abyss rank penalty calculation
		final int winnerAbyssRank = maxRank;
		final int defeatedAbyssRank = defeated.getAbyssRank().getRank().getId();
		final int abyssRankDifference = winnerAbyssRank - defeatedAbyssRank;
		
		if ((winnerAbyssRank <= 7) && (abyssRankDifference > 0))
		{
			final float penaltyPercent = abyssRankDifference * 0.05f;
			
			pointsGained -= Math.round(pointsGained * penaltyPercent);
		}
		
		return pointsGained;
	}
	
	/**
	 * Calculates the amount of PvP Defense Points gained by a player after being defeated.<br>
	 * This method uses the {@code defeated} player's rank and level to determine the final value.<br>
	 * It applies adjustments based on the provided {@code maxRank} and {@code maxLevel}.
	 * @param defeated The {@link Player} who was defeated in combat.
	 * @param maxRank The maximum rank used for calculation scaling.
	 * @param maxLevel The maximum level used for calculation scaling.
	 * @return The total amount of PvP Defense Points gained as an {@code int}.
	 */
	public static int calculatePvpDpGained(Player defeated, int maxRank, int maxLevel)
	{
		int pointsGained = 0;
		
		// base values
		final int baseDp = 1064;
		final int dpPerRank = 57;
		
		// adjust by rank
		pointsGained = ((defeated.getAbyssRank().getRank().getId() - maxRank) * dpPerRank) + baseDp;
		
		// adjust by level
		pointsGained = StatFunctions.adjustPvpDpGained(pointsGained, defeated.getLevel(), maxLevel);
		
		return pointsGained;
	}
	
	/**
	 * Adjusts the amount of PvP DP gained based on the level difference.<br>
	 * This method modifies {@code points} depending on whether the killer is stronger or weaker than the defeated player.<br>
	 * It applies specific multipliers for small level gaps and sets the reward to {@code 0} if the level gap is too large.
	 * @param points The base amount of DP to be awarded.
	 * @param defeatedLvl The level of the player who was defeated.
	 * @param killerLvl The level of the player who won the fight.
	 * @return The adjusted amount of DP gained as an {@code int}.
	 */
	public static int adjustPvpDpGained(int points, int defeatedLvl, int killerLvl)
	{
		int pointsGained = points;
		
		final int difference = killerLvl - defeatedLvl;
		
		// adjust by level
		if (difference >= 10)
		{
			pointsGained = 0;
		}
		else if ((difference < 10) && (difference >= 0))
		{
			pointsGained -= pointsGained * difference * 0.1;
		}
		else if (difference <= -10)
		{
			pointsGained *= 1.1;
		}
		else if ((difference > -10) && (difference < 0))
		{
			pointsGained += pointsGained * Math.abs(difference) * 0.01;
		}
		
		return pointsGained;
	}
	
	/**
	 * Calculates the amount of DP reward a {@link Player} receives from a {@link Creature}.<br>
	 * This calculation considers the target level, NPC rating, and player experience rates.
	 * @param player The {@link Player} who is receiving the reward.
	 * @param target The {@link Creature} that was defeated.
	 * @return The total amount of DP points awarded as an {@code int}.
	 */
	public static int calculateGroupDPReward(Player player, Creature target)
	{
		final int playerLevel = player.getCommonData().getLevel();
		final int targetLevel = target.getLevel();
		final NpcRating npcRating = ((Npc) target).getObjectTemplate().getRating();
		
		// TODO: fix to see monster Rating level, NORMAL lvl 1, 2 | ELITE lvl 1, 2 etc..
		final int baseDP = targetLevel * calculateRatingMultipler(npcRating);
		final int xpPercentage = XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);
		final float rate = player.getRates().getDpNpcRate();
		
		return (int) Math.floor((baseDP * xpPercentage * rate) / 100);
	}
	
	/**
	 * Calculates the current hate value for a specific {@link Creature}.<br>
	 * This method applies an additional boost to the {@code BOOST_HATE} stat.
	 * @param creature The {@code Creature} object to update.
	 * @param value The numerical amount to add to the hate boost.
	 * @return The updated current hate value as an {@code int}.
	 */
	public static int calculateHate(Creature creature, int value)
	{
		final Stat2 stat = new AdditionStat(StatEnum.BOOST_HATE, value, creature, 0.1f);
		return (creature.getGameStats().getStat(StatEnum.BOOST_HATE, stat).getCurrent());
	}
	
	/**
	 * Calculates the total damage dealt by an attacker to a target.<br>
	 * It determines if the attack is physical or magical based on the {@code SkillElement}.<br>
	 * The final value is adjusted for levels and NPC modifiers.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param target The {@link Creature} receiving the damage.
	 * @param isMainHand A boolean indicating if the attack uses the main hand weapon.
	 * @param element The {@link SkillElement} type of the skill used.
	 * @return The final calculated damage as an {@code int}.
	 */
	public static int calculateAttackDamage(Creature attacker, Creature target, boolean isMainHand, SkillElement element)
	{
		int resultDamage = 0;
		if (element == SkillElement.NONE)
		{
			// physical damage
			resultDamage = calculatePhysicalAttackDamage(attacker, target, isMainHand);
		}
		else
		{
			// magical damage
			resultDamage = calculateMagicalAttackDamage(attacker, target, element, isMainHand);
		}
		
		// adjusting baseDamages according to attacker and target level
		elements = element;
		resultDamage = (int) adjustDamages(attacker, target, resultDamage, 0, true);
		
		// magical defense
		/*
		 * if (element != SkillElement.NONE) resultDamage -= target.getGameStats().getStat(StatEnum.MAGICAL_DEFEND, 0).getCurrent();
		 */
		if (target instanceof Npc)
		{
			return target.getAi2().modifyDamage(resultDamage);
		}
		
		if (attacker instanceof Npc)
		{
			return attacker.getAi2().modifyOwnerDamage(resultDamage);
		}
		
		return resultDamage;
	}
	
	/**
	 * Calculates the physical attack damage dealt by a {@link Creature}.<br>
	 * It considers weapon stats, power, and defense values.<br>
	 * The calculation varies based on whether it is a main hand or off-hand attack.
	 * @param attacker The creature performing the attack.
	 * @param target The creature receiving the damage.
	 * @param isMainHand A boolean indicating if the attack uses the main hand weapon.
	 * @return The final calculated physical damage as an {@code int}.
	 */
	public static int calculatePhysicalAttackDamage(Creature attacker, Creature target, boolean isMainHand)
	{
		Stat2 pAttack;
		if (isMainHand)
		{
			pAttack = attacker.getGameStats().getMainHandPAttack();
		}
		else
		{
			pAttack = ((Player) attacker).getGameStats().getOffHandPAttack();
		}
		
		float resultDamage = pAttack.getCurrent();
		final float baseDamage = pAttack.getBase();
		if (attacker instanceof Player)
		{
			final Equipment equipment = ((Player) attacker).getEquipment();
			Item weapon;
			if (isMainHand)
			{
				weapon = equipment.getMainHandWeapon();
			}
			else
			{
				weapon = equipment.getOffHandWeapon();
			}
			
			if (weapon != null)
			{
				final WeaponStats weaponStat = weapon.getItemTemplate().getWeaponStats();
				if (weaponStat == null)
				{
					return 0;
				}
				
				final int totalMin = weaponStat.getMinDamage();
				final int totalMax = weaponStat.getMaxDamage();
				if ((totalMax - totalMin) < 1)
				{
					log.warn("Weapon stat MIN_MAX_DAMAGE resulted average zero in main-hand calculation");
					log.warn("Weapon ID: " + String.valueOf(equipment.getMainHandWeapon().getItemTemplate().getTemplateId()));
					log.warn("MIN_DAMAGE = " + String.valueOf(totalMin));
					log.warn("MAX_DAMAGE = " + String.valueOf(totalMax));
				}
				
				final float power = attacker.getGameStats().getPower().getCurrent() * 0.01f;
				final int diff = Math.round(((totalMax - totalMin) * power) / 2);
				resultDamage = pAttack.getBonus() + baseDamage;
				
				// Adjust the value from WeaponDualEffect to lower the minimum damage cap, making offhand damage more random.
				int negativeDiff = diff;
				if (!isMainHand)
				{
					negativeDiff = (int) Math.round((200 - ((Player) attacker).getDualEffectValue()) * 0.01 * diff);
				}
				
				resultDamage += Rnd.get(-negativeDiff, diff);
				
				// add powerShard damage
				if (attacker.isInState(CreatureState.POWERSHARD))
				{
					Item firstShard;
					Item secondShard = null;
					if (isMainHand)
					{
						firstShard = equipment.getMainHandPowerShard();
						if (weapon.getItemTemplate().isTwoHandWeapon())
						{
							secondShard = equipment.getOffHandPowerShard();
						}
					}
					else
					{
						firstShard = equipment.getOffHandPowerShard();
					}
					
					if (firstShard != null)
					{
						equipment.usePowerShard(firstShard, 1);
						resultDamage += firstShard.getItemTemplate().getWeaponBoost();
					}
					
					if (secondShard != null)
					{
						equipment.usePowerShard(secondShard, 1);
						resultDamage += secondShard.getItemTemplate().getWeaponBoost();
					}
				}
			}
			else
			{// if hand attack
				final int totalMin = 16;
				final int totalMax = 20;
				
				final float power = attacker.getGameStats().getPower().getCurrent() * 0.01f;
				final int diff = Math.round(((totalMax - totalMin) * power) / 2);
				resultDamage = pAttack.getBonus() + baseDamage;
				resultDamage += Rnd.get(-diff, diff);
			}
		}
		else
		{
			final int rnd = (int) (resultDamage * 0.25);
			resultDamage += Rnd.get(-rnd, rnd);
		}
		
		// subtract defense
		final float pDef = target.getGameStats().getPDef().getBonus() + getMovementModifier(target, StatEnum.PHYSICAL_DEFENSE, target.getGameStats().getPDef().getBase());
		resultDamage -= (pDef * 0.10f);
		
		if (resultDamage <= 0)
		{
			resultDamage = 1;
		}
		
		return Math.round(resultDamage);
	}
	
	/**
	 * Calculates the total magical attack damage dealt by an attacker to a target.<br>
	 * This method considers weapon stats, knowledge modifiers, and power shards for players.<br>
	 * It also applies elemental resistance reductions based on the provided {@code SkillElement}.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param target The {@link Creature} receiving the damage.
	 * @param element The {@link SkillElement} type of the skill used.
	 * @param isMainHand A boolean indicating if the attack uses the main hand weapon.
	 * @return The final calculated damage as an {@code int}.
	 */
	public static int calculateMagicalAttackDamage(Creature attacker, Creature target, SkillElement element, boolean isMainHand)
	{
		Objects.requireNonNull(element, "Skill element should be NONE instead of null");
		Stat2 mAttack;
		
		if (isMainHand)
		{
			mAttack = attacker.getGameStats().getMainHandMAttack();
		}
		else
		{
			mAttack = attacker.getGameStats().getOffHandMAttack();
		}
		
		float resultDamage = mAttack.getCurrent();
		
		if (attacker instanceof Player)
		{
			final Equipment equipment = ((Player) attacker).getEquipment();
			final Item weapon = equipment.getMainHandWeapon();
			
			if (weapon != null)
			{
				final WeaponStats weaponStat = weapon.getItemTemplate().getWeaponStats();
				if (weaponStat == null)
				{
					return 0;
				}
				
				final int totalMin = weaponStat.getMinDamage();
				final int totalMax = weaponStat.getMaxDamage();
				if ((totalMax - totalMin) < 1)
				{
					log.warn("Weapon stat MIN_MAX_DAMAGE resulted average zero in main-hand calculation");
					log.warn("Weapon ID: " + String.valueOf(equipment.getMainHandWeapon().getItemTemplate().getTemplateId()));
					log.warn("MIN_DAMAGE = " + String.valueOf(totalMin));
					log.warn("MAX_DAMAGE = " + String.valueOf(totalMax));
				}
				
				final float knowledge = attacker.getGameStats().getKnowledge().getCurrent() * 0.01f;
				final int diff = Math.round(((totalMax - totalMin) * knowledge) / 2);
				resultDamage = mAttack.getBonus() + getMovementModifier(attacker, StatEnum.MAGICAL_ATTACK, mAttack.getBase());
				resultDamage += Rnd.get(-diff, diff);
				
				if (attacker.isInState(CreatureState.POWERSHARD))
				{
					final Item firstShard = equipment.getMainHandPowerShard();
					final Item secondShard = equipment.getOffHandPowerShard();
					if (firstShard != null)
					{
						equipment.usePowerShard(firstShard, 1);
						resultDamage += firstShard.getItemTemplate().getWeaponBoost();
					}
					
					if (secondShard != null)
					{
						equipment.usePowerShard(secondShard, 1);
						resultDamage += secondShard.getItemTemplate().getWeaponBoost();
					}
				}
			}
		}
		
		if (element != SkillElement.NONE)
		{
			final float elementalDef = getMovementModifier(target, SkillElement.getResistanceForElement(element), target.getGameStats().getMagicalDefenseFor(element));
			resultDamage = Math.round(resultDamage * (1 - (elementalDef / 1300f)));
		}
		
		if (resultDamage <= 0)
		{
			resultDamage = 1;
		}
		
		return Math.round(resultDamage);
	}
	
	/**
	 * Calculates the final damage dealt by a magical skill.<br>
	 * This method accounts for magic boost, knowledge, and elemental resistances.<br>
	 * It also applies specific modifiers based on whether the target is an NPC or player.
	 * @param speller The {@link Creature} casting the spell.
	 * @param target The {@link Creature} receiving the damage.
	 * @param baseDamages The initial damage value of the skill.
	 * @param bonus Additional flat damage to be added.
	 * @param element The {@link SkillElement} type of the spell.
	 * @param useMagicBoost Whether to include magic boost stats in the calculation.
	 * @param useKnowledge Whether to include knowledge stats in the calculation.
	 * @param noReduce If {@code true}, ignores elemental damage reduction.
	 * @param pvpDamage Damage value used for PvP specific adjustments.
	 * @return The final calculated damage as an {@code int}.
	 */
	public static int calculateMagicalSkillDamage(Creature speller, Creature target, int baseDamages, int bonus, SkillElement element, boolean useMagicBoost, boolean useKnowledge, boolean noReduce, int pvpDamage)
	{
		final CreatureGameStats<?> sgs = speller.getGameStats();
		final CreatureGameStats<?> tgs = target.getGameStats();
		int magicBoost = useMagicBoost ? sgs.getMBoost().getCurrent() : 0;
		final int mBResist = tgs.getMBResist().getCurrent();
		final int MDef = tgs.getMDef().getCurrent();
		final int knowledge = useKnowledge ? sgs.getKnowledge().getCurrent() : 100;
		if ((magicBoost - mBResist) > 3200)
		{
			magicBoost = 3201;
		}
		else
		{
			magicBoost = magicBoost - mBResist;
		}
		
		if ((magicBoost - MDef) < 1)
		{
			magicBoost = 1;
		}
		else
		{
			magicBoost -= MDef;
		}
		
		float damages = baseDamages * ((knowledge / 100f) + (magicBoost / 1000f));
		
		damages = sgs.getStat(StatEnum.BOOST_SPELL_ATTACK, (int) damages).getCurrent();
		
		// add bonus damage
		damages += bonus;
		
		/*
		 * element resist: fire, wind, water, eath 10 elemental resist ~ 1% reduce of magical baseDamages
		 */
		if (!noReduce && (element != SkillElement.NONE))
		{
			final float elementalDef = getMovementModifier(target, SkillElement.getResistanceForElement(element), tgs.getMagicalDefenseFor(element));
			damages = Math.round(damages * (1 - (elementalDef / 1250f)));
		}
		
		elements = element;
		damages = adjustDamages(speller, target, damages, pvpDamage, useKnowledge);
		
		// Apply magical defense if reduction is not disabled and an element is specified.
		// damages -= target.getGameStats().getMDef().getCurrent();
		// }
		
		if (damages <= 0)
		{
			damages = 1;
		}
		
		if (target instanceof Npc)
		{
			return target.getAi2().modifyDamage((int) damages);
		}
		
		return Math.round(damages);
	}
	
	/**
	 * Determines if an attack results in a magical critical hit.<br>
	 * This method checks the stats of both the {@link Creature} attacker and the target.<br>
	 * It calculates the final rate based on resistance values and the provided probability.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param attacked The {@link Creature} receiving the attack.
	 * @param criticalProb The base critical probability percentage to apply.
	 * @return {@code true} if the hit is a magical critical, {@code false} otherwise.
	 */
	public static boolean calculateMagicalCriticalRate(Creature attacker, Creature attacked, int criticalProb)
	{
		if ((attacker instanceof Servant) || (attacker instanceof Homing))
		{
			return false;
		}
		
		int critical = attacker.getGameStats().getMCritical().getCurrent();
		if (attacked instanceof Player)
		{
			critical = attacked.getGameStats().getPositiveReverseStat(StatEnum.MAGICAL_CRITICAL_RESIST, critical) + attacked.getGameStats().getPositiveReverseStat(StatEnum.PVP_MAGICAL_RESIST, critical);
		}
		else
		{
			critical = attacked.getGameStats().getPositiveReverseStat(StatEnum.MAGICAL_CRITICAL_RESIST, critical);
		}
		
		// add critical Prob
		critical *= criticalProb / 100f;
		
		double criticalRate;
		
		if (critical <= 440)
		{
			criticalRate = critical * 0.1f;
		}
		else if (critical <= 600)
		{
			criticalRate = (440 * 0.1f) + ((critical - 440) * 0.05f);
		}
		else
		{
			criticalRate = (440 * 0.1f) + (160 * 0.05f) + ((critical - 600) * 0.02f);
		}
		
		return Rnd.nextInt(100) < criticalRate;
	}
	
	/**
	 * Calculates the experience multiplier based on an {@link NpcRating}.<br>
	 * This method maps specific NPC types to their corresponding numeric values.
	 * @param npcRating The rating of the NPC being evaluated.
	 * @return The integer multiplier associated with the given rating.
	 */
	public static int calculateRatingMultipler(NpcRating npcRating)
	{
		// FIXME: to correct formula, have any reference?
		int multipler;
		switch (npcRating)
		{
			case JUNK:
				multipler = 1;
				break;
			case NORMAL:
				multipler = 2;
				break;
			case ELITE:
				multipler = 3;
				break;
			case HERO:
				multipler = 4;
				break;
			case LEGENDARY:
				multipler = 5;
				break;
			default:
				multipler = 1;
		}
		
		return multipler;
	}
	
	/**
	 * Calculates the experience multiplier based on an {@code NpcRating}.<br>
	 * This method maps specific NPC types to their corresponding reward values.
	 * @param npcRating The rating of the NPC being evaluated.
	 * @return The integer multiplier for the experience reward.
	 */
	public static int ApNpcRating(NpcRating npcRating)
	{
		int multipler;
		switch (npcRating)
		{
			case JUNK:
				multipler = 1;
				break;
			case NORMAL:
				multipler = 2;
				break;
			case ELITE:
				multipler = 4;
				break;
			case HERO:
				multipler = 5;
				break;
			case LEGENDARY:
				multipler = 6;
				break;
			default:
				multipler = 1;
		}
		
		return multipler;
	}
	
	/**
	 * This method retrieves the experience multiplier for a specific NPC rating.<br>
	 * It maps {@link NpcRating} types to their corresponding integer values.
	 * @param npcRating The {@code NpcRating} of the creature.
	 * @return The integer multiplier value.
	 */
	public static int GpNpcRating(NpcRating npcRating)
	{
		int multipler;
		switch (npcRating)
		{
			case JUNK:
				multipler = 1;
				break;
			case NORMAL:
				multipler = 2;
				break;
			case ELITE:
				multipler = 3;
				break;
			case HERO:
				multipler = 4;
				break;
			case LEGENDARY:
				multipler = 5;
				break;
			default:
				multipler = 1;
		}
		
		return multipler;
	}
	
	/**
	 * Calculates the final damage value based on various combat factors.<br>
	 * This method applies modifiers for PVP, NPC level differences, and movement.<br>
	 * It also accounts for specific player class multipliers and race bonuses.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param target The {@link Creature} receiving the damage.
	 * @param damages The initial base damage amount.
	 * @param pvpDamage The PVP damage multiplier from skill templates.
	 * @param useMovement A boolean indicating if movement damage bonuses should be applied.
	 * @return The final adjusted damage as a {@code float}.
	 */
	public static float adjustDamages(Creature attacker, Creature target, float damages, int pvpDamage, boolean useMovement)
	{
		// Artifacts do not have this limitation; consider setting the correct NPC levels in npc_template.xml and deleting this instead.
		if (attacker instanceof Npc)
		{
			if (attacker.getAi2() != null)
			{
				if (attacker.getAi2().getName().equalsIgnoreCase("artifact"))
				{
					return damages;
				}
			}
		}
		
		if (attacker.isPvpTarget(target))
		{
			// adjust damamage by pvp damage from skill_templates.xml
			if (pvpDamage > 0)
			{
				damages *= pvpDamage * 0.01;
			}
			
			// PVP damages is capped of 50% of the actual baseDamage
			damages = Math.round(damages * 0.50f);
			float pvpAttackBonus = attacker.getGameStats().getStat(StatEnum.PVP_ATTACK_RATIO, 0).getCurrent();
			float pvpDefenceBonus = target.getGameStats().getStat(StatEnum.PVP_DEFEND_RATIO, 0).getCurrent();
			switch (elements)
			{
				case NONE:
					pvpAttackBonus += attacker.getGameStats().getStat(StatEnum.PVP_PHYSICAL_ATTACK, 0).getCurrent();
					pvpDefenceBonus += target.getGameStats().getStat(StatEnum.PVP_PHYSICAL_DEFEND, 0).getCurrent();
					break;
				case FIRE:
				case WATER:
				case WIND:
				case EARTH:
				case LIGHT:
				case DARK:
					pvpAttackBonus += attacker.getGameStats().getStat(StatEnum.PVP_MAGICAL_ATTACK, 0).getCurrent();
					pvpDefenceBonus += target.getGameStats().getStat(StatEnum.PVP_MAGICAL_DEFEND, 0).getCurrent();
					break;
				default:
					break;
			}
			
			pvpAttackBonus = pvpAttackBonus * 0.001f;
			pvpDefenceBonus = pvpDefenceBonus * 0.001f;
			damages = Math.round((damages + (damages * pvpAttackBonus)) - (damages * pvpDefenceBonus));
			
			// Apply Race modifier
			if ((attacker.getRace() != target.getRace()) && !attacker.isInInstance())
			{
				damages *= Influence.getInstance().getPvpRaceBonus(attacker.getRace());
			}
		}
		else if (target instanceof Npc)
		{
			final int levelDiff = target.getLevel() - attacker.getLevel();
			damages *= (1f - getNpcLevelDiffMod(levelDiff, 0));
		}
		
		if (useMovement)
		{
			damages = movementDamageBonus(attacker, damages);
		}
		
		if (attacker instanceof Player)
		{
			final PlayerClass playerClass = ((Player) attacker).getPlayerClass();
			if (playerClass != null)
			{
				switch (playerClass)
				{
					case RIDER:
						damages *= 0.8f;
						break;
					case GUNNER:
						damages *= 0.7f;
						break;
					case BARD:
						damages *= 0.7f;
						break;
					case SORCERER:
						damages *= 0.7f;
						break;
					default:
						damages *= 1f;
				}
			}
		}
		
		return damages;
	}
	
	/**
	 * Determines if an attack successfully dodges based on physical stats.<br>
	 * This method compares the attacker's accuracy against the target's evasion.<br>
	 * It handles special cases like blinded status and NPC level differences.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param attacked The {@link Creature} receiving the attack.
	 * @param accMod An additional modifier applied to the attacker's accuracy.
	 * @return {@code true} if the attack is dodged, {@code false} otherwise.
	 */
	public static boolean calculatePhysicalDodgeRate(Creature attacker, Creature attacked, int accMod)
	{
		// Check if the attacker is blinded and always dodge.
		if (attacker.getObserveController().checkAttackerStatus(AttackStatus.DODGE) || attacked.getObserveController().checkAttackStatus(AttackStatus.DODGE))
		{
			return true;
		}
		
		final float accuracy = attacker.getGameStats().getMainHandPAccuracy().getCurrent() + accMod;
		float dodge = 0;
		if (attacked instanceof Player)
		{
			dodge = attacked.getGameStats().getEvasion().getBonus() + getMovementModifier(attacked, StatEnum.EVASION, attacked.getGameStats().getEvasion().getBase()) + attacked.getGameStats().getStat(StatEnum.PVP_DODGE, 0).getCurrent();
		}
		else
		{
			dodge = attacked.getGameStats().getEvasion().getBonus() + getMovementModifier(attacked, StatEnum.EVASION, attacked.getGameStats().getEvasion().getBase());
		}
		
		float dodgeRate = dodge - accuracy;
		if (attacked instanceof Npc)
		{
			final int levelDiff = attacked.getLevel() - attacker.getLevel();
			dodgeRate *= 1 + getNpcLevelDiffMod(levelDiff, 0);
			
			// static npcs never dodge
			if (((Npc) attacked).hasStatic())
			{
				return false;
			}
		}
		
		return calculatePhysicalEvasion(dodgeRate, 300);
	}
	
	/**
	 * Determines if an attack from one creature is successfully parried by another.<br>
	 * This method checks for constant parry statuses and calculates the final rate based on stats.<br>
	 * It compares the attacker's accuracy against the defender's parry bonuses.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param attacked The {@link Creature} receiving the attack.
	 * @return {@code true} if the attack is parried, {@code false} otherwise.
	 */
	public static boolean calculatePhysicalParryRate(Creature attacker, Creature attacked)
	{
		// check always parry
		if (attacked.getObserveController().checkAttackStatus(AttackStatus.PARRY))
		{
			return true;
		}
		
		final float accuracy = attacker.getGameStats().getMainHandPAccuracy().getCurrent();
		float parry = 0;
		if (attacked instanceof Player)
		{
			parry = attacked.getGameStats().getParry().getBonus() + getMovementModifier(attacked, StatEnum.PARRY, attacked.getGameStats().getParry().getBase()) + attacked.getGameStats().getStat(StatEnum.PVP_PARRY, 0).getCurrent();
		}
		else
		{
			parry = attacked.getGameStats().getParry().getBonus() + getMovementModifier(attacked, StatEnum.PARRY, attacked.getGameStats().getParry().getBase());
		}
		
		final float parryRate = parry - accuracy;
		return calculatePhysicalEvasion(parryRate, 400);
	}
	
	/**
	 * Determines if an attack is successfully blocked by the target.<br>
	 * This method compares the attacker's accuracy against the defender's block stats.<br>
	 * It returns {@code true} if the random roll falls within the calculated block rate.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param attacked The {@link Creature} receiving the attack.
	 * @return {@code true} if the attack is blocked, {@code false} otherwise.
	 */
	public static boolean calculatePhysicalBlockRate(Creature attacker, Creature attacked)
	{
		// check always block
		if (attacked.getObserveController().checkAttackStatus(AttackStatus.BLOCK))
		{
			return true;
		}
		
		final float accuracy = attacker.getGameStats().getMainHandPAccuracy().getCurrent();
		float block = 0;
		if (attacked instanceof Player)
		{
			block = attacked.getGameStats().getBlock().getBonus() + getMovementModifier(attacked, StatEnum.BLOCK, attacked.getGameStats().getBlock().getBase()) + attacked.getGameStats().getStat(StatEnum.PVP_BLOCK, 0).getCurrent();
		}
		else
		{
			block = attacked.getGameStats().getBlock().getBonus() + getMovementModifier(attacked, StatEnum.BLOCK, attacked.getGameStats().getBlock().getBase());
		}
		
		float blockRate = block - accuracy;
		
		// blockRate = blockRate*0.6f+50;
		if (blockRate > 500)
		{
			blockRate = 500;
		}
		
		return Rnd.nextInt(1000) < blockRate;
	}
	
	/**
	 * Calculates the probability of a physical evasion occurring.<br>
	 * This method adjusts the input difference and compares it against an upper limit.<br>
	 * It then returns {@code true} if a random roll succeeds based on the final value.
	 * @param diff The base difference used to determine evasion chance.
	 * @param upperCap The maximum allowed value for the evasion calculation.
	 * @return {@code true} if the evasion is successful, otherwise {@code false}.
	 */
	public static boolean calculatePhysicalEvasion(float diff, int upperCap)
	{
		diff = (diff * 0.6f) + 50;
		if (diff > upperCap)
		{
			diff = upperCap;
		}
		
		return Rnd.nextInt(1000) < diff;
	}
	
	/**
	 * Determines if an attack results in a physical critical hit.<br>
	 * This method calculates the final rate based on attacker stats, defender resistance, and skill modifiers.<br>
	 * It returns {@code true} if the random roll succeeds.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param attacked The {@link Creature} receiving the attack.
	 * @param isMainHand Indicates if the attack is performed with the main hand.
	 * @param criticalProb The additional critical probability percentage to apply.
	 * @param isSkill Indicates if the attack is a skill.
	 * @return {@code true} if the hit is a critical, {@code false} otherwise.
	 */
	public static boolean calculatePhysicalCriticalRate(Creature attacker, Creature attacked, boolean isMainHand, int criticalProb, boolean isSkill)
	{
		if ((attacker instanceof Servant) || (attacker instanceof Homing))
		{
			return false;
		}
		
		int critical;
		if ((attacker instanceof Player) && !isMainHand)
		{
			critical = ((PlayerGameStats) attacker.getGameStats()).getOffHandPCritical().getCurrent();
		}
		else
		{
			critical = attacker.getGameStats().getMainHandPCritical().getCurrent();
		}
		
		// check one time boost skill critical
		final AttackerCriticalStatus acStatus = attacker.getObserveController().checkAttackerCriticalStatus(AttackStatus.CRITICAL, isSkill);
		if (acStatus.isResult())
		{
			if (acStatus.isPercent())
			{
				critical *= (1 + (acStatus.getValue() / 100));
			}
			else
			{
				return Rnd.nextInt(1000) < acStatus.getValue();
			}
		}
		
		critical = attacked.getGameStats().getPositiveReverseStat(StatEnum.PHYSICAL_CRITICAL_RESIST, critical) - attacker.getGameStats().getStat(StatEnum.PVP_HIT_ACCURACY, 0).getCurrent();
		
		// add critical Prob
		critical *= criticalProb / 100f;
		
		double criticalRate;
		
		if (critical <= 440)
		{
			criticalRate = critical * 0.1f;
		}
		else if (critical <= 600)
		{
			criticalRate = (440 * 0.1f) + ((critical - 440) * 0.05f);
		}
		else
		{
			criticalRate = (440 * 0.1f) + (160 * 0.05f) + ((critical - 600) * 0.02f);
		}
		
		return Rnd.nextInt(100) < criticalRate;
	}
	
	/**
	 * Calculates the magical resistance rate between two creatures.<br>
	 * This method determines how likely an attack will be resisted based on stats and levels.<br>
	 * It accounts for magical resistance, accuracy, and level differences.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param attacked The {@link Creature} receiving the attack.
	 * @param accMod An additional modifier applied to the accuracy calculation.
	 * @return The calculated magical resistance rate as an integer.
	 */
	public static int calculateMagicalResistRate(Creature attacker, Creature attacked, int accMod)
	{
		if (attacked.getObserveController().checkAttackStatus(AttackStatus.RESIST))
		{
			return 1000;
		}
		
		final int attackerLevel = attacker.getLevel();
		final int targetLevel = attacked.getLevel();
		
		int resistRate = attacked.getGameStats().getMResist().getCurrent() - attacker.getGameStats().getMAccuracy().getCurrent() - attacker.getGameStats().getStat(StatEnum.PVP_MAGICAL_HIT_ACCURACY, 0).getCurrent() - accMod;
		
		if ((targetLevel - attackerLevel) > 2)
		{
			resistRate += (targetLevel - attackerLevel - 2) * 100;
		}
		
		// if MR < MA - never resist
		if (resistRate <= 0)
		{
			resistRate = 1; // its 0.1% because its min possible
		}
		
		if (resistRate > 500)
		{
			resistRate = 500;
		}
		
		return resistRate;
	}
	
	/**
	 * Determines if a {@link Player} should take damage from falling.<br>
	 * This method checks the fall distance and movement status to apply health reduction.<br>
	 * It handles both lethal falls and partial damage based on configuration settings.
	 * @param player The {@link Player} who fell.
	 * @param distance The height of the fall in meters.
	 * @param stoped A boolean indicating if the player has stopped moving.
	 * @return {@code true} if the player died from the fall, otherwise {@code false}.
	 */
	public static boolean calculateFallDamage(Player player, float distance, boolean stoped)
	{
		if (player.isInvul())
		{
			return false;
		}
		
		if ((distance >= FallDamageConfig.MAXIMUM_DISTANCE_DAMAGE) || !stoped)
		{
			player.getController().onStopMove();
			player.getFlyController().onStopGliding(false);
			player.getLifeStats().reduceHp(player.getLifeStats().getMaxHp() + 1, player);
			return true;
		}
		else if (distance >= FallDamageConfig.MINIMUM_DISTANCE_DAMAGE)
		{
			final float dmgPerMeter = (player.getLifeStats().getMaxHp() * FallDamageConfig.FALL_DAMAGE_PERCENTAGE) / 100f;
			final int damage = (int) (distance * dmgPerMeter);
			player.getLifeStats().reduceHp(damage, player);
			player.getObserveController().notifyAttackedObservers(player);
			PacketSendUtility.sendPacket(player, new SM_ATTACK_STATUS(player, player, SM_ATTACK_STATUS.TYPE.FALL_DAMAGE, 0, -damage));
		}
		
		return false;
	}
	
	/**
	 * Calculates a movement modifier for a {@link Creature} based on its heading.<br>
	 * This method applies specific adjustments to stats like {@code SPEED} or {@code EVASION}.<br>
	 * It only modifies values for {@link Player} objects with valid headings.
	 * @param creature The {@link Creature} to check for movement modifiers.
	 * @param stat The {@link StatEnum} type that will be modified.
	 * @param value The original base value to modify.
	 * @return The adjusted modifier as a {@code float}.
	 */
	public static float getMovementModifier(Creature creature, StatEnum stat, float value)
	{
		if (!(creature instanceof Player) || (stat == null))
		{
			return value;
		}
		
		final Player player = (Player) creature;
		final int h = player.getMoveController().getMovementHeading();
		if (h < 0)
		{
			return value;
		}
		
		// 7 0 1
		// \ | /
		// 6- -2
		// / | \
		// 5 4 3
		switch (h)
		{
			case 7:
			case 0:
			case 1:
				switch (stat)
				{
					case WATER_RESISTANCE:
					case WIND_RESISTANCE:
					case FIRE_RESISTANCE:
					case EARTH_RESISTANCE:
					case ELEMENTAL_RESISTANCE_DARK:
					case ELEMENTAL_RESISTANCE_LIGHT:
					case PHYSICAL_DEFENSE:
						return value * 0.8f;
					default:
						break;
				}
				break;
			case 6:
			case 2:
				switch (stat)
				{
					case EVASION:
						return value + 300;
					case SPEED:
						return value * 0.8f;
					default:
						break;
				}
				break;
			case 5:
			case 4:
			case 3:
				switch (stat)
				{
					case PARRY:
					case BLOCK:
						return value + 500;
					case SPEED:
						return value * 0.6f;
					default:
						break;
				}
				break;
		}
		
		return value;
	}
	
	/**
	 * Calculates the damage bonus based on a {@link Creature}'s movement heading.<br>
	 * This method applies specific multipliers if the target is a {@link Player}.<br>
	 * It returns the modified damage value.
	 * @param creature The {@code Creature} to check for movement direction.
	 * @param value The base damage {@code float} value to modify.
	 * @return The final calculated damage bonus as a {@code float}.
	 */
	private static float movementDamageBonus(Creature creature, float value)
	{
		if (!(creature instanceof Player))
		{
			return value;
		}
		
		final Player player = (Player) creature;
		final int h = player.getMoveController().getMovementHeading();
		if (h < 0)
		{
			return value;
		}
		
		switch (h)
		{
			case 7:
			case 0:
			case 1:
				value = value * 1.1f;
				break;
			case 6:
			case 2:
				value -= value * 0.2f;
				break;
			case 5:
			case 4:
			case 3:
				value -= value * 0.2f;
				break;
		}
		
		return value;
	}
	
	/**
	 * Calculates the modifier for NPC level differences.<br>
	 * This method returns a specific float value based on the {@code levelDiff}.<br>
	 * If no specific case is met, it defaults to the provided {@code base} value.
	 * @param levelDiff The difference in levels between the player and the NPC.
	 * @param base The default modifier to return if no conditions are met.
	 * @return The calculated float modifier for the level difference.
	 */
	private static float getNpcLevelDiffMod(int levelDiff, int base)
	{
		switch (levelDiff)
		{
			case 3:
				return 0.1f;
			case 4:
				return 0.2f;
			case 5:
				return 0.3f;
			case 6:
				return 0.4f;
			case 7:
				return 0.5f;
			case 8:
				return 0.6f;
			case 9:
				return 0.7f;
			default:
				if (levelDiff > 9)
				{
					return 0.8f;
				}
		}
		
		return base;
	}
}
