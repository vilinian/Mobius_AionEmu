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
package com.aionemu.gameserver.ai2.handler;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.manager.AttackManager;
import com.aionemu.gameserver.ai2.manager.FollowManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/**
 * This class handles events related to target selection for {@link NpcAI2} entities.<br>
 * It manages how NPCs interact with {@link Creature} and {@link VisibleObject} targets during AI execution.
 * @author ATracer
 */
public class TargetEventHandler
{
	/**
	 * This method is called when an NPC reaches its intended target.<br>
	 * It updates the movement and actions based on the current {@link AIState}.<br>
	 * The behavior changes depending on whether the NPC is fighting, following, or walking.
	 * @param npcAI The {@code NpcAI2} instance that reached the target.
	 */
	public static void onTargetReached(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onTargetReached");
		}
		
		final AIState currentState = npcAI.getState();
		switch (currentState)
		{
			case FIGHT:
				npcAI.getOwner().getMoveController().abortMove();
				AttackManager.scheduleNextAttack(npcAI);
				if (npcAI.getOwner().getMoveController().isFollowingTarget())
				{
					npcAI.getOwner().getMoveController().storeStep();
				}
				break;
			case RETURNING:
				npcAI.getOwner().getMoveController().abortMove();
				npcAI.getOwner().getMoveController().recallPreviousStep();
				if (npcAI.getOwner().isAtSpawnLocation())
				{
					npcAI.onGeneralEvent(AIEventType.BACK_HOME);
				}
				else
				{
					npcAI.onGeneralEvent(AIEventType.NOT_AT_HOME);
				}
				break;
			case FOLLOWING:
				npcAI.getOwner().getMoveController().abortMove();
				npcAI.getOwner().getMoveController().storeStep();
				break;
			case FEAR:
				npcAI.getOwner().getMoveController().abortMove();
				npcAI.getOwner().getMoveController().storeStep();
				break;
			case WALKING:
				WalkManager.targetReached(npcAI);
				checkAggro(npcAI);
				break;
			default:
				break;
		}
	}
	
	/**
	 * Handles the logic when a target is too far away.<br>
	 * This method checks the current {@link AIState} of the NPC.<br>
	 * It triggers specific actions based on whether the NPC is fighting or following.
	 * @param npcAI The {@code NpcAI2} instance to update.
	 */
	public static void onTargetTooFar(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onTargetTooFar");
		}
		
		switch (npcAI.getState())
		{
			case FIGHT:
				AttackManager.targetTooFar(npcAI);
				break;
			case FOLLOWING:
				FollowManager.targetTooFar(npcAI);
				break;
			case FEAR:
				break;
			default:
				if (npcAI.isLogging())
				{
					AI2Logger.info(npcAI, "default onTargetTooFar");
				}
		}
	}
	
	/**
	 * Handles the logic when an {@link NpcAI2} gives up on its current target.<br>
	 * It stops the hate for the target and cancels any active movement.<br>
	 * If the NPC is still alive, it triggers a new thought cycle.
	 * @param npcAI The {@code NpcAI2} instance performing the action.
	 */
	public static void onTargetGiveup(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onTargetGiveup");
		}
		
		final VisibleObject target = npcAI.getOwner().getTarget();
		if (target != null)
		{
			npcAI.getOwner().getAggroList().stopHating(target);
		}
		
		if (npcAI.isMoveSupported())
		{
			npcAI.getOwner().getMoveController().abortMove();
		}
		
		if (!npcAI.isAlreadyDead())
		{
			npcAI.think();
		}
	}
	
	/**
	 * Handles the logic when an {@link NpcAI2} changes its target.<br>
	 * It updates the owner's target and schedules an attack if in a fight state.
	 * @param npcAI The AI instance handling the behavior.
	 * @param creature The new creature being targeted.
	 */
	public static void onTargetChange(NpcAI2 npcAI, Creature creature)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onTargetChange");
		}
		
		if (npcAI.isInState(AIState.FIGHT))
		{
			npcAI.getOwner().setTarget(creature);
			AttackManager.scheduleNextAttack(npcAI);
		}
	}
	
	/**
	 * Checks if any nearby creatures should trigger an aggressive response.<br>
	 * This method iterates through all known objects of the owner.<br>
	 * It calls {@code Creature)} for every creature found.
	 * @param npcAI The {@code NpcAI2} instance to check for aggression.
	 */
	private static void checkAggro(NpcAI2 npcAI)
	{
		for (VisibleObject obj : npcAI.getOwner().getKnownList().getKnownObjects().values())
		{
			if (obj instanceof Creature)
			{
				CreatureEventHandler.checkAggro(npcAI, (Creature) obj);
			}
		}
	}
}
