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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HitType;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * Provides utility methods for handling various attack-related logic.<br>
 * This class simplifies common operations involving {@link Creature} interactions and damage calculations.
 * @author ATracer
 */
public class AttackUtil
{
	/**
	 * Calculates the results of a physical attack between two creatures.<br>
	 * This method determines damage for both main hand and off-hand weapons.<br>
	 * It also checks for shield statuses on the target.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param attacked The {@link Creature} receiving the attack.
	 * @return A {@code List} of {@link AttackResult} objects containing the calculation details.
	 */
	public static List<AttackResult> calculatePhysicalAttackResult(Creature attacker, Creature attacked)
	{
		final AttackStatus attackerStatus = null;
		final int damage = StatFunctions.calculateAttackDamage(attacker, attacked, true, SkillElement.NONE);
		final List<AttackResult> attackList = new ArrayList<>();
		final AttackStatus mainHandStatus = calculateMainHandResult(attacker, attacked, attackerStatus, damage, attackList);
		
		if ((attacker instanceof Player) && (((Player) attacker).getEquipment().getOffHandWeaponType() != null))
		{
			calculateOffHandResult(attacker, attacked, mainHandStatus, attackList);
		}
		
		attacked.getObserveController().checkShieldStatus(attackList, null, attacker);
		return attackList;
	}
	
	/**
	 * Calculates the result of a main hand physical attack.<br>
	 * It determines the number of hits based on the attacker type and weapon stats.<br>
	 * The method then splits the damage into multiple results.
	 * @param attacker The {@code Creature} performing the attack.
	 * @param attacked The {@code Creature} receiving the attack.
	 * @param attackerStatus The current {@code AttackStatus} of the attacker, or {@code null}.
	 * @param damage The total amount of damage to be distributed.
	 * @param attackList The list where new {@code AttackResult} objects are added.
	 * @return The final {@code AttackStatus} for the main hand attack.
	 */
	private static AttackStatus calculateMainHandResult(Creature attacker, Creature attacked, AttackStatus attackerStatus, int damage, List<AttackResult> attackList)
	{
		AttackStatus mainHandStatus = attackerStatus;
		if (mainHandStatus == null)
		{
			mainHandStatus = calculatePhysicalStatus(attacker, attacked, true);
		}
		
		int mainHandHits = 1;
		if (attacker instanceof Player)
		{
			final Item mainHandWeapon = ((Player) attacker).getEquipment().getMainHandWeapon();
			if (mainHandWeapon != null)
			{
				mainHandHits = Rnd.get(1, mainHandWeapon.getItemTemplate().getWeaponStats().getHitCount());
			}
		}
		else
		{
			mainHandHits = Rnd.get(1, 3);
		}
		
		splitPhysicalDamage(attacker, attacked, mainHandHits, damage, mainHandStatus, attackList);
		return mainHandStatus;
	}
	
	/**
	 * Calculates the damage and results for an off-hand weapon attack.<br>
	 * This method uses the {@code mainHandStatus} to derive the necessary stats.<br>
	 * It then updates the provided {@code attackList} with the new results.
	 * @param attacker The creature performing the attack.
	 * @param attacked The creature receiving the attack.
	 * @param mainHandStatus The status result from the primary hand attack.
	 * @param attackList The list of attack results to be updated.
	 */
	private static void calculateOffHandResult(Creature attacker, Creature attacked, AttackStatus mainHandStatus, List<AttackResult> attackList)
	{
		final AttackStatus offHandStatus = AttackStatus.getOffHandStats(mainHandStatus);
		final Item offHandWeapon = ((Player) attacker).getEquipment().getOffHandWeapon();
		final int offHandDamage = StatFunctions.calculateAttackDamage(attacker, attacked, false, SkillElement.NONE);
		final int offHandHits = Rnd.get(1, offHandWeapon.getItemTemplate().getWeaponStats().getHitCount());
		splitPhysicalDamage(attacker, attacked, offHandHits, offHandDamage, offHandStatus, attackList);
	}
	
	/**
	 * Splits the total physical damage into multiple hits based on the hit count.<br>
	 * This method applies damage reductions for block, dodge, and parry statuses.<br>
	 * It also calculates critical damage if the {@code status} is a critical hit.<br>
	 * The final results are added to the provided {@code attackList}.
	 * @param attacker The creature performing the attack.
	 * @param attacked The creature receiving the damage.
	 * @param hitCount The total number of hits to split the damage into.
	 * @param damage The base amount of physical damage to be distributed.
	 * @param status The current {@link AttackStatus} of the attack.
	 * @param attackList The list where new {@link AttackResult} objects will be stored.
	 * @return The updated list of {@link AttackResult} objects.
	 */
	private static List<AttackResult> splitPhysicalDamage(Creature attacker, Creature attacked, int hitCount, int damage, AttackStatus status, List<AttackResult> attackList)
	{
		WeaponType weaponType;
		
		switch (AttackStatus.getBaseStatus(status))
		{
			case BLOCK:
				int reduce = damage - attacked.getGameStats().getPositiveReverseStat(StatEnum.DAMAGE_REDUCE, damage);
				if (attacked instanceof Player)
				{
					final Item shield = ((Player) attacked).getEquipment().getEquippedShield();
					if (shield != null)
					{
						final int reduceMax = shield.getItemTemplate().getWeaponStats().getReduceMax();
						if ((reduceMax > 0) && (reduceMax < reduce))
						{
							reduce = reduceMax;
						}
					}
				}
				
				damage -= reduce;
				break;
			case DODGE:
				damage = 0;
				break;
			case PARRY:
				damage *= 0.6;
				break;
			default:
				break;
		}
		
		if (status.isCritical())
		{
			if (attacker instanceof Player)
			{
				weaponType = ((Player) attacker).getEquipment().getMainHandWeaponType();
				damage = (int) calculateWeaponCritical(attacked, damage, weaponType, StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE);
				
				// Proc Stumble/Stagger on Crit calculation
				applyEffectOnCritical((Player) attacker, attacked, 0);
			}
			else
			{
				damage = (int) calculateWeaponCritical(attacked, damage, null, StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE);
			}
		}
		
		if (damage < 1)
		{
			damage = 0;
		}
		
		final int firstHit = (int) (damage * (1f - (0.1f * (hitCount - 1))));
		final int otherHits = Math.round(damage * 0.1f);
		for (int i = 0; i < hitCount; i++)
		{
			final int dmg = (i == 0 ? firstHit : otherHits);
			attackList.add(new AttackResult(dmg, status, HitType.PHHIT));
		}
		
		return attackList;
	}
	
