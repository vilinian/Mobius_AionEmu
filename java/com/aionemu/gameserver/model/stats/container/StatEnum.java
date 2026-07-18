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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.items.ItemSlot;

/**
 * Defines the various types of statistics available within the game.<br>
 * This enumeration is used to categorize different {@link com.aionemu.gameserver.model.items.ItemSlot} attributes.
 * @author xavier
 * @author ATracer
 */
@XmlType(name = "StatEnum")
@XmlEnum
public enum StatEnum
{
	MAXDP(22), // Maximum DP
	MAXHP(18), // HP
	MAXMP(20), // MP
	
	AGILITY(9, true),
	BLOCK(33),
	EVASION(31),
	CONCENTRATION(41),
	WILL(11, true),
	HEALTH(7, true),
	ACCURACY(8, true),
	KNOWLEDGE(10, true),
	PARRY(32),
	POWER(6, true),
	SPEED(36, true),
	ALLSPEED,
	WEIGHT(39, true),
	HIT_COUNT(35, true),
	ATTACK_RANGE(38, true), // Atk Range
	ATTACK_SPEED(29, -1, true), // Atk Speed
	PHYSICAL_ATTACK(25), // Attack
	PHYSICAL_ACCURACY(30), // Accuracy
	PHYSICAL_CRITICAL(34), // Critical Strike
	PHYSICAL_DEFENSE(26), // Physical Def
	MAIN_HAND_HITS,
	MAIN_HAND_ACCURACY,
	MAIN_HAND_CRITICAL,
	MAIN_HAND_POWER,
	MAIN_HAND_ATTACK_SPEED,
	OFF_HAND_HITS,
	OFF_HAND_ACCURACY,
	OFF_HAND_CRITICAL,
	OFF_HAND_POWER,
	OFF_HAND_ATTACK_SPEED,
	MAGICAL_ATTACK(27), // Magical Attack
	MAIN_HAND_MAGICAL_ATTACK,
	OFF_HAND_MAGICAL_ATTACK,
	MAGICAL_ACCURACY(105),
	MAIN_HAND_MAGICAL_ACCURACY,
	OFF_HAND_MAGICAL_ACCURACY,
	MAGICAL_CRITICAL(40), // Critical Spell
	MAGICAL_RESIST(28), // Magic Resist
	MAIN_HAND_MAGICAL_POWER,
	MAIN_HAND_MAGICAL_CRITICAL,
	OFF_HAND_MAGICAL_POWER,
	OFF_HAND_MAGICAL_CRITICAL,
	MAX_DAMAGES,
	MIN_DAMAGES,
	IS_MAGICAL_ATTACK(0, true),
	EARTH_RESISTANCE(14),
	FIRE_RESISTANCE(15),
	WIND_RESISTANCE(13),
	WATER_RESISTANCE(12),
	DARK_RESISTANCE(17),
	LIGHT_RESISTANCE(16),
	BOOST_MAGICAL_SKILL(104),
	BOOST_SPELL_ATTACK,
	BOOST_CASTING_TIME(108), // Casting Speed
	BOOST_CASTING_TIME_HEAL,
	BOOST_CASTING_TIME_TRAP,
	BOOST_CASTING_TIME_ATTACK,
	BOOST_CASTING_TIME_SKILL,
	BOOST_CASTING_TIME_SUMMONHOMING,
	BOOST_CASTING_TIME_SUMMON,
	BOOST_HATE(109), // Enmity Boost
	
