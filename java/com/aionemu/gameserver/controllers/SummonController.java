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

import com.aionemu.gameserver.ai2.AI2;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic and network communication for {@link Summon} entities.<br>
 * This controller manages summon behaviors, updates, and interactions within the game world.
 * @author ATracer
 * @author RotO (Attack-speed hack protection) modified by Sippolo
 */
public class SummonController extends CreatureController<Summon>
{
	private long lastAttackMilis = 0;
	private final boolean isAttacked = false;
	private int releaseAfterSkill = -1;
	
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
		if (getOwner().getMaster() == null)
		{
			return;
		}
		
		if (object.getObjectId() == getOwner().getMaster().getObjectId())
		{
			SummonsService.release(getOwner(), UnsummonType.DISTANCE, isAttacked);
		}
	}
	
	/**
	 * Releases the summon from its current state.<br>
	 * This method calls {@code UnsummonType, boolean)} to handle the release logic.
	 * @param unsummonType The type of unsummon action to perform.
	 */
	public void release(UnsummonType unsummonType)
	{
		SummonsService.release(getOwner(), unsummonType, isAttacked);
	}
	
	/**
	 * Retrieves the owner of this summon.<br>
	 * This method returns the {@link Summon} object associated with the AI.
	 * @return The {@code Summon} that owns this entity.
	 */
	@Override
	public Summon getOwner()
	{
		return (Summon) super.getOwner();
	}
	
	/**
	 * Puts the siege weapon into rest mode.<br>
	 * This method cancels the {@code SUMMON_FOLLOW} task.<br>
	 * It also triggers a stop follow event for the owner.
	 */
	public void restMode()
	{
		SummonsService.restMode(getOwner());
	}
	
	/**
	 * Sets the summon to an unknown mode.<br>
	 * This method updates the status of the owner via {@link SummonsService}.
	 */
	public void setUnkMode()
	{
		SummonsService.setUnkMode(getOwner());
	}
	
	/**
	 * Switches the siege weapon into guard mode.<br>
	 * This method makes the unit protect its master.<br>
	 * It cancels existing follow tasks and updates the AI behavior.
	 */
	public void guardMode()
	{
		SummonsService.guardMode(getOwner());
	}
	
	/**
	 * Switches the summon to attack mode.<br>
	 * This method sets a specific target for the owner.<br>
	 * It triggers movement and follow behaviors toward that target.
	 * @param targetObjId The unique identifier of the {@code Creature} to attack.
	 */
	public void attackMode(int targetObjId)
	{
		final VisibleObject obj = getOwner().getKnownList().getObject(targetObjId);
		if ((obj != null) && (obj instanceof Creature))
		{
			SummonsService.attackMode(getOwner());
		}
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
	@Override
	public void attackTarget(Creature target, int attackNo, int time, int type)
	{
		final Player master = getOwner().getMaster();
		
		if (!RestrictionsManager.canAttack(master, target))
		{
			return;
		}
		
		final int attackSpeed = getOwner().getGameStats().getAttackSpeed().getCurrent();
		final long milis = System.currentTimeMillis();
		if ((milis - lastAttackMilis) < attackSpeed)
		{
			/**
			 * Hack!
			 */
			return;
		}
		
		lastAttackMilis = milis;
		super.attackTarget(target, attackNo, time, type);
	}
	
	/**
	 * Handles the logic when this entity is attacked by another creature.<br>
	 * It identifies the attacker and triggers relevant quest or packet updates.
	 * @param creature The {@code Creature} performing the attack.
	 * @param skillId The unique identifier for the skill used in the attack.
	 * @param type The {@code TYPE} of the attack performed.
	 * @param damage The amount of damage dealt during this attack.
	 * @param notifyAttack A boolean indicating if the attack should be broadcasted as a notification.
	 * @param log The {@code LOG} object used for recording the action details.
	 */
	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log)
	{
		// temp
		if (getOwner().getLifeStats().isAlreadyDead() || (getOwner().getMode() == SummonMode.RELEASE))
		{
			return;
		}
		
		super.onAttack(creature, skillId, type, damage, notifyAttack, log);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), creature, TYPE.REGULAR, 0, damage, log));
		PacketSendUtility.sendPacket(getOwner().getMaster(), new SM_SUMMON_UPDATE(getOwner()));
	}
	
	/**
	 * Handles the logic when a summon dies.<br>
	 * This method releases the summon and broadcasts a death emotion to the owner.<br>
	 * It also schedules an aggro update for the master if the last attacker was not already dead.
	 * @param lastAttacker The {@link Creature} that dealt the final blow.
	 */
	@Override
	public void onDie(Creature lastAttacker)
	{
		if (lastAttacker == null)
		{
			throw new NullPointerException("lastAttacker");
		}
		
		super.onDie(lastAttacker);
		SummonsService.release(getOwner(), UnsummonType.UNSPECIFIED, isAttacked);
		final Summon owner = getOwner();
		final Player master = getOwner().getMaster();
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.DIE, 0, lastAttacker.equals(owner) ? 0 : lastAttacker.getObjectId()));
		
		if (!master.equals(lastAttacker) && !owner.equals(lastAttacker) && !master.getLifeStats().isAlreadyDead() && !lastAttacker.getLifeStats().isAlreadyDead())
		{
			ThreadPoolManager.getInstance().schedule(() -> lastAttacker.getAggroList().addHate(master, 1), 1000);
		}
	}
	
	/**
	 * Executes a specific skill on a target creature.<br>
	 * This method checks if the owner's pet has the required skill before proceeding.<br>
	 * It handles automatic release logic if the skill matches {@code releaseAfterSkill}.
	 * @param skillId The unique identifier of the skill to use.
	 * @param target The {@link Creature} that will be affected by the skill.
	 */
	public void useSkill(int skillId, Creature target)
	{
		final Creature creature = getOwner();
		final boolean petHasSkill = DataManager.PET_SKILL_DATA.petHasSkill(getOwner().getObjectTemplate().getTemplateId(), skillId);
		if (!petHasSkill)
		{
			// hackers!)
			return;
		}
		
		final Skill skill = SkillEngine.getInstance().getSkill(creature, skillId, 1, target);
		if (skill != null)
		{
			// If skill succeeds, handle automatic release if expected
			if (skill.useSkill() && (skillId == releaseAfterSkill))
			{
				ThreadPoolManager.getInstance().schedule(() -> SummonsService.release(getOwner(), UnsummonType.UNSPECIFIED, isAttacked), 1000);
			}
			
			setReleaseAfterSkill(-1);
		}
	}
	
	/**
	 * Sets the specific skill ID that triggers a release.<br>
	 * This value is used to determine when the summon should be released.
	 * @param skillId The unique identifier for the skill.
	 */
	public void setReleaseAfterSkill(int skillId)
	{
		releaseAfterSkill = skillId;
	}
	
	/**
	 * This method is called when the entity begins to move.<br>
	 * It notifies all observers of the movement.<br>
	 * It also triggers the {@code notifyAIOnMove} logic.
	 */
	@Override
	public void onStartMove()
	{
		super.onStartMove();
		getOwner().getMoveController().setInMove(true);
		getOwner().getObserveController().notifyMoveObservers();
		PlayerMoveTaskManager.getInstance().addPlayer(getOwner());
	}
	
	/**
	 * This method is called when a creature stops moving.<br>
	 * It updates the movement state for the owner and notifies observers.<br>
	 * The {@code PlayerMoveTaskManager} removes the owner from its active list.
	 */
	@Override
	public void onStopMove()
	{
		super.onStopMove();
		PlayerMoveTaskManager.getInstance().removePlayer(getOwner());
		getOwner().getObserveController().notifyMoveObservers();
		getOwner().getMoveController().setInMove(false);
	}
	
	/**
	 * This method is called when the creature starts moving.<br>
	 * It notifies the {@link AI2} system and updates the current zone.
	 */
	@Override
	public void onMove()
	{
		getOwner().getObserveController().notifyMoveObservers();
		super.onMove();
	}
	
	/**
	 * Retrieves the master player of this summon.<br>
	 * This method calls {@code getOwner} to find the owner.<br>
	 * It then returns the {@code Player} associated with that owner.
	 * @return The {@code Player} who owns the summon.
	 */
	protected Player getMaster()
	{
		return getOwner().getMaster();
	}
}
