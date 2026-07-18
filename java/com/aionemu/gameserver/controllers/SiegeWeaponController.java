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

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.follow.FollowStartService;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplates;

/**
 * This class manages the logic and behavior for siege weapons in the game.<br>
 * It handles specific actions such as movement, targeting, and skill execution for these entities. It extends {@link SummonController} to provide specialized functionality for large-scale warfare objects.
 * @author xTz
 */
public class SiegeWeaponController extends SummonController
{
	private final NpcSkillTemplates skills;
	
	/**
	 * Creates a new instance of {@link SiegeWeaponController}.<br>
	 * This constructor initializes the skills for the siege weapon.
	 * @param npcId The unique identifier for the NPC.
	 */
	public SiegeWeaponController(int npcId)
	{
		skills = DataManager.NPC_SKILL_DATA.getNpcSkillList(npcId);
	}
	
	/**
	 * Releases the siege weapon from its current state.<br>
	 * This method cancels the follow task and stops any active movement.<br>
	 * It then calls {@code release} to finalize the process.
	 * @param unsummonType The type of unsummon action to perform.
	 */
	@Override
	public void release(UnsummonType unsummonType)
	{
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		getOwner().getMoveController().abortMove();
		super.release(unsummonType);
	}
	
	/**
	 * Puts the siege weapon into rest mode.<br>
	 * This method cancels the {@code SUMMON_FOLLOW} task.<br>
	 * It also triggers a stop follow event for the owner.
	 */
	@Override
	public void restMode()
	{
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		super.restMode();
		getOwner().getAi2().onCreatureEvent(AIEventType.STOP_FOLLOW_ME, getMaster());
	}
	
	/**
	 * Sets the siege weapon to an unknown mode.<br>
	 * This method calls {@code setUnkMode} from the parent class.<br>
	 * It also cancels the {@code SUMMON_FOLLOW} task for the master.
	 */
	@Override
	public void setUnkMode()
	{
		super.setUnkMode();
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
	}
	
	/**
	 * Switches the siege weapon into guard mode.<br>
	 * This method makes the unit protect its master.<br>
	 * It cancels existing follow tasks and updates the AI behavior.
	 */
	@Override
	public void guardMode()
	{
		super.guardMode();
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		getOwner().setTarget(getMaster());
		getOwner().getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, getMaster());
		getOwner().getMoveController().moveToTargetObject();
		getMaster().getController().addTask(TaskId.SUMMON_FOLLOW, FollowStartService.newFollowingToTargetCheckTask(getOwner(), getMaster()));
	}
	
	/**
	 * Switches the siege weapon to attack mode.<br>
	 * This method sets a specific target for the owner.<br>
	 * It triggers movement and follow behaviors toward that target.
	 * @param targetObjId The unique identifier of the {@code Creature} to attack.
	 */
	@Override
	public void attackMode(int targetObjId)
	{
		super.attackMode(targetObjId);
		final Creature target = (Creature) getOwner().getKnownList().getObject(targetObjId);
		if (target == null)
		{
			return;
		}
		
		getOwner().setTarget(target);
		getOwner().getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, target);
		getOwner().getMoveController().moveToTargetObject();
		getMaster().getController().addTask(TaskId.SUMMON_FOLLOW, FollowStartService.newFollowingToTargetCheckTask(getOwner(), target));
	}
	
	/**
	 * Handles the logic when a creature dies.<br>
	 * This method stops movement and clears active effects.<br>
	 * It updates the {@link CreatureState} based on whether the owner is a {@link Player} or an {@link Npc}.<br>
	 * Finally, it notifies all death observers about the {@code lastAttacker}.
	 * @param lastAttacker The creature that dealt the final blow.
	 */
	@Override
	public void onDie(Creature lastAttacker)
	{
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		super.onDie(lastAttacker);
	}
	
	/**
	 * Retrieves the skill templates for this NPC.<br>
	 * This method returns the {@code skills} object used by the controller.
	 * @return the {@link NpcSkillTemplates} instance.
	 */
	public NpcSkillTemplates getNpcSkillTemplates()
	{
		return skills;
	}
}
