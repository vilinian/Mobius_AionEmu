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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STANCE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_ACTIVATION;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.effect.DamageEffect;
import com.aionemu.gameserver.skillengine.effect.DelayedSpellAttackInstantEffect;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.Effects;
import com.aionemu.gameserver.skillengine.effect.FearEffect;
import com.aionemu.gameserver.skillengine.effect.HideEffect;
import com.aionemu.gameserver.skillengine.effect.ParalyzeEffect;
import com.aionemu.gameserver.skillengine.effect.PetOrderUseUltraSkillEffect;
import com.aionemu.gameserver.skillengine.effect.SanctuaryEffect;
import com.aionemu.gameserver.skillengine.effect.SummonEffect;
import com.aionemu.gameserver.skillengine.effect.TransformEffect;
import com.aionemu.gameserver.skillengine.periodicaction.PeriodicAction;
import com.aionemu.gameserver.skillengine.periodicaction.PeriodicActions;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Represents a specific effect triggered by a skill within the game engine.<br>
 * This class serves as a base model for various mechanics such as damage, buffs, and status changes.<br>
 * It implements {@link StatOwner} to allow effects to influence character statistics.
 * @author ATracer
 * @modified by Wakizashi
 * @modified by Sippolo
 * @modified by kecimis
 * @Reworked Kill3r (updated 4.8)
 */
public class Effect implements StatOwner
{
	private Skill skill;
	private final SkillTemplate skillTemplate;
	private final int skillLevel;
	private int duration;
	private long endTime;
	private final PeriodicActions periodicActions;
	private SkillMoveType skillMoveType = SkillMoveType.DEFAULT;
	private final Creature effected;
	private final Creature effector;
	private Future<?> task = null;
	private Future<?>[] periodicTasks = null;
	private Future<?> periodicActionsTask = null;
	private boolean isHideEffect = false;
	private boolean isParalyzeEffect = false;
	private boolean isSanctuaryEffect = false;
	private float targetX = 0;
	private float targetY = 0;
	private float targetZ = 0;
	private int mpShield = 0;
	private boolean isPhysicalState = false;
	private boolean isMagicalState = false;
	/**
	 * Used for damage/heal values
	 */
	private int reserved1;
	/**
	 * Used for shield total hit damage;
	 */
	private int reserved2;
	/**
	 * Used for shield hit damage
	 */
	private int reserved3;
	/**
	 * Used for tick heals from HoT's (Heal Over Time)
	 */
	private int reserved4;
	/**
	 * Used for tick damages from DoT's (Damage Over Time)
	 */
	private int reserved5;
	private int[] reservedInts;
	/**
	 * Spell Status 1 : stumble 2 : knockback 4 : open aerial 8 : close aerial 16 : spin 32 : block 64 : parry 128 : dodge 256 : resist
	 */
	private SpellStatus spellStatus = SpellStatus.NONE;
	private DashStatus dashStatus = DashStatus.NONE;
	private AttackStatus attackStatus = AttackStatus.NORMALHIT;
	/**
	 * shield effects related
	 */
	private int shieldDefense;
	private int reflectedDamage = 0;
	private int reflectedSkillId = 0;
	private int protectedSkillId = 0;
	private int protectedDamage = 0;
	private int protectorId = 0;
	private boolean addedToController;
	private AttackCalcObserver[] attackStatusObserver;
	private AttackCalcObserver[] attackShieldObserver;
	private boolean launchSubEffect = true;
	private Effect subEffect;
	private boolean isStopped;
	private boolean isDelayedDamage;
	private boolean isDamageEffect;
	private boolean isPetOrder;
	private boolean isSummoning;
	
	// Xp Boost
	private boolean isXpBoost;
	
	// Ap Boost
	private boolean isApBoost;
	
	// Dr Boost
	private boolean isDrBoost;
	
	// Bdr Boost
	private boolean isBdrBoost;
	
	// Authorize Boost
	private boolean isAuthorizeBoost;
	
	// Enchant Boost
	private boolean isEnchantBoost;
	
	// Enchant Option Boost
	private boolean isEnchantOptionBoost;
	
	// Idun Drop Boost
	private boolean isIdunDropBoost;
	
	// New Effect
	private boolean isSprintFpReduce;
	private boolean isReturnCoolReduce;
	private boolean isOdellaRecoverIncrease;
	private boolean isCancelOnDmg;
	private boolean subEffectAbortedBySubConditions;
	private ItemTemplate itemTemplate;
	private boolean isDeathPenaltyReduce;
	private boolean isNoDeathPenalty;
	private boolean isNoDeathPenaltyReduce;
	private boolean isNoResurrectPenalty;
	private boolean isHiPass;
	/**
	 * Hate that will be placed on effected list
	 */
	private int tauntHate;
	/**
	 * Total hate that will be broadcasted
	 */
	private int effectHate;
	private final Map<Integer, EffectTemplate> successEffects = new ConcurrentHashMap<>();
	private int carvedSignet = 0;
	private int signetBurstedCount = 0;
	protected int abnormals;
	/**
	 * Action observer that should be removed after effect end
	 */
	private ActionObserver[] actionObserver;
	float x, y, z;
	int worldId, instanceId;
	/**
	 * used to force duration, you should be very careful when to use it
	 */
	private boolean forcedDuration = false;
	private boolean isForcedEffect = false;
	/**
	 * power of effect ( used for dispels)
	 */
	private int power = 10;
	/**
	 * accModBoost used for SignetBurstEffect
	 */
	private int accModBoost = 0;
	private EffectResult effectResult = EffectResult.NORMAL;
	
	/**
	 * Retrieves the {@code Skill} object associated with this effect.<br>
	 * This method returns the current active skill instance.
	 * @return The {@code Skill} object.
	 */
	public Skill getSkill()
	{
		return skill;
	}
	
	/**
	 * Updates the abnormal state of the owner.<br>
	 * This method applies the provided bitmask to the current {@code abnormalities} value.
	 * @param mask The bitmask representing the abnormal states to add.
	 */
	public void setAbnormal(int mask)
	{
		abnormals |= mask;
	}
	
	/**
	 * Retrieves the total number of abnormal effects currently active.<br>
	 * This value is used to track how many {@code AbnormalState} effects are applied.
	 * @return The count of active abnormal effects as an {@code int}.
	 */
	public int getAbnormals()
	{
		return abnormals;
	}
	
	/**
	 * Creates a new {@code Effect} instance for a specific skill action.<br>
	 * This constructor initializes the source, target, and skill details.<br>
	 * It also sets up periodic actions and initial power based on the template.
	 * @param effector The {@link Creature} who performs the action.
	 * @param effected The {@link Creature} who receives the effect.
	 * @param skillTemplate The {@link SkillTemplate} defining the skill properties.
	 * @param skillLevel The level of the skill being used.
	 * @param duration The length of time the effect lasts in seconds.
	 */
	public Effect(Creature effector, Creature effected, SkillTemplate skillTemplate, int skillLevel, int duration)
	{
		this.effector = effector;
		this.effected = effected;
		this.skillTemplate = skillTemplate;
		this.skillLevel = skillLevel;
		this.duration = duration;
		periodicActions = skillTemplate.getPeriodicActions();
		
		power = initializePower(skillTemplate);
	}
	
	/**
	 * Creates a new {@link Effect} instance using an {@code ItemTemplate}.<br>
	 * This constructor initializes the effect with all standard parameters.<br>
	 * It also assigns the specific item template to this effect.
	 * @param effector The {@link Creature} that performs the action.
	 * @param effected The {@link Creature} that receives the effect.
	 * @param skillTemplate The template defining the skill properties.
	 * @param skillLevel The level of the skill being used.
	 * @param duration The length of time the effect lasts.
	 * @param itemTemplate The {@link ItemTemplate} associated with this effect.
	 */
	public Effect(Creature effector, Creature effected, SkillTemplate skillTemplate, int skillLevel, int duration, ItemTemplate itemTemplate)
	{
		this(effector, effected, skillTemplate, skillLevel, duration);
		this.itemTemplate = itemTemplate;
	}
	
	/**
	 * Creates a new {@code Effect} instance based on a specific {@link Skill}.<br>
	 * This constructor initializes the effect using the properties of the provided {@code Skill}.
	 * @param skill The {@code Skill} used to generate this effect.
	 * @param effected The {@code Creature} that is receiving the effect.
	 * @param duration The length of time the effect lasts.
	 * @param itemTemplate The {@code ItemTemplate} associated with this effect.
	 */
	public Effect(Skill skill, Creature effected, int duration, ItemTemplate itemTemplate)
	{
		this(skill.getEffector(), effected, skill.getSkillTemplate(), skill.getSkillLevel(), duration, itemTemplate);
		this.skill = skill;
	}
	
	/**
	 * Updates the 3D coordinates and location identifiers for this effect.<br>
	 * This method sets the {@code x}, {@code y}, and {@code z} values.<br>
	 * It also updates the {@code worldId} and {@code instanceId}.
	 * @param worldId The unique identifier for the game world.
	 * @param instanceId The unique identifier for the specific instance.
	 * @param x The horizontal coordinate in the 3D space.
	 * @param y The vertical coordinate in the 3D space.
	 * @param z The depth coordinate in the 3D space.
	 */
	public void setWorldPosition(int worldId, int instanceId, float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldId = worldId;
		this.instanceId = instanceId;
	}
	
	/**
	 * Retrieves the unique identifier of the creature performing the effect.<br>
	 * This method calls {@code getObjectId} on the internal effector object.
	 * @return The {@code int} ID of the effector.
	 */
	public int getEffectorId()
	{
		return effector.getObjectId();
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this effect.<br>
	 * This value is obtained from the {@link SkillTemplate}.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillTemplate.getSkillId();
	}
	
	/**
	 * Retrieves the display name of the skill.<br>
	 * This method looks up the {@link SkillTemplate} using the current {@code skillId}.
	 * @return The name of the skill as a {@code String}.
	 */
	public String getSkillName()
	{
		return skillTemplate.getName();
	}
	
	/**
	 * Retrieves the {@link SkillTemplate} associated with this skill.<br>
	 * It uses the current {@code skillId} to fetch data from the {@code DataManager}.
	 * @return The {@code SkillTemplate} object for this skill.
	 */
	public SkillTemplate getSkillTemplate()
	{
		return skillTemplate;
	}
	
	/**
	 * Retrieves the sub-type of the current skill.<br>
	 * This value is obtained from the associated {@link SkillTemplate}.
	 * @return the {@code SkillSubType} of the skill.
	 */
	public SkillSubType getSkillSubType()
	{
		return skillTemplate.getSubType();
	}
	