	/**
	 * Calculates the final damage for a weapon critical hit.<br>
	 * This method determines how much extra damage is applied based on specific stats.<br>
	 * It uses the {@code attacked} creature and the base {@code damages} as inputs.
	 * @param attacked The {@link Creature} being hit by the attack.
	 * @param damages The base amount of damage to be calculated.
	 * @param weaponType The {@link WeaponType} of the weapon used for the attack.
	 * @param stat The specific {@link StatEnum} used to determine the critical multiplier.
	 * @return The final calculated damage as a {@code float}.
	 */
	private static float calculateWeaponCritical(Creature attacked, float damages, WeaponType weaponType, StatEnum stat)
	{
		return calculateWeaponCritical(attacked, damages, weaponType, 0, stat);
	}
	
	/**
	 * Calculates the final damage for a weapon critical hit.<br>
	 * This method determines the multiplier based on the {@code WeaponType}.<br>
	 * It also adjusts the damage using player stats and additional critical modifiers.
	 * @param attacked The {@link Creature} being hit by the attack.
	 * @param damages The base damage amount before critical calculation.
	 * @param weaponType The type of weapon used for the attack.
	 * @param critAddDmg The extra damage value to add to the critical multiplier.
	 * @param stat The {@link StatEnum} used to determine specific reductions or modifiers.
	 * @return The final calculated damage as a {@code float}.
	 */
	private static float calculateWeaponCritical(Creature attacked, float damages, WeaponType weaponType, int critAddDmg, StatEnum stat)
	{
		float coeficient = 2f;
		
		if (weaponType != null)
		{
			switch (weaponType)
			{
				case GUN_1H:
				case DAGGER_1H:
					coeficient = 2.3f;
					break;
				case SWORD_1H:
					coeficient = 2.2f;
					break;
				case MACE_1H:
					coeficient = 2f;
					break;
				case SWORD_2H:
				case POLEARM_2H:
					// NOTE: Handled by Default (1.5f)
					// case CANNON_2H:
					// case KEYBLADE_2H:
					// case KEYHAMMER_2H:
					coeficient = 1.8f;
					break;
				case STAFF_2H:
				case BOW:
					coeficient = 1.7f;
					break;
				default:
					coeficient = 1.5f;
					break;
			}
			
			if (stat.equals(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE))
			{
				coeficient = 1.5f; // Magical skill with physical weapon TODO: confirm this
			}
		}
		
		if (attacked instanceof Player)
		{
			// Strike Fortitude lowers the crit multiplier
			final Player player = (Player) attacked;
			int fortitude = 0;
			switch (stat)
			{
				case PHYSICAL_CRITICAL_DAMAGE_REDUCE:
				case MAGICAL_CRITICAL_DAMAGE_REDUCE:
					fortitude = player.getGameStats().getStat(stat, 0).getCurrent();
					coeficient -= Math.round(fortitude / 1000f);
					break;
				default:
					break;
			}
		}
		
		// add critical add dmg
		coeficient += critAddDmg / 100f;
		
		damages = Math.round(damages * coeficient);
		
		if (attacked instanceof Npc)
		{
			damages = attacked.getAi2().modifyDamage((int) damages);
		}
		
		return damages;
	}
	
