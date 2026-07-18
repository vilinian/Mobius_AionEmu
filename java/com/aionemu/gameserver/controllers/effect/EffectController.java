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
package com.aionemu.gameserver.controllers.effect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_EFFECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.EffectType;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.EffectResult;
import com.aionemu.gameserver.skillengine.model.SkillSubType;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.skillengine.model.TransformType;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the application and lifecycle of game effects on {@link Creature} objects.<br>
 * It handles logic for applying, removing, and broadcasting {@link Effect} data to clients.
 * @author ATracer modified by Wakizashi, Sippolo, Cheatkiller
 */
public class EffectController
{
	private final Creature owner;
	protected Map<String, Effect> passiveEffectMap = new ConcurrentHashMap<>();
	protected Map<String, Effect> noshowEffects = new ConcurrentHashMap<>();
	protected Map<String, Effect> abnormalEffectMap = new ConcurrentHashMap<>();
	private final Lock lock = new ReentrantLock();
	protected int abnormals;
	private boolean isUnderShield = false;
	
	/**
	 * Creates a new {@link EffectController} for a specific creature.<br>
	 * This controller manages all effects applied to the {@code owner}.
	 * @param owner The {@link Creature} that will own this controller.
	 */
	public EffectController(Creature owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Retrieves the {@link Creature} that owns this AI instance.
	 * @return the {@code Creature} owner of this AI.
	 */
	public Creature getOwner()
	{
		return owner;
	}
	
	/**
	 * Checks if the owner of this controller is currently under a shield.
	 * @return {@code true} if the owner is shielded, otherwise {@code false}.
	 */
	public boolean isUnderShield()
	{
		return isUnderShield;
	}
	
	/**
	 * Updates the shield status of the creature.<br>
	 * This method sets whether the owner is currently protected by a shield.
	 * @param isUnderShield The new shield state to apply. Use {@code true} for active and {@code false} for inactive.
	 */
	public void setUnderShield(boolean isUnderShield)
	{
		this.isUnderShield = isUnderShield;
	}
	
	/**
	 * Checks if a specific skill belongs to the Bard class.<br>
	 * This method returns {@code true} for Inspiration, Exultation, and Impassion.
	 * @param skillId The unique identifier of the skill to check.
	 * @return {@code true} if the skill is a Bard effect, otherwise {@code false}.
	 */
	public boolean isBardEffect(int skillId)
	{
		switch (skillId)
		{
			case 4538: // Inspiration
			case 4589: // Exultation
			case 4590: // Impassion
				return true;
		}
		
		return false;
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
	 * Adds a new {@code Effect} to the owner.<br>
	 * This method handles logic for stacking, conflicts, and maximum limits.<br>
	 * It ensures that only valid effects are applied based on skill types.
	 * @param nextEffect The {@code Effect} object to be added.
	 */
	public void addEffect(Effect nextEffect)
	{
		final Map<String, Effect> mapToUpdate = getMapForEffect(nextEffect);
		
		lock.lock();
		try
		{
			if (nextEffect.isPassive())
			{
				boolean useEffectId = true;
				final Effect existingEffect = mapToUpdate.get(nextEffect.getStack());
				if ((existingEffect != null) && existingEffect.isPassive())
				{
					// Check the skill level when the stack levels are the same.
					if ((existingEffect.getSkillStackLvl() > nextEffect.getSkillStackLvl()) || ((existingEffect.getSkillStackLvl() == nextEffect.getSkillStackLvl()) && (existingEffect.getSkillLevel() > nextEffect.getSkillLevel())))
					{
						return;
					}
					
					existingEffect.endEffect();
					useEffectId = false;
				}
				
				if (useEffectId)
				{
					/**
					 * idea here is that effects with same effectId shouldnt stack effect with higher basiclvl takes priority
					 */
					for (Effect effect : mapToUpdate.values())
					{
						if (effect.getTargetSlot() == nextEffect.getTargetSlot())
						{
							for (EffectTemplate et : effect.getEffectTemplates())
							{
								if (et.getEffectid() == 0)
								{
									continue;
								}
								
								for (EffectTemplate et2 : nextEffect.getEffectTemplates())
								{
									if (et2.getEffectid() == 0)
									{
										continue;
									}
									
									if (et.getEffectid() == et2.getEffectid())
									{
										if (et.getBasicLvl() > et2.getBasicLvl())
										{
											return;
										}
										
										effect.endEffect();
									}
								}
							}
						}
					}
				}
			}
			
			final Effect conflictedEffect = findConflictedEffect(mapToUpdate, nextEffect);
			if (conflictedEffect != null)
			{
				conflictedEffect.endEffect();
			}
			
			// max 3 aura effects
			if (nextEffect.isToggle() && !nextEffect.isRiderEffect(nextEffect.getSkillTemplate().getSkillId()))
			{
				int mts = 1;
				
				if (nextEffect.getSkillSubType() == SkillSubType.CHANT)
				{
					mts = 3;
				}
				else if (isBardEffect(nextEffect.getSkillId()))
				{
					mts = 2;
				}
				else
				{
					mts = 1;
				}
				
				if (mapToUpdate.size() >= mts)
				{
					final Iterator<Effect> iter = mapToUpdate.values().iterator();
					final Effect effect = iter.next();
					effect.endEffect();
					iter.remove();
				}
			}
			
			// max 4 chants
			if (nextEffect.isChant())
			{
				final Collection<Effect> chants = getChantEffects();
				if (chants.size() >= 4)
				{
					final Iterator<Effect> chantIter = chants.iterator();
					chantIter.next().endEffect();
				}
			}
			
			// max 2 eyes
			if (nextEffect.isRangerEye())
			{
				final Collection<Effect> eyes = getRangerEyes();
				if (eyes.size() >= 2)
				{
					final Iterator<Effect> eyeIter = eyes.iterator();
					eyeIter.next().endEffect();
				}
			}
			
			if (!nextEffect.isPassive())
			{
				if (searchConflict(nextEffect))
				{
					return;
				}
				
				checkEffectCooldownId(nextEffect);
			}
			
			mapToUpdate.put(nextEffect.getStack(), nextEffect);
			
		}
		finally
		{
			lock.unlock();
		}
		
		// ? move into lock area
		nextEffect.startEffect(false);
		
		if (!nextEffect.isPassive())
		{
			broadCastEffects();
		}
	}
	
	/**
	 * Checks if an existing effect conflicts with a new one.<br>
	 * It compares the {@code conflictId} of the effects.<br>
	 * If a match is found, it returns the existing effect.
	 * @param mapToUpdate The map containing current effects to check.
	 * @param newEffect The new effect being added.
	 * @return The conflicting {@code Effect} object or {@code null} if no conflict exists.
	 */
	private Effect findConflictedEffect(Map<String, Effect> mapToUpdate, Effect newEffect)
	{
		final int conflictId = newEffect.getSkillTemplate().getConflictId();
		if (conflictId == 0)
		{
			return null;
		}
		
		for (Effect effect : mapToUpdate.values())
		{
			if (effect.getSkillTemplate().getConflictId() == conflictId)
			{
				return effect;
			}
		}
		
		return null;
	}
	
	/**
	 * Determines which internal map to use based on the properties of an {@code Effect}.<br>
	 * It checks if the effect is passive, a toggle, or an abnormal state.
	 * @param effect The {@code Effect} object to evaluate.
	 * @return A {@code Map} containing the relevant effects for the given type.
	 */
	private Map<String, Effect> getMapForEffect(Effect effect)
	{
		if (effect.isPassive())
		{
			return passiveEffectMap;
		}
		
		if (effect.isToggle())
		{
			return noshowEffects;
		}
		
		return abnormalEffectMap;
	}
	
	/**
	 * Retrieves an abnormal effect based on a specific stack key.<br>
	 * This method looks up the value in the {@code abnormalEffectMap}.
	 * @param stack The unique identifier for the effect stack.
	 * @return The {@link Effect} associated with the provided stack, or {@code null} if not found.
	 */
	public Effect getAnormalEffect(String stack)
	{
		return abnormalEffectMap.get(stack);
	}
	
	/**
	 * Checks if the owner currently has a specific abnormal effect.<br>
	 * It searches through all active effects in the {@code abnormalEffectMap}.
	 * @param skillId The unique identifier of the skill to check.
	 * @return {@code true} if an effect with the matching {@code skillId} exists, otherwise {@code false}.
	 */
	public boolean hasAbnormalEffect(int skillId)
	{
		final Iterator<Effect> localIterator = abnormalEffectMap.values().iterator();
		while (localIterator.hasNext())
		{
			final Effect localEffect = localIterator.next();
			if (localEffect.getSkillId() == skillId)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Sends a notification to all nearby clients about the current effects.<br>
	 * This method updates the {@code BroadcastMode} for the {@link Creature}.
	 */
	public void broadCastEffects()
	{
		owner.addPacketBroadcastMask(BroadcastMode.BROAD_CAST_EFFECTS);
	}
	
	/**
	 * Sends the current abnormal effects to all nearby clients.<br>
	 * This method uses {@code getAbnormalEffects} to retrieve the list of active effects.<br>
	 * It then broadcasts an {@code SM_ABNORMAL_EFFECT} packet to everyone around the {@code owner}.
	 */
	public void broadCastEffectsImp()
	{
		final List<Effect> effects = getAbnormalEffects();
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ABNORMAL_EFFECT(getOwner(), abnormals, effects));
	}
	
	/**
	 * Sends the current abnormal effect icons to a specific player.<br>
	 * This method uses {@code getAbnormalEffects} to retrieve the list of active effects.<br>
	 * It then sends an {@code SM_ABNORMAL_EFFECT} packet to the target.
	 * @param player The {@link Player} who will receive the effect icons.
	 */
	public void sendEffectIconsTo(Player player)
	{
		final List<Effect> effects = getAbnormalEffects();
		PacketSendUtility.sendPacket(player, new SM_ABNORMAL_EFFECT(getOwner(), abnormals, effects));
	}
	
	/**
	 * Removes a specific {@code Effect} from the owner.<br>
	 * This method identifies the correct map using {@code getMapForEffect}.<br>
	 * It deletes the effect based on its stack and then calls {@code broadCastEffects}.
	 * @param effect The {@code Effect} object to be removed.
	 */
	public void clearEffect(Effect effect)
	{
		final Map<String, Effect> mapForEffect = getMapForEffect(effect);
		mapForEffect.remove(effect.getStack());
		broadCastEffects();
	}
	
	/**
	 * Removes an effect from the creature based on its skill ID.<br>
	 * This method searches through abnormal, passive, and hidden effects.<br>
	 * It calls {@code endEffect} for every matching effect found.
	 * @param skillid The unique identifier of the skill to remove.
	 */
	public void removeEffect(int skillid)
	{
		for (Effect effect : abnormalEffectMap.values())
		{
			if (effect.getSkillId() == skillid)
			{
				effect.endEffect();
			}
		}
		
		for (Effect effect : passiveEffectMap.values())
		{
			if (effect.getSkillId() == skillid)
			{
				effect.endEffect();
			}
		}
		
		for (Effect effect : noshowEffects.values())
		{
			if (effect.getSkillId() == skillid)
			{
				effect.endEffect();
			}
		}
	}
	
	/**
	 * Removes hidden effects from the {@code abnormalEffectMap}.<br>
	 * This method checks if an effect is a hide effect and if the owner's visual state is less than 10.<br>
	 * If both conditions are met, it ends the effect and removes it from the map.
	 */
	public void removeHideEffects()
	{
		for (Effect effect : abnormalEffectMap.values())
		{
			if (effect.isHideEffect() && (owner.getVisualState() < 10))
			{
				effect.endEffect();
				abnormalEffectMap.remove(effect.getStack());
			}
		}
	}
	
	/**
	 * Removes all active paralyze effects from the owner.<br>
	 * This method iterates through the {@code abnormalEffectMap}.<br>
	 * It calls {@code endEffect} on any effect that is a paralyze type.<br>
	 * The corresponding entry is then removed from the map.
	 */
	public void removeParalyzeEffects()
	{
		for (Effect effect : abnormalEffectMap.values())
		{
			if (effect.isParalyzeEffect())
			{
				effect.endEffect();
				abnormalEffectMap.remove(effect.getStack());
			}
		}
	}
	
	/**
	 * Removes an effect based on its unique identifier.<br>
	 * This method iterates through all abnormal effects and ends any that contain the specified {@code effectId}.
	 * @param effectId The unique ID of the effect to be removed.
	 */
	public void removeEffectByEffectId(int effectId)
	{
		for (Effect effect : abnormalEffectMap.values())
		{
			if (effect.containsEffectId(effectId))
			{
				effect.endEffect();
			}
		}
	}
	
	/**
	 * Calculates the total count of effects that can be removed based on a specific dispel level.<br>
	 * This method filters out permanent, sanctuary, or high-level target slot effects.<br>
	 * It only counts effects where the required dispel level is less than or equal to {@code dispelLevel}.
	 * @param dispelLevel The level of the dispel skill being used.
	 * @return The total number of removable effects.
	 */
	public int calculateNumberOfEffects(int dispelLevel)
	{
		int number = 0;
		
		for (Effect effect : abnormalEffectMap.values())
		{
			final DispelCategoryType dispelCat = effect.getDispelCategory();
			final SkillTargetSlot tragetSlot = effect.getSkillTemplate().getTargetSlot();
			
			// Effects with a duration of 86,400,000 cannot be dispelled; TODO recheck.
			if (((effect.getDuration() >= 86400000) && !removebleEffect(effect)) || effect.isSanctuaryEffect())
			{
				continue;
			}
			
			// check for targetslot, effects with target slot higher or equal to 2 cant be removed (ex. skillId: 11885)
			if (((tragetSlot != SkillTargetSlot.BUFF) && ((tragetSlot != SkillTargetSlot.DEBUFF) && (dispelCat != DispelCategoryType.ALL))) || (effect.getTargetSlotLevel() >= 2))
			{
				continue;
			}
			
			switch (dispelCat)
			{
				case ALL:
				case BUFF: // DispelBuffCounterAtkEffect
					if (effect.getReqDispelLevel() <= dispelLevel)
					{
						number++;
					}
					break;
				default:
					break;
			}
		}
		
		return number;
	}
	
	/**
	 * Removes abnormal effects based on specific dispel categories and requirements.<br>
	 * This method checks the effect type, level, and target slot before removal.<br>
	 * It handles logic for item-triggered dispels and power requirements.
	 * @param dispelCat The category of the dispel action to perform.
	 * @param targetSlot The specific slot where the effect is located.
	 * @param count The number of effects to remove.
	 * @param dispelLevel The level required to perform the dispel.
	 * @param power The amount of power used for the dispel action.
	 * @param itemTriggered Whether the dispel was triggered by an item.
	 */
	public void removeEffectByDispelCat(DispelCategoryType dispelCat, SkillTargetSlot targetSlot, int count, int dispelLevel, int power, boolean itemTriggered)
	{
		for (Effect effect : abnormalEffectMap.values())
		{
			if (count == 0)
			{
				break;
			}
			
			// Effects with a duration of 86,400,000 cannot be dispelled; TODO recheck.
			if (((effect.getDuration() >= 86400000) && !removebleEffect(effect)) || effect.isSanctuaryEffect())
			{
				continue;
			}
			
			// If dispel is triggered by an item (ex. Healing Potion)
			// Because the debuff is unpottable, do not perform a dispel check for target slots where the effect level is greater than or equal to 2 (e.g., skillId: 11885).
			if (((effect.getSkillTemplate().isUndispellableByPotions()) && itemTriggered) || (effect.getTargetSlot() != targetSlot.ordinal()) || (effect.getTargetSlotLevel() >= 2))
			{
				continue;
			}
			
			boolean remove = false;
			switch (dispelCat)
			{
				case ALL: // DispelDebuffEffect
					if (((effect.getDispelCategory() == DispelCategoryType.ALL) || (effect.getDispelCategory() == DispelCategoryType.DEBUFF_MENTAL) || (effect.getDispelCategory() == DispelCategoryType.DEBUFF_PHYSICAL)) && (effect.getReqDispelLevel() <= dispelLevel))
					{
						remove = true;
					}
					break;
				case DEBUFF_MENTAL: // DispelDebuffMentalEffect
					if (((effect.getDispelCategory() == DispelCategoryType.ALL) || (effect.getDispelCategory() == DispelCategoryType.DEBUFF_MENTAL)) && (effect.getReqDispelLevel() <= dispelLevel))
					{
						remove = true;
					}
					break;
				case DEBUFF_PHYSICAL: // DispelDebuffPhysicalEffect
					if (((effect.getDispelCategory() == DispelCategoryType.ALL) || (effect.getDispelCategory() == DispelCategoryType.DEBUFF_PHYSICAL)) && (effect.getReqDispelLevel() <= dispelLevel))
					{
						remove = true;
					}
					break;
				case BUFF: // DispelBuffEffect or DispelBuffCounterAtkEffect
					if ((effect.getDispelCategory() == DispelCategoryType.BUFF) && (effect.getReqDispelLevel() <= dispelLevel))
					{
						remove = true;
					}
					break;
				case STUN:
					if (effect.getDispelCategory() == DispelCategoryType.STUN)
					{
						remove = true;
					}
					break;
				case NPC_BUFF: // DispelNpcBuff
					if (effect.getDispelCategory() == DispelCategoryType.NPC_BUFF)
					{
						remove = true;
					}
					break;
				case NPC_DEBUFF_PHYSICAL: // DispelNpcDebuff
					if (effect.getDispelCategory() == DispelCategoryType.NPC_DEBUFF_PHYSICAL)
					{
						remove = true;
					}
					break;
				default:
					break;
			}
			
			if (remove)
			{
				if (removePower(effect, power))
				{
					effect.endEffect();
					abnormalEffectMap.remove(effect.getStack());
				}
				else if (owner instanceof Player)
				{
					PacketSendUtility.sendPacket((Player) owner, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_DISPELCOUNT);
				}
				
				count--;
			}
			else if (owner instanceof Player)
			{
				PacketSendUtility.sendPacket((Player) owner, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_DISPELLEVEL);
			}
		}
	}
	
	/**
	 * Removes abnormal effects from the owner based on dispel rules.<br>
	 * This method checks for duration, sanctuary status, and target slots.<br>
	 * It verifies if the {@code dispelLevel} and {@code power} are sufficient to remove an effect.
	 * @param count The number of effects to attempt to remove.
	 * @param dispelLevel The required level needed to dispel the effect.
	 * @param power The amount of power used to perform the dispel action.
	 */
	public void dispelBuffCounterAtkEffect(int count, int dispelLevel, int power)
	{
		for (Effect effect : abnormalEffectMap.values())
		{
			final DispelCategoryType dispelCat = effect.getDispelCategory();
			final SkillTargetSlot tragetSlot = effect.getSkillTemplate().getTargetSlot();
			if (count == 0)
			{
				break;
			}
			
			if (((effect.getDuration() >= 86400000) && !removebleEffect(effect)) || effect.isSanctuaryEffect())
			{
				continue;
			}
			
			if (((tragetSlot != SkillTargetSlot.BUFF) && ((tragetSlot != SkillTargetSlot.DEBUFF) && (dispelCat != DispelCategoryType.ALL))) || (effect.getTargetSlotLevel() >= 2))
			{
				continue;
			}
			
			boolean remove = false;
			switch (dispelCat)
			{
				case ALL:
				case BUFF:
					if (effect.getReqDispelLevel() <= dispelLevel)
					{
						remove = true;
					}
					break;
				default:
					break;
			}
			
			if (remove)
			{
				if (removePower(effect, power))
				{
					effect.endEffect();
					abnormalEffectMap.remove(effect.getStack());
				}
				else if (owner instanceof Player)
				{
					PacketSendUtility.sendPacket((Player) owner, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_DISPELCOUNT);
				}
				
				count--;
			}
			else if (owner instanceof Player)
			{
				PacketSendUtility.sendPacket((Player) owner, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_DISPELLEVEL);
			}
		}
	}
	
	/**
	 * Checks if a specific {@code Effect} can be removed based on its skill ID.<br>
	 * This method returns {@code true} for certain hardcoded skill IDs.<br>
	 * It returns {@code false} for all other cases.
	 * @param effect The {@code Effect} object to check.
	 * @return {@code true} if the effect is removable, otherwise {@code false}.
	 */
	private boolean removebleEffect(Effect effect)
	{
		final int skillId = effect.getSkillId();
		switch (skillId)
		{
			case 20941:
			case 20942:
			case 19370:
			case 19371:
			case 19372:
			case 20530:
			case 20531:
			case 19345:
			case 19346:
				// TODO
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Removes all abnormal effects that match a specific type.<br>
	 * This method iterates through the {@code abnormalEffectMap}.<br>
	 * It checks each effect's success template for the matching {@code EffectType}.<br>
	 * If a match is found, it calls {@code endEffect} on that effect.
	 * @param effectType The type of effect to be removed.
	 */
	public void removeEffectByEffectType(EffectType effectType)
	{
		for (Effect effect : abnormalEffectMap.values())
		{
			for (EffectTemplate et : effect.getSuccessEffect())
			{
				if (effectType == et.getEffectType())
				{
					effect.endEffect();
				}
			}
		}
	}
	
	/**
	 * Reduces the power level of a specific {@code Effect}.<br>
	 * Returns {@code true} if the effect is removed.<br>
	 * Returns {@code false} if the effect still remains.
	 * @param effect The {@code Effect} object to modify.
	 * @param power The amount of power to subtract from the effect.
	 * @return A boolean indicating if the effect was fully removed.
	 */
	private boolean removePower(Effect effect, int power)
	{
		final int effectPower = effect.removePower(power);
		
		if (effectPower <= 0)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes a specific passive effect from the owner.<br>
	 * This method iterates through all active passive effects.<br>
	 * It calls {@code endEffect} on any effect matching the provided ID.
	 * @param skillid The unique identifier of the skill to remove.
	 */
	public void removePassiveEffect(int skillid)
	{
		for (Effect effect : passiveEffectMap.values())
		{
			if (effect.getSkillId() == skillid)
			{
				effect.endEffect();
			}
		}
	}
	
	/**
	 * Removes a specific "no-show" effect from the owner.<br>
	 * This method iterates through all {@code noshowEffects}.<br>
	 * It calls {@code endEffect} on any effect matching the provided ID.
	 * @param skillid The unique identifier of the skill to remove.
	 */
	public void removeNoshowEffect(int skillid)
	{
		for (Effect effect : noshowEffects.values())
		{
			if (effect.getSkillId() == skillid)
			{
				effect.endEffect();
			}
		}
	}
	
	/**
	 * Removes all abnormal effects from a specific target slot.<br>
	 * This method iterates through the {@code abnormalEffectMap}.<br>
	 * It calls {@code endEffect} on any effect matching the provided {@code targetSlot}.
	 * @param targetSlot The {@link SkillTargetSlot} to clear effects from.
	 */
	public void removeAbnormalEffectsByTargetSlot(SkillTargetSlot targetSlot)
	{
		for (Effect effect : abnormalEffectMap.values())
		{
			if (effect.getTargetSlot() == targetSlot.ordinal())
			{
				effect.endEffect();
			}
		}
	}
	
	/**
	 * Removes all active effects from the owner.<br>
	 * This method clears every effect stored in the internal maps.<br>
	 * It is used to reset the state of a {@link Creature}.
	 */
	public void removeAllEffects()
	{
		this.removeAllEffects(false);
	}
	
	/**
	 * Removes active effects from the owner based on the logout status.<br>
	 * If {@code logout} is {@code false}, it removes non-persistent abnormal and hidden effects.<br>
	 * If {@code logout} is {@code true}, it clears all types of effects including passive ones.
	 * @param logout A boolean indicating if the character is logging out.
	 */
	public void removeAllEffects(boolean logout)
	{
		if (!logout)
		{
			final Iterator<Map.Entry<String, Effect>> it = abnormalEffectMap.entrySet().iterator();
			while (it.hasNext())
			{
				final Map.Entry<String, Effect> entry = it.next();
				
				// TODO recheck - kecimis
				if (!entry.getValue().getSkillTemplate().isNoRemoveAtDie() && !entry.getValue().isXpBoost() && !entry.getValue().isApBoost() && !entry.getValue().isDrBoost() && !entry.getValue().isBdrBoost() && !entry.getValue().isEnchantBoost() && !entry.getValue().isIdunDropBoost() && !entry.getValue().isAuthorizeBoost() && !entry.getValue().isSprintFpReduce() && !entry.getValue().isReturnCoolReduce() && !entry.getValue().isEnchantOptionBoost() && !entry.getValue().isDeathPenaltyReduce() && !entry.getValue().isOdellaRecoverIncrease())
				{
					entry.getValue().endEffect();
					it.remove();
				}
			}
			
			for (Effect effect : noshowEffects.values())
			{
				effect.endEffect();
			}
			
			noshowEffects.clear();
		}
		else
		{
			// remove all effects on logout
			for (Effect effect : abnormalEffectMap.values())
			{
				effect.endEffect();
			}
			
			abnormalEffectMap.clear();
			for (Effect effect : noshowEffects.values())
			{
				effect.endEffect();
			}
			
			noshowEffects.clear();
			for (Effect effect : passiveEffectMap.values())
			{
				effect.endEffect();
			}
			
			passiveEffectMap.clear();
		}
	}
	
	/**
	 * Checks if a specific abnormal effect is currently active.<br>
	 * This method iterates through all effects in the {@code abnormalEffectMap}.<br>
	 * It returns {@code true} if an effect with the matching {@code skillId} exists.
	 * @param skillId The unique identifier of the skill to check.
	 * @return {@code true} if the abnormal effect is present, otherwise {@code false}.
	 */
	public boolean isAbnormalPresentBySkillId(int skillId)
	{
		for (Effect effect : abnormalEffectMap.values())
		{
			if (effect.getSkillId() == skillId)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific skill has a "no-show" effect active.<br>
	 * This method iterates through the {@code noshowEffects} map to find a match.
	 * @param skillId The unique identifier of the skill to check.
	 * @return {@code true} if the skill is present in the no-show list, otherwise {@code false}.
	 */
	public boolean isNoshowPresentBySkillId(int skillId)
	{
		for (Effect effect : noshowEffects.values())
		{
			if (effect.getSkillId() == skillId)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific passive effect is currently active.<br>
	 * It searches through the {@code passiveEffectMap} for the given skill ID.
	 * @param skillId The unique identifier of the skill to check.
	 * @return {@code true} if the passive effect exists, otherwise {@code false}.
	 */
	public boolean isPassivePresentBySkillId(int skillId)
	{
		for (Effect effect : passiveEffectMap.values())
		{
			if (effect.getSkillId() == skillId)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the creature is currently affected by a fear state.<br>
	 * This method uses {@code hasAbnormalEffect} logic to verify the status.
	 * @return {@code true} if the creature is under fear, {@code false} otherwise.
	 */
	public boolean isUnderFear()
	{
		return isAbnormalSet(AbnormalState.FEAR);
	}
	
	/**
	 * Updates the visual icons for all active effects on the player.<br>
	 * This method ensures that the client displays the correct status symbols.<br>
	 * It synchronizes the current state of {@code passiveEffectMap}, {@code noshowEffects}, and {@code abnormalEffectMap}.
	 */
	public void updatePlayerEffectIcons()
	{
	}
	
	/**
	 * Updates the visual icons for player effects.<br>
	 * This method refreshes the displayed status icons on the {@link Player}.
	 */
	public void updatePlayerEffectIconsImpl()
	{
	}
	
	/**
	 * Retrieves all current abnormal effects for the owner.<br>
	 * This method filters out any {@code null} values from the internal collection.
	 * @return A {@code List} of {@link Effect} objects.
	 */
	public List<Effect> getAbnormalEffects()
	{
		final List<Effect> effects = new ArrayList<>();
		final Iterator<Effect> iterator = iterator();
		while (iterator.hasNext())
		{
			final Effect effect = iterator.next();
			if (effect != null)
			{
				effects.add(effect);
			}
		}
		
		return effects;
	}
	
	/**
	 * Retrieves a list of abnormal effects that should be visible to players.<br>
	 * This method filters out any {@link Effect} where the target slot is set to {@code NOSHOW}.
	 * @return A {@code Collection} of {@link Effect} objects that are eligible to be displayed.
	 */
	public Collection<Effect> getAbnormalEffectsToShow()
	{
		return abnormalEffectMap.values().stream().filter(effect -> effect.getSkillTemplate().getTargetSlot() != SkillTargetSlot.NOSHOW).collect(Collectors.toList());
	}
	
	/**
	 * Retrieves all active chant effects from the {@code abnormalEffectMap}.<br>
	 * This method filters the collection to include only those where {@code isChant} is true.
	 * @return A {@code Collection} of {@link Effect} objects that are classified as chants.
	 */
	public Collection<Effect> getChantEffects()
	{
		return abnormalEffectMap.values().stream().filter(effect -> effect.isChant()).collect(Collectors.toList());
	}
	
	/**
	 * Retrieves all active Ranger Eye effects.<br>
	 * This method filters the {@code abnormalEffectMap} to find specific effects.
	 * @return A {@code Collection} of {@link Effect} objects that are identified as Ranger Eyes.
	 */
	public Collection<Effect> getRangerEyes()
	{
		return abnormalEffectMap.values().stream().filter(effect -> effect.isRangerEye()).collect(Collectors.toList());
	}
	
	/**
	 * Retrieves all active buff effects from the abnormal state map.<br>
	 * This method filters the {@code abnormalEffectMap} to include only those that are buffs.
	 * @return A {@code Collection} of {@link Effect} objects that are classified as buffs.
	 */
	public Collection<Effect> getBuffEffects()
	{
		return abnormalEffectMap.values().stream().filter(effect -> effect.isBuff()).collect(Collectors.toList());
	}
	
	/**
	 * Sets the abnormal state for the owner.<br>
	 * This method updates the {@code abnormalities} bitmask.<br>
	 * It also notifies observers of the new state using {@link AbnormalState}.
	 * @param mask The bitmask representing the abnormal state to apply.
	 */
	public void setAbnormal(int mask)
	{
		owner.getObserveController().notifyAbnormalSettedObservers(AbnormalState.getStateById(mask));
		abnormals |= mask;
	}
	
	/**
	 * Removes abnormal effects based on the provided bitmask.<br>
	 * This method checks all effects in the {@code abnormalEffectMap}.<br>
	 * If only one or zero effects match the {@code mask}, the corresponding bits are cleared from {@code abnormalities}.
	 * @param mask The bitmask used to identify which abnormal effects to remove.
	 */
	public void unsetAbnormal(int mask)
	{
		int count = 0;
		for (Effect effect : abnormalEffectMap.values())
		{
			if ((effect.getAbnormals() & mask) == mask)
			{
				count++;
			}
		}
		
		if (count <= 1)
		{
			abnormals &= ~mask;
		}
	}
	
	/**
	 * Checks if a specific abnormal state is currently active.<br>
	 * This method compares the bitmask of current abnormalities against the provided {@code AbnormalState}.
	 * @param id The {@link AbnormalState} to check for.
	 * @return {@code true} if the state is active, otherwise {@code false}.
	 */
	public boolean isAbnormalSet(AbnormalState id)
	{
		return (abnormals & id.getId()) == id.getId();
	}
	
	/**
	 * Checks if a specific {@link AbnormalState} is currently active.<br>
	 * It compares the provided {@code id} against the current bitmask of states.
	 * @param id The {@code AbnormalState} to check.
	 * @return {@code true} if the state is active, otherwise {@code false}.
	 */
	public boolean isAbnormalState(AbnormalState id)
	{
		final int state = abnormals & id.getId();
		return (state > 0) && (state <= id.getId());
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
	 * Returns an {@link Iterator} for all active abnormal effects.<br>
	 * This allows you to loop through the {@code abnormalEffectMap}.
	 * @return An {@link Iterator} containing all {@link Effect} objects in the abnormal map.
	 */
	public Iterator<Effect> iterator()
	{
		return abnormalEffectMap.values().iterator();
	}
	
	/**
	 * Retrieves the current transformation type of the creature.<br>
	 * It checks for deity avatar status first.<br>
	 * If no specific transform is found, it returns {@code TransformType.NONE}.
	 * @return The {@link TransformType} associated with the active effect.
	 */
	public TransformType getTransformType()
	{
		for (Effect eff : getAbnormalEffects())
		{
			if (eff.isDeityAvatar())
			{
				return TransformType.AVATAR;
			}
			
			return eff.getTransformType();
		}
		
		return TransformType.NONE;
	}
	
	/**
	 * Checks if there are any abnormal effects currently active.<br>
	 * Returns {@code true} if the {@code abnormalEffectMap} is empty.<br>
	 * Returns {@code false} if there is at least one abnormal effect.
	 * @return a boolean value indicating whether the map of abnormal effects is empty
	 */
	public boolean isEmpty()
	{
		return abnormalEffectMap.isEmpty();
	}
	
	/**
	 * Checks the cooldown ID of an {@code Effect} to manage active effects.<br>
	 * This method handles specific logic for limiting concurrent effects based on their {@code CooldownId}.<br>
	 * It identifies and ends redundant effects if they exceed a predefined size limit.
	 * @param effect The {@code Effect} object to be checked.
	 */
	public void checkEffectCooldownId(Effect effect)
	{
		final Collection<Effect> effects = getAbnormalEffectsToShow();
		final int delayId = effect.getSkillTemplate().getCooldownId();
		int rDelay = 0;
		int size = 0;
		if (delayId == 1)
		{
			return;
		}
		
		switch (delayId)
		{
			case 2005:
			case 2022:
			case 2024:
			case 2026:
			case 2028:
				size = 2;
				break;
			// TODO
		}
		
		rDelay = delayId;
		
		if ((delayId == rDelay) && (effects.size() >= size))
		{
			int i = 0;
			Effect toRemove = null;
			final Iterator<Effect> iter2 = effects.iterator();
			while (iter2.hasNext())
			{
				final Effect nextEffect = iter2.next();
				if ((nextEffect.getSkillTemplate().getCooldownId() == rDelay) && (nextEffect.getTargetSlot() == effect.getTargetSlot()))
				{
					i++;
					if (toRemove == null)
					{
						toRemove = nextEffect;
					}
				}
			}
			
			if ((i >= size) && (toRemove != null))
			{
				toRemove.endEffect();
			}
		}
	}
	
	/**
	 * Checks if an {@code Effect} should replace an existing one.<br>
	 * This happens when both effects belong to the {@code EXTRA} dispel category.<br>
	 * If a replacement occurs, the old effect is ended immediately.
	 * @param effect The new {@code Effect} to check.
	 * @return {@code true} if the old effect was replaced, otherwise {@code false}.
	 */
	private boolean checkExtraEffect(Effect effect)
	{
		final Effect existingEffect = getMapForEffect(effect).get(effect.getStack());
		if (existingEffect != null)
		{
			if ((existingEffect.getDispelCategory() == DispelCategoryType.EXTRA) && (effect.getDispelCategory() == DispelCategoryType.EXTRA))
			{
				existingEffect.endEffect();
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the {@code nextEffect} conflicts with any existing effects.<br>
	 * It compares skill sub-types and target slots to identify overlapping effects.<br>
	 * If a conflict is found, it determines if the new effect should be replaced.
	 * @param nextEffect The {@code Effect} object to check for conflicts.
	 * @return {@code true} if a conflict exists and the new effect is superior; {@code false} otherwise.
	 */
	private boolean searchConflict(Effect nextEffect)
	{
		if (priorityStigmaEffect(nextEffect) || checkExtraEffect(nextEffect))
		{
			return false;
		}
		
		for (Effect effect : abnormalEffectMap.values())
		{
			if (effect.getSkillSubType().equals(nextEffect.getSkillSubType()) || effect.getTargetSlotEnum().equals(nextEffect.getTargetSlotEnum()))
			{
				for (EffectTemplate et : effect.getEffectTemplates())
				{
					if (et.getEffectid() == 0)
					{
						continue;
					}
					
					for (EffectTemplate et2 : nextEffect.getEffectTemplates())
					{
						if (et2.getEffectid() == 0)
						{
							continue;
						}
						
						if (et.getEffectid() == et2.getEffectid())
						{
							if (et.getBasicLvl() > et2.getBasicLvl())
							{
								if (nextEffect.getTargetSlotEnum() != SkillTargetSlot.DEBUFF)
								{
									nextEffect.setEffectResult(EffectResult.CONFLICT);
								}
								
								return true;
							}
							
							effect.endEffect();
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the new effect should replace an existing stigma.<br>
	 * It compares the stigma type ID and target slot details.<br>
	 * If a matching effect ID is found on a lower priority stigma, it ends that effect.
	 * @param nextEffect The {@code Effect} being added to the creature.
	 * @return {@code true} if an existing effect was ended, {@code false} otherwise.
	 */
	private boolean priorityStigmaEffect(Effect nextEffect)
	{
		for (Effect effect : abnormalEffectMap.values())
		{
			if ((effect.getSkillTemplate().getStigmaType().getId() < nextEffect.getSkillTemplate().getStigmaType().getId()) && (effect.getTargetSlot() == nextEffect.getTargetSlot()) && (effect.getTargetSlotLevel() == nextEffect.getTargetSlotLevel()))
			{
				for (EffectTemplate et : effect.getEffectTemplates())
				{
					if (et.getEffectid() == 0)
					{
						continue;
					}
					
					for (EffectTemplate et2 : nextEffect.getEffectTemplates())
					{
						if (et2.getEffectid() == 0)
						{
							continue;
						}
						
						if (et.getEffectid() == et2.getEffectid())
						{
							effect.endEffect();
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the creature has any physical state effects.<br>
	 * This method iterates through all active abnormal effects.<br>
	 * It returns {@code true} if at least one effect is a physical state.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if a physical state effect exists, {@code false} otherwise.
	 */
	public boolean hasPhysicalStateEffect()
	{
		final Iterator<Effect> effectIterator = abnormalEffectMap.values().iterator();
		while (effectIterator.hasNext())
		{
			final Effect localEffect = effectIterator.next();
			if (localEffect.isPhysicalState())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the creature has any active magical state effects.<br>
	 * This method iterates through all effects in the {@code abnormalEffectMap}.<br>
	 * It returns {@code true} if at least one effect is a magical state.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if a magical state effect exists, {@code false} otherwise.
	 */
	public boolean hasMagicalStateEffect()
	{
		final Iterator<Effect> effectIterator = abnormalEffectMap.values().iterator();
		while (effectIterator.hasNext())
		{
			final Effect localEffect = effectIterator.next();
			if (localEffect.isMagicalState())
			{
				return true;
			}
		}
		
		return false;
	}
}