	FLY_TIME(23),
	FLY_SPEED(37),
	FLYBOOST_SPEED,
	DAMAGE_REDUCE,
	DAMAGE_REDUCE_MAX,
	BLEED_RESISTANCE(44), // Bleed Resist
	BLIND_RESISTANCE(48), // Blind Resist
	BLOCK_PENETRATION,
	BIND_RESISTANCE,
	CHARM_RESISTANCE(49), // Charm Resist
	CONFUSE_RESISTANCE(54), // Confusion Resist
	CURSE_RESISTANCE(53), // Curse Resist
	DISEASE_RESISTANCE(50), // Disease Resist
	DEFORM_RESISTANCE,
	FEAR_RESISTANCE(52), // Fear Resist
	OPENAREIAL_RESISTANCE(59), // Aether's Hold Resist
	PARALYZE_RESISTANCE(45), // Paralysis Resistance
	PERIFICATION_RESISTANCE(56), // Petrification Resist
	POISON_RESISTANCE(43), // Poison Resist
	PULLED_RESISTANCE, // TODO: Find ID !!!
	ROOT_RESISTANCE(47), // Immobilization Resist
	SILENCE_RESISTANCE(51),
	SLEEP_RESISTANCE(46), // Sleep Resist
	SLOW_RESISTANCE(60), // Reduce Speed Resist
	SNARE_RESISTANCE(61), // Reduce Attack Speed Resist
	SPIN_RESISTANCE(62), // Spin Resist
	STAGGER_RESISTANCE(58), // Knock Back Resist
	STUMBLE_RESISTANCE(57), // Stumble Resist
	STUN_RESISTANCE(55), // Stun Resist
	
	SILENCE_RESISTANCE_PENETRATION(77), // Silence Resistance Penetration
	PARALYZE_RESISTANCE_PENETRATION(71), // Paralysis Resistance Penetration
	POISON_RESISTANCE_PENETRATION(69), // Poisoning Penetration
	BLEED_RESISTANCE_PENETRATION(70), // Bleeding Penetration
	SLEEP_RESISTANCE_PENETRATION(72), // Sleep Penetration
	ROOT_RESISTANCE_PENETRATION(73), // Immobilization Penetration
	BLIND_RESISTANCE_PENETRATION(74), // Blindness Penetration
	CHARM_RESISTANCE_PENETRATION(75), // Char Penetration
	DISEASE_RESISTANCE_PENETRATION(76), // Disease Penetration
	FEAR_RESISTANCE_PENETRATION(78), // Fear Penetration
	SPIN_RESISTANCE_PENETRATION(88), // Spin Penetration
	CURSE_RESISTANCE_PENETRATION(79), // Curse Penetration
	CONFUSE_RESISTANCE_PENETRATION(80), // Confusion Penetration
	STUN_RESISTANCE_PENETRATION(81), // Stun Penetration
	PERIFICATION_RESISTANCE_PENETRATION(82), // Petrification Penetration
	STUMBLE_RESISTANCE_PENETRATION(83), // Stumble Penetration
	STAGGER_RESISTANCE_PENETRATION(84), // Knock Back Penetration
	OPENAREIAL_RESISTANCE_PENETRATION(85), // Aether's Hold Penetration
	SNARE_RESISTANCE_PENETRATION(87), // Reduce Attack Speed Penetration
	SLOW_RESISTANCE_PENETRATION(86), // Reduce Movement Speed Penetration
	REGEN_MP(21), // Natural Mana Treatment
	REGEN_HP(19), // Natural Healing
	REGEN_FP(24), // Natural Flight Serum
	HEAL_BOOST(110), // Healing Boost, not BOOST_CASTING_TIME_HEAL ?
	HEAL_SKILL_BOOST,
	HEAL_SKILL_DEBOOST,
	ALLRESIST(2), // All Stats ?
	STUNLIKE_RESISTANCE,
	ELEMENTAL_RESISTANCE_DARK,
	ELEMENTAL_RESISTANCE_LIGHT,
	MAGICAL_CRITICAL_RESIST(116), // Spell Resist
	MAGICAL_CRITICAL_DAMAGE_REDUCE(118), // Spell Fortitude
	PHYSICAL_CRITICAL_RESIST(115), // Strike Resist
	PHYSICAL_CRITICAL_DAMAGE_REDUCE(117), // Strike Fortitude
	ERFIRE,
	ERAIR,
	EREARTH,
	ERWATER,
	ABNORMAL_RESISTANCE_ALL(1), // All Altered State Resist ?
	ALLPARA,
	KNOWIL(4), // Knowledge and Will
	AGIDEX(5), // Accuracy and Agility
	STRVIT(3), // Power and Health
	
