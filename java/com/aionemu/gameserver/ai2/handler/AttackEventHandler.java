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
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.AttackManager;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * This class handles the logic for combat actions and attacks within the {@link NpcAI2} system.<br>
 * It processes attack-related events to coordinate behaviors between creatures and NPCs.
 * @author ATracer
 */
public class AttackEventHandler
{
	/**
	 * Handles the logic when an NPC starts attacking a target.<br>
	 * This method validates the {@code creature} and updates the AI state.<br>
	 * It stops walking behaviors and initiates the attack sequence via {@link AttackManager}.
	 * @param npcAI The {@link NpcAI2} instance performing the action.
	 * @param creature The {@link Creature} being attacked.
	 */
	public static void onAttack(NpcAI2 npcAI, Creature creature)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onAttack");
		}
		
		if ((creature == null) || creature.getLifeStats().isAlreadyDead() || !npcAI.canThink())
		{
			return;
		}
		
		if (npcAI.isInState(AIState.WALKING) || npcAI.isInState(AIState.RETURNING))
		{
			WalkManager.stopWalking(npcAI);
		}
		
		npcAI.getOwner().getGameStats().renewLastAttackedTime();
		if (!npcAI.isInState(AIState.FIGHT))
		{
			npcAI.setStateIfNot(AIState.FIGHT);
			if (npcAI.isLogging())
			{
				AI2Logger.info(npcAI, "onAttack() -> startAttacking");
			}
			
			npcAI.setSubStateIfNot(AISubState.NONE);
			npcAI.getOwner().setTarget(creature);
			AttackManager.startAttacking(npcAI);
			if (npcAI.poll(AIQuestion.CAN_SHOUT))
			{
				ShoutEventHandler.onAttackBegin(npcAI, (Creature) npcAI.getOwner().getTarget());
			}
		}
	}
	
	/**
	 * Handles the logic when an NPC is forced to start an attack.<br>
	 * This method triggers the {@code Creature)} logic.<br>
	 * It automatically retrieves the target from the owner of the {@code npcAI}.
	 * @param npcAI The AI instance for the NPC being updated.
	 */
	public static void onForcedAttack(NpcAI2 npcAI)
	{
		onAttack(npcAI, (Creature) npcAI.getOwner().getTarget());
	}
	
	/**
	 * This method is called when an NPC finishes its current attack.<br>
	 * It logs the time delta if logging is enabled.<br>
	 * It resets the last attack time and schedules the next attack via {@link AttackManager}.
	 * @param npcAI The {@code NpcAI2} instance of the NPC that finished attacking.
	 */
	public static void onAttackComplete(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onAttackComplete: " + npcAI.getOwner().getGameStats().getLastAttackTimeDelta());
		}
		
		npcAI.getOwner().getGameStats().renewLastAttackTime();
		AttackManager.scheduleNextAttack(npcAI);
	}
	
	/**
	 * Handles the logic when an NPC finishes an attack.<br>
	 * It resets the NPC state and clears its target.<br>
	 * This method is called by the {@link AttackEventHandler}.
	 * @param npcAI The {@code NpcAI2} instance of the NPC that finished attacking.
	 */
	public static void onFinishAttack(NpcAI2 npcAI)
	{
		if (!npcAI.canThink())
		{
			return;
		}
		
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onFinishAttack");
		}
		
		final Npc npc = npcAI.getOwner();
		EmoteManager.emoteStopAttacking(npc);
		npc.getLifeStats().startResting();
		npc.getAggroList().clear();
		if (npcAI.poll(AIQuestion.CAN_SHOUT))
		{
			ShoutEventHandler.onAttackEnd(npcAI);
		}
		
		npc.setTarget(null);
		npc.setSkillNumber(0);
	}
}
