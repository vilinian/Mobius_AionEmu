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
package com.aionemu.gameserver.skillengine.effect;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.change.Change;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifiers;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HitType;
import com.aionemu.gameserver.skillengine.model.HopType;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.model.SkillType;
import com.aionemu.gameserver.skillengine.model.SpellStatus;
import com.aionemu.gameserver.skillengine.model.TransformType;
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * Represents a base template for all skill effects in the game.<br>
 * It defines the core properties and behaviors that are applied when a skill is executed.<br>
 * This class serves as a blueprint for specific effect types like buffs, debuffs, or damage.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Effect")
public abstract class EffectTemplate
{
	protected ActionModifiers modifiers;
	protected List<Change> change;
	@XmlAttribute
	protected int effectid;
	@XmlAttribute(required = true)
	protected int duration2;
	@XmlAttribute
	protected int duration1;
	@XmlAttribute(name = "randomtime")
	protected int randomTime;
	@XmlAttribute(name = "e")
	protected int position;
	@XmlAttribute(name = "basiclvl")
	protected int basicLvl;
	@XmlAttribute(name = "hittype", required = false)
	protected HitType hitType = HitType.EVERYHIT;
	@XmlAttribute(name = "hittypeprob2", required = false)
	protected float hitTypeProb = 100f;
	@XmlAttribute(name = "element")
	protected SkillElement element = SkillElement.NONE;
	@XmlElement(name = "subeffect")
	protected SubEffect subEffect;
	@XmlElement(name = "conditions")
	protected Conditions effectConditions;
	@XmlElement(name = "subconditions")
	protected Conditions effectSubConditions;
	@XmlAttribute(name = "hoptype")
	protected HopType hopType;
	@XmlAttribute(name = "hopa")
	protected int hopA; // effects the agro-value (hate)
	@XmlAttribute(name = "hopb")
	protected int hopB; // effects the agro-value (hate)
	@XmlAttribute(name = "noresist")
	protected boolean noResist;
	@XmlAttribute(name = "accmod1")
	protected int accMod1; // accdelta
	@XmlAttribute(name = "accmod2")
	protected int accMod2; // accvalue
	@XmlAttribute(name = "preeffect")
	protected String preEffect;
	@XmlAttribute(name = "preeffect_prob")
	protected int preEffectProb = 100;
	@XmlAttribute(name = "critprobmod2")
	protected int critProbMod2 = 100;
	@XmlAttribute(name = "critadddmg1")
	protected int critAddDmg1 = 0;
	@XmlAttribute(name = "critadddmg2")
	protected int critAddDmg2 = 0;
	@XmlAttribute
	protected int value;
	@XmlAttribute
	protected int delta;
	@XmlTransient
	protected EffectType effectType = null;
	@XmlTransient
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return value;
	}
	
	/**
	 * Retrieves the change amount for this effect.<br>
	 * This value represents the {@code delta} associated with the {@link Change} object.
	 * @return The current {@code delta} value as an {@code int}.
	 */
	public int getDelta()
	{
		return delta;
	}
	
	/**
	 * Retrieves the second duration value of the effect.<br>
	 * This value is used to determine how long a specific part of the effect lasts.
	 * @return the {@code int} value of {@code duration2}.
	 */
	public int getDuration2()
	{
		return duration2;
	}
	
	/**
	 * Retrieves the first duration value for this effect.<br>
	 * This value is used to determine how long the effect lasts.
	 * @return the {@code int} value of {@code duration1}
	 */
	public int getDuration1()
	{
		return duration1;
	}
	
	/**
	 * Retrieves a randomly generated time value.<br>
	 * This method returns the {@code randomTime} variable stored in the object.
	 * @return The random time as an {@code int}.
	 */
	public int getRandomTime()
	{
		return randomTime;
	}
	
	/**
	 * Retrieves the list of action modifiers associated with this effect.<br>
	 * This method returns the {@code ActionModifiers} object containing all active changes.
	 * @return The {@code ActionModifiers} instance for this effect.
	 */
	public ActionModifiers getModifiers()
	{
		return modifiers;
	}
	
	/**
	 * Retrieves the list of changes associated with this effect.<br>
	 * This method returns a {@code List} of {@link Change} objects.
	 * @return A {@code List} containing all {@code Change} objects.
	 */
	public List<Change> getChange()
	{
		return change;
	}
	
	/**
	 * Retrieves the unique identifier for this effect.<br>
	 * This value is used to identify specific effects in the system.
	 * @return The {@code int} ID of the effect.
	 */
	public int getEffectid()
	{
		return effectid;
	}
	
	/**
	 * Retrieves the current rank of the player.<br>
	 * This value represents the numerical position in the arena rewards.
	 * @return The integer value of the {@code position}.
	 */
	public int getPosition()
	{
		return position;
	}
	
	/**
	 * Retrieves the base level of the effect.<br>
	 * This value represents the starting level for the {@code Effect}.
	 * @return The integer value of the base level.
	 */
	public int getBasicLvl()
	{
		return basicLvl;
	}
	
	/**
	 * Retrieves the {@link SkillElement} associated with this effect.<br>
	 * This method returns the specific element data used for skill processing.
	 * @return The {@code SkillElement} object.
	 */
	public SkillElement getElement()
	{
		return element;
	}
	
	/**
	 * Retrieves the name of the pre-effect associated with this effect.<br>
	 * This value is used to identify a specific secondary effect triggered before the main one.
	 * @return The {@code String} name of the pre-effect.
	 */
	public String getPreEffect()
	{
		return preEffect;
	}
	
	/**
	 * Retrieves the probability of a pre-effect occurring.<br>
	 * This value is used to determine if an associated pre-effect should trigger.
	 * @return The probability as an {@code int}.
	 */
	public int getPreEffectProb()
	{
		return preEffectProb;
	}
	
	/**
	 * Retrieves the second critical probability modifier.<br>
	 * This value is used to adjust the chance of a critical hit.
	 * @return the {@code int} value of the second critical probability modifier.
	 */
	public int getCritProbMod2()
	{
		return critProbMod2;
	}
	
	/**
	 * Retrieves the first additional critical damage value.<br>
	 * This value is used to increase damage when a critical hit occurs.
	 * @return The {@code int} value of the first additional critical damage.
	 */
	public int getCritAddDmg1()
	{
		return critAddDmg1;
	}
	
	/**
	 * Retrieves the second additional critical damage value.<br>
	 * This value is used to modify damage when a critical hit occurs.
	 * @return The {@code int} value of the second additional critical damage.
	 */
	public int getCritAddDmg2()
	{
		return critAddDmg2;
	}
	
	/**
	 * Retrieves the conditions required for this effect to trigger.<br>
	 * This method returns a {@link Conditions} object containing all necessary rules.
	 * @return the {@code Conditions} associated with this effect.
	 */
	public Conditions getEffectConditions()
	{
		return effectConditions;
	}
	
	/**
	 * Retrieves the sub-conditions associated with this effect.<br>
	 * These conditions are checked in addition to the primary effect conditions.
	 * @return the {@code Conditions} object containing the sub-conditions.
	 */
	public Conditions getEffectSubConditions()
	{
		return effectSubConditions;
	}
	
	/**
	 * Retrieves a specific {@link ActionModifier} for the given {@code effect}.<br>
	 * This method iterates through all available modifiers.<br>
	 * It returns the first modifier that passes its internal check.<br>
	 * If no valid modifier is found or if none exist, it returns {@code null}.
	 * @param effect The {@code Effect} object to check against the modifiers.
	 * @return The first matching {@link ActionModifier}, or {@code null} if none apply.
	 */
	protected ActionModifier getActionModifiers(Effect effect)
	{
		if (modifiers == null)
		{
			return null;
		}
		
		/**
		 * Only one of modifiers will be applied now
		 */
		for (ActionModifier modifier : modifiers.getActionModifiers())
		{
			if (modifier.check(effect))
			{
				return modifier;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the type of the effect.<br>
	 * This method returns the {@code EffectType} associated with this object.
	 * @return The {@code EffectType} of the current effect.
	 */
	public EffectType getEffectType()
	{
		return effectType;
	}
	
	/**
	 * Retrieves the secondary effect associated with this object.<br>
	 * This method returns a {@link SubEffect} instance if one exists.
	 * @return the {@code SubEffect} object.
	 */
	public SubEffect getSubEffect()
	{
		return subEffect;
	}
	
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an AP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	public void calculate(Effect effect)
	{
		calculate(effect, null, null);
	}
	
	/**
	 * Determines if an {@link Effect} is successfully applied to a target.<br>
	 * This method checks various conditions such as passive status, forced effects, and resistance rates.<br>
	 * It also handles dodge calculations based on the skill type and player class.
	 * @param effect The {@link Effect} object to be processed.
	 * @param statEnum The {@link StatEnum} representing the specific statistic being affected.
	 * @param spellStatus The current {@link SpellStatus} of the casting action.
	 * @return {@code true} if the effect is successfully applied, otherwise {@code false}.
	 */
	public boolean calculate(Effect effect, StatEnum statEnum, SpellStatus spellStatus)
	{
		if (effect.getSkillTemplate().isPassive())
		{
			addSuccessEffect(effect, spellStatus);
			return true;
		}
		
		if ((statEnum != null) && isAlteredState(statEnum) && isImuneToAbnormal(effect, statEnum))
		{
			return false;
		}
		
		// dont check for forced effect
		if (effect.getIsForcedEffect())
		{
			addSuccessEffect(effect, spellStatus);
			return true;
		}
		
		// check conditions
		if (!effectConditionsCheck(effect))
		{
			return false;
		}
		
		// preeffects
		if (getPosition() > 1)
		{
			final List<Integer> positions = getPreEffects();
			for (int pos : positions)
			{
				if (!effect.isInSuccessEffects(pos))
				{
					return false;
				}
			}
			
			// check preeffect probability
			if (Rnd.get(0, 100) > getPreEffectProb())
			{
				return false;
			}
		}
		
		// check effectresistrate
		if (!calculateEffectResistRate(effect, statEnum))
		{
			if (!effect.isDamageEffect())
			{
				effect.clearSucessEffects();
			}
			
			effect.setAttackStatus(AttackStatus.BUF);
			return false;
		}
		
		SkillType skillType = effect.getSkillType();
		
		// certain effects are magical by default
		if (isMagicalEffectTemp())
		{
			skillType = SkillType.MAGICAL;
		}
		
		boolean cannotMiss = false;
		if (this instanceof SkillAttackInstantEffect)
		{
			cannotMiss = ((SkillAttackInstantEffect) this).isCannotmiss();
		}
		
		if (!noResist && !cannotMiss)
		{
			// check for BOOST_RESIST
			int boostResist = 0;
			switch (effect.getSkillTemplate().getSubType())
			{
				case DEBUFF:
					boostResist = effect.getEffector().getGameStats().getStat(StatEnum.BOOST_RESIST_DEBUFF, 0).getCurrent();
					break;
				default:
					break;
			}
			
			final int accMod = accMod2 + (accMod1 * effect.getSkillLevel()) + effect.getAccModBoost() + boostResist;
			switch (skillType)
			{
				case PHYSICAL:
					if (effect.getEffector() instanceof Player)
					{
						final Player player = (Player) effect.getEffector();
						if ((player.getPlayerClass() == PlayerClass.GUNNER) || (player.getPlayerClass() == PlayerClass.RIDER))
						{
							if (Rnd.get(0, 1000) < StatFunctions.calculateMagicalResistRate(effect.getEffector(), effect.getEffected(), accMod))
							{
								return false;
							}
						}
						else
						{
							if (StatFunctions.calculatePhysicalDodgeRate(effect.getEffector(), effect.getEffected(), accMod))
							{
								return false;
							}
						}
					}
					else
					{
						if (StatFunctions.calculatePhysicalDodgeRate(effect.getEffector(), effect.getEffected(), accMod))
						{
							return false;
						}
					}
					break;
				case MAGICAL:
					if (Rnd.get(0, 1000) < StatFunctions.calculateMagicalResistRate(effect.getEffector(), effect.getEffected(), accMod))
					{
						return false;
					}
					break;
				case ALL:
					if (effect.getEffector() instanceof Player)
					{
						final Player player = (Player) effect.getEffector();
						if ((player.getPlayerClass() == PlayerClass.GUNNER) || (player.getPlayerClass() == PlayerClass.RIDER))
						{
							if (Rnd.get(0, 1000) < StatFunctions.calculateMagicalResistRate(effect.getEffector(), effect.getEffected(), accMod))
							{
								return false;
							}
						}
						else
						{
							if (StatFunctions.calculatePhysicalDodgeRate(effect.getEffector(), effect.getEffected(), accMod))
							{
								return false;
							}
						}
					}
					else
					{
						if (StatFunctions.calculatePhysicalDodgeRate(effect.getEffector(), effect.getEffected(), accMod))
						{
							return false;
						}
					}
					
					if (Rnd.get(0, 1000) < StatFunctions.calculateMagicalResistRate(effect.getEffector(), effect.getEffected(), accMod))
					{
						return false;
					}
					break;
				default:
					break;
			}
		}
		
		addSuccessEffect(effect, spellStatus);
		return true;
	}
	
	/**
	 * Adds a success effect to the current spell status.<br>
	 * This method links the {@code Effect} to this instance.<br>
	 * It also sets the {@code SpellStatus} if it is not {@code null}.
	 * @param effect The {@code Effect} object to be added.
	 * @param spellStatus The {@code SpellStatus} associated with the effect.
	 */
	private void addSuccessEffect(Effect effect, SpellStatus spellStatus)
	{
		effect.addSucessEffect(this);
		if (spellStatus != null)
		{
			effect.setSpellStatus(spellStatus);
		}
	}
	
	/**
	 * Checks if the conditions for a specific {@code Effect} are met.<br>
	 * It retrieves the conditions from {@code getEffectConditions}.<br>
	 * If no conditions exist, it returns {@code true}.<br>
	 * Otherwise, it validates the effect against the required conditions.
	 * @param effect The {@code Effect} object to validate.
	 * @return {@code true} if the conditions are met or missing, {@code false} otherwise.
	 */
	private boolean effectConditionsCheck(Effect effect)
	{
		final Conditions effectConditions = getEffectConditions();
		return effectConditions != null ? effectConditions.validate(effect) : true;
	}
	
	/**
	 * Retrieves a list of effect IDs from the pre-effect string.<br>
	 * This method parses the {@code getPreEffect()} value by splitting it at underscores.<br>
	 * It converts each resulting part into an {@code Integer}.
	 * @return A {@code List} containing the parsed effect IDs, or an empty list if no pre-effect exists.
	 */
	private List<Integer> getPreEffects()
	{
		final List<Integer> preEffects = new ArrayList<>();
		
		if (getPreEffect() == null)
		{
			return preEffects;
		}
		
		final String[] parts = getPreEffect().split("_");
		for (String part : parts)
		{
			preEffects.add(Integer.parseInt(part));
		}
		
		return preEffects;
	}
	
	/**
	 * Apply effect to effected
	 * @param effect
	 */
	public abstract void applyEffect(Effect effect);
	
	/**
	 * Starts a new {@link Effect} instance.<br>
	 * This method initializes the effect and begins its execution.<br>
	 * It is a convenience method that passes {@code null} for the abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	public void startEffect(Effect effect)
	{
	}
	
	/**
	 * Calculates and applies a sub-effect to the provided {@code Effect}.<br>
	 * This method checks if the sub-effect conditions are met.<br>
	 * It also determines if the sub-effect triggers based on its chance.<br>
	 * If successful, it initializes a new {@code Effect} and updates the original object.
	 * @param effect The {@code Effect} object to be updated.
	 */
	public void calculateSubEffect(Effect effect)
	{
		if (subEffect == null)
		{
			return;
		}
		
		// Pre-Check for sub effect conditions
		if (!effectSubConditionsCheck(effect))
		{
			effect.setSubEffectAborted(true);
			return;
		}
		
		// chance to trigger subeffect
		if (Rnd.get(100) > subEffect.getChance())
		{
			return;
		}
		
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(subEffect.getSkillId());
		int level = 1;
		if (subEffect.isAddEffect())
		{
			level = effect.getSignetBurstedCount();
		}
		
		final Effect newEffect = new Effect(effect.getEffector(), effect.getEffected(), template, level, 0);
		newEffect.setAccModBoost(effect.getAccModBoost());
		newEffect.initialize();
		if ((newEffect.getSpellStatus() != SpellStatus.DODGE) && (newEffect.getSpellStatus() != SpellStatus.RESIST))
		{
			effect.setSpellStatus(newEffect.getSpellStatus());
		}
		
		effect.setSubEffect(newEffect);
		effect.setSkillMoveType(newEffect.getSkillMoveType());
		effect.setTargetLoc(newEffect.getTargetX(), newEffect.getTargetY(), newEffect.getTargetZ());
	}
	
	/**
	 * Validates the sub-conditions for a specific {@code Effect}.<br>
	 * It checks if the conditions defined in {@code getEffectSubConditions} are met.<br>
	 * If no sub-conditions exist, it returns {@code true}.
	 * @param effect The {@code Effect} object to validate.
	 * @return {@code true} if all sub-conditions are satisfied or if none exist; {@code false} otherwise.
	 */
	private boolean effectSubConditionsCheck(Effect effect)
	{
		return effectSubConditions != null ? effectSubConditions.validate(effect) : true;
	}
	
	/**
	 * Calculates the hate value for a specific {@link Effect}.<br>
	 * This method updates the effect's hate based on its type and level.<br>
	 * It ensures that the final hate value is at least 1.
	 * @param effect The {@code Effect} object to update.
	 */
	public void calculateHate(Effect effect)
	{
		if ((hopType == null) || effect.getSuccessEffect().isEmpty())
		{
			return;
		}
		
		int currentHate = effect.getEffectHate();
		if (hopType != null)
		{
			switch (hopType)
			{
				case DAMAGE:
					currentHate += effect.getReserved1();
					break;
				case SKILLLV:
					final int skillLvl = effect.getSkillLevel();
					currentHate += hopB + (hopA * skillLvl); // Agro-value of the effect
				default:
					break;
			}
		}
		
		if (currentHate == 0)
		{
			currentHate = 1;
		}
		
		effect.setEffectHate(StatFunctions.calculateHate(effect.getEffector(), currentHate));
	}
	
	/**
	 * Starts a sub-effect associated with the provided {@code Effect}.<br>
	 * This method checks if the sub-effect exists and meets all required conditions.<br>
	 * If valid, it triggers the {@code applyEffect} method.
	 * @param effect The primary {@code Effect} containing the sub-effect data.
	 */
	public void startSubEffect(Effect effect)
	{
		// Apply-Check for sub effect conditions
		if ((subEffect == null) || effect.isSubEffectAbortedBySubConditions())
		{
			return;
		}
		
		if (effect.getSubEffect() != null)
		{
			effect.getSubEffect().applyEffect();
		}
	}
	
	/**
	 * Executes the periodic logic for a specific {@code Effect}.<br>
	 * This method checks if the effector is online and applies the effect to nearby players.<br>
	 * It handles group range calculations and ensures effects are applied correctly during duels.<br>
	 * Finally, it broadcasts the updated effect packet to the effector.
	 * @param effect The {@code Effect} object to be processed.
	 */
	public void onPeriodicAction(Effect effect)
	{
	}
	
	/**
	 * Stops a specific {@code Effect} from being active.<br>
	 * This method removes the associated observers from the target controller.<br>
	 * Use this to clean up effects when they expire or are removed.
	 * @param effect The {@code Effect} object to stop.
	 */
	public void endEffect(Effect effect)
	{
	}
	
	/**
	 * Determines if an {@code Effect} successfully applies based on resistance rates.<br>
	 * This method calculates the final power by checking stats, penetration, and PvP/PvE modifiers.<br>
	 * It returns {@code true} if the random roll is less than or equal to the calculated power.
	 * @param effect The {@code Effect} object being processed.
	 * @param statEnum The {@link StatEnum} used to determine the specific resistance type.
	 * @return {@code true} if the effect succeeds, otherwise {@code false}.
	 */
	public boolean calculateEffectResistRate(Effect effect, StatEnum statEnum)
	{
		if ((effect.getEffected() == null) || (effect.getEffected().getGameStats() == null) || (effect.getEffector() == null) || (effect.getEffector().getGameStats() == null))
		{
			return false;
		}
		
		final Creature effected = effect.getEffected();
		final Creature effector = effect.getEffector();
		
		if (statEnum == null)
		{
			return true;
		}
		
		int effectPower = 1000;
		
		if (isAlteredState(statEnum))
		{
			effectPower -= effect.getEffected().getGameStats().getStat(StatEnum.ABNORMAL_RESISTANCE_ALL, 0).getCurrent();
		}
		
		// effect resistance
		effectPower -= effect.getEffected().getGameStats().getStat(statEnum, 0).getCurrent();
		
		// penetration
		final StatEnum penetrationStat = getPenetrationStat(statEnum);
		if (penetrationStat != null)
		{
			effectPower += effector.getGameStats().getStat(penetrationStat, 0).getCurrent();
		}
		
		// resist mod pvp
		if (effector.isPvpTarget(effect.getEffected()))
		{
			final int differ = (effected.getLevel() - effector.getLevel());
			if ((differ > 2) && (differ < 8))
			{
				effectPower -= Math.round(((effectPower * (differ - 2)) / 15f));
			}
			else if (differ >= 8)
			{
				effectPower *= 0.1f;
			}
		}
		
		// resist mod PvE
		if (effect.getEffected() instanceof Npc)
		{
			final Npc effectrd = (Npc) effect.getEffected();
			final int hpGaugeMod = effectrd.getObjectTemplate().getRank().ordinal() - 1;
			effectPower -= hpGaugeMod * 100;
		}
		
		return Rnd.get(1000) <= effectPower;
	}
	
	/**
	 * Checks if a creature is immune to an abnormal effect based on its type and stats.<br>
	 * This method evaluates specific conditions for bosses, NPCs, and avatars.<br>
	 * It also considers the current resistance value of the target.
	 * @param effect The {@code Effect} being applied to the target.
	 * @param statEnum The {@code StatEnum} type of the effect to check.
	 * @return {@code true} if the creature is immune, {@code false} otherwise.
	 */
	private boolean isImuneToAbnormal(Effect effect, StatEnum statEnum)
	{
		final Creature effected = effect.getEffected();
		if (effected != effect.getEffector())
		{
			if (effected instanceof Npc)
			{
				final Npc npc = (Npc) effected;
				if (npc.isBoss() || npc.hasStatic() || (npc instanceof Kisk) || npc.getAi2().ask(AIQuestion.CAN_RESIST_ABNORMAL).isPositive())
				{
					return true;
				}
				
				if (npc.getObjectTemplate().getStatsTemplate().getRunSpeed() == 0)
				{
					if ((statEnum == StatEnum.PULLED_RESISTANCE) || (statEnum == StatEnum.STAGGER_RESISTANCE) || (statEnum == StatEnum.STUMBLE_RESISTANCE))
					{
						return true;
					}
				}
			}
			
			if (effected.getTransformModel().getType() == TransformType.AVATAR)
			{
				if (statEnum == StatEnum.SLOW_RESISTANCE)
				{
					return true;
				}
			}
			
			final int resist = effected.getGameStats().getStat(StatEnum.ABNORMAL_RESISTANCE_ALL, 0).getCurrent();
			return Rnd.get(1000) < resist;
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific stat belongs to the resistance category.<br>
	 * This method identifies various status effect resistances.
	 * @param stat The {@link StatEnum} to check.
	 * @return {@code true} if the stat is a resistance type, otherwise {@code false}.
	 */
	private boolean isAlteredState(StatEnum stat)
	{
		switch (stat)
		{
			case BIND_RESISTANCE:
			case BLIND_RESISTANCE:
			case CHARM_RESISTANCE:
			case CONFUSE_RESISTANCE:
			case CURSE_RESISTANCE:
			case DEFORM_RESISTANCE:
			case FEAR_RESISTANCE:
			case OPENAREIAL_RESISTANCE:
			case PARALYZE_RESISTANCE:
			case PULLED_RESISTANCE:
			case ROOT_RESISTANCE:
			case SILENCE_RESISTANCE:
			case SLEEP_RESISTANCE:
			case SLOW_RESISTANCE:
			case SNARE_RESISTANCE:
			case SPIN_RESISTANCE:
			case STAGGER_RESISTANCE:
			case STUMBLE_RESISTANCE:
			case STUN_RESISTANCE:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Maps a resistance statistic to its corresponding penetration statistic.<br>
	 * This method helps identify which penetration type applies to a specific resistance.
	 * @param statEnum The {@code StatEnum} representing the resistance to check.
	 * @return The matching {@code StatEnum} for penetration, or {@code null} if no mapping exists.
	 */
	private StatEnum getPenetrationStat(StatEnum statEnum)
	{
		switch (statEnum)
		{
			case BLEED_RESISTANCE:
				return StatEnum.BLEED_RESISTANCE_PENETRATION;
			case BLIND_RESISTANCE:
				return StatEnum.BLIND_RESISTANCE_PENETRATION;
			// case BIND_RESISTANCE:
			case CHARM_RESISTANCE:
				return StatEnum.CHARM_RESISTANCE_PENETRATION;
			case CONFUSE_RESISTANCE:
				return StatEnum.CONFUSE_RESISTANCE_PENETRATION;
			case CURSE_RESISTANCE:
				return StatEnum.CURSE_RESISTANCE_PENETRATION;
			// case DEFORM_RESISTANCE:
			case DISEASE_RESISTANCE:
				return StatEnum.DISEASE_RESISTANCE_PENETRATION;
			case FEAR_RESISTANCE:
				return StatEnum.FEAR_RESISTANCE_PENETRATION;
			case OPENAREIAL_RESISTANCE:
				return StatEnum.OPENAREIAL_RESISTANCE_PENETRATION;
			case PARALYZE_RESISTANCE:
				return StatEnum.PARALYZE_RESISTANCE_PENETRATION;
			case PERIFICATION_RESISTANCE:
				return StatEnum.PERIFICATION_RESISTANCE_PENETRATION;
			case POISON_RESISTANCE:
				return StatEnum.POISON_RESISTANCE_PENETRATION;
			case ROOT_RESISTANCE:
				return StatEnum.ROOT_RESISTANCE_PENETRATION;
			case SILENCE_RESISTANCE:
				return StatEnum.SILENCE_RESISTANCE_PENETRATION;
			case SLEEP_RESISTANCE:
				return StatEnum.SLEEP_RESISTANCE_PENETRATION;
			case SLOW_RESISTANCE:
				return StatEnum.SLOW_RESISTANCE_PENETRATION;
			case SNARE_RESISTANCE:
				return StatEnum.SNARE_RESISTANCE_PENETRATION;
			case SPIN_RESISTANCE:
				return StatEnum.SPIN_RESISTANCE_PENETRATION;
			case STAGGER_RESISTANCE:
				return StatEnum.STAGGER_RESISTANCE_PENETRATION;
			case STUMBLE_RESISTANCE:
				return StatEnum.STUMBLE_RESISTANCE_PENETRATION;
			case STUN_RESISTANCE:
				return StatEnum.STUN_RESISTANCE_PENETRATION;
			default:
				return null;
		}
	}
	
	/**
	 * Checks if the current effect is a temporary status condition.<br>
	 * It returns {@code true} for effects like Silence, Sleep, or Stun.<br>
	 * It returns {@code false} for all other types of effects.
	 * @return {@code true} if the effect is a temporary status; {@code false} otherwise.
	 */
	private boolean isMagicalEffectTemp()
	{
		if ((this instanceof SilenceEffect) || (this instanceof SleepEffect) || (this instanceof RootEffect) || (this instanceof SnareEffect) || (this instanceof StunEffect) || (this instanceof PoisonEffect) || (this instanceof BindEffect) || (this instanceof BleedEffect) || (this instanceof BlindEffect) || (this instanceof DeboostHealEffect) || (this instanceof ParalyzeEffect) || (this instanceof SlowEffect))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It determines and sets the {@code effectType} based on the class name.<br>
	 * The logic extracts the type from the package path and converts it to uppercase.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		EffectType temp = null;
		try
		{
			temp = EffectType.valueOf(this.getClass().getName().replaceAll("com.aionemu.gameserver.skillengine.effect.", "").replaceAll("Effect", "").toUpperCase());
		}
		catch (Exception e)
		{
			log.info("missing effectype for " + this.getClass().getName().replaceAll("com.aionemu.gameserver.skillengine.effect.", "").replaceAll("Effect", "").toUpperCase());
		}
		
		effectType = temp;
	}
}
