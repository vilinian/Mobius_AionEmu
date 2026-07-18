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
package com.aionemu.gameserver.controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.ShoutEventHandler;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Homing;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_CANCEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_UPDATE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.ChargeSkill;
import com.aionemu.gameserver.skillengine.model.HealType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.Skill.SkillMethod;
import com.aionemu.gameserver.taskmanager.tasks.MovementNotifyTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneUpdateService;

/**
 * This class handles the core logic for controlling {@link Creature} entities, including both NPCs and players.<br>
 * It manages behaviors such as movement, combat actions, and state updates for all creature types.
 * @author -Nemesiss-, ATracer(2009-09-29), Sarynth
 * @author GiGatR00n v4.7.5.x
 * @param <T>
 * @modified by Wakizashi
 */
public abstract class CreatureController<T extends Creature>extends VisibleObjectController<Creature>
{
	private static final Logger log = LoggerFactory.getLogger(CreatureController.class);
	private final Map<Integer, Future<?>> tasks = new ConcurrentHashMap<>();
	private int SimpleAttackType; // 0=Ground Attacks | 1=Air Attacks (v4.7.5.17)
	
	/**
	 * Updates the visibility status of a specific object.<br>
	 * This method also clears the target if the object is the current target.
	 * @param object The {@code VisibleObject} that is no longer seen.
	 * @param isOutOfRange Whether the object is outside of the visible range.
	 */
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange)
	{
		super.notSee(object, isOutOfRange);
		if (object == getOwner().getTarget())
		{
			getOwner().setTarget(null);
		}
	}
	
	/**
	 * This method is called when the entity begins to move.<br>
	 * It notifies all observers of the movement.<br>
	 * It also triggers the {@code notifyAIOnMove} logic.
	 */
	public void onStartMove()
	{
		getOwner().getObserveController().notifyMoveObservers();
		notifyAIOnMove();
	}
	
	/**
	 * This method is called when the creature starts moving.<br>
	 * It notifies the {@link AI2} system and updates the current zone.
	 */
	public void onMove()
	{
		notifyAIOnMove();
		updateZone();
	}
	
	/**
	 * This method is called when a creature stops moving.<br>
	 * It triggers the {@code notifyAIOnMove} method to update the AI state.
	 */
	public void onStopMove()
	{
		notifyAIOnMove();
	}
	
	/**
	 * Triggers the logic for an entity returning to its home location.<br>
	 * This method is called when a creature decides to head back to its spawn point.
	 */
	public void onReturnHome()
	{
	}
	
	/**
	 * Notifies the AI system that a movement action has occurred.<br>
	 * This method adds the owner to the {@link MovementNotifyTask} queue.<br>
	 * It ensures the AI stays updated on position changes.
	 */
	protected void notifyAIOnMove()
	{
		MovementNotifyTask.getInstance().add(getOwner());
	}
	
	/**
	 * Updates the zone information for the owner of this object.<br>
	 * This method calls {@code revalidateZones} on the owner to refresh data.
	 */
	public void refreshZoneImpl()
	{
		getOwner().revalidateZones();
	}
	
	/**
	 * Registers the owner of this object with the {@code ZoneUpdateService}.<br>
	 * This ensures that zone updates are applied to the correct entity.
	 */
	public void updateZone()
	{
		ZoneUpdateService.getInstance().add(getOwner());
	}
	
	/**
	 * This method is called when an entity enters a new zone.<br>
	 * It handles the logic required for transitioning into a {@code ZoneInstance}.
	 * @param zoneInstance The instance of the zone that was entered.
	 */
	public void onEnterZone(ZoneInstance zoneInstance)
	{
	}
	
	/**
	 * This method is called when an entity leaves a specific zone.<br>
	 * It handles the logic required for transitioning out of a {@code ZoneInstance}.
	 * @param zoneInstance The {@code ZoneInstance} that the entity is leaving.
	 */
	public void onLeaveZone(ZoneInstance zoneInstance)
	{
	}
	
	/**
	 * Handles the logic when a creature dies.<br>
	 * This method stops movement and clears active effects.<br>
	 * It updates the {@link CreatureState} based on whether the owner is a {@link Player} or an {@link Npc}.<br>
	 * Finally, it notifies all death observers about the {@code lastAttacker}.
	 * @param lastAttacker The creature that dealt the final blow.
	 */
	public void onDie(Creature lastAttacker)
	{
		getOwner().getMoveController().abortMove();
		getOwner().setCasting(null);
		getOwner().getEffectController().removeAllEffects();
		
		// exception for player
		if (getOwner() instanceof Player)
		{
			if (((Player) getOwner()).getIsFlyingBeforeDeath())
			{
				getOwner().unsetState(CreatureState.ACTIVE);
				getOwner().setState(CreatureState.FLOATING_CORPSE);
			}
			else
			{
				getOwner().setState(CreatureState.DEAD);
			}
		}
		else
		{
			if (getOwner() instanceof Npc)
			{
				if (((Npc) getOwner()).getObjectTemplate().isFloatCorpse())
				{
					getOwner().setState(CreatureState.FLOATING_CORPSE);
				}
			}
			
			getOwner().setState(CreatureState.DEAD);
		}
		
		getOwner().getObserveController().notifyDeathObservers(lastAttacker);
	}
	
	/**
	 * Handles the logic when a creature dies while under a silence effect.<br>
	 * This method stops all movement and cancels active casting for the owner.<br>
	 * It removes all current effects and updates the {@code CreatureState}.<br>
	 * The state is set to {@code DEAD} or {@code FLOATING_CORPSE} based on the creature type.
	 */
	public void onDieSilence()
	{
		getOwner().getMoveController().abortMove();
		getOwner().setCasting(null);
		getOwner().getEffectController().removeAllEffects();
		
		// exception for player
		if (getOwner() instanceof Player)
		{
			if (((Player) getOwner()).getIsFlyingBeforeDeath())
			{
				getOwner().unsetState(CreatureState.ACTIVE);
				getOwner().setState(CreatureState.FLOATING_CORPSE);
			}
			else
			{
				getOwner().setState(CreatureState.DEAD);
			}
		}
		else
		{
			if (getOwner() instanceof Npc)
			{
				if (((Npc) getOwner()).getObjectTemplate().isFloatCorpse())
				{
					getOwner().setState(CreatureState.FLOATING_CORPSE);
				}
			}
			
			getOwner().setState(CreatureState.DEAD);
		}
	}
	
	/**
	 * Handles the logic when this creature is attacked by another entity.<br>
	 * It calculates skill cancellations based on damage and concentration stats.<br>
	 * This method updates health, aggro lists, and triggers AI shout events.<br>
	 * It also manages target selection for players who are attacked.
	 * @param attacker The {@link Creature} performing the attack.
	 * @param skillId The unique identifier of the skill used.
	 * @param type The {@link TYPE} of the attack.
	 * @param damage The amount of damage dealt to this creature.
	 * @param notifyAttack A boolean indicating if observers should be notified.
	 * @param log The {@link LOG} type associated with the attack.
	 */
	public void onAttack(Creature attacker, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log)
	{
		if ((damage != 0) && !((getOwner() instanceof Npc) && ((Npc) getOwner()).isBoss()))
		{
			final Skill skill = getOwner().getCastingSkill();
			if ((skill != null) && (log != LOG.BLEED) && (log != LOG.SPELLATK) && (log != LOG.POISON))
			{
				if (skill.getSkillMethod() == SkillMethod.ITEM)
				{
					cancelCurrentSkill();
				}
				else
				{
					final int cancelRate = skill.getSkillTemplate().getCancelRate();
					if (cancelRate == 100000)
					{
						cancelCurrentSkill();
					}
					else if (cancelRate > 0)
					{
						final int conc = getOwner().getGameStats().getStat(StatEnum.CONCENTRATION, 0).getCurrent();
						final float maxHp = getOwner().getGameStats().getMaxHp().getCurrent();
						
						final float cancel = ((7f * (damage / maxHp) * 100f) - (conc / 2f)) * (cancelRate / 100f);
						if (Rnd.get(100) < cancel)
						{
							cancelCurrentSkill();
						}
					}
				}
			}
		}
		
		// Do NOT notify attacked observers if the damage is 0 and shield is up (means the attack has been absorbed)
		if ((damage == 0) && getOwner().getEffectController().isUnderShield())
		{
			notifyAttack = false;
		}
		
		if (notifyAttack)
		{
			getOwner().getObserveController().notifyAttackedObservers(attacker);
		}
		
		// Reduce the damage to exactly what is required to ensure death.
		// Do not include 7k worth of damage when the creature only has 100 hp remaining for the AggroList damage count.
		if (damage > getOwner().getLifeStats().getCurrentHp())
		{
			damage = getOwner().getLifeStats().getCurrentHp() + 1;
		}
		
		getOwner().getAggroList().addDamage(attacker, damage);
		getOwner().getLifeStats().reduceHp(damage, attacker);
		
		if (getOwner() instanceof Npc)
		{
			final AI2 ai = getOwner().getAi2();
			if (ai.poll(AIQuestion.CAN_SHOUT))
			{
				if (attacker instanceof Player)
				{
					ShoutEventHandler.onHelp((NpcAI2) ai, attacker);
				}
				else
				{
					ShoutEventHandler.onEnemyAttack((NpcAI2) ai, attacker);
				}
			}
		}
		else if ((getOwner() instanceof Player) && (attacker instanceof Npc))
		{
			final AI2 ai = attacker.getAi2();
			if (ai.poll(AIQuestion.CAN_SHOUT))
			{
				ShoutEventHandler.onAttack((NpcAI2) ai, getOwner());
			}
		}
		
		getOwner().incrementAttackedCount();
		
		// notify all NPC's around that creature is attacking me
		getOwner().getKnownList().doOnAllNpcs(object -> object.getAi2().onCreatureEvent(AIEventType.CREATURE_NEEDS_SUPPORT, getOwner()));
		
		// If the attacked player does not target the attacker, add it to the attacker.
		if ((getOwner() instanceof Player) && (attacker instanceof Player))
		{
			if ((getOwner().getTarget() == null) || ((getOwner().getTarget() instanceof Player) && !((Player) getOwner().getTarget()).isEnemy(getOwner())))
			{
				final Player player = (Player) getOwner();
				player.setTarget(attacker);
				PacketSendUtility.sendPacket(player, new SM_TARGET_SELECTED(player));
				PacketSendUtility.broadcastPacket(player, new SM_TARGET_UPDATE(player));
			}
		}
	}
	
	/**
	 * Handles the logic when this creature is attacked.<br>
	 * It processes the incoming damage and updates the state.<br>
	 * This method calls {@code int, TYPE, int, boolean, LOG)} with default values.
	 * @param creature The creature performing the attack.
	 * @param skillId The unique identifier for the skill used.
	 * @param damage The amount of damage dealt to this creature.
	 * @param notifyAttack Whether to send a notification packet to the client.
	 */
	public void onAttack(Creature creature, int skillId, int damage, boolean notifyAttack)
	{
		this.onAttack(creature, skillId, TYPE.REGULAR, damage, notifyAttack, LOG.REGULAR);
	}
	
	/**
	 * Handles the logic when this entity is attacked by another creature.<br>
	 * It processes the incoming {@code damage} and updates the state of the target.<br>
	 * This method is called internally to trigger attack effects.
	 * @param creature The {@link Creature} that performed the attack.
	 * @param damage The amount of {@code damage} dealt during the attack.
	 * @param notifyAttack A boolean indicating if the attack should be sent as a network notification.
	 */
	public void onAttack(Creature creature, int damage, boolean notifyAttack)
	{
		this.onAttack(creature, 0, TYPE.REGULAR, damage, notifyAttack, LOG.REGULAR);
	}
	
	/**
	 * Handles the restoration of life stats for the owner.<br>
	 * This method updates {@code HP}, {@code MP}, or {@code FP} based on the provided type.
	 * @param hopType The type of stat to restore.
	 * @param value The amount of points to add to the stat.
	 */
	public void onRestore(HealType hopType, int value)
	{
		switch (hopType)
		{
			case HP:
				getOwner().getLifeStats().increaseHp(TYPE.HP, value);
				break;
			case MP:
				getOwner().getLifeStats().increaseMp(TYPE.MP, value);
				break;
			case FP:
				getOwner().getLifeStats().increaseFp(TYPE.FP, value);
				break;
			default:
				break;
		}
	}
	
	/**
	 * Handles the logic for dropping items or objects.<br>
	 * This method is triggered when a {@link Player} performs a drop action.
	 * @param player The {@code Player} who initiated the drop.
	 */
	public void doDrop(Player player)
	{
	}
	
	/**
	 * Processes the rewards for completing a task.<br>
	 * This method handles giving items or other benefits to the player.<br>
	 * It is called after a successful action or event.
	 */
	public void doReward()
	{
	}
	
	/**
	 * Handles the request from a {@link Player} to start a dialog.<br>
	 * This method is triggered when a player interacts with an NPC.
	 * @param player The {@code Player} object who initiated the request.
	 */
	public void onDialogRequest(Player player)
	{
	}
	
	/**
	 * Retrieves the current simple attack type.<br>
	 * This value is used to determine how an entity performs basic attacks.
	 * @return The {@code int} value representing the simple attack type.
	 */
	public int getSimpleAttackType()
	{
		return SimpleAttackType;
	}
	
	/**
	 * Sets the simple attack type for this entity.<br>
	 * This value is used to determine how the entity performs basic attacks.
	 * @param attackType The {@code int} value representing the new attack type.
	 */
	public void setSimpleAttackType(int attackType)
	{
		SimpleAttackType = attackType;
	}
	
	/**
	 * Executes an attack against a specific target.<br>
	 * This method validates the owner's state before calculating and applying damage.<br>
	 * It broadcasts the attack packet and notifies observers if the attack hits.
	 * @param target The {@code Creature} being attacked.
	 * @param attackNo The unique identifier for the current attack sequence.
	 * @param time The delay in milliseconds before the damage is applied.
	 * @param type The specific category or style of the attack.
	 */
	public void attackTarget(Creature target, int attackNo, int time, int type)
	{
		boolean addAttackObservers = true;
		/**
		 * Check all prerequisites
		 */
		if ((target == null) || !getOwner().canAttack() || getOwner().getLifeStats().isAlreadyDead() || !getOwner().isSpawned())
		{
			return;
		}
		
		/**
		 * Calculate and apply damage
		 */
		int attackType = 0;
		List<AttackResult> attackResult;
		if (getOwner() instanceof Homing)
		{
			attackResult = AttackUtil.calculateHomingAttackResult(getOwner(), target, getOwner().getAttackType().getMagicalElement());
			attackType = 1;
		}
		else
		{
			if (getOwner().getAttackType() == ItemAttackType.PHYSICAL)
			{
				attackResult = AttackUtil.calculatePhysicalAttackResult(getOwner(), target);
			}
			else
			{
				attackResult = AttackUtil.calculateMagicalAttackResult(getOwner(), target, getOwner().getAttackType().getMagicalElement());
				attackType = 1;
			}
		}
		
		int damage = 0;
		for (AttackResult result : attackResult)
		{
			if ((result.getAttackStatus() == AttackStatus.RESIST) || (result.getAttackStatus() == AttackStatus.DODGE))
			{
				addAttackObservers = false;
			}
			
			damage += result.getDamage();
		}
		
		// getOwner().getGameStats().increaseAttackCounter();
		PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_ATTACK(getOwner(), target, attackNo, time, attackType, attackResult));
		
		if (addAttackObservers)
		{
			getOwner().getObserveController().notifyAttackObservers(target);
		}
		
		final Creature creature = getOwner();
		if (time == 0)
		{
			target.getController().onAttack(getOwner(), damage, true);
		}
		else
		{
			ThreadPoolManager.getInstance().schedule(new DelayedOnAttack(target, creature, damage), time);
		}
	}
	
	/**
	 * Stops the movement of the current creature.<br>
	 * This method updates the position in the {@code World} instance.<br>
	 * It also broadcasts an {@code SM_MOVE} packet to all clients.
	 */
	public void stopMoving()
	{
		final Creature owner = getOwner();
		World.getInstance().updatePosition(owner, owner.getX(), owner.getY(), owner.getZ(), owner.getHeading());
		PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
	}
	
	/**
	 * Handles the logic when a player selects an option in a dialog.<br>
	 * This method processes rewards and quest updates based on the selection.
	 * @param dialogId The unique identifier for the current dialog.
	 * @param player The {@link Player} who interacted with the dialog.
	 * @param questId The ID of the quest associated with this interaction.
	 * @param extendedRewardIndex The index of the specific reward to grant.
	 * @param unk An unknown parameter for future use.
	 */
	public void onDialogSelect(int dialogId, Player player, int questId, int extendedRewardIndex, int unk)
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * Retrieves a specific task based on the provided {@code TaskId}.<br>
	 * This method returns a {@link Future} representing the background task.
	 * @param taskId The unique identifier for the task to retrieve.
	 * @return A {@link Future} object associated with the given {@code taskId}.
	 */
	public Future<?> getTask(TaskId taskId)
	{
		return tasks.get(taskId.ordinal());
	}
	
	/**
	 * Checks if a specific task exists in the current task list.<br>
	 * It uses the {@code ordinal()} of the {@code TaskId} to perform the check.
	 * @param taskId The unique identifier for the task to search for.
	 * @return {@code true} if the task is found, otherwise {@code false}.
	 */
	public boolean hasTask(TaskId taskId)
	{
		return tasks.containsKey(taskId.ordinal());
	}
	
	/**
	 * Checks if a specific task is currently running.<br>
	 * It returns {@code true} if the task exists and is not finished.<br>
	 * It returns {@code false} if the task is completed or does not exist.
	 * @param taskId The unique identifier for the task to check.
	 * @return {@code true} if the task is still active, otherwise {@code false}.
	 */
	public boolean hasScheduledTask(TaskId taskId)
	{
		final Future<?> task = tasks.get(taskId.ordinal());
		return task != null ? !task.isDone() : false;
	}
	
	/**
	 * Stops a specific background task using its unique identifier.<br>
	 * This method removes the task from the internal list and attempts to cancel it.<br>
	 * It does not interrupt the task if it is already running.
	 * @param taskId The {@code TaskId} of the task to be cancelled.
	 * @return A {@code Future} object representing the removed task, or {@code null} if no task was found.
	 */
	public Future<?> cancelTask(TaskId taskId)
	{
		final Future<?> task = tasks.remove(taskId.ordinal());
		if (task != null)
		{
			task.cancel(false);
		}
		
		return task;
	}
	
	/**
	 * Adds a new task to the internal task list.<br>
	 * This method replaces any existing task associated with the given {@code TaskId}.<br>
	 * It ensures that only one task is active for each unique ID.
	 * @param taskId The unique identifier for the task.
	 * @param task The {@link Future} object representing the task to be executed.
	 */
	public void addTask(TaskId taskId, Future<?> task)
	{
		cancelTask(taskId);
		tasks.put(taskId.ordinal(), task);
	}
	
	/**
	 * Stops all currently active tasks for this entity.<br>
	 * It iterates through the {@code tasks} map and cancels each {@code Future}.<br>
	 * The {@code TaskId.RESPAWN} task is specifically ignored during this process.<br>
	 * Finally, it clears all entries from the internal task collection.
	 */
	public void cancelAllTasks()
	{
		for (int i : tasks.keySet())
		{
			final Future<?> task = tasks.get(i);
			if ((task != null) && (i != TaskId.RESPAWN.ordinal()))
			{
				task.cancel(false);
			}
		}
		
		tasks.clear();
	}
	
	/**
	 * Removes this object from the game world.<br>
	 * This method cancels all active tasks before calling {@code delete}.
	 */
	@Override
	public void delete()
	{
		cancelAllTasks();
		super.delete();
	}
	
	/**
	 * Reduces the health of the owner to zero.<br>
	 * This method is used to simulate a death event for the entity.<br>
	 * It updates the {@code LifeStats} of the owner.
	 */
	public void die()
	{
		getOwner().getLifeStats().reduceHp(getOwner().getLifeStats().getCurrentHp() + 1, getOwner());
	}
	
	/**
	 * Attempts to execute a specific skill for the creature.<br>
	 * This method checks if the skill can be used and performs the action.
	 * @param skillId The unique identifier of the skill to use.
	 * @return {@code true} if the skill was successfully activated, {@code false} otherwise.
	 */
	public boolean useSkill(int skillId)
	{
		return useSkill(skillId, 1);
	}
	
	/**
	 * Attempts to execute a specific skill for the owner.<br>
	 * This method retrieves the skill from {@code SkillEngine}.<br>
	 * It returns the result of the skill execution if successful.
	 * @param skillId The unique identifier for the skill to be used.
	 * @param skillLevel The level of the skill to be executed.
	 * @return {@code true} if the skill was successfully used, or {@code false} otherwise.
	 */
	public boolean useSkill(int skillId, int skillLevel)
	{
		try
		{
			final Creature creature = getOwner();
			final Skill skill = SkillEngine.getInstance().getSkill(creature, skillId, skillLevel, creature.getTarget());
			if (skill != null)
			{
				return skill.useSkill();
			}
		}
		catch (Exception ex)
		{
			log.error("Exception during skill use: " + skillId, ex);
		}
		
		return false;
	}
	
	/**
	 * Attempts to execute a specific charge skill for the player.<br>
	 * This method retrieves the skill from {@link SkillEngine} and executes it if found.<br>
	 * It returns {@code true} if the skill was successfully used, otherwise {@code false}.
	 * @param skillId The unique identifier of the skill to use.
	 * @param skillLevel The level of the skill to execute.
	 * @return {@code true} if the skill was used successfully; {@code false} otherwise.
	 */
	public boolean useChargeSkill(int skillId, int skillLevel)
	{
		try
		{
			final Player creature = (Player) getOwner();
			final ChargeSkill skill = SkillEngine.getInstance().getChargeSkill(creature, skillId, skillLevel, creature.getTarget());
			if (skill != null)
			{
				return skill.useSkill();
			}
		}
		catch (Exception ex)
		{
			log.error("Exception during skill use: " + skillId, ex);
		}
		
		return false;
	}
	
	/**
	 * Sends a hate notification to all nearby creatures.<br>
	 * This method iterates through known objects and updates their aggro lists.
	 * @param value The amount of hate to broadcast.
	 */
	public void broadcastHate(int value)
	{
		for (VisibleObject visibleObject : getOwner().getKnownList().getKnownObjects().values())
		{
			if (visibleObject instanceof Creature)
			{
				((Creature) visibleObject).getAggroList().notifyHate(getOwner(), value);
			}
		}
	}
	
	/**
	 * Cancels the current casting action of the owner.<br>
	 * This method sets the casting skill to {@code null}.<br>
	 * It also decrements the active skill number if it is greater than {@code 0}.
	 */
	public void abortCast()
	{
		final Creature creature = getOwner();
		final Skill skill = creature.getCastingSkill();
		if (skill == null)
		{
			return;
		}
		
		creature.setCasting(null);
		if (creature.getSkillNumber() > 0)
		{
			creature.setSkillNumber(creature.getSkillNumber() - 1);
		}
	}
	
	/**
	 * Cancels the skill currently being cast by the owner.<br>
	 * This method stops the casting process and removes any active cooldowns.<br>
	 * It also updates the AI state if the owner is an {@link NpcAI2}.
	 */
	public void cancelCurrentSkill()
	{
		if (getOwner().getCastingSkill() == null)
		{
			return;
		}
		
		final Creature creature = getOwner();
		final Skill castingSkill = creature.getCastingSkill();
		castingSkill.cancelCast();
		creature.removeSkillCoolDown(castingSkill.getSkillTemplate().getCooldownId());
		creature.setCasting(null);
		PacketSendUtility.broadcastPacketAndReceive(creature, new SM_SKILL_CANCEL(creature, castingSkill.getSkillTemplate().getSkillId()));
		if (getOwner().getAi2() instanceof NpcAI2)
		{
			final NpcAI2 npcAI = (NpcAI2) getOwner().getAi2();
			npcAI.setSubStateIfNot(AISubState.NONE);
			npcAI.onGeneralEvent(AIEventType.ATTACK_COMPLETE);
			if (creature.getSkillNumber() > 0)
			{
				creature.setSkillNumber(creature.getSkillNumber() - 1);
			}
		}
	}
	
	/**
	 * Stops the current item usage action.<br>
	 * This method cancels any active effects from using an item.<br>
	 * It resets the state of the {@code Creature}.
	 */
	public void cancelUseItem()
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * Handles the logic when a creature is despawned.<br>
	 * This method cancels the {@code DECAY} task.<br>
	 * It also clears the aggro list and observation controller of the owner if they are still spawned.
	 */
	@Override
	public void onDespawn()
	{
		cancelTask(TaskId.DECAY);
		
		final Creature owner = getOwner();
		if ((owner == null) || !owner.isSpawned())
		{
			return;
		}
		
		owner.getAggroList().clear();
		owner.getObserveController().clear();
	}
	
	private static final class DelayedOnAttack implements Runnable
	{
		private Creature target;
		private Creature creature;
		private final int finalDamage;
		
		public DelayedOnAttack(Creature target, Creature creature, int finalDamage)
		{
			this.target = target;
			this.creature = creature;
			this.finalDamage = finalDamage;
		}
		
		@Override
		public void run()
		{
			target.getController().onAttack(creature, finalDamage, true);
			target = null;
			creature = null;
		}
	}
	
	/**
	 * This method is called after the object has been spawned.<br>
	 * It triggers a revalidation of zones for the owner.
	 */
	@Override
	public void onAfterSpawn()
	{
		super.onAfterSpawn();
		getOwner().revalidateZones();
	}
}