	/**
	 * Retrieves the stack name for this skill.<br>
	 * This value is obtained from the associated {@link SkillTemplate}.
	 * @return The stack name as a {@code String}.
	 */
	public String getStack()
	{
		return skillTemplate.getStack();
	}
	
	/**
	 * Retrieves the current level of the skill.<br>
	 * This value is used to determine the power of the action performed by the {@link AbstractAI}.
	 * @return The integer level of the skill.
	 */
	public int getSkillLevel()
	{
		return skillLevel;
	}
	
	/**
	 * Retrieves the level of the skill stack.<br>
	 * This value is obtained from the {@link SkillTemplate}.
	 * @return The integer level of the skill stack.
	 */
	public int getSkillStackLvl()
	{
		return skillTemplate.getLvl();
	}
	
	/**
	 * Retrieves the category of the skill.<br>
	 * This method returns the {@code SkillType} from the associated {@link SkillTemplate}.
	 * @return the {@code SkillType} of this skill.
	 */
	public SkillType getSkillType()
	{
		return skillTemplate.getType();
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
	 * Updates the duration of the effect.<br>
	 * This method sets the {@code duration} field to a new value.
	 * @param newDuration The new duration value to apply.
	 */
	public void setDuration(int newDuration)
	{
		duration = newDuration;
	}
	
	/**
	 * Retrieves the creature that is affected by this effect.
	 * @return the {@link Creature} instance being targeted.
	 */
	public Creature getEffected()
	{
		return effected;
	}
	
	/**
	 * Retrieves the creature that performs the effect.<br>
	 * This is the entity responsible for casting or triggering the skill.
	 * @return the {@link Creature} acting as the effector.
	 */
	public Creature getEffector()
	{
		return effector;
	}
	
	/**
	 * Checks if the skill is a passive ability.<br>
	 * This method retrieves the status from the {@link SkillTemplate}.
	 * @return {@code true} if the skill is passive, {@code false} otherwise.
	 */
	public boolean isPassive()
	{
		return skillTemplate.isPassive();
	}
	
	/**
	 * Sets the background task for this effect.<br>
	 * This method assigns a {@code Future<?>} object to the internal task field.
	 * @param task The {@code Future<?>} task to be assigned.
	 */
	public void setTask(Future<?> task)
	{
		this.task = task;
	}
	
	/**
	 * Retrieves a specific periodic task from the internal list.<br>
	 * The method uses a one-based index to access the tasks.
	 * @param i The position of the task in the list.
	 * @return A {@code Future<?>} representing the requested periodic task.
	 */
	public Future<?> getPeriodicTask(int i)
	{
		return periodicTasks[i - 1];
	}
	
	/**
	 * Assigns a specific task to the internal list of periodic tasks.<br>
	 * This method stores the provided {@code Future<?>} at the specified index.<br>
	 * The index is adjusted by subtracting 1 to match zero-based array logic.
	 * @param periodicTask The {@code Future<?>} object to be stored.
	 * @param i The position in the list where the task should be placed.
	 */
	public void setPeriodicTask(Future<?> periodicTask, int i)
	{
		if (periodicTasks == null)
		{
			periodicTasks = new Future<?>[4];
		}
		
		periodicTasks[i - 1] = periodicTask;
	}
	
	/**
	 * Retrieves the first reserved value for this effect.<br>
	 * This value is used for internal data storage.
	 * @return the integer value of {@code reserved1}
	 */
	public int getReserved1()
	{
		return reserved1;
	}
	
	/**
	 * Sets the first reserved value for this effect.<br>
	 * This value is used for internal data storage.
	 * @param reserved1 The integer value to store in {@code reserved1}.
	 */
	public void setReserved1(int reserved1)
	{
		this.reserved1 = reserved1;
	}
	
	/**
	 * Retrieves the second reserved value for this effect.<br>
	 * This value is used for internal data storage.
	 * @return the {@code int} value of {@code reserved2}
	 */
	public int getReserved2()
	{
		return reserved2;
	}
	
	/**
	 * Sets the second reserved value for this effect.<br>
	 * This value is used for internal data storage.
	 * @param reserved2 The integer value to store in {@code reserved2}.
	 */
	public void setReserved2(int reserved2)
	{
		this.reserved2 = reserved2;
	}
	
	/**
	 * Retrieves the third reserved value for this effect.<br>
	 * This value is used for internal data storage.
	 * @return the {@code int} value of the third reserved field.
	 */
	public int getReserved3()
	{
		return reserved3;
	}
	
	/**
	 * Sets the third reserved value for this effect.<br>
	 * This value is used for internal data storage.
	 * @param reserved3 The integer value to store in {@code reserved3}.
	 */
	public void setReserved3(int reserved3)
	{
		this.reserved3 = reserved3;
	}
	
	/**
	 * Retrieves the value of the fourth reserved field.<br>
	 * This value is used for internal data storage.
	 * @return the integer value of {@code reserved4}
	 */
	public int getReserved4()
	{
		return reserved4;
	}
	
	/**
	 * Sets the value for the fourth reserved field.<br>
	 * This field is used for internal data storage.
	 * @param reserved4 The integer value to assign to {@code reserved4}.
	 */
	public void setReserved4(int reserved4)
	{
		this.reserved4 = reserved4;
	}
	
	/**
	 * Retrieves the value of the fifth reserved field.<br>
	 * This value is used for internal data storage.
	 * @return the integer value of {@code reserved5}
	 */
	public int getReserved5()
	{
		return reserved5;
	}
	
	/**
	 * Sets the value for the fifth reserved field.<br>
	 * This is used to store additional data that does not fit into standard fields.
	 * @param reserved5 The integer value to assign to {@code reserved5}.
	 */
	public void setReserved5(int reserved5)
	{
		this.reserved5 = reserved5;
	}
	
	/**
	 * Retrieves the current status of the attack.<br>
	 * This method returns the {@code AttackStatus} object associated with this result.
	 * @return The {@code AttackStatus} of the attack.
	 */
	public AttackStatus getAttackStatus()
	{
		return attackStatus;
	}
	
	/**
	 * Updates the current {@code AttackStatus} of the effect.<br>
	 * This method sets the status used for calculating attacks.
	 * @param attackStatus The new {@link AttackStatus} to apply.
	 */
	public void setAttackStatus(AttackStatus attackStatus)
	{
		this.attackStatus = attackStatus;
	}
	
	/**
	 * Retrieves the list of effects associated with the skill template.<br>
	 * This method fetches all {@link EffectTemplate} objects from the underlying data.
	 * @return a {@code List} containing all {@link EffectTemplate} objects.
	 */
	public List<EffectTemplate> getEffectTemplates()
	{
		return skillTemplate.getEffects().getEffects();
	}
	
	/**
	 * Checks if the skill provides an instant MP heal.<br>
	 * This method retrieves the {@code Effects} from the skill template.<br>
	 * It returns {@code true} if the effects exist and are marked as instant MP heals.
	 * @return {@code true} if the skill is an instant MP heal, otherwise {@code false}.
	 */
	public boolean isMphealInstant()
	{
		final Effects effects = skillTemplate.getEffects();
		return (effects != null) && effects.isMpHealInstant();
	}
	
	/**
	 * Checks if the skill is a toggle type.<br>
	 * This method returns {@code true} if the activation attribute is set to {@code TOGGLE}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the skill is a toggle; {@code false} otherwise.
	 */
	public boolean isToggle()
	{
		return skillTemplate.getActivationAttribute() == ActivationAttribute.TOGGLE;
	}
	
	/**
	 * Checks if the skill requires a chant.<br>
	 * This method verifies if the {@code targetSlot} of the associated {@link SkillTemplate} is set to {@code CHANT}.
	 * @return {@code true} if the skill is a chant, otherwise {@code false}.
	 */
	public boolean isChant()
	{
		return skillTemplate.getTargetSlot() == SkillTargetSlot.CHANT;
	}
	
	/**
	 * Checks if the skill belongs to the Ranger Eye category.<br>
	 * This method identifies specific skills like Hunter's Eye or Aiming.
	 * @return {@code true} if the skill is a Ranger Eye skill, otherwise {@code false}.
	 */
	public boolean isRangerEye()
	{
		final int skillId = skillTemplate.getSkillId();
		switch (skillId)
		{
			case 796: // Strong Shots.
			case 809: // Dodging.
			case 813: // Focused Shots.
			case 888: // Hunter's Might.
			case 889: // Bestial Fury.
			case 1053: // Aiming.
			case 1099: // Hunter's Eye.
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Checks if the skill targets a buff slot.<br>
	 * This method returns {@code true} if the target slot is {@code BUFF}.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if it is a buff, {@code false} otherwise.
	 */
	public boolean isBuff()
	{
		return skillTemplate.getTargetSlot() == SkillTargetSlot.BUFF;
	}
	
	/**
	 * Retrieves the target slot for this skill.<br>
	 * This method looks up the {@code ordinal()} value from the {@link SkillTemplate}.
	 * @return The integer index of the target slot.
	 */
	public int getTargetSlot()
	{
		return skillTemplate.getTargetSlot().ordinal();
	}
	
	/**
	 * Retrieves the target slot for the associated skill.<br>
	 * This value is obtained from the {@link SkillTemplate}.
	 * @return the {@code SkillTargetSlot} of the skill template.
	 */
	public SkillTargetSlot getTargetSlotEnum()
	{
		return skillTemplate.getTargetSlot();
	}
	
	/**
	 * Retrieves the target slot level from the {@link SkillTemplate}.<br>
	 * This value determines which specific slot is targeted by the skill.
	 * @return the integer value of the target slot level.
	 */
	public int getTargetSlotLevel()
	{
		return skillTemplate.getTargetSlotLevel();
	}
	
	/**
	 * Retrieves the dispel category for this effect.<br>
	 * This value is obtained from the associated {@link SkillTemplate}.
	 * @return the {@code DispelCategoryType} of the skill.
	 */
	public DispelCategoryType getDispelCategory()
	{
		return skillTemplate.getDispelCategory();
	}
	
	/**
	 * Retrieves the required dispel level for this skill.<br>
	 * This value is fetched from the {@link SkillTemplate}.
	 * @return The integer value of the required dispel level.
	 */
	public int getReqDispelLevel()
	{
		return skillTemplate.getReqDispelLevel();
	}
	
	/**
	 * Retrieves an {@link AttackCalcObserver} from the internal list.<br>
	 * The index provided is 1-based, so it maps to the array at {@code i - 1}.
	 * @param i The 1-based position of the observer to retrieve.
	 * @return The {@link AttackCalcObserver} at the specified position, or {@code null} if none exists.
	 */
	public AttackCalcObserver getAttackStatusObserver(int i)
	{
		return attackStatusObserver != null ? attackStatusObserver[i - 1] : null;
	}
	
	/**
	 * Registers an {@link AttackCalcObserver} at a specific index.<br>
	 * This method initializes the observer array if it is currently {@code null}.<br>
	 * The observer is stored using a 1-based index system.
	 * @param attackStatusObserver The observer to be registered.
	 * @param i The position in the array where the observer should be placed.
	 */
	public void setAttackStatusObserver(AttackCalcObserver attackStatusObserver, int i)
	{
		if (this.attackStatusObserver == null)
		{
			this.attackStatusObserver = new AttackCalcObserver[4];
		}
		
		this.attackStatusObserver[i - 1] = attackStatusObserver;
	}
	
	/**
	 * Retrieves a specific {@link AttackCalcObserver} from the internal list.<br>
	 * The index provided is 1-based, so it maps to the array at position {@code i - 1}.<br>
	 * Returns {@code null} if no observer exists at that position.
	 * @param i The 1-based index of the observer to retrieve.
	 * @return The {@link AttackCalcObserver} at the specified index, or {@code null}.
	 */
	public AttackCalcObserver getAttackShieldObserver(int i)
	{
		return attackShieldObserver != null ? attackShieldObserver[i - 1] : null;
	}
	
	/**
	 * Registers an {@link AttackCalcObserver} for a specific shield slot.<br>
	 * This method initializes the observer array if it is currently {@code null}.<br>
	 * The index provided is converted to a zero-based position.
	 * @param attackShieldObserver The observer to be assigned to the shield slot.
	 * @param i The slot index for the shield observer.
	 */
	public void setAttackShieldObserver(AttackCalcObserver attackShieldObserver, int i)
	{
		if (this.attackShieldObserver == null)
		{
			this.attackShieldObserver = new AttackCalcObserver[4];
		}
		
		this.attackShieldObserver[i - 1] = attackShieldObserver;
	}
	
	/**
	 * Retrieves a specific integer from the reserved values list.<br>
	 * It returns {@code 0} if the internal list is {@code null}.
	 * @param i The index of the value to retrieve.
	 * @return The integer value at the specified position.
	 */
	public int getReservedInt(int i)
	{
		return reservedInts != null ? reservedInts[i - 1] : 0;
	}
	
	/**
	 * Sets a specific reserved integer value.<br>
	 * This method updates the internal array of reserved integers.
	 * @param i The index to update, starting from {@code 1}.
	 * @param value The new integer value to store.
	 */
	public void setReservedInt(int i, int value)
	{
		if (reservedInts == null)
		{
			reservedInts = new int[4];
		}
		
		reservedInts[i - 1] = value;
	}
	
	/**
	 * Checks if the attack should trigger a sub-effect.<br>
	 * This is used to determine if additional effects are launched after an attack.
	 * @return {@code true} if a sub-effect is launched, {@code false} otherwise.
	 */
	public boolean isLaunchSubEffect()
	{
		return launchSubEffect;
	}
	
	/**
	 * Sets whether a sub-effect should be triggered during the attack.<br>
	 * Use {@code true} to enable the effect.<br>
	 * Use {@code false} to disable the effect.
	 * @param launchSubEffect The boolean value determining if the sub-effect launches.
	 */
	public void setLaunchSubEffect(boolean launchSubEffect)
	{
		this.launchSubEffect = launchSubEffect;
	}
	
	/**
	 * Retrieves the current defense value of the shield.<br>
	 * This value is used to calculate damage reduction.
	 * @return The integer value of the shield defense.
	 */
	public int getShieldDefense()
	{
		return shieldDefense;
	}
	
	/**
	 * Sets the defense value for the shield.<br>
	 * This updates the {@code shieldDefense} field of this object.
	 * @param shieldDefense The new defense value to apply to the shield.
	 */
	public void setShieldDefense(int shieldDefense)
	{
		this.shieldDefense = shieldDefense;
	}
	
	/**
	 * Retrieves the amount of damage reflected during an attack.<br>
	 * This value is stored in the {@code reflectedDamage} field.
	 * @return The total amount of reflected damage as an {@code int}.
	 */
	public int getReflectedDamage()
	{
		return reflectedDamage;
	}
	
	/**
	 * Sets the amount of damage reflected by this effect.<br>
	 * This value determines how much damage is sent back to the attacker.
	 * @param value The amount of damage to be reflected.
	 */
	public void setReflectedDamage(int value)
	{
		reflectedDamage = value;
	}
	
	/**
	 * Retrieves the ID of the skill that was reflected.<br>
	 * This value is used to identify which specific skill caused a reflection effect.
	 * @return The {@code int} identifier for the reflected skill.
	 */
	public int getReflectedSkillId()
	{
		return reflectedSkillId;
	}
	
	/**
	 * Sets the unique identifier for the reflected skill.<br>
	 * This value is used to identify which skill triggered a reflection effect.
	 * @param value The {@code int} ID of the reflected skill.
	 */
	public void setReflectedSkillId(int value)
	{
		reflectedSkillId = value;
	}
	
	/**
	 * Retrieves the unique identifier for the skill providing protection.<br>
	 * This value is used to identify which specific skill triggered a protective effect.
	 * @return The {@code int} ID of the protected skill.
	 */
	public int getProtectedSkillId()
	{
		return protectedSkillId;
	}
	
	/**
	 * Sets the unique identifier for a protected skill.<br>
	 * This value is used to determine which skills are shielded.
	 * @param skillId The {@code int} ID of the protected skill.
	 */
	public void setProtectedSkillId(int skillId)
	{
		protectedSkillId = skillId;
	}
	
	/**
	 * Retrieves the amount of damage that was blocked or mitigated.<br>
	 * This value is used to track protection effects during an attack.
	 * @return The total {@code protectedDamage} as an {@code int}.
	 */
	public int getProtectedDamage()
	{
		return protectedDamage;
	}
	
	/**
	 * Sets the amount of damage that is protected.<br>
	 * This value is used to determine how much damage is blocked by a protector.
	 * @param protectedDamage The amount of damage to be protected.
	 */
	public void setProtectedDamage(int protectedDamage)
	{
		this.protectedDamage = protectedDamage;
	}
	
	/**
	 * Retrieves the unique identifier for the protector.<br>
	 * This value is used to identify which entity provided protection.
	 * @return The {@code int} ID of the protector.
	 */
	public int getProtectorId()
	{
		return protectorId;
	}
	
	/**
	 * Sets the unique identifier for the protector.<br>
	 * This value is used to identify which entity provides protection.
	 * @param protectorId The {@code int} ID of the protector.
	 */
	public void setProtectorId(int protectorId)
	{
		this.protectorId = protectorId;
	}
	
	/**
	 * Retrieves the current status of the spell.<br>
	 * This method returns the {@code SpellStatus} object associated with this effect.
	 * @return The current {@code SpellStatus}.
	 */
	public SpellStatus getSpellStatus()
	{
		return spellStatus;
	}
	
	/**
	 * Updates the current status of the spell.<br>
	 * This method sets the {@code spellStatus} field to a new value.
	 * @param spellStatus The new {@link SpellStatus} to apply.
	 */
	public void setSpellStatus(SpellStatus spellStatus)
	{
		this.spellStatus = spellStatus;
	}
	
	/**
	 * Retrieves the current status of the dash.<br>
	 * This method returns the {@code DashStatus} value for the skill.
	 * @return The current {@code DashStatus}.
	 */
	public DashStatus getDashStatus()
	{
		return dashStatus;
	}
	
	/**
	 * Updates the current dash status of the effect.<br>
	 * This method sets the {@code dashStatus} field to a new value.
	 * @param dashStatus The new {@link DashStatus} to apply.
	 */
	public void setDashStatus(DashStatus dashStatus)
	{
		this.dashStatus = dashStatus;
	}
	
	/**
	 * Retrieves the value of the carved signet.<br>
	 * This method returns the current {@code int} value stored in the {@code carvedSignet} field.
	 * @return The integer value of the carved signet.
	 */
	public int getCarvedSignet()
	{
		return carvedSignet;
	}
	
	/**
	 * Sets the carved signet value for this effect.<br>
	 * This updates the internal {@code carvedSignet} field.
	 * @param value The new integer value to assign to the carved signet.
	 */
	public void setCarvedSignet(int value)
	{
		carvedSignet = value;
	}
	
	/**
	 * Retrieves the secondary effect associated with this instance.<br>
	 * This is useful when a skill triggers an additional {@link Effect}.
	 * @return the {@code Effect} object or {@code null} if no sub-effect exists.
	 */
	public Effect getSubEffect()
	{
		return subEffect;
	}
	
	/**
	 * Sets the secondary effect for this {@link Effect}.<br>
	 * This allows an effect to have a nested or additional behavior.
	 * @param subEffect The {@code Effect} object to be assigned as the sub-effect.
	 */
	public void setSubEffect(Effect subEffect)
	{
		this.subEffect = subEffect;
	}
	
	/**
	 * Checks if a specific effect exists in the success effects list.<br>
	 * It compares the provided {@code effectId} against all registered templates.
	 * @param effectId The unique identifier of the effect to search for.
	 * @return {@code true} if the ID is found, otherwise {@code false}.
	 */
	public boolean containsEffectId(int effectId)
	{
		for (EffectTemplate template : successEffects.values())
		{
			if (template.getEffectid() == effectId)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the transformation type from the skill effects.<br>
	 * It searches through all effects in the {@link SkillTemplate}.<br>
	 * If a {@code TransformEffect} is found, it returns its specific type.<br>
	 * Returns {@code TransformType.NONE} if no such effect exists.
	 * @return The {@link TransformType} of the first transformation effect found.
	 */
	public TransformType getTransformType()
	{
		for (EffectTemplate et : skillTemplate.getEffects().getEffects())
		{
			if (et instanceof TransformEffect)
			{
				return ((TransformEffect) et).getTransformType();
			}
		}
		
		return TransformType.NONE;
	}
	
	/**
	 * Sets whether the duration of the effect is forced.<br>
	 * This determines if the duration should be strictly applied.
	 * @param forcedDuration The value to set for {@code forcedDuration}.
	 */
	public void setForcedDuration(boolean forcedDuration)
	{
		this.forcedDuration = forcedDuration;
	}
	
	/**
	 * Sets whether the effect is forced to occur.<br>
	 * This determines if the effect bypasses normal conditions.
	 * @param isForcedEffect The boolean value to set for {@code isForcedEffect}.
	 */
	public void setIsForcedEffect(boolean isForcedEffect)
	{
		this.isForcedEffect = isForcedEffect;
	}
	
	/**
	 * Checks if the effect is forced or triggered by a material skill.<br>
	 * This method returns {@code true} if the effect is manually forced.<br>
	 * It also returns {@code true} if the skill ID belongs to a material skill.
	 * @return {@code true} if the effect is forced or a material skill, {@code false} otherwise.
	 */
	public boolean getIsForcedEffect()
	{
		return isForcedEffect || DataManager.MATERIAL_DATA.isMaterialSkill(getSkillId());
	}
	
	/**
	 * Correct lifecycle of Effect - INITIALIZE - APPLY - START - END
	 */
	/**
	 * Initializes the skill by calculating all associated effects.<br>
	 * It determines specific effect flags like damage, hide, or paralyze based on the templates.<br>
	 * This method also sets the final attack status and spell status for the skill execution.
	 */
	public void initialize()
	{
		if (skillTemplate.getEffects() == null)
		{
			System.out.println("Effect = NULL");
			return;
		}
		
		for (EffectTemplate template : getEffectTemplates())
		{
			template.calculate(this);
			
			if (template instanceof DelayedSpellAttackInstantEffect)
			{
				setDelayedDamage(true);
			}
			
			if (template instanceof PetOrderUseUltraSkillEffect)
			{
				setPetOrder(true);
			}
			
			if (template instanceof SummonEffect)
			{
				setSumonning(true);
			}
			
			if (template instanceof DamageEffect)
			{
				setDamageEffect(true);
			}
			
			if (template instanceof HideEffect)
			{
				isHideEffect = true;
			}
			
			if (template instanceof ParalyzeEffect)
			{
				isParalyzeEffect = true;
			}
			
			if (template instanceof SanctuaryEffect)
			{
				isSanctuaryEffect = true;
			}
		}
		
		for (EffectTemplate template : getEffectTemplates())
		{
			template.calculateHate(this);
		}
		
		if (isLaunchSubEffect())
		{
			for (EffectTemplate template : successEffects.values())
			{
				template.calculateSubEffect(this);
			}
		}
		
		if (successEffects.isEmpty())
		{
			skillMoveType = SkillMoveType.RESIST;
			if (getSkillType() == SkillType.PHYSICAL)
			{
				if (getEffector() instanceof Player)
				{
					final Player pl = (Player) getEffector();
					if ((pl.getPlayerClass() == PlayerClass.GUNNER) || (pl.getPlayerClass() == PlayerClass.RIDER))
					{
						if (getAttackStatus() == AttackStatus.CRITICAL)
						{
							setAttackStatus(AttackStatus.CRITICAL_RESIST); // TODO recheck
						}
						else
						{
							setAttackStatus(AttackStatus.RESIST);
						}
					}
					else
					{
						if (getAttackStatus() == AttackStatus.CRITICAL)
						{
							setAttackStatus(AttackStatus.CRITICAL_DODGE);
						}
						else
						{
							setAttackStatus(AttackStatus.DODGE);
						}
					}
				}
				else
				{
					if (getAttackStatus() == AttackStatus.CRITICAL)
					{
						setAttackStatus(AttackStatus.CRITICAL_DODGE);
					}
					else
					{
						setAttackStatus(AttackStatus.DODGE);
					}
				}
			}
			else
			{
				if (getAttackStatus() == AttackStatus.CRITICAL)
				{
					setAttackStatus(AttackStatus.CRITICAL_RESIST); // TODO recheck
				}
				else
				{
					setAttackStatus(AttackStatus.RESIST);
				}
			}
		}
		
		// set spellstatus for sm_castspell_end packet
		switch (AttackStatus.getBaseStatus(getAttackStatus()))
		{
			case DODGE:
				setSpellStatus(SpellStatus.DODGE);
				break;
			case PARRY:
				if (getSpellStatus() == SpellStatus.NONE)
				{
					setSpellStatus(SpellStatus.PARRY);
				}
				break;
			case BLOCK:
				if (getSpellStatus() == SpellStatus.NONE)
				{
					setSpellStatus(SpellStatus.BLOCK);
				}
				break;
			case RESIST:
				setSpellStatus(SpellStatus.RESIST);
				break;
			default:
				break;
		}
	}
	
	/**
	 * Executes the logic for applying a specific skill effect to a target.<br>
	 * This method handles special conditions like gliding states and hate broadcasting.<br>
	 * It then iterates through all successful effects to apply them to the affected creature.
	 */
	public void applyEffect()
	{
		// Move this to a more appropriate location because fear is not applied to gliding players.
		if (isFearEffect())
		{
			if (getEffected().isInState(CreatureState.GLIDING))
			{
				// Only if the player is not in flying mode; TODO: verify on retail if this check is needed.
				if (getEffected() instanceof Player)
				{
					if (!((Player) getEffected()).isInFlyingMode())
					{
						((Player) getEffected()).getFlyController().onStopGliding(true);
						return;
					}
				}
			}
		}
		
		/**
		 * broadcast final hate to all visible objects
		 */
		// TODO hostile_type?
		if (effectHate != 0)
		{
			if ((getEffected() instanceof Npc) && !isDelayedDamage() && !isPetOrder() && !isSummoning())
			{
				getEffected().getAggroList().addHate(effector, 1);
			}
			
			effector.getController().broadcastHate(effectHate);
		}
		
		if ((skillTemplate.getEffects() == null) || successEffects.isEmpty())
		{
			return;
		}
		
		for (EffectTemplate template : successEffects.values())
		{
			if (getEffected() != null)
			{
				if (getEffected().getLifeStats().isAlreadyDead() && !skillTemplate.hasResurrectEffect())
				{
					continue;
				}
			}
			
			template.applyEffect(this);
			template.startSubEffect(this);
		}
	}
	
	/**
	 * Checks if a specific skill belongs to the rider effect category.<br>
	 * This method returns {@code true} for known mounting and mobility skills.<br>
	 * It returns {@code false} for all other skill IDs.
	 * @param skillId The unique identifier of the skill to check.
	 * @return {@code true} if the skill is a rider effect, otherwise {@code false}.
	 */
	public boolean isRiderEffect(int skillId)
	{
		switch (skillId)
		{
			case 2767: // Embark
			case 2768:
			case 2769:
			case 2770:
			case 2771:
			case 2772:
			case 2773:
			case 2774:
			case 2775:
			case 2776:
			case 2777:
			case 2778:
			case 2440: // Kinetic Battery
			case 2441:
			case 2442:
			case 2443:
			case 2444:
			case 2445:
			case 2446:
			case 2447:
			case 2448:
			case 2449:
			case 2579: // Kinetic Bulwark
			case 2580:
			case 2581:
			case 2421: // Mobility Thrusters
			case 2422:
			case 2736: // Stability Thrusters
			case 2737:
			case 2738:
			case 2739:
			case 2740:
			case 2838: // Mounting Frustration
			case 2839:
			case 2840:
			case 2841:
			case 2842:
			case 2843:
			case 2844:
			case 2845:
			case 2846:
			case 2847:
			case 2848:
				return true;
		}
		
		return false;
	}
	
	/**
	 * Determines the specific category of a skill effect.<br>
	 * This method identifies if a skill is Impassion, Exultation, or another type.
	 * @param skillId The unique identifier for the skill to check.
	 * @return An integer where 0 represents Impassion, 1 represents Exultation, and 2 represents any other skill.
	 */
	public int checkForToggleBardEffect(int skillId)
	{
		// Returns 0 for Impassion, 1 for Exultation, and 2 for any other skill.
		final int Impassion = 4590;
		final int Exultation = 4589;
		
		if (skillId == Impassion)
		{
			return 0;
		}
		
		if (skillId == Exultation)
		{
			return 1;
		}
		
		return 2;
	}
	
	/**
	 * Checks if a specific skill belongs to the Battery or Bulwark categories.<br>
	 * This method identifies the effect type based on the provided {@code skillId}.<br>
	 * It returns different integer values depending on which category the skill matches.
	 * @param skillId The unique identifier of the skill to check.
	 * @return Returns 0 for Battery skills, 1 for Bulwark skills, and 2 for all other skills.
	 */
	public int checkForToggleRideEffect(int skillId)
	{
		final int[] Battery =
		{
			2440,
			2441,
			2442,
			2443,
			2444,
			2445,
			2446,
			2447,
			2448,
			2449
		};
		final int[] Bulwark =
		{
			2579,
			2580,
			2581
		};
		
		for (int id : Battery)
		{
			if (id == skillId)
			{
				return 0;
			}
		}
		
		for (int id : Bulwark)
		{
			if (id == skillId)
			{
				return 1;
			}
		}
		
		return 2;
	}
	
	/**
	 * Starts the application of effects for a skill.<br>
	 * This method handles periodic actions and calculates the remaining duration.<br>
	 * It also manages special logic for toggle, ride, and kinetic skills.
	 * @param restored Indicates whether the effect was restored from a previous state.
	 */
	public void startEffect(boolean restored)
	{
		if (successEffects.isEmpty())
		{
			return;
		}
		
		schedulePeriodicActions();
		
		for (EffectTemplate template : successEffects.values())
		{
			template.startEffect(this);
			checkUseEquipmentConditions();
			checkCancelOnDmg();
		}
		
		if ((isToggle() && (effector instanceof Player)) || isRiderEffect(getSkillId()))
		{
			activateToggleSkill();
		}
		
		if (!restored && !forcedDuration)
		{
			duration = getEffectsDuration();
		}
		
		if ((checkForToggleBardEffect(getSkillId()) == 0) || ((checkForToggleBardEffect(getSkillId()) == 1) && (checkForToggleBardEffect(getSkillId()) != 2)))
		{
			if (checkForToggleBardEffect(getSkillId()) == 0)
			{
				checkBardEffects(0);
			}
			else
			{
				checkBardEffects(1);
			}
		}
		
		if ((checkForToggleRideEffect(getSkillId()) == 0) || ((checkForToggleRideEffect(getSkillId()) == 1) && (checkForToggleRideEffect(getSkillId()) != 2)))
		{
			if (checkForToggleRideEffect(getSkillId()) == 0)
			{
				checkRideEffects(0);
			}
			else
			{
				checkRideEffects(1);
			}
		}
		
		if (isKineticSkill())
		{
			duration = skillTemplate.getToggleTimer();
		}
		
		if (isToggle())
		{
			duration = skillTemplate.getToggleTimer();
		}
		
		if (duration == 0)
		{
			return;
		}
		
		if (isOpenAerialSkill())
		{
			duration = skillTemplate.getDuration();
		}
		
		endTime = System.currentTimeMillis() + duration;
		task = ThreadPoolManager.getInstance().schedule(() -> endEffect(), duration);
	}
	
	/**
	 * Checks if the current skill belongs to the kinetic category.<br>
	 * This method identifies specific skills like Kinetic Battery and Kinetic Bulwark.
	 * @return {@code true} if the skill is a kinetic skill, otherwise {@code false}.
	 */
	private boolean isKineticSkill()
	{
		switch (getSkillId())
		{
			case 2440:// Kinetic BAttery Start
			case 2441:
			case 2442:
			case 2443:
			case 2444:
			case 2445:
			case 2446:
			case 2447:
			case 2448:
			case 2449:
			case 2579:// Kinetic Bulwark Start
			case 2580:
			case 2581:
				return true;
		}
		
		return false;
	}
	
	/**
	 * Checks and removes specific visual effects from a player.<br>
	 * This method handles the removal of {@code Kinetic Battery} or {@code Kinetic Bulwark} effects.<br>
	 * It identifies which skills to remove based on the provided {@code code}.
	 * @param code The identifier used to determine which set of effects to clear. Use {@code 0} for Bulwark and other values for Battery.
	 */
	private void checkRideEffects(int code)
	{
		final int[] Battery =
		{
			2440,
			2441,
			2442,
			2443,
			2444,
			2445,
			2446,
			2447,
			2448,
			2449
		};
		final int[] Bulwark =
		{
			2579,
			2580,
			2581
		};
		
		// The values represent Kinetic Battery for 0, Kinetic Bulwark for 1, and NULL for 2.
		if (effector instanceof Player)
		{
			final Player player = (Player) effector;
			
			if (code == 0)
			{
				for (int skillId : Bulwark)
				{
					if (player.getEffectController().isNoshowPresentBySkillId(skillId))
					{
						player.getEffectController().removeNoshowEffect(skillId);
					}
				}
			}
			else
			{
				for (int skillId : Battery)
				{
					if (player.getEffectController().isNoshowPresentBySkillId(skillId))
					{
						player.getEffectController().removeNoshowEffect(skillId);
					}
				}
			}
		}
	}
	
	/**
	 * Validates and manages specific visual effects for players.<br>
	 * This method checks if a player has conflicting "noshow" effects based on the provided code.<br>
	 * It removes the incorrect effect to ensure only one is active at a time.
	 * @param code The identifier used to determine which effect to check, where {@code 0} represents Impassion and {@code 1} represents Excultation.
	 */
	private void checkBardEffects(int code)
	{
		// 0 equals Impassion (4590) and 1 equals Excultation (4589).
		if (effector instanceof Player)
		{
			final Player player = (Player) effector;
			
			if (code == 0)
			{
				if (player.getEffectController().isNoshowPresentBySkillId(4589))
				{
					// if Impassion is on check for Excultation
					player.getEffectController().removeNoshowEffect(4589);
				}
			}
			else
			{
				if (player.getEffectController().isNoshowPresentBySkillId(4590))
				{
					// if Excultation is on Check for Impassion
					player.getEffectController().removeNoshowEffect(4590);
				}
			}
		}
	}
	
	/**
	 * Activates the skill for the player.<br>
	 * This method sends an {@code SM_SKILL_ACTIVATION} packet to the {@code effector}.<br>
	 * It uses the skill ID retrieved from {@code getSkillId}.
	 */
	private void activateToggleSkill()
	{
		PacketSendUtility.sendPacket((Player) effector, new SM_SKILL_ACTIVATION(getSkillId(), true));
	}
	
	/**
	 * Deactivates the current skill for the player.<br>
	 * Sends an {@code SM_SKILL_ACTIVATION} packet with a {@code false} value to indicate deactivation.
	 */
	private void deactivateToggleSkill()
	{
		PacketSendUtility.sendPacket((Player) effector, new SM_SKILL_ACTIVATION(getSkillId(), false));
	}
	
	/**
	 * Stops the current effect and cleans up all associated resources.<br>
	 * This method handles template completion, stance removal, and task termination.<br>
	 * It marks the effect as stopped so it cannot be processed again.
	 */
	public synchronized void endEffect()
	{
		if (isStopped)
		{
			return;
		}
		
		for (EffectTemplate template : successEffects.values())
		{
			template.endEffect(this);
		}
		
		// if effect is a stance, remove stance from player
		if (effector instanceof Player)
		{
			final Player player = (Player) effector;
			if (player.getController().getStanceSkillId() == getSkillId())
			{
				PacketSendUtility.sendPacket(player, new SM_PLAYER_STANCE(player, 0));
				player.getController().startStance(0);
			}
		}
		
		if ((isToggle() && (effector instanceof Player)) || isRiderEffect(getSkillId()))
		{
			deactivateToggleSkill();
		}
		
		stopTasks();
		effected.getEffectController().clearEffect(this);
		isStopped = true;
		addedToController = false;
	}
	
	/**
	 * Stops all active tasks associated with this effect.<br>
	 * This method cancels the main task and all periodic tasks.<br>
	 * It also calls {@code stopPeriodicActions} to clean up actions.
	 */
	public void stopTasks()
	{
		if (task != null)
		{
			task.cancel(false);
			task = null;
		}
		
		if (periodicTasks != null)
		{
			for (Future<?> periodicTask : periodicTasks)
			{
				if (periodicTask != null)
				{
					periodicTask.cancel(false);
					periodicTask = null;
				}
			}
		}
		
		stopPeriodicActions();
	}
	
	/**
	 * Calculates the remaining time until this effect expires.<br>
	 * It returns the difference between the end time and the current system time.<br>
	 * If the duration is 24 hours or longer, it returns -1.
	 * @return The remaining time in milliseconds as an {@code int}.
	 */
	public int getRemainingTime()
	{
		return getDuration() >= 86400000 ? -1 : (int) (endTime - System.currentTimeMillis());
	}
	
	/**
	 * Retrieves the end time of the effect.<br>
	 * This value represents when the current effect is scheduled to finish.
	 * @return The end time as a {@code long}.
	 */
	public long getEndTime()
	{
		return endTime;
	}
	
	/**
	 * Retrieves the base damage value for Player vs Player combat.<br>
	 * This value is fetched from the associated {@link SkillTemplate}.
	 * @return The integer damage value used in {@code pvp} scenarios.
	 */
	public int getPvpDamage()
	{
		return skillTemplate.getPvpDamage();
	}
	
	/**
	 * Retrieves the {@link ItemTemplate} for this drop.<br>
	 * It returns the cached {@code template} if it exists.<br>
	 * If the {@code template} is {@code null}, it fetches the data from {@link DataManager}.
	 * @return The {@link ItemTemplate} associated with this drop.
	 */
	public ItemTemplate getItemTemplate()
	{
		return itemTemplate;
	}
	
	/**
	 * Registers this effect with the target creature's controller.<br>
	 * It checks if the target is alive and not already registered.<br>
	 * This method updates the {@code addedToController} flag to {@code true}.
	 */
	public void addToEffectedController()
	{
		if ((!addedToController) && (effected.getLifeStats() != null) && (!effected.getLifeStats().isAlreadyDead()))
		{
			effected.getEffectController().addEffect(this);
			addedToController = true;
		}
	}
	
	/**
	 * Retrieves the hate value generated by this effect.<br>
	 * This value is used to determine how much aggression an NPC feels toward the effector.
	 * @return The current {@code int} hate value.
	 */
	public int getEffectHate()
	{
		return effectHate;
	}
	
	/**
	 * Sets the hate value for this effect.<br>
	 * This value determines how much aggression is generated by the effect.
	 * @param effectHate The new hate value to assign.
	 */
	public void setEffectHate(int effectHate)
	{
		this.effectHate = effectHate;
	}
	
	/**
	 * Retrieves the current taunt hate value.<br>
	 * This value represents how much attention a creature draws from enemies.
	 * @return The integer value of the taunt hate.
	 */
	public int getTauntHate()
	{
		return tauntHate;
	}
	
	/**
	 * Sets the amount of hate generated by a taunt.<br>
	 * This value determines how much attention the target will receive from enemies.
	 * @param tauntHate The new hate value to assign.
	 */
	public void setTauntHate(int tauntHate)
	{
		this.tauntHate = tauntHate;
	}
	
	/**
	 * Retrieves an {@link ActionObserver} from the internal list.<br>
	 * The index provided is 1-based, so it maps to the array at {@code i - 1}.
	 * @param i The 1-based position of the observer to retrieve.
	 * @return The {@link ActionObserver} at the specified position.
	 */
	public ActionObserver getActionObserver(int i)
	{
		return actionObserver[i - 1];
	}
	
	/**
	 * Registers an {@link ActionObserver} at a specific index.<br>
	 * If the internal array is null, it initializes a new array of size {@code 4}.<br>
	 * The observer is stored using a 1-based index.
	 * @param observer The {@link ActionObserver} to be registered.
	 * @param i The position in the list where the observer should be placed.
	 */
	public void setActionObserver(ActionObserver observer, int i)
	{
		if (actionObserver == null)
		{
			actionObserver = new ActionObserver[4];
		}
		
		actionObserver[i - 1] = observer;
	}
	
	/**
	 * Adds a success effect to the internal collection.<br>
	 * This method maps the {@code EffectTemplate} to its specific position.
	 * @param effect The {@code EffectTemplate} to be added.
	 */
	public void addSucessEffect(EffectTemplate effect)
	{
		successEffects.put(effect.getPosition(), effect);
	}
	
	/**
	 * Checks if there is a success effect at the specified position.<br>
	 * This method looks into the {@code successEffects} map.<br>
	 * It returns {@code true} if an effect exists and {@code false} otherwise.
	 * @param position The index or key to check in the effects list.
	 * @return {@code true} if an effect is found, {@code false} if it is null.
	 */
	public boolean isInSuccessEffects(int position)
	{
		if (successEffects.get(position) != null)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves all effects associated with a successful skill activation.<br>
	 * This method returns the collection of {@link EffectTemplate} objects.
	 * @return A {@code Collection} of {@code EffectTemplate} objects.
	 */
	public Collection<EffectTemplate> getSuccessEffect()
	{
		return successEffects.values();
	}
	
	/**
	 * Clears the current success effects list.<br>
	 * Populates the {@code successEffects} map with all available templates from {@code getEffectTemplates}.
	 */
	public void addAllEffectToSucess()
	{
		successEffects.clear();
		for (EffectTemplate template : getEffectTemplates())
		{
			successEffects.put(template.getPosition(), template);
		}
	}
	
	/**
	 * Removes all success effects from the current effect.<br>
	 * This method clears the {@code successEffects} collection.
	 */
	public void clearSucessEffects()
	{
		successEffects.clear();
	}
	
	/**
	 * Schedules a recurring task to execute periodic actions.<br>
	 * This method checks if there are any active actions to perform.<br>
	 * It uses {@code ThreadPoolManager} to run the actions at a fixed rate.
	 */
	private void schedulePeriodicActions()
	{
		if ((periodicActions == null) || (periodicActions.getPeriodicActions() == null) || periodicActions.getPeriodicActions().isEmpty())
		{
			return;
		}
		
		final int checktime = periodicActions.getChecktime();
		periodicActionsTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			for (PeriodicAction action : periodicActions.getPeriodicActions())
			{
				action.act(Effect.this);
			}
		}, 0, checktime);
	}
	
	/**
	 * Stops any active periodic tasks associated with this effect.<br>
	 * It cancels the {@code periodicActionsTask} if it is not {@code null}.<br>
	 * The task reference is then set to {@code null}.
	 */
	private void stopPeriodicActions()
	{
		if (periodicActionsTask != null)
		{
			periodicActionsTask.cancel(false);
			periodicActionsTask = null;
		}
	}
	
	/**
	 * Calculates the final duration of all effects for this skill.<br>
	 * This method iterates through all successful effects to find a common valid time.<br>
	 * It applies adjustments based on skill level, random values, and buff types.<br>
	 * The result is capped at a maximum value of 86400000 milliseconds.
	 * @return the calculated duration as an {@code int}.
	 */
	public int getEffectsDuration()
	{
		int duration = 0;
		
		// iterate skill's effects until we can calculate a duration time, which is valid for all of them
		final Iterator<EffectTemplate> itr = successEffects.values().iterator();
		while (itr.hasNext() && (duration == 0))
		{
			final EffectTemplate et = itr.next();
			int effectDuration = et.getDuration2() + (et.getDuration1() * getSkillLevel());
			if (et.getRandomTime() > 0)
			{
				effectDuration -= Rnd.get(et.getRandomTime());
			}
			
			duration = duration > effectDuration ? duration : effectDuration;
		}
		
		// adjust with BOOST_DURATION
		switch (skillTemplate.getSubType())
		{
			case BUFF:
				duration = effector.getGameStats().getStat(StatEnum.BOOST_DURATION_BUFF, duration).getCurrent();
				break;
			default:
				break;
		}
		
		// adjust with pvp duration
		if ((effected instanceof Player) && (skillTemplate.getPvpDuration() != 0))
		{
			duration = (duration * skillTemplate.getPvpDuration()) / 100;
		}
		
		if (duration > 86400000)
		{
			duration = 86400000;
		}
		
		return duration;
	}
	
	/**
	 * Checks if the skill belongs to a deity avatar.<br>
	 * This method retrieves the status from the {@link SkillTemplate}.
	 * @return {@code true} if it is a deity avatar, {@code false} otherwise.
	 */
	public boolean isDeityAvatar()
	{
		return skillTemplate.isDeityAvatar();
	}
	
	/**
	 * Retrieves the X coordinate of the bookmark.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return y;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return z;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return worldId;
	}
	
	/**
	 * Retrieves the unique identifier for this instance.<br>
	 * This ID was provided during the construction of {@link CollisionResults}.
	 * @return The {@code int} value representing the instance ID.
	 */
	public int getInstanceId()
	{
		return instanceId;
	}
	
	/**
	 * Retrieves the movement type associated with this skill.<br>
	 * This value determines how a character moves when performing the action.
	 * @return the {@code SkillMoveType} of the current skill.
	 */
	public SkillMoveType getSkillMoveType()
	{
		return skillMoveType;
	}
	
	/**
	 * Sets the movement type for this specific skill.<br>
	 * This value determines how the character moves while performing the action.
	 * @param skillMoveType The {@code SkillMoveType} to assign to this skill.
	 */
	public void setSkillMoveType(SkillMoveType skillMoveType)
	{
		this.skillMoveType = skillMoveType;
	}
	
	/**
	 * Retrieves the X coordinate of the target.<br>
	 * This value represents the horizontal position in the game world.
	 * @return The {@code float} value of the target's X coordinate.
	 */
	public float getTargetX()
	{
		return targetX;
	}
	
	/**
	 * Retrieves the vertical position of the target.<br>
	 * This value represents the {@code y} coordinate in the world.
	 * @return The current {@code y} coordinate as a {@code float}.
	 */
	public float getTargetY()
	{
		return targetY;
	}
	
	/**
	 * Retrieves the Z coordinate of the target.<br>
	 * This value represents the vertical position in the world.
	 * @return The {@code float} value of the target's Z coordinate.
	 */
	public float getTargetZ()
	{
		return targetZ;
	}
	
	/**
	 * Sets the target coordinates for this effect.<br>
	 * This updates the {@code x}, {@code y}, and {@code z} values.
	 * @param x The horizontal coordinate of the target.
	 * @param y The vertical coordinate of the target.
	 * @param z The depth coordinate of the target.
	 */
	public void setTargetLoc(float x, float y, float z)
	{
		targetX = x;
		targetY = y;
		targetZ = z;
	}
	
	/**
	 * Sets whether the sub-effect was aborted by specific conditions.<br>
	 * This updates the {@code subEffectAbortedBySubConditions} field.
	 * @param value The boolean state to set for the aborted status.
	 */
	public void setSubEffectAborted(boolean value)
	{
		subEffectAbortedBySubConditions = value;
	}
	
	/**
	 * Checks if the sub-effect was cancelled by specific sub-conditions.<br>
	 * This method returns {@code true} if an interruption occurred.
	 * @return {@code true} if aborted, {@code false} otherwise.
	 */
	public boolean isSubEffectAbortedBySubConditions()
	{
		return subEffectAbortedBySubConditions;
	}
	
	/**
	 * Sets whether this effect provides an experience point boost.<br>
	 * Use {@code true} to enable the boost and {@code false} to disable it.
	 * @param value The boolean state of the experience boost.
	 */
	public void setXpBoost(boolean value)
	{
		isXpBoost = value;
	}
	
	/**
	 * Checks if the effect provides an experience point boost.<br>
	 * Returns {@code true} if it is a boost, otherwise returns {@code false}.
	 * @return The status of the experience boost.
	 */
	public boolean isXpBoost()
	{
		return isXpBoost;
	}
	
	/**
	 * Sets whether the AP boost effect is active.<br>
	 * This updates the {@code isApBoost} flag for this effect.
	 * @param value The boolean value to set for the AP boost status.
	 */
	public void setApBoost(boolean value)
	{
		isApBoost = value;
	}
	
	/**
	 * Checks if the current effect provides an AP boost.<br>
	 * This is used to determine if the skill increases Attack Power.
	 * @return {@code true} if it is an AP boost, {@code false} otherwise.
	 */
	public boolean isApBoost()
	{
		return isApBoost;
	}
	
	/**
	 * Sets whether the skill provides a damage boost.<br>
	 * This updates the {@code isDrBoost} flag for this effect.
	 * @param value The boolean value to set for the damage boost status.
	 */
	public void setDrBoost(boolean value)
	{
		isDrBoost = value;
	}
	
	/**
	 * Checks if the current effect provides a Dr. Boost.<br>
	 * This is used to identify specific buffs or status changes.
	 * @return {@code true} if it is a Dr. Boost, otherwise {@code false}.
	 */
	public boolean isDrBoost()
	{
		return isDrBoost;
	}
	
	/**
	 * Sets whether the BDR boost is active.<br>
	 * This updates the {@code isBdrBoost} flag for the effect.
	 * @param value The boolean state to set for the BDR boost.
	 */
	public void setBdrBoost(boolean value)
	{
		isBdrBoost = value;
	}
	
	/**
	 * Checks if the BDR boost is currently active.<br>
	 * This method returns {@code true} if the boost is enabled.
	 * @return {@code true} if the boost is active, {@code false} otherwise.
	 */
	public boolean isBdrBoost()
	{
		return isBdrBoost;
	}
	
	/**
	 * Sets whether the skill provides an authorization boost.<br>
	 * This flag determines if the effect should be applied to the authorized status.
	 * @param value The {@code boolean} value to set for the authorization boost.
	 */
	public void setAuthorizeBoost(boolean value)
	{
		isAuthorizeBoost = value;
	}
	
	/**
	 * Checks if the boost authorization is currently enabled.<br>
	 * This method returns the value of the {@code isAuthorizeBoost} flag.
	 * @return {@code true} if authorized, {@code false} otherwise.
	 */
	public boolean isAuthorizeBoost()
	{
		return isAuthorizeBoost;
	}
	
	/**
	 * Sets whether this effect provides an enchantment boost.<br>
	 * Use {@code true} to enable the boost and {@code false} to disable it.
	 * @param value The boolean state of the enchantment boost.
	 */
	public void setEnchantBoost(boolean value)
	{
		isEnchantBoost = value;
	}
	
	/**
	 * Checks if the effect provides an enchantment boost.<br>
	 * This property determines if the skill enhances enchanted items.
	 * @return {@code true} if it is an enchant boost, {@code false} otherwise.
	 */
	public boolean isEnchantBoost()
	{
		return isEnchantBoost;
	}
	
	/**
	 * Sets whether the enchantment option boost is active.<br>
	 * This updates the {@code isEnchantOptionBoost} flag for this effect.
	 * @param value The boolean value to set for the enchantment option boost.
	 */
	public void setEnchantOptionBoost(boolean value)
	{
		isEnchantOptionBoost = value;
	}
	
	/**
	 * Checks if the effect provides an enchantment option boost.<br>
	 * This helps identify specific types of item enhancements.
	 * @return {@code true} if it is a boost, {@code false} otherwise.
	 */
	public boolean isEnchantOptionBoost()
	{
		return isEnchantOptionBoost;
	}
	
	/**
	 * Sets whether the Idun drop boost is active.<br>
	 * This updates the {@code isIdunDropBoost} flag for this effect.
	 * @param value The boolean value to set for the drop boost.
	 */
	public void setIdunDropBoost(boolean value)
	{
		isIdunDropBoost = value;
	}
	
	/**
	 * Checks if the Idun drop boost is currently active.<br>
	 * This status determines if special loot bonuses are applied.
	 * @return {@code true} if the boost is active, {@code false} otherwise.
	 */
	public boolean isIdunDropBoost()
	{
		return isIdunDropBoost;
	}
	
	/**
	 * Sets whether the skill reduces the death penalty.<br>
	 * This determines if players face fewer penalties upon dying.
	 * @param value The boolean state to set for this reduction.
	 */
	public void setNoDeathPenaltyReduce(boolean value)
	{
		isNoDeathPenaltyReduce = value;
	}
	
	/**
	 * Checks if the skill reduces death penalties.<br>
	 * This flag determines whether the effect should bypass standard penalty rules.
	 * @return {@code true} if the reduction is active, {@code false} otherwise.
	 */
	public boolean isNoDeathPenaltyReduce()
	{
		return isNoDeathPenaltyReduce;
	}
	
	/**
	 * Sets whether the skill has a death penalty.<br>
	 * Use {@code true} to disable the penalty.<br>
	 * Use {@code false} to enable the penalty.
	 * @param value The boolean value to set for the death penalty status.
	 */
	public void setNoDeathPenalty(boolean value)
	{
		isNoDeathPenalty = value;
	}
	
	/**
	 * Checks if the skill has no death penalty.<br>
	 * This determines whether a player loses items or experience upon dying.
	 * @return {@code true} if there is no death penalty, {@code false} otherwise.
	 */
	public boolean isNoDeathPenalty()
	{
		return isNoDeathPenalty;
	}
	
	/**
	 * Sets whether the resurrection penalty is disabled.<br>
	 * Use {@code true} to remove the penalty.<br>
	 * Use {@code false} to keep the standard penalty.
	 * @param value The boolean state for the resurrection penalty.
	 */
	public void setNoResurrectPenalty(boolean value)
	{
		isNoResurrectPenalty = value;
	}
	
	/**
	 * Checks if the resurrection penalty is disabled.<br>
	 * Returns {@code true} if there is no penalty.<br>
	 * Returns {@code false} if a penalty applies.
	 * @return The status of the resurrection penalty.
	 */
	public boolean isNoResurrectPenalty()
	{
		return isNoResurrectPenalty;
	}
	
	/**
	 * Sets whether the skill effect should bypass certain checks.<br>
	 * This determines if the effect is allowed to pass through specific filters.
	 * @param value The {@code boolean} flag to set for high priority passing.
	 */
	public void setHiPass(boolean value)
	{
		isHiPass = value;
	}
	
	/**
	 * Checks if the skill has high priority.<br>
	 * This determines if the effect should bypass certain checks.
	 * @return {@code true} if it is a high priority skill, {@code false} otherwise.
	 */
	public boolean isHiPass()
	{
		return isHiPass;
	}
	
	/**
	 * Sets whether the return cooldown reduction is active.<br>
	 * This determines if the skill reduces the cooldown of returned attacks.
	 * @param value The boolean state to set for {@code isReturnCoolReduce}.
	 */
	public void setReturnCoolReduce(boolean value)
	{
		isReturnCoolReduce = value;
	}
	
	/**
	 * Checks if the skill should reduce the cooldown.<br>
	 * This value determines whether a specific reduction effect is applied.
	 * @return {@code true} if the cooldown reduction is active, {@code false} otherwise.
	 */
	public boolean isReturnCoolReduce()
	{
		return isReturnCoolReduce;
	}
	
	/**
	 * Sets whether the Odella recovery increase effect is active.<br>
	 * This updates the {@code isOdellaRecoverIncrease} flag for this skill effect.
	 * @param value The boolean state to set for the recovery increase.
	 */
	public void setOdellaRecoverIncrease(boolean value)
	{
		isOdellaRecoverIncrease = value;
	}
	
	/**
	 * Checks if the Odella Recover Increase effect is active.<br>
	 * This method returns {@code true} if the condition is met.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the increase is active, {@code false} otherwise.
	 */
	public boolean isOdellaRecoverIncrease()
	{
		return isOdellaRecoverIncrease;
	}
	
	/**
	 * Sets whether the sprint fast protect reduction is active.<br>
	 * This determines if the skill reduces damage while sprinting.
	 * @param value The boolean state to set for {@code isSprintFpReduce}.
	 */
	public void setSprintFpReduce(boolean value)
	{
		isSprintFpReduce = value;
	}
	
	/**
	 * Checks if the sprint force point reduction is active.<br>
	 * This flag determines if specific movement mechanics are modified.
	 * @return {@code true} if the reduction is active, {@code false} otherwise.
	 */
	public boolean isSprintFpReduce()
	{
		return isSprintFpReduce;
	}
	
	/**
	 * Sets whether the death penalty reduction is active.<br>
	 * This determines if the character receives a reduced penalty upon death.
	 * @param value The boolean state to set for {@code isDeathPenaltyReduce}.
	 */
	public void setDeathPenaltyReduce(boolean value)
	{
		isDeathPenaltyReduce = value;
	}
	
	/**
	 * Checks if the skill reduces death penalties.<br>
	 * This property determines if the effect applies to penalty reduction mechanics.
	 * @return {@code true} if the skill reduces death penalties, {@code false} otherwise.
	 */
	public boolean isDeathPenaltyReduce()
	{
		return isDeathPenaltyReduce;
	}
	
	/**
	 * Checks if the current effect meets the required equipment conditions.<br>
	 * It retrieves the {@code Conditions} from the skill template.<br>
	 * If no conditions exist, it returns {@code true}.<br>
	 * Otherwise, it validates the conditions against this object.
	 * @return {@code true} if all conditions are met or none are defined; {@code false} otherwise.
	 */
	private boolean useEquipmentConditionsCheck()
	{
		final Conditions useEquipConditions = skillTemplate.getUseEquipmentconditions();
		return useEquipConditions != null ? useEquipConditions.validate(this) : true;
	}
	
	/**
	 * Checks if the skill requires specific equipment to be active.<br>
	 * It adds an {@link ActionObserver} to monitor for unequip events.<br>
	 * If a required item is removed, it ends the effect and removes the observer.
	 */
	private void checkUseEquipmentConditions()
	{
		// If the skill has use equipment conditions, observe for an unequip event and remove the effect if it occurs.
		if ((getSkillTemplate().getUseEquipmentconditions() != null) && (getSkillTemplate().getUseEquipmentconditions().getConditions().size() > 0))
		{
			final ActionObserver observer = new ActionObserver(ObserverType.UNEQUIP)
			{
				@Override
				public void unequip(Item item, Player owner)
				{
					if (!useEquipmentConditionsCheck())
					{
						endEffect();
						effected.getObserveController().removeObserver(this);
					}
				}
			};
			effected.getObserveController().addObserver(observer);
		}
	}
	
	/**
	 * Checks if the effect should be cancelled when the target takes damage.<br>
	 * It attaches observers to handle {@code ATTACKED} and {@code DOT_ATTACKED} actions.<br>
	 * If these actions occur, it removes the current effect using {@code getSkillId}.
	 */
	private void checkCancelOnDmg()
	{
		if (isCancelOnDmg())
		{
			effected.getObserveController().attach(new ActionObserver(ObserverType.ATTACKED)
			{
				@Override
				public void attacked(Creature creature)
				{
					effected.getEffectController().removeEffect(getSkillId());
				}
			});
			
			effected.getObserveController().attach(new ActionObserver(ObserverType.DOT_ATTACKED)
			{
				@Override
				public void dotattacked(Creature creature, Effect dotEffect)
				{
					effected.getEffectController().removeEffect(getSkillId());
				}
			});
		}
	}
	
	/**
	 * Sets whether the effect should be cancelled when damage is taken.<br>
	 * This determines if receiving damage stops the current action.
	 * @param value The boolean value to set for {@code isCancelOnDmg}.
	 */
	public void setCancelOnDmg(boolean value)
	{
		isCancelOnDmg = value;
	}
	
	/**
	 * Checks if the effect should be cancelled when damage occurs.<br>
	 * Returns {@code true} if the effect stops upon dealing damage.<br>
	 * Returns {@code false} otherwise.
	 * @return a boolean indicating if the effect is cancelled on damage.
	 */
	public boolean isCancelOnDmg()
	{
		return isCancelOnDmg;
	}
	
	/**
	 * Finalizes all active effects associated with this instance.<br>
	 * It iterates through the {@code successEffects} map.<br>
	 * Each {@link EffectTemplate} calls its own {@code endEffect} method.
	 */
	public void endEffects()
	{
		for (EffectTemplate template : successEffects.values())
		{
			template.endEffect(this);
		}
	}
	
	/**
	 * Checks if the effect contains a fear status.<br>
	 * It iterates through all successful effects to find any instance of {@link FearEffect}.
	 * @return {@code true} if at least one fear effect is found, otherwise {@code false}.
	 */
	public boolean isFearEffect()
	{
		for (EffectTemplate template : successEffects.values())
		{
			if (template instanceof FearEffect)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the effect deals damage after a delay.<br>
	 * This helps determine if the damage should be applied immediately or scheduled.
	 * @return {@code true} if the damage is delayed, {@code false} otherwise.
	 */
	public boolean isDelayedDamage()
	{
		return isDelayedDamage;
	}
	
	/**
	 * Sets whether the damage from this effect is delayed.<br>
	 * Use {@code true} to enable a delay before the damage occurs.<br>
	 * Use {@code false} to apply the damage immediately.
	 * @param value The boolean flag to set for delayed damage.
	 */
	public void setDelayedDamage(boolean value)
	{
		isDelayedDamage = value;
	}
	
	/**
	 * Checks if the current effect follows a pet order.<br>
	 * This determines if the action was initiated by a pet.
	 * @return {@code true} if it is a pet order, {@code false} otherwise.
	 */
	public boolean isPetOrder()
	{
		return isPetOrder;
	}
	
	/**
	 * Sets whether this effect is triggered by a pet.<br>
	 * Use {@code true} if the action comes from a pet.<br>
	 * Use {@code false} otherwise.
	 * @param value The boolean state to set for pet order.
	 */
	public void setPetOrder(boolean value)
	{
		isPetOrder = value;
	}
	
	/**
	 * Checks if the current effect involves a summoning action.<br>
	 * This helps identify skills that create new entities.
	 * @return {@code true} if it is a summoning skill, {@code false} otherwise.
	 */
	public boolean isSummoning()
	{
		return isSummoning;
	}
	
	/**
	 * Sets whether this effect is considered a summoning action.<br>
	 * This flag helps the engine identify specific types of skill behaviors.
	 * @param value The {@code boolean} value to set for the summoning state.
	 */
	public void setSumonning(boolean value)
	{
		isSummoning = value;
	}
	
	/**
	 * Determines the power value for a specific skill.<br>
	 * This method checks the {@code SkillTemplate} to assign different values based on its ID or activation attribute.<br>
	 * It handles special cases for buffs, debuffs, and NPC skills.
	 * @param skill The {@link SkillTemplate} used to determine the power value.
	 * @return The calculated integer power value.
	 */
	private int initializePower(SkillTemplate skill)
	{
		// tweak for pet order spirit substitution and bodyguard
		if (skill.getActivationAttribute().equals(ActivationAttribute.MAINTAIN))
		{
			return 30;
		}
		
		switch (skill.getSkillId())
		{
			case 4170: // Word Of Destruction I
			case 4171: // Word Of Destruction II
			case 4172: // Word Of Destruction III
			case 4173: // Word Of Destruction IV
			case 4174: // Word Of Destruction V
			case 4175: // Word Of Destruction VI
			case 1066: // Silence Arrow I
			case 1067: // Silence Arrow II
			case 1068: // Silence Arrow III
			case 1069: // Silence Arrow IV
			case 1070: // Silence Arrow V
			case 1071: // Silence Arrow VI
			case 1072: // Silence Arrow VII
			case 1073: // Silence Arrow VIII
			case 1074: // Silence Arrow IX
				return 20;
			case 2932: // Unwavering Devotion I
			case 3127: // Iron Skin I
			case 2922: // Empyrean Providence I
			case 3128: // Prayer Of Freedom I
			case 3839: // Spirit Preserve I
			case 1832: // Elemental Screen I
			case 1192: // Gain Mana I
			case 1193: // Gain Mana II
			case 1194: // Gain Mana III
			case 1195: // Gain Mana IV
			case 1196: // Gain Mana V
			case 1197: // Gain Mana VI
			case 1198: // Gain Mana VII
			case 1199: // Gain Mana VIII
			case 1200: // Gain Mana IX
			case 1201: // Gain Mana X
			case 1202: // Gain Mana XI
			case 1203: // Gain Mana XII
			case 4725: // [ArchDaeva] Gain Mana 5.1
			case 1022: // Shackle Arrow I
			case 1023: // Shackle Arrow II
			case 1024: // Shackle Arrow III
			case 1025: // Shackle Arrow IV
			case 1026: // Shackle Arrow V
			case 618: // Ankle Snare I
			case 1329: // Curse Of Weakness I
			case 1330: // Curse Of Weakness II
			case 1331: // Curse Of Weakness II
			case 1332: // Curse Of Weakness IV
			case 1333: // Curse Of Weakness V
			case 1334: // Curse Of Weakness VI
			case 1335: // Curse Of Weakness VII
			case 1336: // Curse Of Weakness VIII
			case 4144: // Chain Of Suffering I
			case 4145: // Chain Of Suffering II
			case 4146: // Chain Of Suffering III
			case 4147: // Chain Of Suffering IV
			case 4148: // Chain Of Suffering V
			case 4149: // Chain Of Suffering VI
			case 1754: // Stilling Word I
			case 3854: // Wing Root I Npc Skill
			case 18214: // Protective Shield
			case 18232: // Explosion Of Wrath
			case 18239: // Soul Petrify
				return 30;
			case 3790: // Cursecloud I
			case 3791: // Cursecloud II
			case 3792: // Cursecloud III
			case 3793: // Cursecloud IV
			case 3794: // Cursecloud V
			case 3795: // Cursecloud VI
				return 40;
			// Npc Skill
			case 18889: // Submissive Strike I
			case 18892: // Weeping Curtain I
			case 18994: // Weakness I
			case 19090: // Spinning Smash I
			case 19148: // Resistance I
			case 19504: // Canyonguard's Target I
			case 19505: // Relic Explosion I
			case 19512: // Sap Damage I
			case 19513: // Sap Damage II
			case 19514: // Sap Damage III
			case 19515: // Sap Damage IV
			case 19516: // Sap Damage V
			case 19644: // Total Exhaustion I
			case 19647: // Weaken I
				return 255;
		}
		
		return 10;
	}
	
	/**
	 * Retrieves the current power value.<br>
	 * This value is stored in the {@code power} field.
	 * @return The integer power value.
	 */
	public int getPower()
	{
		return power;
	}
	
	/**
	 * Sets the power value for this template.<br>
	 * This updates the {@code power} field used in calculations.
	 * @param power The new integer value to set for the power attribute.
	 */
	public void setPower(int power)
	{
		this.power = power;
	}
	
	/**
	 * Reduces the current power of the effect.<br>
	 * The method subtracts the specified amount from the internal {@code power} value.
	 * @param power The amount of power to remove.
	 * @return The updated power value after subtraction.
	 */
	public int removePower(int power)
	{
		this.power -= power;
		return this.power;
	}
	
	/**
	 * Sets the accuracy modification boost value.<br>
	 * This value determines how much the accuracy is increased by this effect.
	 * @param accModBoost The amount of accuracy boost to apply.
	 */
	public void setAccModBoost(int accModBoost)
	{
		this.accModBoost = accModBoost;
	}
	
	/**
	 * Retrieves the accuracy modification boost value.<br>
	 * This value is used to calculate how much a skill's accuracy is increased.
	 * @return The current {@code int} value of the accuracy boost.
	 */
	public int getAccModBoost()
	{
		return accModBoost;
	}
	
	/**
	 * Checks if the current effect is a hide effect.<br>
	 * This method returns {@code true} if the effect hides the target.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if it is a hide effect, {@code false} otherwise.
	 */
	public boolean isHideEffect()
	{
		return isHideEffect;
	}
	
	/**
	 * Checks if the current effect is a paralyze effect.<br>
	 * This method returns {@code true} if the effect matches {@link ParalyzeEffect}.
	 * @return {@code true} if it is a paralyze effect, otherwise {@code false}.
	 */
	public boolean isParalyzeEffect()
	{
		return isParalyzeEffect;
	}
	
	/**
	 * Checks if the current effect is a sanctuary effect.<br>
	 * This helps identify specific types of protective status effects.
	 * @return {@code true} if this is a sanctuary effect, {@code false} otherwise.
	 */
	public boolean isSanctuaryEffect()
	{
		return isSanctuaryEffect;
	}
	
	/**
	 * Checks if this effect deals damage.<br>
	 * It returns {@code true} if the effect is a damage type.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the effect causes damage, {@code false} otherwise.
	 */
	public boolean isDamageEffect()
	{
		return isDamageEffect;
	}
	
	/**
	 * Sets whether this effect deals damage.<br>
	 * Use {@code true} if the effect should be treated as a damage type.<br>
	 * Use {@code false} for non-damaging effects.
	 * @param isDamageEffect The boolean flag indicating if this is a damage effect.
	 */
	public void setDamageEffect(boolean isDamageEffect)
	{
		this.isDamageEffect = isDamageEffect;
	}
	
	/**
	 * Retrieves the current number of times a signet has burst.<br>
	 * This value tracks how many times the specific effect has triggered.
	 * @return The total count of signet bursts as an {@code int}.
	 */
	public int getSignetBurstedCount()
	{
		return signetBurstedCount;
	}
	
	/**
	 * Sets the number of times a signet has been bursted.<br>
	 * This value is used to track burst occurrences for specific skills.
	 * @param signetBurstedCount The new count of bursted signets.
	 */
	public void setSignetBurstedCount(int signetBurstedCount)
	{
		this.signetBurstedCount = signetBurstedCount;
	}
	
	/**
	 * Retrieves the final result of the processed effect.<br>
	 * This method returns the {@code EffectResult} object after all calculations are complete.
	 * @return The resulting {@code EffectResult}.
	 */
	public EffectResult getEffectResult()
	{
		return effectResult;
	}
	
	/**
	 * Sets the result of an effect.<br>
	 * This method updates the {@code effectResult} field for this instance.
	 * @param effectResult The {@code EffectResult} to be stored.
	 */
	public void setEffectResult(EffectResult effectResult)
	{
		this.effectResult = effectResult;
	}
	
	/**
	 * Retrieves the current MP shield value.<br>
	 * This value represents the amount of mana protected by a shield effect.
	 * @return The current {@code int} value of the MP shield.
	 */
	public int getMpShield()
	{
		return mpShield;
	}
	
	/**
	 * Sets the MP shield value for this effect.<br>
	 * This updates the {@code mpShield} field with a new integer value.
	 * @param mpShield The new value to set for the MP shield.
	 */
	public void setMpShield(int mpShield)
	{
		this.mpShield = mpShield;
	}
	
	/**
	 * Checks if the current skill effect is in a physical state.<br>
	 * This helps determine how the game engine handles specific interactions.
	 * @return {@code true} if the state is physical, {@code false} otherwise.
	 */
	public boolean isPhysicalState()
	{
		return isPhysicalState;
	}
	
	/**
	 * Sets whether the skill is in a physical state.<br>
	 * This determines if the effect should be treated as physical or magical.
	 * @param isPhysicalState The boolean value to set for the physical state.
	 */
	public void setIsPhysicalState(boolean isPhysicalState)
	{
		this.isPhysicalState = isPhysicalState;
	}
	
	/**
	 * Checks if the current state is considered magical.<br>
	 * This helps determine how effects interact with magic-resistant targets.
	 * @return {@code true} if the state is magical, otherwise {@code false}.
	 */
	public boolean isMagicalState()
	{
		return isMagicalState;
	}
	
	/**
	 * Updates the magical state of the effect.<br>
	 * This determines if the effect is considered a magic-based action.
	 * @param isMagicalState The new {@code boolean} value for the magical state.
	 */
	public void setIsMagicalState(boolean isMagicalState)
	{
		this.isMagicalState = isMagicalState;
	}
	
	/**
	 * Checks if the current skill is an aerial skill.<br>
	 * This method returns {@code true} for specific hardcoded skill IDs.<br>
	 * It returns {@code false} for all other skills.
	 * @return {@code true} if the skill is an aerial type, otherwise {@code false}.
	 */
	private boolean isOpenAerialSkill()
	{
		switch (getSkillId())
		{
			case 8224:
			case 8678:
			case 9173:
			case 19552:
			case 20371:
			case 20680:
			case 20872:
			case 21133:
			case 21476:
			case 21529:
			case 21911:
				return true;
		}
		
		return false;
	}
}