	/**
	 * Calculates the final damage and attack status for a specific effect.<br>
	 * This method handles physical and magical damage logic.<br>
	 * It applies modifiers, critical hits, and random damage variations.<br>
	 * Finally, it calls {@code Creature, int, AttackStatus, HitType, boolean)} to apply the result.
	 * @param effect The {@code Effect} object containing effector and effected data.
	 * @param skillDamage The base damage value of the skill.
	 * @param modifier An optional {@link ActionModifier} to apply extra bonuses.
	 * @param func A {@link Func} used to determine how skill damage is added.
	 * @param randomDamage An integer code for applying specific random damage multipliers.
	 * @param accMod The accuracy modification value.
	 * @param criticalProb The probability of landing a critical hit.
	 * @param critAddDmg The additional damage applied on a critical hit.
	 * @param cannotMiss A boolean indicating if the attack is guaranteed to hit.
	 * @param shared A boolean determining if damage should be split among multiple targets
	 * @param ignoreShield
	 * @param isMainHand
	 */
	public static void calculateSkillResult(Effect effect, int skillDamage, ActionModifier modifier, Func func, int randomDamage, int accMod, int criticalProb, int critAddDmg, boolean cannotMiss, boolean shared, boolean ignoreShield, boolean isMainHand)
	{
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		
		int damage = 0;
		int baseAttack = 0;
		/**
		 * - Some Archdaeva equipment will give boosted combat stats against certain monster types. - If the gear and the monster type match, you will get bonus damage. - Some items focus on a single monster type while others can affect multiple types. - There are four monster types in total: Warrior, Assassin, Mage, and Special.
		 */
		if (effector.getEffectController().hasAbnormalEffect(22987) && effector.getEffectController().hasAbnormalEffect(22988) && effector.getEffectController().hasAbnormalEffect(22989) && effector.getEffectController().hasAbnormalEffect(22990))
		{
			damage = (StatFunctions.calculatePhysicalAttackDamage(effect.getEffector(), effect.getEffected(), true) * 2) / 100;
		}
		
		if (effector.getAttackType() == ItemAttackType.PHYSICAL)
		{
			baseAttack = effector.getGameStats().getMainHandPAttack().getBase();
			damage = StatFunctions.calculatePhysicalAttackDamage(effector, effected, true);
		}
		else
		{
			if (isMainHand)
			{
				baseAttack = effector.getGameStats().getMainHandMAttack().getBase();
			}
			else
			{
				baseAttack = effector.getGameStats().getOffHandMAttack().getBase();
			}
			
			damage = StatFunctions.calculateMagicalAttackDamage(effector, effected, effector.getAttackType().getMagicalElement(), isMainHand);
		}
		
		// add skill damage
		if (func != null)
		{
			switch (func)
			{
				case ADD:
					damage += skillDamage;
					break;
				case PERCENT:
					damage += (baseAttack * skillDamage) / 100f;
					break;
				default:
					break;
			}
		}
		
		// add bonus damage
		if (modifier != null)
		{
			final int bonus = modifier.analyze(effect);
			switch (modifier.getFunc())
			{
				case ADD:
					damage += bonus;
					break;
				case PERCENT:
					damage += (baseAttack * bonus) / 100f;
					break;
				default:
					break;
			}
		}
		
		// adjusting baseDamages according to attacker and target level
		damage = (int) StatFunctions.adjustDamages(effect.getEffector(), effect.getEffected(), damage, effect.getPvpDamage(), true);
		
		final float damageMultiplier = effector.getObserveController().getBasePhysicalDamageMultiplier(true);
		damage = Math.round(damage * damageMultiplier);
		
		// implementation of random damage for skills like Stunning Shot, etc
		if (randomDamage > 0)
		{
			final int randomChance = Rnd.get(100);
			
			// TODO Hard fix
			if (effect.getSkillId() == 20033)
			{
				damage *= 10;
			}
			
			switch (randomDamage)
			{
				case 1:
					if (randomChance <= 40)
					{
						damage /= 2;
					}
					else if (randomChance <= 70)
					{
						damage *= 1.5;
					}
					break;
				case 2:
					if (randomChance <= 25)
					{
						damage *= 3;
					}
					break;
				case 6:
					if (randomChance <= 30)
					{
						damage *= 2;
					}
					break;
				// TODO rest of the cases
				default:
					/*
					 * chance to do from 50% to 200% damage This must NOT be calculated after critical status check, or it will be over powered and not retail
					 */
					damage *= (Rnd.get(25, 100) * 0.02f);
					break;
			}
		}
		
		AttackStatus status = AttackStatus.NORMALHIT;
		if (effector.getAttackType() == ItemAttackType.PHYSICAL)
		{
			status = calculatePhysicalStatus(effector, effected, true, accMod, criticalProb, true, cannotMiss);
		}
		else
		{
			status = calculateMagicalStatus(effector, effected, criticalProb, true);
		}
		
		switch (AttackStatus.getBaseStatus(status))
		{
			case BLOCK:
				int reduce = damage - effected.getGameStats().getPositiveReverseStat(StatEnum.DAMAGE_REDUCE, damage);
				if (effected instanceof Player)
				{
					final Item shield = ((Player) effected).getEquipment().getEquippedShield();
					if (shield != null)
					{
						final int reduceMax = shield.getItemTemplate().getWeaponStats().getReduceMax();
						if ((reduceMax > 0) && (reduceMax < reduce))
						{
							reduce = reduceMax;
						}
					}
				}
				
				damage -= reduce;
				break;
			case PARRY:
				damage *= 0.6;
				break;
			default:
				break;
		}
		
		if (status.isCritical())
		{
			if (effector instanceof Player)
			{
				final WeaponType weaponType = ((Player) effector).getEquipment().getMainHandWeaponType();
				damage = (int) calculateWeaponCritical(effected, damage, weaponType, critAddDmg, StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE);
				
				// Proc Stumble/Stagger on Crit calculation
				applyEffectOnCritical((Player) effector, effected, effect.getSkillId());
			}
			else
			{
				damage = (int) calculateWeaponCritical(effected, damage, null, critAddDmg, StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE);
			}
		}
		
		if (effected instanceof Npc)
		{
			damage = effected.getAi2().modifyDamage(damage);
		}
		
		if (effector instanceof Npc)
		{
			damage = effector.getAi2().modifyOwnerDamage(damage);
		}
		
		if (shared && !effect.getSkill().getEffectedList().isEmpty())
		{
			damage /= effect.getSkill().getEffectedList().size();
		}
		
		if (damage < 0)
		{
			damage = 0;
		}
		
		calculateEffectResult(effect, effected, damage, status, HitType.PHHIT, ignoreShield);
	}
	
	/**
	 * Processes the final result of an effect on a target creature.<br>
	 * This method updates the {@code Effect} object with data from an {@code AttackResult}.<br>
	 * It also checks for shield status if {@code ignoreShield} is {@code false}.
	 * @param effect The {@code Effect} being processed.
	 * @param effected The {@code Creature} receiving the effect.
	 * @param damage The amount of damage dealt.
	 * @param status The {@code AttackStatus} of the hit.
	 * @param hitType The type of the hit.
	 * @param ignoreShield Whether to skip shield checks.
	 */
	private static void calculateEffectResult(Effect effect, Creature effected, int damage, AttackStatus status, HitType hitType, boolean ignoreShield)
	{
		final AttackResult attackResult = new AttackResult(damage, status, hitType);
		if (!ignoreShield)
		{
			effected.getObserveController().checkShieldStatus(Collections.singletonList(attackResult), effect, effect.getEffector());
		}
		
		effect.setReserved1(attackResult.getDamage());
		effect.setAttackStatus(attackResult.getAttackStatus());
		effect.setLaunchSubEffect(attackResult.isLaunchSubEffect());
		effect.setReflectedDamage(attackResult.getReflectedDamage());
		effect.setReflectedSkillId(attackResult.getReflectedSkillId());
		effect.setMpShield(attackResult.getShieldMp());
		effect.setProtectedDamage(attackResult.getProtectedDamage());
		effect.setProtectedSkillId(attackResult.getProtectedSkillId());
		effect.setProtectorId(attackResult.getProtectorId());
		effect.setShieldDefense(attackResult.getShieldType());
	}
	