	MAGICAL_DEFEND(125), // Magical Defense
	MAGIC_SKILL_BOOST_RESIST(126), // Magic Supression
	
	// Effects stats (bossts, deboosts)
	BOOST_HUNTING_XP_RATE,
	BOOST_GROUP_HUNTING_XP_RATE,
	BOOST_QUEST_XP_RATE,
	BOOST_CRAFTING_XP_RATE, // for all craft skills
	BOOST_COOKING_XP_RATE,
	BOOST_WEAPONSMITHING_XP_RATE,
	BOOST_ARMORSMITHING_XP_RATE,
	BOOST_TAILORING_XP_RATE,
	BOOST_ALCHEMY_XP_RATE,
	BOOST_HANDICRAFTING_XP_RATE,
	BOOST_MENUISIER_XP_RATE,
	BOOST_GATHERING_XP_RATE, // for all gathering skills
	BOOST_AETHERTAPPING_XP_RATE,
	BOOST_ESSENCETAPPING_XP_RATE,
	BOOST_DROP_RATE,
	BOOST_MANTRA_RANGE,
	BOOST_DURATION_BUFF, // extend_duration
	BOOST_RESIST_DEBUFF,
	
	// 3.5
	ELEMENTAL_FIRE,
	
	// PvP and PvE
	PVP_PHYSICAL_ATTACK,
	PVP_PHYSICAL_DEFEND,
	PVP_MAGICAL_ATTACK,
	PVP_MAGICAL_DEFEND,
	PVP_ATTACK_RATIO(106),
	PVP_ATTACK_RATIO_MAGICAL(111),
	PVP_ATTACK_RATIO_PHYSICAL(113),
	PVP_DEFEND_RATIO(107),
	PVP_DEFEND_RATIO_PHYSICAL(112),
	PVP_DEFEND_RATIO_MAGICAL(114),
	PVE_ATTACK_RATIO,
	PVE_ATTACK_RATIO_MAGICAL,
	PVE_ATTACK_RATIO_PHYSICAL,
	PVE_DEFEND_RATIO,
	PVE_DEFEND_RATIO_PHYSICAL,
	PVE_DEFEND_RATIO_MAGICAL,
	AP_BOOST,
	DR_BOOST,
	BOOST_CHARGE_TIME,
	
	// 4.7
	PHYSICAL_DAMAGE,
	MAGICAL_DAMAGE,
	PHYSICAL_CRITICAL_REDUCE_RATE,
	MAGICAL_CRITICAL_REDUCE_RATE,
	PROC_REDUCE_RATE,
	PVP_DODGE,
	PVP_BLOCK,
	PVP_PARRY,
	PVP_HIT_ACCURACY,
	PVP_MAGICAL_RESIST,
	PVP_MAGICAL_HIT_ACCURACY,
	
	// 4.8
	ENCHANT_BOOST,
	AP_REDUCE_RATE,
	AUTHORIZE_BOOST,
	INDUN_DROP_BOOST,
	DEATH_PENALTY_REDUCE,
	ENCHANT_OPTION_BOOST,
	ORDALIE_REWARD,
	
	HIDDEN_PVE_ATTACK_RATIO,
	HIDDEN_PVE_DEFEND_RATIO,
	BOOST_BOOK_XP_RATE,
	
	// new 7.x
	PVP_ATTACK,
	PVP_DEFENSE,
	PVE_ATTACK,
	PVE_DEFENSE,
	
