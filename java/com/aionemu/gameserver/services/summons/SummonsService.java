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
package com.aionemu.gameserver.services.summons;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_OWNER_REMOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_PANEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_PANEL_REMOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This service manages the lifecycle and logic of player summons.<br>
 * It handles summoning, unsummoning, and updating {@link Summon} entities in the game world. It coordinates interactions between {@link Player} objects and their associated creatures.
 * @author xTz
 */
public class SummonsService
{
	/**
	 * Creates a new summon for a specific player.<br>
	 * This method checks if the {@code master} already has an active summon.<br>
	 * It uses {@code spawnSummon} to generate the creature.<br>
	 * The new summon is then linked to the player and broadcast to other clients.
	 * @param master The {@code Player} who will own the summon.
	 * @param npcId The unique identifier for the NPC type.
	 * @param skillId The ID of the skill used to create the summon.
	 * @param skillLevel The level of the skill being used.
	 * @param time The duration or timing value for the summon.
	 */
	public static void createSummon(Player master, int npcId, int skillId, int skillLevel, int time)
	{
		if (master.getSummon() != null)
		{
			PacketSendUtility.sendPacket(master, new SM_SYSTEM_MESSAGE(1300072));
			return;
		}
		
		final Summon summon = VisibleObjectSpawner.spawnSummon(master, npcId, skillId, skillLevel, time);
		if (summon.getAi2().getName().equals("siege_weapon"))
		{
			summon.getAi2().onGeneralEvent(AIEventType.SPAWNED);
		}
		
		master.setSummon(summon);
		PacketSendUtility.sendPacket(master, new SM_SUMMON_PANEL(summon));
		PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, EmotionType.START_EMOTE2));
		PacketSendUtility.broadcastPacket(summon, new SM_SUMMON_UPDATE(summon));
	}
	
	/**
	 * Releases a specific summon from its current state.<br>
	 * This method cancels active skills and updates the summon mode to {@code RELEASE}.<br>
	 * It sends appropriate system messages based on the provided unsummon type.
	 * @param summon The {@link Summon} object to be released.
	 * @param unsummonType The reason or type of unsummoning.
	 * @param isAttacked A boolean indicating if the summon was under attack during release.
	 */
	public static void release(Summon summon, UnsummonType unsummonType, boolean isAttacked)
	{
		if (summon.getMode() == SummonMode.RELEASE)
		{
			return;
		}
		
		summon.getController().cancelCurrentSkill();
		summon.setMode(SummonMode.RELEASE);
		final Player master = summon.getMaster();
		switch (unsummonType)
		{
			case COMMAND:
				PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_UNSUMMON_FOLLOWER(summon.getNameId()));
				PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
				break;
			case DISTANCE:
				PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_UNSUMMON_BY_TOO_DISTANCE);
				PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
				break;
			case LOGOUT:
				break;
			case UNSPECIFIED:
				PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
				break;
		}
		
		summon.getObserveController().notifySummonReleaseObservers();
		if (unsummonType == UnsummonType.UNSPECIFIED)
		{
			summon.setReleaseTask(ThreadPoolManager.getInstance().schedule(new ReleaseSummonTask(summon, unsummonType, isAttacked), 1000));
		}
		else
		{
			summon.setReleaseTask(ThreadPoolManager.getInstance().schedule(new ReleaseSummonTask(summon, unsummonType, isAttacked), 3000));
		}
	}
	
	public static class ReleaseSummonTask implements Runnable
	{
		private final Summon owner;
		private final UnsummonType unsummonType;
		private final Player master;
		private final VisibleObject target;
		private final boolean isAttacked;
		
		public ReleaseSummonTask(Summon owner, UnsummonType unsummonType, boolean isAttacked)
		{
			this.owner = owner;
			this.unsummonType = unsummonType;
			master = owner.getMaster();
			target = master.getTarget();
			this.isAttacked = isAttacked;
		}
		
		@Override
		public void run()
		{
			owner.getController().delete();
			owner.setMaster(null);
			master.setSummon(null);
			
			switch (unsummonType)
			{
				case COMMAND:
				case DISTANCE:
				case UNSPECIFIED:
					PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_UNSUMMONED(owner.getNameId()));
					PacketSendUtility.sendPacket(master, new SM_SUMMON_OWNER_REMOVE(owner.getObjectId()));
					
					// TODO temp till found on retail
					PacketSendUtility.sendPacket(master, new SM_SUMMON_PANEL_REMOVE());
					if (target instanceof Creature)
					{
						final Creature lastAttacker = (Creature) target;
						if (!master.getLifeStats().isAlreadyDead() && !lastAttacker.getLifeStats().isAlreadyDead() && isAttacked)
						{
							ThreadPoolManager.getInstance().schedule(() -> lastAttacker.getAggroList().addHate(master, 1), 1000);
						}
					}
					break;
				case LOGOUT:
					break;
			}
		}
	}
	
	/**
	 * Sets the summon to rest mode.<br>
	 * This method cancels any active skills and triggers a life restoration task.<br>
	 * It also sends a system message and an update packet to the master player.
	 * @param summon The {@code Summon} object to put into rest mode.
	 */
	public static void restMode(Summon summon)
	{
		summon.getController().cancelCurrentSkill();
		summon.setMode(SummonMode.REST);
		final Player master = summon.getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_REST_MODE(summon.getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
		summon.getLifeStats().triggerRestoreTask();
	}
	
	/**
	 * Sets the {@link Summon} to unknown mode.<br>
	 * This method updates the state and notifies the master player.
	 * @param summon The {@code Summon} object to update.
	 */
	public static void setUnkMode(Summon summon)
	{
		summon.setMode(SummonMode.UNK);
		final Player master = summon.getMaster();
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
	}
	
	/**
	 * Sets the {@link Summon} to guard mode.<br>
	 * This method cancels any active skills and triggers a health restoration task.<br>
	 * It also sends a system message and an update packet to the master player.
	 * @param summon The {@code Summon} object to change into guard mode.
	 */
	public static void guardMode(Summon summon)
	{
		summon.getController().cancelCurrentSkill();
		summon.setMode(SummonMode.GUARD);
		final Player master = summon.getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_GUARD_MODE(summon.getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
		summon.getLifeStats().triggerRestoreTask();
	}
	
	/**
	 * Sets the {@link Summon} to attack mode.<br>
	 * This method updates the summon state and notifies the master player.<br>
	 * It also cancels any active life restoration tasks for the summon.
	 * @param summon The {@code Summon} object to change into attack mode.
	 */
	public static void attackMode(Summon summon)
	{
		summon.setMode(SummonMode.ATTACK);
		final Player master = summon.getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_ATTACK_MODE(summon.getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
		summon.getLifeStats().cancelRestoreTask();
	}
	
	/**
	 * Changes the behavior mode of a specific summon.<br>
	 * This method updates the state based on the provided {@code SummonMode}.
	 * @param summonMode The new mode to apply to the summon.
	 * @param summon The {@link Summon} object to be updated.
	 */
	public static void doMode(SummonMode summonMode, Summon summon)
	{
		doMode(summonMode, summon, 0, null);
	}
	
	/**
	 * Updates the behavior mode of a specific {@link Summon}.<br>
	 * This method handles transitions between different {@link SummonMode} states.<br>
	 * It also processes any associated {@link UnsummonType} logic.
	 * @param summonMode The new mode to apply to the summon.
	 * @param summon The {@link Summon} object being modified.
	 * @param unsummonType The type of unsummon action to perform.
	 */
	public static void doMode(SummonMode summonMode, Summon summon, UnsummonType unsummonType)
	{
		doMode(summonMode, summon, 0, unsummonType);
	}
	
	/**
	 * Changes the behavior mode of a specific {@link Summon}.<br>
	 * This method updates the summon's state based on the provided {@code SummonMode}.<br>
	 * It handles actions like resting, attacking, guarding, or releasing.
	 * @param summonMode The new mode to apply to the summon.
	 * @param summon The {@link Summon} object to modify.
	 * @param targetObjId The ID of the target object if the mode is set to attack.
	 * @param unsummonType The type of unsummoning to perform if the mode is release.
	 */
	public static void doMode(SummonMode summonMode, Summon summon, int targetObjId, UnsummonType unsummonType)
	{
		if (summon.getLifeStats().isAlreadyDead())
		{
			return;
		}
		
		if ((unsummonType != null) && unsummonType.equals(UnsummonType.COMMAND) && !summonMode.equals(SummonMode.RELEASE))
		{
			summon.cancelReleaseTask();
		}
		
		final SummonController summonController = summon.getController();
		if (summonController == null)
		{
			return;
		}
		
		if (summon.getMaster() == null)
		{
			summon.getController().onDelete();
			return;
		}
		
		switch (summonMode)
		{
			case REST:
				summonController.restMode();
				break;
			case ATTACK:
				summonController.attackMode(targetObjId);
				break;
			case GUARD:
				summonController.guardMode();
				break;
			case RELEASE:
				if (unsummonType != null)
				{
					summonController.release(unsummonType);
				}
				break;
			case UNK:
				break;
		}
	}
}