	/**
	 * Calculates the result of a magical attack between two creatures.<br>
	 * This method determines damage and status for both main hand and off-hand weapons.<br>
	 * It also applies level adjustments and shield checks.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param attacked The {@link Creature} receiving the attack.
	 * @param elem The {@link SkillElement} type of the magical attack.
	 * @return A {@code List} of {@link AttackResult} objects containing damage and status.
	 */
	public static List<AttackResult> calculateMagicalAttackResult(Creature attacker, Creature attacked, SkillElement elem)
	{
		final List<AttackResult> attackList = new ArrayList<>();
		
		int damage = StatFunctions.calculateAttackDamage(attacker, attacked, true, elem);
		
		// calculate status
		final AttackStatus status = calculateMagicalStatus(attacker, attacked, 100, false);
		
		if (status == AttackStatus.CRITICAL)
		{
			damage = (int) calculateWeaponCritical(attacked, damage, ((Player) attacker).getEquipment().getMainHandWeaponType(), StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
		}
		
		// adjusting baseDamages according to attacker and target level
		damage = (int) StatFunctions.adjustDamages(attacker, attacked, damage, 0, false);
		
		if (damage <= 0)
		{
			damage = 1;
		}
		
		switch (status)
		{
			case RESIST:
			case CRITICAL_RESIST:
				damage = 0;
				break;
			default:
				break;
		}
		
		attackList.add(new AttackResult(damage, status));
		
		// calculate offhand damage
		if ((attacker instanceof Player) && (((Player) attacker).getEquipment().getOffHandWeaponType() != null))
		{
			int offHandDamage = StatFunctions.calculateAttackDamage(attacker, attacked, false, elem);
			
			final AttackStatus offHandStatus = calculateMagicalStatus(attacker, attacked, 100, false);
			if (offHandStatus == AttackStatus.CRITICAL)
			{
				offHandDamage = (int) calculateWeaponCritical(attacked, damage, ((Player) attacker).getEquipment().getMainHandWeaponType(), StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
			}
			
			offHandDamage = (int) StatFunctions.adjustDamages(attacker, attacked, damage, 0, false);
			
			if (offHandDamage <= 0)
			{
				offHandDamage = 1;
			}
			
			switch (offHandStatus)
			{
				case RESIST:
				case CRITICAL_RESIST:
					offHandDamage = 0;
					break;
				default:
					break;
			}
			
			attackList.add(new AttackResult(offHandDamage, status));
		}
		
		// check for shield
		attacked.getObserveController().checkShieldStatus(attackList, null, attacker);
		
		return attackList;
	}
	
	/**
	 * Calculates the result of a homing attack between two creatures.<br>
	 * This method determines the damage and status based on the provided {@code SkillElement}.<br>
	 * It applies reductions for actions like {@code PARRY} or {@code BLOCK}.
	 * @param attacker The creature performing the attack.
	 * @param attacked The creature receiving the attack.
	 * @param elem The element type of the skill used.
	 * @return A list containing the final {@code AttackResult}.
	 */
	public static List<AttackResult> calculateHomingAttackResult(Creature attacker, Creature attacked, SkillElement elem)
	{
		int damage = StatFunctions.calculateAttackDamage(attacker, attacked, true, elem);
		
		final AttackStatus status = calculateHomingAttackStatus(attacker, attacked);
		final List<AttackResult> attackList = new ArrayList<>();
		switch (status)
		{
			case RESIST:
			case DODGE:
				damage = 0;
				break;
			case PARRY:
				damage *= 0.6;
				break;
			case BLOCK:
				damage /= 2;
				break;
			default:
				break;
		}
		
		attackList.add(new AttackResult(damage, status));
		attacked.getObserveController().checkShieldStatus(attackList, null, attacker);
		return attackList;
	}
	
	/**
	 * Calculates the final damage for a magical over-time skill effect.<br>
	 * This method applies multipliers, elements, and critical hit logic.<br>
	 * It also adjusts damage based on target types like {@code Npc}.
	 * @param effect The {@link Effect} object containing the effector and effected creatures.
	 * @param skillDamage The base damage value of the skill.
	 * @param element The {@link SkillElement} type of the attack.
	 * @param position The hit position index for the attack.
	 * @param useMagicBoost Whether to apply the magic boost modifier.
	 * @param criticalProb The probability of landing a critical hit.
	 * @param critAddDmg The additional damage added by a critical hit.
	 * @return The final calculated damage as an {@code int}.
	 */
	public static int calculateMagicalOverTimeSkillResult(Effect effect, int skillDamage, SkillElement element, int position, boolean useMagicBoost, int criticalProb, int critAddDmg)
	{
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		
		// TODO is damage multiplier used on dot?
		final float damageMultiplier = effector.getObserveController().getBaseMagicalDamageMultiplier();
		
		int damage = Math.round(StatFunctions.calculateMagicalSkillDamage(effect.getEffector(), effect.getEffected(), skillDamage, 0, element, useMagicBoost, false, false, effect.getSkillTemplate().getPvpDamage()) * damageMultiplier);
		
		AttackStatus status = effect.getAttackStatus();
		
		// calculate attack status only if it has not been forced already
		if ((status == AttackStatus.NORMALHIT) && (position == 1))
		{
			status = calculateMagicalStatus(effector, effected, criticalProb, true);
		}
		
		switch (status)
		{
			case CRITICAL:
				if (effector instanceof Player)
				{
					final WeaponType weaponType = ((Player) effector).getEquipment().getMainHandWeaponType();
					damage = (int) calculateWeaponCritical(effected, damage, weaponType, critAddDmg, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
				}
				else
				{
					damage = (int) calculateWeaponCritical(effected, damage, null, critAddDmg, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
				}
				break;
			default:
				break;
		}
		
		if (damage <= 0)
		{
			damage = 1;
		}
		
		if (effected instanceof Npc)
		{
			damage = effected.getAi2().modifyDamage(damage);
		}
		
		return damage;
	}
	
	/**
	 * Calculates the final result of a magical skill attack.<br>
	 * This method processes damage based on specific effects and modifiers.<br>
	 * It is used to determine how much magic damage is dealt to a target.
	 * @param effect The {@link Effect} applied by the skill.
	 * @param skillDamage The base damage value of the skill.
	 * @param modifier The {@link ActionModifier} affecting the action.
	 * @param element The {@link SkillElement} type of the magic.
	 */
	public static void calculateMagicalSkillResult(Effect effect, int skillDamage, ActionModifier modifier, SkillElement element)
	{
		calculateMagicalSkillResult(effect, skillDamage, modifier, element, true, true, false, Func.ADD, 100, 0, false, false);
	}
	
	/**
	 * Calculates the final result of a magical skill attack.<br>
	 * This method determines the total damage and applies various modifiers like critical hits and shared damage.<br>
	 * It then calls {@code Creature, int, AttackStatus, HitType, boolean)} to apply the outcome.
	 * @param effect The {@code Effect} object containing the effector and effected creatures.
	 * @param skillDamage The base damage value of the skill.
	 * @param modifier An optional {@link ActionModifier} to adjust the damage calculation.
	 * @param element The {@link SkillElement} type of the magic used.
	 * @param useMagicBoost Whether to apply the magic boost multiplier.
	 * @param useKnowledge Whether to include knowledge stats in the calculation.
	 * @param noReduce Whether to ignore certain damage reductions.
	 * @param func The {@link Func} type used for calculating bonus damage.
	 * @param criticalProb The probability of landing a critical hit.
	 * @param critAddDmg The additional damage added on a critical hit.
	 * @param shared Whether to divide the damage among multiple targets. @
	 * @param ignoreShield
	 */
	public static void calculateMagicalSkillResult(Effect effect, int skillDamage, ActionModifier modifier, SkillElement element, boolean useMagicBoost, boolean useKnowledge, boolean noReduce, Func func, int criticalProb, int critAddDmg, boolean shared, boolean ignoreShield)
	{
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		
		final float damageMultiplier = effector.getObserveController().getBaseMagicalDamageMultiplier();
		final int baseAttack = effector.getGameStats().getMainHandPAttack().getBase(); // Npc spells scale with this
		int damages = 0;
		int bonus = 0;
		
		if (func.equals(Func.PERCENT) && (effector instanceof Npc))
		{
			damages = Math.round((baseAttack * skillDamage) / 100f);
		}
		else
		{
			damages = skillDamage;
		}
		
		// add bonus damage
		if (modifier != null)
		{
			bonus = modifier.analyze(effect);
			switch (modifier.getFunc())
			{
				case ADD:
					break;
				case PERCENT:
					if (effector instanceof Npc)
					{
						bonus = Math.round((baseAttack * bonus) / 100f);
					}
					break;
				default:
					break;
			}
		}
		
		int damage = Math.round(StatFunctions.calculateMagicalSkillDamage(effect.getEffector(), effect.getEffected(), damages, bonus, element, useMagicBoost, useKnowledge, noReduce, effect.getSkillTemplate().getPvpDamage()) * damageMultiplier);
		
		final AttackStatus status = calculateMagicalStatus(effector, effected, criticalProb, true);
		switch (status)
		{
			case CRITICAL:
				if (effector instanceof Player)
				{
					final WeaponType weaponType = ((Player) effector).getEquipment().getMainHandWeaponType();
					damage = (int) calculateWeaponCritical(effected, damage, weaponType, critAddDmg, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
				}
				else
				{
					damage = (int) calculateWeaponCritical(effected, damage, null, critAddDmg, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
				}
				break;
			default:
				break;
		}
		
		if (shared && !effect.getSkill().getEffectedList().isEmpty())
		{
			damage /= effect.getSkill().getEffectedList().size();
		}
		
		calculateEffectResult(effect, effected, damage, status, HitType.MAHIT, ignoreShield);
	}
	
	/**
	 * Calculates the physical status of an attack.<br>
	 * This method determines how damage is applied between two creatures.<br>
	 * It takes into account whether the attack is performed with the main hand.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param attacked The {@link Creature} receiving the attack.
	 * @param isMainHand A boolean indicating if the attack uses the main hand weapon.
	 * @return The resulting {@code AttackStatus} of the physical calculation.
	 */
	public static AttackStatus calculatePhysicalStatus(Creature attacker, Creature attacked, boolean isMainHand)
	{
		return calculatePhysicalStatus(attacker, attacked, isMainHand, 0, 100, false, false);
	}
	
	/**
	 * Calculates the physical attack status between two creatures.<br>
	 * This method determines if an attack results in a hit, dodge, parry, block, or critical success.<br>
	 * It takes into account weapon types, equipment, and various combat modifiers.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param attacked The {@link Creature} receiving the attack.
	 * @param isMainHand Indicates if the attack is performed with the main hand ({@code true}) or off-hand ({@code false}).
	 * @param accMod The accuracy modifier used for dodge calculations.
	 * @param criticalProb The probability of landing a critical hit.
	 * @param isSkill Indicates if the attack is a skill rather than a basic physical attack.
	 * @param cannotMiss Indicates if the attack is guaranteed to land.
	 * @return The resulting {@link AttackStatus} of the interaction.
	 */
	public static AttackStatus calculatePhysicalStatus(Creature attacker, Creature attacked, boolean isMainHand, int accMod, int criticalProb, boolean isSkill, boolean cannotMiss)
	{
		AttackStatus status = AttackStatus.NORMALHIT;
		if (!isMainHand)
		{
			status = AttackStatus.OFFHAND_NORMALHIT;
		}
		
		if (!cannotMiss)
		{
			if ((attacked instanceof Player) && ((Player) attacked).getEquipment().isShieldEquipped() && StatFunctions.calculatePhysicalBlockRate(attacker, attacked))// TODO accMod
			{
				status = AttackStatus.BLOCK;
			} // Parry can only be done with weapon, also weapon can have humanoid mobs, but for now there isnt implementation of monster category
			else if ((attacked instanceof Player) && (((Player) attacked).getEquipment().getMainHandWeaponType() != null) && StatFunctions.calculatePhysicalParryRate(attacker, attacked))// TODO accMod
			{
				status = AttackStatus.PARRY;
			}
			else if (!isSkill && StatFunctions.calculatePhysicalDodgeRate(attacker, attacked, accMod))
			{
				status = AttackStatus.DODGE;
			}
		}
		else
		{
			/**
			 * Check AlwaysDodge Check AlwaysParry Check AlwaysBlock
			 */
			StatFunctions.calculatePhysicalDodgeRate(attacker, attacked, accMod);
			StatFunctions.calculatePhysicalParryRate(attacker, attacked);
			StatFunctions.calculatePhysicalBlockRate(attacker, attacked);
		}
		
		if (StatFunctions.calculatePhysicalCriticalRate(attacker, attacked, isMainHand, criticalProb, isSkill))
		{
			switch (status)
			{
				case BLOCK:
					if (isMainHand)
					{
						status = AttackStatus.CRITICAL_BLOCK;
					}
					else
					{
						status = AttackStatus.OFFHAND_CRITICAL_BLOCK;
					}
					break;
				case PARRY:
					if (isMainHand)
					{
						status = AttackStatus.CRITICAL_PARRY;
					}
					else
					{
						status = AttackStatus.OFFHAND_CRITICAL_PARRY;
					}
					break;
				case DODGE:
					if (isMainHand)
					{
						status = AttackStatus.CRITICAL_DODGE;
					}
					else
					{
						status = AttackStatus.OFFHAND_CRITICAL_DODGE;
					}
					break;
				default:
					if (isMainHand)
					{
						status = AttackStatus.CRITICAL;
					}
					else
					{
						status = AttackStatus.OFFHAND_CRITICAL;
					}
					break;
			}
		}
		
		return status;
	}
	
	/**
	 * Calculates the magical attack status between two creatures.<br>
	 * This method determines if an attack is a resist, critical hit, or normal hit.<br>
	 * It uses {@link StatFunctions} to check resistance and critical rates.
	 * @param attacker The creature performing the attack.
	 * @param attacked The creature receiving the attack.
	 * @param criticalProb The base probability for a critical hit.
	 * @param isSkill A boolean flag indicating if the attack is a skill.
	 * @return The resulting {@code AttackStatus} of the magical attack.
	 */
	public static AttackStatus calculateMagicalStatus(Creature attacker, Creature attacked, int criticalProb, boolean isSkill)
	{
		if (!isSkill)
		{
			if (Rnd.get(0, 1000) < StatFunctions.calculateMagicalResistRate(attacker, attacked, 0))
			{
				return AttackStatus.RESIST;
			}
		}
		
		if (StatFunctions.calculateMagicalCriticalRate(attacker, attacked, criticalProb))
		{
			return AttackStatus.CRITICAL;
		}
		
		return AttackStatus.NORMALHIT;
	}
	
	/**
	 * Determines the outcome of a homing attack between two creatures.<br>
	 * This method checks for resistance, dodge, parry, and block chances.<br>
	 * It returns {@code AttackStatus.NORMALHIT} if no defensive actions succeed.
	 * @param attacker The creature performing the attack.
	 * @param attacked The creature receiving the attack.
	 * @return The resulting {@code AttackStatus} of the interaction.
	 */
	private static AttackStatus calculateHomingAttackStatus(Creature attacker, Creature attacked)
	{
		if (Rnd.get(0, 1000) < StatFunctions.calculateMagicalResistRate(attacker, attacked, 0))
		{
			return AttackStatus.RESIST;
		}
		
		else if (StatFunctions.calculatePhysicalDodgeRate(attacker, attacked, 0))
		{
			return AttackStatus.DODGE;
		}
		
		else if (StatFunctions.calculatePhysicalParryRate(attacker, attacked))
		{
			return AttackStatus.PARRY;
		}
		
		else if (StatFunctions.calculatePhysicalBlockRate(attacker, attacked))
		{
			return AttackStatus.BLOCK;
		}
		else
		{
			return AttackStatus.NORMALHIT;
		}
		
	}
	
	/**
	 * Cancels the casting action of all players and NPCs targeting a specific creature.<br>
	 * This method checks for observers who have the {@code target} as their current goal.<br>
	 * It then calls {@code Creature)} or the equivalent NPC version.
	 * @param target The {@code Creature} whose targeters will have their casting cancelled.
	 */
	public static void cancelCastOn(Creature target)
	{
		target.getKnownList().doOnAllPlayers(observer ->
		{
			if (observer.getTarget() == target)
			{
				cancelCast(observer, target);
			}
		});
		
		target.getKnownList().doOnAllNpcs(observer ->
		{
			if (observer.getTarget() == target)
			{
				cancelCast(observer, target);
			}
		});
	}
	
	/**
	 * Checks if a {@link Creature} is currently casting a skill on a specific target.<br>
	 * If the target matches the first required target, it cancels the current skill.
	 * @param creature The entity performing the action.
	 * @param target The entity being targeted by the action.
	 */
	private static void cancelCast(Creature creature, Creature target)
	{
		if ((target != null) && (creature.getCastingSkill() != null))
		{
			if (creature.getCastingSkill().getFirstTarget().equals(target))
			{
				creature.getController().cancelCurrentSkill();
			}
		}
	}
	
	/**
	 * Removes the specified {@link Creature} from the current target list.<br>
	 * This method updates the targeting state of the entity.
	 * @param object The {@code Creature} to be removed.
	 */
	public static void removeTargetFrom(Creature object)
	{
		removeTargetFrom(object, false);
	}
	
	/**
	 * Removes the specified {@code Creature} from all players who have it as a target.<br>
	 * This method handles visibility checks if {@code validateSee} is set to {@code true}.<br>
	 * It also sends an {@link com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED} packet to update the client.
	 * @param object The {@code Creature} to be removed from target lists.
	 * @param validateSee A boolean flag to determine if visibility should be checked before removal.
	 */
	public static void removeTargetFrom(Creature object, boolean validateSee)
	{
		object.getKnownList().doOnAllPlayers(observer ->
		{
			if (validateSee && (observer.getTarget() == object))
			{
				if (!observer.canSee(object))
				{
					observer.setTarget(null);
					
					// retail packet (//fsc 0x44 dhdd 0 0 0 0) right after SM_PLAYER_STATE
					PacketSendUtility.sendPacket(observer, new SM_TARGET_SELECTED(observer));
				}
			}
			else if (observer.getTarget() == object)
			{
				observer.setTarget(null);
				
				// retail packet (//fsc 0x44 dhdd 0 0 0 0) right after SM_PLAYER_STATE
				PacketSendUtility.sendPacket(observer, new SM_TARGET_SELECTED(observer));
			}
		});
	}
	
	/**
	 * Checks if a specific skill ID belongs to the effect categories.<br>
	 * This method identifies skills that trigger Stagger, Stumble, or Openaerial effects.
	 * @param skillId The unique identifier of the skill to check.
	 * @return {@code true} if the skill is an effect; {@code false} otherwise.
	 */
	public static boolean isSkillEffect(int skillId)
	{
		switch (skillId)
		{
			/********************
			 * [Stagger Effect] *
			 ********************/
			case 1054: // Finishing Arrow I.
			case 1055: // Finishing Arrow II.
			case 1056: // Finishing Arrow III.
			case 1102: // Rupture Arrow I.
			case 1103: // Rupture Arrow II.
			case 1104: // Rupture Arrow III.
			case 1105: // Rupture Arrow IV.
			case 1106: // Rupture Arrow V.
			case 1107: // Rupture Arrow VI.
			case 1108: // Rupture Arrow VII.
			case 1109: // Rupture Arrow VIII.
			case 1110: // Rupture Arrow IX.
			case 1226: // Frozen Shock I.
			case 1227: // Frozen Shock II.
			case 1228: // Frozen Shock III.
			case 1229: // Frozen Shock IV.
			case 1230: // Frozen Shock V.
			case 1231: // Frozen Shock VI.
			case 1232: // Frozen Shock VII.
			case 1233: // Frozen Shock VIII.
			case 1234: // Frozen Shock IX.
			case 1235: // Frozen Shock X.
			case 1236: // Frozen Shock XI.
			case 1237: // Frozen Shock XII.
			case 1258: // Aetherflame I.
			case 4728: // [ArchDaeva] Aetherflame 5.1
			case 1826: // Tremor I.
			case 1827: // Tremor II.
			case 1828: // Tremor III.
			case 1829: // Tremor IV.
			case 1830: // Tremor V.
			case 1831: // Tremor VI.
			case 2055: // Trunk Shot I.
			case 2056: // Trunk Shot II.
			case 2057: // Trunk Shot III.
			case 2058: // Trunk Shot IV.
			case 2059: // Trunk Shot V.
			case 2060: // Trunk Shot VI.
			case 2061: // Trunk Shot VII.
			case 2062: // Trunk Shot VIII.
			case 2063: // Trunk Shot IX.
			case 2064: // Trunk Shot X.
			case 2065: // Trunk Shot XI.
			case 2232: // Shock & Awe I.
			case 2235: // Shock & Awe II.
			case 2238: // Shock & Awe III.
			case 2241: // Shock & Awe IV.
			case 2244: // Shock & Awe V.
			case 2247: // Shock & Awe VI.
			case 2250: // Shock & Awe VII.
			case 2253: // Shock & Awe VIII.
			case 3614: // Stone Shock I.
			case 3615: // Stone Shock II.
			case 3616: // Stone Shock III.
			case 3617: // Stone Shock IV.
			case 3618: // Stone Shock V.
			case 3619: // Stone Shock VI.
			case 3620: // Stone Shock VII.
			case 3621: // Stone Shock VIII.
			case 3622: // Stone Shock IX.
			case 3623: // Stone Shock X.
			case 3624: // Stone Shock XI.
			case 4396: // Chorus Of Fortitude I.
			case 4397: // Chorus Of Fortitude II.
			case 4398: // Chorus Of Fortitude III.
			case 4399: // Chorus Of Fortitude IV.
			case 4522: // Sonic Gust I.
			case 4523: // Sonic Gust II.
			case 4790: // [ArchDaeva] Sonic Gust 5.1
				/********************
				 * [Stumble Effect] *
				 ********************/
			case 519: // Explosion Of Rage I.
			case 520: // Explosion Of Rage II.
			case 521: // Explosion Of Rage III.
			case 522: // Explosion Of Rage IV.
			case 523: // Explosion Of Rage V.
			case 524: // Explosion Of Rage VI.
			case 525: // Explosion Of Rage VII.
			case 526: // Explosion Of Rage VIII.
			case 527: // Explosion Of Rage IX.
			case 528: // Explosion Of Rage X.
			case 529: // Explosion Of Rage XI.
			case 530: // Explosion Of Rage XII.
			case 531: // Crushing Blow I.
			case 532: // Crushing Blow II.
			case 533: // Crushing Blow III.
			case 534: // Crushing Blow IV.
			case 535: // Crushing Blow V.
			case 536: // Crushing Blow VI.
			case 537: // Crushing Blow VII.
			case 538: // Crushing Blow VIII.
			case 555: // Seismic Billow I.
			case 556: // Seismic Billow II.
			case 557: // Seismic Billow III.
			case 558: // Seismic Billow IV.
			case 559: // Seismic Billow V.
			case 560: // Seismic Billow VI.
			case 561: // Seismic Billow VII.
			case 562: // Seismic Billow VIII.
			case 584: // Spite Strike I.
			case 585: // Spite Strike II.
			case 586: // Spite Strike III.
			case 587: // Spite Strike IV.
			case 588: // Spite Strike V.
			case 589: // Spite Strike VI.
			case 621: // Wrathful Explosion I.
			case 622: // Wrathful Explosion II.
			case 623: // Wrathful Explosion III.
			case 624: // Wrathful Strike I.
			case 625: // Wrathful Strike II.
			case 626: // Wrathful Strike III.
			case 627: // Wrathful Strike IV.
			case 628: // Wrathful Strike V.
			case 629: // Wrathful Strike VI.
			case 630: // Wrathful Strike VII.
			case 631: // Wrathful Strike VIII.
			case 632: // Wrathful Strike IX.
			case 633: // Wrathful Strike X.
			case 634: // Wrathful Strike XI.
			case 635: // Wrathful Wave I.
			case 636: // Wrathful Wave II.
			case 637: // Wrathful Wave III.
			case 638: // Wrathful Wave IV.
			case 639: // Wrathful Wave V.
			case 640: // Wrathful Wave VI.
			case 728: // Wind Lance I.
			case 729: // Wind Lance II.
			case 730: // Wind Lance III.
			case 731: // Wind Lance IV.
			case 732: // Wind Lance V.
			case 733: // Severe Precision Cut I.
			case 734: // Severe Precision Cut II.
			case 735: // Severe Precision Cut III.
			case 736: // Severe Precision Cut IV.
			case 737: // Severe Precision Cut V.
			case 738: // Severe Precision Cut VI.
			case 1863: // Disorienting Blow I.
			case 1864: // Disorienting Blow II.
			case 1865: // Disorienting Blow III.
			case 1866: // Disorienting Blow IV.
			case 1867: // Disorienting Blow V.
			case 1868: // Disorienting Blow VI.
			case 1875: // Pentacle Shock I.
			case 1876: // Pentacle Shock II.
			case 1877: // Pentacle Shock III.
			case 1878: // Pentacle Shock IV.
			case 1879: // Pentacle Shock V.
			case 1880: // Pentacle Shock VI.
			case 1881: // Pentacle Shock VII.
			case 1882: // Pentacle Shock VIII.
			case 1891: // Soul Crush I.
			case 1892: // Soul Crush II.
			case 1893: // Soul Crush III.
			case 1894: // Soul Crush IV.
			case 1895: // Soul Crush V.
			case 1896: // Soul Crush VI.
			case 1897: // Soul Crush VII.
			case 1898: // Soul Crush VIII.
			case 2399: // Beatdown I.
			case 2530: // Annihilation Barrage I.
			case 2531: // Annihilation Barrage II.
			case 2532: // Annihilation Barrage III.
			case 2533: // Annihilation Barrage IV.
			case 2534: // Annihilation Barrage V.
			case 2535: // Annihilation Barrage VI.
			case 2568: // Uppercut I.
			case 2569: // Uppercut II.
			case 2570: // Uppercut III.
			case 2571: // Uppercut IV.
			case 2606: // Kinetic Slam I.
			case 2609: // Kinetic Slam II.
			case 2612: // Kinetic Slam III.
			case 2615: // Kinetic Slam IV.
			case 2618: // Kinetic Slam V.
			case 2621: // Kinetic Slam VI.
			case 2624: // Kinetic Slam VII.
			case 2627: // Kinetic Slam VIII.
			case 2630: // Kinetic Slam IX.
			case 2633: // Kinetic Slam X.
			case 2336: // Kinetic Slam XI.
			case 2639: // Kinetic Slam XII.
			case 4797: // [ArchDaeva] Kinetic Slam 5.1
			case 4798: // [ArchDaeva] Kinetic Slam 5.1
			case 4799: // [ArchDaeva] Kinetic Slam 5.1
			case 2923: // Shieldburst I.
			case 2924: // Shieldburst II.
			case 2925: // Shieldburst III.
			case 3106: // Face Smash I.
			case 3107: // Face Smash II.
			case 3108: // Face Smash III.
			case 3109: // Face Smash IV.
			case 3110: // Face Smash V.
			case 3111: // Face Smash VI.
			case 3112: // Face Smash VII.
			case 3113: // Swinging Shield Counter I.
			case 3114: // Swinging Shield Counter II.
			case 3115: // Swinging Shield Counter III.
			case 3125: // Sword Storm I.
			case 3126: // Sword Storm II.
			case 3330: // Shadowfall I.
			case 4591: // Shadowfall II.
			case 4592: // Shadowfall III.
			case 4593: // Shadowfall IV.
			case 4594: // Shadowfall V.
			case 4595: // Shadowfall VI.
			case 4596: // Shadowfall VII.
				/***********************
				 * [Openaerial Effect] *
				 ***********************/
			case 545: // Aerial Lockdown I.
			case 546: // Aerial Lockdown II.
			case 547: // Aerial Lockdown III.
			case 548: // Aerial Lockdown IV.
			case 549: // Aerial Lockdown V.
			case 550: // Aerial Lockdown VI.
			case 551: // Aerial Lockdown VII.
			case 552: // Aerial Lockdown VIII.
			case 553: // Aerial Lockdown IX.
			case 1184: // Aether's Hold I.
			case 1185: // Aether's Hold II.
			case 1186: // Aether's Hold III.
			case 1187: // Aether's Hold IV.
			case 1188: // Aether's Hold V.
			case 1189: // Aether's Hold VI.
			case 1190: // Aether's Hold VII.
			case 1191: // Aether's Hold VIII.
			case 2109: // Paralysis Cannon I.
			case 2110: // Paralysis Cannon II.
			case 2111: // Paralysis Cannon III.
			case 2112: // Paralysis Cannon IV.
			case 2113: // Paralysis Cannon V.
			case 2114: // Paralysis Cannon VI.
			case 3406: // Binding Rune I.
			case 3407: // Binding Rune II.
			case 3408: // Binding Rune III.
			case 3409: // Binding Rune IV.
			case 3410: // Binding Rune V.
			case 3411: // Binding Rune VI.
			case 3412: // Binding Rune VII.
			case 3413: // Binding Rune VIII.
			case 3414: // Binding Rune IX.
				/*******************
				 * [Pulled Effect] *
				 *******************/
			case 326: // Sweeping Hook.
			case 2967: // Illusion Chains.
			case 4721: // [ArchDaeva] Illusion Chains 5.1
			case 3071: // Ensnaring Blow.
			case 3123: // Doom Lure.
			case 3162: // Divine Grasp I.
			case 3163: // Divine Grasp II.
			case 3164: // Divine Grasp III.
			case 3165: // Divine Grasp IV.
			case 3166: // Divine Grasp V.
			case 3167: // Divine Grasp VI.
				return true;
		}
		
		return false;
	}
	
	/**
	 * Applies a special effect when an attack results in a critical hit.<br>
	 * This method checks for specific weapon types and handles the logic for applying effects based on probabilities.<br>
	 * It ensures that certain conditions, like active status effects or skill IDs, are met before execution.
	 * @param attacker The {@link Player} who performs the attack.
	 * @param attacked The {@link Creature} receiving the damage.
	 * @param returnSkill The specific skill ID to check for effect overrides.
	 */
	public static void applyEffectOnCritical(Player attacker, Creature attacked, int returnSkill)
	{
		int skillId = 0;
		
		// players with Remove Shock cant be effected
		for (Effect ef : attacked.getEffectController().getAbnormalEffects())
		{
			if (ef.getSkillId() == 1968)
			{
				return;
			}
		}
		
		final WeaponType mainHandWeaponType = attacker.getEquipment().getMainHandWeaponType();
		if (mainHandWeaponType != null)
		{
			switch (mainHandWeaponType)
			{
				case POLEARM_2H:
				case CANNON_2H:
				case STAFF_2H:
				case SWORD_2H:
				case KEYBLADE_2H:
				case KEYHAMMER_2H:
					skillId = 8218;
					break;
				case BOW:
					skillId = 8217;
					break;
				default:
					break;
			}
		}
		
		// On retail this effect apply on each crit with 10% of base chance plus bonus effect penetration calculated above
		if ((skillId == 0) || (Rnd.get(100) > (6 * attacked.getCriticalEffectMulti())) || isSkillEffect(returnSkill))
		{
			return;
		}
		
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (template == null)
		{
			return;
		}
		
		final Effect e = new Effect(attacker, attacked, template, template.getLvl(), 0);
		e.initialize();
		e.applyEffect();
	}
}
