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
package com.aionemu.gameserver.skillengine.model;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.skillengine.action.Actions;
import com.aionemu.gameserver.skillengine.condition.ChainCondition;
import com.aionemu.gameserver.skillengine.condition.Condition;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.condition.DpCondition;
import com.aionemu.gameserver.skillengine.condition.HpCondition;
import com.aionemu.gameserver.skillengine.condition.PlayerMovedCondition;
import com.aionemu.gameserver.skillengine.condition.SkillChargeCondition;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.EffectType;
import com.aionemu.gameserver.skillengine.effect.Effects;
import com.aionemu.gameserver.skillengine.periodicaction.PeriodicActions;
import com.aionemu.gameserver.skillengine.properties.Properties;

/**
 * Represents the base configuration and data for a skill in the game.<br>
 * This class serves as a template that defines how a skill behaves, its requirements, and its effects.
 * @author ATracer modified by Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "skillTemplate", propOrder =
{
	"properties",
	"startconditions",
	"useconditions",
	"endconditions",
	"useequipmentconditions",
	"effects",
	"actions",
	"periodicActions",
	"motion"
})
public class SkillTemplate
{
	protected Properties properties;
	protected Conditions startconditions;
	protected Conditions useconditions;
	private Conditions endconditions;
	protected Conditions useequipmentconditions;
	protected Effects effects;
	protected Actions actions;
	@XmlElement(name = "periodicactions")
	protected PeriodicActions periodicActions;
	protected Motion motion;
	@XmlAttribute(name = "skill_id", required = true)
	protected int skillId;
	@XmlAttribute(required = true)
	protected String name;
	@XmlAttribute(name = "name_desc")
	private String namedesc;
	@XmlAttribute(required = true)
	protected int nameId;
	@XmlAttribute
	protected String stack = "NONE";
	@XmlAttribute
	protected int cooldownId;
	@XmlAttribute
	protected int lvl;
	@XmlAttribute(name = "skilltype", required = true)
	protected SkillType type = SkillType.NONE;
	@XmlAttribute(name = "skillsubtype", required = true)
	protected SkillSubType subType;
	@XmlAttribute(name = "tslot")
	protected SkillTargetSlot targetSlot;
	@XmlAttribute(name = "tslot_level")
	protected int targetSlotLevel;
	@XmlAttribute(name = "toggle_timer")
	protected int toggleTimer;
	@XmlAttribute(name = "dispel_category")
	protected DispelCategoryType dispelCategory = DispelCategoryType.NONE;
	@XmlAttribute(name = "req_dispel_level")
	protected int reqDispelLevel;
	@XmlAttribute(name = "activation", required = true)
	protected ActivationAttribute activationAttribute;
	@XmlAttribute(required = true)
	protected int duration;
	@XmlAttribute(name = "cooldown")
	protected int cooldown;
	@XmlAttribute(name = "penalty_skill_id")
	protected int penaltySkillId;
	@XmlAttribute(name = "pvp_damage")
	protected int pvpDamage;
	@XmlAttribute(name = "pvp_duration")
	protected int pvpDuration;
	@XmlAttribute(name = "chain_skill_prob")
	protected int chainSkillProb = 100;
	@XmlAttribute(name = "cancel_rate")
	protected int cancelRate;
	@XmlAttribute(name = "stance")
	protected boolean stance;
	@XmlAttribute(name = "avatar")
	protected boolean isDeityAvatar;
	@XmlAttribute(name = "ground")
	protected boolean isGroundSkill; // TODO remove!
	@XmlAttribute(name = "unpottable")
	protected boolean isUndispellableByPotions;
	@XmlAttribute(name = "ammospeed")
	protected int ammoSpeed;
	@XmlAttribute(name = "conflict_id")
	protected int conflictId;
	@XmlAttribute(name = "counter_skill")
	protected AttackStatus counterSkill = null;
	@XmlAttribute(name = "noremoveatdie")
	protected boolean noRemoveAtDie = false;
	@XmlAttribute(name = "boost_casting_time")
	protected boolean boostCastingTime = false;
	@XmlAttribute(name = "stigma")
	protected StigmaType stigmaType = StigmaType.NONE;
	@XmlAttribute(name = "is_minion_skill")
	protected boolean isminionSkill = false;
	@XmlTransient
	protected HashMap<Integer, Integer> effectIds = null;
	@XmlAttribute(name = "skill_group")
	private String skill_group;
	
	/**
	 * Retrieves the {@code Properties} associated with this skill template.<br>
	 * This method returns the internal property object used for configuration.
	 * @return the {@code Properties} object.
	 */
	public Properties getProperties()
	{
		return properties;
	}
	
	/**
	 * Retrieves the starting conditions for a skill.<br>
	 * These conditions must be met before the skill can begin.
	 * @return the {@code Conditions} object containing the requirements.
	 */
	public Conditions getStartconditions()
	{
		return startconditions;
	}
	
	/**
	 * Retrieves the current timer value for a toggle skill.<br>
	 * This value tracks how much time remains or has passed for the active state.
	 * @return The current {@code int} value of the toggle timer.
	 */
	public int getToggleTimer()
	{
		return toggleTimer;
	}
	
	/**
	 * Retrieves the conditions required to use a skill.<br>
	 * This method returns the {@code Conditions} object associated with the skill's usage requirements.
	 * @return the {@code Conditions} for using the skill.
	 */
	public Conditions getUseconditions()
	{
		return useconditions;
	}
	
	/**
	 * Retrieves the equipment requirements for a skill.<br>
	 * This method returns the {@code Conditions} object associated with equipment usage.
	 * @return the {@code Conditions} of equipment requirements.
	 */
	public Conditions getUseEquipmentconditions()
	{
		return useequipmentconditions;
	}
	
	/**
	 * Retrieves the list of effects associated with this skill.<br>
	 * This method returns the {@code Effects} object containing all active modifiers.
	 * @return the {@code Effects} object for this skill template.
	 */
	public Effects getEffects()
	{
		return effects;
	}
	
	/**
	 * Retrieves the list of actions associated with this skill.<br>
	 * These actions are executed when the skill is triggered.
	 * @return the {@code Actions} object containing all skill actions.
	 */
	public Actions getActions()
	{
		return actions;
	}
	
	/**
	 * Retrieves the list of actions that repeat over time.<br>
	 * These are used for skills with ongoing effects.
	 * @return the {@code PeriodicActions} object containing these actions.
	 */
	public PeriodicActions getPeriodicActions()
	{
		return periodicActions;
	}
	
	/**
	 * Retrieves the {@link Motion} associated with this skill template.<br>
	 * This object contains the animation data for the skill.
	 * @return the {@code Motion} object.
	 */
	public Motion getMotion()
	{
		return motion;
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillId;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the description of the item.<br>
	 * This value is stored in the {@code name_desc} attribute.
	 * @return The description string for this item template.
	 */
	public String getNamedesc()
	{
		return namedesc;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	public int getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the stack name for this skill.<br>
	 * This value is obtained from the associated {@link SkillTemplate}.
	 * @return The stack name as a {@code String}.
	 */
	public String getStack()
	{
		return stack;
	}
	
	/**
	 * Retrieves the group name for this skill skin.<br>
	 * This value is used to categorize different skills.
	 * @return The {@code String} representing the skill group.
	 */
	public String getSkillGroup()
	{
		return skill_group;
	}
	
	/**
	 * Retrieves the current level of the skill.<br>
	 * This value represents the numerical rank of the {@link SkillTemplate}.
	 * @return The integer level of the skill.
	 */
	public int getLvl()
	{
		return lvl;
	}
	
	/**
	 * Retrieves the category of the skill.<br>
	 * This identifies whether the skill is an active, passive, or other type.
	 * @return the {@code SkillType} of this skill.
	 */
	public SkillType getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the specific subtype of the skill.<br>
	 * This value helps identify how a {@link SkillType} behaves.
	 * @return the {@code SkillSubType} of this template.
	 */
	public SkillSubType getSubType()
	{
		return subType;
	}
	
	/**
	 * Retrieves the target slot for this skill.<br>
	 * This identifies which part of the target is affected by the skill.
	 * @return the {@code SkillTargetSlot} associated with this template.
	 */
	public SkillTargetSlot getTargetSlot()
	{
		return targetSlot;
	}
	
	/**
	 * Retrieves the target slot level from the {@link SkillTemplate}.<br>
	 * This value determines which specific slot is targeted by the skill.
	 * @return the integer value of the target slot level.
	 */
	public int getTargetSlotLevel()
	{
		return targetSlotLevel;
	}
	
	/**
	 * Retrieves the dispel category for this skill.<br>
	 * This value is obtained from the associated {@link SkillTemplate}.
	 * @return the {@code DispelCategoryType} of the skill.
	 */
	public DispelCategoryType getDispelCategory()
	{
		return dispelCategory;
	}
	
	/**
	 * Retrieves the required dispel level for this skill.<br>
	 * This value is fetched from the {@link SkillTemplate}.
	 * @return The integer value of the required dispel level.
	 */
	public int getReqDispelLevel()
	{
		return reqDispelLevel;
	}
	
	/**
	 * Retrieves the total duration for a dynamic portal.<br>
	 * This value is fetched from {@code CustomConfig}.
	 * @return The duration as an {@code int}.
	 */
	public int getDuration()
	{
		return duration;
	}
	
	/**
	 * Retrieves the {@code StigmaType} associated with this skill.<br>
	 * This value determines which specific stigma category applies to the template.
	 * @return the {@code StigmaType} of the skill.
	 */
	public StigmaType getStigmaType()
	{
		return stigmaType;
	}
	
	/**
	 * Retrieves the activation attribute for this skill.<br>
	 * This value defines how the skill is triggered or activated.
	 * @return the {@code ActivationAttribute} associated with this skill.
	 */
	public ActivationAttribute getActivationAttribute()
	{
		return activationAttribute;
	}
	
	/**
	 * Checks if the skill is a passive ability.<br>
	 * This method retrieves the status from the {@link SkillTemplate}.
	 * @return {@code true} if the skill is passive, {@code false} otherwise.
	 */
	public boolean isPassive()
	{
		return activationAttribute == ActivationAttribute.PASSIVE;
	}
	
	/**
	 * Checks if the skill is a toggle type.<br>
	 * This method returns {@code true} if the activation attribute is set to {@code TOGGLE}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the skill is a toggle; {@code false} otherwise.
	 */
	public boolean isToggle()
	{
		return activationAttribute == ActivationAttribute.TOGGLE;
	}
	
	/**
	 * Checks if the skill is triggered by a provocation.<br>
	 * This method returns {@code true} if the {@link ActivationAttribute} is set to {@code PROVOKED}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the skill is provoked, {@code false} otherwise.
	 */
	public boolean isProvoked()
	{
		return activationAttribute == ActivationAttribute.PROVOKED;
	}
	
	/**
	 * Checks if the skill requires maintenance.<br>
	 * This method returns {@code true} if the {@link ActivationAttribute} is set to {@code MAINTAIN}.
	 * @return {@code true} if the skill is a maintain type, {@code false} otherwise.
	 */
	public boolean isMaintain()
	{
		return activationAttribute == ActivationAttribute.MAINTAIN;
	}
	
	/**
	 * Checks if the skill is currently active.<br>
	 * This method returns {@code true} if the {@code activationAttribute} is set to {@code ActivationAttribute.ACTIVE}.
	 * @return {@code true} if the skill is active, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return activationAttribute == ActivationAttribute.ACTIVE;
	}
	
	/**
	 * Checks if the skill requires a charge to be activated.<br>
	 * This method returns {@code true} if the {@link ActivationAttribute} is set to {@code CHARGE}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the skill is a charge type, {@code false} otherwise.
	 */
	public boolean isCharge()
	{
		return activationAttribute == ActivationAttribute.CHARGE;
	}
	
	/**
	 * Checks if the skill is a minion skill.<br>
	 * Returns {@code true} if it is a minion skill, otherwise returns {@code false}.
	 * @return The status of whether this is a minion skill.
	 */
	public boolean isMinionSkill()
	{
		return isminionSkill;
	}
	
	/**
	 * Retrieves a specific effect from the skill template based on its index.<br>
	 * The method checks if the requested {@code position} is valid before returning the result.<br>
	 * It returns {@code null} if the list of effects is empty or the index is out of bounds.
	 * @param position The 1-based index of the effect to retrieve.
	 * @return The {@link EffectTemplate} at the specified position, or {@code null} if not found.
	 */
	public EffectTemplate getEffectTemplate(int position)
	{
		return (effects != null) && (effects.getEffects().size() >= position) ? effects.getEffects().get(position - 1) : null;
		
	}
	
	/**
	 * Retrieves the cooldown time for this skill.<br>
	 * This value determines how long to wait before the skill can be used again.
	 * @return the {@code int} value of the cooldown.
	 */
	public int getCooldown()
	{
		return cooldown;
	}
	
	/**
	 * Retrieves the unique identifier for the penalty skill.<br>
	 * This value is used to identify specific skills that apply penalties.
	 * @return the {@code int} ID of the penalty skill.
	 */
	public int getPenaltySkillId()
	{
		return penaltySkillId;
	}
	
	/**
	 * Retrieves the base damage value for Player vs Player combat.<br>
	 * This value is fetched from the associated {@link SkillTemplate}.
	 * @return The integer damage value used in {@code pvp} scenarios.
	 */
	public int getPvpDamage()
	{
		return pvpDamage;
	}
	
	/**
	 * Retrieves the duration of the skill in a {@code PvP} environment.<br>
	 * This value represents how long the effect lasts during player versus player combat.
	 * @return The duration as an {@code int}.
	 */
	public int getPvpDuration()
	{
		return pvpDuration;
	}
	
	/**
	 * Retrieves the probability of a skill triggering a chain effect.<br>
	 * This value is stored in the {@code chainSkillProb} field.
	 * @return The chain skill probability as an {@code int}.
	 */
	public int getChainSkillProb()
	{
		return chainSkillProb;
	}
	
	/**
	 * Retrieves the cancellation rate for this skill.<br>
	 * This value determines how often a skill can be interrupted.
	 * @return The current {@code int} value of the cancel rate.
	 */
	public int getCancelRate()
	{
		return cancelRate;
	}
	
	/**
	 * Checks if the skill is a stance.<br>
	 * Returns {@code true} if it is a stance, otherwise returns {@code false}.
	 * @return The status of whether this skill is a stance.
	 */
	public boolean isStance()
	{
		return stance;
	}
	
	/**
	 * Checks if the skill has a resurrection effect.<br>
	 * This method verifies that {@code getEffects} is not {@code null}.<br>
	 * It then checks if the effects contain a resurrection property.
	 * @return {@code true} if the skill has a resurrection effect, otherwise {@code false}
	 */
	public boolean hasResurrectEffect()
	{
		return (getEffects() != null) && getEffects().isResurrect();
	}
	
	/**
	 * Checks if the skill has an instant heal effect.<br>
	 * This method verifies that {@code getEffects} is not {@code null}.<br>
	 * It also checks for the presence of the {@code PROCFPHEALINSTANT} effect type.
	 * @return {@code true} if the skill has an instant heal effect, otherwise {@code false}
	 */
	public boolean hasItemHealFpEffect()
	{
		return (getEffects() != null) && getEffects().isEffectTypePresent(EffectType.PROCFPHEALINSTANT);
	}
	
	/**
	 * Checks if the skill has an evade effect.<br>
	 * This method verifies that {@code getEffects} is not {@code null}.<br>
	 * It also checks if the {@code EVADE} type is present in the effects list.
	 * @return {@code true} if the skill contains an evade effect, otherwise {@code false}.
	 */
	public boolean hasEvadeEffect()
	{
		return (getEffects() != null) && getEffects().isEffectTypePresent(EffectType.EVADE);
	}
	
	/**
	 * Checks if the skill has an instant recall effect.<br>
	 * This method verifies that {@code getEffects} is not {@code null}.<br>
	 * It also checks if the {@code EffectType.RECALLINSTANT} is present.
	 * @return {@code true} if the skill has an instant recall effect, otherwise {@code false}
	 */
	public boolean hasRecallInstant()
	{
		return (getEffects() != null) && getEffects().isEffectTypePresent(EffectType.RECALLINSTANT);
	}
	
	/**
	 * Checks if the skill has a healing effect.<br>
	 * It looks for {@code EffectType.HEAL} or {@code EffectType.HEALINSTANT} in the effects list.
	 * @return {@code true} if the skill provides healing, otherwise {@code false}.
	 */
	public boolean hasHealEffect()
	{
		return (getEffects() != null) && (getEffects().isEffectTypePresent(EffectType.HEAL) || getEffects().isEffectTypePresent(EffectType.HEALINSTANT));
	}
	
	/**
	 * Checks if the skill has a random movement effect.<br>
	 * This method verifies that the {@link Effects} object contains a {@code RANDOMMOVELOC} type.<br>
	 * It excludes specific skills like hypergate detonation from this check.
	 * @return {@code true} if the skill has a random move effect, otherwise {@code false}.
	 */
	public boolean hasRandomMoveEffect()
	{
		return (getEffects() != null) && getEffects().isEffectTypePresent(EffectType.RANDOMMOVELOC) && ((getSkillId() != 3818) || (getSkillId() != 3853)); // all move loc except hypergate detonation
	}
	
	/**
	 * Retrieves the unique identifier for the skill cooldown.<br>
	 * If no specific cooldown ID exists, it returns the {@code skillId}.
	 * @return The cooldown ID or the default skill ID.
	 */
	public int getCooldownId()
	{
		return (cooldownId > 0) ? cooldownId : skillId;
	}
	
	/**
	 * Checks if the skill belongs to a deity avatar.<br>
	 * This method retrieves the status from the {@link SkillTemplate}.
	 * @return {@code true} if it is a deity avatar, {@code false} otherwise.
	 */
	public boolean isDeityAvatar()
	{
		return isDeityAvatar;
	}
	
	/**
	 * Checks if the skill is a ground-based skill.<br>
	 * This method retrieves the status from the {@code SkillTemplate}.
	 * @return {@code true} if it is a ground skill, {@code false} otherwise.
	 */
	public boolean isGroundSkill()
	{
		return isGroundSkill;
	}
	
	/**
	 * Retrieves the counter skill associated with this template.
	 * @return the {@code AttackStatus} of the counter skill.
	 */
	public AttackStatus getCounterSkill()
	{
		return counterSkill;
	}
	
	/**
	 * Checks if the skill can be protected from being dispelled by potions.
	 * @return {@code true} if the skill is undispellable by potions, otherwise {@code false}.
	 */
	public boolean isUndispellableByPotions()
	{
		return isUndispellableByPotions;
	}
	
	/**
	 * Retrieves the speed of the ammunition for this skill skin.<br>
	 * This value is used to determine how fast projectiles move.
	 * @return The {@code int} value representing the ammo speed.
	 */
	public int getAmmoSpeed()
	{
		return ammoSpeed;
	}
	
	/**
	 * Retrieves the unique identifier for a skill conflict.<br>
	 * This ID is used to determine if two skills interfere with each other.
	 * @return The {@code int} value of the conflict ID.
	 */
	public int getConflictId()
	{
		return conflictId;
	}
	
	/**
	 * Checks if the skill remains active after the character dies.<br>
	 * Returns {@code true} if the skill is not removed upon death.<br>
	 * Returns {@code false} if the skill is removed upon death.
	 * @return a boolean indicating whether the skill persists after death.
	 */
	public boolean isNoRemoveAtDie()
	{
		return noRemoveAtDie;
	}
	
	/**
	 * Calculates the total duration of skill effects based on a specific level.<br>
	 * It iterates through all available {@link EffectTemplate} objects.<br>
	 * The result is the maximum duration found among all active effects.
	 * @param skillLevel The current level of the skill used to calculate duration.
	 * @return The longest calculated effect duration as an {@code int}.
	 */
	public int getEffectsDuration(int skillLevel)
	{
		int duration = 0;
		final Iterator<EffectTemplate> itr = getEffects().getEffects().iterator();
		while (itr.hasNext() && (duration == 0))
		{
			final EffectTemplate et = itr.next();
			int effectDuration = et.getDuration2() + (et.getDuration1() * skillLevel);
			if (et.getRandomTime() > 0)
			{
				effectDuration -= Rnd.get(et.getRandomTime());
			}
			
			duration = duration > effectDuration ? duration : effectDuration;
		}
		
		return duration;
	}
	
	/**
	 * Retrieves the {@link ChainCondition} from the start conditions.<br>
	 * It searches through all conditions in the {@code startconditions} list.<br>
	 * If no such condition exists, it returns {@code null}.
	 * @return The first {@code ChainCondition} found, or {@code null} if none exist.
	 */
	public ChainCondition getChainCondition()
	{
		if (startconditions != null)
		{
			for (Condition cond : startconditions.getConditions())
			{
				if (cond instanceof ChainCondition)
				{
					return (ChainCondition) cond;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@code SkillChargeCondition} from the start conditions.<br>
	 * It searches through all conditions in {@code getStartconditions}.<br>
	 * Returns {@code null} if no such condition is found.
	 * @return The {@code SkillChargeCondition} object or {@code null}.
	 */
	public SkillChargeCondition getSkillChargeCondition()
	{
		if (startconditions != null)
		{
			for (Condition cond : startconditions.getConditions())
			{
				if (cond instanceof SkillChargeCondition)
				{
					return (SkillChargeCondition) cond;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the unique identifiers for all effects associated with this skill.<br>
	 * The IDs are stored in a {@code HashMap}.
	 * @return A {@code HashMap} containing the effect IDs.
	 */
	public HashMap<Integer, Integer> getEffectIds()
	{
		return effectIds;
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code effectIds} map using the list of {@link EffectTemplate} objects.<br>
	 * The {@code effectIds} map is initialized if it is currently {@code null}.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if ((getEffects() != null) && (getEffects().getEffects() != null))
		{
			for (EffectTemplate et : getEffects().getEffects())
			{
				if (et.getEffectid() != 0)
				{
					if (effectIds == null)
					{
						effectIds = new HashMap<>();
					}
					
					effectIds.put(et.getEffectid(), et.getBasicLvl());
				}
			}
		}
	}
	
	/**
	 * Retrieves the {@code HpCondition} from the start conditions.<br>
	 * It searches through all conditions in the {@code getStartconditions} list.<br>
	 * Returns the first matching condition or {@code null} if none are found.
	 * @return The {@code HpCondition} object, or {@code null}.
	 */
	public HpCondition getHpCondition()
	{
		for (Condition c : startconditions.getConditions())
		{
			if (c instanceof HpCondition)
			{
				return ((HpCondition) c);
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@code DpCondition} from the start conditions.<br>
	 * It searches through all conditions in the {@code getStartconditions} list.<br>
	 * Returns the first matching condition or {@code null} if none are found.
	 * @return the {@code DpCondition} object or {@code null}.
	 */
	public DpCondition getDpCondition()
	{
		for (Condition c : startconditions.getConditions())
		{
			if (c instanceof DpCondition)
			{
				return ((DpCondition) c);
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@link PlayerMovedCondition} from the start conditions.<br>
	 * This method searches through all start conditions to find a movement check.<br>
	 * It returns {@code null} if no such condition exists.
	 * @return the {@code PlayerMovedCondition} object or {@code null}.
	 */
	public PlayerMovedCondition getMovedCondition()
	{
		for (Condition c : startconditions.getConditions())
		{
			if (c instanceof PlayerMovedCondition)
			{
				return ((PlayerMovedCondition) c);
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the conditions required to end a skill.<br>
	 * This method returns the {@code Conditions} object associated with this template.
	 * @return the {@code Conditions} for ending the skill
	 */
	public Conditions getEndConditions()
	{
		return endconditions;
	}
	
	/**
	 * Sets whether the skill remains active when a character dies.<br>
	 * If {@code true}, the skill will not be removed upon death.<br>
	 * If {@code false}, the skill is removed immediately.
	 * @param noRemoveAtDie The flag to determine if the skill persists after death.
	 */
	public void setNoRemoveAtDie(boolean noRemoveAtDie)
	{
		this.noRemoveAtDie = noRemoveAtDie;
	}
	
	/**
	 * Checks if the skill has a boosted casting time.<br>
	 * Returns {@code true} if the casting time is boosted.<br>
	 * Returns {@code false} otherwise.
	 * @return The status of the boostCastingTime property.
	 */
	public boolean isBoostCastingTime()
	{
		return boostCastingTime;
	}
}