	EXTRA_ERESHKIGAL_DAMAGE,
	REDUCE_ERESHKIGAL_DAMAGE,
	idf7_weapon_hard_boss_1st_atk,
	idf7_weapon_hard_boss_2nd_atk,
	idf7_weapon_hard_boss_3rd_atk,
	idf7_weapon_hard_boss_final_atk,
	bidldf8_lab_boss_04_atk,
	idf8_house_hugerider_atk,
	IDF7_Weapon_Hard_Boss_1st,
	IDF7_Weapon_Hard_Boss_2nd,
	IDF7_Weapon_Hard_Boss_3rd,
	IDF7_Weapon_Hard_Boss_Final,
	IDLDF8_Lab_Boss,
	IDF8_House_HugeRider,
	idseal_hard_boss_1st_atk,
	idf8_Dragon_Altar_atk,
	idseal_hard_boss_1st,
	idf8_Dragon_Altar,
	idseal_hard_boss_2nd_atk,
	idseal_hard_boss_2nd,
	cubic_stat_catacombs_3rd_atk,
	cubic_stat_catacombs_3rd,
	idseal_hard_boss_3rd_atk,
	idseal_hard_boss_3rd;
	
	// If STAT id is 135, the Shrewd Cloth Set was checked up to 160 in version 3.5.
	private final boolean replace;
	private final int sign;
	private final int itemStoneMask;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This enum should only be accessed via its static factory methods.
	 */
	private StatEnum()
	{
		this(0);
	}
	
	/**
	 * Creates a new {@link StatEnum} instance using a default sign and replace value.<br>
	 * This constructor calls the internal constructor with a sign of {@code 1} and {@code false} for replacement.
	 * @param stoneMask The bitmask representing the specific stat.
	 */
	private StatEnum(int stoneMask)
	{
		this(stoneMask, 1, false);
	}
	
	/**
	 * Creates a new {@link StatEnum} instance using a stone mask and a replacement flag.<br>
	 * This constructor sets the sign to {@code 1} by default.
	 * @param stoneMask The bitmask representing the specific stat.
	 * @param replace A boolean indicating if this stat should replace others.
	 */
	private StatEnum(int stoneMask, boolean replace)
	{
		this(stoneMask, 1, replace);
	}
	
	/**
	 * Creates a new {@link StatEnum} instance using a stone mask and a specific sign.<br>
	 * This constructor sets the replacement flag to {@code false}.
	 * @param stoneMask The bitmask representing the item stat.
	 * @param sign The numerical value associated with the stat.
	 */
	private StatEnum(int stoneMask, int sign)
	{
		this(stoneMask, sign, false);
	}
	
	/**
	 * Constructs a new {@link StatEnum} with specific properties.<br>
	 * This constructor sets the mask, sign, and replacement flag for the stat.
	 * @param stoneMask The bitmask identifying the specific stat.
	 * @param sign The numerical value associated with the stat's direction or type.
	 * @param replace A boolean indicating if this stat should overwrite others.
	 */
	private StatEnum(int stoneMask, int sign, boolean replace)
	{
		itemStoneMask = stoneMask;
		this.replace = replace;
		this.sign = sign;
	}
	
	/**
	 * Retrieves the sign value associated with this {@code StatEnum}.<br>
	 * This value is used to determine specific behavior for certain stats.
	 * @return The integer value of the sign.
	 */
	public int getSign()
	{
		return sign;
	}
	
	/**
	 * Retrieves the unique bitmask associated with this {@code StatEnum}.<br>
	 * This value is used to identify specific stats for items.
	 * @return The integer mask representing the stat.
	 */
	public int getItemStoneMask()
	{
		return itemStoneMask;
	}
	
	/**
	 * Finds a {@link StatEnum} based on its unique stone mask.<br>
	 * This method searches through all available values to match the provided integer.<br>
	 * It throws an {@code IllegalArgumentException} if no matching mask is found.
	 * @param mask The integer value of the item stone mask to search for.
	 * @return The corresponding {@link StatEnum} constant.
	 */
	public static StatEnum findByItemStoneMask(int mask)
	{
		for (StatEnum sEnum : values())
		{
			if (sEnum.getItemStoneMask() == mask)
			{
				return sEnum;
			}
		}
		
		throw new IllegalArgumentException("Cannot find StatEnum for stone mask: " + mask);
	}
	
