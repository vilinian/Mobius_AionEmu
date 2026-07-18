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
package com.aionemu.gameserver.ai2.manager;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * Manages the combat logic and attack behaviors for {@link Npc} entities.<br>
 * It handles how creatures initiate, execute, and transition between different types of attacks.
 * @author ATracer
 */
public class AttackManager
{
	/**
	 * Initiates the attack sequence for a specific NPC.<br>
	 * This method updates the fight start time and triggers the starting emote.<br>
	 * It also calls {@code scheduleNextAttack} to queue the next action.
	 * @param npcAI The {@code NpcAI2} instance that will begin attacking.
	 */
	public static void startAttacking(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "AttackManager: startAttacking");
		}
		
		npcAI.getOwner().getGameStats().setFightStartingTime();
		EmoteManager.emoteStartAttacking(npcAI.getOwner());
		scheduleNextAttack(npcAI);
	}
	
	/**
	 * Schedules the next attack for a specific NPC.<br>
	 * This method checks if the {@link NpcAI2} is currently in an idle state.<br>
	 * If it is not casting, it calls {@code int)} to determine the next action.
	 * @param npcAI The {@code NpcAI2} instance to update.
	 */
	public static void scheduleNextAttack(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "AttackManager: scheduleNextAttack");
		}
		
		// don't start attack while in casting substate
		final AISubState subState = npcAI.getSubState();
		if (subState == AISubState.NONE)
		{
			chooseAttack(npcAI, npcAI.getOwner().getGameStats().getNextAttackInterval());
		}
		else
		{
			if (npcAI.isLogging())
			{
				AI2Logger.info(npcAI, "Will not choose attack in substate" + subState);
			}
		}
	}
	
	/**
	 * Determines and executes the next attack for a specific NPC.<br>
	 * It checks if the {@code npcAI} can think before performing an action.<br>
	 * The method selects an attack type based on the current intention.
	 * @param npcAI The {@link NpcAI2} instance to perform the attack on.
	 * @param delay The amount of time to wait before starting the attack.
	 */
	protected static void chooseAttack(NpcAI2 npcAI, int delay)
	{
		final AttackIntention attackIntention = npcAI.chooseAttackIntention();
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "AttackManager: chooseAttack " + attackIntention + " delay " + delay);
		}
		
		if (!npcAI.canThink())
		{
			return;
		}
		
		switch (attackIntention)
		{
			case SIMPLE_ATTACK:
				SimpleAttackManager.performAttack(npcAI, delay);
				break;
			case SKILL_ATTACK:
				SkillAttackManager.performAttack(npcAI, delay);
				break;
			case FINISH_ATTACK:
				npcAI.think();
				break;
			default:
				break;
		}
	}
	
	/**
	 * Handles logic for when a target is too far away.<br>
	 * It checks if the {@link Npc} can see its current target.<br>
	 * It also evaluates if the NPC should switch to a more hated creature.<br>
	 * If conditions are not met, it triggers a {@code TARGET_GIVEUP} event.
	 * @param npcAI The {@code NpcAI2} instance to process.
	 */
	public static void targetTooFar(NpcAI2 npcAI)
	{
		final Npc npc = npcAI.getOwner();
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "AttackManager: attackTimeDelta " + npc.getGameStats().getLastAttackTimeDelta());
		}
		
		// switch target if there is more hated creature
		if (npc.getGameStats().getLastChangeTargetTimeDelta() > 5)
		{
			final Creature mostHated = npc.getAggroList().getMostHated();
			if ((mostHated != null) && !mostHated.getLifeStats().isAlreadyDead() && !npc.isTargeting(mostHated.getObjectId()))
			{
				if (npcAI.isLogging())
				{
					AI2Logger.info(npcAI, "AttackManager: switching target during chase");
				}
				
				npcAI.onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
				return;
			}
		}
		
		if (!npc.canSee((Creature) npc.getTarget()) || checkGiveupDistance(npcAI))
		{
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
			return;
		}
		
		if (npcAI.isMoveSupported())
		{
			npc.getMoveController().moveToTargetObject();
			return;
		}
		
		npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
	}
	
	/**
	 * Checks if an NPC should give up its current target.<br>
	 * This happens if the target is too far away or the NPC is too far from home.<br>
	 * It also considers specific time and distance thresholds for default monsters.
	 * @param npcAI The {@link NpcAI2} instance to check.
	 * @return {@code true} if the NPC should give up, {@code false} otherwise.
	 */
	private static boolean checkGiveupDistance(NpcAI2 npcAI)
	{
		final Npc npc = npcAI.getOwner();
		
		// if target run away too far
		final float distanceToTarget = npc.getDistanceToTarget();
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "AttackManager: distanceToTarget " + distanceToTarget);
		}
		
		// TODO may be ask AI too
		final int chaseTarget = npc.isBoss() ? 50 : npc.getPosition().getWorldMapInstance().getTemplate().getAiInfo().getChaseTarget();
		if (distanceToTarget > chaseTarget)
		{
			return true;
		}
		
		final double distanceToHome = npc.getDistanceToSpawnLocation();
		
		// if npc is far away from home
		final int chaseHome = npc.isBoss() ? 150 : npc.getPosition().getWorldMapInstance().getTemplate().getAiInfo().getChaseHome();
		if (distanceToHome > chaseHome)
		{
			return true;
		}
		
		// start thinking about home after 100 meters and no attack for 10 seconds (only for default monsters)
		if (chaseHome <= 200)
		{
			// TODO: Check Client and use chase_user_by_trace value
			if (((npc.getGameStats().getLastAttackTimeDelta() > 20) && (npc.getGameStats().getLastAttackedTimeDelta() > 20)) || ((distanceToHome > (chaseHome / 2)) && (npc.getGameStats().getLastAttackedTimeDelta() > 10)))
			{
				return true;
			}
		}
		
		return false;
	}
}