	/**
	 * Finds a {@link StatEnum} constant based on its string name.<br>
	 * This method is case-insensitive when comparing the input.<br>
	 * It throws an {@code IllegalArgumentException} if no match is found.
	 * @param name The string name of the stat to search for.
	 * @return The matching {@link StatEnum} constant.
	 */
	public static StatEnum findByStringName(String name)
	{
		for (StatEnum sEnum : values())
		{
			if (sEnum.name().equalsIgnoreCase(name))
			{
				return sEnum;
			}
		}
		
		throw new IllegalArgumentException("Cannot find StatEnum by name: " + name);
	}
	
	/**
	 * Determines the specific stat based on the provided item slot.<br>
	 * This method checks if a general stat should be split into main hand or off hand variants.<br>
	 * It uses {@link ItemSlot} to identify which hand is being referenced.
	 * @param itemSlot The unique ID mask of the item slot.
	 * @return The specific {@code StatEnum} for the given slot.
	 */
	public StatEnum getHandStat(long itemSlot)
	{
		switch (this)
		{
			case MAGICAL_ATTACK:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_MAGICAL_ATTACK : OFF_HAND_MAGICAL_ATTACK;
			case MAGICAL_ACCURACY:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_MAGICAL_ACCURACY : OFF_HAND_MAGICAL_ACCURACY;
			case PHYSICAL_ATTACK:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_POWER : OFF_HAND_POWER;
			case PHYSICAL_ACCURACY:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_ACCURACY : OFF_HAND_ACCURACY;
			case PHYSICAL_CRITICAL:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_CRITICAL : OFF_HAND_CRITICAL;
			default:
				return this;
		}
	}
	
	/**
	 * Checks if the current {@code StatEnum} belongs to a main or sub-hand weapon statistic.<br>
	 * This method returns {@code true} for stats like power, accuracy, and critical strike.<br>
	 * It returns {@code false} for all other types of statistics.
	 * @return {@code true} if the stat is related to hand weapons, otherwise {@code false}.
	 */
	public boolean isMainOrSubHandStat()
	{
		switch (this)
		{
			case MAGICAL_ATTACK:
			case MAGICAL_ACCURACY:
			case PHYSICAL_ATTACK:
			case POWER:
			case PHYSICAL_ACCURACY:
			case PHYSICAL_CRITICAL:
				return true;
			
			default:
				return false;
		}
	}
	
	/**
	 * Checks if this stat type should replace other values.<br>
	 * This method returns the value of the {@code replace} flag.
	 * @return {@code true} if it replaces others, {@code false} otherwise.
	 */
	public boolean isReplace()
	{
		return replace;
	}
	
	/**
	 * Retrieves the {@link StatEnum} associated with a specific skill ID.<br>
	 * This method maps various crafting and tapping skills to their respective experience rate modifiers.
	 * @param skillId The unique identifier for the skill.
	 * @return The corresponding {@link StatEnum} or {@code null} if no mapping exists.
	 */
	public static StatEnum getModifier(int skillId)
	{
		switch (skillId)
		{
			case 30001:
			case 30002:
				return BOOST_ESSENCETAPPING_XP_RATE;
			case 30003:
				return BOOST_AETHERTAPPING_XP_RATE;
			case 40001:
				return BOOST_COOKING_XP_RATE;
			case 40002:
				return BOOST_WEAPONSMITHING_XP_RATE;
			case 40003:
				return BOOST_ARMORSMITHING_XP_RATE;
			case 40004:
				return BOOST_TAILORING_XP_RATE;
			case 40007:
				return BOOST_ALCHEMY_XP_RATE;
			case 40008:
				return BOOST_HANDICRAFTING_XP_RATE;
			case 40010:
				return BOOST_MENUISIER_XP_RATE;
			case 40011:
				return null; // TODO ?
			default:
				return null;
		}
	}
}
